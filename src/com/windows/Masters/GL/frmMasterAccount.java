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
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.InfoBox;
import com.generic.QueryExe;
import com.generic.ResourceManager;
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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
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

public class frmMasterAccount implements transactionalForm {
	private AbstractLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	
	private HorizontalLayout m_mainLayout = new HorizontalLayout();
	
	private VerticalLayout mainLayout = new VerticalLayout();
	private localTableModel data_Items = new localTableModel();
	private VerticalLayout basicLayout = new VerticalLayout();
	private VerticalLayout otherLayout = new VerticalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout accNoLayout = new HorizontalLayout();
	private HorizontalLayout accNameLayout = new HorizontalLayout();
	private HorizontalLayout accTypeLayout = new HorizontalLayout();
	private HorizontalLayout nameEngLayout = new HorizontalLayout();
	private HorizontalLayout parentAccLayout = new HorizontalLayout();
	private HorizontalLayout closeAccLayout = new HorizontalLayout();
	private HorizontalLayout startDateLayout = new HorizontalLayout();
	private HorizontalLayout todayDateLayout = new HorizontalLayout();
	private HorizontalLayout checkLayout = new HorizontalLayout();
	private HorizontalLayout custCreditLayout = new HorizontalLayout();
	private HorizontalLayout currencyCodeLayout = new HorizontalLayout();
	private HorizontalLayout currPricelayout = new HorizontalLayout();
	private HorizontalLayout custInvoiceLayout = new HorizontalLayout();
	private HorizontalLayout invoiceLayout = new HorizontalLayout();
	private HorizontalLayout areaLayout = new HorizontalLayout();
	private HorizontalLayout cityLayout = new HorizontalLayout();
	private HorizontalLayout countryLayout = new HorizontalLayout();
	private HorizontalLayout refNameLayout = new HorizontalLayout();
	private HorizontalLayout refTitleLayout = new HorizontalLayout();
	private HorizontalLayout refTelLayout = new HorizontalLayout();
	private HorizontalLayout telLayout = new HorizontalLayout();
	private HorizontalLayout faxLayout = new HorizontalLayout();
	private HorizontalLayout fullAddrLayout = new HorizontalLayout();
	private HorizontalLayout emailLayout = new HorizontalLayout();
	private HorizontalLayout bankLayout = new HorizontalLayout();
	private HorizontalLayout paymentLayout = new HorizontalLayout();
	private HorizontalLayout remarksLayout = new HorizontalLayout();
	// private HorizontalSplitPanel horzSplitPanel = new HorizontalSplitPanel();
	private HorizontalLayout referLayout = new HorizontalLayout();
	private HorizontalLayout contactLayout = new HorizontalLayout();
	private HorizontalLayout accGridLayout = new HorizontalLayout();
	private HorizontalLayout accTypeHorzLayout = new HorizontalLayout();
	private HorizontalLayout creditGridLayout = new HorizontalLayout();
	private HorizontalLayout invoiceHorzLayout = new HorizontalLayout();
	private HorizontalLayout dateGridLayout = new HorizontalLayout();
	private HorizontalLayout contactGridLayout = new HorizontalLayout();

	private Panel tbSheetPanel = new Panel();

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
	private NativeButton cmdCloseAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearParentAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearCloseAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");

	private CheckBox chkLock = ControlsFactory.CreateCheckField(
			"Lock / Unlock", "flag", "1", "2", lstfldinfo);
	private CheckBox chkIsCustomer = ControlsFactory.CreateCheckField(
			"IsCustomer", "isCust", "Y", "N", lstfldinfo);
	private CheckBox chkBankCash = ControlsFactory.CreateCheckField(
			"Bank / Cash", "isBankCash", "Y", "N", lstfldinfo);
	private CheckBox chkSupplies = ControlsFactory.CreateCheckField("Supplier",
			"issupp", "Y", "N", lstfldinfo);
	private CheckBox chkisFixedPrice = ControlsFactory.CreateCheckField(
			"isFixedPrice", "isFixed", "Y", "N", lstfldinfo);
	private CheckBox chkDiscOnInvoice = ControlsFactory.CreateCheckField(
			"Disc On Price", "is_item_disc_allowed", "Y", "N", lstfldinfo);
	private CheckBox chkDiscByPrice = ControlsFactory.CreateCheckField(
			"Disc By Price", "is_inv_disc_allowed", "Y", "N", lstfldinfo);

	private Label lblMessage = new Label("");
	private Label lblAccNo = new Label("A/C No");
	private Label lblAccName = new Label("Name");
	private Label lblAccType = new Label("Type");
	private Label lblNameEng = new Label("Name Eng");
	private Label lblParentAcc = new Label("Parent A/C");
	private Label lblCloseAcc = new Label("Close A/C");
	private Label lblStartDate = new Label("Start Date");
	private Label lblTodayDate = new Label("Today Date");
	private Label lblCustCredit = new Label("Cust.Crd.");
	private Label lblCurrencyCode = new Label("Curr.Code");
	private Label lblCustInvDays = new Label("Cust Inv.Days");
	private Label lblCurrPriceNo = new Label("Curr.Price");
	private Label lblFullAddr = new Label("Full Addr.");
	private Label lblTel = new Label("Tel");
	private Label lblFax = new Label("Fax");
	private Label lblArea = new Label("Area");
	private Label lblCity = new Label("City");
	private Label lblCountry = new Label("Country");
	private Label lblRefName = new Label("Ref. Name");
	private Label lblRefTitle = new Label("Ref. Title");
	private Label lblRefTel = new Label("Ref. Tel");
	private Label lblEmailAddr = new Label("Email Addr.");
	private Label lblBankDetails = new Label("Bank Details");
	private Label lblPaymentTerms = new Label("Pay Terms");
	private Label lblRemarks = new Label("Remarks");
	private Label emptyLabel1 = new Label("");
	private Label emptyLabel2 = new Label("");
	private Label emptyLabel3 = new Label("");

	private InfoBox accBrowse = null;

	private TextField txtAccno = ControlsFactory.CreateTextField("", "accno",
			lstfldinfo);
	private TextField txtAccName = ControlsFactory.CreateTextField("", "name",
			lstfldinfo);
	private ComboBox txtAccType = ControlsFactory.CreateListField("", "actype",
			"", lstfldinfo);
	private TextField txtNameEng = ControlsFactory.CreateTextField("", "namea",
			lstfldinfo);
	private TextField txtParentAcc = ControlsFactory.CreateTextField("",
			"parentAcc", lstfldinfo);
	private TextField txtParentName = ControlsFactory.CreateTextField("", "",
			null);
	private TextField txtCloseAcc = ControlsFactory.CreateTextField("",
			"closeAcc", lstfldinfo);
	private TextField txtCloseName = ControlsFactory.CreateTextField("", "",
			null);
	private TextField txtCustCredit = ControlsFactory.CreateTextField("",
			"crd_limit", lstfldinfo);
	private TextField txtCurrencyCode = ControlsFactory.CreateTextField("",
			"currency_code", lstfldinfo);
	private TextField txtCustInvoiceDays = ControlsFactory.CreateTextField("",
			"inv_due_days", lstfldinfo);
	private TextField txtCurrentPrice = ControlsFactory.CreateTextField("",
			"default_price", lstfldinfo);
	private TextField txtTodayDate = ControlsFactory.CreateTextField("", "",
			null);
	private TextField txtFullAddr = ControlsFactory.CreateTextField("",
			"full_addr", lstfldinfo);
	private TextField txtTel = ControlsFactory.CreateTextField("", "tel",
			lstfldinfo);
	private TextField txtFax = ControlsFactory.CreateTextField("", "fax",
			lstfldinfo);
	private TextField txtArea = ControlsFactory.CreateTextField("", "area",
			lstfldinfo);
	private TextField txtCity = ControlsFactory.CreateTextField("", "city",
			lstfldinfo);
	private TextField txtCountry = ControlsFactory.CreateTextField("",
			"country", lstfldinfo);
	private TextField txtRefName = ControlsFactory.CreateTextField("",
			"reference_name", lstfldinfo);
	private TextField txtRefTitle = ControlsFactory.CreateTextField("",
			"reference_title", lstfldinfo);
	private TextField txtRefTel = ControlsFactory.CreateTextField("",
			"reference_tel", lstfldinfo);
	private TextField txtEmailAddr = ControlsFactory.CreateTextField("",
			"email_addr", lstfldinfo);
	private TextField txtBankDetails = ControlsFactory.CreateTextField("",
			"bank_details", lstfldinfo);
	private TextField txtPaymentTerms = ControlsFactory.CreateTextField("",
			"payment_terms", lstfldinfo);
	private TextField txtRemarks = ControlsFactory.CreateTextField("",
			"remarks", lstfldinfo);
	private DateField txtStartDate = ControlsFactory.CreateDateField("",
			"start_date", lstfldinfo);

	public boolean show_tree_panel = true;

	private java.util.Map<String, String> mapActAccBrows = new HashMap<String, String>();

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
			String p = utils.generatePath(utils
					.nvl(txtParentAcc.getValue(), ""), utils.nvl(txtAccno
					.getValue(), ""), "ACACCOUNT", "ACCNO", "PATH", con);
			qe.setParaValue("path", p);
			qe.setParaValue("LEVELNO", utils.countString(p, "\\") - 1);
			qe.setParaValue("START_DATE", dt);

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("acaccount"); // if
			} else {
				qe.AutoGenerateUpdateStatment("acaccount", "'ACCNO'",
						" WHERE ACCNO=:accno"); // if to update..
			}
			qe.execute();
			con.commit();
			if (show_tree_panel && accBrowse != null) {
				/*
				 * menuItem mn = new menuItem(txtAccno.getValue().toString(),
				 * txtAccName.getValue() + "-" +
				 * txtAccno.getValue().toString());
				 * mn.setParentID(utils.nvl(txtParentAcc.getValue(), ""));
				 * 
				 * accBrowse.updateTree(mn);
				 */accBrowse.refreshTree(true);
			}
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtAccno.getValue().toString();
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

	public void load_data() {

		try {
			tbsheet.setSelectedTab(basicLayout);
			lblMessage.setValue("Adding New Account....");
			txtAccno.setReadOnly(false);
			utilsVaadin.resetValues(basicLayout, false, false);
			txtCustCredit.setValue("0.00");
			txtCurrentPrice.setValue("0.00");
			txtCustInvoiceDays.setValue("0.00");
			txtParentName.setReadOnly(true);
			txtCloseName.setReadOnly(true);
			txtParentAcc.setReadOnly(true);
			txtCloseAcc.setReadOnly(true);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"Select * from acaccount where accno='" + QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				txtAccno.setReadOnly(true);
				lblMessage.setValue("Editing : " + txtAccName.getValue());
				fetchAccountInfo();
			}
			if (accBrowse != null && show_tree_panel) {
				accBrowse.locateTree(QRYSES);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
		txtAccType.setValue(utilsVaadin.findByValue(txtAccType, "0"));
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
		load_data();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		m_mainLayout.setSizeFull();
		m_mainLayout.setSpacing(true);
		m_mainLayout.setMargin(true);

		mainLayout.setWidth("600px");
		mainLayout.setHeight("100%");

		basicLayout.setWidth("100%");
		accGridLayout.setWidth("100%");
		creditGridLayout.setWidth("100%");
		checkLayout.setWidth("100%");
		invoiceLayout.setWidth("100%");
		otherLayout.setWidth("100%");
		invoiceHorzLayout.setWidth("100%");
		accTypeHorzLayout.setWidth("100%");
		accNoLayout.setWidth("100%");
		accNameLayout.setWidth("100%");
		accTypeLayout.setWidth("100%");
		nameEngLayout.setWidth("100%");
		parentAccLayout.setWidth("100%");
		closeAccLayout.setWidth("100%");
		startDateLayout.setWidth("100%");
		todayDateLayout.setWidth("100%");
		custCreditLayout.setWidth("100%");
		currencyCodeLayout.setWidth("100%");
		currPricelayout.setWidth("100%");
		custInvoiceLayout.setWidth("100%");
		areaLayout.setWidth("100%");
		cityLayout.setWidth("100%");
		countryLayout.setWidth("100%");
		refNameLayout.setWidth("100%");
		refTitleLayout.setWidth("100%");
		refTelLayout.setWidth("100%");
		telLayout.setWidth("100%");
		faxLayout.setWidth("100%");
		fullAddrLayout.setWidth("100%");
		emailLayout.setWidth("100%");
		bankLayout.setWidth("100%");
		paymentLayout.setWidth("100%");
		remarksLayout.setWidth("100%");
		contactLayout.setWidth("100%");
		contactGridLayout.setWidth("100%");
		dateGridLayout.setWidth("100%");
		referLayout.setWidth("100%");

		tbSheetPanel.setScrollable(true);
		tbSheetPanel.getContent().setHeight("-1px");
		tbSheetPanel.setHeight("-1px");
		tbSheetPanel.setWidth("600px");

		tbsheet.addTab(basicLayout, "Basic Information", null);
		tbsheet.addTab(otherLayout, "Others", null);

		basicLayout.setSpacing(true);

		basicLayout.addStyleName("formLayout");
		otherLayout.addStyleName("formLayout");

		txtCustCredit.addStyleName("netAmtStyle");
		txtCurrencyCode.addStyleName("netAmtStyle");
		txtCustInvoiceDays.addStyleName("netAmtStyle");
		txtCurrentPrice.addStyleName("netAmtStyle");

		lblAccNo.setWidth("70px");
		lblAccName.setWidth("70px");
		lblAccType.setWidth("70px");
		lblNameEng.setWidth("70px");
		lblParentAcc.setWidth("63px");
		lblCloseAcc.setWidth("63px");
		lblStartDate.setWidth("70px");
		lblTodayDate.setWidth("70px");
		lblCustCredit.setWidth("70px");
		lblCurrencyCode.setWidth("70px");
		lblCustInvDays.setWidth("70px");
		lblCurrPriceNo.setWidth("70px");
		lblFullAddr.setWidth("77px");
		lblTel.setWidth("85px");
		lblFax.setWidth("40px");
		lblArea.setWidth("87px");
		lblCity.setWidth("60px");
		lblCountry.setWidth("60px");
		lblRefName.setWidth("87px");
		lblRefTitle.setWidth("60px");
		lblRefTel.setWidth("60px");
		lblEmailAddr.setWidth("78px");
		lblBankDetails.setWidth("78px");
		lblPaymentTerms.setWidth("78px");
		lblRemarks.setWidth("78px");

		emptyLabel1.setWidth("15px");
		emptyLabel2.setWidth("25px");

		txtAccno.setWidth("100%");
		txtAccName.setWidth("100%");
		txtAccType.setWidth("100%");
		txtNameEng.setWidth("100%");
		txtParentAcc.setWidth("100%");
		txtParentName.setWidth("100%");
		txtCloseAcc.setWidth("100%");
		txtCloseName.setWidth("100%");
		txtCustCredit.setWidth("100%");
		txtCurrencyCode.setWidth("100%");
		txtCustInvoiceDays.setWidth("100%");
		txtCurrentPrice.setWidth("100%");
		txtTodayDate.setWidth("100%");
		txtFullAddr.setWidth("100%");
		txtTel.setWidth("100%");
		txtFax.setWidth("100%");
		txtArea.setWidth("100%");
		txtCity.setWidth("100%");
		txtCountry.setWidth("100%");
		txtRefName.setWidth("100%");
		txtRefTitle.setWidth("100%");
		txtRefTel.setWidth("100%");
		txtEmailAddr.setWidth("100%");
		txtBankDetails.setWidth("100%");
		txtPaymentTerms.setWidth("100%");
		txtRemarks.setWidth("100%");
		txtStartDate.setWidth("100%");

		basicLayout.setMargin(true);

		utils.findFieldInfoByObject(txtCustCredit, lstfldinfo).format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		utils.findFieldInfoByObject(txtStartDate, lstfldinfo).format = utils.FORMAT_SHORT_DATE;

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

		ResourceManager.addComponent(tbsheet, basicLayout);

		ResourceManager.addComponent(basicLayout, chkLock);
		ResourceManager.addComponent(basicLayout, accGridLayout);
		ResourceManager.addComponent(basicLayout, accTypeHorzLayout);
		ResourceManager.addComponent(basicLayout, parentAccLayout);
		ResourceManager.addComponent(basicLayout, closeAccLayout);
		ResourceManager.addComponent(basicLayout, dateGridLayout);
		ResourceManager.addComponent(basicLayout, checkLayout);
		ResourceManager.addComponent(basicLayout, creditGridLayout);
		ResourceManager.addComponent(basicLayout, invoiceHorzLayout);
		ResourceManager.addComponent(basicLayout, emptyLabel3);
		ResourceManager.addComponent(basicLayout, invoiceLayout);

		ResourceManager.addComponent(accGridLayout, accNoLayout);
		ResourceManager.addComponent(accGridLayout, accNameLayout);

		ResourceManager.addComponent(accTypeHorzLayout, accTypeLayout);
		ResourceManager.addComponent(accTypeHorzLayout, nameEngLayout);

		ResourceManager.addComponent(creditGridLayout, custCreditLayout);
		ResourceManager.addComponent(creditGridLayout, currencyCodeLayout);

		ResourceManager.addComponent(invoiceHorzLayout, custInvoiceLayout);
		ResourceManager.addComponent(invoiceHorzLayout, currPricelayout);

		ResourceManager.addComponent(dateGridLayout, startDateLayout);
		ResourceManager.addComponent(dateGridLayout, todayDateLayout);

		ResourceManager.addComponent(accNoLayout, lblAccNo);
		ResourceManager.addComponent(accNoLayout, txtAccno);

		ResourceManager.addComponent(accNameLayout, lblAccName);
		ResourceManager.addComponent(accNameLayout, txtAccName);

		ResourceManager.addComponent(accTypeLayout, lblAccType);
		ResourceManager.addComponent(accTypeLayout, txtAccType);

		ResourceManager.addComponent(nameEngLayout, lblNameEng);
		ResourceManager.addComponent(nameEngLayout, txtNameEng);

		ResourceManager.addComponent(parentAccLayout, lblParentAcc);
		ResourceManager.addComponent(parentAccLayout, txtParentAcc);
		ResourceManager.addComponent(parentAccLayout, cmdParentAcc);
		ResourceManager.addComponent(parentAccLayout, cmdClearParentAcc);
		ResourceManager.addComponent(parentAccLayout, txtParentName);

		ResourceManager.addComponent(closeAccLayout, lblCloseAcc);
		ResourceManager.addComponent(closeAccLayout, txtCloseAcc);
		ResourceManager.addComponent(closeAccLayout, cmdCloseAcc);
		ResourceManager.addComponent(closeAccLayout, cmdClearCloseAcc);
		ResourceManager.addComponent(closeAccLayout, txtCloseName);

		ResourceManager.addComponent(startDateLayout, lblStartDate);
		ResourceManager.addComponent(startDateLayout, txtStartDate);

		ResourceManager.addComponent(todayDateLayout, lblTodayDate);
		ResourceManager.addComponent(todayDateLayout, txtTodayDate);

		ResourceManager.addComponent(checkLayout, emptyLabel1);
		ResourceManager.addComponent(checkLayout, chkIsCustomer);
		ResourceManager.addComponent(checkLayout, chkBankCash);
		ResourceManager.addComponent(checkLayout, chkSupplies);

		ResourceManager.addComponent(custCreditLayout, lblCustCredit);
		ResourceManager.addComponent(custCreditLayout, txtCustCredit);

		ResourceManager.addComponent(currencyCodeLayout, lblCurrencyCode);
		ResourceManager.addComponent(currencyCodeLayout, txtCurrencyCode);

		ResourceManager.addComponent(custInvoiceLayout, lblCustInvDays);
		ResourceManager.addComponent(custInvoiceLayout, txtCustInvoiceDays);

		ResourceManager.addComponent(currPricelayout, lblCurrPriceNo);
		ResourceManager.addComponent(currPricelayout, txtCurrentPrice);

		ResourceManager.addComponent(invoiceLayout, emptyLabel2);
		ResourceManager.addComponent(invoiceLayout, chkDiscOnInvoice);
		ResourceManager.addComponent(invoiceLayout, chkisFixedPrice);
		ResourceManager.addComponent(invoiceLayout, chkDiscByPrice);

		ResourceManager.addComponent(otherLayout, fullAddrLayout);
		ResourceManager.addComponent(otherLayout, contactLayout);
		ResourceManager.addComponent(otherLayout, contactGridLayout);
		ResourceManager.addComponent(otherLayout, referLayout);
		ResourceManager.addComponent(otherLayout, emailLayout);
		ResourceManager.addComponent(otherLayout, bankLayout);
		ResourceManager.addComponent(otherLayout, paymentLayout);
		ResourceManager.addComponent(otherLayout, remarksLayout);

		ResourceManager.addComponent(fullAddrLayout, lblFullAddr);
		ResourceManager.addComponent(fullAddrLayout, txtFullAddr);

		ResourceManager.addComponent(contactLayout, telLayout);
		ResourceManager.addComponent(contactLayout, faxLayout);

		ResourceManager.addComponent(contactGridLayout, areaLayout);
		ResourceManager.addComponent(contactGridLayout, cityLayout);
		ResourceManager.addComponent(contactGridLayout, countryLayout);

		ResourceManager.addComponent(referLayout, refNameLayout);
		ResourceManager.addComponent(referLayout, refTitleLayout);
		ResourceManager.addComponent(referLayout, refTelLayout);

		ResourceManager.addComponent(telLayout, lblTel);
		ResourceManager.addComponent(telLayout, txtTel);

		ResourceManager.addComponent(faxLayout, lblFax);
		ResourceManager.addComponent(faxLayout, txtFax);

		ResourceManager.addComponent(areaLayout, lblArea);
		ResourceManager.addComponent(areaLayout, txtArea);

		ResourceManager.addComponent(cityLayout, lblCity);
		ResourceManager.addComponent(cityLayout, txtCity);

		ResourceManager.addComponent(countryLayout, lblCountry);
		ResourceManager.addComponent(countryLayout, txtCountry);

		ResourceManager.addComponent(refNameLayout, lblRefName);
		ResourceManager.addComponent(refNameLayout, txtRefName);

		ResourceManager.addComponent(refTitleLayout, lblRefTitle);
		ResourceManager.addComponent(refTitleLayout, txtRefTitle);

		ResourceManager.addComponent(refTelLayout, lblRefTel);
		ResourceManager.addComponent(refTelLayout, txtRefTel);

		ResourceManager.addComponent(emailLayout, lblEmailAddr);
		ResourceManager.addComponent(emailLayout, txtEmailAddr);

		ResourceManager.addComponent(bankLayout, lblBankDetails);
		ResourceManager.addComponent(bankLayout, txtBankDetails);

		ResourceManager.addComponent(paymentLayout, lblPaymentTerms);
		ResourceManager.addComponent(paymentLayout, txtPaymentTerms);

		ResourceManager.addComponent(remarksLayout, lblRemarks);
		ResourceManager.addComponent(remarksLayout, txtRemarks);

		accNoLayout.setComponentAlignment(lblAccNo, Alignment.BOTTOM_CENTER);

		accNameLayout
				.setComponentAlignment(lblAccName, Alignment.BOTTOM_CENTER);

		accTypeLayout
				.setComponentAlignment(lblAccType, Alignment.BOTTOM_CENTER);

		nameEngLayout
				.setComponentAlignment(lblNameEng, Alignment.BOTTOM_CENTER);

		parentAccLayout.setComponentAlignment(lblParentAcc,
				Alignment.BOTTOM_CENTER);

		closeAccLayout.setComponentAlignment(lblCloseAcc,
				Alignment.BOTTOM_CENTER);

		startDateLayout.setComponentAlignment(lblStartDate,
				Alignment.BOTTOM_CENTER);

		todayDateLayout.setComponentAlignment(lblTodayDate,
				Alignment.BOTTOM_CENTER);
		basicLayout.setComponentAlignment(chkLock, Alignment.BOTTOM_RIGHT);

		custCreditLayout.setComponentAlignment(lblCustCredit,
				Alignment.BOTTOM_CENTER);

		currencyCodeLayout.setComponentAlignment(lblCurrencyCode,
				Alignment.BOTTOM_CENTER);

		custInvoiceLayout.setComponentAlignment(lblCustInvDays,
				Alignment.BOTTOM_CENTER);

		currPricelayout.setComponentAlignment(lblCurrPriceNo,
				Alignment.BOTTOM_CENTER);

		parentAccLayout.setComponentAlignment(cmdParentAcc,
				Alignment.BOTTOM_CENTER);
		closeAccLayout.setComponentAlignment(cmdCloseAcc,
				Alignment.BOTTOM_CENTER);
		parentAccLayout.setComponentAlignment(cmdClearParentAcc,
				Alignment.BOTTOM_CENTER);
		closeAccLayout.setComponentAlignment(cmdClearCloseAcc,
				Alignment.BOTTOM_CENTER);

		fullAddrLayout.setComponentAlignment(lblFullAddr,
				Alignment.BOTTOM_CENTER);

		telLayout.setComponentAlignment(lblTel, Alignment.BOTTOM_CENTER);

		faxLayout.setComponentAlignment(lblFax, Alignment.BOTTOM_CENTER);

		areaLayout.setComponentAlignment(lblArea, Alignment.BOTTOM_CENTER);

		cityLayout.setComponentAlignment(lblCity, Alignment.BOTTOM_CENTER);

		refNameLayout
				.setComponentAlignment(lblRefName, Alignment.BOTTOM_CENTER);

		refTitleLayout.setComponentAlignment(lblRefTitle,
				Alignment.BOTTOM_CENTER);

		refTelLayout.setComponentAlignment(lblRefTel, Alignment.BOTTOM_CENTER);

		countryLayout
				.setComponentAlignment(lblCountry, Alignment.BOTTOM_CENTER);

		emailLayout
				.setComponentAlignment(lblEmailAddr, Alignment.BOTTOM_CENTER);

		bankLayout.setComponentAlignment(lblBankDetails,
				Alignment.BOTTOM_CENTER);

		paymentLayout.setComponentAlignment(lblPaymentTerms,
				Alignment.BOTTOM_CENTER);

		remarksLayout
				.setComponentAlignment(lblRemarks, Alignment.BOTTOM_CENTER);

		mainLayout.setExpandRatio(buttonLayout, 0.1f);
		mainLayout.setExpandRatio(tbSheetPanel, 3.9f);

		accNoLayout.setExpandRatio(lblAccNo, 0.1f);
		accNoLayout.setExpandRatio(txtAccno, 3.9f);

		accGridLayout.setExpandRatio(accNoLayout, 1.5f);
		accGridLayout.setExpandRatio(accNameLayout, 2.5f);

		accNameLayout.setExpandRatio(lblAccName, 0.1f);
		accNameLayout.setExpandRatio(txtAccName, 3.9f);

		accTypeHorzLayout.setExpandRatio(accTypeLayout, 1.5f);
		accTypeHorzLayout.setExpandRatio(nameEngLayout, 2.5f);

		accTypeLayout.setExpandRatio(lblAccType, 0.1f);
		accTypeLayout.setExpandRatio(txtAccType, 3.9f);

		nameEngLayout.setExpandRatio(lblNameEng, 0.1f);
		nameEngLayout.setExpandRatio(txtNameEng, 3.9f);

		parentAccLayout.setExpandRatio(lblParentAcc, 0.1f);
		parentAccLayout.setExpandRatio(txtParentAcc, 0.5f);
		parentAccLayout.setExpandRatio(cmdParentAcc, 0.1f);
		parentAccLayout.setExpandRatio(cmdClearParentAcc, 0.1f);
		parentAccLayout.setExpandRatio(txtParentName, 3.2f);

		closeAccLayout.setExpandRatio(lblCloseAcc, 0.1f);
		closeAccLayout.setExpandRatio(txtCloseAcc, 0.5f);
		closeAccLayout.setExpandRatio(cmdCloseAcc, 0.1f);
		closeAccLayout.setExpandRatio(cmdClearCloseAcc, 0.1f);
		closeAccLayout.setExpandRatio(txtCloseName, 3.2f);

		dateGridLayout.setExpandRatio(startDateLayout, 2f);
		dateGridLayout.setExpandRatio(todayDateLayout, 2f);

		startDateLayout.setExpandRatio(lblStartDate, 0.1f);
		startDateLayout.setExpandRatio(txtStartDate, 3.9f);

		todayDateLayout.setExpandRatio(lblTodayDate, 0.1f);
		todayDateLayout.setExpandRatio(txtTodayDate, 3.9f);

		creditGridLayout.setExpandRatio(custCreditLayout, 1.5f);
		creditGridLayout.setExpandRatio(currencyCodeLayout, 2.5f);

		custCreditLayout.setExpandRatio(lblCustCredit, 0.1f);
		custCreditLayout.setExpandRatio(txtCustCredit, 3.9f);

		currencyCodeLayout.setExpandRatio(lblCurrencyCode, 0.1f);
		currencyCodeLayout.setExpandRatio(txtCurrencyCode, 3.9f);

		invoiceHorzLayout.setExpandRatio(custInvoiceLayout, 1.5f);
		invoiceHorzLayout.setExpandRatio(currPricelayout, 2.5f);

		custInvoiceLayout.setExpandRatio(lblCustInvDays, 0.1f);
		custInvoiceLayout.setExpandRatio(txtCustInvoiceDays, 3.9f);

		currPricelayout.setExpandRatio(lblCurrPriceNo, 0.1f);
		currPricelayout.setExpandRatio(txtCurrentPrice, 3.9f);

		fullAddrLayout.setExpandRatio(lblFullAddr, 0.1f);
		fullAddrLayout.setExpandRatio(txtFullAddr, 3.9f);

		contactLayout.setExpandRatio(telLayout, 2f);
		contactLayout.setExpandRatio(faxLayout, 2f);

		telLayout.setExpandRatio(lblTel, 0.1f);
		telLayout.setExpandRatio(txtTel, 3.9f);

		faxLayout.setExpandRatio(lblFax, 0.1f);
		faxLayout.setExpandRatio(txtFax, 3.9f);

		contactGridLayout.setExpandRatio(areaLayout, 1.5f);
		contactGridLayout.setExpandRatio(cityLayout, 1.5f);
		contactGridLayout.setExpandRatio(countryLayout, 1f);

		referLayout.setExpandRatio(refNameLayout, 1.5f);
		referLayout.setExpandRatio(refTitleLayout, 1.5f);
		referLayout.setExpandRatio(refTelLayout, 1f);

		areaLayout.setExpandRatio(lblArea, 0.1f);
		areaLayout.setExpandRatio(txtArea, 3.9f);

		cityLayout.setExpandRatio(lblCity, 0.1f);
		cityLayout.setExpandRatio(txtCity, 3.9f);

		countryLayout.setExpandRatio(lblCountry, 0.1f);
		countryLayout.setExpandRatio(txtCountry, 3.9f);

		refNameLayout.setExpandRatio(lblRefName, 0.1f);
		refNameLayout.setExpandRatio(txtRefName, 3.9f);

		refTitleLayout.setExpandRatio(lblRefTitle, 0.1f);
		refTitleLayout.setExpandRatio(txtRefTitle, 3.9f);

		refTelLayout.setExpandRatio(lblRefTel, 0.1f);
		refTelLayout.setExpandRatio(txtRefTel, 3.9f);

		emailLayout.setExpandRatio(lblEmailAddr, 0.1f);
		emailLayout.setExpandRatio(txtEmailAddr, 3.9f);

		bankLayout.setExpandRatio(lblBankDetails, 0.1f);
		bankLayout.setExpandRatio(txtBankDetails, 3.9f);

		paymentLayout.setExpandRatio(lblPaymentTerms, 0.1f);
		paymentLayout.setExpandRatio(txtPaymentTerms, 3.9f);

		remarksLayout.setExpandRatio(lblRemarks, 0.1f);
		remarksLayout.setExpandRatio(txtRemarks, 3.9f);

		checkLayout.setExpandRatio(emptyLabel1, 1f);
		checkLayout.setExpandRatio(chkIsCustomer, 1f);
		checkLayout.setExpandRatio(chkBankCash, 0.8f);
		checkLayout.setExpandRatio(chkSupplies, 1.2f);

		invoiceLayout.setExpandRatio(emptyLabel2, 1f);
		invoiceLayout.setExpandRatio(chkDiscOnInvoice, 1f);
		invoiceLayout.setExpandRatio(chkisFixedPrice, 1f);
		invoiceLayout.setExpandRatio(chkDiscByPrice, 1f);

		txtAccType.removeAllItems();
		txtAccType.addItem(new dataCell("Normal", "0"));
		txtAccType.addItem(new dataCell("Closing", "1"));

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
			cmdCloseAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listCloseAcc();
				}
			});
			cmdClearParentAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtParentAcc.setReadOnly(false);
					txtParentName.setReadOnly(false);
					txtParentAcc.setValue("");
					txtParentName.setValue("");
					txtParentAcc.setReadOnly(true);
					txtParentName.setReadOnly(true);
				}
			});
			cmdClearCloseAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtCloseAcc.setReadOnly(false);
					txtCloseName.setReadOnly(false);
					txtCloseAcc.setValue("");
					txtCloseName.setValue("");
					txtCloseAcc.setReadOnly(true);
					txtCloseName.setReadOnly(true);
				}
			});

			listnerAdded = true;
		}

	}

	private void createInfoBox() {
		try {
			if (show_tree_panel) {
				if (accBrowse == null) {
					accBrowse = InfoBox.createInstance(data_Items, "ACCNO",
							"PARENTACC", "NAME", "CHILDCOUNT", "Accounts",
							"USECOUNT", "");

					m_mainLayout.addComponent(accBrowse);
					accBrowse.setHeight("100%");
					accBrowse.setWidth("300px");
					accBrowse.tree.addActionHandler(new Handler() {

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
										"50%", "90%", true, true);
								frmMasterAccount frm = new frmMasterAccount();
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
								createAcc(m.getId());
							}

							if (action.getCaption().equals(
									mapActAccBrows.get("create_branch_n"))) {
								Window wnd = ControlsFactory.CreateWindow(
										"50%", "90%", true, true);
								frmMasterAccount frm = new frmMasterAccount();
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
							if (accBrowse.tree.getValue() != null) {
								menuItem itm = (menuItem) accBrowse.tree
										.getValue();
								QRYSES = itm.getId();
								txtAccName.focus();
								load_data();
							} else {
								QRYSES = "";
								load_data();
								txtAccName.focus();
							}
						}
					};
				}
				data_Items
						.executeQuery(
								"select accno,name,childcount,usecount,path,parentacc from acaccount order by path",
								true);
				m_mainLayout.removeComponent(accBrowse);
				m_mainLayout.addComponent(accBrowse);
				accBrowse.initView();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void createAcc(String acc) {
		if (!txtAccno.isReadOnly()) {
			txtAccno.setValue(utils.getNewAccNo(acc, con));
			utilsVaadin.setValueByForce(txtParentName, utils
					.getSqlValue("select name from"
							+ " acaccount where accno='" + acc + "'", con));
			utilsVaadin.setValueByForce(txtParentAcc, "");
			if (txtParentName.getValue() != null)
				utilsVaadin.setValueByForce(txtParentAcc, acc);

		}
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		m_mainLayout.removeAllComponents();
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		otherLayout.removeAllComponents();
		buttonLayout.removeAllComponents();
		accNoLayout.removeAllComponents();
		accNameLayout.removeAllComponents();
		accTypeLayout.removeAllComponents();
		nameEngLayout.removeAllComponents();
		parentAccLayout.removeAllComponents();
		closeAccLayout.removeAllComponents();
		startDateLayout.removeAllComponents();
		todayDateLayout.removeAllComponents();
		checkLayout.removeAllComponents();
		custCreditLayout.removeAllComponents();
		currencyCodeLayout.removeAllComponents();
		dateGridLayout.removeAllComponents();
		accGridLayout.removeAllComponents();
		creditGridLayout.removeAllComponents();
		custInvoiceLayout.removeAllComponents();
		invoiceLayout.removeAllComponents();
		currPricelayout.removeAllComponents();
		areaLayout.removeAllComponents();
		cityLayout.removeAllComponents();
		countryLayout.removeAllComponents();
		refNameLayout.removeAllComponents();
		refTitleLayout.removeAllComponents();
		refTelLayout.removeAllComponents();
		telLayout.removeAllComponents();
		faxLayout.removeAllComponents();
		fullAddrLayout.removeAllComponents();
		emailLayout.removeAllComponents();
		bankLayout.removeAllComponents();
		paymentLayout.removeAllComponents();
		remarksLayout.removeAllComponents();
		contactLayout.removeAllComponents();
		accTypeLayout.removeAllComponents();
		contactGridLayout.removeAllComponents();
		accTypeHorzLayout.removeAllComponents();
		referLayout.removeAllComponents();

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	protected void fetchAccountInfo() {
		txtParentName.setReadOnly(false);
		txtCloseName.setReadOnly(false);
		txtParentName.setValue(utils.getSqlValue(
				"Select Name from Acaccount where AccNo = '"
						+ txtParentAcc.getValue().toString() + "'", con));

		txtCloseName.setValue(utils.getSqlValue(
				"Select Name from Acaccount where AccNo = '"
						+ txtCloseAcc.getValue().toString() + "'", con));
		txtParentName.setReadOnly(true);
		txtCloseName.setReadOnly(true);
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
							QRYSES = tv.getData().getFieldValue(rn, "accno")
									.toString();
							load_data();
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
											txtParentName.setReadOnly(false);
											txtParentAcc.setReadOnly(false);
											int rn = tv.getSelectionValue();
											txtParentName.setValue(tv.getData()
													.getFieldValue(rn, "NAME")
													.toString());
											txtParentAcc.setValue(tv.getData()
													.getFieldValue(rn, "ACCNO")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										} finally {
											txtParentName.setReadOnly(true);
											txtParentAcc.setReadOnly(true);
										}
									}
								}
							},
							con,
							"select AccNo, Name from Acaccount where actype = 0 and usecount=0 order by path",
							true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_listCloseAcc() {
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
											txtCloseName.setReadOnly(false);
											txtCloseAcc.setReadOnly(false);
											int rn = tv.getSelectionValue();
											txtCloseName.setValue(tv.getData()
													.getFieldValue(rn, "Name")
													.toString());
											txtCloseAcc.setValue(tv.getData()
													.getFieldValue(rn, "AccNo")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										} finally {
											txtParentName.setReadOnly(true);
											txtParentAcc.setReadOnly(true);
										}
									}
								}
							},
							con,
							"select AccNo, Name from Acaccount where actype =1 order by path",
							true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
