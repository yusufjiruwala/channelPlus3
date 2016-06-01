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

public class frmAcBkPayVou implements transactionalForm {
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
	private HorizontalLayout noLayout = new HorizontalLayout();
	private HorizontalLayout dateLayout = new HorizontalLayout();
	private HorizontalLayout chequeNoLayout = new HorizontalLayout();
	private HorizontalLayout dueDateLayout = new HorizontalLayout();
	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout descrLayout = new HorizontalLayout();
	private HorizontalLayout bankCashLayout = new HorizontalLayout();
	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout payLayout = new HorizontalLayout();
	private HorizontalLayout salesManLayout = new HorizontalLayout();
	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout costCentLayout = new HorizontalLayout();
	private HorizontalLayout fcLayout = new HorizontalLayout();
	private HorizontalLayout rateLayout = new HorizontalLayout();

	private HorizontalLayout content5Layout = new HorizontalLayout();
	private HorizontalLayout dAmtLayout = new HorizontalLayout();
	private HorizontalLayout outStandLayout = new HorizontalLayout();

	private HorizontalLayout content6Layout = new HorizontalLayout();
	private HorizontalLayout content7Layout = new HorizontalLayout();
	private HorizontalLayout content8Layout = new HorizontalLayout();

	private Label lblNo = new Label("No");
	private Label lblDate = new Label("Date");
	private Label lblChNo = new Label("Cheque No");
	private Label lblDueDate = new Label("Due Date");
	private Label lblDescr = new Label("Descr");
	private Label lblBankCash = new Label("Bank/Cash");
	private Label lblPay = new Label("Pay To");
	private Label lblSalesMan = new Label("Sales Man");
	private Label lblCostCent = new Label("Cost Center");
	private Label lblFC = new Label("F.C.");
	private Label lblRate = new Label("Rate");
	private Label lblDAmt = new Label("DAmt");
	private Label lblOutStand = new Label("Out Stand");

	private TextField txtNo = ControlsFactory.CreateTextField(null, "NO",
			lstfldinfo);
	private DateField txtDate = ControlsFactory.CreateDateField(null,
			"VOU_DATE", lstfldinfo);
	private TextField txtChNo = ControlsFactory.CreateTextField(null,
			"CHEQUENO", lstfldinfo);
	private DateField txtDueDate = ControlsFactory.CreateDateField(null,
			"DUEDATE", lstfldinfo);
	private TextField txtDescr = ControlsFactory.CreateTextField(null, "DESCR",
			lstfldinfo);
	private TextField txtBankCash = ControlsFactory.CreateTextField(null,
			"CODE", lstfldinfo);
	private TextField txtBankCashName = ControlsFactory.CreateTextField(null,
			"", null);
	private TextField txtPay = ControlsFactory.CreateTextField(null, "RCVFROM",
			lstfldinfo);
	private ComboBox txtSalesMan = ControlsFactory.CreateListField(null,
			"SLSMN", "Select no,name from salesp where type='S' order by no",
			lstfldinfo);
	private TextField txtCostCent = ControlsFactory.CreateTextField(null,
			"COSTCENT", lstfldinfo);
	private TextField txtCostCentName = ControlsFactory.CreateTextField(null,
			"", null);
	private TextField txtFC = ControlsFactory.CreateTextField(null, "FCCODE",
			lstfldinfo);
	private TextField txtRate = ControlsFactory.CreateTextField(null, "FCRATE",
			lstfldinfo);
	private TextField txtDAMT = ControlsFactory.CreateTextField(null, "", null);
	private TextField txtOutStand = ControlsFactory.CreateTextField(null, "",
			null);

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

	public void load_data() {
		try {
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) {
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
							"Select  acvoucher2.*,accostcent1.title csname  from  "
									+ "acvoucher2,accostcent1 where Acvoucher2.costcent=accostcent1.code(+) and "
									+ "  acvoucher2.keyfld='" + QRYSES
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
		txtBankCashName.setReadOnly(false);
		txtBankCashName.setValue(utils.getSqlValue(
				"Select name from acaccount where accno  = '"
						+ txtBankCash.getValue().toString() + "'", con));

		txtCostCentName.setValue(utils.getSqlValue(
				"Select title from accostcent1 where code = '"
						+ txtCostCent.getValue().toString() + "'", con));
	}

	public void applyColumnsOnBranch() {
		lstBranchCols.clear();
		try {
			utilsVaadin.applyColumns("FRMACBKPAYVOU.ACVOUCHER2", table
					.getTable(), table.listFields, con);

		} catch (SQLException e) {
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

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();

		mainLayout.setWidth("700px");
		basicLayout.setWidth("100%");
		content1Layout.setWidth("100%");
		noLayout.setWidth("100%");
		dateLayout.setWidth("100%");
		chequeNoLayout.setWidth("100%");
		dueDateLayout.setWidth("100%");
		content2Layout.setWidth("100%");
		descrLayout.setWidth("100%");
		bankCashLayout.setWidth("100%");
		content3Layout.setWidth("100%");
		payLayout.setWidth("100%");
		salesManLayout.setWidth("100%");
		content4Layout.setWidth("100%");
		costCentLayout.setWidth("100%");
		fcLayout.setWidth("100%");
		rateLayout.setWidth("100%");
		content5Layout.setWidth("100%");
		dAmtLayout.setWidth("100%");
		outStandLayout.setWidth("100%");

		content6Layout.setWidth("100%");
		content7Layout.setWidth("100%");
		content8Layout.setWidth("100%");

		txtNo.setWidth("100%");
		txtDate.setWidth("100%");
		txtChNo.setWidth("100%");
		txtDueDate.setWidth("100%");
		txtDescr.setWidth("100%");
		txtBankCash.setWidth("100%");
		txtBankCashName.setWidth("100%");
		txtPay.setWidth("100%");
		txtSalesMan.setWidth("100%");
		txtCostCent.setWidth("100%");
		txtCostCentName.setWidth("100%");
		txtFC.setWidth("100%");
		txtRate.setWidth("100%");
		txtDAMT.setWidth("100%");
		txtOutStand.setWidth("100%");

		lblNo.setWidth("72px");
		lblDate.setWidth("70px");
		lblChNo.setWidth("70px");
		lblDueDate.setWidth("70px");
		lblDescr.setWidth("68px");
		lblBankCash.setWidth("68px");
		lblPay.setWidth("68px");
		lblSalesMan.setWidth("68px");
		lblCostCent.setWidth("70px");
		lblFC.setWidth("70px");
		lblRate.setWidth("70px");
		lblDAmt.setWidth("70px");
		lblOutStand.setWidth("70px");

		tableLayout.setHeight("275px");
		table.getTable().setHeight("250px");

		basicLayout.addStyleName("formLayout");

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);

		ResourceManager.addComponent(buttonLayout, cmdCls);
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
		ResourceManager.addComponent(basicLayout, tableLayout);
		ResourceManager.addComponent(basicLayout, content5Layout);

		ResourceManager.addComponent(content1Layout, content6Layout);
		ResourceManager.addComponent(content1Layout, content7Layout);

		ResourceManager.addComponent(content6Layout, noLayout);
		ResourceManager.addComponent(content6Layout, dateLayout);

		ResourceManager.addComponent(content7Layout, chequeNoLayout);
		ResourceManager.addComponent(content7Layout, dueDateLayout);

		ResourceManager.addComponent(content2Layout, descrLayout);
		ResourceManager.addComponent(content2Layout, bankCashLayout);

		ResourceManager.addComponent(content3Layout, payLayout);
		ResourceManager.addComponent(content3Layout, salesManLayout);

		ResourceManager.addComponent(content4Layout, costCentLayout);
		ResourceManager.addComponent(content4Layout, content8Layout);

		ResourceManager.addComponent(content8Layout, fcLayout);
		ResourceManager.addComponent(content8Layout, rateLayout);

		ResourceManager.addComponent(content5Layout, dAmtLayout);
		ResourceManager.addComponent(content5Layout, outStandLayout);

		ResourceManager.addComponent(noLayout, lblNo);
		ResourceManager.addComponent(noLayout, txtNo);

		ResourceManager.addComponent(dateLayout, lblDate);
		ResourceManager.addComponent(dateLayout, txtDate);

		ResourceManager.addComponent(chequeNoLayout, lblChNo);
		ResourceManager.addComponent(chequeNoLayout, txtChNo);

		ResourceManager.addComponent(dueDateLayout, lblDueDate);
		ResourceManager.addComponent(dueDateLayout, txtDueDate);

		ResourceManager.addComponent(descrLayout, lblDescr);
		ResourceManager.addComponent(descrLayout, txtDescr);

		ResourceManager.addComponent(bankCashLayout, lblBankCash);
		ResourceManager.addComponent(bankCashLayout, txtBankCash);
		ResourceManager.addComponent(bankCashLayout, cmdCode);
		ResourceManager.addComponent(bankCashLayout, cmdClearCode);
		ResourceManager.addComponent(bankCashLayout, txtBankCashName);

		ResourceManager.addComponent(payLayout, lblPay);
		ResourceManager.addComponent(payLayout, txtPay);

		ResourceManager.addComponent(salesManLayout, lblSalesMan);
		ResourceManager.addComponent(salesManLayout, txtSalesMan);

		ResourceManager.addComponent(costCentLayout, lblCostCent);
		ResourceManager.addComponent(costCentLayout, txtCostCent);
		ResourceManager.addComponent(costCentLayout, cmdCostCent);
		ResourceManager.addComponent(costCentLayout, cmdClearCostCent);
		ResourceManager.addComponent(costCentLayout, txtCostCentName);

		ResourceManager.addComponent(fcLayout, lblFC);
		ResourceManager.addComponent(fcLayout, txtFC);

		ResourceManager.addComponent(rateLayout, lblRate);
		ResourceManager.addComponent(rateLayout, txtRate);

		ResourceManager.addComponent(dAmtLayout, lblDAmt);
		ResourceManager.addComponent(dAmtLayout, txtDAMT);

		ResourceManager.addComponent(outStandLayout, lblOutStand);
		ResourceManager.addComponent(outStandLayout, txtOutStand);

		noLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);

		dateLayout.setComponentAlignment(lblDate, Alignment.BOTTOM_CENTER);

		chequeNoLayout.setComponentAlignment(lblChNo, Alignment.BOTTOM_CENTER);

		dueDateLayout
				.setComponentAlignment(lblDueDate, Alignment.BOTTOM_CENTER);

		descrLayout.setComponentAlignment(lblDescr, Alignment.BOTTOM_CENTER);

		bankCashLayout.setComponentAlignment(lblBankCash,
				Alignment.BOTTOM_CENTER);
		bankCashLayout.setComponentAlignment(cmdCode, Alignment.BOTTOM_CENTER);
		bankCashLayout.setComponentAlignment(cmdClearCode,
				Alignment.BOTTOM_CENTER);

		payLayout.setComponentAlignment(lblPay, Alignment.BOTTOM_CENTER);

		content3Layout.setComponentAlignment(salesManLayout,
				Alignment.BOTTOM_CENTER);
		salesManLayout.setComponentAlignment(lblSalesMan,
				Alignment.BOTTOM_CENTER);
		salesManLayout.setComponentAlignment(txtSalesMan,
				Alignment.MIDDLE_CENTER);

		costCentLayout.setComponentAlignment(lblCostCent,
				Alignment.BOTTOM_CENTER);
		costCentLayout.setComponentAlignment(cmdCostCent,
				Alignment.BOTTOM_CENTER);
		costCentLayout.setComponentAlignment(cmdClearCostCent,
				Alignment.BOTTOM_CENTER);

		fcLayout.setComponentAlignment(lblFC, Alignment.BOTTOM_CENTER);

		rateLayout.setComponentAlignment(lblRate, Alignment.BOTTOM_CENTER);

		dAmtLayout.setComponentAlignment(lblDAmt, Alignment.BOTTOM_CENTER);

		outStandLayout.setComponentAlignment(lblOutStand,
				Alignment.BOTTOM_CENTER);

		content1Layout.setExpandRatio(content6Layout, 2f);
		content1Layout.setExpandRatio(content7Layout, 2f);

		content6Layout.setExpandRatio(noLayout, 2f);
		content6Layout.setExpandRatio(dateLayout, 2f);

		content7Layout.setExpandRatio(chequeNoLayout, 2f);
		content7Layout.setExpandRatio(dueDateLayout, 2f);

		content2Layout.setExpandRatio(descrLayout, 2f);
		content2Layout.setExpandRatio(bankCashLayout, 2f);

		content3Layout.setExpandRatio(payLayout, 2f);
		content3Layout.setExpandRatio(salesManLayout, 2f);

		content4Layout.setExpandRatio(costCentLayout, 2f);
		content4Layout.setExpandRatio(content8Layout, 2f);

		content8Layout.setExpandRatio(fcLayout, 2f);
		content8Layout.setExpandRatio(rateLayout, 2f);

		content5Layout.setExpandRatio(dAmtLayout, 2f);
		content5Layout.setExpandRatio(outStandLayout, 2f);

		noLayout.setExpandRatio(lblNo, 0.1f);
		noLayout.setExpandRatio(txtNo, 3.9f);

		dateLayout.setExpandRatio(lblDate, 0.1f);
		dateLayout.setExpandRatio(txtDate, 3.9f);

		chequeNoLayout.setExpandRatio(lblChNo, 0.1f);
		chequeNoLayout.setExpandRatio(txtChNo, 3.9f);

		dueDateLayout.setExpandRatio(lblDueDate, 0.1f);
		dueDateLayout.setExpandRatio(txtDueDate, 3.9f);

		descrLayout.setExpandRatio(lblDescr, 0.1f);
		descrLayout.setExpandRatio(txtDescr, 3.9f);

		bankCashLayout.setExpandRatio(lblBankCash, 0.1f);
		bankCashLayout.setExpandRatio(txtBankCash, 1f);
		bankCashLayout.setExpandRatio(cmdCode, 0.1f);
		bankCashLayout.setExpandRatio(cmdClearCode, 0.1f);
		bankCashLayout.setExpandRatio(txtBankCashName, 2.7f);

		payLayout.setExpandRatio(lblPay, 0.1f);
		payLayout.setExpandRatio(txtPay, 3.9f);

		salesManLayout.setExpandRatio(lblSalesMan, 0.1f);
		salesManLayout.setExpandRatio(txtSalesMan, 3.9f);

		costCentLayout.setExpandRatio(lblCostCent, 0.1f);
		costCentLayout.setExpandRatio(txtCostCent, 1f);
		costCentLayout.setExpandRatio(cmdCostCent, 0.1f);
		costCentLayout.setExpandRatio(cmdClearCostCent, 0.1f);
		costCentLayout.setExpandRatio(txtCostCentName, 2.7f);

		fcLayout.setExpandRatio(lblFC, 0.1f);
		fcLayout.setExpandRatio(txtFC, 3.9f);

		rateLayout.setExpandRatio(lblRate, 0.1f);
		rateLayout.setExpandRatio(txtRate, 3.9f);

		dAmtLayout.setExpandRatio(lblDAmt, 0.1f);
		dAmtLayout.setExpandRatio(txtDAMT, 3.9f);

		outStandLayout.setExpandRatio(lblOutStand, 0.1f);
		outStandLayout.setExpandRatio(txtOutStand, 3.9f);

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
					txtBankCash.setReadOnly(false);
					txtBankCashName.setReadOnly(false);
					txtBankCash.setValue("");
					txtBankCashName.setValue("");
					txtBankCash.setReadOnly(true);
					txtBankCashName.setReadOnly(true);
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
			cmdCode.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtBankCash.setReadOnly(false);
					txtBankCashName.setReadOnly(false);
					show_CodeList();
				}
			});
			cmdCostCent.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtCostCent.setReadOnly(false);
					txtCostCentName.setReadOnly(false);
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
													.getFieldValue(rn, "keyfld")
													.toString();
											load_data();
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select no,descr, vou_date,keyfld from acvoucher1"
									+ " where vou_code=3 and type=1 order by vou_date desc ,no desc",
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
							txtBankCashName.setValue(tv.getData()
									.getFieldValue(rn, "name").toString());
							txtBankCash.setValue(tv.getData().getFieldValue(rn,
									"accno").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "Select accno,name from acaccount "
					+ "where actype=0 and isbankcash='Y' order by path", true);
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
							txtCostCentName.setValue(tv.getData()
									.getFieldValue(rn, "title").toString());
							txtCostCent.setValue(tv.getData().getFieldValue(rn,
									"code").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "Select code,title from accostcent1", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
