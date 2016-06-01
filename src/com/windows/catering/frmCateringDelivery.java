package com.windows.catering;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.doc.views.TableView;
import com.doc.views.YesNoDialog;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog.Callback;
import com.generic.ColumnProperty;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window.Notification;

public class frmCateringDelivery implements transactionalForm {
	private Connection con = null;
	private localTableModel data_delivery = new localTableModel();

	private double varKeyfld = -1;
	private String QRYSES = "";
	private List<ColumnProperty> lstDlvCols = new ArrayList<ColumnProperty>();

	private Window wndList = new Window();
	VerticalLayout lstLayout = new VerticalLayout();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private GridLayout basicLayout = new GridLayout(4, 2);

	private NativeButton cmdSave = new NativeButton("Save & Post");
	private NativeButton cmdSaveSummary = new NativeButton(
			"Save & Show Summary");
	private NativeButton cmdRevert = new NativeButton("Revert");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdGetData = new NativeButton("Get Data");
	private NativeButton cmdDel = new NativeButton("Delete & Unpost");
	private NativeButton cmdCLS = new NativeButton("Clear");

	private TextField txtCustParent = new TextField("Parent Customer", "14");
	private TextField txtCustAc = new TextField("Customer Acc", "12050101");
	private TextField txtVouNo = new TextField("Voucher No");
	private TextField txtDescr = new TextField("Description");
	private DateField txtVouDate = new DateField("Date");

	private CheckBox chkGenerateCustomer = new CheckBox("Generate Customer ",
			true);

	private Upload upload = new Upload("Upload XLS file for auto delivery: ",
			new Receiver() {
				public OutputStream receiveUpload(String filename,
						String mimeType) {
					FileOutputStream fos = null; // Output stream to write to
					File file;
					file = new File(
							((WebApplicationContext) Channelplus3Application
									.getInstance().getContext())
									.getHttpSession().getServletContext()
									.getRealPath("/WEB-INF/")
									+ "/tmp/uploads/" + "tmp.xls");
					try {
						// Open the file for writing.
						fos = new FileOutputStream(file);
					} catch (final java.io.FileNotFoundException e) {
						e.printStackTrace();
						return null;
					}
					return fos; // Return the output stream to write to
				}

			});

	private boolean listnerAdded = false;

	private Table tbl_delivery = new Table();

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public frmCateringDelivery() {

	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		tbl_delivery.removeAllItems();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		commandLayout.removeAllComponents();

	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();

		centralPanel.setSizeFull();
		tbl_delivery.setSizeFull();
		mainLayout.setSizeFull();
		basicLayout.setSizeFull();
		txtDescr.setWidth("100%");
		txtVouDate.setWidth("100%");
		txtVouNo.setWidth("100%");

		cmdGetData.setSizeFull();

		txtVouDate.setResolution(DateField.RESOLUTION_DAY);
		txtVouDate.setDateFormat(utils.FORMAT_SHORT_DATE);

		tbl_delivery.setSelectable(true);
		tbl_delivery.setSortDisabled(true);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);
		ResourceManager.addComponent(mainLayout, tbl_delivery);

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdSaveSummary);
		ResourceManager.addComponent(commandLayout, cmdList);
		ResourceManager.addComponent(commandLayout, cmdDel);
		ResourceManager.addComponent(commandLayout, cmdRevert);
		ResourceManager.addComponent(commandLayout, cmdCLS);
		ResourceManager.addComponent(commandLayout, cmdGetData);

		ResourceManager.addComponent(basicLayout, txtVouNo);
		ResourceManager.addComponent(basicLayout, txtDescr);
		ResourceManager.addComponent(basicLayout, txtVouDate);
		ResourceManager.addComponent(basicLayout, upload);
		ResourceManager.addComponent(basicLayout, chkGenerateCustomer);
		ResourceManager.addComponent(basicLayout, txtCustParent);
		ResourceManager.addComponent(basicLayout, txtCustAc);

		mainLayout.setExpandRatio(commandLayout, .1f);
		mainLayout.setExpandRatio(basicLayout, .6f);
		mainLayout.setExpandRatio(tbl_delivery, 3.3f);
		basicLayout.setComponentAlignment(upload, Alignment.TOP_LEFT);
		upload.setButtonCaption("Upload");

		if (!listnerAdded) {

			cmdSaveSummary.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						save_data(false);
						Window wnd = new Window();
						VerticalLayout la = new VerticalLayout();
						wnd.setContent(la);
						utilsVaadin
								.showSearch(la,
										new SelectionListener() {

											public void onSelection(TableView tv) {
											}
										},
										con,
										"select ctg_code CATERGORY,sum(tot_amt) tot_amount,COUNT(*) COUNTS from kk_trans where trans_code=11 and KEYFLD= '"
												+ QRYSES
												+ "' GROUP BY CTG_CODE", true);
					} catch (SQLException ex) {
						ex.printStackTrace();

					}
				}
			});
			cmdCLS.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					load_data();

				}
			});

			cmdDel.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					if (QRYSES.isEmpty()) {
						load_data();
						return;
					}

					delete_data();
				}

			});

			upload.addListener(new SucceededListener() {

				public void uploadSucceeded(SucceededEvent event) {
					String fn = ((WebApplicationContext) Channelplus3Application
							.getInstance().getContext()).getHttpSession()
							.getServletContext().getRealPath("/WEB-INF/")
							+ "/tmp/uploads/tmp.xls";// + event.getFilename();

					String formname = "CAT_DELIVERY";
					try {
						data_delivery.getRows().clear();
						utils.fill_data_from_excel(fn, formname, data_delivery,
								con, true);
						data_update();
						fill_delivery_table();

					} catch (Exception e) {
						e.printStackTrace();
						parentLayout.getWindow()
								.showNotification(e.getMessage(),
										Notification.TYPE_ERROR_MESSAGE);
						data_delivery.getRows().clear();
					}

				}
			});

			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					showList();

				}
			});
			cmdRevert.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					load_data();
				}
			});
			cmdGetData.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						get_data();
					} catch (SQLException ex) {
						ex.printStackTrace();
						parentLayout.getWindow().showNotification(
								ex.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);
					}
					if (data_delivery.getRows().size() > 0) {
						txtVouDate.setReadOnly(true);
					} else {
						txtVouDate.setReadOnly(false);
					}
				}
			});
			listnerAdded = true;
		}
	}

	public void data_update() throws SQLException {

		PreparedStatement pst = con.prepareStatement(
				"select ctg_code,ctg_price from kk_ctg",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		Map<String, BigDecimal> mapDc = new HashMap<String, BigDecimal>();
		pst.executeQuery().beforeFirst();
		while (pst.getResultSet().next()) {
			mapDc.put(pst.getResultSet().getString("CTG_CODE"), pst
					.getResultSet().getBigDecimal("CTG_PRICE"));
		}
		pst.close();

		try {
			QueryExe qe = new QueryExe(
					"insert into temporary(idno,usernm,field1,field2,field3,field4,field5) values (7878,'A', :CRD_CUST_CODE , :TOT_AMT , :CTG_CODE , :POS , :NAME2 ) ",
					con);
			qe.parse();

			utils.execSql("delete from temporary where idno=7878", con);

			for (int i = 0; i < data_delivery.getRows().size(); i++) {

				if (mapDc.get(data_delivery.getFieldValue(i, "CTG_CODE")) == null) {
					fill_delivery_table();
					throw new SQLException(data_delivery.getFieldValue(i,
							"CTG_CODE")
							+ " not existed at row # " + i);

				}

				data_delivery.setFieldValue(i, "TOT_AMT", mapDc
						.get(data_delivery.getFieldValue(i, "CTG_CODE")
								.toString()));
				qe.setParaValue("CRD_CUST_CODE", data_delivery.getFieldValue(i,
						"CRD_CUST_CODE"));
				qe.setParaValue("CTG_CODE", data_delivery.getFieldValue(i,
						"CTG_CODE"));
				qe.setParaValue("TOT_AMT", data_delivery.getFieldValue(i,
						"TOT_AMT"));
				qe.setParaValue("POS", i + 1);
				qe.setParaValue("NAME2", data_delivery.getFieldValue(i,
						"CUST_NAME"));

				qe.execute(false);

			}
			qe.close();
			con.commit();

			String sql = "select code cust_code, field3 CTG_CODE, c_ycust.FLAG,to_number(field2) PRICE, NAME, ac_no CUST_ACCNO, value REVENUE_AC"
					+ " from temporary,c_ycust,setup where c_ycust.reference=field1 and idno=7878 and usernm='A' and setup.variable='KK_SALE_ACCNO' "
					+ " and c_ycust.code not in (select crd_cust_code from kk_trans where trans_code=11 and VOU_DATE= :DAT )  order by to_number(field4)  ";
			String sql2 = "select field1 code,field5 NAME,SETUP.VALUE revenue_ac,ac_no cust_accno,field3 ctg_code,to_number(field2) price,NAME NM from temporary,c_ycust,SETUP "
					+ "where idno=7878 and usernm='A' and field1=reference(+)  and name is  null AND SETUP.VARIABLE='KK_SALE_ACCNO' order by  to_number(field4) ";

			ResultSet rst = utils.getSqlRS(sql2, con);
			if (rst != null && rst.first()) {
				rst.close();
				final Window win = new Window();
				VerticalLayout lst = new VerticalLayout();
				win.setContent(lst);
				utilsVaadin.showSearch(lst, new SelectionListener() {

					public void onSelection(TableView tv) {
						Channelplus3Application.getInstance().getWindows()
								.remove(win);
					}
				}, con, sql2, true);
				parentLayout.getWindow().showNotification(
						" Some customer not existed in master ",
						Notification.TYPE_ERROR_MESSAGE);

			}
			get_data_temp(sql);
			if (chkGenerateCustomer.booleanValue() == true) {
				get_data_temp_not_existed(sql2);
			}

		} catch (SQLException ex) {
			con.rollback();
			throw ex;

		}

	}

	public void get_data_temp_not_existed(String sql) throws SQLException {
		QueryExe qe = new QueryExe(sql, con);
		qe.setParaValue("DAT", txtVouDate.getValue());
		ResultSet rs = qe.executeRS();
		rs.beforeFirst();
		int rn = -1;
		while (rs.next()) {
			rn = data_delivery.addRecord();
			data_delivery.setFieldValue(rn, "CRD_CUST_CODE", rs
					.getString("CODE"));
			data_delivery.setFieldValue(rn, "CRD_CUST_ACC", rs
					.getString("REVENUE_AC"));
			data_delivery.setFieldValue(rn, "DEB_PAY_AC", rs
					.getString("CUST_ACCNO"));
			data_delivery.setFieldValue(rn, "TOT_AMT", rs
					.getBigDecimal("PRICE"));
			data_delivery.setFieldValue(rn, "CTG_CODE", rs
					.getString("CTG_CODE"));
			data_delivery.setFieldValue(rn, "CUST_NAME", rs.getString("NAME"));
		}

	}

	public void get_data_temp(String sql) throws SQLException {
		data_delivery.getRows().clear();
		QueryExe qe = new QueryExe(sql, con);
		qe.setParaValue("DAT", txtVouDate.getValue());
		ResultSet rs = qe.executeRS();
		rs.beforeFirst();
		int rn = -1;
		while (rs.next()) {
			rn = data_delivery.addRecord();
			data_delivery.setFieldValue(rn, "CRD_CUST_CODE", rs
					.getString("CUST_CODE"));
			data_delivery.setFieldValue(rn, "CRD_CUST_ACC", rs
					.getString("REVENUE_AC"));
			data_delivery.setFieldValue(rn, "DEB_PAY_AC", rs
					.getString("CUST_ACCNO"));
			data_delivery.setFieldValue(rn, "TOT_AMT", rs
					.getBigDecimal("PRICE"));
			data_delivery.setFieldValue(rn, "CTG_CODE", rs
					.getString("CTG_CODE"));
			data_delivery.setFieldValue(rn, "CUST_NAME", rs.getString("NAME"));
		}
		rs.close();
	}

	public void get_data() throws SQLException {
		data_delivery.getRows().clear();
		tbl_delivery.removeAllItems();
		String sql = "select CUST_CODE, CTG_CODE, FROMDATE, TODATE, FLAG, PRICE, NAME, CUST_ACCNO, REVENUE_AC"
				+ " from kk_cust_delivery where :DAT >= FROMDATE and :DAT <= TODATE and flag=1 "
				+ " and cust_code not in (select crd_cust_code from kk_trans where trans_code=11 and kk_trans.vou_date= :DAT  and keyfld!= :KEYFLD )"
				+ "order by cust_code ";
		QueryExe qe = new QueryExe(sql, con);
		qe.setParaValue("DAT", txtVouDate.getValue());
		qe.setParaValue("KEYFLD", varKeyfld);
		ResultSet rs = qe.executeRS();
		rs.beforeFirst();
		int rn = -1;
		while (rs.next()) {
			rn = data_delivery.addRecord();
			data_delivery.setFieldValue(rn, "CRD_CUST_CODE", rs
					.getString("CUST_CODE"));
			data_delivery.setFieldValue(rn, "CRD_CUST_ACC", rs
					.getString("REVENUE_AC"));
			data_delivery.setFieldValue(rn, "DEB_PAY_AC", rs
					.getString("CUST_ACCNO"));
			data_delivery.setFieldValue(rn, "TOT_AMT", rs
					.getBigDecimal("PRICE"));
			data_delivery.setFieldValue(rn, "CTG_CODE", rs
					.getString("CTG_CODE"));
			data_delivery.setFieldValue(rn, "CUST_NAME", rs.getString("NAME"));
		}
		rs.close();
		fill_delivery_table();
	}

	public void showList() {

		wndList.setContent(lstLayout);
		TableView.SelectionListener sl = new SelectionListener() {
			public void onSelection(TableView tv) {
				Channelplus3Application.getInstance().getMainWindow()
						.removeWindow(wndList);
				try {
					QRYSES = tv.getData().getFieldValue(tv.getSelectionValue(),
							"KEYFLD").toString();
					load_data();
				} catch (Exception e) {
					Logger.getLogger(frmCateringRegistration.class.getName())
							.log(Level.SEVERE, null, e);
					parentLayout.getWindow().showNotification(
							"Error loading data from selection",
							Notification.TYPE_ERROR_MESSAGE);
				}

			}
		};

		try {
			String sq = "select descr,TO_CHAR(trans_date,'DD/MM/RRRR') TRANS_DATE, keyfld from kk_trans_1 where trans_code=11 order by keyfld desc";
			final TableView tblview = utilsVaadin.showSearch(lstLayout, sl,
					con, sq, true);
		} catch (Exception e) {
			Logger.getLogger(frmCateringRegistration.class.getName()).log(
					Level.SEVERE, null, e);
			parentLayout.getWindow().showNotification("Error filling table",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {
			utilsVaadin.applyColumns("CATERING.CUST_DELIVERY", tbl_delivery,
					lstDlvCols, con);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		if (data_delivery.getDbclass() == null) {
			try {
				data_delivery.createDBClassFromConnection(con);
			} catch (SQLException e) {
			}
		}

		load_data();

	}

	public void load_data() {
		txtVouDate.setReadOnly(false);
		varKeyfld = -1;
		txtVouNo.setValue("");
		txtDescr
				.setValue(utils
						.getSqlValue(
								"select nvl(max(value),'Membership delivery') from setup where variable='KK_TEXT'",
								con));
		tbl_delivery.removeAllItems();

		data_delivery.clearALl();

		if (txtVouDate.getValue() != null) {
			((java.util.Date) txtVouDate.getValue()).setTime(System
					.currentTimeMillis());
		} else {
			java.util.Date dt = new java.util.Date(System.currentTimeMillis());
			txtVouDate.setValue(dt);
		}
		try {

			PreparedStatement ps = con.prepareStatement(
					"select *from KK_TRANS_1 where keyfld='" + QRYSES + "'",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rst = ps.executeQuery();

			if (rst.first()) {
				varKeyfld = Double.valueOf(QRYSES);
				txtVouNo.setValue(rst.getString("KEYFLD"));
				((java.util.Date) txtVouDate.getValue()).setTime(rst
						.getTimestamp("TRANS_DATE").getTime());
				txtDescr.setValue(rst.getString("DESCR"));
			} else {
				varKeyfld = Double.valueOf(utils.getSqlValue(
						"select nvl(max(keyfld),0)+1 from KK_TRANS_1", con));
				txtVouNo.setValue(String.valueOf(varKeyfld));
			}

			ps.close();
			fetch_delivery_data();
			if (data_delivery.getRows().size() > 0) {
				txtVouDate.setReadOnly(true);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void fetch_delivery_data() throws SQLException {

		String sq = "select c_ycust.code cust_code,c_ycust.name cust_name,kk_trans.* from kk_trans,c_ycust"
				+ " where code=crd_cust_code and kk_trans.keyfld='"
				+ QRYSES
				+ "' order by no";
		data_delivery.executeQuery(sq, true);
		fill_delivery_table();

	}

	private void fill_delivery_table() throws SQLException {
		tbl_delivery.removeAllItems();
		utilsVaadin.query_data2(tbl_delivery, data_delivery, lstDlvCols);

	}

	public void print_data() {

	}

	public boolean validateData(int fromrowno, int torowno, boolean showMessage) {

		return true;
	}

	public void save_data() {
		save_data(true);
	}

	public void save_data(boolean clear) {
		if (!validateData(0, data_delivery.getRows().size(), true)) {
			return;
		}
		String sq1 = "INSERT INTO KK_TRANS_1(KEYFLD, DESCR, TRANS_DATE, JV_KEYFLD, TRANS_CODE) VALUES "
				+ "(:KEYFLD, :DESCR, :TRANS_DATE, NULL, 11)";
		String sq2 = "begin "
				+ "INSERT INTO KK_TRANS (KEYFLD, NO, TRANS_CODE, PAY_TYPE, "
				+ "VOU_DATE, BOOK_DATE, BOOK_NO, "
				+ "CRD_CUST_CODE, CRD_CUST_ACC, DEB_CODE, "
				+ "DEB_PAY_AC, DEB_PAY_EXP_AC, DEB_PAY_COMMISION_RATE, "
				+ "TOT_AMT, DISC_AMT, CONTRACT_DAYS, DISH_GIVEN, JV_KEYFLD, CTG_CODE) VALUES ( "
				+ ":KEYFLD, :NO, :TRANS_CODE, 0 , "
				+ ":VOU_DATE, null , NULL, "
				+ ":CRD_CUST_CODE, :CRD_CUST_ACC, null, "
				+ ":DEB_PAY_AC, null , 1 , "
				+ " :TOT_AMT, 0, 0, 0 , NULL, :CTG_CODE  );end;";
		String sq_del = "declare a number; cursor v2 is select accno,count(*) cnt "
				+ " from acvoucher2 where referkeyfld=:KEYFLD and refercode=10002 group by accno;"
				+ " begin   for x in v2 loop "
				+ " repair.decaccuses(x.accno,x.cnt); end loop;"
				+ " delete from acvoucher1 where refercode=10002 and referkeyfld=:KEYFLD; "
				+ " delete from acvoucher2 where refercode=10002 and referkeyfld=:KEYFLD; "
				+ " delete from kk_trans_1 where trans_code=11 and keyfld=:KEYFLD;  "
				+ " delete from kk_trans where trans_code=11 and keyfld=:KEYFLD;"
				+ " end;";
		try {

			con.setAutoCommit(false);
			if (!QRYSES.equals("")) {
				QueryExe qe_del = new QueryExe(sq_del, con);
				qe_del.setParaValue("KEYFLD", varKeyfld);
				qe_del.execute(true);
				qe_del.close();
			} else {
				QRYSES = utils.getSqlValue(
						"select nvl(max(keyfld),0)+1 from KK_TRANS_1", con);
				txtVouNo.setValue(QRYSES);
				varKeyfld = Double.valueOf(QRYSES);
			}

			QueryExe qe1 = new QueryExe(sq1, con);
			qe1.setParaValue("KEYFLD", varKeyfld);
			qe1.setParaValue("DESCR", txtDescr.getValue());
			qe1.setParaValue("TRANS_DATE", txtVouDate.getValue());
			qe1.execute();
			QueryExe qe2 = new QueryExe(sq2, con);
			qe2.parse();
			int recs = 0;
			for (int i = 0; i < data_delivery.getRows().size(); i++) {
				try {
					if (data_delivery.getFieldValue(i, "DEB_PAY_AC") == null
							|| data_delivery.getFieldValue(i, "DEB_PAY_AC")
									.toString().isEmpty()) {
						create_customer(i);
					}
					qe2.setParaValue("KEYFLD", varKeyfld);
					qe2.setParaValue("NO", (i + 1));
					qe2.setParaValue("TRANS_CODE", 11);
					qe2.setParaValue("VOU_DATE", txtVouDate.getValue());
					qe2.setParaValue("CRD_CUST_CODE", data_delivery
							.getFieldValue(i, "CRD_CUST_CODE"));
					qe2.setParaValue("CRD_CUST_ACC", data_delivery
							.getFieldValue(i, "CRD_CUST_ACC"));
					qe2.setParaValue("DEB_PAY_AC", data_delivery.getFieldValue(
							i, "DEB_PAY_AC"));
					qe2.setParaValue("TOT_AMT", data_delivery.getFieldValue(i,
							"TOT_AMT"));
					qe2.setParaValue("CTG_CODE", data_delivery.getFieldValue(i,
							"CTG_CODE"));
					qe2.execute(false);
					recs++;
				} catch (SQLException exx) {
					parentLayout.getWindow().showNotification(
							"Customer : "
									+ data_delivery.getFieldValue(i,
											"CRD_CUST_CODE") + " ",
							Notification.TYPE_ERROR_MESSAGE);
					throw exx;
				}
			}
			qe2.close();
			qe1.close();
			utils.execSql("begin X_POST_JV_CATERING_DELIVERY(" + varKeyfld
					+ "); end;", con);
			con.commit();

			parentLayout.getWindow().showNotification(
					recs + " # Customers  Saved Successfully",
					Notification.TYPE_ERROR_MESSAGE);
			if (clear) {
				QRYSES = "";
				load_data();
			} else {
				QRYSES = String.valueOf(varKeyfld);
				load_data();
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

	}

	public void create_customer(int rn) throws SQLException {
		String sql = "insert into c_ycust(code,name,ac_no,parentcustomer,path,reference,iscust,issupp) "
				+ "values ( :CODE , :NAME , :AC_NO , :PARENTCUSTOMER , :PATH ,  :REFERENCE, 'Y','N')";
		String ref = data_delivery.getFieldValue(rn, "CRD_CUST_CODE")
				.toString();
		String nm = data_delivery.getFieldValue(rn, "CUST_NAME").toString();
		String cod = data_delivery.getFieldValue(rn, "CRD_CUST_CODE")
				.toString().replaceAll("[a-zA-Z]", "");
		QueryExe qe = new QueryExe(sql, con);
		qe.setParaValue("CODE", cod);
		qe.setParaValue("NAME", nm);
		qe.setParaValue("AC_NO", txtCustAc.getValue());
		qe.setParaValue("PARENTCUSTOMER", txtCustParent.getValue());
		qe.setParaValue("PATH", "XXX\\1\\" + txtCustParent.getValue() + "\\"
				+ cod + "\"");
		qe.setParaValue("REFERENCE", ref);
		qe.execute();
		qe.close();

		data_delivery.setFieldValue(rn, "CRD_CUST_CODE", cod);
		data_delivery.setFieldValue(rn, "DEB_PAY_AC", txtCustAc.getValue());
	}

	public void delete_data() {

		Callback cb = new Callback() {
			public void onDialogResult(boolean resultIsYes) {

				if (!resultIsYes) {
					return;
				}

				String sq_del = "declare a number; cursor v2 is select accno,count(*) cnt "
						+ " from acvoucher2 where referkeyfld=:KEYFLD and refercode=10002 group by accno;"
						+ " begin   for x in v2 loop "
						+ " repair.decaccuses(x.accno,x.cnt); end loop;"
						+ " delete from acvoucher1 where refercode=10002 and referkeyfld=:KEYFLD; "
						+ " delete from acvoucher2 where refercode=10002 and referkeyfld=:KEYFLD; "
						+ " delete from kk_trans_1 where trans_code=11 and keyfld=:KEYFLD;  "
						+ " delete from kk_trans where trans_code=11 and keyfld=:KEYFLD;"
						+ " end;";
				try {

					con.setAutoCommit(false);
					if (!QRYSES.equals("")) {
						QueryExe qe_del = new QueryExe(sq_del, con);
						qe_del.setParaValue("KEYFLD", varKeyfld);
						qe_del.execute(true);
						qe_del.close();
						con.commit();

					}
					parentLayout.getWindow().showNotification(
							"Deleted successfully");
					QRYSES = "";
					load_data();

				} catch (SQLException ex) {
					ex.printStackTrace();
					parentLayout.getWindow().showNotification(ex.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
					try {
						con.rollback();
					} catch (SQLException ex1) {

					}
				}

			}

		};

		mainLayout
				.getWindow()
				.addWindow(
						new YesNoDialog(
								"Channel Plus Warning",
								"Do you want to remove delivery of date # "
										+ (new SimpleDateFormat(
												utils.FORMAT_SHORT_DATE)
												.format((java.util.Date) txtVouDate
														.getValue())), cb));
	}

	public void showInitView() {
		QRYSES = "";
		init();
		initForm();
	}

}
