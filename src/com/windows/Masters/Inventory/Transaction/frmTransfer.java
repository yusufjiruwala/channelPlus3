package com.windows.Masters.Inventory.Transaction;

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
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmTransfer implements transactionalForm {
	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	Tree tree = new Tree();
	private localTableModel data_Items = new localTableModel();

	private VerticalLayout tableLayout = new VerticalLayout();
	TableLayoutVaadin table = new TableLayoutVaadin(tableLayout);
	private final List<ColumnProperty> lstBranchCols = new ArrayList<ColumnProperty>();

	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();

	private HorizontalLayout buttonLayout = new HorizontalLayout();

	private HorizontalLayout content1Layout = new HorizontalLayout();
	private HorizontalLayout keyfldLayout = new HorizontalLayout();
	private HorizontalLayout locationCodeLayout = new HorizontalLayout();

	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout invNoLayout = new HorizontalLayout();
	private HorizontalLayout invDateLayout = new HorizontalLayout();
	private HorizontalLayout salesPerLayout = new HorizontalLayout();

	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout straLayout = new HorizontalLayout();
	private HorizontalLayout strbLayout = new HorizontalLayout();

	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout packLayout = new HorizontalLayout();
	private HorizontalLayout balLayout = new HorizontalLayout();
	private HorizontalLayout totCostLayout = new HorizontalLayout();

	private HorizontalLayout content5Layout = new HorizontalLayout();
	private HorizontalLayout prdDateLayout = new HorizontalLayout();
	private HorizontalLayout expDateLayout = new HorizontalLayout();
	private HorizontalLayout rcvLayout = new HorizontalLayout();
	private HorizontalLayout issuedLayout = new HorizontalLayout();

	private Label lblKeyFld = new Label("Key Id");
	private Label lblLocationCode = new Label("Loc Code");
	private Label lblInvNo = new Label("Inv no");
	private Label lblInvDate = new Label("Inv Date");
	private Label lblSalesPer = new Label("Sales Per");
	private Label lblStra = new Label("Stra");
	private Label lblStrb = new Label("Strb");
	private Label lblPack = new Label("Pack");
	private Label lblBalance = new Label("Bal");
	private Label lblTotCost = new Label("Tot Cost");
	private Label lblPrdDate = new Label("Prd Date");
	private Label lblExpDate = new Label("Exp Date");
	private Label lblRcv = new Label("Receive");
	private Label lblIssued = new Label("Issue");

	private TextField txtKeyFld = ControlsFactory.CreateTextField("", "KEYFLD",
			lstfldinfo);
	private TextField txtLocationCode = ControlsFactory.CreateTextField("",
			"LOCATION_CODE", lstfldinfo);
	private TextField txtInvNo = ControlsFactory.CreateTextField("",
			"INVOICE_NO", lstfldinfo);
	private DateField txtInvDate = ControlsFactory.CreateDateField("",
			"INVOICE_DATE", lstfldinfo);
	private TextField txtSalesPer = ControlsFactory.CreateTextField("",
			"SLSMN", lstfldinfo);
	private TextField txtStra = ControlsFactory.CreateTextField("", "STRA",
			lstfldinfo);
	private TextField txtStrb = ControlsFactory.CreateTextField("", "STRB",
			lstfldinfo);
	private TextField txtPack = ControlsFactory.CreateTextField("", "PACKD",
			lstfldinfo);
	private TextField txtBalance = ControlsFactory.CreateTextField("", "BLNC",
			lstfldinfo);
	private TextField txtTotCost = ControlsFactory.CreateTextField("", "AMT",
			lstfldinfo);
	private DateField txtPrdDate = ControlsFactory.CreateDateField("",
			"PRD_DATE", lstfldinfo);
	private DateField txtExpDate = ControlsFactory.CreateDateField("",
			"EXP_DATE", lstfldinfo);
	private TextField txtRcv = ControlsFactory.CreateTextField("",
			"TOT_HAS_RECIEVED", lstfldinfo);
	private TextField txtIssued = ControlsFactory.CreateTextField("",
			"TOT_HAS_ISSUED", lstfldinfo);

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");
	private NativeButton cmdPrint = ControlsFactory.CreateCustomButton("Print",
			"img/pdf.png", "Save", "");
	private NativeButton cmdCopyVou = ControlsFactory.CreateCustomButton(
			"Copy", "img/copy.png", "Exporting to pdf", "");
	private NativeButton cmdColumn = ControlsFactory.CreateCustomButton(
			"Columns", "img/columns.png", "Columns", "");

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
			qe.setParaValue("PERIODCODE", "");
			qe.setParaValue("INVOICE_CODE", "21");
			qe.setParaValue("DUEDATE", dt);
			qe.setParaValue("FLAG", "0");
			qe.setParaValue("YEAR", "");
			qe.setParaValue("CREATDT", dt);
			qe.setParaValue("ISCLOSE", "Y");
			qe.setParaValue("ADD_CHARGE", "0");
			qe.setParaValue("ADD_CHARGEX", "0");
			qe.setParaValue("PRINTCOUNT", "0");
			qe.setParaValue("HANDLE_KD", "0.0");
			qe.setParaValue("FRGHT_KD", "0.0");
			qe.setParaValue("FRGHT_FC", "0.0");
			qe.setParaValue("RATE", "0");
			qe.setParaValue("WAGES", "0");
			qe.setParaValue("BANK_CHG", "0");
			qe.setParaValue("OTHER", "0");
			qe.setParaValue("KDCOST", "0");
			qe.setParaValue("CHG_KDAMT", "0");
			qe.setParaValue("CHG_CODE", "0");
			qe.setParaValue("DELIVEREDG", "0");
			qe.setParaValue("DELIVERED", "0");
			qe.setParaValue("PAIDAMT2", "0");
			qe.setParaValue("PAIDAMT1", "0");
			qe.setParaValue("TOTQTY", "0.0");
			qe.setParaValue("TOT_HAS_ISSUED", "0.0");
			qe.setParaValue("NO_OF_ISSUES", "0.0");
			qe.setParaValue("NO_OF_RECIEVED", "0.0");
			qe.setParaValue("TOT_DAMAGE_QTY", "0");

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("PUR1"); // if
			} else {
				qe.AutoGenerateUpdateStatment("PUR1", "'KEYFLD'",
						" WHERE KEYFLD=:KEYFLD"); // if to update..
			}
			qe.execute();
			con.commit();
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtLocationCode.getValue().toString();
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
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"select * from pur1 where invoice_code=3 and keyfld='"
								+ QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void print_data() {

	}

	public void applyColumnsOnBranch() {
		lstBranchCols.clear();
		try {
			utilsVaadin.applyColumns("FRMOPEN.PUR2", table.getTable(),
					lstBranchCols, con);
		} catch (SQLException e) {
		}
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
		mainLayout.setWidth("600px");
		basicLayout.setWidth("100%");

		content1Layout.setWidth("100%");
		keyfldLayout.setWidth("100%");
		locationCodeLayout.setWidth("100%");

		content2Layout.setWidth("100%");
		invNoLayout.setWidth("100%");
		invDateLayout.setWidth("100%");
		salesPerLayout.setWidth("100%");

		content3Layout.setWidth("100%");
		straLayout.setWidth("100%");
		strbLayout.setWidth("100%");

		content4Layout.setWidth("100%");
		packLayout.setWidth("100%");
		balLayout.setWidth("100%");
		totCostLayout.setWidth("100%");

		content5Layout.setWidth("100%");
		prdDateLayout.setWidth("100%");
		expDateLayout.setWidth("100%");
		rcvLayout.setWidth("100%");
		issuedLayout.setWidth("100%");

		lblKeyFld.setWidth("60px");
		lblLocationCode.setWidth("60px");
		lblInvNo.setWidth("60px");
		lblInvDate.setWidth("60px");
		lblSalesPer.setWidth("60px");
		lblStra.setWidth("60px");
		lblStrb.setWidth("60px");
		lblPack.setWidth("60px");
		lblBalance.setWidth("60px");
		lblTotCost.setWidth("60px");
		lblPrdDate.setWidth("65px");
		lblExpDate.setWidth("60px");
		lblRcv.setWidth("60px");
		lblIssued.setWidth("60px");

		txtKeyFld.setWidth("100%");
		txtLocationCode.setWidth("100%");
		txtInvNo.setWidth("100%");
		txtInvDate.setWidth("100%");
		txtSalesPer.setWidth("100%");
		txtStra.setWidth("100%");
		txtStrb.setWidth("100%");
		txtPack.setWidth("100%");
		txtBalance.setWidth("100%");
		txtTotCost.setWidth("100%");
		txtPrdDate.setWidth("100%");
		txtExpDate.setWidth("100%");
		txtRcv.setWidth("100%");
		txtIssued.setWidth("100%");

		tableLayout.setHeight("120px");
		table.getTable().setHeight("110px");

		basicLayout.addStyleName("formLayout");

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdCopyVou);
		ResourceManager.addComponent(buttonLayout, cmdPrint);
		ResourceManager.addComponent(buttonLayout, cmdColumn);
		ResourceManager.addComponent(buttonLayout, cmdCls);

		ResourceManager.addComponent(basicLayout, content1Layout);
		ResourceManager.addComponent(basicLayout, content2Layout);
		ResourceManager.addComponent(basicLayout, content3Layout);
		ResourceManager.addComponent(basicLayout, tableLayout);
		ResourceManager.addComponent(basicLayout, content4Layout);
		ResourceManager.addComponent(basicLayout, content5Layout);

		ResourceManager.addComponent(content1Layout, keyfldLayout);
		ResourceManager.addComponent(content1Layout, locationCodeLayout);

		ResourceManager.addComponent(content2Layout, invNoLayout);
		ResourceManager.addComponent(content2Layout, invDateLayout);
		ResourceManager.addComponent(content2Layout, salesPerLayout);

		ResourceManager.addComponent(content3Layout, straLayout);
		ResourceManager.addComponent(content3Layout, strbLayout);

		ResourceManager.addComponent(content4Layout, packLayout);
		ResourceManager.addComponent(content4Layout, balLayout);
		ResourceManager.addComponent(content4Layout, totCostLayout);

		ResourceManager.addComponent(content5Layout, prdDateLayout);
		ResourceManager.addComponent(content5Layout, expDateLayout);
		ResourceManager.addComponent(content5Layout, rcvLayout);
		ResourceManager.addComponent(content5Layout, issuedLayout);

		ResourceManager.addComponent(keyfldLayout, lblKeyFld);
		ResourceManager.addComponent(keyfldLayout, txtKeyFld);

		ResourceManager.addComponent(locationCodeLayout, lblLocationCode);
		ResourceManager.addComponent(locationCodeLayout, txtLocationCode);

		ResourceManager.addComponent(invNoLayout, lblInvNo);
		ResourceManager.addComponent(invNoLayout, txtInvNo);

		ResourceManager.addComponent(invDateLayout, lblInvDate);
		ResourceManager.addComponent(invDateLayout, txtInvDate);

		ResourceManager.addComponent(salesPerLayout, lblSalesPer);
		ResourceManager.addComponent(salesPerLayout, txtSalesPer);

		ResourceManager.addComponent(straLayout, lblStra);
		ResourceManager.addComponent(straLayout, txtStra);

		ResourceManager.addComponent(strbLayout, lblStrb);
		ResourceManager.addComponent(strbLayout, txtStrb);

		ResourceManager.addComponent(packLayout, lblPack);
		ResourceManager.addComponent(packLayout, txtPack);

		ResourceManager.addComponent(balLayout, lblBalance);
		ResourceManager.addComponent(balLayout, txtBalance);

		ResourceManager.addComponent(totCostLayout, lblTotCost);
		ResourceManager.addComponent(totCostLayout, txtTotCost);

		ResourceManager.addComponent(prdDateLayout, lblPrdDate);
		ResourceManager.addComponent(prdDateLayout, txtPrdDate);

		ResourceManager.addComponent(expDateLayout, lblExpDate);
		ResourceManager.addComponent(expDateLayout, txtExpDate);

		ResourceManager.addComponent(rcvLayout, lblRcv);
		ResourceManager.addComponent(rcvLayout, txtRcv);

		ResourceManager.addComponent(issuedLayout, lblIssued);
		ResourceManager.addComponent(issuedLayout, txtIssued);

		keyfldLayout.setComponentAlignment(lblKeyFld, Alignment.BOTTOM_CENTER);
		locationCodeLayout.setComponentAlignment(lblLocationCode,
				Alignment.BOTTOM_CENTER);
		invNoLayout.setComponentAlignment(lblInvNo, Alignment.BOTTOM_CENTER);
		invDateLayout
				.setComponentAlignment(lblInvDate, Alignment.BOTTOM_CENTER);
		salesPerLayout.setComponentAlignment(lblSalesPer,
				Alignment.BOTTOM_CENTER);
		straLayout.setComponentAlignment(lblStra, Alignment.BOTTOM_CENTER);
		strbLayout.setComponentAlignment(lblStrb, Alignment.BOTTOM_CENTER);
		packLayout.setComponentAlignment(lblPack, Alignment.BOTTOM_CENTER);
		balLayout.setComponentAlignment(lblBalance, Alignment.BOTTOM_CENTER);
		totCostLayout
				.setComponentAlignment(lblTotCost, Alignment.BOTTOM_CENTER);
		prdDateLayout
				.setComponentAlignment(lblPrdDate, Alignment.BOTTOM_CENTER);
		expDateLayout
				.setComponentAlignment(lblExpDate, Alignment.BOTTOM_CENTER);
		rcvLayout.setComponentAlignment(lblRcv, Alignment.BOTTOM_CENTER);
		issuedLayout.setComponentAlignment(lblIssued, Alignment.BOTTOM_CENTER);

		content1Layout.setExpandRatio(keyfldLayout, 1.4f);
		content1Layout.setExpandRatio(locationCodeLayout, 2.6f);

		content2Layout.setExpandRatio(invNoLayout, 1.4f);
		content2Layout.setExpandRatio(invDateLayout, 1.3f);
		content2Layout.setExpandRatio(salesPerLayout, 1.3f);

		content3Layout.setExpandRatio(straLayout, 1.4f);
		content3Layout.setExpandRatio(strbLayout, 2.6f);

		content4Layout.setExpandRatio(packLayout, 1.4f);
		content4Layout.setExpandRatio(packLayout, 1.3f);
		content4Layout.setExpandRatio(packLayout, 1.3f);

		content5Layout.setExpandRatio(prdDateLayout, 1f);
		content5Layout.setExpandRatio(expDateLayout, 1f);
		content5Layout.setExpandRatio(rcvLayout, 1f);
		content5Layout.setExpandRatio(issuedLayout, 1f);

		keyfldLayout.setExpandRatio(lblKeyFld, 0.1f);
		keyfldLayout.setExpandRatio(txtKeyFld, 3.9f);

		locationCodeLayout.setExpandRatio(lblLocationCode, 0.1f);
		locationCodeLayout.setExpandRatio(txtLocationCode, 3.9f);

		invNoLayout.setExpandRatio(lblInvNo, 0.1f);
		invNoLayout.setExpandRatio(txtInvNo, 3.9f);

		invDateLayout.setExpandRatio(lblInvDate, 0.1f);
		invDateLayout.setExpandRatio(txtInvDate, 3.9f);

		salesPerLayout.setExpandRatio(lblSalesPer, 0.1f);
		salesPerLayout.setExpandRatio(txtSalesPer, 3.9f);

		straLayout.setExpandRatio(lblStra, 0.1f);
		straLayout.setExpandRatio(txtStra, 3.9f);

		strbLayout.setExpandRatio(lblStrb, 0.1f);
		strbLayout.setExpandRatio(txtStrb, 3.9f);

		packLayout.setExpandRatio(lblPack, 0.1f);
		packLayout.setExpandRatio(txtPack, 3.9f);

		balLayout.setExpandRatio(lblBalance, 0.1f);
		balLayout.setExpandRatio(txtBalance, 3.9f);

		totCostLayout.setExpandRatio(lblTotCost, 0.1f);
		totCostLayout.setExpandRatio(txtTotCost, 3.9f);

		prdDateLayout.setExpandRatio(lblPrdDate, 0.1f);
		prdDateLayout.setExpandRatio(txtPrdDate, 3.9f);

		expDateLayout.setExpandRatio(lblExpDate, 0.1f);
		expDateLayout.setExpandRatio(txtExpDate, 3.9f);

		rcvLayout.setExpandRatio(lblRcv, 0.1f);
		rcvLayout.setExpandRatio(txtRcv, 3.9f);

		issuedLayout.setExpandRatio(lblIssued, 0.1f);
		issuedLayout.setExpandRatio(txtIssued, 3.9f);

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

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();

		content1Layout.removeAllComponents();
		keyfldLayout.removeAllComponents();
		locationCodeLayout.removeAllComponents();

		content2Layout.removeAllComponents();
		invNoLayout.removeAllComponents();
		invDateLayout.removeAllComponents();
		salesPerLayout.removeAllComponents();

		content3Layout.removeAllComponents();
		straLayout.removeAllComponents();
		strbLayout.removeAllComponents();

		content4Layout.removeAllComponents();
		packLayout.removeAllComponents();
		balLayout.removeAllComponents();
		totCostLayout.removeAllComponents();

		content5Layout.removeAllComponents();
		prdDateLayout.removeAllComponents();
		expDateLayout.removeAllComponents();
		rcvLayout.removeAllComponents();
		issuedLayout.removeAllComponents();

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractOrderedLayout) parentLayout;
	}

	public void showInitView() {
		QRYSES = "";
		initForm();
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
													.getFieldValue(rn,
															"INVOICE_NO")
													.toString();
											load_data();
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select invoice_no,invoice_date,c_cus_no,inv_amt-disc_amt net_amount from pur1 where  invoicecode=3  order by invoice_no desc",
							true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
