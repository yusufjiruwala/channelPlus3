package com.windows.catering;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.generic.ColumnProperty;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.Row;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.qryColumn;
import com.generic.rowTriggerListner;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.ColumnProperty.ColumnAction;
import com.generic.localTableModel.DefaultValueListner;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmCateringSubscription implements transactionalForm {

	private static final long serialVersionUID = 101212L;
	private Connection con = null;
	PreparedStatement ps_cust_data = null;
	PreparedStatement ps_subs_data = null;

	private localTableModel data_customers = new localTableModel();
	private localTableModel data_subscription = new localTableModel();
	private Table tbl_customers = new Table();
	private String QRYSES = "";

	private VerticalLayout layout1 = new VerticalLayout();
	private VerticalLayout layout2 = new VerticalLayout();
	private VerticalSplitPanel splitPanel = new VerticalSplitPanel();
	private HorizontalLayout headerLayout2 = new HorizontalLayout();
	private HorizontalLayout headerLayout1 = new HorizontalLayout();
	private HorizontalLayout commandLayout1 = new HorizontalLayout();
	private HorizontalLayout commandLayout2 = new HorizontalLayout();

	private AbstractLayout parentLayout = null;

	private TextField txtFind = new TextField();
	private NativeButton cmdSave = new NativeButton();
	private NativeButton cmdAdd = new NativeButton();
	private NativeButton cmdRemove = new NativeButton();
	private NativeButton cmdRevert = new NativeButton();

	private NativeButton cmdFind = new NativeButton();

	private Table tbl_subscription = new Table();

	private final List<ColumnProperty> lstCustomerCols = new ArrayList<ColumnProperty>();
	private final List<ColumnProperty> lstSubCols = new ArrayList<ColumnProperty>();

	private boolean listnerAdded = false;

	public frmCateringSubscription() {
		init();
	}

	public void resetFormLayout() {
		data_customers.clearALl();
		data_subscription.clearALl();

		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		layout1.removeAllComponents();
		layout2.removeAllComponents();
		headerLayout1.removeAllComponents();
		headerLayout2.removeAllComponents();
		tbl_subscription.removeAllItems();
		tbl_customers.removeAllItems();

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		tbl_subscription.setSizeFull();
		tbl_customers.setSizeFull();
		layout1.setSizeFull();
		layout2.setSizeFull();
		tbl_customers.setSelectable(true);
		tbl_subscription.setSortDisabled(true);
		headerLayout1.setSizeFull();
		headerLayout2.setSizeFull();
		centralPanel.setSizeFull();
		splitPanel.setSizeFull();

		tbl_subscription.setSelectable(true);
		tbl_subscription.setImmediate(true);
		tbl_customers.setImmediate(true);
		tbl_subscription.setSelectable(true);

		splitPanel.setFirstComponent(layout1);
		splitPanel.setSecondComponent(layout2);
		centralPanel.addComponent(splitPanel);
		ResourceManager.addComponent(layout1, headerLayout1);
		ResourceManager.addComponent(layout1, tbl_customers);
		ResourceManager.addComponent(layout2, headerLayout2);
		ResourceManager.addComponent(layout2, tbl_subscription);

		ResourceManager.addComponent(headerLayout1, commandLayout1);
		ResourceManager.addComponent(commandLayout1, txtFind);
		ResourceManager.addComponent(commandLayout1, cmdFind);
		ResourceManager.addComponent(headerLayout2, commandLayout2);
		ResourceManager.addComponent(commandLayout2, cmdAdd);
		ResourceManager.addComponent(commandLayout2, cmdRemove);
		ResourceManager.addComponent(commandLayout2, cmdRevert);
		ResourceManager.addComponent(commandLayout2, cmdSave);

		splitPanel.setSplitPosition(40);
		layout1.setExpandRatio(headerLayout1, .4f);
		layout1.setExpandRatio(tbl_customers, 3.6f);
		layout2.setExpandRatio(headerLayout2, .4f);
		layout2.setExpandRatio(tbl_subscription, 3.6f);

		if (!listnerAdded) {
			tbl_customers.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						fetch_subs_data();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			});
			cmdRemove.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (tbl_subscription.getValue() == null) {
						return;
					}
					Integer rn = (Integer) tbl_subscription.getValue();
					Timestamp fdt = (Timestamp) data_subscription
							.getFieldValue(rn, "FROMDATE");
					Timestamp tdt = (Timestamp) data_subscription
							.getFieldValue(rn, "TODATe");
					try {
						PreparedStatement ps = con
								.prepareStatement(
										"select to_char(vou_date,'dd/mm/rrrr') vou_date,crd_cust_code from "
												+ " kk_trans where vou_date>=? and vou_date<=? and ctg_code=? order by vou_date",
										ResultSet.TYPE_SCROLL_INSENSITIVE,
										ResultSet.CONCUR_READ_ONLY);
						ps.setTimestamp(1, fdt);
						ps.setTimestamp(2, tdt);
						ps.setObject(3, data_subscription.getFieldValue(rn,
								"CTG_CODE"));
						ResultSet rst = ps.executeQuery();
						if (rst.first()) {
							ps.close();
							parentLayout.getWindow().showNotification(
									"Can not delete , Found #",
									rst.getString("VOU_DATE")
											+ " for customer : "
											+ rst.getString("CRD_CUST_CODE"));
							ps.close();
							return;
						}
						ps.close();
						data_subscription.deleteRow(rn);
						fill_subs_table();

					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			});

			cmdAdd.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						if (tbl_customers.getValue() != null) {
							add_subscription();
						}
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			});
			cmdRevert.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						fetch_subs_data();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			});
			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			data_subscription.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (cursorNo >= data_subscription.getRows().size()) {
						return;
					}
					if (data_subscription.getFieldValue(cursorNo, "FROMDATE") != null
							&& data_subscription.getFieldValue(cursorNo,
									"FROMDATE") != null) {
						Timestamp todt = (Timestamp) data_subscription
								.getFieldValue(cursorNo, "TODATE");
						Timestamp fromdt = (Timestamp) data_subscription
								.getFieldValue(cursorNo, "FROMDATE");
						int days = utils.daysBetween(fromdt, todt);
						data_subscription.setFieldValue(cursorNo, "TOTAL_DAYS",
								BigDecimal.valueOf(days));

						if (days > 0) {
							double price = ((Number) data_subscription
									.getFieldValue(cursorNo, "PRICE"))
									.doubleValue();
							data_subscription.setFieldValue(cursorNo,
									"TOTAL_AMOUNT", BigDecimal
											.valueOf((days * price)));
						}
						utilsVaadin.refreshRowFromData(data_subscription,
								tbl_subscription, cursorNo, lstSubCols);

					}
				}
			});
			data_subscription.setDefaultValuelistner(new DefaultValueListner() {

				public void setValue(dataCell dc, qryColumn qc) {
					java.text.SimpleDateFormat sd = new java.text.SimpleDateFormat(
							utils.FORMAT_SHORT_DATE);

					if (qc.getColname().equals("FROMDATE")
							|| qc.getColname().equals("TODATE")) {
						Timestamp dt = new Timestamp(System.currentTimeMillis());
						dc.setValue(sd.format(dt), dt);

					}
					if (qc.getColname().equals("TODATE")) {
						Timestamp todt = new Timestamp(System
								.currentTimeMillis());
						Calendar cl = Calendar.getInstance();
						cl.setTimeInMillis(todt.getTime());
						cl.add(Calendar.MONTH, 1);
						todt.setTime(cl.getTimeInMillis());
						dc.setValue(sd.format(todt), todt);

					}
					if (qc.getColname().equals("PRICE")) {
						dc.setValue("0.000", BigDecimal.valueOf(0l));
					}

					if (qc.getColname().equals("FLAG")) {
						dc.setValue("1", BigDecimal.valueOf(1l));
					}

				}
			});
			listnerAdded = true;
		}

	}

	public void add_subscription() throws SQLException {
		int rn = data_subscription.addRecord();
		Calendar cl = Calendar.getInstance();
		Timestamp dt = new Timestamp(System.currentTimeMillis());
		if (rn - 1 >= 0) {
			dt.setTime(((Timestamp) data_subscription.getFieldValue(rn - 1,
					"TODATE")).getTime());
			cl.setTimeInMillis(dt.getTime());
			cl.add(Calendar.DAY_OF_MONTH, 1);
			dt.setTime(cl.getTimeInMillis());
			((Timestamp) data_subscription.getFieldValue(rn, "FROMDATE"))
					.setTime(dt.getTime());
			cl.add(Calendar.MONTH, 1);
			dt.setTime(cl.getTimeInMillis());
			((Timestamp) data_subscription.getFieldValue(rn, "TODATE"))
					.setTime(dt.getTime());
		}

		fill_subs_table();

		if (rn > 0) {
			validate_controls();

		}
	}

	public void validate_controls() {
		for (int j = 0; j < tbl_subscription.getItemIds().size() - 1; j++) {
			Integer idno = (Integer) tbl_subscription.getItemIds().toArray()[j];
			for (int i = 0; i < lstSubCols.size(); i++) {
				Object obj = tbl_subscription.getItem(idno).getItemProperty(
						lstSubCols.get(i).descr).getValue();
				if (obj instanceof TextField) {
					((TextField) obj).setReadOnly(true);
				}
				if (obj instanceof DateField) {
					((DateField) obj).setReadOnly(true);
				}
				if (obj instanceof ComboBox) {
					((ComboBox) obj).setReadOnly(true);
				}

			}
		}

	}

	public void init() {

		cmdFind.setIcon(new ThemeResource("img/find.png"));
		cmdFind.setStyleName("toolbar");
		cmdAdd.setIcon(new ThemeResource("img/add.png"));
		cmdAdd.setStyleName("toolbar");
		cmdSave.setIcon(new ThemeResource("img/save.png"));
		cmdSave.setStyleName("toolbar");
		cmdRevert.setIcon(new ThemeResource("img/revert.png"));
		cmdRevert.setStyleName("toolbar");
		cmdRemove.setIcon(new ThemeResource("img/remove.png"));
		cmdRemove.setStyleName("toolbar");
		headerLayout1.setStyleName("toolpanel");
		headerLayout2.setStyleName("toolpanel");

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		if (data_customers.getDbclass() == null) {
			try {
				data_customers.createDBClassFromConnection(con);
				data_subscription.createDBClassFromConnection(con);
			} catch (SQLException e) {
			}
		}

		load_data();

	}

	public void load_data() {
		data_customers.clearALl();
		data_subscription.clearALl();
		tbl_customers.removeAllItems();
		tbl_subscription.removeAllItems();

		try {
			fetch_customer_data();
			fetch_subs_data();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void fill_customer_table() throws SQLException {
		tbl_customers.removeAllItems();
		utilsVaadin.applyColumns("CATERING.SUBSCRIPTION_CUSTOMERS",
				tbl_customers, lstCustomerCols, con);
		utilsVaadin.query_data2(tbl_customers, data_customers, lstCustomerCols);
	}

	public void fetch_customer_data() throws SQLException {
		data_customers
				.executeQuery("select * from c_ycust order by code", true);
		fill_customer_table();
	}

	public void fetch_subs_data() throws SQLException {
		if (tbl_customers.getValue() == null) {
			return;
		}
		int i = Integer.valueOf(tbl_customers.getValue().toString());
		String custcode = data_customers.getFieldValue(i, "CODE").toString();
		data_subscription
				.executeQuery(
						"select c_ycust_sub.*,TODATE-FROMDATE TOTAL_DAYS,PRICE*(TODATE-FROMDATE) TOTAL_AMOUNT,"
								+ " (SELECT NVL(SUM(TOT_AMT),0) FROM KK_TRANS WHERE TRANS_CODE=11 AND VOU_DATE>=FROMDATE AND VOU_DATE<=TODATE AND CRD_CUST_CODE=CUST_CODE and kk_trans.ctg_code=c_ycust_sub.ctg_code ) DELIVERED_AMOUNT,"
								+ " (SELECT NVL(COUNT(*),0) FROM KK_TRANS WHERE TRANS_CODE=11 AND VOU_DATE>=FROMDATE AND VOU_DATE<=TODATE AND CRD_CUST_CODE=CUST_CODE and kk_trans.ctg_code=c_ycust_sub.ctg_code  ) DELIVERED_DAYS from "
								+ " c_ycust_sub where cust_code='"
								+ custcode
								+ "' order by FROMDATE", true);
		fill_subs_table();
	}

	public void fill_subs_table() throws SQLException {
		tbl_subscription.removeAllItems();
		utilsVaadin.applyColumns("CATERING.SUBSCRIPTION_CUSTOMERS2",
				tbl_subscription, lstSubCols, con);

		utilsVaadin.findColByCol("CTG_CODE", lstSubCols).lov_sql = new ArrayList<dataCell>();
		utils.FillLov(utilsVaadin.findColByCol("CTG_CODE", lstSubCols).lov_sql,
				"select ctg_code,ctg_name from kk_ctg order by ctg_code", con);
		utilsVaadin.findColByCol("CTG_CODE", lstSubCols).action = new ColumnAction() {

			public Object onValueChange(int rowno, String colname, Object vl) {
				if (utilsVaadin.system_status.equals("QUERY")) {
					return null;
				}

				try {

					if (data_subscription.getRows().get(rowno).getRowStatus()
							.equals(Row.ROW_INSERTED)) {
						String s = utils.getSqlValue(
								"select ctg_price from kk_ctg where ctg_code='"
										+ vl + "'", con);
						data_subscription.setFieldValue(rowno, "PRICE",
								BigDecimal.valueOf(Double.valueOf(s)));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return vl;
			}
		};
		utilsVaadin.findColByCol("FLAG", lstSubCols).onCheckValue = "2";
		utilsVaadin.findColByCol("FLAG", lstSubCols).onUnCheckValue = "1";
		utilsVaadin
				.query_data2(tbl_subscription, data_subscription, lstSubCols);
		validate_controls();
	}

	public void print_data() {

	}

	public void save_data() {
		String sql1 = "";
		String delsql = "";
		if (tbl_customers.getValue() == null) {
			return;
		}
		int rc = (Integer) tbl_customers.getValue();
		String cust_code = data_customers.getFieldValue(rc, "CODE").toString();

		sql1 = "insert into c_ycust_sub(CUST_CODE, CTG_CODE, FROMDATE, TODATE, FLAG, PRICE) values ("
				+ ":CUST_CODE, :CTG_CODE, :FROMDATE, :TODATE, :FLAG, :PRICE)";

		try {
			QueryExe qe = new QueryExe(con);
			qe.setSqlStr(sql1);
			qe.parse();
			con.setAutoCommit(false);
			utils.execSql("delete from c_ycust_sub where cust_code='"
					+ cust_code + "'", con);
			for (int i = 0; i < data_subscription.getRows().size(); i++) {

				qe.setParaValue("CUST_CODE", cust_code);
				qe.setParaValue("CTG_CODE", data_subscription.getFieldValue(i,
						"CTG_CODE"));
				qe.setParaValue("FROMDATE", data_subscription.getFieldValue(i,
						"FROMDATE"));
				qe.setParaValue("TODATE", data_subscription.getFieldValue(i,
						"TODATE"));
				qe.setParaValue("FLAG", data_subscription.getFieldValue(i,
						"FLAG"));
				qe.setParaValue("PRICE", data_subscription.getFieldValue(i,
						"PRICE"));
				qe.execute();
			}
			qe.close();
			con.commit();
			parentLayout.getWindow().showNotification("Saved Successfully");
			fetch_subs_data();
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

	public void showInitView() {
		initForm();
	}

	public boolean isAllRowValid(boolean showControl) {
		for (int i = 0; i < data_subscription.getRows().size(); i++) {
			if (!isRowValid(i, showControl)) {
				return false;
			}
		}
		return true;
	}

	public boolean isRowValid(int rowno, boolean showControl) {
		if (data_subscription.getFieldValue(rowno, "CTG_CODE") == null) {
			if (showControl) {
				parentLayout.getWindow().showNotification("Specify category",
						Notification.TYPE_ERROR_MESSAGE);
				tbl_subscription.select(Integer.valueOf(rowno));
			}
			return false;
		}

		return true;
	}
}
