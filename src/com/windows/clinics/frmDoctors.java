package com.windows.clinics;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.localTableModel;
import com.generic.rowTriggerListner;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.ColumnProperty.ColumnAction;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmDoctors implements transactionalForm {
	private Connection con = null;
	private Window wndList = new Window();
	private VerticalLayout lstLayout = new VerticalLayout();
	private localTableModel data_items = new localTableModel();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();

	private NativeButton cmdSave = new NativeButton("Save & Post");
	private NativeButton cmdRevert = new NativeButton("Revert");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdDel = new NativeButton("Delete");
	private NativeButton cmdCls = new NativeButton("Clear");
	private NativeButton cmdChkAll = new NativeButton("Check All");
	private NativeButton cmdUnChkAll = new NativeButton("Un Check All");
	
	private TextField txtNo = new TextField("Doctor No");
	private TextField txtName = new TextField("Name Eng");
	private TextField txtNameA = new TextField("Name Arb");
	private ComboBox txtCostCenter = ControlsFactory.CreateListField(
			"Cost center", "cost",
			"select code , title from accostcent1 order by code", null);

	private Table tbl_items = new Table();

	private String QRYSES = "";
	private boolean listnerAdded = false;
	private List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();

	public frmDoctors() {
		init();
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		commandLayout.removeAllComponents();

		tbl_items.removeAllItems();
		data_items.clearALl();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();

		// centralPanel.setSizeFull();
		// mainLayout.se
		// basicLayout.setSizeFull();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		txtNo.setWidth("100px");
		txtName.setWidth("200px");
		txtNameA.setWidth("200px");
		txtCostCenter.setWidth("200px");
		tbl_items.setSizeFull();
		basicLayout.setHeight("100%");
		commandLayout.setHeight("100%");
		txtNo.setImmediate(true);

		tbl_items.setSelectable(true);
		tbl_items.setColumnReorderingAllowed(false);
		tbl_items.setSortDisabled(true);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdList);
		ResourceManager.addComponent(commandLayout, cmdDel);
		ResourceManager.addComponent(commandLayout, cmdRevert);
		ResourceManager.addComponent(commandLayout, cmdCls);
		ResourceManager.addComponent(commandLayout, cmdChkAll);
		ResourceManager.addComponent(commandLayout, cmdUnChkAll);

		ResourceManager.addComponent(basicLayout, txtNo);
		ResourceManager.addComponent(basicLayout, txtName);
		ResourceManager.addComponent(basicLayout, txtNameA);
		ResourceManager.addComponent(basicLayout, txtCostCenter);

		ResourceManager.addComponent(mainLayout, tbl_items);
		mainLayout.setExpandRatio(commandLayout, .2f);
		mainLayout.setExpandRatio(basicLayout, .8f);
		mainLayout.setExpandRatio(tbl_items, 3f);

		if (!listnerAdded) {
			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			cmdDel.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

					delete_data();
				}
			});
			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_list();
				}
			});
			cmdCls.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					load_data();
				}
			});
			cmdRevert.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					load_data();

				}
			});

			cmdChkAll.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					do_checkbox_value(true);

				}
			});
			cmdUnChkAll.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					do_checkbox_value(false);
				}
			});
			txtNo.addListener(new BlurListener() {

				public void blur(BlurEvent event) {
					if (!txtNo.isReadOnly() && txtNo.getValue() != null) {
						String fnd = utils.getSqlValue(
								"select name from salesp where no='"
										+ txtNo.getValue().toString()
												.toUpperCase()
										+ "' and type='DR'", con);
						if (!fnd.isEmpty()) {
							parentLayout.getWindow().showNotification(
									"Found # " + fnd);
							QRYSES = txtNo.getValue().toString().toUpperCase();
							load_data();
						}
					}
				}
			});
			data_items.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (cursorNo < 0) {
						return;
					}
					utilsVaadin.refreshRowFromData(data_items, tbl_items,
							cursorNo, lstItemCols);
				}
			});
			listnerAdded = true;
		}
	}

	private void do_checkbox_value(boolean b) {
		for (int i = 0; i < tbl_items.getItemIds().size(); i++) {
			final Integer idno = (Integer) tbl_items.getItemIds().toArray()[i];
			final CheckBox c = (CheckBox) tbl_items
					.getItem(idno)
					.getItemProperty(
							utilsVaadin.findColByCol("SELECTION", lstItemCols).descr)
					.getValue();
			final TextField t = (TextField) tbl_items
					.getItem(idno)
					.getItemProperty(
							utilsVaadin.findColByCol("RATE", lstItemCols).descr)
					.getValue();

			c.setValue(b);
			if (c.booleanValue()) {
				t.setEnabled(true);
			} else {
				t.setEnabled(false);
				data_items.setFieldValue(idno, "RATE", BigDecimal.valueOf(0f));
			}

			/*
			 * if (b) { data_items.setFieldValue(idno, "SELECTION", "Y"); } else
			 * { data_items.setFieldValue(idno, "SELECTION", "N"); }
			 */
		}
	}

	protected void show_list() {
		try {
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
							QRYSES = tv.getData().getFieldValue(rn, "NO")
									.toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select no,name from salesp where type='DR' order by no",
					true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void delete_data() {

	}

	public void fetch_items_table() {
		try {
			data_items
					.executeQuery(
							"select reference,'N' SELECTION,reference||' - '||descr ITEM,nvl(CLQ_DR_FEES.rate,0) RATE from items,CLQ_DR_FEES"
									+ "  where childcounts=0 AND REFER(+)=REFERENCE and (no(+)='"
									+ QRYSES + "') order by itprice4,descr2 ",
							true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void fill_item_table() {
		tbl_items.removeAllItems();
		utilsVaadin.query_data2(tbl_items, data_items, lstItemCols);
		do_triggers();
	}

	public void do_triggers() {
		for (int i = 0; i < tbl_items.getItemIds().size(); i++) {
			final Integer idno = (Integer) tbl_items.getItemIds().toArray()[i];
			final CheckBox c = (CheckBox) tbl_items
					.getItem(idno)
					.getItemProperty(
							utilsVaadin.findColByCol("SELECTION", lstItemCols).descr)
					.getValue();
			final TextField t = (TextField) tbl_items
					.getItem(idno)
					.getItemProperty(
							utilsVaadin.findColByCol("RATE", lstItemCols).descr)
					.getValue();

			double rt = ((BigDecimal) data_items.getFieldValue(idno, "RATE"))
					.doubleValue();

			if (rt > 0) {
				data_items.setFieldValue(idno, "SELECTION", "Y");
				c.setValue(true);
				t.setEnabled(true);
			} else {
				data_items.setFieldValue(idno, "SELECTION", "N");
				t.setEnabled(false);
			}
			c.setImmediate(true);
			t.setImmediate(true);

			c.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

					if (c.booleanValue()) {
						t.setEnabled(true);
					} else {
						t.setEnabled(false);
						data_items.setFieldValue(idno, "RATE", BigDecimal
								.valueOf(0f));
					}
				}
			});

		}
	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		{
			ColumnProperty cp = new ColumnProperty();
			lstItemCols.add(cp);
			cp.col_class = CheckBox.class;
			cp.data_type = Parameter.DATA_TYPE_STRING;
			cp.colname = "SELECTION";
			cp.onCheckValue = "Y";
			cp.onUnCheckValue = "N";
			cp.descr = "Select";
			cp.display_width = 50;
			cp.pos = 1;
			cp.display_format = "NONE";

			cp = new ColumnProperty();
			lstItemCols.add(cp);
			cp.data_type = Parameter.DATA_TYPE_STRING;
			cp.col_class = Label.class;
			cp.descr = "Item";
			cp.display_width = 250;
			cp.colname = "ITEM";
			cp.pos = 2;
			cp.display_format = "NONE";

			cp = new ColumnProperty();
			lstItemCols.add(cp);
			cp.descr = "Fees %";
			cp.col_class = TextField.class;
			cp.colname = "RATE";
			cp.data_type = Parameter.DATA_TYPE_NUMBER;
			cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
			cp.display_width = 100;
			cp.display_align = ColumnProperty.ALIGN_RIGHT;
			cp.pos = 3;
			cp.action = new ColumnAction() {

				public Object onValueChange(int rowno, String colname, Object vl) {

					if (colname.equals("RATE")) {
						double v = Double.valueOf(vl.toString());
						if (v > 100) {
							return BigDecimal.valueOf(100f);
						}
					}
					return vl;
				}
			};
		}
		for (Iterator iterator = lstItemCols.iterator(); iterator.hasNext();) {
			ColumnProperty cp = (ColumnProperty) iterator.next();
			tbl_items.addContainerProperty(cp.descr, cp.col_class, null);
			tbl_items.setColumnWidth(cp.descr, cp.display_width);
		}

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {
			if (data_items.getDbclass() == null) {
				data_items.createDBClassFromConnection(con);
			}

		} catch (SQLException e) {
		}

		createView();
		load_data();

	}

	public void load_data() {
		txtNo.setReadOnly(false);
		txtNo.setValue("");
		txtName.setValue("");
		txtNameA.setValue("");
		txtCostCenter.setValue(null);

		try {
			PreparedStatement ps = con.prepareStatement(
					"select *from salesp where no='" + QRYSES
							+ "' AND TYPE='DR'",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rst = ps.executeQuery();

			if (rst.first()) {
				txtNo.setValue(rst.getString("NO"));
				txtNo.setReadOnly(true);
				txtName.setValue(rst.getString("NAME"));
				txtNameA.setValue(utils.nvl(rst.getString("NAMEA"), ""));
				txtCostCenter.setValue(utilsVaadin.findByValue(txtCostCenter,
						rst.getString("HADDR")));
			} else {
				txtNo.setValue(utils.getSqlValue(
						"select nvl(max(no),0)+1 from salesp", con));

			}
			ps.close();
			fetch_items_table();
			fill_item_table();
		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public void print_data() {

	}

	public void validate_data() throws Exception {
		if (txtName.getValue() == null) {
			throw new Exception("Name must have value");
		}

	}

	public void save_data() {
		try {
			validate_data();
			String sqlIns = "insert into salesp(no,name,namea,flag,TYPE,HADDR) values (:no,:name,:namea,1,'DR',:COSTCENT)";
			String sqlUpd = "update salesp set name=:name,namea=:namea,flag=1,HADDR=:COSTCENT where no=:no";
			try {
				con.setAutoCommit(false);
				QueryExe qe = new QueryExe(con);
				if (QRYSES.equals("")) {
					qe.setSqlStr(sqlIns);
				} else {
					qe.setSqlStr(sqlUpd);
				}
				qe.setParaValue("no", txtNo.getValue());
				qe.setParaValue("name", txtName.getValue());
				qe.setParaValue("namea", utils.nvl(txtNameA.getValue(), ""));
				qe.setParaValue("COSTCENT", txtCostCenter.getValue());
				qe.execute();
				qe.close();
				save_data_items();
				con.commit();
				parentLayout.getWindow().showNotification("Saved Successfully");
				QRYSES = "";
				load_data();

			} catch (SQLException ex) {
				ex.printStackTrace();
				parentLayout.getWindow().showNotification(ex.getMessage(),
						Notification.TYPE_ERROR_MESSAGE);
				try {
					con.rollback();
				} catch (SQLException ex1) {

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}
	}

	public void save_data_items() throws SQLException {
		QueryExe qe = new QueryExe(
				"insert into clq_dr_fees(no,refer,rate) values (:NO,:REFER,:RATE)",
				con);
		QueryExe qedel = new QueryExe("delete from clq_dr_fees where no=:NO",
				con);
		qedel.setParaValue("NO", txtNo.getValue());
		qedel.execute();
		qedel.close();
		qe.parse();
		for (int i = 0; i < data_items.getRows().size(); i++) {
			if (((BigDecimal) data_items.getFieldValue(i, "RATE"))
					.doubleValue() > 0) {
				qe.setParaValue("refer", data_items.getFieldValue(i,
						"REFERENCE"));
				qe.setParaValue("rate", data_items.getFieldValue(i, "RATE"));
				qe.setParaValue("NO", txtNo.getValue());
				qe.execute(false);
			}
		}
		qe.close();
	}

	public void showInitView() {
		QRYSES = "";
		// init();
		initForm();

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

}
