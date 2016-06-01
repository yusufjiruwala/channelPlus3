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
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
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

public class frmQuotation implements transactionalForm {
	private AbstractLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	Tree tree = new Tree();
	private localTableModel data_Items = new localTableModel();

	private VerticalLayout tableLayout = new VerticalLayout();
	TableLayoutVaadin table = new TableLayoutVaadin(tableLayout);
	private final List<ColumnProperty> lstOrder2Cols = new ArrayList<ColumnProperty>();

	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout content1Layout = new HorizontalLayout();
	private HorizontalLayout noLayout = new HorizontalLayout();
	private HorizontalLayout dateLayout = new HorizontalLayout();
	private HorizontalLayout typeLayout = new HorizontalLayout();
	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout employeeLayout = new HorizontalLayout();
	private HorizontalLayout dueDateLayout = new HorizontalLayout();
	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout customerLayout = new HorizontalLayout();
	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout payItemLayout = new HorizontalLayout();
	private HorizontalLayout attentionLayout = new HorizontalLayout();
	private HorizontalLayout totalLayout = new HorizontalLayout();
	private HorizontalLayout content5Layout = new HorizontalLayout();
	private HorizontalLayout allQtyLayout = new HorizontalLayout();
	private HorizontalLayout balanceLayout = new HorizontalLayout();
	private HorizontalLayout packLayout = new HorizontalLayout();
	private HorizontalLayout content6Layout = new HorizontalLayout();
	private HorizontalLayout discAmtLayout = new HorizontalLayout();
	private HorizontalLayout qtyLayout = new HorizontalLayout();
	private HorizontalLayout amtLayout = new HorizontalLayout();

	private TextField txtNo = ControlsFactory.CreateTextField("", "ORD_NO",
			lstfldinfo);
	private DateField txtDate = ControlsFactory.CreateDateField("", "ORD_DATE",
			lstfldinfo);
	private TextField txtEmp = ControlsFactory.CreateTextField("", "ORD_EMPNO",
			lstfldinfo);
	private TextField txtEmpName = ControlsFactory
			.CreateTextField("", "", null);
	private ComboBox txtType = ControlsFactory.CreateListField("", "ORD_SHIP",
			"", lstfldinfo);
	private TextField txtCust = ControlsFactory.CreateTextField("", "ORD_REF",
			lstfldinfo);
	private TextField txtCustName = ControlsFactory.CreateTextField("", "",
			null);
	private DateField txtDueDate = ControlsFactory.CreateDateField("",
			"ORD_SHPDT", lstfldinfo);
	private TextField txtPayItem = ControlsFactory.CreateTextField("",
			"PAYTERM", lstfldinfo);
	private TextField txtAttention = ControlsFactory.CreateTextField("",
			"ATTN", lstfldinfo);
	private TextField txtTotal = ControlsFactory.CreateTextField("", "", null);
	private TextField txtAllQty = ControlsFactory.CreateTextField("", "", null);
	private TextField txtBalance = ControlsFactory
			.CreateTextField("", "", null);
	private TextField txtPack = ControlsFactory.CreateTextField("", "", null);
	private TextField txtDiscAmt = ControlsFactory.CreateTextField("",
			"ORD_DISCAMT", lstfldinfo);
	private TextField txtQty = ControlsFactory.CreateTextField("", "", null);
	private TextField txtAmt = ControlsFactory.CreateTextField("", "ORD_AMT",
			lstfldinfo);

	private Label lblNo = new Label("No");
	private Label lblDate = new Label("Date");
	private Label lblEmp = new Label("Employee");
	private Label lblType = new Label("Type");
	private Label lblCust = new Label("Customer");
	private Label lblDueDate = new Label("Due Date");
	private Label lblPayItem = new Label("Pay Item");
	private Label lblAttention = new Label("Attention");
	private Label lblTotal = new Label("Total");
	private Label lblAllQty = new Label("All Qty");
	private Label lblBalance = new Label("Balance");
	private Label lblPack = new Label("Pack");
	private Label lblDiscAmt = new Label("Disc Amt");
	private Label lblQty = new Label("Qty");
	private Label lblAmt = new Label("Amt");

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

	private NativeButton cmdEmp = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearEmp = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	private NativeButton cmdCust = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearCust = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");

	public int varOrdCode = 101;

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
			qe.setParaValue("PERIODCODE", "Y");
			qe.setParaValue("ORD_CODE", varOrdCode);
			qe.setParaValue("YEAR", "Y");
			qe.setParaValue("ORD_FLAG", "0");
			qe.setParaValue("DELIVEREDQTY", "0");
			qe.setParaValue("ORDERDQTY", "0");
			qe.setParaValue("LOCATION_CODE", "Y");
			qe.setParaValue("ORD_TYPE", "0");
			qe.setParaValue("DELIVERED_FREEQTY", "0");
			qe.setParaValue("HAVE_ADJUSTMENT", "0");
			qe.setParaValue("ADJUST_AMOUNT", "0");
			qe.setParaValue("ADJUST_CURRENCY", "0");
			qe.setParaValue("ADJUST_RATE", "0");

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("ORDER1"); // if
			} else {
				qe.AutoGenerateUpdateStatment("ORDER1", "'ORD_NO'",
						" WHERE ORD_NO=:ORD_NO"); // if to update..
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
						"Select * from order1  where ord_no='" + QRYSES
								+ "' and ord_code=" + varOrdCode
								+ " order by ord_no",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
			}
			fetchAccountInfo();
			applyColumns();
			table.data.executeQuery("select order2.*, 0 discp,0 amount,"
					+ "0 ord_balance_avail,0 ord_cost_price,0 ord_cost_amt"
					+ ",0 lsprice,0 lsamt from order2 where ord_no='" + QRYSES
					+ "' and ord_code=" + varOrdCode + " order by ord_pos",
					true);
			table.fill_table();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	protected void fetchAccountInfo() {
		txtEmpName.setReadOnly(false);
		txtEmpName.setValue(utils.getSqlValue(
				" Select name from salesp where no ='"
						+ txtEmp.getValue().toString() + "'", con));
		txtCustName.setReadOnly(false);
		txtCustName.setValue(utils.getSqlValue(
				"Select name  from c_ycust  where code = '"
						+ txtCust.getValue().toString() + "'", con));
	}

	public void applyColumns() {
		table.listFields.clear();
		try {
			utilsVaadin.applyColumns("FRMQUOTATION.ORDER2", table.getTable(),
					table.listFields, con);

		} catch (SQLException e) {
			e.printStackTrace();
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
		mainLayout.setWidth("600px");
		basicLayout.setWidth("100%");
		content1Layout.setWidth("100%");
		noLayout.setWidth("100%");
		dateLayout.setWidth("100%");
		typeLayout.setWidth("100%");
		content2Layout.setWidth("100%");
		employeeLayout.setWidth("100%");
		dueDateLayout.setWidth("100%");
		content3Layout.setWidth("100%");
		customerLayout.setWidth("100%");
		content4Layout.setWidth("100%");
		payItemLayout.setWidth("100%");
		attentionLayout.setWidth("100%");
		totalLayout.setWidth("100%");
		content5Layout.setWidth("100%");
		allQtyLayout.setWidth("100%");
		balanceLayout.setWidth("100%");
		packLayout.setWidth("100%");
		content6Layout.setWidth("100%");
		discAmtLayout.setWidth("100%");
		qtyLayout.setWidth("100%");
		amtLayout.setWidth("100%");

		txtNo.setWidth("100%");
		txtDate.setWidth("100%");
		txtEmp.setWidth("100%");
		txtEmpName.setWidth("100%");
		txtType.setWidth("100%");
		txtCust.setWidth("100%");
		txtCustName.setWidth("100%");
		txtDueDate.setWidth("100%");
		txtPayItem.setWidth("100%");
		txtAttention.setWidth("100%");
		txtTotal.setWidth("100%");
		txtAllQty.setWidth("100%");
		txtBalance.setWidth("100%");
		txtPack.setWidth("100%");
		txtDiscAmt.setWidth("100%");
		txtQty.setWidth("100%");
		txtAmt.setWidth("100%");

		lblNo.setWidth("60px");
		lblDate.setWidth("60px");
		lblEmp.setWidth("57px");
		lblType.setWidth("60px");
		lblCust.setWidth("52px");
		lblDueDate.setWidth("60px");
		lblPayItem.setWidth("60px");
		lblAttention.setWidth("60px");
		lblTotal.setWidth("60px");
		lblAllQty.setWidth("60px");
		lblBalance.setWidth("60px");
		lblPack.setWidth("60px");
		lblDiscAmt.setWidth("60px");
		lblQty.setWidth("60px");
		lblAmt.setWidth("60px");

		tableLayout.setHeight("220px");
		table.getTable().setHeight("200px");

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
		ResourceManager.addComponent(basicLayout, tableLayout);
		ResourceManager.addComponent(basicLayout, content5Layout);
		ResourceManager.addComponent(basicLayout, content6Layout);

		ResourceManager.addComponent(content1Layout, noLayout);
		ResourceManager.addComponent(content1Layout, dateLayout);
		ResourceManager.addComponent(content1Layout, typeLayout);

		ResourceManager.addComponent(content2Layout, employeeLayout);
		ResourceManager.addComponent(content2Layout, dueDateLayout);

		ResourceManager.addComponent(content3Layout, customerLayout);

		ResourceManager.addComponent(content4Layout, payItemLayout);
		ResourceManager.addComponent(content4Layout, attentionLayout);
		ResourceManager.addComponent(content4Layout, totalLayout);

		ResourceManager.addComponent(content5Layout, allQtyLayout);
		ResourceManager.addComponent(content5Layout, balanceLayout);
		ResourceManager.addComponent(content5Layout, packLayout);

		ResourceManager.addComponent(content6Layout, discAmtLayout);
		ResourceManager.addComponent(content6Layout, qtyLayout);
		ResourceManager.addComponent(content6Layout, amtLayout);

		ResourceManager.addComponent(noLayout, lblNo);
		ResourceManager.addComponent(noLayout, txtNo);

		ResourceManager.addComponent(dateLayout, lblDate);
		ResourceManager.addComponent(dateLayout, txtDate);

		ResourceManager.addComponent(typeLayout, lblType);
		ResourceManager.addComponent(typeLayout, txtType);

		ResourceManager.addComponent(dueDateLayout, lblDueDate);
		ResourceManager.addComponent(dueDateLayout, txtDueDate);

		ResourceManager.addComponent(payItemLayout, lblPayItem);
		ResourceManager.addComponent(payItemLayout, txtPayItem);

		ResourceManager.addComponent(attentionLayout, lblAttention);
		ResourceManager.addComponent(attentionLayout, txtAttention);

		ResourceManager.addComponent(totalLayout, lblTotal);
		ResourceManager.addComponent(totalLayout, txtTotal);

		ResourceManager.addComponent(allQtyLayout, lblAllQty);
		ResourceManager.addComponent(allQtyLayout, txtAllQty);

		ResourceManager.addComponent(balanceLayout, lblBalance);
		ResourceManager.addComponent(balanceLayout, txtBalance);

		ResourceManager.addComponent(packLayout, lblPack);
		ResourceManager.addComponent(packLayout, txtPack);

		ResourceManager.addComponent(discAmtLayout, lblDiscAmt);
		ResourceManager.addComponent(discAmtLayout, txtDiscAmt);

		ResourceManager.addComponent(qtyLayout, lblQty);
		ResourceManager.addComponent(qtyLayout, txtQty);

		ResourceManager.addComponent(amtLayout, lblAmt);
		ResourceManager.addComponent(amtLayout, txtAmt);

		ResourceManager.addComponent(employeeLayout, lblEmp);
		ResourceManager.addComponent(employeeLayout, txtEmp);
		ResourceManager.addComponent(employeeLayout, cmdEmp);
		ResourceManager.addComponent(employeeLayout, cmdClearEmp);
		ResourceManager.addComponent(employeeLayout, txtEmpName);

		ResourceManager.addComponent(customerLayout, lblCust);
		ResourceManager.addComponent(customerLayout, txtCust);
		ResourceManager.addComponent(customerLayout, cmdCust);
		ResourceManager.addComponent(customerLayout, cmdClearCust);
		ResourceManager.addComponent(customerLayout, txtCustName);

		noLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);

		dateLayout.setComponentAlignment(lblDate, Alignment.BOTTOM_CENTER);

		employeeLayout.setComponentAlignment(lblEmp, Alignment.BOTTOM_CENTER);
		employeeLayout.setComponentAlignment(cmdEmp, Alignment.BOTTOM_CENTER);
		employeeLayout.setComponentAlignment(cmdClearEmp,
				Alignment.BOTTOM_CENTER);

		typeLayout.setComponentAlignment(lblType, Alignment.BOTTOM_CENTER);

		customerLayout.setComponentAlignment(lblCust, Alignment.BOTTOM_CENTER);
		customerLayout.setComponentAlignment(cmdCust, Alignment.BOTTOM_CENTER);
		customerLayout.setComponentAlignment(cmdClearCust,
				Alignment.BOTTOM_CENTER);

		dueDateLayout
				.setComponentAlignment(lblDueDate, Alignment.BOTTOM_CENTER);

		payItemLayout
				.setComponentAlignment(lblPayItem, Alignment.BOTTOM_CENTER);

		attentionLayout.setComponentAlignment(lblAttention,
				Alignment.BOTTOM_CENTER);

		totalLayout.setComponentAlignment(lblTotal, Alignment.BOTTOM_CENTER);

		allQtyLayout.setComponentAlignment(lblAllQty, Alignment.BOTTOM_CENTER);

		balanceLayout
				.setComponentAlignment(lblBalance, Alignment.BOTTOM_CENTER);

		packLayout.setComponentAlignment(lblPack, Alignment.BOTTOM_CENTER);

		discAmtLayout
				.setComponentAlignment(lblDiscAmt, Alignment.BOTTOM_CENTER);

		qtyLayout.setComponentAlignment(lblQty, Alignment.BOTTOM_CENTER);

		amtLayout.setComponentAlignment(lblAmt, Alignment.BOTTOM_CENTER);

		content1Layout.setExpandRatio(noLayout, 1.3f);
		content1Layout.setExpandRatio(dateLayout, 1.3f);
		content1Layout.setExpandRatio(typeLayout, 1.4f);

		content2Layout.setExpandRatio(employeeLayout, 2.6f);
		content2Layout.setExpandRatio(dueDateLayout, 1.4f);

		content3Layout.setExpandRatio(customerLayout, 4f);

		content4Layout.setExpandRatio(payItemLayout, 1.3f);
		content4Layout.setExpandRatio(attentionLayout, 1.3f);
		content4Layout.setExpandRatio(totalLayout, 1.4f);

		content5Layout.setExpandRatio(allQtyLayout, 1.3f);
		content5Layout.setExpandRatio(balanceLayout, 1.3f);
		content5Layout.setExpandRatio(packLayout, 1.4f);

		content6Layout.setExpandRatio(discAmtLayout, 1.3f);
		content6Layout.setExpandRatio(qtyLayout, 1.3f);
		content6Layout.setExpandRatio(amtLayout, 1.4f);

		noLayout.setExpandRatio(lblNo, 0.1f);
		noLayout.setExpandRatio(txtNo, 3.9f);

		dateLayout.setExpandRatio(lblDate, 0.1f);
		dateLayout.setExpandRatio(txtDate, 3.9f);

		typeLayout.setExpandRatio(lblType, 0.1f);
		typeLayout.setExpandRatio(txtType, 3.9f);

		employeeLayout.setExpandRatio(lblEmp, 0.1f);
		employeeLayout.setExpandRatio(txtEmp, 1f);
		employeeLayout.setExpandRatio(cmdEmp, 0.1f);
		employeeLayout.setExpandRatio(cmdClearEmp, 0.1f);
		employeeLayout.setExpandRatio(txtEmpName, 2.7f);

		customerLayout.setExpandRatio(lblCust, 0.1f);
		customerLayout.setExpandRatio(txtCust, 1f);
		customerLayout.setExpandRatio(cmdCust, 0.1f);
		customerLayout.setExpandRatio(cmdClearCust, 0.1f);
		customerLayout.setExpandRatio(txtCustName, 2.7f);

		dueDateLayout.setExpandRatio(lblDueDate, 0.1f);
		dueDateLayout.setExpandRatio(txtDueDate, 3.9f);

		allQtyLayout.setExpandRatio(lblAllQty, 0.1f);
		allQtyLayout.setExpandRatio(txtAllQty, 3.9f);

		balanceLayout.setExpandRatio(lblBalance, 0.1f);
		balanceLayout.setExpandRatio(txtBalance, 3.9F);

		packLayout.setExpandRatio(lblPack, 0.1f);
		packLayout.setExpandRatio(txtPack, 3.9f);

		discAmtLayout.setExpandRatio(lblDiscAmt, 0.1f);
		discAmtLayout.setExpandRatio(txtDiscAmt, 3.9f);

		qtyLayout.setExpandRatio(lblQty, 0.1f);
		qtyLayout.setExpandRatio(txtQty, 3.9f);

		amtLayout.setExpandRatio(lblAmt, 0.1f);
		amtLayout.setExpandRatio(txtAmt, 3.9f);

		payItemLayout.setExpandRatio(lblPayItem, 0.1f);
		payItemLayout.setExpandRatio(txtPayItem, 3.9f);

		attentionLayout.setExpandRatio(lblAttention, 0.1f);
		attentionLayout.setExpandRatio(txtAttention, 3.9f);

		totalLayout.setExpandRatio(lblTotal, 0.1f);
		totalLayout.setExpandRatio(txtTotal, 3.9f);

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
			cmdClearEmp.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtEmp.setReadOnly(false);
					txtEmpName.setReadOnly(false);
					txtEmp.setValue("");
					txtEmpName.setValue("");
					txtEmp.setReadOnly(true);
					txtEmpName.setReadOnly(true);
				}
			});
			cmdClearCust.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtCust.setReadOnly(false);
					txtCustName.setReadOnly(false);
					txtCust.setValue("");
					txtCustName.setValue("");
					txtCust.setReadOnly(true);
					txtCustName.setReadOnly(true);
				}
			});
			cmdEmp.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_EmpList();
				}
			});
			cmdCust.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_CustList();
				}
			});
			listnerAdded = true;
		}

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractLayout) parentLayout;
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
		content1Layout.removeAllComponents();
		noLayout.removeAllComponents();
		dateLayout.removeAllComponents();
		typeLayout.removeAllComponents();
		content2Layout.removeAllComponents();
		employeeLayout.removeAllComponents();
		dueDateLayout.removeAllComponents();
		content3Layout.removeAllComponents();
		customerLayout.removeAllComponents();
		content4Layout.removeAllComponents();
		payItemLayout.removeAllComponents();
		attentionLayout.removeAllComponents();
		totalLayout.removeAllComponents();
		content5Layout.removeAllComponents();
		;
		allQtyLayout.removeAllComponents();
		balanceLayout.removeAllComponents();
		packLayout.removeAllComponents();
		content6Layout.removeAllComponents();
		discAmtLayout.removeAllComponents();
		qtyLayout.removeAllComponents();
		amtLayout.removeAllComponents();
		table.getTable().removeAllItems();
		table.data.clearALl();
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
							QRYSES = tv.getData().getFieldValue(rn, "ord_no")
									.toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select ord_no,ord_refnm reference,ord_date  "
					+ "from order1 where ord_code=" + varOrdCode, true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_EmpList() {
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
							txtEmpName.setValue(tv.getData().getFieldValue(rn,
									"name").toString());
							txtEmp.setValue(tv.getData()
									.getFieldValue(rn, "no").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "Select no,name from salesp where type='S' order by no",
					true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_CustList() {
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
											txtCustName.setValue(tv.getData()
													.getFieldValue(rn, "name")
													.toString());
											txtCust.setValue(tv.getData()
													.getFieldValue(rn, "code")
													.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"Select code,name  from c_ycust  where iscust='Y' order by path",
							true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
