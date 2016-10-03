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
import com.generic.ColumnProperty;
import com.generic.ColumnProperty.ColumnAction;
import com.generic.ColumnProperty.afterModelUpdated;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.FormLayoutManager;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.localTableModel.DefaultValueListner;
import com.generic.qryColumn;
import com.generic.rowTriggerListner;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.utilsVaadinPrintHandler;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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
import com.vaadin.ui.Window.Notification;

public class frmPurOrd implements transactionalForm {
	private AbstractLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	public boolean standAloneMode = false;
	public String standAloneRef = "";
	public Window standAloneWnd = null;
	public String standAloneType = "";
	public double standAloneNo = -1;
	private SimpleDateFormat sdf = new SimpleDateFormat(utils.FORMAT_SHORT_DATE);

	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	Tree tree = new Tree();
	private localTableModel data_Items = new localTableModel();

	private VerticalLayout tableLayout = new VerticalLayout();
	TableLayoutVaadin table = new TableLayoutVaadin(tableLayout);
	private final List<ColumnProperty> lstBranchCols = new ArrayList<ColumnProperty>();

	private FormLayoutManager basicLayout = new FormLayoutManager("100%");

	private VerticalLayout mainLayout = new VerticalLayout();
	// private VerticalLayout basicLayout = new VerticalLayout();

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout content1Layout = new HorizontalLayout();

	private String strDefaultCurrency = "KWD";
	private String strCurrencyFormat = "";

	private HorizontalLayout noLayout = new HorizontalLayout();
	private HorizontalLayout dateLayout = new HorizontalLayout();
	private HorizontalLayout typeLayout = new HorizontalLayout();
	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout employeeLayout = new HorizontalLayout();
	private HorizontalLayout dueDateLayout = new HorizontalLayout();
	private HorizontalLayout content7Layout = new HorizontalLayout();
	private HorizontalLayout orderLayout = new HorizontalLayout();
	private HorizontalLayout shimpLayout = new HorizontalLayout();
	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout customerLayout = new HorizontalLayout();
	private HorizontalLayout storeLayout = new HorizontalLayout();
	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout payItemLayout = new HorizontalLayout();
	private HorizontalLayout attentionLayout = new HorizontalLayout();
	private HorizontalLayout totalLayout = new HorizontalLayout();
	private HorizontalLayout content6Layout = new HorizontalLayout();
	private HorizontalLayout discAmtLayout = new HorizontalLayout();
	private HorizontalLayout amtLayout = new HorizontalLayout();
	private HorizontalLayout flagLayout = new HorizontalLayout();
	private HorizontalLayout content8Layout = new HorizontalLayout();

	private ComboBox txtItemsKind = ControlsFactory.CreateListField(null,
			"ONAME", "#LG_ITEM_ORDER", lstfldinfo);

	private TextField txtNo = ControlsFactory.CreateTextField(null, "ORD_NO",
			lstfldinfo);
	private DateField txtDate = ControlsFactory.CreateDateField(null,
			"ORD_DATE", lstfldinfo);

	private SearchField txtEmp = ControlsFactory.CreateSearchField(null,
			"ORD_EMPNO", lstfldinfo, "100%",
			"select no,name from salesp  where flag=1 order by no ", "NO",
			"NAME");

	private ComboBox txtType = ControlsFactory.CreateListField(null,
			"ORD_TYPE", "", lstfldinfo);

	private SearchField txtCust = ControlsFactory
			.CreateSearchField(
					null,
					"ORD_REF",
					lstfldinfo,
					"100%",
					"select code,name from c_ycust where issupp='Y' and flag=1 order by path",
					"CODE", "NAME");

	private ComboBox txtStore = ControlsFactory.CreateListField(null, "STRA",
			"select no,name from store order by no", lstfldinfo);

	private TextField txtCustName = ControlsFactory.CreateTextField(null,
			"ORD_REFNM", lstfldinfo);

	private DateField txtDueDate = ControlsFactory.CreateDateField(null,
			"ORD_SHPDT", lstfldinfo);

	private TextField txtPayItem = ControlsFactory.CreateTextField(null,
			"PAYTERM", lstfldinfo);

	private TextField txtInv = ControlsFactory.CreateTextField(null, "ATTN",
			lstfldinfo);

	private TextField txtTotal = ControlsFactory
			.CreateTextField(null, "", null);

	private TextField txtDiscAmt = ControlsFactory.CreateTextField(null,
			"ORD_DISCAMT", lstfldinfo);

	private TextField txtAmt = ControlsFactory.CreateTextField(null, "ORD_AMT",
			lstfldinfo);

	private TextField txtFcDescr = ControlsFactory.CreateTextField(null,
			"ORD_FC_DESCR", lstfldinfo, "100%", "KWD");
	private TextField txtFcRate = ControlsFactory.CreateTextField(null,
			"ORD_FC_RATE", lstfldinfo, "100%", "1");
	private SearchField txtOrderAcc = ControlsFactory
			.CreateSearchField(
					null,
					"ORDACC",
					lstfldinfo,
					"100%",
					"select accno,name from acaccount where actype=0 and childcount=0 order by path",
					"ACCNO", "NAME");

	private TextField txtShimp = ControlsFactory.CreateTextField(null,
			"ORD_REFERENCE", lstfldinfo);

	private CheckBox chkFlag = ControlsFactory.CreateCheckField(null,
			"SAL_AND_ISS", "N", "Y", null);

	private Label lblNo = new Label("No");
	private Label lblDate = new Label("Date");
	private Label lblEmp = new Label("Employee");
	private Label lblType = new Label("Type");
	private Label lblCust = new Label("lier");
	private Label lblDueDate = new Label("Due Date");
	private Label lblPayItem = new Label("Remarks");
	private Label lblInv = new Label(". Invoice Ref#");
	private Label lblTotal = new Label("Net");
	private Label lblDiscAmt = new Label("Disc Amt");
	private Label lblAmt = new Label("Gross amount");
	private Label lblOrder = new Label("Order Acc");
	private Label lblShimp = new Label("Reference");
	private Label lblGenClosing = new Label("SRV & Purchase");
	private Label lblStore = new Label("Store");

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

	private int varOrdCode = 103;
	private String varPurSrv = "N";
	private String varPurKeyfld = "";
	private String varSRVKeyfld = "";
	private String varOrdFlag = "2";
	private String varDefaultOrdFlag = "2";
	private String varApprovalPurOrdRequire = "FALSE";
	private String varDefaultStore = "";
	private String varPayKeyflds = "";

	public void save_data() {
		save_data(true);
	}

	public void save_data(boolean cls) {

		if ((!varPurKeyfld.isEmpty() || !varSRVKeyfld.isEmpty())
				&& varPurSrv.equals("N")) {
			return;
		}
		if (varOrdFlag.equals("1")) {
			return;
		}

		try {
			if (!varPayKeyflds.isEmpty())
				throw new Exception("Payment Receipt is existed # "
						+ varPayKeyflds);

			if (!QRYSES.isEmpty() && check_any_qty_issued() > 0)
				throw new Exception("Issued qty on this purchase !");

			validateData();
			DecimalFormat df = new DecimalFormat(strCurrencyFormat);
			con.setAutoCommit(false);

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

			String pcode = utils
					.getSqlValue(
							"select repair.getsetupvalue_2('CURRENT_PERIOD') from dual",
							con);
			String location = utils
					.getSqlValue(
							"select repair.getsetupvalue_2('DEFAULT_LOCATION') from dual",
							con);

			qe.setParaValue("PERIODCODE", pcode);
			qe.setParaValue("LOCATION_CODE", location);
			qe.setParaValue("ORD_CODE", varOrdCode);
			qe.setParaValue("YEAR", "Y");
			qe.setParaValue("DELIVEREDQTY", "0");
			qe.setParaValue("ORDERDQTY", "0");
			qe.setParaValue("DELIVERED_FREEQTY", "0");
			qe.setParaValue("HAVE_ADJUSTMENT", "0");
			qe.setParaValue("ADJUST_AMOUNT", "0");
			qe.setParaValue("ADJUST_CURRENCY", "0");
			qe.setParaValue("ADJUST_RATE", "0");
			qe.setParaValue("ORD_FLAG", varOrdFlag);
			qe.setParaValue("ORD_AMT",
					df.parse(utils.nvl(txtAmt.getValue(), "0")));
			qe.setParaValue("ORD_DISCAMT",
					df.parse(utils.nvl(txtDiscAmt.getValue(), "0")));

			if (QRYSES.isEmpty()) {
				if (varApprovalPurOrdRequire.equals("TRUE")) {
					varOrdFlag = "0";
					qe.setParaValue("ORD_FLAG", "0");
				}
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
			mp.put("PERIODCODE", pcode);
			mp.put("LOCATION_CODE", location);
			mp.put("ORD_FC_DESCR", txtFcDescr.getValue());
			mp.put("ORD_FC_RATE", txtFcRate.getValue());
			table.insert_to_table("ORDER2", mp, "", "ORD_ITMAVER , ");

			ResultSet rsr = QueryExe
					.getSqlRS(
							"select ord_rcptno,nvl(count(*),0) cnt from joined_order "
									+ "  where ord_code="
									+ varOrdCode
									+ " and ord_reference='"
									+ txtShimp.getValue()
									+ "' and ord_rcptno in (select ord_rcptno from order2 where ord_code="
									+ varOrdCode + " and ord_no="
									+ txtNo.getValue() + ") "
									+ " group by ord_rcptno "
									+ " having count(*)>1", con);
			if (rsr != null) {
				if (rsr.first()) {
					String rn = rsr.getString("ord_rcptno");
					rsr.close();
					throw new Exception("Recipt no found duplicate # " + rn);
				} else
					rsr.close();
			}

			pur_and_srv(Double.valueOf(txtNo.getValue() + ""));

			if (QRYSES.isEmpty())
				save_parameters();
			con.commit();
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtNo.getValue().toString();
			}

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
			}
		}
	}

	private void save_parameters() throws SQLException {
		String sql = "begin delete from cp_user_profiles where variable='PURCHASE_ORDER_ACCOUNT' and profileno= :PF ; ";
		sql = sql
				+ "insert into cp_user_profiles(variable,profileno,value,setgrpno) values ('PURCHASE_ORDER_ACCOUNT',:PF, :VL, 'GNR' ); end;";
		QueryExe qe = new QueryExe(sql, con);
		qe.setParaValue("PF", Channelplus3Application.getInstance()
				.getFrmUserLogin().CURRENT_PROFILENO);
		Channelplus3Application
				.getInstance()
				.getFrmUserLogin()
				.getMapVars()
				.put("PURCHASE_ORDER_ACCOUNT",
						txtOrderAcc.getValue().toString());
		qe.setParaValue("VL", txtOrderAcc.getValue());
		qe.execute();
		qe.close();
	}

	private void load_parametes() throws SQLException {
		ResultSet rso = utils.getSqlRS(
				"select accno,name from acaccount where accno='"
						+ Channelplus3Application.getInstance()
								.getFrmUserLogin().getMapVars()
								.get("PURCHASE_ORDER_ACCOUNT") + "'", con);
		if (rso != null && rso.first()) {
			txtOrderAcc.setDisplayValue(rso.getString("ACCNO"),
					rso.getString("NAME"));
		}
	}

	public void pur_and_srv(final double ordno) throws SQLException {
		if (chkFlag.booleanValue())
			utils.execSql("begin x_pur_and_srv('" + ordno + "',false); end;",
					con);
		else
			utils.execSql("begin x_pur_and_srv ('" + ordno + "',true); end;",
					con);
	}

	private double check_any_qty_issued() throws Exception {

		double no = 0;
		no = Double.valueOf(QueryExe
				.getSqlValue(
						"select sum(issued_qty) from order2 where ord_code="
								+ varOrdCode + " and ord_no='" + QRYSES + "'",
						con, "0")
				+ "");
		return no;
	}

	public void load_data() {
		try {
			strDefaultCurrency = Channelplus3Application.getInstance()
					.getFrmUserLogin().getMapVars().get("DEFAULT_CURRENCY");
			utils.findFieldInfoByObject(txtFcDescr, lstfldinfo).defaultValue = strDefaultCurrency;

			utilsVaadin.resetValues(basicLayout, false, false);
			utilsVaadin.setDefaultValues(lstfldinfo, false);

			strCurrencyFormat = getCurrencyFormat();

			varPurKeyfld = "";
			varSRVKeyfld = "";
			varPurSrv = "N";
			varOrdFlag = "2";
			varPayKeyflds = "";

			chkFlag.setEnabled(true);
			chkFlag.setValue(true);
			cmdSave.setEnabled(true);
			cmdDelete.setEnabled(true);
			txtItemsKind.setReadOnly(false);
			txtItemsKind.setValue(utilsVaadin.findByValue(txtItemsKind, "FR"));
			txtStore.setValue(utilsVaadin
					.findByValue(txtStore, varDefaultStore));
			txtType.setValue(utilsVaadin.findByValue(txtType, "1"));
			txtNo.setValue(utils.getSqlValue(
					"select nvl(max(ord_no),0)+1 from order1 where ord_code='"
							+ varOrdCode + "'", con));

			txtNo.setEnabled(true);

			if (txtDate.getValue() == null) {
				txtDate.setValue(new Date(System.currentTimeMillis()));
			}

			((Date) txtDate.getValue()).setTime(System.currentTimeMillis());

			if (standAloneMode
					&& !standAloneWnd.getCaption().contains("/ CLIENT")) {
				String custnm = QueryExe.getSqlValue(
						"select ord_ref||' - '||ord_refnm "
								+ " from  order1 where ord_no=" + standAloneRef
								+ " and ord_code=106", con, "")
						+ "";

				standAloneWnd.setCaption(standAloneWnd.getCaption()
						+ " / CLIENT : " + custnm);
			}
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"Select * from order1 where ord_no='" + QRYSES
								+ "' and ord_code=" + varOrdCode,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);

				varPurSrv = rst.getString("PUR_AND_SRV");
				varOrdFlag = rst.getString("ord_flag");
				varPurKeyfld = utils.nvl(rst.getString("PUR_KEYFLD"), "");
				varSRVKeyfld = utils.nvl(rst.getString("RECIPT_KEYFLD"), "");
				varPayKeyflds = utils.nvl(rst.getString("PAY_KEYFLD"), "");
				chkFlag.setValue(false);

				if (!varPurSrv.equals("Y")
						&& (!varPurKeyfld.isEmpty() || !varSRVKeyfld.isEmpty())) {
					chkFlag.setEnabled(false);
				}

				if (varPurSrv.equals("Y")) {
					chkFlag.setValue(true);
				}

				/*
				 * if ((!varPurKeyfld.isEmpty() || !varSRVKeyfld.isEmpty()) &&
				 * varPurSrv.equals("N")) { cmdSave.setEnabled(false);
				 * cmdDelete.setEnabled(false); }
				 */
				// finding recipt in sales or sales return
				ResultSet rs = QueryExe.getSqlRS(
						"select ord_rcptno , ord_reference from joined_order "
								+ " where ord_code=103 and ord_no='"
								+ txtNo.getValue() + "'", con);
				if (rs != null && rs.first()) {
					rs.beforeFirst();
					while (rs.next()) {
						String rn = rs.getString("ORD_RCPTNO");
						String rf = rs.getString("ORD_REFERENCE");
						double cnt = Double
								.valueOf(QueryExe
										.getSqlValue(
												" select nvl(count(*),0) from joined_order "
														+ "  where ord_code!=103 and ord_reference='"
														+ rf
														+ "' and ord_rcptno='"
														+ rn + "'", con, "0")
										+ "");
						if (cnt > 0) {
							parentLayout.getWindow().showNotification("",
									" Recipt Found !,  # " + rn);
							cmdSave.setEnabled(false);
							cmdDelete.setEnabled(false);
						}
					}
				}
				if (varOrdFlag.equals("1")) {
					cmdSave.setEnabled(false);
					cmdDelete.setEnabled(false);
				}

				// if (!varPayKeyflds.isEmpty()) {
				// cmdSave.setEnabled(false);
				// }

				txtNo.setEnabled(false);
				fetch_query();
				pstmt.close();

				if (table.data.getRowCount() > 0)
					txtItemsKind.setReadOnly(true);
				// cmdSave.setEnabled(false);
				// if (check_any_qty_issued() > 0)
				// cmdSave.setEnabled(false);
			} else {
				load_parametes();
			}

			table.data
					.executeQuery(
							"select order2.*, 0 discp,((ORD_PRICE-ORD_DISCAMT)/ORD_PACK)*ORD_ALLQTY amount,"
									+ "0 ord_balance_avail,0 ord_cost_price,0 ord_cost_amt"
									+ ",0 lsprice,0 lsamt from order2 where ord_no='"
									+ QRYSES
									+ "' and ord_code="
									+ varOrdCode
									+ " order by ord_pos", true);
			table.fill_table();

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void fetch_query() {
		if (txtEmp.getValue() != null
				&& !txtEmp.getValue().toString().isEmpty())
			txtEmp.setDisplay(utils.getSqlValue(
					"select name from salesp where no='" + txtEmp.getValue()
							+ "'", con));
		if (txtOrderAcc.getValue() != null
				&& !txtOrderAcc.getValue().toString().isEmpty())
			txtOrderAcc.setDisplay(utils.getSqlValue(
					"select name from acaccount where accno='"
							+ txtOrderAcc.getValue() + "'", con));

	}

	public void applyColumnsOnBranch() {
		lstBranchCols.clear();
		try {
			utilsVaadin.applyColumns("FRMPURORD.ORDER2", table.getTable(),
					table.listFields, con);
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
					pk = (pk == 0 ? 1 : pk);
					double amt = (allqt) * ((pr - disc) / pk);

					table.data.setFieldValue(rowno, "ORD_ALLQTY",
							BigDecimal.valueOf(allqt));
					table.data.setFieldValue(rowno, "AMOUNT",
							BigDecimal.valueOf(amt));
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
					double disc_amt = ((BigDecimal) table.data.getFieldValue(
							rowno, "ORD_PRICE")).doubleValue();

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
					ResultSet rst = utils.getSqlRS(
							"select *from items where reference='" + vl + "'",
							con);
					try {

						if (rst != null && rst.first()) {
							table.data.setFieldValue(rowno, "DESCR",
									rst.getString("DESCR"));
							table.data.setFieldValue(rowno, "ORD_PACKD",
									rst.getString("PACKD"));

							table.data.setFieldValue(rowno, "ORD_UNITD",
									rst.getString("UNITD"));
							if (rst.getString("lpprice") != null) {
								table.data.setFieldValue(rowno, "ORD_PRICE",
										rst.getBigDecimal("LPPRICE"));
							}
							table.data.setFieldValue(rowno, "ORD_PACK",
									rst.getBigDecimal("PACK"));
							rst.close();
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
			if (!chkFlag.booleanValue())
				return;
			String qr = utils.getSqlValue(
					"select pur_keyfld from order1 where ord_no='" + QRYSES
							+ "' and ord_code=" + varOrdCode, con);
			utilsVaadinPrintHandler.printPurchase(Integer.valueOf(qr), con);
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

		varApprovalPurOrdRequire = utils
				.getSqlValue(
						"select nvl(max(repair.getsetupvalue_2('APPROVAL_PURORD_REQUIRE')) ,'FALSE') from dual",
						con);

		createView();
		setupValues();
		load_data();
	}

	public void setupValues() {
		varDefaultStore = (Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars().get("DEFAULT_STORE"));
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
		content6Layout.setWidth("100%");
		discAmtLayout.setWidth("100%");
		amtLayout.setWidth("100%");
		content7Layout.setWidth("100%");
		orderLayout.setWidth("100%");
		shimpLayout.setWidth("100%");
		flagLayout.setWidth("100%");
		storeLayout.setWidth("100%");

		txtNo.setWidth("100%");
		txtDate.setWidth("100%");
		txtEmp.setWidth("100%");
		txtType.setWidth("100%");
		txtCust.setWidth("100%");
		txtCustName.setWidth("100%");
		txtDueDate.setWidth("100%");
		txtPayItem.setWidth("100%");
		txtInv.setWidth("100%");
		txtTotal.setWidth("100%");
		txtStore.setWidth("100%");

		txtDiscAmt.setWidth("100%");
		txtAmt.setWidth("100%");
		txtOrderAcc.setWidth("100%");
		txtShimp.setWidth("100%");
		chkFlag.setWidth("100%");

		lblNo.setWidth("64px");
		lblStore.setWidth("60px");
		lblDate.setWidth("60px");
		lblEmp.setWidth("60px");
		lblType.setWidth("60px");
		lblCust.setWidth("62px");
		lblDueDate.setWidth("60px");
		lblPayItem.setWidth("62px");
		lblInv.setWidth("120px");
		lblTotal.setWidth("60px");
		lblDiscAmt.setWidth("60px");
		lblAmt.setWidth("60px");
		lblShimp.setWidth("60px");
		lblOrder.setWidth("60px");
		lblGenClosing.setWidth("90px");

		txtCust.setImmediate(true);
		txtDiscAmt.setImmediate(true);
		txtFcDescr.setImmediate(true);
		txtFcDescr.addStyleName("uptxt");

		txtCust.setShowDisplayWithValue(false);
		txtCust.setShowValueOnly(true);

		tableLayout.setHeight("300px");
		table.getTable().setHeight("275px");

		basicLayout.addStyleName("formLayout");

		txtAmt.addStyleName("netAmtStyle yellowField");
		txtTotal.addStyleName("netAmtStyle yellowField");
		txtDiscAmt.addStyleName("netAmtStyle yellowField");

		utils.findFieldInfoByObject(txtAmt, lstfldinfo).format = strCurrencyFormat;
		utils.findFieldInfoByObject(txtAmt, lstfldinfo).fieldType = Parameter.DATA_TYPE_NUMBER;

		utils.findFieldInfoByObject(txtDiscAmt, lstfldinfo).format = strCurrencyFormat;
		utils.findFieldInfoByObject(txtDiscAmt, lstfldinfo).fieldType = Parameter.DATA_TYPE_NUMBER;

		txtAmt.setReadOnly(true);
		txtTotal.setReadOnly(true);

		txtType.removeAllItems();
		txtType.addItem(new dataCell("W/O OFFER", "1"));
		txtType.addItem(new dataCell("JOB_ORDER", "2"));

		txtType.addItem(new dataCell("OFFER", "3"));
		txtType.setValue(utilsVaadin.findByValue(txtType, "1"));

		txtItemsKind.setNullSelectionAllowed(false);
		txtItemsKind.setImmediate(true);

		txtShimp.setEnabled(false);

		utils.findFieldInfoByObject(txtAmt, lstfldinfo).format = Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY;
		utils.findFieldInfoByObject(txtAmt, lstfldinfo).fieldType = Parameter.DATA_TYPE_NUMBER;

		utils.findFieldInfoByObject(txtDiscAmt, lstfldinfo).format = Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY;
		utils.findFieldInfoByObject(txtDiscAmt, lstfldinfo).fieldType = Parameter.DATA_TYPE_NUMBER;

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
		ResourceManager.addComponent(basicLayout, content7Layout);
		ResourceManager.addComponent(basicLayout, content3Layout);
		ResourceManager.addComponent(basicLayout, content4Layout);

		ResourceManager.addComponent(basicLayout, tableLayout);

		ResourceManager.addComponent(basicLayout, content6Layout);
		ResourceManager.addComponent(basicLayout, content8Layout);

		ResourceManager.addComponent(content1Layout, noLayout);
		ResourceManager.addComponent(content1Layout, dateLayout);
		ResourceManager.addComponent(content1Layout, typeLayout);

		ResourceManager.addComponent(content2Layout, employeeLayout);
		ResourceManager.addComponent(content2Layout, shimpLayout);

		ResourceManager.addComponent(content3Layout, customerLayout);
		ResourceManager.addComponent(content3Layout, storeLayout);

		ResourceManager.addComponent(content4Layout, payItemLayout);
		ResourceManager.addComponent(content4Layout, attentionLayout);
		ResourceManager.addComponent(content4Layout, flagLayout);

		ResourceManager.addComponent(content6Layout, amtLayout);
		ResourceManager.addComponent(content6Layout, discAmtLayout);
		ResourceManager.addComponent(content6Layout, totalLayout);

		ResourceManager.addComponent(content7Layout, orderLayout);
		ResourceManager.addComponent(content7Layout, dueDateLayout);

		ResourceManager.addComponent(orderLayout, lblOrder);
		ResourceManager.addComponent(orderLayout, txtOrderAcc);

		ResourceManager.addComponent(shimpLayout, lblShimp);
		ResourceManager.addComponent(shimpLayout, txtShimp);

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

		ResourceManager.addComponent(attentionLayout, lblInv);
		ResourceManager.addComponent(attentionLayout, txtInv);

		ResourceManager.addComponent(flagLayout, lblGenClosing);
		ResourceManager.addComponent(flagLayout, chkFlag);

		ResourceManager.addComponent(totalLayout, lblTotal);
		ResourceManager.addComponent(totalLayout, txtTotal);

		ResourceManager.addComponent(discAmtLayout, lblDiscAmt);
		ResourceManager.addComponent(discAmtLayout, txtDiscAmt);

		ResourceManager.addComponent(amtLayout, lblAmt);
		ResourceManager.addComponent(amtLayout, txtAmt);

		ResourceManager.addComponent(content8Layout, new Label("FC Descr"));
		ResourceManager.addComponent(content8Layout, txtFcDescr);
		ResourceManager.addComponent(content8Layout, new Label("Rate"));
		ResourceManager.addComponent(content8Layout, txtFcRate);

		ResourceManager.addComponent(employeeLayout, lblEmp);
		ResourceManager.addComponent(employeeLayout, txtEmp);
		ResourceManager.addComponent(customerLayout, lblCust);
		ResourceManager.addComponent(customerLayout, txtCust);
		ResourceManager.addComponent(customerLayout, txtCustName);
		ResourceManager.addComponent(storeLayout, lblStore);
		ResourceManager.addComponent(storeLayout, txtStore);

		noLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);

		dateLayout.setComponentAlignment(lblDate, Alignment.BOTTOM_CENTER);

		employeeLayout.setComponentAlignment(lblEmp, Alignment.BOTTOM_CENTER);

		typeLayout.setComponentAlignment(lblType, Alignment.BOTTOM_CENTER);

		customerLayout.setComponentAlignment(lblCust, Alignment.BOTTOM_CENTER);

		storeLayout.setComponentAlignment(lblStore, Alignment.BOTTOM_CENTER);

		dueDateLayout
				.setComponentAlignment(lblDueDate, Alignment.BOTTOM_CENTER);

		payItemLayout
				.setComponentAlignment(lblPayItem, Alignment.BOTTOM_CENTER);

		attentionLayout.setComponentAlignment(lblInv, Alignment.BOTTOM_CENTER);

		flagLayout
				.setComponentAlignment(lblGenClosing, Alignment.BOTTOM_CENTER);

		totalLayout.setComponentAlignment(lblTotal, Alignment.BOTTOM_CENTER);

		discAmtLayout
				.setComponentAlignment(lblDiscAmt, Alignment.BOTTOM_CENTER);

		amtLayout.setComponentAlignment(lblAmt, Alignment.BOTTOM_CENTER);

		orderLayout.setComponentAlignment(lblOrder, Alignment.BOTTOM_CENTER);

		shimpLayout.setComponentAlignment(lblShimp, Alignment.BOTTOM_CENTER);

		content1Layout.setExpandRatio(noLayout, 1.4f);
		content1Layout.setExpandRatio(dateLayout, 1.3f);
		content1Layout.setExpandRatio(typeLayout, 1.4f);

		content2Layout.setExpandRatio(employeeLayout, 2.6f);
		content2Layout.setExpandRatio(shimpLayout, 1.4f);

		content3Layout.setExpandRatio(customerLayout, 2.6f);
		content3Layout.setExpandRatio(storeLayout, 1.4f);

		content4Layout.setExpandRatio(payItemLayout, 1.5f);
		content4Layout.setExpandRatio(attentionLayout, 1.5f);
		content4Layout.setExpandRatio(flagLayout, 1f);

		content6Layout.setExpandRatio(discAmtLayout, 1.2f);
		content6Layout.setExpandRatio(amtLayout, 1.4f);
		content6Layout.setExpandRatio(totalLayout, 1.4f);

		table.getHzLayout().addComponent(txtItemsKind);

		noLayout.setExpandRatio(lblNo, 0.1f);
		noLayout.setExpandRatio(txtNo, 3.9f);

		dateLayout.setExpandRatio(lblDate, 0.1f);
		dateLayout.setExpandRatio(txtDate, 3.9f);

		typeLayout.setExpandRatio(lblType, 0.1f);
		typeLayout.setExpandRatio(txtType, 3.9f);

		content7Layout.setExpandRatio(orderLayout, 2.6f);
		content7Layout.setExpandRatio(dueDateLayout, 1.4f);

		employeeLayout.setExpandRatio(lblEmp, 0.1f);
		employeeLayout.setExpandRatio(txtEmp, 2.9f);

		customerLayout.setExpandRatio(lblCust, 0.1f);
		customerLayout.setExpandRatio(txtCust, 1f);
		customerLayout.setExpandRatio(txtCustName, 2.9f);

		storeLayout.setExpandRatio(lblStore, 0.1f);
		storeLayout.setExpandRatio(txtStore, 2.9f);

		orderLayout.setExpandRatio(lblOrder, 0.1f);
		orderLayout.setExpandRatio(txtOrderAcc, 2.9f);

		shimpLayout.setExpandRatio(lblShimp, 0.1f);
		shimpLayout.setExpandRatio(txtShimp, 3.9f);

		dueDateLayout.setExpandRatio(lblDueDate, 0.1f);
		dueDateLayout.setExpandRatio(txtDueDate, 3.9f);

		discAmtLayout.setExpandRatio(lblDiscAmt, 0.1f);
		discAmtLayout.setExpandRatio(txtDiscAmt, 3.9f);

		amtLayout.setExpandRatio(lblAmt, 0.1f);
		amtLayout.setExpandRatio(txtAmt, 3.9f);

		payItemLayout.setExpandRatio(lblPayItem, 0.1f);
		payItemLayout.setExpandRatio(txtPayItem, 3.9f);

		attentionLayout.setExpandRatio(lblInv, 1f);
		attentionLayout.setExpandRatio(txtInv, 3f);

		flagLayout.setExpandRatio(lblGenClosing, 3f);
		flagLayout.setExpandRatio(chkFlag, 1f);

		totalLayout.setExpandRatio(lblTotal, 0.1f);
		totalLayout.setExpandRatio(txtTotal, 3.9f);

		applyColumnsOnBranch();
		if (!listnerAdded) {

			txtItemsKind.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					utilsVaadin.findColByCol("ORD_REFER", table.listFields).searchListSQL = get_item_listSQL();

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
						if (!QRYSES.isEmpty() && check_any_qty_issued() > 0)
							throw new SQLException(
									"Issued qty on this purchase !");

					} catch (Exception e) {
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
			cmdPrint.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					print_data();
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
					if (qc.getColname().contains("ORD_ALLQTY")) {
						dc.setValue("1", BigDecimal.valueOf(1.0f));
					}

				}
			});
			table.data.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (cursorNo < 0)
						return;
					calc_summary();
				}
			});
			txtCust.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					utilsVaadin.setValueByForce(txtCustName,
							txtCust.getDisplay());

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

			txtFcDescr.addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					change_fc_rate();
				}
			});

			listnerAdded = true;

		}

	}

	protected void delete_data(double ordno) {
		try {
			if (!QRYSES.isEmpty() && check_any_qty_issued() > 0)
				throw new Exception("Issued qty on this purchase !");

			int ord_flg = Integer.valueOf(utils.getSqlValue(
					"select nvl(max(ord_flag),-1) from order1 where ord_code="
							+ varOrdCode + " and  ord_no=" + ordno, con));
			if (ord_flg == 1)
				throw new SQLException("Closed by other user");

			if (ord_flg < 0)
				throw new SQLException(
						"Not found this order due to delete by other user");

			utils.execSql("begin x_pur_and_srv('" + ordno
					+ "',true); delete from order1 where ord_code="
					+ varOrdCode + " and ord_no=" + ordno + ";"
					+ "delete from order2 where ord_code=" + varOrdCode
					+ " and ord_no=" + ordno + "; end;", con);
			if (!varPayKeyflds.isEmpty()) {
				utils.execSql(
						" declare "
								+ " cursor kfs is select *from lg_pays where keyfld in ("
								+ varPayKeyflds
								+ "); "
								+ " begin "
								+ " for x in kfs loop "
								+ " delete from acvoucher2 where keyfld=x.close_jv;"
								+ " delete from acvoucher1 where keyfld=x.close_jv;"
								+ " end loop; "
								+ " update lg_pays set po_ord_no=null, flag=1 WHERE keyfld in ("
								+ varPayKeyflds + ");end;", con);
			}
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			parentLayout.getWindow().showNotification(e.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e1) {
			}

		}

	}

	protected void calc_summary() {
		double d = table.data.getSummaryOf("AMOUNT",
				localTableModel.SUMMARY_SUM);
		double ds = 0;
		if (txtDiscAmt.getValue() != null
				&& !txtDiscAmt.getValue().toString().isEmpty())
			ds = Double.valueOf(txtDiscAmt.getValue().toString());
		DecimalFormat df = new DecimalFormat(strCurrencyFormat);
		// utilsVaadin.setValueByForce(txtDiscAmt, df.format(ds));
		utilsVaadin.setValueByForce(txtAmt, df.format(d));
		utilsVaadin.setValueByForce(txtTotal, df.format(d - ds));

		if (table.data.getRowCount() > 0)
			txtItemsKind.setReadOnly(true);
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

		content6Layout.removeAllComponents();
		content8Layout.removeAllComponents();
		discAmtLayout.removeAllComponents();
		amtLayout.removeAllComponents();
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
			}, con,
					"select ord_no,ord_refnm,ord_date from order1 where ord_code='"
							+ varOrdCode
							+ "' order by ord_date desc,ord_no desc", true);

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
							"Select code,name  from c_ycust  where is_cust='Y' order by path",
							true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	public void validateData() throws Exception {
		if (!((dataCell) txtType.getValue()).getValue().toString().equals("1")
				&& (txtShimp.getValue() == null || txtShimp.getValue()
						.toString().isEmpty()))
			throw new SQLException("Must assign reference !");
		if (txtOrderAcc.getValue() == null
				|| txtOrderAcc.getValue().toString().isEmpty()) {
			throw new SQLException("Order A/c must have value");
		}
		if (txtStore.getValue() == null)
			throw new SQLException("Store must have value");

		if (txtCust.getValue() == null
				|| txtCust.getValue().toString().isEmpty())
			throw new SQLException("lier must have value");

		if (txtType.getValue() == null)
			throw new SQLException("Type must have value");

		if (txtDate.getValue() == null)
			throw new SQLException("Date should have value !");

		Date dt = (Date) QueryExe.getSqlValue(
				"select ord_date from order1 where ord_code=106 "
						+ " and ord_no='" + txtShimp.getValue() + "'", con,
				null);
		if (dt.compareTo((Date) txtDate.getValue()) > 0)
			throw new SQLException("Date is less than job order date # "
					+ sdf.format(dt));

		if (QRYSES.isEmpty()) {
			String ord = utils.getSqlValue(
					"select ord_no from order1 where ord_code=" + varOrdCode
							+ " and ord_no='" + txtNo.getValue() + "'", con);
			if (!ord.isEmpty()) {
				throw new SQLException("Order no already existed");
			}
		} else {
			int ord_flg = Integer.valueOf(utils.getSqlValue(
					"select nvl(max(ord_flag),-1) from order1 where ord_code="
							+ varOrdCode + " and ord_no=" + QRYSES, con));
			if (ord_flg == 1)
				throw new SQLException("Closed by other user");

			if (ord_flg < 0)
				throw new SQLException(
						"Not found this order due to delete by other user");
		}

		for (int i = 0; i < table.data.getRows().size(); i++) {

			String rfr = table.data.getFieldValue(i, "ORD_REFER").toString();
			String rcptno = utils.nvl(
					table.data.getFieldValue(i, "ORD_RCPTNO"), "");

			if (rcptno.isEmpty())
				throw new SQLException("Recipt No must have value");

			if (txtDiscAmt.getValue() == null
					|| txtDiscAmt.getValue().toString().isEmpty())
				txtDiscAmt.setValue("0.000");

			DecimalFormat df = new DecimalFormat(strCurrencyFormat);

			double discInv = df.parse(txtDiscAmt.getValue() + "").doubleValue();
			double InvAmt = df.parse(txtAmt.getValue() + "").doubleValue();

			double disc = ((BigDecimal) table.data.getFieldValue(i,
					"ORD_DISCAMT")).doubleValue();
			double pkqt = ((BigDecimal) table.data
					.getFieldValue(i, "ORD_PKQTY")).doubleValue();
			double qt = ((BigDecimal) table.data.getFieldValue(i, "ORD_UNQTY"))
					.doubleValue();
			double pk = ((BigDecimal) table.data.getFieldValue(i, "ORD_PACK"))
					.doubleValue();
			double allqt = (pkqt * pk) + qt;
			double pr = ((BigDecimal) table.data.getFieldValue(i, "ORD_PRICE"))
					.doubleValue();

			if (discInv > InvAmt) {
				txtDiscAmt.focus();
				throw new SQLException(
						"Invoice discount is greater than total amount");
			}

			if (disc > pr || disc < 0 || pr < 0) {
				table.getTable().setValue(Integer.valueOf(i));
				throw new SQLException(
						"Discount or price is not valid for Item ->" + rfr);
			}
			if (allqt <= 0 || pkqt < 0 || qt < 0) {
				table.getTable().setValue(Integer.valueOf(i));
				throw new SQLException("Qty is not valid for Item ->" + rfr);
			}

			int cnt = Integer.valueOf(utils.getSqlValue(
					"select nvl(count(*),0 ) from items where parentitem='"
							+ rfr + "'", con));

			if (cnt > 0) {
				table.getTable().setValue(Integer.valueOf(i));
				throw new SQLException("You can not use parent item here ->"
						+ rfr);

			}

		}
	}

	protected String get_item_listSQL() {
		String sq = "select reference,descr from items where flag= 1 order by descr2";

		String itmkind = Channelplus3Application.getInstance()
				.getFrmUserLogin().getMapVars()
				.get("LG_ITEMS_" + txtItemsKind.getValue());

		if (txtShimp.getValue() != null
				&& !txtShimp.getValue().toString().isEmpty()
				&& txtType.getValue() != null) {
			String typ = ((dataCell) txtType.getValue()).getValue().toString();
			if (!typ.equals("1")) {

				String customer = utils.getSqlValue(
						"select ord_ref from order1 where ord_code=106 and ord_no='"
								+ txtShimp.getValue() + "'", con);
				sq = "select reference,descr from items where flag=1 and "
						+ "descr2 like (select descr2||'%' from items where reference='"
						+ itmkind
						+ "') and reference in (select cost_item from lg_custitems where code='"
						+ customer + "') order by descr2";
			}
		}

		return sq;
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
				txtShimp.setValue(standAloneRef);
				ResultSet rsx = utils.getSqlRS(
						"select ord_ref,ord_refnm from order1 where ord_code=decode("
								+ typ + ",2,106,3,100) and ord_no='" + txtRefer
								+ "'", con);

				txtCust.setValue(rsx.getString("ORD_REF"));
				txtCustName.setValue(rsx.getString("ord_refnm"));
				rsx.getStatement().close();
			} else {
				QRYSES = on + "";
				load_data();
			}
			utilsVaadin.findColByCol("ORD_REFER", table.listFields).searchListSQL = get_item_listSQL();
			txtType.setEnabled(false);
			txtShimp.setEnabled(false);
		} catch (SQLException e) {
			Channelplus3Application.getInstance().getMainWindow()
					.removeWindow(wnd);
			e.printStackTrace();
		}

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

}
