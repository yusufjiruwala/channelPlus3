package com.windows;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.vaadin.vaadinvisualizations.ColumnChart;

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

public class frmMain extends VerticalLayout implements ValueChangeListener {
	private Label lblCompanyName = new Label(utils.textCompanyName);
	private Label specifiecation2 = new Label(utils.textSpec2);
	private Label specifiecation = new Label(utils.textSpec1);

	private HorizontalLayout mainLayout = new HorizontalLayout();
	private VerticalLayout centralPanel = new VerticalLayout();
	private Tree tree = null;

	final static String MASTER = "Master";
	final static String TRANSACTION = "Transaction";
	final static String REPORTS = "Reports";

	final AccountQueries mAccountQueries = new AccountQueries();

	final static String MainMenus[] = { MASTER, TRANSACTION, REPORTS };

	private menuItem[][] menus = new menuItem[][] {
			new menuItem[] {

			new menuItem("FS", "Financial Statments"),
					new menuItem("GL", "Trial balance"),
					new menuItem("STATMENT", "Accounts Ledger"), },

			new menuItem[] { new menuItem("ANALYTICAL", "Analytical Reports"),
					new menuItem("ANA1", "Report1"),
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
		Item masterItem = tree.addItem(MASTER);
		tree.addItem(TRANSACTION);
		tree.addItem(REPORTS);
		tree.setChildrenAllowed(MASTER, true);
		tree.setChildrenAllowed(TRANSACTION, true);
		tree.setChildrenAllowed(REPORTS, true);

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

		mAccountQueries.setParentLayout(centralPanel);
	}

	public frmMain(Window mainWindow) {
		super();
		this.mainWindow = mainWindow;
		createView();

	}

	public void valueChange(ValueChangeEvent event) {
		if (event.getProperty().getValue() == null) {
			return;
		}

		String s = ((menuItem) event.getProperty().getValue()).getId();
		centralPanel.removeAllComponents();
		centralPanel.setWidth("700px");
		if (s.equals(menus[0][1].getId()) /* && canshowForm(s) */) {
			mAccountQueries.showTrialbalance();
		}
		if (s.equals(menus[0][2].getId()) /* && canshowForm(s) */) {
			centralPanel.removeAllComponents();
			ColumnChart cc = new ColumnChart();
			cc.setOption("is3D", true);
			cc.setOption("isStacked", true);

			cc.addXAxisLabel("Year");
			cc.addColumn("Expenses");
			cc.addColumn("Sales");
			cc.addColumn("Stock");

			// Values in double are Expenses, Sales, Stock
			cc.add("2004", new double[] { 100, 200, 320 });
			cc.add("2005", new double[] { 75, 100, 250 });
			cc.add("2006", new double[] { 32, 234, 75 });
			cc.add("2007", new double[] { 25, 253, 4 });
			cc.add("2008", new double[] { 343, 12, 260 });
			cc.setWidth("700px");
			cc.setHeight("700px");
			centralPanel.addComponent(cc);

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