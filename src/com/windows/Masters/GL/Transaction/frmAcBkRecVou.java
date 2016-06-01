package com.windows.Masters.GL.Transaction;

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
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
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

public class frmAcBkRecVou implements transactionalForm {

	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	Tree tree = new Tree();
	private localTableModel data_Items = new localTableModel();

	private VerticalLayout tableLayout = new VerticalLayout();

	private final List<ColumnProperty> lstBranchCols = new ArrayList<ColumnProperty>();

	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout content1Layout = new HorizontalLayout();
	private HorizontalLayout noLayout = new HorizontalLayout();
	private HorizontalLayout bSerialLayout = new HorizontalLayout();
	private HorizontalLayout dateLayout = new HorizontalLayout();
	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout descrLayout = new HorizontalLayout();
	private HorizontalLayout rFromLayout = new HorizontalLayout();
	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout chequeNoLayout = new HorizontalLayout();
	private HorizontalLayout dueDateLayout = new HorizontalLayout();
	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout salesManLayout = new HorizontalLayout();
	private HorizontalLayout invoiceTypeLayout = new HorizontalLayout();
	private HorizontalLayout content5Layout = new HorizontalLayout();
	private HorizontalLayout codeLayout = new HorizontalLayout();
	private HorizontalLayout costCentLayout = new HorizontalLayout();
	private HorizontalLayout content6Layout = new HorizontalLayout();
	private HorizontalLayout fcCodeLayout = new HorizontalLayout();
	private HorizontalLayout fcRateLayout = new HorizontalLayout();
	private HorizontalLayout content7Layout = new HorizontalLayout();
	private HorizontalLayout cAmtLayout = new HorizontalLayout();
	private HorizontalLayout acBalLayout = new HorizontalLayout();
	private HorizontalLayout emptyLayout = new HorizontalLayout();

	private Label lblNo = new Label("No");
	private Label lblBSerial = new Label("Book Serial");
	private Label lblDate = new Label("Date");
	private Label lblDescr = new Label("Descr");
	private Label lblRFrom = new Label("Recieved From");
	private Label lblChNo = new Label("Cheque No");
	private Label lblDueDate = new Label("Due Date");
	private Label lblSalesMan = new Label("Sales Man");
	private Label lblCollector = new Label("Collector");
	private Label lblCode = new Label("Code");
	private Label lblCostCent = new Label("Cost Center");
	private Label lblFC = new Label("F.C.");
	private Label lblRate = new Label("Rate");
	private Label lblCAmt = new Label("Cr Amt");
	private Label lblAcBal = new Label("A/c Balance");

	private Label emptyLabel = new Label("");

	private TextField txtNo = ControlsFactory.CreateTextField(null, "NO",
			lstfldinfo);
	private TextField txtBSerial = ControlsFactory.CreateTextField(null,
			"BOOKSERIALNO", lstfldinfo);
	private DateField txtDate = ControlsFactory.CreateDateField(null,
			"VOU_DATE", lstfldinfo);
	private TextField txtDescr = ControlsFactory.CreateTextField(null, "DESCR",
			lstfldinfo);
	private TextField txtReceivedFrom = ControlsFactory.CreateTextField(null,
			"RCVFROM", lstfldinfo);
	private TextField txtChNo = ControlsFactory.CreateTextField(null,
			"CHEQUENO", lstfldinfo);
	private DateField txtDueDate = ControlsFactory.CreateDateField(null,
			"DUEDATE", lstfldinfo);
	private ComboBox txtSalesMan = ControlsFactory.CreateListField(null,
			"SLSMN", "Select no,name from salesp where type='S' order by no",
			lstfldinfo);
	private ComboBox txtCollector = ControlsFactory
			.CreateListField(null, "INVOICE_TYPE",
					"Select no,name from salesp where type='C' order by no",
					lstfldinfo);
	private TextField txtCode = ControlsFactory.CreateTextField(null, "CODE",
			lstfldinfo);
	private TextField txtCodeName = ControlsFactory.CreateTextField(null, "",
			null);
	private TextField txtCostCent = ControlsFactory.CreateTextField(null,
			"COSTCENT", lstfldinfo);
	private TextField txtCostName = ControlsFactory.CreateTextField(null, "",
			null);
	private TextField txtFCCode = ControlsFactory.CreateTextField(null,
			"FCCODE", lstfldinfo);
	private TextField txtRate = ControlsFactory.CreateTextField(null, "FCRATE",
			lstfldinfo);
	private TextField txtCAMT = ControlsFactory.CreateTextField(null, "", null);
	private TextField txtACBal = ControlsFactory
			.CreateTextField(null, "", null);

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

	private NativeButton cmdCode = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearCode = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdCostCent = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearCostCent = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");

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
			qe.setParaValue("KEYFLD", "0");
			qe.setParaValue("PERIODCODE", "Y");
			qe.setParaValue("TYPE", "0");
			qe.setParaValue("VOU_CODE", "0");
			qe.setParaValue("FLAG", "0");
			qe.setParaValue("CREATDT", dt);
			qe.setParaValue("ISCHANGE", "Y");
			qe.setParaValue("ISNEW", "Y");
			qe.setParaValue("INVOICE_CODE", "Y");
			qe.setParaValue("PRINTCOUNT", "0");
			qe.setParaValue("FCDEBAMT", "0");
			qe.setParaValue("FCCRDAMT", "0");
			qe.setParaValue("FCCODEAMT", "0");
			qe.setParaValue("FC_MAIN_1", "0");
			qe.setParaValue("FC_MAIN_RATE_1", "0");

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("acvoucher1"); // if
			} else {
				qe.AutoGenerateUpdateStatment("acvoucher1", "'NO'",
						" WHERE NO=:NO"); // if to update..
			}
			qe.execute();
			qe.close();
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

	public void applyColumnsOnBranch() {
		lstBranchCols.clear();
		try {
			utilsVaadin.applyColumns("FRMACBKRECVOU.ACVOUCHER2", table
					.getTable(), table.listFields, con);

		} catch (SQLException e) {
		}
	}

	public void load_data() {
		try {
			utilsVaadin.resetValues(basicLayout, false, false);
			txtNo.setReadOnly(false);
			txtCostName.setReadOnly(true);
			txtCodeName.setReadOnly(true);
			txtCode.setReadOnly(true);
			txtCostCent.setReadOnly(true);
			if (!QRYSES.isEmpty()) {
				txtNo.setReadOnly(true);
				PreparedStatement pstmt = con.prepareStatement(
						"Select * from acvoucher1 where keyfld = '" + QRYSES
								+ "'", ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
			}
			fetchAccountInfo();
			table.data
					.executeQuery(
							" Select acvoucher2.*,accostcent1.title csname  from "
									+ " acvoucher2,accostcent1 where Acvoucher2.costcent=accostcent1.code(+) "
									+ " and acvoucher2.keyfld='" + QRYSES
									+ "' and acvoucher2.type=1 order by pos",
							true);
			table.fill_table();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	protected void fetchAccountInfo() {
		utilsVaadin.setValueByForce(txtCodeName, utils.getSqlValue(
				"Select name from acaccount where accno = '"
						+ txtCode.getValue().toString() + "'", con));
		utilsVaadin.setValueByForce(txtCostName, utils.getSqlValue(
				"Select title from accostcent1 where code = '"
						+ txtCostCent.getValue().toString() + "'", con));
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
		mainLayout.setWidth("700px");
		basicLayout.setWidth("100%");
		content1Layout.setWidth("100%");
		noLayout.setWidth("100%");
		bSerialLayout.setWidth("100%");
		dateLayout.setWidth("100%");
		content2Layout.setWidth("100%");
		descrLayout.setWidth("100%");
		rFromLayout.setWidth("100%");
		content3Layout.setWidth("100%");
		chequeNoLayout.setWidth("100%");
		dueDateLayout.setWidth("100%");
		content4Layout.setWidth("100%");
		salesManLayout.setWidth("100%");
		invoiceTypeLayout.setWidth("100%");
		content5Layout.setWidth("100%");
		codeLayout.setWidth("100%");
		costCentLayout.setWidth("100%");
		content6Layout.setWidth("100%");
		fcCodeLayout.setWidth("100%");
		fcRateLayout.setWidth("100%");
		content7Layout.setWidth("100%");
		cAmtLayout.setWidth("100%");
		acBalLayout.setWidth("100%");

		txtNo.setReadOnly(true);

		lblNo.setWidth("85px");
		lblBSerial.setWidth("80px");
		lblDate.setWidth("80px");
		lblDescr.setWidth("80px");
		lblRFrom.setWidth("80px");
		lblChNo.setWidth("80px");
		lblDueDate.setWidth("80px");
		lblSalesMan.setWidth("80px");
		lblCollector.setWidth("80px");
		lblCode.setWidth("82px");
		lblCostCent.setWidth("80px");
		lblFC.setWidth("80px");
		lblRate.setWidth("80px");
		lblCAmt.setWidth("80px");
		lblAcBal.setWidth("80px");
		emptyLabel.setHeight("15px");

		txtNo.setWidth("100%");
		txtBSerial.setWidth("100%");
		txtDate.setWidth("100%");
		txtDescr.setWidth("100%");
		txtReceivedFrom.setWidth("100%");
		txtChNo.setWidth("100%");
		txtDueDate.setWidth("100%");
		txtSalesMan.setWidth("100%");
		txtCollector.setWidth("100%");
		txtCode.setWidth("100%");
		txtCodeName.setWidth("100%");
		txtCostCent.setWidth("100%");
		txtCostName.setWidth("100%");
		txtFCCode.setWidth("100%");
		txtRate.setWidth("100%");
		txtCAMT.setWidth("100%");
		txtACBal.setWidth("100%");

		tableLayout.setHeight("275px");
		table.getTable().setHeight("250px");

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
		// ResourceManager.addComponent(basicLayout, emptyLayout);
		ResourceManager.addComponent(basicLayout, content4Layout);
		ResourceManager.addComponent(basicLayout, content5Layout);
		ResourceManager.addComponent(basicLayout, content6Layout);
		ResourceManager.addComponent(basicLayout, tableLayout);
		ResourceManager.addComponent(basicLayout, content7Layout);

		ResourceManager.addComponent(emptyLayout, emptyLabel);

		ResourceManager.addComponent(content1Layout, noLayout);
		ResourceManager.addComponent(content1Layout, bSerialLayout);
		ResourceManager.addComponent(content1Layout, dateLayout);

		ResourceManager.addComponent(content2Layout, descrLayout);
		ResourceManager.addComponent(content2Layout, rFromLayout);

		ResourceManager.addComponent(content3Layout, chequeNoLayout);
		ResourceManager.addComponent(content3Layout, dueDateLayout);

		ResourceManager.addComponent(content4Layout, salesManLayout);
		ResourceManager.addComponent(content4Layout, invoiceTypeLayout);

		ResourceManager.addComponent(content5Layout, codeLayout);
		ResourceManager.addComponent(content5Layout, costCentLayout);

		ResourceManager.addComponent(content6Layout, fcCodeLayout);
		ResourceManager.addComponent(content6Layout, fcRateLayout);

		ResourceManager.addComponent(content7Layout, cAmtLayout);
		ResourceManager.addComponent(content7Layout, acBalLayout);

		ResourceManager.addComponent(noLayout, lblNo);
		ResourceManager.addComponent(noLayout, txtNo);

		ResourceManager.addComponent(bSerialLayout, lblBSerial);
		ResourceManager.addComponent(bSerialLayout, txtBSerial);

		ResourceManager.addComponent(dateLayout, lblDate);
		ResourceManager.addComponent(dateLayout, txtDate);

		ResourceManager.addComponent(descrLayout, lblDescr);
		ResourceManager.addComponent(descrLayout, txtDescr);

		ResourceManager.addComponent(rFromLayout, lblRFrom);
		ResourceManager.addComponent(rFromLayout, txtReceivedFrom);

		ResourceManager.addComponent(chequeNoLayout, lblChNo);
		ResourceManager.addComponent(chequeNoLayout, txtChNo);

		ResourceManager.addComponent(dueDateLayout, lblDueDate);
		ResourceManager.addComponent(dueDateLayout, txtDueDate);

		ResourceManager.addComponent(salesManLayout, lblSalesMan);
		ResourceManager.addComponent(salesManLayout, txtSalesMan);

		ResourceManager.addComponent(invoiceTypeLayout, lblCollector);
		ResourceManager.addComponent(invoiceTypeLayout, txtCollector);

		ResourceManager.addComponent(codeLayout, lblCode);
		ResourceManager.addComponent(codeLayout, txtCode);
		ResourceManager.addComponent(codeLayout, cmdCode);
		ResourceManager.addComponent(codeLayout, cmdClearCode);
		ResourceManager.addComponent(codeLayout, txtCodeName);

		ResourceManager.addComponent(costCentLayout, lblCostCent);
		ResourceManager.addComponent(costCentLayout, txtCostCent);
		ResourceManager.addComponent(costCentLayout, cmdCostCent);
		ResourceManager.addComponent(costCentLayout, cmdClearCostCent);
		ResourceManager.addComponent(costCentLayout, txtCostName);

		ResourceManager.addComponent(fcCodeLayout, lblFC);
		ResourceManager.addComponent(fcCodeLayout, txtFCCode);

		ResourceManager.addComponent(fcRateLayout, lblRate);
		ResourceManager.addComponent(fcRateLayout, txtRate);

		ResourceManager.addComponent(cAmtLayout, lblCAmt);
		ResourceManager.addComponent(cAmtLayout, txtCAMT);

		ResourceManager.addComponent(acBalLayout, lblAcBal);
		ResourceManager.addComponent(acBalLayout, txtACBal);

		noLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);

		bSerialLayout
				.setComponentAlignment(lblBSerial, Alignment.BOTTOM_CENTER);

		dateLayout.setComponentAlignment(lblDate, Alignment.BOTTOM_CENTER);

		descrLayout.setComponentAlignment(lblDescr, Alignment.BOTTOM_CENTER);

		rFromLayout.setComponentAlignment(lblRFrom, Alignment.BOTTOM_CENTER);

		chequeNoLayout.setComponentAlignment(lblChNo, Alignment.BOTTOM_CENTER);

		dueDateLayout
				.setComponentAlignment(lblDueDate, Alignment.BOTTOM_CENTER);

		salesManLayout.setComponentAlignment(lblSalesMan,
				Alignment.BOTTOM_CENTER);

		invoiceTypeLayout.setComponentAlignment(lblCollector,
				Alignment.BOTTOM_CENTER);

		codeLayout.setComponentAlignment(lblCode, Alignment.BOTTOM_CENTER);
		codeLayout.setComponentAlignment(cmdCode, Alignment.BOTTOM_CENTER);
		codeLayout.setComponentAlignment(cmdClearCode, Alignment.BOTTOM_CENTER);

		costCentLayout.setComponentAlignment(lblCostCent,
				Alignment.BOTTOM_CENTER);
		costCentLayout.setComponentAlignment(cmdCostCent,
				Alignment.BOTTOM_CENTER);
		costCentLayout.setComponentAlignment(cmdClearCostCent,
				Alignment.BOTTOM_CENTER);

		fcCodeLayout.setComponentAlignment(lblFC, Alignment.BOTTOM_CENTER);

		fcRateLayout.setComponentAlignment(lblRate, Alignment.BOTTOM_CENTER);

		cAmtLayout.setComponentAlignment(lblCAmt, Alignment.BOTTOM_CENTER);

		acBalLayout.setComponentAlignment(lblAcBal, Alignment.BOTTOM_CENTER);

		content1Layout.setExpandRatio(noLayout, 1.3f);
		content1Layout.setExpandRatio(bSerialLayout, 1.3f);
		content1Layout.setExpandRatio(dateLayout, 1.4f);

		content2Layout.setExpandRatio(descrLayout, 2f);
		content2Layout.setExpandRatio(rFromLayout, 2f);

		content3Layout.setExpandRatio(chequeNoLayout, 2f);
		content3Layout.setExpandRatio(dueDateLayout, 2f);

		content4Layout.setExpandRatio(salesManLayout, 2f);
		content4Layout.setExpandRatio(invoiceTypeLayout, 2f);

		content5Layout.setExpandRatio(codeLayout, 2f);
		content5Layout.setExpandRatio(costCentLayout, 2f);

		content6Layout.setExpandRatio(fcCodeLayout, 2f);
		content6Layout.setExpandRatio(fcRateLayout, 2f);

		content7Layout.setExpandRatio(cAmtLayout, 2f);
		content7Layout.setExpandRatio(acBalLayout, 2f);

		noLayout.setExpandRatio(lblNo, 0.1f);
		noLayout.setExpandRatio(txtNo, 3.9f);

		bSerialLayout.setExpandRatio(lblBSerial, 0.1f);
		bSerialLayout.setExpandRatio(txtBSerial, 3.9f);

		dateLayout.setExpandRatio(lblDate, 0.1f);
		dateLayout.setExpandRatio(txtDate, 3.9f);

		descrLayout.setExpandRatio(lblDescr, 0.1f);
		descrLayout.setExpandRatio(txtDescr, 3.9f);

		rFromLayout.setExpandRatio(lblRFrom, 0.1f);
		rFromLayout.setExpandRatio(txtReceivedFrom, 3.9f);

		chequeNoLayout.setExpandRatio(lblChNo, 0.1f);
		chequeNoLayout.setExpandRatio(txtChNo, 3.9f);

		dueDateLayout.setExpandRatio(lblDueDate, 0.1f);
		dueDateLayout.setExpandRatio(txtDueDate, 3.9f);

		salesManLayout.setExpandRatio(lblSalesMan, 0.1f);
		salesManLayout.setExpandRatio(txtSalesMan, 3.9f);

		invoiceTypeLayout.setExpandRatio(lblCollector, 0.1f);
		invoiceTypeLayout.setExpandRatio(txtCollector, 3.9f);

		codeLayout.setExpandRatio(lblCode, 0.1f);
		codeLayout.setExpandRatio(txtCode, 1f);
		codeLayout.setExpandRatio(cmdCode, 0.1f);
		codeLayout.setExpandRatio(cmdClearCode, 0.1f);
		codeLayout.setExpandRatio(txtCodeName, 2.7f);

		costCentLayout.setExpandRatio(lblCostCent, 0.1f);
		costCentLayout.setExpandRatio(txtCostCent, 1f);
		costCentLayout.setExpandRatio(cmdCostCent, 0.1f);
		costCentLayout.setExpandRatio(cmdClearCostCent, 0.1f);
		costCentLayout.setExpandRatio(txtCostName, 2.7f);

		fcCodeLayout.setExpandRatio(lblFC, 0.1f);
		fcCodeLayout.setExpandRatio(txtFCCode, 3.9f);

		fcRateLayout.setExpandRatio(lblRate, 0.1f);
		fcRateLayout.setExpandRatio(txtRate, 3.9f);

		cAmtLayout.setExpandRatio(lblCAmt, 0.1f);
		cAmtLayout.setExpandRatio(txtCAMT, 3.9f);

		acBalLayout.setExpandRatio(lblAcBal, 0.1f);
		acBalLayout.setExpandRatio(txtACBal, 3.9f);

		applyColumnsOnBranch();

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
			cmdClearCode.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					utilsVaadin.setValueByForce(txtCode, "");
					utilsVaadin.setValueByForce(txtCodeName, "");
				}
			});
			cmdClearCostCent.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					utilsVaadin.setValueByForce(txtCostCent, "");
					utilsVaadin.setValueByForce(txtCostName, "");
				}
			});
			cmdCode.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_CodeList();
				}
			});
			cmdCostCent.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_CostCentList();
				}
			});
			listnerAdded = true;
		}
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractOrderedLayout) parentLayout;
	}

	public void showInitView() {
		QRYSES = "";
		initForm();
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
											QRYSES = tv
													.getData()
													.getFieldValue(rn, "KEYFLD")
													.toString();
											load_data();
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select no,descr, to_char(vou_date,'dd/mm/rrrr') vou_date,"
									+ "keyfld from acvoucher1 "
									+ "where vou_code=2 and type=1 order by vou_date desc,no desc",
							true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_CodeList() {
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
							txtCodeName.setValue(tv.getData().getFieldValue(rn,
									"title").toString());
							txtCode.setValue(tv.getData().getFieldValue(rn,
									"code").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "Select code, title from accostcent1 order by path", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_CostCentList() {
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
							utilsVaadin.setValueByForce(txtCostName, tv
									.getData().getFieldValue(rn, "title")
									.toString());
							utilsVaadin.setValueByForce(txtCostCent, tv
									.getData().getFieldValue(rn, "CODE")
									.toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "Select code,title from accostcent1 order by path", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

}
