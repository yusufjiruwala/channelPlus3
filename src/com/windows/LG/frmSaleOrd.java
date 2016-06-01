package com.windows.LG;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog;
import com.doc.views.YesNoDialog.Callback;
import com.example.components.SearchField;
import com.example.components.SearchField.ButtonPress;
import com.generic.ColumnProperty;
import com.generic.ColumnProperty.ColumnAction;
import com.generic.ColumnProperty.afterModelUpdated;
import com.generic.ControlsFactory;
import com.generic.DBClass;
import com.generic.FieldInfo;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.TableLayoutVaadin.Triggers;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.localTableModel.DefaultValueListner;
import com.generic.qryColumn;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.utilsVaadinPrintHandler;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;

public class frmSaleOrd implements transactionalForm {
	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	public boolean standAloneMode = false;
	public String standAloneRef = "";
	public Window standAloneWnd = null;
	public String standAloneType = "";
	public double standAloneNo = -1;
	private SimpleDateFormat sdf = new SimpleDateFormat(utils.FORMAT_SHORT_DATE);
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	Tree tree = new Tree();
	private localTableModel data_Items = new localTableModel();

	private VerticalLayout tableLayout = new VerticalLayout();
	TableLayoutVaadin table = new TableLayoutVaadin(tableLayout);
	private final List<ColumnProperty> lstBranchCols = new ArrayList<ColumnProperty>();

	private Panel mainPanel = new Panel();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();

	private String strDefaultCurrency = "KWD";
	private String strCurrencyFormat = "";

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout content1Layout = new HorizontalLayout();
	private HorizontalLayout checkLayout = new HorizontalLayout();
	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout noLayout = new HorizontalLayout();
	private HorizontalLayout refLayout = new HorizontalLayout();
	private HorizontalLayout storeLayout = new HorizontalLayout();
	private HorizontalLayout locationLayout = new HorizontalLayout();
	private HorizontalLayout dateLayout = new HorizontalLayout();
	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout salesPerLayout = new HorizontalLayout();
	private HorizontalLayout shippingDateLayout = new HorizontalLayout();
	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout customerLayout = new HorizontalLayout();
	private HorizontalLayout referenceLayout = new HorizontalLayout();
	private HorizontalLayout content5Layout = new HorizontalLayout();
	private HorizontalLayout content6Layout = new HorizontalLayout();
	private HorizontalLayout amtLayout = new HorizontalLayout();;
	private HorizontalLayout discAmtLayout = new HorizontalLayout();
	private HorizontalLayout typeLayout = new HorizontalLayout();
	private boolean doing_query = false;

	private HorizontalLayout netLayout = new HorizontalLayout();

	private TextField txtNo = ControlsFactory.CreateTextField(null, "ORD_NO",
			lstfldinfo);

	private TextField txtInvRef = ControlsFactory.CreateTextField(null, "LCNO",
			lstfldinfo);

	private ComboBox txtStore = ControlsFactory.CreateListField(null, "STRA",
			"select no,name from store order by no", lstfldinfo);

	private ComboBox txtLocation = ControlsFactory.CreateListField(null,
			"LOCATION_CODE", "select code,name from locations order by code",
			lstfldinfo);

	private DateField txtDate = ControlsFactory.CreateDateField(null,
			"ORD_DATE", lstfldinfo);

	private SearchField txtSalesPer = ControlsFactory.CreateSearchField(null,
			"ORD_EMPNO", lstfldinfo, "100%",
			"select no,name from salesp where flag=1", "NO", "NAME");

	private SearchField txtCust = ControlsFactory
			.CreateSearchField(
					null,
					"ORD_REF",
					lstfldinfo,
					"100%",
					"select code,name from c_ycust where iscust='Y' and flag=1 order by path",
					"code", "name");
	private TextField txtCustName = ControlsFactory.CreateTextField(null,
			"ORD_REFNM", lstfldinfo);
	private DateField txtShipingDate = ControlsFactory.CreateDateField(null,
			"ORD_SHPDT", lstfldinfo);
	private TextField txtFcDescr = ControlsFactory.CreateTextField(null,
			"ORD_FC_DESCR", lstfldinfo, "100%", "KWD");
	private TextField txtFcRate = ControlsFactory.CreateTextField(null,
			"ORD_FC_RATE", lstfldinfo, "100%", "1");
	private SearchField txtReference = ControlsFactory.CreateSearchField(null,
			"ORD_REFERENCE", lstfldinfo, "100%", "", "", "");
	private TextField txtAmt = ControlsFactory.CreateTextField(null, "ORD_AMT",
			lstfldinfo);
	private TextField txtDiscAmt = ControlsFactory.CreateTextField(null,
			"ORD_DISCAMT", lstfldinfo);

	private TextField txtNet = ControlsFactory.CreateTextField(null, "", null);
	private ComboBox txtType = ControlsFactory.CreateListField(null,
			"ORD_TYPE", "", lstfldinfo, "100%", "1");

	private ComboBox txtItemsKind = ControlsFactory.CreateListField(null,
			"ONAME", "#LG_ITEM_ORDER", lstfldinfo);

	private Label lblNo = new Label("No");
	private Label lblStore = new Label("Store");
	private Label lblLocation = new Label("Location");
	private Label lblDate = new Label("Date");
	private Label lblRef = new Label("Inv Ref #");
	private Label lblSalesPer = new Label("Sale Per");
	private Label lblCust = new Label("Customer");
	private Label lblShipDate = new Label("Delivery");
	private Label lblRefer = new Label("Reference");
	private Label lblAmt = new Label("Amt");
	private Label lblDiscAmt = new Label("Disc Amount");
	private Label lblNetAmt = new Label("Net");
	private Label lblType = new Label("Pricing");

	private Label emptyLabel1 = new Label("");

	private CheckBox chkFlag = ControlsFactory.CreateCheckField(
			"Sales & Issue voucher", "SAL_AND_ISS", "Y", "N", null);

	private NativeButton cmdAdditm = ControlsFactory.CreateCustomButton("",
			"img/add.png", "Add item w/o purchase.", "");

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");

	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");

	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");

	private NativeButton cmdPrint = ControlsFactory.CreateCustomButton("Print",
			"img/pdf.png", "Print", "");
	private NativeButton cmdCopyVou = ControlsFactory.CreateCustomButton(
			"Copy", "img/copy.png", "Exporting to pdf", "");
	private NativeButton cmdColumn = ControlsFactory.CreateCustomButton(
			"Columns", "img/columns.png", "Columns", "");

	private NativeButton cmdChangeItemDescr = ControlsFactory
			.CreateCustomButton("", "img/details.png",
					"change item description.", "");

	private int varOrdCode = 111;
	private String varOrdLocation = "";
	private String varOrdPeriod = "";
	private String varOrdFlag = "2";
	private int varStore = 1;
	private String varApprovalPurOrdRequire = "FALSE";
	private String varSalKeyfld = "";
	private String varIssKeyfld = "";
	private String varSalIss = "";
	private boolean save_successed = false;

	public void save_data() {
		save_data(true);
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
			utils.execSql("begin x_sal_and_iss('" + ordno
					+ "',true); delete from order1 where ord_code="
					+ varOrdCode + " and ord_no=" + ordno + ";"
					+ "delete from order2 where ord_code=" + varOrdCode
					+ " and ord_no=" + ordno + ";  X_LG_UPDATE_ISSUES("
					+ txtReference.getValue() + "); end;", con);

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

	public void validateData() throws SQLException {
		for (int j = 0; j < table.data.getRowCount(); j++) {
			String rfr = table.data.getFieldValue(j, "ORD_REFER").toString();
			String rcptno = table.data.getFieldValue(j, "ORD_RCPTNO")
					.toString();

			if (!rcptno.isEmpty()) {
				String rf = "";
				String on = "";
				ResultSet rst = QueryExe.getSqlRS(
						"select  ord_no,ord_refer from joined_order "
								+ " where ord_code=103 and ord_reference='"
								+ txtReference.getValue()
								+ "' and ORD_RCPTNO='" + rcptno + "'", con);
				if (rst != null) {
					rst.first();
					rf = rst.getString("ORD_REFER");
					on = rst.getString("ord_no");

					rst.close();

					if (!rf.equals(rfr)) {
						throw new SQLException("Rcpt # " + rcptno
								+ " should be Item # " + rf
								+ ", check Purchase # " + on);
					}

				}
				// --------finding qty in hand .
				rst = QueryExe
						.getSqlRS(
								"select nvl(sum((ord_allqty+saleret_qty)-(issued_qty+PURRET_QTY)),0) from "
										+ " JOINED_ORDER where ord_code=103 and ord_reference='"
										+ txtReference.getValue()
										+ "' and ord_rcptno='" + rcptno + "'",
								con);

				rst.first();
				double qt = rst.getDouble(1);
				rst.close();

				if (qt <= 0)
					throw new SQLException("Recipt No #  " + rcptno
							+ " not in hand , QTY in HAND =" + qt);
			}
		}

		if (txtStore.getValue() == null)
			throw new SQLException("Store must have value");

		if (txtCust.getValue() == null
				|| txtCust.getValue().toString().isEmpty())
			throw new SQLException("Customer must have value");
		if (txtLocation.getValue() == null)
			throw new SQLException("Location must have value");

		String s = utils.getSqlValue(
				"select lcno from order1 where ord_code=106 and ord_no='"
						+ txtReference.getValue() + "'", con);
		if (s == null || s.isEmpty()) {
			do_select_company();
			throw new SQLException("Must assign company");
		}

		if (txtDate.getValue() == null)
			throw new SQLException("Date should have value !");
		Date dt = (Date) QueryExe.getSqlValue(
				"select ord_date from order1 where ord_code=106 "
						+ " and ord_no='" + txtReference.getValue() + "'", con,
				null);
		if (dt.compareTo((Date) txtDate.getValue()) > 0)
			throw new SQLException("Date is less than job order date # "
					+ sdf.format(dt));

	}

	public void do_select_company() {

		try {
			String sq = "select code,name from company order  by code";

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
							String s = tv.getData().getFieldValue(rn, "code")
									.toString();
							con.setAutoCommit(false);
							utils.execSql("update order1 set lcno='" + s
									+ "' where ord_code=106 and ord_no="
									+ txtReference, con);
							con.commit();
						} catch (Exception ex) {
							ex.printStackTrace();
							try {
								con.rollback();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}, con, sq, true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	public void save_data(boolean cls) {
		save_successed = false;

		try {
			validateData();
			con.setAutoCommit(false);
			DecimalFormat df = new DecimalFormat(strCurrencyFormat);
			QueryExe qe = new QueryExe(con);
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
			qe.setParaValue("ORD_AMT",
					df.parse(utils.nvl(txtAmt.getValue(), "0")));
			qe.setParaValue("ORD_DISCAMT",
					df.parse(utils.nvl(txtDiscAmt.getValue(), "0")));

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("ORDER1"); // if
			} else {
				qe.AutoGenerateUpdateStatment("ORDER1", "'ORD_NO','ORD_CODE'",
						" WHERE ORD_NO=:ORD_NO AND ORD_CODE=:ORD_CODE ");
			}

			qe.execute();
			qe.close();
			utils.execSql("delete from order2 where ord_code='" + varOrdCode
					+ "' and ord_no='" + txtNo.getValue() + "'", con);

			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("YEAR", "2000");
			mp.put("ORD_CODE", varOrdCode);
			mp.put("ORD_TYPE", txtType.getValue());
			mp.put("ORD_DATE", txtDate.getValue());
			mp.put("ORD_FLAG", varOrdFlag);
			mp.put("ORD_NO", txtNo.getValue());
			mp.put("ORD_FC_DESCR", txtFcDescr.getValue());
			mp.put("ORD_FC_RATE", txtFcRate.getValue());
			mp.put("PERIODCODE", varOrdPeriod);
			mp.put("LOCATION_CODE", txtLocation.getValue());

			for (int i = 0; i < table.data.getRowCount(); i++) {
				String rfr = table.data.getFieldValue(i, "ORD_REFER") + "";
				String rno = utils.nvl(
						table.data.getFieldValue(i, "ORD_RCPTNO"), "");

				// check if this rcpt is sold previously
				double d = Double
						.valueOf(QueryExe
								.getSqlValue(
										"select nvl(max(ord_price*ORD_FC_rate),0)"
												+ " from joined_order where ord_refer= :RFR and ord_rcptno= :RNO and ord_reference= :ON and ord_code=103 ",
										con,
										"0.0",
										new Parameter("RFR", rfr),
										new Parameter("RNO", rno),
										new Parameter("ON", txtReference
												.getValue()))
								+ "");
				table.data.setFieldValue(i, "ORD_COST_PRICE",
						BigDecimal.valueOf(d));

			}

			table.insert_to_table("ORDER2", mp, "", "ORD_ITMAVER , SELECTION ");

			ResultSet rsr = QueryExe
					.getSqlRS(
							"select ord_rcptno,nvl(count(*),0) cnt from joined_order "
									+ "  where ord_code="
									+ varOrdCode
									+ " and ord_reference='"
									+ txtReference.getValue()
									+ "' and ord_rcptno in (select ord_rcptno from order2 where ord_code="
									+ varOrdCode + " and ord_no="
									+ txtNo.getValue() + ") "
									+ " group by ord_rcptno "
									+ " having count(*)>1", con);

			if (rsr != null) {
				if (rsr.first()) {
					String rn = rsr.getString("ord_rcptno");
					rsr.close();
					throw new Exception("Recipt  duplicates encountered !  # "
							+ rn);
				} else
					rsr.close();
			}

			sal_and_iss(Double.valueOf(txtNo.getValue() + ""));

			con.commit();
			save_successed = true;
			String rfr = "";
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtNo.getValue().toString();

			}

			double qh1 = 0;
			if (standAloneMode) {
				Channelplus3Application.getInstance().getMainWindow()
						.removeWindow(standAloneWnd);
				return;
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

	private void check_after_closing() {
		if (txtReference.getValue() == null)
			return;
		final double jon = Double.valueOf(txtReference.getValue() + "");
		final double on = Double.valueOf(txtNo.getValue() + "");
		Callback cb = new Callback() {

			@Override
			public void onDialogResult(boolean resultIsYes) {
				if (!resultIsYes)
					return;
				try {
					QueryExe.execute(
							"update order1 set ord_flag=1 where ord_no='" + jon
									+ "' and " + "ord_code=106", con);
					con.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		};

	}

	private void sal_and_iss(Double ordno) throws SQLException {
		if (chkFlag.booleanValue())
			utils.execSql("begin x_sal_and_iss('" + ordno + "',false); end;",
					con);
		else
			utils.execSql("begin x_sal_and_iss ('" + ordno + "',true); end;",
					con);

	}

	public void load_data() {
		try {
			strDefaultCurrency = Channelplus3Application.getInstance()
					.getFrmUserLogin().getMapVars().get("DEFAULT_CURRENCY");
			utils.findFieldInfoByObject(txtFcDescr, lstfldinfo).defaultValue = strDefaultCurrency;
			utilsVaadin.resetValues(basicLayout, false, false);
			utilsVaadin.setDefaultValues(lstfldinfo, false);
			change_fc_rate();
			strCurrencyFormat = getCurrencyFormat();

			doing_query = true;
			varSalKeyfld = "";
			varIssKeyfld = "";
			varSalIss = "N";
			varOrdFlag = "2";

			txtNo.setValue(utils.getSqlValue(
					"select nvl(max(ord_no),0)+1 from order1 where ord_code='"
							+ varOrdCode + "'", con));

			txtNo.setEnabled(true);
			cmdSave.setEnabled(true);
			cmdDelete.setEnabled(true);
			txtItemsKind.setEnabled(true);

			txtItemsKind.setReadOnly(false);
			txtItemsKind.setValue(null);
			txtReference.setEnabled(false);
			txtType.setEnabled(true);
			chkFlag.setValue(true);

			utilsVaadin.setValueByForce(txtAmt, "0.000");
			utilsVaadin.setValueByForce(txtDiscAmt, "0.000");
			utilsVaadin.setValueByForce(txtNet, "0.000");

			if (txtDate.getValue() == null) {
				txtDate.setValue(new Date(System.currentTimeMillis()));
			}
			((Date) txtDate.getValue()).setTime(System.currentTimeMillis());
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"Select * from order1 where ORD_NO='" + QRYSES
								+ "' AND ORD_CODE=" + varOrdCode,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				varSalIss = rst.getString("SAL_AND_ISS");
				varOrdFlag = rst.getString("ord_flag");
				varSalKeyfld = utils.nvl(rst.getString("SALEINV"), "");
				varIssKeyfld = utils.nvl(rst.getString("ISSUE_KEYFLD"), "");
				chkFlag.setValue(false);

				if (!varSalIss.equals("Y")
						&& (!varSalKeyfld.isEmpty() || !varIssKeyfld.isEmpty())) {
					chkFlag.setEnabled(false);
				}

				if (varSalIss.equals("Y")) {
					chkFlag.setValue(true);
				}
				if ((!varSalKeyfld.isEmpty() || !varIssKeyfld.isEmpty())
						&& varSalIss.equals("N")) {
					cmdSave.setEnabled(false);
					cmdDelete.setEnabled(false);
				}
				if (varOrdFlag.equals("1")) {
					cmdSave.setEnabled(false);
					cmdDelete.setEnabled(false);

				}

				txtNo.setEnabled(false);
				txtType.setEnabled(false);
				txtReference.setEnabled(false);
				txtItemsKind.setEnabled(false);

				fetchAccountInfo();
				pstmt.close();

				if (table.data.getRowCount() > 0)
					txtItemsKind.setReadOnly(true);

			} else {
				load_parametes();
			}

			table.data
					.executeQuery(
							"select order2.*, 0 discp,items.pack itpack,"
									+ "((ORD_PRICE-ORD_DISCAMT)/ORD_PACK)*ORD_ALLQTY amount,"
									+ "(select nvl(sum(qtyin-qtyout),0) from invoice2 "
									+ " where refer=ord_refer) ord_balance_avail,"
									+ " ord_cost_price * items.pack ord_cost_price , "
									+ " ord_cost_price *ord_allqty  ord_cost_amt"
									+ " ,items.lsprice,(items.lsprice/items.pack)*ord_allqty "
									+ "  lsamt from order2,items  where ord_no='"
									+ QRYSES
									+ "' and items.reference=ord_refer and order2.ord_code="
									+ varOrdCode + " order by ord_pos", true);
			table.fill_table();
			doing_query = false;
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			doing_query = false;
		}
	}

	private void load_parametes() throws SQLException {
	}

	protected void fetchAccountInfo() {
		if (txtSalesPer.getValue() != null
				&& !txtSalesPer.getValue().toString().isEmpty())
			txtSalesPer.setDisplay(utils.getSqlValue(
					"select name from salesp where no='"
							+ txtSalesPer.getValue() + "'", con));
	}

	private String getCurrencyFormat() throws SQLException {
		String s = QueryExe
				.getSqlValue(
						"select max(format_j2ee) from currency where code='"
								+ txtFcDescr.getValue() + "'",
						con,
						Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY)
				+ "";
		return s;

	}

	public void applyColumns() {
		try {
			utilsVaadin.applyColumns("FRMSALORD.ORDER2", table.getTable(),
					table.listFields, con);

			table.addSummaryField("AMOUNT", txtAmt);

			utilsVaadin.findColByCol("ORD_PRICE", table.listFields).actionAfterUpdate = new afterModelUpdated() {
				public Object onValueChange(int rowno, String colname, Object vl) {

					if (utilsVaadin.system_status.equals("QUERY")) {
						return null;
					}
					double disc = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_DISCAMT")).doubleValue();
					double pkqt = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_PKQTY")).doubleValue();
					double qt = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_UNQTY")).doubleValue();
					double pk = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_PACK")).doubleValue();
					double allqt = (pkqt * pk) + qt;
					double pr = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_PRICE")).doubleValue();
					double lspr = Double.valueOf(utils.nvl(
							table.data.getFieldValue(rowno, "LSPRICE"), "0"));
					double cp = Double.valueOf(utils.nvl(
							table.data.getFieldValue(rowno, "ORD_COST_PRICE"),
							"0"));

					pk = (pk == 0 ? 1 : pk);

					double amt = (allqt) * ((pr - disc) / pk);
					double lsamt = (allqt) * ((lspr) / pk);
					double csamt = (allqt) * ((cp) / pk);

					table.data.setFieldValue(rowno, "ORD_ALLQTY",
							BigDecimal.valueOf(allqt));
					table.data.setFieldValue(rowno, "AMOUNT",
							BigDecimal.valueOf(amt));
					table.data.setFieldValue(rowno, "LSAMT",
							BigDecimal.valueOf(lsamt));

					// table.data.setFieldValue(rowno, "ORD_COST_PRICE",
					// BigDecimal.valueOf(csamt));

					utilsVaadin.refreshRowFromData(table.data,
							table.getTable(), rowno, table.listFields);

					return vl;
				}
			};

			utilsVaadin.findColByCol("AMOUNT", table.listFields).actionAfterUpdate = new afterModelUpdated() {
				public Object onValueChange(int rowno, String colname, Object vl) {

					if (utilsVaadin.system_status.equals("QUERY")) {
						return null;
					}

					table.data.setFieldValue(rowno, "ORD_DISCAMT",
							BigDecimal.valueOf(0.0f));

					double disc = 0;
					double pkqt = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_PKQTY")).doubleValue();
					double qt = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_UNQTY")).doubleValue();
					double pk = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_PACK")).doubleValue();
					pk = (pk == 0 ? 1 : pk);
					double allqt = (pkqt * pk) + qt;

					double amt = ((BigDecimal) table.data.getFieldValue(rowno,
							"AMOUNT")).doubleValue();

					table.data.setFieldValue(rowno, "ORD_ALLQTY",
							BigDecimal.valueOf(allqt));
					double pr = (amt / allqt) * pk;

					table.data.setFieldValue(rowno, "ORD_PRICE",
							BigDecimal.valueOf(pr));

					utilsVaadin.refreshRowFromData(table.data,
							table.getTable(), rowno, table.listFields);

					return vl;
				}
			};
			utilsVaadin.findColByCol("ORD_DISCAMT", table.listFields).actionAfterUpdate = new afterModelUpdated() {
				public Object onValueChange(int rowno, String colname, Object vl) {

					if (utilsVaadin.system_status.equals("QUERY")) {
						return null;
					}

					double pr = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_PRICE")).doubleValue();
					double lspr = Double.valueOf(utils.nvl(
							(BigDecimal) table.data.getFieldValue(rowno,
									"LSPRICE"), "0"));

					double disc_amt = ((BigDecimal) table.data.getFieldValue(
							rowno, "ORD_DISCAMT")).doubleValue();

					double discp = ((BigDecimal) table.data.getFieldValue(
							rowno, "DISCP")).doubleValue();

					if (colname.equals("ORD_DISCAMT")) {
						discp = (100 / pr) * disc_amt;

						table.data.setFieldValue(rowno, "DISCP",
								BigDecimal.valueOf(discp));
					}

					if (colname.equals("DISCP")) {
						disc_amt = (pr / 100) * discp;
						table.data.setFieldValue(rowno, "ORD_DISCAMT",
								BigDecimal.valueOf(disc_amt));
					}

					utilsVaadin.findColByCol("ORD_PRICE", table.listFields).actionAfterUpdate
							.onValueChange(rowno, colname, vl);

					utilsVaadin.refreshRowFromData(table.data,
							table.getTable(), rowno, table.listFields);

					return vl;

				}
			};

			utilsVaadin.findColByCol("ORD_REFER", table.listFields).action = new ColumnAction() {
				public Object onValueChange(int rowno, String colname, Object vl) {
					int cnt = Integer.valueOf(utils.getSqlValue(
							"select nvl(count(*),0 ) from items where parentitem='"
									+ vl + "'", con));
					if (cnt > 0) {
						parentLayout.getWindow().showNotification(
								"You can not use parent item here",
								Notification.TYPE_ERROR_MESSAGE);
						return null;
					}

					return vl;
				}
			};

			utilsVaadin.findColByCol("ORD_REFER", table.listFields).actionAfterUpdate = new afterModelUpdated() {

				public Object onValueChange(int rowno, String colname, Object vl) {
					ResultSet rst = utils
							.getSqlRS(
									"select ITEMS.*,get_item_cost(reference)*items.pack PACK_COST ,"
											+ " get_item_price(items.reference,'"
											+ txtCust.getValue()
											+ "','"
											+ txtReference.getValue()
											+ "','"
											+ ((dataCell) txtType.getValue())
													.getValue()
											+ "') pricex, get_item_discount(items.reference,'"
											+ txtCust.getValue()
											+ "','"
											+ txtReference.getValue()
											+ "','"
											+ ((dataCell) txtType.getValue())
													.getValue() + "')  discx"
											+ "  from items where reference='"
											+ vl + "'", con);
					try {

						if (rst != null && rst.first()) {
							table.data.setFieldValue(rowno, "DESCR",
									rst.getString("DESCR"));
							table.data.setFieldValue(rowno, "ORD_PACKD",
									rst.getString("PACKD"));

							table.data.setFieldValue(rowno, "ORD_UNITD",
									rst.getString("UNITD"));

							/*
							 * if (rst.getString("PRICEX") != null) {
							 * table.data.setFieldValue(rowno, "ORD_PRICE",
							 * rst.getBigDecimal("PRICEX")); } if
							 * (rst.getString("DISCX") != null) {
							 * table.data.setFieldValue(rowno, "ORD_DISCAMT",
							 * rst.getBigDecimal("DISCX")); }
							 */

							table.data.setFieldValue(rowno, "ORD_PACK",
									rst.getBigDecimal("PACK"));

							table.data.setFieldValue(rowno, "LSPRICE",
									rst.getBigDecimal("LSPRICE"));
							table.data.setFieldValue(rowno, "ITPACK",
									rst.getBigDecimal("PACK"));
							table.data.setFieldValue(rowno, "ORD_COST_PRICE",
									BigDecimal.valueOf(0.0));

							rst.close();

							utilsVaadin.findColByCol("ORD_PRICE",
									table.listFields).actionAfterUpdate
									.onValueChange(rowno, colname, vl);
							utilsVaadin.findColByCol("ORD_DISCAMT",
									table.listFields).actionAfterUpdate
									.onValueChange(rowno, "ORD_DISCAMT", vl);

							utilsVaadin.refreshRowFromData(table.data,
									table.getTable(), rowno, table.listFields);

						}
					} catch (SQLException e) {
						parentLayout.getWindow().showNotification(
								e.getMessage());
						e.printStackTrace();
					}
					return vl;
				}
			};

			utilsVaadin.findColByCol("ORD_PACKD", table.listFields).actionAfterUpdate = new afterModelUpdated() {

				public Object onValueChange(int rowno, String colname, Object vl) {
					if (utilsVaadin.system_status.equals("QUERY")) {
						return null;
					}

					double pr = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_PRICE")).doubleValue();
					double pk = ((BigDecimal) table.data.getFieldValue(rowno,
							"ORD_PACK")).doubleValue();
					double amt = ((BigDecimal) table.data.getFieldValue(rowno,
							"AMOUNT")).doubleValue();
					double allqty = ((BigDecimal) table.data.getFieldValue(
							rowno, "ORD_ALLQTY")).doubleValue();

					table.data.setFieldValue(rowno, "ORD_DISCAMT",
							BigDecimal.valueOf(0));
					table.data.setFieldValue(rowno, "DISCP",
							BigDecimal.valueOf(0));

					table.data.setFieldValue(rowno, "ORD_PRICE",
							BigDecimal.valueOf(((amt / allqty)) * pk));

					utilsVaadin.findColByCol("ORD_PRICE", table.listFields).actionAfterUpdate
							.onValueChange(rowno, colname, vl);

					utilsVaadin.refreshRowFromData(table.data,
							table.getTable(), rowno, table.listFields);

					return vl;
				}
			};

			utilsVaadin.findColByCol("ORD_PKQTY", table.listFields).actionAfterUpdate = utilsVaadin
					.findColByCol("ORD_PRICE", table.listFields).actionAfterUpdate;
			utilsVaadin.findColByCol("ORD_UNQTY", table.listFields).actionAfterUpdate = utilsVaadin
					.findColByCol("ORD_PRICE", table.listFields).actionAfterUpdate;
			utilsVaadin.findColByCol("ORD_PACK", table.listFields).actionAfterUpdate = utilsVaadin
					.findColByCol("ORD_PRICE", table.listFields).actionAfterUpdate;
			utilsVaadin.findColByCol("ORD_UNQTY", table.listFields).actionAfterUpdate = utilsVaadin
					.findColByCol("ORD_PRICE", table.listFields).actionAfterUpdate;

			utilsVaadin.findColByCol("DISCP", table.listFields).actionAfterUpdate = utilsVaadin
					.findColByCol("ORD_DISCAMT", table.listFields).actionAfterUpdate;

		} catch (SQLException e) {
		}
	}

	public void print_data() {

		try {
			String s = utils.getSqlValue(
					"select lcno from order1 where ord_code=106 and ord_no='"
							+ txtReference.getValue() + "'", con);
			if (s == null || s.isEmpty()) {
				throw new SQLException("Must assign company");
			}

			String ss = Channelplus3Application.getInstance().getFrmUserLogin()
					.getMapVars().get("user").toUpperCase();

			utilsVaadinPrintHandler.printSO(Double.valueOf(QRYSES), "rptVouSO_"
					+ ss + "_" + s, con);

		} catch (Exception ex) {
			mainLayout.getWindow().showNotification("Unable to print:",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			ex.printStackTrace();
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
		setupValues();
		load_data();

	}

	public void setupValues() {
		varOrdLocation = Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars().get("DEFAULT_LOCATION");
		varStore = Integer.valueOf(Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars().get("DEFAULT_STORE"));
		utils.findFieldInfoByObject(txtLocation, lstfldinfo).defaultValue = utilsVaadin
				.findByValue(txtLocation, varOrdLocation);
		utils.findFieldInfoByObject(txtStore, lstfldinfo).defaultValue = utilsVaadin
				.findByValue(txtStore, varStore + "");
		varOrdPeriod = Channelplus3Application.getInstance().getFrmUserLogin()
				.getMapVars().get("CURRENT_PERIOD");
		varApprovalPurOrdRequire = Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars().get("APPROVAL_SALORD_REQUIRE");
	}

	public void createView() {
		doing_query = false;
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();

		mainPanel.setWidth("700px");
		mainPanel.setHeight("100%");
		mainPanel.getContent().setHeight("-1px");

		table.createView();
		centralPanel.setSizeFull();
		mainLayout.setWidth("700px");
		basicLayout.setWidth("100%");
		content1Layout.setWidth("100%");
		checkLayout.setWidth("100%");
		content2Layout.setWidth("100%");
		noLayout.setWidth("100%");
		storeLayout.setWidth("100%");
		locationLayout.setWidth("100%");
		dateLayout.setWidth("100%");
		refLayout.setWidth("100%");
		content3Layout.setWidth("100%");
		salesPerLayout.setWidth("100%");
		shippingDateLayout.setWidth("100%");
		content4Layout.setWidth("100%");
		customerLayout.setWidth("100%");
		referenceLayout.setWidth("100%");
		content5Layout.setWidth("100%");
		amtLayout.setWidth("100%");
		discAmtLayout.setWidth("100%");
		netLayout.setWidth("100%");
		typeLayout.setWidth("100%");

		txtNo.setWidth("100%");
		txtInvRef.setWidth("100%");
		txtStore.setWidth("100%");
		txtLocation.setWidth("100%");
		txtDate.setWidth("100%");
		txtSalesPer.setWidth("100%");
		txtCust.setWidth("100%");
		txtCustName.setWidth("100%");
		txtShipingDate.setWidth("100%");
		txtReference.setWidth("100%");
		txtAmt.setWidth("100%");
		txtDiscAmt.setWidth("100%");
		txtNet.setWidth("100%");

		lblNo.setWidth("60px");
		lblType.setWidth("60px");
		lblDate.setWidth("60px");
		lblStore.setWidth("60px");
		lblLocation.setWidth("60px");
		lblSalesPer.setWidth("60px");
		lblCust.setWidth("60px");
		lblShipDate.setWidth("60px");
		lblRefer.setWidth("60px");
		lblAmt.setWidth("60px");
		lblDiscAmt.setWidth("60px");
		lblNetAmt.setWidth("60px");

		tableLayout.setHeight("325px");
		table.getTable().setHeight("300px");

		txtCust.setImmediate(true);
		txtDiscAmt.setImmediate(true);
		txtAmt.setImmediate(true);
		txtType.setImmediate(true);
		txtItemsKind.setImmediate(true);

		txtAmt.setReadOnly(true);
		txtNet.setReadOnly(true);
		txtCust.setEnabled(false);
		txtCustName.setEnabled(false);

		txtAmt.addStyleName("netAmtStyle yellowField");
		txtNet.addStyleName("netAmtStyle yellowField");
		txtDiscAmt.addStyleName("netAmtStyle yellowField");

		utils.findFieldInfoByObject(txtAmt, lstfldinfo).format = strCurrencyFormat;
		utils.findFieldInfoByObject(txtAmt, lstfldinfo).fieldType = Parameter.DATA_TYPE_NUMBER;

		utils.findFieldInfoByObject(txtDiscAmt, lstfldinfo).format = strCurrencyFormat;
		utils.findFieldInfoByObject(txtDiscAmt, lstfldinfo).fieldType = Parameter.DATA_TYPE_NUMBER;

		txtItemsKind.setNullSelectionAllowed(false);
		txtItemsKind.setImmediate(true);
		txtFcDescr.setImmediate(true);
		txtFcDescr.addStyleName("uptxt");

		txtCust.setShowDisplayWithValue(false);
		txtCust.setShowValueOnly(true);

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

		ResourceManager.addComponent(content1Layout, locationLayout);
		ResourceManager.addComponent(content1Layout, refLayout);
		ResourceManager.addComponent(content1Layout, chkFlag);

		ResourceManager.addComponent(content2Layout, noLayout);
		ResourceManager.addComponent(content2Layout, dateLayout);
		ResourceManager.addComponent(content2Layout, storeLayout);

		ResourceManager.addComponent(content3Layout, typeLayout);
		ResourceManager.addComponent(content3Layout, shippingDateLayout);
		ResourceManager.addComponent(content3Layout, salesPerLayout);

		ResourceManager.addComponent(content4Layout, customerLayout);
		ResourceManager.addComponent(content4Layout, referenceLayout);

		ResourceManager.addComponent(content5Layout, amtLayout);
		ResourceManager.addComponent(content5Layout, discAmtLayout);
		ResourceManager.addComponent(content5Layout, netLayout);

		ResourceManager.addComponent(content6Layout, new Label("FC Descr"));
		ResourceManager.addComponent(content6Layout, txtFcDescr);
		ResourceManager.addComponent(content6Layout, new Label("Rate"));
		ResourceManager.addComponent(content6Layout, txtFcRate);

		ResourceManager.addComponent(noLayout, lblNo);
		ResourceManager.addComponent(noLayout, txtNo);

		ResourceManager.addComponent(storeLayout, lblStore);
		ResourceManager.addComponent(storeLayout, txtStore);

		ResourceManager.addComponent(locationLayout, lblLocation);
		ResourceManager.addComponent(locationLayout, txtLocation);

		ResourceManager.addComponent(refLayout, lblRef);
		ResourceManager.addComponent(refLayout, txtInvRef);

		ResourceManager.addComponent(dateLayout, lblDate);
		ResourceManager.addComponent(dateLayout, txtDate);

		ResourceManager.addComponent(salesPerLayout, lblSalesPer);
		ResourceManager.addComponent(salesPerLayout, txtSalesPer);

		ResourceManager.addComponent(typeLayout, lblType);
		ResourceManager.addComponent(typeLayout, txtType);

		ResourceManager.addComponent(customerLayout, lblCust);
		ResourceManager.addComponent(customerLayout, txtCust);
		ResourceManager.addComponent(customerLayout, txtCustName);

		ResourceManager.addComponent(shippingDateLayout, lblShipDate);
		ResourceManager.addComponent(shippingDateLayout, txtShipingDate);

		ResourceManager.addComponent(referenceLayout, lblRefer);
		ResourceManager.addComponent(referenceLayout, txtReference);

		ResourceManager.addComponent(amtLayout, lblAmt);
		ResourceManager.addComponent(amtLayout, txtAmt);

		ResourceManager.addComponent(discAmtLayout, lblDiscAmt);
		ResourceManager.addComponent(discAmtLayout, txtDiscAmt);

		ResourceManager.addComponent(netLayout, lblNetAmt);
		ResourceManager.addComponent(netLayout, txtNet);

		table.getHzLayout().addComponent(txtItemsKind);
		// table.getHzLayout().addComponent(cmdAdditm);
		table.getHzLayout().addComponent(cmdChangeItemDescr);

		noLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);
		refLayout.setComponentAlignment(lblRef, Alignment.BOTTOM_CENTER);
		dateLayout.setComponentAlignment(lblDate, Alignment.BOTTOM_CENTER);
		locationLayout.setComponentAlignment(lblLocation,
				Alignment.BOTTOM_CENTER);
		storeLayout.setComponentAlignment(lblStore, Alignment.BOTTOM_CENTER);
		salesPerLayout.setComponentAlignment(lblSalesPer,
				Alignment.BOTTOM_CENTER);

		typeLayout.setComponentAlignment(lblType, Alignment.BOTTOM_CENTER);

		customerLayout.setComponentAlignment(lblCust, Alignment.BOTTOM_CENTER);
		shippingDateLayout.setComponentAlignment(lblShipDate,
				Alignment.BOTTOM_CENTER);
		referenceLayout
				.setComponentAlignment(lblRefer, Alignment.BOTTOM_CENTER);
		amtLayout.setComponentAlignment(lblAmt, Alignment.BOTTOM_CENTER);
		discAmtLayout
				.setComponentAlignment(lblDiscAmt, Alignment.BOTTOM_CENTER);
		netLayout.setComponentAlignment(lblNetAmt, Alignment.BOTTOM_CENTER);

		content1Layout.setExpandRatio(refLayout, 1.7f);
		content1Layout.setExpandRatio(chkFlag, 1.3f);
		content1Layout.setExpandRatio(locationLayout, 1f);

		content2Layout.setExpandRatio(noLayout, 1f);
		content2Layout.setExpandRatio(dateLayout, 1.25f);
		content2Layout.setExpandRatio(storeLayout, 1.75f);

		content3Layout.setExpandRatio(typeLayout, 1);
		content3Layout.setExpandRatio(shippingDateLayout, 1.25f);
		content3Layout.setExpandRatio(salesPerLayout, 1.75f);

		content4Layout.setExpandRatio(customerLayout, 2.7f);
		content4Layout.setExpandRatio(referenceLayout, 1.3f);

		content5Layout.setExpandRatio(amtLayout, 1.3f);
		content5Layout.setExpandRatio(discAmtLayout, 1.3f);
		content5Layout.setExpandRatio(netLayout, 1.4f);

		noLayout.setExpandRatio(lblNo, 0.1f);
		noLayout.setExpandRatio(txtNo, 3.9f);

		dateLayout.setExpandRatio(lblDate, 0.1f);
		dateLayout.setExpandRatio(txtDate, 3.9f);

		refLayout.setExpandRatio(lblRef, 1f);
		refLayout.setExpandRatio(txtInvRef, 3f);

		storeLayout.setExpandRatio(lblStore, 0.1f);
		storeLayout.setExpandRatio(txtStore, 3.9f);

		locationLayout.setExpandRatio(lblLocation, 0.1f);
		locationLayout.setExpandRatio(txtLocation, 3.9f);

		salesPerLayout.setExpandRatio(lblSalesPer, 0.1f);
		salesPerLayout.setExpandRatio(txtSalesPer, 3.9f);

		customerLayout.setExpandRatio(lblCust, 0.1f);
		customerLayout.setExpandRatio(txtCust, 1.2f);
		customerLayout.setExpandRatio(txtCustName, 2.7f);

		shippingDateLayout.setExpandRatio(lblShipDate, 0.1f);
		shippingDateLayout.setExpandRatio(txtShipingDate, 3.9f);

		typeLayout.setExpandRatio(lblType, 0.1f);
		typeLayout.setExpandRatio(txtType, 3.9f);

		referenceLayout.setExpandRatio(lblRefer, 0.1f);
		referenceLayout.setExpandRatio(txtReference, 3.9f);

		amtLayout.setExpandRatio(lblAmt, 0.1f);
		amtLayout.setExpandRatio(txtAmt, 3.9f);

		discAmtLayout.setExpandRatio(lblDiscAmt, 0.1f);
		discAmtLayout.setExpandRatio(txtDiscAmt, 3.9f);

		netLayout.setExpandRatio(lblNetAmt, 0.1f);
		netLayout.setExpandRatio(txtNet, 3.9f);

		applyColumns();

		txtType.removeAllItems();
		txtType.addItem(new dataCell("CONTRACT", "1"));
		txtType.addItem(new dataCell("JOB_ORDER", "2"));
		txtType.addItem(new dataCell("QUOTATION", "3"));

		txtReference.setShowValueOnly(true);

		if (!listnerAdded) {

			ValueChangeListener vl1 = new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					utilsVaadin.findColByCol("ORD_REFER", table.listFields).searchListSQL = get_item_listSQL();
					if (doing_query)
						return;
					try {
						show_selection_items(true);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			};

			cmdChangeItemDescr.addListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					change_item_descr();
				}
			});

			cmdAdditm.addListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						show_selection_items2(true);
					} catch (Exception e) {
						e.printStackTrace();

					}

				}
			});
			txtItemsKind.addListener(vl1);
			table.cmdAdd.removeListener(table.cmd_add_listner);
			table.cmdAdd.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

					utilsVaadin.findColByCol("ORD_REFER", table.listFields).searchListSQL = get_item_listSQL();
					try {
						show_selection_items(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			table.trig = new Triggers() {
				public void beforeSumOnCalc(int recno) {

				}

				public boolean beforeAddRecord() {
					try {
						if (txtType.getValue() == null)
							throw new Exception("Must have type value");

						if (txtItemsKind.getValue() == null)
							throw new Exception("Must select Items Kind value");

						if (!((dataCell) txtType.getValue()).getValue()
								.toString().equals("1")
								&& (txtReference.getValue() == null || txtReference
										.getValue().toString().isEmpty()))
							throw new Exception("Must have reference value");
						if (txtCust.getValue() == null
								|| txtCust.getValue().toString().isEmpty())
							throw new Exception("Must select customer !");

					} catch (Exception ex) {

						parentLayout.getWindow().showNotification("ERROR",
								ex.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);
						return false;
					}

					return true;
				}

				public void AfterAddRecord(int recno) {
					txtReference.setEnabled(false);
					txtType.setEnabled(false);
					txtItemsKind.setEnabled(false);
				}

				public void afterDelRecord(int recno) {
					// TODO Auto-generated method stub

				}

				public boolean beforeDelRecord() {
					// TODO Auto-generated method stub
					return true;
				}
			};
			txtReference.setButtonPress(new ButtonPress() {

				public void onButton(String button) {
					if (button.equals("CLEAR"))
						return;
					if (txtType.getValue() == null)
						return;
					if (table.data.getRowCount() > 0)
						return;

					String typ = ((dataCell) txtType.getValue()).getValue()
							+ "";

					if (typ.equals("1"))
						return;

					String sq = "select ord_no,ord_date,ord_ref ref , "
							+ "ord_refnm ref_name from order1 where ord_code=decode("
							+ typ + ",3,101,2,106) order by ord_no desc ";

					final Window wnd = new Window();
					final VerticalLayout la = new VerticalLayout();
					wnd.setContent(la);

					try {
						utilsVaadin.showSearch(la, new SelectionListener() {

							public void onSelection(TableView tv) {
								Channelplus3Application.getInstance()
										.getMainWindow().removeWindow(wnd);
								if (tv.getSelectionValue() < 0)
									return;
								txtReference.setValue(tv.getData()
										.getFieldValue(tv.getSelectionValue(),
												"ORD_NO"));

							}
						}, con, sq, true);
					} catch (SQLException ex) {
						ex.printStackTrace();
						parentLayout.getWindow().showNotification("ERROR",
								ex.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);
					}
				}
			});

			table.data.setDefaultValuelistner(new DefaultValueListner() {

				public void setValue(dataCell dc, qryColumn qc) {
					if (dc == null || qc == null)
						return;
					if (qc.getColname().contains("QTY")) {
						dc.setValue("0", BigDecimal.valueOf(0.0f));
					}
					if (qc.getColname().contains("ORD_COST_PRICE")) {
						dc.setValue("0", BigDecimal.valueOf(0.0f));
					}
				}
			});

			txtAmt.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					calc_summary();
				}
			});
			txtDiscAmt.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					double ds = 0;
					if (txtDiscAmt.getValue() != null
							&& !txtDiscAmt.getValue().toString().isEmpty())
						ds = Double.valueOf(txtDiscAmt.getValue().toString());
					DecimalFormat df = new DecimalFormat(strCurrencyFormat);
					utilsVaadin.setValueByForce(txtDiscAmt, df.format(ds));

					calc_summary();

				}
			});
			cmdDelete.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						int ord_flg = Integer.valueOf(utils.getSqlValue(
								"select nvl(max(ord_flag),-1) from order1 where ord_code="
										+ varOrdCode + " and ord_no=" + QRYSES,
								con));
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
								if (standAloneMode) {
									Channelplus3Application.getInstance()
											.getMainWindow()
											.removeWindow(standAloneWnd);
									return;
								}
								QRYSES = "";
								load_data();
								parentLayout.getWindow().showNotification(
										"Delete successfull");
							}

						}
					};
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.addWindow(
									new YesNoDialog("Confirmation",
											"Do you want to remove order no # "
													+ QRYSES + " ?", cb,
											"250px", "-1px"));
				}

			});

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
			cmdPrint.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (cmdSave.isEnabled()) {
						save_data(false);
						if (save_successed)
							print_data();
					} else {
						print_data();
					}
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

				}
			});
			txtType.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if (table.data.getRowCount() > 0)
						return;
					if (!QRYSES.isEmpty())
						return;
					if (txtType.getValue() == null)
						return;

					txtReference.setValue("");
					txtReference.setEnabled(true);

					if (((dataCell) txtType.getValue()).getValue().toString()
							.equals("1"))
						txtReference.setEnabled(false);

				}
			});
			txtFcDescr.addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					event.getProperty().setValue(
							event.getProperty().getValue().toString()
									.toUpperCase());
					change_fc_rate();
				}
			});
			listnerAdded = true;
		}
	}

	private void change_fc_rate() {
		try {
			strCurrencyFormat = getCurrencyFormat();
			String cv = QueryExe.getSqlValue(
					"select nvl(max(rate),1) from currency where code='"
							+ txtFcDescr.getValue() + "'", con, "1")
					+ "";
			txtFcRate.setValue(cv);
			calc_summary();
			utils.findFieldInfoByObject(txtAmt, lstfldinfo).format = strCurrencyFormat;
			utils.findFieldInfoByObject(txtDiscAmt, lstfldinfo).format = strCurrencyFormat;
			try {
				double a = utilsVaadin.getFieldInfoDoubleValue(txtAmt,
						lstfldinfo);
				double d = utilsVaadin.getFieldInfoDoubleValue(txtDiscAmt,
						lstfldinfo);
				utilsVaadin.settFormattedValue(txtAmt, lstfldinfo, a);
				utilsVaadin.settFormattedValue(txtDiscAmt, lstfldinfo, d);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	public void change_item_descr() {
		if (table.getTable().getValue() == null)
			return;

		final int sr = (Integer) table.getTable().getValue();
		if (sr < 0)
			return;
		String rfr = utils.nvl(table.data.getFieldValue(sr, "ORD_REFER"), "");
		if (rfr.isEmpty())
			return;
		String cust = txtCust.getValue().toString();
		String sq = " select  distinct selling_descr from lg_custitems where code='"
				+ cust + "'and cost_item='" + rfr + "'";
		final Window wnd = new Window();
		final VerticalLayout la = new VerticalLayout();
		wnd.setContent(la);

		try {
			utilsVaadin.showSearch(la, new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);

					if (tv.getSelectionValue() > -1) {
						try {
							int rn = tv.getSelectionValue();
							String s = tv.getData()
									.getFieldValue(rn, "selling_descr")
									.toString();

							if (!s.isEmpty()) {
								table.data.setFieldValue(sr, "DESCR", s);
								table.refreshRow(sr);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, sq, true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void calc_summary() {
		double ds = 0;
		double d = 0;
		DecimalFormat df = new DecimalFormat(strCurrencyFormat);
		try {
			if (txtAmt.getValue() != null
					&& !txtAmt.getValue().toString().isEmpty())

				d = df.parse(txtAmt.getValue().toString()).doubleValue();
			if (txtDiscAmt.getValue() != null
					&& !txtDiscAmt.getValue().toString().isEmpty())
				ds = Double.valueOf(txtDiscAmt.getValue().toString());

		} catch (ParseException e) {
			utilsVaadin.showMessage(parentLayout.getWindow(), e.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}

		utilsVaadin.setValueByForce(txtNet, df.format(d - ds));

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
		content1Layout.removeAllComponents();
		checkLayout.removeAllComponents();
		content2Layout.removeAllComponents();
		noLayout.removeAllComponents();
		dateLayout.removeAllComponents();
		content3Layout.removeAllComponents();
		salesPerLayout.removeAllComponents();
		shippingDateLayout.removeAllComponents();
		content4Layout.removeAllComponents();
		customerLayout.removeAllComponents();
		referenceLayout.removeAllComponents();
		content5Layout.removeAllComponents();
		content6Layout.removeAllComponents();
		amtLayout.removeAllComponents();
		discAmtLayout.removeAllComponents();
		netLayout.removeAllComponents();
		storeLayout.removeAllComponents();
		locationLayout.removeAllComponents();
		table.resetLayout();
	}

	protected String get_item_listSQL() {

		String sq = "select reference,descr from items where flag=1 order by descr2";

		String itmkind = Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars()
				.get("LG_ITEMS_" + txtItemsKind.getValue());

		if (txtReference.getValue() != null
				&& !txtReference.getValue().toString().isEmpty()
				&& txtType.getValue() != null) {
			String typ = ((dataCell) txtType.getValue()).getValue().toString();
			if (!typ.equals("1"))
				sq = "select cost_item,selling_descr descr,items.descr cost_item_descr,"
						+ " fc_price,fc_rate,fc_descr,lg_custitems.keyfld from "
						+ "lg_custitems,items where items.reference=cost_item and"
						+ " code='"
						+ txtCust.getValue()
						+ "' and descr2 like (select descr2||'%' from items where reference='"
						+ itmkind + "') order by cost_item,type_of_frieght";

		}
		return sq;
	}

	protected void show_list() {
		try {
			String sq = "select ord_no,ord_refnm reference,ord_date  from order1  where ord_code="
					+ varOrdCode + " order by ord_no";

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
			}, con, sq, true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	public void show_stand_alone(Window wnd, String typ, String txtRefer,
			double on) {
		standAloneMode = true;
		standAloneRef = txtRefer;
		standAloneWnd = wnd;
		standAloneNo = on;
		QRYSES = "";
		cmdList.setEnabled(false);
		cmdCls.setEnabled(false);
		initForm();
		try {
			if (on == -1) {
				QRYSES = "";
				load_data();
				txtType.setValue(utilsVaadin.findByValue(txtType, typ));
				txtReference.setValue(standAloneRef);
				ResultSet rsx = utils.getSqlRS(
						"select ord_ref,ord_refnm from order1 where ord_code=decode("
								+ typ + ",2,106,3,101) and ord_no='" + txtRefer
								+ "'", con);

				txtCust.setValue(rsx.getString("ORD_REF"));
				txtCustName.setValue(rsx.getString("ord_refnm"));
				rsx.getStatement().close();
			} else {
				QRYSES = on + "";
				load_data();
			}
			/*
			 * if (typ.equals("2")) { show_selection_items(); }
			 */
			utilsVaadin.findColByCol("ORD_REFER", table.listFields).searchListSQL = "";// get_item_listSQL();
			txtType.setEnabled(false);
			txtReference.setEnabled(false);
		} catch (Exception e) {
			Channelplus3Application.getInstance().getMainWindow()
					.removeWindow(wnd);
			e.printStackTrace();
		}

	}

	private void show_selection_items(boolean show_only_empty_table)
			throws Exception {

		if (table.data.getRowCount() > 0 && show_only_empty_table)
			return;
		if (txtCust.getValue() == null
				|| txtCust.getValue().toString().isEmpty())
			return;
		String itmkind = Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars()
				.get("LG_ITEMS_" + txtItemsKind.getValue());
		if (itmkind == null || itmkind.isEmpty())
			return;

		Window wndList = ControlsFactory.CreateWindow("70%", "70%", true, true);
		final TableView tbl = new TableView(new DBClass(con));
		tbl.setCheckDefaultSelection(false);
		tbl.setCheckSelectionField(true);
		tbl.setParentPanel((VerticalLayout) wndList.getContent());

		tbl.FetchSql("select distinct items.reference cost_item,items.descr cost_item_descr,"
				+ " items.descr selling_descr,o2.ORD_RCPTNO RCPT_NO,"
				+ " (o2.ord_price*o2.ord_fc_rate) cost, o1.ord_ref,o1.ord_refnm,o1.ord_no,o1.ord_reference,"
				+ " 0 fc_price,1 fc_rate,'"
				+ strDefaultCurrency
				+ "' fc_descr,o2.payment_reference,"
				+ " (ord_allqty+saleret_qty)-(issued_qty+purret_qty) Qty_in_Hand  from "
				+ " items ,order2 o2,order1 o1 "
				+ " where "
				+ " o2.ord_rcptno is not null  "
				+ " and o2.ord_refer=items.reference and (ord_allqty+saleret_qty)-(issued_qty+purret_qty) >0 "
				+ " and o2.ord_code=103 and o1.ord_code=103 and o1.ord_no=o2.ord_no "
				+ " and o1.ord_reference='"
				+ txtReference.getValue()
				+ "' and descr2 like (select descr2||'%' from items where reference='"
				+ itmkind + "') " + " order by o1.ord_no,cost_item");

		// tbl.FetchSql("select distinct cu.cost_item,items.descr cost_item_descr,"
		// + " cu.selling_descr,o2.ORD_RCPTNO RCPT_NO,"
		// +
		// " o2.ord_price cost, o1.ord_ref,o1.ord_refnm,o1.ord_no,o1.ord_reference,"
		// + " fc_price,fc_rate,fc_descr,o2.payment_reference from "
		// + " lg_custitems cu,items ,order2 o2,order1 o1 "
		// + " where items.reference=cost_item and o2.ORD_REFER=CU.COST_ITEM "
		// + " and o2.ord_rcptno is not null  "
		// + " and o2.ord_refer=items.reference and ord_allqty-issued_qty>0 "
		// + " and o2.ord_code=103 and o1.ord_code=103 and o1.ord_no=o2.ord_no "
		// + " and  code='"
		// + txtCust.getValue()
		// + "' and o1.ord_reference='"
		// + txtReference.getValue()
		// +
		// "' and descr2 like (select descr2||'%' from items where reference='"
		// + itmkind + "') " + " order by o1.ord_no,cost_item");

		wndList.addListener(new CloseListener() {
			public void windowClose(CloseEvent e) {
				try {
					for (int i = 0; i < tbl.getLstCheckSelection().size(); i++) {
						if (tbl.getLstCheckSelection().get(i).booleanValue()) {
							table.add_record();
							int lr = table.data.getRowCount() - 1;
							table.data.setFieldValue(lr, "ORD_REFER", tbl
									.getData().getFieldValue(i, "COST_ITEM"));
							table.data.setFieldValue(lr, "DESCR", tbl.getData()
									.getFieldValue(i, "SELLING_DESCR"));
							table.data.setFieldValue(lr, "ORD_PRICE", tbl
									.getData().getFieldValue(i, "FC_PRICE"));
							table.data.setFieldValue(lr, "PUR_KEYFLD", tbl
									.getData().getFieldValue(i, "KEYFLD"));
							table.data.setFieldValue(lr, "PUR_KEYFLD", tbl
									.getData().getFieldValue(i, "KEYFLD"));
							table.data.setFieldValue(lr, "ORD_RCPTNO", tbl
									.getData().getFieldValue(i, "RCPT_NO"));
							table.data.setFieldValue(lr, "ORD_RCPTNO", tbl
									.getData().getFieldValue(i, "RCPT_NO"));
							table.data.setFieldValue(lr, "ORD_RCPTNO", tbl
									.getData().getFieldValue(i, "RCPT_NO"));
							table.data.setFieldValue(lr, "ORD_COST_PRICE", tbl
									.getData().getFieldValue(i, "COST"));

							table.data.setFieldValue(lr, "PO_SR_NO", tbl
									.getData().getFieldValue(i, "ORD_NO"));

							String pr = utils.nvl(
									tbl.getData().getFieldValue(i,
											"PAYMENT_REFERENCE"), "");

							if (!pr.isEmpty()) {
								table.data.setFieldValue(lr, "ORD_PRICE", tbl
										.getData().getFieldValue(i, "COST"));
							}
							table.refreshRow(lr);

							utilsVaadin.findColByCol("ORD_PRICE",
									table.listFields).actionAfterUpdate
									.onValueChange(lr, "ORD_PRICE", table.data
											.getFieldValue(lr, "ORD_PRICE"));
							txtNo.focus();
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					parentLayout.getWindow().showNotification(
							"Error loading query:", e1.getMessage(),
							Notification.TYPE_HUMANIZED_MESSAGE);

				}
			}
		});

		tbl.createView();

		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wndList)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wndList);
		}
	}

	private void show_selection_items2(boolean show_only_empty_table)
			throws Exception {

		// if (table.data.getRowCount() > 0 && show_only_empty_table)
		// return;
		if (txtCust.getValue() == null
				|| txtCust.getValue().toString().isEmpty())
			return;
		String itmkind = Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars()
				.get("LG_ITEMS_" + txtItemsKind.getValue());
		if (itmkind == null || itmkind.isEmpty())
			return;

		Window wndList = ControlsFactory.CreateWindow("70%", "70%", true, true);
		final TableView tbl = new TableView(new DBClass(con));
		tbl.setCheckDefaultSelection(false);
		tbl.setCheckSelectionField(true);
		tbl.setParentPanel((VerticalLayout) wndList.getContent());

		tbl.FetchSql("select cost_item,items.descr cost_item_descr,"
				+ " selling_descr,fc_price,fc_rate,fc_descr,lg_custitems.keyfld from "
				+ "lg_custitems,items where items.reference=cost_item and"
				+ " code='"
				+ txtCust.getValue()
				+ "' and descr2 like (select descr2||'%' from items where reference='"
				+ itmkind + "') order by cost_item,type_of_frieght");

		wndList.addListener(new CloseListener() {
			public void windowClose(CloseEvent e) {
				try {
					for (int i = 0; i < tbl.getLstCheckSelection().size(); i++) {
						if (tbl.getLstCheckSelection().get(i).booleanValue()) {
							table.add_record();
							int lr = table.data.getRowCount() - 1;
							table.data.setFieldValue(lr, "ORD_REFER", tbl
									.getData().getFieldValue(i, "COST_ITEM"));
							table.data.setFieldValue(lr, "DESCR", tbl.getData()
									.getFieldValue(i, "SELLING_DESCR"));
							table.data.setFieldValue(lr, "ORD_PRICE", tbl
									.getData().getFieldValue(i, "FC_PRICE"));
							table.data.setFieldValue(lr, "PUR_KEYFLD", tbl
									.getData().getFieldValue(i, "KEYFLD"));
							table.data.setFieldValue(lr, "PUR_KEYFLD", tbl
									.getData().getFieldValue(i, "KEYFLD"));
							table.data.setFieldValue(lr, "ORD_RCPTNO", tbl
									.getData().getFieldValue(i, "RCPT_NO"));
							table.data.setFieldValue(lr, "ORD_RCPTNO", tbl
									.getData().getFieldValue(i, "RCPT_NO"));
							table.data.setFieldValue(lr, "ORD_RCPTNO", tbl
									.getData().getFieldValue(i, "RCPT_NO"));
							table.data.setFieldValue(lr, "ORD_COST_PRICE", tbl
									.getData().getFieldValue(i, "COST"));

							table.data.setFieldValue(lr, "PO_SR_NO", tbl
									.getData().getFieldValue(i, "ORD_NO"));

							String pr = utils.nvl(
									tbl.getData().getFieldValue(i,
											"PAYMENT_REFERENCE"), "");

							if (!pr.isEmpty()) {
								table.data.setFieldValue(lr, "ORD_PRICE", tbl
										.getData().getFieldValue(i, "COST"));
							}
							table.refreshRow(lr);

							utilsVaadin.findColByCol("ORD_PRICE",
									table.listFields).actionAfterUpdate
									.onValueChange(lr, "ORD_PRICE", table.data
											.getFieldValue(lr, "ORD_PRICE"));
							txtNo.focus();
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					parentLayout.getWindow().showNotification(
							"Error loading query:", e1.getMessage(),
							Notification.TYPE_HUMANIZED_MESSAGE);

				}
			}
		});

		tbl.createView();

		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wndList)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wndList);
		}
	}

}