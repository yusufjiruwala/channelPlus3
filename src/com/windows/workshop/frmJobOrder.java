package com.windows.workshop;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.doc.views.TableView;
import com.doc.views.YesNoDialog;
import com.doc.views.Query.QueryView;
import com.doc.views.Query.tableDataListner;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog.Callback;
import com.example.components.SearchField;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.FormLayoutManager;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.qryColumn;
import com.generic.rowTriggerListner;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.utilsVaadinPrintHandler;
import com.generic.ColumnProperty.afterModelUpdated;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.windows.Masters.Purchase.frmPurchaseCost;

public class frmJobOrder implements transactionalForm {
	private final String CLICK_TEXT = "Click here for item";
	private Connection con = null;
	private localTableModel data_ord_item = new localTableModel();
	private localTableModel data_ord_svc = new localTableModel();

	private Window wndList = new Window();
	VerticalLayout lstLayout = new VerticalLayout();
	private List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private HorizontalLayout commandLayout2 = new HorizontalLayout();
	private HorizontalLayout subLayout = new HorizontalLayout();
	private FormLayoutManager basicLayout1 = new FormLayoutManager();
	private GridLayout basicLayout2 = new GridLayout(3, 5);
	private VerticalLayout layoutItemDescr = new VerticalLayout();

	// private VerticalLayout basicLayout2 = new VerticalLayout();
	private TabSheet tabs = new TabSheet();
	private VerticalLayout itemlayout = new VerticalLayout();
	private VerticalLayout svclayout = new VerticalLayout();

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdRevert = ControlsFactory.CreateCustomButton(
			"Revert", "img/revert.png", "", "");
	private NativeButton cmdPost = ControlsFactory.CreateCustomButton(
			"Post to Invoice", "img/ok.png", "Post this order to invoice", "");
	private NativeButton cmdPrint = ControlsFactory.CreateCustomButton("Print",
			"img/print.png", "Print job order", "");
	private NativeButton cmdDel = ControlsFactory.CreateCustomButton("Delete",
			"img/remove.png", "Remove this order", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/eraser.png", "Clear the screen", "");
	private NativeButton cmdCancel = ControlsFactory.CreateCustomButton(
			"Cancel", "img/cancel.png", "Cancel Order", "");
	private NativeButton cmdBack = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "Back to list", "");

	private NativeButton cmdAddItm = ControlsFactory.CreateCustomButton(
			"Add item", "img/add.png", "Add item", "");
	private NativeButton cmdDelItm = new NativeButton();
	private NativeButton cmdDelAll = new NativeButton();
	private NativeButton cmdStartAll = ControlsFactory.CreateToolbarButton(
			"img/start_all.png", "Start all");
	private NativeButton cmdFinishAll = ControlsFactory.CreateToolbarButton(
			"img/finish_all.png", "Finish all");
	
	private ComboBox txtlocation = ControlsFactory.CreateListField(null,
			"location_code",
			"select code,name from locations where flag=1 order by code",
			lstfldinfo);

	private ComboBox txtStore = ControlsFactory.CreateListField(null, "STRA",
			"select no,name from store where flag=1 order by no", lstfldinfo);

	private TextField txtOrdNo = ControlsFactory.CreateTextField(null,
			"ORD_NO", lstfldinfo, "100%", "");
	private DateField txtOrDate = ControlsFactory.CreateDateField(null,
			"ORD_DATE", lstfldinfo, "100%");
	private ComboBox txtJobType = ControlsFactory.CreateListField(null,
			"ORD_TYPE", "select no,descr from ord_job_types order by no",
			lstfldinfo, "100%", "", false);
	private ComboBox txtInvType = ControlsFactory.CreateListField(null,
			"ORD_DISCAMT", "", lstfldinfo, "100%", "", false);
	
	private SearchField txtCustomer = ControlsFactory
			.CreateSearchField(
					null,
					"ORD_REF",
					lstfldinfo,
					"100%",
					"select code,name from c_ycust where flag=1 and childcount=0 order by path",
					"CODE", "NAME");

	private DateField txtDelivered = ControlsFactory.CreateDateField(null,
			"ORD_SHPDT", lstfldinfo, "100%");
	private Label txtItemCode = new Label(CLICK_TEXT);
	private TextField txtItemSerial = ControlsFactory.CreateTextField(null,
			"LCNO", lstfldinfo);
	private SearchField txtEmpNo = ControlsFactory.CreateSearchField(null,
			"ord_empno", lstfldinfo, "100%",
			"select no,name from salesp order by no", "no", "name");
	private TextField txtDescr = ControlsFactory.CreateTextField(null,
			"REMARKS", lstfldinfo, "100%", "");
	private Label lblSoldDate = new Label("Sold Date");
	private Label lblPurDate = new Label("Purchased Date");
	private Label txtSoldDate = new Label("");
	private Label txtPurDate = new Label("");
	private Label txtSoldyr = new Label("");
	private Label txtPuryr = new Label("");
	private Label lblWorkStatus = new Label("Status");
	private Label txtWorkStatus_f = new Label("");
	private Label txtWorkStatus_c = new Label("");
	private Label lblCS = new Label("Commited Summary");
	private Label txtCS_f = new Label("");
	private Label txtCS_c = new Label("");

	private String QRYSES = "";
	private boolean listnerAdded = false;

	private Table tbl_ord_item = new Table();
	private Table tbl_ord_svc = new Table();
	private double varSaleKeyfld = -1;
	private String varLocation = "";
	private double varOrdNo = -1;
	private double varOrdCode = 106;
	private double varOrdFlag = 1;
	private Date varCreateDate = new java.util.Date(System.currentTimeMillis());
	private String varUserCreated = utils.CPUSER;
	private String varUserApproved = "";
	private String varItemRcv = "";
	private String varItemRcvDescr = CLICK_TEXT;
	private double varFinishedRate = 0;
	private double varCancelledRate = 0;

	private List<ColumnProperty> lstItmCols = new ArrayList<ColumnProperty>();
	private List<ColumnProperty> lstSvcCols = new ArrayList<ColumnProperty>();

	private Map<String, String> mapActionStrs = new HashMap<String, String>();
	private boolean saved_successed = false;

	public frmJobOrder() {
		init();
	}

	private void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		// centralPanel.setHeight("-1px");

		txtlocation.setReadOnly(false);
		txtOrdNo.setReadOnly(false);
		txtItemSerial.setReadOnly(false);
		txtJobType.setReadOnly(false);

		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout1.removeAll();
		basicLayout2.removeAllComponents();
		subLayout.removeAllComponents();
		commandLayout.removeAllComponents();
		itemlayout.removeAllComponents();
		svclayout.removeAllComponents();
		tabs.removeAllComponents();
		data_ord_item.clearALl();
		data_ord_svc.clearALl();
		tbl_ord_item.removeAllItems();
		tbl_ord_svc.removeAllItems();

	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		basicLayout2.setMargin(true, true, false, true);
		txtDescr.setWidth("100%");
		txtItemCode.setWidth("100%");
		txtPurDate.setWidth("100%");
		txtPuryr.setWidth("100%");
		txtSoldDate.setWidth("100%");
		txtSoldyr.setWidth("100%");
		lblPurDate.setWidth("100%");
		lblSoldDate.setWidth("100%");
		basicLayout1.setWidth("100%");
		commandLayout.setHeight("100%");
		commandLayout2.setHeight("100%");
		subLayout.setWidth("100%");
		subLayout.setHeight("100%");
		layoutItemDescr.setSizeFull();
		basicLayout2.setSizeFull();
		mainLayout.setWidth("700px");
		mainLayout.setHeight("600px");
		itemlayout.setSizeFull();
		svclayout.setSizeFull();
		tbl_ord_item.setSizeFull();
		tbl_ord_svc.setSizeFull();
		tabs.setSizeFull();
		txtWorkStatus_c.setContentMode(Label.CONTENT_XHTML);
		txtWorkStatus_f.setContentMode(Label.CONTENT_XHTML);
		basicLayout2.addStyleName("vborder");
		txtItemCode.addStyleName("lov_field");

		tbl_ord_item.setSelectable(true);
		tbl_ord_svc.setSelectable(true);
		tbl_ord_item.setSortDisabled(true);
		tbl_ord_svc.setSortDisabled(true);
		tbl_ord_item.setColumnReorderingAllowed(false);
		tbl_ord_svc.setColumnReorderingAllowed(false);
		mainLayout.addStyleName("formLayout");

		tabs.addTab(itemlayout, "Items", null);
		tabs.addTab(svclayout, "Services", null);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, subLayout);
		ResourceManager.addComponent(subLayout, basicLayout1);
		ResourceManager.addComponent(subLayout, basicLayout2);

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdPost);
		ResourceManager.addComponent(commandLayout, cmdPrint);
		ResourceManager.addComponent(commandLayout, cmdDel);
		ResourceManager.addComponent(commandLayout, cmdRevert);
		ResourceManager.addComponent(commandLayout, cmdCls);
		ResourceManager.addComponent(commandLayout, cmdCancel);
		ResourceManager.addComponent(commandLayout, cmdBack);

		ResourceManager.addComponent(commandLayout2, cmdAddItm);
		ResourceManager.addComponent(commandLayout2, cmdDelItm);
		ResourceManager.addComponent(commandLayout2, cmdDelAll);
		ResourceManager.addComponent(commandLayout2, cmdStartAll);
		ResourceManager.addComponent(commandLayout2, cmdFinishAll);

		basicLayout1.setComponentsRow(1,
				"caption=Location ,width=100%,align=right," + utils.OO2_1,
				txtlocation, "immediate=true,width=100%," + utils.OO2_2,
				"caption=Ord No ,width=100%,align=right," + utils.OO2_3,
				txtOrdNo, "width=100%," + utils.OO2_4);

		basicLayout1.setComponentsRow(2,
				"caption=Ord Date ,width=100%,align=right," + utils.OO2_1,
				txtOrDate, "immediate=true,width=100%," + utils.OO2_2,
				"caption=Job Type ,width=100%,align=right," + utils.OO2_3,
				txtJobType, "width=100%,immediate=true," + utils.OO2_4);

		basicLayout1.setComponentsRow(3,
				"caption=Invoice Type ,width=100%,align=right," + utils.OO2_1,
				txtInvType, "immediate=true,width=100%," + utils.OO2_2,
				"caption=Delivered ,width=100%,align=right," + utils.OO2_3,
				txtDelivered, "width=100%," + utils.OO2_4);

		basicLayout1.setComponentsRow(4,
				"caption=Item Serial,width=100%,align=right," + utils.OO2_1,
				txtItemSerial, "width=100%,immediate=true," + utils.OO2_2,
				"caption=Store ,width=100%,align=right," + utils.OO2_3,
				txtStore, "width=100%," + utils.OO2_4);
		basicLayout1.setComponentsRow(5,
				"caption=Employee,width=100%,align=right," + utils.OO1_1,
				txtEmpNo, "width=100%,immediate=true," + utils.OO1_2);
		basicLayout1.setComponentsRow(6,
				"caption=Customer,width=100%,align=right," + utils.OO1_1,
				txtCustomer, "width=100%,immediate=true," + utils.OO1_2);

		ResourceManager.addComponent(basicLayout2, lblWorkStatus);
		ResourceManager.addComponent(basicLayout2, txtWorkStatus_f);
		ResourceManager.addComponent(basicLayout2, txtWorkStatus_c);
		ResourceManager.addComponent(basicLayout2, lblPurDate);
		ResourceManager.addComponent(basicLayout2, txtPurDate);
		ResourceManager.addComponent(basicLayout2, txtPuryr);
		ResourceManager.addComponent(basicLayout2, lblSoldDate);
		ResourceManager.addComponent(basicLayout2, txtSoldDate);
		ResourceManager.addComponent(basicLayout2, txtSoldyr);

		ResourceManager.addComponent(layoutItemDescr, txtItemCode);
		ResourceManager.addComponent(layoutItemDescr, txtDescr);
		ResourceManager.addComponent(mainLayout, layoutItemDescr);

		ResourceManager.addComponent(mainLayout, commandLayout2);

		ResourceManager.addComponent(mainLayout, tabs);
		ResourceManager.addComponent(itemlayout, tbl_ord_item);
		ResourceManager.addComponent(svclayout, tbl_ord_svc);

		layoutItemDescr.setComponentAlignment(txtItemCode, Alignment.TOP_LEFT);
		layoutItemDescr.setComponentAlignment(txtDescr, Alignment.TOP_LEFT);

		mainLayout.setExpandRatio(commandLayout, .2f);
		mainLayout.setExpandRatio(subLayout, 1.1f);
		mainLayout.setExpandRatio(layoutItemDescr, 0.6f);
		mainLayout.setExpandRatio(commandLayout2, 0.2f);
		mainLayout.setExpandRatio(tabs, 1.9f);

		subLayout.setExpandRatio(basicLayout1, 2.5f);
		subLayout.setExpandRatio(basicLayout2, 1.5f);

		if (!listnerAdded) {

			layoutItemDescr.addListener(new LayoutClickListener() {

				public void layoutClick(LayoutClickEvent event) {
					if (event.getChildComponent() != null
							&& event.getChildComponent() == txtItemCode) {

						show_item_list();
					}
				}
			});

			txtlocation.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						varLocation = "";
						if (txtlocation.getValue() != null) {
							varLocation = ((dataCell) txtlocation.getValue())
									.getValue().toString();
						}
						utilsVaadin.FillCombo(txtInvType,
								"select no,descr from invoicetype where location_code='"
										+ varLocation + "' order by no", con);
						txtInvType
								.setValue(utilsVaadin
										.findByValue(
												txtInvType,
												utils
														.getSqlValue(
																"select repair.getsetupvalue_2('DEFAULT_TYPE') from dual",
																con)));
					} catch (SQLException ex) {
						ex.printStackTrace();
					}

				}
			});
			cmdPrint.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (cmdSave.isEnabled()) {
						save_data(false);
						if (saved_successed)
							print_data();
					} else {
						print_data();
					}
				}
			});

			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			cmdDel.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

					delete_data();
				}
			});
			cmdPost.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					post_voucher();
				}
			});
			cmdCls.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					load_data();

				}
			});
			cmdRevert.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					load_data();

				}
			});
			cmdCancel.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

					Callback cb = new Callback() {

						public void onDialogResult(boolean resultIsYes) {

							if (!resultIsYes) {
								return;
							}
							do_status_service(-1);
							save_data(false);
							try {
								utils.execSql(
										"update order1 set ord_flag=-1 where ord_no='"
												+ QRYSES + "' and ord_code="
												+ varOrdCode, con);
								con.commit();
								QRYSES = "";
								showInitView();
							} catch (SQLException sq) {
								sq.printStackTrace();
								parentLayout.getWindow().showNotification(
										sq.getMessage(),
										Notification.TYPE_ERROR_MESSAGE);
								try {
									con.rollback();
								} catch (SQLException e) {
								}
							}
						}
					};
					parentLayout.getWindow().addWindow(
							new YesNoDialog("Confirmation",
									"Do you want to Cancel order no # "
											+ QRYSES + " ?", cb, "250px",
									"-1px"));

				}
			});
			cmdBack.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					showInitView();

				}
			});
			cmdAddItm.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					add_item();
				}
			});
			cmdFinishAll.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					do_status_service(2);
				}
			});
			cmdStartAll.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					do_status_service(1);

				}
			});
			cmdDel.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					delete_item();

				}
			});
			txtOrdNo.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if (txtOrdNo.getValue() == null
							|| txtOrdNo.getValue().toString().isEmpty()) {
						txtOrdNo.setValue(utils.getSqlValue(
								"select nvl(max(ord_no),0)+1 from order1 where ord_code= "
										+ varOrdCode, con));
					}
					varOrdNo = Double.valueOf(txtOrdNo.getValue().toString());

				}
			});
			txtItemSerial.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						txtPurDate.setValue("");
						txtPuryr.setValue("");
						txtSoldDate.setValue("");
						txtSoldyr.setValue("");

						ResultSet rst = utils
								.getSqlRS(
										"select *from pur_stock_serials where serial_code='"
												+ txtItemSerial.getValue()
												+ "' and (item_code='"
												+ txtItemCode.getValue()
												+ "' or '"
												+ txtItemCode.getValue()
												+ "' is null)", con);

						if (rst != null && rst.first()) {
							SimpleDateFormat sdf = new SimpleDateFormat(
									utils.FORMAT_SHORT_DATE);
							if (rst.getString("PUR_DATE") != null) {
								txtPurDate.setValue(sdf.format(rst
										.getDate("PUR_DATE")));
								int monthscount = utils.getNumberOfMonths(rst
										.getDate("PUR_DATE"), (Date) txtOrDate
										.getValue());
								txtPuryr.setValue(monthscount + " Months");
							}

							if (rst.getString("SOLD_DATE") != null) {
								txtSoldDate.setValue(sdf.format(rst
										.getDate("SOLD_DATE")));
								int monthscount = utils.getNumberOfMonths(rst
										.getDate("SOLD_DATE"), (Date) txtOrDate
										.getValue());
								txtSoldyr.setValue(monthscount + " Months");
							}

						}
					} catch (SQLException ex) {

					}
				}
			});
			txtJobType.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					if (txtJobType.getValue() == null) {
						commandLayout2.setEnabled(false);
					} else {
						commandLayout2.setEnabled(true);
					}

					commandLayout2.requestRepaintAll();
					if (txtJobType.getValue() == null) {
						return;
					}

					try {
						ResultSet rst = utils.getSqlRS(
								"select *from ord_job_types where no='"
										+ ((dataCell) txtJobType.getValue())
												.getValue().toString() + "'",
								con);
						if (rst != null && rst.first()) {
							if (!rst.getString("HAVE_SERVICES").equals("Y")) {
								svclayout.setEnabled(false);
							} else {
								svclayout.setEnabled(true);
							}

							if (!rst.getString("HAVE_SPARE_PARTS").equals("Y")) {
								itemlayout.setEnabled(false);
							} else {
								itemlayout.setEnabled(true);
							}
							tabs.requestRepaintAll();
							itemlayout.requestRepaintAll();
							svclayout.requestRepaintAll();
							rst.close();
						}

					} catch (SQLException ex) {
						ex.printStackTrace();
						parentLayout.getWindow().showNotification(
								ex.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);

					}
					validate_controls();
				}
			});
			txtInvType.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if (txtInvType.getValue() == null) {
						return;
					}
					validate_controls();
				}
			});
			data_ord_item.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (!(data_ord_item.getFieldValue(cursorNo, "ORD_PRICE") == null)
							&& !(data_ord_item.getFieldValue(cursorNo,
									"ORD_PKQTY") == null)) {
						double pr = ((BigDecimal) data_ord_item.getFieldValue(
								cursorNo, "ORD_PRICE")).doubleValue();
						double pk = ((BigDecimal) data_ord_item.getFieldValue(
								cursorNo, "ORD_PACK")).doubleValue();
						double qt = ((BigDecimal) data_ord_item.getFieldValue(
								cursorNo, "ORD_PKQTY")).doubleValue();
						double amt = pr * qt;
						data_ord_item.setFieldValue(cursorNo, "AMOUNT",
								BigDecimal.valueOf(amt));
						data_ord_item.setFieldValue(cursorNo, "ORD_ALLQTY",
								BigDecimal.valueOf(qt * pk));
						utilsVaadin.refreshRowFromData(data_ord_item,
								tbl_ord_item, cursorNo, lstItmCols);
						DecimalFormat df = new DecimalFormat(
								Channelplus3Application.getInstance()
										.getFrmUserLogin().FORMAT_MONEY);
						String a = df.format(data_ord_item.getSummaryOf(
								"AMOUNT", localTableModel.SUMMARY_SUM));
						tabs.getTab(itemlayout).setCaption("Items # " + a);
					}

				}
			});

			data_ord_svc.setRowlistner(new rowTriggerListner() {
				public void onCalc(int cursorNo) {
					if (!(data_ord_svc.getFieldValue(cursorNo, "ORD_PRICE") == null)
							&& !(data_ord_svc.getFieldValue(cursorNo,
									"ORD_PKQTY") == null)) {
						double pr = ((BigDecimal) data_ord_svc.getFieldValue(
								cursorNo, "ORD_PRICE")).doubleValue();
						double pk = ((BigDecimal) data_ord_svc.getFieldValue(
								cursorNo, "ORD_PACK")).doubleValue();
						double qt = ((BigDecimal) data_ord_svc.getFieldValue(
								cursorNo, "ORD_PKQTY")).doubleValue();
						double amt = pr * qt;
						data_ord_svc.setFieldValue(cursorNo, "AMOUNT",
								BigDecimal.valueOf(amt));
						data_ord_svc.setFieldValue(cursorNo, "ORD_ALLQTY",
								BigDecimal.valueOf(qt * pk));
						utilsVaadin.refreshRowFromData(data_ord_svc,
								tbl_ord_svc, cursorNo, lstSvcCols);
						DecimalFormat df = new DecimalFormat(
								Channelplus3Application.getInstance()
										.getFrmUserLogin().FORMAT_MONEY);
						String a = df.format(data_ord_svc.getSummaryOf(
								"AMOUNT", localTableModel.SUMMARY_SUM));
						tabs.getTab(svclayout).setCaption("Services # " + a);
						calc_status();
					}
				}
			});
			listnerAdded = true;
		}
	}

	protected void do_status_service(int i) {
		for (int j = 0; j < data_ord_svc.getRows().size(); j++) {
			data_ord_svc
					.setFieldValue(j, "DELIVEREDQTY", BigDecimal.valueOf(i));
		}

	}

	public void calc_status() {
		int cntFinished = 0;
		int cntCancelled = 0;
		for (int i = 0; i < data_ord_svc.getRows().size(); i++) {
			BigDecimal bg = (BigDecimal) data_ord_svc.getFieldValue(i,
					"DELIVEREDQTY");
			if (bg == null) {
				bg = BigDecimal.valueOf(0);
			}
			if (bg.doubleValue() == 2) {
				cntFinished++;
			}
			if (bg.doubleValue() == -1) {
				cntCancelled++;
			}
		}
		if (data_ord_svc.getRows().size() > 0) {
			varCancelledRate = (100 / data_ord_svc.getRows().size())
					* cntCancelled;
			varFinishedRate = (100 / data_ord_svc.getRows().size())
					* cntFinished;

		} else {
			varFinishedRate = 100;
			varCancelledRate = 0;
		}

		txtWorkStatus_f.setValue("<font size=2pt color=green><b>Finished = "
				+ varFinishedRate + " %</b></font>");
		txtWorkStatus_c.setValue("<font size=2pt color=red><b>Cancelled= "
				+ varCancelledRate + "%</b></font>");
	}

	public void show_item_list() {
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
											String rfr = tv.getData()
													.getFieldValue(rn,
															"REFERENCE")
													.toString();
											varItemRcv = rfr;
											varItemRcvDescr = tv.getData()
													.getFieldValue(rn, "DESCR")
													.toString()
													+ " - " + varItemRcv;
											txtItemCode
													.setValue(varItemRcvDescr);
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select reference,descr from items where childcounts=0 order by descr2",
							true);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	private void add_item() {
		final Window wnd = new Window();
		final VerticalLayout la = new VerticalLayout();
		wnd.setContent(la);
		String sql = "";
		if (itemlayout.isEnabled() && tabs.getSelectedTab() == itemlayout) {
			sql = "select reference,descr,packd,pack,unitd,price1 from items where childcounts=0 and itprice4=0 order by descr2";
		}
		if (svclayout.isEnabled() && tabs.getSelectedTab() == svclayout) {
			sql = "select reference,descr,packd,pack,unitd,price1 from items where childcounts=0 and itprice4=1 order by descr2";
		}
		try {
			if (sql.isEmpty()) {
				throw new SQLException("Can not select here..");
			}

			utilsVaadin.showSearch(la, new SelectionListener() {

				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);

					if (tv.getSelectionValue() > -1) {
						int rn = -1;
						localTableModel dt = null;
						try {
							int sr = tv.getSelectionValue();

							if (tabs.getSelectedTab() == itemlayout) {
								dt = data_ord_item;
							}
							if (tabs.getSelectedTab() == svclayout) {
								dt = data_ord_svc;
							}
							rn = dt.addRecord();
							dt.setFieldValue(rn, "ORD_REFER", tv.getData()
									.getFieldValue(sr, "REFERENCE"));
							dt.setFieldValue(rn, "DESCR", tv.getData()
									.getFieldValue(sr, "DESCR"));
							dt.setFieldValue(rn, "ORD_PACKD", tv.getData()
									.getFieldValue(sr, "PACKD"));
							dt.setFieldValue(rn, "ORD_PACK", tv.getData()
									.getFieldValue(sr, "PACK"));
							dt.setFieldValue(rn, "ORD_UNITD", tv.getData()
									.getFieldValue(sr, "UNITD"));
							dt.setFieldValue(rn, "ORD_PRICE", tv.getData()
									.getFieldValue(sr, "PRICE1"));
							dt.setFieldValue(rn, "ORD_PKQTY", BigDecimal
									.valueOf(1));
							dt.setFieldValue(rn, "ORD_ALLQTY", BigDecimal
									.valueOf(1));
							dt.setFieldValue(rn, "DELIVEREDQTY", BigDecimal
									.valueOf(0));
							fill_items_table();
							fill_services_table();

						} catch (Exception ex) {
							ex.printStackTrace();
							parentLayout.getWindow().showNotification(
									ex.getMessage(),
									Notification.TYPE_ERROR_MESSAGE);
							if (dt != null && rn > -1) {
								dt.deleteRow(rn);
							}

						}
					}
					validate_controls();
				}
			}, con, sql, true);
		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void delete_item() {

	}

	private void show_list() {
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
							QRYSES = tv.getData().getFieldValue(rn, "NO")
									.toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select no,descr from ORD_job_types order by NO", true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	protected void delete_data() {

	}

	public void init() {
		cmdAddItm.setIcon(new ThemeResource("img/newmenu.png"));
		cmdAddItm.setStyleName("toolbar");
		cmdDelItm.setIcon(new ThemeResource("img/remove.png"));
		cmdDelItm.setStyleName("toolbar");
		cmdDelAll.setIcon(new ThemeResource("img/revert.png"));
		cmdDelAll.setStyleName("toolbar");

		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {
			utilsVaadin.applyColumns("WORKSHOP_JOB_VOU_SVC", tbl_ord_svc,
					lstSvcCols, con);
			utilsVaadin.applyColumns("WORKSHOP_JOB_VOU_ITM", tbl_ord_item,
					lstItmCols, con);
			if (utilsVaadin.findColByCol("DELIVEREDQTY", lstSvcCols) == null) {
				return;
			}
			utilsVaadin.findColByCol("DELIVEREDQTY", lstSvcCols).lov_sql = new ArrayList<dataCell>();
			utilsVaadin.findColByCol("DELIVEREDQTY", lstSvcCols).lov_sql
					.add(new dataCell("Not Started", BigDecimal.valueOf(0)));
			utilsVaadin.findColByCol("DELIVEREDQTY", lstSvcCols).lov_sql
					.add(new dataCell("Started", BigDecimal.valueOf(1)));
			utilsVaadin.findColByCol("DELIVEREDQTY", lstSvcCols).lov_sql
					.add(new dataCell("Finished", BigDecimal.valueOf(2)));
			utilsVaadin.findColByCol("DELIVEREDQTY", lstSvcCols).lov_sql
					.add(new dataCell("Cancelled", BigDecimal.valueOf(-1)));

			utilsVaadin.findColByCol("DELIVEREDQTY", lstSvcCols).actionAfterUpdate = new afterModelUpdated() {

				public Object onValueChange(int rowno, String colname, Object vl) {
					if (vl != null && vl instanceof BigDecimal
							&& ((BigDecimal) vl).doubleValue() == -1) {
						data_ord_svc.setFieldValue(rowno, "ORD_PRICE",
								BigDecimal.valueOf(0));
						utilsVaadin.refreshRowFromData(data_ord_svc,
								tbl_ord_svc, rowno, lstSvcCols);

					}
					return vl;
				}
			};
		} catch (SQLException ex) {

		}

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {
			if (data_ord_item.getDbclass() == null) {
				data_ord_item.createDBClassFromConnection(con);
			}
			if (data_ord_svc.getDbclass() == null) {
				data_ord_svc.createDBClassFromConnection(con);
			}

		} catch (SQLException e) {
		}

		createView();
		load_data();

	}

	public void load_data() {
		saved_successed = true;
		txtlocation.setReadOnly(false);
		txtOrdNo.setReadOnly(false);
		txtItemSerial.setReadOnly(false);
		txtJobType.setReadOnly(false);
		txtDescr.setValue("");
		txtItemCode.setValue(CLICK_TEXT);
		varItemRcv = "";
		varItemRcvDescr = "";

		/*
		 * txtItemSerial.setValue(""); txtJobType.setValue(null);
		 * txtDelivered.setValue(null); txtDescr.setValue("");
		 * txtEmpNo.setValue(null); txtInvType.setValue(null);
		 * txtItemCode.setValue(""); txtlocation.setValue(null);
		 */

		utilsVaadin.resetValues(basicLayout1, false, false);
		utilsVaadin.resetValues(basicLayout2, false, false);

		String st = Channelplus3Application.getInstance().getFrmUserLogin()
				.getMapVars().get("DEFAULT_STORE");
		txtStore.setValue(utilsVaadin.findByValue(txtStore, st));
		txtStore.setNullSelectionAllowed(false);
		txtPurDate.setValue(" ");
		txtPuryr.setValue(" ");
		txtSoldDate.setValue(" ");
		txtSoldyr.setValue(" ");
		txtWorkStatus_c.setValue("");
		txtWorkStatus_f.setValue("");
		varFinishedRate = 0;
		varCancelledRate = 0;
		varSaleKeyfld = -1;
		varOrdFlag = 1;
		varUserApproved = "";

		if (txtOrDate.getValue() != null) {
			((java.util.Date) txtOrDate.getValue()).setTime(System
					.currentTimeMillis());
		} else {
			txtOrDate.setValue(new java.util.Date(System.currentTimeMillis()));
		}
		txtOrdNo.setValue("");
		varLocation = utils.getSqlValue(
				"select repair.getsetupvalue_2('DEFAULT_LOCATION') from dual ",
				con);
		txtlocation.setValue(utilsVaadin.findByValue(txtlocation, varLocation));

		try {
			PreparedStatement ps = con
					.prepareStatement(
							"select order1.*,rtrim(items.descr||' '||items.reference) item_descr from order1,items "
									+ " where ordacc=reference(+) and ord_no='"
									+ QRYSES + "' and ord_code=" + varOrdCode,
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ResultSet rst = ps.executeQuery();

			if (rst.first()) {
				varOrdNo = rst.getDouble("ord_no");
				varLocation = rst.getString("location_code");
				varOrdFlag = rst.getDouble("ord_flag");
				varSaleKeyfld = rst.getDouble("saleinv");
				varUserApproved = utils.nvl(rst.getString("APPROVED_BY"), "");
				varUserCreated = rst.getString("LAST_MODIFYED_BY");

				varItemRcv = utils.nvl(rst.getString("ORDACC"), "");
				varItemRcvDescr = utils.nvl(rst.getString("item_descr"), "");
				utilsVaadin.assignValues(rst, lstfldinfo);
				txtCustomer.setDisplay(rst.getString("ORD_REFNM"));
				if (txtEmpNo.getValue() != null
						&& !txtEmpNo.getValue().toString().isEmpty())
					txtEmpNo.setDisplay(utils.getSqlValue(
							"select name from salesp where no='"
									+ txtEmpNo.getValue() + "'", con));
				txtItemSerial.setReadOnly(false);
				txtItemSerial.setValue(rst.getString("LCNO"));
				txtOrdNo.setValue(varOrdNo);
				txtDescr.setValue(utils.nvl(rst.getString("REMARKS"), ""));
				txtlocation.setReadOnly(true);
				txtOrdNo.setReadOnly(true);
				txtItemSerial.setReadOnly(true);
				txtJobType.setReadOnly(true);
				commandLayout2.setEnabled(true);

			} else {
				txtOrdNo.setValue(utils.getSqlValue(
						"select nvl(max(ord_no),0)+1 from order1 where ord_code='"
								+ varOrdCode + "'", con));
				commandLayout2.setEnabled(false);
			}
			varOrdNo = Double.valueOf(txtOrdNo.getValue().toString());
			ps.close();
			fetch_services();
			fetch_items();
			fill_items_table();
			fill_services_table();

			validate_controls();

			if (data_ord_item.getRowCount() > data_ord_svc.getRowCount())
				tabs.setSelectedTab(0);
			else
				tabs.setSelectedTab(1);

		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public void validate_controls() {

		if (varOrdFlag == 2) {
			cmdSave.setEnabled(false);
		} else {
			cmdSave.setEnabled(true);
		}
		if (varOrdFlag == 1 && !varUserApproved.equals(varUserCreated)
				&& !varUserApproved.isEmpty()) {
			cmdSave.setEnabled(false);
		}

		if (data_ord_item.getRows().size() > 0
				|| data_ord_svc.getRows().size() > 0) {
			txtJobType.setReadOnly(true);
			txtItemSerial.setReadOnly(true);
		} else {
			txtJobType.setReadOnly(false);
			txtItemSerial.setReadOnly(false);
		}

		if (txtJobType.getValue() != null && txtItemSerial.getValue() != null) {
			cmdAddItm.setEnabled(true);
		} else {
			cmdAddItm.setEnabled(false);
		}
	}

	public void fetch_services() {
		try {
			data_ord_svc
					.executeQuery(
							"select order2.*,(ord_price/ord_pack)*ord_allqty amount from order2 WHERE ORD_CODE="
									+ varOrdCode
									+ " and ord_no='"
									+ QRYSES
									+ "' and ord_type=2", true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fetch_items() {
		try {
			data_ord_item
					.executeQuery(
							"select order2.*,(ord_price/ord_pack)*ord_allqty amount from order2 WHERE ORD_CODE="
									+ varOrdCode
									+ " and ord_no='"
									+ QRYSES
									+ "' and ord_type=1", true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void fill_services_table() {
		tbl_ord_svc.removeAllItems();
		utilsVaadin.query_data2(tbl_ord_svc, data_ord_svc, lstSvcCols);

	}

	public void fill_items_table() {
		tbl_ord_item.removeAllItems();
		utilsVaadin.query_data2(tbl_ord_item, data_ord_item, lstItmCols);
	}

	public void print_data() {

		try {
			utilsVaadinPrintHandler.printWorkshop(Integer.parseInt(QRYSES),
					"rptWorkshop", con);
		} catch (Exception ex) {
			mainLayout.getWindow().showNotification("Unable to print:",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			ex.printStackTrace();
		}

	}

	public void validate_data() throws Exception {
		if (txtCustomer.getValue() == null) {
			throw new SQLException("Customer must have  value");
		}
		calc_status();
		if (!QRYSES.isEmpty()) {
			varOrdFlag = Double.valueOf(utils.getSqlValue(
					"select ord_flag from order1 where ord_code=" + varOrdCode
							+ " and ord_no='" + QRYSES + "'", con));
			if (varOrdFlag == 2) {
				throw new SQLException("Closed by other user");
			}

			if (varOrdFlag == 1 && !varUserApproved.equals(varUserCreated)
					&& !varUserApproved.isEmpty()) {
				throw new SQLException(
						"Can not update , must be approval user same as created by user");
			}
		}

	}

	public void save_data() {
		save_data(true);
	}

	public void save_data(boolean showlist) {
		try {
			saved_successed = false;
			con.setAutoCommit(false);
			validate_data();

			if (varOrdFlag == 2) {
				throw new SQLException(
						"This job order is closed with invoice # "
								+ utils.getSqlValue(
										"select invoice_no from pur1 where keyfld="
												+ varSaleKeyfld, con));
			}
			String sqlIns = "insert into order1(PERIODCODE,LOCATION_CODE,ORD_NO,ORD_CODE,ORD_TYPE,ORD_DISCAMT,ORD_SHPDT,"
					+ "LCNO, ORD_EMPNO, REMARKS, SALEINV , LAST_MODIFIED_TIME ,ORD_FLAG ,ORD_DATE ,ORD_REF, ORD_REFNM ,ORD_AMT, ORDACC, DELIVEREDQTY, ORDERDQTY , STRA )  VALUES "
					+ "( :PERIODCODE,:LOCATION_CODE, :ORD_NO , :ORD_CODE , :ORD_TYPE, :ORD_DISCAMT, :ORD_SHPDT, "
					+ " :LCNO, :ORD_EMPNO, :REMARKS, :SALEINV , :LAST_MODIFIED_DATE , :ORD_FLAG , :ORD_DATE, :ORD_REF, :ORD_REFNM, :ORD_AMT, :ORDACC , :DELIVEREDQTY, :ORDERDQTY, :STRA )";
			String sqlUpd = "UPDATE ORDER1 SET PERIODCODE = :PERIODCODE , LOCATION_CODE = :LOCATION_CODE, "
					+ "ORD_CODE= :ORD_CODE, ORD_TYPE= :ORD_TYPE , ORD_DISCAMT = :ORD_DISCAMT,"
					+ "ORD_SHPDT=:ORD_SHPDT ,"
					+ "LCNO=:LCNO , ORD_EMPNO=:ORD_EMPNO , REMARKS=:REMARKS, SALEINV=:SALEINV , "
					+ "LAST_MODIFIED_TIME= :LAST_MODIFIED_DATE , ORD_FLAG=:ORD_FLAG "
					+ ",ORD_REF=:ORD_REF , ORD_REFNM=:ORD_REFNM, ORD_AMT=:ORD_AMT, ORDACC= :ORDACC , DELIVEREDQTY = :DELIVEREDQTY , ORDERDQTY=:ORDERDQTY , STRA=:STRA  "
					+ "WHERE ORD_CODE=:ORD_CODE  and ord_no=:ORD_NO  ";
			String sqlItems = "insert into order2 (PERIODCODE,LOCATION_CODE, ORD_DATE, ORD_NO,ORD_CODE,ORD_TYPE,ORD_POS,ORD_REFER,DESCR,ORD_PRICE,"
					+ "ORD_PKQTY, ORD_ALLQTY, ORD_PACK, ORD_PACKD, ORD_UNITD, ORD_FLAG ,SALEINV, DELIVEREDQTY ) VALUES "
					+ "(:PERIODCODE,:LOCATION_CODE, :ORD_DATE, :ORD_NO, :ORD_CODE,:ORD_TYPE,:ORD_POS,:ORD_REFER,:DESCR,:ORD_PRICE,"
					+ ":ORD_PKQTY, :ORD_ALLQTY, :ORD_PACK, :ORD_PACKD, :ORD_UNITD, :ORD_FLAG ,:SALEINV , 0 )";
			String sqlSvc = "insert into order2 (PERIODCODE,LOCATION_CODE,ORD_NO,ORD_DATE,ORD_CODE,ORD_TYPE,ORD_POS,ORD_REFER,DESCR,ORD_PRICE,"
					+ "ORD_PKQTY, ORD_ALLQTY, ORD_PACK, ORD_PACKD, ORD_UNITD, ORD_FLAG ,SALEINV , DELIVEREDQTY ) VALUES "
					+ "(:PERIODCODE,:LOCATION_CODE, :ORD_NO, :ORD_DATE, :ORD_CODE,:ORD_TYPE,:ORD_POS,:ORD_REFER,:DESCR,:ORD_PRICE,"
					+ ":ORD_PKQTY, :ORD_ALLQTY, :ORD_PACK, :ORD_PACKD, :ORD_UNITD, :ORD_FLAG ,:SALEINV ,:DELIVEREDQTY  )";

			String sqlDel = "begin delete from order2 where ord_code=:ORD_CODE and ord_no=:ORD_NO; end;";
			try {
				con.setAutoCommit(false);
				QueryExe qe = new QueryExe(sqlIns, con);
				QueryExe qeItem = new QueryExe(sqlItems, con);
				QueryExe qeSvc = new QueryExe(sqlSvc, con);
				QueryExe qeDel = new QueryExe(sqlDel, con);

				if (QRYSES.equals("")) {
					qe.setSqlStr(sqlIns);
					if (txtOrdNo.getValue() != null) {
						varOrdNo = Double.valueOf(txtOrdNo.getValue()
								.toString());
					} else {
						if (txtOrdNo.getValue() == null
								|| txtOrdNo.getValue().toString().isEmpty()) {
							txtOrdNo.setValue(utils.getSqlValue(
									"select nvl(max(ord_no),0)+1 from order1 where ord_code='"
											+ varOrdCode + "'", con));
						}
					}
				} else {
					qeDel.setParaValue("ORD_CODE", varOrdCode);
					qeDel.setParaValue("ORD_NO", varOrdNo);
					qeDel.execute();
					qe.setSqlStr(sqlUpd);
				}
				double amt = data_ord_svc.getSummaryOf("AMOUNT",
						localTableModel.SUMMARY_SUM);
				double amt2 = data_ord_item.getSummaryOf("AMOUNT",
						localTableModel.SUMMARY_SUM);

				String pcod = utils
						.getSqlValue(
								"select repair.getsetupvalue_2('CURRENT_PERIOD') from dual",
								con);
				qe.setParaValue("PERIODCODE", pcod);
				qe.setParaValue("LOCATION_CODE", ((dataCell) txtlocation
						.getValue()));
				qe.setParaValue("ORD_NO", varOrdNo);
				qe.setParaValue("ORD_CODE", varOrdCode);
				qe.setParaValue("ORD_DATE", txtOrDate.getValue());
				qe.setParaValue("ORD_TYPE", ((dataCell) txtJobType.getValue()));
				qe.setParaValue("ORD_DISCAMT", ((dataCell) txtInvType
						.getValue()));
				qe.setParaValue("ORD_SHPDT", txtDelivered.getValue());
				qe.setParaValue("ORDACC", varItemRcv);
				qe.setParaValue("LCNO", txtItemSerial.getValue());
				qe.setParaValue("ORD_REF", txtCustomer.getValue());
				qe.setParaValue("ORD_REFNM", txtCustomer.getDisplay());
				qe.setParaValue("ORD_AMT", amt + amt2);
				qe.setParaValue("DELIVEREDQTY", varFinishedRate);
				qe.setParaValue("ORDERDQTY", varCancelledRate);
				qe.setParaValue("STRA", txtStore.getValue());

				if (txtEmpNo.getValue() != null) {
					qe.setParaValue("ORD_EMPNO", txtEmpNo.getValue());
				} else {
					qe.setParaValue("ORD_EMPNO", null);
				}

				qe.setParaValue("REMARKS", utils.nvl(txtDescr.getValue(), ""));
				if (varSaleKeyfld == -1) {
					qe.setParaValue("SALEINV", varSaleKeyfld);
				} else {
					qe.setParaValue("SALEINV", null);
				}

				qe.setParaValue("LAST_MODIFIED_DATE", new java.util.Date(System
						.currentTimeMillis()));
				qe.setParaValue("ORD_FLAG", varOrdFlag);
				qe.execute();
				qeItem.parse();
				qeSvc.parse();
				for (int i = 0; i < data_ord_item.getRows().size(); i++) {
					qeItem.setParaValue("PERIODCODE", pcod);
					qeItem.setParaValue("ORD_CODE", varOrdCode);
					qeItem
							.setParaValue("LOCATION_CODE", txtlocation
									.getValue());
					qeItem.setParaValue("ORD_NO", txtOrdNo.getValue());
					qeItem.setParaValue("ORD_TYPE", 1);
					qeItem.setParaValue("ORD_POS", i + 1);
					qeItem.setParaValue("ORD_DATE", txtOrDate.getValue());
					qeItem.setParaValue("ORD_REFER", data_ord_item
							.getFieldValue(i, "ORD_REFER"));
					qeItem.setParaValue("DESCR", data_ord_item.getFieldValue(i,
							"DESCR"));
					qeItem.setParaValue("ORD_PKQTY", data_ord_item
							.getFieldValue(i, "ORD_PKQTY"));
					qeItem.setParaValue("ORD_ALLQTY", data_ord_item
							.getFieldValue(i, "ORD_ALLQTY"));
					qeItem.setParaValue("ORD_PRICE", data_ord_item
							.getFieldValue(i, "ORD_PRICE"));
					qeItem.setParaValue("ORD_PACK", data_ord_item
							.getFieldValue(i, "ORD_PACK"));
					qeItem.setParaValue("ORD_PACKD", data_ord_item
							.getFieldValue(i, "ORD_PACKD"));
					qeItem.setParaValue("ORD_UNITD", data_ord_item
							.getFieldValue(i, "ORD_UNITD"));
					qeItem.setParaValue("ORD_FLAG", varOrdFlag);
					if (varSaleKeyfld == -1) {
						qeItem.setParaValue("SALEINV", varSaleKeyfld);
					} else {
						qeItem.setParaValue("SALEINV", null);
					}
					qeItem.execute(false);
				}
				for (int i = 0; i < data_ord_svc.getRows().size(); i++) {
					qeSvc.setParaValue("PERIODCODE", pcod);
					qeSvc.setParaValue("ORD_CODE", varOrdCode);
					qeSvc.setParaValue("LOCATION_CODE", txtlocation.getValue());
					qeSvc.setParaValue("ORD_NO", txtOrdNo.getValue());
					qeSvc.setParaValue("ORD_TYPE", 2);
					qeSvc.setParaValue("ORD_POS", i + 1);
					qeSvc.setParaValue("ORD_DATE", txtOrDate.getValue());
					qeSvc.setParaValue("ORD_REFER", data_ord_svc.getFieldValue(
							i, "ORD_REFER"));
					qeSvc.setParaValue("DESCR", data_ord_svc.getFieldValue(i,
							"DESCR"));
					qeSvc.setParaValue("ORD_PKQTY", data_ord_svc.getFieldValue(
							i, "ORD_PKQTY"));
					qeSvc.setParaValue("ORD_ALLQTY", data_ord_svc
							.getFieldValue(i, "ORD_ALLQTY"));
					qeSvc.setParaValue("ORD_PRICE", data_ord_svc.getFieldValue(
							i, "ORD_PRICE"));
					qeSvc.setParaValue("ORD_PACK", data_ord_svc.getFieldValue(
							i, "ORD_PACK"));
					qeSvc.setParaValue("ORD_PACKD", data_ord_svc.getFieldValue(
							i, "ORD_PACKD"));
					qeSvc.setParaValue("ORD_UNITD", data_ord_svc.getFieldValue(
							i, "ORD_UNITD"));
					qeSvc.setParaValue("DELIVEREDQTY", data_ord_svc
							.getFieldValue(i, "DELIVEREDQTY"));
					qeSvc.setParaValue("ORD_FLAG", varOrdFlag);
					if (varSaleKeyfld == -1) {
						qeSvc.setParaValue("SALEINV", varSaleKeyfld);
					} else {
						qeSvc.setParaValue("SALEINV", null);
					}
					qeSvc.execute(false);

				}
				qe.close();
				qeItem.close();
				qeSvc.close();
				con.commit();
				saved_successed = true;
				parentLayout.getWindow().showNotification("Saved Successfully");
				if (showlist) {
					QRYSES = "";
					showInitView();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				parentLayout.getWindow().showNotification(ex.getMessage(),
						Notification.TYPE_ERROR_MESSAGE);
				try {
					con.rollback();
				} catch (SQLException ex1) {

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}
	}

	public void showInitView() {
		showJobOrdersView();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void showJobOrdersView() {
		mapActionStrs.clear();
		mapActionStrs.put("edit_job", "Edit Job Order");
		mapActionStrs.put("post_job", "Post Job order to Invoice..");
		mapActionStrs.put("uncancel_job", "Withdraw job order..");
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		resetFormLayout();
		try {
			Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
					.getDbConnection().setAutoCommit(false);
			final QueryView qv = new QueryView(con);
			qv
					.setSqlquery(" select ord_no,ord_ref,ord_refnm,to_char(ord_date,'dd/mm/rrrr') ord_date"
							+ ",ord_amt, ord_flag,"
							+ "(select max(descr) from items "
							+ "where (reference=order1.ordacc or order1.ordacc is null)) item_code,"
							+ "lcno serial,(select max(invoice_no) "
							+ "from pur1 where keyfld=order1.saleinv) Invoice_No , ord_shpdt,"
							+ "DELIVEREDQTY||'%' FINISHED , ORDERDQTY||'%' CANCELLED "
							+ "from order1 where ord_code=106 and ord_flag=:status order by ord_shpdt,ord_no");
			centralPanel.setHeight("100%");
			ResourceManager.addComponent(centralPanel, qv);
			qv.setSizeFull();
			Parameter pm = new Parameter("status");
			pm.setDescription("Status ");
			pm.getLovList().add(new dataCell("Closed, Invoiced", "2"));
			pm.getLovList().add(new dataCell("Opened", "1"));
			pm.getLovList().add(new dataCell("Not Approved", "0"));
			pm.getLovList().add(new dataCell("Cancellled", "-1"));
			pm.setValue("1");
			qv.addParameter(pm);
			qv.reportSetting.doStandard();
			qv.reportSetting.setTitle("Job Orders");
			qv.setHideCols(new String[] { "ORD_FLAG" });
			qv.getDataListners().add(new tableDataListner() {

				public void afterQuery() {

					qv.getLctb().getColByName("ORD_AMT").setNumberFormat(
							Channelplus3Application.getInstance()
									.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb().getColByName("ORD_SHPDT").setDateFormat(
							utils.FORMAT_SHORT_DATE);
					qv.getLctb().getColByName("ORD_SHPDT").setTitle(
							"Delivery Date");
					qv.getLctb().getColByName("ORD_REFNM").setTitle(
							"Customer Title");
					qv.getLctb().getColByName("ORD_REF").setTitle(
							"Customer Code");

				}

				public void afterVisual() {
					qv.getTable().setHeight("100%");
					qv.setColumnWidth("ORD_AMT", 120);
					qv.setColumnWidth("ORD_NO", 50);
					qv.getTable().addActionHandler(new Handler() {

						public Action[] getActions(Object target, Object sender) {
							if (target == null)
								return null;
							int rowno = Integer.valueOf(target.toString());
							List<Action> acts = new ArrayList<Action>();
							if (rowno < 0) {
								return null;
							}
							double flg = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "ORD_FLAG"))
									.doubleValue();
							if (flg == 1) {
								acts.add(new Action(mapActionStrs
										.get("edit_job")));
							}
							if (flg == -1) {
								acts.add(new Action(mapActionStrs
										.get("uncancel_job")));
							}
							Action[] aa = new Action[acts.size()];
							return acts.toArray(aa);
						}

						public void handleAction(Action action, Object sender,
								Object target) {

							int rowno = Integer.valueOf(target.toString());
							double flg = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "ORD_FLAG"))
									.doubleValue();
							String on = qv.getLctb().getFieldValue(rowno,
									"ORD_NO").toString();

							if (action.getCaption().equals(
									mapActionStrs.get("edit_job"))
									&& flg == 1) {
								QRYSES = on;
								initForm();
							}
							if (action.getCaption().equals(
									mapActionStrs.get("uncancel_job"))
									&& flg == -1) {
								try {
									utils.execSql(
											"update order1 set ord_flag=1 where ord_code="
													+ varOrdCode
													+ " and ord_no=" + on, con);
									con.commit();
									QRYSES = on;
									initForm();
								} catch (SQLException e) {
									e.printStackTrace();
									try {
										con.rollback();
									} catch (SQLException e1) {
									}
								}

							}
						}

					});
				}

				public void beforeQuery() {

				}

				public String calcSummary(List<String> qcGroup, qryColumn qc) {
					return "";
				}

				public String getCellStyle(qryColumn qc, int recordNo) {
					return "";
				}

			});
			Button bt = new NativeButton();
			bt.setIcon(new ThemeResource("img/newmenu.png"));
			bt.setDescription("New Voucher..");
			bt.setStyleName("toolbar");
			qv.getListUserButtons().add(bt);
			bt.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					initForm();
				}
			});
			qv.createView();
			centralPanel.requestRepaintAll();

		} catch (Exception ex) {
			Logger.getLogger(frmPurchaseCost.class.getName()).log(Level.SEVERE,
					null, ex);
			centralPanel.getWindow().showNotification("Error loading form ",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public void post_voucher() {
		try {
			con.setAutoCommit(false);
			if (txtDelivered.getValue() == null) {
				throw new SQLException(
						"Must assign delivered date or closed date");
			}
			if (varFinishedRate <= 0 && data_ord_svc.getRows().size() > 0) {
				throw new SQLException("Can not post without any finished work");
			}

			save_data(false);

			if (!QRYSES.isEmpty()) {

				utils.execSql("begin post_job_order('" + QRYSES
						+ "'); commit;end; ", con);
			} else {
				throw new SQLException(
						"Must save first in order to post voucher");

			}
			parentLayout.getWindow().showNotification("",
					"Posting successed, invoice created.",
					Notification.TYPE_ERROR_MESSAGE);

		} catch (SQLException ex) {

			Logger.getLogger(frmPurchaseCost.class.getName()).log(Level.SEVERE,
					null, ex);
			parentLayout.getWindow().showNotification("Error posting ",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);

			try {
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}