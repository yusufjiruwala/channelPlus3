package com.windows.Masters.General;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.dataCell;
import com.generic.transactionalForm;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmProfile implements transactionalForm {
	private AbstractLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	Panel basicPanel = new Panel();
	private final List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout checkLayout = new VerticalLayout();
	private VerticalLayout contentLayout = new VerticalLayout();

	private HorizontalLayout profileLayout = new HorizontalLayout();
	private HorizontalLayout typeOfScreenLayout = new HorizontalLayout();
	private HorizontalLayout tableLayout = new HorizontalLayout();
	private HorizontalLayout basicLayout = new HorizontalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout check1Layout = new HorizontalLayout();
	private HorizontalLayout check2Layout = new HorizontalLayout();
	private HorizontalLayout searchLayout = new HorizontalLayout();

	TableLayoutVaadin table = new TableLayoutVaadin(tableLayout);
	private NativeButton cmdSearch = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");

	private CheckBox chkCheckAllAccess = ControlsFactory.CreateCheckField(
			"Check / Uncheck Access", "flag", "1", "2", lstfldinfo);
	private CheckBox chkCheckAllUpdate = ControlsFactory.CreateCheckField(
			"Check / Uncheck Update", "flag", "1", "2", lstfldinfo);
	private CheckBox chkCheckAllDelete = ControlsFactory.CreateCheckField(
			"Check / Uncheck Delete", "flag", "1", "2", lstfldinfo);
	private CheckBox chkCheckAllInsert = ControlsFactory.CreateCheckField(
			"Check / Uncheck Insert", "flag", "1", "2", lstfldinfo);

	private Label lblProfile = new Label("Profile : ");
	private Label lblTypeScreen = new Label("Type Of Screen : ");
	private Label emptyLabel1 = new Label("");
	private Label emptyLabel2 = new Label("");
	private Label lblSearch = new Label("Search : ");
	private Label emptyLabel3 = new Label();

	private TextField txtProfile = ControlsFactory.CreateTextField("",
			"profileno", lstfldinfo);
	private TextField txtSearh = ControlsFactory.CreateTextField("", "", null);

	private ComboBox cmbTypeOfScreen = ControlsFactory.CreateListField("",
			"typeofscren",
			"Select distinct typeofscren, typeofscren t1 from cp_security ",
			lstfldinfo);

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");

	private NativeButton cmdGetData = new NativeButton("Get Data");

	public void save_data() {

	}

	public void applyColumns() {
		lstItemCols.clear();
		ColumnProperty cp = new ColumnProperty();
		cp.col_class = Label.class;
		cp.colname = "TITLE_ENG";
		cp.descr = "Title_Eng";
		cp.display_width = 300;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = CheckBox.class;
		cp.colname = "ACCESS_PROFILE";
		cp.descr = "Access";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = CheckBox.class;
		cp.colname = "UPDATE_PROFILE";
		cp.descr = "Update";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 3;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = CheckBox.class;
		cp.colname = "DELETE_PROFILE";
		cp.descr = "Delete";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 4;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = CheckBox.class;
		cp.colname = "INSERT_PROFILE";
		cp.descr = "Insert";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 5;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		table.addColumn(cp, false);

	}

	public void load_data() {
		try {
			/*
			 * lblMessage.setValue("Adding New Item....");
			 * txtProCode.setReadOnly(false);
			 * utilsVaadin.resetValues(basicLayout, false, false);
			 */
			if (!QRYSES.isEmpty()) {
				/*
				 * PreparedStatement pstmt = con .prepareStatement(
				 * "Select * from Items  where reference='" + QRYSES + "'",
				 * ResultSet.TYPE_SCROLL_INSENSITIVE,
				 * ResultSet.CONCUR_READ_ONLY); ResultSet rst =
				 * pstmt.executeQuery(); utilsVaadin.assignValues(rst,
				 * lstfldinfo); pstmt.close(); fetch_AccountName();
				 * txtProCode.setReadOnly(true);
				 * lblMessage.setValue("Editing : "+txtDescr1.getValue());
				 */
			}

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

		mainLayout.setWidth("600px");

		profileLayout.setWidth("100%");
		typeOfScreenLayout.setWidth("100%");
		tableLayout.setWidth("100%");
		check1Layout.setWidth("100%");
		check2Layout.setWidth("100%");
		checkLayout.setWidth("100%");
		contentLayout.setWidth("100%");
		searchLayout.setWidth("100%");

		lblProfile.setWidth("100px");
		lblTypeScreen.setWidth("100px");
		lblSearch.setWidth("90px");

		txtProfile.setWidth("100%");
		cmbTypeOfScreen.setWidth("100%");
		txtSearh.setWidth("100%");

		emptyLabel1.setWidth("10px");
		emptyLabel2.setWidth("10px");
		emptyLabel3.setWidth("4px");

		table.getTable().setSelectable(true);

		table.hideToolbar();
		table.hideAddCommand();
		table.hideDelCommand();
		table.hideRevertCommand();
		cmbTypeOfScreen.setImmediate(true);
		chkCheckAllAccess.setImmediate(true);

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicPanel);

		ResourceManager.addComponent(basicPanel, basicLayout);
		ResourceManager.addComponent(basicPanel, searchLayout);
		ResourceManager.addComponent(basicPanel, tableLayout);

		ResourceManager.addComponent(basicLayout, contentLayout);

		ResourceManager.addComponent(contentLayout, profileLayout);
		ResourceManager.addComponent(contentLayout, typeOfScreenLayout);

		ResourceManager.addComponent(tableLayout, table);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdGetData);

		ResourceManager.addComponent(check1Layout, chkCheckAllAccess);
		ResourceManager.addComponent(check1Layout, chkCheckAllUpdate);

		ResourceManager.addComponent(check2Layout, chkCheckAllDelete);
		ResourceManager.addComponent(check2Layout, emptyLabel3);
		ResourceManager.addComponent(check2Layout, chkCheckAllInsert);

		ResourceManager.addComponent(profileLayout, lblProfile);
		ResourceManager.addComponent(profileLayout, txtProfile);
		ResourceManager.addComponent(profileLayout, emptyLabel1);

		ResourceManager.addComponent(searchLayout, lblSearch);
		ResourceManager.addComponent(searchLayout, txtSearh);
		ResourceManager.addComponent(searchLayout, cmdSearch);

		ResourceManager.addComponent(typeOfScreenLayout, lblTypeScreen);
		ResourceManager.addComponent(typeOfScreenLayout, cmbTypeOfScreen);
		ResourceManager.addComponent(typeOfScreenLayout, emptyLabel2);

		ResourceManager.addComponent(typeOfScreenLayout, checkLayout);
		ResourceManager.addComponent(checkLayout, check1Layout);
		ResourceManager.addComponent(checkLayout, check2Layout);

		profileLayout
				.setComponentAlignment(lblProfile, Alignment.BOTTOM_CENTER);

		typeOfScreenLayout.setComponentAlignment(lblTypeScreen,
				Alignment.BOTTOM_CENTER);

		searchLayout.setComponentAlignment(lblSearch, Alignment.BOTTOM_CENTER);
		searchLayout.setComponentAlignment(cmdSearch, Alignment.BOTTOM_CENTER);

		mainLayout.setExpandRatio(buttonLayout, 0.1f);
		mainLayout.setExpandRatio(basicPanel, 3.9f);

		contentLayout.setExpandRatio(profileLayout, 2f);
		contentLayout.setExpandRatio(typeOfScreenLayout, 2f);

		checkLayout.setExpandRatio(check1Layout, 2f);
		checkLayout.setExpandRatio(check2Layout, 2f);

		searchLayout.setExpandRatio(lblSearch, 0.1f);
		searchLayout.setExpandRatio(txtSearh, 3.8f);
		searchLayout.setExpandRatio(cmdSearch, 0.1f);

		profileLayout.setExpandRatio(lblProfile, 0.1f);
		profileLayout.setExpandRatio(txtProfile, 1.2f);
		profileLayout.setExpandRatio(emptyLabel1, 2.7f);

		typeOfScreenLayout.setExpandRatio(lblTypeScreen, 0.1f);
		typeOfScreenLayout.setExpandRatio(cmbTypeOfScreen, 1f);

		check1Layout.setExpandRatio(chkCheckAllAccess, 2f);
		check1Layout.setExpandRatio(chkCheckAllUpdate, 2f);

		check2Layout.setExpandRatio(chkCheckAllDelete, 2f);
		check2Layout.setExpandRatio(chkCheckAllInsert, 2f);

		if (!listnerAdded) {
			cmbTypeOfScreen.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						applyColumns();
						table.data.executeQuery(
								"Select * from cp_security where typeofscren = '"
										+ ((dataCell) cmbTypeOfScreen
												.getValue()).getValue() + "'",
								true);
						table.fill_table();
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			});
			chkCheckAllAccess.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					List<Object> accessData = new ArrayList<Object>(table
							.getTable().getItemIds());

					for (Iterator i = accessData.iterator(); i.hasNext();) {
						Object iid = i.next();
						Item item = table.getTable().getItem(iid);
						CheckBox vlObj = (CheckBox) item.getItemProperty(
								utilsVaadin.findColByCol("ACCESS_PROFILE",
										table.listFields).descr).getValue();
						if (vlObj != null) {
							vlObj.setValue(chkCheckAllAccess.booleanValue());
						}
					}
				}
			});
			listnerAdded = true;
		}

	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void showInitView() {
		QRYSES = "";
		initForm();
	}
}
