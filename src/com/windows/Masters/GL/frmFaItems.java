package com.windows.Masters.GL;

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

public class frmFaItems implements transactionalForm {
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

	private HorizontalLayout contentLayout = new HorizontalLayout();
	private HorizontalLayout codeLayout = new HorizontalLayout();
	private HorizontalLayout groupLayout = new HorizontalLayout();
	private HorizontalLayout descrLayout = new HorizontalLayout();
	private HorizontalLayout AccLayout = new HorizontalLayout();
	private HorizontalLayout depAccLayout = new HorizontalLayout();
	private HorizontalLayout depExpAccLayout = new HorizontalLayout();
	private HorizontalLayout costCenterLayout = new HorizontalLayout();
	private HorizontalLayout purLayout = new HorizontalLayout();
	private HorizontalLayout purDateLayout = new HorizontalLayout();
	private HorizontalLayout purPriceLayout = new HorizontalLayout();
	private HorizontalLayout depLayout = new HorizontalLayout();
	private HorizontalLayout totalLayout = new HorizontalLayout();
	private HorizontalLayout lastNetLayout = new HorizontalLayout();
	private HorizontalLayout depRateLayout = new HorizontalLayout();
	private HorizontalLayout priorDepLayout = new HorizontalLayout();
	private HorizontalLayout totExpLayout = new HorizontalLayout();
	private HorizontalLayout totValueLayout = new HorizontalLayout();
	private HorizontalLayout lastDepDateLayout = new HorizontalLayout();
	private HorizontalLayout netBookValueLayout = new HorizontalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();

	private Label lblCode = new Label("Code");
	private Label lblGroup = new Label("Group");
	private Label lblDescr = new Label("Descr");
	private Label lblAccNo = new Label("FACostAcc No");
	private Label lblDepAcc = new Label("DepAcc No");
	private Label lblDepExpAcc = new Label("DepExpAcc No");
	private Label lblCostCenter = new Label("Cost Center");
	private Label lblPurDate = new Label("Pur Date");
	private Label lblPurPrice = new Label("Pur Price");
	private Label lblDepRate = new Label("Dep Rate");
	private Label lblPriorDep = new Label("Prior Dep");
	private Label lblTotExp = new Label("Total Exp");
	private Label lblTotValue = new Label("Total Val");
	private Label lblLastDepDate = new Label("LastDep Date");
	private Label lblNetBookValue = new Label("NetBook Val");

	private TextField txtCode = ControlsFactory.CreateTextField("", "CODE",
			lstfldinfo);
	private TextField txtGroup = ControlsFactory.CreateTextField("", "CATNO",
			lstfldinfo);
	private TextField txtGroupName = ControlsFactory.CreateTextField("",
			"CATNAME", null);
	private TextField txtDescr = ControlsFactory.CreateTextField("", "DESCR",
			lstfldinfo);
	private TextField txtAccNo = ControlsFactory.CreateTextField("", "ACCNO",
			lstfldinfo);
	private TextField txtAccName = ControlsFactory.CreateTextField("", "NAME",
			null);
	private TextField txtDepAccNo = ControlsFactory.CreateTextField("",
			"DEPACCNO", lstfldinfo);
	private TextField txtDepAccName = ControlsFactory.CreateTextField("",
			"DEPACNAME", null);
	private TextField txtExpAccNo = ControlsFactory.CreateTextField("",
			"EXPACCNO", lstfldinfo);
	private TextField txtExpAccName = ControlsFactory.CreateTextField("",
			"EXPACNAME", null);
	private TextField txtCostCent = ControlsFactory.CreateTextField("",
			"COSTCENT", lstfldinfo);
	private TextField txtCostCentName = ControlsFactory.CreateTextField("",
			"COSTNAME", null);
	private DateField txtPurDate = ControlsFactory.CreateDateField("",
			"PURDATE", lstfldinfo);
	private TextField txtPurPrice = ControlsFactory.CreateTextField("",
			"PURPRICE", lstfldinfo);
	private TextField txtDepRate = ControlsFactory.CreateTextField("",
			"DEPRATE", lstfldinfo);
	private TextField txtPriorDep = ControlsFactory.CreateTextField("",
			"PRIORDEP", lstfldinfo);
	private TextField txtTotExp = ControlsFactory.CreateTextField("",
			"PRIORDEPAMT", lstfldinfo);
	private TextField txtTotVal = ControlsFactory.CreateTextField("",
			"TOTALVALUE", null);
	private DateField txtLastDepDate = ControlsFactory.CreateDateField("",
			"LASTDEPDATE", lstfldinfo);
	private TextField txtNetBookVal = ControlsFactory.CreateTextField("",
			"NETBOOKVALUE", lstfldinfo);

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");

	private NativeButton cmdGroup = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearGroup = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearAcc = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdDepAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearDepAcc = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdExpAcc = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearExpAcc = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdCostCent = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearCostCent = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");

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
			qe.setParaValue("DEPTYPE", "0");
			qe.setParaValue("CURRENTVALUE", "0");
			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("faitems");
			} else {
				qe.AutoGenerateUpdateStatment("faitems", "'CODE'",
						" WHERE CODE=:code");
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
			txtDepRate.setValue("1.000");
			txtPriorDep.setValue("1.000");
			txtNetBookVal.setValue("1.000");
			txtTotExp.setValue("1.000");
			txtPurPrice.setValue("1.000");
			txtTotVal.setValue("0.000");
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"Select * from FAITEMS where CODE='" + QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				fetchLocation();
				pstmt.close();
				txtCode.setReadOnly(true);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void fetchLocation() {
		txtGroup.setReadOnly(false);
		txtGroupName.setReadOnly(false);
		txtDepAccName.setReadOnly(false);
		txtDepAccNo.setReadOnly(false);
		txtExpAccName.setReadOnly(false);
		txtExpAccNo.setReadOnly(false);

		txtGroupName.setValue(utils.getSqlValue(
				"Select CATNAME from FACAT where CATNO = '"
						+ txtGroup.getValue().toString() + "'", con));

		txtDepAccName.setValue(utils.getSqlValue(
				"Select Name from Acaccount where AccNo = '"
						+ txtDepAccNo.getValue().toString() + "'", con));

		txtAccName.setValue(utils.getSqlValue(
				"Select Name from Acaccount where AccNo = '"
						+ txtAccNo.getValue().toString() + "'", con));

		txtExpAccName.setValue(utils.getSqlValue(
				"Select Name from Acaccount where AccNo = '"
						+ txtExpAccNo.getValue().toString() + "'", con));

		txtCostCentName.setValue(utils.getSqlValue(
				"select title from accostcent1 where code = '"
						+ txtCostCent.getValue().toString() + "'", con));
		txtGroup.setReadOnly(true);
		txtGroupName.setReadOnly(true);
		txtDepAccName.setReadOnly(true);
		txtDepAccNo.setReadOnly(true);
		txtExpAccName.setReadOnly(true);
		txtExpAccNo.setReadOnly(true);

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

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		basicPanel.setWidth("650px");
		mainLayout.setWidth("100%");
		basicLayout.setWidth("100%");
		contentLayout.setWidth("100%");
		codeLayout.setWidth("100%");
		groupLayout.setWidth("100%");
		descrLayout.setWidth("100%");
		depAccLayout.setWidth("100%");
		depExpAccLayout.setWidth("100%");
		costCenterLayout.setWidth("100%");
		purLayout.setWidth("100%");
		purDateLayout.setWidth("100%");
		purPriceLayout.setWidth("100%");
		depLayout.setWidth("100%");
		totalLayout.setWidth("100%");
		lastNetLayout.setWidth("100%");
		depRateLayout.setWidth("100%");
		priorDepLayout.setWidth("100%");
		totExpLayout.setWidth("100%");
		lastDepDateLayout.setWidth("100%");
		netBookValueLayout.setWidth("100%");
		totValueLayout.setWidth("100%");
		AccLayout.setWidth("100%");
		contentLayout.setSpacing(true);
		codeLayout.setSpacing(true);

		lblCode.setWidth("101px");
		lblGroup.setWidth("90px");
		lblDescr.setWidth("90px");
		lblAccNo.setWidth("90px");
		lblDepAcc.setWidth("90px");
		lblDepExpAcc.setWidth("90px");
		lblCostCenter.setWidth("90px");
		lblPurDate.setWidth("90px");
		lblPurPrice.setWidth("90px");
		lblDepRate.setWidth("90px");
		lblPriorDep.setWidth("90px");
		lblTotExp.setWidth("90px");
		lblTotValue.setWidth("90px");
		lblLastDepDate.setWidth("90px");
		lblNetBookValue.setWidth("90px");

		txtCode.setWidth("100%");
		txtGroup.setWidth("100%");
		txtGroupName.setWidth("100%");
		txtDescr.setWidth("100%");
		txtAccNo.setWidth("100%");
		txtAccName.setWidth("100%");
		txtDepAccNo.setWidth("100%");
		txtDepAccName.setWidth("100%");
		txtExpAccNo.setWidth("100%");
		txtExpAccName.setWidth("100%");
		txtCostCent.setWidth("100%");
		txtCostCentName.setWidth("100%");
		txtPurDate.setWidth("100%");
		txtPurPrice.setWidth("100%");
		txtDepRate.setWidth("100%");
		txtPriorDep.setWidth("100%");
		txtTotExp.setWidth("100%");
		txtTotVal.setWidth("100%");
		txtLastDepDate.setWidth("100%");
		txtNetBookVal.setWidth("100%");

		basicLayout.addStyleName("formLayout");

		txtPriorDep.addStyleName("netAmtStyle");
		txtDepRate.addStyleName("netAmtStyle");
		txtNetBookVal.addStyleName("netAmtStyle");
		txtTotExp.addStyleName("netAmtStyle");
		txtPurPrice.addStyleName("netAmtStyle");
		txtTotVal.addStyleName("netAmtStyle");

		utils.findFieldInfoByObject(txtPriorDep, lstfldinfo).format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		utils.findFieldInfoByObject(txtNetBookVal, lstfldinfo).format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		utils.findFieldInfoByObject(txtPurPrice, lstfldinfo).format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		utils.findFieldInfoByObject(txtTotExp, lstfldinfo).format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		utils.findFieldInfoByObject(txtPurDate, lstfldinfo).format = utils.FORMAT_SHORT_DATE;
		utils.findFieldInfoByObject(txtDepRate, lstfldinfo).format = utils.FORMAT_SHORT_DATE;

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicPanel);

		ResourceManager.addComponent(basicPanel, basicLayout);

		ResourceManager.addComponent(basicLayout, contentLayout);
		ResourceManager.addComponent(basicLayout, descrLayout);
		ResourceManager.addComponent(basicLayout, AccLayout);
		ResourceManager.addComponent(basicLayout, depAccLayout);
		ResourceManager.addComponent(basicLayout, depExpAccLayout);
		ResourceManager.addComponent(basicLayout, costCenterLayout);
		ResourceManager.addComponent(basicLayout, purLayout);
		ResourceManager.addComponent(basicLayout, depLayout);
		ResourceManager.addComponent(basicLayout, totalLayout);
		ResourceManager.addComponent(basicLayout, lastNetLayout);

		ResourceManager.addComponent(contentLayout, codeLayout);
		ResourceManager.addComponent(contentLayout, groupLayout);

		ResourceManager.addComponent(codeLayout, lblCode);
		ResourceManager.addComponent(codeLayout, txtCode);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdCls);

		ResourceManager.addComponent(groupLayout, lblGroup);
		ResourceManager.addComponent(groupLayout, txtGroup);
		ResourceManager.addComponent(groupLayout, cmdClearGroup);
		ResourceManager.addComponent(groupLayout, cmdGroup);
		ResourceManager.addComponent(groupLayout, txtGroupName);

		ResourceManager.addComponent(descrLayout, lblDescr);
		ResourceManager.addComponent(descrLayout, txtDescr);

		ResourceManager.addComponent(AccLayout, lblAccNo);
		ResourceManager.addComponent(AccLayout, txtAccNo);
		ResourceManager.addComponent(AccLayout, cmdClearAcc);
		ResourceManager.addComponent(AccLayout, cmdAcc);
		ResourceManager.addComponent(AccLayout, txtAccName);

		ResourceManager.addComponent(depAccLayout, lblDepAcc);
		ResourceManager.addComponent(depAccLayout, txtDepAccNo);
		ResourceManager.addComponent(depAccLayout, cmdClearDepAcc);
		ResourceManager.addComponent(depAccLayout, cmdDepAcc);
		ResourceManager.addComponent(depAccLayout, txtDepAccName);

		ResourceManager.addComponent(depExpAccLayout, lblDepExpAcc);
		ResourceManager.addComponent(depExpAccLayout, txtExpAccNo);
		ResourceManager.addComponent(depExpAccLayout, cmdClearExpAcc);
		ResourceManager.addComponent(depExpAccLayout, cmdExpAcc);
		ResourceManager.addComponent(depExpAccLayout, txtExpAccName);

		ResourceManager.addComponent(costCenterLayout, lblCostCenter);
		ResourceManager.addComponent(costCenterLayout, txtCostCent);
		ResourceManager.addComponent(costCenterLayout, cmdClearCostCent);
		ResourceManager.addComponent(costCenterLayout, cmdCostCent);
		ResourceManager.addComponent(costCenterLayout, txtCostCentName);

		ResourceManager.addComponent(purLayout, purDateLayout);
		ResourceManager.addComponent(purLayout, purPriceLayout);

		ResourceManager.addComponent(purDateLayout, lblPurDate);
		ResourceManager.addComponent(purDateLayout, txtPurDate);

		ResourceManager.addComponent(purPriceLayout, lblPurPrice);
		ResourceManager.addComponent(purPriceLayout, txtPurPrice);

		ResourceManager.addComponent(depLayout, depRateLayout);
		ResourceManager.addComponent(depLayout, priorDepLayout);

		ResourceManager.addComponent(depRateLayout, lblDepRate);
		ResourceManager.addComponent(depRateLayout, txtDepRate);

		ResourceManager.addComponent(priorDepLayout, lblPriorDep);
		ResourceManager.addComponent(priorDepLayout, txtPriorDep);

		ResourceManager.addComponent(totalLayout, totExpLayout);
		ResourceManager.addComponent(totalLayout, totValueLayout);

		ResourceManager.addComponent(totExpLayout, lblTotExp);
		ResourceManager.addComponent(totExpLayout, txtTotExp);

		ResourceManager.addComponent(totValueLayout, lblTotValue);
		ResourceManager.addComponent(totValueLayout, txtTotVal);

		ResourceManager.addComponent(lastNetLayout, lastDepDateLayout);
		ResourceManager.addComponent(lastNetLayout, netBookValueLayout);

		ResourceManager.addComponent(lastDepDateLayout, lblLastDepDate);
		ResourceManager.addComponent(lastDepDateLayout, txtLastDepDate);

		ResourceManager.addComponent(netBookValueLayout, lblNetBookValue);
		ResourceManager.addComponent(netBookValueLayout, txtNetBookVal);

		codeLayout.setComponentAlignment(lblCode, Alignment.BOTTOM_CENTER);

		groupLayout.setComponentAlignment(lblGroup, Alignment.BOTTOM_CENTER);
		groupLayout.setComponentAlignment(cmdClearGroup,
				Alignment.BOTTOM_CENTER);
		groupLayout.setComponentAlignment(cmdGroup, Alignment.BOTTOM_CENTER);

		descrLayout.setComponentAlignment(lblDescr, Alignment.BOTTOM_CENTER);

		AccLayout.setComponentAlignment(lblAccNo, Alignment.BOTTOM_CENTER);
		AccLayout.setComponentAlignment(cmdClearAcc, Alignment.BOTTOM_CENTER);
		AccLayout.setComponentAlignment(cmdAcc, Alignment.BOTTOM_CENTER);

		depAccLayout.setComponentAlignment(lblDepAcc, Alignment.BOTTOM_CENTER);
		depAccLayout.setComponentAlignment(cmdClearDepAcc,
				Alignment.BOTTOM_CENTER);
		depAccLayout.setComponentAlignment(cmdDepAcc, Alignment.BOTTOM_CENTER);

		depExpAccLayout.setComponentAlignment(lblDepExpAcc,
				Alignment.BOTTOM_CENTER);
		depExpAccLayout.setComponentAlignment(cmdClearExpAcc,
				Alignment.BOTTOM_CENTER);
		depExpAccLayout.setComponentAlignment(cmdExpAcc,
				Alignment.BOTTOM_CENTER);

		costCenterLayout.setComponentAlignment(lblCostCenter,
				Alignment.BOTTOM_CENTER);
		costCenterLayout.setComponentAlignment(cmdClearCostCent,
				Alignment.BOTTOM_CENTER);
		costCenterLayout.setComponentAlignment(cmdCostCent,
				Alignment.BOTTOM_CENTER);

		purDateLayout
				.setComponentAlignment(lblPurDate, Alignment.BOTTOM_CENTER);

		purPriceLayout.setComponentAlignment(lblPurPrice,
				Alignment.BOTTOM_CENTER);

		depRateLayout
				.setComponentAlignment(lblDepRate, Alignment.BOTTOM_CENTER);

		priorDepLayout.setComponentAlignment(lblPriorDep,
				Alignment.BOTTOM_CENTER);

		totExpLayout.setComponentAlignment(lblTotExp, Alignment.BOTTOM_CENTER);

		totValueLayout.setComponentAlignment(lblTotValue,
				Alignment.BOTTOM_CENTER);

		lastDepDateLayout.setComponentAlignment(lblLastDepDate,
				Alignment.BOTTOM_CENTER);

		netBookValueLayout.setComponentAlignment(lblNetBookValue,
				Alignment.BOTTOM_CENTER);

		contentLayout.setExpandRatio(codeLayout, 1.4f);
		contentLayout.setExpandRatio(groupLayout, 2.6f);

		codeLayout.setExpandRatio(lblCode, 0.1f);
		codeLayout.setExpandRatio(txtCode, 3.9f);

		groupLayout.setExpandRatio(lblGroup, 0.1f);
		groupLayout.setExpandRatio(txtGroup, 1f);
		groupLayout.setExpandRatio(cmdClearGroup, 0.1f);
		groupLayout.setExpandRatio(cmdGroup, 0.1F);
		groupLayout.setExpandRatio(txtGroupName, 2.7f);

		descrLayout.setExpandRatio(lblDescr, 0.1f);
		descrLayout.setExpandRatio(txtDescr, 3.9f);

		AccLayout.setExpandRatio(lblAccNo, 0.1f);
		AccLayout.setExpandRatio(txtAccNo, 1f);
		AccLayout.setExpandRatio(cmdClearAcc, 0.1f);
		AccLayout.setExpandRatio(cmdAcc, 0.1f);
		AccLayout.setExpandRatio(txtAccName, 2.7f);

		depAccLayout.setExpandRatio(lblDepAcc, 0.1f);
		depAccLayout.setExpandRatio(txtDepAccNo, 1f);
		depAccLayout.setExpandRatio(cmdClearDepAcc, 0.1f);
		depAccLayout.setExpandRatio(cmdDepAcc, 0.1f);
		depAccLayout.setExpandRatio(txtDepAccName, 2.7f);

		depExpAccLayout.setExpandRatio(lblDepExpAcc, 0.1f);
		depExpAccLayout.setExpandRatio(txtExpAccNo, 1f);
		depExpAccLayout.setExpandRatio(cmdClearExpAcc, 0.1f);
		depExpAccLayout.setExpandRatio(cmdExpAcc, 0.1f);
		depExpAccLayout.setExpandRatio(txtExpAccName, 2.7f);

		costCenterLayout.setExpandRatio(lblCostCenter, 0.1f);
		costCenterLayout.setExpandRatio(txtCostCent, 1f);
		costCenterLayout.setExpandRatio(cmdClearCostCent, 0.1f);
		costCenterLayout.setExpandRatio(cmdCostCent, 0.1f);
		costCenterLayout.setExpandRatio(txtCostCentName, 2.7f);

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
			cmdAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listAcc();
				}
			});
			cmdDepAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listDepAcc();
				}
			});
			cmdExpAcc.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_listExpAcc();
				}
			});
			cmdCostCent.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listCostCent();
				}
			});
			cmdGroup.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_listGroup();
				}
			});
			cmdClearAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtAccNo.setReadOnly(false);
					txtAccName.setReadOnly(false);
					txtAccNo.setValue("");
					txtAccName.setValue("");
					txtAccNo.setReadOnly(true);
					txtAccName.setReadOnly(true);
				}
			});
			cmdClearDepAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtDepAccNo.setReadOnly(false);
					txtDepAccName.setReadOnly(false);
					txtDepAccNo.setValue("");
					txtDepAccName.setValue("");
					txtDepAccNo.setReadOnly(true);
					txtDepAccName.setReadOnly(true);
				}
			});
			cmdClearExpAcc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtExpAccNo.setReadOnly(false);
					txtExpAccName.setReadOnly(false);
					txtExpAccNo.setValue("");
					txtExpAccName.setValue("");
					txtExpAccNo.setReadOnly(true);
					txtExpAccName.setReadOnly(true);
				}
			});
			cmdClearCostCent.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtCostCent.setReadOnly(false);
					txtCostCentName.setReadOnly(false);
					txtCostCent.setValue("");
					txtCostCentName.setValue("");
					txtCostCent.setReadOnly(true);
					txtCostCentName.setReadOnly(true);
				}
			});
			cmdClearGroup.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtGroup.setReadOnly(false);
					txtGroupName.setReadOnly(false);
					txtGroup.setValue("");
					txtGroupName.setValue("");
					txtGroup.setReadOnly(true);
					txtGroupName.setReadOnly(true);
				}
			});
			listnerAdded = true;
		}
	}

	protected void show_listAcc() {
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
											txtAccName.setReadOnly(false);
											txtAccNo.setReadOnly(false);
											int rn = tv.getSelectionValue();
											txtAccName.setValue(tv.getData()
													.getFieldValue(rn, "NAME")
													.toString());
											txtAccNo.setValue(tv.getData()
													.getFieldValue(rn, "accno")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										} finally {
											txtAccName.setReadOnly(true);
											txtAccNo.setReadOnly(true);
										}
									}
								}
							},
							con,
							"select accno , name from acaccount where usecount=0 and actype=0",
							true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	protected void show_listDepAcc() {
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
											txtDepAccName.setReadOnly(false);
											txtDepAccNo.setReadOnly(false);
											int rn = tv.getSelectionValue();
											txtDepAccName.setValue(tv.getData()
													.getFieldValue(rn, "NAME")
													.toString());
											txtDepAccNo.setValue(tv.getData()
													.getFieldValue(rn, "accno")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										} finally {
											txtDepAccName.setReadOnly(true);
											txtDepAccNo.setReadOnly(true);
										}
									}
								}
							},
							con,
							"select accno , name from acaccount where usecount=0 and actype=0",
							true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	protected void show_listExpAcc() {
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
											txtExpAccName.setReadOnly(false);
											txtExpAccNo.setReadOnly(false);
											int rn = tv.getSelectionValue();
											txtExpAccName.setValue(tv.getData()
													.getFieldValue(rn, "NAME")
													.toString());
											txtExpAccNo.setValue(tv.getData()
													.getFieldValue(rn, "accno")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										} finally {
											txtExpAccName.setReadOnly(true);
											txtExpAccNo.setReadOnly(true);
										}
									}
								}
							},
							con,
							"select accno , name from acaccount where usecount=0 and actype=0",
							true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	protected void show_listCostCent() {
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
							txtCostCentName.setReadOnly(false);
							txtCostCent.setReadOnly(false);
							int rn = tv.getSelectionValue();
							txtCostCentName.setValue(tv.getData()
									.getFieldValue(rn, "title").toString());
							txtCostCent.setValue(tv.getData().getFieldValue(rn,
									"code").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							txtCostCentName.setReadOnly(true);
							txtCostCent.setReadOnly(true);
						}
					}
				}
			}, con, "select code , title from accostcent1", true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	protected void show_listGroup() {
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
							txtGroupName.setReadOnly(false);
							txtGroup.setReadOnly(false);
							int rn = tv.getSelectionValue();
							txtGroupName.setValue(tv.getData().getFieldValue(
									rn, "CATNO").toString());
							txtGroup.setValue(tv.getData().getFieldValue(rn,
									"CATNAME").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							txtGroupName.setReadOnly(true);
							txtGroup.setReadOnly(true);
						}
					}
				}
			}, con, "select CATNO,CATNAME from facat", true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	protected void show_list() {
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
											QRYSES = tv.getData()
													.getFieldValue(rn, "code")
													.toString();
											load_data();
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"Select faitems.code, faitems.descr , faitems.accno, acaccount.name from faitems,acaccount where acaccount.accno=faitems.accno order by faitems.code",
							true);
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

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();

		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		contentLayout.removeAllComponents();
		codeLayout.removeAllComponents();
		groupLayout.removeAllComponents();
		descrLayout.removeAllComponents();
		AccLayout.removeAllComponents();
		depAccLayout.removeAllComponents();
		depExpAccLayout.removeAllComponents();
		costCenterLayout.removeAllComponents();
		purLayout.removeAllComponents();
		purDateLayout.removeAllComponents();
		purPriceLayout.removeAllComponents();
		depLayout.removeAllComponents();
		totalLayout.removeAllComponents();
		lastNetLayout.removeAllComponents();
		depRateLayout.removeAllComponents();
		priorDepLayout.removeAllComponents();
		totExpLayout.removeAllComponents();
		totValueLayout.removeAllComponents();
		lastDepDateLayout.removeAllComponents();
		netBookValueLayout.removeAllComponents();
		buttonLayout.removeAllComponents();
	}

}
