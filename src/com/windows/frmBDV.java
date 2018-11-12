package com.windows;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog;
import com.doc.views.YesNoDialog.Callback;
import com.doc.views.Query.QueryView;
import com.doc.views.Query.tableDataListner;
import com.generic.ColumnProperty;
import com.generic.Parameter;
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
import com.generic.utilsVaadinPrintHandler;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.windows.Masters.Purchase.frmPurchaseCost;

public class frmBDV implements transactionalForm {

	private Window wndList = new Window();
	private VerticalLayout listlayout = new VerticalLayout();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private HorizontalLayout commandLayout2 = new HorizontalLayout();
	private GridLayout basicLayout = new GridLayout(4, 2);
	private String titleStr = "Bank Deposite voucher from Cash Recipt";

	private Label txtTitle = new Label(titleStr);

	private TextField txtJvno = new TextField("JV No");
	private DateField txtDate = new DateField("Date");
	private TextField txtDescr = new TextField("Descr");
	private ComboBox txtBankAcc = new ComboBox("Bank Account");

	private double varJvno = 0;
	private int varJvKeyfld = 0;
	private double varJvType = 992;
	private String varPeriodCode = "";

	private Table tbl_recipts = new Table("Recipt Vouchers");

	private Button cmdSave = new Button("Save & Back");
	private Button cmdPrint = new Button("Print");
	private Button cmdCancel = new Button("Revert Changes");
	private Button cmdList = new Button("List");
	private Button cmdDelete = new Button("Delete");
	private Button cmd2DelRecord = new Button("Del Row");
	private Button cmd2selAll = new Button("Sel All");
	private Button cmd2AddRecord = new Button("Add Recipt");

	private Map<String, String> mapActionStrs = new HashMap<String, String>();
	private final List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();
	private localTableModel data_recipts = new localTableModel();

	private String QRYSES = "";
	private String QRYSES_ACCNO = "";
	private boolean listnerAdded = false;
	private PreparedStatement ps_jv = null;

	public AbstractLayout getParentLayout() {
		return parentLayout;
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public String getQRYSES() {
		return QRYSES;
	}

	public void setQRYSES(String qRYSES) {
		QRYSES = qRYSES;
	}

	private Connection con = null;
	private java.util.Date fromdt = new java.util.Date(System
			.currentTimeMillis());
	private java.util.Date todt = new java.util.Date(System.currentTimeMillis());

	public void createView() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		resetFormLayout();

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdCancel);
		ResourceManager.addComponent(commandLayout, cmdPrint);
		ResourceManager.addComponent(commandLayout, cmdList);
		ResourceManager.addComponent(commandLayout, cmdDelete);
		ResourceManager.addComponent(commandLayout2, cmd2AddRecord);
		ResourceManager.addComponent(commandLayout2, cmd2DelRecord);
		ResourceManager.addComponent(commandLayout2, cmd2selAll);

		ResourceManager.addComponent(mainLayout, txtTitle);

		basicLayout.setWidth("100%");

		txtBankAcc.setWidth("100%");
		txtDate.setWidth("100%");
		txtDescr.setWidth("100%");
		txtJvno.setWidth("100%");
		txtDate.setResolution(DateField.RESOLUTION_DAY);
		txtDate.setDateFormat(utils.FORMAT_SHORT_DATE);

		ResourceManager.addComponent(basicLayout, txtJvno);
		ResourceManager.addComponent(basicLayout, txtDate);
		ResourceManager.addComponent(basicLayout, txtBankAcc);
		ResourceManager.addComponent(basicLayout, txtDescr);
		ResourceManager.addComponent(basicLayout, new Label());

		tbl_recipts.setHeight("100%");
		tbl_recipts.setWidth("100%");

		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);
		ResourceManager.addComponent(mainLayout, commandLayout2);
		ResourceManager.addComponent(mainLayout, tbl_recipts);
		ResourceManager.addComponent(centralPanel, mainLayout);
		centralPanel.requestRepaintAll();

		mainLayout.setWidth("100%");
		tbl_recipts.removeAllItems();
		tbl_recipts.setSelectable(true);
		tbl_recipts.setNullSelectionAllowed(false);
		tbl_recipts.setFooterVisible(true);

		try {
			String vl = utils
					.getSqlValue(
							"select path FROM cp_user_profiles,acaccount "
									+ " where accno=value and profileno=0 and variable='BANK_GROUP'",
							con);
			utilsVaadin.FillCombo(txtBankAcc,
					"select accno,name from acaccount where path like '" + vl
							+ "%' and childcount=0", con);
		} catch (SQLException e) {

		}

		if (!listnerAdded) {
			cmd2selAll.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					tbl_recipts.setValue(tbl_recipts.getItemIds());
				}
			});
			cmd2AddRecord.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					add_recipt();
				}
			});
			cmd2DelRecord.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					Set<Integer> l = (Set<Integer>) tbl_recipts.getValue();
					for (Iterator iterator = l.iterator(); iterator.hasNext();) {
						Integer id = (Integer) iterator.next();
						if (!data_recipts.deleteRow(id)) {
							return;
						}
						tbl_recipts.removeItem(id);
					}
					calcSums();
				}
			});
			cmdCancel.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					resetFormLayout();
					showOutstandingReciptVoucher();
				}
			});
			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					showList();
				}
			});
			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			cmdPrint.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					if (cmdSave.isEnabled()) {
						save_data(false, true);
					} else {
						print_data();
					}
				}
			});
			cmdDelete.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					Callback cb = new Callback() {
						public void onDialogResult(boolean resultIsYes) {

							if (!resultIsYes) {
								return;
							}
							try {
								if (QRYSES.length() > 0) {
									String sqDelete = "declare "
											+ " cursor ii is select *from acvoucher2 where keyfld=:BDV_KEYFLD order by pos ;"
											+ " begin  for x in ii loop "
											+ " repair.decaccuses(x.accno);"
											+ " update acvoucher2 set referkeyfld=null,referno=null where keyfld=x.referkeyfld and vou_code=2;"
											+ " update acvoucher1 set referkeyfld=null,referno=null where keyfld=x.referkeyfld and vou_code=2;"
											+ " end loop; "
											+ " delete from acvoucher1 where keyfld=:BDV_KEYFLD;"
											+ " delete from acvoucher2 where keyfld=:BDV_KEYFLD; end;";
									QueryExe qe = new QueryExe(sqDelete, con);
									qe.setParaValue("BDV_KEYFLD", QRYSES);
									qe.execute();
									con.commit();
									resetFormLayout();
									showOutstandingReciptVoucher();
								}
							} catch (SQLException ex) {
								Logger.getLogger(frmBDV.class.getName()).log(
										Level.SEVERE, null, ex);
								parentLayout.getWindow().showNotification(
										"Unable to save:", ex.toString(),
										Notification.TYPE_ERROR_MESSAGE);
								try {
									con.rollback();

								} catch (SQLException e) {
								}

							}

						}
					};
					mainLayout.getWindow().addWindow(
							new YesNoDialog("Channel Plus Warning",
									"Delete this BDV ?", cb));

				}
			});

			data_recipts.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (cursorNo < 0) {
						return;
					}
					if (((BigDecimal) data_recipts.getFieldValue(cursorNo,
							"DEBIT_AMOUNT")) == null) {
						return;
					}
					calcSums();
				}
			});

			listnerAdded = true;

		}
	}

	public void showList() {
		wndList.setContent(listlayout);
		TableView.SelectionListener sl = new SelectionListener() {
			public void onSelection(TableView tv) {
				Channelplus3Application.getInstance().getMainWindow()
						.removeWindow(wndList);
				try {
					QRYSES = tv.getData().getFieldValue(tv.getSelectionValue(),
							"KEYFLD").toString();
					initForm();
				} catch (Exception e) {
					Logger.getLogger(frmBDV.class.getName()).log(Level.SEVERE,
							null, e);
					parentLayout.getWindow().showNotification(
							"Error loading data from selection",
							Notification.TYPE_ERROR_MESSAGE);
				}

			}
		};
		try {
			String sq = "select to_char(VOU_DATE,'dd/mm/rrrr') vou_date,DESCR,NO,CODEACC ACCNO,KEYFLD FROM ACVOUCHER1 WHERE VOU_CODE=1 AND TYPE=992 ORDER BY VOU_DATE DESC,NO";
			final TableView tblview = utilsVaadin.showSearch(listlayout, sl,
					con, sq, true, new String[] { "KEYFLD" });
		} catch (Exception e) {
			Logger.getLogger(frmBDV.class.getName()).log(Level.SEVERE, null, e);
			parentLayout.getWindow().showNotification("Error filling table",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void add_recipt() {
		wndList.setContent(listlayout);
		TableView.SelectionListener sl = new SelectionListener() {
			public void onSelection(TableView tv) {
				Channelplus3Application.getInstance().getMainWindow()
						.removeWindow(wndList);
				try {
					BigDecimal kf = (BigDecimal) tv.getData().getFieldValue(
							tv.getSelectionValue(), "KEYFLD");
					if (kf == null) {
						return;
					}
					String acn = (String) tv.getData().getFieldValue(
							tv.getSelectionValue(), "ACc_NO");
					String acname = (String) tv.getData().getFieldValue(
							tv.getSelectionValue(), "AC_NAME");
					String descr = (String) tv.getData().getFieldValue(
							tv.getSelectionValue(), "DESCR");
					BigDecimal no = (BigDecimal) tv.getData().getFieldValue(
							tv.getSelectionValue(), "NO");

					String amt = (new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY))
							.format(Float.valueOf(((BigDecimal) tv.getData()
									.getFieldValue(tv.getSelectionValue(),
											"DEBIT_AMOUNT")).floatValue()));
					BigDecimal am = (BigDecimal) tv.getData().getFieldValue(
							tv.getSelectionValue(), "DEBIT_AMOUNT");
					Timestamp dt = (Timestamp) tv.getData().getFieldValue(
							tv.getSelectionValue(), "VOU_DATE");

					int rowno = data_recipts.addRecord();
					data_recipts.setFieldValue(rowno, "KEYFLD", kf);
					data_recipts.setFieldValue(rowno, "ACC_NO", acn);
					data_recipts.setFieldValue(rowno, "AC_NAME", acname);
					data_recipts.setFieldValue(rowno, "DESCR", descr);
					data_recipts.setFieldValue(rowno, "DEBIT_AMOUNT", am);
					data_recipts.setFieldValue(rowno, "VOU_DATE", dt);
					data_recipts.setFieldValue(rowno, "NO", no);
					data_recipts.getRows().get(rowno).setRowStatus(
							Row.ROW_INSERTED);
					fill_recipt_table();
				} catch (Exception e) {
					Logger.getLogger(frmBDV.class.getName()).log(Level.SEVERE,
							null, e);
					parentLayout.getWindow().showNotification(
							"Error additng to table",
							Notification.TYPE_ERROR_MESSAGE);
				}

			}
		};
		try {
			String vl = utils
					.getSqlValue(
							"select path FROM cp_user_profiles,acaccount "
									+ " where accno=value and profileno=0 and variable='BANK_GROUP'",
							con)
					+ "%";
			String ex_kf = "";
			for (int rn = 0; rn < data_recipts.getRows().size(); rn++) {
				if (rn == 0) {
					ex_kf = data_recipts.getFieldValue(rn, "KEYFLD").toString();

				} else {
					ex_kf = ex_kf
							+ ","
							+ data_recipts.getFieldValue(rn, "KEYFLD")
									.toString();
				}
			}
			if (data_recipts.getRows().size() <= 0) {
				ex_kf = "-.00001";
			}

			String sq = "select acvoucher1.keyfld,acvoucher1.no,VOU_DATE,descr,acaccount.accno ACC_NO,acaccount.name AC_NAME,codeamt DEBIT_AMOUNT "
					+ " from acvoucher1,acaccount "
					+ " where acvoucher1.codeacc=acaccount.accno AND ACVOUCHER1.REFERKEYFLD is null"
					+ " and acvoucher1.vou_code=2 "
					+ " and path not like '"
					+ vl
					+ "' and codeacc='"
					+ QRYSES_ACCNO
					+ "' and "
					+ " acvoucher1.keyfld not in (" + ex_kf + ") ORDER BY NO";

			final TableView tblview = utilsVaadin.showSearch(listlayout, sl,
					con, sq, true, new String[] { "KEYFLD" });
		} catch (Exception e) {
			Logger.getLogger(frmBDV.class.getName()).log(Level.SEVERE, null, e);
			parentLayout.getWindow().showNotification("Error filling table",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void calcSums() {
		if (utilsVaadin.findColByCol("DEBIT_AMOUNT", lstItemCols) == null
				|| data_recipts.getColByName("DEBIT_AMOUNT") == null) {
			return;
		}
		double damt = data_recipts.getSummaryOf("DEBIT_AMOUNT",
				localTableModel.SUMMARY_SUM);
		String a1 = new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY).format(damt);
		tbl_recipts.setColumnFooter(utilsVaadin.findColByCol("DEBIT_AMOUNT",
				lstItemCols).descr, a1);
	}

	public void resetFormLayout() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		mainLayout.removeAllComponents();
		commandLayout.removeAllComponents();
		commandLayout2.removeAllComponents();
		basicLayout.removeAllComponents();
		centralPanel.setHeight("-1px");
		centralPanel.removeAllComponents();
	}

	public void init() {
		QRYSES = "";
		initForm();
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		if (data_recipts.getDbclass() == null) {
			try {
				data_recipts.createDBClassFromConnection(con);
			} catch (SQLException e) {
			}
		}

		load_data();

	}

	public void load_data() {
		cmdDelete.setEnabled(false);
		txtJvno.setEnabled(true);
		data_recipts.clearALl();
		tbl_recipts.removeAllItems();

		varJvType = Double.valueOf(utils.getSqlValue(
				"select nvl(repair.getsetupvalue_2('BDV_JV'),992) from dual",
				con));
		varPeriodCode = utils.getSqlValue(
				"select repair.getsetupvalue_2('CURRENT_PERIOD') from dual",
				con);
		varJvno = Double.valueOf(utils
				.getSqlValue(
						"select nvl(max(no),0)+1 from  acvoucher1 where vou_code=1 and type= "
								+ varJvType + " and periodcode='"
								+ varPeriodCode + "'", con));
		varJvKeyfld = -1;
		txtJvno.setValue(String.valueOf(varJvno));
		txtDescr.setValue("");
		txtBankAcc.setValue(null);
		if (txtDate.getValue() != null) {
			((java.util.Date) txtDate.getValue()).setTime(System
					.currentTimeMillis());
		} else {
			java.util.Date dt = new java.util.Date(System.currentTimeMillis());
			txtDate.setValue(dt);
		}
		try {

			if (ps_jv != null) {
				ps_jv.close();
			}
			if (QRYSES.length() <= 0) {
				fetch_recipt_data();
			} else {
				txtJvno.setEnabled(false);
				cmdDelete.setEnabled(true);
				ps_jv = con.prepareStatement(
						"select *from acvoucher1 where keyfld=" + QRYSES,
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rsj = ps_jv.executeQuery();
				if (rsj.first()) {
					varJvKeyfld = Integer.valueOf(QRYSES);
					varJvno = rsj.getDouble("NO");
					txtJvno.setValue(String.valueOf(varJvno));
					txtDescr.setValue(rsj.getString("DESCR"));
					txtBankAcc.setValue(utilsVaadin.findByValue(txtBankAcc, rsj
							.getString("CODEACC")));
					((java.util.Date) txtDate.getValue()).setTime(rsj.getDate(
							"VOU_DATE").getTime());
					fetch_recipt_data();
				}
			}

		} catch (SQLException ex) {
			Logger.getLogger(frmBDV.class.getName())
					.log(Level.SEVERE, null, ex);
			parentLayout.getWindow().showNotification("Unable to load data",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void print_data() {
		try {
			load_data();
			utilsVaadinPrintHandler.printAccVoucher((Integer.valueOf(QRYSES)),
					con);
		} catch (Exception ex) {
			mainLayout.getWindow().showNotification("Unable to print:",
					ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			Logger.getLogger(frmPurchaseCost.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public void save_data() {

		save_data(true, false);
	}

	public void save_data(boolean backToList, boolean doPrint) {
		String sq1 = "";
		String sq2 = "";
		String vouKeys = "";
		String sqDelete = "";

		try {
			if (!cmdSave.isEnabled()) {
				return;
			}
			varJvno = Double.valueOf(txtJvno.getValue().toString());
			if (txtBankAcc.getValue() == null) {
				txtBankAcc.focus();
				throw new SQLException("Not selected Bank A/c");
			}
			if (txtDescr.getValue() == null) {
				txtDescr.focus();
				throw new SQLException("Enter some DESCRIPTION");
			}
			if (data_recipts.getRows().size() <= 0) {
				throw new SQLException("No recipt vouchers found");
			}

			// delete existing bdv
			if (QRYSES.length() > 0) {
				sqDelete = "declare "
						+ " cursor ii is select *from acvoucher2 where keyfld=:BDV_KEYFLD order by pos ;"
						+ " begin  for x in ii loop "
						+ " repair.decaccuses(x.accno);"
						+ " update acvoucher2 set referkeyfld=null,referno=null where keyfld=x.referkeyfld and vou_code=2;"
						+ " update acvoucher1 set referkeyfld=null,referno=null where keyfld=x.referkeyfld and vou_code=2;"
						+ " end loop; "
						+ " delete from acvoucher1 where keyfld=:BDV_KEYFLD;"
						+ " delete from acvoucher2 where keyfld=:BDV_KEYFLD; end;";
				QueryExe qe = new QueryExe(sqDelete, con);
				qe.setParaValue("BDV_KEYFLD", QRYSES);
				qe.execute();
			}
			String kfld = "";
			for (int i = 0; i < data_recipts.getRows().size(); i++) {
				kfld = data_recipts.getFieldValue(i, "KEYFLD").toString();
				vouKeys = vouKeys + " generate_bdv.add_voucher(" + kfld + "); ";
			}
			sq1 = "begin generate_bdv.debit_acc:='"
					+ ((dataCell) txtBankAcc.getValue()).getValue() + "'; "
					+ " generate_bdv.bdv_vou_no:=" + varJvno + ";"
					+ "generate_bdv.descr:='" + txtDescr.getValue() + "'; "
					+ "generate_bdv.vou_date:= :VOU_DATE ;" + vouKeys + " "
					+ "generate_bdv.generate;" + "end;";
			QueryExe qe = new QueryExe(sq1, con);
			qe.setParaValue("VOU_DATE", txtDate.getValue());
			qe.execute();
			if (QRYSES.length() == 0) {
				varJvKeyfld = Integer.valueOf(utils.getSqlValue(
						"select nvl(max(keyfld),-1) from acvoucher1 where vou_code=1 and type="
								+ varJvType, con));
			} else {

				varJvKeyfld = Integer.valueOf(utils.getSqlValue(
						"select nvl(max(keyfld),-1) from acvoucher1 where vou_code=1 and type="
								+ varJvType, con));
				QRYSES = String.valueOf(varJvKeyfld);
			}
			con.commit();

			if (doPrint) {
				if (QRYSES.length() == 0) {
					QRYSES = String.valueOf(varJvKeyfld);
				}
				print_data();
			}
			if (backToList) {
				resetFormLayout();
				showOutstandingReciptVoucher();
			}

		} catch (SQLException ex) {
			Logger.getLogger(frmBDV.class.getName())
					.log(Level.SEVERE, null, ex);
			parentLayout.getWindow().showNotification("Unable to save:",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();

			} catch (SQLException e) {
			}
		}
	}

	public void showInitView() {
		showOutstandingReciptVoucher();

	}

	public void showOutstandingReciptVoucher() {
		mapActionStrs.clear();
		mapActionStrs.put("generate", "Generate BDV...");

		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		QRYSES = "";
		QRYSES_ACCNO = "";

		try {
			String vl = utils
					.getSqlValue(
							"select path FROM cp_user_profiles,acaccount "
									+ " where accno=value and profileno=0 and variable='BANK_GROUP'",
							con)
					+ "%";
			final QueryView qv = new QueryView(con);
			qv
					.setSqlquery("select acaccount.accno,acaccount.name,sum(codeamt) DEBIT_AMOUNT from acvoucher1,acaccount "
							+ " where acvoucher1.codeacc=acaccount.accno AND ACVOUCHER1.REFERKEYFLD is null"
							+ " and acvoucher1.vou_code=2  "
							+ " and vou_date>=:FROMDATE and vou_date<=:TODATE "
							+ " and path not like '"
							+ vl
							+ "' GROUP BY acaccount.ACCNO ,acaccount.NAME");
			Parameter pm = new Parameter("FROMDATE");
			pm.setValueType(Parameter.DATA_TYPE_DATE);
			pm.setValue(fromdt);
			qv.addParameter(pm);
			pm = new Parameter("TODATE");
			pm.setValueType(Parameter.DATA_TYPE_DATE);
			pm.setValue(todt);
			qv.addParameter(pm);
			centralPanel.setHeight("100%");
			ResourceManager.addComponent(centralPanel, qv);
			qv.setSizeFull();

			qv.getDataListners().add(new tableDataListner() {
				public void afterQuery() {
					qv.getLctb().getColByName("DEBIT_AMOUNT").setNumberFormat(
							Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
				}

				public void afterVisual() {
					qv.getTable().setHeight("100%");

					qv.getTable().addActionHandler(new Handler() {
						public void handleAction(Action action, Object sender,
								Object target) {
							int rowno = Integer.valueOf(target.toString());
							QRYSES = "";
							QRYSES_ACCNO = qv.getLctb().getFieldValue(rowno,
									"ACCNO").toString();
							fromdt = (java.util.Date) qv.getMapParameters()
									.get("FROMDATE").getValue();
							todt = (java.util.Date) qv.getMapParameters().get(
									"TODATE").getValue();
							if (QRYSES_ACCNO != null
									&& QRYSES_ACCNO.length() > 0) {
								initForm();
							}
						}

						public Action[] getActions(Object target, Object sender) {
							List<Action> acts = new ArrayList<Action>();
							acts.clear();
							acts.add(new Action(mapActionStrs.get("generate")));
							Action[] ac_ar = new Action[acts.size()];
							return acts.toArray(ac_ar);
						}
					});
				}

				public void beforeQuery() {

				}

				public String calcSummary(List<String> qcGroup, qryColumn qc) {

					return "";
				}

				public String getCellStyle(qryColumn qc, int recordNo) {
					return null;
				}

			});
			
			Button bt = new Button("New");
			qv.getListUserButtons().add(bt);
			bt.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					initForm();
				}
			});

			qv.createView();
			centralPanel.requestRepaintAll();
		} catch (SQLException ex) {
			Logger.getLogger(frmGatePass.class.getName()).log(Level.SEVERE,
					null, ex);
			parentLayout.getWindow().showNotification("Unable to LOAD DATA:",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	public void fetch_recipt_data() throws SQLException {
		data_recipts.clearALl();
		String sq = "";
		String vl = utils
				.getSqlValue(
						"select path FROM cp_user_profiles,acaccount "
								+ " where accno=value and profileno=0 and variable='BANK_GROUP'",
						con)
				+ "%";
		if (QRYSES.length() == 0) {
			sq = "select acvoucher1.keyfld,acvoucher1.no,VOU_DATE,descr,acaccount.accno ACC_NO,acaccount.name AC_NAME,codeamt DEBIT_AMOUNT "
					+ " from acvoucher1,acaccount "
					+ " where acvoucher1.codeacc=acaccount.accno AND ACVOUCHER1.REFERKEYFLD is null"
					+ " and acvoucher1.vou_code=2 "
					+ " and vou_date>="
					+ utils.getOraDateValue(fromdt)
					+ " and vou_date<="
					+ utils.getOraDateValue(todt)
					+ " and path not like '"
					+ vl
					+ "' and codeacc='" + QRYSES_ACCNO + "' ORDER BY NO";
		} else {
			sq = "select v2.referkeyfld keyfld,v2.referno no, v3.vou_date, v2.descr,v2.accno ACC_NO,ac.name AC_NAME,v2.credit DEBIT_AMOUNT from "
					+ "acvoucher2 v2,acvoucher1 v3,acaccount ac where"
					+ " v2.accno=ac.accno and v3.keyfld=v2.referkeyfld and v3.vou_code=2  and v2.keyfld="
					+ QRYSES + " order by v2.POS";
		}

		data_recipts.executeQuery(sq, true);
		data_recipts.setMasterRowStatusAll(Row.ROW_QUERIED);
		fill_recipt_table();
	}

	public void fill_recipt_table() throws SQLException {
		tbl_recipts.removeAllItems();
		utilsVaadin.applyColumns("CASH.BDV", tbl_recipts, lstItemCols, con);
		utilsVaadin.query_data2(tbl_recipts, data_recipts, lstItemCols);
		tbl_recipts.setMultiSelect(true);
		calcSums();
	}
}
