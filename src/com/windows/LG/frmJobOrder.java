package com.windows.LG;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog;
import com.doc.views.YesNoDialog.Callback;
import com.doc.views.Query.QueryView;
import com.doc.views.Query.tableDataListner;
import com.example.components.SearchField;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.FormLayoutManager;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.qryColumn;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.utilsVaadinPrintHandler;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.windows.Masters.Purchase.frmPurchaseCost;

public class frmJobOrder implements transactionalForm {
	private Connection con = null;
	private Window wndList = new Window();
	private VerticalLayout lstLayout = new VerticalLayout();
	private Map<String, String> mapActionStrs = new HashMap<String, String>();

	private AbstractLayout parentLayout = null;

	private Panel mainPanel = new Panel();
	private Panel tbSheetPanel = new Panel();
	private Panel tbSheetPanel2 = new Panel();
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayoutManager basicLayout = new FormLayoutManager("100%");
	private HorizontalLayout buttonLayout = new HorizontalLayout();

	private VerticalLayout docStatLayout = new VerticalLayout();
	private VerticalLayout docsClearLayout = new VerticalLayout();
	private VerticalLayout itemsLayout = new VerticalLayout();
	private VerticalLayout othersLayout = new VerticalLayout();

	private FormLayoutManager basicInfoLayout = new FormLayoutManager("100%",
			"-1px");
	private FormLayoutManager basicInfoLayout2 = new FormLayoutManager("100%",
			"-1px");

	private TabSheet tbs = new TabSheet();

	private List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	private List<FieldInfo> lstfldinfo2 = new ArrayList<FieldInfo>();
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
	private NativeButton cmdBackList = ControlsFactory.CreateCustomButton(
			"Cancel & Back", "img/back.png", "Back to list", "");

	private TextField txtOrdNo = ControlsFactory.CreateTextField(null,
			"ORD_NO", lstfldinfo, "100%", "");

	private ComboBox txtCompany = ControlsFactory.CreateListField(null, "LCNO",
			"select code,name from COMPANY order by code", lstfldinfo);

	private ComboBox txtLocation = ControlsFactory.CreateListField(null,
			"location_code",
			"select code,name from locations where flag=1 order by code",
			lstfldinfo);

	private ComboBox txtOrdKind = ControlsFactory.CreateListField(null,
			"ORD_TYPE", "", lstfldinfo, "100%", "1");
	private TextField txtCustReference = ControlsFactory.CreateTextField(null,
			"ORD_SHIP", lstfldinfo, "100%", "");
	private SearchField txtCust = ControlsFactory.CreateSearchField(null,
			"ORD_REF", lstfldinfo, "100%",
			"select code,name from c_ycust where flag=1  order by path",
			"CODE", "NAME");
	private TextField txtCustName = ControlsFactory.CreateTextField(null,
			"ORD_REFNM", lstfldinfo, "100%", "");

	private TextField txtCustInvoiceNo = ControlsFactory.CreateTextField(null,
			"ADJUST_DESCR", lstfldinfo, "100%", "");

	private SearchField txtCostCenter = ControlsFactory
			.CreateSearchField(null, "COSTCENT", lstfldinfo, "100%",
					"select code,title from accostcent1 order by path", "CODE",
					"TITLE");
	private DateField txtEndDate = ControlsFactory.CreateDateField(null,
			"ORD_SHPDT", lstfldinfo, "100%");

	private DateField txtOrdDate = ControlsFactory.CreateDateField(null,
			"ORD_DATE", lstfldinfo, "100%");

	private ComboBox txtActivity = ControlsFactory.CreateListField(null,
			"VALIDATIY", "#LG_ACTIVITY", lstfldinfo, "100%", null, true);

	private TextField txtOrigin = ControlsFactory.CreateTextField(null, "ATTN",
			lstfldinfo, "100%", "");

	private TextField txtDestination = ControlsFactory.CreateTextField(null,
			"PAYTERM", lstfldinfo, "100%", "");

	private CheckBox txtLgPerm = ControlsFactory.CreateCheckField(null,
			"LG_PERMANENT_EXEMPTION", "N", "Y", lstfldinfo2);

	private CheckBox txtLgTemp = ControlsFactory.CreateCheckField(null,
			"LG_TEMPORARY_IMPORT", "N", "Y", lstfldinfo2);

	private CheckBox chkLgDuty = ControlsFactory.CreateCheckField(null,
			"LG_DUTY_PAID", "N", "Y", lstfldinfo2);

	private CheckBox txtLgLreexp = ControlsFactory.CreateCheckField(null,
			"LG_L_RE_EXPORT", "N", "Y", lstfldinfo2);

	private TextField txtlgLlocal = ControlsFactory.CreateTextField(null,
			"LG_L_LOCAL", lstfldinfo2, "100%", "");

	private TextField txtLgLorigintruck = ControlsFactory.CreateTextField(null,
			"LG_L_ORIGIN_TRUCK", lstfldinfo2, "100%", "");

	private TextField txtLgLDriver = ControlsFactory.CreateTextField(null,
			"LG_DRIVER_NO", lstfldinfo2, "100%", "");

	private TextField txtLgTruckIq = ControlsFactory.CreateTextField(null,
			"LG_TRUCK_IQ", lstfldinfo2, "100%", "");

	private TextField txtLgTruckType = ControlsFactory.CreateTextField(null,
			"LG_TRUCK_TYPE", lstfldinfo2, "100%", "");

	private TextField txtLgShipper = ControlsFactory.CreateTextField(null,
			"LG_SHIPPER", lstfldinfo2, "100%", "");

	private TextField txtLgConsignee = ControlsFactory.CreateTextField(null,
			"LG_CONSIGNEE", lstfldinfo2, "100%", "");

	private TextField txtLgSVessel = ControlsFactory.CreateTextField(null,
			"LG_S_VESSEL_NAME", lstfldinfo2, "100%", "");
	private TextField txtLgSContainer = ControlsFactory.CreateTextField(null,
			"LG_S_CONTAINER_NO", lstfldinfo2, "100%", "");
	private TextField txtLgSMbl = ControlsFactory.CreateTextField(null,
			"LG_S_MBL", lstfldinfo2, "100%", "");
	private TextField txtLgSHbl = ControlsFactory.CreateTextField(null,
			"LG_S_HBL", lstfldinfo2, "100%", "");
	private TextField txtLgSFcl = ControlsFactory.CreateTextField(null,
			"LG_S_FCL_LCL_BB", lstfldinfo2, "100%", "");
	private TextField txtLgAAir = ControlsFactory.CreateTextField(null,
			"LG_A_AIRLINE", lstfldinfo2, "100%", "");
	private TextField txtLgAFlt = ControlsFactory.CreateTextField(null,
			"LG_A_FLT_NO", lstfldinfo2, "100%", "");
	private TextField txtLgAMawb = ControlsFactory.CreateTextField(null,
			"LG_A_MAWB", lstfldinfo2, "100%", "");
	private TextField txtLgAHawb = ControlsFactory.CreateTextField(null,
			"LG_A_HAWB", lstfldinfo2, "100%", "");
	private TextField txtLgAOffLoad = ControlsFactory.CreateTextField(null,
			"LG_OFFLOADING_DATE", lstfldinfo2, "100%", "");
	private TextField txtLgNoOfPcs = ControlsFactory.CreateTextField(null,
			"LG_NO_OF_PCS", lstfldinfo2, "100%", "");
	private TextField txtLgWeight = ControlsFactory.CreateTextField(null,
			"LG_WEIGHT", lstfldinfo2, "100%", "");
	private TextField txtLgMeasurment = ControlsFactory.CreateTextField(null,
			"LG_MEASUREMENT", lstfldinfo2, "100%", "");
	private TextField txtLgDescr = ControlsFactory.CreateTextField(null,
			"LG_DESCRIPTION", lstfldinfo2, "100%", "");
	private TextField txtLgNotes = ControlsFactory.CreateTextField(null,
			"LG_NOTES", lstfldinfo2, "100%", "");
	private DateField txtlgArrivalDate = ControlsFactory.CreateDateField(null,
			"LG_L_ARRIVAL_DATE", lstfldinfo2);
	private DateField txtlgDeliveryDate = ControlsFactory.CreateDateField(null,
			"LG_L_DELIVERY_DATE", lstfldinfo2);
	private DateField txtlgClearanceDate = ControlsFactory.CreateDateField(
			null, "LG_L_CLEARANCE_DATE", lstfldinfo2);

	private TableLayoutVaadin tbldocs = new TableLayoutVaadin(docsClearLayout);
	private TableLayoutVaadin tblitems = new TableLayoutVaadin(itemsLayout);
	private TableLayoutVaadin tblstatus = new TableLayoutVaadin(docStatLayout);

	private String QRYSES = "";
	private boolean listnerAdded = false;
	private String varItems = "ALL";
	private int varOrdCode = 106;
	private String varOrdLocation = "";
	private String varOrdPeriod = "";
	private int varOrdFlag = 2;
	private int varStore = 1;
	private String varApprovalPurOrdRequire = "FALSE";
	private int varNoOfSale = 0;
	private int varNoOfPur = 0;
	private boolean saved_successed = false;

	public frmJobOrder() {

	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		mainPanel.removeAllComponents();
		basicLayout.removeAll();
		tbldocs.resetLayout();
		tblitems.resetLayout();
		tblstatus.resetLayout();
		tbldocs.createView();
		tblitems.createView();
		tblstatus.createView();

	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		mainPanel.setWidth("700px");
		mainPanel.setHeight("100%");
		mainPanel.getContent().setHeight("-1px");
		tbSheetPanel.setWidth("100%");
		tbSheetPanel.setHeight("300px");
		tbSheetPanel.setContent(basicInfoLayout);
		tbSheetPanel.getContent().setHeight("-1px");

		tbSheetPanel2.setWidth("100%");
		tbSheetPanel2.setHeight("300px");
		tbSheetPanel2.setContent(basicInfoLayout2);
		tbSheetPanel2.getContent().setHeight("-1px");

		tbs.setWidth("100%");
		tbs.setSizeFull();

		// mainLayout.setMargin(true);
		mainLayout.setHeight("-1px");
		mainLayout.setWidth("100%");

		basicLayout.addStyleName("formLayout");

		txtOrdKind.removeAllItems();
		txtOrdKind.addItem(new dataCell("LAND ", "1"));
		txtOrdKind.addItem(new dataCell("SEA", "2"));
		txtOrdKind.addItem(new dataCell("AIR", "3"));

		txtCust.setShowValueOnly(true);

		tbldocs.setHeight("300px");
		tbldocs.getTable().setHeight("275px");
		tbldocs.hideToolbar();

		tblitems.setHeight("300px");
		tblitems.getTable().setHeight("275px");
		tblitems.hideToolbar();

		tblstatus.setHeight("300px");
		tblstatus.getTable().setHeight("275px");
		tblstatus.hideToolbar();

		tbs.addTab(tbSheetPanel, "Basic Info", null);
		tbs.addTab(tbSheetPanel2, "Others", null);
		tbs.addTab(docsClearLayout, "Clearance Docs", null);
		tbs.addTab(docStatLayout, "Status", null);

		// tbs.addTab(itemsLayout, "Services", null);

		ResourceManager.addComponent(centralPanel, mainPanel);
		ResourceManager.addComponent(mainPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);
		ResourceManager.addComponent(mainLayout, tbs);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdCopyVou);
		ResourceManager.addComponent(buttonLayout, cmdPrint);
		ResourceManager.addComponent(buttonLayout, cmdColumn);
		ResourceManager.addComponent(buttonLayout, cmdCls);
		ResourceManager.addComponent(buttonLayout, cmdBackList);

		basicLayout.setComponentsRow(1,
				"caption=Company,width=100%,expand=0.4", txtCompany,
				"immediate=true,width=100%,expand=1", "caption=.,expand=2.6");

		basicLayout.setComponentsRow(2,
				"caption=Location,width=100%,expand=0.4", txtLocation,
				"immediate=true,width=100%,expand=1",
				"caption=Fr.Type,width=100%,expand=0.4", txtOrdKind,
				"immediate=true,width=100%,expand=1",
				"caption=Cust.Ref,width=100%,expand=0.4", txtCustReference,
				"width=100%,expand=.8");

		basicLayout.setComponentsRow(3,
				"caption=Order No,width=100%,expand=0.4", txtOrdNo,
				"width=100%,expand=1",
				"caption=Order Date,width=100%,expand=0.4", txtOrdDate,
				"width=100%,expand=1",
				"caption=Cost Center,width=100% , expand=0.4", txtCostCenter,
				"width=100%,expand=.8");

		basicLayout.setComponentsRow(4,
				"caption=Customer,width=100%,expand=0.4", txtCust,
				"width=100%,expand=1,immediate=true", txtCustName,
				"width=100%,expand=1.2",
				"caption=End Date,width=100%,expand=0.4", txtEndDate,
				"width=100%,expand=1,enable=false");
		basicLayout.setComponentsRow(5,
				"caption=Activity,width=100%,expand=0.4", txtActivity,
				"width=100%,expand=0.6",
				"caption=Origin,width=100%,expand=0.4", txtOrigin,
				"width=100%,expand=.5",
				"caption=Destination,width=100% , expand=0.4", txtDestination,
				"width=100%,expand=.5 ",
				"caption=Cust Inv#,width=100%,expand=0.4 ", txtCustInvoiceNo,
				"width=100%,expand=.8");

		try {
			createViewInfos();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		applyColDocs();

		if (!listnerAdded) {

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

			cmdList.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_list();
				}
			});
			cmdDelete.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						if (QRYSES.isEmpty()) {
							load_data();
							return;
						}
						if (!QRYSES.isEmpty() && !utilsVaadin.canDeleteTrans("LGJO", con))
							throw new SQLException("Deletion Denied.. !");

						int n = Integer.valueOf(utils.nvl(
								utils.getSqlValue(
										"select nvl(count(*),0) from order1 ox"
												+ " where  ox.ord_code in (111,103) and "
												+ " ox.ord_type=2 AND ox.ORD_REFERENCE='"
												+ QRYSES + "'", con), "0"));
						if (n > 0)
							throw new SQLException(
									"found purchase or sales order !");
						int ord_flg = Integer.valueOf(utils.getSqlValue(
								"select nvl(max(ord_flag),-1) from order1 where ord_code="
										+ varOrdCode + " and ord_no='" + QRYSES
										+ "'", con));
						if (ord_flg == 1)
							throw new SQLException("Closed by other user");

						if (ord_flg < 0)
							throw new SQLException(
									"Not found this order due to delete by other user");

					} catch (SQLException e) {
						e.printStackTrace();
						parentLayout.getWindow()
								.showNotification(e.getMessage(),
										Notification.TYPE_ERROR_MESSAGE);
						return;
					}
					Callback cb = new Callback() {

						public void onDialogResult(boolean resultIsYes) {

							if (resultIsYes) {
								if (QRYSES.isEmpty()) {
									load_data();
									return;
								}

								delete_data(Double.valueOf(QRYSES));
								QRYSES = "";
								load_data();
								parentLayout.getWindow().showNotification(
										"Delete successfull");
							}

						}
					};
					parentLayout.getWindow().addWindow(
							new YesNoDialog("Confirmation",
									"Do you want to remove order no # "
											+ QRYSES + " ?", cb, "250px",
									"-1px"));
				}

			});
			txtCust.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					utilsVaadin.setValueByForce(txtCustName, "");

					if (txtCust.getValue() == null
							&& txtCust.getValue().toString().isEmpty())
						return;
					int cnt = Integer.valueOf(utils.getSqlValue(
							"select nvl(count(*),0) from c_ycust where parentcustomer='"
									+ txtCust.getValue() + "'", con));
					if (cnt > 0) {
						utilsVaadin.showMessage(parentLayout.getWindow(),
								"Parent customer not allow to choose",
								Notification.TYPE_ERROR_MESSAGE);
						return;
					}

					utilsVaadin.setValueByForce(txtCustName,
							txtCust.getDisplay());
					load_items();
				}
			});
			txtOrdKind.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						createViewInfos();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}

				}
			});

			basicInfoLayout.addListener(new LayoutClickListener() {

				public void layoutClick(LayoutClickEvent event) {
					if (!event.isDoubleClick())
						return;
					if (!(event.getClickedComponent() instanceof TextField))
						return;
					final FieldInfo fld = utils.findFieldInfoByObject(
							event.getClickedComponent(), lstfldinfo2);

					final Window wnd = new Window();
					final VerticalLayout la = new VerticalLayout();
					wnd.setContent(la);

					try {
						utilsVaadin.showSearch(
								la,
								new SelectionListener() {
									public void onSelection(TableView tv) {
										Channelplus3Application.getInstance()
												.getMainWindow()
												.removeWindow(wnd);

										if (tv.getSelectionValue() > -1) {
											try {
												int rn = tv.getSelectionValue();
												((AbstractField) fld.obj)
														.setValue(tv
																.getData()
																.getFieldValue(
																		rn,
																		"DESCR")
																.toString());
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
									}
								}, con,
								"select distinct descr,pos from relists where idlist='"
										+ fld.fieldName
										+ "' union select distinct "
										+ fld.fieldName + ",0 from lg_info "
										+ "order by 1,2", true);
					} catch (SQLException e) {
						e.printStackTrace();

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

			cmdBackList.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					resetFormLayout();
					showInitView();
				}
			});
			listnerAdded = true;
		}
	}

	protected void delete_data(double ordno) {
		try {
			int ord_flg = Integer.valueOf(utils.getSqlValue(
					"select nvl(max(ord_flag),-1) from order1 where ord_code="
							+ varOrdCode + " and  ord_no=" + ordno, con));
			if (ord_flg == 1)
				throw new SQLException("Closed by other user");
			if (ord_flg < 0)
				throw new SQLException(
						"Not found this order due to delete by other user");

			String sq = "begin delete from order1 where ord_code=:ORD_CODE"
					+ " and ord_no=:ORD_NO;"
					+ " delete from order2 where ord_code=:ORD_CODE"
					+ " and ord_no=:ORD_NO ; delete from lg_info where ord_no=:ORD_NO;"
					+ " delete from lg_docs_status where ord_no=:ORD_NO; "
					+ " delete from lg_docs_status2 where ord_no=:ORD_NO;end;";

			QueryExe qe = new QueryExe(sq, con);
			qe.setParaValue("ORD_NO", ordno);
			qe.setParaValue("ORD_CODE", varOrdCode);
			qe.execute();
			qe.close();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			parentLayout.getWindow().showNotification(e.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e1) {
			}

		}

	}

	public void setupValues() {
		varOrdLocation = Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars().get("DEFAULT_LOCATION");
		varStore = Integer.valueOf(Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars().get("DEFAULT_STORE"));
		varOrdPeriod = Channelplus3Application.getInstance().getFrmUserLogin()
				.getMapVars().get("CURRENT_PERIOD");
		varApprovalPurOrdRequire = utils.nvl(
				Channelplus3Application.getInstance().getFrmUserLogin()
						.getMapVars().get("APPROVAL_LG_JOB_ORDER_REQUIRE"),
				"FALSE");

	}

	protected void setupItemVars() {
		String strTyp = ((dataCell) txtOrdKind.getValue()).getValue()
				.toString();

		varItems = Channelplus3Application.getInstance().getFrmUserLogin()
				.getMapVars().get("LG_ITEMS_" + strTyp);
		if (varItems.toUpperCase().equals("ALL"))
			varItems = "%";
		else
			varItems = utils.getSqlValue(
					"select descr2||'%' from items where reference='"
							+ varItems + "'", con);
		load_items();
	}

	protected void createViewInfos() throws SQLException {
		if (txtOrdKind.getValue() == null)
			return;
		basicInfoLayout.removeAll();
		basicInfoLayout2.removeAll();

		String strTyp = ((dataCell) txtOrdKind.getValue()).getValue()
				.toString();
		setupItemVars();

		// land
		if (strTyp.equals("1")) {
			basicInfoLayout.setComponentsRow(1,
					"caption=Permanent Exemption,expand=.8", txtLgPerm,
					"expand=1.8", "expand=.2", "caption=No Of Pcs,expand=.5",
					txtLgNoOfPcs, "expand=.7");
			basicInfoLayout.setComponentsRow(2,
					"caption=Temporary Import,expand=.8", txtLgTemp,
					"expand=1.8", "expand=.2", "caption=Weight,expand=.5",
					txtLgWeight, "expand=.7");
			basicInfoLayout.setComponentsRow(3, "caption=Duty Paid,expand=.8",
					chkLgDuty, "expand=1.8", "expand=.2",
					"caption=Measurement,expand=.5", txtLgMeasurment,
					"expand=.7");
			basicInfoLayout.setComponentsRow(4, "caption=Re Export,expand=.8",
					txtLgLreexp, "expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(5, "caption=Local,expand=.8",
					txtlgLlocal, "expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(6,
					"caption=No of Truck,expand=.8", txtLgLorigintruck,
					"expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(7, "caption=Driver,expand=.8",
					txtLgLDriver, "expand=1.8", "expand=1.4");
			basicInfoLayout
					.setComponentsRow(8, "caption=IQ TRUCK,expand=.8",
							txtLgTruckIq, "expand=1.8", "expand=.2",
							"caption=Truck Type,expand=.5", txtLgTruckType,
							"expand=.7");

			basicInfoLayout.setComponentsRow(9, "caption=Shipper,expand=.8",
					txtLgShipper, "expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(10, "caption=Consignee,expand=.8",
					txtLgConsignee, "expand=1.8", "expand=1.4");

		}
		// sea
		if (strTyp.equals("2")) {
			basicInfoLayout.setComponentsRow(1,
					"caption=Permanent Exemption,expand=.8", txtLgPerm,
					"expand=1.8", "expand=.2", "caption=No Of Pcs,expand=.5",
					txtLgNoOfPcs, "expand=.7");
			basicInfoLayout.setComponentsRow(2,
					"caption=Temporary Import,expand=.8", txtLgTemp,
					"expand=1.8", "expand=.2", "caption=Weight,expand=.5",
					txtLgWeight, "expand=.7");
			basicInfoLayout.setComponentsRow(3, "caption=Duty Paid,expand=.8",
					chkLgDuty, "expand=1.8", "expand=.2",
					"caption=Measurement,expand=.5", txtLgMeasurment,
					"expand=.7");
			basicInfoLayout.setComponentsRow(4,
					"caption=Vessel Name,expand=.8", txtLgSVessel,
					"expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(5,
					"caption=Container No,expand=.8", txtLgSContainer,
					"expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(6,
					"caption=No of Truck,expand=.8", txtLgLorigintruck,
					"expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(7, "caption=MBL,expand=.8",
					txtLgSMbl, "expand=1.8", "expand=1.4");

			basicInfoLayout.setComponentsRow(8, "caption=HBL,expand=.8",
					txtLgSHbl, "expand=1.8", "expand=1.4");
			basicInfoLayout
					.setComponentsRow(9, "caption=IQ TRUCK,expand=.8",
							txtLgTruckIq, "expand=1.8", "expand=.2",
							"caption=Truck Type,expand=.5", txtLgTruckType,
							"expand=.7");
			basicInfoLayout.setComponentsRow(10,
					"caption=FCL/LCL/BB ,expand=.8", txtLgSFcl, "expand=1.8",
					"expand=1.4");
			basicInfoLayout.setComponentsRow(11, "caption=Shipper,expand=.8",
					txtLgShipper, "expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(12, "caption=Consignee,expand=.8",
					txtLgConsignee, "expand=1.8", "expand=1.4");

		}

		if (strTyp.equals("3")) {
			basicInfoLayout.setComponentsRow(1,
					"caption=Permanent Exemption,expand=.8", txtLgPerm,
					"expand=1.8", "expand=.2", "caption=No Of Pcs,expand=.5",
					txtLgNoOfPcs, "expand=.7");
			basicInfoLayout.setComponentsRow(2,
					"caption=Temporary Import,expand=.8", txtLgTemp,
					"expand=1.8", "expand=.2", "caption=Weight,expand=.5",
					txtLgWeight, "expand=.7");
			basicInfoLayout.setComponentsRow(3, "caption=Duty Paid,expand=.8",
					chkLgDuty, "expand=1.8", "expand=.2",
					"caption=Measurement,expand=.5", txtLgMeasurment,
					"expand=.7");
			basicInfoLayout.setComponentsRow(4, "caption=Airline,expand=.8",
					txtLgAAir, "expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(5, "caption=Flt No,expand=.8",
					txtLgAFlt, "expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(6,
					"caption=No of Truck,expand=.8", txtLgLorigintruck,
					"expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(7, "caption=MAWB,expand=.8",
					txtLgAMawb, "expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(8, "caption=HAWB,expand=.8",
					txtLgAHawb, "expand=1.8", "expand=1.4");
			basicInfoLayout
					.setComponentsRow(9, "caption=IQ TRUCK,expand=.8",
							txtLgTruckIq, "expand=1.8", "expand=.2",
							"caption=Truck Type,expand=.5", txtLgTruckType,
							"expand=.7");
			basicInfoLayout.setComponentsRow(10, "caption=Shipper,expand=.8",
					txtLgShipper, "expand=1.8", "expand=1.4");
			basicInfoLayout.setComponentsRow(11, "caption=Consignee,expand=.8",
					txtLgConsignee, "expand=1.8", "expand=1.4");

		}

		basicInfoLayout2
				.setComponentsRow(1, "caption=Descr,expand=.8", txtLgDescr,
						"expand=1.8", "expand=.2", "caption=Remarks,expand=.5",
						txtLgNotes, "expand=.7,width=100%");
		basicInfoLayout2.setComponentsRow(2, "caption=Arrival Date,expand=.8",
				txtlgArrivalDate, "expand=1.8", "expand=1.4");
		basicInfoLayout2.setComponentsRow(3, "caption=Delivery Date,expand=.8",
				txtlgDeliveryDate, "expand=1.8", "expand=1.4");

		basicInfoLayout2.setComponentsRow(4,
				"caption=Clearance Date,expand=.8", txtlgClearanceDate,
				"expand=1.8", "expand=1.4");

		for (Iterator iterator = lstfldinfo2.iterator(); iterator.hasNext();) {
			FieldInfo fld = (FieldInfo) iterator.next();
			if (fld.obj instanceof TextField)
				((TextField) fld.obj)
						.setInputPrompt("double click here , list items #"
								+ fld.fieldName);
			if (fld.obj instanceof ComboBox)
				((ComboBox) fld.obj)
						.setInputPrompt("double click here , list items #"
								+ fld.fieldName);

		}
	}

	private void load_items() {
		/*
		 * try { String dcd =
		 * "decode((select max(1) from order2 where ord_code=106 and ord_no='" +
		 * QRYSES + "' and ord_refer=reference),1,'Y','N') "; tblitems.data
		 * .executeQuery( "select  " + dcd +
		 * " selection,reference ord_refer,descr,packd ord_packd,rownum ord_pos,"
		 * + " pack ord_pack ,unitd ord_unitd,1 ord_pkqty," +
		 * " 1 ord_allqty,0 ord_unqty," +
		 * " fc_price ord_price, 0 ord_discamt, '' costcent" +
		 * "  from lg_custitems,items  " +
		 * " where  cost_item=reference and  code='" + txtCust.getValue() +
		 * "' and childcounts=0 and flag=1 and (descr2 like '" + varItems +
		 * "') order by descr2", true); if (!QRYSES.isEmpty()) for (int i = 0; i
		 * < tblitems.data.getRowCount(); i++) {
		 * 
		 * }
		 * 
		 * tblitems.fill_table(); } catch (SQLException ex) {
		 * ex.printStackTrace();
		 * parentLayout.getWindow().showNotification("ERROR loading",
		 * ex.getMessage(), Notification.TYPE_ERROR_MESSAGE); }
		 */
	}

	protected void applyColDocs() {
		tbldocs.listFields.clear();

		ColumnProperty cp = new ColumnProperty();
		cp.colname = "DOC_NAME";
		cp.descr = "Document";
		cp.col_class = Label.class;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_format = "NONE";
		cp.pos = 1;
		cp.display_width = 150;
		tbldocs.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "DOC_ORIGINAL";
		cp.descr = "Original";
		cp.data_type = Parameter.DATA_TYPE_NUMBER;
		cp.col_class = TextField.class;
		cp.display_format = utils.FORMAT_QTY;
		cp.defaultValue = "0";
		cp.pos = 2;
		cp.display_width = 50;

		tbldocs.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "DOC_COPY";
		cp.descr = "Copy";
		cp.col_class = TextField.class;
		cp.data_type = Parameter.DATA_TYPE_NUMBER;
		cp.display_format = utils.FORMAT_QTY;
		cp.defaultValue = "0";
		cp.pos = 3;
		cp.display_width = 50;
		tbldocs.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "DOC_RECIEVED";
		cp.descr = "Recieved";
		cp.data_type = Parameter.DATA_TYPE_NUMBER;
		cp.col_class = TextField.class;
		cp.display_format = utils.FORMAT_QTY;
		cp.defaultValue = "0";
		cp.pos = 4;
		cp.display_width = 50;
		tbldocs.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "DOC_DATE";
		cp.descr = "Date";
		cp.data_type = Parameter.DATA_TYPE_DATETIME;
		cp.col_class = DateField.class;
		cp.display_format = utils.FORMAT_SHORT_DATE;
		cp.pos = 5;
		cp.display_width = 100;
		tbldocs.listFields.add(cp);

		tblitems.listFields.clear();

		cp = new ColumnProperty();
		cp.colname = "SELECTION";
		cp.descr = "Select";
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.col_class = CheckBox.class;
		cp.display_format = "NONE";
		cp.defaultValue = "N";
		cp.onCheckValue = "Y";
		cp.onUnCheckValue = "N";
		cp.pos = 1;
		cp.display_width = 50;
		tblitems.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "DESCR";
		cp.descr = "Item";
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.col_class = Label.class;
		cp.display_format = "NONE";
		cp.defaultValue = "N";
		cp.onCheckValue = "Y";
		cp.onUnCheckValue = "N";
		cp.pos = 2;
		cp.display_width = 150;
		tblitems.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "ORD_PRICE";
		cp.descr = "PRICE";
		cp.data_type = Parameter.DATA_TYPE_NUMBER;
		cp.col_class = TextField.class;
		cp.display_format = Channelplus3Application.getInstance()
				.getFrmUserLogin().FORMAT_MONEY;
		cp.defaultValue = "0";
		cp.pos = 3;
		cp.display_width = 100;
		tblitems.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "STATUS_VAR";
		cp.descr = "Status";
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.col_class = Label.class;
		cp.display_format = "NONE";
		cp.defaultValue = "";
		cp.pos = 1;
		cp.display_width = 100;
		tblstatus.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "STATUS_DAT";
		cp.descr = "Date";
		cp.data_type = Parameter.DATA_TYPE_DATETIME;
		cp.col_class = DateField.class;
		cp.display_format = utils.FORMAT_SHORT_DATE;
		cp.pos = 2;
		cp.display_width = 100;
		tblstatus.listFields.add(cp);

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
													.getFieldValue(rn, "ord_no")
													.toString();
											load_data();
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select ord_no,ord_refnm customer,"
									+ " to_char(ord_date,'dd/mm/rrrr') ord_date, ord_ship Reference  from order1 "
									+ " where ord_code=" + varOrdCode
									+ " order by ord_no", true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void delete_data() {

	}

	public void init() {

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {

		} catch (Exception e) {
		}

		setupValues();

		createView();

		load_data();

	}

	public void load_data() {
		utilsVaadin.resetValues(basicLayout, false, false);
		utilsVaadin.setDefaultValues(lstfldinfo, false);
		utilsVaadin.setDefaultValues(lstfldinfo2, true, true);
		tbs.setSelectedTab(0);
		varOrdFlag = 2;
		varNoOfPur = 0;
		varNoOfSale = 0;
		txtOrdNo.setEnabled(true);
		txtOrdNo.setReadOnly(false);
		cmdSave.setEnabled(true);
		cmdDelete.setEnabled(true);
		txtCustReference.setReadOnly(false);
		txtCust.setEnabled(true);
		txtOrdDate.setReadOnly(false);

		txtOrdNo.setValue(utils.getSqlValue(
				"select nvl(max(ord_no),0)+1 from order1 where ord_code='"
						+ varOrdCode + "'", con));
		if (txtOrdDate.getValue() == null) {
			txtOrdDate.setValue(new Date(System.currentTimeMillis()));
		}
		((Date) txtOrdDate.getValue()).setTime(System.currentTimeMillis());

		txtLocation.setValue(utilsVaadin.findByValue(txtLocation,
				varOrdLocation));
		txtCompany.setNullSelectionAllowed(false);
		txtCompany.setValue(txtCompany.getItemIds().toArray()[0]);

		try {
			if (!QRYSES.isEmpty()) {
				PreparedStatement pso = con
						.prepareStatement(
								"SELECT order1.*, "
										+ "(select nvl(count(*),0) from order1 ox"
										+ " where  ox.ord_code in (111,103) and ox.ord_type=2 "
										+ " AND ox.ORD_REFERENCE=ORDER1.ORD_NO) noof"
										+ " FROM ORDER1 WHERE ORD_CODE="
										+ varOrdCode + " AND ORD_NO='" + QRYSES
										+ "'",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pso.executeQuery();
				PreparedStatement psl = con.prepareStatement(
						"SELECT *  FROM lg_info WHERE ORD_NO=" + QRYSES,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rsl = psl.executeQuery();

				utilsVaadin.assignValues(rst, lstfldinfo);
				utilsVaadin.assignValues(rsl, lstfldinfo2);

				varOrdFlag = rst.getInt("ORD_FLAG");
				varNoOfPur = rst.getInt("noof");

				if (varNoOfPur > 0 || varOrdFlag == 1) {
					// cmdSave.setEnabled(false);
					cmdDelete.setEnabled(false);
					txtCust.setEnabled(false);
					txtOrdDate.setReadOnly(true);
				}

				pso.close();
				psl.close();
				fetchData();

				txtOrdNo.setReadOnly(true);
				// /txtCustReference.setReadOnly(true);

				tbldocs.data
						.executeQuery(
								"select descr doc_name, doc_original, doc_copy,"
										+ "nvl(lg_docs_status.doc_date,sysdate) doc_date, "
										+ "doc_recieved ,pos doc_no,DOC_FLAG from lg_docs_status,relists "
										+ "where DOC_FLAG=1 AND idlist='LG_CLEARANCE_DOCS' and "
										+ "descr=lg_docs_status.doc_name(+) and ord_no='"
										+ QRYSES + "' order by pos", true);

				tblstatus.data.executeQuery(
						"select pos, status_var,status_dat from LG_DOCS_STATUS2 where ord_no='"
								+ QRYSES + "' order by pos", true);

			} else {
				tbldocs.data
						.executeQuery(
								"select descr doc_name, 0 doc_original, 0 doc_copy,"
										+ "sysdate doc_date, "
										+ "0 doc_recieved,pos doc_no,1 DOC_FLAG from relists "
										+ "where idlist='LG_CLEARANCE_DOCS' order by pos",
								true);
				tblstatus.data.executeQuery(
						"select 0 pos, descr status_var,sysdate status_dat from relists"
								+ " where idlist='LG_DOC_STATUS' order by pos",
						true);
				utils.setAllColumnValue(tblstatus.data, "STATUS_DAT", null);

			}

			tbldocs.fill_table();
			tblstatus.fill_table();

			load_items();

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public void fetchData() {
		if (txtCostCenter.getValue() != null
				&& !txtCostCenter.getValue().toString().isEmpty())
			txtCostCenter.setDisplay(utils.getSqlValue(
					"select title from accostcent1 where code='"
							+ txtCostCenter.getValue() + "'", con));

	}

	public void print_data() {
		try {
			utilsVaadinPrintHandler.printLGJob(con, QRYSES);
		} catch (Exception ex) {
			mainLayout.getWindow().showNotification("Unable to print:",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			Logger.getLogger(frmPurchaseCost.class.getName()).log(Level.SEVERE,
					null, ex);
		}

	}

	public void validate_data() throws Exception {
		if (txtCompany.getValue() == null)
			throw new SQLException("Must specify company");
		if (txtCust.getValue() == null
				|| txtCust.getValue().toString().isEmpty())
			throw new SQLException("Customer must have value");
		if (txtCustReference.getValue() == null
				|| txtCustReference.getValue().toString().isEmpty())
			throw new SQLException("Customer reference must have value");
		if (txtLocation.getValue() == null)
			throw new SQLException("Location must have value");

		// if (tblitems.data.getCountOf("SELECTION", "Y") <= 0)
		// throw new SQLException("You must select some services");

		/*
		 * int n = Integer.valueOf(utils.nvl(
		 * utils.getSqlValue("select nvl(count(*),0) from order1 ox" +
		 * " where  ox.ord_code in (111,103) and ord_type=2 " +
		 * " AND ox.ORD_REFERENCE='" + QRYSES + "'", con), "0"));
		 * 
		 * if (n > 0) throw new SQLException("found #" + n +
		 * " purchase or sales order !");
		 */

		if (QRYSES.isEmpty()) {
			String s = utils.nvl(utils.getSqlValue(
					"select ord_ref||'-'||ord_refnm||' - Date # '||ord_date from order1 ox "
							+ " where  ox.ord_code =" + varOrdCode
							+ " AND ox.ord_no='" + txtOrdNo.getValue() + "'",
					con), "");

			if (!s.isEmpty())
				throw new SQLException("found #" + s + " job order !");
		}
		if (txtActivity.getValue() != null) {
			utils.insert_list(con, txtActivity.getValue() + "",
					txtActivity.getValue() + "", "LG_ACTIVITY");
		}
	}

	public void save_data() {
		save_data(true);
	}

	private void CreateCostCenter() throws SQLException {
		if (!QRYSES.isEmpty())
			return;
		if (txtCostCenter.getValue() != null
				&& !txtCostCenter.getValue().toString().isEmpty())
			return;
		String pc = utils.nvl(Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars().get("LG_PARENT_CS"), "");
		if (pc.toUpperCase().equals("NONE"))
			pc = "";

		con.setAutoCommit(false);
		QueryExe qe = new QueryExe(con);
		Date dt = new Date(System.currentTimeMillis());
		DecimalFormat dc = new DecimalFormat("000");
		String cod = pc + txtOrdNo.getValue() + "";
		String pt = utils.generatePath(pc, cod + "", "ACCOSTCENT1", "CODE",
				"PATH", con);
		qe.setParaValue("path", pt);
		qe.setParaValue("LEVELNO", utils.countString(pt, "\\") - 1);
		qe.setParaValue("EXPAMT", "0");
		qe.setParaValue("EARAMT", "0");
		qe.setParaValue("TYPE", "0");
		qe.setParaValue("CLOSECTG", "Y");
		qe.setParaValue("CODE", cod);
		qe.setParaValue("PARENTCOSTCENT", pc);
		qe.setParaValue("TITLE", "JOB ORDER # " + txtOrdNo);
		qe.setParaValue("KEYFLD", utils.getSqlValue(
				"select nvl(max(keyfld),0)+1 from accostcent1", con));
		qe.setParaValue("CHILDCOUNT", 0);
		qe.setParaValue("USECOUNT", 0);
		qe.AutoGenerateInsertStatment("accostcent1"); // if
		qe.execute();
		qe.close();
		utils.execSql("update order1 set costcent='" + cod + "' where ord_no='"
				+ txtOrdNo.getValue() + "' and ord_code=" + varOrdCode, con);
		con.commit();

	}

	private void save_data(boolean cls) {
		saved_successed = false;
		try {
			if (!QRYSES.isEmpty() && !utilsVaadin.canEditTrans("LGJO", con))
				throw new Exception("Editing Denied !");

			validate_data();
			con.setAutoCommit(false);
			DecimalFormat df = new DecimalFormat(Channelplus3Application
					.getInstance().getFrmUserLogin().FORMAT_MONEY);
			QueryExe qe = new QueryExe(con);
			QueryExe qeinfo = new QueryExe(con);
			for (Iterator iterator = lstfldinfo.iterator(); iterator.hasNext();) {
				FieldInfo fl = (FieldInfo) iterator.next();
				if (!(fl.obj instanceof CheckBox)) {
					qe.setParaValue(fl.fieldName,
							((AbstractField) fl.obj).getValue());
				} else {
					qe.setParaValue(fl.fieldName, (((CheckBox) fl.obj)
							.booleanValue() ? fl.valueOnTrue : fl.valueOnFalse));
				}
			}

			for (Iterator iterator = lstfldinfo2.iterator(); iterator.hasNext();) {
				FieldInfo fl = (FieldInfo) iterator.next();
				if (!(fl.obj instanceof CheckBox)) {
					qeinfo.setParaValue(fl.fieldName,
							((AbstractField) fl.obj).getValue());
				} else {
					qeinfo.setParaValue(fl.fieldName, (((CheckBox) fl.obj)
							.booleanValue() ? fl.valueOnTrue : fl.valueOnFalse));
				}
			}

			Date dt = new Date(System.currentTimeMillis());
			qe.setParaValue("PERIODCODE", varOrdPeriod);
			qe.setParaValue("ORD_CODE", varOrdCode);
			qe.setParaValue("YEAR", "Y");
			qe.setParaValue("ORD_FLAG", varOrdFlag);
			qe.setParaValue("DELIVEREDQTY", "0");
			qe.setParaValue("ORDERDQTY", "0");
			qe.setParaValue("DELIVERED_FREEQTY", "0");
			qe.setParaValue("HAVE_ADJUSTMENT", "0");
			qe.setParaValue("ADJUST_AMOUNT", "0");
			qe.setParaValue("ADJUST_CURRENCY", "0");
			qe.setParaValue("ADJUST_RATE", "0");
			qe.setParaValue("ORD_AMT", df.parse(utils.nvl(null, "0")));
			qe.setParaValue("ORD_DISCAMT", df.parse(utils.nvl(null, "0")));

			qeinfo.setParaValue("ORD_NO", txtOrdNo.getValue());
			qeinfo.setParaValue("ORD_CODE", varOrdCode);
			qeinfo.setParaValue("ORD_DATE", txtOrdDate.getValue());

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("ORDER1"); // if
				qeinfo.AutoGenerateInsertStatment("LG_INFO"); // if
			} else {
				qe.AutoGenerateUpdateStatment("ORDER1", "'ORD_NO','ORD_CODE'",
						" WHERE ORD_NO=:ORD_NO AND ORD_CODE=:ORD_CODE ");
				qeinfo.AutoGenerateUpdateStatment("LG_INFO", "'ORD_NO'",
						" WHERE ORD_NO=:ORD_NO");
			}
			qe.execute();
			qeinfo.execute();
			qe.close();
			qeinfo.close();

			utils.execSql("delete from lg_docs_status where ord_no='"
					+ txtOrdNo.getValue() + "'", con);

			utils.execSql("delete from lg_docs_status2 where ord_no='"
					+ txtOrdNo.getValue() + "'", con);

			utils.execSql("delete from order2 where ord_code=" + varOrdCode
					+ " and ord_no='" + txtOrdNo.getValue() + "'", con);

			Map<String, Object> mpl = new HashMap<String, Object>();
			mpl.put("ORD_NO", txtOrdNo.getValue());
			mpl.put("DOC_FLAG", 1);
			tbldocs.insert_to_table("lg_docs_status", mpl, "", "");

			TableLayoutVaadin tbl = new TableLayoutVaadin(null);
			tbl.data.setCols(tblitems.data.getColumnCount());
			tbl.data.getQrycols().addAll(tblitems.data.getQrycols());

			int cc = 0;
			for (int i = 0; i < tblitems.data.getRows().size(); i++)
				if (tblitems.data.getFieldValue(i, "SELECTION").toString()
						.equals("Y")) {
					tbl.data.getRows().add(tblitems.data.getRows().get(i));
					tbl.data.setFieldValue(tbl.data.getRowCount() - 1,
							"ORD_POS", BigDecimal.valueOf(++cc));
				}
			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("YEAR", "2000");
			mp.put("ORD_CODE", varOrdCode);
			mp.put("ORD_TYPE", txtOrdKind.getValue());
			mp.put("ORD_DATE", txtOrdDate.getValue());
			mp.put("ORD_FLAG", varOrdFlag);
			mp.put("ORD_NO", txtOrdNo.getValue());
			mp.put("PERIODCODE", varOrdPeriod);
			mp.put("LOCATION_CODE", ((dataCell) txtLocation.getValue()));
			tbl.insert_to_table(
					"order2",
					mp,
					tbl.data.getColumnsString()
							+ ",YEAR,ORD_CODE,ORD_TYPE,ORD_DATE,ORD_FLAG,ORD_NO,PERIODCODE,LOCATION_CODE",
					"SELECTION , ORD_ITMAVER, DELIVEREDQTY , ORDEREDQTY");
			Map<String, Object> mpd = new HashMap<String, Object>();
			for (int i = 0; i < tblstatus.data.getRows().size(); i++)
				tblstatus.data.setFieldValue(i, "POS",
						BigDecimal.valueOf(i + 1));

			mpd.put("ORD_NO", txtOrdNo.getValue());

			tblstatus.insert_to_table("LG_DOCS_STATUS2", mpd,
					tblstatus.data.getColumnsString() + ",ORD_NO", "");
			CreateCostCenter();
			con.commit();
			saved_successed = true;
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtOrdNo.getValue().toString();
			}

			load_data();
			tbl.data.clearALl();
		} catch (Exception ex) {

			ex.printStackTrace();
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void showInitView() {
		showJobOrdView();
	}

	public void showJobOrdView() {
		mapActionStrs.clear();
		mapActionStrs.put("create_po", "Create P.O.");
		mapActionStrs.put("edit_po", "Edit P.O.");
		mapActionStrs.put("delete_po", "Delete P.O.");
		mapActionStrs.put("create_so", "Create S.O.");
		mapActionStrs.put("edit_so", "Edit S.O.");
		mapActionStrs.put("delete_so", "Delete S.O.");
		mapActionStrs.put("edit_jo_n", "Edit Job Order [New window]");
		mapActionStrs.put("edit_jo", "Edit Job Order");
		mapActionStrs.put("close_jo", "Close Job Order");
		mapActionStrs.put("print_so", "Print S.O.");
		mapActionStrs.put("print_po_all", "Print All P.O.");
		mapActionStrs.put("print_jo", "Print Job Order");
		mapActionStrs.put("pays", "DE  Payments Handling..");
		mapActionStrs.put("add_info_so", "Add info. SO..");

		mapActionStrs.put("create_sr", "Create Sales Return ");
		mapActionStrs.put("create_pr", "Create Purchase Return ");

		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();

		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;

		try {
			Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
					.getDbConnection().setAutoCommit(false);
			final QueryView qv = new QueryView(con);
			qv.setSqlquery("select *from v_lg_jo where " + " ord_flag=:STATUS "
					+ " ORDER BY ord_ref,ord_refnm,ORD_DATE,ORD_NO");
			centralPanel.setHeight("100%");

			// centralPanel.addComponent(qv);
			ResourceManager.addComponent(centralPanel, qv);
			qv.setSizeFull();
			Parameter pm = new Parameter("STATUS");
			pm.setDescription("Status ");
			pm.getLovList().add(new dataCell("Opened", "2"));
			pm.getLovList().add(new dataCell("Closed", "1"));
			pm.setValue("2");
			qv.addParameter(pm);
			qv.reportSetting.doStandard();
			qv.reportSetting.setTitle("Job Order");
			qv.setHideCols(new String[] { "ORD_FLAG" });
			qv.doExecuteOnParameterChange(true);
			NativeButton bt = ControlsFactory.CreateToolbarButton(
					"img/add.png", "New");
			bt.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					centralPanel.removeAllComponents();
					QRYSES = "";
					initForm();
				}
			});

			qv.getListUserButtons().add(bt);
			qv.getDataListners().add(new tableDataListner() {

				public void afterQuery() {
					qv.getLctb().getColByName("ORD_REFNM")
							.setTitle("Customer Name");
					qv.getLctb().getColByName("ORD_REF")
							.setTitle("Customer Code");
					qv.getLctb().getColByName("ORD_DATE")
							.setDateFormat(utils.FORMAT_SHORT_DATE);
					qv.getLctb()
							.getColByName("TOTAL_SALES")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb()
							.getColByName("TOTAL_PURCHASE")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb()
							.getColByName("COST_IN_HAND")
							.setNumberFormat(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().FORMAT_MONEY);

				}

				public void afterVisual() {
					qv.getTable().setHeight("100%");
					qv.setColumnWidth("ORD_REFNM", 120);
					qv.setColumnWidth("ORD_REF", 40);
					qv.setColumnWidth("ORD_NO", 40);
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
							double so = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "NO_OF_SO"))
									.doubleValue();
							double po = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "NO_OF_PO"))
									.doubleValue();

							if (flg == 0) {
								acts.clear();
							}

							if (flg == 2) {
								acts.add(new Action(mapActionStrs
										.get("create_po")));
								acts.add(new Action(mapActionStrs
										.get("create_so")));
								acts.add(new Action("-"));
								acts.add(new Action(mapActionStrs
										.get("create_sr")));
								acts.add(new Action(mapActionStrs
										.get("create_pr")));
								acts.add(new Action("-"));
								acts.add(new Action(mapActionStrs
										.get("edit_jo_n")));
								acts.add(new Action(mapActionStrs
										.get("edit_jo")));
								acts.add(new Action("-"));
								acts.add(new Action(mapActionStrs
										.get("close_jo")));
								acts.add(new Action("-"));
								if (so > 0) {
									acts.add(new Action(mapActionStrs
											.get("edit_so")));
									acts.add(new Action(mapActionStrs
											.get("add_info_so")));

								}
								if (po > 0) {
									acts.add(new Action(mapActionStrs
											.get("edit_po")));
									acts.add(new Action(mapActionStrs
											.get("print_po_all")));

								}
								acts.add(new Action("-"));
								if (so > 0)
									acts.add(new Action(mapActionStrs
											.get("print_so")));

								acts.add(new Action(mapActionStrs
										.get("print_jo")));

								acts.add(new Action("-"));
								acts.add(new Action(mapActionStrs.get("pays")));

							}
							if (flg == 1) {
								if (so > 0)
									acts.add(new Action(mapActionStrs
											.get("print_so")));

								acts.add(new Action(mapActionStrs
										.get("print_jo")));
							}

							Action[] ac_ar = new Action[acts.size()];
							return acts.toArray(ac_ar);
						}

						public void handleAction(Action action, Object sender,
								Object target) {
							if (target == null)
								return;
							int rowno = Integer.valueOf(target.toString());
							double flg = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "ORD_FLAG"))
									.doubleValue();
							double on = ((BigDecimal) qv.getLctb()
									.getFieldValue(rowno, "ORD_NO"))
									.doubleValue();

							String cust = qv.getLctb()
									.getFieldValue(rowno, "ORD_REF").toString();
							String custnm = qv.getLctb()
									.getFieldValue(rowno, "ORD_REFNM")
									.toString();

							if (action.getCaption().equals(
									mapActionStrs.get("add_info_so"))) {
								add_info_so(on);
							}

							if (action.getCaption().equals(
									mapActionStrs.get("create_po"))) {
								create_po(on);
							}
							if (action.getCaption().equals(
									mapActionStrs.get("create_so"))) {
								create_so(on);
							}

							if (action.getCaption().equals(
									mapActionStrs.get("create_sr"))) {
								create_sr(on);
							}
							if (action.getCaption().equals(
									mapActionStrs.get("create_pr"))) {
								create_pr(on);
							}

							if (action.getCaption().equals(
									mapActionStrs.get("edit_po"))) {
								show_po(on);
							}
							if (action.getCaption().equals(
									mapActionStrs.get("print_po_all"))) {
								print_all_po(on);
							}

							if (action.getCaption().equals(
									mapActionStrs.get("print_so"))) {
								print_so(on);
							}
							if (action.getCaption().equals(
									mapActionStrs.get("print_jo"))) {
								try {
									utilsVaadinPrintHandler.printLGJob(con, on
											+ "");
								} catch (SQLException e) {
									e.printStackTrace();

								}
							}

							if (action.getCaption().equals(
									mapActionStrs.get("edit_so"))) {
								show_so(on);
							}
							if (action.getCaption().equals(
									mapActionStrs.get("edit_jo_n"))) {
								Window wnd = ControlsFactory.CreateWindow(
										"70%", "70%", true, true);
								wnd.setContent(new VerticalLayout());
								frmJobOrder frmj = new frmJobOrder();
								frmj.setParentLayout((AbstractLayout) wnd
										.getContent());
								frmj.initForm();
								frmj.QRYSES = String.valueOf(on);
								frmj.load_data();
							}
							if (action.getCaption().equals(
									mapActionStrs.get("edit_jo"))) {
								initForm();
								QRYSES = String.valueOf(on);
								load_data();
							}
							if (action.getCaption().equals(
									mapActionStrs.get("close_jo"))) {
								do_close_jo(on);
							}
							if (action.getCaption().equals(
									mapActionStrs.get("pays"))) {
								Window wnd = ControlsFactory.CreateWindow(
										"700px", "600px", true, true);
								frmPays fd = new frmPays();
								fd.QRYSES_ON = on + "";
								fd.QRYSES_ON_ORD_REF = cust;
								fd.QRYSES_ON_ORD_REFNM = custnm;
								fd.setParentLayout((AbstractLayout) wnd
										.getContent());
								fd.initForm();
							}

						}

					});
				}

				private void add_info_so(double on) {
					show_add_info_form("", on, on);

				}

				public void beforeQuery() {

				}

				public String calcSummary(List<String> qcGroup, qryColumn qc) {

					return "0";
				}

				public String getCellStyle(qryColumn qc, int recordNo) {

					return null;
				}

			});
			qv.createView();
			centralPanel.requestRepaintAll();

		} catch (SQLException ex) {
			centralPanel.getWindow().showNotification("", ex.getMessage());
			ex.printStackTrace();
		}

	}

	private void do_close_jo(final double on) {
		Callback cb = new Callback() {

			public void onDialogResult(boolean resultIsYes) {
				if (!resultIsYes)
					return;
				try {
					con.setAutoCommit(false);
					utils.execSql("update order1 set ord_flag=1 where ord_no='"
							+ on + "' and " + "ord_code=" + varOrdCode, con);
					con.commit();
					parentLayout.getWindow().showNotification("",
							"Closed Successfully...");
					resetFormLayout();
					showInitView();
				} catch (SQLException ex) {
					ex.printStackTrace();
					parentLayout.getWindow().showNotification("",
							ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
					try {
						con.rollback();
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			}
		};

		parentLayout.getWindow().addWindow(
				new YesNoDialog("Confirmation",
						"Do you want to close order no # " + on + " ?", cb,
						"250px", "-1px"));

	}

	private void print_so(final double on) {

		try {
			String sq = "select ord_no,ord_refnm ,ord_ref,ord_date,ord_amt-ord_discamt Net_amt from order1  where ord_code=111"
					+ "and ord_reference='"
					+ on
					+ "' and ord_type=2 order by ord_no";
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
							double ord = Double.valueOf(tv.getData()
									.getFieldValue(rn, "ord_no").toString());
							String s = utils.getSqlValue(
									"select lcno from order1 where ord_code=106 and ord_no='  "
											+ on + "'", con);
							if (s == null || s.isEmpty()) {
								throw new SQLException("Must assign company");
							}

							String ss = Channelplus3Application.getInstance()
									.getFrmUserLogin().getMapVars().get("user")
									.toUpperCase();

							utilsVaadinPrintHandler.printSO(ord, "rptVouSO_"
									+ ss + "_" + s, con);

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, sq, true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	private void show_so(final double on) {

		try {
			String sq = "select ord_no,ord_refnm ,ord_ref,ord_date,ord_amt-ord_discamt Net_amt  from order1  where ord_code=111"
					+ "and ord_reference='"
					+ on
					+ "' and ord_type=2 order by ord_no";
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
							double ord = Double.valueOf(tv.getData()
									.getFieldValue(rn, "ord_no").toString());
							show_sub_form(new frmSaleOrd(),
									"SALE ORDER / JOB ORDER #" + on, on, ord);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, sq, true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	private void print_all_po(final double on) {

		try {
			utilsVaadinPrintHandler.printSO(on, "rptVouPurCostAll", con);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void show_po(final double on) {

		try {
			String sq = "select ord_no,ord_refnm ,ord_ref ,ord_date,ord_amt-ord_discamt Net_amt  from order1  where ord_code=103"
					+ "and ord_reference='"
					+ on
					+ "' and ord_type=2 order by ord_no";

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
							double ord = Double.valueOf(tv.getData()
									.getFieldValue(rn, "ord_no").toString());

							show_sub_form(new frmPurOrd(),
									"PURCHASE COSTING / JOB ORDER # " + on, on,
									ord);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, sq, true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	private void create_so(double on) {
		frmSaleOrd f = new frmSaleOrd();
		show_sub_form(f, "SALES ORDER / JOB ORDER #" + on, on, -1);

	}

	private void create_po(double on) {

		show_sub_form(new frmPurOrd(), "PURCHASE ORDER / JOB ORDER #" + on, on,
				-1);

	}

	private void create_sr(double on) {
		frmSaleReturn f = new frmSaleReturn();
		show_sub_form(f, "SALES RETURN / JOB ORDER #" + on, on, -1);

	}

	private void create_pr(double on) {
		// frmSaleOrd f = new frmSaleOrd();
		// show_sub_form(f, "PURCHASE RETURN / JOB ORDER #" + on, on, -1);

	}

	public void show_sub_form(transactionalForm frm, String cap, double jobno,
			double on) {
		Window wnd = ControlsFactory.CreateWindow("750px", "600px", true, true);
		wnd.center();
		wnd.setCaption(cap);

		frm.setParentLayout((VerticalLayout) wnd.getContent());
		((VerticalLayout) wnd.getContent()).setMargin(true);
		if (frm instanceof frmSaleOrd)
			((frmSaleOrd) frm).show_stand_alone(wnd, "2", jobno + "", on);
		if (frm instanceof frmPurOrd)
			((frmPurOrd) frm).show_stand_alone(wnd, "2", jobno + "", on);
		if (frm instanceof frmSaleReturn)
			((frmSaleReturn) frm).show_stand_alone(wnd, "2", jobno + "", on);

	}

	public void show_add_info_form(String cap, double jobno, double on) {
		final Window wnd = ControlsFactory.CreateWindow("500px", "350px", true,
				true);
		wnd.center();
		wnd.setCaption("SO of Job Ord # " + on);
		// create view...
		VerticalLayout ly = (VerticalLayout) wnd.getContent();
		Label vLbl = new Label();
		List<ColumnProperty> vLc = new ArrayList<ColumnProperty>();

		final localTableModel vData = new localTableModel();
		HorizontalLayout hz = new HorizontalLayout();
		HorizontalLayout hz2 = new HorizontalLayout();

		Table vTbl = ControlsFactory.CreatTable(cap, "FRMSALORD.ADD_INFO", vLc);
		NativeButton vSave = ControlsFactory.CreateCustomButton("Update",
				"img/save.png", "Save", "");

		hz.addComponent(vLbl);
		ly.addComponent(hz);
		ly.addComponent(vTbl);
		ly.addComponent(hz2);

		hz2.addComponent(vSave);
		hz2.setComponentAlignment(vSave, Alignment.MIDDLE_RIGHT);
		hz2.setWidth("100%");
		hz.setWidth("100%");
		vTbl.setWidth("100%");
		vTbl.setHeight("200px");
		String vCustCode = "", vCustName = "";

		try {

			vData.createDBClassFromConnection(con);
			vData.executeQuery(
					"select *from order1 where ord_code=111 and ord_reference='"
							+ on + "'", true);
			vCustCode = utils.nvl(vData.getFieldValue(0, "ORD_REF"), "");
			vCustName = utils.nvl(vData.getFieldValue(0, "ORD_REFNM"), "");
			vLbl.setValue(vCustCode + " - " + vCustName);
			utilsVaadin.query_data2(vTbl, vData, vLc);

			vSave.addListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						con.setAutoCommit(false);
						for (int i = 0; i < vData.getRowCount(); i++) {
							Parameter pmw = new Parameter("WO", utils.nvl(
									vData.getFieldValue(i, "ORD_TXT_WO"), ""));
							Parameter pmi = new Parameter("IID", utils.nvl(
									vData.getFieldValue(i, "ORD_TXT_IID"), ""));
							String on = utils.nvl(
									vData.getFieldValue(i, "ORD_NO"), "");
							String oc = utils.nvl(
									vData.getFieldValue(i, "ORD_CODE"), "");

							QueryExe.execute(
									"update order1 set ord_txt_wo = :WO , ord_txt_iid = :IID "
											+ " where ord_no='" + on
											+ "' and ord_code='" + oc + "'",
									con, pmw, pmi);
						}
						con.commit();

						if (Channelplus3Application.getInstance()
								.getMainWindow().getChildWindows()
								.contains(wnd)) {
							Channelplus3Application.getInstance()
									.getMainWindow().removeWindow(wnd);
							utilsVaadin.showMessage("Saved Successfully");
						}
					} catch (SQLException e) {
						e.printStackTrace();
						try {
							con.rollback();
						} catch (SQLException e1) {
						}
					}

				}
			});

		} catch (SQLException e) {
			e.printStackTrace();

		}

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

}
