package com.windows.workshop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmCustomer implements transactionalForm {
	private Connection con = null;
	private List<FieldInfo> lstFlds = new ArrayList<FieldInfo>();
	private localTableModel data_o1 = new localTableModel();
	private localTableModel data_o2 = new localTableModel();
	private List<ColumnProperty> lstColo1 = new ArrayList<ColumnProperty>();
	private List<ColumnProperty> lstColo2 = new ArrayList<ColumnProperty>();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();
	private HorizontalLayout h1Layout = new HorizontalLayout();
	private HorizontalLayout h2Layout = new HorizontalLayout();

	private Table tbl_ord1 = ControlsFactory.CreatTable("Job Orders",
			"WORKSHOP_CUSTOMER_O1", lstColo1);
	private Table tbl_ord2 = ControlsFactory.CreatTable("Services/Items",
			"WORKSHOP_CUSTOMER_O2", lstColo2);
	private HorizontalSplitPanel pnl_details = ControlsFactory
			.CreateHorizontalSplitPanel(tbl_ord1, tbl_ord2, 60);

	private NativeButton cmdSave = new NativeButton("Save & Post");
	private NativeButton cmdRevert = new NativeButton("Revert");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdDel = new NativeButton("Delete");
	private NativeButton cmdCls = new NativeButton("Clear");

	private TextField txtCustCode = ControlsFactory.CreateTextField(
			"Customer Code", "CODE", lstFlds);
	private TextField txtCustName1 = ControlsFactory.CreateTextField(" Name 1",
			"NAME", lstFlds);
	private TextField txtCustName2 = ControlsFactory.CreateTextField("Name 2",
			"NAME_A", lstFlds);
	private TextField txtAc = ControlsFactory.CreateTextField("A/c", "AC_NO",
			lstFlds);
	private TextField txtAddress = ControlsFactory.CreateTextField("Address",
			"ADDR", lstFlds);
	private TextField txtEmail = ControlsFactory.CreateTextField("Email",
			"EMAIL", lstFlds);
	private TextField txtTel = ControlsFactory.CreateTextField("Tel", "TEL",
			lstFlds);

	private String QRYSES = "";
	private boolean listnerAdded = false;

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		commandLayout.removeAllComponents();
		data_o1.clearALl();
		data_o2.clearALl();
		fill_table_ord1();
		fill_table_ord2();
	}

	private void fetch_data_o1() {
		try {
			data_o1
					.executeQuery(
							"select order1.*, items.descr||'-'||reference item_descr,"
									+ "deliveredqty||'%' finished,"
									+ "orderdqty||'%' cancelled,"
									+ "(select max(invoice_no) from pur1 where keyfld=order1.saleinv) invoice_no"
									+ " from items,order1"
									+ " where ord_code=106 and ord_ref='"
									+ txtCustCode.getValue()
									+ "' and ordacc=reference order by ord_date desc",
							true);
			fill_table_ord1();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void fetch_data_o2() {
		if (tbl_ord1.getValue() == null) {
			return;
		}
		try {
			Integer rn = (Integer) tbl_ord1.getValue();

			String on = data_o1.getFieldValue(rn, "ORD_NO").toString();
			data_o2
					.executeQuery(
							"select order2.*,ORDER2.DESCR ITEM_DESCR,DECODE(DELIVEREDQTY,0,'NOT STARTED',"
									+ "1,'STARTED',2,'FINISHED',-1,'CANCELLED') STATUS_SERVICE"
									+ ",'' EMP  from order2 where ord_no='"
									+ on
									+ "' and ord_code=106 and order2.ord_type=2 "
									+ "order by order2.ord_pos", true);
			fill_table_ord2();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void fill_table_ord1() {
		utilsVaadin.query_data2(tbl_ord1, data_o1, lstColo1);
	}

	private void fill_table_ord2() {
		utilsVaadin.query_data2(tbl_ord2, data_o2, lstColo2);
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();

		// centralPanel.setSizeUndefined();
		// mainLayout.se
		mainLayout.setWidth("100%");
		basicLayout.setWidth("100%");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		txtCustCode.setWidth("20%");
		txtCustName1.setWidth("50%");
		txtCustName2.setWidth("50%");
		h1Layout.setWidth("100%");
		h2Layout.setWidth("100%");
		txtEmail.setWidth("50%");
		txtTel.setWidth("50%");
		txtAc.setWidth("20%");
		txtAddress.setWidth("100%");
		txtAddress.setHeight("50px");
		pnl_details.setHeight("200px");
		pnl_details.setWidth("100%");

		txtAc.setEnabled(false);

		txtCustCode.setImmediate(true);
		txtCustCode.setReadOnly(false);

		tbl_ord1.setSelectable(true);
		tbl_ord1.setSortDisabled(true);
		tbl_ord1.setColumnReorderingAllowed(false);
		tbl_ord2.setSelectable(true);
		tbl_ord2.setSortDisabled(true);
		tbl_ord2.setColumnReorderingAllowed(false);
		tbl_ord1.setImmediate(true);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdList);
		ResourceManager.addComponent(commandLayout, cmdDel);
		ResourceManager.addComponent(commandLayout, cmdRevert);
		ResourceManager.addComponent(commandLayout, cmdCls);

		ResourceManager.addComponent(basicLayout, txtCustCode);
		ResourceManager.addComponent(basicLayout, txtAc);

		ResourceManager.addComponent(h1Layout, txtCustName1);
		ResourceManager.addComponent(h1Layout, txtCustName2);
		ResourceManager.addComponent(basicLayout, h1Layout);

		ResourceManager.addComponent(basicLayout, txtAddress);

		ResourceManager.addComponent(h2Layout, txtEmail);
		ResourceManager.addComponent(h2Layout, txtTel);
		ResourceManager.addComponent(basicLayout, h2Layout);

		ResourceManager.addComponent(mainLayout, pnl_details);

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
			txtCustCode.addListener(new BlurListener() {

				public void blur(BlurEvent event) {
					if (!txtCustCode.isReadOnly()
							&& txtCustCode.getValue() != null) {
						String fnd = utils.getSqlValue(
								"select name from c_ycust where upper(code)='"
										+ txtCustCode.getValue().toString()
												.toUpperCase() + "'", con);
						if (!fnd.isEmpty()) {
							parentLayout.getWindow().showNotification(
									"Found # " + fnd);
							QRYSES = txtCustCode.getValue().toString()
									.toUpperCase();
							load_data();

						}
					}
				}
			});
			tbl_ord1.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					fetch_data_o2();

				}
			});
			listnerAdded = true;
		}
	}

	protected void show_list() {
		try {
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);

			utilsVaadin
					.showSearch(
							la,
							new SelectionListener() {

								public void onSelection(TableView tv) {
									Channelplus3Application.getInstance()
											.getMainWindow().removeWindow(wnd);

									if (tv.getSelectionValue() > -1) {
										try {
											int rn = tv.getSelectionValue();
											QRYSES = tv.getData()
													.getFieldValue(rn, "CODE")
													.toString();
											load_data();
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select code,name from c_ycust where iscust='Y' order by path",
							true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void delete_data() {

	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {

			if (data_o1.getDbclass() == null) {
				data_o1.createDBClassFromConnection(con);
			}
			if (data_o2.getDbclass() == null) {
				data_o2.createDBClassFromConnection(con);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		load_data();

	}

	public void load_data() {
		data_o1.clearALl();
		data_o2.clearALl();
		tbl_ord1.removeAllItems();
		tbl_ord2.removeAllItems();
		txtCustCode.setReadOnly(false);
		txtAc
				.setValue(utils
						.getSqlValue(
								"select repair.getsetupvalue_2('MAIN_CUST_ACC') from dual",
								con));
		txtCustCode.setValue("");
		txtCustName1.setValue("");
		txtCustName2.setValue("");
		txtAddress.setValue("");
		txtEmail.setValue("");
		txtTel.setValue("");
		txtCustCode.setReadOnly(false);

		try {
			PreparedStatement ps = con.prepareStatement(
					"select *from C_YCUST where CODE='" + QRYSES + "'",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rst = ps.executeQuery();

			if (rst.first()) {
				txtCustCode.setValue(rst.getString("CODE"));
				txtCustCode.setReadOnly(true);
				txtCustName1.setValue(rst.getString("NAME"));
				txtCustName2.setValue(utils.nvl(rst.getString("NAMEA"), ""));
				txtAddress.setValue(utils.nvl(rst.getString("ADDR"), ""));
				txtAc.setValue(utils.nvl(rst.getString("AC_NO"), ""));
				txtTel.setValue(utils.nvl(rst.getString("TEL"), ""));
				txtEmail.setValue(utils.nvl(rst.getString("EMAIL"), ""));
			}
			fetch_data_o1();
			fetch_data_o2();
			ps.close();

		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public void print_data() {

	}

	public void validate_data() throws Exception {
		String chld = utils.getSqlValue(
				"select max(childcount) from acaccount where accno="
						+ txtAc.getValue() + " and actype=0 ", con);
		if (chld == null || chld.isEmpty()) {
			throw new Exception("A/C not found or not valid ");
		}
		if (Double.valueOf(chld).doubleValue() > 0) {
			throw new Exception("A/c is not valid, select child a/c");
		}

	}

	public void save_data() {
		try {
			validate_data();
			String sqlIns = "INSERT INTO c_ycust(code,name,namea,ac_no,addr,tel,email,path ,iscust) values ("
					+ " :CODE , :NAME ,:NAMEA , :AC_NO , :ADDR , :TEL , :EMAIL , :PATH ,'Y')";
			String sqlUpd = "update c_ycust set name =:NAME ,namea = :NAMEA, path = :PATH , iscust='Y' "
					+ ", ac_no =:AC_NO ,addr = :ADDR , tel= :TEL , email=:EMAIL where code=:CODE";
			try {

				con.setAutoCommit(false);
				QueryExe qe = new QueryExe(con);
				if (QRYSES.equals("")) {
					qe.setSqlStr(sqlIns);
				} else {
					qe.setSqlStr(sqlUpd);
				}

				qe.setParaValue("CODE", txtCustCode.getValue());
				qe.setParaValue("NAME", txtCustName1.getValue());
				qe.setParaValue("NAMEA", txtCustName2.getValue());
				qe.setParaValue("AC_NO", txtAc.getValue());
				qe.setParaValue("ADDR", txtAddress.getValue());
				qe.setParaValue("TEL", txtTel.getValue());
				qe.setParaValue("EMAIL", txtEmail.getValue());
				qe.setParaValue("PATH", "XXX\\" + txtCustCode + "\\");
				qe.execute();
				qe.close();
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

	public void showInitView() {
		QRYSES = "";
		init();
		initForm();

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

}
