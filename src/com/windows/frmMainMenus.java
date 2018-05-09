package com.windows;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.doc.views.YesNoDialog;
import com.doc.views.Query.QueryView;
import com.doc.views.Query.tableDataListner;
import com.doc.views.YesNoDialog.Callback;
import com.generic.ControlsFactory;
import com.generic.ResourceManager;
import com.generic.localTableModel;
import com.generic.menuItem;
import com.generic.qryColumn;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsExe;
import com.generic.utilsVaadin;
import com.generic.queryBuilder.QueryBuilder;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.windows.Masters.GL.frmCostCent;
import com.windows.Masters.GL.frmFaCat;
import com.windows.Masters.GL.frmFaItems;
import com.windows.Masters.GL.frmMasterAccount;
import com.windows.Masters.GL.frmMasterCustomer;
import com.windows.Masters.GL.Transaction.frmAcBkPayVou;
import com.windows.Masters.GL.Transaction.frmAcBkRecVou;
import com.windows.Masters.GL.Transaction.frmAcJv;
import com.windows.Masters.GL.Transaction.frmAcPayVou;
import com.windows.Masters.GL.Transaction.frmAcRecVou;
import com.windows.Masters.General.frmCompany;
import com.windows.Masters.General.frmCreateUser;
import com.windows.Masters.General.frmProfile;
import com.windows.Masters.General.frmQueryDesigner;
import com.windows.Masters.Inventory.frmEmpInventory;
import com.windows.Masters.Inventory.frmLocationTypes;
import com.windows.Masters.Inventory.frmLocations;
import com.windows.Masters.Inventory.frmMasterItems;
import com.windows.Masters.Inventory.frmMasterStore;
import com.windows.Masters.Inventory.Transaction.frmQuotation;
import com.windows.Masters.Inventory.Transaction.frmSale;
import com.windows.Masters.Inventory.Transaction.frmSaleOrd;
import com.windows.Masters.Inventory.Transaction.frmSaleRet;
import com.windows.Masters.Purchase.frmPurOrd;
import com.windows.Masters.Purchase.frmPurOrderManag;
import com.windows.Masters.Purchase.frmPurchaseCost;
import com.windows.catering.frmCateringDelivery;
import com.windows.catering.frmCateringRegistration;
import com.windows.catering.frmCateringSubscription;
import com.windows.clinics.frmClinicMain;
import com.windows.clinics.frmDoctors;
import com.windows.clinics.frmPatients;
import com.windows.wizards.dev.frmWzDesignQuery;
import com.windows.workshop.frmCustomer;
import com.windows.workshop.frmJobOrder;
import com.windows.workshop.frmJobTypes;
import com.windows.workshop.frmSerialUpload;

@SuppressWarnings("serial")
public class frmMainMenus extends VerticalLayout implements ValueChangeListener {

	final static String MainMenus[] = { "Order Management", "Queries",
			"Approvals" };
	public final static String TYPE_FORM = "FORM";
	public final static String QUICK_REP = "QUICKREP";
	public final static String TYPE_COMMAND = "COMMAND";
	public final static String TYPE_FUNCTION = "FUNCTION";
	public final static String TYPE_QUERY = "QUERY";
	public final static String TYPE_SWITCH = "SWITCH";
	private TextField txtSearch = new TextField();
	public final static String FORM_PURORD = "PURORD";
	public final static String FORM_PURCOST = "PURCOST";
	public final static String FORM_BDV = "FORM_GL_BDV";
	public final static String FORM_APPROVALS = "APPROVALS";
	public final static String FORM_CLOSE_PERIOD = "CLOSE_PERIOD";
	public final static String FORM_LOCK_AUDIT_PERIOD = "LOCK_AUDIT_PERIOD";
	public final static String FORM_PLANS = "ACCPLANS";
	public final static String FORM_QUICKREP = "QUICKREP";
	public final static String FORM_CUSTOMER_SUBSCRIPTION = "FORM_SUBSCRIPTION";
	public final static String FORM_CUSTOMER_REG = "FORM_REGISTRATION";
	public final static String FORM_DELIVERY = "FORM_DELIVERY";
	public final static String FORM_CUSTOMER = "FORM_CUSTOMER";
	public final static String FORM_JOB_TYPES = "FORM_JOB_TYPES";
	public final static String FORM_SERIAL_UPLOADS = "FORM_SERIAL_UPLOADS";
	public final static String FORM_JOB_ORDER = "FORM_JOB_ORDER";
	public final static String FORM_DOCTORS = "FORM_DOCTORS";
	public final static String FORM_PATIENTS = "FORM_PATIENTS";
	public final static String FORM_CLINIC_MAIN = "FORM_CLINIC_MAIN";
	public final static String FORM_LISTS = "FORM_LISTS";
	public final static String FORM_MASTER_ITEMS = "FORM_MASTER_ITEMS";
	public final static String FORM_MASTER_COST_CENT = "FORM_MASTER_COST_CENT";
	public final static String FORM_MASTER_COMPANY = "FORM_MASTER_COMPANY";
	public final static String FORM_FA_ITEMS = "FORM_FA_ITEMS";
	public final static String FORM_LG_CONTRACT = "FORM_LG_CONTRACT";

	private Label lblCompanyName = new Label(utils.textCompanyName);
	private Label lblLogonUser = new Label("Logon User: " + utils.CPUSER+" - Master :"+utils.DBUSER);
	private Label specifiecation = new Label(utils.textSpec1);
	private Label specifiecation2 = new Label(utils.textSpec2);
	public Map<String, transactionalForm> mapForms = new HashMap<String, transactionalForm>();
	private final Map<String, String> mapActionStrs = new HashMap<String, String>();
	private Panel detailspanel = new Panel("");
	private VerticalLayout centralPanel = (VerticalLayout) detailspanel
			.getContent();
	public Tree tree = null;

	private frmPurOrderManag po = new frmPurOrderManag();
	private frmPurchaseCost pcost = new frmPurchaseCost();
	private frmGatePass gpass = new frmGatePass();
	private frmBDV bdv = new frmBDV();
	private frmApprovals aprvls = new frmApprovals();
	private frmLockNAuditPeiriod lockauditperiod = new frmLockNAuditPeiriod();
	private frmPlans plansfrm = new frmPlans();
	private frmQuickRep qcfrm = new frmQuickRep();
	private AccountQueries acq = new AccountQueries();
	private frmCateringSubscription subs = new frmCateringSubscription();
	private frmCateringRegistration reg = new frmCateringRegistration();
	private frmCateringDelivery dlv = new frmCateringDelivery();
	private frmCustomer cust = new frmCustomer();
	private frmJobTypes jobtypes = new frmJobTypes();
	private frmSerialUpload serialuploads = new frmSerialUpload();
	public frmJobOrder jobord = (frmJobOrder) ControlsFactory.CreateForm(
			new frmJobOrder(), centralPanel, mapForms, FORM_JOB_ORDER);
	private frmClinicMain mainclinic = new frmClinicMain();

	private frmMasterAccount mast_acc = (frmMasterAccount) ControlsFactory
			.CreateForm(new frmMasterAccount(), centralPanel, mapForms,
					"FORM_MASTER_ACCOUNTS");
	private frmOraApp frmor = (frmOraApp) ControlsFactory.CreateForm(
			new frmOraApp(), centralPanel, mapForms, "ORA_APP");

	private frmMasterCustomer mast_cust = (frmMasterCustomer) ControlsFactory
			.CreateForm(new frmMasterCustomer(), centralPanel, mapForms,
					"FORM_MASTER_CUSTOMER");
	private frmCostCent mast_cost_cent = (frmCostCent) ControlsFactory
			.CreateForm(new frmCostCent(), centralPanel, mapForms,
					"FORM_MASTER_COSTCENTER");
	private frmCompany company = (frmCompany) ControlsFactory.CreateForm(
			new frmCompany(), centralPanel, mapForms, FORM_MASTER_COMPANY);

	private frmCreateUser user = (frmCreateUser) ControlsFactory.CreateForm(
			new frmCreateUser(), centralPanel, mapForms, "FORM_CREATE_USER");
	private frmEmpInventory emp_inv = (frmEmpInventory) ControlsFactory
			.CreateForm(new frmEmpInventory(), centralPanel, mapForms,
					"FORM_EMP_INVENTORY");
	private frmFaCat fa_cat = (frmFaCat) ControlsFactory.CreateForm(
			new frmFaCat(), centralPanel, mapForms, "FORM_FA_CAT");

	private frmLocations locations = (frmLocations) ControlsFactory.CreateForm(
			new frmLocations(), centralPanel, mapForms, "FORM_LOCATIONS");
	private frmLocationTypes location_types = (frmLocationTypes) ControlsFactory
			.CreateForm(new frmLocationTypes(), centralPanel, mapForms,
					"FORM_LOCATION_TYPES");
	private frmMasterStore frmMasterStore = (frmMasterStore) ControlsFactory
			.CreateForm(new frmMasterStore(), centralPanel, mapForms,
					"FORM_INVENTORY_STORE");
	private frmProfile profile = (frmProfile) ControlsFactory.CreateForm(
			new frmProfile(), centralPanel, mapForms, "FORM_PROFILE");
	private frmMasterItems mast_item = (frmMasterItems) ControlsFactory
			.CreateForm(new frmMasterItems(), centralPanel, mapForms,
					"FORM_INVENTORY_ITEMS");

	private frmSale sales = (frmSale) ControlsFactory.CreateForm(new frmSale(),
			centralPanel, mapForms, "FORM_SALES_INVOICE");

	private frmSaleRet salesret = (frmSaleRet) ControlsFactory.CreateForm(
			new frmSaleRet(), centralPanel, mapForms, "FORM_SALES_RETURN");

	private frmPurOrd purord = (frmPurOrd) ControlsFactory.CreateForm(
			new frmPurOrd(), centralPanel, mapForms, "FORM_PURCHASE_ORDER");

	private frmFaItems fa_items = (frmFaItems) ControlsFactory.CreateForm(
			new frmFaItems(), centralPanel, mapForms, FORM_FA_ITEMS);

	// inventory-orders
	private frmQuotation invQuotation = (frmQuotation) ControlsFactory
			.CreateForm(new frmQuotation(), centralPanel, mapForms,
					"FORM_INVENOTRY_ORDER_QUOTATION");

	// gl -transaction
	private frmAcBkPayVou glAcBkPay = (frmAcBkPayVou) ControlsFactory
			.CreateForm(new frmAcBkPayVou(), centralPanel, mapForms,
					"FORM_GL_ACBKPAYVOU");
	private frmAcJv glacjv = (frmAcJv) ControlsFactory.CreateForm(
			new frmAcJv(), centralPanel, mapForms, "FORM_GL_ACJV");

	private frmAcRecVou glacrecvou = (frmAcRecVou) ControlsFactory.CreateForm(
			new frmAcRecVou(), centralPanel, mapForms, "FORM_GL_ACRECVOU");

	private frmAcBkRecVou glacbkrecvou = (frmAcBkRecVou) ControlsFactory
			.CreateForm(new frmAcBkRecVou(), centralPanel, mapForms,
					"FORM_GL_ACBKRECVOU");

	private frmAcPayVou glacpayvou = (frmAcPayVou) ControlsFactory.CreateForm(
			new frmAcPayVou(), centralPanel, mapForms, "FORM_GL_ACPAYVOU");

	private frmQueryDesigner qd = (frmQueryDesigner) ControlsFactory
			.CreateForm(new frmQueryDesigner(), centralPanel, mapForms,
					"FORM_QUERY_DESIGN");

	private frmEmpInventory ei = (frmEmpInventory) ControlsFactory.CreateForm(
			new frmEmpInventory(), centralPanel, mapForms,
			"FORM_INVENTORY_EMPLOYEE");

	private frmPurOrd pur_o = (frmPurOrd) ControlsFactory.CreateForm(
			new frmPurOrd(), centralPanel, mapForms, "FORM_INVENTORY_PURORD");

	private frmSaleOrd sal_o = (frmSaleOrd) ControlsFactory.CreateForm(
			new frmSaleOrd(), centralPanel, mapForms, "FORM_SALES_ORDER");

	private com.windows.LG.frmJobOrder job_o = (com.windows.LG.frmJobOrder) ControlsFactory
			.CreateForm(new com.windows.LG.frmJobOrder(), centralPanel,
					mapForms, "FORM_LG_JOB_ORDER");

	private com.windows.LG.frmLgContract lg_cont = (com.windows.LG.frmLgContract) ControlsFactory
			.CreateForm(new com.windows.LG.frmLgContract(), centralPanel,
					mapForms, "FORM_LG_CONTRACTS");

	private frmWzDesignQuery wzdesign = (frmWzDesignQuery) ControlsFactory
			.CreateForm(new frmWzDesignQuery(), centralPanel, mapForms,
					"FORM_WZ_QUERY");

	private frmDoctors doc = new frmDoctors();
	private frmPatients patients = new frmPatients();
	private frmLists lst = new frmLists();
	public int profileno = Channelplus3Application.getInstance()
			.getFrmUserLogin().CURRENT_PROFILENO;

	private Window mainWindow = null;
	public Map<String, Button> mapCmds = new HashMap<String, Button>();

	public void show_shortcuts(HorizontalLayout profileBar) throws SQLException {
		Connection con = Channelplus3Application.getInstance()
				.getFrmUserLogin().getDbc().getDbConnection();
		int cp = Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_PROFILENO;

		ResultSet rst = utils
				.getSqlRS(
						"select MENU_CODE,MENU_TITLE,NVL(NVL(SHORTCUT_ICON,ICONFILE),'img/'||lower(type_of_exec)||'.png') shortcut_icon,"
								+ "MENU_TITLE FROM CP_MAIN_MENUS WHERE SHORTCUT='Y' AND GROUP_CODE='"
								+ Channelplus3Application.getInstance()
										.getFrmUserLogin().CURRENT_MENU_CODE
								+ "' order by menu_path", con);
		if (!(rst != null && rst.first()))
			return;
		rst.beforeFirst();
		profileBar.removeAllComponents();
		while (rst.next()) {
			menuItem mn = utilsVaadin.findNodeByValue(tree,
					rst.getString("menu_code"), null);
			if (mn != null) {
				NativeButton bt = ControlsFactory.CreateCustomButton(
						rst.getString("menu_title"),
						rst.getString("shortcut_icon"),
						rst.getString("menu_title"), "");
				bt.setData(mn);
				profileBar.addComponent(bt);
				bt.addListener(new ClickListener() {

					public void buttonClick(ClickEvent event) {
						NativeButton b = (NativeButton) event.getComponent();
						menuItem m = (menuItem) b.getData();
						tree.setValue(m);
					}
				});
			}
		}
		rst.close();
	}

	public void show_menus(HorizontalLayout profileBar) {
		try {

			int cp = Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_PROFILENO;
			PreparedStatement pss = Channelplus3Application
					.getInstance()
					.getFrmUserLogin()
					.getDbc()
					.getDbConnection()
					.prepareStatement(
							"select initcap(module_name) module_name from cp_main_groups"
									+ " WHERE (profiles like '%\"" + cp
									+ "\"%' or " + cp + "=0)  order by code",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ResultSet rss = pss.executeQuery();
			rss.beforeFirst();
			profileBar.removeAllComponents();
			while (rss.next()) {
				Label lbl = new Label();
				if (Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_MENU_MODULE
						.toUpperCase().equals(
								rss.getString("module_name").toUpperCase())) {
					lbl.setValue("[ " + rss.getString("module_name") + " ]");
					lbl.addStyleName("profilesel");
				} else {
					lbl.addStyleName("profile");
					lbl.setValue(rss.getString("module_name"));
				}
				ResourceManager.addComponent(profileBar, lbl);
				profileBar.addComponent(lbl);
				profileBar.setExpandRatio(lbl, 1);
			}
			pss.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	public void createView() {

		System.gc();
		ResourceManager.setRTL(false);
		removeAllComponents();
		detailspanel.removeAllComponents();
		centralPanel.removeAllComponents();

		VerticalLayout root = this;
		mainWindow.setSizeFull();
		root.addStyleName("applayout");
		root.addStyleName("apprtl");
		root.setSizeFull();
		// Add the components

		// Title bar
		HorizontalLayout titleBar = new HorizontalLayout();
		VerticalLayout titleBar2 = new VerticalLayout();
		final HorizontalLayout profileBar = new HorizontalLayout();
		final HorizontalLayout profileBar2 = new HorizontalLayout();
		VerticalLayout layoutCmd = new VerticalLayout();

		Button cmdExpand = new NativeButton();
		Button cmdCollapse = new NativeButton();
		Button cmdCopyMenu = new NativeButton();
		Button cmdNewSubMenu = new NativeButton();
		Button cmdBack = new NativeButton();
		Button cmdForward = new NativeButton();
		Button cmdLogOff = new NativeButton();

		titleBar.setWidth("100%");
		titleBar2.setSizeFull();
		specifiecation.setSizeFull();
		specifiecation2.setSizeFull();
		lblLogonUser.setSizeFull();
		ResourceManager.addComponent(root, titleBar);
		ResourceManager.addComponent(root, profileBar);

		lblCompanyName.addStyleName("title");
		lblLogonUser.addStyleName("blinker");
		specifiecation.addStyleName("title1");
		specifiecation2.addStyleName("title2");

		ResourceManager.addComponent(titleBar, lblCompanyName);
		ResourceManager.addComponent(titleBar2, lblLogonUser);
		ResourceManager.addComponent(titleBar2, specifiecation2);
		ResourceManager.addComponent(titleBar, titleBar2);
		show_menus(profileBar);

		profileBar.addListener(new LayoutClickListener() {

			public void layoutClick(final LayoutClickEvent event) {
				if (event.getChildComponent() != null
						&& event.getChildComponent() instanceof Label) {
					change_profile(event.getChildComponent().toString());
					show_menus(profileBar);
				}
			}
		});

		profileBar.setSpacing(true);
		profileBar.addStyleName("profilebar");
		profileBar.setWidth("100%");
		titleBar2.setSizeFull();
		titleBar.setSpacing(false);
		titleBar2.setSpacing(false);
		titleBar.setMargin(false);
		titleBar2.setMargin(false);
		titleBar2.addStyleName("title1");
		titleBar.setExpandRatio(lblCompanyName, 4); // Minimize the comment
		titleBar.setExpandRatio(titleBar2, 1);

		// Horizontal layout with selection tree on the left and
		// a details panel on the right.

		HorizontalLayout horlayout = new HorizontalLayout();
		horlayout.setSizeFull();
		horlayout.setSpacing(true);
		ResourceManager.addComponent(root, profileBar2);
		ResourceManager.addComponent(root, horlayout);

		// root.setComponentAlignment(profileBar2, Alignment.MIDDLE_CENTER);
		root.setExpandRatio(horlayout, 1);
		Panel menuContainer = new Panel(Channelplus3Application.getInstance()
				.getFrmUserLogin().CURRENT_MENU_NAME);

		menuContainer.addStyleName("light"); // No border
		menuContainer.setWidth("-1px"); // Undefined width
		menuContainer.setHeight("100%");
		menuContainer.getContent().setWidth("-1px"); // Undefined width

		ResourceManager.addComponent(horlayout, layoutCmd);
		ResourceManager.addComponent(horlayout, menuContainer);

		// horlayout.addComponent(menuContainer);
		// menuContainer.addComponent(txtSearch);
		ResourceManager.addComponent(menuContainer, txtSearch);
		txtSearch.setWidth("100%");
		txtSearch.setImmediate(true);

		// A menu tree, fill it later.
		tree = new Tree();
		tree.setSelectable(true);
		tree.setNullSelectionAllowed(false);
		tree.setSizeUndefined();

		ResourceManager.addComponent(layoutCmd, cmdExpand);
		ResourceManager.addComponent(layoutCmd, cmdCollapse);
		ResourceManager.addComponent(layoutCmd, cmdNewSubMenu);
		ResourceManager.addComponent(layoutCmd, cmdCopyMenu);
		ResourceManager.addComponent(layoutCmd, cmdBack);
		ResourceManager.addComponent(layoutCmd, cmdForward);
		ResourceManager.addComponent(layoutCmd, cmdLogOff);

		ResourceManager.addComponent(menuContainer.getContent(), tree);

		// menuContainer.addComponent(tree);
		// A panel for the main view area on the right side
		detailspanel.addStyleName("detailspanel");
		detailspanel.addStyleName("light"); // No borders
		detailspanel.setWidth("100%"); // Undefined width
		detailspanel.setHeight("100%");
		detailspanel.getContent().setWidth("-1px"); // Undefined width

		ResourceManager.addComponent(horlayout, detailspanel);

		// horlayout.addComponent(detailspanel);
		horlayout.setComponentAlignment(detailspanel, Alignment.TOP_RIGHT);
		horlayout.setComponentAlignment(menuContainer, Alignment.TOP_RIGHT);
		centralPanel = (VerticalLayout) detailspanel.getContent();
		centralPanel.removeAllComponents();
		centralPanel.setWidth("100%");
		// detailspanel.addComponent(centralPanel);
		// detailslayout.setComponentAlignment(centralPanel,
		// Alignment.TOP_CENTER);

		// Let the details panel take as much space as possible and
		// have the selection tree to be as small as possible
		horlayout.setExpandRatio(detailspanel, 1);
		horlayout.setExpandRatio(menuContainer, 0);
		// horlayout.setExpandRatio(layoutCmd, 0);

		layoutCmd.setWidth("33px");
		layoutCmd.setHeight("100%");
		layoutCmd.setStyleName("toolpanel");
		layoutCmd.setSpacing(true);

		layoutCmd.setExpandRatio(cmdExpand, 0);
		layoutCmd.setExpandRatio(cmdCollapse, 0);
		layoutCmd.setExpandRatio(cmdCopyMenu, 0);
		layoutCmd.setExpandRatio(cmdForward, 0);
		layoutCmd.setExpandRatio(cmdLogOff, 0);
		layoutCmd.setExpandRatio(cmdBack, 0);
		layoutCmd.setExpandRatio(cmdNewSubMenu, 0);

		cmdExpand.setWidth("100%");
		cmdBack.setWidth("100%");
		cmdCollapse.setWidth("100%");
		cmdCopyMenu.setWidth("100%");
		cmdExpand.setWidth("100%");
		cmdLogOff.setWidth("100%");
		cmdNewSubMenu.setWidth("100%");

		cmdCollapse.setStyleName("toolbar");
		cmdBack.setStyleName("toolbar");
		cmdCopyMenu.setStyleName("toolbar");
		cmdForward.setStyleName("toolbar");
		cmdLogOff.setStyleName("toolbar");
		cmdNewSubMenu.setStyleName("toolbar");
		cmdExpand.setStyleName("toolbar");

		cmdCollapse.setIcon(new ThemeResource("img/collapse.png"));
		cmdExpand.setIcon(new ThemeResource("img/expand.png"));
		cmdBack.setIcon(new ThemeResource("img/back.png"));
		cmdCopyMenu.setIcon(new ThemeResource("img/copymenu.png"));
		cmdForward.setIcon(new ThemeResource("img/forward.png"));
		cmdLogOff.setIcon(new ThemeResource("img/logoff.png"));
		cmdNewSubMenu.setIcon(new ThemeResource("img/newmenu.png"));

		mapCmds.clear();
		mapCmds.put("back", cmdBack);
		mapCmds.put("forward", cmdForward);
		mapCmds.put("expand", cmdExpand);
		mapCmds.put("collapse", cmdCollapse);
		mapCmds.put("logoff", cmdLogOff);
		mapCmds.put("copymenu", cmdCopyMenu);
		mapCmds.put("newmenu", cmdNewSubMenu);

		cmdCopyMenu.setEnabled(false);
		cmdNewSubMenu.setEnabled(false);

		cmdExpand.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				for (Iterator<?> it = tree.rootItemIds().iterator(); it
						.hasNext();) {
					menuItem cm = (menuItem) it.next();
					tree.expandItemsRecursively(cm);
				}

			}
		});

		cmdCollapse.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				for (Iterator<?> it = tree.rootItemIds().iterator(); it
						.hasNext();) {
					menuItem cm = (menuItem) it.next();
					tree.collapseItemsRecursively(cm);
				}

			}
		});

		cmdLogOff.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				YesNoDialog.Callback cb = new Callback() {

					public void onDialogResult(boolean resultIsYes) {
						if (resultIsYes) {
							utilsExe.Execute("EXIT", centralPanel);
						}

					}
				};
				getWindow().addWindow(
						new YesNoDialog("Confirm to exit",
								"Do you want to logoff ? \n \n", cb, "200px",
								"100px"));

			}
		});
		cmdCopyMenu.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				menuItem cm = (menuItem) tree.getValue();
				if (cm != null) {
					utilsExe.copyMenu(Channelplus3Application.getInstance()
							.getFrmUserLogin().CURRENT_MENU_CODE, cm.getId());
				}

			}
		});

		cmdNewSubMenu.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				menuItem cm = (menuItem) tree.getValue();
				if (cm != null && tree.hasChildren(cm)) {
					utilsExe.createMenu(Channelplus3Application.getInstance()
							.getFrmUserLogin().CURRENT_MENU_CODE, cm.getId());
				}

			}
		});

		localTableModel lc = new localTableModel();
		try {
			lc.createDBClassFromConnection(Channelplus3Application
					.getInstance().getFrmUserLogin().getDbc().getDbConnection());
			lc.executeQuery(
					"select GROUP_CODE, MENU_CODE, MENU_TITLE, MENU_TITLEA, "
							+ "TYPE_OF_EXEC, EXEC_LINE,"
							+ " PROFILES, PARENT_MENUCODE, CHILDCOUNT,"
							+ " MENU_PATH, EXPAND_NODE, RESERVED1, RESERVED2,"
							+ " nvl(ICONFILE,lower('img/'||TYPE_OF_EXEC||'.png')) iconfile,"
							+ " SHORTCUT, SHORTCUT_ICON,nvl(access_profile,'\"0\"') AP"
							+ " from cp_main_menus,cp_security "
							+ " where cp_main_menus.TYPE_OF_EXEC = TYPEOFSCREN(+) and exec_line=id(+) and "
							+ " group_code='"
							+ Channelplus3Application.getInstance()
									.getFrmUserLogin().CURRENT_MENU_CODE
							+ "' order by menu_path ", true);
			String stri = ((utils.findFieldInRS("cp_main_menus", "ICONFILE", lc
					.getDbclass().getDbConnection()) ? "ICONFILE" : ""));
			utilsVaadin.fillTree(tree, lc, "PARENT_MENUCODE", "MENU_CODE",
					"MENU_TITLE", "CHILDCOUNT", "TYPE_OF_EXEC", "EXEC_LINE",
					"EXPAND_NODE", "AP", stri);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		tree.addListener(this);
		tree.setImmediate(true);
		tree.addActionHandler(new Handler() {
			public void handleAction(Action action, Object sender, Object target) {
				menuItem cm = (menuItem) target;
				try {

					if (action.getCaption().equals("Open in New Window..")) {
						Window wnd = ControlsFactory.CreateWindow("90%", "80%",
								true, true);
						((VerticalLayout) wnd.getContent()).setMargin(true);
						utilsVaadin.removeAllTreeWindows();
						try {
							utilsExe.OpenForm(
									(AbstractLayout) wnd.getContent(), cm, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (action.getCaption().equals("New Sub Menu")) {
						utilsExe.createMenu(
								Channelplus3Application.getInstance()
										.getFrmUserLogin().CURRENT_MENU_CODE,
								cm.getId());
					}
					if (action.getCaption().equals("Edit")) {
						utilsExe.editMenu(Channelplus3Application.getInstance()
								.getFrmUserLogin().CURRENT_MENU_CODE, cm
								.getId());
					}

					if (action.getCaption().equals("Copy to..")) {
						utilsExe.copyMenu(Channelplus3Application.getInstance()
								.getFrmUserLogin().CURRENT_MENU_CODE, cm
								.getId());
					}
					if (action.getCaption().equals("Delete")) {
						utilsExe.deleteMenu(
								Channelplus3Application.getInstance()
										.getFrmUserLogin().CURRENT_MENU_CODE,
								cm.getId());
					}
				} catch (SQLException e) {
					e.printStackTrace();
					getWindow().showNotification("", e.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
				}
			}

			public Action[] getActions(Object target, Object sender) {
				menuItem cm = (menuItem) target;
				List<Action> acts = new ArrayList<Action>();
				if (tree.areChildrenAllowed(cm)) {
					Action act = new Action("New Sub Menu");
					acts.add(act);
				}

				acts.add(new Action("Copy to.."));
				acts.add(new Action("Edit"));
				acts.add(new Action("Delete"));
				if (!tree.areChildrenAllowed(cm)) {
					acts.add(new Action("Open in New Window.."));
				}
				Action[] act = new Action[acts.size()];
				return acts.toArray(act);
			}
		});

		for (Iterator<?> it = tree.rootItemIds().iterator(); it.hasNext();) {
			menuItem cm = (menuItem) it.next();
			if (cm.getPara3Val() != null && cm.getPara3Val().equals("Y")) {
				tree.expandItemsRecursively(cm);
			}
		}

		tree.setItemStyleGenerator(new Tree.ItemStyleGenerator() {
			public String getStyle(Object itemId) {
				menuItem cm = ((menuItem) itemId);
				if (cm.getPara4Val() != null
						&& !cm.getPara4Val().toString().isEmpty()
						&& (cm.getPara4Val().toString()
								.indexOf("\"" + profileno + "\"") <= -1 && cm
								.getPara4Val().toString()
								.indexOf("\"" + 0 + "\"") <= -1)) {
					return "disabled";
				}
				return null;
			}
		});

		final Button but = new Button("search");
		ResourceManager.addComponent(menuContainer, but);
		but.setHeight("0px");
		but.setWidth("0px");
		but.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if (txtSearch.getValue() == null) {
					return;
				}
				String sr = txtSearch.getValue().toString();
				List<menuItem> mns = new ArrayList(tree.getItemIds());
				List<menuItem> mns2 = mns;
				if (tree.getValue() != null) {
					mns2 = mns.subList(mns.indexOf(tree.getValue()) + 1,
							mns.size());
				}
				for (Iterator<?> it = mns2.iterator(); it.hasNext();) {
					menuItem mn = (menuItem) it.next();
					if (mn.getDisplay().toUpperCase()
							.contains(sr.toUpperCase())
							|| mn.getId().toUpperCase()
									.contains(sr.toUpperCase())) {
						if (tree.getParent(mn) != null) {
							tree.expandItem(tree.getParent(mn));
						}
						tree.select(mn);
						return;
					}
				}
				// search again from begining...
				if (tree.getValue() != null && mns.indexOf(tree.getValue()) > 0) {
					mns2 = mns.subList(0, mns.indexOf(tree.getValue()) - 1);
					for (Iterator<?> it = mns2.iterator(); it.hasNext();) {
						menuItem mn = (menuItem) it.next();
						if (mn.getDisplay().toUpperCase()
								.contains(sr.toUpperCase())
								|| mn.getId().toUpperCase()
										.contains(sr.toUpperCase())) {
							if (tree.getParent(mn) != null) {
								tree.expandItem(tree.getParent(mn));
							}
							tree.select(mn);
							return;
						}
					}

				}

			}

		});
		txtSearch.addListener(new FocusListener() {
			public void focus(FocusEvent event) {
				but.setClickShortcut(KeyCode.ENTER);
			}
		});

		txtSearch.addListener(new BlurListener() {
			public void blur(BlurEvent event) {
				but.removeClickShortcut();
			}
		});

		try {
			show_shortcuts(profileBar2);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		po.setParentLayout(centralPanel);
		pcost.setParentLayout(centralPanel);
		gpass.setParentLayout(centralPanel);
		bdv.setParentLayout(centralPanel);
		aprvls.setParentLayout(centralPanel);
		lockauditperiod.setParentLayout(centralPanel);
		plansfrm.setParentLayout(centralPanel);
		qcfrm.setParentLayout(centralPanel);
		subs.setParentLayout(centralPanel);
		reg.setParentLayout(centralPanel);
		dlv.setParentLayout(centralPanel);
		cust.setParentLayout(centralPanel);
		jobtypes.setParentLayout(centralPanel);
		serialuploads.setParentLayout(centralPanel);

		mainclinic.setParentLayout(centralPanel);
		doc.setParentLayout(centralPanel);
		patients.setParentLayout(centralPanel);
		lst.setParentLayout(centralPanel);
	}

	private Object prevItem = null;

	public void valueChange(ValueChangeEvent event) {
		utilsVaadin.removeAllTreeWindows();
		if (event.getProperty().getValue() == null) {
			return;
		}
		String s = ((menuItem) event.getProperty().getValue()).getId();
		menuItem menusel = ((menuItem) event.getProperty().getValue());
		menuItem cm = ((menuItem) event.getProperty().getValue());

		menuItem m = (menuItem) tree.getParent(menusel);
		while (m != null) {
			if (!tree.isExpanded(m))
				tree.expandItem(m);
			m = (menuItem) tree.getParent(m);
		}
		if (prevItem == cm) {
			return;
		}
		if (cm.getPara4Val() != null
				&& !cm.getPara4Val().toString().isEmpty()
				&& (cm.getPara4Val().toString()
						.indexOf("\"" + profileno + "\"") <= -1 && cm
						.getPara4Val().toString().indexOf("\"" + 0 + "\"") <= -1)) {
			getWindow().showNotification("Access Denied !",
					Notification.TYPE_ERROR_MESSAGE);
			tree.setValue(prevItem);
			return;
		} else
			prevItem = menusel;

		centralPanel.removeAllComponents();
		centralPanel.setWidth("100%");
		centralPanel.setHeight("-1px"); // Undefined height

		detailspanel.setCaption(((menuItem) event.getProperty().getValue())
				.getDisplay());
		mapCmds.get("newmenu").setEnabled(false);
		mapCmds.get("copymenu").setEnabled(true);

		if (tree.hasChildren(menusel)) {
			mapCmds.get("newmenu").setEnabled(true);
		}

		if (menusel.getPara1Val().toString().equals(TYPE_FORM)
				&& !menusel.getPara1Val().toString().toUpperCase()
						.startsWith("FORM")
				&& mapForms.get(menusel.getPara2Val().toString()) != null) {
			mapForms.get(menusel.getPara2Val().toString()).showInitView();
		}

		if (menusel.getPara1Val().toString().equals(QUICK_REP)) {
			detailspanel.setCaption(((menuItem) event.getProperty().getValue())
					.getDisplay() + " - " + menusel.getPara2Val().toString());

			qcfrm.setQRYSTR(menusel.getPara2Val().toString());
			qcfrm.showInitView();
		}
		if (menusel.getPara1Val().toString().equals("ORA_APP")) {
			frmor.setQRYSES(menusel.getPara2Val().toString());
			frmor.showInitView();
		}
		if (menusel.getPara1Val().toString().toUpperCase().startsWith("FORM")) {
			mapForms.get(menusel.getPara2Val().toString().toUpperCase())
					.showInitView();
		}

		if (menusel.getPara1Val().toString().equals(TYPE_FUNCTION)) {
			if (menusel.getPara2Val().toString().equals("PURCHASE_QUERY")) {
				showPurchaePeiodicQuery();
			}
			if (menusel.getPara2Val().toString().equals("APPROVAL_ORDERS")) {
				showApprovalOrders();
			}
			if (menusel.getPara2Val().toString().equals("PURCHASE_ITEM_QUERY")) {
				showPurchaePeiodicItemQuery();
			}
			if (menusel.getPara2Val().toString().equals("showtrialbalance")) {
				acq.showTrialbalance(centralPanel);
			}

		}
		if (menusel.getPara1Val().toString().equals(TYPE_COMMAND)) {
			centralPanel.removeAllComponents();
			centralPanel.setSizeFull();
			utilsExe.setCentralPanel(centralPanel);
			String title = ((menuItem) event.getProperty().getValue())
					.getDisplay();
			utilsExe.getWindow().setCaption(title);
			utilsExe.execute(menusel.getPara2Val().toString());

		}
		if (menusel.getPara1Val().toString().equals(TYPE_QUERY)) {
			String title = ((menuItem) event.getProperty().getValue())
					.getDisplay();
			showQuery(menusel.getPara2Val().toString(), title);
		}
		if (menusel.getPara1Val().toString().equals(TYPE_SWITCH)) {
			if (menusel.getPara1Val() != null
					&& menusel.getPara1Val().toString().length() > 0) {
				Channelplus3Application
						.getInstance()
						.getFrmUserLogin()
						.show_module_selection(menusel.getPara1Val().toString());
			} else {
				Channelplus3Application.getInstance().getFrmUserLogin()
						.show_module_selection();
			}
		}

	}

	public frmMainMenus(Window mainWindow) {
		super();
		this.mainWindow = mainWindow;
		mapForms.put("PURORD", po);
		mapForms.put("PURCOST", pcost);
		mapForms.put("DAILY_GATEPASS", gpass);
		mapForms.put(FORM_BDV, bdv);
		mapForms.put(FORM_APPROVALS, aprvls);
		mapForms.put(FORM_LOCK_AUDIT_PERIOD, lockauditperiod);
		mapForms.put(FORM_PLANS, plansfrm);
		mapForms.put(FORM_QUICKREP, qcfrm);
		mapForms.put(FORM_CUSTOMER_SUBSCRIPTION, subs);
		mapForms.put(FORM_CUSTOMER_REG, reg);
		mapForms.put(FORM_DELIVERY, dlv);
		mapForms.put(FORM_CUSTOMER, cust);
		mapForms.put(FORM_JOB_TYPES, jobtypes);
		mapForms.put(FORM_SERIAL_UPLOADS, serialuploads);
		mapForms.put(FORM_DOCTORS, doc);
		mapForms.put(FORM_PATIENTS, patients);
		mapForms.put(FORM_CLINIC_MAIN, mainclinic);
		mapForms.put(FORM_LISTS, lst);
		utilsExe.mapForms.clear();
		utilsExe.mapForms.putAll(mapForms);

		createView();
	}

	public void showApprovalOrders() {
		final QueryView qv = new QueryView();
		final String approv = "APPROV";
		mapActionStrs.clear();
		mapActionStrs.put(approv, "Do Approv");
		centralPanel.removeAllComponents();
		centralPanel.setHeight("100%");
		qv.setSizeFull();
		ResourceManager.addComponent(centralPanel, qv);
		// centralPanel.addComponent(qv);
		try {
			qv.setSqlquery("select acaccount.name order_no,ord_no,ord_ref,ord_refnm||' '||ord_ref ord_refnm,ord_amt-ord_discamt ord_amount"
					+ " from order1,acaccount where acaccount.accno=order1.ordacc and ord_code=103  and ordacc is not null and ord_flag=0 order by ord_no");
			qv.createDBClassFromConnection(Channelplus3Application
					.getInstance().getFrmUserLogin().getDbc().getDbConnection());
			qv.getListGroupsBy().add("ORDER_NO");
			qv.reportSetting.doStandard();
			qv.reportSetting.setTitle("Purchase Orders");
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
					qv.getLctb()
							.getColByName("ORD_AMOUNT")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);

				}

				public void afterVisual() {
					qv.getTable().setHeight("100%");
					qv.getTable().addActionHandler(new Handler() {
						public void do_approv(float on) {
							Connection con = Channelplus3Application
									.getInstance().getFrmUserLogin().getDbc()
									.getDbConnection();
							String sql = "UPDATE ORDER1 SET ORD_FLAG=1,APPROVED_BY='"
									+ utils.DBUSER
									+ "',approved_time=sysdate where ord_code=103 and "
									+ "ord_no=" + on;
							try {
								utils.execSql(sql, con);
								con.commit();
							} catch (SQLException e) {
								Logger.getLogger(frmMainMenus.class.getName())
										.log(Level.SEVERE, null, e);
								getWindow().showNotification(
										"Unable to update :", e.getMessage(),
										Notification.TYPE_ERROR_MESSAGE);
							}

						}

						public void handleAction(Action action, Object sender,
								Object target) {
							int rowno = Integer.valueOf(target.toString());
							float purk = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "ORD_NO"))
									.floatValue();
							if (((Action) action).getCaption().equals(
									mapActionStrs.get("APPROV"))) {
								do_approv(purk);
								try {
									qv.createView(true);
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}

						public Action[] getActions(Object target, Object sender) {
							int rowno = Integer.valueOf(target.toString());
							List<Action> acts = new ArrayList<Action>();
							if (rowno < 0) {
								return null;
							}

							acts.clear();
							acts.add(new Action(mapActionStrs.get("APPROV")));
							Action[] ac_ar = new Action[acts.size()];
							return acts.toArray(ac_ar);
						}

					});
				}
			});

			qv.createView();

		} catch (Exception ex) {
			Logger.getLogger(frmPurchaseCost.class.getName()).log(Level.SEVERE,
					null, ex);
			centralPanel.getWindow().showNotification("Error loading form ",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public void showPurchaePeiodicQuery() {
		final QueryView qv = new QueryView();
		centralPanel.removeAllComponents();
		centralPanel.setHeight("100%");
		qv.setSizeFull();
		// centralPanel.addComponent(qv);
		ResourceManager.addComponent(centralPanel, qv);
		try {
			qv.setSqlquery("select acaccount.name order_no, order1.ord_no, to_char(invoice_date,'dd/mm/rrrr') Closing_Date, "
					+ " ord_ref,  ord_refnm||' '||ord_ref ord_refnm,"
					+ " ord_amt-ord_discamt ord_amount, pur1.chg_kdamt Other_Expenses,"
					+ " ((pur1.inv_amt-pur1.disc_amt)*rate)+chg_kdamt Kd_Amount, pur1.currency,  pur1.rate,  pur1.kdcost"
					+ " from order1,acaccount,pur1 where "
					+ " acaccount.accno=order1.ordacc and ord_code=103  and "
					+ " ordacc is not null and  "
					+ " pur1.orderno=order1.ord_no and "
					+ " pur1.invoice_code=11 " + " order by order1.ord_no");
			qv.createDBClassFromConnection(Channelplus3Application
					.getInstance().getFrmUserLogin().getDbc().getDbConnection());
			qv.getListGroupsBy().add("ORDER_NO");
			qv.reportSetting.doStandard();
			qv.reportSetting.setTitle("Periodic Purchase Cost");

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
					qv.getLctb()
							.getColByName("ORD_AMOUNT")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb()
							.getColByName("KD_AMOUNT")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb()
							.getColByName("OTHER_EXPENSES")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb()
							.getColByName("KDCOST")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);

					qv.getLctb().getColByName("ORD_AMOUNT")
							.setTitle("Order Amount");
					qv.getLctb().getColByName("KDCOST").setTitle("KD Cost");

				}

				public void afterVisual() {
					qv.getTable().setHeight("100%");
				}
			});
			qv.createView();

		} catch (Exception ex) {
			Logger.getLogger(frmPurchaseCost.class.getName()).log(Level.SEVERE,
					null, ex);
			centralPanel.getWindow().showNotification("Error loading form ",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);

		}
	}

	public void showPurchaePeiodicItemQuery() {
		final QueryView qv = new QueryView();
		centralPanel.removeAllComponents();
		// centralPanel.addComponent(qv);
		ResourceManager.addComponent(centralPanel, qv);
		qv.setSizeFull();
		centralPanel.setHeight("100%");
		try {
			qv.setSqlquery("select acaccount.name order_no, order1.ord_no, to_char(pur1.invoice_date,'dd/mm/rrrr') Closing_Date, "
					+ " ord_ref,  ord_refnm||' '||ord_ref ord_refnm,"
					+ " pur2.refer, nvl(items.descra,items.descr) descr, (pur2.price-pur2.disc_amt) price, "
					+ " pur2.pkcost*pur2.pack pack_cost,"
					+ " pur1.rate,pur1.kdcost,pur2.pkcost*pur2.allqty amount"
					+ " from pur1,pur2,order1,acaccount,items "
					+ " where order1.ord_no=pur1.orderno and order1.ord_code=103 and "
					+ " pur2.keyfld=pur1.keyfld and items.reference=pur2.refer and "
					+ " acaccount.accno=ordacc order by ord_ref,pur1.invoice_date");
			qv.createDBClassFromConnection(Channelplus3Application
					.getInstance().getFrmUserLogin().getDbc().getDbConnection());
			qv.reportSetting.doStandard();
			qv.setShowGroupHead(true);
			qv.getListGroupsBy().add("ORD_REFNM");
			qv.getListGroupsBy().add("ORD_REF");
			qv.getListGroupSum().add("AMOUNT");
			qv.reportSetting.setTitle("Periodic Purchase Cost Items");
			qv.getDataListners().add(new tableDataListner() {

				public String getCellStyle(qryColumn qc, int recordNo) {
					return null;
				}

				public String calcSummary(List<String> qcGroup, qryColumn qc) {
					if (qcGroup != null && qcGroup.size() > 0
							&& qc.getColname().equals("AMOUNT")) {
						String s = String.valueOf(qv.getLctb().getSummaryOf(
								qv.getListGroupsBy(), "AMOUNT",
								localTableModel.SUMMARY_SUM));
						return s;
					}

					if (qcGroup == null && qc.getColname().equals("AMOUNT")) {
						String s = String.valueOf(qv.getLctb().getSummaryOf(
								"AMOUNT", localTableModel.SUMMARY_SUM));
						return s;
					}
					return "";

				}

				public void beforeQuery() {
				}

				public void afterQuery() {
					qv.getLctb()
							.getColByName("price")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb()
							.getColByName("pack_cost")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb()
							.getColByName("rate")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb()
							.getColByName("kdcost")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb()
							.getColByName("amount")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb().getColByName("KDCOST").setTitle("KD Cost");
					qv.getLctb().getColByName("ORD_REFNM").setTitle("Supplier");
				}

				public void afterVisual() {
					qv.getTable().setHeight("100%");
				}
			});
			qv.createView(false);
		} catch (Exception ex) {
			Logger.getLogger(frmMainMenus.class.getName()).log(Level.SEVERE,
					null, ex);
			centralPanel.getWindow().showNotification("Error loading form ",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void showQuery(String code, String title) {
		QueryBuilder qb = new QueryBuilder(Channelplus3Application
				.getInstance().getFrmUserLogin().getDbc().getDbConnection(),
				code);

		try {
			centralPanel.setSizeFull();
			qb.title1 = title;
			qb.buildSql(true, centralPanel);
		} catch (Exception e) {
			getWindow().showNotification(e.toString());
			Logger.getLogger(frmMainMenus.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	public void change_profile(final String prof) {

		Callback cb = new Callback() {

			public void onDialogResult(boolean resultIsYes) {
				if (resultIsYes) {
					String dc = prof;
					Channelplus3Application.getInstance().getFrmUserLogin()
							.show_module_selection(dc);

				}

			}

		};

		if (centralPanel.getComponentCount() > 0) {
			getWindow().addWindow(
					new YesNoDialog("CHAINEL Bi", "Do you want to switch to :"
							+ prof, cb));

		} else {
			Channelplus3Application.getInstance().getFrmUserLogin()
					.show_module_selection(prof);

		}
	}
}
