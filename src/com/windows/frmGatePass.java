package com.windows;

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
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog.Callback;
import com.generic.ColumnProperty;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.qryColumn;
import com.generic.rowTriggerListner;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.utilsVaadinPrintHandler;
import com.generic.localTableModel.DefaultValueListner;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
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

public class frmGatePass implements transactionalForm {
	private String titleStr = "Gate-pass ";
	private Label txtTitle = new Label(titleStr);
	private String QRYSES_LOCATION = "";
	private java.util.Date QRYSES_DATE = null;
	private Button cmdSave = new Button("Save & Back");
	private Button cmdBack = new Button("Cancel & Back");
	private Button cmdPrint = new Button("Print");
	private Button cmdAddItem = new Button("Add Item");
	private Button cmdRetItem = new Button("Ret.All Items");
	private Button cmdSaveTemp = new Button("Save Retun Qty");
	private Action actAddItem = new ShortcutAction("F9",
			ShortcutAction.KeyCode.F9, new int[] {});

	private TextField txtLocation = new TextField("Location");
	private TextField txtKeyfld = new TextField("No");
	private DateField txtTransDate = new DateField("Date");
	private TextField txtDescr = new TextField("Description");
	private Table tbl_items = new Table();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private GridLayout basicLayout = new GridLayout(2, 1);
	private String QRYSES = "";

	private Connection con = null;
	private PreparedStatement ps_g1 = null;
	private Map<String, String> mapVars = new HashMap<String, String>();
	private localTableModel data_items = new localTableModel();
	private boolean listenerAdded = false;

	private Window wndList = new Window();
	VerticalLayout lstLayout = new VerticalLayout();

	private float varKeyfld = -1;
	private String varLocationCode = "";
	private String varApprovedBy = "";
	private float varTransferKeyfld = -1;
	private float varRetTransferKeyfld = -1;
	private String varUserName = utils.CPUSER;
	private float varFlag = 0;
	private float varDefaultStore = 1;
	private String varStatus = "Creating";
	private BigDecimal varTmpQty = BigDecimal.valueOf(0);
	private Timestamp varTmpPrdDate = null;
	private Timestamp varTmpExpDate = null;
	private BigDecimal vartmpStore = BigDecimal.valueOf(0);

	private java.sql.Timestamp varCurrentTime = new Timestamp(System
			.currentTimeMillis());
	private java.sql.Timestamp varApprovedTime = null;
	private final List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();
	final Map<String, String> acts_item = new HashMap<String, String>();

	private String pcode = "";

	public String getQRYSES_LOCATION() {
		return QRYSES_LOCATION;
	}

	public void setQRYSES_LOCATION(String qRYSESLOCATION) {
		QRYSES_LOCATION = qRYSESLOCATION;
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public frmGatePass() {
		acts_item.put("change_item", "Change Item..");
		acts_item.put("delete_item", "Delete Record..");
		acts_item.put("put_qty", "Return variance..");

	}

	public void resetFormLayout() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		commandLayout.removeAllComponents();
		centralPanel.removeAllComponents();
	}

	public void showDailyGatePass(AbstractLayout abs) {
		this.parentLayout = abs;
		showDailyGatePass();
	}

	public void showDailyGatePass() {

		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		final String STAT_CLOSED = "CLOSED"; // flag=1
		final String STAT_OPENED = "OPENED"; // flag=2
		final String STAT_UNDER_PROCESS = "UNDER PROCESS"; // flag=0
		final String STAT_NOT_CREATED = "NOT CREATED";

		QRYSES = "";
		QRYSES_LOCATION = "";

		mapVars.clear();
		mapVars.put("create_gatepass", "Create New...");
		mapVars.put("edit_gatepass", "Edit Gate pass");
		mapVars.put("open_gatepass", "Open/Post Gate pass");
		mapVars.put("return_gatepass", "Return Gate pass");
		mapVars.put("delete_gatepass", "Delete Gate pass");
		mapVars.put("print_gatepass", "Print/Preview...");

		resetFormLayout();
		try {
			con = Channelplus3Application.getInstance().getFrmUserLogin()
					.getDbc().getDbConnection();
			final QueryView qv = new QueryView(con);
			qv
					.setSqlquery("select name,code ,"
							+ " decode((select max(FLAG) from cp_gatepass1 "
							+ " where  trans_date=:QUERY_DATE AND LOCATION_CODE=CODE),'1','CLOSED',"
							+ "2,'OPENED',0,'UNDER PROCESS','NOT CREATED') STATUS "
							+ " from locations  WHERE has_gatepass='Y' order by code");
			Parameter pm = new Parameter("QUERY_DATE");
			pm.setValueType(Parameter.DATA_TYPE_DATE);
			pm.setValue(new java.util.Date(System.currentTimeMillis()));
			qv.addParameter(pm);
			centralPanel.addComponent(qv);
			qv.reportSetting.doStandard();
			qv.doExecuteOnParameterChange(true);
			qv.getDataListners().add(new tableDataListner() {

				public String getCellStyle(qryColumn qc, int recordNo) {
					return null;
				}

				public String calcSummary(List<String> qcGroup, qryColumn qc) {
					if (qc != null && qc.equals("ADJUST_QTY")) {
						double f = data_items.getSummaryOf("ADJUST_QTY",
								localTableModel.SUMMARY_SUM);
						return (new DecimalFormat(utils.FORMAT_QTY)).format(f);
					}
					return "";
				}

				public void beforeQuery() {

				}

				public void afterVisual() {
					qv.getTable().addActionHandler(new Handler() {

						// / calll back to update status as open
						Callback cb_delete = new Callback() {
							public void onDialogResult(boolean resultIsYes) {
								if (resultIsYes) {
									try {
										con.setAutoCommit(false);
										Integer flg = Integer.valueOf(utils
												.getSqlValue(
														"select flag from cp_gatepass1 where keyfld="
																+ QRYSES, con));
										if (flg != 0) {
											throw new Exception(
													" Gate pass is either opened or closed, can not be removed. ");
										}
										utils
												.execSql(
														"declare cursor itm is select *from cp_gatepass2 where keyfld="
																+ QRYSES
																+ "; begin delete from cp_gatepass1 where keyfld="
																+ QRYSES
																+ "; for x in itm loop update stori set qty_reserved=qty_reserved-x.dlv_allqty where"
																+ " refer=x.refer and strno=x.strno and prd_dt=x.dlv_prd_date and exp_dt=x.dlv_exp_date;"
																+ "end loop; "
																+ "delete from cp_gatepass2 where keyfld="
																+ QRYSES
																+ ";end;", con);
										qv.createView(true);
										con.commit();

									} catch (Exception ex) {
										Logger.getLogger(
												frmGatePass.class.getName())
												.log(Level.SEVERE, null, ex);
										centralPanel
												.getWindow()
												.showNotification(
														"Unable to Delete :",
														ex.getMessage(),
														Notification.TYPE_ERROR_MESSAGE);
										try {
											con.rollback();
										} catch (SQLException e) {
										}
									}

								}

							}
						};

						// / calll back to update status as open
						Callback cb_open = new Callback() {

							public void onDialogResult(boolean resultIsYes) {
								if (resultIsYes) {
									try {
										con.setAutoCommit(false);

										utils
												.execSql(
														" begin update cp_gatepass1 set flag=2 where keyfld="
																+ QRYSES
																+ "; update cp_gatepass2 set flag=2 where keyfld="
																+ QRYSES
																+ "; end;", con);

										con.commit();
										qv.createView(true);

									} catch (Exception ex) {
										Logger.getLogger(
												frmGatePass.class.getName())
												.log(Level.SEVERE, null, ex);
										centralPanel
												.getWindow()
												.showNotification(
														"Unable to update status :",
														ex.toString(),
														Notification.TYPE_ERROR_MESSAGE);
										try {
											con.rollback();
										} catch (SQLException e) {
										}

									}

								}

							}
						};

						public void handleAction(Action action, Object sender,
								Object target) {

							int rowno = Integer.valueOf(target.toString());
							String stat = ((String) qv.getLctb().getFieldValue(
									rowno, "STATUS"));
							QRYSES_LOCATION = ((String) qv.getLctb()
									.getFieldValue(rowno, "CODE"));
							QRYSES_DATE = (java.util.Date) qv
									.getMapParameters().get("QUERY_DATE")
									.getValue();
							QRYSES = getExistedKeyfld(QRYSES_LOCATION,
									(java.util.Date) qv.getMapParameters().get(
											"QUERY_DATE").getValue());

							// action for stat and menu selection
							if (stat.equals(STAT_NOT_CREATED)
									&& action.getCaption().equals(
											mapVars.get("create_gatepass"))) {
								QRYSES = "";
								initForm();
							}
							// edit gatepass
							if (stat.equals(STAT_UNDER_PROCESS)
									&& action.getCaption().equals(
											mapVars.get("edit_gatepass"))) {
								initForm();
							}
							// open gatepass
							if (stat.equals(STAT_UNDER_PROCESS)
									&& action.getCaption().equals(
											mapVars.get("open_gatepass"))) {
								String dd = hasAnyGatepassExistedBefore(QRYSES,
										QRYSES_LOCATION);
								mainLayout.addComponent(new Label("#" + dd
										+ "#"));
								if (!dd.equals("notfound")) {
									centralPanel.getWindow().showNotification(
											"", "Close first date of # " + dd,
											Notification.TYPE_ERROR_MESSAGE);
								} else {
									centralPanel.getWindow().addWindow(
											new YesNoDialog("Updating Status",
													"Update status to OPEN?",
													cb_open));
								}
							}
							// returning
							if (stat.equals(STAT_OPENED)
									&& action.getCaption().equals(
											mapVars.get("return_gatepass"))) {
								initForm();
							}
							if (stat.equals(STAT_UNDER_PROCESS)
									&& action.getCaption().equals(
											mapVars.get("delete_gatepass"))) {
								centralPanel.getWindow().addWindow(
										new YesNoDialog("Deleting",
												"Do you want to remove for "
														+ qv.getLctb()
																.getFieldValue(
																		rowno,
																		"NAME")
																.toString(),
												cb_delete));
							}
							if (action.getCaption().equals(
									mapVars.get("print_gatepass"))) {
								print_data();
							}

						}

						public Action[] getActions(Object target, Object sender) {
							int rowno = Integer.valueOf(target.toString());
							String stat = ((String) qv.getLctb().getFieldValue(
									rowno, "STATUS"));

							List<Action> act = new ArrayList<Action>();

							if (stat.equals(STAT_NOT_CREATED)) {
								act.add(new Action(mapVars
										.get("create_gatepass")));
							}

							if (stat.equals(STAT_UNDER_PROCESS)) {
								act
										.add(new Action(mapVars
												.get("edit_gatepass")));
								act
										.add(new Action(mapVars
												.get("open_gatepass")));

								act.add(new Action(mapVars
										.get("delete_gatepass")));
								act.add(new Action(mapVars
										.get("print_gatepass")));

							}
							if (stat.equals(STAT_OPENED)) {
								act.add(new Action(mapVars
										.get("return_gatepass")));
								act.add(new Action(mapVars
										.get("print_gatepass")));

							}
							if (stat.equals(STAT_CLOSED)) {
								act.add(new Action(mapVars
										.get("print_gatepass")));

							}

							Action[] ac = new Action[act.size()];
							return act.toArray(ac);
						}
					});

				}

				public void afterQuery() {

				}
			});

			qv.createView();

		} catch (Exception ex) {
			Logger.getLogger(frmGatePass.class.getName()).log(Level.SEVERE,
					null, ex);
			parentLayout.getWindow().showNotification("Unable to LOAD DATA:",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public String getExistedKeyfld(String loc, Date dt) {
		String s = utils.getSqlValue(
				"select max(keyfld) from cp_gatepass1 where location_code='"
						+ loc + "' and " + " trans_date="
						+ utils.getOraDateValue(dt), con);
		return s;
	}

	public void createView() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		resetFormLayout();

		commandLayout.addComponent(cmdSave);
		commandLayout.addComponent(cmdSaveTemp);
		commandLayout.addComponent(cmdBack);
		commandLayout.addComponent(cmdPrint);
		commandLayout.addComponent(cmdAddItem);
		commandLayout.addComponent(cmdRetItem);

		basicLayout.addComponent(txtKeyfld);
		basicLayout.addComponent(txtLocation);
		basicLayout.addComponent(txtTransDate);
		basicLayout.addComponent(txtDescr);

		mainLayout.addComponent(txtTitle);
		mainLayout.addComponent(commandLayout);
		mainLayout.addComponent(basicLayout);
		mainLayout.addComponent(tbl_items);

		txtDescr.setWidth("100%");
		txtKeyfld.setWidth("100%");
		txtTransDate.setWidth("100%");
		txtDescr.setWidth("100%");
		txtLocation.setWidth("100%");
		basicLayout.setWidth("100%");
		mainLayout.setWidth("100%");
		tbl_items.setWidth("100%");
		txtTitle.addStyleName("formTitle");

		tbl_items.setSelectable(true);
		tbl_items.setHeight("450px");
		tbl_items.setFooterVisible(true);
		txtTransDate.setResolution(DateField.RESOLUTION_DAY);
		txtTransDate.setDateFormat(utils.FORMAT_SHORT_DATE);

		centralPanel.addComponent(mainLayout);
		centralPanel.requestRepaintAll();

		if (!listenerAdded) {

			data_items.setDefaultValuelistner(new DefaultValueListner() {

				public void setValue(dataCell dc, qryColumn qc) {
					if (qc.getColname().equals("ITEMPOS")) {
						Double nxt = data_items.getSummaryOf("ITEMPOS",
								localTableModel.SUMMARY_MAX);
						if (nxt == null) {
							dc.setValue("1.0", BigDecimal.valueOf(1));
						} else {
							nxt++;
							dc.setValue(String.valueOf(nxt), BigDecimal
									.valueOf(nxt.intValue()));
						}
					}
					if (qc.getColname().equals("SALE_ALLQTY")
							|| qc.getColname().equals("SALERET_ALLQTY")
							|| qc.getColname().equals("RET_PKQTY")
							|| qc.getColname().equals("RET_QTY")
							|| qc.getColname().equals("DLV_QTY")
							|| qc.getColname().equals("DLV_PKQTY")
							|| qc.getColname().equals("DLV_ALLQTY")
							|| qc.getColname().equals("RET_ALLQTY")) {
						dc
								.setValue("0", BigDecimal.valueOf(Float
										.valueOf("0")));
					}

				}
			});
			data_items.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (cursorNo < 0) {
						return;
					}
					if ((BigDecimal) data_items.getFieldValue(cursorNo, "PACK") == null) {
						return;
					}
					float pk = ((BigDecimal) data_items.getFieldValue(cursorNo,
							"PACK")).floatValue();
					float dlv_allqty = (((BigDecimal) data_items.getFieldValue(
							cursorNo, "DLV_PKQTY")).floatValue() * pk)
							+ ((BigDecimal) data_items.getFieldValue(cursorNo,
									"DLV_QTY")).floatValue();

					data_items.setFieldValue(cursorNo, "DLV_ALLQTY", BigDecimal
							.valueOf(dlv_allqty));

					float ret_allqty = (((BigDecimal) data_items.getFieldValue(
							cursorNo, "RET_PKQTY")).floatValue() * pk)
							+ ((BigDecimal) data_items.getFieldValue(cursorNo,
									"RET_QTY")).floatValue();
					float sret_allqty = ((BigDecimal) data_items.getFieldValue(
							cursorNo, "SALERET_ALLQTY")).floatValue();
					float sale_allqty = ((BigDecimal) data_items.getFieldValue(
							cursorNo, "SALE_ALLQTY")).floatValue();
					float adj_qty = ((ret_allqty + sale_allqty) - (dlv_allqty + sret_allqty))
							/ pk;
					data_items.setFieldValue(cursorNo, "RET_ALLQTY", BigDecimal
							.valueOf(ret_allqty));
					data_items.setFieldValue(cursorNo, "ADJUST_QTY", BigDecimal
							.valueOf(adj_qty));
				}
			});

			cmdAddItem.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					tbl_items.select(null);
					add_change_item();
				}
			});
			cmdBack.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					resetFormLayout();
					showDailyGatePass();
				}
			});

			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});

			tbl_items.setCellStyleGenerator(new CellStyleGenerator() {

				public String getStyle(Object itemId, Object propertyId) {
					return utilsVaadin.getStyleFromCol(lstItemCols,
							(String) propertyId);
				}
			});
			cmdAddItem.setClickShortcut(KeyCode.F9);
			tbl_items.addActionHandler(new Handler() {

				public void handleAction(Action action, Object sender,
						Object target) {

					int rowno = Integer.valueOf(target.toString());
					if (action.getCaption()
							.equals(acts_item.get("change_item"))) {
						tbl_items.setValue(rowno);
						add_change_item();
					}
					if (action.getCaption()
							.equals(acts_item.get("delete_item"))) {
						data_items.deleteRow(rowno);
						tbl_items.removeItem(rowno);
						tbl_items.requestRepaintAll();
					}
					if (action.getCaption().equals(acts_item.get("put_qty"))) {
						return_single_items(rowno);
					}
				};

				public Action[] getActions(Object target, Object sender) {
					List<Action> lstact = new ArrayList<Action>();
					lstact.add(new Action(acts_item.get("change_item")));
					lstact.add(new Action(acts_item.get("delete_item")));
					if (varFlag != 0) {
						lstact.clear();
					}
					if (varFlag == 2) {
						lstact.add(new Action(acts_item.get("put_qty")));
					}
					Action[] act = new Action[lstact.size()];
					return lstact.toArray(act);
				}

				public void addItem() {
					tbl_items.setValue(null);
					add_change_item();

				}
			});
			cmdRetItem.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					return_all_items();

				}
			});
			cmdPrint.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					if (cmdSave.isEnabled()) {
						save_data(false, true);
					} else {
						print_data();
					}

				}
			});
			cmdSaveTemp.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_returns();
				}
			});

			listenerAdded = true;
		}
	}

	public void add_change_item() {

		try {
			wndList.setContent(lstLayout);
			TableView.SelectionListener sl = new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wndList);
					try {
						int rn = -1;
						String rfr = (String) tv.getData().getFieldValue(
								tv.getSelectionValue(), "REFERENCE");
						String des = (String) tv.getData().getFieldValue(
								tv.getSelectionValue(), "DESCR");
						String pkd = (String) tv.getData().getFieldValue(
								tv.getSelectionValue(), "PACKD");
						String und = (String) tv.getData().getFieldValue(
								tv.getSelectionValue(), "UNITD");
						BigDecimal pk = (BigDecimal) tv.getData()
								.getFieldValue(tv.getSelectionValue(), "PACK");
						Timestamp pdt = (Timestamp) tv.getData().getFieldValue(
								tv.getSelectionValue(), "PRD_DT");
						Timestamp edt = (Timestamp) tv.getData().getFieldValue(
								tv.getSelectionValue(), "EXP_DT");

						if (tbl_items.getValue() == null) {
							rn = data_items.addRecord();
						} else {
							if (tbl_items.getValue() != null) {
								rn = Integer.valueOf(tbl_items.getValue()
										.toString());
							}
						}

						data_items.setFieldValue(rn, "REFER", rfr);
						data_items.setFieldValue(rn, "DESCR", des);
						data_items.setFieldValue(rn, "PACKD", pkd);
						data_items.setFieldValue(rn, "UNITD", und);
						data_items.setFieldValue(rn, "PACK", pk);
						data_items.setFieldValue(rn, "DLV_PRD_DATE", pdt);
						data_items.setFieldValue(rn, "DLV_EXP_DATE", edt);

						vartmpStore = BigDecimal.valueOf(varDefaultStore);
						if (getItemQty(true, rfr, rn) == 0) {
							parentLayout.getWindow().showNotification(
									"Not found quantity in store for item # ",
									des + " : " + rfr,
									Notification.TYPE_ERROR_MESSAGE);
							data_items.deleteRow(rn);
							tbl_items.removeItem(rn);
						}

						fill_data_items();

					} catch (SQLException e) {
					}

				}
			};
			final TableView tblview = utilsVaadin
					.showSearch(
							lstLayout,
							sl,
							con,
							"Select reference,nvl(Descra,descr) descr,packd,unitd,pack,max(prd_dt) prd_dt,max(exp_dt) exp_dt,sum(round((qty-qty_reserved)/pack,3)) PackQty,descr2 from strbal "
									+ " where qty>0 and strno='"
									+ varDefaultStore
									+ "' group by reference,NVL(descrA,DESCR),packd,unitd,pack,descr2 "
									+ " order by descr2 ", true, new String[] {
									"UNITD", "PACK", "DESCR2", "PRD_DT",
									"EXP_DT" });

		} catch (Exception e) {
			Logger.getLogger(frmGatePass.class.getName()).log(Level.SEVERE,
					null, e);
			parentLayout.getWindow().showNotification("Error filling table",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public boolean has_item_existed(String rfr, Timestamp prd, Timestamp exp,
			int rn) {
		for (int i = 0; i < data_items.getRows().size(); i++) {
			if (i != rn) {
				Timestamp dprd = (Timestamp) data_items.getFieldValue(i,
						"DLV_PRD_DATE");
				Timestamp dexp = (Timestamp) data_items.getFieldValue(i,
						"DLV_EXP_DATE");
				if (data_items.getFieldValue(i, "REFER").toString().equals(rfr)
						&& dprd.equals(prd) && dexp.equals(exp)) {
					return true;
				}
			}
		}
		return false;
	}

	public int getItemQty(boolean showListSelection, final String rfr,
			final int rn) throws SQLException {
		final String sq1 = "select strno,(qty-qty_reserved)/pack pkqty,packd,prd_dt prd_date,exp_dt exp_date,qty "
				+ "from strbal where reference='"
				+ rfr
				+ "' and strno="
				+ vartmpStore.floatValue()
				+ " and (qty-qty_reserved)>0 order by qty desc ";
		int cnt = 0;
		ResultSet rs_qt = utils.getSqlRS(sq1, con);
		if (rs_qt == null || !rs_qt.first()) {
			return 0;
		}
		rs_qt.beforeFirst();
		while (rs_qt.next()) {
			cnt++;
		}
		rs_qt.first();
		varTmpQty = rs_qt.getBigDecimal("pkqty");
		vartmpStore = rs_qt.getBigDecimal("strno");
		varTmpPrdDate = rs_qt.getTimestamp("prd_date");
		varTmpExpDate = rs_qt.getTimestamp("exp_date");
		rs_qt.close();
		if (cnt > 1 && showListSelection) {
			utilsVaadin.showSearch(lstLayout, new SelectionListener() {

				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wndList);

					varTmpQty = (BigDecimal) tv.getData().getFieldValue(
							tv.getSelectionValue(), "PKQTY");
					vartmpStore = (BigDecimal) tv.getData().getFieldValue(
							tv.getSelectionValue(), "STRNO");
					varTmpPrdDate = (Timestamp) tv.getData().getFieldValue(
							tv.getSelectionValue(), "PRD_DATE");
					varTmpExpDate = (Timestamp) tv.getData().getFieldValue(
							tv.getSelectionValue(), "EXP_DATE");
					data_items.setFieldValue(rn, "DLV_PKQTY", BigDecimal
							.valueOf(0.0));
					data_items.setFieldValue(rn, "DLV_PRD_DATE", varTmpPrdDate);
					data_items.setFieldValue(rn, "DLV_EXP_DATE", varTmpExpDate);
					data_items.setFieldValue(rn, "STRNO", vartmpStore);
					if (has_item_existed(rfr, varTmpPrdDate, varTmpExpDate, rn)) {
						data_items.deleteRow(rn);
						tbl_items.removeItem(rn);
					} else {
						utilsVaadin.refreshRowFromData(data_items, tbl_items,
								rn, lstItemCols);
					}
				}
			}, con, sq1, true, new String[] { "QTY" });
		} else {
			data_items.setFieldValue(rn, "DLV_PKQTY", BigDecimal.valueOf(0.0));
			data_items.setFieldValue(rn, "DLV_PRD_DATE", varTmpPrdDate);
			data_items.setFieldValue(rn, "DLV_EXP_DATE", varTmpExpDate);
			data_items.setFieldValue(rn, "STRNO", vartmpStore);
			if (has_item_existed(rfr, varTmpPrdDate, varTmpExpDate, rn)) {
				data_items.deleteRow(rn);
			}
		}
		rs_qt.close();
		return cnt;
	}

	public void init() {

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		if (data_items.getDbclass() == null) {
			try {
				data_items.createDBClassFromConnection(con);
			} catch (SQLException e) {
			}
		}
		load_data();
	}

	public void setFieldsReadOnly(boolean readonly) {
		txtKeyfld.setReadOnly(readonly);
		txtLocation.setReadOnly(readonly);
		txtTransDate.setReadOnly(readonly);
	}

	public void do_adjust_return() {
		for (Iterator iterator = tbl_items.getItemIds().iterator(); iterator
				.hasNext();) {
			Integer rowno = (Integer) iterator.next();
			Object txtfld = null;

			// make dlv_pkqty read only
			if (utilsVaadin.findColByCol("DLV_PKQTY", lstItemCols) != null) {
				txtfld = (TextField) tbl_items.getItem(rowno)
						.getItemProperty(
								utilsVaadin.findColByCol("DLV_PKQTY",
										lstItemCols).descr).getValue();
				((TextField) txtfld).setReadOnly(true);
			}

			//
			if (utilsVaadin.findColByCol("DLV_QTY", lstItemCols) != null) {
				txtfld = tbl_items.getItem(rowno).getItemProperty(
						utilsVaadin.findColByCol("DLV_QTY", lstItemCols).descr)
						.getValue();

				((TextField) txtfld).setReadOnly(true);
			}

			// make ret qty for write....
			if (utilsVaadin.findColByCol("RET_QTY", lstItemCols) != null) {
				txtfld = tbl_items.getItem(rowno).getItemProperty(
						utilsVaadin.findColByCol("RET_QTY", lstItemCols).descr)
						.getValue();

				((TextField) txtfld).setReadOnly(false);
			}
			if (utilsVaadin.findColByCol("RET_PKQTY", lstItemCols) != null) {
				txtfld = tbl_items.getItem(rowno)
						.getItemProperty(
								utilsVaadin.findColByCol("RET_PKQTY",
										lstItemCols).descr).getValue();
				((TextField) txtfld).setReadOnly(false);
			}

		}
	}

	public String hasAnyGatepassExistedBefore(String Keyfld, String loc) {
		String cc = "";
		if (Keyfld.length() == 0) {
			cc = utils
					.getSqlValue(
							"select nvl(min(to_char(trans_date,'dd/mm/rrrr')) ,'notfound') dat from cp_gatepass1 where flag!=1  and location_code ='"
									+ loc + "'", con);

		} else {
			cc = utils
					.getSqlValue(
							"select nvl(min(to_char(trans_date,'dd/mm/rrrr')) ,'notfound') dat from cp_gatepass1 where flag!=1  and location_code ='"
									+ loc + "' and keyfld!=" + Keyfld, con);

		}
		if (!cc.equals("notfound")) {
			return cc;
		}
		if (cc == null || cc.length() == 0) {
			cc = " ";
		}

		return cc;
	}

	public void load_data() {

		setFieldsReadOnly(false);
		cmdSave.setCaption("Save & Back ");
		cmdSaveTemp.setEnabled(false);
		data_items.clearALl();
		tbl_items.removeAllItems();
		varApprovedBy = "";
		varCurrentTime.setTime(System.currentTimeMillis());
		varFlag = 0;
		varKeyfld = -1;
		varTransferKeyfld = -1;
		varUserName = utils.DBUSER;
		if (QRYSES_LOCATION.length() == 0) {
			varLocationCode = utils
					.getSqlValue(
							"select repair.getsetupvalue_2('DEFAULT_LOCATION') from dual",
							con);
			varDefaultStore = Float.valueOf(utils.getSqlValue(
					"select repair.getsetupvalue_2('DEFAULT_STORE') from dual",
					con));
		} else {
			varLocationCode = QRYSES_LOCATION;
		}

		if (txtTransDate.getValue() != null) {
			((java.util.Date) txtTransDate.getValue()).setTime(QRYSES_DATE
					.getTime());
		} else {
			txtTransDate
					.setValue(new java.util.Date(System.currentTimeMillis()));
		}

		((java.util.Date) txtTransDate.getValue()).setTime(QRYSES_DATE
				.getTime());
		// FETCHING DATA FROM gatepass1
		try {
			if (QRYSES.length() > 0) {
				if (ps_g1 != null) {
					ps_g1.close();
				}
				utils.execSql("begin GATEPASS_UPDATE_BY_KEY(" + QRYSES
						+ "); COMMIT;END;", con);
				ps_g1 = con
						.prepareStatement(
								"select cp_gatepass1.*, decode(flag,1,'CLOSED',"
										+ " 2,'OPENED',0,'UNDER PROCESS','NOT CREATED') STATUS "
										+ " from cp_gatepass1 where keyfld="
										+ QRYSES,
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				ResultSet rs_g1 = ps_g1.executeQuery();
				if (!rs_g1.first()) {
					throw new Exception("Record not found Key ID: " + QRYSES);
				}
				varApprovedBy = rs_g1.getString("APPROVED_BY");
				varFlag = rs_g1.getFloat("flag");
				varStatus = rs_g1.getString("status");
				varApprovedTime = rs_g1.getTimestamp("APPROVED_TIME");
				varLocationCode = rs_g1.getString("LOCATION_CODE");
				varTransferKeyfld = rs_g1.getFloat("TRANS_KEYFLD");
				varRetTransferKeyfld = rs_g1.getFloat("RET_TRANS_KEYFLD");
				varKeyfld = Float.valueOf(QRYSES);
				txtDescr.setValue(rs_g1.getString("DESCR"));
				txtLocation.setValue(utils.getSqlValue(
						"select name||'-'||code from locations where code='"
								+ varLocationCode + "'", con));
				((java.util.Date) txtTransDate.getValue()).setTime(rs_g1
						.getDate("trans_date").getTime());
				txtKeyfld.setValue(QRYSES);

			} else {

				varKeyfld = getNextKF();
				txtLocation.setValue(utils.getSqlValue(
						"select name||' - '||code from locations where code='"
								+ varLocationCode + "'", con));
				txtKeyfld.setValue(String.valueOf(varKeyfld));
			}
			fetch_data_items();

			// changing UI according to status
			txtTitle.setValue(titleStr + " # " + txtLocation.getValue()
					+ " , Status=" + varStatus);
			cmdSave.setEnabled(true);
			cmdAddItem.setEnabled(true);
			cmdRetItem.setEnabled(true);
			if (varFlag == 1) {
				cmdSave.setEnabled(false);
				cmdAddItem.setEnabled(false);
				cmdRetItem.setEnabled(false);
			}

			if (varFlag == 2) {
				do_adjust_return();
				cmdAddItem.setEnabled(false);
				cmdSave.setCaption("Do Close");
				cmdSaveTemp.setEnabled(true);
			}
			if (varFlag == 0) {
				cmdRetItem.setEnabled(false);
			}
		} catch (Exception ex) {
			Logger.getLogger(frmGatePass.class.getName()).log(Level.SEVERE,
					null, ex);
			parentLayout.getWindow().showNotification("Unable to load data",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		setFieldsReadOnly(true);
	}

	public void fetch_data_items() throws SQLException {
		data_items.clearALl();
		String sq = "select CP_GATEPASS2.*,nvl(items.descra,items.descr) descr,sale_allqty/CP_GATEPASS2.pack sale_pkqty,"
				+ " saleret_allqty/CP_GATEPASS2.pack saleret_pkqty,ITEMS.PRD_DT,ITEMS.EXP_DT,0 ADJUST_QTY "
				+ "  from CP_GATEPASS2,ITEMS "
				+ " where REFER=REFERENCE AND CP_GATEPASS2.keyfld=" + varKeyfld;
		data_items.executeQuery(sq, true);
		fill_data_items();
	}

	public void fill_data_items() throws SQLException {
		tbl_items.removeAllItems();
		utilsVaadin.applyColumns("WAREHOUSE.GATEPASS", tbl_items, lstItemCols,
				con);
		utilsVaadin.query_data(tbl_items, data_items, lstItemCols);
	}

	public void print_data() {
		try {
			utilsVaadinPrintHandler.printGatepass(Integer.valueOf(QRYSES), con);
		} catch (Exception ex) {
			mainLayout.getWindow().showNotification("Unable to print:",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void save_data() {
		save_data(true, false);
	}

	public void refresh_delivery_items() throws SQLException {
		if (QRYSES.length() == 0 || varFlag == 0) {
			return;
		}
		String sq = "select CP_GATEPASS2.*,items.descr,sale_allqty/CP_GATEPASS2.pack sale_pkqty,"
				+ " saleret_allqty/CP_GATEPASS2.pack saleret_pkqty,ITEMS.PRD_DT,ITEMS.EXP_DT,0 ADJUST_QTY "
				+ "  from CP_GATEPASS2,ITEMS "
				+ " where REFER=REFERENCE AND CP_GATEPASS2.keyfld="
				+ varKeyfld
				+ " and CP_GATEPASS2.itempos=?";
		PreparedStatement ps_t = con.prepareStatement(sq,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		for (int i = 0; i < data_items.getRows().size(); i++) {
			ps_t.setFloat(1, ((Number) data_items.getFieldValue(i, "ITEMPOS"))
					.floatValue());
			ResultSet rsx = ps_t.executeQuery();
			if (!rsx.first()) {
				ps_t.close();
				throw new SQLException(
						"ER !  Other user may changed data , do refresh data");
			}
			if (!rsx.getString("REFER").equals(
					(String) data_items.getFieldValue(i, "REFER"))) {
				ps_t.close();
				throw new SQLException(
						"ER !  Other user may changed some data , do refresh data");
			}
			if (rsx.getFloat("SALERET_PKQTY") != ((BigDecimal) data_items
					.getFieldValue(i, "SALERET_PKQTY")).floatValue()) {
				ps_t.close();
				throw new SQLException(
						"ER !  Other user may changed some data , do refresh data");

			}
			if (rsx.getFloat("SALE_PKQTY") != ((BigDecimal) data_items
					.getFieldValue(i, "SALE_PKQTY")).floatValue()) {
				ps_t.close();
				throw new SQLException(
						"ER !  Other user may changed some data , do refresh data");

			}

			data_items.setFieldValue(i, "DLV_ALLQTY", rsx
					.getBigDecimal("DLV_ALLQTY"));
			data_items.setFieldValue(i, "DLV_PKQTY", rsx
					.getBigDecimal("DLV_PKQTY"));
			data_items
					.setFieldValue(i, "DLV_QTY", rsx.getBigDecimal("DLV_QTY"));
			data_items.setFieldValue(i, "SALE_PKQTY", rsx
					.getBigDecimal("SALE_PKQTY"));
			data_items.setFieldValue(i, "SALE_PKQTY", rsx
					.getBigDecimal("SALE_PKQTY"));

			utilsVaadin.refreshRowFromData(data_items, tbl_items, i,
					lstItemCols);
		}
		ps_t.close();
	}

	public void update_data_items() throws SQLException {
		String sql = "insert into cp_gatepass2 "
				+ "  ( PERIODCODE,  LOCATION_CODE,  REFER,  PACKD,  UNITD,  PACK,  TRANS_DATE , RET_PKQTY,  RET_QTY, RET_ALLQTY, DLV_QTY,  DLV_PKQTY,  DLV_ALLQTY,  DLV_PRD_DATE,  DLV_EXP_DATE,  DLV_FROM_STORE,  KEYFLD, ITEMPOS,FLAG,SENT,STRNO,SALE_ALLQTY,SALERET_ALLQTY) values "
				+ "(:PERIODCODE, :LOCATION_CODE, :REFER, :PACKD, :UNITD, :PACK, :TRANS_DATE, :RET_PKQTY, :RET_QTY,:RET_ALLQTY, :DLV_QTY, :DLV_PKQTY, :DLV_ALLQTY, :DLV_PRD_DATE, :DLV_EXP_DATE, :DLV_FROM_STORE, :KEYFLD, :ITEMPOS,:FLAG ,:SENT,:STORE,:SALE_ALLQTY,:SALERET_ALLQTY)";
		String updateSQL = "declare a number;"
				+ "	begin select nvl(sum(dlv_allqty),0) into a from cp_gatepass2 where "
				+ "  flag!=1 and dlv_prd_date=:PRD_DATE and dlv_exp_date=:EXP_DATE and refer=:REFER and strno=:STORE;"
				+ " update stori"
				+ " set QTY_RESERVED=a where strno=:STORE "
				+ " and refer=:REFER and prd_dt=:PRD_DATE and exp_dt=:EXP_DATE;end;";
		if (QRYSES.length() > 0) {

			utils
					.execSql(
							"declare cursor itm is select *from cp_gatepass2 where keyfld="
									+ varKeyfld
									+ "; begin "
									+ " for x in itm loop update stori set qty_reserved=qty_reserved-x.dlv_allqty where"
									+ " refer=x.refer and strno=x.strno and prd_dt=x.dlv_prd_date and exp_dt=x.dlv_exp_date;"
									+ " end loop; "
									+ " delete from cp_gatepass2 where keyfld="
									+ varKeyfld + ";end;", con);
		}

		java.sql.Date g_dt = new java.sql.Date(((java.util.Date) txtTransDate
				.getValue()).getTime());

		QueryExe qe = new QueryExe(sql, con);
		QueryExe qe2 = new QueryExe(updateSQL, con);
		qe.parse();
		qe2.parse();

		for (int i = 0; i < data_items.getRows().size(); i++) {
			float pk = ((BigDecimal) data_items.getFieldValue(i, "PACK"))
					.floatValue();
			float dlv_allqty = (((BigDecimal) data_items.getFieldValue(i,
					"DLV_PKQTY")).floatValue() * pk)
					+ ((BigDecimal) data_items.getFieldValue(i, "DLV_QTY"))
							.floatValue();
			float sale_allqty = (((BigDecimal) data_items.getFieldValue(i,
					"SALE_ALLQTY")).floatValue());

			float saleret_allqty = (((BigDecimal) data_items.getFieldValue(i,
					"SALERET_ALLQTY")).floatValue());

			float ret_allqty = (((BigDecimal) data_items.getFieldValue(i,
					"RET_PKQTY")).floatValue() * pk)
					+ ((BigDecimal) data_items.getFieldValue(i, "RET_QTY"))
							.floatValue();

			float available_qty = Float.valueOf(utils.getSqlValue(
					"select nvl(sum(qty-QTY_RESERVED),0) from stori where strno="
							+ varDefaultStore
							+ " and refer=+'"
							+ (String) data_items.getFieldValue(i, "REFER")
							+ "' and prd_dt="
							+ utils.getOraDateValue((Timestamp) data_items
									.getFieldValue(i, "DLV_PRD_DATE"))
							+ " and exp_dt="
							+ utils.getOraDateValue((Timestamp) data_items
									.getFieldValue(i, "DLV_EXP_DATE")), con));
			// check if any avaialbe stock
			if (varFlag == 0 && available_qty < dlv_allqty) {
				throw new SQLException("Stock for item not available :"
						+ (String) data_items.getFieldValue(i, "DESCR"));
			}
			// check if adjust_qty is negative
			// ((ret_allqty + sale_allqty)-(dlv_allqty+sret_allqty) )
			if (varFlag == 1
					&& (ret_allqty + sale_allqty)
							- (dlv_allqty + saleret_allqty) < 0) {
				throw new SQLException("Item #"
						+ (String) data_items.getFieldValue(i, "DESCR")
						+ " Return is negative");
			}

			qe.setParaValue("PERIODCODE", pcode);
			qe.setParaValue("LOCATION_CODE", varLocationCode);
			qe.setParaValue("REFER", (String) data_items.getFieldValue(i,
					"REFER"));
			qe.setParaValue("PACKD", (String) data_items.getFieldValue(i,
					"PACKD"));
			qe.setParaValue("UNITD", (String) data_items.getFieldValue(i,
					"UNITD"));
			qe.setParaValue("PACK", (BigDecimal) data_items.getFieldValue(i,
					"PACK"));
			qe.setParaValue("TRANS_DATE", g_dt);
			qe.setParaValue("RET_PKQTY", (BigDecimal) data_items.getFieldValue(
					i, "RET_PKQTY"));
			qe.setParaValue("RET_QTY", (BigDecimal) data_items.getFieldValue(i,
					"RET_QTY"));
			qe.setParaValue("DLV_PKQTY", (BigDecimal) data_items.getFieldValue(
					i, "DLV_PKQTY"));
			qe.setParaValue("DLV_QTY", (BigDecimal) data_items.getFieldValue(i,
					"DLV_QTY"));
			qe.setParaValue("DLV_ALLQTY", BigDecimal.valueOf(dlv_allqty));
			qe.setParaValue("RET_ALLQTY", BigDecimal.valueOf(ret_allqty));
			qe.setParaValue("DLV_PRD_DATE", (Timestamp) data_items
					.getFieldValue(i, "DLV_PRD_DATE"));
			qe.setParaValue("DLV_EXP_DATE", (Timestamp) data_items
					.getFieldValue(i, "DLV_EXP_DATE"));
			qe.setParaValue("DLV_FROM_STORE", BigDecimal.valueOf(1));
			qe.setParaValue("DLV_FROM_STORE", BigDecimal.valueOf(1));
			qe.setParaValue("SALE_ALLQTY", BigDecimal.valueOf(sale_allqty));
			qe.setParaValue("SALERET_ALLQTY", BigDecimal
					.valueOf(saleret_allqty));
			qe.setParaValue("KEYFLD", BigDecimal.valueOf(varKeyfld));
			qe.setParaValue("ITEMPOS", (BigDecimal) data_items.getFieldValue(i,
					"ITEMPOS"));
			qe.setParaValue("FLAG", BigDecimal.valueOf(varFlag));
			qe.setParaValue("SENT", "N");
			qe.setParaValue("STORE", varDefaultStore);
			qe.execute(false);

			//  
			qe2.setParaValue("EXP_DATE", (Timestamp) data_items.getFieldValue(
					i, "DLV_EXP_DATE"));
			qe2.setParaValue("PRD_DATE", (Timestamp) data_items.getFieldValue(
					i, "DLV_PRD_DATE"));
			qe2.setParaValue("REFER", (String) data_items.getFieldValue(i,
					"REFER"));
			qe2.setParaValue("STORE", varDefaultStore);
			qe2.execute();
		}
		qe.close();
		qe2.close();
	}

	public void save_returns() {
		if (QRYSES.length() == 0) {
			return;
		}
		if (varFlag != 2) {
			return;
		}
		try {

			con.setAutoCommit(false);
			QueryExe qe = new QueryExe(con);
			String sq = "update cp_gatepass2 set ret_pkqty=:RET_PKQTY,ret_qty=:RET_QTY,ret_allqty=:RET_ALLQTY"
					+ " where keyfld=:KEYFLD and itempos=:ITEMPOS";
			qe.setSqlStr(sq);
			qe.parse();
			for (int i = 0; i < data_items.getRows().size(); i++) {
				float pk = ((BigDecimal) data_items.getFieldValue(i, "PACK"))
						.floatValue();
				float pos = ((BigDecimal) data_items
						.getFieldValue(i, "ITEMPOS")).floatValue();
				float ret_allqty = (((BigDecimal) data_items.getFieldValue(i,
						"RET_PKQTY")).floatValue() * pk)
						+ ((BigDecimal) data_items.getFieldValue(i, "RET_QTY"))
								.floatValue();
				float ret_pkqty = ((BigDecimal) data_items.getFieldValue(i,
						"RET_PKQTY")).floatValue();
				float ret_qty = ((BigDecimal) data_items.getFieldValue(i,
						"RET_QTY")).floatValue();
				qe.setParaValue("RET_PKQTY", BigDecimal.valueOf(ret_pkqty));
				qe.setParaValue("RET_ALLQTY", BigDecimal.valueOf(ret_allqty));
				qe.setParaValue("RET_QTY", BigDecimal.valueOf(ret_qty));
				qe.setParaValue("KEYFLD", BigDecimal.valueOf(Double
						.valueOf(QRYSES)));
				qe.setParaValue("ITEMPOS", BigDecimal.valueOf(pos));
				qe.execute();

			}
			con.commit();
			resetFormLayout();
			showDailyGatePass();

			parentLayout.getWindow().showNotification("Commit Successed");

		} catch (Exception ex) {
			Logger.getLogger(frmGatePass.class.getName()).log(Level.SEVERE,
					null, ex);
			parentLayout.getWindow().showNotification("Unable to save:",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}

	}

	public void save_data(boolean backlist, boolean doprint) {
		String sql1 = "";
		String sql2 = "";

		QueryExe qe = new QueryExe(con);

		sql1 = "insert into cp_gatepass1 (KEYFLD, PERIODCODE, LOCATION_CODE, TRANS_DATE, DESCR, FLAG, USERNAME, CREATE_TIME, MODIFIED_TIME,SENT) values "
				+ "( :KEYFLD, :PERIODCODE, :LOCATION_CODE, :TRANS_DATE, :DESCR, :FLAG, :USERNAME, :CREATE_TIME, :MODIFIED_TIME,:SENT ) ";
		try {
			con.setAutoCommit(false);
			refresh_delivery_items();
			Timestamp created_time = null;

			// fetching temporary variables like current period code
			PreparedStatement ps_tmp1 = con.prepareStatement(
					"select repair.getcurperiodcode,sysdate from dual ",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs_tmp1 = ps_tmp1.executeQuery();

			if (!rs_tmp1.first()) {
				throw new Exception("unable to fetch current period code");
			}

			pcode = rs_tmp1.getString(1);
			Timestamp modified_time = rs_tmp1.getTimestamp(2);
			ps_tmp1.close();

			if (QRYSES.length() > 0) {
				varKeyfld = Float.valueOf(QRYSES);
				ResultSet rs_flg = utils.getSqlRS(
						"select flag,create_time from cp_gatepass1 where keyfld="
								+ QRYSES, con);
				BigDecimal flg = rs_flg.getBigDecimal(1);
				created_time = rs_flg.getTimestamp(2);
				rs_flg.close();

				if (flg.intValue() == 1) {
					throw new Exception("Gatepass is closed , can not edit..");
				}
				varFlag = flg.floatValue();
				if (flg.intValue() == 2) {
					varFlag = 1;
				}

				utils.execSql("delete from cp_gatepass1 where keyfld="
						+ varKeyfld, con);

			} else {
				float fromno = 0, tono = 0;

				varKeyfld = getNextKF();
				created_time = modified_time;
			}
			double sumAdjQty = data_items.getSummaryOf("ADJUST_QTY",
					localTableModel.SUMMARY_SUM);
			if (varFlag == 1 && sumAdjQty < 0) {
				throw new SQLException("Adjustment qty is negative.");
			}
			qe.setSqlStr(sql1);

			java.sql.Date g_dt = new java.sql.Date(
					((java.util.Date) txtTransDate.getValue()).getTime());

			qe.setParaValue("PERIODCODE", pcode);
			qe.setParaValue("KEYFLD", varKeyfld);
			qe.setParaValue("LOCATION_CODE", varLocationCode);
			qe.setParaValue("TRANS_DATE", g_dt);
			qe.setParaValue("DESCR", (String) txtDescr.getValue());
			qe.setParaValue("FLAG", BigDecimal.valueOf(varFlag));
			qe.setParaValue("USERNAME", varUserName);
			qe.setParaValue("CREATE_TIME", created_time);
			qe.setParaValue("MODIFIED_TIME", modified_time);
			qe.setParaValue("SENT", "N");

			qe.execute();
			qe.close();
			update_data_items();

			con.commit();

			if (doprint) {
				print_data();
			}

			if (backlist) {
				resetFormLayout();
				showDailyGatePass();
			} else {
				load_data();
			}

			parentLayout.getWindow().showNotification("Commit Successed");

		} catch (Exception ex) {
			Logger.getLogger(frmGatePass.class.getName()).log(Level.SEVERE,
					null, ex);
			parentLayout.getWindow().showNotification("Unable to save:",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}
	}

	public float getNextKF() throws SQLException {
		float fromno, tono, kf = 0;
		PreparedStatement pst1 = con.prepareStatement(
				"select from_keyfld,to_keyfld from locations where code='"
						+ QRYSES_LOCATION + "'",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rst1 = pst1.executeQuery();
		rst1.first();
		fromno = rst1.getFloat("from_keyfld");
		tono = rst1.getFloat("to_keyfld");
		kf = Float.valueOf(utils.getSqlValue(
				"select nvl(max(keyfld),0)+1 from cp_gatepass1 where location_code='"
						+ QRYSES_LOCATION + "' and " + " keyfld>=" + fromno
						+ " and keyfld<=" + tono, con));
		if (kf <= 1) {
			kf = fromno + 1;
		}
		pst1.close();
		return kf;
	}

	public void return_all_items() {
		if (varFlag == 1 || varFlag == 0) {
			return;
		}
		for (int i = 0; i < data_items.getRows().size(); i++) {
			float pk = ((BigDecimal) data_items.getFieldValue(i, "PACK"))
					.floatValue();
			float dlv_allqty = (((BigDecimal) data_items.getFieldValue(i,
					"DLV_PKQTY")).floatValue() * pk)
					+ ((BigDecimal) data_items.getFieldValue(i, "DLV_QTY"))
							.floatValue();
			float sale_allqty = (((BigDecimal) data_items.getFieldValue(i,
					"SALE_ALLQTY")).floatValue());

			float saleret_allqty = (((BigDecimal) data_items.getFieldValue(i,
					"SALERET_ALLQTY")).floatValue());

			double ret_pkqty = ((dlv_allqty + saleret_allqty) - (sale_allqty))
					/ pk;
			double ret_qty = 0;
			if (String.valueOf(ret_pkqty).contains(".")) {
				double fr1 = Math.floor(ret_pkqty);
				double fr2 = ret_pkqty - fr1;
				ret_pkqty = fr1;
				ret_qty = fr2 * pk;
			}
			data_items.setFieldValue(i, "RET_PKQTY", BigDecimal
					.valueOf(ret_pkqty));
			data_items.setFieldValue(i, "RET_QTY", BigDecimal.valueOf(ret_qty));
			utilsVaadin.refreshRowFromData(data_items, tbl_items, i,
					lstItemCols);
		}
	}

	public void return_single_items(int i) {
		if (varFlag == 1 || varFlag == 0) {
			return;
		}

		float pk = ((BigDecimal) data_items.getFieldValue(i, "PACK"))
				.floatValue();
		float dlv_allqty = (((BigDecimal) data_items.getFieldValue(i,
				"DLV_PKQTY")).floatValue() * pk)
				+ ((BigDecimal) data_items.getFieldValue(i, "DLV_QTY"))
						.floatValue();
		float sale_allqty = (((BigDecimal) data_items.getFieldValue(i,
				"SALE_ALLQTY")).floatValue());

		float saleret_allqty = (((BigDecimal) data_items.getFieldValue(i,
				"SALERET_ALLQTY")).floatValue());

		double ret_pkqty = ((dlv_allqty + saleret_allqty) - (sale_allqty)) / pk;
		double ret_qty = 0;
		if (String.valueOf(ret_pkqty).contains(".")) {
			double fr1 = Math.floor(ret_pkqty);
			double fr2 = ret_pkqty - fr1;
			ret_pkqty = fr1;
			ret_qty = fr2 * pk;
		}
		data_items.setFieldValue(i, "RET_PKQTY", BigDecimal.valueOf(ret_pkqty));
		data_items.setFieldValue(i, "RET_QTY", BigDecimal.valueOf(ret_qty));
		utilsVaadin.refreshRowFromData(data_items, tbl_items, i, lstItemCols);
	}

	public void showInitView() {
		showDailyGatePass();

	}

}