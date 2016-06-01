package com.windows.Masters.GL;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.InfoBox;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.menuItem;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmMasterCustomer implements transactionalForm {
	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();

	private localTableModel data_Items = new localTableModel();

	private final List<ColumnProperty> lstBranchCols = new ArrayList<ColumnProperty>();
	private final List<ColumnProperty> lstContractCols = new ArrayList<ColumnProperty>();

	private HorizontalLayout m_mainLayout = new HorizontalLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();
	private VerticalLayout other1Layout = new VerticalLayout();
	private VerticalLayout other2Layout = new VerticalLayout();
	private VerticalLayout branchesLayout = new VerticalLayout();
	private VerticalLayout contractedLayout = new VerticalLayout();
	private VerticalLayout branchTableLayout = new VerticalLayout();
	private VerticalLayout contractTableLayout = new VerticalLayout();

	TableLayoutVaadin tableBranches = new TableLayoutVaadin(branchTableLayout);
	TableLayoutVaadin tableContracted = new TableLayoutVaadin(
			contractTableLayout);

	private HorizontalLayout codeLayout = new HorizontalLayout();
	private HorizontalLayout kindLayout = new HorizontalLayout();
	private HorizontalLayout codeKindLayout = new HorizontalLayout();
	private HorizontalLayout nameELayout = new HorizontalLayout();
	private HorizontalLayout nameALayout = new HorizontalLayout();
	private HorizontalLayout nameLayout = new HorizontalLayout();
	private HorizontalLayout parentCustLayout = new HorizontalLayout();
	private HorizontalLayout otherLayout = new HorizontalLayout();
	private HorizontalLayout referLayout = new HorizontalLayout();
	private HorizontalLayout remarksLayout = new HorizontalLayout();
	private HorizontalLayout creditLimitLayout = new HorizontalLayout();
	private HorizontalLayout bankDetailsLayout = new HorizontalLayout();
	private HorizontalLayout salesPersonLayout = new HorizontalLayout();
	private HorizontalLayout marketingChainLayout = new HorizontalLayout();
	private HorizontalLayout licenseLayout = new HorizontalLayout();
	private HorizontalLayout accLayout = new HorizontalLayout();
	private HorizontalLayout typeLayout = new HorizontalLayout();
	private HorizontalLayout paymentTermsLayout = new HorizontalLayout();
	private HorizontalLayout telLayout = new HorizontalLayout();
	private HorizontalLayout contactLayout = new HorizontalLayout();
	private HorizontalLayout faxLayout = new HorizontalLayout();
	private HorizontalLayout pagerLayout = new HorizontalLayout();
	private HorizontalLayout areaLayout = new HorizontalLayout();
	private HorizontalLayout addressLayout = new HorizontalLayout();
	private HorizontalLayout emailLayout = new HorizontalLayout();
	private HorizontalLayout checkLayout = new HorizontalLayout();
	private HorizontalLayout bankCashLayout = new HorizontalLayout();
	private HorizontalLayout priceLayout = new HorizontalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout commissionLayout = new HorizontalLayout();
	private HorizontalLayout commissionAccLayout = new HorizontalLayout();
	private HorizontalLayout commissionRateLayout = new HorizontalLayout();
	private HorizontalLayout revenueAccLayout = new HorizontalLayout();

	private Label lblMessage = new Label("");
	private Label lblCode = new Label("Code :");
	private Label lblKind = new Label("Kind :");
	private Label lblnameE = new Label("Name E :");
	private Label lblnameA = new Label("Name Arb :");
	private Label lblParentCust = new Label("Parent Cust :");
	private Label lblRef = new Label("Reference :");
	private Label lblRemarks = new Label("Remarks :");
	private Label lblCreditLimit = new Label("Credit Limit :");
	private Label lblSalesPerson = new Label("Sale Per. :");
	private Label lblLicense = new Label("License :");
	private Label lblAcc = new Label("A/C :");
	private Label lblType = new Label("Type :");
	private Label lbltel = new Label("Telephone :");
	private Label lblFax = new Label("Fax :");
	private Label lblPager = new Label("Pager :");
	private Label lblArea = new Label("Area :");
	private Label lblAddress = new Label("Address :");
	private Label lblEmail = new Label("Email :");
	private Label lblPriceNo = new Label("Price No :");
	private Label emptyLabel = new Label("");
	private Label emptyLabel2 = new Label("");
	private Label lblCommissionAcc = new Label("Commission A/C");
	private Label lblCommissionRate = new Label("Commission Rate %");
	private Label lblRevenueAcc = new Label("Revenue A/C");

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");

	private NativeButton cmdParentAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearParentAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearAcc = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdCommAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearCommAcc = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdRevAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearRevAcc = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");

	private Panel tbSheetPanel = new Panel();

	public boolean show_tree_panel = true;
	public InfoBox custBrowse = null;
	private java.util.Map<String, String> mapActAccBrows = new HashMap<String, String>();

	private CheckBox chkLock = ControlsFactory.CreateCheckField(
			"Lock / Unlock", "flag", "1", "2", lstfldinfo);
	private CheckBox chkIsCustomer = ControlsFactory.CreateCheckField(
			"IsCustomer", "isCust", "Y", "N", lstfldinfo);
	private CheckBox chkBankCash = ControlsFactory.CreateCheckField(
			"Bank / Cash", "isBankCash", "Y", "N", lstfldinfo);
	private CheckBox chkSupplies = ControlsFactory.CreateCheckField("Supplier",
			"issupp", "Y", "N", lstfldinfo);

	private ComboBox cmbKind = ControlsFactory.CreateListField("", "etype", "",
			lstfldinfo);
	private ComboBox cmbSalePerson = ControlsFactory.CreateListField(null,
			"salesp", "", lstfldinfo);
	private ComboBox cmbType = ControlsFactory.CreateListField(null, "type",
			"", lstfldinfo);
	private ComboBox cmbMarketingChain = ControlsFactory.CreateListField(
			"Marketing Chain", "bcust", "", lstfldinfo);
	private ComboBox cmbPaymentTerms = ControlsFactory.CreateListField(
			"Payment Terms", "termofpay", "", lstfldinfo);

	private TextField txtCode = ControlsFactory.CreateTextField("", "CODE",
			lstfldinfo);
	private TextField txtNameE = ControlsFactory.CreateTextField("", "name",
			lstfldinfo);
	private TextField txtNameA = ControlsFactory.CreateTextField("", "namea",
			lstfldinfo);
	private TextField txtParentCust = ControlsFactory.CreateTextField("",
			"parentcustomer", lstfldinfo);
	private TextField txtParentCustName = ControlsFactory.CreateTextField("",
			"", null);
	private TextField txtRefer = ControlsFactory.CreateTextField(null,
			"reference", lstfldinfo);
	private TextField txtRemarks = ControlsFactory.CreateTextField(null,
			"remark", lstfldinfo);
	private TextField txtCreditLimit = ControlsFactory.CreateTextField(null,
			"crd_limit", lstfldinfo);
	private TextField txtBankDetails = ControlsFactory.CreateTextField(
			"Bank Details", "bDetail", lstfldinfo);
	private TextField txtLicense = ControlsFactory.CreateTextField(null,
			"license", lstfldinfo);
	private TextField txtAccNo = ControlsFactory.CreateTextField(null, "ac_no",
			lstfldinfo);
	private TextField txtAccName = ControlsFactory.CreateTextField(null,
			"ac_no", lstfldinfo);
	private TextField txtTel = ControlsFactory.CreateTextField(null, "tel",
			lstfldinfo);
	private TextField txtFax = ControlsFactory.CreateTextField(null, "fax",
			lstfldinfo);
	private TextField txtPager = ControlsFactory.CreateTextField(null, "pager",
			lstfldinfo);
	private TextField txtArea = ControlsFactory.CreateTextField(null, "area",
			lstfldinfo);
	private TextField txtAddress = ControlsFactory.CreateTextField(null,
			"addr", lstfldinfo);
	private TextField txtEmail = ControlsFactory.CreateTextField(null, "email",
			lstfldinfo);
	private TextField txtPriceNo = ControlsFactory.CreateTextField(null,
			"BFBALANCE", lstfldinfo);
	private TextField txtCommissionAcc = ControlsFactory.CreateTextField("",
			"COMMISION_AC", lstfldinfo);
	private TextField txtCommissionAccName = ControlsFactory.CreateTextField(
			"", "", null);
	private TextField txtCommissionRate = ControlsFactory.CreateTextField("",
			"COMMISION_RATE", lstfldinfo);
	private TextField txtRevenueAcc = ControlsFactory.CreateTextField("",
			"revenue_ac", lstfldinfo);
	private TextField txtRevenueAccName = ControlsFactory.CreateTextField("",
			"", null);

	public void save_data() {
		save_data(true);
	}

	public void save_data(boolean cls) {
		try {
			con.setAutoCommit(false);
			QueryExe qe = new QueryExe(con);
			for (Iterator iterator = lstfldinfo.iterator(); iterator.hasNext();) {
				FieldInfo fl = (FieldInfo) iterator.next();
				if (!(fl.obj instanceof CheckBox)) {
					qe.setParaValue(fl.fieldName, ((AbstractField) fl.obj)
							.getValue());
				} else {
					qe
							.setParaValue(fl.fieldName, (((CheckBox) fl.obj)
									.booleanValue() ? fl.valueOnTrue
									: fl.valueOnFalse));
				}
			}
			Date dt = new Date(System.currentTimeMillis());

			String p = utils.generatePath(utils.nvl(txtParentCust.getValue(),
					""), utils.nvl(txtCode.getValue(), ""), "C_YCUST", "CODE",
					"PATH", con);

			qe.setParaValue("path", p);
			qe.setParaValue("LEVELNO", utils.countString(p, "\\") - 1);
			// qe.setParaValue("CHILDCOUNT", "0");
			// qe.setParaValue("USECOUNT", "0");
			qe.setParaValue("ISFIXED", "Y");
			qe.setParaValue("IS_ITEM_DISC_ALLOWED", "Y");
			qe.setParaValue("IS_INV_DISC_ALLOWED", "Y");

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("c_ycust"); // if
			} else {
				qe.AutoGenerateUpdateStatment("c_ycust", "'CODE'",
						" WHERE CODE=:code"); // if to update..
			}
			qe.execute();
			con.commit();
			if (show_tree_panel && custBrowse != null) {
				/*
				 * menuItem mn = new menuItem(txtAccno.getValue().toString(),
				 * txtAccName.getValue() + "-" +
				 * txtAccno.getValue().toString());
				 * mn.setParentID(utils.nvl(txtParentAcc.getValue(), ""));
				 * 
				 * accBrowse.updateTree(mn);
				 */
				custBrowse.refreshTree(true);
			}
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtCode.getValue().toString();
			}
			load_data();
		} catch (Exception ex) {

			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void applyColumnsOnBranch() {
		lstBranchCols.clear();
		ColumnProperty cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "BRNO";
		cp.descr = "Br No";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		tableBranches.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "B_NAME";
		cp.descr = "b_name";
		cp.display_width = 150;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableBranches.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "AREA";
		cp.descr = "Area";
		cp.display_width = 75;
		cp.display_type = "NONE";
		cp.pos = 3;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableBranches.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "TEL";
		cp.descr = "Tel";
		cp.display_width = 75;
		cp.display_type = "NONE";
		cp.pos = 4;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = utils.FORMAT_QTY;
		cp.other_styles = "";
		tableBranches.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "FAX";
		cp.descr = "Fax";
		cp.display_width = 75;
		cp.display_type = "NONE";
		cp.pos = 5;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = utils.FORMAT_QTY;
		cp.other_styles = "";
		tableBranches.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "ACCNO";
		cp.descr = "A/C";
		cp.display_width = 75;
		cp.display_type = "NONE";
		cp.pos = 6;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		tableBranches.addColumn(cp, false);
	}

	public void applyColumnsOnContract() {
		lstContractCols.clear();
		ColumnProperty cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "REFER";
		cp.descr = "Reference";
		cp.display_width = 75;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableContracted.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "DESCR";
		cp.descr = "Description";
		cp.display_width = 150;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableContracted.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "PACKD";
		cp.descr = "PackD";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 3;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableContracted.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "UNITD";
		cp.descr = "UnitD";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 4;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableContracted.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "PACK";
		cp.descr = "Pack";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 5;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		tableContracted.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "PRICE";
		cp.descr = "Price";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 6;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		tableContracted.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "DESCR";
		cp.descr = "Remark";
		cp.display_width = 75;
		cp.display_type = "NONE";
		cp.pos = 7;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableContracted.addColumn(cp, false);
	}

	public void load_data() {

		try {
			lblMessage.setValue("Adding New Account....");
			txtCode.setReadOnly(false);
			utilsVaadin.resetValues(basicLayout, false, false);
			txtParentCustName.setReadOnly(false);
			txtParentCust.setReadOnly(false);
			txtAccName.setReadOnly(false);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"Select * from c_ycust where code='" + QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				lblMessage.setValue("Editing : " + txtNameE.getValue());
				fetchAccountInfo();
				txtCode.setReadOnly(true);
				txtParentCustName.setReadOnly(true);
				txtParentCust.setReadOnly(true);
				txtAccName.setReadOnly(true);
			}
			tableBranches.data.executeQuery(
					"select * from cbranch where code ='" + QRYSES
							+ "' order by brno ", true);
			tableBranches.fill_table();
			tableContracted.data.executeQuery(
					"select * from custitems where code ='" + QRYSES
							+ "' order by refer ", true);
			tableContracted.fill_table();
			if (custBrowse != null && show_tree_panel) {
				custBrowse.locateTree(QRYSES);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
		cmbKind.setValue(utilsVaadin.findByValue(cmbKind, "0"));
	}

	protected void fetchAccountInfo() {
		txtParentCustName.setReadOnly(false);
		txtParentCustName.setValue(utils.getSqlValue(
				"Select Name from c_ycust where code = '"
						+ txtParentCust.getValue().toString() + "'", con));

		txtAccName.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtAccNo.getValue().toString() + "'", con));

		txtRevenueAccName.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtRevenueAcc.getValue().toString() + "'", con));

		txtCommissionAccName.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtCommissionAcc.getValue().toString() + "'", con));
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
		try {
			if (data_Items.getDbclass() == null) {
				data_Items.createDBClassFromConnection(con);
			}

		} catch (SQLException e) {
		}

		mapActAccBrows.clear();
		mapActAccBrows.put("open", "Open..");
		mapActAccBrows.put("open_n", "Open New Window..");
		mapActAccBrows.put("create_branch", "Create Branch..");
		mapActAccBrows.put("create_branch_n", "Create Branch New Window..");

		createView();
		applyColumnsOnBranch();
		applyColumnsOnContract();
		load_data();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		m_mainLayout.setSizeFull();
		m_mainLayout.setSpacing(true);

		mainLayout.setWidth("600px");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		basicLayout.setWidth("100%");
		codeKindLayout.setWidth("100%");
		codeLayout.setWidth("100%");
		kindLayout.setWidth("100%");
		nameELayout.setWidth("100%");
		nameALayout.setWidth("100%");
		nameLayout.setWidth("100%");
		parentCustLayout.setWidth("100%");
		otherLayout.setWidth("100%");
		other1Layout.setWidth("100%");
		other2Layout.setWidth("100%");
		referLayout.setWidth("100%");
		remarksLayout.setWidth("100%");
		creditLimitLayout.setWidth("100%");
		bankDetailsLayout.setWidth("100%");
		salesPersonLayout.setWidth("100%");
		marketingChainLayout.setWidth("100%");
		licenseLayout.setWidth("100%");
		accLayout.setWidth("100%");
		typeLayout.setWidth("100%");
		paymentTermsLayout.setWidth("100%");
		telLayout.setWidth("100%");
		contactLayout.setWidth("100%");
		faxLayout.setWidth("100%");
		pagerLayout.setWidth("100%");
		areaLayout.setWidth("100%");
		addressLayout.setWidth("100%");
		emailLayout.setWidth("100%");
		checkLayout.setWidth("100%");
		bankCashLayout.setWidth("100%");
		priceLayout.setWidth("100%");
		commissionAccLayout.setWidth("100%");
		commissionLayout.setWidth("100%");
		commissionRateLayout.setWidth("100%");
		revenueAccLayout.setWidth("100%");
		branchesLayout.setWidth("100%");
		contractedLayout.setWidth("100%");
		branchesLayout.setHeight("100%");
		contractedLayout.setHeight("100%");

		branchTableLayout.setHeight("350px");
		tableBranches.getTable().setHeight("320px");
		tableContracted.getTable().setHeight("400px");

		otherLayout.setSpacing(true);
		other1Layout.setSpacing(true);
		other2Layout.setSpacing(true);
		tableBranches.setWidth("100%");
		tableContracted.setWidth("100%");

		lblCode.setWidth("85px");
		lblKind.setWidth("80px");
		lblnameE.setWidth("84px");
		lblnameA.setWidth("80px");

		lblParentCust.setWidth("77px");
		lblRef.setWidth("80px");
		lblRemarks.setWidth("80px");
		lblCreditLimit.setWidth("80px");
		lblSalesPerson.setWidth("80px");
		lblLicense.setWidth("80px");
		lblAcc.setWidth("80px");
		lblType.setWidth("80px");
		lbltel.setWidth("80px");
		lblFax.setWidth("84px");
		lblPager.setWidth("80px");
		lblArea.setWidth("80px");
		lblAddress.setWidth("80px");
		lblEmail.setWidth("80px");
		lblPriceNo.setWidth("80px");
		emptyLabel.setWidth("105px");
		emptyLabel.setWidth("100px");
		lblCommissionAcc.setWidth("80px");
		lblCommissionRate.setWidth("80px");
		lblRevenueAcc.setWidth("77px");

		txtCode.setWidth("100%");
		txtNameE.setWidth("100%");
		txtNameA.setWidth("100%");
		txtParentCust.setWidth("100%");
		txtParentCustName.setWidth("100%");
		txtRefer.setWidth("100%");
		txtCreditLimit.setWidth("100%");
		txtBankDetails.setWidth("100%");
		txtLicense.setWidth("100%");
		txtAccName.setWidth("100%");
		txtFax.setWidth("100%");
		txtPager.setWidth("100%");
		txtArea.setWidth("100%");
		txtAddress.setWidth("100%");
		txtEmail.setWidth("100%");
		txtPriceNo.setWidth("100%");
		txtRemarks.setWidth("100%");
		txtTel.setWidth("100%");
		txtCommissionAccName.setWidth("100%");
		txtCommissionRate.setWidth("100%");
		txtRevenueAccName.setWidth("100%");

		cmbKind.setWidth("100%");
		cmbSalePerson.setWidth("100%");
		cmbType.setWidth("100%");
		cmbMarketingChain.setWidth("100%");
		cmbPaymentTerms.setWidth("100%");

		tbSheetPanel.setScrollable(true);

		tbSheetPanel.getContent().setHeight("-1px");
		tbSheetPanel.setHeight("-1px");
		tbSheetPanel.setWidth("600px");

		tbsheet.addTab(basicLayout, "Basic Information", null);
		tbsheet.addTab(branchesLayout, "Branches & A/C", null);
		tbsheet.addTab(contractedLayout, "Contracted Items", null);

		basicLayout.addStyleName("formLayout");

		ResourceManager.addComponent(centralPanel, m_mainLayout);
		ResourceManager.addComponent(m_mainLayout, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, tbSheetPanel);
		ResourceManager.addComponent(tbSheetPanel, tbsheet);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdCls);
		ResourceManager.addComponent(buttonLayout, lblMessage);

		ResourceManager.addComponent(branchesLayout, branchTableLayout);
		ResourceManager.addComponent(branchesLayout, commissionLayout);
		ResourceManager.addComponent(branchesLayout, revenueAccLayout);
		ResourceManager.addComponent(contractedLayout, contractTableLayout);

		ResourceManager.addComponent(branchTableLayout, tableBranches);
		ResourceManager.addComponent(contractTableLayout, tableContracted);

		tbSheetPanel.addComponent(tbsheet);

		ResourceManager.addComponent(tbsheet, basicLayout);

		ResourceManager.addComponent(basicLayout, codeKindLayout);
		ResourceManager.addComponent(basicLayout, nameLayout);
		ResourceManager.addComponent(basicLayout, parentCustLayout);
		ResourceManager.addComponent(basicLayout, otherLayout);

		ResourceManager.addComponent(codeKindLayout, codeLayout);
		ResourceManager.addComponent(codeKindLayout, kindLayout);

		ResourceManager.addComponent(codeLayout, lblCode);
		ResourceManager.addComponent(codeLayout, txtCode);

		ResourceManager.addComponent(kindLayout, lblKind);
		ResourceManager.addComponent(kindLayout, cmbKind);
		ResourceManager.addComponent(kindLayout, chkLock);

		ResourceManager.addComponent(otherLayout, other1Layout);
		ResourceManager.addComponent(otherLayout, emptyLabel2);
		ResourceManager.addComponent(otherLayout, other2Layout);

		ResourceManager.addComponent(nameLayout, nameELayout);
		ResourceManager.addComponent(nameLayout, nameALayout);

		ResourceManager.addComponent(nameELayout, lblnameE);
		ResourceManager.addComponent(nameELayout, txtNameE);

		ResourceManager.addComponent(nameALayout, lblnameA);
		ResourceManager.addComponent(nameALayout, txtNameA);

		ResourceManager.addComponent(parentCustLayout, lblParentCust);
		ResourceManager.addComponent(parentCustLayout, txtParentCust);
		ResourceManager.addComponent(parentCustLayout, cmdClearParentAcc);
		ResourceManager.addComponent(parentCustLayout, cmdParentAcc);
		ResourceManager.addComponent(parentCustLayout, txtParentCustName);

		ResourceManager.addComponent(other1Layout, referLayout);
		ResourceManager.addComponent(other1Layout, creditLimitLayout);
		ResourceManager.addComponent(other1Layout, salesPersonLayout);
		ResourceManager.addComponent(other1Layout, licenseLayout);
		ResourceManager.addComponent(other1Layout, accLayout);
		ResourceManager.addComponent(other1Layout, typeLayout);
		ResourceManager.addComponent(other1Layout, telLayout);
		ResourceManager.addComponent(other1Layout, contactLayout);
		ResourceManager.addComponent(other1Layout, areaLayout);
		ResourceManager.addComponent(other1Layout, addressLayout);
		ResourceManager.addComponent(other1Layout, emailLayout);

		ResourceManager.addComponent(referLayout, lblRef);
		ResourceManager.addComponent(referLayout, txtRefer);

		ResourceManager.addComponent(creditLimitLayout, lblCreditLimit);
		ResourceManager.addComponent(creditLimitLayout, txtCreditLimit);

		ResourceManager.addComponent(salesPersonLayout, lblSalesPerson);
		ResourceManager.addComponent(salesPersonLayout, cmbSalePerson);

		ResourceManager.addComponent(licenseLayout, lblLicense);
		ResourceManager.addComponent(licenseLayout, txtLicense);

		ResourceManager.addComponent(accLayout, lblAcc);
		ResourceManager.addComponent(accLayout, txtAccName);
		ResourceManager.addComponent(accLayout, cmdClearAcc);
		ResourceManager.addComponent(accLayout, cmdAcc);

		ResourceManager.addComponent(typeLayout, lblType);
		ResourceManager.addComponent(typeLayout, cmbType);

		ResourceManager.addComponent(telLayout, lbltel);
		ResourceManager.addComponent(telLayout, txtTel);

		ResourceManager.addComponent(contactLayout, faxLayout);
		ResourceManager.addComponent(contactLayout, pagerLayout);

		ResourceManager.addComponent(faxLayout, lblFax);
		ResourceManager.addComponent(faxLayout, txtFax);

		ResourceManager.addComponent(pagerLayout, lblPager);
		ResourceManager.addComponent(pagerLayout, txtPager);

		ResourceManager.addComponent(areaLayout, lblArea);
		ResourceManager.addComponent(areaLayout, txtArea);

		ResourceManager.addComponent(addressLayout, lblAddress);
		ResourceManager.addComponent(addressLayout, txtAddress);

		ResourceManager.addComponent(emailLayout, lblEmail);
		ResourceManager.addComponent(emailLayout, txtEmail);

		ResourceManager.addComponent(other2Layout, remarksLayout);
		ResourceManager.addComponent(other2Layout, bankDetailsLayout);
		ResourceManager.addComponent(other2Layout, marketingChainLayout);
		ResourceManager.addComponent(other2Layout, paymentTermsLayout);
		ResourceManager.addComponent(other2Layout, checkLayout);
		ResourceManager.addComponent(other2Layout, bankCashLayout);
		ResourceManager.addComponent(other2Layout, priceLayout);

		ResourceManager.addComponent(remarksLayout, lblRemarks);
		ResourceManager.addComponent(remarksLayout, txtRemarks);

		ResourceManager.addComponent(bankDetailsLayout, txtBankDetails);

		ResourceManager.addComponent(marketingChainLayout, cmbMarketingChain);

		ResourceManager.addComponent(paymentTermsLayout, cmbPaymentTerms);

		ResourceManager.addComponent(checkLayout, chkIsCustomer);
		ResourceManager.addComponent(checkLayout, chkSupplies);

		ResourceManager.addComponent(bankCashLayout, emptyLabel);
		ResourceManager.addComponent(bankCashLayout, chkBankCash);

		ResourceManager.addComponent(priceLayout, lblPriceNo);
		ResourceManager.addComponent(priceLayout, txtPriceNo);

		ResourceManager.addComponent(commissionLayout, commissionAccLayout);
		ResourceManager.addComponent(commissionLayout, commissionRateLayout);

		ResourceManager.addComponent(commissionAccLayout, lblCommissionAcc);
		ResourceManager.addComponent(commissionAccLayout, txtCommissionAccName);
		ResourceManager.addComponent(commissionAccLayout, cmdCommAcc);
		ResourceManager.addComponent(commissionAccLayout, cmdClearCommAcc);

		ResourceManager.addComponent(commissionRateLayout, lblCommissionRate);
		ResourceManager.addComponent(commissionRateLayout, txtCommissionRate);

		ResourceManager.addComponent(revenueAccLayout, lblRevenueAcc);
		ResourceManager.addComponent(revenueAccLayout, txtRevenueAccName);
		ResourceManager.addComponent(revenueAccLayout, cmdRevAcc);
		ResourceManager.addComponent(revenueAccLayout, cmdClearRevAcc);

		codeLayout.setComponentAlignment(lblCode, Alignment.MIDDLE_CENTER);

		kindLayout.setComponentAlignment(lblKind, Alignment.BOTTOM_CENTER);
		kindLayout.setComponentAlignment(chkLock, Alignment.BOTTOM_CENTER);

		nameELayout.setComponentAlignment(lblnameE, Alignment.BOTTOM_CENTER);

		nameALayout.setComponentAlignment(lblnameA, Alignment.BOTTOM_CENTER);

		parentCustLayout.setComponentAlignment(lblParentCust,
				Alignment.BOTTOM_CENTER);
		parentCustLayout.setComponentAlignment(cmdClearParentAcc,
				Alignment.BOTTOM_CENTER);
		parentCustLayout.setComponentAlignment(cmdParentAcc,
				Alignment.BOTTOM_CENTER);

		referLayout.setComponentAlignment(lblRef, Alignment.BOTTOM_CENTER);

		remarksLayout
				.setComponentAlignment(lblRemarks, Alignment.BOTTOM_CENTER);

		creditLimitLayout.setComponentAlignment(lblCreditLimit,
				Alignment.BOTTOM_CENTER);

		salesPersonLayout.setComponentAlignment(lblSalesPerson,
				Alignment.BOTTOM_CENTER);

		licenseLayout
				.setComponentAlignment(lblLicense, Alignment.BOTTOM_CENTER);

		accLayout.setComponentAlignment(lblAcc, Alignment.BOTTOM_CENTER);
		accLayout.setComponentAlignment(cmdClearAcc, Alignment.BOTTOM_CENTER);
		accLayout.setComponentAlignment(cmdAcc, Alignment.BOTTOM_CENTER);

		typeLayout.setComponentAlignment(lblType, Alignment.BOTTOM_CENTER);

		telLayout.setComponentAlignment(lbltel, Alignment.MIDDLE_CENTER);

		faxLayout.setComponentAlignment(lblFax, Alignment.MIDDLE_CENTER);

		pagerLayout.setComponentAlignment(lblPager, Alignment.MIDDLE_CENTER);

		areaLayout.setComponentAlignment(lblArea, Alignment.MIDDLE_CENTER);

		addressLayout
				.setComponentAlignment(lblAddress, Alignment.MIDDLE_CENTER);

		emailLayout.setComponentAlignment(lblEmail, Alignment.MIDDLE_CENTER);

		priceLayout.setComponentAlignment(lblPriceNo, Alignment.MIDDLE_CENTER);

		buttonLayout.setComponentAlignment(lblMessage, Alignment.BOTTOM_RIGHT);

		commissionAccLayout.setComponentAlignment(lblCommissionAcc,
				Alignment.BOTTOM_CENTER);
		commissionAccLayout.setComponentAlignment(cmdCommAcc,
				Alignment.BOTTOM_CENTER);
		commissionAccLayout.setComponentAlignment(cmdClearCommAcc,
				Alignment.BOTTOM_CENTER);

		commissionRateLayout.setComponentAlignment(lblCommissionRate,
				Alignment.BOTTOM_CENTER);

		revenueAccLayout.setComponentAlignment(lblRevenueAcc,
				Alignment.BOTTOM_CENTER);
		revenueAccLayout.setComponentAlignment(cmdRevAcc,
				Alignment.BOTTOM_CENTER);
		revenueAccLayout.setComponentAlignment(cmdClearRevAcc,
				Alignment.BOTTOM_CENTER);

		commissionLayout.setExpandRatio(commissionAccLayout, 3f);
		commissionLayout.setExpandRatio(commissionRateLayout, 1f);

		commissionAccLayout.setExpandRatio(lblCommissionAcc, 0.1f);
		commissionAccLayout.setExpandRatio(txtCommissionAccName, 3.9f);

		commissionRateLayout.setExpandRatio(lblCommissionRate, 0.1f);
		commissionRateLayout.setExpandRatio(txtCommissionRate, 3.9f);

		revenueAccLayout.setExpandRatio(lblRevenueAcc, 0.1f);
		revenueAccLayout.setExpandRatio(txtRevenueAccName, 3.9f);

		mainLayout.setExpandRatio(buttonLayout, 0.1f);
		mainLayout.setExpandRatio(tbSheetPanel, 3.9f);

		codeKindLayout.setExpandRatio(codeLayout, 1.5f);
		codeKindLayout.setExpandRatio(kindLayout, 2.5f);

		nameLayout.setExpandRatio(nameELayout, 2f);
		nameLayout.setExpandRatio(nameALayout, 2f);

		otherLayout.setExpandRatio(other1Layout, 2f);
		otherLayout.setExpandRatio(emptyLabel2, 0.1f);
		otherLayout.setExpandRatio(other2Layout, 1.9f);

		codeLayout.setExpandRatio(lblCode, 0.1f);
		codeLayout.setExpandRatio(txtCode, 3.9f);

		kindLayout.setExpandRatio(lblKind, 0.1f);
		kindLayout.setExpandRatio(cmbKind, 2f);
		kindLayout.setExpandRatio(chkLock, 1f);

		nameELayout.setExpandRatio(lblnameE, 0.1f);
		nameELayout.setExpandRatio(txtNameE, 3.9f);

		nameALayout.setExpandRatio(lblnameA, 0.1f);
		nameALayout.setExpandRatio(txtNameA, 3.9f);

		parentCustLayout.setExpandRatio(lblParentCust, 0.1f);
		parentCustLayout.setExpandRatio(txtParentCust, 0.5f);
		parentCustLayout.setExpandRatio(cmdClearParentAcc, 0.1f);
		parentCustLayout.setExpandRatio(cmdParentAcc, 0.1f);
		parentCustLayout.setExpandRatio(txtParentCustName, 3.2f);

		referLayout.setExpandRatio(lblRef, 0.1f);
		referLayout.setExpandRatio(txtRefer, 3.9f);

		remarksLayout.setExpandRatio(lblRemarks, 0.1f);
		remarksLayout.setExpandRatio(txtRemarks, 3.9f);

		creditLimitLayout.setExpandRatio(lblCreditLimit, 0.1f);
		creditLimitLayout.setExpandRatio(txtCreditLimit, 3.9f);

		licenseLayout.setExpandRatio(lblLicense, 0.1f);
		licenseLayout.setExpandRatio(txtLicense, 3.9f);

		salesPersonLayout.setExpandRatio(lblSalesPerson, 0.1f);
		salesPersonLayout.setExpandRatio(cmbSalePerson, 3.9f);

		accLayout.setExpandRatio(lblAcc, 0.1f);
		accLayout.setExpandRatio(txtAccName, 3.7f);
		accLayout.setExpandRatio(cmdClearAcc, 0.1f);
		accLayout.setExpandRatio(cmdAcc, 0.1f);

		typeLayout.setExpandRatio(lblType, 0.1f);
		typeLayout.setExpandRatio(cmbType, 3.9f);

		telLayout.setExpandRatio(lbltel, 0.1f);
		telLayout.setExpandRatio(txtTel, 3.9f);

		faxLayout.setExpandRatio(lblFax, 0.1f);
		faxLayout.setExpandRatio(txtFax, 3.9f);

		pagerLayout.setExpandRatio(lblPager, 0.1f);
		pagerLayout.setExpandRatio(txtPager, 3.9f);

		areaLayout.setExpandRatio(lblArea, 0.1f);
		areaLayout.setExpandRatio(txtArea, 3.9f);

		addressLayout.setExpandRatio(lblAddress, 0.1f);
		addressLayout.setExpandRatio(txtAddress, 3.9f);

		checkLayout.setExpandRatio(chkIsCustomer, 2f);
		checkLayout.setExpandRatio(chkSupplies, 2f);

		bankCashLayout.setExpandRatio(emptyLabel, 2f);
		bankCashLayout.setExpandRatio(chkBankCash, 2f);

		priceLayout.setExpandRatio(lblPriceNo, 0.1f);
		priceLayout.setExpandRatio(txtPriceNo, 3.9f);

		emailLayout.setExpandRatio(lblEmail, 0.1f);
		emailLayout.setExpandRatio(txtEmail, 3.9f);

		branchesLayout.setExpandRatio(branchTableLayout, 2f);
		branchesLayout.setExpandRatio(commissionLayout, 1f);
		branchesLayout.setExpandRatio(revenueAccLayout, 1f);

		cmbKind.removeAllItems();
		cmbKind.addItem(new dataCell("Local", "0"));
		cmbKind.addItem(new dataCell("Wholesale", "1"));

		applyColumnsOnBranch();
		applyColumnsOnContract();
		createInfoBox();
		if (!listnerAdded) {
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
			cmdCls.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					load_data();
				}
			});
			cmdParentAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listParentAcc();
				}
			});
			cmdAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listAcc();
				}
			});
			cmdCommAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_CommAccountList();
				}
			});
			cmdRevAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_RevenueAccountList();
				}
			});

			cmdClearParentAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtParentCust.setReadOnly(false);
					txtParentCustName.setReadOnly(false);
					txtParentCust.setValue("");
					txtParentCustName.setValue("");
					txtParentCust.setReadOnly(true);
					txtParentCustName.setReadOnly(true);
				}
			});
			cmdClearAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtAccNo.setReadOnly(false);
					txtAccName.setReadOnly(false);
					txtAccNo.setValue("");
					txtAccName.setValue("");
					txtAccNo.setReadOnly(true);
					txtAccName.setReadOnly(true);
				}
			});
			cmdClearCommAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtCommissionAcc.setReadOnly(false);
					txtCommissionAccName.setReadOnly(false);
					txtCommissionAcc.setValue("");
					txtCommissionAccName.setValue("");
					txtCommissionAcc.setReadOnly(true);
					txtCommissionAccName.setReadOnly(true);
				}
			});
			cmdClearRevAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtRevenueAcc.setReadOnly(false);
					txtRevenueAccName.setReadOnly(false);
					txtRevenueAcc.setValue("");
					txtRevenueAccName.setValue("");
					txtRevenueAcc.setReadOnly(true);
					txtRevenueAccName.setReadOnly(true);
				}
			});
			listnerAdded = true;
		}

	}

	private void createInfoBox() {

		try {
			if (show_tree_panel) {
				if (custBrowse == null) {
					custBrowse = InfoBox.createInstance(data_Items, "CODE",
							"PARENTCUSTOMER", "NAME", "CHILDCOUNT",
							"Customers", "USECOUNT", "");

					m_mainLayout.addComponent(custBrowse);
					custBrowse.setHeight("100%");
					custBrowse.setWidth("300px");
					custBrowse.tree.addActionHandler(new Handler() {

						public void handleAction(Action action, Object sender,
								Object target) {
							menuItem m = (menuItem) target;

							// open
							if (action.getCaption().equals(
									mapActAccBrows.get("open"))) {

								QRYSES = m.getId();
								load_data();
							}

							// opne-n
							if (action.getCaption().equals(
									mapActAccBrows.get("open_n"))) {
								Window wnd = ControlsFactory.CreateWindow(
										"600px", "350px", true, true);
								frmCostCent frm = new frmCostCent();
								frm.setParentLayout((AbstractLayout) wnd
										.getContent());
								frm.show_tree_panel = false;
								frm.showInitView();
								frm.QRYSES = m.getId();
								frm.load_data();

								wnd.setScrollable(true);
							}

							// create_branch
							if (action.getCaption().equals(
									mapActAccBrows.get("create_branch"))) {
								createCust(m.getId());
							}

							if (action.getCaption().equals(
									mapActAccBrows.get("create_branch_n"))) {
								Window wnd = ControlsFactory.CreateWindow(
										"600px", "350px", true, true);
								frmCostCent frm = new frmCostCent();
								frm.setParentLayout((AbstractLayout) wnd
										.getContent());
								frm.show_tree_panel = false;
								frm.showInitView();
								frm.createAcc(m.getId());
							}

						}

						public Action[] getActions(Object target, Object sender) {
							if (target == null)
								return null;

							List<Action> acts = new ArrayList<Action>();
							acts.add(new Action(mapActAccBrows.get("open")));
							acts.add(new Action(mapActAccBrows.get("open_n")));
							menuItem m = (menuItem) target;
							if (((BigDecimal) m.getPara2Val()).intValue() == 0) {
								acts.add(new Action(mapActAccBrows
										.get("create_branch")));
								acts.add(new Action(mapActAccBrows
										.get("create_branch_n")));
							}
							Action act[] = new Action[acts.size()];
							return acts.toArray(act);
						}
					});

					new ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							if (custBrowse.tree.getValue() != null) {
								menuItem itm = (menuItem) custBrowse.tree
										.getValue();
								QRYSES = itm.getId();
								txtNameA.focus();
								load_data();
							} else {
								QRYSES = "";
								load_data();
								txtNameA.focus();
							}
						}
					};
				}
				data_Items.executeQuery(
						"select code,name,namea,path,parentcustomer,childcount,usecount "
								+ "from c_ycust   order by path", true);
				m_mainLayout.removeComponent(custBrowse);
				m_mainLayout.addComponent(custBrowse);
				custBrowse.initView();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		m_mainLayout.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		other1Layout.removeAllComponents();
		other2Layout.removeAllComponents();
		branchesLayout.removeAllComponents();
		contractedLayout.removeAllComponents();
		branchTableLayout.removeAllComponents();
		contractTableLayout.removeAllComponents();
		codeLayout.removeAllComponents();
		kindLayout.removeAllComponents();
		codeKindLayout.removeAllComponents();
		nameELayout.removeAllComponents();
		nameALayout.removeAllComponents();
		nameLayout.removeAllComponents();
		parentCustLayout.removeAllComponents();
		otherLayout.removeAllComponents();
		referLayout.removeAllComponents();
		remarksLayout.removeAllComponents();
		creditLimitLayout.removeAllComponents();
		bankDetailsLayout.removeAllComponents();
		salesPersonLayout.removeAllComponents();
		marketingChainLayout.removeAllComponents();
		licenseLayout.removeAllComponents();
		accLayout.removeAllComponents();
		typeLayout.removeAllComponents();
		paymentTermsLayout.removeAllComponents();
		telLayout.removeAllComponents();
		contactLayout.removeAllComponents();
		faxLayout.removeAllComponents();
		pagerLayout.removeAllComponents();
		areaLayout.removeAllComponents();
		addressLayout.removeAllComponents();
		emailLayout.removeAllComponents();
		checkLayout.removeAllComponents();
		bankCashLayout.removeAllComponents();
		priceLayout.removeAllComponents();
		buttonLayout.removeAllComponents();
		commissionLayout.removeAllComponents();
		commissionAccLayout.removeAllComponents();
		commissionRateLayout.removeAllComponents();
		revenueAccLayout.removeAllComponents();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractOrderedLayout) parentLayout;
	}

	public void showInitView() {
		QRYSES = "";
		initForm();
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
							QRYSES = tv.getData().getFieldValue(rn, "code")
									.toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select code, name from c_ycust order by path", true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_CommAccountList() {
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
							txtCommissionAccName.setValue(tv.getData()
									.getFieldValue(rn, "AccNo").toString()
									+ "-"
									+ tv.getData().getFieldValue(rn, "Name")
											.toString());
							txtCommissionAcc.setValue(tv.getData()
									.getFieldValue(rn, "AccNo").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select AccNo, Name from Acaccount order by path", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_RevenueAccountList() {
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
							txtRevenueAccName.setValue(tv.getData()
									.getFieldValue(rn, "AccNo").toString()
									+ "-"
									+ tv.getData().getFieldValue(rn, "Name")
											.toString());
							txtRevenueAcc.setValue(tv.getData().getFieldValue(
									rn, "AccNo").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select AccNo, Name from Acaccount order by path", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_listParentAcc() {
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
											txtParentCustName
													.setReadOnly(false);
											txtParentCust.setReadOnly(false);
											int rn = tv.getSelectionValue();
											txtParentCustName.setValue(tv
													.getData().getFieldValue(
															rn, "NAME")
													.toString());
											txtParentCust.setValue(tv.getData()
													.getFieldValue(rn, "code")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										} finally {
											txtParentCustName.setReadOnly(true);
											txtParentCust.setReadOnly(true);
										}
									}
								}
							},
							con,
							"select code,name from c_ycust where usecount=0 order by path",
							true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	protected void show_listAcc() {
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
											txtAccName.setReadOnly(false);
											int rn = tv.getSelectionValue();
											txtAccName.setValue(tv.getData()
													.getFieldValue(rn, "NAME")
													.toString());
											txtAccNo.setValue(tv.getData()
													.getFieldValue(rn, "code")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										} finally {
											txtAccName.setReadOnly(true);
										}
									}
								}
							},
							con,
							"select code,name from c_ycust where usecount=0 order by path",
							true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	public void createCust(String cod) {
		if (!txtCode.isReadOnly()) {
			txtCode.setValue(utils.getNewNo(
					"select to_number(nvl(max(code),'0')) from c_ycust where  (parentcustomer='"
							+ cod + "')", cod, con));
			utilsVaadin.setValueByForce(txtParentCustName, utils.getSqlValue(
					"select title from" + " c_ycust where code='" + cod + "'",
					con));
			utilsVaadin.setValueByForce(txtParentCust, "");
			if (txtParentCustName.getValue() != null)
				utilsVaadin.setValueByForce(txtParentCust, cod);
		}
	}

}
