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
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.dataCell;
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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmEmpInventory implements transactionalForm {
	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	Tree tree = new Tree();
	private localTableModel data_Items = new localTableModel();

	private final List<ColumnProperty> lstContractCols = new ArrayList<ColumnProperty>();

	private Panel basicPanel = new Panel();

	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();
	private VerticalLayout tableLayout = new VerticalLayout();

	private HorizontalLayout contentLayout = new HorizontalLayout();
	private HorizontalLayout noLayout = new HorizontalLayout();
	private HorizontalLayout userLayout = new HorizontalLayout();
	private HorizontalLayout nameLayout = new HorizontalLayout();
	private HorizontalLayout name2Layout = new HorizontalLayout();
	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout typeLayout = new HorizontalLayout();
	private HorizontalLayout vehicleLayout = new HorizontalLayout();
	private HorizontalLayout labelLayout = new HorizontalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();

	private Label lblNo = new Label("No");
	private Label lblUsers = new Label("User");
	private Label lblName = new Label("Name");
	private Label lblNameA = new Label("Name2");
	private Label lblType = new Label("Type");
	private Label lblVehicle = new Label("Vehicle No");
	private Label lblCommission = new Label("Commission Base");
	private Label emptyLabel = new Label("");

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");

	private TextField txtNo = ControlsFactory.CreateTextField("", "NO",
			lstfldinfo);
	private TextField txtUser = ControlsFactory.CreateTextField("", "DLIC",
			lstfldinfo);
	private TextField txtName = ControlsFactory.CreateTextField("", "NAME",
			lstfldinfo);
	private TextField txtNameA = ControlsFactory.CreateTextField("", "NAMEA",
			lstfldinfo);
	private ComboBox txtType = ControlsFactory.CreateListField("", "TYPE", "",
			lstfldinfo, "", "S");
	private TextField txtVehicle = ControlsFactory.CreateTextField("",
			"VEHICLENO", lstfldinfo);

	private CheckBox chkLock = ControlsFactory.CreateCheckField(
			"Lock / Unlock", "FLAG", "1", "2", lstfldinfo);

	TableLayoutVaadin table = new TableLayoutVaadin(tableLayout);

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

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("salesp"); // if
			} else {
				qe
						.AutoGenerateUpdateStatment("salesp", "'NO'",
								" WHERE NO=:no"); // if to update..
			}
			qe.execute();
			con.commit();
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtNo.getValue().toString();
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
			txtNo.setReadOnly(false);
			utilsVaadin.resetValues(basicLayout, false, false);
			utilsVaadin.setDefaultValues(lstfldinfo, true);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"select * from salesp where no= '" + QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				txtNo.setReadOnly(true);
			}
			table.data.executeQuery("select * from commision where no ='"
					+ QRYSES + "' order by no ", true);
			table.fill_table();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void print_data() {

	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
	}

	public void applyColumns() {
		lstContractCols.clear();
		ColumnProperty cp = new ColumnProperty();
		cp.col_class = ComboBox.class;
		cp.colname = "COMMISION_TYPE";
		cp.descr = "Type";
		cp.display_width = 100;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		cp.lov_sql = new ArrayList<dataCell>();
		cp.lov_sql.add(new dataCell("Net Sales", "NETSALES"));
		cp.lov_sql.add(new dataCell("Collection", "COLLECTION"));
		cp.lov_sql.add(new dataCell("No Of Invoices", "NOOFINVOICES"));
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "ONAMT";
		cp.descr = "On Amount";
		cp.display_width = 150;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "COMP";
		cp.descr = "Share %";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 3;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "BONUS";
		cp.descr = "Bonus";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 4;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "MEMO";
		cp.descr = "Description";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 5;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);
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
		applyColumns();
		load_data();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		mainLayout.setWidth("100%");
		basicPanel.setWidth("500px");
		basicLayout.setWidth("100%");
		contentLayout.setWidth("100%");
		noLayout.setWidth("100%");
		userLayout.setWidth("100%");
		nameLayout.setWidth("100%");
		name2Layout.setWidth("100%");
		content2Layout.setWidth("100%");
		typeLayout.setWidth("100%");
		vehicleLayout.setWidth("100%");
		labelLayout.setWidth("100%");

		lblNo.setWidth("100px");
		lblUsers.setWidth("80px");
		lblName.setWidth("90px");
		lblNameA.setWidth("90px");
		lblType.setWidth("100px");
		lblVehicle.setWidth("80px");
		lblCommission.setWidth("200px");
		emptyLabel.setHeight("20px");

		txtNo.setWidth("100%");
		txtName.setWidth("100%");
		txtNameA.setWidth("100%");
		txtUser.setWidth("100%");
		txtType.setWidth("100%");
		txtVehicle.setWidth("100%");

		table.getTable().setPageLength(5);
		basicLayout.addStyleName("formLayout");

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicPanel);

		ResourceManager.addComponent(basicPanel, basicLayout);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdCls);

		ResourceManager.addComponent(basicLayout, contentLayout);
		ResourceManager.addComponent(basicLayout, nameLayout);
		ResourceManager.addComponent(basicLayout, name2Layout);
		ResourceManager.addComponent(basicLayout, content2Layout);
		ResourceManager.addComponent(basicLayout, emptyLabel);
		ResourceManager.addComponent(basicLayout, labelLayout);
		ResourceManager.addComponent(basicLayout, tableLayout);

		ResourceManager.addComponent(contentLayout, noLayout);
		ResourceManager.addComponent(contentLayout, userLayout);

		ResourceManager.addComponent(noLayout, lblNo);
		ResourceManager.addComponent(noLayout, txtNo);

		ResourceManager.addComponent(userLayout, lblUsers);
		ResourceManager.addComponent(userLayout, txtUser);
		ResourceManager.addComponent(userLayout, chkLock);

		ResourceManager.addComponent(nameLayout, lblName);
		ResourceManager.addComponent(nameLayout, txtName);

		ResourceManager.addComponent(name2Layout, lblNameA);
		ResourceManager.addComponent(name2Layout, txtNameA);

		ResourceManager.addComponent(content2Layout, typeLayout);
		ResourceManager.addComponent(content2Layout, vehicleLayout);

		ResourceManager.addComponent(typeLayout, lblType);
		ResourceManager.addComponent(typeLayout, txtType);

		ResourceManager.addComponent(vehicleLayout, lblVehicle);
		ResourceManager.addComponent(vehicleLayout, txtVehicle);

		ResourceManager.addComponent(labelLayout, lblCommission);

		ResourceManager.addComponent(tableLayout, table);

		noLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);
		userLayout.setComponentAlignment(lblUsers, Alignment.BOTTOM_CENTER);
		userLayout.setComponentAlignment(chkLock, Alignment.BOTTOM_CENTER);
		nameLayout.setComponentAlignment(lblName, Alignment.BOTTOM_CENTER);
		name2Layout.setComponentAlignment(lblNameA, Alignment.BOTTOM_CENTER);
		typeLayout.setComponentAlignment(lblType, Alignment.BOTTOM_CENTER);
		vehicleLayout
				.setComponentAlignment(lblVehicle, Alignment.BOTTOM_CENTER);

		content2Layout.setExpandRatio(typeLayout, 1.7f);
		content2Layout.setExpandRatio(vehicleLayout, 2.3f);

		contentLayout.setExpandRatio(noLayout, 1.7f);
		contentLayout.setExpandRatio(userLayout, 2.3f);

		noLayout.setExpandRatio(lblNo, 0.1f);
		noLayout.setExpandRatio(txtNo, 3.9f);

		userLayout.setExpandRatio(lblUsers, 0.1f);
		userLayout.setExpandRatio(txtUser, 3.9f);

		nameLayout.setExpandRatio(lblName, 0.1f);
		nameLayout.setExpandRatio(txtName, 3.9f);

		name2Layout.setExpandRatio(lblNameA, 0.1f);
		name2Layout.setExpandRatio(txtNameA, 3.9f);

		typeLayout.setExpandRatio(lblType, 0.1f);
		typeLayout.setExpandRatio(txtType, 3.9f);

		vehicleLayout.setExpandRatio(lblVehicle, 0.1f);
		vehicleLayout.setExpandRatio(txtVehicle, 3.9f);

		txtType.removeAllItems();
		txtType.addItem(new dataCell("Sales Person", "S"));
		txtType.addItem(new dataCell("Drivers", "D"));
		txtType.addItem(new dataCell("Collectors", "C"));
		txtType.addItem(new dataCell("Employess", "E"));

		applyColumns();

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
			listnerAdded = true;
		}
	}

	public void showInitView() {

		QRYSES = "";
		initForm();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractOrderedLayout) parentLayout;
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

			utilsVaadin.showSearch(la, new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);
					if (tv.getSelectionValue() > -1) {
						try {
							int rn = tv.getSelectionValue();
							QRYSES = tv.getData().getFieldValue(rn, "no")
									.toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select no, name from salesp order by no", true);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
