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

public class frmOpen implements transactionalForm {
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
	private HorizontalLayout keyLayout = new HorizontalLayout();
	private HorizontalLayout locationCodeLayout = new HorizontalLayout();
	private HorizontalLayout checkayout = new HorizontalLayout();

	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout voiceDateLayout = new HorizontalLayout();
	private HorizontalLayout invoiceNoLayout = new HorizontalLayout();

	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout memoLayout = new HorizontalLayout();

	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout invRefLayout = new HorizontalLayout();

	private HorizontalLayout content5Layout = new HorizontalLayout();
	private HorizontalLayout packLayout = new HorizontalLayout();
	private HorizontalLayout balanceLayout = new HorizontalLayout();
	private HorizontalLayout amountLayout = new HorizontalLayout();

	private HorizontalLayout content6Layout = new HorizontalLayout();
	private HorizontalLayout prdDateLayout = new HorizontalLayout();
	private HorizontalLayout expDateLayout = new HorizontalLayout();

	private Label lblKey = new Label("KeyFld");
	private Label lblLocationCode = new Label("Loc Code");
	private Label lblVDate = new Label("V Date");
	private Label lblNo = new Label("Inv No");
	private Label lblMemo = new Label("Memo");
	private Label lblInvRef = new Label("Inv Ref");
	private Label lblPack = new Label("Pack");
	private Label lblBal = new Label("Bal");
	private Label lblAmt = new Label("Amt");
	private Label lblPrdDate = new Label("Prd Date");
	private Label lblExpDate = new Label("Exp Date");

	private TextField txtKey = ControlsFactory.CreateTextField("", "KEYID",
			lstfldinfo);
	private ComboBox txtLocationCode = ControlsFactory.CreateListField("",
			"LOCATION_CODE", "Select code,name from locations order by code",
			lstfldinfo);
	private DateField txtVDate = ControlsFactory.CreateDateField("", "KEYID",
			lstfldinfo);
	private TextField txtNo = ControlsFactory.CreateTextField("", "KEYID",
			lstfldinfo);
	private TextField txtMemo = ControlsFactory.CreateTextField("", "KEYID",
			lstfldinfo);
	private TextField txtInvRef = ControlsFactory.CreateTextField("", "KEYID",
			lstfldinfo);
	private TextField txtInvRefName = ControlsFactory.CreateTextField("",
			"KEYID", lstfldinfo);
	private TextField txtPack = ControlsFactory.CreateTextField("", "KEYID",
			lstfldinfo);
	private TextField txtBal = ControlsFactory.CreateTextField("", "KEYID",
			lstfldinfo);
	private TextField txtAmt = ControlsFactory.CreateTextField("", "KEYID",
			lstfldinfo);
	private DateField txtPrdDate = ControlsFactory.CreateDateField("", "KEYID",
			lstfldinfo);
	private DateField txtExpDate = ControlsFactory.CreateDateField("", "KEYID",
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

	private NativeButton cmdInvRef = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearInvRef = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");

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

	protected void fetchAccountInfo() {
		txtInvRefName.setReadOnly(false);
		txtInvRefName.setValue(utils.getSqlValue(
				"select name from acaccount where accno ='"
						+ txtInvRef.getValue().toString() + "'", con));
	}

	public void load_data() {

		try {
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"select * from pur1 where invoice_code=1 and keyfld='"
								+ QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
			}
			fetchAccountInfo();

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
		content1Layout.setWidth("100%");
		keyLayout.setWidth("100%");
		locationCodeLayout.setWidth("100%");
		checkayout.setWidth("100%");
		content2Layout.setWidth("100%");
		voiceDateLayout.setWidth("100%");
		invoiceNoLayout.setWidth("100%");
		content3Layout.setWidth("100%");
		memoLayout.setWidth("100%");
		content4Layout.setWidth("100%");
		invRefLayout.setWidth("100%");
		content5Layout.setWidth("100%");
		packLayout.setWidth("100%");
		balanceLayout.setWidth("100%");
		amountLayout.setWidth("100%");
		content6Layout.setWidth("100%");
		prdDateLayout.setWidth("100%");
		expDateLayout.setWidth("100%");

		lblKey.setWidth("60px");
		lblLocationCode.setWidth("60px");
		lblVDate.setWidth("60px");
		lblNo.setWidth("60px");
		lblMemo.setWidth("52px");
		lblInvRef.setWidth("54px");
		lblPack.setWidth("60px");
		lblBal.setWidth("60px");
		lblAmt.setWidth("60px");
		lblPrdDate.setWidth("55px");
		lblExpDate.setWidth("60px");

		txtKey.setWidth("100%");
		txtLocationCode.setWidth("100%");
		txtVDate.setWidth("100%");
		txtNo.setWidth("100%");
		txtMemo.setWidth("100%");
		txtInvRef.setWidth("100%");
		txtInvRefName.setWidth("100%");
		txtPack.setWidth("100%");
		txtBal.setWidth("100%");
		txtAmt.setWidth("100%");
		txtPrdDate.setWidth("100%");
		txtExpDate.setWidth("100%");

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
		ResourceManager.addComponent(basicLayout, tableLayout);
		ResourceManager.addComponent(basicLayout, content5Layout);
		ResourceManager.addComponent(basicLayout, content6Layout);

		ResourceManager.addComponent(content1Layout, keyLayout);
		ResourceManager.addComponent(content1Layout, locationCodeLayout);

		ResourceManager.addComponent(content2Layout, voiceDateLayout);
		ResourceManager.addComponent(content2Layout, invoiceNoLayout);

		ResourceManager.addComponent(content3Layout, memoLayout);

		ResourceManager.addComponent(content4Layout, invRefLayout);

		ResourceManager.addComponent(content5Layout, packLayout);
		ResourceManager.addComponent(content5Layout, balanceLayout);
		ResourceManager.addComponent(content5Layout, amountLayout);

		ResourceManager.addComponent(content6Layout, prdDateLayout);
		ResourceManager.addComponent(content6Layout, expDateLayout);

		ResourceManager.addComponent(keyLayout, lblKey);
		ResourceManager.addComponent(keyLayout, txtKey);

		ResourceManager.addComponent(locationCodeLayout, lblLocationCode);
		ResourceManager.addComponent(locationCodeLayout, txtLocationCode);

		ResourceManager.addComponent(voiceDateLayout, lblVDate);
		ResourceManager.addComponent(voiceDateLayout, txtVDate);

		ResourceManager.addComponent(invoiceNoLayout, lblNo);
		ResourceManager.addComponent(invoiceNoLayout, txtNo);

		ResourceManager.addComponent(memoLayout, lblMemo);
		ResourceManager.addComponent(memoLayout, txtMemo);

		ResourceManager.addComponent(invRefLayout, lblInvRef);
		ResourceManager.addComponent(invRefLayout, txtInvRef);
		ResourceManager.addComponent(invRefLayout, cmdInvRef);
		ResourceManager.addComponent(invRefLayout, cmdClearInvRef);
		ResourceManager.addComponent(invRefLayout, txtInvRefName);

		ResourceManager.addComponent(packLayout, lblPack);
		ResourceManager.addComponent(packLayout, txtPack);

		ResourceManager.addComponent(balanceLayout, lblBal);
		ResourceManager.addComponent(balanceLayout, txtBal);

		ResourceManager.addComponent(amountLayout, lblAmt);
		ResourceManager.addComponent(amountLayout, txtAmt);

		ResourceManager.addComponent(prdDateLayout, lblPrdDate);
		ResourceManager.addComponent(prdDateLayout, txtPrdDate);

		ResourceManager.addComponent(expDateLayout, lblExpDate);
		ResourceManager.addComponent(expDateLayout, txtExpDate);

		keyLayout.setComponentAlignment(lblKey, Alignment.BOTTOM_CENTER);
		locationCodeLayout.setComponentAlignment(lblLocationCode,
				Alignment.BOTTOM_CENTER);
		voiceDateLayout
				.setComponentAlignment(lblVDate, Alignment.BOTTOM_CENTER);
		invoiceNoLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);
		memoLayout.setComponentAlignment(lblMemo, Alignment.BOTTOM_CENTER);
		invRefLayout.setComponentAlignment(lblInvRef, Alignment.BOTTOM_CENTER);
		invRefLayout.setComponentAlignment(cmdInvRef, Alignment.BOTTOM_CENTER);
		invRefLayout.setComponentAlignment(cmdClearInvRef,
				Alignment.BOTTOM_CENTER);
		packLayout.setComponentAlignment(lblPack, Alignment.BOTTOM_CENTER);
		balanceLayout.setComponentAlignment(lblBal, Alignment.BOTTOM_CENTER);
		amountLayout.setComponentAlignment(lblAmt, Alignment.BOTTOM_CENTER);
		prdDateLayout
				.setComponentAlignment(lblPrdDate, Alignment.BOTTOM_CENTER);
		expDateLayout
				.setComponentAlignment(lblExpDate, Alignment.BOTTOM_CENTER);

		content1Layout.setExpandRatio(keyLayout, 2f);
		content1Layout.setExpandRatio(locationCodeLayout, 2f);

		content2Layout.setExpandRatio(voiceDateLayout, 2f);
		content2Layout.setExpandRatio(invoiceNoLayout, 2f);

		content3Layout.setExpandRatio(memoLayout, 4f);

		content4Layout.setExpandRatio(invRefLayout, 4f);

		content5Layout.setExpandRatio(packLayout, 1.4f);
		content5Layout.setExpandRatio(balanceLayout, 1.3f);
		content5Layout.setExpandRatio(amountLayout, 1.3f);

		content6Layout.setExpandRatio(prdDateLayout, 2.7f);
		content6Layout.setExpandRatio(expDateLayout, 1.3f);

		keyLayout.setExpandRatio(lblKey, 0.1f);
		keyLayout.setExpandRatio(txtKey, 3.9f);

		invoiceNoLayout.setExpandRatio(lblNo, 0.1f);
		invoiceNoLayout.setExpandRatio(txtNo, 3.9f);

		voiceDateLayout.setExpandRatio(lblVDate, 0.1f);
		voiceDateLayout.setExpandRatio(txtVDate, 3.9f);

		locationCodeLayout.setExpandRatio(lblLocationCode, 0.1f);
		locationCodeLayout.setExpandRatio(txtLocationCode, 3.9f);

		memoLayout.setExpandRatio(lblMemo, 0.1f);
		memoLayout.setExpandRatio(txtMemo, 3.9f);

		invRefLayout.setExpandRatio(lblInvRef, 0.1f);
		invRefLayout.setExpandRatio(txtInvRef, 1f);
		invRefLayout.setExpandRatio(cmdInvRef, 0.1f);
		invRefLayout.setExpandRatio(cmdClearInvRef, 0.1f);
		invRefLayout.setExpandRatio(txtInvRefName, 2.7f);

		packLayout.setExpandRatio(lblPack, 0.1f);
		packLayout.setExpandRatio(txtPack, 3.9f);

		balanceLayout.setExpandRatio(lblBal, 0.1f);
		balanceLayout.setExpandRatio(txtBal, 3.9f);

		amountLayout.setExpandRatio(lblAmt, 0.1f);
		amountLayout.setExpandRatio(txtAmt, 3.9f);

		prdDateLayout.setExpandRatio(lblPrdDate, 0.1f);
		prdDateLayout.setExpandRatio(txtPrdDate, 3.9f);

		expDateLayout.setExpandRatio(lblExpDate, 0.1f);
		expDateLayout.setExpandRatio(txtExpDate, 3.9f);

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
			cmdInvRef.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtInvRef.setReadOnly(false);
					txtInvRefName.setReadOnly(false);
					show_InvRefList();
				}
			});
			cmdClearInvRef.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtInvRef.setReadOnly(false);
					txtInvRefName.setReadOnly(false);
					txtInvRef.setValue("");
					txtInvRefName.setValue("");
					txtInvRef.setReadOnly(true);
					txtInvRefName.setReadOnly(true);
				}
			});
			listnerAdded = true;
		}
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		content1Layout.removeAllComponents();
		keyLayout.removeAllComponents();
		locationCodeLayout.removeAllComponents();
		checkayout.removeAllComponents();
		content2Layout.removeAllComponents();
		voiceDateLayout.removeAllComponents();
		invoiceNoLayout.removeAllComponents();
		content3Layout.removeAllComponents();
		memoLayout.removeAllComponents();
		content4Layout.removeAllComponents();
		invRefLayout.removeAllComponents();
		content5Layout.removeAllComponents();
		packLayout.removeAllComponents();
		balanceLayout.removeAllComponents();
		amountLayout.removeAllComponents();
		content6Layout.removeAllComponents();
		prdDateLayout.removeAllComponents();
		expDateLayout.removeAllComponents();

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
							"select invoice_no,invoice_date,c_cus_no,inv_amt-disc_amt net_amount from pur1 where  invoicecode=1  order by invoice_no desc",
							true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void show_InvRefList() {
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
							txtInvRefName.setValue(tv.getData().getFieldValue(
									rn, "name").toString());
							txtInvRef.setValue(tv.getData().getFieldValue(rn,
									"accno").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "Select accno,name  from acaccount", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
