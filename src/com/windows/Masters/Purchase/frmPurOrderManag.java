package com.windows.Masters.Purchase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.doc.views.TableView;
import com.doc.views.YesNoDialog;
import com.doc.views.Query.QueryView;
import com.doc.views.Query.tableDataListner;
import com.doc.views.YesNoDialog.Callback;
import com.generic.DBClass;
import com.generic.Parameter;
import com.generic.ResourceManager;
import com.generic.Row;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.qryColumn;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.utilsVaadinPrintHandler;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Window.Notification;
import com.windows.frmMainMenus;

public class frmPurOrderManag implements transactionalForm {
	private AbstractLayout parentLayout = null;
	private String titleStr = "Store Recipt Voucher : Order No :";
	private Label txtTitle = new Label(titleStr);

	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private GridLayout headerLayout = new GridLayout(4, 1);

	private TextField txtOrdNo = new TextField("Order No ", "");
	private TextField txtOrdAccount = new TextField("Order Account ", "");
	private DateField txtOrdDate = new DateField("Order Date ");
	private TextField txtOrdRef_name = new TextField("Order Reference ", "");
	private TextField txtOrdAmt = new TextField("Order Amount");
	private TextField txtOrdDiscAmt = new TextField("Discount Amount");
	private TextField txtNetAmount = new TextField("Net Amount");
	private TextField txtReciptNo = new TextField("SRV No");
	private TextField txtLocation = new TextField("Location ");
	private DateField txtReciptDate = new DateField("Recieved Date");
	private ComboBox txtStore = new ComboBox("Store");
	private String varLocationCode = "";
	private double varReciptkeyfld = -1;
	private double varPurtkeyfld = -1;
	private double varOrdAmt = 0;
	private double varOrdDiscamt = 0;
	private String varOrdRef = "";
	private String varOrdAccount = "";
	private double varReciptNo = -1;
	private Map<String, String> mapActionStrs = new HashMap<String, String>();
	private Table tbl = new Table();
	private localTableModel data = new localTableModel();
	private Button cmdSave = new Button("Save & Back");
	private Button cmdBack = new Button("Cancel SRV");
	private Button cmdRecievedAllqty = new Button("Recieved All");
	private Button cmdList = new Button("List SRV");
	private Button cmdPrint = new Button("Print");

	private String QRYSES = "";
	private String QRYRCPTKEYFLD = "";
	private PreparedStatement ps_data = null;
	private ResultSet rs_data = null;

	final String colPos = "Pos";
	final String colItem = "Item Code";
	final String colItemName = "Item Descr";
	final String colOrdQty = "Ord PackQty";
	final String colOrdFreeqty = "Ord Free Qty";
	final String colReQty = "Rcvd Qty";
	final String colRcvFreeqty = "Rcvd Free Qty";
	final String colAdjQty = "Adjustment";
	final String colPrdDate = "Prod Date";
	final String colExpDate = "Exp Date";
	final String colPack = "PACK";
	final String colPackingDescr = "PACKING DESCR";

	private Window wndList = new Window();
	private VerticalLayout listlayout = new VerticalLayout();
	private boolean listenerAdded = false;
	private Connection con = null;

	public AbstractLayout getParentLayout() {
		return parentLayout;
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void showPurOrderManagement(AbstractLayout abs) {
		this.parentLayout = abs;

		showPurOrderManagement();
	}

	public void showPurOrderManagement() {
		mapActionStrs.clear();
		mapActionStrs.put("create_srv", "Create New S.R.V..");
		mapActionStrs.put("edit_srv", "Edit S.R.V");
		mapActionStrs.put("delete_srv", "Delete S.R.V");

		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		try {
			con = Channelplus3Application.getInstance().getFrmUserLogin()
					.getDbc().getDbConnection();
			Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
					.getDbConnection().setAutoCommit(false);
			final QueryView qv = new QueryView(Channelplus3Application
					.getInstance().getFrmUserLogin().getDbc().getDbConnection());
			qv
					.setSqlquery("select ordacc Order_Acc,acaccount.name order_no,ord_no,ATTN,ord_ref,ord_refnm,ord_amt-ORD_DISCAMT Order_amount"
							+ " ,case when orderdqty>0 then to_char(round((100/orderdqty)*(deliveredqty+delivered_freeqty),2))||'%' else '100%' end Recieved,"
							+ " case when orderdqty>0 then round((100/orderdqty)*(deliveredqty+delivered_freeqty),2) else 100 end RECIEVED_NUM,ORD_FLAG,"
							+ " decode(ord_flag,2,'Open',1,'Closed',0,'Not Approved') status,ord_date from order1,acaccount "
							+ " where ordacc=accno and ord_code=103 and ordacc is not null and ord_flag=:status order by ordacc,ord_no ");
			centralPanel.addComponent(qv);
			centralPanel.setHeight("100%");
			qv.setHeight("100%");
			Parameter pm = new Parameter("status");
			pm.setDescription("Status ");
			pm.getLovList().add(new dataCell("Opened", "2"));
			pm.getLovList().add(new dataCell("Closed", "1"));
			pm.getLovList().add(new dataCell("Not Approved", "0"));
			pm.setValue("2");
			qv.addParameter(pm);
			qv.reportSetting.doStandard();
			qv.reportSetting.setTitle("Outstanding Orders");
			qv.setHideCols(new String[] { "RECIEVED_NUM", "ORD_FLAG",
					"ORDER_ACC" });
			qv.getListGroupsBy().add("ORDER_NO");
			qv.getDataListners().add(new tableDataListner() {

				public String getCellStyle(qryColumn qc, int recordNo) {
					return null;
				}

				public String calcSummary(List<String> qcGroup, qryColumn qc) {
					return "";
				}

				public void beforeQuery() {

				}

				public void afterQuery() {
					qv.getLctb().getColByName("ORDER_AMOUNT").setNumberFormat(
							Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb().getColByName("ATTN").setTitle("Supp.Inv");

				}

				public void afterVisual() {
					qv.getTable().setHeight("100%");
					qv.setColumnWidth("ORD_REFNM", 120);
					qv.setColumnWidth("ORD_REF", 40);
					qv.setColumnWidth("ORDER_AMOUNT", 60);
					qv.setColumnWidth("ORD_NO", 40);
					qv.getTable().addActionHandler(new Handler() {
						public void handleAction(Action action, Object sender,
								Object target) {
							int rowno = Integer.valueOf(target.toString());
							double flg = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "ORD_FLAG"))
									.doubleValue();
							QRYSES = qv.getLctb()
									.getFieldValue(rowno, "ORD_NO").toString();

							if (action.getCaption().equals(
									mapActionStrs.get("create_srv"))
									&& target != null) {
								QRYRCPTKEYFLD = "";
								initForm();
							}
							if (action.getCaption().equals(
									mapActionStrs.get("edit_srv"))) {
								try {
									showReciptList();
								} catch (Exception e) {
									e.printStackTrace();
								}
								// initForm();
							}

							if (action.getCaption().equals(
									mapActionStrs.get("delete_srv"))) {
								try {
									deleteSrv();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}

						public Action[] getActions(Object target, Object sender) {

							if (target == null) {
								return null;
							}
							List<Action> acts = new ArrayList<Action>();
							int rowno = Integer.valueOf(target.toString());
							if (rowno < 0) {
								return null;
							}
							double flg = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "ORD_FLAG"))
									.doubleValue();
							double rcvd = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "RECIEVED_NUM"))
									.doubleValue();
							if (rcvd >= 0.00000001 && rcvd < 100 && flg == 2) {
								acts.clear();
								acts.add(new Action(mapActionStrs
										.get("create_srv")));
								acts.add(new Action(mapActionStrs
										.get("edit_srv")));
								acts.add(new Action(mapActionStrs
										.get("delete_srv")));
							}
							if (flg == 1) {
								acts.clear();
							}
							if (flg == 2 && rcvd >= 100) {
								acts.clear();
								acts.add(new Action(mapActionStrs
										.get("edit_srv")));
								acts.add(new Action(mapActionStrs
										.get("delete_srv")));
							}
							if (rcvd <= 0 && flg == 2) {
								acts.clear();
								acts.add(new Action(mapActionStrs
										.get("create_srv")));
							}
							Action[] act_array = new Action[acts.size()];
							return acts.toArray(act_array);
						}
					});

				}
			});
			qv.createView();
			centralPanel.requestRepaintAll();
		} catch (Exception ex) {
			Logger.getLogger(frmMainMenus.class.getName()).log(Level.SEVERE,
					null, ex);
			centralPanel.getWindow().showNotification("Error loading form ",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);

		}
	}

	public void deleteSrv() throws SQLException {
		final Callback cb = new Callback() {

			public void onDialogResult(boolean resultIsYes) {
				if (resultIsYes) {
					PreparedStatement ps_del2;
					try {
						con.setAutoCommit(false);
						ps_del2 = con
								.prepareStatement("declare a1 number; a2 number;a3 number;"
										+ " cursor det is select *from invoice2 where keyfld=?;"
										+ " begin for x in det loop repair.outstore(x.stra,x.refer,x.allqty+x.free_allqty,x.prd_date,x.exp_date);"
										+ " repair.decitemcount(x.refer); update order2 set deliveredqty=deliveredqty-x.allqty,"
										+ " delivered_freeqty=delivered_freeqty-x.free_allqty where ord_code=103 and ord_no=? "
										+ " and ord_pos=x.slsmnxx;end loop;"
										+ " delete from invoice2 where keyfld=?;"
										+ " delete from invoice1 where keyfld=?; "
										+ " delete from acvoucher1 where refertype=11 and refercode=13 and referkeyfld=?;"
										+ " delete from acvoucher2 where refertype=11 and refercode=13 and referkeyfld=?;"
										+ " select sum(deliveredqty),sum(delivered_freeqty) into a1,a2 "
										+ " from order2 where ord_code=103 and ord_no=?; "
										+ " select max(keyfld) into a3 from invoice1 where orderno=? and invoice_code=13;"
										+ " update order1 set deliveredqty=a1 ,delivered_freeqty=a2,recipt_keyfld=a3"
										+ " where ord_code=103 and ord_no=?;"
										+ " UPDATE ORDER2 SET RECIPT_KEYFLD=A3 WHERE ORD_CODE=103 AND ORD_NO=?;end;");

						ps_del2.setDouble(1, varReciptkeyfld);
						ps_del2.setDouble(2, Double.valueOf(QRYSES));
						ps_del2.setDouble(3, varReciptkeyfld);
						ps_del2.setDouble(4, varReciptkeyfld);
						ps_del2.setDouble(5, varReciptkeyfld);
						ps_del2.setDouble(6, varReciptkeyfld);
						ps_del2.setDouble(7, Double.valueOf(QRYSES));
						ps_del2.setDouble(8, Double.valueOf(QRYSES));
						ps_del2.setDouble(9, Double.valueOf(QRYSES));
						ps_del2.setDouble(10, Double.valueOf(QRYSES));
						ps_del2.executeUpdate();
						ps_del2.close();
						con.commit();
						resetFormLayout();
						showPurOrderManagement();
					} catch (SQLException ex) {
						Logger.getLogger(frmMainMenus.class.getName()).log(
								Level.SEVERE, null, ex);
						mainLayout.getWindow().showNotification(
								"Error deleting: ", ex.toString(),
								Notification.TYPE_ERROR_MESSAGE);

						try {
							con.rollback();
						} catch (SQLException e1) {
						}
					}

				}
			}
		};
		wndList.setWidth("70%");
		wndList.setHeight("70%");
		wndList.setImmediate(true);
		wndList.setModal(true);
		wndList.setContent(listlayout);
		listlayout.setSizeFull();
		DBClass dbcx = new DBClass(con);
		listlayout.removeAllComponents();
		TableView tblview = new TableView(dbcx);
		final TableView tblfinal = tblview;
		String sql = "select orderno,to_char(invoice_date,'dd/mm/rrrr') invoice_date,inv_refnm,keyfld from INVOICE1 where orderno="
				+ QRYSES + " AND invoice_code=13 order by keyfld";
		tblview.FetchSql(sql);
		tblview.setParentPanel(listlayout);
		tblview.setOnseleciton(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				QRYRCPTKEYFLD = tblfinal.getData().getFieldValue(
						tblfinal.getSelectionValue(), "KEYFLD").toString();
				varReciptkeyfld = Double.valueOf(QRYRCPTKEYFLD);
				String rfrname = tblfinal.getData().getFieldValue(
						tblfinal.getSelectionValue(), "INV_REFNM").toString();
				parentLayout.getWindow().removeWindow(wndList);
				parentLayout.getWindow().addWindow(
						new YesNoDialog("Channel Plus Warning",
								"Are you sure you want to delete Recipt for "
										+ rfrname + " ? ", cb));
			}
		});

		tblview.createView();

		if (!parentLayout.getWindow().getChildWindows().contains(wndList)) {
			parentLayout.getWindow().addWindow(wndList);
		}

	}

	public void createView() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		resetFormLayout();

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdBack);
		ResourceManager.addComponent(commandLayout, cmdRecievedAllqty);
		ResourceManager.addComponent(commandLayout, cmdList);
		ResourceManager.addComponent(commandLayout, cmdPrint);

		txtOrdNo.setWidth("100%");
		txtOrdAccount.setWidth("100%");
		txtOrdAmt.setWidth("100%");
		txtOrdRef_name.setWidth("100%");
		txtReciptNo.setWidth("100%");
		txtOrdDiscAmt.setWidth("100%");
		txtOrdDate.setWidth("100%");
		txtNetAmount.setWidth("100%");
		txtLocation.setWidth("100%");
		txtReciptDate.setWidth("100%");
		txtStore.setWidth("100%");
		txtNetAmount.addStyleName("netAmtStyle");
		txtOrdAmt.addStyleName("netAmtStyle");
		txtOrdDiscAmt.addStyleName("netAmtStyle");
		txtStore.setNullSelectionAllowed(false);

		tbl.setWidth("100%");
		tbl.setHeight("100%");

		tbl.setImmediate(true);
		tbl.setMultiSelect(false);
		tbl.setColumnReorderingAllowed(false);
		tbl.setSelectable(true);
		tbl.setSortDisabled(true);
		txtTitle.addStyleName("formTitle");
		txtTitle.setHeight("100%");

		txtOrdDate.setDateFormat(utils.FORMAT_SHORT_DATE);
		txtReciptDate.setDateFormat(utils.FORMAT_SHORT_DATE);
		txtOrdDate.setResolution(DateField.RESOLUTION_DAY);
		txtReciptDate.setResolution(DateField.RESOLUTION_DAY);

		headerLayout.setWidth("100%");

		headerLayout.addComponent(txtLocation);
		headerLayout.addComponent(txtOrdAccount);
		headerLayout.addComponent(txtOrdNo);
		headerLayout.addComponent(txtReciptNo);
		headerLayout.addComponent(txtOrdDate);
		headerLayout.addComponent(txtOrdRef_name);
		headerLayout.addComponent(txtOrdAmt);
		headerLayout.addComponent(txtOrdDiscAmt);
		headerLayout.addComponent(txtNetAmount);
		headerLayout.addComponent(txtReciptDate);
		headerLayout.addComponent(txtStore);

		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		headerLayout.setHeight("100%");
		commandLayout.setHeight("100%");

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, txtTitle);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, headerLayout);
		ResourceManager.addComponent(mainLayout, tbl);
		mainLayout.setExpandRatio(tbl, 3.0f);
		mainLayout.setExpandRatio(txtTitle, .2f);
		mainLayout.setExpandRatio(commandLayout, .3f);
		mainLayout.setExpandRatio(headerLayout, 1.5f);
		try {
			utilsVaadin.FillCombo(txtStore,
					"select no,name from store where flag=1 order by no", con);
		} catch (SQLException e) {

		}
		if (!listenerAdded) {
			cmdSave.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					save_data();
					resetFormLayout();
					showPurOrderManagement();

				}
			});
			cmdBack.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					tbl.removeAllItems();
					resetFormLayout();
					showPurOrderManagement();
				}
			});

			cmdRecievedAllqty.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					recieved_allqty();
				}
			});
			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						showReciptList();
					} catch (Exception e) {

					}

				}
			});
			tbl.setCellStyleGenerator(new CellStyleGenerator() {

				public String getStyle(Object itemId, Object propertyId) {
					if (propertyId != null && propertyId.equals(colOrdQty)) {
						return "centeralign";
					}

					if (propertyId != null && propertyId.equals(colAdjQty)) {
						return "centeralign";
					}
					if (propertyId != null && propertyId.equals(colPos)) {
						return "centeralign";
					}
					if (propertyId != null && propertyId.equals(colOrdFreeqty)) {
						return "centeralign";
					}

					return null;
				}
			});
			cmdPrint.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					print_data();

				}
			});
			listenerAdded = true;
		}

	}

	public void init() {

	}

	public void resetFormLayout() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		mainLayout.removeAllComponents();
		headerLayout.removeAllComponents();
		commandLayout.removeAllComponents();
		centralPanel.removeAllComponents();
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		if (data.getDbclass() == null) {
			try {
				data.createDBClassFromConnection(con);
			} catch (SQLException e) {
			}
		}

		load_data();
	}

	private void setFieldsReadOnly(boolean tf) {
		txtOrdNo.setReadOnly(tf);
		txtOrdAccount.setReadOnly(tf);
		txtOrdAmt.setReadOnly(tf);
		txtOrdRef_name.setReadOnly(tf);
		txtReciptNo.setReadOnly(tf);
		txtOrdDiscAmt.setReadOnly(tf);
		txtOrdDate.setReadOnly(tf);
		txtNetAmount.setReadOnly(tf);
		txtLocation.setReadOnly(tf);
		// txtLocation.removeStyleName("readonly");
	}

	public boolean showReciptList() throws Exception {

		wndList.setWidth("70%");
		wndList.setHeight("70%");
		wndList.setImmediate(true);
		wndList.setModal(true);
		wndList.setContent(listlayout);
		listlayout.setSizeFull();
		DBClass dbcx = new DBClass(con);
		listlayout.removeAllComponents();
		TableView tblview = new TableView(dbcx);
		final TableView tblfinal = tblview;
		String sql = "select orderno,to_char(invoice_date,'dd/mm/rrrr') invoice_date,inv_refnm,keyfld from"
				+ " INVOICE1 where orderno="
				+ QRYSES
				+ " AND invoice_code=13 order by keyfld";		
		tblview.FetchSql(sql);
		tblview.setParentPanel(listlayout);
		tblview.setOnseleciton(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				QRYRCPTKEYFLD = tblfinal.getData().getFieldValue(
						tblfinal.getSelectionValue(), "KEYFLD").toString();
				initForm();
				load_data();
				parentLayout.getWindow().removeWindow(wndList);
			}
		});

		tblview.createView();

		if (!parentLayout.getWindow().getChildWindows().contains(wndList)) {
			parentLayout.getWindow().addWindow(wndList);
		}

		return true;
	}

	public void load_data() {
		setFieldsReadOnly(false);
		// String s=utils.getSqlValue("", con);
		txtOrdAmt.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(0)));
		txtOrdNo.setValue("");
		txtOrdDiscAmt
				.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(0)));
		txtNetAmount
				.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(0)));
		txtOrdRef_name.setValue("");
		Date dt = new Date();
		dt.setTime(System.currentTimeMillis());
		txtOrdDate.setValue(new Date());
		txtReciptDate.setValue(new java.util.Date());
		varReciptkeyfld = -1;
		varOrdAmt = 0;
		varOrdDiscamt = 0;
		data.clearALl();
		tbl.removeAllItems();
		try {
			if (QRYSES.length() > 0) {
				ps_data = con
						.prepareStatement(
								"select order1.*,locations.name location_name from order1,locations"
										+ " where location_code=locations.code and ord_code=103 and ord_no="
										+ QRYSES,
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				rs_data = ps_data.executeQuery();

				if (rs_data.first()) {
					if (QRYRCPTKEYFLD.length() == 0) {
						varReciptkeyfld = -1;
						varReciptNo = Integer.valueOf(utils.getSqlValue(
								"select nvl(max(invoice_no),0)+1 from invoice1 where "
										+ "invoice_code=13 and location_code='"
										+ varLocationCode + "'", con));

					} else {

						varReciptkeyfld = Double.valueOf(QRYRCPTKEYFLD);
						PreparedStatement pstmp1 = con.prepareStatement(
								"select *from invoice1 where keyfld="
										+ QRYRCPTKEYFLD,
								ResultSet.TYPE_SCROLL_SENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
						ResultSet rstmp1 = pstmp1.executeQuery();
						if (rstmp1.first()) {
							varReciptNo = rstmp1.getDouble("INVOICE_NO");
							((Date) txtReciptDate.getValue()).setTime(rstmp1
									.getDate("INVOICE_DATE").getTime());

						} else {
							throw new SQLException("Not found Recipt voucher");
						}
					}

					varLocationCode = rs_data.getString("location_code");
					varOrdAmt = rs_data.getDouble("ord_amt");
					varOrdDiscamt = rs_data.getDouble("ord_discamt");
					varOrdAccount = rs_data.getString("ordacc");
					varOrdRef = rs_data.getString("ord_ref");
					txtLocation.setValue(rs_data.getString("location_name"));
					txtOrdAmt.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
							.format(varOrdAmt)));
					txtOrdDiscAmt.setValue((new DecimalFormat(
							Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(varOrdDiscamt)));
					((java.util.Date) txtOrdDate.getValue()).setTime(rs_data
							.getDate("ord_date").getTime());
					txtOrdNo.setValue(rs_data.getString("ord_no"));
					txtNetAmount
							.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
									.format(varOrdAmt - varOrdDiscamt)));
					String s = utils.getSqlValue(
							"select name from acaccount where accno='"
									+ varOrdAccount + "'", con);
					txtOrdAccount.setValue(s);
					txtOrdRef_name.setValue(rs_data.getString("ord_refnm")
							+ "-" + rs_data.getString("ord_ref"));

					txtReciptNo.setValue(String.valueOf(varReciptNo));
					String ds = utils
							.getSqlValue(
									"select repair.getsetupvalue_2('DEFAULT_STORE') from dual",
									con);
					txtStore.setValue(utilsVaadin.findByValue(txtStore, ds));
					ps_data.close();
					fetch_table_data();
					txtTitle.setValue(titleStr + " "
							+ txtOrdAccount.getValue().toString());
				} else {
					throw new Exception(
							"Order can not be found ! may some other user have deleted this order");
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(frmPurOrderManag.class.getName()).log(
					Level.SEVERE, null, ex);
			parentLayout.getWindow().showNotification(ex.toString(),
					Notification.TYPE_ERROR_MESSAGE);
		}
		setFieldsReadOnly(true);
	}

	public void fetch_table_data() throws Exception {
		tbl.removeAllItems();
		data.clearALl();
		String sql1 = "";

		sql1 = "select order2.*"
				+ " ,nvl(items.descra,ITEMS.DESCR) DESCR,"
				+ " ((ord_allqty-order2.DELIVEREDQTY)/ord_pack) rcvd_qty,"
				+ " ((ord_freeallqty-order2.DELIVERED_FREEQTY)/ord_pack) rcvd_freeqty,"
				+ " items.prd_dt prd_date,items.exp_dt exp_date,"
				+ " ORD_ALLQTY/ORD_PACK ORD_TOTPK,ORD_FREEALLQTY/ORD_PACK ORD_TOTPKFREE,"
				+ "ORD_PACK ,PACKING_DESCR from order2,items where "
				+ " reference=ord_refer and ord_code=103 and ord_no=" + QRYSES
				+ " order by ord_pos";

		data.executeQuery(sql1, true);

		if (QRYRCPTKEYFLD.length() > 0) {
			for (int i = 0; i < data.getRows().size(); i++) {
				data.setFieldValue(i, "RCVD_QTY", BigDecimal.valueOf(0));
				data.setFieldValue(i, "RCVD_FREEQTY", BigDecimal.valueOf(0));
			}
			PreparedStatement psi2 = con
					.prepareStatement(
							"select pack,allqty,pkqty,qty,free_allqty,free_pkqty,free_qty,slsmnxx,refer,prd_date,exp_date from invoice2 "
									+ "where keyfld=" + varReciptkeyfld,
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ResultSet rsi2 = psi2.executeQuery();
			rsi2.beforeFirst();
			while (rsi2.next()) {
				Row r = data.findRow(data.getColByName("ORD_POS").getColpos(),
						rsi2.getString("SLSMNXX"));
				if (r != null) {
					int rn = data.getRows().indexOf(r);
					double v = rsi2.getDouble("ALLQTY")
							/ rsi2.getDouble("pack");
					data.setFieldValue(rn, "RCVD_QTY", BigDecimal.valueOf(v));

					v = ((((BigDecimal) data.getFieldValue(rn, "DELIVEREDQTY"))
							.doubleValue() / rsi2.getDouble("pack")) - v)
							* rsi2.getDouble("pack");

					data.setFieldValue(rn, "DELIVEREDQTY", BigDecimal
							.valueOf(v));
					// }
					v = rsi2.getDouble("FREE_ALLQTY") / rsi2.getDouble("pack");
					data.setFieldValue(rn, "RCVD_FREEQTY", BigDecimal
							.valueOf(v));

					v = ((((BigDecimal) data.getFieldValue(rn,
							"DELIVERED_FREEQTY")).doubleValue() / rsi2
							.getDouble("pack")) - v)
							* rsi2.getDouble("pack");

					data.setFieldValue(rn, "DELIVERED_FREEQTY", BigDecimal
							.valueOf(v));
					data.setFieldValue(rn, "PRD_DATE", rsi2
							.getObject("PRD_DATE"));
					data.setFieldValue(rn, "EXP_DATE", rsi2
							.getObject("EXP_DATE"));
					// }
				}
			}
		}

		tbl.addContainerProperty(colPos, String.class, null);
		tbl.addContainerProperty(colItem, String.class, null);
		tbl.addContainerProperty(colItemName, String.class, null);
		tbl.addContainerProperty(colOrdQty, String.class, null);
		tbl.addContainerProperty(colOrdFreeqty, String.class, null);
		tbl.addContainerProperty(colReQty, TextField.class, null);
		tbl.addContainerProperty(colRcvFreeqty, TextField.class, null);
		tbl.addContainerProperty(colAdjQty, String.class, null);
		tbl.addContainerProperty(colPrdDate, DateField.class, null);
		tbl.addContainerProperty(colExpDate, DateField.class, null);
		tbl.addContainerProperty(colPack, String.class, null);
		tbl.addContainerProperty(colPackingDescr, String.class, null);

		for (int i = 0; i < data.getRows().size(); i++) {
			final Object ar[] = new Object[12];
			ar[0] = ((BigDecimal) data.getFieldValue(i, "ORD_POS")).toString();
			ar[1] = data.getFieldValue(i, "ORD_REFER");
			ar[2] = data.getFieldValue(i, "DESCR");
			ar[3] = ((BigDecimal) data.getFieldValue(i, "ORD_TOTPK"))
					.toString();
			ar[4] = data.getFieldValue(i, "ORD_TOTPKFREE");
			ar[5] = (Object) new TextField("", ((BigDecimal) data
					.getFieldValue(i, "RCVD_QTY")).toString());
			ar[6] = (Object) new TextField("", ((BigDecimal) data
					.getFieldValue(i, "RCVD_FREEQTY")).toString());
			ar[7] = Double.valueOf(0);
			ar[8] = new DateField("", new Date(((Timestamp) data.getFieldValue(
					i, "PRD_DATE")).getTime()));
			ar[9] = new DateField("", new Date(((Timestamp) data.getFieldValue(
					i, "EXP_DATE")).getTime()));
			ar[10] = ((BigDecimal) data.getFieldValue(i, "ORD_PACK"))
					.toString();
			ar[11] = data.getFieldValue(i, "PACKING_DESCR");

			((TextField) ar[5]).setImmediate(true);
			((TextField) ar[6]).setImmediate(true);

			((DateField) ar[8]).setImmediate(true);
			((DateField) ar[9]).setImmediate(true);
			((DateField) ar[8]).setResolution(DateField.RESOLUTION_DAY);
			((DateField) ar[9]).setResolution(DateField.RESOLUTION_DAY);
			((DateField) ar[8]).setDateFormat(utils.FORMAT_SHORT_DATE);
			((DateField) ar[9]).setDateFormat(utils.FORMAT_SHORT_DATE);

			final int rn = i;
			ValueChangeListener vc = new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					double adj = 0;
					BigDecimal bd = BigDecimal
							.valueOf(Double.valueOf(((TextField) ar[5])
									.getValue().toString()));
					data.setFieldValue(rn, "RCVD_QTY", bd);
					BigDecimal bd_f = BigDecimal
							.valueOf(Double.valueOf(((TextField) ar[6])
									.getValue().toString()));

					data.setFieldValue(rn, "RCVD_FREEQTY", bd_f);

					java.sql.Timestamp prd_dt = ((java.sql.Timestamp) data
							.getFieldValue(rn, "PRD_DATE"));
					java.sql.Timestamp exp_dt = ((java.sql.Timestamp) data
							.getFieldValue(rn, "EXP_DATE"));
					prd_dt.setTime(((Date) ((DateField) ar[8]).getValue())
							.getTime());
					exp_dt.setTime(((Date) ((DateField) ar[9]).getValue())
							.getTime());
					double ordqt = ((BigDecimal) data.getFieldValue(rn,
							"ORD_ALLQTY")).doubleValue();
					ordqt = ordqt
							+ ((BigDecimal) data.getFieldValue(rn,
									"ORD_FREEALLQTY")).doubleValue();
					double dlv_qty = ((BigDecimal) data.getFieldValue(rn,
							"DELIVEREDQTY")).doubleValue();
					dlv_qty = dlv_qty
							+ ((BigDecimal) data.getFieldValue(rn,
									"DELIVERED_FREEQTY")).doubleValue();

					Item itm = tbl.getItem((rn));
					if (itm != null) {
						double pk = ((BigDecimal) data.getFieldValue(rn,
								"ORD_PACK")).doubleValue();
						dlv_qty = dlv_qty
								+ (Double.valueOf(((TextField) ar[5])
										.getValue().toString()) * pk);
						dlv_qty = dlv_qty
								+ (Double.valueOf(((TextField) ar[6])
										.getValue().toString()) * pk);
						itm.getItemProperty(colAdjQty).setValue(
								String.valueOf((dlv_qty - ordqt) / pk));

					}
				}
			};

			((TextField) ar[5]).addListener(vc);
			((TextField) ar[6]).addListener(vc);
			((DateField) ar[8]).addListener(vc);
			((DateField) ar[9]).addListener(vc);

			((TextField) ar[5]).addStyleName("rightalign");
			((TextField) ar[6]).addStyleName("rightalign");
			((TextField) ar[5]).setWidth("100%");
			((TextField) ar[6]).setWidth("100%");
			((DateField) ar[8]).setWidth("100%");
			((DateField) ar[9]).setWidth("100%");

			// executing change event
			tbl.addItem(ar, Integer.valueOf(i));
			vc.valueChange(null);
		}
		tbl.setColumnWidth(colItemName, 200);
		tbl.setColumnWidth(colPos, 25);
		tbl.setColumnWidth(colItem, 75);
		tbl.setColumnWidth(colAdjQty, 75);
		tbl.setColumnWidth(colOrdFreeqty, 75);
		tbl.setColumnWidth(colOrdQty, 75);
		tbl.setColumnWidth(colRcvFreeqty, 75);
		tbl.setColumnWidth(colReQty, 75);
		tbl.setColumnWidth(colPrdDate, 100);
		tbl.setColumnWidth(colExpDate, 100);

	}

	public void recieved_allqty() {
		for (Iterator iterator = tbl.getItemIds().iterator(); iterator
				.hasNext();) {
			final Integer idno = (Integer) iterator.next();
			final Item itm = tbl.getItem(idno);
			if (itm != null) {
				double pk = (((BigDecimal) data.getFieldValue(idno, "ORD_PACK"))
						.doubleValue());
				TextField t = (TextField) itm.getItemProperty(colReQty)
						.getValue();
				Double f = (((BigDecimal) data.getFieldValue(idno, "ORD_TOTPK"))
						.doubleValue());
				t.setValue(f);
				t = (TextField) itm.getItemProperty(colRcvFreeqty).getValue();
				f = ((BigDecimal) data.getFieldValue(idno, "ORD_TOTPKFREE"))
						.doubleValue();
				t.setValue(f);
			}

		}
	}

	public void print_data() {

		try {
			if (cmdSave.isEnabled()) {
				save_data(false, true);
			} else {
				utilsVaadinPrintHandler.printSRV(
						Integer.valueOf(QRYRCPTKEYFLD), Double.valueOf(QRYSES),
						con);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean CheckBelowDeniedQty() {
		return true;
	}

	public void save_data() {
		save_data(true, false);
	}

	public void save_data(boolean backlist, boolean doprint) {
		if (QRYSES.length() == 0) {
			return;
		}

		String sql1 = "";
		String sql2 = "";

		sql1 = "";

		// 1=periodcode 2=locatoin_code 3=invoice_no 4=invoice_date, 5=stra
		// 6=inv_ref, 7=inv_refnm, 8=orderno,9=c_cus_no,10-branchno
		sql1 = ""
				+ " declare a1 number; a2 number; "
				+ " begin insert into invoice1(PERIODCODE, LOCATION_CODE, INVOICE_NO,"
				+ " INVOICE_CODE, TYPE, INVOICE_DATE, STRA, INV_REF, INV_REFNM, "
				+ " INV_AMT, DISC_AMT, INV_COST, CREATDT, ORDERNO,C_CUS_NO,C_BRANCH_NO,YEAR,KEYFLD) values "
				+ " (?,?,?,13,11,?,?,?,?,0,0,0,sysdate,?,?,?,'2003',?);select sum(deliveredqty),sum(delivered_freeqty) into a1,a2 "
				+ " from order2 where ord_code=103 and ord_no=?; update order1 set deliveredqty=a1 ,delivered_freeqty=a2,recipt_keyfld=? "
				+ " where ord_code=103 and ord_no=?;end;";
		// 1=periodcode, 2=location_code 3=invoice_no
		// 4=itempos 5=refer 6=stra
		// 7=price 8=pkcost 9=pack
		// 10=packd 11=unitd 12=dat
		// 13=qty 14=pkqty 15=allqty
		// 16=prd_date 17=exp_date 18=year
		// 19=ordwas 20=keyfld 21=additional_amt
		// 22=orderno 23=qtyin 24=qtyout
		// 25=slsmnxx 26=free_qty 27=free_pkqty 28=free_allqty
		// 29=store 30=
		// 
		sql2 = "begin insert into invoice2"
				+ "   (PERIODCODE, LOCATION_CODE, INVOICE_NO,"
				+ "  INVOICE_CODE, TYPE, ITEMPOS, REFER, STRA, PRICE,"
				+ "  PKCOST, PACK, PACKD, UNITD, DAT, QTY, PKQTY, ALLQTY, PRD_DATE, EXP_DATE, "
				+ " YEAR, ORDWAS, KEYFLD, ADDITIONAL_AMT, CREATDT, ORDERNO, QTYIN, QTYOUT, SLSMNXX,"
				+ " FREE_QTY, FREE_PKQTY, FREE_ALLQTY) values "
				+ " (?,?,?,13,11,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,?,?);"
				+ " repair.instore(?,?,?,?,?);repair.incitemcount(?);" + "end;";
		try {
			con.setAutoCommit(false);
			boolean is_creating = false;
			// getting current period code
			String pcode = utils.getSqlValue(
					"select repair.getcurperiodcode from dual", con);
			java.sql.Date inv_dt = new java.sql.Date(
					((java.util.Date) txtReciptDate.getValue()).getTime());
			String accno = utils.getSqlValue(
					"select ac_no from c_ycust where code='" + varOrdRef + "'",
					con);
			// statment for prd_date,exp_date,cost
			PreparedStatement ps_tmp1 = con.prepareStatement(
					"select prd_dt,exp_dt,pkaver from items where reference=?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ps_data = con.prepareStatement(
					"select *from order1 where ord_code=103 and ord_no="
							+ QRYSES, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs_data = ps_data.executeQuery();

			if (varReciptkeyfld == -1) {
				varReciptkeyfld = Double.valueOf(utils.getSqlValue(
						"select nvl(max(keyfld),0)+1 from invoice1", con));
				varReciptNo = Double.valueOf(utils.getSqlValue(
						"select nvl(max(invoice_no),0)+1 from invoice1 "
								+ "where invoice_code=13 and location_code='"
								+ varLocationCode + "'", con));
				is_creating = true;
			} else {
				varReciptNo = Integer.valueOf(utils.getSqlValue(
						"select invoice_no from invoice1 where " + " keyfld="
								+ varReciptkeyfld, con));
				is_creating = false;
			}
			if (!CheckBelowDeniedQty()) {
				return;
			}
			PreparedStatement ps_del2 = con
					.prepareStatement("declare a1 number; a2 number;a3 number;"
							+ " cursor det is select *from invoice2 where keyfld=?;"
							+ " begin for x in det loop repair.outstore(x.stra,x.refer,x.allqty+x.free_allqty,x.prd_date,x.exp_date);"
							+ " repair.decitemcount(x.refer); update order2 set deliveredqty=deliveredqty-x.allqty,"
							+ " delivered_freeqty=delivered_freeqty-x.free_allqty where ord_code=103 and ord_no=? "
							+ " and ord_pos=x.slsmnxx;end loop;"
							+ " delete from invoice2 where keyfld=?;"
							+ " delete from invoice1 where keyfld=?;"
							+ " select sum(deliveredqty),sum(delivered_freeqty) into a1,a2 "
							+ " from order2 where ord_code=103 and ord_no=?; "
							+ " select max(keyfld) into a3 from invoice1 where orderno=? and invoice_code=13;"
							+ " update order1 set deliveredqty=a1 ,delivered_freeqty=a2,recipt_keyfld=a3"
							+ " where ord_code=103 and ord_no=?;"
							+ " UPDATE ORDER2 SET RECIPT_KEYFLD=A3 WHERE ORD_CODE=103 AND ORD_NO=?;end;");

			ps_del2.setDouble(1, varReciptkeyfld);
			ps_del2.setDouble(2, Double.valueOf(QRYSES));
			ps_del2.setDouble(3, varReciptkeyfld);
			ps_del2.setDouble(4, varReciptkeyfld);
			ps_del2.setDouble(5, Double.valueOf(QRYSES));
			ps_del2.setDouble(6, Double.valueOf(QRYSES));
			ps_del2.setDouble(7, Double.valueOf(QRYSES));
			ps_del2.setDouble(8, Double.valueOf(QRYSES));
			ps_del2.executeUpdate();
			ps_del2.close();
			PreparedStatement ps_ins2 = con.prepareStatement(sql2);
			double pos = 0;
			for (Iterator iterator = tbl.getItemIds().iterator(); iterator
					.hasNext();) {
				Integer idno = (Integer) iterator.next();
				Item itm = tbl.getItem(idno);
				// item product code and cost
				ps_tmp1.setString(1, (String) data.getFieldValue(idno,
						"ORD_REFER"));
				ResultSet rs_tmp1 = ps_tmp1.executeQuery();
				rs_tmp1.first();
				pos++;
				double l_pk = ((BigDecimal) data
						.getFieldValue(idno, "ORD_PACK")).doubleValue();
				double l_pk_qty = Double.valueOf(((TextField) itm
						.getItemProperty(colReQty).getValue()).getValue()
						.toString());
				double l_pk_allqty = Double.valueOf(((TextField) itm
						.getItemProperty(colReQty).getValue()).getValue()
						.toString())
						* l_pk;
				double l_pk_freeqty = Double.valueOf(((TextField) itm
						.getItemProperty(colRcvFreeqty).getValue()).getValue()
						.toString());
				double l_pk_freeallqty = Double.valueOf(((TextField) itm
						.getItemProperty(colRcvFreeqty).getValue()).getValue()
						.toString())
						* l_pk;
				double l_ord_allqty = Double.valueOf((String) itm
						.getItemProperty(colOrdQty).getValue())
						* l_pk;
				double l_ord_freeqty = Double.valueOf((String) itm
						.getItemProperty(colOrdFreeqty).getValue())
						* l_pk;

				java.sql.Timestamp l_prd_dt = ((java.sql.Timestamp) data
						.getFieldValue(idno, "PRD_DATE"));
				java.sql.Timestamp l_exp_dt = ((java.sql.Timestamp) data
						.getFieldValue(idno, "EXP_DATE"));
				// not to insert qty 0 recieved.
				if (l_pk_freeqty < 0 || l_pk_allqty < 0 || l_pk_freeallqty < 0
						|| l_pk_qty < 0 || l_pk_qty < 0) {
					throw new SQLException("Negative quantities not allowed");
				}
				if (l_pk_allqty + l_pk_freeallqty <= 0) {
					continue;
				}

				ps_ins2.setString(1, pcode);
				ps_ins2.setString(2, varLocationCode);
				ps_ins2.setDouble(3, varReciptNo);
				ps_ins2.setDouble(4, pos);
				ps_ins2.setString(5, (String) data.getFieldValue(idno,
						"ORD_REFER"));
				ps_ins2.setString(6, (String) ((dataCell) txtStore.getValue())
						.getValue());
				ps_ins2.setDouble(7, rs_tmp1.getDouble("pkaver"));// price
				ps_ins2.setDouble(8, rs_tmp1.getDouble("pkaver") / l_pk); // pkcost
				ps_ins2.setDouble(9, l_pk);
				ps_ins2.setString(10, (String) data.getFieldValue(idno,
						"ORD_PACKD"));
				ps_ins2.setString(11, (String) data.getFieldValue(idno,
						"ORD_UNITD"));
				ps_ins2.setDate(12, inv_dt);
				ps_ins2.setDouble(13, 0);
				ps_ins2.setDouble(14, l_pk_qty);
				ps_ins2.setDouble(15, l_pk_allqty);
				ps_ins2.setDate(16, new java.sql.Date(l_prd_dt.getTime()));
				ps_ins2.setDate(17, new java.sql.Date(l_exp_dt.getTime()));
				ps_ins2.setString(18, "2003");
				ps_ins2.setDouble(19, l_ord_freeqty);
				ps_ins2.setDouble(20, varReciptkeyfld);
				ps_ins2.setDouble(21, (l_ord_allqty + l_ord_freeqty) / l_pk);
				ps_ins2.setBigDecimal(22, (BigDecimal) data.getFieldValue(idno,
						"ORD_NO"));
				ps_ins2.setDouble(23, l_pk_allqty + l_pk_freeallqty);
				ps_ins2.setDouble(24, 0);
				ps_ins2.setBigDecimal(25, (BigDecimal) data.getFieldValue(idno,
						"ORD_POS"));
				ps_ins2.setDouble(26, 0);
				ps_ins2.setDouble(27, l_pk_freeqty);
				ps_ins2.setDouble(28, l_pk_freeallqty);
				// instore procedure
				ps_ins2.setString(29, (String) ((dataCell) txtStore.getValue())
						.getValue());
				ps_ins2.setString(30, (String) data.getFieldValue(idno,
						"ORD_REFER"));
				ps_ins2.setDouble(31, l_pk_allqty + l_pk_freeallqty);
				ps_ins2.setTimestamp(32, l_prd_dt);
				ps_ins2.setTimestamp(33, l_exp_dt);
				ps_ins2.setString(34, (String) data.getFieldValue(idno,
						"ORD_REFER"));
				utils.execSql("update order2 set deliveredqty=deliveredqty+"
						+ l_pk_allqty
						+ ",delivered_freeqty=delivered_freeqty+"
						+ l_pk_freeallqty
						+ " where "
						+ " ORD_CODE=103 AND ORD_NO="
						+ ((BigDecimal) data.getFieldValue(idno, "ORD_NO"))
								.toString()
						+ " and ord_pos="
						+ ((BigDecimal) data.getFieldValue(idno, "ORD_POS"))
								.toString(), con);
				ps_ins2.executeUpdate();
			}

			ps_tmp1.close();
			ps_ins2.close();

			PreparedStatement ps_ins1 = con.prepareStatement(sql1);
			ps_ins1.setString(1, pcode);
			ps_ins1.setString(2, varLocationCode);
			ps_ins1.setDouble(3, varReciptNo);
			ps_ins1.setDate(4, inv_dt);
			ps_ins1.setString(5, (String) ((dataCell) txtStore.getValue())
					.getValue());
			ps_ins1.setString(6, varOrdAccount);
			ps_ins1.setString(7, (String) txtOrdRef_name.getValue());
			ps_ins1.setString(8, (String) txtOrdNo.getValue());
			ps_ins1.setString(9, "");
			ps_ins1.setString(10, "1");
			ps_ins1.setDouble(11, varReciptkeyfld);
			ps_ins1.setString(12, (String) txtOrdNo.getValue());
			ps_ins1.setDouble(13, varReciptkeyfld);
			ps_ins1.setString(14, (String) txtOrdNo.getValue());
			ps_ins1.executeUpdate();

			ps_ins1.close();

			con.commit();

			parentLayout.getWindow().showNotification("Saved successful",
					Notification.TYPE_HUMANIZED_MESSAGE);
			if (doprint) {
				utilsVaadinPrintHandler.printSRV(
						Integer.valueOf(QRYRCPTKEYFLD), Double.valueOf(QRYSES),
						con);
			}
			if (backlist) {
				resetFormLayout();
				showPurOrderManagement();
			}

		} catch (Exception ex) {
			Logger.getLogger(frmPurOrderManag.class.getName()).log(
					Level.SEVERE, null, ex);
			parentLayout.getWindow().showNotification("Unable to save:",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}

	}

	public void showInitView() {
		showPurOrderManagement();
	}
}
