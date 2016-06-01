package com.windows;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import com.generic.menuItem;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class frmMainWareHousing extends VerticalLayout implements
		ValueChangeListener {
	private Label lblCompanyName = new Label(utils.textCompanyName);
	private Label specifiecation = new Label(utils.textSpec1);
	private Label specifiecation2 = new Label(utils.textSpec2);

	private HorizontalLayout mainLayout = new HorizontalLayout();
	private VerticalLayout centralPanel = new VerticalLayout();
	private Tree tree = null;

	final static String MASTER = "Gate pass";
	final static String TRANSACTION = "Approval";

	final frmGatePass gatepass = new frmGatePass();

	final static String MainMenus[] = { MASTER, TRANSACTION };

	private menuItem[][] menus = new menuItem[][] {
			new menuItem[] { new menuItem("DailyGP", "Daily Gate Pass"),
					new menuItem("QRYGATEPASS", "Query Gate Pass") },
			new menuItem[] { new menuItem("APPROVAL", "Approval Management"),
					new menuItem("EXIT", "Log Out") } };
	private Window mainWindow = null;

	public void createView() {
		mainWindow.setStyleName("financialWindow");
		mainWindow.setSizeFull();
		mainWindow.removeAllComponents();
		Panel treePanel = new Panel(); // f
		treePanel.addStyleName(Panel.STYLE_LIGHT);
		mainWindow.setWidth("100%");
		mainWindow.setHeight("100%");
		// mainWindow.center();

		treePanel.getContent().setSizeUndefined();

		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		addComponent(lblCompanyName);
		addComponent(specifiecation);
		addComponent(specifiecation2);
		lblCompanyName.setStyleName("formTitle");
		addComponent(mainLayout);

		// mainWindow.setContent(this);

		tree = new Tree();
		tree.setSelectable(true);
		tree.setNullSelectionAllowed(false);
		tree.addItem(MASTER);
		tree.addItem(TRANSACTION);
		tree.setChildrenAllowed(MASTER, true);
		tree.setChildrenAllowed(TRANSACTION, true);
		tree.setWidth("100%");

		for (int j = 0; j < menus.length; j++) {
			for (int i = 0; i < menus[j].length; i++) {
				Item itm = tree.addItem(menus[j][i]);
				tree.setChildrenAllowed(menus[j][i], false);
				tree.setParent(menus[j][i], MainMenus[j]);
			}

		}

		tree.addListener(this);
		tree.setImmediate(true);

		for (Iterator<?> it = tree.rootItemIds().iterator(); it.hasNext();) {
			tree.expandItemsRecursively(it.next());
		}

		treePanel.addComponent(tree);
		mainLayout.addComponent(treePanel);
		mainLayout.addComponent(centralPanel);

		gatepass.setParentLayout(centralPanel);
		centralPanel.removeAllComponents();
		centralPanel.setWidth("750px");
		gatepass.showDailyGatePass();

	}

	public frmMainWareHousing(Window mainWindow) {
		super();
		this.mainWindow = mainWindow;
		createView();

	}

	public void valueChange(ValueChangeEvent event) {
		centralPanel.removeAllComponents();
		centralPanel.setWidth("750px");

		if (event.getProperty().getValue() == null) {
			return;
		}

		String s = ((menuItem) event.getProperty().getValue()).getId();
		if (s.equals("DailyGP") /* && canshowForm(s) */) {
			gatepass.showDailyGatePass();
		}
		if (s.equals("QRYGATEPASS") /* && canshowForm(s) */) {

		}
		if (s.equals("APPROVAL") /* && canshowForm(s) */) {

		}

		if (s.equals("EXIT")) {
			Connection con = Channelplus3Application.getInstance()
					.getFrmUserLogin().getDbc().getDbConnection();
			try {
				con.rollback();
				con.close();
			} catch (SQLException e) {
			}

			Channelplus3Application.getInstance().close();
			mainWindow.open(new ExternalResource(Channelplus3Application
					.getInstance().loginWindow.getURL()));
		}

	}

	private boolean canshowForm(String str) {
		if (!utilsVaadin.CanIOpen(str)) {
			getWindow().showNotification("Oops.! ",
					"you are not authorized to view here..",
					Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		return true;
	}

}
