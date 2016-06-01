package com.windows.LG;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doc.views.TableView;
import com.doc.views.YesNoDialog;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog.Callback;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.ColumnProperty.afterModelUpdated;
import com.generic.TableLayoutVaadin.Triggers;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmLgContract implements transactionalForm {
	private Connection con = null;
	private Window wndList = new Window();
	private VerticalLayout lstLayout = new VerticalLayout();

	private localTableModel data_ = new localTableModel();

	private Panel mainPanel = new Panel();
	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();

	private List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();
	private List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();

	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");
	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");

	private Label txtCustomer = new Label("");

	private HorizontalLayout tableLayout = new HorizontalLayout();

	private VerticalLayout tableCostLayout = new VerticalLayout();
	private VerticalLayout tableSaleLayout = new VerticalLayout();
	TableLayoutVaadin tableCostItems = new TableLayoutVaadin(tableCostLayout);
	TableLayoutVaadin tableSaleItems = new TableLayoutVaadin(tableSaleLayout);

	private String QRYSES = "";
	private boolean listnerAdded = false;
	private Double max_kf = null;
	private Double added_record = 0.0;

	public frmLgContract() {

	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		tableCostItems.resetLayout();
		tableSaleItems.resetLayout();
	}

	public void applyColumns() {

		try {
			utilsVaadin.applyColumns("FRMLGCONTRACT.COST_ITEMS",
					tableCostItems.getTable(), tableCostItems.listFields, con);
			utilsVaadin.applyColumns("FRMLGCONTRACT.SALES_ITEMS",
					tableSaleItems.getTable(), tableSaleItems.listFields, con);

			utilsVaadin.findColByCol("TYPE_OF_FRIEGHT",
					tableSaleItems.listFields).lov_sql = new ArrayList<dataCell>();
			utilsVaadin.findColByCol("TYPE_OF_FRIEGHT",
					tableSaleItems.listFields).lov_sql.add(new dataCell("LAND",
					"1"));
			utilsVaadin.findColByCol("TYPE_OF_FRIEGHT",
					tableSaleItems.listFields).lov_sql.add(new dataCell("SEA",
					"2"));
			utilsVaadin.findColByCol("TYPE_OF_FRIEGHT",
					tableSaleItems.listFields).lov_sql.add(new dataCell("AIR",
					"3"));
			utilsVaadin.findColByCol("TYPE_OF_FRIEGHT",
					tableSaleItems.listFields).defaultValue = "1";

			utilsVaadin.findColByCol("COST_ITEM", tableCostItems.listFields).actionAfterUpdate = new afterModelUpdated() {

				public Object onValueChange(int rowno, String colname, Object vl) {
					if (utils.nvl(vl, "").isEmpty())
						tableSaleItems.disableToolbar();
					else
						tableSaleItems.enableToolbar();
					tableCostItems.getTable().setValue(rowno);
					return vl;
				}
			};

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createView() {

		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		tableCostItems.createView();
		tableSaleItems.createView();

		centralPanel.setSizeFull();
		mainPanel.setWidth("700px");
		mainPanel.setHeight("100%");
		mainPanel.getContent().setHeight("-1px");

		tableLayout.setSizeFull();
		tableCostLayout.setSizeFull();
		tableSaleLayout.setSizeFull();

		tableCostItems.setHeight("400px");
		tableCostItems.getTable().setHeight("360px");
		tableCostItems.getTable().setImmediate(true);

		tableSaleItems.setHeight("400px");
		tableSaleItems.getTable().setHeight("360px");
		txtCustomer.addStyleName("yellowLabel");

		ResourceManager.addComponent(centralPanel, mainPanel);
		ResourceManager.addComponent(mainPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, buttonLayout);

		ResourceManager.addComponent(buttonLayout, cmdCls);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdSave);

		ResourceManager.addComponent(mainLayout, basicLayout);
		ResourceManager.addComponent(basicLayout, txtCustomer);

		ResourceManager.addComponent(mainLayout, tableLayout);
		ResourceManager.addComponent(tableLayout, tableCostLayout);
		ResourceManager.addComponent(tableLayout, tableSaleLayout);

		tableLayout.setExpandRatio(tableCostLayout, 1.1f);
		tableLayout.setExpandRatio(tableSaleLayout, 2.9f);

		applyColumns();

		if (!listnerAdded) {
			listnerAdded = true;

			tableSaleItems.trig = new Triggers() {

				public void beforeSumOnCalc(int recno) {

				}

				public boolean beforeDelRecord() {

					return true;
				}

				public boolean beforeAddRecord() {
					return true;
				}

				public void afterDelRecord(int recno) {

				}

				public void AfterAddRecord(int recno) {
					if (max_kf == null || max_kf <= 0)
						max_kf = Double
								.valueOf(utils
										.getSqlValue(
												"select nvl(max(keyfld),0)+1 from LG_CUSTITEMS",
												con));
					else
						max_kf = max_kf + (++added_record);

					tableSaleItems.data.setFieldValue(recno, "KEYFLD",
							BigDecimal.valueOf(max_kf.doubleValue()));
				}
			};

			tableCostItems.trig = new Triggers() {

				public void beforeSumOnCalc(int recno) {

				}

				public boolean beforeAddRecord() {

					return true;
				}

				public void AfterAddRecord(int recno) {
					show_item_list(recno);
				}

				public void afterDelRecord(int recno) {

				}

				public boolean beforeDelRecord() {

					Callback cb = new Callback() {

						public void onDialogResult(boolean resultIsYes) {

							if (resultIsYes) {
								delete_data();
								load_data();
								parentLayout.getWindow().showNotification("");
							}

						}
					};
					parentLayout
							.getWindow()
							.addWindow(
									new YesNoDialog(
											"Confirmation",
											"Do you want delete selected cost items  ?",
											cb, "150px", "-1px"));

					return false;
				}
			};
			tableCostItems.getTable().addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					tableSaleItems.disableToolbar();
					if (tableCostItems.getTable().getValue() != null
							&& !utils.nvl(
									tableCostItems.data.getFieldValue(
											((Integer) tableCostItems
													.getTable().getValue()),
											"COST_ITEM"), "").isEmpty())
						tableSaleItems.enableToolbar();

					try {
						fetch_tableSale();
					} catch (Exception e) {
						parentLayout.getWindow()
								.showNotification("", e.getMessage(),
										Notification.TYPE_ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});

			cmdCls.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					txtCustomer.setValue(null);
					load_data();
				}
			});

			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_list();
				}
			});
			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();

				}
			});
		}
	}

	protected void show_list() {
		try {
			String sq = "select code,name from c_ycust where iscust='Y' order by path";
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);

			utilsVaadin.showSearch(la, new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);

					if (tv.getSelectionValue() > -1) {
						try {
							int rn = tv.getSelectionValue();
							QRYSES = tv.getData().getFieldValue(rn, "code")
									.toString();

							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, sq, true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	public void show_item_list(final int rcno) {
		try {
			String ia = "";
			for (int i = 0; i < tableCostItems.data.getRowCount(); i++) {
				if (tableCostItems.data.getFieldValue(i, "COST_ITEM") == null)
					continue;
				ia = ia + (ia.isEmpty() ? "" : ",") + "'"
						+ tableCostItems.data.getFieldValue(i, "COST_ITEM")
						+ "'";
			}
			String sq = "select reference,descr from items where childcounts=0 and "
					+ "flag=1 order by descr2";
			if (!ia.isEmpty())
				sq = "select reference,descr from items where childcounts=0 and "
						+ "flag=1 and reference not in ("
						+ ia
						+ ") order by descr2";
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);

			utilsVaadin.showSearch(la, new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);

					if (tv.getSelectionValue() > -1) {
						try {
							int rn = tv.getSelectionValue();
							tableCostItems.data
									.setFieldValue(
											rcno,
											"COST_ITEM",
											tv.getData().getFieldValue(rn,
													"REFERENCE"));
							tableCostItems.data.setFieldValue(rcno,
									"COST_ITEM_DESCR", tv.getData()
											.getFieldValue(rn, "DESCR"));
							tableCostItems.fill_table();
							tableCostItems.getTable().setValue(rcno);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, sq, true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void delete_data() {
		try {
			con.setAutoCommit(false);
			String ci = getSelectedCostItem();
			if (!ci.isEmpty())
				utils.execSql("delete from lg_custitems where cost_item='" + ci
						+ "' and code='" + QRYSES + "'", con);
			else
				utils.execSql(
						"delete from lg_custitems where cost_item is null"
								+ " and code='" + QRYSES + "'", con);
			con.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}
	}

	public void init() {

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {
			if (data_.getDbclass() == null) {
				data_.createDBClassFromConnection(con);
			}

		} catch (SQLException e) {

		}

		createView();
		load_data();

	}

	public void load_data() {
		utilsVaadin.resetValues(basicLayout, false, false);
		utilsVaadin.setDefaultValues(lstfldinfo, false);
		tableCostItems.data.clearALl();
		tableSaleItems.data.clearALl();
		tableCostItems.getTable().removeAllItems();
		tableSaleItems.getTable().removeAllItems();

		tableSaleItems.disableToolbar();
		tableCostItems.disableToolbar();
		try {
			if (!QRYSES.isEmpty()) {
				txtCustomer.setValue(utils.getSqlValue(
						"select code||' - '||name from c_ycust where code='"
								+ QRYSES + "'", con));

				// tableSaleItems.enableToolbar();
				tableCostItems.enableToolbar();
				fetch_tableCost();
				fetch_tableSale();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void fetch_tableCost() throws SQLException {
		tableCostItems.data.executeQuery(
				"select distinct cost_item,descr cost_item_descr from items,lg_custitems "
						+ "where cost_item=reference and code='" + QRYSES
						+ "' order by cost_item", true);
		tableCostItems.fill_table();

	}

	public void fetch_tableSale() throws Exception {
		String ci = getSelectedCostItem();
		tableSaleItems.data.executeQuery(
				"select *from  lg_custitems where code='" + QRYSES + "'"
						+ " and cost_item='" + ci
						+ "' order by type_of_frieght,frieght_descr", true);
		tableSaleItems.fill_table();
		// max_kf = Double.valueOf(utils.getSqlValue(
		// "select nvl(max(keyfld),0)+1 from lg_custitems", con));
		max_kf = null;
		added_record = 0.0;
	}

	public void print_data() {

	}

	public void validate_data() throws Exception {
		if (tableSaleItems.data.getRowCount() < 0)
			throw new Exception("No record found in selling items ");
		if (tableCostItems.data.getRowCount() < 0)
			throw new Exception("No record found in cost items ");
		if (tableCostItems.getTable().getValue() == null)
			throw new Exception("Nothing selected in cost items");
		String ci = getSelectedCostItem();
		if (ci.isEmpty())
			throw new Exception("No any cost item selected !");
		if (QRYSES.isEmpty())
			throw new Exception("Select customer first !");
	}

	public String getSelectedCostItem() {
		Integer n = -1;
		String ci = "";
		if (tableCostItems.getTable().getValue() != null)
			n = (Integer) tableCostItems.getTable().getValue();
		if (n >= 0)
			ci = utils.nvl(tableCostItems.data.getFieldValue(n, "COST_ITEM"),
					"");
		return ci;
	}

	public void save_data() {
		try {
			validate_data();
			con.setAutoCommit(false);

			String ci = getSelectedCostItem();
			utils.execSql("delete from lg_custitems where cost_item='" + ci
					+ "' and code='" + QRYSES + "'", con);
			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("COST_ITEM", ci);
			mp.put("CODE", QRYSES);			

			// double k = Double.valueOf(utils.getSqlValue(
			// "select nvl(max(keyfld),0)+1 from lg_custitems", con));
			//
			// for (int i = 0; i < tableSaleItems.data.getRowCount(); i++)
			// tableSaleItems.data.setFieldValue(i, "KEYFLD", BigDecimal
			// .valueOf(k++));
			
			tableSaleItems.insert_to_table("LG_CUSTITEMS", mp, "", "");
			con.commit();			
			parentLayout.getWindow().showNotification("", "Saved successfully");
			
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}
	}

	public void showInitView() {
		QRYSES = "";
		initForm();

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

}