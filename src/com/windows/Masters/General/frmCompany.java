package com.windows.Masters.General;

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
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
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

public class frmCompany implements transactionalForm {
	private AbstractLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	Tree tree = new Tree();
	private localTableModel data_Items = new localTableModel();

	private Panel basicPanel = new Panel();

	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout codeLayout = new HorizontalLayout();
	private HorizontalLayout nameLayout = new HorizontalLayout();
	private HorizontalLayout nameSpecLayout = new HorizontalLayout();
	private HorizontalLayout name2Layout = new HorizontalLayout();
	private HorizontalLayout name2SpecLayout = new HorizontalLayout();
	private HorizontalLayout licenseLayout = new HorizontalLayout();
	private HorizontalLayout licenseNoLayout = new HorizontalLayout();
	private HorizontalLayout licenseExpireLayout = new HorizontalLayout();
	private HorizontalLayout logoFileLayout = new HorizontalLayout();

	private Label lblCode = new Label("Code");
	private Label lblName = new Label("Name");
	private Label lblSpecName = new Label("Specification ");
	private Label lblName2 = new Label("Name 2");
	private Label lblSpecName2 = new Label("Specification");
	private Label lblLicenseNo = new Label("License No");
	private Label lblLicenseExpire = new Label("License Expire");
	private Label lblLogoFile = new Label("LOGO File");
	private Label emptyLabel = new Label("");

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");

	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");

	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");

	private TextField txtCode = ControlsFactory.CreateTextField("", "CODE",
			lstfldinfo);
	private TextField txtName = ControlsFactory.CreateTextField("", "NAME",
			lstfldinfo);
	private TextField txtNameSpec = ControlsFactory.CreateTextField("",
			"SPECIFICATION", lstfldinfo);
	private TextField txtName2 = ControlsFactory.CreateTextField("", "NAMEA",
			lstfldinfo);
	private TextField txtName2Spec = ControlsFactory.CreateTextField("",
			"SPECIFICATIONA", lstfldinfo);
	private TextField txtLicenseNo = ControlsFactory.CreateTextField("",
			"LICNESENO", lstfldinfo);
	private DateField txtLicenseExpire = ControlsFactory.CreateDateField("",
			"LICENSE_EXPIRE", lstfldinfo);
	private TextField txtLogoFile = ControlsFactory.CreateTextField("",
			"FILENAME", lstfldinfo);

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
				qe.AutoGenerateInsertStatment("COMPANY");
			} else {
				qe.AutoGenerateUpdateStatment("COMPANY", "'CODE'",
						" WHERE CODE=:code");
			}
			qe.execute();
			qe.close();
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
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"Select * from company where CODE='" + QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				txtCode.setReadOnly(true);
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
		try {
			if (data_Items.getDbclass() == null) {
				data_Items.createDBClassFromConnection(con);
			}

		} catch (SQLException e) {
		}
		createView();
		load_data();
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		buttonLayout.removeAllComponents();
		codeLayout.removeAllComponents();
		nameLayout.removeAllComponents();
		nameSpecLayout.removeAllComponents();
		name2Layout.removeAllComponents();
		name2SpecLayout.removeAllComponents();
		licenseLayout.removeAllComponents();
		licenseNoLayout.removeAllComponents();
		licenseExpireLayout.removeAllComponents();
		logoFileLayout.removeAllComponents();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		basicPanel.setWidth("400px");
		mainLayout.setWidth("100%");
		basicLayout.setWidth("100%");
		codeLayout.setWidth("100%");
		nameLayout.setWidth("100%");
		nameSpecLayout.setWidth("100%");
		name2Layout.setWidth("100%");
		name2SpecLayout.setWidth("100%");
		licenseLayout.setWidth("100%");
		licenseExpireLayout.setWidth("100%");
		licenseNoLayout.setWidth("100%");
		logoFileLayout.setWidth("100%");
		licenseLayout.setSpacing(true);

		lblCode.setWidth("80px");
		lblName.setWidth("80px");
		lblSpecName.setWidth("80px");
		lblName2.setWidth("80px");
		lblSpecName2.setWidth("80px");
		lblLicenseNo.setWidth("85px");
		lblLicenseExpire.setWidth("80px");
		lblLogoFile.setWidth("80px");

		txtCode.setWidth("100%");
		txtName.setWidth("100%");
		txtNameSpec.setWidth("100%");
		txtName2.setWidth("100%");
		txtName2Spec.setWidth("100%");
		txtLicenseNo.setWidth("100%");
		txtLicenseExpire.setWidth("100%");
		txtLogoFile.setWidth("100%");

		basicLayout.addStyleName("formLayout");

		txtNameSpec.setRows(3);
		txtName2Spec.setRows(3);

		txtLicenseNo.addStyleName("netAmtStyle");

		utils.findFieldInfoByObject(txtLicenseNo, lstfldinfo).format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		utils.findFieldInfoByObject(txtLicenseExpire, lstfldinfo).format = utils.FORMAT_SHORT_DATE;

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicPanel);

		ResourceManager.addComponent(basicPanel, basicLayout);

		ResourceManager.addComponent(basicLayout, codeLayout);
		ResourceManager.addComponent(basicLayout, nameLayout);
		ResourceManager.addComponent(basicLayout, nameSpecLayout);
		ResourceManager.addComponent(basicLayout, name2Layout);
		ResourceManager.addComponent(basicLayout, name2SpecLayout);
		ResourceManager.addComponent(basicLayout, licenseLayout);
		ResourceManager.addComponent(basicLayout, logoFileLayout);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdCls);

		ResourceManager.addComponent(codeLayout, lblCode);
		ResourceManager.addComponent(codeLayout, txtCode);
		ResourceManager.addComponent(codeLayout, emptyLabel);

		ResourceManager.addComponent(nameLayout, lblName);
		ResourceManager.addComponent(nameLayout, txtName);

		ResourceManager.addComponent(nameSpecLayout, lblSpecName);
		ResourceManager.addComponent(nameSpecLayout, txtNameSpec);

		ResourceManager.addComponent(name2Layout, lblName2);
		ResourceManager.addComponent(name2Layout, txtName2);

		ResourceManager.addComponent(name2SpecLayout, lblSpecName2);
		ResourceManager.addComponent(name2SpecLayout, txtName2Spec);

		ResourceManager.addComponent(licenseLayout, licenseNoLayout);
		ResourceManager.addComponent(licenseLayout, licenseExpireLayout);

		ResourceManager.addComponent(logoFileLayout, lblLogoFile);
		ResourceManager.addComponent(logoFileLayout, txtLogoFile);

		ResourceManager.addComponent(licenseNoLayout, lblLicenseNo);
		ResourceManager.addComponent(licenseNoLayout, txtLicenseNo);

		ResourceManager.addComponent(licenseExpireLayout, lblLicenseExpire);
		ResourceManager.addComponent(licenseExpireLayout, txtLicenseExpire);

		codeLayout.setComponentAlignment(lblCode, Alignment.MIDDLE_CENTER);

		nameLayout.setComponentAlignment(lblName, Alignment.MIDDLE_CENTER);

		nameSpecLayout.setComponentAlignment(lblSpecName,
				Alignment.MIDDLE_CENTER);

		name2Layout.setComponentAlignment(lblName2, Alignment.MIDDLE_CENTER);

		name2SpecLayout.setComponentAlignment(lblSpecName2,
				Alignment.MIDDLE_CENTER);

		licenseNoLayout.setComponentAlignment(lblLicenseNo,
				Alignment.MIDDLE_CENTER);

		licenseExpireLayout.setComponentAlignment(lblLicenseExpire,
				Alignment.MIDDLE_CENTER);

		logoFileLayout.setComponentAlignment(lblLogoFile,
				Alignment.MIDDLE_CENTER);

		codeLayout.setExpandRatio(lblCode, 0.1f);
		codeLayout.setExpandRatio(txtCode, 1.4f);
		codeLayout.setExpandRatio(emptyLabel, 2.5f);

		nameLayout.setExpandRatio(lblName, 0.1f);
		nameLayout.setExpandRatio(txtName, 3.9f);

		nameSpecLayout.setExpandRatio(lblSpecName, 0.1f);
		nameSpecLayout.setExpandRatio(txtNameSpec, 3.9f);

		name2Layout.setExpandRatio(lblName2, 0.1f);
		name2Layout.setExpandRatio(txtName2, 3.9f);

		name2SpecLayout.setExpandRatio(lblSpecName2, 0.1f);
		name2SpecLayout.setExpandRatio(txtName2Spec, 3.9f);

		licenseLayout.setExpandRatio(licenseNoLayout, 2f);
		licenseLayout.setExpandRatio(licenseExpireLayout, 2f);

		licenseNoLayout.setExpandRatio(lblLicenseNo, 0.1f);
		licenseNoLayout.setExpandRatio(txtLicenseNo, 3.9f);

		licenseExpireLayout.setExpandRatio(lblLicenseExpire, 0.1f);
		licenseExpireLayout.setExpandRatio(txtLicenseExpire, 3.9f);

		logoFileLayout.setExpandRatio(lblLogoFile, 0.1f);
		logoFileLayout.setExpandRatio(txtLogoFile, 3.9f);
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
			}, con, "select code, name from company order by code", true);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void showInitView() {
		QRYSES = "";
		initForm();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}
}
