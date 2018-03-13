package com.windows.LG;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog;
import com.doc.views.YesNoDialog.Callback;
import com.example.components.SearchField;
import com.example.components.SearchField.ButtonPress;
import com.generic.ColumnProperty;
import com.generic.ColumnProperty.afterModelUpdated;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.FormLayoutManager;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.localTableModel.DefaultValueListner;
import com.generic.qryColumn;
import com.generic.rowTriggerListner;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.utilsVaadinPrintHandler;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class frmPays implements transactionalForm {

	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	private Panel mainPanel = new Panel(new HorizontalLayout());
	private Panel tbSheetPanel = new Panel(new VerticalLayout());
	private Panel pnlList = new Panel(new VerticalLayout());
	private Panel pnlRcpt1 = new Panel(new VerticalLayout());
	private TabSheet tbs = new TabSheet();
	private VerticalLayout layoutRcpt = new VerticalLayout();
	private VerticalLayout layoutRcpt3 = new VerticalLayout();

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private Panel pnlPayBatch = new Panel(new FormLayoutManager());
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout dutyTimeLayout = new VerticalLayout();

	private HorizontalLayout basicLayout = new HorizontalLayout();

	private FormLayoutManager basicInfoLayout = new FormLayoutManager("100%",
			"-1px");

	private Panel infoPanel = new Panel(new FormLayoutManager("100%", "-1px"));

	private TableLayoutVaadin tbl_rcpt1 = new TableLayoutVaadin(
			(AbstractOrderedLayout) pnlRcpt1.getContent());

	private TableLayoutVaadin tbl_rcpt2 = new TableLayoutVaadin(layoutRcpt);
	private TableLayoutVaadin tbl_rcpt3 = new TableLayoutVaadin(layoutRcpt3);

	// ------------------------------------------------------------------------------------------------------------------------------------
	// variables
	// ------------------------------------------------------------------------------------------------------------------------------------
	private TableView varSelTV = null;
	private List<FieldInfo> lstfldPays = new ArrayList<FieldInfo>();
	private List<FieldInfo> lstfldRefunds = new ArrayList<FieldInfo>();
	private List<FieldInfo> lstfldClose = new ArrayList<FieldInfo>();
	private List<FieldInfo> lstfldInfo = new ArrayList<FieldInfo>();
	private List<FieldInfo> lstfldPO = new ArrayList<FieldInfo>();

	private List<ColumnProperty> lstRcpts = new ArrayList<>();

	public List<String> listTargetMode = new ArrayList<String>();

	private java.sql.Connection con = null;

	private double varSelectedPay = -1;
	private String varRfndDrAc = "";
	private String varRfndCrAc = "";
	public String QRYSES = "";
	public String QRYSES_REFUND = "";
	public String QRYSES_ON = "";
	public String QRYSES_ON_ORD_REF = "";
	public String QRYSES_ON_ORD_REFNM = "";

	private String varPrintQRYSES = "";
	private String varPrintQRYSES_ON = "";
	private String varPrintQRYSES_REFUND = "";
	private boolean varErrorOnSave = false;

	public final dataCell OPTION_REFUND = new dataCell("Refund", "1");
	public final dataCell OPTION_REPAID = new dataCell("Re-Paid", "2");

	public final String MODE_MAIN_SCREEN = "@";
	public final String MODE_PAYMENTS = "PAYMENTS";
	public final String MODE_PAY_REFUNDS = "REFUNDS";
	public final String MODE_PAY_RECEIPTS = "RECEIPTS";
	public final String MODE_CLOSE_PAYMMENT = "CLOSE_PAY";
	public final String MODE_PO_PAYMMENT = "SALE_PAY";

	public final String MODE_SEL_PAYS = "SEL_PAYS";
	public final String MODE_SEL_PAYS_FUNDS = "SEL_PAYS_FUNDS";
	public final String MODE_SEL_PO = "SEL_SALE";

	public final String MODE_FORM_PAYMENTS = "FORM_PAYMENTS";
	public final String MODE_FORM_REFUNDS = "FORM_REFUNDS";
	public final String MODE_FORM_RECIPTS1 = "FORM_RECIPTS1";
	public final String MODE_FORM_RECIPTS2 = "FORM_RECIPTS2";
	public final String MODE_FORM_CLOSE = "CLOSE";
	public final String MODE_FORM_PO = "FORM_SALE";

	public String mode_current = "@";
	// ------------------------------------------------------------------------------------------------------------------------------------
	// for info on main panel
	// ------------------------------------------------------------------------------------------------------------------------------------

	private TextField txtTotPay = new TextField();
	private TextField txtTotRefund = new TextField();
	private TextField txtTotDue = new TextField();
	private TextField txtTotRepay = new TextField();
	private TextField txtTotClosed = new TextField();

	private TextField txtTotRcptAmt = new TextField();
	private TextField txtTotRcptDue = new TextField();
	private TextField txtTotRcptTot = new TextField();

	private TextField txtInfoInitPay = new TextField();
	private TextField txtInfoUPay = new TextField();
	private TextField txtInfoURefund = new TextField();
	private TextField txtInfoURate = new TextField();
	private TextField txtInfoBalance = new TextField();
	private TextField txtInfoSaleable = new TextField();
	private TextField txtInfoDueBal = new TextField();

	private double varInfoInitPay = 0;
	private double varInfoUpay = 0;
	private double varInfoURefund = 0;
	private double varInfoBal = 0;
	private double varInfoSaleBal = 0;
	private double varInfoRate = 1;

	// ------------------------------------------------------------------------------------------------------------------------------------
	// command buttons
	// ------------------------------------------------------------------------------------------------------------------------------------

	private NativeButton cmdPrint = ControlsFactory.CreateCustomButton("Print",
			"img/print.png", "Print", "", new ClickListener() {

				public void buttonClick(ClickEvent event) {
					do_print(false, false);
				}
			});

	private NativeButton cmdPayments = ControlsFactory.CreateCustomButton(
			"Issue document", "img/radio_button.png", "Payments",
			"link title3", new ClickListener() {

				public void buttonClick(ClickEvent event) {
					listTargetMode.clear();
					QRYSES = "";
					varSelectedPay = -1;
					listTargetMode.add(MODE_PAYMENTS);
					listTargetMode.add(MODE_SEL_PAYS);
					listTargetMode.add(MODE_FORM_PAYMENTS);
					// listTargetMode.add(MODE_FORM_RECIPTS1);
					setCurrentMode(MODE_SEL_PAYS);
				}
			});

	private NativeButton cmdRecipts = ControlsFactory.CreateCustomButton(
			"Issue document", "img/radio_button.png", "Assign Recipts",
			"link title3", new ClickListener() {

				public void buttonClick(ClickEvent event) {
					listTargetMode.clear();
					QRYSES = "";
					varSelectedPay = -1;
					listTargetMode.add(MODE_PAY_RECEIPTS);
					listTargetMode.add(MODE_SEL_PAYS);
					listTargetMode.add(MODE_FORM_RECIPTS1);
					setCurrentMode(MODE_SEL_PAYS);
				}
			});

	private NativeButton cmdClose = ControlsFactory.CreateCustomButton(
			"Issue document", "img/radio_button.png",
			"Closing Payments & Generate Sales", "link title3",
			new ClickListener() {
				public void buttonClick(ClickEvent event) {
					listTargetMode.clear();
					QRYSES = "";
					varSelectedPay = -1;
					listTargetMode.add(MODE_CLOSE_PAYMMENT);
					listTargetMode.add(MODE_SEL_PAYS);
					listTargetMode.add(MODE_FORM_CLOSE);
					setCurrentMode(MODE_SEL_PAYS);
				}
			});
	private NativeButton cmdRefunds = ControlsFactory.CreateCustomButton(
			"Issue document", "img/radio_button.png", "Refunds", "link title3",
			new ClickListener() {
				public void buttonClick(ClickEvent event) {
					listTargetMode.clear();
					QRYSES = "";
					varSelectedPay = -1;
					listTargetMode.add(MODE_PAY_REFUNDS);
					listTargetMode.add(MODE_SEL_PAYS);
					listTargetMode.add(MODE_SEL_PAYS_FUNDS);
					listTargetMode.add(MODE_FORM_REFUNDS);
					setCurrentMode(MODE_SEL_PAYS);
				}
			});

	private NativeButton cmdGenPurchase = ControlsFactory.CreateCustomButton(
			"Sales", "img/radio_button.png", "Sales", "link title3",
			new ClickListener() {
				public void buttonClick(ClickEvent event) {
					listTargetMode.clear();
					listTargetMode.add(MODE_PO_PAYMMENT);
					listTargetMode.add(MODE_SEL_PO);
					listTargetMode.add(MODE_FORM_PO);
					QRYSES = "";
					setCurrentMode(MODE_SEL_PO);
				}
			});

	private NativeButton cmdCancel = ControlsFactory.CreateCustomButton(
			"Cancel", "img/cancel.png", "cancel and take it to Main screen",
			"", new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					listTargetMode.clear();
					mode_current = MODE_MAIN_SCREEN;
					createView_main_screen();
				}
			});

	private NativeButton cmdFinish = ControlsFactory.CreateCustomButton(
			"Finish", "img/ok.png", "Finish", "", new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (listTargetMode.size() <= 0)
						return;
					mode_current = listTargetMode.get(listTargetMode.size() - 1);
					save_data();
				}
			});

	private NativeButton cmdBack = ControlsFactory.CreateCustomButton("Back",
			"img/back.png", "back step", "", new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					setPriorMode();
				}
			});

	private NativeButton cmdNext = ControlsFactory.CreateCustomButton("Next",
			"img/forward.png", "Next step", "", new ClickListener() {

				public void buttonClick(ClickEvent event) {
					setNextMode();
				}
			});

	private NativeButton cmdRefundRepaidDel = ControlsFactory
			.CreateCustomButton("Delete this Refund ", "",
					"Delete this Refund.", "link", new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {

							if (QRYSES.isEmpty() || QRYSES_REFUND.isEmpty())
								return;

							Callback cb = new Callback() {
								@Override
								public void onDialogResult(boolean resultIsYes) {
									if (resultIsYes) {
										try {
											double varNewk = utilsVaadin
													.getFieldInfoDoubleValue(
															txtRfndTrnNo,
															lstfldRefunds);

											con.setAutoCommit(false);
											QueryExe.execute(
													"begin  X_LG_POST_PAY_REFUND("
															+ QRYSES
															+ ","
															+ QRYSES_REFUND
															+ ",TRUE); delete from LG_PAYS_REFUNDS where keyfld_pay='"
															+ QRYSES
															+ "' and trn_no='"
															+ QRYSES_REFUND
															+ "'; end;", con);
											con.commit();
											parentLayout
													.getWindow()
													.showNotification("",
															"Deleted successfully...");
											cmdCancel.click();
										} catch (Exception e) {

											try {
												con.rollback();
											} catch (SQLException e1) {
											}

											parentLayout
													.getWindow()
													.showNotification(
															"",
															e.getMessage(),
															Notification.TYPE_ERROR_MESSAGE);
											e.printStackTrace();

										}

									}
								}
							};

							Channelplus3Application
									.getInstance()
									.getMainWindow()
									.addWindow(
											new YesNoDialog("Confirmation",
													"Do you want to remove this Refund/Repaid # "
															+ QRYSES_REFUND
															+ " ?", cb,
													"250px", "-1px"));
						}

					});

	private NativeButton cmdPayDel = ControlsFactory.CreateCustomButton(
			"Delete this Payment ", "", "Delete this Payment", "link",
			new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (QRYSES.isEmpty())
						return;
					if (!QRYSES.isEmpty()
							&& !utilsVaadin.canDeleteTrans("LGPAYS", con)) {
						utilsVaadin.showMessage("Deleting Denied !");
						return;
					}

					Callback cb = new Callback() {

						@Override
						public void onDialogResult(boolean resultIsYes) {
							if (resultIsYes) {
								try {
									int nf = Integer.valueOf(QueryExe
											.getSqlValue(
													"select nvl(count(*),0) from "
															+ " LG_PAYS_RCPT where keyfld_pay='"
															+ QRYSES + "'",
													con, "0")
											+ "");
									if (nf > 0) {
										throw new Exception(
												"Refund / Repaid existed for this payment !");
									}
									con.setAutoCommit(false);
									QueryExe.execute(
											"begin  X_LG_POST_PAY("
													+ QRYSES
													+ ",TRUE); delete from lg_pays where keyfld='"
													+ QRYSES + "'; end;", con);

									con.commit();
									cmdCancel.click();
								} catch (Exception e) {

									try {
										con.rollback();
									} catch (SQLException e1) {
									}

									parentLayout.getWindow().showNotification(
											"", e.getMessage(),
											Notification.TYPE_ERROR_MESSAGE);
									e.printStackTrace();

								}

							}
						}
					};
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.addWindow(
									new YesNoDialog("Confirmation",
											"Do you want to remove this payment # "
													+ QRYSES + " ?", cb,
											"250px", "-1px"));
				}
			});

	// ------------------------------------------------------------------------------------------------------------------------------------
	// CLOSING INFO
	// ------------------------------------------------------------------------------------------------------------------------------------
	private TextField txtInfoKeyfld = ControlsFactory.CreateTextField(null,
			"keyfld", lstfldInfo, "0", Parameter.DATA_TYPE_NUMBER,
			Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_QTY,
			false);

	private DateField txtInfoDate = ControlsFactory.CreateDateField(null,
			"DATE_JV", lstfldInfo, "100%", utils.FORMAT_SHORT_DATE, null);

	private TextField txtInfoDescr = ControlsFactory.CreateTextField(null,
			"DESCR", lstfldInfo, "100%", "");

	// ------------------------------------------------------------------------------------------------------------------------------------
	private ComboBox txtLocation = ControlsFactory.CreateListField(null,
			"LOCATION_CODE", "SELECT CODE,NAME FROM LOCATIONS ORDER BY CODE",
			lstfldPays, "100%", "");
	private TextField txtPayChequeNo = ControlsFactory.CreateTextField(null,
			"NO_CHEQUE", lstfldPays, "100%", "");

	private DateField txtPayChequeDate = ControlsFactory.CreateDateField(null,
			"DATE_CHEQUE", lstfldPays, "100%", utils.FORMAT_SHORT_DATE, null);

	private SearchField txtPayEmpNo = ControlsFactory
			.CreateSearchField(null, "NO_EMP", lstfldPays, "100%",
					"select no,name from salesp where flag=1 order by no",
					"NO", "NAME");

	private SearchField txtPayEmpDrAc = ControlsFactory
			.CreateSearchField(
					null,
					"AC_EMP_DR",
					lstfldPays,
					"100%",
					"select code,name from c_ycust where flag=1 and childcount=0 order by path",
					"CODE", "NAME");
	private SearchField txtPayMainCrAc = ControlsFactory
			.CreateSearchField(
					null,
					"AC_MAIN_CR",
					lstfldPays,
					"100%",
					"select code,name from c_ycust where flag=1 and childcount=0 order by path",
					"CODE", "NAME");

	private TextField txtPayEstRefundAmt = ControlsFactory.CreateTextField(
			null, "AMT_REFUND_ESTIMATE", lstfldPays, "0",
			Parameter.DATA_TYPE_NUMBER, Channelplus3Application.getInstance()
					.getFrmUserLogin().FORMAT_MONEY, false);
	private DateField txtPayExpiryDate = ControlsFactory.CreateDateField(null,
			"DATE_EXPIRY", lstfldPays, "100%", utils.FORMAT_SHORT_DATE, null);

	// ------------------------------------------------------------------------------------------------------------------------------------
	// refund fields
	// ------------------------------------------------------------------------------------------------------------------------------------
	private OptionGroup optType = new OptionGroup(null);
	private TextField txtRfndTrnNo = ControlsFactory
			.CreateTextField(null, "TRN_NO", lstfldRefunds, "0",
					Parameter.DATA_TYPE_STRING, "", false);

	private DateField txtRfndDate = ControlsFactory.CreateDateField(null,
			"TRN_DATE", lstfldRefunds, "100%", utils.FORMAT_SHORT_DATE, null);

	private TextField txtRfndDescr = ControlsFactory.CreateTextField(null,
			"DESCR", lstfldRefunds, "100%", "");

	private SearchField txtRfndRcpt = ControlsFactory.CreateSearchField(null,
			"RCPT_NO", lstfldRefunds, "100%",
			"SELECT RCPT_NO,DOC_NO,DESCR,DATE_ISSUE FROM LG_PAYS_RCPT WHERE KEYFLD_PAY='"
					+ QRYSES + "' AND FLAG=1 ORDER BY RCPT_NO", "RCPT_NO",
			"DESCR");
	private TextField txtRfndAmt = ControlsFactory
			.CreateTextField(null, "AMT_FC", lstfldRefunds, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);

	private TextField txtRfndRate = ControlsFactory.CreateTextField(null,
			"RATE", lstfldRefunds, "1", Parameter.DATA_TYPE_NUMBER, "", false);
	private TextField txtRfndCurrency = ControlsFactory.CreateTextField(null,
			"CURRENCY", lstfldRefunds, "USD", Parameter.DATA_TYPE_STRING, "",
			false);

	private TextField txtRfndAmtLC = ControlsFactory
			.CreateTextField(null, "AMT", lstfldRefunds, "0",
					Parameter.DATA_TYPE_STRING, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY, true);

	private TextField txtRfndDrAc = ControlsFactory.CreateTextField(null, "",
			lstfldRefunds, "100%", "");

	private TextField txtRfndCrAc = ControlsFactory.CreateTextField(null, "",
			lstfldRefunds, "100%", "");
	private TextField txtTotAmt = ControlsFactory
			.CreateTextField(null, "", lstfldRefunds, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);
	private TextField txtRfndTotPay = ControlsFactory
			.CreateTextField(null, "", lstfldRefunds, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);
	private TextField txtRfndTotRefunded = ControlsFactory
			.CreateTextField(null, "", lstfldRefunds, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);
	private TextField txtRfndTotDue = ControlsFactory
			.CreateTextField(null, "", lstfldRefunds, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);

	private TextField txtRfndAttachcptNo = ControlsFactory.CreateTextField(
			null, "ATTACHED_RCPT_NO", lstfldRefunds, "100%", "");

	private SearchField txtRfndItemCode = ControlsFactory
			.CreateSearchField(
					null,
					"ATTACHED_ITEM_REFER",
					lstfldRefunds,
					"100%",
					"select REFERENCE,DESCR FROM ITEMS WHERE CHILDCOUNTS=0 AND FLAG=1 ORDER BY DESCR2",
					"REFERENCE", "DESCR");

	// ------------------------------------------------------------------------------------------------------------------------------------
	// PAYMENTS FORM
	// ------------------------------------------------------------------------------------------------------------------------------------

	private TextField txtPayKeyfld = ControlsFactory.CreateTextField(null,
			"keyfld", lstfldPays, "0", Parameter.DATA_TYPE_NUMBER, "", false);

	private DateField txtPayDate = ControlsFactory.CreateDateField(null,
			"DATE_JV", lstfldPays, "100%", utils.FORMAT_SHORT_DATE, null);

	private TextField txtPayDescr = ControlsFactory.CreateTextField(null,
			"DESCR", lstfldPays, "100%", "");

	private TextField txtPayBankTitle = ControlsFactory.CreateTextField(null,
			"BANK_TITLE", lstfldPays, "100%", "");

	private TextField txtPayAmt = ControlsFactory
			.CreateTextField(null, "AMT_JV_FC", lstfldPays, "0",
					Parameter.DATA_TYPE_NUMBER, "", false);
	private TextField txtPayRate = ControlsFactory.CreateTextField(null,
			"RATE", lstfldPays, "1", Parameter.DATA_TYPE_NUMBER, "", false);
	private TextField txtPayCurrency = ControlsFactory.CreateTextField(null,
			"CURRENCY", lstfldPays, "USD", Parameter.DATA_TYPE_STRING, "",
			false);

	private TextField txtPayAmtLC = ControlsFactory
			.CreateTextField(null, "AMT_JV", lstfldPays, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY, true);

	private CheckBox chkPrint = ControlsFactory.CreateCheckField(null, "", "N",
			"Y", null, "", Boolean.TRUE);

	// ------------------------------------------------------------------------------------------------------------------------------------
	// PO FORM
	// ------------------------------------------------------------------------------------------------------------------------------------
	private TextField txtPONo = ControlsFactory.CreateTextField(null, "ORD_NO",
			lstfldPO, "0", Parameter.DATA_TYPE_STRING, "", false);

	private DateField txtPODate = ControlsFactory.CreateDateField(null,
			"ORD_DATE", lstfldPO, "100%", utils.FORMAT_SHORT_DATE, null);

	private TextField txtPODescr = ControlsFactory.CreateTextField(null,
			"DESCR", lstfldPO, "100%", "");
	private TextField txtPurAmt = ControlsFactory
			.CreateTextField(null, "TOT_AMT", lstfldPO, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);
	private TextField txtPurVarAmt = ControlsFactory
			.CreateTextField(null, "TOT_VAR_AMT", lstfldPO, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);
	private TextField txtSaleNet = ControlsFactory
			.CreateTextField(null, "TOT_VAR_NET", lstfldPO, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);

	private TextField txtPurRate = ControlsFactory.CreateTextField(null,
			"RATE", lstfldPO, "1", Parameter.DATA_TYPE_NUMBER, "", false);
	private TextField txtPurCurrency = ControlsFactory.CreateTextField(null,
			"CURRENCY", lstfldPO, "USD", Parameter.DATA_TYPE_STRING, "", false);

	private TextField txtInv = ControlsFactory.CreateTextField(null, "ATTN",
			lstfldPO);
	private SearchField txtPOSupplier = ControlsFactory
			.CreateSearchField(
					null,
					"ORD_REF",
					lstfldPO,
					"100%",
					"select code,name from c_ycust where flag=1 and childcount=0 and issupp='Y' order by path",
					"CODE", "NAME");

	// ------------------------------------------------------------------------------------------------------------------------------------

	private void setNextMode() {
		if (listTargetMode.size() == 0)
			return;
		int n = listTargetMode.indexOf(mode_current);

		try {
			setCurrentMode(listTargetMode.get(n + 1));
		} catch (Exception ex) {
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			ex.printStackTrace();
		}
	}

	protected void do_print(final boolean before_print_confirm,
			final boolean save_and_print) {
		if (before_print_confirm) {
			Callback cb = new Callback() {

				@Override
				public void onDialogResult(boolean resultIsYes) {
					print_data();
				}
			};

			Channelplus3Application
					.getInstance()
					.getMainWindow()
					.addWindow(
							new YesNoDialog("Confirmation",
									"Do you want to remove order no # "
											+ QRYSES + " ?", cb, "250px",
									"-1px"));
		}
	}

	public void setCurrentMode(String mode) {
		String old_mode = mode_current;
		try {
			mode_current = mode;
			createView();
			load_data();
		} catch (Exception ex) {
			ex.printStackTrace();
			mode_current = old_mode;
		}
	}

	private void setPriorMode() {
		int n = listTargetMode.indexOf(mode_current);
		if (n == 0)
			return;
		setCurrentMode(listTargetMode.get(n - 1));
	}

	public void createView() {
		switch (mode_current) {
		case MODE_MAIN_SCREEN:
			createView_main_screen();
			break;
		case MODE_SEL_PAYS:
			show_payments_sel();
			break;
		case MODE_SEL_PAYS_FUNDS:
			show_refund_sel();
			break;
		case MODE_FORM_PAYMENTS:
			show_payments();
			break;
		case MODE_FORM_REFUNDS:
			show_refunds();
			break;
		case MODE_FORM_RECIPTS1:
			show_rcpts1();
			break;
		case MODE_FORM_CLOSE:
			show_close_form();
			break;
		case MODE_SEL_PO:
			show_PO_sel();
			break;

		case MODE_FORM_PO:
			show_PO_form();
			break;

		default:
			break;
		}
	}

	private void show_PO_sel() {
		resetLayout();
		tbs.addTab(pnlList, "Select the Payments for PO ..");
		tbs.setSelectedTab(pnlList);
		cmdFinish.setEnabled(false);
		cmdBack.setEnabled(false);
		cmdNext.setEnabled(true);
		cmdCancel.setEnabled(true);
		QRYSES = "";

		try {
			SelectionListener sel = new SelectionListener() {
				@Override
				public void onSelection(TableView tv) {
					varSelectedPay = -1;
					QRYSES = "";
					for (int i = 0; i < tv.getLstCheckSelection().size(); i++) {
						if (tv.getLstCheckSelection().get(i).booleanValue()) {
							QRYSES = QRYSES + (QRYSES.isEmpty() ? "" : ",")
									+ tv.getData().getFieldValue(i, "KEYFLD");
						}
					}
				}
			};

			varSelTV = utilsVaadin
					.showSearchWithSelection(
							(VerticalLayout) pnlList.getContent(),
							sel,
							con,
							"select P.KEYFLD,P.descr,to_char(P.DATE_JV,'dd/mm/rrrr') ISSUE_DATE,L.NAME LOCATIOIN_NAME"
									+ "  from LG_PAYS P, LOCATIONS L where P.LOCATION_CODE=L.CODE AND P.flag=2 AND P.JOB_ORD_NO='"
									+ QRYSES_ON + "' order by P.keyfld",
							new String[] {});

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void show_PO_form() {
		try {

			QRYSES = "";
			for (int i = 0; i < varSelTV.getLstCheckSelection().size(); i++) {
				if (varSelTV.getLstCheckSelection().get(i).booleanValue()) {
					QRYSES = QRYSES + (QRYSES.isEmpty() ? "" : ",")
							+ varSelTV.getData().getFieldValue(i, "KEYFLD");
				}
			}
			if (QRYSES.isEmpty()) {
				setPriorMode();
				return;
			}

			resetLayout();
			tbs.addTab(pnlPayBatch, "Generating Purchase Order");
			tbs.setSelectedTab(pnlPayBatch);

			cmdFinish.setEnabled(true);
			cmdBack.setEnabled(true);
			cmdNext.setEnabled(false);
			cmdCancel.setEnabled(true);

			FormLayoutManager f = (FormLayoutManager) pnlPayBatch.getContent();
			f.setMargin(true);
			f.removeAll();

			f.addComponentsRow(
					"caption=PO No #,width=100%,align=end,expand=.5", txtPONo,
					"expand=1.5,width=100%",
					"caption=PO Date #,align=end,width=100%,expand=.5",
					txtPODate, "expand=1.5,width=100%");
			f.addComponentsRow(
					"caption=Supplier #,width=100%,align=end,expand=.5",
					txtPOSupplier, "expand=3.5,width=100%,readonly=false");

			f.addComponentsRow(
					"caption=Descr #,width=100%,align=end,expand=.5",
					txtPODescr, "expand=1.5,width=100%",
					"caption=Inv Ref#,align=end,width=100%,expand=.5", txtInv,
					"expand=1.5,width=100%");

			f.addComponentsRow(layoutRcpt3);
			f.addComponentsRow("caption=.,width=100%,expand=2.5",
					"caption=Amount #,width=100%,align=end,expand=.5",
					txtPurAmt, "expand=1,width=100%");
			f.addComponentsRow(
					"caption=Curr.Rate,align=end,width=100%,expand=.5",
					txtPurRate, "expand=1.5,width=100%,align_text=end",
					"caption=Currency,align=end,width=100%,expand=.5",
					txtPurCurrency,
					"caption=Curr.Rate,align=end,width=100%,expand=1.5");

			utilsVaadin.applyColumns("LG_PAY_RCPT_3", tbl_rcpt3.getTable(),
					tbl_rcpt3.listFields, con);

			/*
			 * f.addComponentsRow("caption=.,width=100%,expand=2.5",
			 * "caption=Variance #,width=100%,align=end,expand=.5",
			 * txtPurVarAmt, "expand=1,width=100%");
			 * f.addComponentsRow("caption=.,width=100%,expand=2.5",
			 * "caption=Net Sale #,width=100%,align=end,expand=.5", txtSaleNet,
			 * "expand=1,width=100%");
			 */

			// utilsVaadin.findColByCol("NEW_AMT",
			// tbl_rcpt3.listFields).actionAfterUpdate = new
			// afterModelUpdated()
			// {
			//
			// @Override
			// public Object onValueChange(int rowno, String colname, Object
			// vl)
			// {
			// calc_summary_sale(rowno);
			// return vl;
			// }
			//

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void show_payments() {

		resetLayout();
		tbs.addTab(pnlPayBatch, "Payment");
		tbs.setSelectedTab(pnlPayBatch);

		cmdFinish.setEnabled(true);
		cmdBack.setEnabled(true);
		cmdNext.setEnabled(false);
		cmdCancel.setEnabled(true);

		FormLayoutManager f = (FormLayoutManager) pnlPayBatch.getContent();
		f.setMargin(true);
		f.removeAll();
		f.addComponentsRow("caption=Location #,width=100%,align=end,expand=.5",
				txtLocation, "expand=1.5,width=100%", "expand=2,width=100%");

		f.addComponentsRow("caption=Trn No #,width=100%,align=end,expand=.5",
				txtPayKeyfld, "expand=1.5,width=100%",
				"caption=Date #,align=end,width=100%,expand=.5", txtPayDate,
				"expand=1.5,width=100%");
		f.addComponentsRow("caption=Descr #,width=100%,align=end,expand=.5",
				txtPayDescr, "expand=3.5,width=100%");

		f.addComponentsRow(
				"caption=Cheque No #,width=100%,align=end,expand=.5",
				txtPayChequeNo, "expand=1.5,width=100%",
				"caption=Date #,align=end,width=100%,expand=.5",
				txtPayChequeDate, "expand=1.5,width=100%");

		f.addComponentsRow(
				"caption=Bank Title #,width=100%,align=end,expand=.5",
				txtPayBankTitle, "expand=3.5,width=100%");

		f.addComponentsRow(
				// "caption=Employee #,width=100%,align=end,expand=.5",
				// txtPayEmpNo, "expand=1.5,width=100%",
				"caption=Emp A/c,align=end,width=100%,expand=.5",
				txtPayEmpDrAc, "expand=3.5,width=100%");
		f.addComponentsRow(
				"caption=Main Cash #,width=100%,align=end,expand=.5",
				txtPayMainCrAc, "expand=3.5,width=100%");
		f.addComponentsRow("caption=.,width=100%,align=end,expand=2",
				"caption=Paid Amount FC,align=end,width=100%,expand=.7",
				txtPayAmt, "expand=1.3,width=100%,style=yellow");

		f.addComponentsRow("caption=.,width=100%,align=end,expand=2.3",
				"caption=Curr.Rate,align=end,width=100%,expand=.7", txtPayRate,
				"expand=1,width=100%,align_text=end");

		f.addComponentsRow("caption=.,width=100%,align=end,expand=2.3",
				"caption=Paid Amount,align=end,width=100%,expand=.7",
				txtPayCurrency, "expand=1,width=100%");
		f.addComponentsRow("caption=.,width=100%,align=end,expand=2.3",
				"caption=Paid Amount in LC,align=end,width=100%,expand=.7",
				txtPayAmtLC,
				"expand=1,width=100%,style=yellow,readonly=true,align_text=end");

		f.addLine();
		f.addComponentsRow("caption=Additional Predicted info");

		f.addComponentsRow("caption=.,width=100%,align=end,expand=1.5",
				"caption=Estimate Refund Amount,align=end,width=100%,expand=1",
				txtPayEstRefundAmt, "expand=1.5,width=100%");

		f.addComponentsRow("caption=.,width=100%,align=end,expand=1.5",
				"caption=Completion on,align=end,width=100%,expand=1",
				txtPayExpiryDate, "expand=1.5,width=100%");

		f.addComponentsRow(cmdPayDel, "width=150px");
		f.addComponentsRow(chkPrint, "caption=Print after save,widh=100%");
	}

	private void show_refunds() {
		resetLayout();
		tbs.addTab(pnlPayBatch, "Refund");
		tbs.setSelectedTab(pnlPayBatch);

		cmdFinish.setEnabled(true);
		cmdBack.setEnabled(true);
		cmdNext.setEnabled(false);
		cmdCancel.setEnabled(true);

		FormLayoutManager f = (FormLayoutManager) pnlPayBatch.getContent();
		f.setMargin(true);
		f.removeAll();

		optType.removeAllItems();
		optType.addItem(OPTION_REFUND);
		optType.addItem(OPTION_REPAID);
		optType.addStyleName("horizontalgrouo");
		optType.setValue(OPTION_REFUND);
		optType.setImmediate(true);

		f.addComponentsRow("caption=Trans Kind,width=70px,align=end,expand=.5",
				optType, "width=100%,expand=1.5",
				"caption=.,width=100%,expand=2");

		f.addComponentsRow("caption=Trn No #,width=100%,align=end,expand=.5",
				txtRfndTrnNo, "expand=1.5,width=100%",
				"caption=Date #,align=end,width=100%,expand=.5", txtRfndDate,
				"expand=1.5,width=100%");

		f.addComponentsRow("caption=Descr #,width=100%,align=end,expand=.5",
				txtRfndDescr, "expand=3.5,width=100%");

		f.addComponentsRow("caption=Rcpt # ,width=100%,align=end,expand=2",
				// txtRfndRcpt, "expand=1.5,width=100%",

				"caption=Amount,align=end,width=100%,expand=.5", txtRfndAmt,
				"expand=1.5,width=100%,style=yellow");

		f.addComponentsRow("caption=.,width=100%,align=end,expand=2.3",
				"caption=Curr.Rate,align=end,width=100%,expand=.7",
				txtRfndRate, "expand=1,width=100%,align_text=end,readonly=true");

		f.addComponentsRow("caption=.,width=100%,align=end,expand=2.3",
				"caption=Paid Amount,align=end,width=100%,expand=.7",
				txtRfndCurrency, "expand=1,width=100%,readonly=true");
		f.addComponentsRow("caption=.,width=100%,align=end,expand=2.3",
				"caption=Paid Amount in LC,align=end,width=100%,expand=.7",
				txtRfndAmtLC,
				"expand=1,width=100%,style=yellow,readonly=true,align_text=end");

		f.addLine();
		f.addComponentsRow("caption=Attached information");
		f.addComponentsRow(
				"caption=Attached Item,width=100%,align=end,expand=1",
				txtRfndItemCode, "expand=1.5,width=100%,style=yellow",
				"caption=.,align=end,width=100%,expand=1.5");
		f.addComponentsRow(
				"caption=Attached Recipt #,width=100%,align=end,expand=1",
				txtRfndAttachcptNo,
				"expand=1.5,width=100%,style=yellow,readonly=false",
				"caption=.,align=end,width=100%,expand=1.5");

		f.addLine();

		f.addComponentsRow("caption=Debit A/c,width=100%,align=end,expand=.5",
				txtRfndDrAc, "expand=1.5,width=100%,readonly=true",
				"caption=Tot Pay,align=end,width=100%,expand=.5",
				txtRfndTotPay, "expand=1.5,width=100%,readonly=true");
		f.addComponentsRow("caption=Creidt A/c,width=100%,align=end,expand=.5",
				txtRfndCrAc, "expand=1.5,width=100%,readonly=true",
				"caption=Refunded ,align=end,width=100%,expand=.5",
				txtRfndTotRefunded, "expand=1.5,width=100%,readonly=true");
		f.addComponentsRow("caption=.,width=100%,align=end,expand=2",
				"caption=Due,align=end,width=100%,expand=.5", txtRfndTotDue,
				"expand=1.5,width=100%,style=yellow,readonly=true");

		f.addComponentsRow(cmdRefundRepaidDel, "width=150px");

		f.addComponentsRow(chkPrint, "caption=Print after save,widh=100%");
	}

	private void show_close_form() {
		resetLayout();
		tbs.addTab(pnlPayBatch, "Closing form...");
		tbs.setSelectedTab(pnlPayBatch);

		cmdFinish.setEnabled(true);
		cmdBack.setEnabled(false);
		cmdNext.setEnabled(false);
		cmdCancel.setEnabled(true);

		FormLayoutManager f = (FormLayoutManager) pnlPayBatch.getContent();
		f.setMargin(true);
		f.removeAll();

		f.addComponentsRow("caption=Trn No #,width=100%,align=end,expand=.5",
				txtInfoKeyfld, "expand=1.5,width=100%,readonly=true",
				"caption=Date #,align=end,width=100%,expand=.5", txtInfoDate,
				"expand=1.5,width=100%,readonly=false");

		f.addComponentsRow("caption=Descr #,width=100%,align=end,expand=.5",
				txtInfoDescr, "expand=3.5,width=100%,readonly=false");

		f.addComponentsRow(layoutRcpt);

		f.addComponentsRow("caption=intial pay / Un allocated re-paid & refund,style=title1 ");
		f.addLine();

		f.addComponentsRow("caption=Init Pay,width=100%,align=end,expand=.5",
				txtInfoInitPay,
				"expand=1,width=100%,readonly=true,style=numberStyle",
				"caption=.,width=100%,expand=1",
				"caption=Balance,align=end,width=100%,expand=.5",
				txtInfoBalance,
				"expand=1,width=100%,readonly=true,style=numberStyle yellowField");

		f.addComponentsRow("caption=U.Re-Pay,width=100%,align=end,expand=.5",
				txtInfoUPay,
				"expand=1,width=100%,readonly=true,style=numberStyle",
				"caption=.,width=100%,expand=1",
				"caption=Saleable,align=end,width=100%,expand=.5",
				txtInfoSaleable,
				"expand=1,width=100%,readonly=true,style=numberStyle");

		f.addComponentsRow("caption=U.Refund,width=100%,align=end,expand=.5",
				txtInfoURefund,
				"expand=1,width=100%,readonly=true,style=numberStyle",
				"caption=.,width=100%,expand=1",
				"caption=Due Bal,align=end,width=100%,expand=.5",
				txtInfoDueBal,
				"expand=1,width=100%,readonly=true,style=numberStyle yellowField");

		f.addComponentsRow("caption=U.Refund,width=100%,align=end,expand=.5",
				txtInfoURate,
				"expand=1,width=100%,readonly=true,style=numberStyle",
				"caption=.,width=100%,expand=1",
				"caption=Due Bal,align=end,width=100%,expand=1.5");
		f.addComponentsRow("caption=.,width=100%,align=end,expand=3",
				new Label("(negative Due is re-pay)"), "expand=1,width=100%");

		try {
			utilsVaadin.applyColumns("LG_PAY_RCPT_2", tbl_rcpt2.getTable(),
					tbl_rcpt2.listFields, con);
			utilsVaadin.findColByCol("ITEM_REFER", tbl_rcpt2.listFields).actionAfterUpdate = new afterModelUpdated() {

				@Override
				public Object onValueChange(int rowno, String colname, Object vl) {
					QueryExe qei = new QueryExe(
							"select i.STOREACC,i.EXPACC, (SELECT MAX(NAME) FROM ACACCOUNT WHERE ACCNO=I.STOREACC) storeacc_name,"
									+ " (SELECT MAX(NAME) FROM ACACCOUNT WHERE ACCNO=I.EXPACC) expacc_name "
									+ " from items i "
									+ "  where i.reference= :REFER " + "   ",
							con);
					qei.setParaValue("refer", vl);
					ResultSet rst;
					try {
						rst = qei.executeRS();
						if (rst != null && rst.first()) {
							tbl_rcpt2.data.setFieldValue(rowno, "ac_store_dr",
									rst.getString("STOREACC"));
							tbl_rcpt2.data.setFieldValue(rowno,
									"ac_store_dr_name",
									rst.getString("STOREACC_NAME"));
							tbl_rcpt2.data.setFieldValue(rowno,
									"ac_expense_cr", rst.getString("EXPACC"));
							tbl_rcpt2.data.setFieldValue(rowno,
									"ac_expense_cr_name",
									rst.getString("EXPACC_NAME"));

						}
						qei.close();
					} catch (SQLException e) {

						e.printStackTrace();
					}

					return vl;
				}
			};
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// utilsVaadin.findColByCol("AMT_ACTUAL",
		// tbl_rcpt2.listFields).actionAfterUpdate = new afterModelUpdated() {
		//
		// @Override
		// public Object onValueChange(int rowno, String colname, Object vl) {
		// calc_summary(rowno);
		// return null;
		// }
		// };

	}

	private void show_payments_sel() {

		resetLayout();
		tbs.addTab(pnlList, "Select the Payment ..");
		tbs.setSelectedTab(pnlList);

		cmdFinish.setEnabled(false);
		cmdBack.setEnabled(false);
		cmdNext.setEnabled(true);
		cmdCancel.setEnabled(true);
		QRYSES = "";

		if (!listTargetMode.get(0).equals(MODE_PAYMENTS))
			cmdNext.setEnabled(false);

		try {

			int cnt = Integer.valueOf(QueryExe.getSqlValue(
					"select count(*) from LG_PAYS where flag=1 AND JOB_ORD_NO='"
							+ QRYSES_ON + "'", con, "0").toString());
			if (cnt == 0 && listTargetMode.get(0).equals(MODE_PAYMENTS)) {
				setNextMode();
				return;
			}

			SelectionListener sel = new SelectionListener() {
				@Override
				public void onSelection(TableView tv) {
					varSelectedPay = -1;
					QRYSES = "";

					if (tv.getTable().getValue() == null
							|| tv.getSelectionValue() < 0)
						return;
					int ln = tv.getSelectionValue();
					varSelectedPay = ((Number) tv.getData().getFieldValue(ln,
							"KEYFLD")).doubleValue();
					QRYSES = tv.getData().getFieldValue(ln, "KEYFLD")

					.toString();
					if (!listTargetMode.get(0).equals(MODE_PAYMENTS))
						cmdNext.setEnabled(true);
				}
			};

			utilsVaadin
					.showSearch(
							(VerticalLayout) pnlList.getContent(),
							sel,
							con,
							"select P.KEYFLD,P.descr,to_char(P.DATE_JV,'dd/mm/rrrr') ISSUE_DATE ,L.NAME LOCATION_NAME "
									+ "  from LG_PAYS P, LOCATIONS L where P.LOCATION_CODE=L.CODE AND "
									+ " P.flag=1 AND P.JOB_ORD_NO='"
									+ QRYSES_ON + "' order by P.keyfld", true);

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void show_refund_sel() {
		resetLayout();
		tbs.addTab(pnlList, "Select the Refund ..");
		tbs.setSelectedTab(pnlList);

		cmdFinish.setEnabled(false);
		cmdBack.setEnabled(true);
		cmdNext.setEnabled(true);
		cmdCancel.setEnabled(true);

		QRYSES_REFUND = "";

		try {
			int cnt = Integer.valueOf(QueryExe.getSqlValue(
					"select count(*) from LG_PAYS_REFUNDS where JOB_ORD_NO='"
							+ QRYSES_ON + "' and keyfld_pay='" + QRYSES + "'",
					con, "0").toString());
			if (cnt == 0) {
				setNextMode();
				return;
			}

			SelectionListener sel = new SelectionListener() {
				@Override
				public void onSelection(TableView tv) {
					QRYSES_REFUND = "";

					if (tv.getTable().getValue() == null
							|| tv.getSelectionValue() < 0)
						return;
					int ln = tv.getSelectionValue();
					QRYSES_REFUND = tv.getData().getFieldValue(ln, "TRN_NO")
							.toString();
				}
			};

			utilsVaadin
					.showSearch(
							(VerticalLayout) pnlList.getContent(),
							sel,
							con,
							"select TRN_NO,to_char(TRN_DATE,'dd/mm/rrrr') trn_date,DESCR,AMT from lg_pays_refunds where JOB_ORD_NO='"
									+ QRYSES_ON
									+ "' and keyfld_pay='"
									+ QRYSES
									+ "' order by trn_no", true);

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void show_rcpts1() {
		resetLayout();
		tbs.addTab(pnlRcpt1, "Select Payment for recipts  ..");
		tbs.setSelectedTab(pnlRcpt1);

		cmdFinish.setEnabled(true);
		cmdBack.setEnabled(true);
		cmdNext.setEnabled(false);
		cmdCancel.setEnabled(true);

		try {
			utilsVaadin.applyColumns("LG_PAY_RCPT_1", tbl_rcpt1.getTable(),
					tbl_rcpt1.listFields, con);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createView_main_screen() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetLayout();
		centralPanel.setSizeFull();
		mainPanel.setWidth("700px");
		mainPanel.setHeight("600px");
		mainPanel.setContent(mainLayout);
		pnlPayBatch.setSizeFull();

		tbSheetPanel.setContent(basicLayout);

		tbSheetPanel.setSizeFull();
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);
		((VerticalLayout) pnlRcpt1.getContent()).setMargin(true);

		layoutRcpt.setWidth("100%");
		layoutRcpt.setHeight("225px");
		layoutRcpt3.setWidth("100%");
		layoutRcpt3.setHeight("225px");
		tbl_rcpt3.getTable().setHeight("200px");

		basicInfoLayout.setHeight("-1px");
		basicInfoLayout.setWidth("100%");

		mainPanel.addStyleName("formLayout");
		tbSheetPanel.addStyleName("formLayout");
		pnlPayBatch.addStyleName("formLayout");
		pnlRcpt1.addStyleName("formLayout");
		pnlRcpt1.getContent().addStyleName("formLayout");

		((FormLayoutManager) pnlPayBatch.getContent())
				.addStyleName("formLayout");

		tbs.addTab(tbSheetPanel, "Main Screen");
		tbs.setSizeFull();

		tbl_rcpt2.getTable().setFooterVisible(true);

		mainLayout.addStyleName("formLayout");
		basicInfoLayout.addStyleName("formLayout");
		basicLayout.addStyleName("formLayout");
		tbSheetPanel.addStyleName("formLayout");

		pnlPayBatch.setSizeFull();
		pnlList.setSizeFull();
		basicLayout.setSizeFull();
		infoPanel.setSizeFull();

		txtLocation.setImmediate(true);
		txtPayEmpNo.setImmediate(true);

		((FormLayoutManager) pnlPayBatch.getContent()).setSpacing(true);

		ResourceManager.addComponent(centralPanel, mainPanel);
		ResourceManager.addComponent(mainLayout, tbs);
		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(basicLayout, basicInfoLayout);
		ResourceManager.addComponent(basicLayout, infoPanel);
		ResourceManager.addComponent(buttonLayout, cmdCancel);
		ResourceManager.addComponent(buttonLayout, cmdFinish);
		ResourceManager.addComponent(buttonLayout, cmdNext);
		ResourceManager.addComponent(buttonLayout, cmdBack);

		basicLayout.setExpandRatio(basicInfoLayout, 2.7f);
		basicLayout.setExpandRatio(infoPanel, 1.3f);

		mainLayout.setExpandRatio(buttonLayout, .3f);
		mainLayout.setExpandRatio(tbs, 3.7f);

		basicInfoLayout.setMargin(true);
		basicInfoLayout.setSpacing(true);

		DecimalFormat dcf = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);

		// opened recipt assinged display on menu ..-------------------
		double topn = 0;
		String sopn = "";
		try {
			topn = Double.valueOf(QueryExe.getSqlValue(
					"select nvl(sum(AMT_PREDICT),0) from LG_PAYS_RCPT where job_ord_no="
							+ QRYSES_ON + " and FLAG=1", con, "").toString());
		} catch (NumberFormatException | SQLException e1) {
			e1.printStackTrace();
		}

		if (topn > 0)
			sopn = ("...# " + dcf.format(topn) + " # Amount predicted..")
					.replace(",", "");
		;

		// -------------------

		basicInfoLayout.addComponentsRow("caption=.,height=5px");
		String str = "Select a task JOB ORDER # " + QRYSES_ON + " - "
				+ QRYSES_ON_ORD_REF + " / " + QRYSES_ON_ORD_REFNM;
		parentLayout.getWindow().setCaption(str);
		basicInfoLayout.addComponentsRow("caption=" + str
				+ " ,width=-1px,align=start,style=title1,height=20px");
		basicInfoLayout.addComponentsRow(cmdPayments,
				"caption=New Batch Payment ,width=-1px");
		basicInfoLayout.addComponentsRow(cmdRefunds,
				"caption=Refunds/Repaid,width=-1px");
		basicInfoLayout.addComponentsRow(cmdRecipts, "caption=Assign Receipts"
				+ sopn + ",width=-1px");
		basicInfoLayout
				.addComponentsRow(cmdClose, "caption=Closing,width=-1px");
		basicInfoLayout.addComponentsRow(cmdGenPurchase,
				"caption=Generating PO from closed payments,width=-1px");

		FormLayoutManager inf = (FormLayoutManager) infoPanel.getContent();
		inf.removeAll();
		inf.setSpacing(true);
		inf.setMargin(true);

		cmdFinish.setEnabled(false);
		cmdBack.setEnabled(false);
		cmdNext.setEnabled(false);
		cmdCancel.setEnabled(true);

		txtTotDue.setCaption(null);
		txtTotPay.setCaption(null);
		txtTotRefund.setCaption(null);
		txtTotPay.removeStyleName("yellowField");
		txtTotDue.removeStyleName("yellowField");
		txtTotRefund.removeStyleName("yellowField");
		txtTotRepay.removeStyleName("yellowField");
		txtTotClosed.removeStyleName("yellowField");

		inf.addComponentsRow("caption=Summary for all");

		inf.addComponentsRow(
				"caption=Total pay,width=100%,align=start,expand=2", txtTotPay,
				"expand=2,readonly=true,align_text=end");
		inf.addComponentsRow(
				"caption=Total Refunded,width=100%,align=start,expand=2",
				txtTotRefund, "expand=2,readonly=true,align_text=end");

		inf.addComponentsRow(
				"caption=Total Re-paid,width=100%,align=start,expand=2",
				txtTotRepay, "expand=2,readonly=true,align_text=end");

		inf.addLine();
		inf.addComponentsRow(
				"caption=Saleable,width=100%,align=start,expand=2", txtTotDue,
				"expand=2,readonly=true,style=yellowField,align_text=end");
		inf.addLine();

		// inf.addComponentsRow(
		// "caption=No Of Invoiced,width=100%,align=start,expand=2",
		// new TextField(null, "0"),
		// "expand=2,readonly=true,style=numberStyle,align_text=end");
		// inf.addLine();

		inf.addComponentsRow(
				"caption=Total Closed,width=100%,align=start,expand=2",
				txtTotClosed, "expand=2,readonly=true,align_text=end");

		try {
			double tp = Double
					.valueOf(QueryExe.getSqlValue(
							"select sum(amt_jv) from lg_pays where job_ord_no='"
									+ QRYSES_ON + "' and flag!=3", con, "0")
							.toString());
			double tr = Double
					.valueOf(QueryExe
							.getSqlValue(
									"select nvl(sum(AMT),0) from LG_PAYS_REFUNDS where job_ord_no="
											+ QRYSES_ON
											+ " and trans_code=1 and"
											+ " keyfld_pay in (select keyfld from lg_pays where flag!=3)",
									con, "").toString());
			double trp = Double
					.valueOf(QueryExe
							.getSqlValue(
									"select nvl(sum(AMT),0) from LG_PAYS_REFUNDS where job_ord_no="
											+ QRYSES_ON
											+ " and trans_code=2 and"
											+ " keyfld_pay in (select keyfld from lg_pays where flag!=3)",
									con, "").toString());
			double tclos = Double.valueOf(QueryExe.getSqlValue(
					"select nvl(sum(AMT_ACTUAL),0) from LG_PAYS_RCPT where job_ord_no="
							+ QRYSES_ON + " and FLAG=2", con, "").toString());

			double t = tp - tr;
			utilsVaadin.setValueByForce(txtTotPay, dcf.format(tp));
			utilsVaadin.setValueByForce(txtTotRefund, dcf.format(tr));
			utilsVaadin.setValueByForce(txtTotDue, dcf.format(t));
			utilsVaadin.setValueByForce(txtTotRepay, dcf.format(trp));
			utilsVaadin.setValueByForce(txtTotClosed, dcf.format(tclos));

			ResultSet rs = QueryExe
					.getSqlRS(
							"select sum(R.amt_ACTUAL) amt,P.currency from LG_PAYS_RCPT R,"
									+ "	lg_pays P where P.KEYFLD=R.KEYFLD_PAY AND r.job_ord_no='"
									+ QRYSES_ON
									+ "' AND r.FLAG=2 group by p.currency", con);
			if (rs != null) {
				rs.beforeFirst();
				inf.addLine();
				while (rs.next()) {

					inf.addComponentsRow(
							new Label(rs.getString("currency")
									+ ": " + dcf.format(rs.getDouble("amt"))),
							"width=100%,align=start,expand=4");

				}
				rs.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!listnerAdded) {
			tbl_rcpt1.cmdAdd.removeListener(tbl_rcpt1.cmd_add_listner);
			tbl_rcpt1.cmdAdd.addListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					tbl_rcpt1.add_record();
					show_item_for_rcpt_list();

				}
			});

			optType.addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (optType.getValue() == null)
						return;
					ResultSet rs = null;
					try {
						rs = QueryExe.getSqlRS(
								"select *from lg_pays where keyfld='" + QRYSES
										+ "'", con);
						double vl = Double.valueOf(((dataCell) optType
								.getValue()).getValue().toString());
						varRfndCrAc = (vl == 1 ? rs.getString("AC_EMP_DR") : rs
								.getString("AC_MAIN_CR"));
						varRfndDrAc = (vl == 1 ? rs.getString("AC_MAIN_CR")
								: rs.getString("AC_EMP_DR"));

						rs.close();
						utilsVaadin.setValueByForce(
								txtRfndDrAc,
								varRfndDrAc
										+ " - "
										+ QueryExe.getSqlValue(
												"select name from c_ycust where code='"
														+ varRfndDrAc + "'",
												con, "#ERR!NOTFOUND")
												.toString());
						utilsVaadin
								.setValueByForce(
										txtRfndCrAc,
										varRfndCrAc
												+ " - "
												+ QueryExe.getSqlValue(
														"select name from c_ycust where code='"
																+ varRfndCrAc
																+ "'", con,
														"ERR!NOTFOUND")
														.toString());
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			});
			tbl_rcpt1.data.setDefaultValuelistner(new DefaultValueListner() {

				public void setValue(dataCell dc, qryColumn qc) {
					if (dc == null || qc == null)
						return;
					if (qc.getColname().equals("RCPT_NO"))
						dc.setValue(tbl_rcpt1.data.getRowCount(), BigDecimal
								.valueOf(tbl_rcpt1.data.getRowCount()));
					if (qc.getColname().equals("DATE_ISSUE")) {
						Timestamp tm = new Timestamp(System.currentTimeMillis());
						utils.roundDate(tm);
						dc.setValue(tm.toString(), tm);
					}
					if (qc.getColname().equals("AMT_REFUND"))
						dc.setValue("0", BigDecimal.valueOf(0f));
					if (qc.getColname().equals("AMT_PREDICT"))
						dc.setValue("0", BigDecimal.valueOf(0f));
					if (qc.getColname().equals("AMT_ACTUAL"))
						dc.setValue("0", BigDecimal.valueOf(0f));

				}
			});

			tbl_rcpt2.data.setDefaultValuelistner(new DefaultValueListner() {

				public void setValue(dataCell dc, qryColumn qc) {
					if (dc == null || qc == null)
						return;
					if (qc.getColname().equals("RCPT_NO"))
						dc.setValue(tbl_rcpt2.data.getRowCount(), BigDecimal
								.valueOf(tbl_rcpt1.data.getRowCount()));
					if (qc.getColname().equals("DATE_ISSUE")) {
						Timestamp tm = new Timestamp(System.currentTimeMillis());
						utils.roundDate(tm);
						dc.setValue(tm.toString(), tm);
					}
					if (qc.getColname().equals("AMT_REFUND"))
						dc.setValue("0", BigDecimal.valueOf(0f));
					if (qc.getColname().equals("AMT_DUE"))
						dc.setValue("0", BigDecimal.valueOf(0f));
					if (qc.getColname().equals("AMT_ACTUAL"))
						dc.setValue("0", BigDecimal.valueOf(0f));
				}
			});

			txtRfndRcpt.setButtonPress(new ButtonPress() {

				@Override
				public void onButton(String button) {
					if (button.equals("SEARCH"))
						show_recipt_list();
				}
			});

			tbl_rcpt2.data.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (cursorNo < 0)
						return;
					calc_summary(cursorNo);
				}
			});
			txtLocation.addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (txtLocation.getValue() == null)
						return;
					try {
						String vl = Channelplus3Application
								.getInstance()
								.getFrmUserLogin()
								.getMapVars()
								.get("LOCATION_CASH_"
										+ ((dataCell) txtLocation.getValue())
												.getValue());
						if (vl != null && !vl.isEmpty()) {
							String disp = QueryExe.getSqlValue(
									"select name from c_ycust where code='"
											+ vl + "'", con, "")
									+ "";
							txtPayMainCrAc.setDisplayValue(vl, disp);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			txtPayEmpNo.addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						String vl = Channelplus3Application
								.getInstance()
								.getFrmUserLogin()
								.getMapVars()
								.get("EMP_CASH_"
										+ ((dataCell) txtLocation.getValue())
												.getValue());
						if (vl != null && !vl.isEmpty()) {
							String disp = QueryExe.getSqlValue(
									"select name from c_ycust where code='"
											+ vl + "'", con, "")
									+ "";

							txtPayEmpDrAc.setDisplayValue(vl, disp);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			ValueChangeListener pyv = new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					DecimalFormat dcf = new DecimalFormat(
							Channelplus3Application.getInstance()
									.getFrmUserLogin().FORMAT_MONEY);

					try {
						double amt = utilsVaadin.getFieldInfoDoubleValue(
								txtPayAmt, lstfldPays);
						double rate = utilsVaadin.getFieldInfoDoubleValue(
								txtPayRate, lstfldPays);
						utilsVaadin.setValueByForce(txtPayAmtLC,
								dcf.format(amt * rate));
					} catch (Exception e) {
						e.printStackTrace();
						utilsVaadin.showMessage(Channelplus3Application
								.getInstance().getMainWindow(), e.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);

					}
				}
			};
			txtPayRate.addListener(pyv);
			txtPayAmt.addListener(pyv);

			txtRfndAmt.addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					DecimalFormat dcf = new DecimalFormat(
							Channelplus3Application.getInstance()
									.getFrmUserLogin().FORMAT_MONEY);

					try {
						double amt = utilsVaadin.getFieldInfoDoubleValue(
								txtRfndAmt, lstfldRefunds);
						double rate = utilsVaadin.getFieldInfoDoubleValue(
								txtRfndRate, lstfldRefunds);
						utilsVaadin.setValueByForce(txtRfndAmtLC,
								dcf.format(amt * rate));
					} catch (Exception e) {
						e.printStackTrace();
						utilsVaadin.showMessage(Channelplus3Application
								.getInstance().getMainWindow(), e.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);
					}
				}
			});

			listnerAdded = true;
		}
	}

	protected void calc_summary(int cursorNo) {
		DecimalFormat dcf = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);

		double ramt = ((Number) (tbl_rcpt2.data.getFieldValue(cursorNo,
				"AMT_ACTUAL"))).doubleValue();
		double rrefund = ((Number) (tbl_rcpt2.data.getFieldValue(cursorNo,
				"AMT_REFUND"))).doubleValue();
		double rrepaid = ((Number) (tbl_rcpt2.data.getFieldValue(cursorNo,
				"AMT_REPAID"))).doubleValue();
		double rsal = (ramt + rrepaid) - rrefund;
		tbl_rcpt2.data.setFieldValue(cursorNo, "AMT_SALEABLE", rsal);
		tbl_rcpt2.refreshRow(cursorNo);

		double tamt = tbl_rcpt2.data.getSummaryOf("AMT_ACTUAL",
				localTableModel.SUMMARY_SUM);
		double refund = tbl_rcpt2.data.getSummaryOf("AMT_REFUND",
				localTableModel.SUMMARY_SUM);

		double repaid = tbl_rcpt2.data.getSummaryOf("AMT_REPAID",
				localTableModel.SUMMARY_SUM);

		varInfoSaleBal = tbl_rcpt2.data.getSummaryOf("AMT_SALEABLE",
				localTableModel.SUMMARY_SUM);

		tbl_rcpt2
				.getTable()
				.setColumnFooter(
						utilsVaadin.findColByCol("AMT_REFUND",
								tbl_rcpt2.listFields).descr, dcf.format(refund));
		tbl_rcpt2.getTable()
				.setColumnFooter(
						utilsVaadin.findColByCol("AMT_ACTUAL",
								tbl_rcpt2.listFields).descr, dcf.format(tamt));
		tbl_rcpt2.getTable()
				.setColumnFooter(
						utilsVaadin.findColByCol("AMT_SALEABLE",
								tbl_rcpt2.listFields).descr,
						dcf.format(varInfoSaleBal));
		tbl_rcpt2
				.getTable()
				.setColumnFooter(
						utilsVaadin.findColByCol("AMT_REPAID",
								tbl_rcpt2.listFields).descr, dcf.format(repaid));

		utilsVaadin
				.setValueByForce(txtInfoSaleable, dcf.format(varInfoSaleBal));
		utilsVaadin
				.setValueByForce(
						txtInfoDueBal,
						dcf.format(((varInfoBal + repaid) - (varInfoSaleBal + refund))));
	}

	protected void calc_summary_sale(int cursorNo) {
		DecimalFormat dcf = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);
		if (cursorNo >= 0) {
			double slr = ((BigDecimal) tbl_rcpt3.data.getFieldValue(cursorNo,
					"AMT_SALEABLE")).doubleValue();
			double slnw = ((BigDecimal) tbl_rcpt3.data.getFieldValue(cursorNo,
					"NEW_AMT")).doubleValue();
			double slnt = slnw - slr;
			tbl_rcpt3.data.setFieldValue(cursorNo, "VARIANCE_AMT",
					BigDecimal.valueOf(slnt));
			tbl_rcpt3.refreshRow(cursorNo);
		}
		double sl = tbl_rcpt3.data.getSummaryOf("AMT_SALEABLE",
				localTableModel.SUMMARY_SUM);
		double slvar = tbl_rcpt3.data.getSummaryOf("VARIANCE_AMT",
				localTableModel.SUMMARY_SUM);
		double slNet = tbl_rcpt3.data.getSummaryOf("NEW_AMT",
				localTableModel.SUMMARY_SUM);

		tbl_rcpt3.getTable()
				.setColumnFooter(
						utilsVaadin.findColByCol("AMT_SALEABLE",
								tbl_rcpt3.listFields).descr, dcf.format(sl));

		utilsVaadin.setValueByForce(txtPurAmt, dcf.format(sl));
		utilsVaadin.setValueByForce(txtPurVarAmt, dcf.format(slvar));
		utilsVaadin.setValueByForce(txtSaleNet, dcf.format(slNet));

	}

	protected void show_recipt_list() {
		final Window wnd = new Window();
		final VerticalLayout la = new VerticalLayout();
		wnd.setContent(la);

		try {
			utilsVaadin.showSearch(la, new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);
					if (tv.getSelectionValue() > -1) {
						try {
							int rn = tv.getSelectionValue();
							txtRfndRcpt.setDisplayValue(tv.getData()
									.getFieldValue(rn, "RCPT_NO").toString(),
									tv.getData().getFieldValue(rn, "DESCR")
											.toString());
							txtRfndRcpt.setData(new dataCell(tv.getData()
									.getFieldValue(rn, "DESCR").toString(), tv
									.getData().getFieldValue(rn, "RCPT_NO")
									.toString()));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con,
					"SELECT RCPT_NO,DOC_NO,DESCR,DATE_ISSUE FROM LG_PAYS_RCPT WHERE KEYFLD_PAY='"
							+ QRYSES + "' AND FLAG=1 ORDER BY RCPT_NO", true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void resetLayout() {
		switch (mode_current) {
		case MODE_MAIN_SCREEN:
			final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
			centralPanel.removeAllComponents();
			mainLayout.removeAllComponents();
			((AbstractOrderedLayout) mainPanel.getContent())
					.removeAllComponents();
			basicLayout.removeAllComponents();
			((FormLayoutManager) infoPanel.getContent()).removeAll();
			basicInfoLayout.removeAll();
			tbs.removeAllComponents();
			break;
		case MODE_SEL_PAYS:
			pnlList.removeAllComponents();
			break;
		case MODE_FORM_RECIPTS1:
			tbl_rcpt1.resetLayout();
			tbl_rcpt1.createView();
			tbl_rcpt1.getTable().setSizeFull();
			tbl_rcpt1.getTable().setHeight("400px");
			break;
		case MODE_FORM_PO:
			layoutRcpt3.setHeight("225px");
			tbl_rcpt3.getTable().setHeight("200px");
			tbl_rcpt3.resetLayout();
			tbl_rcpt3.createView();
			break;
		case MODE_SEL_PO:
			pnlList.removeAllComponents();
			tbl_rcpt3.resetLayout();
			tbl_rcpt3.createView();
			tbl_rcpt2.getTable().setHeight("200px");
			break;
		case MODE_FORM_CLOSE:
			tbl_rcpt2.getTable().setFooterVisible(true);
			tbl_rcpt2.resetLayout();
			tbl_rcpt2.createView();
			tbl_rcpt2.getTable().setHeight("200px");
			break;
		default:
			break;
		}
		((FormLayoutManager) pnlPayBatch.getContent()).removeAll();
		basicInfoLayout.removeAll();
		tbs.removeAllComponents();
		tbs.addTab(tbSheetPanel, "Main Screen");
		parentLayout.getWindow().center();
	}

	public void showInitView() {

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractOrderedLayout) parentLayout;
	}

	public void save_data() {
		save_data(true);
	}

	public void save_data(boolean cls) {
		String mod = listTargetMode.get(0);

		varPrintQRYSES = QRYSES;
		varPrintQRYSES_ON = QRYSES_ON;
		varPrintQRYSES_REFUND = QRYSES_REFUND;

		try {
			varErrorOnSave = false;
			con.setAutoCommit(false);
			if (mod.equals(MODE_PAYMENTS))
				save_payment();
			if (mod.equals(MODE_PAY_RECEIPTS))
				save_rcpt1();
			if (mod.equals(MODE_PAY_REFUNDS))
				save_refunds();
			if (mod.equals(MODE_CLOSE_PAYMMENT))
				save_closing();
			if (mod.equals(MODE_PO_PAYMMENT))
				save_to_po();

			con.commit();
			parentLayout.getWindow().showNotification("Saved Successfully");

			if (chkPrint.booleanValue())
				print_data();

			if (cls)
				cmdCancel.click();

		} catch (Exception ex) {
			ex.printStackTrace();
			varErrorOnSave = true;
			parentLayout.getWindow().showNotification("Err Saving ! ",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}
	}

	private void save_to_po() throws Exception {

		if (tbl_rcpt3.data.getRowCount() <= 0)
			throw new Exception("No data found to generate PO!");
		if (txtPOSupplier.getValue() == null
				|| txtPOSupplier.getValue().toString().isEmpty())
			throw new Exception("Please select supplier !");
		calc_summary_sale(0);
		String pcode = utils.getSqlValue(
				"select repair.getsetupvalue_2('CURRENT_PERIOD') from dual",
				con);
		String location = utils.getSqlValue(
				"select repair.getsetupvalue_2('DEFAULT_LOCATION') from dual",
				con);
		String poExist = utils.getSqlValue(
				"select nvl(max(ord_no),0) from order1 where ord_code=103 and ord_no='"
						+ txtPONo.getValue() + "'", con);
		if (!poExist.trim().equals("0")) {
			utilsVaadin
					.setValueByForce(
							txtPONo,
							utils.getSqlValue(
									"select nvl(max(ord_no),0)+1 from order1 where ord_code=103",
									con));
		}

		String sq = "insert into order1 "
				+ "("
				+ " PERIODCODE, ORD_NO, ORD_CODE, ORD_REF, ORD_REFNM,"
				+ " ORD_DATE, ORD_AMT, ORD_DISCAMT,"
				+ " ORD_FLAG, YEAR, REMARKS,"
				+ " ATTN, DELIVEREDQTY, ORDERDQTY, ONAME,"
				+ " LOCATION_CODE, ORD_TYPE, RECIPT_KEYFLD,"
				+ " PUR_KEYFLD, LCNO, ORDACC, ORD_REFERENCE , STRA ,"
				+ " PAY_KEYFLD ,ORD_FC_DESCR ,ORD_FC_RATE )"
				+ " VALUES  ( "
				+ ":PERIODCODE, :ORD_NO, :ORD_CODE, :ORD_REF, :ORD_REFNM,"
				+ ":ORD_DATE, :ORD_AMT, :ORD_DISCAMT,"
				+ ":ORD_FLAG, '2003', :REMARKS, :ATTN, 0, 0, 'DE',"
				+ ":LOCATION_CODE, 2, null,"
				+ "	null, null, :ORDACC, :ORD_REFERENCE, :STRA, :PK, :CURRENCY , :RATE )";

		String sq2 = " INSERT INTO ORDER2 ( "
				+ " PERIODCODE, ORD_NO, ORD_CODE,"
				+ " ORD_POS, ORD_DATE, ORD_REFER,"
				+ " ORD_PRICE, ORD_ITMAVER, ORD_PKQTY,"
				+ " ORD_UNQTY, ORD_ALLQTY, ORD_PACK, "
				+ " ORD_PACKD, ORD_UNITD, ORD_DISCAMT,"
				+ " ORD_FLAG, YEAR, DESCR,"
				+ " KEYFLD, DELIVEREDQTY, SALEINV, "
				+ " ORD_REQ_DATE, LOCATION_CODE, ORD_TYPE,"
				+ " ORD_RCPTNO , PAYMENT_REFERENCE,ORD_FC_DESCR) "
				+ " VALUES " // values
				+ " ( :PERIODCODE, :ORD_NO, :ORD_CODE, "
				+ " :ORD_POS, :ORD_DATE, :ORD_REFER,"
				+ " :ORD_PRICE, 0, 1 , "
				+ " 0, 1, :ORD_PACK, "
				+ " :ORD_PACKD, :ORD_UNITD , 0,"
				+ " 2, '2003', :DESCR, "
				+ " NULL, 0, NULL,"
				+ " NULL , :LOCATION_CODE,  2,"
				+ "  :ORD_RCPTNO , :PAYMENT_REFERENCE , (SELECT NVL(MAX(VALUE),'KWD')"
				+ "      FROM CP_USER_PROFILES WHERE VARIABLE='DEFAULT_CURRENCY'))  ";

		con.setAutoCommit(false);
		QueryExe qe = new QueryExe(sq, con);
		qe.setParaValue("PERIODCODE", pcode);
		qe.setParaValue("ORD_NO", txtPONo.getValue());
		qe.setParaValue("ORD_CODE", 103);
		qe.setParaValue("ORD_REF", txtPOSupplier.getValue());
		qe.setParaValue("ORD_REFNM", txtPOSupplier.getDisplay());
		qe.setParaValue("ORD_DATE", txtPODate.getValue());
		qe.setParaValue("ORD_AMT",
				utilsVaadin.getFieldInfoDoubleValue(txtPurAmt, lstfldPO));
		qe.setParaValue("ORD_DISCAMT", 0);
		qe.setParaValue("ORD_FLAG", 2);
		qe.setParaValue("REMARKS", txtPODescr.getValue());
		qe.setParaValue("ATTN", txtInv.getValue());
		qe.setParaValue("LOCATION_CODE", location);
		qe.setParaValue("ORDACC",
				utilsVaadin.getMapVars().get("PURCHASE_ORDER_ACCOUNT"));
		qe.setParaValue("ORD_REFERENCE", QRYSES_ON);
		qe.setParaValue("STRA", utilsVaadin.getMapVars().get("DEFAULT_STORE"));
		qe.setParaValue("PK", QRYSES);
		qe.setParaValue("CURRENCY", txtPurCurrency.getValue());
		qe.setParaValue("RATE",
				utilsVaadin.getFieldInfoDoubleValue(txtPurRate, lstfldPO));

		qe.execute();

		QueryExe qe2 = new QueryExe(sq2, con);

		QueryExe qeitm = new QueryExe(
				"select *from items where reference=:RFR", con);

		qeitm.parse();
		qe2.parse();

		for (int i = 0; i < tbl_rcpt3.data.getRowCount(); i++) {
			String rfr = tbl_rcpt3.data.getFieldValue(i, "ITEM_REFER")
					.toString();
			String rno = tbl_rcpt3.data.getFieldValue(i, "DOC_NO").toString();

			double price = ((Number) tbl_rcpt3.data.getFieldValue(i,
					"AMT_SALEABLE")).doubleValue();

			qeitm.setParaValue("RFR", rfr);
			ResultSet rs = qeitm.executeRS(false);

			if (rs == null || !rs.first())
				throw new Exception(
						"Item removed, or unexpected error to fetch items  # "
								+ rfr + " !");

			qe2.setParaValue("PERIODCODE", pcode);
			qe2.setParaValue("ORD_NO", txtPONo.getValue());
			qe2.setParaValue("ORD_CODE", 103);
			qe2.setParaValue("ORD_POS", i + 1);
			qe2.setParaValue("ORD_DATE", txtPODate.getValue());
			qe2.setParaValue("ORD_REFER", rfr);
			qe2.setParaValue("ORD_PRICE", price);
			qe2.setParaValue("ORD_PACK", rs.getDouble("PACK"));
			qe2.setParaValue("ORD_PACKD", rs.getString("PACKD"));
			qe2.setParaValue("ORD_UNITD", rs.getString("UNITD"));
			qe2.setParaValue("DESCR", rs.getString("DESCR"));
			qe2.setParaValue("LOCATION_CODE", location);
			qe2.setParaValue("ORD_RCPTNO", rno);
			qe2.setParaValue("PAYMENT_REFERENCE", QRYSES);
			qe2.execute(false);
			rs.close();
		}
		QueryExe.execute(
				"begin X_PUR_AND_SRV(" + txtPONo.getValue() + ");"
						+ " update LG_PAYS set flag=3 , PO_ORD_NO='"
						+ txtPONo.getValue() + "' where keyfld in (" + QRYSES
						+ "); end;", con);
		qeitm.close();
		qe2.close();
		qe.close();
		con.commit();

	}

	private void save_closing() throws Exception {
		validate_data();
		Map<String, Object> mp = new HashMap<String, Object>();
		QueryExe.execute("delete from LG_PAYS_RCPT where keyfld_pay=" + QRYSES,
				con);
		mp.put("KEYFLD_PAY", QRYSES);
		mp.put("JOB_ORD_NO", QRYSES_ON);
		mp.put("FLAG", 1);

		tbl_rcpt2.insert_to_table("LG_PAYS_RCPT", mp, "", "AMT_PREDICT");

		ResultSet rsr = QueryExe.getSqlRS(
				"SELECT DOC_NO, NVL(COUNT(*),0)  FROM LG_PAYS_RCPT  WHERE KEYFLD_PAY="
						+ QRYSES + "" + " GROUP BY DOC_NO HAVING COUNT(*)>1",
				con);
		if (rsr != null) {
			if (rsr.first()) {
				String rcp = rsr.getString("doc_no");
				rsr.close();
				throw new Exception("Recipt # " + rcp
						+ " , encountered multiple entry !");
			}
			rsr.close();
		}

		QueryExe.execute(
				"begin X_LG_close_pay(" + QRYSES + ",'"
						+ txtInfoDescr.getValue() + "', :DT ); "
						+ " UPDATE LG_PAYS_RCPT "
						+ " SET AMT_PREDICT=AMT_ACTUAL WHERE KEYFLD_PAY="
						+ QRYSES + "; end; ", con, new Parameter("DT",
						txtInfoDate.getValue()));
	}

	private void save_refunds() throws Exception {
		validate_data();
		Map<String, Object> mapVarDBs = new HashMap<String, Object>();
		Date dt = new Date(System.currentTimeMillis());
		double varNewk = -1;
		if (QRYSES_REFUND.isEmpty()) {
			if (Double.valueOf(QueryExe.getSqlValue(
					"select 1 from lg_pays_refunds where trn_no='"
							+ txtRfndTrnNo.getValue() + "' and keyfld_pay='"
							+ QRYSES + "'", con, "0").toString()) == 1)
				varNewk = Double.valueOf(utils.getSqlValue(
						"Select nvl(max(TRN_NO),0)+1 from lg_pay" + "" + ""
								+ "s_refunds where keyfld_pay='" + QRYSES
								+ "' and trn_no='" + QRYSES_REFUND + "'", con));
			else
				varNewk = utilsVaadin.getFieldInfoDoubleValue(txtRfndTrnNo,
						lstfldRefunds);
		} else
			varNewk = utilsVaadin.getFieldInfoDoubleValue(txtRfndTrnNo,
					lstfldRefunds);

		mapVarDBs.put("KEYFLD_PAY", QRYSES);
		mapVarDBs.put("AC_DR", varRfndDrAc);
		mapVarDBs.put("AC_CR", varRfndCrAc);
		mapVarDBs.put("JOB_ORD_NO", QRYSES_ON);
		mapVarDBs.put("TRANS_CODE", optType.getValue());

		QueryExe qea = new QueryExe(con);
		utilsVaadin.setListToQueryExe(lstfldRefunds, qea, mapVarDBs);
		if (QRYSES_REFUND.isEmpty())
			qea.AutoGenerateInsertStatment("LG_PAYS_REFUNDS");
		else
			qea.AutoGenerateUpdateStatment("LG_PAYS_REFUNDS",
					"'KEYFLD_PAY','TRN_NO'",
					" WHERE KEYFLD_PAY = :KEYFLD_PAY AND TRN_NO = :TRN_NO ");
		qea.execute();
		qea.close();
		QueryExe.execute("begin X_LG_POST_PAY_REFUND(" + QRYSES + "," + varNewk
				+ "); end;", con);

		update_all_rcpts();
	}

	private void update_all_rcpts() throws SQLException {
		String rfnd_str = "  update_on_refund boolean:=false;  ";
		if (listTargetMode.get(0).equals(MODE_PAY_REFUNDS))
			rfnd_str = "  update_on_refund boolean:=true;  ";
		QueryExe.execute(
				"declare "
						+ rfnd_str
						+ "nm number:=0;"
						+ " pd number:=0; "
						+ " ai varchar2(100);"
						+ " ar varchar2(100);"
						+ " aamt number:=0;"
						+ " aac varchar2(100);"
						+ " rcpt varchar2(100);"
						+ " cursor ix is select *from lg_pays_rcpt  where keyfld_pay= :KEYFLD_PAY order by RCPT_NO ; "
						+ " begin"
						+ " for x in ix loop "
						+ "   select nvl(sum(AMT),0) into nm from LG_PAYS_REFUNDS "
						+ "     where keyfld_pay= :KEYFLD_PAY and rcpt_no=x.rcpt_no and trans_code=1;"
						+ "   select nvl(sum(AMT),0) into pd from LG_PAYS_REFUNDS "
						+ "     where keyfld_pay= :KEYFLD_PAY and rcpt_no=x.rcpt_no and trans_code=2;"
						+ "   update lg_pays_rcpt set amt_refund=nm , amt_repaid=pd ,"
						+ "     ATTACHED_RCPT_NO=ar , ATTACHED_REFUND_OR_REPAY_AMT= aamt,"
						+ "     ATTACHED_ITEM_REFER = ai , ATTACHED_AC_STORE_DR = aac "
						+ "		where keyfld_pay = :KEYFLD_PAY and rcpt_no=x.rcpt_no; "
						+ "   select nvl(sum(AMT),0) into nm from LG_PAYS_REFUNDS "
						+ "     where keyfld_pay= :KEYFLD_PAY and trans_code=1; "
						+ "   select nvl(sum(AMT),0) into pd from LG_PAYS_REFUNDS "
						+ "     where keyfld_pay= :KEYFLD_PAY and trans_code=2; "
						+ "   update lg_pays set TOT_REFUNDED = nm  where keyfld = :KEYFLD_PAY ;  "
						+ "  update lg_pays set TOT_PAID_ON_RCPT = nm where keyfld = :KEYFLD_PAY ; "
						+ " end loop; "
						+ " select MAX(attached_rcpt_no) into ar from lg_pays_refunds where keyfld_pay= :KEYFLD_PAY and trn_no= :TRN_NO; "
						+ "  if update_on_refund=true and ar is not null then "
						+ "   select r.amt, r.ATTACHED_RCPT_NO, r.ATTACHED_ITEM_REFER, i.storeacc , rcpt_no "
						+ "     into aamt, ar , ai , aac , rcpt   "
						+ "     from lg_pays_refunds r,items i  where i.reference=r.ATTACHED_ITEM_REFER and "
						+ "     r.keyfld_pay= :KEYFLD_PAY and r.TRN_NO = :TRN_NO ; "
						+ "    update lg_pays_rcpt set amt_refund=nm , amt_repaid=pd ,"
						+ "      ATTACHED_RCPT_NO=ar , ATTACHED_REFUND_OR_REPAY_AMT= aamt,"
						+ "      ATTACHED_ITEM_REFER = ai , ATTACHED_AC_STORE_DR = aac "
						+ "		 where keyfld_pay = :KEYFLD_PAY and rcpt_no=rcpt;  "
						+ "  end if; " + "    end; ", con, new Parameter(
						"KEYFLD_PAY", QRYSES), new Parameter("TRN_NO",
						txtRfndTrnNo.getValue()));
	}

	private void save_payment() throws Exception {
		validate_data();
		Map<String, Object> mapVarDBs = new HashMap<String, Object>();
		Date dt = new Date(System.currentTimeMillis());
		if (!QRYSES.isEmpty() && !utilsVaadin.canEditTrans("LGPAYS", con))
			throw new Exception("Editing Denied !");

		if (QRYSES.isEmpty()) {
			if (QueryExe.getSqlValue(
					"select 1 from lg_pays where keyfld='"
							+ txtPayKeyfld.getValue() + "'", con, "").equals(
					"1"))
				varSelectedPay = Double.valueOf(utils.getSqlValue(
						"Select nvl(max(keyfld),0)+1 from lg_pays", con));
			else
				varSelectedPay = utilsVaadin.getFieldInfoDoubleValue(
						txtPayKeyfld, lstfldPays);

			txtPayKeyfld.setValue(varSelectedPay);
			mapVarDBs.put("date_create", dt);
			mapVarDBs.put("username", Channelplus3Application.getInstance()
					.getFrmUserLogin().getTxtUser().getValue());
		}

		mapVarDBs.put("date_modified", dt);
		mapVarDBs.put("JOB_ORD_NO", QRYSES_ON);
		QueryExe qea = new QueryExe(con);
		utilsVaadin.setListToQueryExe(lstfldPays, qea, mapVarDBs);
		if (QRYSES.isEmpty())
			qea.AutoGenerateInsertStatment("LG_PAYS");
		else
			qea.AutoGenerateUpdateStatment("LG_PAYS", "'KEYFLD'",
					" WHERE KEYFLD = :KEYFLD ");
		qea.execute();
		qea.close();
		// save_rcpt1();

		QueryExe.execute("begin X_LG_POST_PAY(" + varSelectedPay + "); end;",
				con);

		// cmdCancel.click();

	}

	private void save_rcpt1() throws SQLException {
		if (QRYSES.isEmpty())
			throw new SQLException("No payment selected !");

		QueryExe.execute("delete from LG_PAYS_RCPT where keyfld_pay='" + QRYSES
				+ "'", con);
		Map<String, Object> mp = new HashMap<String, Object>();
		mp.put("KEYFLD_PAY", QRYSES);
		mp.put("JOB_ORD_NO", QRYSES_ON);
		mp.put("FLAG", 1);
		tbl_rcpt1.insert_to_table("LG_PAYS_RCPT", mp, "",
				"AMT_ACTUAL, AMT_DUE, AMT_REFUND, AMT_REPAID , "
						+ "AC_DUE_DR, AC_STORE_DR, AC_EXPENSE_CR");

		// check items if not existed in contract
		// --------------------------------------------------

		for (int i = 0; i < tbl_rcpt1.data.getRowCount(); i++) {
			String st = utils.nvl(
					tbl_rcpt1.data.getFieldValue(i, "ITEM_REFER"), "");

			int fnd = Integer.valueOf(QueryExe.getSqlValue(
					"select nvl(count(*),0) from  LG_CUSTITEMS where code='"
							+ QRYSES_ON_ORD_REF + "' and COST_ITEM='" + st
							+ "'", con, "0")
					+ "");
			if (fnd <= 0)
				throw new SQLException(
						"Error , this item has no contract with client-> " + st);
		}

		// --------------------------------------------------
		// checking if any duplicate recipt is there ?
		// --------------------------------------------------
		ResultSet rs = QueryExe.getSqlRS("select doc_no,nvl(count(*),0) cnt "
				+ "from lg_pays_rcpt where keyfld_pay=" + QRYSES + "  "
				+ " group by doc_no " + " having count(*)>1", con);
		if (rs != null && rs.first()) {
			String dn = rs.getString("doc_no");
			rs.close();
			throw new SQLException(" Recipt no duplicated :" + dn);
		}
		// --------------------------------------------------

		if (rs != null)
			rs.close();

		update_all_rcpts();
	}

	private void validate_data() throws Exception {
		if (listTargetMode.get(0).equals(MODE_PAYMENTS)) {
			if (txtLocation.getValue() == null)
				throw new Exception("Location is not defined !");
			// if (txtPayEmpNo.getValue() == null
			// || txtPayEmpNo.getValue().toString().isEmpty())
			// throw new Exception("Employee must define !");
			if (txtPayEmpDrAc.getValue() == null
					|| txtPayEmpDrAc.getValue().toString().isEmpty())
				throw new Exception("Emp Cash A/c must define !");
			if (txtPayMainCrAc.getValue() == null
					|| txtPayMainCrAc.getValue().toString().isEmpty())
				throw new Exception("Main Cash a/c must define !");
			if (txtPayDescr.getValue() == null
					|| txtPayDescr.getValue().toString().isEmpty())
				throw new Exception("Descr must define !");
			if (utilsVaadin.getFieldInfoDoubleValue(txtPayAmt, lstfldPays) <= 0)
				throw new Exception(
						"Pay amount should be greater than 0.0 value !");

		}

		if (listTargetMode.get(0).equals(MODE_PAY_REFUNDS)) {
			if (txtRfndDescr.getValue() == null
					|| txtRfndDescr.getValue().toString().isEmpty())
				throw new Exception("Descr must define !");
			if (utilsVaadin.getFieldInfoDoubleValue(txtRfndAmt, lstfldRefunds) <= 0)
				throw new Exception(
						"Refund amount should be greater than 0.0 value !");

			// if (optType.getValue().equals(OPTION_REPAID)
			// && utils.nvl(txtRfndRcpt.getValue(), "").isEmpty())
			// throw new Exception("When  Re-pay ,Recipt has to select!");

			if (optType.getValue().equals(OPTION_REFUND)
					&& !utils.nvl(txtRfndRcpt.getValue(), "").isEmpty()
					&& QRYSES_REFUND.isEmpty()) {

				Double rcpt_amt = 0.0;
				if (QRYSES_REFUND.isEmpty())
					rcpt_amt = ((BigDecimal) QueryExe.getSqlValue(
							"select AMT_PREDICT-AMT_REFUND from lg_pays_rcpt where keyfld_pay = "
									+ QRYSES + " and rcpt_no='"
									+ txtRfndRcpt.getValue() + "'", con,
							BigDecimal.valueOf(0.0f))).doubleValue();
				else
					rcpt_amt = ((BigDecimal) QueryExe
							.getSqlValue(
									"select AMT_PREDICT - (SELECT NVL(SUM(AMT),0) FROM LG_PAYS_REFUNDS "
											+ " WHERE trans_code=1 and KEYFLD_PAY='"
											+ QRYSES
											+ "' and rcpt_no='"
											+ txtRfndRcpt.getValue()
											+ "') from lg_pays_rcpt where keyfld_pay = "
											+ QRYSES + " and rcpt_no='"
											+ txtRfndRcpt.getValue() + "'",
									con, BigDecimal.valueOf(0.0)))
							.doubleValue();
				if (rcpt_amt == 0.0f)
					throw new Exception("Precited amount is 0 in recipt !");
				Double rfnd_amt = utilsVaadin.getFieldInfoDoubleValue(
						txtRfndAmt, lstfldRefunds);
				if (rfnd_amt > rcpt_amt)
					throw new Exception(
							"Precited amount must be in LESS THAN (" + rcpt_amt
									+ ")");
			}
		}

		if (listTargetMode.get(0).equals(MODE_CLOSE_PAYMMENT)) {
			if (tbl_rcpt2.data.getRowCount() <= 0)
				throw new Exception("Must have at least 1 recipt !");
			for (int i = 0; i < tbl_rcpt2.data.getRowCount(); i++)
				if (utils.nvl(tbl_rcpt2.data.getFieldValue(i, "DOC_NO"), "")
						.isEmpty())
					throw new Exception("Recipt No must have entry !");

			calc_summary(0);
			double refund = tbl_rcpt2.data.getSummaryOf("AMT_REFUND",
					localTableModel.SUMMARY_SUM);

			double repaid = tbl_rcpt2.data.getSummaryOf("AMT_REPAID",
					localTableModel.SUMMARY_SUM);

			if (utils.round((varInfoBal + repaid), 2) != utils.round(
					(varInfoSaleBal + refund), 2))
				throw new Exception("Saleable # Balance , " + varInfoSaleBal
						+ " < > " + varInfoBal);
		}

	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		load_data();
	}

	@Override
	public void load_data() {
		try {
			if (mode_current.equals(MODE_FORM_PAYMENTS))
				load_paymment();
			if (mode_current.equals(MODE_FORM_RECIPTS1))
				load_recipts();
			if (mode_current.equals(MODE_FORM_CLOSE))
				load_closing();
			if (mode_current.equals(MODE_FORM_REFUNDS))
				load_refunds();
			if (mode_current.equals(MODE_FORM_PO))
				load_PO();

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("Err Loading ! ",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void load_PO() throws SQLException {

		tbl_rcpt3.data
				.executeQuery(
						"select L.* , ((l.amt_actual+l.amt_repaid) -amt_refund) "
								+ " AMT_SALEABLE from lg_pays_rcpt L where KEYFLD_PAY IN ("
								+ QRYSES + ") ORDER BY KEYFLD_PAY,RCPT_NO",
						true);

		for (int i = 0; i < tbl_rcpt3.data.getRowCount(); i++) {

			double slr = ((BigDecimal) tbl_rcpt3.data.getFieldValue(i,
					"AMT_SALEABLE")).doubleValue();

			double amt = ((BigDecimal) tbl_rcpt3.data.getFieldValue(i,
					"AMT_ACTUAL")).doubleValue();
			tbl_rcpt3.data.setFieldValue(i, "AMT_ACTUAL",
					BigDecimal.valueOf(amt));
			tbl_rcpt3.data.setFieldValue(i, "NEW_AMT", BigDecimal.valueOf(slr));
			double slnw = ((BigDecimal) tbl_rcpt3.data.getFieldValue(i,
					"NEW_AMT")).doubleValue();
			double slnt = slnw - slr;

			tbl_rcpt3.data.setFieldValue(i, "VARIANCE_AMT",
					BigDecimal.valueOf(slnt));
		}
		tbl_rcpt3.fill_table();

		utilsVaadin.setValueByForce(txtPONo, utils.getSqlValue(
				"select nvl(max(ord_no),0)+1 from order1 where ord_code=103 ",
				con));
		String cd = utilsVaadin.getMapVars().get("LG_RCPT_SUPPLIER_CODE");
		String cur = utilsVaadin.getMapVars().get("LG_DEFAULT_CURRENCY");
		double rat = Double.valueOf(utilsVaadin.getMapVars().get(
				"LG_DEFAULT_RATE"));

		if (cd != null && !cd.isEmpty()) {
			String cdnm = QueryExe
					.getSqlValue("select name from c_ycust where code='" + cd
							+ "'", con, "")
					+ "";
			txtPOSupplier.setDisplayValue(cd, cdnm);

		}
		if (cur != null && !cur.isEmpty())
			utilsVaadin.setValueByForce(txtPurCurrency, cur);

		utilsVaadin.setValueByForce(txtPurRate, rat);

		if (txtPODate.getValue() == null)
			txtPODate.setValue(new Date());
		((Date) txtPODate.getValue()).setTime(System.currentTimeMillis());
		calc_summary_sale(0);

	}

	private void load_refunds() {
		utilsVaadin.setDefaultValues(lstfldRefunds, true);
		chkPrint.setValue(false);
		optType.setValue(OPTION_REFUND);
		try {
			if (txtRfndDate.getValue() == null)
				txtRfndDate.setValue(new Date());
			((Date) txtRfndDate.getValue()).setTime(System.currentTimeMillis());
			txtRfndTrnNo.setEnabled(false);
			if (QRYSES.isEmpty())
				throw new SQLException("Must select payment !");
			ResultSet rs = QueryExe.getSqlRS(
					"select *from lg_pays where keyfld='" + QRYSES + "'", con);
			varRfndCrAc = rs.getString("AC_EMP_DR");
			varRfndDrAc = rs.getString("AC_MAIN_CR");
			double rate = rs.getDouble("RATE");
			String currency = rs.getString("CURRENCY");
			rs.close();
			utilsVaadin.setValueByForce(txtRfndRate, rate);
			utilsVaadin.setValueByForce(txtRfndCurrency, currency);
			utilsVaadin.setValueByForce(
					txtRfndDrAc,
					varRfndDrAc
							+ " - "
							+ QueryExe.getSqlValue(
									"select name from c_ycust where code='"
											+ varRfndDrAc + "'", con, "")
									.toString());
			utilsVaadin.setValueByForce(
					txtRfndCrAc,
					varRfndCrAc
							+ " - "
							+ QueryExe.getSqlValue(
									"select name from c_ycust where code='"
											+ varRfndCrAc + "'", con, "")
									.toString());
			txtRfndTrnNo.setEnabled(true);

			if (!QRYSES_REFUND.isEmpty()) {
				QueryExe qe = new QueryExe(
						"select *from LG_PAYS_REFUNDS where keyfld_pay='"
								+ QRYSES + "' and trn_no='" + QRYSES_REFUND
								+ "'", con);
				rs = qe.executeRS();
				if (rs != null && rs.first()) {
					utilsVaadin.assignValues(rs, lstfldRefunds);
					txtRfndTrnNo.setEnabled(false);
				}
				if (rs.getDouble("TRANS_CODE") == 1)
					optType.setValue(OPTION_REFUND);
				else
					optType.setValue(OPTION_REPAID);

				qe.close();
			} else
				utilsVaadin.setValueByForce(txtRfndTrnNo, QueryExe.getSqlValue(
						"select nvl(max(trn_no),0)+1 from lg_pays_refunds where keyfld_pay='"
								+ QRYSES + "'", con, "1"));

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("Err Saving ! ",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void load_closing() {
		DecimalFormat dcf = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);
		varInfoRate = 1;
		try {

			varInfoRate = Double.valueOf(QueryExe.getSqlValue(
					"select rate from lg_pays where job_ord_no='" + QRYSES_ON
							+ "' AND KEYFLD=" + QRYSES, con, "0").toString());

			varInfoInitPay = Double.valueOf(QueryExe.getSqlValue(
					"select sum(amt_jv_fc) from lg_pays where job_ord_no='"
							+ QRYSES_ON + "' AND KEYFLD=" + QRYSES, con, "0")
					.toString());

			varInfoUpay = Double
					.valueOf(QueryExe
							.getSqlValue(
									"select nvl(sum(AMT_fc),0) from LG_PAYS_REFUNDS where rcpt_no is null and TRANS_CODE=2 AND job_ord_no="
											+ QRYSES_ON
											+ " and keyfld_pay="
											+ QRYSES, con, "").toString());
			varInfoURefund = Double
					.valueOf(QueryExe
							.getSqlValue(
									"select nvl(sum(AMT_FC),0) from LG_PAYS_REFUNDS where rcpt_no is null and TRANS_CODE=1 AND job_ord_no="
											+ QRYSES_ON
											+ " and keyfld_pay="
											+ QRYSES, con, "").toString());

			varInfoBal = (varInfoInitPay + varInfoUpay) - varInfoURefund;
			utilsVaadin.setValueByForce(txtInfoURate, varInfoRate + "");
			utilsVaadin.setValueByForce(txtInfoInitPay,
					dcf.format(varInfoInitPay));
			utilsVaadin.setValueByForce(txtInfoBalance, dcf.format(varInfoBal));
			utilsVaadin.setValueByForce(txtInfoURefund,
					dcf.format(varInfoURefund));
			utilsVaadin.setValueByForce(txtInfoUPay, dcf.format(varInfoUpay));

			tbl_rcpt2.data
					.executeQuery(
							"select L.* , 0 AMT_SALEABLE , "
									+ " (SELECT MAX(NAME) FROM ACACCOUNT WHERE ACCNO=L.AC_STORE_DR) AC_STORE_DR_NAME,"
									+ " (SELECT MAX(NAME) FROM ACACCOUNT WHERE ACCNO=L.AC_EXPENSE_CR) AC_EXPENSE_CR_NAME,"
									+ " (SELECT MAX(NAME) FROM ACACCOUNT WHERE ACCNO=L.AC_DUE_DR) AC_DUE_DR_NAME "
									+ " from LG_PAYS_RCPT L where KEYFLD_pay='"
									+ QRYSES + "'", true);

			QueryExe qe = new QueryExe("select *from lg_pays where keyfld='"
					+ QRYSES + "'", con);

			ResultSet rs = qe.executeRS();

			if (rs != null && rs.first())
				utilsVaadin.assignValues(rs, lstfldInfo);

			qe.close();
			utilsVaadin.setValueByForce(txtInfoDescr, "Closing Job Ord #"
					+ QRYSES_ON + " , Payment ID # " + QRYSES);

			((Date) txtInfoDate.getValue()).setTime(System.currentTimeMillis());
			utils.roundDate(((Date) txtInfoDate.getValue()));

			QueryExe qei = new QueryExe(
					"select i.STOREACC,i.EXPACC, (SELECT MAX(NAME) FROM ACACCOUNT WHERE ACCNO=I.STOREACC) storeacc_name,"
							+ " (SELECT MAX(NAME) FROM ACACCOUNT WHERE ACCNO=I.EXPACC) expacc_name "
							+ " from items i "
							+ "  where i.reference= :REFER "
							+ "   ", con);

			qei.parse();
			for (int i = 0; i < tbl_rcpt2.data.getRowCount(); i++) {
				if (utils.nvl(tbl_rcpt2.data.getFieldValue(i, "AC_STORE_DR"),
						"").isEmpty()) {
					qei.setParaValue("refer",
							tbl_rcpt2.data.getFieldValue(i, "item_refer"));
					ResultSet rst = qei.executeRS(false);
					if (rst != null && rst.first()) {
						tbl_rcpt2.data.setFieldValue(i, "ac_store_dr",
								rst.getString("STOREACC"));
						tbl_rcpt2.data.setFieldValue(i, "ac_store_dr_name",
								rst.getString("STOREACC_NAME"));
						tbl_rcpt2.data.setFieldValue(i, "ac_expense_cr",
								rst.getString("EXPACC"));
						tbl_rcpt2.data.setFieldValue(i, "ac_expense_cr_name",
								rst.getString("EXPACC_NAME"));

					}
					BigDecimal b = new BigDecimal(tbl_rcpt2.data.getFieldValue(
							i, "AMT_PREDICT").toString());
					tbl_rcpt2.data.setFieldValue(i, "AMT_ACTUAL", b);
				}
			}
			qei.close();
			tbl_rcpt2.fill_table();
		} catch (Exception e) {
			e.printStackTrace();
			parentLayout.getWindow().showNotification("Err Saving ! ",
					e.getMessage(), Notification.TYPE_ERROR_MESSAGE);

		}

	}

	private void load_recipts() throws SQLException {
		tbl_rcpt1.data.executeQuery(
				"select *from LG_PAYS_RCPT where KEYFLD_pay='" + QRYSES + "'",
				true);
		tbl_rcpt1.fill_table();
	}

	private void load_paymment() {
		utilsVaadin.setDefaultValues(lstfldPays, true);
		chkPrint.setValue(false);
		try {
			if (txtPayDate.getValue() == null)
				txtPayDate.setValue(new Date());
			if (txtPayChequeDate.getValue() == null)
				txtPayChequeDate.setValue(new Date());
			if (txtPayExpiryDate.getValue() == null)
				txtPayExpiryDate.setValue(new Date());
			((Date) txtPayDate.getValue()).setTime(System.currentTimeMillis());
			((Date) txtPayChequeDate.getValue()).setTime(System
					.currentTimeMillis());
			((Date) txtPayExpiryDate.getValue()).setTime(System
					.currentTimeMillis());
			txtPayKeyfld.setEnabled(true);
			txtLocation.setValue(utilsVaadin.findByValue(txtLocation,
					Channelplus3Application.getInstance().getFrmUserLogin()
							.getMapVars().get("DEFAULT_LOCATION")));

			if (QRYSES.isEmpty()) {
				txtPayKeyfld.setValue(QueryExe.getSqlValue(
						"select NVL(max(keyfld),0)+1 from lg_pays ", con, "1"));
			} else {
				txtPayKeyfld.setEnabled(false);
				QueryExe qe = new QueryExe(
						"select *from lg_pays where keyfld='" + QRYSES + "'",
						con);
				ResultSet rs = qe.executeRS();
				if (rs != null && rs.first()) {
					utilsVaadin.assignValues(rs, lstfldPays);
					txtPayEmpDrAc.setDisplay(QueryExe.getSqlValue(
							"select name from acaccount where accno='"
									+ txtPayEmpDrAc.getValue() + "'", con, "")
							.toString());
					txtPayMainCrAc.setDisplay(QueryExe.getSqlValue(
							"select name from acaccount where accno='"
									+ txtPayMainCrAc.getValue() + "'", con, "")
							.toString());
				}
				qe.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("Err Saving ! ",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);

		}
	}

	@Override
	public void print_data() {
		if (varErrorOnSave)
			return;
		if (listTargetMode.get(0).equals(MODE_PAYMENTS))
			try {
				utilsVaadinPrintHandler.printLGFRPayment(
						Double.valueOf(varPrintQRYSES), con);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private void show_item_for_rcpt_list() {
		try {
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);

			utilsVaadin.showSearch(
					la,
					new SelectionListener() {
						public void onSelection(TableView tv) {
							Channelplus3Application.getInstance()
									.getMainWindow().removeWindow(wnd);

							if (tv.getSelectionValue() > -1) {
								try {
									int rn = tv.getSelectionValue();
									String rfr = tv.getData()
											.getFieldValue(rn, "REFERENCE")
											.toString();
									String descr = tv.getData()
											.getFieldValue(rn, "DESCR")
											.toString();
									int r = tbl_rcpt1.data.getRowCount() - 1;
									if (r >= 0) {
										tbl_rcpt1.data.setFieldValue(r,
												"ITEM_REFER", rfr);
										tbl_rcpt1.data.setFieldValue(r,
												"DESCR", descr);

										tbl_rcpt1.refreshRow(r);
									}

								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}
					},
					con,
					" SELECT REFERENCE,DESCR FROM ITEMS "
							+ " where childcounts=0 and parentitem='0102' and reference in "
							+ " (select cost_item from lg_custitems where code='"
							+ QRYSES_ON_ORD_REF + "' )" + "  ORDER BY DESCR2",
					true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
