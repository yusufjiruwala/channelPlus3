package com.windows;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.generic.Row;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.menuItem;
import com.generic.rowTriggerListner;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmPlans implements transactionalForm {

	private AbstractOrderedLayout parentLayout = null;
	private HorizontalLayout mainLayout = new HorizontalLayout();
	private HorizontalLayout toolbarLayout = new HorizontalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private VerticalLayout basiclayoutMain = new VerticalLayout();
	private VerticalLayout tablesLayout = new VerticalLayout();
	private HorizontalLayout totLayout = new HorizontalLayout();
	private VerticalLayout montLayout = new VerticalLayout();
	private HorizontalLayout periodLayout = new HorizontalLayout();
	private GridLayout basicLayout = new GridLayout(4, 1);

	private String titleStr = "Budget and Financial statments ";
	private Label txtTitle = new Label(titleStr);
	private String QRYSES = "";
	private Connection con = null;
	private Panel treeContainer = new Panel("");
	private Tree tree = new Tree();
	private Table tbl_accs = new Table();
	private Table tbl_months = new Table();

	private Button cmdSave = new Button("Save");
	private Button cmdDelete = new Button("Delete");
	private Button cmdAddAccount = new Button("Add Account/Group");
	private Button cmdUpdatevalue = new Button("Update values");

	private TextField txtTotDr = new TextField("Total Dr Balance");
	private TextField txtTotCr = new TextField("Total Cr Balance");

	private TextField txtKeyfld = new TextField("Plan No ");
	private TextField txtDescr = new TextField("Descr ");
	private TextField txtDescra = new TextField("Descr Arb");
	private ComboBox txtPeriodCode = new ComboBox("Period Code ");
	private ComboBox txtParentCode = new ComboBox("Parent Group ");
	private TextField txtFromDate = new TextField("From Date");
	private TextField txtToDate = new TextField("To Date");

	private double varKeyid = -1;
	private String varPathCode = "";
	private double varChildcount = 0;
	private double varLevelno = 0;
	private Timestamp varCreated_time = null;

	private final List<ColumnProperty> lstItemCols_accs = new ArrayList<ColumnProperty>();
	private final List<ColumnProperty> lstItemCols_months = new ArrayList<ColumnProperty>();
	private localTableModel data_plans = new localTableModel();
	private localTableModel data_months = new localTableModel();
	private localTableModel data_tree = new localTableModel();

	private Window wndList = new Window();
	private VerticalLayout listlayout = new VerticalLayout();

	private boolean listnerAdded = false;

	public void resetFormLayout() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		commandLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		basiclayoutMain.removeAllComponents();
		mainLayout.removeAllComponents();
		toolbarLayout.removeAllComponents();
		tree.removeAllItems();
		centralPanel.setHeight("-1px");
		centralPanel.setWidth("-1px");
		treeContainer.getContent().removeAllComponents();
		// treeContainer.getContent().setWidth("-1px");
	}

	public void setParentLayout(AbstractLayout abs) {
		this.parentLayout = (AbstractOrderedLayout) abs;
	}

	public void createView() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		resetFormLayout();

		tree.setSelectable(true);
		tree.setNullSelectionAllowed(false);
		// tree.setSizeUndefined();
		tree.setSizeFull();

		basicLayout.setSizeFull();
		commandLayout.setHeight("100%");
		basiclayoutMain.setSizeFull();

		treeContainer.addStyleName("menucontainer");
		treeContainer.addStyleName("light"); // No border
		treeContainer.setSizeFull();
		treeContainer.getContent().setSizeFull();
		toolbarLayout.setSizeFull();
		mainLayout.setSizeFull();
		tablesLayout.setSizeFull();
		// tbl_accs.setSizeFull();
		// tbl_months.setSizeFull();
		tbl_accs.setWidth("600px");
		tbl_months.setWidth("600px");
		// tbl_accs.setHeight("200px");
		// tbl_months.setHeight("150px");
		tbl_accs.setPageLength(5);
		tbl_months.setPageLength(12);
		totLayout.setSizeFull();
		txtTotCr.addStyleName("rightalign");
		txtTotDr.addStyleName("rightalign");

		tbl_accs.setImmediate(true);
		tbl_accs.setSelectable(true);
		tbl_months.setSelectable(true);
		tbl_accs.setNullSelectionAllowed(false);
		tbl_accs.setFooterVisible(true);

		txtKeyfld.setEnabled(false);
		txtFromDate.setEnabled(false);
		txtToDate.setEnabled(false);
		txtPeriodCode.setImmediate(true);
		txtParentCode.setImmediate(true);

		ResourceManager.addComponent(basicLayout, txtKeyfld);
		ResourceManager.addComponent(basicLayout, txtDescr);
		ResourceManager.addComponent(basicLayout, txtDescra);
		ResourceManager.addComponent(basicLayout, txtParentCode);

		ResourceManager.addComponent(periodLayout, txtPeriodCode);
		ResourceManager.addComponent(periodLayout, txtFromDate);
		ResourceManager.addComponent(periodLayout, txtToDate);

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdDelete);
		ResourceManager.addComponent(commandLayout, cmdAddAccount);

		ResourceManager.addComponent(treeContainer.getContent(), tree);

		ResourceManager.addComponent(tablesLayout, tbl_accs);
		ResourceManager.addComponent(tablesLayout, periodLayout);
		ResourceManager.addComponent(tablesLayout, montLayout);

		ResourceManager.addComponent(basiclayoutMain, basicLayout);
		ResourceManager.addComponent(basiclayoutMain, tablesLayout);

		ResourceManager.addComponent(montLayout, tbl_months);
		ResourceManager.addComponent(montLayout, totLayout);

		ResourceManager.addComponent(totLayout, txtTotDr);
		ResourceManager.addComponent(totLayout, txtTotCr);
		ResourceManager.addComponent(totLayout, cmdUpdatevalue);

		ResourceManager.addComponent(toolbarLayout, txtTitle);
		ResourceManager.addComponent(toolbarLayout, commandLayout);

		ResourceManager.addComponent(parentLayout, toolbarLayout);
		ResourceManager.addComponent(mainLayout, treeContainer);
		ResourceManager.addComponent(mainLayout, basiclayoutMain);

		ResourceManager.addComponent(centralPanel, mainLayout);

		(parentLayout).setExpandRatio(toolbarLayout, .4f);
		(parentLayout).setExpandRatio(mainLayout, 3.6f);

		mainLayout.setExpandRatio(basiclayoutMain, 2.5f);
		mainLayout.setExpandRatio(treeContainer, 1.5f);

		basiclayoutMain.setExpandRatio(basicLayout, 1f);
		basiclayoutMain.setExpandRatio(tablesLayout, 3f);

		tablesLayout.setExpandRatio(tbl_accs, 2.5f);
		tablesLayout.setExpandRatio(montLayout, 1.25f);
		tablesLayout.setExpandRatio(periodLayout, .25f);

		// mainLayout.setExpandRatio(txtTitle, .1f);
		// mainLayout.setExpandRatio(commandLayout, .3f);

		montLayout.setExpandRatio(tbl_months, 3.5f);
		montLayout.setExpandRatio(totLayout, .5f);
		totLayout
				.setComponentAlignment(cmdUpdatevalue, Alignment.MIDDLE_CENTER);

		try {
			utilsVaadin.FillCombo(txtPeriodCode,
					"select code,code nm from fiscalperiod", con);
			utilsVaadin
					.FillCombo(
							txtParentCode,
							"select keyfld,descr from accplans_1 where usecount=0 order by path ",
							con);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (!listnerAdded) {
			txtPeriodCode.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if (!txtPeriodCode.isEnabled()) {
						return;
					}

					txtFromDate.setValue("");
					txtToDate.setValue("");

					if (txtPeriodCode.getValue() == null) {
						return;
					}

					String cod = ((dataCell) txtPeriodCode.getValue())
							.getValue().toString();
					try {

						PreparedStatement pst = con.prepareStatement(
								"select fromdate,todate from fiscalperiod where "
										+ " code='" + cod + "'",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
						ResultSet rst = pst.executeQuery();
						if (rst.first()) {
							txtFromDate
									.setValue((new java.text.SimpleDateFormat(
											utils.FORMAT_SHORT_DATE))
											.format(rst.getDate("fromdate")));
							txtToDate.setValue((new java.text.SimpleDateFormat(
									utils.FORMAT_SHORT_DATE)).format(rst
									.getDate("todate")));

						}
						pst.close();
						fill_month_table();
						if (tbl_months.size() <= 0) {
							generate_month_data("", "");
							fill_month_table();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			cmdAddAccount.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					add_account();
				}
			});
			tbl_accs.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						fill_month_table();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});
			data_months.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					calcMonthSum();

				}
			});
			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			tree.addActionHandler(new Handler() {

				public void handleAction(Action action, Object sender,
						Object target) {
					menuItem menusel = ((menuItem) target);
					if (action.getCaption().equals("Open")) {
						QRYSES = menusel.getId();
						load_data();
					}
				}

				public Action[] getActions(Object target, Object sender) {
					List<Action> lstact = new ArrayList<Action>();
					menuItem menusel = ((menuItem) target);
					lstact.add(new Action("Open"));
					Action[] act = new Action[lstact.size()];
					return lstact.toArray(act);
				}

			});
			cmdDelete.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					delete_data();

				}
			});
			cmdUpdatevalue.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					if (tbl_accs.getValue() == null) {
						return;
					}
					if (tbl_months.size() != 12) {
						return;
					}
					try {
						double vl_dr = (new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY))
								.parse(txtTotDr.getValue().toString())
								.doubleValue();
						double vl_cr = (new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY))
								.parse(txtTotCr.getValue().toString())
								.doubleValue();
						double v1_dr = vl_dr / 12;
						double v1_cr = vl_cr / 12;
						for (int i = 0; i < data_months.getRows().size(); i++) {
							data_months.setFieldValue(i, "DEBIT", BigDecimal
									.valueOf(v1_dr));
							data_months.setFieldValue(i, "CREDIT", BigDecimal
									.valueOf(v1_cr));
						}
						fill_month_table();
					} catch (Exception ex) {
						ex.printStackTrace();
						parentLayout.getWindow().showNotification("",
								ex.toString(), Notification.TYPE_ERROR_MESSAGE);
					}
				}
			});
			data_plans.setRowlistner(new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					DecimalFormat df = new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
					double d1 = data_plans.getSummaryOf("DEBIT",
							localTableModel.SUMMARY_SUM);
					double c1 = data_plans.getSummaryOf("CREDIT",
							localTableModel.SUMMARY_SUM);
					tbl_accs.setColumnFooter(utilsVaadin.findColByCol("DEBIT",
							lstItemCols_accs).descr, df.format(d1));
					tbl_accs.setColumnFooter(utilsVaadin.findColByCol("CREDIT",
							lstItemCols_accs).descr, df.format(d1));

				}
			});
			listnerAdded = true;
		}

	}

	public void delete_data() {
		Callback cb_del = new Callback() {

			public void onDialogResult(boolean resultIsYes) {
				if (resultIsYes) {
					try {
						if (QRYSES.length() == 0) {
							QRYSES = "";
							load_data();
							return;
						}
						double cnt = Double.valueOf(utils.getSqlValue(
								"Select nvl(count(*),0) from accplans_1 where parentcode="
										+ QRYSES, con));
						if (cnt > 0) {
							throw new Exception(
									"childs are  existed, remove first child");
						}
						con.setAutoCommit(false);
						utils
								.execSql(
										"declare c number;p number;begin select parentcode into p from accplans_1 where keyfld='"
												+ QRYSES
												+ "';delete from accplans_1 where keyfld='"
												+ QRYSES
												+ "'; delete from accplans_2 where keyfld='"
												+ QRYSES
												+ "'; "
												+ " select nvl(count(*),0) into c from accplans_1 where parentcode=p; "
												+ " update accplans_1 set childcount=c where keyfld=p; end; ",
										con);
						con.commit();
						QRYSES = "";
						load_data();
						fill_up_tree();
					} catch (Exception ex) {
						ex.printStackTrace();
						parentLayout.getWindow().showNotification(
								"Unable to update status :", ex.toString(),
								Notification.TYPE_ERROR_MESSAGE);
						try {
							con.rollback();
						} catch (SQLException e) {
						}

					}

				}

			}
		};
		double cnt = Double.valueOf(utils.getSqlValue(
				"Select nvl(count(*),0) from accplans_1 where parentcode='"
						+ QRYSES + "'", con));
		if (cnt > 0) {
			parentLayout.getWindow().showNotification(
					"Childs are existed, remove first childeren",
					Notification.TYPE_ERROR_MESSAGE);
			return;
		}

		parentLayout.getWindow().addWindow(
				new YesNoDialog("Deleteting Budget",
						"Update this Budget to remove ??", cb_del));

	}

	public void calcMonthSum() {
		double d = data_months.getSummaryOf("DEBIT",
				localTableModel.SUMMARY_SUM);
		double c = data_months.getSummaryOf("CREDIT",
				localTableModel.SUMMARY_SUM);
		DecimalFormat df = new DecimalFormat(Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
		txtTotDr.setValue(df.format(d));
		txtTotCr.setValue(df.format(c));
		if (tbl_accs.getValue() != null) {
			int rn = (Integer) tbl_accs.getValue();
			data_plans.setFieldValue(rn, "DEBIT", BigDecimal.valueOf(d));
			data_plans.setFieldValue(rn, "CREDIT", BigDecimal.valueOf(c));
			utilsVaadin.refreshRowFromData(data_plans, tbl_accs, rn,
					lstItemCols_accs);
		}

	}

	public void add_account() {
		try {
			wndList.setContent(listlayout);
			TableView.SelectionListener sl = new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wndList);
					try {
						String rfr = (String) tv.getData().getFieldValue(
								tv.getSelectionValue(), "ACCNO");
						String rfrnm = (String) tv.getData().getFieldValue(
								tv.getSelectionValue(), "NAME");
						boolean fnd = false;
						if (data_plans.locate("ACCNO", rfr,
								localTableModel.FIND_EXACT) >= 0) {
							fnd = true;
							parentLayout.getWindow().showNotification(
									"Account existed",
									Notification.TYPE_ERROR_MESSAGE);
							return;
						}
						int rn = data_plans.addRecord();
						data_plans.setFieldValue(rn, "ACCNO", rfr);
						data_plans.setFieldValue(rn, "ACNAME", rfrnm);
						data_plans.setFieldValue(rn, "DEBIT", BigDecimal
								.valueOf(0));
						data_plans.setFieldValue(rn, "CREDIT", BigDecimal
								.valueOf(0));
						data_plans.setFieldValue(rn, "ACTUAL", BigDecimal
								.valueOf(0));
						int p_rn = rn;
						generate_month_data(rfr, rfrnm);
						fill_acc_table();
					} catch (Exception e) {
						e.printStackTrace();
						Logger.getLogger(frmPlans.class.getName()).log(
								Level.SEVERE, null, e);
						parentLayout.getWindow().showNotification(
								"Error filling table",
								Notification.TYPE_ERROR_MESSAGE);

					}

				}
			};
			final TableView tblview = utilsVaadin
					.showSearch(
							listlayout,
							sl,
							con,
							"select accno,name from acaccount where actype in (0,2) order by path",
							true);

		} catch (Exception e) {
			Logger.getLogger(frmPlans.class.getName()).log(Level.SEVERE, null,
					e);
			parentLayout.getWindow().showNotification("Error filling table",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void generate_month_data(String accn, String accname)
			throws Exception {
		DecimalFormat df = new DecimalFormat("00");
		java.text.SimpleDateFormat f1 = new java.text.SimpleDateFormat("MMM");
		java.text.SimpleDateFormat f2 = new java.text.SimpleDateFormat("MM");
		java.text.SimpleDateFormat f3 = new java.text.SimpleDateFormat("yyyy");
		String rfr = accn, rfrnm = accname;

		if (tbl_accs.getValue() != null && rfr.isEmpty()) {
			int sl = (Integer) tbl_accs.getValue();
			rfr = data_plans.getFieldValue(sl, "ACCNO").toString();
		}
		if (tbl_accs.getValue() != null && rfrnm.isEmpty()) {
			int sl = (Integer) tbl_accs.getValue();
			rfrnm = data_plans.getFieldValue(sl, "ACNAME").toString();
		}
		if (rfr.isEmpty() || rfrnm.isEmpty()) {
			return;
		}

		int rn = 0;
		Calendar dt = Calendar.getInstance();
		dt.setTimeInMillis((((new SimpleDateFormat(utils.FORMAT_SHORT_DATE))
				.parse(txtFromDate.getValue().toString())).getTime()));
		// use latter joda-time-2.0 for date
		for (int i = dt.get(Calendar.MONTH) + 1; i < 13; i++) {
			rn = data_months.addRecord();
			data_months.setFieldValue(rn, "ACCNO", rfr);
			data_months.setFieldValue(rn, "ACNAME", rfrnm);
			data_months.setFieldValue(rn, "DEBIT", BigDecimal.valueOf(0));
			data_months.setFieldValue(rn, "CREDIT", BigDecimal.valueOf(0));
			data_months.setFieldValue(rn, "ACTUAL", BigDecimal.valueOf(0));
			data_months.setFieldValue(rn, "MONTH_NO", dt.get(Calendar.YEAR)
					+ "/" + df.format(i));

			data_months.setFieldValue(rn, "MONTH_DESCR", f1.format(f2.parse(df
					.format(i))));
			data_months
					.setFieldValue(rn, "PERIODCODE", ((dataCell) txtPeriodCode
							.getValue()).getValue().toString());
			data_months.getMasterRows().add(data_months.getRows().get(rn));
			Calendar cl = Calendar.getInstance();
			cl.set(dt.get(Calendar.YEAR), i - 1, 1, 0, 0, 0);
			data_months.setFieldValue(rn, "FROMDATE", new Timestamp(cl
					.getTimeInMillis()));
			cl.set(Calendar.DAY_OF_MONTH, cl
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			data_months.setFieldValue(rn, "TODATE", new Timestamp(cl
					.getTimeInMillis()));

		}
	}

	public void init() {
		QRYSES = "";
		initForm();
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		if (data_months.getDbclass() == null) {
			try {
				data_months.createDBClassFromConnection(con);
				data_plans.createDBClassFromConnection(con);
			} catch (SQLException e) {

			}
		}
		try {
			fill_up_tree();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		load_data();

	}

	private void fill_up_tree() throws SQLException {
		frmMainMenus fm = ((frmMainMenus) Channelplus3Application.getInstance()
				.getPurWnd().getContent());

		data_tree.createDBClassFromConnection(Channelplus3Application
				.getInstance().getFrmUserLogin().getDbc().getDbConnection());
		data_tree.clearALl();
		data_tree
				.executeQuery("select * from accplans_1  order by path ", true);
		utilsVaadin.fillTree(tree, data_tree, "PARENTCODE", "KEYFLD", "DESCR",
				"CHILDCOUNT");

	}

	public void load_data() {
		txtPeriodCode.setEnabled(true);
		txtParentCode.setEnabled(true);
		cmdAddAccount.setEnabled(true);

		data_months.clearALl();
		data_plans.clearALl();
		tbl_accs.removeAllItems();
		tbl_months.removeAllItems();

		varChildcount = 0;
		varKeyid = -1;
		varLevelno = 1;
		varPathCode = "XXX\\";
		txtFromDate.setValue("");
		txtToDate.setValue("");
		txtDescr.setValue("");
		txtDescra.setValue("");
		txtParentCode.setValue(null);
		txtPeriodCode.setValue(null);
		txtPeriodCode.setValue(utilsVaadin.findByValue(txtPeriodCode, utils
				.getSqlValue("select repair.getcurperiodcode from dual", con)));
		txtTotCr.setValue("0");
		txtTotDr.setValue("0");
		try {
			if (QRYSES.length() > 0) {
				// txtParentCode.setEnabled(false);
				PreparedStatement pst = con.prepareStatement(
						"select *from accplans_1 where keyfld=" + QRYSES,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pst.executeQuery();
				rst.first();
				varKeyid = rst.getDouble("KEYFLD");
				varChildcount = rst.getDouble("CHILDCOUNT");
				varLevelno = rst.getDouble("LEVELNO");
				varPathCode = rst.getString("path");
				txtDescr.setValue(rst.getString("descr"));
				if (rst.getString("descra") != null) {
					txtDescra.setValue(rst.getString("descra"));
				}
				txtParentCode.setValue(utilsVaadin.findByValue(txtParentCode,
						rst.getString("parentcode")));
				txtPeriodCode.setValue(utilsVaadin.findByValue(txtPeriodCode,
						rst.getString("periodcode")));

				txtFromDate.setValue((new SimpleDateFormat(
						utils.FORMAT_SHORT_DATE)).format(rst
						.getDate("fromdate")));
				txtToDate
						.setValue((new SimpleDateFormat(utils.FORMAT_SHORT_DATE))
								.format(rst.getDate("todate")));
				txtKeyfld.setValue(String.valueOf(varKeyid));
				varCreated_time = rst.getTimestamp("CREATED_DATE");
				if (rst.getInt("CHILDCOUNT") > 0) {
					cmdAddAccount.setEnabled(false);
				}
				pst.close();

			} else {
				txtKeyfld.setValue(utils.getSqlValue(
						"select nvl(max(keyfld),0)+1 from accplans_1 ", con));
				varCreated_time = new Timestamp(System.currentTimeMillis());
			}
			fetch_accs();
			calcMonthSum();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void fetch_accs() throws SQLException {
		tbl_months.removeAllItems();
		data_plans.clearALl();
		String sq = " select refer ACCNO,ACACCOUNT.NAME ACNAME,sum(DEBIT) DEBIT , SUM(CREDIT) CREDIT,ACACCOUNT.PATH,RATE_A_FROM,RATE_B_FROM,"
				+ "  0 ACTUAL,ACCPLANS_2.PERIODCODE PERIODCODE FROM ACACCOUNT,ACCPLANS_2 "
				+ " WHERE ACCPLANS_2.REFER=ACCNO and ACCPLANS_2.KEYFLD='"
				+ QRYSES
				+ "' "
				+ " GROUP BY REFER,ACACCOUNT.NAME,ACACCOUNT.PATH,0 ,RATE_A_FROM,RATE_B_FROM,ACCPLANS_2.PERIODCODE "
				+ " ORDER BY ACACCOUNT.PATH";
		data_plans.executeQuery(sq, true);
		data_plans.setMasterRowStatusAll(Row.ROW_QUERIED);
		fetch_months();
		fill_acc_table();
	}

	public void fetch_months() throws SQLException {
		tbl_months.removeAllItems();
		data_months.clearALl();
		String sq = " select refer ACCNO,ACACCOUNT.NAME ACNAME,DEBIT, CREDIT,MNTH MONTH_NO,ACACCOUNT.PATH,ACCPLANS_2.PERIODCODE,"
				+ " TO_CHAR(FROMDATE,'MON') MONTH_DESCR, 0 ACTUAL ,FROMDATE,TODATE FROM ACACCOUNT,ACCPLANS_2 "
				+ " WHERE ACCPLANS_2.REFER=ACCNO and ACCPLANS_2.KEYFLD='"
				+ QRYSES + "' " + " ORDER BY ACACCOUNT.PATH,month_no";
		data_months.executeQuery(sq, true);
		data_months.setMasterRowStatusAll(Row.ROW_QUERIED);
		data_months.setMasterRowsAsRows();

		fill_month_table();

	}

	public void fill_acc_table() throws SQLException {
		tbl_accs.removeAllItems();
		utilsVaadin
				.applyColumns("BUDGET.ACCS", tbl_accs, lstItemCols_accs, con);

		utilsVaadin.findColByCol("RATE_A_FROM", lstItemCols_accs).lov_sql = new ArrayList<dataCell>();
		utilsVaadin.findColByCol("RATE_B_FROM", lstItemCols_accs).lov_sql = new ArrayList<dataCell>();
		utils
				.FillLov(utilsVaadin.findColByCol("RATE_A_FROM",
						lstItemCols_accs).lov_sql,
						"select keyfld,descr from accplans_1 where keyfld!=nvl('"
								+ QRYSES + "',-99999) order by path", con);

		utils
				.FillLov(utilsVaadin.findColByCol("RATE_B_FROM",
						lstItemCols_accs).lov_sql,
						"select keyfld,descr from accplans_1 where keyfld!=nvl('"
								+ QRYSES + "',-99999) order by path", con);

		utilsVaadin.query_data2(tbl_accs, data_plans, lstItemCols_accs);

	}

	public void fill_month_table() throws SQLException {
		tbl_months.removeAllItems();

		if (tbl_accs.getValue() == null || tbl_accs.size() == 0
				|| data_plans.getRows().size() <= 0) {
			data_months.getRows().clear();
		} else {
			List<Parameter> lst = new ArrayList<Parameter>();
			Integer cur = (Integer) tbl_accs.getValue();
			Parameter pm = new Parameter("ACCNO", "="
					+ data_plans.getFieldValue(cur, "ACCNO").toString());
			if (txtPeriodCode.getValue() != null) {
				Parameter pm2 = new Parameter("PERIODCODE", "="
						+ ((dataCell) txtPeriodCode.getValue()).getValue()
								.toString());
				lst.add(pm2);
			}

			lst.add(pm);

			data_months.setColumnsFilter(lst);
		}

		utilsVaadin.applyColumns("BUDGET.MONTH", tbl_months,
				lstItemCols_months, con);

		utilsVaadin.query_data2(tbl_months, data_months, lstItemCols_months);
		calcMonthSum();
	}

	public void print_data() {

	}

	public void save_data() {
		save_data(true, false);
	}

	public boolean is_validation_pass() throws SQLException {
		if (txtDescr.getValue() == null
				|| txtDescr.getValue().toString().length() == 0) {
			throw new SQLException("Description must have value");

		}
		if (txtPeriodCode.getValue() == null) {
			throw new SQLException("Period  must have value");

		}
		if (txtParentCode.getValue() != null
				&& QRYSES.length() > 0
				&& Double.valueOf(((dataCell) txtParentCode.getValue())
						.getValue().toString()) == Double.valueOf(QRYSES)) {
			throw new SQLException(
					"parent and current id can not have same value must have value");

		}

		return true;
	}

	public void save_data(boolean backlist, boolean doprint) {
		String sql1 = "";
		String sql2 = "";
		String delsql = "";

		sql1 = "insert into accplans_1(KEYFLD,FROMDATE,TODATE,PERIODCODE,DESCR,DESCRA,PARENTCODE,PATH,CHILDCOUNT,LEVELNO,CREATED_DATE,MODIFIED_DATE,USERNM,FLAG,USECOUNT) VALUES "
				+ " (:KEYFLD,:FROMDATE,:TODATE,:PERIODCODE,:DESCR,:DESCRA,:PARENTCODE,:PATH,:CHILDCOUNT,:LEVELNO,:CREATED_DATE,:MODIFIED_DATE,:USERNM,1,:USECOUNT)";

		sql2 = "insert into accplans_2(KEYFLD,KIND,REFER,FROMDATE,TODATE,MNTH,PERIODCODE,DEBIT,CREDIT,RATE_A_FROM,RATE_B_FROM) VALUES "
				+ " (:KEYFLD,'ACCOUNT',:REFER,:FROMDATE,:TODATE,:MNTH,:PERIODCODE,:DEBIT,:CREDIT,:RATE_A_FROM,:RATE_B_FROM) ";

		try {
			if (!is_validation_pass()) {
				return;
			}
			Timestamp mo = null;
			SimpleDateFormat sd = new SimpleDateFormat(utils.FORMAT_SHORT_DATE);
			QueryExe qe = new QueryExe(con);
			QueryExe qe2 = new QueryExe(con);

			con.setAutoCommit(false);
			data_months.setColumnsFilter(null);
			if (QRYSES.length() > 0) {
				utils.execSql("begin delete from accplans_1 where keyfld='"
						+ QRYSES + "';"
						+ "delete from accplans_2 where keyfld='" + QRYSES
						+ "'; end;", con);
				mo = new Timestamp(System.currentTimeMillis());
				varKeyid = Integer.valueOf(QRYSES);
			} else {
				varCreated_time.setTime(System.currentTimeMillis());
				mo = new Timestamp(varCreated_time.getTime());
				varKeyid = Double.valueOf(utils.getSqlValue(
						"select nvl(max(keyfld),0)+1 from accplans_1", con));
			}

			String pr = "";
			if (txtParentCode.getValue() != null) {
				pr = ((dataCell) txtParentCode.getValue()).getValue()
						.toString();
			}
			varPathCode = generatePath(pr, (new DecimalFormat("0000000"))
					.format(varKeyid));
			varLevelno = utils.noOfCountInString(varPathCode, "\\") - 1;
			varChildcount = Double.valueOf(utils.getSqlValue(
					"select nvl(count(*),0) from accplans_1 where parentcode='"
							+ QRYSES + "'", con));
			int usecnt = 0;
			// (:KEYFLD,'ACCOUNT',:REFER,:FROMDATE,:TODATE,:MNTH,:PERIODCODE,:DEBIT,:CREDIT)
			// ";
			qe2.setSqlStr(sql2);
			qe2.parse();

			String afrom = "", bfrom = "";
			int rfound = -1;

			for (int i = 0; i < data_months.getRows().size(); i++) {
				qe2.setParaValue("KEYFLD", varKeyid);
				qe2
						.setParaValue("REFER", data_months.getFieldValue(i,
								"ACCNO"));
				qe2.setParaValue("FROMDATE", data_months.getFieldValue(i,
						"FROMDATE"));
				qe2.setParaValue("TODATE", data_months.getFieldValue(i,
						"TODATE"));
				qe2.setParaValue("MNTH", data_months.getFieldValue(i,
						"MONTH_NO"));
				qe2.setParaValue("PERIODCODE", ((dataCell) txtPeriodCode
						.getValue()).getValue());
				qe2
						.setParaValue("DEBIT", data_months.getFieldValue(i,
								"DEBIT"));
				qe2.setParaValue("CREDIT", data_months.getFieldValue(i,
						"CREDIT"));
				afrom = "";
				bfrom = "";
				rfound = data_plans.locate("ACCNO", data_months.getFieldValue(
						i, "ACCNO").toString(), localTableModel.FIND_EXACT);
				if (rfound >= 0) {
					if (data_plans.getFieldValue(rfound, "RATE_A_FROM") != null) {
						afrom = data_plans.getFieldValue(rfound, "RATE_A_FROM")
								.toString();
					}
					if (data_plans.getFieldValue(rfound, "RATE_B_FROM") != null) {
						bfrom = data_plans.getFieldValue(rfound, "RATE_B_FROM")
								.toString();
					}

				}
				qe2.setParaValue("RATE_A_FROM", afrom);
				qe2.setParaValue("RATE_B_FROM", bfrom);

				usecnt++;
				qe2.execute(false);
			}
			qe2.close();
			// :KEYFLD,:FROMDATE,:TODATE,:PERIODCODE,:DESCR,:DESCRA,:PARENTCODE,:PATH,:CHILDCOUNT,:LEVELNO,
			// :CREATED_DATE,:MODIFIED_DATE,:USERNM,:USECOUNT

			qe.setSqlStr(sql1);
			qe.setParaValue("KEYFLD", varKeyid);
			qe.setParaValue("FROMDATE", sd.parse(txtFromDate.getValue()
					.toString()));
			qe
					.setParaValue("TODATE", sd.parse(txtToDate.getValue()
							.toString()));
			qe.setParaValue("PERIODCODE", ((dataCell) txtPeriodCode.getValue())
					.getValue());
			qe.setParaValue("DESCR", txtDescr.getValue());
			qe.setParaValue("DESCRA", txtDescra.getValue());
			if (txtParentCode.getValue() != null) {
				qe.setParaValue("PARENTCODE", ((dataCell) txtParentCode
						.getValue()).getValue().toString());
			} else {
				qe.setParaValue("PARENTCODE", "");

			}
			qe.setParaValue("PATH", varPathCode);
			qe.setParaValue("CHILDCOUNT", varChildcount);
			qe.setParaValue("LEVELNO", varLevelno);
			qe.setParaValue("CREATED_DATE", varCreated_time);
			qe.setParaValue("MODIFIED_DATE", mo);
			qe.setParaValue("USERNM", utils.CPUSER);
			qe.setParaValue("USECOUNT", usecnt);
			qe.execute(true);
			if (txtParentCode.getValue() != null) {
				String s = ((dataCell) txtParentCode.getValue()).getValue()
						.toString();
				utils
						.execSql(
								"declare a number; begin select nvl(count(*),0) into a  from accplans_1 where parentcode='"
										+ s
										+ "'; update accplans_1 set childcount=a where keyfld='"
										+ s + "'; end; ", con);

			}

			con.commit();

			parentLayout.getWindow().showNotification("Saved Successfully");
			fill_up_tree();
			QRYSES = "";
			load_data();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("Unable to save:",
					ex.toString(), Notification.TYPE_ERROR_MESSAGE);

			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}
	}

	public String generatePath(String pcode, String curr) throws SQLException {
		PreparedStatement psx = con.prepareStatement(
				"select path from ACCPLANS_1 where keyfld='" + pcode + "'",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rst = psx.executeQuery();
		String pth = "XXX\\" + curr + "\\";
		if (rst.first()) {
			pth = rst.getString("PATH") + curr + "\\";
		}
		psx.close();
		return pth;
	}

	public void showInitView() {
		init();
	}

}
