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
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog.Callback;
import com.generic.ColumnProperty;
import com.generic.DBClass;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.Row;
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
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Window.Notification;
import com.windows.frmGatePass;

public class frmPurchaseCost implements transactionalForm {
	public static final dataCell OPTION_OPEN = new dataCell("Keep Open", "OPEN");
	public static final dataCell OPTION_CLOSE_NO_JV = new dataCell(
			"Close without Adj. Jv", "CLOSE_NO_JV");
	public static final dataCell OPTION_CLOSE_JV = new dataCell(
			"Close with Adj. Jv", "CLOSE_JV");

	private String titleStr = "Purchase Closing/Costing : Order No :";
	private Label txtTitle = new Label(titleStr);

	private String titleStr2 = "Recived Goods Status :";
	private Label txtTitle2 = new Label(titleStr2);

	private AbstractLayout parentLayout = null;
	public static final String PROP_NONE = "NONE";
	public static final String PROP_INVISIBLE = "INVISIBLE";

	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private GridLayout basicLayout = new GridLayout(4, 1);
	private GridLayout summaryLayout = new GridLayout(4, 1);
	private GridLayout otherLayout = new GridLayout(4, 1);
	private HorizontalLayout expenseLayout = new HorizontalLayout();

	private Button cmdSave = new Button("Save & Back");
	private Button cmdBack = new Button("Cancel");
	private Button cmdAddExp = new Button("Add Expense");
	private Button cmdPrint = new Button("Print");
	private Button cmdDelete = new Button("Delete");

	private OptionGroup optClose = new OptionGroup("Close Order ");
	// basic
	private TextField txtInvNo = new TextField("Invoice No");
	private TextField txtLocation = new TextField("Location ");
	private DateField txtInvDate = new DateField("Invoice Date ");
	private TextField txtOrdNo = new TextField("Order No");
	private TextField txtInvdRef_name = new TextField("Order Reference ", "");
	private ComboBox txtInvType = new ComboBox("Type");
	private TextField txtSuppInvNo = new TextField("Supp Inv");
	private ComboBox txtStore = new ComboBox("Store");

	// summary
	private TextField txtInvAmt = new TextField("Inv Amt");
	private TextField txtInvDiscAmt = new TextField("Disc Amt");
	private TextField txtNetAmount = new TextField("Net Amount");
	private TextField txtOtherExp = new TextField("Other Expenses");
	private TextField txtCurrency = new TextField("Currency Descr.");
	private TextField txtRate = new TextField("Currency Rate");
	private TextField txtKDAmount = new TextField("KWD Amt");
	private TextField txtKDCost = new TextField("KWD Cost");
	private DateField txtRcvdDate = new DateField("Goods Rcvd Date");
	private TextField txtRemarks = new TextField("Remarks");

	// Other
	private TextField txtLCNo = new TextField("LC No");
	private TextField txtSalord = new TextField("Sales Order");
	private TextField txtPolicyNo = new TextField("Policy No");
	private TextField txtInsCo = new TextField("Insurance Co.");
	private TextField txtShipCo = new TextField("Ship Co");
	private TextField txtBankName = new TextField("Bank Name");

	private Table tbl_items = new Table("Items");
	private Table tbl_exp = new Table();

	private String varLocationCode = "";
	private int varReciptkeyfld = -1;
	private int varPurKeyfld = -1;
	private String varPurRef = "";
	private String varPurRefName = "";
	private String varOrdAccount = "";
	private int varReciptNo = -1;
	private double varNoOfSrv = 0;
	private double varOrdNo = -1;
	private double varInvNo = -1;
	private String varCustCode = "";
	private double varKdAmt = 0;
	private double varKdCost = 0;
	private double varInvAmt = 0;
	private double varInvDiscAmt = 0;
	private double varOtherExpense = 0;
	private double varCurRate = 1;
	private double varInvNetAmt = 0;
	private double varOrdFlag = 0;
	private double varAdjKeyfld = -1;
	private double varRcvdRate = 0;
	private double varTotAdjustAmt = 0;
	private double varTotDeliveredAmt = 0;

	private TabSheet tabsheet = new TabSheet();

	private Map<String, String> mapActionStrs = new HashMap<String, String>();
	private Map<String, String> mapActionItemTable = new HashMap<String, String>();

	private localTableModel data_items = new localTableModel();
	private localTableModel data_exp = new localTableModel();

	private String QRYSES = "";
	private String QRYSES_ORDER = "";

	private PreparedStatement ps_data = null;
	private ResultSet rs_data = null;

	private PreparedStatement ps_ord = null;
	private ResultSet rs_ord = null;

	private final List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();

	private Window wndList = new Window();
	private VerticalLayout listlayout = new VerticalLayout();

	private boolean listenerAdded = false;
	private Connection con = null;

	private String colEPos = "Pos";
	private String colEType = "Type ";
	private String colEDebitAcc = "Debit Acc";
	private String colEDebitRef = "Reference";
	private String colECreditAcc = "Credit Acc";
	private String ColECreditCmd = " ";
	private String colEAmt = "Amount";
	private String colERate = "Rate";
	private String colEDescr = "Descr";
	private String colEDate = "Date";

	public void showPurchaseCostView() {
		mapActionStrs.clear();
		mapActionStrs.put("create_pur", "Assign Invoice");
		mapActionStrs.put("edit_pur", "Edit Invoice");

		mapActionItemTable.clear();
		mapActionItemTable.put("delete_item", "Delete item..");
		mapActionItemTable.put("change_item", "Change item..");

		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();

		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		try {
			Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
					.getDbConnection().setAutoCommit(false);
			final QueryView qv = new QueryView(con);
			qv
					.setSqlquery(" select acaccount.name order_no,order1.ord_no,order1.ord_ref,ord_refnm||' '||ord_ref ord_refnm,ord_amt-ord_discamt ord_amount "
							+ " ,decode(ord_flag,2,'Open',1,'Closed',0,'Not Approved') status "
							+ " ,case when orderdqty>0 then to_char(round((100/orderdqty)*(deliveredqty+delivered_freeqty),2))||'%' else '100%' end Recieved,"
							+ " case when orderdqty>0 then round((100/orderdqty)*(deliveredqty+delivered_freeqty),2) else 100 end RECIEVED_NUM,ORD_FLAG,"
							+ " decode(pur_keyfld,null,'No','Yes') Invoiced,NVL(pur_keyfld,-1) PUR_KEYFLD,NVL(order1.recipt_keyfld,-1) RECIPT_KEYFLD,ord_date ,"
							+ " PUR1.INVOICE_NO,PUR1.INVOICE_DATE CLOSE_DATE "
							+ " from order1,acaccount,pur1 where PUR1.KEYFLD(+) = ORDER1.PUR_KEYFLD AND acaccount.accno=order1.ordacc and ord_code=103  and "
							+ " ordacc is not null and ord_flag=:status order by ordacc,order1.ord_no");
			centralPanel.setHeight("100%");
			// centralPanel.addComponent(qv);
			ResourceManager.addComponent(centralPanel, qv);
			qv.setSizeFull();
			Parameter pm = new Parameter("status");
			pm.setDescription("Status ");
			pm.getLovList().add(new dataCell("Opened", "2"));
			pm.getLovList().add(new dataCell("Closed", "1"));
			pm.getLovList().add(new dataCell("Not Approved", "0"));
			pm.setValue("2");
			qv.addParameter(pm);
			qv.reportSetting.doStandard();
			qv.reportSetting.setTitle("Purchase Orders");
			qv.setHideCols(new String[] { "RECIEVED_NUM", "ORD_FLAG",
					"PUR_KEYFLD", "RECIPT_KEYFLD" });
			qv.getListGroupsBy().add("ORDER_NO");

			qv.getDataListners().add(new tableDataListner() {

				public String getCellStyle(qryColumn qc, int recordNo) {
					return null;
				}

				public String calcSummary(List<String> qcGroup, qryColumn qc) {
					if (qcGroup != null && qcGroup.size() > 0
							&& qc.getColname().equals("ORD_AMOUNT")) {
						String s = String.valueOf(qv.getLctb().getSummaryOf(
								qv.getListGroupsBy(), "ORD_AMOUNT",
								localTableModel.SUMMARY_SUM));
						return s;
					}

					if (qcGroup == null && qc.getColname().equals("ORD_AMOUNT")) {
						String s = String.valueOf(qv.getLctb().getSummaryOf(
								"ORD_AMOUNT", localTableModel.SUMMARY_SUM));
						return s;
					}
					return "";
				}

				public void beforeQuery() {

				}

				public void afterQuery() {
					qv.getLctb().getColByName("ORD_AMOUNT").setNumberFormat(
							Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb().getColByName("ORD_DATE").setDateFormat(
							utils.FORMAT_SHORT_DATE);
					qv.getLctb().getColByName("CLOSE_DATE").setDateFormat(
							utils.FORMAT_SHORT_DATE);
					qv.getLctb().getColByName("ORD_REFNM").setTitle(
							"Refrerence Name");

				}

				public void afterVisual() {
					// setting width
					qv.getTable().setHeight("100%");
					qv.setColumnWidth("ORD_REFNM", 120);
					qv.setColumnWidth("ORD_REF", 40);
					qv.setColumnWidth("ORD_AMOUNT", 60);
					qv.setColumnWidth("ORD_NO", 40);
					qv.setColumnWidth("INVOICE_NO", 40);
					// action handler
					qv.getTable().addActionHandler(new Handler() {

						public void handleAction(Action action, Object sender,
								Object target) {
							if (target == null) {
								return;
							}

							int rowno = Integer.valueOf(target.toString());
							double flg = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "ORD_FLAG"))
									.doubleValue();
							int purk = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "PUR_KEYFLD"))
									.intValue();
							QRYSES_ORDER = qv.getLctb().getFieldValue(rowno,
									"ORD_NO").toString();
							if (flg == 0) {
								parentLayout
										.getWindow()
										.showNotification(
												"Not Approved",
												"Please get approval from authorized user first",
												Notification.TYPE_ERROR_MESSAGE);
							}
							if (flg != 0 && purk == -1) {
								QRYSES = "";
								initForm();
							}
							if (flg != 0 && purk != -1) {
								QRYSES = String.valueOf(purk);
								initForm();
							}
						}

						public Action[] getActions(Object target, Object sender) {
							if (target == null) {
								return null;
							}

							int rowno = Integer.valueOf(target.toString());
							List<Action> acts = new ArrayList<Action>();
							if (rowno < 0) {
								return null;
							}
							double flg = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "ORD_FLAG"))
									.doubleValue();
							double purk = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "PUR_KEYFLD"))
									.doubleValue();

							if (flg == 0) {
								acts.clear();
							}

							if (flg != 0 && purk == -1) {
								acts.clear();
								acts.add(new Action(mapActionStrs
										.get("create_pur")));
							}
							if (flg != 0 && purk != -1) {
								acts.clear();
								acts.add(new Action(mapActionStrs
										.get("edit_pur")));

							}
							Action[] ac_ar = new Action[acts.size()];
							return acts.toArray(ac_ar);
						}

					});
				}
			});

			qv.createView();
			centralPanel.requestRepaintAll();
		} catch (Exception ex) {
			Logger.getLogger(frmPurchaseCost.class.getName()).log(Level.SEVERE,
					null, ex);
			centralPanel.getWindow().showNotification("Error loading form ",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public AbstractLayout getParentLayout() {
		return parentLayout;
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void showPurchaseCostView(AbstractLayout abs) {
		this.parentLayout = abs;
		showPurchaseCostView();

	}

	public void resetFormLayout() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		otherLayout.removeAllComponents();
		summaryLayout.removeAllComponents();
		expenseLayout.removeAllComponents();
		commandLayout.removeAllComponents();
		centralPanel.removeAllComponents();
		tabsheet.removeAllComponents();
		centralPanel.setHeight("-1px");
	}

	public void createView() {

		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		resetFormLayout();

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdBack);
		ResourceManager.addComponent(commandLayout, cmdAddExp);
		ResourceManager.addComponent(commandLayout, cmdPrint);
		ResourceManager.addComponent(commandLayout, cmdDelete);

		ResourceManager.addComponent(mainLayout, txtTitle);
		ResourceManager.addComponent(mainLayout, txtTitle2);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, optClose);

		optClose.removeAllItems();
		optClose.addItem(OPTION_OPEN);
		optClose.addItem(OPTION_CLOSE_JV);
		optClose.addItem(OPTION_CLOSE_NO_JV);
		optClose.addStyleName("horizontalgrouo");
		optClose.setSizeFull();
		optClose.setNullSelectionAllowed(false);

		basicLayout.setWidth("100%");
		otherLayout.setWidth("100%");
		summaryLayout.setWidth("100%");
		expenseLayout.setSizeFull();

		txtBankName.setWidth("100%");
		txtCurrency.setWidth("100%");
		txtInsCo.setWidth("100%");
		txtInvAmt.setWidth("100%");
		txtInvDate.setWidth("100%");
		txtInvDiscAmt.setWidth("100%");
		txtInvdRef_name.setWidth("100%");
		txtInvType.setWidth("100%");
		txtKDAmount.setWidth("100%");
		txtKDCost.setWidth("100%");
		txtInvNo.setWidth("100%");
		txtLCNo.setWidth("100%");
		txtLocation.setWidth("100%");
		txtNetAmount.setWidth("100%");
		txtOrdNo.setWidth("100%");
		txtOtherExp.setWidth("100%");
		txtPolicyNo.setWidth("100%");
		txtRate.setWidth("100%");
		txtRcvdDate.setWidth("100%");
		txtRemarks.setWidth("100%");
		txtSalord.setWidth("100%");
		txtShipCo.setWidth("100%");
		txtStore.setWidth("100%");
		txtSuppInvNo.setWidth("100%");
		tabsheet.setWidth("100%");
		tabsheet.setHeight("200px");
		txtInvAmt.addStyleName("netAmtStyle");
		txtInvDiscAmt.addStyleName("netAmtStyle");
		txtNetAmount.addStyleName("netAmtStyle");
		txtKDAmount.addStyleName("netAmtStyle");
		txtKDCost.addStyleName("netAmtStyle");
		txtOtherExp.addStyleName("netAmtStyle");
		txtTitle.addStyleName("formTitle");

		txtInvDate.setResolution(DateField.RESOLUTION_DAY);
		txtInvDate.setDateFormat(utils.FORMAT_SHORT_DATE);
		txtRcvdDate.setResolution(DateField.RESOLUTION_DAY);
		txtRcvdDate.setDateFormat(utils.FORMAT_SHORT_DATE);

		ResourceManager.addComponent(basicLayout, txtInvNo);
		ResourceManager.addComponent(basicLayout, txtLocation);
		ResourceManager.addComponent(basicLayout, txtInvDate);
		ResourceManager.addComponent(basicLayout, txtOrdNo);
		ResourceManager.addComponent(basicLayout, txtInvdRef_name);
		ResourceManager.addComponent(basicLayout, txtInvType);
		ResourceManager.addComponent(basicLayout, txtSuppInvNo);
		ResourceManager.addComponent(basicLayout, txtStore);

		ResourceManager.addComponent(basicLayout, txtLCNo);
		ResourceManager.addComponent(basicLayout, txtSalord);
		ResourceManager.addComponent(basicLayout, txtPolicyNo);
		ResourceManager.addComponent(basicLayout, txtInsCo);
		ResourceManager.addComponent(basicLayout, txtShipCo);
		ResourceManager.addComponent(basicLayout, txtBankName);

		ResourceManager.addComponent(summaryLayout, txtInvAmt);
		ResourceManager.addComponent(summaryLayout, txtInvDiscAmt);
		ResourceManager.addComponent(summaryLayout, txtNetAmount);

		ResourceManager.addComponent(summaryLayout, txtOtherExp);
		ResourceManager.addComponent(summaryLayout, txtCurrency);
		ResourceManager.addComponent(summaryLayout, txtRate);
		ResourceManager.addComponent(summaryLayout, txtKDAmount);
		ResourceManager.addComponent(summaryLayout, txtKDCost);
		ResourceManager.addComponent(summaryLayout, txtRcvdDate);
		ResourceManager.addComponent(summaryLayout, txtRemarks);

		/*
		 * basicLayout.addComponent(txtInvNo);
		 * basicLayout.addComponent(txtLocation);
		 * basicLayout.addComponent(txtInvDate);
		 * basicLayout.addComponent(txtOrdNo);
		 * basicLayout.addComponent(txtInvdRef_name);
		 * basicLayout.addComponent(txtInvType);
		 * basicLayout.addComponent(txtSuppInvNo);
		 * basicLayout.addComponent(txtStore);
		 * 
		 * basicLayout.addComponent(txtLCNo);
		 * basicLayout.addComponent(txtSalord);
		 * basicLayout.addComponent(txtPolicyNo);
		 * basicLayout.addComponent(txtInsCo);
		 * basicLayout.addComponent(txtShipCo);
		 * basicLayout.addComponent(txtBankName);
		 * 
		 * summaryLayout.addComponent(txtInvAmt);
		 * summaryLayout.addComponent(txtInvDiscAmt);
		 * summaryLayout.addComponent(txtNetAmount);
		 * 
		 * summaryLayout.addComponent(txtOtherExp);
		 * summaryLayout.addComponent(txtCurrency);
		 * summaryLayout.addComponent(txtRate);
		 * summaryLayout.addComponent(txtKDAmount);
		 * summaryLayout.addComponent(txtKDCost);
		 * summaryLayout.addComponent(txtRcvdDate);
		 * summaryLayout.addComponent(txtRemarks);
		 */
		tbl_items.setHeight("300px");
		tbl_items.setWidth("100%");
		tbl_exp.setWidth("100%");
		tbl_exp.setHeight("100%");
		ResourceManager.addComponent(expenseLayout, tbl_exp);

		tabsheet.addTab(basicLayout);
		tabsheet.addTab(summaryLayout);
		tabsheet.addTab(expenseLayout);
		tabsheet.getTab(basicLayout).setCaption("Baisc");
		tabsheet.getTab(summaryLayout).setCaption("Summary");
		tabsheet.getTab(expenseLayout).setCaption("Other Expenses");

		ResourceManager.addComponent(mainLayout, tabsheet);
		ResourceManager.addComponent(mainLayout, tbl_items);
		ResourceManager.addComponent(centralPanel, mainLayout);

		mainLayout.setWidth("100%");
		tbl_exp.removeAllItems();
		tbl_items.removeAllItems();
		tbl_exp.setSelectable(true);
		tbl_exp.setNullSelectionAllowed(false);
		tbl_items.setSelectable(true);
		tbl_items.setNullSelectionAllowed(false);
		tbl_items.setFooterVisible(true);
		tbl_exp.setFooterVisible(true);
		txtInvDiscAmt.setImmediate(true);
		txtRate.setImmediate(true);

		try {
			utilsVaadin.FillCombo(txtStore,
					"select no,name from store where flag=1 order by no", con);
		} catch (SQLException e) {
		}

		if (!listenerAdded) {
			// value listner for calculation other fields
			ValueChangeListener tmpV1 = new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if (txtInvDiscAmt.getValue() != null
							&& txtInvDiscAmt.getValue().toString().length() > 0) {
						varInvDiscAmt = Float.valueOf(txtInvDiscAmt.getValue()
								.toString());
					}
					if (txtRate.getValue() != null
							&& txtRate.getValue().toString().length() > 0) {
						varCurRate = Float.valueOf(txtRate.getValue()
								.toString());
					}
					calcSums(true);
				}
			};

			txtInvDiscAmt.addListener(tmpV1);
			txtRate.addListener(tmpV1);

			data_exp.setDefaultValuelistner(new DefaultValueListner() {

				public void setValue(dataCell dc, qryColumn qc) {
					if (qc.getColname().equals("LC_POSITION")) {
						Double nxt = data_exp.getSummaryOf("LC_POSITION",
								localTableModel.SUMMARY_MAX);
						if (nxt == null) {
							dc.setValue("1", BigDecimal.valueOf(1));
						} else {
							nxt++;
							dc.setValue(String.valueOf(nxt), BigDecimal
									.valueOf(nxt));
						}
					}
					if (qc.getColname().equals("LC_DATE")) {
						Date dt = new Date(System.currentTimeMillis());
						dc.setValue(String.valueOf(dt), dt);
					}
					if (qc.getColname().equals("LC_RATE")) {
						dc.setValue(String.valueOf(1), BigDecimal.valueOf(1));
					}
					if (qc.getColname().equals("LC_EXP_AMT")) {
						dc.setValue(String.valueOf(0), BigDecimal.valueOf(0));
					}
				}
			});

			cmdAddExp.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					tabsheet.setSelectedTab(expenseLayout);
					data_exp.addRecord();
					try {
						fill_exp_table();
					} catch (SQLException e) {
						Logger.getLogger(frmPurchaseCost.class.getName()).log(
								Level.SEVERE, null, e);
						parentLayout.getWindow().showNotification(
								"Error filling table",
								Notification.TYPE_ERROR_MESSAGE);
					}
				}
			});
			cmdBack.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					resetFormLayout();
					showPurchaseCostView();
				}
			});

			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			// command for deletion
			cmdDelete.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (varOrdFlag == 1) {
						mainLayout
								.getWindow()
								.showNotification(
										"",
										"Can not remove , order is closed -> call support",
										Notification.TYPE_ERROR_MESSAGE);
						return;
					}
					Callback cb = new Callback() {
						public void onDialogResult(boolean resultIsYes) {

							if (!resultIsYes) {
								return;
							}
							// check either other user changed status ?
							String ordflg = utils.getSqlValue(
									"select ord_flag from order1 where ord_code=103 and ord_no='"
											+ QRYSES_ORDER + "'", con);
							if (ordflg == null || ordflg.isEmpty()) {
								mainLayout.getWindow().showNotification("",
										"Other user has removed this order.",
										Notification.TYPE_ERROR_MESSAGE);
							}
							if (Float.valueOf(ordflg) == 1) {
								mainLayout.getWindow().showNotification("",
										"Other user has closed this order.",
										Notification.TYPE_ERROR_MESSAGE);
							}
							try {
								String sq = "declare cursor acs is select accno,nvl(count(*),0) cnt from acvoucher2 where refercode in (11,1013) and referkeyfld=:PUR_KEYFLD group by accno; "
										+ "cursor itm is select refer,nvl(count(*),0) cnt from pur2 where keyfld=:PUR_KEYFLD group by refer;"
										+ " begin for x in acs loop repair.decaccuses(x.accno,x.cnt);end loop;"
										// " for x in itm loop repair.decitemcount(x.refer); end loop; "
										+ " delete from acvoucher1 where refercode in (1013,11) and referkeyfld=:PUR_KEYFLD ; "
										+ " DELETE FROM PUR2 WHERE KEYFLD=:PUR_KEYFLD ;"
										+ " delete from pur1 where keyfld=:PUR_KEYFLD ; "
										+ " delete from acvoucher2 where refercode in (1013,11) and referkeyfld=:PUR_KEYFLD;  "
										+ " delete from acvoucher1 where refercode in (1013,11) and referkeyfld=:PUR_KEYFLD;  "
										+ " update order1 set pur_keyfld=null where ord_no=:ORDERNO AND ORD_CODE=103; "
										+ " end; ";
								QueryExe qe = new QueryExe(sq, con);
								qe.setParaValue("ORDERNO", QRYSES_ORDER);
								qe.setParaValue("PUR_KEYFLD", QRYSES);
								qe.execute();
								qe.close();
								con.commit();
								resetFormLayout();
								showPurchaseCostView();
							} catch (Exception ex) {
								Logger.getLogger(
										frmPurchaseCost.class.getName()).log(
										Level.SEVERE, null, ex);
								parentLayout.getWindow().showNotification(
										"Error in deletion ", ex.toString(),
										Notification.TYPE_ERROR_MESSAGE);

								try {
									con.rollback();
								} catch (SQLException e) {
								}

							}
						}
					};
					// user confirmation for deleting
					String s0 = utils
							.getSqlValue(
									"select acaccount.name from acaccount,order1 where ordacc=accno and ord_code=103 and ord_no='"
											+ QRYSES_ORDER + "'", con);
					mainLayout.getWindow().addWindow(
							new YesNoDialog("Channel Plus Warning",
									"Delete purchase invoice for order no : "
											+ s0 + " Order No # "
											+ QRYSES_ORDER, cb));

				}
			});

			tbl_exp.setCellStyleGenerator(new CellStyleGenerator() {

				public String getStyle(Object itemId, Object propertyId) {
					if (propertyId != null
							&& (propertyId.toString().equals(colEAmt))) {
						return "rightalign";
					}
					if (propertyId != null
							&& (propertyId.toString().equals(colERate))) {
						return "rightalign";
					}

					if (propertyId != null
							&& propertyId.toString().equals(colEPos)) {
						return "centeralign";
					}
					return null;
				}
			});

			tbl_items.setCellStyleGenerator(new CellStyleGenerator() {

				public String getStyle(Object itemId, Object propertyId) {
					return utilsVaadin.getStyleFromCol(lstItemCols,
							(String) propertyId);

				}
			});

			data_exp.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (cursorNo < 0) {
						return;
					}
					if (((BigDecimal) data_exp.getFieldValue(cursorNo,
							"LC_EXP_AMT")) == null) {
						return;
					}
					double amt = ((BigDecimal) data_exp.getFieldValue(cursorNo,
							"LC_EXP_AMT")).doubleValue();
					double rt = ((BigDecimal) data_exp.getFieldValue(cursorNo,
							"LC_RATE")).doubleValue();
					data_exp.setFieldValue(cursorNo, "LC_KDAMT", BigDecimal
							.valueOf(amt * rt));
					calcSums(true);
				}
			});
			data_items.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (cursorNo < 0) {
						return;
					}
					if (cursorNo >= data_items.getRows().size()) {
						return;
					}
					if (((BigDecimal) data_items.getFieldValue(cursorNo,
							"ORD_PRICE")) == null) {
						return;
					}

					// CALCULATING ORDER PRICE.
					double op = ((BigDecimal) data_items.getFieldValue(
							cursorNo, "ORD_PRICE")).doubleValue();
					double pkcost = ((BigDecimal) data_items.getFieldValue(
							cursorNo, "PKCOST")).doubleValue();
					double disc = ((BigDecimal) data_items.getFieldValue(
							cursorNo, "ORD_DISCAMT")).doubleValue();
					double ord_pkqty = ((BigDecimal) data_items.getFieldValue(
							cursorNo, "PKQTY")).doubleValue();
					double ord_uqty = ((BigDecimal) data_items.getFieldValue(
							cursorNo, "QTY")).doubleValue();
					double pk = ((BigDecimal) data_items.getFieldValue(
							cursorNo, "ORD_PACK")).doubleValue();
					double rcvd_pkqty = ((BigDecimal) data_items.getFieldValue(
							cursorNo, "QTIN_BY_PACK")).doubleValue();
					double rcvd_uqty = ((BigDecimal) data_items.getFieldValue(
							cursorNo, "QTIN_BY_UNIT")).doubleValue();
					double ord_allqty = (ord_pkqty * pk) + ord_uqty;
					double rcvd_allqty = (rcvd_pkqty * pk) + rcvd_uqty;
					double ord_amt = (op - disc) * (ord_allqty / pk);
					double dlv_amt = (op - disc) * (rcvd_allqty / pk);

					// updating allqty

					data_items.setFieldValue(cursorNo, "ORD_ALLQTY", BigDecimal
							.valueOf(ord_allqty));

					data_items.setFieldValue(cursorNo, "AMOUNT2", BigDecimal
							.valueOf(ord_amt));
					// COST FOR ITEM
					if (varInvAmt != 0) {
						double cost = ((varKdCost) * ((op - disc) / pk)) * pk;
						data_items.setFieldValue(cursorNo, "PKCOST", BigDecimal
								.valueOf(cost));
					}
					// QTY RECIEVED AMOUNT

					data_items.setFieldValue(cursorNo, "DELIVERED_AMT",
							BigDecimal.valueOf(dlv_amt));
					data_items.setFieldValue(cursorNo, "ADJUST_AMT", BigDecimal
							.valueOf(dlv_amt - ord_amt));
					utilsVaadin.refreshRowFromData(data_items, tbl_items,
							cursorNo, lstItemCols);
					calcSums(false);

				}
			});
			tbl_exp.addActionHandler(new Handler() {

				public void handleAction(Action action, Object sender,
						Object target) {
					if (target == null) {
						return;
					}

					Integer rn = Integer.valueOf(target.toString());
					data_exp.deleteRow(rn);
					tbl_exp.removeItem(rn);
					calcSums(true);
				}

				public Action[] getActions(Object target, Object sender) {
					if (target == null) {
						return null;
					}

					List<Action> lstAct = new ArrayList<Action>();

					if (cmdSave.isEnabled()) {
						lstAct.add(new Action("Delete"));
					}
					Action[] act = new Action[lstAct.size()];
					return lstAct.toArray(act);
				}
			});

			// action handler for changing and removing item.

			tbl_items.addActionHandler(new Handler() {
				public void handleAction(Action action, Object sender,
						Object target) {
					if (target == null) {
						return;
					}

					Integer rn = Integer.valueOf(target.toString());

					if (action.getCaption().equals(
							mapActionItemTable.get("change_item"))) {
						add_change_item(rn);
					}

				}

				public Action[] getActions(Object target, Object sender) {
					if (target == null) {
						return null;
					}

					List<Action> lsta = new ArrayList<Action>();
					if (varNoOfSrv == 1) {
						lsta.add(new Action(mapActionItemTable
								.get("change_item")));
					}
					Action[] ac = new Action[lsta.size()];
					return lsta.toArray(ac);
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
			listenerAdded = true;

		}

	}

	public void add_change_item(final int rn) {

		try {
			wndList.setContent(listlayout);
			TableView.SelectionListener sl = new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wndList);
					try {
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
						tbl_items.setValue(rn);
						data_items.getRows().get(rn).setRowStatus(
								Row.ROW_UPDATED);

						data_items.setFieldValue(rn, "ORD_REFER", rfr);
						data_items.setFieldValue(rn, "DESCR", des);
						data_items.setFieldValue(rn, "ORD_PACKD", pkd);
						data_items.setFieldValue(rn, "ORD_UNITD", und);
						data_items.setFieldValue(rn, "ORD_PACK", pk);

						fill_item_table();

					} catch (SQLException e) {
					}

				}
			};
			final TableView tblview = utilsVaadin
					.showSearch(
							listlayout,
							sl,
							con,
							"Select reference,nvl(Descra,descr) descr,packd,unitd,pack,prd_dt,exp_dt,descr2 from items where flag=1 ORDER by descr2",
							true, new String[] { "UNITD", "PACK", "DESCR2" });

		} catch (Exception e) {
			Logger.getLogger(frmGatePass.class.getName()).log(Level.SEVERE,
					null, e);
			parentLayout.getWindow().showNotification("Error filling table",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void setFieldsReadOnly(boolean readonly) {
		txtInvNo.setReadOnly(readonly);
		txtInvdRef_name.setReadOnly(readonly);
		txtLocation.setReadOnly(readonly);
		txtKDAmount.setReadOnly(readonly);
		txtKDCost.setReadOnly(readonly);
		txtOrdNo.setReadOnly(readonly);
		txtInvAmt.setReadOnly(readonly);
		txtNetAmount.setReadOnly(readonly);
		txtRcvdDate.setReadOnly(readonly);
		txtOtherExp.setReadOnly(readonly);
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
				data_exp.createDBClassFromConnection(con);
			} catch (SQLException e) {
			}
		}

		load_data();

	}

	public void calcItemPurCost() {
		for (int i = 0; i < data_items.getRows().size(); i++) {
			double price = ((BigDecimal) data_items.getFieldValue(i,
					"ORD_PRICE")).doubleValue();
			double disc = ((BigDecimal) data_items.getFieldValue(i,
					"ORD_DISCAMT")).doubleValue();
			double pk = ((BigDecimal) data_items.getFieldValue(i, "ORD_PACK"))
					.doubleValue();
			double allqty = ((BigDecimal) data_items.getFieldValue(i, "PKQTY"))
					.doubleValue()
					* pk;
			double cost = ((varKdCost) * ((price - disc) / pk)) * pk;
			data_items.setFieldValue(i, "PKCOST", BigDecimal.valueOf(cost));
			Item itm = tbl_items.getItem(i);
			if (itm != null) {
				String v = ((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(cost)));
				Object vl = itm.getItemProperty(
						utilsVaadin.findColByCol("PKCOST", lstItemCols).descr)
						.getValue();
				if (vl instanceof Label) {
					((Label) vl).setValue(v);
				}
				if (vl instanceof TextField) {
					boolean rd = ((TextField) vl).isReadOnly();
					((TextField) vl).setReadOnly(false);
					((TextField) vl).setValue(v);
					((TextField) vl).setReadOnly(rd);

				}
			}
		}
	}

	public void enable_disable_items() throws SQLException {
		PreparedStatement pstmp1 = con.prepareStatement(
				"select prd_date,exp_date from invoice2 "
						+ " where keyfld=? and SLSMNXX=?",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		for (int i = 0; i < data_items.getRows().size(); i++) {
			Item itm = tbl_items.getItem(i);
			if (itm != null) {
				Object vl = itm
						.getItemProperty(
								utilsVaadin.findColByCol("QTIN_BY_PACK",
										lstItemCols).descr).getValue();
				if (vl != null && vl instanceof TextField
						&& (varNoOfSrv <= 0 || varNoOfSrv > 1)) {
					((TextField) vl).setEnabled(false);
				}
				Object vl_uqt = itm
						.getItemProperty(
								utilsVaadin.findColByCol("QTIN_BY_UNIT",
										lstItemCols).descr).getValue();
				if (vl_uqt != null && vl_uqt instanceof TextField
						&& (varNoOfSrv <= 0 || varNoOfSrv > 1)) {
					((TextField) vl_uqt).setEnabled(false);
				}

				Object vl_prdd = itm
						.getItemProperty(
								utilsVaadin.findColByCol("PRD_DATE",
										lstItemCols).descr).getValue();
				if (vl_prdd != null && vl_prdd instanceof DateField
						&& (varNoOfSrv <= 0 || varNoOfSrv > 1)) {
					((DateField) vl_prdd).setEnabled(false);
				}
				Object vl_expp = itm
						.getItemProperty(
								utilsVaadin.findColByCol("EXP_DATE",
										lstItemCols).descr).getValue();
				if (vl_expp != null && vl_expp instanceof DateField
						&& (varNoOfSrv <= 0 || varNoOfSrv > 1)) {
					((DateField) vl_expp).setEnabled(false);
				}

				if (varNoOfSrv == 1) {
					pstmp1
							.setBigDecimal(1, BigDecimal
									.valueOf(varReciptkeyfld));
					pstmp1.setBigDecimal(2, (BigDecimal) data_items
							.getFieldValue(i, "ORD_POS"));
					ResultSet rstmp1 = pstmp1.executeQuery();
					if (rstmp1.first()) {
						data_items.setFieldValue(i, "PRD_DATE", rstmp1
								.getTimestamp("PRD_DATE"));
						data_items.setFieldValue(i, "EXP_DATE", rstmp1
								.getTimestamp("EXP_DATE"));
						utilsVaadin.refreshRowFromData(data_items, tbl_items,
								i, lstItemCols);

					}
				}
			}
		}
		pstmp1.close();
	}

	public void calcSums(boolean doItemCost) {
		// think as initizling.

		if (utilsVaadin.findColByCol("AMOUNT2", lstItemCols) == null
				|| data_exp.getColByName("LC_KDAMT") == null) {
			return;
		}
		setFieldsReadOnly(false);
		try {

			double otherExp = data_exp.getSummaryOf("LC_KDAMT",
					localTableModel.SUMMARY_SUM);
			String a1 = new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(otherExp);
			tbl_exp.setColumnFooter(colEAmt, a1);
			double invamt = data_items.getSummaryOf("AMOUNT2",
					localTableModel.SUMMARY_SUM);
			varTotAdjustAmt = data_items.getSummaryOf("ADJUST_AMT",
					localTableModel.SUMMARY_SUM);
			varTotDeliveredAmt = data_items.getSummaryOf("DELIVERED_AMT",
					localTableModel.SUMMARY_SUM);

			String a2 = new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(invamt);
			String a3 = new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(invamt
					- varInvDiscAmt);
			String a4 = new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
					.format(varTotAdjustAmt);
			String a5 = new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
					.format(varTotDeliveredAmt);

			tbl_items.setColumnFooter(utilsVaadin.findColByCol("AMOUNT2",
					lstItemCols).descr, a2);
			tbl_items.setColumnFooter(utilsVaadin.findColByCol("ADJUST_AMT",
					lstItemCols).descr, a4);
			tbl_items.setColumnFooter(utilsVaadin.findColByCol("DELIVERED_AMT",
					lstItemCols).descr, a5);

			varOtherExpense = otherExp;
			varInvAmt = invamt;
			varInvNetAmt = invamt - varInvDiscAmt;
			txtOtherExp.setValue(a1);
			txtInvAmt.setValue(a2);
			txtNetAmount.setValue(a3);
			varKdAmt = ((invamt - varInvDiscAmt) * varCurRate)
					+ varOtherExpense;
			varKdCost = 1;
			if (varInvAmt != 0) {
				varKdCost = varKdAmt / varInvAmt;
			}
			txtKDAmount.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY))
					.format(varKdAmt));
			txtKDCost.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY))
					.format(varKdCost));
			if (doItemCost) {
				calcItemPurCost();
			}

		} finally {
			setFieldsReadOnly(true);
		}
	}

	public void fill_exp_table() throws SQLException {
		tbl_exp.removeAllItems();
		// tbl_exp.setEditable(true);
		tbl_exp.addContainerProperty(colEPos, String.class, null);
		tbl_exp.addContainerProperty(colEType, ComboBox.class, null);
		tbl_exp.addContainerProperty(colECreditAcc, String.class, null);
		tbl_exp.addContainerProperty(ColECreditCmd, Button.class, null);
		tbl_exp.addContainerProperty(colEAmt, TextField.class, null);
		tbl_exp.addContainerProperty(colERate, TextField.class, null);
		tbl_exp.addContainerProperty(colEDescr, TextField.class, null);
		tbl_exp.addContainerProperty(colEDate, DateField.class, null);
		for (int i = 0; i < data_exp.getRows().size(); i++) {
			final Object ar[] = new Object[8];
			ar[1] = new ComboBox();
			ar[3] = new Button();
			ar[4] = new TextField();
			ar[5] = new TextField();
			ar[6] = new TextField();
			ar[7] = new DateField();

			((Button) ar[3]).setCaption("...");

			((ComboBox) ar[1]).setWidth("100%");
			((Button) ar[3]).setWidth("100%");
			((TextField) ar[4]).setWidth("100%");
			((TextField) ar[5]).setWidth("100%");
			((TextField) ar[6]).setWidth("100%");
			((DateField) ar[7]).setWidth("100%");

			((TextField) ar[4]).addStyleName("rightalign");
			((TextField) ar[5]).addStyleName("rightalign");

			((ComboBox) ar[1]).setImmediate(true);
			((Button) ar[3]).setImmediate(true);
			((TextField) ar[4]).setImmediate(true);
			((TextField) ar[5]).setImmediate(true);
			((TextField) ar[6]).setImmediate(true);
			((DateField) ar[7]).setImmediate(true);

			tbl_exp.setColumnWidth(colEPos, 20);
			tbl_exp.setColumnWidth(colEType, 70);
			tbl_exp.setColumnWidth(colECreditAcc, 150);
			tbl_exp.setColumnWidth(ColECreditCmd, 40);
			tbl_exp.setColumnWidth(colEAmt, 75);
			tbl_exp.setColumnWidth(colERate, 50);
			tbl_exp.setColumnWidth(colEDescr, 100);
			tbl_exp.setColumnWidth(colEDate, 100);

			if (data_exp.getFieldValue(i, "LC_POSITION") != null) {
				ar[0] = data_exp.getFieldValue(i, "LC_POSITION").toString();
			}
			if (data_exp.getFieldValue(i, "ACNAME") != null) {
				ar[2] = data_exp.getFieldValue(i, "ACNAME");
			}

			utilsVaadin
					.FillCombo(
							(ComboBox) ar[1],
							"select NAME,descr from relists where idlist='TYPE_OF_EXP'",
							con);

			if (data_exp.getFieldValue(i, "LC_EXP_TYPE") != null) {
				((ComboBox) ar[1]).setValue(utilsVaadin.findByValue(
						((ComboBox) ar[1]), (String) data_exp.getFieldValue(i,
								"LC_EXP_TYPE")));
			}
			if (data_exp.getFieldValue(i, "LC_EXP_AMT") != null) {
				double vl = ((BigDecimal) data_exp.getFieldValue(i,
						"LC_EXP_AMT")).doubleValue();

				((TextField) ar[4]).setValue((new DecimalFormat(
						Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(vl)));
			}
			if (data_exp.getFieldValue(i, "LC_RATE") != null) {
				double vl = ((BigDecimal) data_exp.getFieldValue(i, "LC_RATE"))
						.doubleValue();
				((TextField) ar[5]).setValue(vl);
			}
			if (data_exp.getFieldValue(i, "LC_DESCR") != null) {
				String vldescr = data_exp.getFieldValue(i, "LC_DESCR")
						.toString();
				((TextField) ar[6]).setValue(vldescr);
			}
			if (data_exp.getFieldValue(i, "LC_DATE") != null) {
				((DateField) ar[7]).setValue((Date) data_exp.getFieldValue(i,
						"LC_DATE"));
				((DateField) ar[7]).setDateFormat(utils.FORMAT_SHORT_DATE);
				((DateField) ar[7]).setResolution(DateField.RESOLUTION_DAY);
			}
			final int rn = i;

			ValueChangeListener vlc = new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					// lc_position
					BigDecimal a0 = BigDecimal.valueOf(Float.valueOf(
							(String) ar[0]).doubleValue());
					data_exp.setFieldValue(rn, "LC_POSITION", a0);
					// lc_exp_type
					String a1 = "";
					if (((ComboBox) ar[1]).getValue() != null) {
						dataCell d1 = (dataCell) ((ComboBox) ar[1]).getValue();
						a1 = (String) d1.getValue();
					}
					data_exp.setFieldValue(rn, "LC_EXP_TYPE", a1);

					// /lc_exp-amt
					BigDecimal a4 = BigDecimal.valueOf(Float.valueOf(0));

					if (((TextField) ar[4]).getValue() != null) {
						a4 = BigDecimal.valueOf(Float.valueOf(
								((TextField) ar[4]).getValue().toString())
								.doubleValue());
					}
					data_exp.setFieldValue(rn, "LC_EXP_AMT", a4);

					// lc_rate
					BigDecimal a5 = BigDecimal.valueOf(Float.valueOf(0));

					if (((TextField) ar[5]).getValue() != null) {
						a5 = BigDecimal.valueOf(Float.valueOf(
								((TextField) ar[5]).getValue().toString())
								.doubleValue());
					}
					data_exp.setFieldValue(rn, "LC_RATE", a5);
					// lc_descr
					String a6 = "";

					if (((TextField) ar[6]).getValue() != null
							&& ((TextField) ar[6]).getValue().toString()
									.length() > 0) {
						a6 = ((TextField) ar[6]).getValue().toString();
					}
					data_exp.setFieldValue(rn, "LC_DESCR", a6);

					Date a7 = null;

					if (((DateField) ar[7]).getValue() != null) {
						a7 = (Date) ((DateField) ar[7]).getValue();
					}
					data_exp.setFieldValue(rn, "LC_DATE", a7);

				}

			};

			((ComboBox) ar[1]).addListener(vlc);
			((Button) ar[3]).addListener(vlc);
			((TextField) ar[4]).addListener(vlc);
			((TextField) ar[5]).addListener(vlc);
			((TextField) ar[6]).addListener(vlc);
			((DateField) ar[7]).addListener(vlc);

			((Button) ar[3]).addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						wndList.setContent(listlayout);
						wndList.setWidth("70%");
						wndList.setHeight("70%");
						wndList.setImmediate(true);
						wndList.setModal(true);
						listlayout.setSizeFull();
						final DBClass dbcx = new DBClass(con);
						listlayout.removeAllComponents();
						final TableView tblview = new TableView(dbcx);
						final TableView tblfinal = tblview;
						String sql = "select accno,name from acaccount where childcount=0 and actype=0 order by path";
						tblview.FetchSql(sql);
						tblview.setParentPanel(listlayout);
						tblview
								.setOnseleciton(new Property.ValueChangeListener() {
									private String l_ac = "";
									private String l_code = "";

									public void valueChange(
											ValueChangeEvent event) {
										// getting selected value and name
										String s = tblfinal
												.getData()
												.getFieldValue(
														tblfinal
																.getSelectionValue(),
														"ACCNO").toString();
										String s1 = tblfinal
												.getData()
												.getFieldValue(
														tblfinal
																.getSelectionValue(),
														"NAME").toString();
										if (l_ac.length() > 0) {
											l_code = s;
										}
										// finding any refer is there...
										int fnd_refer = Integer
												.valueOf(utils
														.getSqlValue(
																"select nvl(count(*),0) from "
																		+ " c_ycust where ac_no='"
																		+ s
																		+ "'",
																dbcx
																		.getDbConnection()));
										if (fnd_refer > 0) {
											try {
												l_ac = s;
												l_code = "";
												tblview.setOnseleciton(null);
												tblview.getData().clearALl();
												tblview
														.FetchSql("select CODE accno,name from C_YCUST where AC_NO='"
																+ l_ac
																+ "' AND childcount=0 order by path");
												tblview.createView(true);
												tblview.setOnseleciton(this);
												return;
											} catch (Exception e) {

											}

										}
										// changing to data model and table
										// view.
										data_exp.setFieldValue(rn,
												"LC_CREDIT_AC", l_ac);
										data_exp.setFieldValue(rn,
												"PUR_CREDIT_REFER", l_code);
										data_exp
												.setFieldValue(rn, "ACNAME", s1);

										Item itm = tbl_exp.getItem(rn);
										itm.getItemProperty(colECreditAcc)
												.setValue(s1);
										parentLayout.getWindow().removeWindow(
												wndList);

									}
								});

						tblview.createView();

						if (!parentLayout.getWindow().getChildWindows()
								.contains(wndList)) {
							parentLayout.getWindow().addWindow(wndList);
						}

					} catch (Exception e) {

					}
				}
			});
			tbl_exp.addItem(ar, Integer.valueOf(i));
		}

	}

	public void fill_item_table() throws SQLException {

		tbl_items.removeAllItems();
		utilsVaadin.applyColumns("PURCHASE.PURCOST", tbl_items, lstItemCols,
				con);
		utilsVaadin.query_data2(tbl_items, data_items, lstItemCols);

		/*
		 * tbl_items.removeAllItems();
		 * utilsVaadin.applyColumns("PURCHASE.PURCOST", tbl_items, lstItemCols,
		 * con); for (int i = 0; i < data_items.getRows().size(); i++) { final
		 * Object ar[] = new Object[lstItemCols.size()]; for (int j = 0; j <
		 * lstItemCols.size(); j++) { ar[j] = new Label(); if
		 * (data_items.getFieldValue(i, lstItemCols.get(j).colname) != null) {
		 * ((Label) ar[j]).setValue(data_items.getFieldValue(i,
		 * lstItemCols.get(j).colname).toString()); if
		 * (lstItemCols.get(j).display_format .equals("MONEY_FORMAT")) { Number
		 * n = (Number) data_items.getFieldValue(i, lstItemCols.get(j).colname);
		 * ((Label) ar[j]).setValue((new DecimalFormat(
		 * Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)).format(n.doubleValue())); } ((Label) ar[j])
		 * .setStyleName(lstItemCols.get(j).display_align); } }
		 * tbl_items.addItem(ar, Integer.valueOf(i));
		 * 
		 * }
		 */
	}

	public void fetch_exp_amount_table() throws SQLException {
		data_exp.clearALl();
		data_exp
				.executeQuery(
						"select LCS_EXPENSES.*,nvl((select max(name) from c_ycust where code=pur_credit_refer),ACACCOUNT.NAME) ACNAME,LC_RATE*LC_EXP_AMT LC_KDAMT "
								+ "from LCS_EXPENSES,ACACCOUNT where LCS_EXPENSES.keyfld='"
								+ QRYSES
								+ "'"
								+ " AND ACACCOUNT.ACCNO=lc_credit_ac", true);

		fill_exp_table();

	}

	public void fetch_item_data() throws SQLException {
		data_items.clearALl();
		String sq = "select order2.*,items.descr,items.pkaver,items.prd_dt,items.exp_dt,"
				+ " (((ord_price-ord_discamt)/ord_pack)*ord_allqty) AMOUNT2, 0 DISCP,(DELIVEREDQTY)/ORD_PACK QTIN_BY_PACK,0 QTIN_BY_UNIT, "
				+ " ORD_UNQTY QTY,ORD_PKQTY PKQTY,0 FREEQTY,ORD_FREEQTY/ORD_PACK FREEPKQTY ,0 PKCOST,items.prd_dt prd_date,items.exp_dt exp_date,"
				+ "(((ORD_PRICE-ORD_DISCAMT)/ORD_PACK)*order2.deliveredqty) DELIVERED_AMT,"
				+ "(((ord_price-ord_discamt)/ord_pack)*ord_allqty)-(((ORD_PRICE-ORD_DISCAMT)/ORD_PACK)*order2.deliveredqty) ADJUST_AMT"
				+ ",ord_pack pack,items.packing_descr  from order2,items where order2.ord_refer=items.reference and ord_code=103 and ord_no="
				+ QRYSES_ORDER + " order by order2.ord_pos";
		data_items.executeQuery(sq, true);
		data_items.setMasterRowStatusAll(Row.ROW_QUERIED);
		fill_item_table();
		enable_disable_items();
	}

	public void load_data() {
		setFieldsReadOnly(false);
		// INITIALIZINGS
		data_exp.clearALl();
		data_items.clearALl();
		tbl_exp.removeAllItems();
		tbl_items.removeAllItems();

		txtInvAmt.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(0)));
		txtOtherExp.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(0)));
		txtNetAmount
				.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(0)));
		txtRate.setValue(new BigDecimal(1));
		txtCurrency.setValue("KWD");
		txtBankName.setValue("");
		txtInsCo.setValue("");
		txtInvDiscAmt
				.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(0)));
		txtInvdRef_name.setValue("");
		txtKDAmount.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(0)));
		txtKDCost.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(1)));
		txtInvNo.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(-1)));
		txtLCNo.setValue("");
		txtLocation.setValue("");
		txtOrdNo.setValue("");

		varInvAmt = 0;
		varInvDiscAmt = 0;
		varInvNetAmt = 0;
		varCurRate = 1;
		varKdAmt = 0;
		varKdCost = 0;
		varOtherExpense = 0;
		varNoOfSrv = 0;

		if (txtInvDate.getValue() != null) {
			((java.util.Date) txtInvDate.getValue()).setTime(System
					.currentTimeMillis());
		} else {
			java.util.Date dt = new java.util.Date(System.currentTimeMillis());
			txtInvDate.setValue(dt);
		}

		if (txtRcvdDate.getValue() != null) {
			((java.util.Date) txtRcvdDate.getValue()).setTime(System
					.currentTimeMillis());
		} else {
			java.util.Date dt = new java.util.Date(System.currentTimeMillis());
			txtRcvdDate.setValue(dt);
		}

		txtRemarks.setValue("");
		txtSalord.setValue("");
		txtShipCo.setValue("");
		txtSuppInvNo.setValue("");
		txtInsCo.setValue("");
		// txtInvType
		// FETCHING DATA FROM ORDER AND PUR1
		try {
			if (ps_ord != null) {
				ps_ord.close();
			}
			ps_ord = con.prepareStatement(
					"select *from order1 where ord_code=103 and ord_no="
							+ QRYSES_ORDER, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs_ord = ps_ord.executeQuery();
			// order assignment
			String tmpStr1 = "";
			if (!rs_ord.first()) {
				throw new Exception("Can't find order");
			}
			txtOrdNo.setValue(utils.getSqlValue(
					"select name||'-'||accno from acaccount where accno='"
							+ rs_ord.getString("ORDACC") + "'", con));

			txtLocation.setValue(utils.getSqlValue(
					"select name||'-'||code from locations where code='"
							+ rs_ord.getString("location_code") + "'", con));
			varOrdNo = Float.valueOf(QRYSES_ORDER);
			varOrdAccount = rs_ord.getString("ORDACC");
			varCustCode = rs_ord.getString("ord_ref");
			varPurRef = utils.getSqlValue(
					"select ac_no from c_ycust where code='"
							+ rs_ord.getString("ORD_REF") + "'", con);
			varPurRefName = utils.getSqlValue(
					"select nvl(name,namea) from c_ycust where code='"
							+ rs_ord.getString("ORD_REF") + "'", con);
			varLocationCode = rs_ord.getString("location_code");
			varOrdFlag = rs_ord.getFloat("ORD_FLAG");
			if (rs_ord.getString("PUR_ADJUST_KEYFLD") == null
					|| rs_ord.getString("PUR_ADJUST_KEYFLD").length() == 0) {
				varAdjKeyfld = -1;
			} else {
				varAdjKeyfld = rs_ord.getFloat("PUR_ADJUST_KEYFLD");
			}

			if (rs_ord.getString("RECIPT_KEYFLD") == null
					|| rs_ord.getString("RECIPT_KEYFLD").length() == 0) {
				varReciptkeyfld = Integer.valueOf(-1);
				optClose.setEnabled(false);
				optClose.select(OPTION_OPEN);
			} else {
				varReciptkeyfld = rs_ord.getInt("RECIPT_KEYFLD");
				optClose.setEnabled(true);
			}
			varNoOfSrv = Float.valueOf(utils.getSqlValue(
					"select nvl(count(*),0) from invoice1 where invoice_code=13 and orderno='"
							+ QRYSES_ORDER + "'", con));

			varRcvdRate = 100
					/ (rs_ord.getFloat("ORDERDQTY"))
					* (rs_ord.getFloat("DELIVEREDQTY") + rs_ord
							.getFloat("DELIVERED_FREEQTY"));

			// / SAVE ENABLE/DSIABLE ,
			cmdSave.setEnabled(true);
			if (varOrdFlag == 1 && varAdjKeyfld > 0) {
				optClose.setValue(OPTION_CLOSE_JV);
				cmdSave.setEnabled(false);

			}
			if (varOrdFlag == 1 && varAdjKeyfld <= 0) {
				optClose.setValue(OPTION_CLOSE_NO_JV);
				cmdSave.setEnabled(false);
			}
			if (varOrdFlag == 2) {
				optClose.setValue(OPTION_OPEN);
				cmdSave.setEnabled(true);
			}
			// status recieved goods

			java.util.Date tmpInvDate = new Date(System.currentTimeMillis());

			utilsVaadin
					.FillCombo(txtInvType,
							"select no,nvl(descr,descra) descr from invoicetype where location_code='"
									+ varLocationCode
									+ "' and flag=1 order by no", con);
			// if assign invoice
			if (QRYSES.length() <= 0) {
				String ds = utils
						.getSqlValue(
								"select repair.getsetupvalue_2('DEFAULT_STORE') from dual",
								con);
				txtStore.setValue(utilsVaadin.findByValue(txtStore, ds));
				ds = utils
						.getSqlValue(
								"select repair.getsetupvalue_2('DEFAULT_TYPE') from dual",
								con);
				txtInvType.setValue(utilsVaadin.findByValue(txtInvType, ds));
				txtInvDate.setValue(tmpInvDate);
				txtRcvdDate.setValue(tmpInvDate);
				varInvNo = Float.valueOf(utils.getSqlValue(
						"select nvl(max(invoice_no),0)+1 from pur1 where invoice_code=11"
								+ " and location_code='" + varLocationCode
								+ "'", con));
				txtInvNo.setValue(String.valueOf(varInvNo));
			} else {
				if (ps_data != null) {
					ps_data.close();
				}
				ps_data = con.prepareStatement(
						"select *from pur1 where keyfld=" + QRYSES,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				rs_data = ps_data.executeQuery();
				// java.util.Date tmpdt = (java.util.Date)
				// txtInvDate.getValue();
				if (!rs_data.first()) {
					throw new Exception("keyID# " + QRYSES
							+ " Invoice not found ");
				}

				varInvNo = rs_data.getFloat("INVOICE_NO");
				varKdAmt = ((rs_data.getFloat("inv_amt") - rs_data
						.getFloat("disc_amt")) * rs_data.getFloat("kdcost"));
				varOtherExpense = rs_data.getFloat("chg_kdamt");
				varCurRate = rs_data.getFloat("RATE");
				varInvAmt = rs_data.getFloat("inv_amt");
				varInvDiscAmt = rs_data.getFloat("disc_amt");
				varInvNetAmt = varInvAmt - varInvDiscAmt;
				varKdAmt = varInvNetAmt * varCurRate;
				((Date) txtInvDate.getValue()).setTime(rs_data.getDate(
						"INVOICE_DATE").getTime());
				txtInvType.setValue(utilsVaadin.findByValue(txtInvType, rs_data
						.getString("type")));
				txtInvNo.setValue(String.valueOf(varInvNo));
				txtInvAmt.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
						.format(varInvAmt)));
				txtInvDiscAmt.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
						.format(rs_data.getFloat("disc_amt"))));
				txtInvdRef_name.setValue(varPurRef + " - " + varPurRefName);

				txtKDAmount.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
						.format(varKdAmt)));
				txtKDCost.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
						.format(rs_data.getFloat("kdcost"))));
				txtNetAmount.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
						.format(varInvAmt - varInvDiscAmt)));
				txtOtherExp.setValue((new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
						.format(varOtherExpense)));
				txtRate.setValue(rs_data.getFloat("rate"));
				txtStore.setValue(utilsVaadin.findByValue(txtStore, rs_data
						.getString("stra")));

				if (rs_data.getDate("duedate") != null) {
					tmpInvDate.setTime(rs_data.getDate("duedate").getTime());
					txtInvDate.setValue(tmpInvDate);
					txtRcvdDate.setValue(tmpInvDate);
				}
				if (rs_data.getString("lcno") != null) {
					txtLCNo.setValue(rs_data.getString("lcno"));
				}
				if (rs_data.getString("ins_no") != null) {
					txtPolicyNo.setValue(rs_data.getString("ins_no"));
				}
				if (rs_data.getString("memo") != null) {
					txtRemarks.setValue(rs_data.getString("memo"));
				}
				if (rs_data.getString("LPNO") != null) {
					txtSalord.setValue(rs_data.getString("LPNO"));
				}
				if (rs_data.getString("shipco") != null) {
					txtShipCo.setValue(rs_data.getString("shipco"));
				}
				if (rs_data.getString("SUPINVNO") != null) {
					txtSuppInvNo.setValue(rs_data.getString("SUPINVNO"));
				}
				if (rs_data.getString("bank") != null) {
					txtBankName.setValue(rs_data.getString("bank"));
				}
				if (rs_data.getString("currency") != null) {
					txtCurrency.setValue(rs_data.getString("currency"));
				}
				if (rs_data.getString("ins_co") != null) {
					txtInsCo.setValue(rs_data.getString("ins_co"));
				}

			}
			fetch_exp_amount_table();
			fetch_item_data();
			txtTitle.setValue(titleStr + " " + txtOrdNo.getValue());
			calcSums(true);
			ps_ord.close();
			txtTitle2.setValue(titleStr2 + " " + varRcvdRate
					+ "% , SRV KEY ID: " + varReciptkeyfld + " No Of SRV : "
					+ varNoOfSrv + "  , Purchase KEYID: " + varPurKeyfld
					+ "  Order No :: " + varOrdNo);
		} catch (Exception ex) {
			Logger.getLogger(frmPurchaseCost.class.getName()).log(Level.SEVERE,
					null, ex);
			parentLayout.getWindow().showNotification("Unable to load data",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}

		setFieldsReadOnly(true);
	}

	public void print_data() {
		try {
			utilsVaadinPrintHandler.printPurchase(Integer.valueOf(QRYSES), con);
		} catch (Exception ex) {
			mainLayout.getWindow().showNotification("Unable to print:",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			Logger.getLogger(frmPurchaseCost.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public void save_data(boolean backToList, boolean doPrint) {
		if (QRYSES_ORDER.length() == 0) {
			return;
		}

		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		String sql5 = "";
		String sql6 = "";

		// 1=periodcode 2=locatoin_code 3=invoice_no 4=invoice_date, 5=stra

		sql1 = " begin insert into PUR1(PERIODCODE, LOCATION_CODE, INVOICE_NO, "
				+ "INVOICE_CODE, TYPE, INVOICE_DATE, STRA, SLSMN, "
				+ "MEMO, INV_REF, INV_REFNM, INV_AMT, DISC_AMT, INV_COST, "
				+ "FLAG, CREATDT, LPNO, BKNO, KEYFLD, USERNAME, SUPINVNO, SHIPCO, "
				+ "INS_CO, BANK, LCNO, INS_NO, RATE, CURRENCY, KDCOST, CHG_KDAMT, ORDERNO, C_CUS_NO,YEAR,NO_OF_RECIEVED) VALUES"
				+ " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'2003',?); "
				+ "x_post_purchase(?);update order1 set pur_keyfld=? where ord_code=103 and ord_no=?; "
				+ "update order2 set pur_keyfld=? where ord_code=103 and ord_no=?;end;";

		sql2 = " insert into pur2(PERIODCODE, LOCATION_CODE, INVOICE_NO, "
				+ "INVOICE_CODE, TYPE, ITEMPOS, REFER, STRA, PRICE, PKCOST, "
				+ "DISC_AMT, PACK, PACKD, UNITD, DAT, QTY, PKQTY, FREEQTY, FREEPKQTY, "
				+ "ALLQTY, PRD_DATE, EXP_DATE, YEAR, FLAG, ORDWAS, KEYFLD, RATE, CURRENCY, "
				+ "CREATDT, ORDERNO, QTYIN, QTYOUT, DISC_AMT_GROSS, SLSMNXX, RECIEVED_KEYFLD, FREE_ALLQTY) values "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		sql3 = "insert into LCS_EXPENSES(KEYFLD, LC_POSITION, LC_CODE, LC_EXP_TYPE, LC_DEBIT_AC, LC_CREDIT_AC, "
				+ "JVKEYFLD, LC_EXP_AMT, LC_DESCR, LC_DATE, LC_RATE,PUR_CREDIT_REFER) values "
				+ " (?,?,?,?,?,?,?,?,?,?,?,?)";
		sql4 = "begin update order1 set adjust_amount=?,adjust_currency=?,adjust_rate=?,adjust_date=?,adjust_descr=? "
				+ " where ord_code=103 and ord_no=?;close_order(?,?); end;";
		sql5 = "declare "
				+ "old_qty number; strn number;prdd date;expp date;rfr varchar2(100);fq number; "
				+ " begin select max(REFER),max(stra),nvl(sum(allqty+free_allqty),0) ,max(prd_date),max(exp_date),nvl(sum(free_allqty),0) into rfr,strn,old_qty,prdd,expp,fq from "
				+ " invoice2 where keyfld=:KEYFLD and slsmnxx=:ORD_POS;"
				+ " repair.outstore(strn ,rfr, old_qty,prdd, expp ); "
				+ " update invoice2 set pkqty=:PACKQTY, qty=:QTY,allqty=:ALLQTY,qtyin=:ALLQTY + fq,qtyout=0, pack=:PACK,PACKD=:PACKD,refer=:REFER,prd_date=:PRD_DATE,exp_date=:EXP_DATE,free_allqty=fq "
				+ " where keyfld=:KEYFLD and slsmnxx=:ORD_POS;"
				+ " repair.instore(strn, :REFER , :ALLQTY + fq ,:PRD_DATE ,:EXP_DATE );  end;";
		sql6 = " declare a1 number; a2 number;a3 number; begin "
				+ " update order2  set ord_allqty=:ORD_ALLQTY , ord_PKQTY=:ORD_PKQTY ,ord_unqty=:ORD_UNQTY,DELIVEREDQTY=:DELIVEREDQTY,"
				+ " ord_price=:ORD_PRICE, ord_discamt=:ORD_DISCAMT,ORD_REFER=:ORD_REFER,ORD_PACKD=:ORD_PACKD,ORD_PACK=:ORD_PACK, DESCR= :DESCR"
				+ " where  ord_no=:ORD_NO and ord_pos=:ORD_POS;"
				+ " select nvl(sum(deliveredqty),0),nvl(sum(delivered_freeqty),0),nvl(sum(ord_allqty),0) into a1,a2,a3 "
				+ " from order2 where ord_code=103 and ord_no=:ORD_NO ; update order1 set deliveredqty=a1 ,delivered_freeqty=a2,orderdqty=a3"
				+ " where ord_code=103 and ord_no=:ORD_NO ;end;";
		try {
			QueryExe qe_upd_inv2 = new QueryExe(sql5, con);
			QueryExe qe_upd_ord2 = new QueryExe(sql6, con);
			qe_upd_inv2.parse();
			qe_upd_ord2.parse();
			calcSums(true);
			if (varTotAdjustAmt == 0 && optClose.getValue() == OPTION_CLOSE_JV) {
				throw new Exception(
						"Invalid Close JV Option , select Close with No Adjustment #"
								+ varTotAdjustAmt);
			}

			con.setAutoCommit(false);

			String pcode = utils.getSqlValue(
					"select repair.getcurperiodcode from dual", con);
			PreparedStatement ps_tmp1 = con.prepareStatement(
					"select prd_dt,exp_dt,pkaver from items where reference=?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (ps_data != null) {
				ps_data.close();
			}
			ps_data = con.prepareStatement(
					"select *from order1 where ord_code=103 and ord_no="
							+ QRYSES_ORDER, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs_data = ps_data.executeQuery();

			// if purchase existed then remove it
			double l_flg = 1;
			String l_rcptkeyfld = "";
			if (varReciptkeyfld < 0) {
				l_rcptkeyfld = "";
				l_flg = 1;
			} else {
				l_flg = 2;
				l_rcptkeyfld = String.valueOf(varReciptkeyfld);

			}
			if (QRYSES.length() <= 0) {
				varPurKeyfld = Integer.valueOf(utils.getSqlValue(
						"select nvl(max(keyfld),0)+1 from pur1", con));
				varInvNo = Integer.valueOf(utils.getSqlValue(
						"select nvl(max(invoice_no),0)+1 from pur1 "
								+ "where invoice_code=11 and location_code='"
								+ varLocationCode + "'", con));

			} else {
				varPurKeyfld = Integer.valueOf(QRYSES);
				varInvNo = Integer.valueOf(utils.getSqlValue(
						"select invoice_no from pur1 where " + " keyfld="
								+ varPurKeyfld, con));
			}
			PreparedStatement ps_del1 = con
					.prepareStatement(" declare cursor itm is select *from pur2 where keyfld="
							+ varPurKeyfld
							+ "; cursor exp is select *from LCS_EXPENSES where keyfld="
							+ varPurKeyfld
							+ "; begin  for x in itm loop repair.decitemcount(x.refer); end loop;"
							+ "delete from pur1 where keyfld="
							+ varPurKeyfld
							+ "; "
							+ "delete from pur2 where keyfld="
							+ varPurKeyfld
							+ ";for x in exp loop REPAIR.DECACCUSES(x.lc_credit_ac);REPAIR.DECACCUSES(x.lc_debit_ac); end loop;"
							+ "delete from lcs_expenses where keyfld= "
							+ varPurKeyfld + "; end;");
			ps_del1.executeUpdate();
			ps_del1.close();
			double pos = 0;
			varOrdNo = Float.valueOf(QRYSES_ORDER);
			int l_invtype = Integer.valueOf(((dataCell) txtInvType.getValue())
					.getValue().toString());
			java.sql.Date l_create_dt = new java.sql.Date(System
					.currentTimeMillis());
			int l_store = Integer.valueOf(((dataCell) txtStore.getValue())
					.getValue().toString());
			java.sql.Date inv_dt = new java.sql.Date(
					((java.util.Date) txtInvDate.getValue()).getTime());
			String accno = utils.getSqlValue(
					"select ac_no from c_ycust where code='" + varCustCode
							+ "'", con);
			PreparedStatement ps_ins2 = con.prepareStatement(sql2);
			PreparedStatement ps_ins1 = con.prepareStatement(sql1);
			PreparedStatement ps_ins3 = con.prepareStatement(sql3);

			for (Iterator iterator = tbl_items.getItemIds().iterator(); iterator
					.hasNext();) {
				Integer idno = (Integer) iterator.next();
				Item itm = tbl_items.getItem(idno);
				ps_tmp1.setString(1, (String) data_items.getFieldValue(idno,
						"ORD_REFER"));
				ResultSet rs_tmp1 = ps_tmp1.executeQuery();
				rs_tmp1.first();
				pos++;
				double l_pk = ((BigDecimal) data_items.getFieldValue(idno,
						"ORD_PACK")).doubleValue();
				double l_pk_qty = ((BigDecimal) data_items.getFieldValue(idno,
						"PKQTY")).doubleValue();
				double l_qty = ((BigDecimal) data_items.getFieldValue(idno,
						"QTY")).doubleValue();
				double l_pk_allqty = (l_pk_qty * l_pk) + l_qty;
				double l_pk_freeqty = ((BigDecimal) data_items.getFieldValue(
						idno, "ORD_FREEALLQTY")).doubleValue()
						/ l_pk;
				double l_pk_freeallqty = l_pk_freeqty * l_pk;
				double l_pk_rcvd_qty = ((BigDecimal) data_items.getFieldValue(
						idno, "QTIN_BY_PACK")).doubleValue();
				double l_rcvd_qty = ((BigDecimal) data_items.getFieldValue(
						idno, "QTIN_BY_UNIT")).doubleValue();
				double l_rcvd_allqty = (l_pk_rcvd_qty * l_pk) + l_rcvd_qty;
				Timestamp l_prd_dt = (Timestamp) data_items.getFieldValue(idno,
						"PRD_DATE");
				Timestamp l_exp_dt = (Timestamp) data_items.getFieldValue(idno,
						"EXP_DATE");

				if (l_rcvd_qty < 0 || l_pk_rcvd_qty < 0 || l_rcvd_allqty < 0
						|| l_pk_allqty < 0 || l_pk_qty < 0 || l_qty < 0) {
					throw new SQLException("negative quantity not allowed");
				}
				ps_ins2.setString(1, pcode);
				ps_ins2.setString(2, varLocationCode);
				ps_ins2.setDouble(3, varInvNo);
				ps_ins2.setInt(4, 11); // invoice code
				ps_ins2.setInt(5, l_invtype); // type
				ps_ins2.setDouble(6, ((BigDecimal) data_items.getFieldValue(
						idno, "ORD_POS")).doubleValue());// itempos
				ps_ins2.setString(7, data_items
						.getFieldValue(idno, "ORD_REFER").toString());// refer
				ps_ins2.setDouble(8, l_store);// stra
				ps_ins2.setBigDecimal(9, (BigDecimal) data_items.getFieldValue(
						idno, "ORD_PRICE"));// price
				ps_ins2.setBigDecimal(10, (BigDecimal) data_items
						.getFieldValue(idno, "PKCOST"));// pkcost
				ps_ins2.setBigDecimal(11, (BigDecimal) data_items
						.getFieldValue(idno, "ORD_DISCAMT"));// disc_amt
				ps_ins2.setBigDecimal(12, (BigDecimal) data_items
						.getFieldValue(idno, "ORD_PACK"));// pack
				ps_ins2.setString(13, (String) data_items.getFieldValue(idno,
						"ORD_PACKD"));// packd
				ps_ins2.setString(14, (String) data_items.getFieldValue(idno,
						"ORD_UNITD"));// unitd
				ps_ins2.setDate(15, inv_dt);// dat
				ps_ins2.setBigDecimal(16, (BigDecimal) data_items
						.getFieldValue(idno, "QTY"));// qty
				ps_ins2.setBigDecimal(17, (BigDecimal) data_items
						.getFieldValue(idno, "PKQTY"));// pkqty
				ps_ins2.setDouble(18, 0);// free qty
				ps_ins2.setBigDecimal(19, (BigDecimal) data_items
						.getFieldValue(idno, "FREEPKQTY"));// freepkqty
				ps_ins2.setBigDecimal(20, (BigDecimal) data_items
						.getFieldValue(idno, "ORD_ALLQTY"));// allqty
				ps_ins2.setTimestamp(21, l_prd_dt);// prd_date
				ps_ins2.setTimestamp(22, l_exp_dt);// exp_date
				ps_ins2.setString(23, "2003");// year
				ps_ins2.setDouble(24, l_flg); // flag
				ps_ins2.setBigDecimal(25, (BigDecimal) data_items
						.getFieldValue(idno, "ORD_POS"));// ordwas
				ps_ins2.setBigDecimal(26, BigDecimal.valueOf(varPurKeyfld));// keyfld
				ps_ins2.setBigDecimal(27, BigDecimal.valueOf(varCurRate));// rate
				ps_ins2.setString(28, (String) txtCurrency.getValue());// currency
				ps_ins2.setDate(29, l_create_dt);// creatdt
				ps_ins2.setDouble(30, varOrdNo);// orderno
				ps_ins2.setBigDecimal(31, (BigDecimal) data_items
						.getFieldValue(idno, "ORD_ALLQTY"));// qtyin
				ps_ins2.setDouble(32, 0);// qtyout
				ps_ins2.setDouble(33, 0);// disc_amt_gross
				ps_ins2.setString(34, "");// slsnxx
				ps_ins2.setString(35, l_rcptkeyfld);// rcvd_keyfld
				ps_ins2.setBigDecimal(36, (BigDecimal) data_items
						.getFieldValue(idno, "ORD_FREEALLQTY"));// free_allqty
				ps_ins2.executeUpdate();

				if (varNoOfSrv == 1) {
					qe_upd_inv2.setParaValue("PACKQTY", l_pk_rcvd_qty);
					qe_upd_inv2.setParaValue("QTY", l_rcvd_qty);
					qe_upd_inv2.setParaValue("ALLQTY", l_rcvd_allqty);
					qe_upd_inv2.setParaValue("KEYFLD", varReciptkeyfld);
					qe_upd_inv2.setParaValue("REFER", data_items.getFieldValue(
							idno, "ORD_REFER").toString());
					qe_upd_inv2.setParaValue("PACKD", data_items.getFieldValue(
							idno, "ORD_PACKD").toString());
					qe_upd_inv2.setParaValue("PACK", BigDecimal.valueOf(l_pk));
					qe_upd_inv2.setParaValue("PRD_DATE", l_prd_dt);
					qe_upd_inv2.setParaValue("EXP_DATE", l_exp_dt);
					qe_upd_inv2.setParaValue("ORD_POS", (BigDecimal) data_items
							.getFieldValue(idno, "ORD_POS"));

					qe_upd_inv2.execute(false);
				}
				qe_upd_ord2.setParaValue("ORD_ALLQTY", l_pk_allqty);
				qe_upd_ord2.setParaValue("ORD_PKQTY", l_pk_qty);
				qe_upd_ord2.setParaValue("ORD_UNQTY", l_qty);
				qe_upd_ord2.setParaValue("ORD_NO", QRYSES_ORDER);
				qe_upd_ord2.setParaValue("ORD_POS", (BigDecimal) data_items
						.getFieldValue(idno, "ORD_POS"));
				qe_upd_ord2.setParaValue("ORD_PRICE", (BigDecimal) data_items
						.getFieldValue(idno, "ORD_PRICE"));
				qe_upd_ord2.setParaValue("ORD_DISCAMT", (BigDecimal) data_items
						.getFieldValue(idno, "ORD_DISCAMT"));
				qe_upd_ord2.setParaValue("DELIVEREDQTY", (l_rcvd_allqty));
				qe_upd_ord2.setParaValue("ORD_REFER", data_items.getFieldValue(
						idno, "ORD_REFER").toString());
				qe_upd_ord2.setParaValue("DESCR", data_items.getFieldValue(
						idno, "DESCR").toString());
				qe_upd_ord2.setParaValue("ORD_PACKD", data_items.getFieldValue(
						idno, "ORD_PACKD").toString());
				qe_upd_ord2.setParaValue("ORD_PACK", BigDecimal.valueOf(l_pk));
				qe_upd_ord2.execute(false);
			}
			ps_ins2.close();
			qe_upd_inv2.close();
			qe_upd_ord2.close();
			for (Iterator iterator = tbl_exp.getItemIds().iterator(); iterator
					.hasNext();) {
				Integer idno = (Integer) iterator.next();
				Item itm = tbl_exp.getItem(idno);
				java.sql.Date l_exp_dt = new java.sql.Date(
						((java.util.Date) data_exp.getFieldValue(idno,
								"LC_DATE")).getTime());
				ps_ins3.setBigDecimal(1, BigDecimal.valueOf(varPurKeyfld)); // KEYFLD
				ps_ins3.setDouble(2, Float.valueOf(data_exp.getFieldValue(idno,
						"LC_POSITION").toString())); // lc_position
				ps_ins3.setString(3, "x"); // lc_code
				ps_ins3.setString(4, data_exp
						.getFieldValue(idno, "LC_EXP_TYPE").toString()); // lc_exp_type
				ps_ins3.setString(5, varOrdAccount); // lc_debit_ac
				ps_ins3.setString(6, data_exp.getFieldValue(idno,
						"LC_CREDIT_AC").toString()); // lc_credit_ac
				ps_ins3.setString(7, ""); // jvkeyfld
				ps_ins3.setDouble(8, Float.valueOf(data_exp.getFieldValue(idno,
						"LC_EXP_AMT").toString())); // lc_exp_amt
				ps_ins3.setString(9, data_exp.getFieldValue(idno, "LC_DESCR")
						.toString()); // lc_descr
				ps_ins3.setDate(10, l_exp_dt); // lc_date
				ps_ins3.setDouble(11, Float.valueOf(data_exp.getFieldValue(
						idno, "LC_RATE").toString())); // lc_rate
				ps_ins3.setString(12, (String) data_exp.getFieldValue(idno,
						"PUR_CREDIT_REFER")); // PUR_CREDIT_REFER
				ps_ins3.executeUpdate();
			}
			ps_ins3.close();
			if (varCurRate < 0 || varInvDiscAmt < 0) {
				throw new SQLException(
						"Negative value not allowed in currency rate or discount");
			}
			ps_ins1.setString(1, pcode);// periodcode
			ps_ins1.setString(2, varLocationCode);// location_code
			ps_ins1.setDouble(3, varInvNo);// invoice_no
			ps_ins1.setDouble(4, 11);// invoice_code
			ps_ins1.setDouble(5, l_invtype);// type
			ps_ins1.setDate(6, inv_dt);// invoice_date
			ps_ins1.setDouble(7, l_store);// stra
			ps_ins1.setString(8, "");// slsmn
			ps_ins1.setString(9, (String) txtRemarks.getValue());// memo
			ps_ins1.setString(10, varPurRef);// inv_ref
			ps_ins1.setString(11, (String) txtInvdRef_name.getValue());// inv_refnm
			ps_ins1.setDouble(12, varInvAmt);// inv_amt
			ps_ins1.setDouble(13, varInvDiscAmt);// disc_amt
			ps_ins1.setDouble(14, 0);// inv_cost
			ps_ins1.setDouble(15, l_flg);// flag
			ps_ins1.setDate(16, l_create_dt);// createdt
			ps_ins1.setString(17, "");// lpno
			ps_ins1.setString(18, "");// bkno
			ps_ins1.setBigDecimal(19, BigDecimal.valueOf(varPurKeyfld));// keyfld
			ps_ins1.setString(20, utils.CPUSER);// username
			ps_ins1.setString(21, (String) txtSuppInvNo.getValue());// supinvno
			ps_ins1.setString(22, (String) txtShipCo.getValue());// shipco
			ps_ins1.setString(23, (String) txtInsCo.getValue());// in_co
			ps_ins1.setString(24, (String) txtBankName.getValue());// bank
			ps_ins1.setString(25, (String) txtLCNo.getValue());// lcno
			ps_ins1.setString(26, (String) txtInsCo.getValue());// ins_no
			ps_ins1.setDouble(27, varCurRate);// rate
			ps_ins1.setString(28, (String) txtCurrency.getValue());// currency
			ps_ins1.setDouble(29, varKdCost);// kdcost
			ps_ins1.setDouble(30, varOtherExpense);// chg_kdamt
			ps_ins1.setDouble(31, varOrdNo);// orderno
			ps_ins1.setString(32, varCustCode);// c_cusno
			ps_ins1.setDouble(33, varNoOfSrv);// RECIEVED NO
			ps_ins1.setBigDecimal(34, BigDecimal.valueOf(varPurKeyfld));// purchae
			// keyfld
			ps_ins1.setBigDecimal(35, BigDecimal.valueOf(varPurKeyfld));// purchae
			// keyfld
			ps_ins1.setDouble(36, varOrdNo);// order no
			ps_ins1.setBigDecimal(37, BigDecimal.valueOf(varPurKeyfld));// purchae
			// keyfl
			ps_ins1.setDouble(38, varOrdNo);// order no
			ps_ins1.executeUpdate();
			if (optClose.getValue() == OPTION_CLOSE_JV) {
				PreparedStatement ps_close = con.prepareStatement(sql4);
				ps_close.setDouble(1, varTotAdjustAmt); // odjust_amt
				ps_close.setString(2, (String) txtCurrency.getValue()); // currency
				ps_close.setDouble(3, varCurRate); // rate
				ps_close.setDate(4, inv_dt); // date
				ps_close.setString(5, ""); // descr
				ps_close.setDouble(6, varOrdNo); // ord_no
				ps_close.setDouble(7, varOrdNo); // ord_no
				ps_close.setString(8, "Adjustment Order # " + varOrdNo
						+ " Invoice :" + varInvNo); // str1
				ps_close.executeUpdate();
				ps_close.close();
			}
			if (optClose.getValue() == OPTION_CLOSE_NO_JV) {
				PreparedStatement ps_close = con
						.prepareStatement("update order1 set ord_flag=1 where ord_code=103 and ord_no="
								+ varOrdNo);
				ps_close.executeUpdate();
				ps_close.close();
			}
			ps_ins1.close();
			con.commit();
			if (doPrint) {
				if (QRYSES.length() == 0) {
					QRYSES = String.valueOf(varPurKeyfld);
				}
				print_data();
			}
			if (backToList) {
				resetFormLayout();
				showPurchaseCostView();
			}
		} catch (Exception ex) {
			Logger.getLogger(frmPurchaseCost.class.getName()).log(Level.SEVERE,
					null, ex);
			parentLayout.getWindow().showNotification("Unable to save:",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();

			} catch (SQLException e) {
			}
		}

	}

	public void save_data() {
		save_data(true, false);

	}

	public void showInitView() {
		showPurchaseCostView();
	}

}
