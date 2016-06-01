package com.windows.LG;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.example.components.SearchField;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.FormLayoutManager;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

public class frmDocs implements transactionalForm {
	
	private AbstractOrderedLayout parentLayout = null;
	private List<FieldInfo> lstfldDocs = new ArrayList<FieldInfo>();	
	private List<FieldInfo> lstfldTrans = new ArrayList<FieldInfo>();
	public List<String> listTargetMode = new ArrayList<String>();
	
	private java.sql.Connection con = null;
	private boolean listnerAdded = false;
	
	private Panel mainPanel = new Panel(new HorizontalLayout());
	private Panel tbSheetPanel = new Panel(new VerticalLayout());
	private Panel pnlList = new Panel(new VerticalLayout());
	private TabSheet tbs = new TabSheet();

	public final String MODE_MAIN_SCREEN = "@";	
	public final String MODE_ISSUE_DOCS = "ISSUE_DOCS";
	public final String MODE_DOC_PAYMENTS = "PAYMENTS";
	public final String MODE_DOC_RECIPTS = "RECIPTS";
	public final String MODE_CLOSE_DOC = "CLOSE_DOC";
	
	public final String MODE_SEL_DOC = "SEL_DOC";
	public final String MODE_SEL_DOC_PAY = "SEL_DOC_PAY";
	public final String MODE_SEL_DOC_REC = "SEL_DOC_REC";

	public final String MODE_FORM_DOCS = "FORM_DOC";
	public final String MODE_FORM_PAYMENTS = "FORM_PAYMENT";
	public final String MODE_FORM_RECIPTS = "FORM_RECIPTS";
	public final String MODE_FORM_CLOSE = "CLOSE";

	private double varSelectedDocKeyfld = -1;
	private double varSelectedDocPayKeyfld = -1;
	
	private double varTVouCode = 2;
	private double varTType = 1;

	
	public String QRYSES = "";
	public String QRYSES_TRANS = "";
	public String QRYSES_ON = "";
	public String QRYSES_ON_ORD_REF = "";
	public String QRYSES_ON_ORD_REFNM = "";

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private Panel pnlDoc = new Panel(new FormLayoutManager());
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout dutyTimeLayout = new VerticalLayout();
	private HorizontalLayout basicLayout = new HorizontalLayout();
	private FormLayoutManager basicInfoLayout = new FormLayoutManager("100%",
			"-1px");
	private Panel infoPanel = new Panel(new FormLayoutManager("100%", "-1px"));
	public String mode_current = "@";
	
	private Label lbl_keyfld = new Label();

	private TextField txtTotPay = new TextField();
	private TextField txtTotRecipt = new TextField();
	private TextField txtTotDue = new TextField();

	private ComboBox txtDocKind = ControlsFactory.CreateListField(null,
			"DOC_KIND", "#LG_CLEARANCE_DOCS", lstfldDocs);

	private TextField txtDocNo = ControlsFactory.CreateTextField(null,
			"DOC_NO", lstfldDocs, "100%", "");

	private TextField txtDescr = ControlsFactory.CreateTextField(null, "DESCR",
			lstfldDocs, "100%", "");
	private DateField txtIssueDate = ControlsFactory.CreateDateField(null,
			"DATE_ISSUE", lstfldDocs, "100%", utils.FORMAT_SHORT_DATE, null);
	private DateField txtRetDate = ControlsFactory.CreateDateField(null,
			"DATE_PREDICT_RECIEVE", lstfldDocs, "100%",
			utils.FORMAT_SHORT_DATE, null);
	private DateField txtDocExpiry = ControlsFactory.CreateDateField(null,
			"DOC_EXPIRY_DATE", lstfldDocs, "100%", utils.FORMAT_SHORT_DATE,
			null);

	private SearchField txtItem = ControlsFactory.CreateSearchField(null,
			"ITEM_CODE", lstfldDocs, "100%",
			"select reference,descr from items " + "order by descr2",
			"REFERENCE", "DESCR");	

	private SearchField txtPersonNo = ControlsFactory
			.CreateSearchField(null, "PERSON_NO", lstfldDocs, "100%",
					"select no,name from salesp where flag=1 order by no",
					"NO", "NAME");

	private SearchField txtCashAc = ControlsFactory
			.CreateSearchField(
					null,
					"CASH_AC",
					lstfldDocs,
					"100%",
					"select accno,name from acaccount where actype=0 and flag=1 and childcount=0 order by path",
					"ACCNO", "NAME");
	private SearchField txtAssetsGrpAc = ControlsFactory
			.CreateSearchField(
					null,
					"ASSET_AC",
					lstfldDocs,
					"100%",
					"select accno , name from acaccount where actype=0 and childcount=0 order by path",
					"ACCNO", "NAME");
	private TextField txtPayLimit = ControlsFactory
			.CreateTextField(null, "PAY_LIMIT", lstfldDocs, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);

	private TextField txtApproxDue = ControlsFactory
			.CreateTextField(null, "APPROX_DUE", lstfldDocs, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);
	private TextField txtTVouNo = ControlsFactory.CreateTextField(null,
			"VOU_NO", lstfldTrans, "0", Parameter.DATA_TYPE_NUMBER,
			Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_QTY,
			false);	
	private DateField txtTDate = ControlsFactory.CreateDateField(null,
			"VOU_DATE", lstfldTrans, "100%", utils.FORMAT_SHORT_DATE, null);

	private TextField txtTDescr = ControlsFactory.CreateTextField(null,
			"DESCR", lstfldTrans, "100%", "");	

	private SearchField txtTCreditAc = ControlsFactory
			.CreateSearchField(
					null,
					"CREDIT_AC",
					lstfldTrans,
					"100%",
					"select accno , name from acaccount where actype=0 and childcount=0 order by path",
					"ACCNO", "NAME");	
	private SearchField txtTDebitAc = ControlsFactory
			.CreateSearchField(
					null,
					"DEBIT_AC",
					lstfldTrans,
					"100%",
					"select accno , name from acaccount where actype=0 and childcount=0 order by path",
					"ACCNO", "NAME");

	private TextField txtTDebit = ControlsFactory
			.CreateTextField(null, "DEBIT", lstfldTrans, "0",
					Parameter.DATA_TYPE_NUMBER, Channelplus3Application
							.getInstance().getFrmUserLogin().FORMAT_MONEY,
					false);

	private NativeButton cmdIssueDocs = ControlsFactory.CreateCustomButton(
			"Issue document", "img/radio_button.png", "Issue Document",
			"link title3", new ClickListener() {

				public void buttonClick(ClickEvent event) {
					listTargetMode.clear();
					QRYSES = "";
					varSelectedDocKeyfld = -1;
					listTargetMode.add(MODE_ISSUE_DOCS);
					listTargetMode.add(MODE_SEL_DOC);
					listTargetMode.add(MODE_FORM_DOCS);
					setCurrentMode(MODE_SEL_DOC);
				}
			});
	private NativeButton cmdPayments = ControlsFactory.CreateCustomButton(

	"Payments", "img/radio_button.png", "Payments on Document", "link title3",
			new ClickListener() {

				public void buttonClick(ClickEvent event) {
					listTargetMode.clear();
					varSelectedDocKeyfld = -1;
					QRYSES = "";
					QRYSES_TRANS = "";
					listTargetMode.add(MODE_DOC_PAYMENTS);
					listTargetMode.add(MODE_SEL_DOC);
					listTargetMode.add(MODE_SEL_DOC_PAY);
					listTargetMode.add(MODE_FORM_PAYMENTS);
					setCurrentMode(MODE_SEL_DOC);

				}
			});

	private NativeButton cmdRecipts = ControlsFactory.CreateCustomButton(
			"Payments", "img/radio_button.png", "Recipts on Document",
			"link title3", new ClickListener() {

				public void buttonClick(ClickEvent event) {
					listTargetMode.clear();
					varSelectedDocKeyfld = -1;
					QRYSES = "";
					QRYSES_TRANS = "";
					listTargetMode.add(MODE_DOC_RECIPTS);
					listTargetMode.add(MODE_SEL_DOC);
					listTargetMode.add(MODE_SEL_DOC_REC);
					listTargetMode.add(MODE_FORM_RECIPTS);
					setCurrentMode(MODE_SEL_DOC);

				}
			});
	private NativeButton cmdCloseDocuments = ControlsFactory
			.CreateCustomButton("Close documents", "img/radio_button.png",
					"Close  Document", "link title3", new ClickListener() {

						public void buttonClick(ClickEvent event) {
							listTargetMode.clear();
							varSelectedDocKeyfld = -1;
							QRYSES = "";
							listTargetMode.add(MODE_CLOSE_DOC);
							listTargetMode.add(MODE_SEL_DOC);
							listTargetMode.add(MODE_FORM_CLOSE);
							setCurrentMode(MODE_SEL_DOC);
						}
					});

	private NativeButton cmdPurchaseDocuments = ControlsFactory
			.CreateCustomButton("Purchase documents", "img/radio_button.png",
					"Purchase Document", "link title3", new ClickListener() {
						public void buttonClick(ClickEvent event) {
							listTargetMode.clear();
							varSelectedDocKeyfld = -1;
							QRYSES = "";
							// listTargetMode.add(MODE_ISSUE_DOCS);
							// listTargetMode.add(MODE_SEL_DOC);
							// setCurrentMode(MODE_SEL_DOC);

						}
					});

	private void setPriorMode() {
		int n = listTargetMode.indexOf(mode_current);
		if (n == 0)
			return;
		setCurrentMode(listTargetMode.get(n - 1));
	}

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

	public void save_data() {
		String mod = listTargetMode.get(0);
		try {
			con.setAutoCommit(false);
			if (mod.equals(MODE_ISSUE_DOCS))
				save_docs(true);
			if (mod.equals(MODE_DOC_PAYMENTS) || mod.equals(MODE_DOC_RECIPTS))
				save_docs_pay(true);
			if (mod.equals(MODE_CLOSE_DOC))
				save_docs_close(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("Err Saving ! ",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}
	}

	private void save_docs_close(boolean cls) throws Exception {
		validate_data();
		DecimalFormat dcf = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);
		double tp = Double.valueOf(utils.getSqlValue(
				"select nvl(sum(debit),0) from lg_docs_trans where vou_code=3 and doc_keyfld="
						+ QRYSES, con));
		double tr = Double.valueOf(utils.getSqlValue(
				"select nvl(sum(debit),0) from lg_docs_trans where vou_code=2 and doc_keyfld="
						+ QRYSES, con));
		double t = tp - tr;

		QueryExe qe = new QueryExe(
				"begin update lg_docs "
						+ " set close_due= :CLOSE_DUE , CLOSE_DEBIT_AC = :CLOSE_DEBIT_AC ,"
						+ "  CLOSE_CREDIT_AC = :CLOSE_CREDIT_AC ,  CLOSE_DATE = :CLOSE_DATE , CLOSE_DESCR=:CLOSE_DESCR"
						+ "  where keyfld = :KEYFLD ;"
						+ "  x_lg_close( :KEYFLD ) ;   end;", con);
		qe.setParaValue("CLOSE_DUE", t);
		qe.setParaValue("CLOSE_DEBIT_AC", txtTDebitAc.getValue());
		qe.setParaValue("CLOSE_CREDIT_AC", txtTCreditAc.getValue());
		qe.setParaValue("CLOSE_DATE", txtIssueDate.getValue());
		qe.setParaValue("CLOSE_DESCR", txtDescr.getValue());
		qe.setParaValue("KEYFLD", QRYSES);

		qe.execute();
		con.commit();
		parentLayout.getWindow().showNotification("Closed Successfully");
		if (cls)
			cmdCancel.click();

	}

	private void save_docs_pay(boolean cls) throws Exception {
		validate_data();
		if (QRYSES.isEmpty())
			throw new Exception("No documents assigned ! error");
		Map<String, Object> mapVarDBs = new HashMap<String, Object>();
		String kf = (QRYSES_TRANS.isEmpty() ? utils.getSqlValue(
				"select nvl(max(keyfld),0)+1 from lg_docs_trans", con)
				: QRYSES_TRANS);
		mapVarDBs.put("KEYFLD", kf);
		mapVarDBs.put("DOC_KEYFLD", QRYSES);
		mapVarDBs.put("JOB_ORD_NO", QRYSES_ON);
		mapVarDBs.put("VOU_CODE", varTVouCode);
		mapVarDBs.put("VOU_TYPE", varTType);
		mapVarDBs.put("CREDIT", txtTDebit.getData());
		QueryExe qea = new QueryExe(con);
		utilsVaadin.setListToQueryExe(lstfldTrans, qea, mapVarDBs);
		if (QRYSES_TRANS.isEmpty())
			qea.AutoGenerateInsertStatment("LG_DOCS_TRANS");
		else
			qea.AutoGenerateUpdateStatment("LG_DOCS_TRANS", "'KEYFLD'",
					" WHERE KEYFLD = :KEYFLD ");
		qea.execute();
		qea.close();
		qea = new QueryExe("begin X_LG_POST_TRANS( :KEYFLD ) ;  end; ", con);
		qea.setParaValue("KEYFLD", QRYSES_TRANS);
		qea.execute();
		qea.close();
		con.commit();
		parentLayout.getWindow().showNotification("Saved Successfully");
		if (cls)
			cmdCancel.click();

	}

	private void save_docs(boolean cls) throws Exception {
		validate_data();
		Map<String, Object> mapVarDBs = new HashMap<String, Object>();
		if (QRYSES.isEmpty())
			varSelectedDocKeyfld = Double.valueOf(utils.getSqlValue(
					"Select nvl(max(keyfld),0)+1 from lg_docs", con));
		mapVarDBs.put("KEYFLD", varSelectedDocKeyfld);
		mapVarDBs.put("JOB_ORD_NO", QRYSES_ON);
		QueryExe qea = new QueryExe(con);
		utilsVaadin.setListToQueryExe(lstfldDocs, qea, mapVarDBs);
		if (QRYSES.isEmpty())
			qea.AutoGenerateInsertStatment("LG_DOCS");
		else
			qea.AutoGenerateUpdateStatment("LG_DOCS", "'KEYFLD'",
					" WHERE KEYFLD = :KEYFLD ");
		qea.execute();
		qea.close();
		con.commit();
		parentLayout.getWindow().showNotification("Saved Successfully");
		if (cls)
			cmdCancel.click();

	}

	public void validate_data() throws Exception {
		if (listTargetMode.get(0).equals(MODE_ISSUE_DOCS)) {
			if (txtAssetsGrpAc.getValue() == null
					|| txtAssetsGrpAc.getValue().toString().isEmpty())
				throw new Exception("Asset a/c have not assigned !");
			if (txtItem.getValue() == null
					|| txtItem.getValue().toString().isEmpty())
				throw new Exception("Asset a/c have not assigned !");
			if (txtCashAc.getValue() == null
					|| txtCashAc.getValue().toString().isEmpty())
				throw new Exception("Cash a/c have not assigned !");

		}
	}

	public void load_data() {
		try {
			if (mode_current.equals(MODE_FORM_DOCS))
				load_data_docs();
			if (mode_current.equals(MODE_FORM_PAYMENTS)
					|| mode_current.equals(MODE_FORM_RECIPTS))
				load_data_trans();
			if (mode_current.equals(MODE_FORM_CLOSE))
				load_close_data();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("Err Loading ! ",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void load_close_data() throws Exception {
		if (txtIssueDate.getValue() == null)
			txtIssueDate.setValue(new Date());
		((Date) txtIssueDate.getValue()).setTime(System.currentTimeMillis());
		DecimalFormat dcf = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);
		double tp = Double.valueOf(utils.getSqlValue(
				"select nvl(sum(debit),0) from lg_docs_trans where vou_code=3 and doc_keyfld="
						+ QRYSES, con));
		double tr = Double.valueOf(utils.getSqlValue(
				"select nvl(sum(debit),0) from lg_docs_trans where vou_code=2 and doc_keyfld="
						+ QRYSES, con));
		double t = tp - tr;
		utilsVaadin.setValueByForce(txtTotPay, dcf.format(tp));
		utilsVaadin.setValueByForce(txtTotRecipt, dcf.format(tr));
		utilsVaadin.setValueByForce(txtTotDue, dcf.format(t));
		QueryExe qe = new QueryExe("select *from lg_docs where keyfld="
				+ QRYSES, con);
		ResultSet rs = qe.executeRS();
		if (rs != null && rs.first()) {

			utilsVaadin.setValueByForce(txtDescr, "Job Ord # " + QRYSES_ON
					+ " , " + rs.getString("doc_kind"));
			utilsVaadin.setValueByForce(txtTDescr, rs.getString("doc_kind"));
		}
		qe.close();

		qe = new QueryExe("select ld.* , ast.name as_nm,ac.name ac_nm "
				+ " from lg_docs ld,acaccount ast,acaccount ac "
				+ " where ld.keyfld='" + QRYSES
				+ "' and ast.accno=ld.ASSET_AC and ac.accno=ld.cash_ac", con);
		rs = qe.executeRS();
		if (rs != null && rs.first()) {
			txtTDebitAc.setDisplayValue(rs.getString("cash_ac"),
					rs.getString("ac_nm"));
			txtTCreditAc.setDisplayValue(rs.getString("asset_ac"),
					rs.getString("as_nm"));
		}
		qe.close();
	}

	private void load_data_trans() throws Exception {
		utilsVaadin.setDefaultValues(lstfldTrans, true);

		if (txtTDate.getValue() == null)
			txtTDate.setValue(new Date());
		((Date) txtTDate.getValue()).setTime(System.currentTimeMillis());
		if (listTargetMode.get(0).equals(MODE_DOC_PAYMENTS))
			varTVouCode = 3;
		else
			varTVouCode = 2;
		txtTVouNo.setEnabled(true);
		varTType = Double.valueOf(utils.nvl(
				Channelplus3Application.getInstance().getFrmUserLogin()
						.getMapVars().get("LG_JV_TYPE_PAYMENTS"), "3"));
		String mainCashAc = utils.nvl(Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars().get("LG_MAIN_CASH_AC"), "");

		if (QRYSES_TRANS.isEmpty()) {
			QueryExe qx = new QueryExe(
					"select ld.doc_kind,ld.cash_ac,ac.name from lg_docs ld,acaccount ac"
							+ "  where ld.cash_ac=ac.accno and ld.keyfld='"
							+ QRYSES + "'", con);
			ResultSet rsx = qx.executeRS();
			if (rsx == null || !rsx.first())
				throw new Exception("Document have been removed !");
			rsx.first();
			txtTDescr.setValue("Job Ord # " + QRYSES_ON + " , "
					+ rsx.getString("doc_kind"));
			((varTVouCode == 2 ? txtTCreditAc : txtTDebitAc)).setDisplayValue(
					rsx.getString("cash_ac"), rsx.getString("name"));
			qx.close();

			if (!mainCashAc.isEmpty()) {
				QueryExe qe = new QueryExe(
						"select accno,name from acaccount where  accno='"
								+ mainCashAc + "'", con);
				ResultSet rs = qe.executeRS();
				if (rs != null && rs.first())
					((varTVouCode == 2 ? txtTDebitAc : txtTCreditAc))
							.setDisplayValue(rs.getString("ACCNO"),
									rs.getString("NAME"));
				qe.close();
			}

			txtTVouNo.setValue(utils.getSqlValue(
					"select nvl(max(vou_no),0)+1 from lg_docs_trans where vou_code='"
							+ varTVouCode + "'", con));

		} else {
			QueryExe qe = new QueryExe("select t.* , ad.name dnm,ac.name cnm "
					+ " from lg_docs_trans t,acaccount ad,acaccount ac "
					+ " where t.keyfld='" + QRYSES_TRANS
					+ "' and ad.accno=t.debit_ac and ac.accno=t.credit_ac ",
					con);
			ResultSet rs = qe.executeRS();
			if (rs != null && rs.first()) {
				utilsVaadin.assignValues(rs, lstfldTrans);
				txtTDebitAc.setDisplayValue(rs.getString("debit_ac"),
						rs.getString("dnm"));
				txtTCreditAc.setDisplayValue(rs.getString("credit_ac"),
						rs.getString("cnm"));
				txtTVouNo.setEnabled(false);
			}
			qe.close();

		}

	}

	public void load_data_docs() throws Exception {
		utilsVaadin.setDefaultValues(lstfldDocs, true);
		if (txtIssueDate.getValue() == null)
			txtIssueDate.setValue(new Date());
		if (txtDocExpiry.getValue() == null)
			txtDocExpiry.setValue(new Date());
		if (txtRetDate.getValue() == null)
			txtRetDate.setValue(new Date());
		((Date) txtRetDate.getValue()).setTime(System.currentTimeMillis());
		((Date) txtDocExpiry.getValue()).setTime(System.currentTimeMillis());
		((Date) txtRetDate.getValue()).setTime(System.currentTimeMillis());
		if (!QRYSES.isEmpty()) {
			QueryExe qe = new QueryExe(
					"select ld.*,a1.name cash_name,a2.name ast_name,i.descr itm_name "
							+ " from lg_docs ld,acaccount a1,acaccount a2,items i where ld.keyfld="
							+ QRYSES + " and  i.reference=ld.item_code and "
							+ " a1.accno=ld.cash_ac and a2.accno=ld.asset_ac  ",
					con);
			ResultSet rs = qe.executeRS();
			if (rs == null || !rs.first()) {
				qe.close();
				throw new Exception(
						"Either another user have removed record or database error !");
			}
			rs.first();
			utilsVaadin.assignValues(rs, lstfldDocs);
			txtCashAc.setDisplay(rs.getString("cash_name"));
			txtAssetsGrpAc.setDisplay(rs.getString("ast_name"));
			lbl_keyfld.setValue(QRYSES);
			qe.close();
		} else {
			varSelectedDocKeyfld = -1;
		}

	}

	public void print_data() {

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

	public void createView() {

		switch (mode_current) {
		case MODE_MAIN_SCREEN:
			createView_main_screen();
			break;
		case MODE_SEL_DOC:
			show_doc_sel();
			break;
		case MODE_SEL_DOC_PAY:
			show_doc_sel_trans();
			break;
		case MODE_SEL_DOC_REC:
			show_doc_sel_trans();
			break;
		case MODE_FORM_DOCS:
			show_doc_form();
			break;
		case MODE_FORM_PAYMENTS:
			show_pay_form();
			break;
		case MODE_FORM_RECIPTS:
			show_recipt_form();
			break;
		case MODE_FORM_CLOSE:
			show_doc_close();
			break;

		default:
			break;
		}
	}

	private void show_doc_close() {
		resetLayout();
		tbs.addTab(pnlDoc, "Payments ..");
		tbs.setSelectedTab(pnlDoc);

		cmdFinish.setEnabled(true);
		cmdBack.setEnabled(true);
		cmdNext.setEnabled(false);
		cmdCancel.setEnabled(true);

		FormLayoutManager f = (FormLayoutManager) pnlDoc.getContent();
		f.setMargin(true);
		f.setSpacing(true);
		f.removeAll();
		f.addComponentsRow("style=title1,caption=Document : " + QRYSES + "");
		f.addComponentsRow("caption=Doc Kind,width=100%,expand=.5", txtTDescr,
				"expand=3.5,width=100%,readonly=true");
		f.addComponentsRow("caption=.,height=15px");

		f.addComponentsRow(
				"caption=Total pay,width=100%,align=start,expand=.5",
				txtTotPay,
				"width=100%,expand=1.5,readonly=true,align_text=end",
				"caption=.,expand=2,width=100%");
		f.addComponentsRow(
				"caption=Total Recieved,width=100%,align=start,expand=.5",
				txtTotRecipt,
				"width=100%,expand=1.5,readonly=true,align_text=end",
				"caption=.,expand=2,width=100%");

		f.addLine();

		f.addComponentsRow("caption=Due,width=100%,align=start,expand=.5",
				txtTotDue,
				"expand=1.5,readonly=true,style=yellowField,align_text=end",
				"caption=.,expand=2,width=100%");
		f.addLine();
		f.addComponentsRow("caption=Closing Jv");
		f.addComponentsRow("caption=Close date,width=100%,expand=.5",
				txtIssueDate, "expand=1.5,width=100%",
				"caption=.,expand=2,width=100%");
		f.addComponentsRow("caption=Doc Descr,width=100%,expand=.5", txtDescr,
				"expand=1.5,width=100%,readonly=false",
				"caption=.,expand=2,width=100%");
		f.addComponentsRow(
				"caption=Debit A/c,width=100%,align=start,expand=.5",
				txtTDebitAc, "expand=1.5,width=100%",
				"caption=.,expand=2,width=100%");

		f.addComponentsRow(
				"caption=Credit A/c,width=100%,align=start,expand=.5",
				txtTCreditAc, "expand=1.5,width=100%",
				"caption=.,expand=2,width=100%");

	}

	private void show_pay_form() {
		resetLayout();
		tbs.addTab(pnlDoc, "Payments ..");
		tbs.setSelectedTab(pnlDoc);

		cmdFinish.setEnabled(true);
		cmdNext.setEnabled(false);
		cmdBack.setEnabled(true);
		cmdCancel.setEnabled(true);
		FormLayoutManager f = (FormLayoutManager) pnlDoc.getContent();
		f.setMargin(true);
		f.setSpacing(true);
		f.removeAll();
		f.addComponentsRow("style=title1,caption=Document : " + QRYSES + "");
		if (QRYSES_TRANS.isEmpty())
			f.addComponentsRow("style=title1,caption= New Payment : ",
					lbl_keyfld, "style=title1");
		else
			f.addComponentsRow("caption=Edit Payment # " + QRYSES_TRANS);
		add_trans_form_fields();
	}

	private void show_recipt_form() {
		resetLayout();
		tbs.addTab(pnlDoc, "Recipts ..");
		tbs.setSelectedTab(pnlDoc);

		cmdFinish.setEnabled(true);
		cmdBack.setEnabled(true);
		cmdNext.setEnabled(false);
		cmdCancel.setEnabled(true);

		FormLayoutManager f = (FormLayoutManager) pnlDoc.getContent();
		f.setMargin(true);
		f.removeAll();
		f.addComponentsRow("style=title1,caption=Document : " + QRYSES + "");
		if (QRYSES_TRANS.isEmpty())
			f.addComponentsRow("style=title1,caption= New Recipt : ",
					lbl_keyfld, "style=title1");
		else
			f.addComponentsRow("caption=Edit Recipts # " + QRYSES_TRANS);
		add_trans_form_fields();
	}

	private void add_trans_form_fields() {
		FormLayoutManager f = (FormLayoutManager) pnlDoc.getContent();
		f.addComponentsRow("caption=Vou No,width=100%,align=end,expand=.5",
				txtTVouNo, "expand=1.5,width=100%",
				"caption=Date,align=end,width=100%,expand=.5", txtTDate,
				"expand=1.5,width=100%");

		f.addComponentsRow("caption=Descr,width=100%,expand=.5", txtTDescr,
				"expand=3.5,width=100%,readonly=false");
		f.addComponentsRow(
				"caption=Debit A/c,width=100%,align=start,expand=.5",
				txtTDebitAc, "expand=3.5,width=100%");

		f.addComponentsRow(
				"caption=Credit A/c,width=100%,align=start,expand=.5",
				txtTCreditAc, "expand=3.5,width=100%");

		f.addComponentsRow("caption=.,height=15px");

		f.addComponentsRow("caption=Amountc,width=100px,align=center,expand=4");
		f.addComponentsRow(txtTDebit,
				"expand=4,width=100px,align=center,style=yellowField");

	}

	private void show_doc_form() {
		resetLayout();
		tbs.addTab(pnlDoc, "Select Document ..");
		tbs.setSelectedTab(pnlDoc);

		cmdFinish.setEnabled(true);
		cmdBack.setEnabled(true);
		cmdNext.setEnabled(false);
		cmdCancel.setEnabled(true);

		FormLayoutManager f = (FormLayoutManager) pnlDoc.getContent();
		f.setMargin(true);
		f.removeAll();
		if (QRYSES.isEmpty())
			f.addComponentsRow("style=title1,caption=Enter New Document : ",
					lbl_keyfld, "style=title1");
		else
			f.addComponentsRow("caption=Edit Document # ", lbl_keyfld);
		f.addComponentsRow("caption=.,height=5px");
		f.addComponentsRow("caption=Doc Kind ,width=100%,align=end,expand=.5",
				txtDocKind, "expand=1.5,width=100%",
				"caption=Doc No #,align=end,width=100%,expand=.5", txtDocNo,
				"expand=1.5,width=100%");
		f.addComponentsRow("caption=Issue date,width=100%,align=end,expand=.5",
				txtIssueDate, "expand=1.5,width=100%",
				"caption=Predict Return,align=end,width=100%,expand=.5",
				txtRetDate, "expand=1.5,width=100%");
		f.addComponentsRow("caption=Person,width=100%,align=end,expand=.5",
				txtPersonNo, "expand=3.5,width=100%");
		f.addComponentsRow("caption=Cash A/c,width=100%,align=end,expand=.5",
				txtCashAc, "expand=3.5,width=100%");
		f.addComponentsRow("caption=Asset A/c,width=100%,align=end,expand=.5",
				txtAssetsGrpAc, "expand=3.5,width=100%");
		f.addComponentsRow("caption=Item,width=100%,align=end,expand=.5",
				txtItem, "expand=3.5,width=100%");
		f.addComponentsRow("caption=Pay Limit,width=100%,align=end,expand=.5",
				txtPayLimit, "expand=1.5,width=100%",
				"caption=Predict Return,align=end,width=100%,expand=.5",
				txtApproxDue, "expand=1.5,width=100%");
		f.addComponentsRow("caption=Descr,width=100%,expand=.5", txtDescr,
				"expand=3.5,width=100%,readonly=false");

		f.addComponentsRow("caption=.,width=100%,expand=2",
				"caption=Expiry width=100%,align=end,expand=.5", txtDocExpiry,
				"expand=1.5,width=100%");

	}

	private void show_doc_sel_trans() {
		resetLayout();
		tbs.addTab(pnlList, "Select Transaction Voucher ..");
		tbs.setSelectedTab(pnlList);

		cmdFinish.setEnabled(false);
		cmdBack.setEnabled(false);
		cmdNext.setEnabled(true);
		cmdCancel.setEnabled(true);

		try {
			SelectionListener sel = new SelectionListener() {
				@Override
				public void onSelection(TableView tv) {
					varSelectedDocPayKeyfld = -1;
					QRYSES_TRANS = "";

					if (tv.getTable().getValue() == null
							|| tv.getSelectionValue() < 0)
						return;
					int ln = tv.getSelectionValue();
					varSelectedDocPayKeyfld = ((Number) tv.getData()
							.getFieldValue(ln, "KEYFLD")).doubleValue();
					QRYSES_TRANS = tv.getData().getFieldValue(ln, "KEYFLD")
							.toString();
				}
			};
			int vc = (listTargetMode.get(0).equals(MODE_DOC_PAYMENTS) ? 3 : 2);
			utilsVaadin.showSearch((VerticalLayout) pnlList.getContent(), sel,
					con,
					"select vou_no,descr,vou_date,keyfld from LG_DOCS_TRANS where vou_code="
							+ vc + " and flag=2 AND JOB_ORD_NO='" + QRYSES_ON
							+ "' " + " and doc_keyfld='" + QRYSES + "'", true);

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void show_doc_sel() {
		resetLayout();
		tbs.addTab(pnlList, "Select Document ..");
		tbs.setSelectedTab(pnlList);

		cmdFinish.setEnabled(false);
		cmdBack.setEnabled(false);
		cmdNext.setEnabled(true);
		cmdCancel.setEnabled(true);

		if (listTargetMode.get(0).equals(MODE_DOC_PAYMENTS)
				|| listTargetMode.get(0).equals(MODE_DOC_RECIPTS))
			cmdNext.setEnabled(false);
		try {
			SelectionListener sel = new SelectionListener() {
				@Override
				public void onSelection(TableView tv) {
					varSelectedDocKeyfld = -1;
					QRYSES = "";

					if (tv.getTable().getValue() == null
							|| tv.getSelectionValue() < 0)
						return;
					int ln = tv.getSelectionValue();
					varSelectedDocKeyfld = ((Number) tv.getData()
							.getFieldValue(ln, "KEYFLD")).doubleValue();
					QRYSES = tv.getData().getFieldValue(ln, "KEYFLD")
							.toString();

					if ((listTargetMode.get(0).equals(MODE_DOC_PAYMENTS) || listTargetMode
							.get(0).equals(MODE_DOC_RECIPTS))
							&& !QRYSES.isEmpty())
						cmdNext.setEnabled(true);
				}
			};

			utilsVaadin
					.showSearch(
							(VerticalLayout) pnlList.getContent(),
							sel,
							con,
							"select DOC_NO,descr,date_issue,keyfld from LG_DOCS where flag=1 AND JOB_ORD_NO='"
									+ QRYSES_ON + "'", true);

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void createView_main_screen() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetLayout();
		centralPanel.setSizeFull();
		mainPanel.setWidth("700px");
		mainPanel.setHeight("600px");
		mainPanel.setContent(mainLayout);

		tbSheetPanel.setContent(basicLayout);

		tbSheetPanel.setSizeFull();
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);

		basicInfoLayout.setHeight("-1px");
		basicInfoLayout.setWidth("100%");

		mainPanel.addStyleName("formLayout");
		tbSheetPanel.addStyleName("formLayout");

		tbs.addTab(tbSheetPanel, "Main Screen");
		tbs.setSizeFull();

		mainLayout.addStyleName("formLayout");
		basicInfoLayout.addStyleName("formLayout");
		basicLayout.addStyleName("formLayout");
		tbSheetPanel.addStyleName("formLayout");

		pnlDoc.setSizeFull();
		pnlList.setSizeFull();
		basicLayout.setSizeFull();
		infoPanel.setSizeFull();

		((FormLayoutManager) pnlDoc.getContent()).setSpacing(true);

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

		basicInfoLayout.addComponentsRow("caption=.,height=5px");
		String str = "Select a task JOB ORDER # " + QRYSES_ON + " - "
				+ QRYSES_ON_ORD_REF + " / " + QRYSES_ON_ORD_REFNM;
		parentLayout.getWindow().setCaption(str);
		basicInfoLayout.addComponentsRow("caption=" + str
				+ " ,width=-1px,align=start,style=title1,height=20px");
		basicInfoLayout.addComponentsRow(cmdIssueDocs,
				"caption=Issue a new document,width=-1px");
		basicInfoLayout.addComponentsRow(cmdPayments,
				"caption=Payments,width=-1px");
		basicInfoLayout.addComponentsRow(cmdRecipts,
				"caption=Receipts,width=-1px");
		basicInfoLayout.addComponentsRow(cmdCloseDocuments,
				"caption=Close/reciev document,width=-1px");
		basicInfoLayout.addComponentsRow("caption=.,width=-1px,height=15px");
		basicInfoLayout.addComponentsRow(cmdPurchaseDocuments,
				"caption=Invoicing with document,width=-1px");

		FormLayoutManager inf = (FormLayoutManager) infoPanel.getContent();
		inf.removeAll();
		inf.setSpacing(true);
		inf.setMargin(true);
		inf.addComponentsRow("caption=Summary for all");

		inf.addComponentsRow(
				"caption=Total pay,width=100%,align=start,expand=2", txtTotPay,
				"expand=2,readonly=true,align_text=end");
		inf.addComponentsRow(
				"caption=Total Recieved,width=100%,align=start,expand=2",
				txtTotRecipt, "expand=2,readonly=true,align_text=end");

		inf.addLine();

		inf.addComponentsRow("caption=Due,width=100%,align=start,expand=2",
				txtTotDue,
				"expand=2,readonly=true,style=yellowField,align_text=end");
		inf.addLine();
		inf.addComponentsRow(
				"caption=No Of Invoiced,width=100%,align=start,expand=2",
				new TextField(null, "0"),
				"expand=2,readonly=true,style=numberStyle,align_text=end");
		DecimalFormat dcf = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);
		double tp = Double.valueOf(utils.getSqlValue(
				"select nvl(sum(debit),0) from lg_docs_trans where vou_code=3 and job_ord_no="
						+ QRYSES_ON, con));
		double tr = Double.valueOf(utils.getSqlValue(
				"select nvl(sum(debit),0) from lg_docs_trans where vou_code=2 and job_ord_no="
						+ QRYSES_ON, con));
		double t = tp - tr;
		utilsVaadin.setValueByForce(txtTotPay, dcf.format(tp));
		utilsVaadin.setValueByForce(txtTotRecipt, dcf.format(tr));
		utilsVaadin.setValueByForce(txtTotDue, dcf.format(t));
		if (!listnerAdded) {
			listnerAdded = true;
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
		case MODE_SEL_DOC:
			pnlList.removeAllComponents();
			break;
		default:
			((FormLayoutManager) pnlDoc.getContent()).removeAll();			
			break;
		}
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

}
	