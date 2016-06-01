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

public class frmJobVou implements transactionalForm {
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
	private HorizontalLayout locationCodeLayout = new HorizontalLayout();
	private HorizontalLayout straLayout = new HorizontalLayout();

	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout typeLayout = new HorizontalLayout();
	private HorizontalLayout noLayout = new HorizontalLayout();
	private HorizontalLayout dateLayout = new HorizontalLayout();

	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout startDateLayout = new HorizontalLayout();
	private HorizontalLayout dueDateLayout = new HorizontalLayout();

	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout remarksLayout = new HorizontalLayout();

	private HorizontalLayout content5Layout = new HorizontalLayout();
	private HorizontalLayout depNoLayout = new HorizontalLayout();
	private HorizontalLayout totCostLayout = new HorizontalLayout();

	private HorizontalLayout content6Layout = new HorizontalLayout();
	private HorizontalLayout bkNoLayout = new HorizontalLayout();
	private HorizontalLayout jvNoLayout = new HorizontalLayout();

	private HorizontalLayout content7Layout = new HorizontalLayout();
	private HorizontalLayout prdDateLayout = new HorizontalLayout();
	private HorizontalLayout expDateLayout = new HorizontalLayout();
	private HorizontalLayout totAmtLayout = new HorizontalLayout();

	private Label lblLocationCode = new Label("Loc Code");
	private Label lblStra = new Label("Stra");
	private Label lblType = new Label("Type");
	private Label lblNo = new Label("No");
	private Label lblDate = new Label(" Inv Date");
	private Label lblStartDate = new Label("Start Date");
	private Label lblDueDate = new Label("Due Date");
	private Label lblMemo = new Label("Memo");
	private Label lblDeptno = new Label("Dept No");
	private Label lblTotCost = new Label("Tot Cose");
	private Label lblBkNo = new Label("Bk No");
	private Label lblJvNo = new Label("Jv No");
	private Label lblPrdDate = new Label("Prd Date");
	private Label lblExpDate = new Label("Exp Date");
	private Label lblAmt = new Label("Tot Amt");

	private TextField txtLocationCode = ControlsFactory.CreateTextField("", "",
			lstfldinfo);
	private TextField txtStra = ControlsFactory.CreateTextField("", "",
			lstfldinfo);
	private TextField txtType = ControlsFactory.CreateTextField("", "",
			lstfldinfo);
	private TextField txtNo = ControlsFactory.CreateTextField("", "",
			lstfldinfo);
	private DateField txtDate = ControlsFactory.CreateDateField("", "",
			lstfldinfo);
	private DateField txtStartDate = ControlsFactory.CreateDateField("", "",
			lstfldinfo);
	private DateField txtDueDate = ControlsFactory.CreateDateField("", "",
			lstfldinfo);
	private TextField txtMemo = ControlsFactory.CreateTextField("", "",
			lstfldinfo);
	private TextField txtDeptno = ControlsFactory.CreateTextField("", "",
			lstfldinfo);
	private TextField txtTotCost = ControlsFactory.CreateTextField("", "",
			lstfldinfo);
	private TextField txtBkNo = ControlsFactory.CreateTextField("", "",
			lstfldinfo);
	private TextField txtJvNo = ControlsFactory.CreateTextField("", "",
			lstfldinfo);
	private DateField txtPrdDate = ControlsFactory.CreateDateField("", "",
			lstfldinfo);
	private DateField txtExpDate = ControlsFactory.CreateDateField("", "",
			lstfldinfo);
	private TextField txtAmt = ControlsFactory.CreateTextField("", "",
			lstfldinfo);

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
						"select * from pur1 where invoice_code=27 and keyfld='"
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
			utilsVaadin.applyColumns("FRMJOBVOU.PUR2", table.getTable(),
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
		locationCodeLayout.setWidth("100%");
		straLayout.setWidth("100%");
		content2Layout.setWidth("100%");
		typeLayout.setWidth("100%");
		noLayout.setWidth("100%");
		dateLayout.setWidth("100%");
		content3Layout.setWidth("100%");
		startDateLayout.setWidth("100%");
		dueDateLayout.setWidth("100%");
		content4Layout.setWidth("100%");
		remarksLayout.setWidth("100%");
		content5Layout.setWidth("100%");
		depNoLayout.setWidth("100%");
		totCostLayout.setWidth("100%");
		content6Layout.setWidth("100%");
		bkNoLayout.setWidth("100%");
		jvNoLayout.setWidth("100%");
		content7Layout.setWidth("100%");
		prdDateLayout.setWidth("100%");
		expDateLayout.setWidth("100%");
		totAmtLayout.setWidth("100%");

		lblLocationCode.setWidth("60px");
		lblStra.setWidth("60px");
		lblType.setWidth("65px");
		lblNo.setWidth("50px");
		lblDate.setWidth("60px");
		lblStartDate.setWidth("60px");
		lblDueDate.setWidth("60px");
		lblMemo.setWidth("55px");
		lblDeptno.setWidth("60px");
		lblTotCost.setWidth("60px");
		lblBkNo.setWidth("60px");
		lblJvNo.setWidth("60px");
		lblPrdDate.setWidth("60px");
		lblExpDate.setWidth("60px");
		lblAmt.setWidth("60px");

		txtLocationCode.setWidth("100%");
		txtStra.setWidth("100%");
		txtType.setWidth("100%");
		txtNo.setWidth("100%");
		txtDate.setWidth("100%");
		txtStartDate.setWidth("100%");
		txtDueDate.setWidth("100%");
		txtMemo.setWidth("100%");
		txtDeptno.setWidth("100%");
		txtTotCost.setWidth("100%");
		txtBkNo.setWidth("100%");
		txtJvNo.setWidth("100%");
		txtPrdDate.setWidth("100%");
		txtExpDate.setWidth("100%");
		txtAmt.setWidth("100%");

		basicLayout.addStyleName("formLayout");

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);

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
		ResourceManager.addComponent(basicLayout, content4Layout);
		ResourceManager.addComponent(basicLayout, content5Layout);
		ResourceManager.addComponent(basicLayout, content6Layout);
		ResourceManager.addComponent(basicLayout, tableLayout);
		ResourceManager.addComponent(basicLayout, content7Layout);

		ResourceManager.addComponent(content1Layout, locationCodeLayout);
		ResourceManager.addComponent(content1Layout, straLayout);

		ResourceManager.addComponent(content2Layout, typeLayout);
		ResourceManager.addComponent(content2Layout, noLayout);
		ResourceManager.addComponent(content2Layout, dateLayout);

		ResourceManager.addComponent(content3Layout, startDateLayout);
		ResourceManager.addComponent(content3Layout, dueDateLayout);

		ResourceManager.addComponent(content4Layout, remarksLayout);

		ResourceManager.addComponent(content5Layout, depNoLayout);
		ResourceManager.addComponent(content5Layout, totCostLayout);

		ResourceManager.addComponent(content6Layout, bkNoLayout);
		ResourceManager.addComponent(content6Layout, jvNoLayout);

		ResourceManager.addComponent(content7Layout, prdDateLayout);
		ResourceManager.addComponent(content7Layout, expDateLayout);
		ResourceManager.addComponent(content7Layout, totAmtLayout);

		ResourceManager.addComponent(locationCodeLayout, lblLocationCode);
		ResourceManager.addComponent(locationCodeLayout, txtLocationCode);

		ResourceManager.addComponent(straLayout, lblStra);
		ResourceManager.addComponent(straLayout, txtStra);

		ResourceManager.addComponent(typeLayout, lblType);
		ResourceManager.addComponent(typeLayout, txtType);

		ResourceManager.addComponent(noLayout, lblNo);
		ResourceManager.addComponent(noLayout, txtNo);

		ResourceManager.addComponent(dateLayout, lblDate);
		ResourceManager.addComponent(dateLayout, txtDate);

		ResourceManager.addComponent(startDateLayout, lblStartDate);
		ResourceManager.addComponent(startDateLayout, txtStartDate);

		ResourceManager.addComponent(dueDateLayout, lblDueDate);
		ResourceManager.addComponent(dueDateLayout, txtDueDate);

		ResourceManager.addComponent(remarksLayout, lblMemo);
		ResourceManager.addComponent(remarksLayout, txtMemo);

		ResourceManager.addComponent(depNoLayout, lblDeptno);
		ResourceManager.addComponent(depNoLayout, txtDeptno);

		ResourceManager.addComponent(totCostLayout, lblTotCost);
		ResourceManager.addComponent(totCostLayout, txtTotCost);

		ResourceManager.addComponent(bkNoLayout, lblBkNo);
		ResourceManager.addComponent(bkNoLayout, txtBkNo);

		ResourceManager.addComponent(jvNoLayout, lblJvNo);
		ResourceManager.addComponent(jvNoLayout, txtJvNo);

		ResourceManager.addComponent(prdDateLayout, lblPrdDate);
		ResourceManager.addComponent(prdDateLayout, txtPrdDate);

		ResourceManager.addComponent(expDateLayout, lblExpDate);
		ResourceManager.addComponent(expDateLayout, txtExpDate);

		ResourceManager.addComponent(totAmtLayout, lblAmt);
		ResourceManager.addComponent(totAmtLayout, txtAmt);

		locationCodeLayout.setComponentAlignment(lblLocationCode,
				Alignment.BOTTOM_CENTER);
		straLayout.setComponentAlignment(lblStra, Alignment.BOTTOM_CENTER);
		typeLayout.setComponentAlignment(lblType, Alignment.BOTTOM_CENTER);
		noLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);
		dateLayout.setComponentAlignment(lblDate, Alignment.BOTTOM_CENTER);
		startDateLayout.setComponentAlignment(lblStartDate,
				Alignment.BOTTOM_CENTER);
		dueDateLayout
				.setComponentAlignment(lblDueDate, Alignment.BOTTOM_CENTER);
		remarksLayout.setComponentAlignment(lblMemo, Alignment.BOTTOM_CENTER);
		depNoLayout.setComponentAlignment(lblDeptno, Alignment.BOTTOM_CENTER);
		totCostLayout
				.setComponentAlignment(lblTotCost, Alignment.BOTTOM_CENTER);
		bkNoLayout.setComponentAlignment(lblBkNo, Alignment.BOTTOM_CENTER);
		jvNoLayout.setComponentAlignment(lblJvNo, Alignment.BOTTOM_CENTER);
		prdDateLayout
				.setComponentAlignment(lblPrdDate, Alignment.BOTTOM_CENTER);
		totAmtLayout.setComponentAlignment(lblAmt, Alignment.BOTTOM_CENTER);
		expDateLayout
				.setComponentAlignment(lblExpDate, Alignment.BOTTOM_CENTER);

		content1Layout.setExpandRatio(locationCodeLayout, 2.7f);
		content1Layout.setExpandRatio(straLayout, 1.3f);

		content2Layout.setExpandRatio(typeLayout, 1.4f);
		content2Layout.setExpandRatio(noLayout, 1.3f);
		content2Layout.setExpandRatio(dateLayout, 1.3f);

		content3Layout.setExpandRatio(startDateLayout, 2.7f);
		content3Layout.setExpandRatio(dueDateLayout, 1.3f);

		content4Layout.setExpandRatio(remarksLayout, 4f);

		content5Layout.setExpandRatio(depNoLayout, 2.7f);
		content5Layout.setExpandRatio(totCostLayout, 1.3f);

		content6Layout.setExpandRatio(bkNoLayout, 2.7f);
		content6Layout.setExpandRatio(jvNoLayout, 1.3f);

		content7Layout.setExpandRatio(prdDateLayout, 1.4f);
		content7Layout.setExpandRatio(expDateLayout, 1.3f);
		content7Layout.setExpandRatio(totAmtLayout, 1.3f);

		locationCodeLayout.setExpandRatio(lblLocationCode, 0.1f);
		locationCodeLayout.setExpandRatio(txtLocationCode, 3.9f);

		straLayout.setExpandRatio(lblStra, 0.1f);
		straLayout.setExpandRatio(txtStra, 3.9f);

		typeLayout.setExpandRatio(lblType, 0.1f);
		typeLayout.setExpandRatio(txtType, 3.9f);

		noLayout.setExpandRatio(lblNo, 0.1f);
		noLayout.setExpandRatio(txtNo, 3.9f);

		dateLayout.setExpandRatio(lblDate, 0.1f);
		dateLayout.setExpandRatio(txtDate, 3.9f);

		startDateLayout.setExpandRatio(lblStartDate, 0.1f);
		startDateLayout.setExpandRatio(txtStartDate, 3.9f);

		dueDateLayout.setExpandRatio(lblDueDate, 0.1f);
		dueDateLayout.setExpandRatio(txtDueDate, 3.9f);

		remarksLayout.setExpandRatio(lblMemo, 0.1f);
		remarksLayout.setExpandRatio(txtMemo, 3.9f);

		depNoLayout.setExpandRatio(lblDeptno, 0.1f);
		depNoLayout.setExpandRatio(txtDeptno, 3.9f);

		totCostLayout.setExpandRatio(lblTotCost, 0.1f);
		totCostLayout.setExpandRatio(txtTotCost, 3.9f);

		bkNoLayout.setExpandRatio(lblBkNo, 0.1f);
		bkNoLayout.setExpandRatio(txtBkNo, 3.9f);

		jvNoLayout.setExpandRatio(lblJvNo, 0.1f);
		jvNoLayout.setExpandRatio(txtJvNo, 3.9f);

		prdDateLayout.setExpandRatio(lblPrdDate, 0.1f);
		prdDateLayout.setExpandRatio(txtPrdDate, 3.9f);

		expDateLayout.setExpandRatio(lblExpDate, 0.1f);
		expDateLayout.setExpandRatio(txtExpDate, 3.9f);

		totAmtLayout.setExpandRatio(lblAmt, 0.1f);
		totAmtLayout.setExpandRatio(txtAmt, 3.9f);

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
							"select invoice_no,invoice_date,c_cus_no,inv_amt-disc_amt net_amount from pur1 where  invoicecode=27  order by invoice_no desc",
							true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
