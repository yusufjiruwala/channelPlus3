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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmLocationTypes implements transactionalForm {
	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSESLocationNo = "";
	public String QRYSESLocationCode = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();

	VerticalLayout mainLayout = new VerticalLayout();
	VerticalLayout basicLayout = new VerticalLayout();
	VerticalLayout salesTypeLayout = new VerticalLayout();
	VerticalLayout tableLayout = new VerticalLayout();

	TableLayoutVaadin tableSaleType = new TableLayoutVaadin(tableLayout);

	HorizontalLayout lockLayout = new HorizontalLayout();
	HorizontalLayout contentLayout = new HorizontalLayout();
	HorizontalLayout locationLayout = new HorizontalLayout();
	HorizontalLayout typeLayout = new HorizontalLayout();
	HorizontalLayout descrLayout = new HorizontalLayout();
	HorizontalLayout descrLatinLayout = new HorizontalLayout();
	HorizontalLayout fixDebitLayout = new HorizontalLayout();
	HorizontalLayout saleLayout = new HorizontalLayout();
	HorizontalLayout saleDiscLayout = new HorizontalLayout();
	HorizontalLayout purLayout = new HorizontalLayout();
	HorizontalLayout purDiscLayout = new HorizontalLayout();
	HorizontalLayout logicalLayout = new HorizontalLayout();
	HorizontalLayout freeSaleLayout = new HorizontalLayout();
	HorizontalLayout posPurLayout = new HorizontalLayout();
	VerticalLayout checkLayout = new VerticalLayout();
	HorizontalLayout buttonLayout = new HorizontalLayout();
	HorizontalLayout content2Layout = new HorizontalLayout();

	private Label lblLocationCode = new Label("Location : ");
	private Label lblTypeNo = new Label("Type No : ");
	private Label lblDescr = new Label("Descr : ");
	private Label lblDescrLatin = new Label("Descr Latin : ");
	private Label lblFixDebit = new Label("Fix Debit Acc : ");
	private Label lblSale = new Label("Sale Acc : ");
	private Label lblSaleDisc = new Label("Saled Disc Acc : ");
	private Label lblPur = new Label("Pur Acc : ");
	private Label lblPurDisc = new Label("Pur Disc Acc : ");
	private Label lblLogical = new Label("Logical Seq. : ");
	private Label emptyLabel = new Label("");

	private Panel basicPanel = new Panel();

	private CheckBox chkLock = ControlsFactory.CreateCheckField("Lock", "flag",
			"1", "2", lstfldinfo);
	private CheckBox chkFree = ControlsFactory.CreateCheckField("Is Free ",
			"IS_FREE", "Y", "N", lstfldinfo);
	private CheckBox chkSales = ControlsFactory.CreateCheckField("Is Sales",
			"IN_SALES", "Y", "N", lstfldinfo);
	private CheckBox chkPos = ControlsFactory.CreateCheckField("In Pos",
			"IN_POS", "Y", "N", lstfldinfo);
	private CheckBox chkPurchase = ControlsFactory.CreateCheckField(
			"In Purchase", "IN_PURCHASE", "Y", "N", lstfldinfo);

	private ComboBox txtLocation = ControlsFactory.CreateListField("",
			"LOCATION_CODE", "select code,name from locations order by code",
			lstfldinfo);
	private TextField txtTypeNo = ControlsFactory.CreateTextField("", "NO",
			lstfldinfo);
	private TextField txtDescr = ControlsFactory.CreateTextField("", "DESCR",
			lstfldinfo);
	private TextField txtDescrL = ControlsFactory.CreateTextField("", "DESCRA",
			lstfldinfo);
	private TextField txtFixDebit = ControlsFactory.CreateTextField("",
			"ACCNO", lstfldinfo);
	private TextField txtFixDebitName = ControlsFactory.CreateTextField("", "",
			null);
	private TextField txtSale = ControlsFactory.CreateTextField("", "SALE_ACC",
			lstfldinfo);
	private TextField txtSaleName = ControlsFactory.CreateTextField("", "",
			null);
	private TextField txtSaleDisc = ControlsFactory.CreateTextField("",
			"SALE_DISC_ACC", lstfldinfo);
	private TextField txtSaleDiscName = ControlsFactory.CreateTextField("", "",
			null);
	private TextField txtPur = ControlsFactory.CreateTextField("", "PUR_ACC",
			lstfldinfo);
	private TextField txtPurName = ControlsFactory
			.CreateTextField("", "", null);
	private TextField txtPurDisc = ControlsFactory.CreateTextField("",
			"PUR_DISC_ACC", lstfldinfo);
	private TextField txtPurDiscName = ControlsFactory.CreateTextField("", "",
			null);
	private TextField txtLogical = ControlsFactory.CreateTextField("",
			"DEFAULT_SEQ", lstfldinfo);

	private NativeButton cmdFixDebit = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdSale = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdSaleDisc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdPur = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdPurDisc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");

	private NativeButton cmdClearFixDebit = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearSale = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdClearSaleDisc = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearPur = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdClearPurDisc = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");

	
	private final List<ColumnProperty> lstSalesTypeCols = new ArrayList<ColumnProperty>();

	public void save_data() {
		save_data(true);
	}

	public void applyColumnsOnSaleType() {
		lstSalesTypeCols.clear();
		ColumnProperty cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "SALE_TYPE_NO";
		cp.descr = "Type No";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = utils.FORMAT_QTY;
		cp.other_styles = "";
		tableSaleType.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "DESCR";
		cp.descr = "Descr";
		cp.display_width = 150;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableSaleType.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "DESCR_LATIN";
		cp.descr = "Descr_Latin";
		cp.display_width = 75;
		cp.display_type = "NONE";
		cp.pos = 3;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableSaleType.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "TYPE_OF_ITEMS";
		cp.descr = "Type of Items";
		cp.display_width = 75;
		cp.display_type = "NONE";
		cp.pos = 4;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableSaleType.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "ITEM_GROUP";
		cp.descr = "Item Group";
		cp.display_width = 75;
		cp.display_type = "NONE";
		cp.pos = 5;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableSaleType.addColumn(cp, false);
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
			if (QRYSESLocationCode.isEmpty() || QRYSESLocationNo.isEmpty()) {
				qe.AutoGenerateInsertStatment("invoicetype");
			} else {
				qe.AutoGenerateUpdateStatment("invoicetype",
						"'LOCATION_CODE' 'NO'",
						" WHERE LOCATION_CODE=:LOCATION_CODE AND NO=:NO");
			}
			qe.execute();
			con.commit();
			if (cls) {
				QRYSESLocationCode = "";
				QRYSESLocationNo = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSESLocationCode = txtLocation.getValue().toString();
				QRYSESLocationNo = txtTypeNo.getValue().toString();
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
			txtFixDebit.setReadOnly(true);
			txtFixDebitName.setReadOnly(true);
			txtSale.setReadOnly(true);
			txtSaleName.setReadOnly(true);
			txtSaleDisc.setReadOnly(true);
			txtSaleDiscName.setReadOnly(true);
			txtPur.setReadOnly(true);
			txtPurName.setReadOnly(true);
			txtPurDisc.setReadOnly(true);
			txtPurDiscName.setReadOnly(true);

			utilsVaadin.resetValues(basicLayout, false, false);
			utilsVaadin.setDefaultValues(lstfldinfo, false);
			txtTypeNo.setReadOnly(false);
			txtLocation.setReadOnly(false);
			if (!QRYSESLocationCode.isEmpty() && !QRYSESLocationNo.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"select * from invoicetype where location_code= '"
								+ QRYSESLocationCode + "' and No= '"
								+ QRYSESLocationNo + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				fetchLocation();
				pstmt.close();
				txtLocation.setReadOnly(true);
				txtTypeNo.setReadOnly(true);
			}
			tableSaleType.data.executeQuery(
					"select * from sale_type where location_code ='"
							+ QRYSESLocationCode + "' and invoice_type = '"
							+ QRYSESLocationNo + "' order by sale_type_no ",
					true);
			tableSaleType.fill_table();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void fetchLocation() {

		utilsVaadin.setValueByForce(txtFixDebitName, utils.getSqlValue(
				"Select Name from c_ycust where code = '"
						+ txtFixDebit.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtSaleName, utils.getSqlValue(
				"Select Name from c_ycust where code = '"
						+ txtSale.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtSaleDiscName, utils.getSqlValue(
				"Select Name from c_ycust where code = '"
						+ txtSaleDisc.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtPurName, utils.getSqlValue(
				"Select Name from c_ycust where code = '"
						+ txtPur.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtPurDiscName, utils.getSqlValue(
				"Select Name from c_ycust where code = '"
						+ txtPurDisc.getValue().toString() + "'", con));

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
		applyColumnsOnSaleType();
		load_data();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		mainLayout.setWidth("600px");
		tbsheet.setWidth("100%");
		basicPanel.setWidth("100%");
		basicLayout.setWidth("100%");
		locationLayout.setWidth("100%");
		typeLayout.setWidth("100%");
		lockLayout.setWidth("100%");
		contentLayout.setWidth("100%");
		descrLayout.setWidth("100%");
		descrLatinLayout.setWidth("100%");
		fixDebitLayout.setWidth("100%");
		saleLayout.setWidth("100%");
		saleDiscLayout.setWidth("100%");
		typeLayout.setWidth("100%");
		purLayout.setWidth("100%");
		purDiscLayout.setWidth("100%");
		logicalLayout.setWidth("100%");
		freeSaleLayout.setWidth("100%");
		posPurLayout.setWidth("100%");
		checkLayout.setWidth("100%");
		content2Layout.setWidth("100%");

		contentLayout.setSpacing(true);
		emptyLabel.setWidth("23px");

		basicPanel.setScrollable(true);

		lblLocationCode.setWidth("103px");
		lblTypeNo.setWidth("100px");
		lblDescr.setWidth("95px");
		lblDescrLatin.setWidth("95px");
		lblFixDebit.setWidth("100px");
		lblSale.setWidth("100px");
		lblSaleDisc.setWidth("100px");
		lblPur.setWidth("100px");
		lblPurDisc.setWidth("100px");
		lblLogical.setWidth("103px");

		txtLocation.setWidth("100%");
		txtTypeNo.setWidth("100%");
		txtDescr.setWidth("100%");
		txtDescrL.setWidth("100%");
		txtFixDebitName.setWidth("100%");
		txtSaleName.setWidth("100%");
		txtSaleDiscName.setWidth("100%");
		txtPurName.setWidth("100%");
		txtPurDiscName.setWidth("100%");
		txtLogical.setWidth("100%");

		tbsheet.addTab(basicLayout, "Basic Information", null);
		tbsheet.addTab(salesTypeLayout, "Sales Type", null);

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(salesTypeLayout, tableLayout);

		ResourceManager.addComponent(tableLayout, tableSaleType);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicPanel);

		ResourceManager.addComponent(basicPanel, tbsheet);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdCls);

		ResourceManager.addComponent(basicLayout, lockLayout);
		ResourceManager.addComponent(basicLayout, contentLayout);
		ResourceManager.addComponent(basicLayout, descrLayout);
		ResourceManager.addComponent(basicLayout, descrLatinLayout);
		ResourceManager.addComponent(basicLayout, fixDebitLayout);
		ResourceManager.addComponent(basicLayout, saleLayout);
		ResourceManager.addComponent(basicLayout, saleDiscLayout);
		ResourceManager.addComponent(basicLayout, purLayout);
		ResourceManager.addComponent(basicLayout, purDiscLayout);
		ResourceManager.addComponent(basicLayout, content2Layout);

		ResourceManager.addComponent(lockLayout, chkLock);

		ResourceManager.addComponent(contentLayout, locationLayout);
		ResourceManager.addComponent(contentLayout, typeLayout);

		ResourceManager.addComponent(locationLayout, lblLocationCode);
		ResourceManager.addComponent(locationLayout, txtLocation);

		ResourceManager.addComponent(typeLayout, lblTypeNo);
		ResourceManager.addComponent(typeLayout, txtTypeNo);

		ResourceManager.addComponent(descrLayout, lblDescr);
		ResourceManager.addComponent(descrLayout, txtDescr);

		ResourceManager.addComponent(descrLatinLayout, lblDescrLatin);
		ResourceManager.addComponent(descrLatinLayout, txtDescrL);

		ResourceManager.addComponent(fixDebitLayout, lblFixDebit);
		ResourceManager.addComponent(fixDebitLayout, txtFixDebit);
		ResourceManager.addComponent(fixDebitLayout, cmdClearFixDebit);
		ResourceManager.addComponent(fixDebitLayout, cmdFixDebit);
		ResourceManager.addComponent(fixDebitLayout, txtFixDebitName);

		ResourceManager.addComponent(saleLayout, lblSale);
		ResourceManager.addComponent(saleLayout, txtSale);
		ResourceManager.addComponent(saleLayout, cmdClearSale);
		ResourceManager.addComponent(saleLayout, cmdSale);
		ResourceManager.addComponent(saleLayout, txtSaleName);

		ResourceManager.addComponent(saleDiscLayout, lblSaleDisc);
		ResourceManager.addComponent(saleDiscLayout, txtSaleDisc);
		ResourceManager.addComponent(saleDiscLayout, cmdClearSaleDisc);
		ResourceManager.addComponent(saleDiscLayout, cmdSaleDisc);
		ResourceManager.addComponent(saleDiscLayout, txtSaleDiscName);

		ResourceManager.addComponent(purLayout, lblPur);
		ResourceManager.addComponent(purLayout, txtPur);
		ResourceManager.addComponent(purLayout, cmdClearPur);
		ResourceManager.addComponent(purLayout, cmdPur);
		ResourceManager.addComponent(purLayout, txtPurName);

		ResourceManager.addComponent(purDiscLayout, lblPurDisc);
		ResourceManager.addComponent(purDiscLayout, txtPurDisc);
		ResourceManager.addComponent(purDiscLayout, cmdClearPurDisc);
		ResourceManager.addComponent(purDiscLayout, cmdPurDisc);
		ResourceManager.addComponent(purDiscLayout, txtPurDiscName);

		ResourceManager.addComponent(content2Layout, logicalLayout);
		ResourceManager.addComponent(content2Layout, checkLayout);

		ResourceManager.addComponent(logicalLayout, lblLogical);
		ResourceManager.addComponent(logicalLayout, txtLogical);

		ResourceManager.addComponent(checkLayout, freeSaleLayout);
		ResourceManager.addComponent(checkLayout, posPurLayout);

		ResourceManager.addComponent(freeSaleLayout, chkFree);
		ResourceManager.addComponent(freeSaleLayout, chkSales);

		ResourceManager.addComponent(posPurLayout, chkPos);
		ResourceManager.addComponent(posPurLayout, emptyLabel);
		ResourceManager.addComponent(posPurLayout, chkPurchase);

		lockLayout.setComponentAlignment(chkLock, Alignment.BOTTOM_RIGHT);

		locationLayout.setComponentAlignment(lblLocationCode,
				Alignment.BOTTOM_CENTER);

		typeLayout.setComponentAlignment(lblTypeNo, Alignment.BOTTOM_CENTER);

		descrLayout.setComponentAlignment(lblDescr, Alignment.BOTTOM_CENTER);

		descrLatinLayout.setComponentAlignment(lblDescrLatin,
				Alignment.BOTTOM_CENTER);

		fixDebitLayout.setComponentAlignment(lblFixDebit,
				Alignment.BOTTOM_CENTER);
		fixDebitLayout.setComponentAlignment(cmdClearFixDebit,
				Alignment.BOTTOM_CENTER);
		fixDebitLayout.setComponentAlignment(cmdFixDebit,
				Alignment.BOTTOM_CENTER);

		saleLayout.setComponentAlignment(lblSale, Alignment.BOTTOM_CENTER);
		saleLayout.setComponentAlignment(cmdClearSale, Alignment.BOTTOM_CENTER);
		saleLayout.setComponentAlignment(cmdSale, Alignment.BOTTOM_CENTER);

		saleDiscLayout.setComponentAlignment(lblSaleDisc,
				Alignment.BOTTOM_CENTER);
		saleDiscLayout.setComponentAlignment(cmdClearSaleDisc,
				Alignment.BOTTOM_CENTER);
		saleDiscLayout.setComponentAlignment(cmdSaleDisc,
				Alignment.BOTTOM_CENTER);

		purLayout.setComponentAlignment(lblPur, Alignment.BOTTOM_CENTER);
		purLayout.setComponentAlignment(cmdClearPur, Alignment.BOTTOM_CENTER);
		purLayout.setComponentAlignment(cmdPur, Alignment.BOTTOM_CENTER);

		purDiscLayout
				.setComponentAlignment(lblPurDisc, Alignment.BOTTOM_CENTER);
		purDiscLayout.setComponentAlignment(cmdClearPurDisc,
				Alignment.BOTTOM_CENTER);
		purDiscLayout
				.setComponentAlignment(cmdPurDisc, Alignment.BOTTOM_CENTER);

		logicalLayout
				.setComponentAlignment(lblLogical, Alignment.BOTTOM_CENTER);

		contentLayout.setExpandRatio(locationLayout, 1.6f);
		contentLayout.setExpandRatio(typeLayout, 2.4f);

		content2Layout.setExpandRatio(logicalLayout, 1.7f);
		content2Layout.setExpandRatio(checkLayout, 2.3f);

		locationLayout.setExpandRatio(lblLocationCode, 0.2f);
		locationLayout.setExpandRatio(txtLocation, 3.8f);

		typeLayout.setExpandRatio(lblTypeNo, 0.1f);
		typeLayout.setExpandRatio(txtTypeNo, 3.9f);

		descrLayout.setExpandRatio(lblDescr, 0.1f);
		descrLayout.setExpandRatio(txtDescr, 3.9f);

		descrLatinLayout.setExpandRatio(lblDescrLatin, 0.1f);
		descrLatinLayout.setExpandRatio(txtDescrL, 3.9f);

		fixDebitLayout.setExpandRatio(lblFixDebit, 0.1f);
		fixDebitLayout.setExpandRatio(txtFixDebit, 0.1f);
		fixDebitLayout.setExpandRatio(cmdClearFixDebit, 0.1f);
		fixDebitLayout.setExpandRatio(cmdFixDebit, 0.1f);
		fixDebitLayout.setExpandRatio(txtFixDebitName, 3.6f);

		saleLayout.setExpandRatio(lblSale, 0.1f);
		saleLayout.setExpandRatio(txtSale, 0.1f);
		saleLayout.setExpandRatio(cmdClearSale, 0.1f);
		saleLayout.setExpandRatio(cmdSale, 0.1f);
		saleLayout.setExpandRatio(txtSaleName, 3.6f);

		saleDiscLayout.setExpandRatio(lblSaleDisc, 0.1f);
		saleDiscLayout.setExpandRatio(txtSaleDisc, 0.1f);
		saleDiscLayout.setExpandRatio(cmdClearSaleDisc, 0.1f);
		saleDiscLayout.setExpandRatio(cmdSaleDisc, 0.1f);
		saleDiscLayout.setExpandRatio(txtSaleDiscName, 3.6f);

		purLayout.setExpandRatio(lblPur, 0.1f);
		purLayout.setExpandRatio(txtPur, 0.1f);
		purLayout.setExpandRatio(cmdClearPur, 0.1f);
		purLayout.setExpandRatio(cmdPur, 0.1f);
		purLayout.setExpandRatio(txtPurName, 3.6f);

		purDiscLayout.setExpandRatio(lblPurDisc, 0.1f);
		purDiscLayout.setExpandRatio(txtPurDisc, 0.1f);
		purDiscLayout.setExpandRatio(cmdClearPurDisc, 0.1f);
		purDiscLayout.setExpandRatio(cmdPurDisc, 0.1f);
		purDiscLayout.setExpandRatio(txtPurDiscName, 3.6f);

		logicalLayout.setExpandRatio(lblLogical, 0.1f);
		logicalLayout.setExpandRatio(txtLogical, 3.9f);

		checkLayout.setExpandRatio(freeSaleLayout, 2f);
		checkLayout.setExpandRatio(posPurLayout, 2f);

		freeSaleLayout.setExpandRatio(chkFree, 2f);
		freeSaleLayout.setExpandRatio(chkSales, 2f);

		posPurLayout.setExpandRatio(chkPos, 2f);
		posPurLayout.setExpandRatio(chkPurchase, 2f);

		applyColumnsOnSaleType();

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
					QRYSESLocationCode = "";
					QRYSESLocationNo = "";
					load_data();
				}
			});
			cmdFixDebit.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listAcc(txtFixDebit, txtFixDebitName);
				}
			});
			cmdSale.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listAcc(txtSale, txtSaleName);
				}
			});
			cmdSaleDisc.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_listAcc(txtSaleDisc, txtSaleDiscName);
				}
			});
			cmdPur.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listAcc(txtPur, txtPurName);
				}
			});
			cmdPurDisc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listAcc(txtPurDisc, txtPurDiscName);
				}
			});
			cmdClearFixDebit.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtFixDebit.setReadOnly(false);
					txtFixDebitName.setReadOnly(false);
					txtFixDebit.setValue("");
					txtFixDebitName.setValue("");
					txtFixDebit.setReadOnly(true);
					txtFixDebitName.setReadOnly(true);

				}
			});
			cmdClearSale.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtSale.setReadOnly(false);
					txtSaleName.setReadOnly(false);
					txtSale.setValue("");
					txtSaleName.setValue("");
					txtSale.setReadOnly(true);
					txtSaleName.setReadOnly(true);

				}
			});
			cmdClearSaleDisc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtSaleDisc.setReadOnly(false);
					txtSaleDiscName.setReadOnly(false);
					txtSaleDisc.setValue("");
					txtSaleDiscName.setValue("");
					txtSaleDisc.setReadOnly(true);
					txtSaleDiscName.setReadOnly(true);

				}
			});
			cmdClearPur.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtPur.setReadOnly(false);
					txtPurName.setReadOnly(false);
					txtPur.setValue("");
					txtPurName.setValue("");
					txtPur.setReadOnly(true);
					txtPurName.setReadOnly(true);

				}
			});
			cmdClearPurDisc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtPurDisc.setReadOnly(false);
					txtPurDiscName.setReadOnly(false);
					txtPurDisc.setValue("");
					txtPurDiscName.setValue("");
					txtPurDisc.setReadOnly(true);
					txtPurDiscName.setReadOnly(true);
				}
			});
			listnerAdded = true;
		}

	}

	protected void show_listAcc(final TextField tx, final TextField txn) {
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
							utilsVaadin.setValueByForce(txn, tv.getData()
									.getFieldValue(rn, "NAME").toString());
							utilsVaadin.setValueByForce(tx, tv.getData()
									.getFieldValue(rn, "CODE").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select code , name from c_ycust where usecount=0", true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	public void showInitView() {
		QRYSESLocationCode = "";
		QRYSESLocationNo = "";
		initForm();
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		salesTypeLayout.removeAllComponents();
		lockLayout.removeAllComponents();
		contentLayout.removeAllComponents();
		locationLayout.removeAllComponents();
		typeLayout.removeAllComponents();
		descrLatinLayout.removeAllComponents();
		descrLayout.removeAllComponents();
		fixDebitLayout.removeAllComponents();
		saleLayout.removeAllComponents();
		saleDiscLayout.removeAllComponents();
		purLayout.removeAllComponents();
		purDiscLayout.removeAllComponents();
		logicalLayout.removeAllComponents();
		freeSaleLayout.removeAllComponents();
		posPurLayout.removeAllComponents();
		checkLayout.removeAllComponents();
		buttonLayout.removeAllComponents();
		content2Layout.removeAllComponents();
		tableLayout.removeAllComponents();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractOrderedLayout) parentLayout;
	}

	protected void show_list() {
		try {
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);

			utilsVaadin.showSearch(
					la,
					new SelectionListener() {
						public void onSelection(TableView tv) {
							Channelplus3Application.getInstance()
									.getMainWindow().removeWindow(wnd);

							if (tv.getSelectionValue() > -1) {
								try {
									int rn = tv.getSelectionValue();
									QRYSESLocationCode = tv.getData()
											.getFieldValue(rn, "LOCATION_CODE")
											.toString();
									QRYSESLocationNo = tv.getData()
											.getFieldValue(rn, "No").toString();
									load_data();
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}
					},
					con,
					"select location_code,no,descr,descra from invoicetype order by location_code,no",
					true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
