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
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
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
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;

public class frmLocations implements transactionalForm {
	private AbstractLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();

	private VerticalLayout tableLayout = new VerticalLayout();
	TableLayoutVaadin tableLocations = new TableLayoutVaadin(tableLayout,
			false, false);
	private final List<ColumnProperty> lstLocationsCols = new ArrayList<ColumnProperty>();
	private VerticalLayout mainLayout = new VerticalLayout();
	private Panel basicPanel = new Panel();

	private VerticalLayout basicLayout = new VerticalLayout();
	private HorizontalLayout contentLayout = new HorizontalLayout();
	private HorizontalLayout codeLayout = new HorizontalLayout();
	private HorizontalLayout nameLayout = new HorizontalLayout();
	private HorizontalLayout name2Layout = new HorizontalLayout();
	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout startKeyIdLayout = new HorizontalLayout();
	private HorizontalLayout endKeyIdLayout = new HorizontalLayout();
	private HorizontalLayout lockLayout = new HorizontalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout nullLayout = new HorizontalLayout();

	private CheckBox chkLock = ControlsFactory.CreateCheckField("Lock", "Flag",
			"1", "2", lstfldinfo);
	private CheckBox chkHasGatepass = ControlsFactory.CreateCheckField(
			"Has Gatepass", "HAS_GATEPASS", "N", "Y", lstfldinfo);

	private TextField txtCode = ControlsFactory.CreateTextField("", "code",
			lstfldinfo);
	private TextField txtName = ControlsFactory.CreateTextField("", "name",
			lstfldinfo);
	private TextField txtNamea = ControlsFactory.CreateTextField("", "namea",
			lstfldinfo);
	private TextField txtStartKey = ControlsFactory.CreateTextField("",
			"FROM_KEYFLD", lstfldinfo, "", "0");
	private TextField txtEndKey = ControlsFactory.CreateTextField("",
			"TO_KEYFLD", lstfldinfo, "", "0");

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");

	private Label lblCode = new Label("Code : ");
	private Label lblName = new Label("Name : ");
	private Label lblName2 = new Label("Name2 : ");
	private Label lblStartKey = new Label("Starting Key : ");
	private Label lblEndKey = new Label("End Key : ");
	private Label emptyLabel = new Label("");

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
			qe.setParaValue("FROM_SALE", "0");
			qe.setParaValue("TO_SALE", "0");
			qe.setParaValue("FROM_SALERET", "0");
			qe.setParaValue("TO_SALERET", "0");
			qe.setParaValue("DEFAULT_SEQ", "A");

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("LOCATIONS"); // if
			} else {
				qe.AutoGenerateUpdateStatment("LOCATIONS", "'CODE'",
						" WHERE CODE=:code"); // if to update..
			}
			qe.execute();
			con.commit();
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

	public void load_data() {
		try {
			txtCode.setReadOnly(false);
			utilsVaadin.resetValues(basicLayout, false, false);
			utilsVaadin.setDefaultValues(lstfldinfo, false);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"select * from locations where code='" + QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				txtCode.setReadOnly(true);
			}
			tableLocations.data.executeQuery(
					"select * from invoiceType where location_code = '"
							+ QRYSES + "'", true);
			tableLocations.fill_table();

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void applyColumns() {
		lstLocationsCols.clear();
		ColumnProperty cp = new ColumnProperty();
		cp.col_class = Label.class;
		cp.colname = "NO";
		cp.descr = "No";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		tableLocations.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = Label.class;
		cp.colname = "DESCR";
		cp.descr = "Descr";
		cp.display_width = 150;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableLocations.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = Label.class;
		cp.colname = "ACCNO";
		cp.descr = "Fixed Debit Account";
		cp.display_width = 150;
		cp.display_type = "NONE";
		cp.pos = 3;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableLocations.addColumn(cp, false);
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
		applyColumns();
		load_data();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		mainLayout.setWidth("100%");
		basicPanel.setWidth("650px");
		tableLayout.setWidth("100%");
		contentLayout.setWidth("100%");
		content2Layout.setWidth("100%");
		codeLayout.setWidth("100%");
		nameLayout.setWidth("100%");
		name2Layout.setWidth("100%");
		startKeyIdLayout.setWidth("100%");
		endKeyIdLayout.setWidth("100%");
		lockLayout.setWidth("100%");

		lblName2.setWidth("90px");
		lblStartKey.setWidth("100px");
		lblEndKey.setWidth("60px");
		lblCode.setWidth("100px");
		lblName.setWidth("90px");

		txtCode.setWidth("100%");
		txtName.setWidth("100%");
		txtNamea.setWidth("100%");
		txtStartKey.setWidth("100%");
		txtEndKey.setWidth("100%");

		tableLocations.setHeight("300px");

		basicLayout.addStyleName("formLayout");

		tableLocations.hideToolbar();
		tableLocations.getTable().setImmediate(true);
		tableLocations.getTable().setCaption(
				"Double Click here to Open Invoice Type Form ");
		tableLocations.getTable().setSelectable(true);

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicPanel);

		ResourceManager.addComponent(basicPanel, basicLayout);

		ResourceManager.addComponent(basicLayout, lockLayout);
		ResourceManager.addComponent(basicLayout, contentLayout);
		ResourceManager.addComponent(basicLayout, nameLayout);
		ResourceManager.addComponent(basicLayout, name2Layout);
		ResourceManager.addComponent(basicLayout, content2Layout);
		ResourceManager.addComponent(basicLayout, nullLayout);
		ResourceManager.addComponent(basicLayout, tableLayout);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdCls);

		ResourceManager.addComponent(lockLayout, chkLock);

		ResourceManager.addComponent(contentLayout, codeLayout);

		ResourceManager.addComponent(codeLayout, lblCode);
		ResourceManager.addComponent(codeLayout, txtCode);

		ResourceManager.addComponent(nameLayout, lblName);
		ResourceManager.addComponent(nameLayout, txtName);

		ResourceManager.addComponent(contentLayout, emptyLabel);

		ResourceManager.addComponent(name2Layout, lblName2);
		ResourceManager.addComponent(name2Layout, txtNamea);

		ResourceManager.addComponent(content2Layout, startKeyIdLayout);

		ResourceManager.addComponent(startKeyIdLayout, lblStartKey);
		ResourceManager.addComponent(startKeyIdLayout, txtStartKey);

		ResourceManager.addComponent(content2Layout, endKeyIdLayout);

		ResourceManager.addComponent(endKeyIdLayout, lblEndKey);
		ResourceManager.addComponent(endKeyIdLayout, txtEndKey);

		ResourceManager.addComponent(content2Layout, chkHasGatepass);

		ResourceManager.addComponent(tableLayout, tableLocations);

		lockLayout.setComponentAlignment(chkLock, Alignment.BOTTOM_RIGHT);
		codeLayout.setComponentAlignment(lblCode, Alignment.BOTTOM_CENTER);
		nameLayout.setComponentAlignment(lblName, Alignment.BOTTOM_CENTER);
		name2Layout.setComponentAlignment(lblName2, Alignment.BOTTOM_CENTER);
		startKeyIdLayout.setComponentAlignment(lblStartKey,
				Alignment.BOTTOM_CENTER);
		endKeyIdLayout
				.setComponentAlignment(lblEndKey, Alignment.BOTTOM_CENTER);
		content2Layout.setComponentAlignment(chkHasGatepass,
				Alignment.BOTTOM_CENTER);

		contentLayout.setExpandRatio(codeLayout, 1.6f);
		contentLayout.setExpandRatio(emptyLabel, 2.4f);

		codeLayout.setExpandRatio(lblCode, 0.1f);
		codeLayout.setExpandRatio(txtCode, 3.9f);

		nameLayout.setExpandRatio(lblName, 0.1f);
		nameLayout.setExpandRatio(txtName, 3.9f);

		name2Layout.setExpandRatio(lblName2, 0.1f);
		name2Layout.setExpandRatio(txtNamea, 3.9f);

		content2Layout.setExpandRatio(startKeyIdLayout, 1.9f);
		content2Layout.setExpandRatio(endKeyIdLayout, 2f);
		content2Layout.setExpandRatio(chkHasGatepass, 0.1f);

		startKeyIdLayout.setExpandRatio(lblStartKey, 0.1f);
		startKeyIdLayout.setExpandRatio(txtStartKey, 3.9f);

		endKeyIdLayout.setExpandRatio(lblEndKey, 0.1f);
		endKeyIdLayout.setExpandRatio(txtEndKey, 3.9f);

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
			tableLocations.getTable().addListener(new ItemClickListener() {
				public void itemClick(ItemClickEvent event) {
					Integer id = (Integer) event.getItemId();
					if (event.isDoubleClick()) {
						frmLocationTypes inv = new frmLocationTypes();
						Window wnd = ControlsFactory.CreateWindow("600px",
								"600px", true, true);
						wnd.center();
						wnd.addListener(new CloseListener() {

							public void windowClose(CloseEvent e) {
								load_data();

							}
						});
						inv.setParentLayout((AbstractOrderedLayout) wnd
								.getContent());
						inv.showInitView();
						inv.QRYSESLocationCode = QRYSES;
						inv.QRYSESLocationNo = tableLocations.data
								.getFieldValue(id.intValue(), "NO").toString();
						inv.load_data();
					}
				}
			});
			listnerAdded = true;
		}

	}

	public void showInitView() {
		QRYSES = "";
		initForm();
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();

		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		contentLayout.removeAllComponents();
		codeLayout.removeAllComponents();
		nameLayout.removeAllComponents();
		name2Layout.removeAllComponents();
		content2Layout.removeAllComponents();
		startKeyIdLayout.removeAllComponents();
		endKeyIdLayout.removeAllComponents();
		lockLayout.removeAllComponents();
		buttonLayout.removeAllComponents();
		nullLayout.removeAllComponents();
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
							QRYSES = tv.getData().getFieldValue(rn, "code")
									.toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select code, name from locations  order by code", true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
