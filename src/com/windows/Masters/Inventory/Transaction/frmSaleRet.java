package com.windows.Masters.Inventory.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
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

public class frmSaleRet implements transactionalForm {

	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();

	private Panel mainPanel = new Panel();
	private Panel tbSheetPanel = new Panel();
	private Panel tablePanel = new Panel();
	private Panel footerPanel = new Panel();

	private localTableModel data_Items = new localTableModel();

	private VerticalLayout tableLayout = new VerticalLayout();
	TableLayoutVaadin table = new TableLayoutVaadin(tableLayout);
	private final List<ColumnProperty> lstBranchCols = new ArrayList<ColumnProperty>();

	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout salesBasicLayout = new VerticalLayout();
	private VerticalLayout otherInfoBasicLayout = new VerticalLayout();
	private VerticalLayout tableTextLayout = new VerticalLayout();

	private HorizontalLayout buttonLayout = new HorizontalLayout();

	private HorizontalLayout saleContent1Layout = new HorizontalLayout();
	private HorizontalLayout invoiceNoLayout = new HorizontalLayout();
	private HorizontalLayout dateLayout = new HorizontalLayout();
	private HorizontalLayout storeLayout = new HorizontalLayout();

	private HorizontalLayout saleContent2Layout = new HorizontalLayout();
	private HorizontalLayout customerLayout = new HorizontalLayout();
	private HorizontalLayout branchNoLayout = new HorizontalLayout();

	private HorizontalLayout saleContent3Layout = new HorizontalLayout();
	private HorizontalLayout typeLayout = new HorizontalLayout();
	private HorizontalLayout salesTypeLayout = new HorizontalLayout();

	private HorizontalLayout saleContent4Layout = new HorizontalLayout();
	private HorizontalLayout salesPerLayout = new HorizontalLayout();

	private HorizontalLayout saleContent5Layout = new HorizontalLayout();
	private HorizontalLayout remarksLayout = new HorizontalLayout();
	private HorizontalLayout referenceLayout = new HorizontalLayout();

	private HorizontalLayout totQtyLayout = new HorizontalLayout();

	private HorizontalLayout saleContent9Layout = new HorizontalLayout();

	private HorizontalLayout saleContent10Layout = new HorizontalLayout();
	private HorizontalLayout grossLayout = new HorizontalLayout();
	private HorizontalLayout genDiscLayout = new HorizontalLayout();
	private HorizontalLayout netAmtLayout = new HorizontalLayout();

	private HorizontalLayout otherInfoContent1Layout = new HorizontalLayout();
	private HorizontalLayout keyLayout = new HorizontalLayout();
	private HorizontalLayout locationCodeLayout = new HorizontalLayout();

	private HorizontalLayout otherInfoContent2Layout = new HorizontalLayout();
	private HorizontalLayout checkLayout = new HorizontalLayout();

	private HorizontalLayout otherInfoContent3Layout = new HorizontalLayout();
	private HorizontalLayout vehicleLayout = new HorizontalLayout();
	private HorizontalLayout issuedDateLayout = new HorizontalLayout();

	private Label lblInvoiceNo = new Label("Invoice No");
	private Label lblDate = new Label("Date");
	private Label lblStoreC = new Label("Store");
	private Label lblCustomer = new Label("Customer");
	private Label lblBranch = new Label("Branch");
	private Label lblType = new Label("Type");
	private Label lblSaleP = new Label("Sales P");
	private Label lblSalesType = new Label("Sales Type");
	private Label lblRemarks = new Label("Remarks");
	private Label lblRefer = new Label("Refer");
	private Label lblTotQty = new Label("Tot Qty");
	private Label lblIssuedQt = new Label("Issued Qt");
	private Label lblCommAcc = new Label("Comm Acc");
	private Label lblRate = new Label("Rate");
	private Label lblInsKd = new Label("Comm");
	private Label lblTotDisc = new Label("Tot Disc");
	private Label lblUserName = new Label("User Name");
	private Label lblGross = new Label("Gross");
	private Label lblGenDisc = new Label("Gen Disc");
	private Label lblNetAmt = new Label("Net Amt");
	private Label lblKeyId = new Label("Key Id");

	private Label lblLocationCode = new Label("Loc Code");
	private Label lblIssuedDate = new Label("Recieved Date");

	private CheckBox chkGoodIssued = ControlsFactory.CreateCheckField(
			"Goods Issued", "FLAG", "1", "2", lstfldinfo);

	private TextField txtInvNo = ControlsFactory.CreateTextField(null,
			"INVOICE_NO", lstfldinfo);
	private DateField txtDate = ControlsFactory.CreateDateField(null,
			"INVOICE_DATE", lstfldinfo);
	private ComboBox txtStoreC = ControlsFactory.CreateListField(null, "STRA",
			"Select no,name from store where flag=1 order by no", lstfldinfo);
	private TextField txtCust = ControlsFactory.CreateTextField(null,
			"C_CUS_NO", lstfldinfo);
	private TextField txtCustName = ControlsFactory.CreateTextField(null,
			"INV_REFNM", lstfldinfo);
	private ComboBox txtBranch = ControlsFactory.CreateListField(null,
			"C_BRANCH_NO", "", lstfldinfo);
	private TextField txtTypeNo = ControlsFactory.CreateTextField(null, "TYPE",
			lstfldinfo);
	private TextField txtTypeText = ControlsFactory.CreateTextField(null, "",
			null);
	private TextField txtSaleP = ControlsFactory.CreateTextField(null, "SLSMN",
			lstfldinfo);
	private TextField txtSalePName = ControlsFactory.CreateTextField(null, "",
			null);
	private ComboBox txtSaleType = ControlsFactory.CreateListField(null,
			"POSTTYPE", "", lstfldinfo);
	private TextField txtRemarks = ControlsFactory.CreateTextField(null,
			"MEMO", lstfldinfo);
	private TextField txtRefer = ControlsFactory.CreateTextField(null,
			"REFERENCE_INFORMATION", lstfldinfo);
	private TextField txtTotQty = ControlsFactory.CreateTextField(null, "",
			null);
	private TextField txtIssuedQt = ControlsFactory.CreateTextField(null, "",
			null);
	private TextField txtCommAcc = ControlsFactory.CreateTextField(null,
			"CREDITACC", lstfldinfo);
	private TextField txtCommAccName = ControlsFactory.CreateTextField(null,
			"INS_KD", null);
	private TextField txtRate = ControlsFactory.CreateTextField(null, "RATE",
			lstfldinfo);
	private TextField txtCommKd = ControlsFactory.CreateTextField(null,
			"INS_KD", lstfldinfo);
	private TextField txtTotDisc = ControlsFactory.CreateTextField(null,
			"DISC_AMT", lstfldinfo);
	private TextField txtUserName = ControlsFactory.CreateTextField(null,
			"USERNAME", lstfldinfo);

	private TextField txtGross = ControlsFactory
			.CreateTextField(null, "", null);
	private TextField txtGenDisc = ControlsFactory.CreateTextField(null,
			"CUSTOM_KD", lstfldinfo);
	private TextField txtNetAmt = ControlsFactory.CreateTextField(null, "",
			null);
	private ComboBox txtLocationCode = ControlsFactory.CreateListField(null,
			"LOCATION_CODE", "Select code,name from locations order by code",
			lstfldinfo);
	private DateField txtIssuedDate = ControlsFactory.CreateDateField(null,
			"ISSUED_DATE", lstfldinfo);
	private TextField txtKeField = ControlsFactory.CreateTextField(null,
			"KEYFLD", lstfldinfo);

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");
	private NativeButton cmdPrint = ControlsFactory.CreateCustomButton("Print",
			"img/pdf.png", "Save", "");
	private NativeButton cmdCopyVou = ControlsFactory.CreateCustomButton(
			"Copy", "img/copy.png", "Exporting to pdf", "");
	private NativeButton cmdColumn = ControlsFactory.CreateCustomButton(
			"Columns", "img/columns.png", "Columns", "");

	private NativeButton cmdCust = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearCust = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdType = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearType = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdSalesP = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearSalesPName = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdCommAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearCommAcc = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");

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
			qe.setParaValue("PERIODCODE", "");
			qe.setParaValue("INVOICE_CODE", "12");
			qe.setParaValue("DUEDATE", dt);
			qe.setParaValue("FLAG", "0");
			qe.setParaValue("YEAR", "");
			qe.setParaValue("CREATDT", dt);
			qe.setParaValue("ISCLOSE", "Y");
			qe.setParaValue("ADD_CHARGE", "0");
			qe.setParaValue("ADD_CHARGEX", "0");
			qe.setParaValue("PRINTCOUNT", "0");
			qe.setParaValue("HANDLE_KD", "0.0");
			qe.setParaValue("FRGHT_KD", "0.0");
			qe.setParaValue("FRGHT_FC", "0.0");
			qe.setParaValue("RATE", "0");
			qe.setParaValue("WAGES", "0");
			qe.setParaValue("BANK_CHG", "0");
			qe.setParaValue("OTHER", "0");
			qe.setParaValue("KDCOST", "0");
			qe.setParaValue("CHG_KDAMT", "0");
			qe.setParaValue("CHG_CODE", "0");
			qe.setParaValue("DELIVEREDG", "0");
			qe.setParaValue("DELIVERED", "0");
			qe.setParaValue("PAIDAMT2", "0");
			qe.setParaValue("PAIDAMT1", "0");
			qe.setParaValue("TOTQTY", "0.0");
			qe.setParaValue("TOT_HAS_ISSUED", "0.0");
			qe.setParaValue("NO_OF_ISSUES", "0.0");
			qe.setParaValue("NO_OF_RECIEVED", "0.0");
			qe.setParaValue("TOT_DAMAGE_QTY", "0");

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("PUR1"); // if
			} else {
				qe.AutoGenerateUpdateStatment("PUR1", "'KEYFLD'",
						" WHERE KEYFLD=:KEYFLD"); // if to update..
			}
			qe.execute();
			con.commit();
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtLocationCode.getValue().toString();
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
			utilsVaadin.resetValues(salesBasicLayout, false, false);
			utilsVaadin.resetValues(tableTextLayout, false, false);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"select * from pur1 where invoice_code=12 and keyfld='"
								+ QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
			}
			fetchAccountInfo();
			table.data
					.executeQuery(
							"select pur2.*,items.descr,pur2.pkcost*(pur2.allqty+pur2.free_allqty) cstamt,"
									+ " allqty* ((pur2.price-pur2.disc_amt)/pur2.pack) amount,"
									+ "items.lsprice"
									+ " from pur2,items where items.reference=pur2.refer and "
									+ "  pur2.keyfld='" + QRYSES
									+ "' order by itempos", true);

			table.fill_table();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	protected void fetchAccountInfo() {

	}

	public void applyColumnsOnBranch() {
		lstBranchCols.clear();
		try {
			utilsVaadin.applyColumns("FRMSALE.PUR2", table.getTable(),
					table.listFields, con);
		} catch (SQLException e) {
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
		try {
			if (data_Items.getDbclass() == null) {
				data_Items.createDBClassFromConnection(con);
			}
		} catch (SQLException e) {
		}
		createView();
		load_data();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		mainPanel.setSizeFull();
		mainPanel.getContent().setHeight("-1px");
		mainPanel.setWidth("700px");
		mainLayout.setMargin(true);

		mainLayout.setHeight("-1px");
		mainLayout.setWidth("100%");
		salesBasicLayout.setWidth("100%");

		otherInfoBasicLayout.setWidth("100%");
		saleContent1Layout.setWidth("100%");
		invoiceNoLayout.setWidth("100%");
		dateLayout.setWidth("100%");
		storeLayout.setWidth("100%");
		saleContent2Layout.setWidth("100%");
		customerLayout.setWidth("100%");
		branchNoLayout.setWidth("100%");
		saleContent3Layout.setWidth("100%");
		typeLayout.setWidth("100%");
		salesTypeLayout.setWidth("100%");
		saleContent4Layout.setWidth("100%");
		salesPerLayout.setWidth("100%");
		saleContent5Layout.setWidth("100%");
		remarksLayout.setWidth("100%");
		referenceLayout.setWidth("100%");
		totQtyLayout.setWidth("100%");
		saleContent9Layout.setWidth("100%");

		saleContent10Layout.setWidth("100%");
		grossLayout.setWidth("100%");
		genDiscLayout.setWidth("100%");
		netAmtLayout.setWidth("100%");
		otherInfoContent1Layout.setWidth("100%");
		keyLayout.setWidth("100%");

		locationCodeLayout.setWidth("100%");
		otherInfoContent2Layout.setWidth("100%");
		checkLayout.setWidth("100%");
		otherInfoContent3Layout.setWidth("100%");
		vehicleLayout.setWidth("100%");
		issuedDateLayout.setWidth("100%");
		tableTextLayout.setWidth("100%");

		lblInvoiceNo.setWidth("60px");
		lblDate.setWidth("60px");
		lblStoreC.setWidth("60px");
		lblCustomer.setWidth("60px");
		lblBranch.setWidth("60px");
		lblType.setWidth("60px");
		lblSaleP.setWidth("60px");
		lblSalesType.setWidth("60px");
		lblRemarks.setWidth("60px");
		lblRefer.setWidth("60px");
		lblTotQty.setWidth("60px");
		lblIssuedQt.setWidth("60px");
		lblCommAcc.setWidth("60px");
		lblRate.setWidth("60px");
		lblInsKd.setWidth("60px");
		lblTotDisc.setWidth("60px");
		lblUserName.setWidth("60px");
		lblGross.setWidth("60px");
		lblGenDisc.setWidth("60px");
		lblNetAmt.setWidth("60px");
		lblKeyId.setWidth("60px");
		lblLocationCode.setWidth("60px");
		lblIssuedDate.setWidth("60px");

		txtKeField.setWidth("100%");
		txtInvNo.setWidth("100%");
		txtDate.setWidth("100%");
		txtStoreC.setWidth("100%");
		txtCust.setWidth("100%");
		txtCustName.setWidth("100%");
		txtBranch.setWidth("100%");
		txtTypeNo.setWidth("100%");
		txtTypeText.setWidth("100%");
		txtSaleP.setWidth("100%");
		txtSalePName.setWidth("100%");
		txtSaleType.setWidth("100%");
		txtRemarks.setWidth("100%");
		txtRefer.setWidth("100%");
		txtTotQty.setWidth("100%");
		txtIssuedQt.setWidth("100%");
		txtCommAcc.setWidth("100%");
		txtCommAccName.setWidth("100%");
		txtRate.setWidth("100%");
		txtCommKd.setWidth("100%");
		txtTotDisc.setWidth("100%");
		txtUserName.setWidth("100%");
		txtGross.setWidth("100%");
		txtGenDisc.setWidth("100%");
		txtNetAmt.setWidth("100%");
		txtLocationCode.setWidth("100%");
		txtIssuedDate.setWidth("100%");
		tablePanel.setWidth("100%");
		tableTextLayout.setWidth("100%");
		tbSheetPanel.setWidth("100%");
		tablePanel.setWidth("100%");

		tableLayout.setHeight("300px");
		table.getTable().setHeight("275px");

		tablePanel.getContent().setHeight("-1px");
		tablePanel.setWidth("100%");
		tablePanel.setHeight("100%");
		tablePanel.setScrollable(true);

		tbsheet.addTab(salesBasicLayout, "Sale", null);
		tbsheet.addTab(otherInfoBasicLayout, "Other Info", null);

		salesBasicLayout.addStyleName("formGrayLayout");
		tableTextLayout.addStyleName("formGrayLayout");
		otherInfoBasicLayout.addStyleName("formGrayLayout");

		tbsheet.addTab(salesBasicLayout, "Basic Information", null);
		tbsheet.addTab(otherInfoBasicLayout, "Contracted Items", null);

		utilsVaadin.format_money(txtNetAmt, lstfldinfo, "yellowField");
		utilsVaadin.format_money(txtGenDisc, lstfldinfo, "yellowField");
		utilsVaadin.format_money(txtGross, lstfldinfo, "yellowField");

		txtNetAmt.addStyleName("");

		ResourceManager.addComponent(centralPanel, mainPanel);
		ResourceManager.addComponent(mainPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, tbSheetPanel);
		ResourceManager.addComponent(mainLayout, tablePanel);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdCopyVou);
		ResourceManager.addComponent(buttonLayout, cmdPrint);
		ResourceManager.addComponent(buttonLayout, cmdColumn);
		ResourceManager.addComponent(buttonLayout, cmdCls);
		
		tbSheetPanel.addComponent(tbsheet);

		tablePanel.addComponent(tableTextLayout);

		ResourceManager.addComponent(salesBasicLayout, saleContent1Layout);
		ResourceManager.addComponent(salesBasicLayout, saleContent2Layout);
		ResourceManager.addComponent(salesBasicLayout, saleContent3Layout);
		ResourceManager.addComponent(salesBasicLayout, saleContent4Layout);
		ResourceManager.addComponent(salesBasicLayout, saleContent5Layout);

		ResourceManager.addComponent(tableTextLayout, tableLayout);

		ResourceManager.addComponent(tableTextLayout, saleContent9Layout);
		ResourceManager.addComponent(tableTextLayout, saleContent10Layout);

		ResourceManager.addComponent(otherInfoBasicLayout,
				otherInfoContent1Layout);
		ResourceManager.addComponent(otherInfoBasicLayout,
				otherInfoContent2Layout);
		ResourceManager.addComponent(otherInfoBasicLayout,
				otherInfoContent3Layout);

		ResourceManager.addComponent(saleContent1Layout, invoiceNoLayout);
		ResourceManager.addComponent(saleContent1Layout, dateLayout);
		ResourceManager.addComponent(saleContent1Layout, storeLayout);

		ResourceManager.addComponent(saleContent2Layout, customerLayout);
		ResourceManager.addComponent(saleContent2Layout, branchNoLayout);

		ResourceManager.addComponent(saleContent3Layout, typeLayout);
		ResourceManager.addComponent(saleContent3Layout, salesTypeLayout);

		ResourceManager.addComponent(saleContent4Layout, salesPerLayout);

		ResourceManager.addComponent(saleContent5Layout, remarksLayout);
		ResourceManager.addComponent(saleContent5Layout, referenceLayout);

		ResourceManager.addComponent(saleContent10Layout, grossLayout);
		ResourceManager.addComponent(saleContent10Layout, genDiscLayout);
		ResourceManager.addComponent(saleContent10Layout, netAmtLayout);

		ResourceManager.addComponent(otherInfoContent1Layout, keyLayout);
		ResourceManager.addComponent(otherInfoContent1Layout,
				locationCodeLayout);

		ResourceManager.addComponent(otherInfoContent2Layout, checkLayout);

		ResourceManager.addComponent(otherInfoContent3Layout, vehicleLayout);
		ResourceManager.addComponent(otherInfoContent3Layout, issuedDateLayout);

		ResourceManager.addComponent(invoiceNoLayout, lblInvoiceNo);
		ResourceManager.addComponent(invoiceNoLayout, txtInvNo);

		ResourceManager.addComponent(dateLayout, lblDate);
		ResourceManager.addComponent(dateLayout, txtDate);

		ResourceManager.addComponent(storeLayout, lblStoreC);
		ResourceManager.addComponent(storeLayout, txtStoreC);

		ResourceManager.addComponent(customerLayout, lblCustomer);
		ResourceManager.addComponent(customerLayout, txtCust);
		ResourceManager.addComponent(customerLayout, cmdCust);
		ResourceManager.addComponent(customerLayout, cmdClearCust);
		ResourceManager.addComponent(customerLayout, txtCustName);

		ResourceManager.addComponent(branchNoLayout, lblBranch);
		ResourceManager.addComponent(branchNoLayout, txtBranch);

		ResourceManager.addComponent(typeLayout, lblType);
		ResourceManager.addComponent(typeLayout, txtTypeNo);
		ResourceManager.addComponent(typeLayout, cmdType);
		ResourceManager.addComponent(typeLayout, cmdClearType);
		ResourceManager.addComponent(typeLayout, txtTypeText);

		ResourceManager.addComponent(salesTypeLayout, lblSalesType);
		ResourceManager.addComponent(salesTypeLayout, txtSaleType);

		ResourceManager.addComponent(salesPerLayout, lblSaleP);
		ResourceManager.addComponent(salesPerLayout, txtSaleP);
		ResourceManager.addComponent(salesPerLayout, cmdSalesP);
		ResourceManager.addComponent(salesPerLayout, cmdClearSalesPName);
		ResourceManager.addComponent(salesPerLayout, txtSalePName);

		ResourceManager.addComponent(remarksLayout, lblRemarks);
		ResourceManager.addComponent(remarksLayout, txtRemarks);

		ResourceManager.addComponent(referenceLayout, lblRefer);
		ResourceManager.addComponent(referenceLayout, txtRefer);

		ResourceManager.addComponent(totQtyLayout, lblTotQty);
		ResourceManager.addComponent(totQtyLayout, txtTotQty);

		ResourceManager.addComponent(grossLayout, lblGross);
		ResourceManager.addComponent(grossLayout, txtGross);

		ResourceManager.addComponent(genDiscLayout, lblGenDisc);
		ResourceManager.addComponent(genDiscLayout, txtGenDisc);

		ResourceManager.addComponent(netAmtLayout, lblNetAmt);
		ResourceManager.addComponent(netAmtLayout, txtNetAmt);

		ResourceManager.addComponent(keyLayout, lblKeyId);
		ResourceManager.addComponent(keyLayout, txtKeField);

		ResourceManager.addComponent(locationCodeLayout, lblLocationCode);
		ResourceManager.addComponent(locationCodeLayout, txtLocationCode);

		ResourceManager.addComponent(checkLayout, chkGoodIssued);

		ResourceManager.addComponent(issuedDateLayout, lblIssuedDate);
		ResourceManager.addComponent(issuedDateLayout, txtIssuedDate);

		invoiceNoLayout.setComponentAlignment(lblInvoiceNo,
				Alignment.BOTTOM_CENTER);

		dateLayout.setComponentAlignment(lblDate, Alignment.BOTTOM_CENTER);

		storeLayout.setComponentAlignment(lblStoreC, Alignment.BOTTOM_CENTER);

		branchNoLayout
				.setComponentAlignment(lblBranch, Alignment.BOTTOM_CENTER);

		customerLayout.setComponentAlignment(lblCustomer,
				Alignment.BOTTOM_CENTER);
		customerLayout.setComponentAlignment(cmdCust, Alignment.BOTTOM_CENTER);
		customerLayout.setComponentAlignment(cmdClearCust,
				Alignment.BOTTOM_CENTER);

		typeLayout.setComponentAlignment(lblType, Alignment.BOTTOM_CENTER);

		typeLayout.setComponentAlignment(cmdType, Alignment.BOTTOM_CENTER);

		typeLayout.setComponentAlignment(cmdClearType, Alignment.BOTTOM_CENTER);

		salesPerLayout.setComponentAlignment(lblSaleP, Alignment.BOTTOM_CENTER);
		salesPerLayout
				.setComponentAlignment(cmdSalesP, Alignment.BOTTOM_CENTER);
		salesPerLayout.setComponentAlignment(cmdClearSalesPName,
				Alignment.BOTTOM_CENTER);

		salesTypeLayout.setComponentAlignment(lblSalesType,
				Alignment.BOTTOM_CENTER);

		remarksLayout
				.setComponentAlignment(lblRemarks, Alignment.BOTTOM_CENTER);

		referenceLayout
				.setComponentAlignment(lblRefer, Alignment.BOTTOM_CENTER);

		totQtyLayout.setComponentAlignment(lblTotQty, Alignment.BOTTOM_CENTER);

		grossLayout.setComponentAlignment(lblGross, Alignment.BOTTOM_CENTER);

		genDiscLayout
				.setComponentAlignment(lblGenDisc, Alignment.BOTTOM_CENTER);

		netAmtLayout.setComponentAlignment(lblNetAmt, Alignment.BOTTOM_CENTER);

		keyLayout.setComponentAlignment(lblKeyId, Alignment.BOTTOM_CENTER);

		locationCodeLayout.setComponentAlignment(lblLocationCode,
				Alignment.BOTTOM_CENTER);

		checkLayout.setComponentAlignment(chkGoodIssued,
				Alignment.BOTTOM_CENTER);

		issuedDateLayout.setComponentAlignment(lblIssuedDate,
				Alignment.BOTTOM_CENTER);

		mainLayout.setExpandRatio(buttonLayout, 0.1f);
		mainLayout.setExpandRatio(tbSheetPanel, 2f);
		mainLayout.setExpandRatio(tablePanel, 1.9f);

		saleContent1Layout.setExpandRatio(invoiceNoLayout, 1.4f);
		saleContent1Layout.setExpandRatio(dateLayout, 1.3f);
		saleContent1Layout.setExpandRatio(storeLayout, 1.3f);

		saleContent2Layout.setExpandRatio(customerLayout, 2.7f);
		saleContent2Layout.setExpandRatio(branchNoLayout, 1.3f);

		saleContent3Layout.setExpandRatio(typeLayout, 2.7f);
		saleContent3Layout.setExpandRatio(salesTypeLayout, 1.3f);

		saleContent4Layout.setExpandRatio(salesPerLayout, 4f);

		saleContent5Layout.setExpandRatio(remarksLayout, 2.7f);
		saleContent5Layout.setExpandRatio(referenceLayout, 1.3f);

		saleContent10Layout.setExpandRatio(grossLayout, 1.4f);
		saleContent10Layout.setExpandRatio(genDiscLayout, 1.3f);
		saleContent10Layout.setExpandRatio(netAmtLayout, 1.3f);

		otherInfoContent1Layout.setExpandRatio(keyLayout, 1.3f);
		otherInfoContent1Layout.setExpandRatio(locationCodeLayout, 2.7f);

		otherInfoContent2Layout.setExpandRatio(checkLayout, 1.4f);

		otherInfoContent3Layout.setExpandRatio(vehicleLayout, 2.6f);
		otherInfoContent3Layout.setExpandRatio(issuedDateLayout, 1.4f);

		invoiceNoLayout.setExpandRatio(lblInvoiceNo, 0.1f);
		invoiceNoLayout.setExpandRatio(txtInvNo, 3.9f);

		dateLayout.setExpandRatio(lblDate, 0.1f);
		dateLayout.setExpandRatio(txtDate, 3.9f);

		storeLayout.setExpandRatio(lblStoreC, 0.1f);
		storeLayout.setExpandRatio(txtStoreC, 3.9f);

		customerLayout.setExpandRatio(lblCustomer, 0.1f);
		customerLayout.setExpandRatio(txtCust, 1f);
		customerLayout.setExpandRatio(cmdCust, 0.1f);
		customerLayout.setExpandRatio(cmdClearCust, 0.1f);
		customerLayout.setExpandRatio(txtCustName, 2.7f);

		branchNoLayout.setExpandRatio(lblBranch, 0.1f);
		branchNoLayout.setExpandRatio(txtBranch, 3.9f);

		typeLayout.setExpandRatio(lblType, 0.1f);
		typeLayout.setExpandRatio(txtTypeNo, 1f);
		typeLayout.setExpandRatio(cmdType, 0.1f);
		typeLayout.setExpandRatio(cmdClearType, 0.1f);
		typeLayout.setExpandRatio(txtTypeText, 2.7f);

		salesTypeLayout.setExpandRatio(lblSalesType, 0.1f);
		salesTypeLayout.setExpandRatio(txtSaleType, 3.9f);

		salesPerLayout.setExpandRatio(lblSaleP, 0.1f);
		salesPerLayout.setExpandRatio(txtSaleP, 1f);
		salesPerLayout.setExpandRatio(cmdSalesP, 0.1f);
		salesPerLayout.setExpandRatio(cmdClearSalesPName, 0.1f);
		salesPerLayout.setExpandRatio(txtSalePName, 2.7f);

		remarksLayout.setExpandRatio(lblRemarks, 0.1f);
		remarksLayout.setExpandRatio(txtRemarks, 3.9f);

		referenceLayout.setExpandRatio(lblRefer, 0.1f);
		referenceLayout.setExpandRatio(txtRefer, 3.9f);

		totQtyLayout.setExpandRatio(lblTotQty, 0.1f);
		totQtyLayout.setExpandRatio(txtTotQty, 3.9f);

		grossLayout.setExpandRatio(lblGross, 0.1f);
		grossLayout.setExpandRatio(txtGross, 3.9f);

		genDiscLayout.setExpandRatio(lblGenDisc, 0.1f);
		genDiscLayout.setExpandRatio(txtGenDisc, 3.9f);

		netAmtLayout.setExpandRatio(lblNetAmt, 0.1f);
		netAmtLayout.setExpandRatio(txtNetAmt, 3.9f);

		keyLayout.setExpandRatio(lblKeyId, 0.1f);
		keyLayout.setExpandRatio(txtKeField, 3.9f);

		locationCodeLayout.setExpandRatio(lblLocationCode, 0.1f);
		locationCodeLayout.setExpandRatio(txtLocationCode, 3.9f);

		checkLayout.setExpandRatio(chkGoodIssued, 4f);

		issuedDateLayout.setExpandRatio(lblIssuedDate, 0.1f);
		issuedDateLayout.setExpandRatio(txtIssuedDate, 3.9f);

		otherInfoContent2Layout.setComponentAlignment(checkLayout,
				Alignment.BOTTOM_CENTER);

		applyColumnsOnBranch();
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
			cmdClearSalesPName.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtSaleP.setReadOnly(false);
					txtSalePName.setReadOnly(false);
					txtSaleP.setValue("");
					txtSalePName.setValue("");
					txtSaleP.setReadOnly(true);
					txtSalePName.setReadOnly(true);
				}
			});
			cmdClearCust.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtCust.setReadOnly(false);
					txtCustName.setReadOnly(false);
					txtCust.setValue("");
					txtCustName.setValue("");
					txtCust.setReadOnly(true);
					txtCustName.setReadOnly(true);
				}
			});
			cmdSalesP.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_SalesPList();
				}
			});
			cmdCust.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_CustList();
				}
			});
			cmdCommAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_CommAccist();
				}
			});

			cmdType.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_TypeList();
				}
			});

			cmdClearCommAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtCommAcc.setReadOnly(false);
					txtCommAccName.setReadOnly(false);
					txtCommAcc.setValue("");
					txtCommAccName.setValue("");
					txtCommAcc.setReadOnly(true);
					txtCommAccName.setReadOnly(true);
				}
			});
			cmdClearType.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtTypeNo.setReadOnly(false);
					txtTypeText.setReadOnly(false);
					txtTypeNo.setValue("");
					txtTypeText.setValue("");
					txtTypeNo.setReadOnly(true);
					txtTypeText.setReadOnly(true);
				}
			});
			cmdClearCust.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtCust.setReadOnly(false);
					txtCustName.setReadOnly(false);
					txtCust.setValue("");
					txtCustName.setValue("");
					txtCust.setReadOnly(true);
					txtCustName.setReadOnly(true);
				}
			});
			cmdClearSalesPName.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtSaleP.setReadOnly(false);
					txtSalePName.setReadOnly(false);
					txtSaleP.setValue("");
					txtSalePName.setValue("");
					txtSaleP.setReadOnly(true);
					txtSalePName.setReadOnly(true);
				}
			});
			listnerAdded = true;
		}
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractOrderedLayout) parentLayout;
	}

	public void showInitView() {
		QRYSES = "";
		initForm();
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
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
											QRYSES = tv
													.getData()
													.getFieldValue(rn, "KEYFLD")
													.toString();
											load_data();
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select invoice_no,invoice_date,c_cus_no,inv_refnm cust_name,round(inv_amt-disc_amt,3)"
									+ " net_amount,keyfld from pur1 where invoice_code=12 order by invoice_date desc , invoice_no desc",
							true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_CommAccist() {
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
											txtCommAccName.setValue(tv
													.getData().getFieldValue(
															rn, "name")
													.toString());
											txtCommAcc.setValue(tv.getData()
													.getFieldValue(rn, "accno")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select  accno,name from acaccount where actype=0  and childcount=0",
							true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_TypeList() {
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
							txtTypeText.setValue(tv.getData().getFieldValue(rn,
									"descr").toString());
							txtTypeNo.setValue(tv.getData().getFieldValue(rn,
									"no").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "Select no, descr from invoicetype where location_code= '"
					+ txtLocationCode.getValue() + "' and flag=1", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_SalesPList() {
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
							txtSalePName.setValue(tv.getData().getFieldValue(
									rn, "name").toString());
							txtSaleP.setValue(tv.getData().getFieldValue(rn,
									"no").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "Select no,name from salesp where type='S' order by no",
					true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_CustList() {
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
											txtCustName.setValue(tv.getData()
													.getFieldValue(rn, "name")
													.toString());
											txtCust.setValue(tv.getData()
													.getFieldValue(rn, "code")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"Select code,name  from c_ycust  where ISCUST='Y' order by path",
							true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
