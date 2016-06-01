package com.windows.Masters.Inventory;

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
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmMasterStore implements transactionalForm {
	private AbstractLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	Panel basicPanel = new Panel();

	private VerticalLayout mainLayout = new VerticalLayout();
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
	private HorizontalLayout buttonLayout = new HorizontalLayout();	
	private HorizontalLayout saleLayout = new HorizontalLayout();
	private HorizontalLayout purchaseLayout = new HorizontalLayout();
	private HorizontalLayout adjLayout = new HorizontalLayout();
	private HorizontalLayout discLayout = new HorizontalLayout();
	private HorizontalLayout storeExpLayout = new HorizontalLayout();
	private HorizontalLayout lockLayout = new HorizontalLayout();
	private HorizontalLayout storeLayout = new HorizontalLayout();
	private HorizontalLayout nameLayout = new HorizontalLayout();
	private HorizontalLayout storeKeeperUserLayout = new HorizontalLayout();
	private HorizontalLayout name2Layout = new HorizontalLayout();
	private HorizontalLayout telLayout = new HorizontalLayout();
	private HorizontalLayout areaLayout = new HorizontalLayout();
	private HorizontalLayout storeKeeperLayout = new HorizontalLayout();
	private HorizontalLayout userLayout = new HorizontalLayout();

	private VerticalLayout basicLayout = new VerticalLayout();

	private HorizontalLayout storeGridLayout = new HorizontalLayout();
	private HorizontalLayout telUserLayout = new HorizontalLayout();

	private TextField txtSaleAcc = ControlsFactory.CreateTextField("",
			"salacc", lstfldinfo);
	private TextField txtSaleAccDisplay = ControlsFactory.CreateTextField("",
			"", null);
	private TextField txtPurAcc = ControlsFactory.CreateTextField("", "puracc",
			lstfldinfo);
	private TextField txtPurAccDisplay = ControlsFactory.CreateTextField("",
			"", null);
	private TextField txtAdjPAcc = ControlsFactory.CreateTextField("",
			"adjpacc", lstfldinfo);
	private TextField txtAdjPAccDisplay = ControlsFactory.CreateTextField("",
			"", null);
	private TextField txtSalesDiscAcc = ControlsFactory.CreateTextField("",
			"saldiscacc", lstfldinfo);
	private TextField txtSalesDiscAccDisplay = ControlsFactory.CreateTextField(
			"", "", null);
	private TextField txtStoreAcc = ControlsFactory.CreateTextField("",
			"storeacc", lstfldinfo);
	private TextField txtStoreAccNameDisplay = ControlsFactory.CreateTextField(
			"", "", null);
	private TextField txtSaleRAcc = ControlsFactory.CreateTextField("",
			"salracc", lstfldinfo);
	private TextField txtSaleRAccDisplay = ControlsFactory.CreateTextField("",
			"", null);
	private TextField txtPurRAcc = ControlsFactory.CreateTextField("",
			"purracc", lstfldinfo);
	private TextField txtPurRAccDisplay = ControlsFactory.CreateTextField("",
			"", null);
	private TextField txtAdjMAcc = ControlsFactory.CreateTextField("",
			"adjmacc", lstfldinfo);
	private TextField txtAdjMAccDisplay = ControlsFactory.CreateTextField("",
			"", null);
	private TextField txtPurDiscAcc = ControlsFactory.CreateTextField("",
			"purdiscacc", lstfldinfo);
	private TextField txtPurDiscAccDisplay = ControlsFactory.CreateTextField(
			"", "", null);
	private TextField txtExpAcc = ControlsFactory.CreateTextField("", "expacc",
			lstfldinfo);
	private TextField txtExpAccNameDisplay = ControlsFactory.CreateTextField(
			"", "", null);
	private TextField txtStoreNo = ControlsFactory.CreateTextField("", "No",
			lstfldinfo);
	private TextField txtStoreName = ControlsFactory.CreateTextField("",
			"Name", lstfldinfo);
	private TextField txtStoreName2 = ControlsFactory.CreateTextField("",
			"NameA", lstfldinfo);
	private TextField txtTel = ControlsFactory.CreateTextField("", "Tel",
			lstfldinfo);
	private TextField txtArea = ControlsFactory.CreateTextField("", "Area",
			lstfldinfo);
	private TextField txtStoreKeeper = ControlsFactory.CreateTextField("",
			"salesp", lstfldinfo);
	private TextField txtUser = ControlsFactory.CreateTextField("", "userName",
			lstfldinfo);

	private Label lblMessage = new Label("");
	private Label lblQtyOnAlarm = new Label("Qty On Alarm :");
	private Label lblBlockIssue = new Label("Block Issues :");
	private Label lblSaleAcc = new Label("Sale A/c :");
	private Label lblPurchaseAcc = new Label("Purchase A/c :");
	private Label lblAdjPAcc = new Label("Adj + A/c :");
	private Label lblSalesDiscAcc = new Label("Sale Disc A/c :");
	private Label lblStoreAcc = new Label("Store A/c :");
	private Label lblSalesRAcc = new Label("Sale R. A/c :");
	private Label lblPurchaseRAcc = new Label("P.Ret. A/c :");
	private Label lblAdjMAcc = new Label("Adj - A/c :");
	private Label lblPurDiscAcc = new Label("Pur. Disc. A/c :");
	private Label lblExpAcc = new Label("Expense A/c :");
	private Label lblStoreNo = new Label("Store No :");
	private Label lblStoreName = new Label("Name :");
	private Label lblStoreName2 = new Label("Name 2 :");
	private Label lblTel = new Label("Tel :");
	private Label lblArea = new Label("Area :");
	private Label lblStoreKeeper = new Label("Str Keeper :");
	private Label lblUser = new Label("User :");

	private CheckBox chkLock = ControlsFactory.CreateCheckField(
			"Lock / Unlock", "flag", "1", "2", lstfldinfo);

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

	private NativeButton cmdClearSalesAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearPurchaseAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearAdjPAcc = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdClearSaleDiscAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearStoreAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearSaleRAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearPurchaseRAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearAdjMAcc = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdClearPurchasDiscAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearExpenseAcc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");



	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		mainLayout.setSpacing(true);
		mainLayout.setWidth("600px");
		mainLayout.setMargin(true);
		basicLayout.setWidth("100%");
		salesAccLayout.setWidth("100%");
		salesRAccLayout.setWidth("100%");
		purchaseAccLayout.setWidth("100%");
		purchaseRAccLayout.setWidth("100%");
		adjPAccLayout.setWidth("100%");
		adjMAccLayout.setWidth("100%");
		saleDiscAccLayout.setWidth("100%");
		purDiscAccLayout.setWidth("100%");
		storeAccLayout.setWidth("100%");
		expAccLayout.setWidth("100%");
		lockLayout.setWidth("100%");
		storeLayout.setWidth("100%");
		nameLayout.setWidth("100%");
		name2Layout.setWidth("100%");
		telLayout.setWidth("100%");
		areaLayout.setWidth("100%");
		storeKeeperLayout.setWidth("100%");
		userLayout.setWidth("100%");
		storeGridLayout.setWidth("100%");
		telUserLayout.setWidth("100%");
		nameLayout.setWidth("100%");
		storeKeeperUserLayout.setWidth("100%");

		lblSaleAcc.setWidth("85px");
		lblPurchaseAcc.setWidth("85px");
		lblAdjPAcc.setWidth("85px");
		lblSalesDiscAcc.setWidth("85px");
		lblStoreAcc.setWidth("85px");
		lblSalesRAcc.setWidth("85px");
		lblPurchaseRAcc.setWidth("85px");
		lblAdjMAcc.setWidth("85px");
		lblPurDiscAcc.setWidth("85px");
		lblExpAcc.setWidth("85px");
		lblStoreNo.setWidth("82px");
		lblStoreName.setWidth("63px");
		lblStoreName2.setWidth("74px");
		lblTel.setWidth("80px");
		lblArea.setWidth("63px");
		lblStoreKeeper.setWidth("80px");
		lblUser.setWidth("63px"); 
		
		txtSaleAccDisplay.setWidth("100%");
		txtPurAccDisplay.setWidth("100%");
		txtAdjPAccDisplay.setWidth("100%");
		txtAdjMAccDisplay.setWidth("100%");
		txtSalesDiscAccDisplay.setWidth("100%");
		txtStoreAccNameDisplay.setWidth("100%");
		txtSaleRAccDisplay.setWidth("100%");
		txtPurRAccDisplay.setWidth("100%");
		txtPurDiscAccDisplay.setWidth("100%");
		txtExpAccNameDisplay.setWidth("100%");
		txtStoreName2.setWidth("83%");
		txtStoreNo.setWidth("65%");
		txtStoreName.setWidth("62%");
		txtTel.setWidth("65%");
		txtArea.setWidth("62%");
		txtStoreKeeper.setWidth("65%");
		txtUser.setWidth("62%");

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);

		ResourceManager.addComponent(basicLayout, storeGridLayout);
		ResourceManager.addComponent(basicLayout, name2Layout);
		ResourceManager.addComponent(basicLayout, telUserLayout);
		ResourceManager.addComponent(basicLayout, storeKeeperUserLayout);
		ResourceManager.addComponent(basicLayout, storeLayout);
		ResourceManager.addComponent(basicLayout, saleLayout);
		ResourceManager.addComponent(basicLayout, purchaseLayout);
		ResourceManager.addComponent(basicLayout, adjLayout);
		ResourceManager.addComponent(basicLayout, discLayout);
		ResourceManager.addComponent(basicLayout, storeExpLayout);

		ResourceManager.addComponent(storeGridLayout, storeLayout);
		ResourceManager.addComponent(storeGridLayout, nameLayout);

		ResourceManager.addComponent(telUserLayout, telLayout);
		ResourceManager.addComponent(telUserLayout, areaLayout);
		ResourceManager.addComponent(storeKeeperUserLayout, storeKeeperLayout);
		ResourceManager.addComponent(storeKeeperUserLayout, userLayout);

		ResourceManager.addComponent(storeLayout, lblStoreNo);
		ResourceManager.addComponent(storeLayout, txtStoreNo);

		ResourceManager.addComponent(nameLayout, lblStoreName);
		ResourceManager.addComponent(nameLayout, txtStoreName);

		ResourceManager.addComponent(name2Layout, lblStoreName2);
		ResourceManager.addComponent(name2Layout, txtStoreName2);

		ResourceManager.addComponent(telLayout, lblTel);
		ResourceManager.addComponent(telLayout, txtTel);

		ResourceManager.addComponent(areaLayout, lblArea);
		ResourceManager.addComponent(areaLayout, txtArea);

		ResourceManager.addComponent(storeKeeperLayout, lblStoreKeeper);
		ResourceManager.addComponent(storeKeeperLayout, txtStoreKeeper);

		ResourceManager.addComponent(userLayout, lblUser);
		ResourceManager.addComponent(userLayout, txtUser);

		ResourceManager.addComponent(lockLayout, chkLock);

		ResourceManager.addComponent(saleLayout, salesAccLayout);
		ResourceManager.addComponent(saleLayout, salesRAccLayout);
		ResourceManager.addComponent(purchaseLayout, purchaseAccLayout);
		ResourceManager.addComponent(purchaseLayout, purchaseRAccLayout);
		ResourceManager.addComponent(adjLayout, adjPAccLayout);
		ResourceManager.addComponent(adjLayout, adjMAccLayout);
		ResourceManager.addComponent(discLayout, saleDiscAccLayout);
		ResourceManager.addComponent(discLayout, purDiscAccLayout);
		ResourceManager.addComponent(storeExpLayout, storeAccLayout);
		ResourceManager.addComponent(storeExpLayout, expAccLayout);

		ResourceManager.addComponent(salesAccLayout, lblSaleAcc);
		ResourceManager.addComponent(salesAccLayout, txtSaleAccDisplay);
		ResourceManager.addComponent(salesAccLayout, cmdClearSalesAcc);
		ResourceManager.addComponent(salesAccLayout, cmdSalesAcc);

		ResourceManager.addComponent(salesRAccLayout, lblSalesRAcc);
		ResourceManager.addComponent(salesRAccLayout, txtSaleRAccDisplay);
		ResourceManager.addComponent(salesRAccLayout, cmdClearSaleRAcc);
		ResourceManager.addComponent(salesRAccLayout, cmdSaleRAcc);

		ResourceManager.addComponent(purchaseAccLayout, lblPurchaseAcc);
		ResourceManager.addComponent(purchaseAccLayout, txtPurAccDisplay);
		ResourceManager.addComponent(purchaseAccLayout, cmdClearPurchaseAcc);
		ResourceManager.addComponent(purchaseAccLayout, cmdPurchaseAcc);

		ResourceManager.addComponent(purchaseRAccLayout, lblPurchaseRAcc);
		ResourceManager.addComponent(purchaseRAccLayout, txtPurRAccDisplay);
		ResourceManager.addComponent(purchaseRAccLayout, cmdClearPurchaseRAcc);
		ResourceManager.addComponent(purchaseRAccLayout, cmdPurchaseRAcc);

		ResourceManager.addComponent(adjPAccLayout, lblAdjPAcc);
		ResourceManager.addComponent(adjPAccLayout, txtAdjPAccDisplay);
		ResourceManager.addComponent(adjPAccLayout, cmdClearAdjPAcc);
		ResourceManager.addComponent(adjPAccLayout, cmdAdjPAcc);

		ResourceManager.addComponent(adjMAccLayout, lblAdjMAcc);
		ResourceManager.addComponent(adjMAccLayout, txtAdjMAccDisplay);
		ResourceManager.addComponent(adjMAccLayout, cmdClearAdjMAcc);
		ResourceManager.addComponent(adjMAccLayout, cmdAdjMAcc);

		ResourceManager.addComponent(saleDiscAccLayout, lblSalesDiscAcc);
		ResourceManager.addComponent(saleDiscAccLayout, txtSalesDiscAccDisplay);
		ResourceManager.addComponent(saleDiscAccLayout, cmdClearSaleDiscAcc);
		ResourceManager.addComponent(saleDiscAccLayout, cmdSaleDiscAcc);

		ResourceManager.addComponent(purDiscAccLayout, lblPurDiscAcc);
		ResourceManager.addComponent(purDiscAccLayout, txtPurDiscAccDisplay);
		ResourceManager.addComponent(purDiscAccLayout, cmdClearPurchasDiscAcc);
		ResourceManager.addComponent(purDiscAccLayout, cmdPurchasDiscAcc);

		ResourceManager.addComponent(storeAccLayout, lblStoreAcc);
		ResourceManager.addComponent(storeAccLayout, txtStoreAccNameDisplay);
		ResourceManager.addComponent(storeAccLayout, cmdClearStoreAcc);
		ResourceManager.addComponent(storeAccLayout, cmdStoreAcc);

		ResourceManager.addComponent(expAccLayout, lblExpAcc);
		ResourceManager.addComponent(expAccLayout, txtExpAccNameDisplay);
		ResourceManager.addComponent(expAccLayout, cmdClearExpenseAcc);
		ResourceManager.addComponent(expAccLayout, cmdExpenseAcc);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdCls);
		ResourceManager.addComponent(buttonLayout, lblMessage);

		salesAccLayout.setComponentAlignment(lblSaleAcc,
				Alignment.MIDDLE_CENTER);
		salesAccLayout.setComponentAlignment(cmdSalesAcc,
				Alignment.BOTTOM_CENTER);
		salesAccLayout.setComponentAlignment(cmdClearSalesAcc,
				Alignment.BOTTOM_CENTER);

		salesRAccLayout.setComponentAlignment(lblSalesRAcc,
				Alignment.MIDDLE_CENTER);
		salesRAccLayout.setComponentAlignment(cmdSaleRAcc,
				Alignment.BOTTOM_CENTER);
		salesRAccLayout.setComponentAlignment(cmdClearSaleRAcc,
				Alignment.BOTTOM_CENTER);

		purchaseAccLayout.setComponentAlignment(lblPurchaseAcc,
				Alignment.MIDDLE_CENTER);
		purchaseAccLayout.setComponentAlignment(cmdPurchaseAcc,
				Alignment.BOTTOM_CENTER);
		purchaseAccLayout.setComponentAlignment(cmdClearPurchaseAcc,
				Alignment.BOTTOM_CENTER);

		purchaseRAccLayout.setComponentAlignment(lblPurchaseRAcc,
				Alignment.MIDDLE_CENTER);
		purchaseRAccLayout.setComponentAlignment(cmdPurchaseRAcc,
				Alignment.BOTTOM_CENTER);
		purchaseRAccLayout.setComponentAlignment(cmdClearPurchaseRAcc,
				Alignment.BOTTOM_CENTER);

		adjPAccLayout
				.setComponentAlignment(lblAdjPAcc, Alignment.MIDDLE_CENTER);
		adjPAccLayout
				.setComponentAlignment(cmdAdjPAcc, Alignment.BOTTOM_CENTER);
		adjPAccLayout.setComponentAlignment(cmdClearAdjPAcc,
				Alignment.BOTTOM_CENTER);

		adjMAccLayout
				.setComponentAlignment(lblAdjMAcc, Alignment.MIDDLE_CENTER);
		adjMAccLayout
				.setComponentAlignment(cmdAdjMAcc, Alignment.BOTTOM_CENTER);
		adjMAccLayout.setComponentAlignment(cmdClearAdjMAcc,
				Alignment.BOTTOM_CENTER);

		saleDiscAccLayout.setComponentAlignment(lblSalesDiscAcc,
				Alignment.MIDDLE_CENTER);
		saleDiscAccLayout.setComponentAlignment(cmdSaleDiscAcc,
				Alignment.BOTTOM_CENTER);
		saleDiscAccLayout.setComponentAlignment(cmdClearSaleDiscAcc,
				Alignment.BOTTOM_CENTER);

		purDiscAccLayout.setComponentAlignment(lblPurDiscAcc,
				Alignment.MIDDLE_CENTER);
		purDiscAccLayout.setComponentAlignment(cmdClearPurchasDiscAcc,
				Alignment.BOTTOM_CENTER);
		purDiscAccLayout.setComponentAlignment(cmdPurchasDiscAcc,
				Alignment.BOTTOM_CENTER);

		storeAccLayout.setComponentAlignment(lblStoreAcc,
				Alignment.MIDDLE_CENTER);
		storeAccLayout.setComponentAlignment(cmdClearStoreAcc,
				Alignment.BOTTOM_CENTER);
		storeAccLayout.setComponentAlignment(cmdStoreAcc,
				Alignment.BOTTOM_CENTER);

		expAccLayout.setComponentAlignment(lblExpAcc, Alignment.MIDDLE_CENTER);
		expAccLayout.setComponentAlignment(cmdClearExpenseAcc,
				Alignment.BOTTOM_CENTER);
		expAccLayout.setComponentAlignment(cmdExpenseAcc,
				Alignment.BOTTOM_CENTER);

		storeLayout.setComponentAlignment(lblStoreNo, Alignment.MIDDLE_CENTER);

		nameLayout.setComponentAlignment(lblStoreName, Alignment.BOTTOM_CENTER);

		name2Layout.setComponentAlignment(lblStoreName2,
				Alignment.MIDDLE_CENTER);

		telLayout.setComponentAlignment(lblTel, Alignment.MIDDLE_CENTER);

		areaLayout.setComponentAlignment(lblArea, Alignment.MIDDLE_CENTER);

		storeKeeperLayout.setComponentAlignment(lblStoreKeeper,
				Alignment.MIDDLE_CENTER);

		userLayout.setComponentAlignment(lblUser, Alignment.MIDDLE_CENTER);

		lockLayout.setComponentAlignment(chkLock, Alignment.TOP_RIGHT);

		mainLayout.setExpandRatio(buttonLayout, 0.1f);
		mainLayout.setExpandRatio(basicLayout, 3.9f);

		saleLayout.setExpandRatio(salesAccLayout, 2f);
		saleLayout.setExpandRatio(salesRAccLayout, 2f);

		purchaseLayout.setExpandRatio(purchaseAccLayout, 2f);
		purchaseLayout.setExpandRatio(purchaseRAccLayout, 2f);

		adjLayout.setExpandRatio(adjPAccLayout, 2f);
		adjLayout.setExpandRatio(adjMAccLayout, 2f);

		discLayout.setExpandRatio(saleDiscAccLayout, 2f);
		discLayout.setExpandRatio(purDiscAccLayout, 2f);

		storeExpLayout.setExpandRatio(storeAccLayout, 2f);
		storeExpLayout.setExpandRatio(expAccLayout, 2f);

		storeLayout.setExpandRatio(lblStoreNo, 0.1f);
		storeLayout.setExpandRatio(txtStoreNo, 3.9f);

		nameLayout.setExpandRatio(lblStoreName, 0.1f);
		nameLayout.setExpandRatio(txtStoreName, 3.9f);

		name2Layout.setExpandRatio(lblStoreName2, 0.1f);
		name2Layout.setExpandRatio(txtStoreName2, 3.9f);

		telUserLayout.setExpandRatio(telLayout, 2f);
		telUserLayout.setExpandRatio(areaLayout, 2f);

		areaLayout.setExpandRatio(lblArea, 0.1f);
		areaLayout.setExpandRatio(txtArea, 3.9f);

		telLayout.setExpandRatio(lblTel, 0.1f);
		telLayout.setExpandRatio(txtTel, 3.9f);

		userLayout.setExpandRatio(lblUser, 0.1f);
		userLayout.setExpandRatio(txtUser, 3.9f);

		storeKeeperUserLayout.setExpandRatio(storeKeeperLayout, 2f);
		storeKeeperUserLayout.setExpandRatio(userLayout, 2f);

		storeKeeperLayout.setExpandRatio(lblStoreKeeper, 0.1f);
		storeKeeperLayout.setExpandRatio(txtStoreKeeper, 3.9f);

		userLayout.setExpandRatio(lblUser, 0.1f);
		userLayout.setExpandRatio(txtUser, 3.9f);

		salesAccLayout.setExpandRatio(lblSaleAcc, 0.1f);
		salesAccLayout.setExpandRatio(txtSaleAccDisplay, 3.9f);

		salesRAccLayout.setExpandRatio(lblSalesRAcc, 0.1f);
		salesRAccLayout.setExpandRatio(txtSaleRAccDisplay, 3.9f);

		purchaseAccLayout.setExpandRatio(lblPurchaseAcc, 0.1f);
		purchaseAccLayout.setExpandRatio(txtPurAccDisplay, 3.9f);

		purchaseRAccLayout.setExpandRatio(lblPurchaseRAcc, 0.1f);
		purchaseRAccLayout.setExpandRatio(txtPurRAccDisplay, 3.9f);

		saleDiscAccLayout.setExpandRatio(lblSalesDiscAcc, 0.1f);
		saleDiscAccLayout.setExpandRatio(txtSalesDiscAccDisplay, 3.9f);

		purDiscAccLayout.setExpandRatio(lblPurDiscAcc, 0.1f);
		purDiscAccLayout.setExpandRatio(txtPurDiscAccDisplay, 3.9f);

		adjPAccLayout.setExpandRatio(lblAdjPAcc, 0.1f);
		adjPAccLayout.setExpandRatio(txtAdjPAccDisplay, 3.9f);

		adjMAccLayout.setExpandRatio(lblAdjMAcc, 0.1f);
		adjMAccLayout.setExpandRatio(txtAdjMAccDisplay, 3.9f);

		storeAccLayout.setExpandRatio(lblStoreAcc, 0.1f);
		storeAccLayout.setExpandRatio(txtStoreAccNameDisplay, 3.9f);

		expAccLayout.setExpandRatio(lblExpAcc, 0.1f);
		expAccLayout.setExpandRatio(txtExpAccNameDisplay, 3.9f);

		if (!listnerAdded) {
			cmdSalesAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listAcc(txtSaleAcc, txtSaleAccDisplay);
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
			listnerAdded = true;
		}
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
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
		buttonLayout.removeAllComponents();
		lockLayout.removeAllComponents();
		storeLayout.removeAllComponents();
		nameLayout.removeAllComponents();
		name2Layout.removeAllComponents();
		telLayout.removeAllComponents();
		areaLayout.removeAllComponents();
		storeKeeperLayout.removeAllComponents();
		userLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		saleLayout.removeAllComponents();
		storeGridLayout.removeAllComponents();
		telUserLayout.removeAllComponents();
		purchaseLayout.removeAllComponents();
		adjLayout.removeAllComponents();
		discLayout.removeAllComponents();
		storeExpLayout.removeAllComponents();
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
			Date dt = new Date(System.currentTimeMillis());
			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("acaccount"); // if
			} else {
				qe.AutoGenerateUpdateStatment("acaccount", "'ACCNO'",
						" WHERE ACCNO=:accno"); // if to update..
			}
			qe.execute();
			con.commit();
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtStoreNo.getValue().toString();
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
			lblMessage.setValue("Adding New Item....");
			txtStoreNo.setReadOnly(false);
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"Select * from Store  where No='" + QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				fetch_AccountName();
				txtStoreNo.setReadOnly(true);
				lblMessage.setValue("Editing : " + txtStoreName.getValue());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void print_data() {

	}

	protected void fetch_AccountName() {

		txtSaleAccDisplay.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtSaleAcc.getValue().toString() + "'", con));

		txtSaleRAccDisplay.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtSaleRAcc.getValue().toString() + "'", con));

		txtPurAccDisplay.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtPurAcc.getValue().toString() + "'", con));

		txtPurRAccDisplay.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtPurRAcc.getValue().toString() + "'", con));

		txtAdjPAccDisplay.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtAdjPAcc.getValue().toString() + "'", con));

		txtAdjMAccDisplay.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtAdjMAcc.getValue().toString() + "'", con));

		txtSalesDiscAccDisplay.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtSalesDiscAcc.getValue().toString() + "'", con));

		txtPurDiscAccDisplay.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtPurDiscAcc.getValue().toString() + "'", con));

		txtStoreAccNameDisplay.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtStoreAcc.getValue().toString() + "'", con));

		txtExpAccNameDisplay.setValue(utils.getSqlValue(
				"Select AccNo||'-'||Name from Acaccount where AccNo = '"
						+ txtExpAcc.getValue().toString() + "'", con));
	}

	protected void show_listAcc(final TextField tx, final TextField txn) {
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
											utilsVaadin.setValueByForce(txn, tv
													.getData().getFieldValue(
															rn, "ACCNO")
													.toString()
													+ "-"
													+ tv.getData()
															.getFieldValue(rn,
																	"NAME")
															.toString());
											utilsVaadin.setValueByForce(tx, tv
													.getData().getFieldValue(
															rn, "ACCNO")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select AccNo, Name from Acaccount where childcount=0 and actype=0 order by path",
							true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		/*
		 * try { //if (data_Items.getDbclass() == null) {
		 * //data_Items.createDBClassFromConnection(con);
		 * //data_prices.createDBClassFromConnection(con); //}
		 * 
		 * } catch (SQLException e) { }
		 */
		createView();

		// applyColumns()

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
							QRYSES = tv.getData().getFieldValue(rn, "No")
									.toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select No, Name from Store order by No", true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

}
