
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

public class frmAcPayVou implements transactionalForm {
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
	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout noLayout = new HorizontalLayout();
	private HorizontalLayout dateLayout = new HorizontalLayout();
	private HorizontalLayout salesManLayout = new HorizontalLayout();
	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout descrLayout = new HorizontalLayout();
	private HorizontalLayout rFromLayout = new HorizontalLayout();
	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout bankCashLayout = new HorizontalLayout();
	private HorizontalLayout costCentLayout = new HorizontalLayout();
	private HorizontalLayout content5Layout = new HorizontalLayout();
	private HorizontalLayout fcCodeLayout = new HorizontalLayout();
	private HorizontalLayout fcRateLayout = new HorizontalLayout();
	private HorizontalLayout content6Layout = new HorizontalLayout();
	private HorizontalLayout dAmtLayout = new HorizontalLayout();
	private HorizontalLayout acBalLayout = new HorizontalLayout();

	private Label lblNo = new Label("No");
	private Label lblDate = new Label("Date");
	private Label lblSaleMan = new Label("Sales Man");
	private Label lblDescr = new Label("Descr");
	private Label lblRFrom = new Label("Rcv From");
	private Label lblBankCash = new Label("Bank/Cash");
	private Label lblCostCent = new Label("Cost Cent");
	private Label lblFC = new Label("F.C.");
	private Label lblRate = new Label("Rate");
	private Label lblDAmt = new Label("DAmt");
	private Label lblAcBal = new Label("Acc Bal");

	private TextField txtNo = ControlsFactory.CreateTextField(null, "No",
			lstfldinfo);
	private DateField txtDate = ControlsFactory.CreateDateField(null,
			"VOU_DATE", lstfldinfo);
	private ComboBox txtSalesMan = ControlsFactory.CreateListField(null,
			"SLSMN", "Select no,name from salesp where type='S' order by no",
			lstfldinfo);
	private TextField txtDescr = ControlsFactory.CreateTextField(null, "DESCR",
			lstfldinfo);
	private TextField txtRFrom = ControlsFactory.CreateTextField(null,
			"RCVFROM", lstfldinfo);
	private TextField txtBankCash = ControlsFactory.CreateTextField(null,
			"CODE", lstfldinfo);
	private TextField txtBankCashName = ControlsFactory.CreateTextField(null,
			"", null);
	private TextField txtCostCent = ControlsFactory.CreateTextField(null,
			"COSTCENT", lstfldinfo);
	private TextField txtCostCentName = ControlsFactory.CreateTextField(null,
			"", null);
	private TextField txtFCCode = ControlsFactory.CreateTextField(null,
			"FCCODE", lstfldinfo);
	private TextField txtFCRate = ControlsFactory.CreateTextField(null,
			"FCRATE", lstfldinfo);
	private TextField txtDAmt = ControlsFactory.CreateTextField(null, "", null);
	private TextField txtAcBal = ControlsFactory
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
						"Select * from acvoucher1 where keyfld='" + QRYSES
								+ "'", ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
			}
			fetchAccountInfo();
			table.data
					.executeQuery(
							" Select  acvoucher2.*,accostcent1.title csname  from  "
									+ "acvoucher2,accostcent1 where Acvoucher2.costcent=accostcent1.code(+) "
									+ "and acvoucher2.keyfld='" + QRYSES
									+ "' order by pos", true);
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
			utilsVaadin.applyColumns("FRMACPAYVOU.ACVOUCHER2",
					table.getTable(), table.listFields, con);
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
		mainLayout.setWidth("700px");
		basicLayout.setWidth("100%");
		content1Layout.setWidth("100%");
		content2Layout.setWidth("100%");
		noLayout.setWidth("100%");
		dateLayout.setWidth("100%");
		content3Layout.setWidth("100%");
		descrLayout.setWidth("100%");
		rFromLayout.setWidth("100%");
		content4Layout.setWidth("100%");
		content5Layout.setWidth("100%");
		bankCashLayout.setWidth("100%");
		costCentLayout.setWidth("100%");
		content6Layout.setWidth("100%");
		fcCodeLayout.setWidth("100%");
		fcRateLayout.setWidth("100%");
		salesManLayout.setWidth("100%");
		acBalLayout.setWidth("100%");
		dAmtLayout.setWidth("100%");

		lblNo.setWidth("75px");
		lblDate.setWidth("60px");
		lblDescr.setWidth("70px");
		lblRFrom.setWidth("70px");
		lblBankCash.setWidth("73px");
		lblCostCent.setWidth("70px");
		lblRate.setWidth("70px");
		lblAcBal.setWidth("70px");
		lblFC.setWidth("70px");
		lblSaleMan.setWidth("70px");
		lblDAmt.setWidth("70px");

		txtNo.setWidth("100%");
		txtDate.setWidth("100%");
		txtDescr.setWidth("100%");
		txtRFrom.setWidth("100%");
		txtBankCash.setWidth("100%");
		txtBankCashName.setWidth("100%");
		txtCostCent.setWidth("100%");
		txtCostCentName.setWidth("100%");
		txtFCCode.setWidth("100%");
		txtAcBal.setWidth("100%");
		txtFCRate.setWidth("100%");
		txtSalesMan.setWidth("100%");
		txtDAmt.setWidth("100%");

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
		ResourceManager.addComponent(basicLayout, content3Layout);
		ResourceManager.addComponent(basicLayout, content4Layout);
		ResourceManager.addComponent(basicLayout, content5Layout);
		ResourceManager.addComponent(basicLayout, tableLayout);
		ResourceManager.addComponent(basicLayout, content6Layout);

		ResourceManager.addComponent(content1Layout, content2Layout);
		ResourceManager.addComponent(content1Layout, salesManLayout);

		ResourceManager.addComponent(content2Layout, noLayout);
		ResourceManager.addComponent(content2Layout, dateLayout);

		ResourceManager.addComponent(content3Layout, descrLayout);
		ResourceManager.addComponent(content3Layout, rFromLayout);

		ResourceManager.addComponent(content4Layout, bankCashLayout);
		ResourceManager.addComponent(content4Layout, costCentLayout);

		ResourceManager.addComponent(content5Layout, fcCodeLayout);
		ResourceManager.addComponent(content5Layout, fcRateLayout);

		ResourceManager.addComponent(content6Layout, dAmtLayout);
		ResourceManager.addComponent(content6Layout, acBalLayout);

		ResourceManager.addComponent(noLayout, lblNo);
		ResourceManager.addComponent(noLayout, txtNo);

		ResourceManager.addComponent(dateLayout, lblDate);
		ResourceManager.addComponent(dateLayout, txtDate);

		ResourceManager.addComponent(salesManLayout, lblSaleMan);
		ResourceManager.addComponent(salesManLayout, txtSalesMan);

		ResourceManager.addComponent(descrLayout, lblDescr);
		ResourceManager.addComponent(descrLayout, txtDescr);

		ResourceManager.addComponent(rFromLayout, lblRFrom);
		ResourceManager.addComponent(rFromLayout, txtRFrom);

		ResourceManager.addComponent(bankCashLayout, lblBankCash);
		ResourceManager.addComponent(bankCashLayout, txtBankCash);
		ResourceManager.addComponent(bankCashLayout, cmdCode);
		ResourceManager.addComponent(bankCashLayout, cmdClearCode);
		ResourceManager.addComponent(bankCashLayout, txtBankCashName);

		ResourceManager.addComponent(costCentLayout, lblCostCent);
		ResourceManager.addComponent(costCentLayout, txtCostCent);
		ResourceManager.addComponent(costCentLayout, cmdCostCent);
		ResourceManager.addComponent(costCentLayout, cmdClearCostCent);
		ResourceManager.addComponent(costCentLayout, txtCostCentName);

		ResourceManager.addComponent(fcCodeLayout, lblFC);
		ResourceManager.addComponent(fcCodeLayout, txtFCCode);

		ResourceManager.addComponent(fcRateLayout, lblRate);
		ResourceManager.addComponent(fcRateLayout, txtFCRate);

		ResourceManager.addComponent(dAmtLayout, lblDAmt);
		ResourceManager.addComponent(dAmtLayout, txtDAmt);

		ResourceManager.addComponent(acBalLayout, lblAcBal);
		ResourceManager.addComponent(acBalLayout, txtAcBal);

		noLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);

		dateLayout.setComponentAlignment(lblDate, Alignment.BOTTOM_CENTER);

		descrLayout.setComponentAlignment(lblDescr, Alignment.BOTTOM_CENTER);

		rFromLayout.setComponentAlignment(lblRFrom, Alignment.BOTTOM_CENTER);

		salesManLayout.setComponentAlignment(lblSaleMan,
				Alignment.BOTTOM_CENTER);

		bankCashLayout.setComponentAlignment(lblBankCash,
				Alignment.BOTTOM_CENTER);
		bankCashLayout.setComponentAlignment(cmdCode, Alignment.BOTTOM_CENTER);
		bankCashLayout.setComponentAlignment(cmdClearCode,
				Alignment.BOTTOM_CENTER);

		costCentLayout.setComponentAlignment(lblCostCent,
				Alignment.BOTTOM_CENTER);
		costCentLayout.setComponentAlignment(cmdCostCent,
				Alignment.BOTTOM_CENTER);
		costCentLayout.setComponentAlignment(cmdClearCostCent,
				Alignment.BOTTOM_CENTER);

		fcCodeLayout.setComponentAlignment(lblFC, Alignment.BOTTOM_CENTER);

		fcRateLayout.setComponentAlignment(lblRate, Alignment.BOTTOM_CENTER);

		dAmtLayout.setComponentAlignment(lblDAmt, Alignment.BOTTOM_CENTER);

		acBalLayout.setComponentAlignment(lblAcBal, Alignment.BOTTOM_CENTER);

		content1Layout.setExpandRatio(content2Layout, 2f);
		content1Layout.setExpandRatio(salesManLayout, 2f);

		content3Layout.setExpandRatio(descrLayout, 2f);
		content3Layout.setExpandRatio(rFromLayout, 2f);

		content4Layout.setExpandRatio(bankCashLayout, 2f);
		content4Layout.setExpandRatio(costCentLayout, 2f);

		content5Layout.setExpandRatio(fcCodeLayout, 2f);
		content5Layout.setExpandRatio(fcRateLayout, 2f);

		content6Layout.setExpandRatio(dAmtLayout, 2f);
		content6Layout.setExpandRatio(acBalLayout, 2f);

		noLayout.setExpandRatio(lblNo, 0.1f);
		noLayout.setExpandRatio(txtNo, 3.9f);

		dateLayout.setExpandRatio(lblDate, 0.1f);
		dateLayout.setExpandRatio(txtDate, 3.9f);

		descrLayout.setExpandRatio(lblDescr, 0.1f);
		descrLayout.setExpandRatio(txtDescr, 3.9f);

		rFromLayout.setExpandRatio(lblRFrom, 0.1f);
		rFromLayout.setExpandRatio(txtRFrom, 3.9f);

		salesManLayout.setExpandRatio(lblSaleMan, 0.1f);
		salesManLayout.setExpandRatio(txtSalesMan, 3.9f);

		bankCashLayout.setExpandRatio(lblBankCash, 0.1f);
		bankCashLayout.setExpandRatio(txtBankCash, 1f);
		bankCashLayout.setExpandRatio(cmdCode, 0.1f);
		bankCashLayout.setExpandRatio(cmdClearCode, 0.1f);
		bankCashLayout.setExpandRatio(txtBankCashName, 2.7f);

		costCentLayout.setExpandRatio(lblCostCent, 0.1f);
		costCentLayout.setExpandRatio(txtCostCent, 1f);
		costCentLayout.setExpandRatio(cmdCostCent, 0.1f);
		costCentLayout.setExpandRatio(cmdClearCostCent, 0.1f);
		costCentLayout.setExpandRatio(txtCostCentName, 2.7f);

		fcCodeLayout.setExpandRatio(lblFC, 0.1f);
		fcCodeLayout.setExpandRatio(txtFCCode, 3.9f);

		fcRateLayout.setExpandRatio(lblRate, 0.1f);
		fcRateLayout.setExpandRatio(txtFCRate, 3.9f);

		dAmtLayout.setExpandRatio(lblDAmt, 0.1f);
		dAmtLayout.setExpandRatio(txtDAmt, 3.9f);

		acBalLayout.setExpandRatio(lblAcBal, 0.1f);
		acBalLayout.setExpandRatio(txtAcBal, 3.9f);

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
					txtBankCash.setReadOnly(false);
					txtBankCashName.setReadOnly(false);
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
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		buttonLayout.removeAllComponents();
		content1Layout.removeAllComponents();
		content2Layout.removeAllComponents();
		noLayout.removeAllComponents();
		dateLayout.removeAllComponents();
		content3Layout.removeAllComponents();
		descrLayout.removeAllComponents();
		rFromLayout.removeAllComponents();
		content4Layout.removeAllComponents();
		content5Layout.removeAllComponents();
		bankCashLayout.removeAllComponents();
		costCentLayout.removeAllComponents();
		content6Layout.removeAllComponents();
		fcCodeLayout.removeAllComponents();
		fcRateLayout.removeAllComponents();
	}

	public void print_data() {

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
							"select no,descr, to_char(vou_date,'dd/mm/rrrr') vou_date,keyfld from acvoucher1 where vou_code=3 and type=2 order by vou_date desc , no desc",
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
											txtBankCashName.setValue(tv
													.getData().getFieldValue(
															rn, "name")
													.toString());
											txtBankCash.setValue(tv.getData()
													.getFieldValue(rn, "accno")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"Select accno,name from acaccount where actype=0 and isbankcash='Y' order by path",
							true);
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
			}, con, "Select code,title from accostcent1 order by path", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

}
