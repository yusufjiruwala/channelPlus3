package com.windows.catering;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.doc.views.TableView;
import com.doc.views.YesNoDialog;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog.Callback;
import com.generic.ColumnProperty;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.qryColumn;
import com.generic.rowTriggerListner;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.utilsVaadinPrintHandler;
import com.generic.ColumnProperty.ColumnAction;
import com.generic.localTableModel.DefaultValueListner;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmCateringRegistration implements com.generic.transactionalForm {
	private Connection con = null;
	private localTableModel data_regs = new localTableModel();

	private Window wndList = new Window();
	VerticalLayout lstLayout = new VerticalLayout();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private GridLayout basicLayout = new GridLayout(4, 1);

	private HorizontalLayout shortLayout = new HorizontalLayout();

	private NativeButton cmdSave = new NativeButton("Save & Post");
	private NativeButton cmdRevert = new NativeButton("Revert");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdDel = new NativeButton("Delete");
	private NativeButton cmdCls = new NativeButton("Clear");
	private NativeButton cmdAddReg = new NativeButton("Add Registration");
	private NativeButton cmdPrint = new NativeButton("Print");
	private NativeButton cmdSaveSummary = new NativeButton(
			"Save & Show Summary");

	private TextField txtVouNo = new TextField("Voucher No");
	private TextField txtDescr = new TextField("Description");
	private DateField txtVouDate = new DateField("Date");
	private ComboBox txtCostCent = new ComboBox("Cost Center");

	private TextField txtShortCustCode = new TextField("Cust Code");
	private ComboBox txtShortPayType = new ComboBox("Pay type");
	private TextField txtShortAmt = new TextField("Amount");
	private NativeButton cmdShortAdd = new NativeButton("Add");

	private Table tbl_regs = new Table();

	private double varKeyfld = -1;
	private String QRYSES = "";
	private String last_saved_QRYSES = "";
	private boolean listenerAdded = false;
	private List<ColumnProperty> lstRegCols = new ArrayList<ColumnProperty>();
	private String cc = "";

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public frmCateringRegistration() {

	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		tbl_regs.removeAllItems();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		commandLayout.removeAllComponents();

		data_regs.clearALl();

	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();

		centralPanel.setSizeFull();
		tbl_regs.setSizeFull();
		mainLayout.setSizeFull();
		basicLayout.setSizeFull();
		txtDescr.setWidth("100%");
		txtVouDate.setWidth("100%");
		txtVouNo.setWidth("100%");

		txtShortCustCode.setImmediate(true);
		txtShortPayType.setImmediate(true);
		txtShortPayType.setImmediate(true);

		txtVouDate.setResolution(DateField.RESOLUTION_DAY);
		txtVouDate.setDateFormat(utils.FORMAT_SHORT_DATE);

		tbl_regs.setSelectable(true);
		tbl_regs.setSortDisabled(true);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);
		ResourceManager.addComponent(mainLayout, tbl_regs);
		ResourceManager.addComponent(mainLayout, shortLayout);

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdSaveSummary);
		ResourceManager.addComponent(commandLayout, cmdPrint);
		ResourceManager.addComponent(commandLayout, cmdList);
		ResourceManager.addComponent(commandLayout, cmdDel);
		ResourceManager.addComponent(commandLayout, cmdAddReg);
		ResourceManager.addComponent(commandLayout, cmdRevert);
		ResourceManager.addComponent(commandLayout, cmdCls);

		ResourceManager.addComponent(basicLayout, txtVouNo);
		ResourceManager.addComponent(basicLayout, txtDescr);
		ResourceManager.addComponent(basicLayout, txtVouDate);
		ResourceManager.addComponent(basicLayout, txtCostCent);

		ResourceManager.addComponent(shortLayout, txtShortCustCode);
		ResourceManager.addComponent(shortLayout, txtShortPayType);
		ResourceManager.addComponent(shortLayout, txtShortAmt);
		ResourceManager.addComponent(shortLayout, cmdShortAdd);

		mainLayout.setExpandRatio(commandLayout, .1f);
		mainLayout.setExpandRatio(basicLayout, .3f);
		mainLayout.setExpandRatio(tbl_regs, 3.2f);
		mainLayout.setExpandRatio(shortLayout, .2f);

		shortLayout.setComponentAlignment(cmdShortAdd, Alignment.BOTTOM_CENTER);

		utilsVaadin.FillComboFromList(txtShortPayType, utilsVaadin
				.findColByCol("PAY_TYPE", lstRegCols).lov_sql);

		try {
			utilsVaadin.FillCombo(txtCostCent,
					"select code,title from accostcent1 order by path", con);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (!listenerAdded) {
			cmdPrint.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data(true);
				}
			});
			cmdDel.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					delete_data();

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
			cmdAddReg.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					add_customer();
				}
			});
			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			cmdSaveSummary.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						save_data(false);
						Window wnd = new Window();
						VerticalLayout la = new VerticalLayout();
						wnd.setContent(la);
						utilsVaadin
								.showSearch(
										la,
										new SelectionListener() {

											public void onSelection(TableView tv) {
											}
										},
										con,
										"select debit,credit,accno,descr2 from acvoucher2 where vou_code=1 and REFERCODE=10001 AND referkeyfld='"
												+ last_saved_QRYSES
												+ "' AND CUST_CODE IS NULL ORDER BY POS",
										true);
					} catch (SQLException ex) {
						ex.printStackTrace();

					}
				}

			});
			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					showList();

				}
			});
			data_regs.setDefaultValuelistner(new DefaultValueListner() {

				public void setValue(dataCell dc, qryColumn qc) {
					DecimalFormat df = new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
					SimpleDateFormat sdf = new SimpleDateFormat(
							utils.FORMAT_SHORT_DATE);
					if (qc.getColname().equals("TOT_AMT")
							|| qc.getColname().equals("DISC_AMT")) {
						dc.setValue(df.format(0), BigDecimal.valueOf(0));
					}
					if (qc.getColname().equals("BOOK_DATE")) {
						Timestamp tm = new Timestamp(
								((java.util.Date) txtVouDate.getValue())
										.getTime());
						dc.setValue(sdf.format(tm), tm);
					}
					if (qc.getColname().equals("CONTRACT_DAYS")) {
						dc.setValue("28", BigDecimal.valueOf(28));
					}
				}
			});
			data_regs.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (!utilsVaadin.system_status.equals("QUERY")) {
						utilsVaadin.refreshRowFromData(data_regs, tbl_regs,
								cursorNo, lstRegCols);
					}
				}
			});
			tbl_regs.addActionHandler(new Handler() {

				public void handleAction(Action action, Object sender,
						Object target) {
					if (action.getCaption().equals("Delete Record...")) {
						int rowno = Integer.valueOf(target.toString());
						data_regs.deleteRow(rowno);
						tbl_regs.removeItem(rowno);
						tbl_regs.requestRepaintAll();
					}

				}

				public Action[] getActions(Object target, Object sender) {

					Action[] acts = { new Action("Delete Record...") };
					return acts;
				}
			});
			cmdShortAdd.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						ResultSet rst = utils.getSqlRS(
								"select code,code||' - '||name name from c_ycust where code='"
										+ txtShortCustCode.getValue()
										+ "' and childcount=0", con);
						if (rst == null || !rst.first()) {
							throw new Exception("Customer  not valid ");

						}
						BigDecimal bd = BigDecimal.valueOf(Double
								.valueOf(txtShortAmt.getValue().toString()));

						int rn = data_regs.addRecord();

						Object id = utilsVaadin.add_record_table(rn, tbl_regs,
								data_regs, lstRegCols);

						data_regs.setFieldValue(rn, "CRD_CUST_CODE", rst
								.getString("CODE"));
						data_regs.setFieldValue(rn, "CUST_NAME", rst
								.getString("NAME"));

						if (txtShortPayType.getValue() != null) {
							Item itm = tbl_regs.getItem(id);
							ComboBox c = (ComboBox) itm.getItemProperty(
									utilsVaadin.findColByCol("PAY_TYPE",
											lstRegCols).descr).getValue();

							c.setValue(txtShortPayType.getValue());
							data_regs.setFieldValue(rn, "PAY_TYPE",
									((dataCell) txtShortPayType.getValue())
											.getValue());
						}
						data_regs.setFieldValue(rn, "TOT_AMT", bd);

						utilsVaadin.refreshRowFromData(data_regs, tbl_regs, rn,
								lstRegCols);
						rst.getStatement().close();
						txtShortAmt.setValue("0.000");
						txtShortCustCode.setValue("");
					} catch (Exception ex) {
						ex.printStackTrace();
						parentLayout.getWindow().showNotification("",
								ex.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);

					}
				}
			});
			listenerAdded = true;
		}

	}

	public void add_customer() {
		wndList.setContent(lstLayout);
		TableView.SelectionListener sl = new SelectionListener() {
			public void onSelection(TableView tv) {
				Channelplus3Application.getInstance().getMainWindow()
						.removeWindow(wndList);
				try {
					if (tv.getSelectionValue() >= 0) {
						int rn = data_regs.addRecord();
						data_regs.setFieldValue("CRD_CUST_CODE", tv.getData()
								.getFieldValue(tv.getSelectionValue(), "CODE"));
						data_regs.setFieldValue("CUST_NAME", tv.getData()
								.getFieldValue(tv.getSelectionValue(), "NAME"));
						utilsVaadin.refreshRowFromData(data_regs, tbl_regs, rn,
								lstRegCols);
						fill_reg_table();
					}

				} catch (Exception e) {
					Logger.getLogger(frmCateringRegistration.class.getName())
							.log(Level.SEVERE, null, e);
					parentLayout.getWindow().showNotification(
							"Error filling table",
							Notification.TYPE_ERROR_MESSAGE);

				}

			}
		};
		try {
			final TableView tblview = utilsVaadin.showSearch(lstLayout, sl,
					con, "select code,name from c_ycust order by code", true);

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

			cc = utils
					.getSqlValue(
							"select repair.getsetupvalue_2('CATERING_REGISTRATION_COSTCENTER') FROM DUAL ",
							con);

			utilsVaadin.applyColumns("CATERING.CUST_REG", tbl_regs, lstRegCols,
					con);
			utilsVaadin.findColByCol("PAY_TYPE", lstRegCols).lov_sql = new ArrayList<dataCell>();
			utilsVaadin.findColByCol("PAY_TYPE", lstRegCols).lov_sql
					.add(new dataCell("Bank", BigDecimal.valueOf(1)));
			utilsVaadin.findColByCol("PAY_TYPE", lstRegCols).lov_sql
					.add(new dataCell("Cash", BigDecimal.valueOf(2)));
			utilsVaadin.findColByCol("PAY_TYPE", lstRegCols).lov_sql
					.add(new dataCell("Knet", BigDecimal.valueOf(3)));
			utilsVaadin.findColByCol("PAY_TYPE", lstRegCols).lov_sql
					.add(new dataCell("Master", BigDecimal.valueOf(4)));
			utilsVaadin.findColByCol("PAY_TYPE", lstRegCols).lov_sql
					.add(new dataCell("Visa", BigDecimal.valueOf(5)));
			utilsVaadin.findColByCol("PAY_TYPE", lstRegCols).lov_sql
					.add(new dataCell("Other", BigDecimal.valueOf(6)));
			
			utilsVaadin.findColByCol("PAY_TYPE", lstRegCols).action = new ColumnAction() {
				public Object onValueChange(int rowno, String colname, Object vl) {

					if (vl == null) {
						return null;
					}

					if (utilsVaadin.system_status.equals("QUERY")) {
						return null;
					}
					ResultSet rst = utils.getSqlRS(
							"select code,name from kk_banks where pay_type='"
									+ vl + "'", con);
					if (rst != null) {
						try {
							data_regs.setFieldValue(rowno, "DEBIT_NAME", rst
									.getString("NAME"));
							data_regs.setFieldValue(rowno, "DEB_CODE", rst
									.getString("CODE"));
							rst.close();
						} catch (SQLException ex) {
							ex.printStackTrace();
						}
					}

					return vl;
				}
			};

			utilsVaadin.findColByCol("TOT_AMT", lstRegCols).action = new ColumnAction() {

				public Object onValueChange(int rowno, String colname, Object vl) {

					if (data_regs.getFieldValue(rowno, "TOT_AMT") == null
							|| data_regs.getFieldValue(rowno, "DISC_AMT") == null) {
						return null;
					}
					double d = ((BigDecimal) data_regs.getFieldValue(rowno,
							"TOT_AMT")).doubleValue();
					d = d
							- ((BigDecimal) data_regs.getFieldValue(rowno,
									"DISC_AMT")).doubleValue();
					data_regs.setFieldValue(rowno, "NET_AMT", BigDecimal
							.valueOf(d));
					if (!utilsVaadin.system_status.equals("QUERY")) {
						validateData(rowno, rowno + 1, false);
					}

					return vl;
				}
			};

			utilsVaadin.findColByCol("DISC_AMT", lstRegCols).action = utilsVaadin
					.findColByCol("TOT_AMT", lstRegCols).action;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		if (data_regs.getDbclass() == null) {
			try {
				data_regs.createDBClassFromConnection(con);
			} catch (SQLException e) {
			}
		}

		load_data();

	}

	public boolean validateData(int fromrowno, int torowno, boolean showMessage) {
		PreparedStatement ps_deb_ac = null;
		PreparedStatement ps_cust_ac = null;

		try {
			ps_deb_ac = con.prepareStatement(
					"select *from kk_banks where pay_type=?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ps_cust_ac = con.prepareStatement(
					"select *from c_ycust where code=?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			for (int i = fromrowno; i < torowno; i++) {
				ps_deb_ac.setBigDecimal(1, (BigDecimal) data_regs
						.getFieldValue(i, "PAY_TYPE"));
				ResultSet rs1 = ps_deb_ac.executeQuery();
				if (rs1.first()) {
					data_regs.setFieldValue(i, "DEB_PAY_AC", rs1
							.getString("DEBIT_AC"));
					data_regs.setFieldValue(i, "DEB_PAY_EXP_AC", rs1
							.getString("COMMISION_AC"));
					data_regs.setFieldValue(i, "DEB_PAY_COMMISION_RATE", rs1
							.getBigDecimal("COMMISION_RATE"));

				} else {
					throw new SQLException("Not found debit code for cust # "
							+ data_regs.getFieldValue(i, "CRD_CUST_CODE"));
				}
				ps_cust_ac.setString(1, (String) data_regs.getFieldValue(i,
						"CRD_CUST_CODE"));
				ResultSet rs2 = ps_cust_ac.executeQuery();
				if (rs2.first()) {
					data_regs.setFieldValue(i, "CRD_CUST_ACC", rs2
							.getString("AC_NO"));
				} else {
					throw new SQLException("Customer not found cust # "
							+ data_regs.getFieldValue(i, "CRD_CUST_CODE"));
				}
				if (((BigDecimal) data_regs.getFieldValue(i, "TOT_AMT"))
						.doubleValue() <= 0
						|| ((BigDecimal) data_regs.getFieldValue(i, "NET_AMT"))
								.doubleValue() < 0) {
					throw new SQLException("Amount invalid for cust # "
							+ data_regs.getFieldValue(i, "CRD_CUST_CODE"));
				}
			}
			ps_deb_ac.close();
			ps_cust_ac.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			if (showMessage) {
				parentLayout.getWindow().showNotification(ex.getMessage(),
						Notification.TYPE_ERROR_MESSAGE);
			}
			try {
				ps_deb_ac.close();
				ps_cust_ac.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public void load_data() {

		varKeyfld = -1;
		txtShortAmt.setValue("0.000");
		txtShortCustCode.setValue("");
		txtShortPayType.setValue(null);

		txtVouNo.setValue("");
		txtDescr.setValue("");
		if (!cc.isEmpty()) {
			txtCostCent.setValue(utilsVaadin.findByValue(txtCostCent, cc));
		}
		txtDescr
				.setValue(utils
						.getSqlValue(
								"select nvl(max(value),'Membership Registration') from setup where variable='KK_TEXT_REG'",
								con));
		tbl_regs.removeAllItems();

		data_regs.clearALl();

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
				txtCostCent.setValue(utilsVaadin.findByValue(txtCostCent, rst
						.getString("COSTCENT")));

			} else {
				varKeyfld = Double.valueOf(utils.getSqlValue(
						"select nvl(max(keyfld),0)+1 from KK_TRANS_1", con));
				txtVouNo.setValue(String.valueOf(varKeyfld));

			}
			ps.close();
			parentLayout.requestRepaintAll();
			fetch_reg_data();
		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void fetch_reg_data() throws SQLException {
		data_regs
				.executeQuery(
						"select rownum,kk_trans.*,kk_ctg.ctg_name,c_ycust.code||' - '||c_ycust.name cust_name,"
								+ "kk_banks.name debit_name,kk_trans.tot_amt-disc_amt net_amt "
								+ "from kk_trans,kk_ctg,c_ycust,kk_banks "
								+ "where kk_ctg.ctg_code(+)=kk_trans.ctg_code "
								+ "and c_ycust.code=crd_cust_code and kk_banks.code=kk_trans.deb_code and kk_trans.keyfld='"
								+ QRYSES + "'", true);
		fill_reg_table();
	}

	public void fill_reg_table() throws SQLException {
		tbl_regs.removeAllItems();
		utilsVaadin.query_data2(tbl_regs, data_regs, lstRegCols);

	}

	public void print_data() {
		try {
			print_voucher();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void print_voucher() throws Exception {
		List<String> grpsum = new ArrayList<String>();
		grpsum.add("TOT_AMT");
		grpsum.add("NET_AMT");
		grpsum.add("DISC_AMT");

		List<Parameter> lstparams = new ArrayList<Parameter>();

		Parameter pm = new Parameter("VOU_NO", txtVouNo.getValue());
		pm.setDescription("Voucher No # ");
		lstparams.add(pm);

		pm = new Parameter("VOU_DESCR", txtDescr.getValue());
		pm.setDescription("Descr # ");
		lstparams.add(pm);

		pm = new Parameter("VOU_DATE", txtVouDate.getValue());

		pm.setDescription("Voucher Date # ");
		pm.setValueType(Parameter.DATA_TYPE_DATETIME);
		lstparams.add(pm);

		List<ColumnProperty> lstc = new ArrayList<ColumnProperty>();
		lstc.addAll(lstRegCols);
		lstc.remove(utilsVaadin.findColByCol("COSTCENT", lstRegCols));
		lstc.remove(utilsVaadin.findColByCol("CRD_CUST_ACC", lstRegCols));
		lstc.remove(utilsVaadin.findColByCol("DEB_PAY_EXP_AC", lstRegCols));
		lstc.remove(utilsVaadin.findColByCol("DEB_PAY_AC", lstRegCols));
		lstc.remove(utilsVaadin.findColByCol("DEB_PAY_COMMISION_RATE",
				lstRegCols));

		utilsVaadinPrintHandler.print_voucher(data_regs, lstc, lstparams,
				grpsum, new ArrayList<String>(), con, "tmp");
	}

	public void save_data() {
		save_data(false);
	}

	public void save_data(boolean print) {

		if (!validateData(0, data_regs.getRows().size(), true)) {
			return;
		}
		String sq1 = "INSERT INTO KK_TRANS_1(KEYFLD, DESCR, TRANS_DATE, JV_KEYFLD, TRANS_CODE, COSTCENT ) VALUES "
				+ "(:KEYFLD, :DESCR, :TRANS_DATE, NULL, 1, :COSTCENT )";
		String sq2 = "INSERT INTO KK_TRANS (KEYFLD, NO, TRANS_CODE, PAY_TYPE, "
				+ "VOU_DATE, BOOK_DATE, BOOK_NO, "
				+ "CRD_CUST_CODE, CRD_CUST_ACC, DEB_CODE, "
				+ "DEB_PAY_AC, DEB_PAY_EXP_AC, DEB_PAY_COMMISION_RATE, "
				+ "TOT_AMT, DISC_AMT, CONTRACT_DAYS, DISH_GIVEN, JV_KEYFLD, CTG_CODE ,COSTCENT ) VALUES ( "
				+ ":KEYFLD, :NO, :TRANS_CODE, :PAY_TYPE, "
				+ ":VOU_DATE, :BOOK_DATE, NULL, "
				+ ":CRD_CUST_CODE, :CRD_CUST_ACC, :DEB_CODE, "
				+ ":DEB_PAY_AC, :DEB_PAY_EXP_AC, :DEB_PAY_COMMISION_RATE, "
				+ ":TOT_AMT, :DISC_AMT, :CONTRACT_DAYS, 0 , NULL, :CTG_CODE , :COSTCENT  )";
		String sq_del = "declare a number;"
				+ " begin  "
				+ " delete from kk_trans_1 where trans_code=1 and keyfld=:KEYFLD;  "
				+ " delete from kk_trans where trans_code=1 and keyfld=:KEYFLD;"
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

			if (txtCostCent.getValue() != null) {
				qe1.setParaValue("COSTCENT",
						((dataCell) txtCostCent.getValue()).getValue());
			} else {
				qe1.setParaValue("COSTCENT", "");
			}

			qe1.execute();
			QueryExe qe2 = new QueryExe(sq2, con);
			qe2.parse();
			for (int i = 0; i < data_regs.getRows().size(); i++) {
				qe2.setParaValue("KEYFLD", varKeyfld);
				qe2.setParaValue("NO", (i + 1));
				qe2.setParaValue("TRANS_CODE", 1);
				qe2.setParaValue("PAY_TYPE", data_regs.getFieldValue(i,
						"PAY_TYPE"));
				qe2.setParaValue("VOU_DATE", txtVouDate.getValue());
				qe2.setParaValue("BOOK_DATE", data_regs.getFieldValue(i,
						"BOOK_DATE"));
				qe2.setParaValue("CRD_CUST_CODE", data_regs.getFieldValue(i,
						"CRD_CUST_CODE"));
				qe2.setParaValue("CRD_CUST_ACC", data_regs.getFieldValue(i,
						"CRD_CUST_ACC"));
				qe2.setParaValue("DEB_CODE", data_regs.getFieldValue(i,
						"DEB_CODE"));
				qe2.setParaValue("DEB_PAY_AC", data_regs.getFieldValue(i,
						"DEB_PAY_AC"));
				qe2.setParaValue("DEB_PAY_EXP_AC", data_regs.getFieldValue(i,
						"DEB_PAY_EXP_AC"));
				qe2.setParaValue("DEB_PAY_COMMISION_RATE", data_regs
						.getFieldValue(i, "DEB_PAY_COMMISION_RATE"));

				qe2.setParaValue("TOT_AMT", data_regs.getFieldValue(i,
						"TOT_AMT"));
				qe2.setParaValue("DISC_AMT", data_regs.getFieldValue(i,
						"DISC_AMT"));
				qe2.setParaValue("CONTRACT_DAYS", data_regs.getFieldValue(i,
						"CONTRACT_DAYS"));
				qe2.setParaValue("CTG_CODE", data_regs.getFieldValue(i,
						"CTG_CODE"));
				if (txtCostCent.getValue() != null) {
					qe2.setParaValue("COSTCENT", ((dataCell) txtCostCent
							.getValue()).getValue());
				} else {
					qe2.setParaValue("COSTCENT", "");
				}

				qe2.execute();
			}
			qe2.close();
			qe1.close();

			utils.execSql("begin X_POST_JV_CATERING_REGS(" + varKeyfld
					+ "); end;", con);

			con.commit();
			last_saved_QRYSES = QRYSES;
			parentLayout.getWindow().showNotification("Saved Successfully");

			if (print) {
				print_data();
			} else {
				QRYSES = "";
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

	public void delete_data() {
		Callback cb = new Callback() {
			public void onDialogResult(boolean resultIsYes) {

				if (!resultIsYes) {
					return;
				}

				String sq_del = "declare a number; cursor v2 is select accno,count(*) cnt "
						+ " from acvoucher2 where referkeyfld=:KEYFLD and refercode=10001 group by accno;"
						+ " begin   for x in v2 loop "
						+ " repair.decaccuses(x.accno,x.cnt); end loop;"
						+ " delete from acvoucher1 where refercode=10001 and referkeyfld=:KEYFLD; "
						+ " delete from acvoucher2 where refercode=10001 and referkeyfld=:KEYFLD; "
						+ " delete from kk_trans_1 where trans_code=1 and keyfld=:KEYFLD;  "
						+ " delete from kk_trans where trans_code=1 and keyfld=:KEYFLD;"
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
					mainLayout.getWindow().showNotification(
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
								"Do you want to remove Registration of date # "
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
					e.printStackTrace();
					parentLayout.getWindow().showNotification(
							"Error loading data from selection",
							Notification.TYPE_ERROR_MESSAGE);
				}

			}
		};

		try {
			String sq = "select descr,trans_date, keyfld,keyfld vou_no  from kk_trans_1 where trans_code=1 order by keyfld desc";
			final TableView tblview = utilsVaadin.showSearch(lstLayout, sl,
					con, sq, true, new String[] { "KEYFLD" });
		} catch (Exception e) {
			Logger.getLogger(frmCateringRegistration.class.getName()).log(
					Level.SEVERE, null, e);
			parentLayout.getWindow().showNotification("Error filling table",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}
}
