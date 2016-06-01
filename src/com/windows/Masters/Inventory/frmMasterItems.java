package com.windows.Masters.Inventory;

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
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmMasterItems implements transactionalForm {

	private AbstractLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	TabSheet tbsheet = new TabSheet();
	private localTableModel data_prices = new localTableModel();
	private Table tbl_prices = new Table("Recipt Vouchers");

	private localTableModel data_Items = new localTableModel();
	private final List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	private HorizontalLayout m_mainLayout = new HorizontalLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout keyLayout = new HorizontalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();
	private HorizontalLayout checkLayout = new HorizontalLayout();
	private HorizontalLayout contentLayout = new HorizontalLayout();
	private HorizontalLayout prodLayout = new HorizontalLayout();
	private HorizontalLayout BarcodeLayout = new HorizontalLayout();
	private HorizontalLayout packDLayout = new HorizontalLayout();
	private HorizontalLayout unitDLayout = new HorizontalLayout();
	private HorizontalLayout packLayout = new HorizontalLayout();
	private HorizontalLayout fractLayout = new HorizontalLayout();
	private HorizontalLayout lowSellPrLayout = new HorizontalLayout();
	private HorizontalLayout remarksLayout = new HorizontalLayout();
	private HorizontalLayout descr1Layout = new HorizontalLayout();
	private HorizontalLayout descr2Layout = new HorizontalLayout();
	private HorizontalLayout suppLayout = new HorizontalLayout();
	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout priceLayout = new HorizontalLayout();
	private HorizontalLayout statusLayout = new HorizontalLayout();
	private HorizontalLayout purPriceLayout = new HorizontalLayout();
	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout partLayout = new HorizontalLayout();
	private HorizontalLayout powerLayout = new HorizontalLayout();
	private HorizontalLayout avgCostLayout = new HorizontalLayout();
	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout orinEngLayout = new HorizontalLayout();
	private HorizontalLayout orinArbLayout = new HorizontalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private VerticalLayout othersBasicLayout = new VerticalLayout();
	private HorizontalLayout tableLayout = new HorizontalLayout();
	private HorizontalLayout salesAccLayout = new HorizontalLayout();
	private HorizontalLayout salesRAccLayout = new HorizontalLayout();
	private HorizontalLayout purchaseAccLayout = new HorizontalLayout();
	private HorizontalLayout purchaseRAccLayout = new HorizontalLayout();
	private HorizontalLayout adjPAccLayout = new HorizontalLayout();
	private HorizontalLayout adjMAccLayout = new HorizontalLayout();
	private HorizontalLayout saleDiscAccLayout = new HorizontalLayout();
	private HorizontalLayout purDiscAccLayout = new HorizontalLayout();
	private HorizontalLayout storeAccLayout = new HorizontalLayout();
	private HorizontalLayout expAccLayout = new HorizontalLayout();
	private GridLayout gridLayout = new GridLayout(2, 2);
	private VerticalLayout pkqtyLayout = new VerticalLayout();
	private HorizontalLayout alarmLayout = new HorizontalLayout();
	private HorizontalLayout blockLayout = new HorizontalLayout();

	private HorizontalLayout searchLayout = new HorizontalLayout();
	private HorizontalLayout content5Layout = new HorizontalLayout();
	private HorizontalLayout content6Layout = new HorizontalLayout();
	private HorizontalLayout parentItemLayout = new HorizontalLayout();

	TableLayoutVaadin table = new TableLayoutVaadin(tableLayout);

	private Panel tbSheetPanel = new Panel();

	InfoBox itmBrowse = null;

	private NativeButton cmdParent = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearParent = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdSupp = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearSupp = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");

	private Label lblKeyId = new Label("Key Id");
	private Label lblProdCode = new Label("Prod. Code");
	private Label lblBarCode = new Label("Barcode");
	private Label lblDescr1 = new Label("Descr1");
	private Label lblDescr2 = new Label("Descr2");
	private Label lblParentItem = new Label("Parent Item");
	private Label lblPackD = new Label("Pack D");
	private Label lblUnitD = new Label("Unit D");
	private Label lblPack = new Label("Pack");
	private Label lblPackFract = new Label("Pack Fract");
	private Label lblLowestSellingPrice = new Label("Low Sell Pr");
	private Label lblRemarks = new Label("Remarks");
	private Label lblSupplier = new Label("Supplier");
	private Label lblPrice1 = new Label("Price 1");
	private Label lblStatus = new Label("Status");
	private Label lblPurPrice = new Label("Pur Price");
	private Label lblPartNo = new Label("Part No");
	private Label lblPower = new Label("Power");
	private Label lblAvgCost = new Label("Avg. Cost");
	private Label lblOriEng = new Label("Ori Eng");
	private Label lblOriArb = new Label("Ori Arb");

	private Label emptyLabel = new Label("");

	private TextField txtKeyId = ControlsFactory.CreateTextField(null,
			"keyfld", lstfldinfo);
	private TextField txtProCode = ControlsFactory.CreateTextField(null,
			"reference", lstfldinfo);
	private TextField txtBarCode = ControlsFactory.CreateTextField(null,
			"barcode", lstfldinfo);
	private TextField txtDescr1 = ControlsFactory.CreateTextField(null,
			"descr", lstfldinfo);
	private TextField txtDescr2 = ControlsFactory.CreateTextField(null,
			"descra", lstfldinfo);
	private TextField txtParentItem = ControlsFactory.CreateTextField(null,
			"parentitem", lstfldinfo);
	private TextField txtPName = ControlsFactory.CreateTextField(null, "pname",
			null);
	private TextField txtPackD = ControlsFactory.CreateTextField(null, "packd",
			lstfldinfo);
	private TextField txtUnitD = ControlsFactory.CreateTextField(null, "unitd",
			lstfldinfo);
	private TextField txtPack = ControlsFactory.CreateTextField(null, "pack",
			lstfldinfo);
	private TextField txtPackFract = ControlsFactory.CreateTextField(null,
			"indeg", lstfldinfo);
	private TextField txtLowSellPrice = ControlsFactory.CreateTextField(null,
			"lsprice", lstfldinfo);
	private TextField txtRemarks = ControlsFactory.CreateTextField(null,
			"remark", lstfldinfo);
	private TextField txtSupplier = ControlsFactory.CreateTextField(null,
			"mfcode", lstfldinfo);
	private TextField txtSupName = ControlsFactory.CreateTextField(null,
			"supname", null);
	private TextField txtPrice1 = ControlsFactory.CreateTextField(null,
			"price1", lstfldinfo);
	private TextField txtStatus = ControlsFactory.CreateTextField(null,
			"status", lstfldinfo);
	private TextField txtPurPrice = ControlsFactory.CreateTextField(null,
			"lpprice", lstfldinfo);
	private TextField txtPartNo = ControlsFactory.CreateTextField(null,
			"partno", lstfldinfo);
	private TextField txtPower = ControlsFactory.CreateTextField(null, "power",
			lstfldinfo);
	private TextField txtAvgCost = ControlsFactory.CreateTextField(null,
			"pkaver", lstfldinfo);
	private TextField txtOriginEng = ControlsFactory.CreateTextField(null,
			"orig_eng", lstfldinfo);
	private TextField txtOriginArb = ControlsFactory.CreateTextField(null,
			"orig_arb", lstfldinfo);
	private CheckBox chkLock = ControlsFactory.CreateCheckField(
			"Lock / Unlock", "flag", "1", "2", lstfldinfo);
	private CheckBox chkStock = ControlsFactory.CreateCheckField("Non Stock",
			"itprice4", "1", "2", lstfldinfo);
	private CheckBox chkhasExpiryDate = ControlsFactory.CreateCheckField(
			"Has Expiry Date", "hasexpirydate", "1", "2", lstfldinfo);

	private TextField txtQtyOnAlarm = ControlsFactory.CreateTextField(null,
			"alarm_pkqty", null); // tobe rectify later
	private TextField txtItem438 = ControlsFactory.CreateTextField(null, "",
			null);
	private TextField txtBlockIssue = ControlsFactory.CreateTextField(null,
			"QTYDENIED", lstfldinfo);
	private TextField txtItem439 = ControlsFactory.CreateTextField(null, "",
			null);
	private TextField txtDefPrize = ControlsFactory.CreateTextField(null,
			"default_price", lstfldinfo);
	private TextField txtSaleAcc = ControlsFactory.CreateTextField(null,
			"salacc", lstfldinfo);
	private TextField txtSaleAccDisplay = ControlsFactory.CreateTextField(null,
			"", null);
	private TextField txtPurAcc = ControlsFactory.CreateTextField(null,
			"puracc", lstfldinfo);
	private TextField txtPurAccDisplay = ControlsFactory.CreateTextField(null,
			"", null);
	private TextField txtAdjPAcc = ControlsFactory.CreateTextField(null,
			"adjpacc", lstfldinfo);
	private TextField txtAdjPAccDisplay = ControlsFactory.CreateTextField(null,
			"", null);
	private TextField txtSalesDiscAcc = ControlsFactory.CreateTextField(null,
			"saldiscacc", lstfldinfo);
	private TextField txtSalesDiscAccDisplay = ControlsFactory.CreateTextField(
			"", "", null);
	private TextField txtStoreAcc = ControlsFactory.CreateTextField(null,
			"storeacc", lstfldinfo);
	private TextField txtStoreAccNameDisplay = ControlsFactory.CreateTextField(
			"", "", null);
	private TextField txtSaleRAcc = ControlsFactory.CreateTextField(null,
			"salracc", lstfldinfo);
	private TextField txtSaleRAccDisplay = ControlsFactory.CreateTextField(
			null, "", null);
	private TextField txtPurRAcc = ControlsFactory.CreateTextField(null,
			"purracc", lstfldinfo);
	private TextField txtPurRAccDisplay = ControlsFactory.CreateTextField(null,
			"", null);
	private TextField txtAdjMAcc = ControlsFactory.CreateTextField(null,
			"adjmacc", lstfldinfo);
	private TextField txtAdjMAccDisplay = ControlsFactory.CreateTextField(null,
			"", null);
	private TextField txtPurDiscAcc = ControlsFactory.CreateTextField(null,
			"purdiscacc", lstfldinfo);
	private TextField txtPurDiscAccDisplay = ControlsFactory.CreateTextField(
			"", "", null);
	private TextField txtExpAcc = ControlsFactory.CreateTextField(null,
			"expacc", lstfldinfo);
	private TextField txtExpAccNameDisplay = ControlsFactory.CreateTextField(
			"", "", null);
	private TextField txtSearchTree = ControlsFactory.CreateTextField("Search",
			"", null);

	private Label lblQtyOnAlarm = new Label("Qty On Alarm");
	private Label lblBlockIssue = new Label("Block Issues");
	private Label lblSaleAcc = new Label("Sale A/c");
	private Label lblPurchaseAcc = new Label("Purchase A/c");
	private Label lblAdjPAcc = new Label("Adj + A/c");
	private Label lblSalesDiscAcc = new Label("Sales Disc A/c");
	private Label lblStoreAcc = new Label("Store A/c");
	private Label lblSalesRAcc = new Label("Sales R. A/c");
	private Label lblPurchaseRAcc = new Label("P.Ret. A/c");
	private Label lblAdjMAcc = new Label("Adj - A/c");
	private Label lblPurDiscAcc = new Label("Pur. Disc. A/c");
	private Label lblExpAcc = new Label("Expense A/c");
	private Label lblMessage = new Label("");

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");

	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");

	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");

	public boolean show_tree_panel = true;

	private NativeButton cmdSearchTree = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdSalesAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdPurchaseAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdAdjPAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdSaleDiscAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdStoreAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdSaleRAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdPurchaseRAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdAdjMAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdPurchasDiscAcc = ControlsFactory
			.CreateToolbarButton("img/find.png", "Find...");
	private NativeButton cmdExpenseAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");

	private java.util.Map<String, String> mapActAccBrows = new HashMap<String, String>();

	public void resetFormLayout() {

		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		m_mainLayout.removeAllComponents();
		mainLayout.removeAllComponents();
		keyLayout.removeAllComponents();
		checkLayout.removeAllComponents();
		prodLayout.removeAllComponents();
		BarcodeLayout.removeAllComponents();
		packDLayout.removeAllComponents();
		unitDLayout.removeAllComponents();
		packLayout.removeAllComponents();
		fractLayout.removeAllComponents();
		lowSellPrLayout.removeAllComponents();
		remarksLayout.removeAllComponents();
		descr1Layout.removeAllComponents();
		descr2Layout.removeAllComponents();
		suppLayout.removeAllComponents();
		priceLayout.removeAllComponents();
		statusLayout.removeAllComponents();
		purPriceLayout.removeAllComponents();
		partLayout.removeAllComponents();
		powerLayout.removeAllComponents();
		avgCostLayout.removeAllComponents();
		orinEngLayout.removeAllComponents();
		orinArbLayout.removeAllComponents();
		tbsheet.removeAllComponents();
		buttonLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		othersBasicLayout.removeAllComponents();
		tableLayout.removeAllComponents();
		salesAccLayout.removeAllComponents();
		salesRAccLayout.removeAllComponents();
		purchaseAccLayout.removeAllComponents();
		purchaseRAccLayout.removeAllComponents();
		adjPAccLayout.removeAllComponents();
		adjMAccLayout.removeAllComponents();
		saleDiscAccLayout.removeAllComponents();
		purDiscAccLayout.removeAllComponents();
		storeAccLayout.removeAllComponents();
		expAccLayout.removeAllComponents();
		gridLayout.removeAllComponents();
	}

	protected void fetch_AccountName() {

		utilsVaadin.setValueByForce(txtPName, utils.getSqlValue(
				"Select descr1 from items where reference = '"
						+ txtParentItem.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtSaleAccDisplay, utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtSaleAcc.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtSaleRAccDisplay, utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtSaleRAcc.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtPurAccDisplay, utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtPurAcc.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtPurRAccDisplay, utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtPurRAcc.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtAdjPAccDisplay, utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtAdjPAcc.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtAdjMAccDisplay, utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtAdjMAcc.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtSalesDiscAccDisplay, utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtSalesDiscAcc.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtPurDiscAccDisplay, utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtPurDiscAcc.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtStoreAccNameDisplay, utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtStoreAcc.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtExpAccNameDisplay, utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtExpAcc.getValue().toString() + "'", con));

	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		m_mainLayout.setSizeFull();
		m_mainLayout.setMargin(true);
		m_mainLayout.setSpacing(true);
		basicLayout.setSpacing(true);

		mainLayout.setSizeFull();
		mainLayout.setWidth("700px");
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);

		contentLayout.setWidth("100%");
		prodLayout.setWidth("100%");
		BarcodeLayout.setWidth("100%");
		checkLayout.setWidth("100%");
		packDLayout.setWidth("100%");
		unitDLayout.setWidth("100%");
		packLayout.setWidth("100%");
		parentItemLayout.setWidth("100%");
		fractLayout.setWidth("100%");
		lowSellPrLayout.setWidth("100%");
		remarksLayout.setWidth("100%");
		descr1Layout.setWidth("100%");
		descr2Layout.setWidth("100%");
		suppLayout.setWidth("100%");
		content2Layout.setWidth("100%");
		priceLayout.setWidth("100%");
		statusLayout.setWidth("100%");
		purPriceLayout.setWidth("100%");
		content3Layout.setWidth("100%");
		partLayout.setWidth("100%");
		powerLayout.setWidth("100%");
		avgCostLayout.setWidth("100%");
		content4Layout.setWidth("100%");
		orinEngLayout.setWidth("100%");
		orinArbLayout.setWidth("100%");
		basicLayout.setWidth("100%");
		packLayout.setWidth("100%");
		othersBasicLayout.setWidth("100%");
		content6Layout.setWidth("100%");
		content5Layout.setWidth("100%");
		keyLayout.setWidth("100%");

		gridLayout.setSizeFull();
		tableLayout.setSizeFull();
		searchLayout.setWidth("100%");
		table.getTable().setPageLength(5);  

		lblKeyId.setWidth("77px");
		lblProdCode.setWidth("78px");
		lblBarCode.setWidth("76px");
		lblDescr1.setWidth("69px");
		lblDescr2.setWidth("69px");
		lblParentItem.setWidth("70px");
		lblPackD.setWidth("80px");
		lblUnitD.setWidth("80px");
		lblPack.setWidth("80px");
		lblPackFract.setWidth("80px");
		lblLowestSellingPrice.setWidth("75px");
		lblRemarks.setWidth("68px");
		lblSupplier.setWidth("70px");
		lblPrice1.setWidth("80px");
		lblStatus.setWidth("80px");
		lblPurPrice.setWidth("80px");
		lblPartNo.setWidth("80px");
		lblPower.setWidth("80px");
		lblAvgCost.setWidth("80px");
		lblOriEng.setWidth("80px");
		lblOriArb.setWidth("74px");
		lblSaleAcc.setWidth("80px");
		lblAdjMAcc.setWidth("80px");
		lblAdjPAcc.setWidth("80px");
		lblExpAcc.setWidth("80px");
		lblPurchaseAcc.setWidth("80px");
		lblPurchaseRAcc.setWidth("80px");
		lblPurDiscAcc.setWidth("80px");
		lblSalesDiscAcc.setWidth("80px");
		lblSalesRAcc.setWidth("80px");
		lblStoreAcc.setWidth("80px");
		emptyLabel.setWidth("100px");

		table.setWidth("100%");
		txtItem438.setWidth("100%");
		txtItem439.setWidth("100%");

		tbSheetPanel.setScrollable(true);
		tbSheetPanel.getContent().setHeight("-1px");
		tbSheetPanel.setWidth("700px");
		tbSheetPanel.setHeight("100%");

		basicLayout.addStyleName("formLayout");
		othersBasicLayout.addStyleName("formLayout");

		txtKeyId.setWidth("100%");
		txtDescr1.setWidth("100%");
		txtDescr2.setWidth("100%");
		txtParentItem.setWidth("100%");
		txtPName.setWidth("100%");
		txtRemarks.setWidth("100%");
		txtSupplier.setWidth("100%");
		txtSupName.setWidth("100%");
		txtProCode.setWidth("100%");
		txtBarCode.setWidth("100%");
		txtParentItem.setWidth("100%");
		txtPName.setWidth("100%");
		txtPackD.setWidth("100%");
		txtUnitD.setWidth("100%");
		txtPack.setWidth("100%");
		txtPackFract.setWidth("100%");
		txtLowSellPrice.setWidth("100%");
		txtStatus.setWidth("100%");
		txtPrice1.setWidth("100%");
		txtPurPrice.setWidth("100%");
		txtPartNo.setWidth("100%");
		txtPower.setWidth("100%");
		txtAvgCost.setWidth("100%");
		txtOriginArb.setWidth("100%");
		txtOriginEng.setWidth("100%");
		txtSearchTree.setWidth("100px");

		tbSheetPanel.setScrollable(true);

		basicLayout.setMargin(true);

		tbsheet.addTab(basicLayout, "Basic Information", null);
		tbsheet.addTab(othersBasicLayout, "Others", null);

		ResourceManager.addComponent(centralPanel, m_mainLayout);
		ResourceManager.addComponent(m_mainLayout, mainLayout);
		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, tbSheetPanel);

		tbSheetPanel.addComponent(tbsheet);

		ResourceManager.addComponent(searchLayout, txtSearchTree);
		ResourceManager.addComponent(searchLayout, cmdSearchTree);

		ResourceManager.addComponent(basicLayout, keyLayout);
		ResourceManager.addComponent(basicLayout, contentLayout);
		ResourceManager.addComponent(basicLayout, descr1Layout);
		ResourceManager.addComponent(basicLayout, descr2Layout);
		ResourceManager.addComponent(basicLayout, parentItemLayout);
		ResourceManager.addComponent(basicLayout, content6Layout);
		ResourceManager.addComponent(basicLayout, content2Layout);
		ResourceManager.addComponent(basicLayout, content5Layout);
		ResourceManager.addComponent(basicLayout, remarksLayout);
		ResourceManager.addComponent(basicLayout, suppLayout);
		ResourceManager.addComponent(basicLayout, content3Layout);
		ResourceManager.addComponent(basicLayout, content4Layout);
		ResourceManager.addComponent(basicLayout, content5Layout);

		ResourceManager.addComponent(keyLayout, lblKeyId);
		ResourceManager.addComponent(keyLayout, txtKeyId);
		ResourceManager.addComponent(keyLayout, emptyLabel);

		ResourceManager.addComponent(contentLayout, prodLayout);
		ResourceManager.addComponent(prodLayout, lblProdCode);
		ResourceManager.addComponent(prodLayout, txtProCode);

		ResourceManager.addComponent(contentLayout, BarcodeLayout);
		ResourceManager.addComponent(BarcodeLayout, lblBarCode);
		ResourceManager.addComponent(BarcodeLayout, txtBarCode);

		ResourceManager.addComponent(parentItemLayout, lblParentItem);
		ResourceManager.addComponent(parentItemLayout, txtParentItem);
		ResourceManager.addComponent(parentItemLayout, cmdParent);
		ResourceManager.addComponent(parentItemLayout, cmdClearParent);
		ResourceManager.addComponent(parentItemLayout, txtPName);

		ResourceManager.addComponent(packDLayout, lblPackD);
		ResourceManager.addComponent(packDLayout, txtPackD);

		ResourceManager.addComponent(unitDLayout, lblUnitD);
		ResourceManager.addComponent(unitDLayout, txtUnitD);

		ResourceManager.addComponent(packLayout, lblPack);
		ResourceManager.addComponent(packLayout, txtPack);

		ResourceManager.addComponent(fractLayout, lblPackFract);
		ResourceManager.addComponent(fractLayout, txtPackFract);

		ResourceManager.addComponent(lowSellPrLayout, lblLowestSellingPrice);
		ResourceManager.addComponent(lowSellPrLayout, txtLowSellPrice);

		ResourceManager.addComponent(remarksLayout, lblRemarks);
		ResourceManager.addComponent(remarksLayout, txtRemarks);

		ResourceManager.addComponent(suppLayout, lblSupplier);
		ResourceManager.addComponent(suppLayout, txtSupplier);
		ResourceManager.addComponent(suppLayout, cmdSupp);
		ResourceManager.addComponent(suppLayout, cmdClearSupp);
		ResourceManager.addComponent(suppLayout, txtSupName);

		ResourceManager.addComponent(priceLayout, lblPrice1);
		ResourceManager.addComponent(priceLayout, txtPrice1);

		ResourceManager.addComponent(statusLayout, lblStatus);
		ResourceManager.addComponent(statusLayout, txtStatus);

		ResourceManager.addComponent(purPriceLayout, lblPurPrice);
		ResourceManager.addComponent(purPriceLayout, txtPurPrice);

		ResourceManager.addComponent(partLayout, lblPartNo);
		ResourceManager.addComponent(partLayout, txtPartNo);

		ResourceManager.addComponent(powerLayout, lblPower);
		ResourceManager.addComponent(powerLayout, txtPower);

		ResourceManager.addComponent(avgCostLayout, lblAvgCost);
		ResourceManager.addComponent(avgCostLayout, txtAvgCost);

		ResourceManager.addComponent(orinEngLayout, lblOriEng);
		ResourceManager.addComponent(orinEngLayout, txtOriginEng);

		ResourceManager.addComponent(orinArbLayout, lblOriArb);
		ResourceManager.addComponent(orinArbLayout, txtOriginArb);

		ResourceManager.addComponent(descr1Layout, lblDescr1);
		ResourceManager.addComponent(descr1Layout, txtDescr1);

		ResourceManager.addComponent(descr2Layout, lblDescr2);
		ResourceManager.addComponent(descr2Layout, txtDescr2);

		ResourceManager.addComponent(content6Layout, packDLayout);
		ResourceManager.addComponent(content6Layout, unitDLayout);
		ResourceManager.addComponent(content6Layout, packLayout);

		ResourceManager.addComponent(content2Layout, fractLayout);
		ResourceManager.addComponent(content2Layout, lowSellPrLayout);

		ResourceManager.addComponent(content3Layout, priceLayout);
		ResourceManager.addComponent(content3Layout, statusLayout);
		ResourceManager.addComponent(content3Layout, purPriceLayout);

		ResourceManager.addComponent(content4Layout, partLayout);
		ResourceManager.addComponent(content4Layout, powerLayout);
		ResourceManager.addComponent(content4Layout, avgCostLayout);

		ResourceManager.addComponent(content5Layout, orinEngLayout);
		ResourceManager.addComponent(content5Layout, orinArbLayout);

		ResourceManager.addComponent(othersBasicLayout, tableLayout);
		ResourceManager.addComponent(othersBasicLayout, gridLayout);

		ResourceManager.addComponent(gridLayout, salesAccLayout);
		ResourceManager.addComponent(gridLayout, salesRAccLayout);
		ResourceManager.addComponent(gridLayout, purchaseAccLayout);
		ResourceManager.addComponent(gridLayout, purchaseRAccLayout);
		ResourceManager.addComponent(gridLayout, adjPAccLayout);
		ResourceManager.addComponent(gridLayout, adjMAccLayout);
		ResourceManager.addComponent(gridLayout, saleDiscAccLayout);
		ResourceManager.addComponent(gridLayout, purDiscAccLayout);
		ResourceManager.addComponent(gridLayout, storeAccLayout);
		ResourceManager.addComponent(gridLayout, expAccLayout);

		ResourceManager.addComponent(salesAccLayout, lblSaleAcc);
		ResourceManager.addComponent(salesAccLayout, txtSaleAccDisplay);
		ResourceManager.addComponent(salesAccLayout, cmdSalesAcc);

		ResourceManager.addComponent(salesRAccLayout, lblSalesRAcc);
		ResourceManager.addComponent(salesRAccLayout, txtSaleRAccDisplay);
		ResourceManager.addComponent(salesRAccLayout, cmdSaleRAcc);

		ResourceManager.addComponent(purchaseAccLayout, lblPurchaseAcc);
		ResourceManager.addComponent(purchaseAccLayout, txtPurAccDisplay);
		ResourceManager.addComponent(purchaseAccLayout, cmdPurchaseAcc);

		ResourceManager.addComponent(purchaseRAccLayout, lblPurchaseRAcc);
		ResourceManager.addComponent(purchaseRAccLayout, txtPurRAccDisplay);
		ResourceManager.addComponent(purchaseRAccLayout, cmdPurchaseRAcc);

		ResourceManager.addComponent(adjPAccLayout, lblAdjPAcc);
		ResourceManager.addComponent(adjPAccLayout, txtAdjPAccDisplay);
		ResourceManager.addComponent(adjPAccLayout, cmdAdjPAcc);

		ResourceManager.addComponent(adjMAccLayout, lblAdjMAcc);
		ResourceManager.addComponent(adjMAccLayout, txtAdjMAccDisplay);
		ResourceManager.addComponent(adjMAccLayout, cmdAdjMAcc);

		ResourceManager.addComponent(saleDiscAccLayout, lblSalesDiscAcc);
		ResourceManager.addComponent(saleDiscAccLayout, txtSalesDiscAcc);
		ResourceManager.addComponent(saleDiscAccLayout, cmdSaleDiscAcc);

		ResourceManager.addComponent(purDiscAccLayout, lblPurDiscAcc);
		ResourceManager.addComponent(purDiscAccLayout, txtPurDiscAcc);
		ResourceManager.addComponent(purDiscAccLayout, cmdPurchasDiscAcc);

		ResourceManager.addComponent(storeAccLayout, lblStoreAcc);
		ResourceManager.addComponent(storeAccLayout, txtStoreAccNameDisplay);
		ResourceManager.addComponent(storeAccLayout, cmdStoreAcc);

		ResourceManager.addComponent(expAccLayout, lblExpAcc);
		ResourceManager.addComponent(expAccLayout, txtExpAccNameDisplay);
		ResourceManager.addComponent(expAccLayout, cmdExpenseAcc);

		ResourceManager.addComponent(tableLayout, table);
		ResourceManager.addComponent(tableLayout, pkqtyLayout);

		ResourceManager.addComponent(pkqtyLayout, alarmLayout);
		ResourceManager.addComponent(pkqtyLayout, blockLayout);

		ResourceManager.addComponent(alarmLayout, lblQtyOnAlarm);
		ResourceManager.addComponent(alarmLayout, txtQtyOnAlarm);
		ResourceManager.addComponent(alarmLayout, txtItem438);

		ResourceManager.addComponent(blockLayout, lblBlockIssue);
		ResourceManager.addComponent(blockLayout, txtBlockIssue);
		ResourceManager.addComponent(blockLayout, txtItem439);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdCls);
		ResourceManager.addComponent(buttonLayout, lblMessage);

		ResourceManager.addComponent(keyLayout, chkLock);
		ResourceManager.addComponent(keyLayout, chkStock);

		mainLayout.setExpandRatio(buttonLayout, 0.1f);
		mainLayout.setExpandRatio(tbSheetPanel, 3.9f);

		keyLayout.setComponentAlignment(lblKeyId, Alignment.BOTTOM_CENTER);

		prodLayout.setComponentAlignment(lblProdCode, Alignment.BOTTOM_CENTER);

		BarcodeLayout
				.setComponentAlignment(lblBarCode, Alignment.BOTTOM_CENTER);

		descr1Layout.setComponentAlignment(lblDescr1, Alignment.BOTTOM_CENTER);

		descr2Layout.setComponentAlignment(lblDescr2, Alignment.BOTTOM_CENTER);

		parentItemLayout.setComponentAlignment(lblParentItem,
				Alignment.BOTTOM_CENTER);
		parentItemLayout.setComponentAlignment(cmdParent,
				Alignment.BOTTOM_CENTER);
		parentItemLayout.setComponentAlignment(cmdClearParent,
				Alignment.BOTTOM_CENTER);

		packDLayout.setComponentAlignment(lblPackD, Alignment.BOTTOM_CENTER);

		unitDLayout.setComponentAlignment(lblUnitD, Alignment.BOTTOM_CENTER);

		packLayout.setComponentAlignment(lblPack, Alignment.BOTTOM_CENTER);

		fractLayout
				.setComponentAlignment(lblPackFract, Alignment.BOTTOM_CENTER);

		lowSellPrLayout.setComponentAlignment(lblLowestSellingPrice,
				Alignment.BOTTOM_CENTER);

		remarksLayout
				.setComponentAlignment(lblRemarks, Alignment.BOTTOM_CENTER);

		suppLayout.setComponentAlignment(lblSupplier, Alignment.BOTTOM_CENTER);
		suppLayout.setComponentAlignment(cmdSupp, Alignment.BOTTOM_CENTER);
		suppLayout.setComponentAlignment(cmdClearSupp, Alignment.BOTTOM_CENTER);

		priceLayout.setComponentAlignment(lblPrice1, Alignment.BOTTOM_CENTER);

		statusLayout.setComponentAlignment(lblStatus, Alignment.BOTTOM_CENTER);

		purPriceLayout.setComponentAlignment(lblPurPrice,
				Alignment.BOTTOM_CENTER);

		partLayout.setComponentAlignment(lblPartNo, Alignment.BOTTOM_CENTER);

		powerLayout.setComponentAlignment(lblPower, Alignment.BOTTOM_CENTER);

		avgCostLayout
				.setComponentAlignment(lblAvgCost, Alignment.BOTTOM_CENTER);

		orinEngLayout.setComponentAlignment(lblOriEng, Alignment.BOTTOM_CENTER);

		orinArbLayout.setComponentAlignment(lblOriArb, Alignment.BOTTOM_CENTER);

		keyLayout.setExpandRatio(lblKeyId, 0.1f);
		keyLayout.setExpandRatio(txtKeyId, 1.3f);
		keyLayout.setExpandRatio(emptyLabel, 2.6f);

		contentLayout.setExpandRatio(prodLayout, 2f);
		contentLayout.setExpandRatio(BarcodeLayout, 2f);

		prodLayout.setExpandRatio(lblProdCode, 0.1f);
		prodLayout.setExpandRatio(txtProCode, 3.9f);

		BarcodeLayout.setExpandRatio(lblBarCode, 0.1f);
		BarcodeLayout.setExpandRatio(txtBarCode, 3.9f);

		descr1Layout.setExpandRatio(lblDescr1, 0.1f);
		descr1Layout.setExpandRatio(txtDescr1, 3.9f);

		descr2Layout.setExpandRatio(lblDescr2, 0.1f);
		descr2Layout.setExpandRatio(txtDescr2, 3.9f);
		
		parentItemLayout.setExpandRatio(lblParentItem, 0.1f);
		parentItemLayout.setExpandRatio(txtParentItem, 1f);
		parentItemLayout.setExpandRatio(cmdParent, 0.1f);
		parentItemLayout.setExpandRatio(cmdClearParent, 0.1f);
		parentItemLayout.setExpandRatio(txtPName, 2.7f);

		content6Layout.setExpandRatio(packDLayout, 1.3f);
		content6Layout.setExpandRatio(unitDLayout, 1.3f);
		content6Layout.setExpandRatio(packLayout, 1.4f);

		packDLayout.setExpandRatio(lblPackD, 0.1f);
		packDLayout.setExpandRatio(txtPackD, 3.9f);

		unitDLayout.setExpandRatio(lblUnitD, 0.1f);
		unitDLayout.setExpandRatio(txtUnitD, 3.9f);

		packLayout.setExpandRatio(lblPack, 0.1f);
		packLayout.setExpandRatio(txtPack, 3.9f);

		content2Layout.setExpandRatio(fractLayout, 1.3f);
		content2Layout.setExpandRatio(lowSellPrLayout, 2.7f);

		fractLayout.setExpandRatio(lblPackFract, 0.1f);
		fractLayout.setExpandRatio(txtPackFract, 3.9f);

		lowSellPrLayout.setExpandRatio(lblLowestSellingPrice, 0.1f);
		lowSellPrLayout.setExpandRatio(txtLowSellPrice, 3.9f);

		remarksLayout.setExpandRatio(lblRemarks, 0.1f);
		remarksLayout.setExpandRatio(txtRemarks, 3.9f);

		suppLayout.setExpandRatio(lblSupplier, 0.1f);
		suppLayout.setExpandRatio(txtSupplier, 1f);
		suppLayout.setExpandRatio(cmdSupp, 0.1f);
		suppLayout.setExpandRatio(cmdClearSupp, 0.1f);
		suppLayout.setExpandRatio(txtSupName, 2.7f);

		content3Layout.setExpandRatio(priceLayout, 1.3f);
		content3Layout.setExpandRatio(statusLayout, 1.3f);
		content3Layout.setExpandRatio(purPriceLayout, 1.4f);

		priceLayout.setExpandRatio(lblPrice1, 0.1f);
		priceLayout.setExpandRatio(txtPrice1, 3.9f);

		statusLayout.setExpandRatio(lblStatus, 0.1f);
		statusLayout.setExpandRatio(txtStatus, 3.9f);

		purPriceLayout.setExpandRatio(lblPurPrice, 0.1f);
		purPriceLayout.setExpandRatio(txtPurPrice, 3.9f);

		content4Layout.setExpandRatio(partLayout, 1.3f);
		content4Layout.setExpandRatio(powerLayout, 1.3f);
		content4Layout.setExpandRatio(avgCostLayout, 1.4f);

		partLayout.setExpandRatio(lblPartNo, 0.1f);
		partLayout.setExpandRatio(txtPartNo, 3.9f);

		powerLayout.setExpandRatio(lblPower, 0.1f);
		powerLayout.setExpandRatio(txtPower, 3.9f);

		avgCostLayout.setExpandRatio(lblAvgCost, 0.1f);
		avgCostLayout.setExpandRatio(txtAvgCost, 3.9f);

		content5Layout.setExpandRatio(orinEngLayout, 1.3f);
		content5Layout.setExpandRatio(orinArbLayout, 2.7f);

		orinEngLayout.setExpandRatio(lblOriEng, 0.1f);
		orinEngLayout.setExpandRatio(txtOriginEng, 3.9f);

		orinArbLayout.setExpandRatio(lblOriArb, 0.1f);
		orinArbLayout.setExpandRatio(txtOriginArb, 3.9f);
		createInfoBox();

		if (!listnerAdded) {
			cmdSave.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			cmdDelete.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

				}
			});
			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_list();
				}
			});
			cmdCls.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtProCode.setReadOnly(false);
					QRYSES = "";
					load_data();
				}
			});
			cmdParent.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_ParentAcc();
				}
			});
			cmdSupp.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_SuppAcc();
				}
			});
			cmdClearParent.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtParentItem.setReadOnly(false);
					txtPName.setReadOnly(false);
					txtParentItem.setValue("");
					txtPName.setValue("");
					txtParentItem.setReadOnly(true);
					txtPName.setReadOnly(true);
				}
			});
			cmdClearSupp.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtSupplier.setReadOnly(false);
					txtSupName.setReadOnly(false);
					txtSupplier.setValue("");
					txtSupName.setValue("");
					txtSupplier.setReadOnly(true);
					txtSupName.setReadOnly(true);
				}
			});
			txtProCode.addListener(new BlurListener() {
				public void blur(BlurEvent event) {
					if (!txtProCode.isReadOnly()
							&& txtProCode.getValue() != null) {
						String fnd = utils.getSqlValue(
								"select * from items where reference='"
										+ txtProCode.getValue().toString()
										+ "'", con);
						if (!fnd.isEmpty()) {
							parentLayout.getWindow().showNotification(
									"Found # " + fnd);
							QRYSES = txtProCode.getValue().toString()
									.toUpperCase();
							load_data();
						}
					}
				}
			});

			listnerAdded = true;
		}
		applyColumns();
	}

	private void createInfoBox() {
		try {
			if (show_tree_panel) {
				if (itmBrowse == null) {
					itmBrowse = InfoBox.createInstance(data_Items, "REFERENCE",
							"PARENTITEM", "DESCR", "CHILDCOUNTS", "Items",
							"USECOUNTS", "");
					m_mainLayout.addComponent(itmBrowse);
					itmBrowse.setHeight("100%");
					itmBrowse.setWidth("300px");
					itmBrowse.tree.addActionHandler(new Handler() {
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
										"750px", "600px", true, true);
								frmMasterItems frm = new frmMasterItems();
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
								createItem(m.getId());
							}

							if (action.getCaption().equals(
									mapActAccBrows.get("create_branch_n"))) {
								Window wnd = ControlsFactory.CreateWindow(
										"750px", "600px", true, true);
								frmMasterItems frm = new frmMasterItems();
								frm.setParentLayout((AbstractLayout) wnd
										.getContent());
								frm.show_tree_panel = false;
								frm.showInitView();
								frm.createItem(m.getId());
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
							if (itmBrowse.tree.getValue() != null) {
								menuItem itm = (menuItem) itmBrowse.tree
										.getValue();
								QRYSES = itm.getId();
								txtDescr1.focus();
								load_data();
							} else {
								QRYSES = "";
								load_data();
								txtDescr1.focus();
							}
						}
					};
				}
				itmBrowse.data = data_Items;
				data_Items
						.executeQuery(
								"select reference,descr,descr2,"
										+ "usecounts,childcounts,parentitem from ITEMS order by DESCR2",
								true);

				m_mainLayout.removeComponent(itmBrowse);
				m_mainLayout.addComponent(itmBrowse);
				itmBrowse.initView();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	protected void createItem(String pd) {
		if (!txtProCode.isReadOnly()) {
			txtProCode.setValue(utils.getNewNo(
					"select to_number(nvl(max(reference),'0')) from items where  (parentitem='"
							+ pd + "')", pd, con));
			try {
				txtParentItem.setReadOnly(false);
				txtPName.setReadOnly(false);
				txtPName.setValue(utils.getSqlValue("select descr1 from"
						+ " items where reference='" + pd + "'", con));
				txtParentItem.setValue(null);
				if (txtPName.getValue() != null)
					txtParentItem.setValue(pd);

			} finally {
				txtParentItem.setReadOnly(true);
				txtPName.setReadOnly(true);
			}
		}

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
				data_prices.createDBClassFromConnection(con);
			}

		} catch (SQLException e) {
		}

		mapActAccBrows.clear();
		mapActAccBrows.put("open", "Open..");
		mapActAccBrows.put("open_n", "Open New Window..");
		mapActAccBrows.put("create_branch", "Create Branch..");
		mapActAccBrows.put("create_branch_n", "Create Branch New Window..");

		createView();
		applyColumns();
		load_data();

	}

	public void applyColumns() {
		lstItemCols.clear();
		ColumnProperty cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "PRICENO";
		cp.descr = "No";
		cp.display_width = 10;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "PRICE";
		cp.descr = "Price";
		cp.display_width = 30;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "DISCP";
		cp.descr = "Descr";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 3;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "PackD";
		cp.descr = "PackD";
		cp.display_width = 30;
		cp.display_type = "NONE";
		cp.pos = 4;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "UnitD";
		cp.descr = "UnitD";
		cp.display_width = 30;
		cp.display_type = "NONE";
		cp.pos = 5;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "Pack";
		cp.descr = "Pack";
		cp.display_width = 30;
		cp.display_type = "NONE";
		cp.pos = 6;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "Remark";
		cp.descr = "Remark";
		cp.display_width = 30;
		cp.display_type = "NONE";
		cp.pos = 7;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

	}

	public void load_data() {
		try {
			lblMessage.setValue("Adding New Item....");
			txtProCode.setReadOnly(false);
			txtParentItem.setReadOnly(true);
			txtPName.setReadOnly(true);
			txtSupName.setReadOnly(true);
			txtSupplier.setReadOnly(true);
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con
						.prepareStatement(
								"Select * from Items  where reference='"
										+ QRYSES + "'",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				fetch_AccountName();

				txtProCode.setReadOnly(true);
				lblMessage.setValue("Editing : " + txtDescr1.getValue());
			}
			table.data.executeQuery("select * from prices where refer ='"
					+ QRYSES + "'", true);
			table.fill_table();
			if (itmBrowse != null && show_tree_panel) {
				itmBrowse.locateTree(QRYSES);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void print_data() {

	}

	public void save_data() {

		save_data(true);
	}

	public void save_data(boolean cls) {
		try {
			// validate_data();
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
			String pth = utils.generatePath(utils.nvl(txtParentItem.getValue(),
					""), utils.nvl(txtProCode.getValue(), ""), "ITEMS",
					"REFERENCE", "DESCR2", con);
			Date dt = new Date(System.currentTimeMillis());
			qe.setParaValue("descr2", pth);
			qe.setParaValue("LEVELNO", utils.countString(pth, "\\") - 1);
			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("items"); // if
			} else {
				qe.AutoGenerateUpdateStatment("items", "'reference'",
						" WHERE reference=:reference"); // if to update..
			}
			qe.execute();
			con.commit();
			if (show_tree_panel && itmBrowse != null) {
				/*
				 * menuItem mn = new menuItem(txtProCode.getValue().toString(),
				 * txtDescr1.getValue() + "-" +
				 * txtProCode.getValue().toString());
				 * mn.setParentID(utils.nvl(txtParentItem.getValue(), ""));
				 * 
				 * itmBrowse.updateTree(mn);
				 */
				itmBrowse.refreshTree(true);
			}

			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtKeyId.getValue().toString();
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

	public void showInitView() {
		QRYSES = "";
		initForm();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
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
							QRYSES = tv.getData()
									.getFieldValue(rn, "Reference").toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select Reference, Descr from items order by descr2", true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_SuppAcc() {
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
											txtSupName.setReadOnly(false);
											int rn = tv.getSelectionValue();
											txtSupName.setValue(tv.getData()
													.getFieldValue(rn, "NAME")
													.toString());
											txtSupplier.setValue(tv.getData()
													.getFieldValue(rn, "accno")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										} finally {
											txtSupName.setReadOnly(true);
										}
									}
								}
							},
							con,
							"select accno,name from acaccunt where issupp='Y' order by path",
							true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	protected void show_ParentAcc() {
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
											utilsVaadin.setValueByForce(
													txtPName, tv.getData()
															.getFieldValue(rn,
																	"descr")
															.toString());
											utilsVaadin.setValueByForce(
													txtParentItem,
													tv.getData().getFieldValue(
															rn, "reference")
															.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select reference,descr from items where usecount=0 order by descr2",
							true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}
}