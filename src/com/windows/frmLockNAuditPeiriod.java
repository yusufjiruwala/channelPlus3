package com.windows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.doc.views.YesNoDialog;
import com.doc.views.YesNoDialog.Callback;
import com.generic.ColumnProperty;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.Row;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class frmLockNAuditPeiriod implements transactionalForm {
	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private GridLayout basicLayout = new GridLayout(5, 2);
	private HorizontalLayout footerLayout = new HorizontalLayout();
	private String titleStr = "Closing / Locking Period ";
	private Label txtTitle = new Label(titleStr);
	private Label txtWorking = new Label("");
	private Button cmdExecuteLock = new Button("Execute");
	private Button cmdUnlock = new Button("Unlock to Closed Date");
	private DateField txtLastClosed = new DateField("Last Closed Period",
			new Date(System.currentTimeMillis()));
	private DateField txtLastLocked = new DateField("Last Closed Period",
			new Date(System.currentTimeMillis()));
	private DateField txtUntilDate = new DateField("Untill date ", new Date(
			System.currentTimeMillis()));
	private CheckBox chkCost = new CheckBox("Average Cost", true);
	private CheckBox chkOnlyQty = new CheckBox("Only Quantity", true);
	private CheckBox chkLock = new CheckBox("Lock", true);
	private CheckBox chkRepairData = new CheckBox("Update/Rectify", true);
	private ProgressIndicator compProgres = new ProgressIndicator();
	private Table tbl_data = new Table("Details");

	private final List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();
	private localTableModel data_details = new localTableModel();
	private Connection con = null;
	private boolean listnerAdded = false;
	
	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void resetFormLayout() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		commandLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		mainLayout.removeAllComponents();
		centralPanel.setHeight("-1px");
		basicLayout.setSpacing(true);
		basicLayout.setMargin(false, true, false, true);

	}

	public void createView() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		resetFormLayout();

		ResourceManager.addComponent(commandLayout, cmdExecuteLock);
		ResourceManager.addComponent(commandLayout, cmdUnlock);
		ResourceManager.addComponent(commandLayout, compProgres);
		ResourceManager.addComponent(commandLayout, txtWorking);

		basicLayout.setHeight("100%");
		footerLayout.setSizeFull();
		tbl_data.setHeight("100%");
		tbl_data.setWidth("100%");
		txtWorking.setWidth("100%");
		compProgres.setWidth("100%");
		txtTitle.setSizeFull();
		mainLayout.setSizeFull();
		commandLayout.setComponentAlignment(compProgres,
				Alignment.MIDDLE_CENTER);
		commandLayout
				.setComponentAlignment(txtWorking, Alignment.MIDDLE_CENTER);
		commandLayout.setMargin(false, true, false, true);

		ResourceManager.addComponent(basicLayout, txtLastClosed);
		ResourceManager.addComponent(basicLayout, txtLastLocked);
		ResourceManager.addComponent(basicLayout, txtUntilDate);

		ResourceManager.addComponent(basicLayout, new Label(""));
		ResourceManager.addComponent(basicLayout, new Label(""));
		ResourceManager.addComponent(basicLayout, chkLock);
		ResourceManager.addComponent(basicLayout, chkCost);
		ResourceManager.addComponent(basicLayout, chkOnlyQty);
		ResourceManager.addComponent(basicLayout, chkRepairData);

		ResourceManager.addComponent(footerLayout, txtWorking);
		ResourceManager.addComponent(footerLayout, compProgres);

		txtLastClosed.setEnabled(false);
		txtLastLocked.setEnabled(false);
		txtUntilDate.setResolution(DateField.RESOLUTION_DAY);
		txtLastClosed.setResolution(DateField.RESOLUTION_DAY);
		txtLastClosed.setDateFormat(utils.FORMAT_SHORT_DATE);
		txtUntilDate.setDateFormat(utils.FORMAT_SHORT_DATE);
		txtLastLocked.setResolution(DateField.RESOLUTION_DAY);
		txtLastLocked.setDateFormat(utils.FORMAT_SHORT_DATE);

		tbl_data.setSelectable(true);

		ResourceManager.addComponent(mainLayout, txtTitle);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);
		ResourceManager.addComponent(mainLayout, tbl_data);
		ResourceManager.addComponent(mainLayout, footerLayout);
		ResourceManager.addComponent(centralPanel, mainLayout);

		mainLayout.setExpandRatio(txtTitle, 0.2f);
		mainLayout.setExpandRatio(commandLayout, 0.5f);
		mainLayout.setExpandRatio(basicLayout, 0.6f);
		mainLayout.setExpandRatio(tbl_data, 3.0f);
		mainLayout.setExpandRatio(footerLayout, 0.2f);

		// commandLayout.setExpandRatio(cmdExecuteLock, 0.5f);

		if (!listnerAdded) {
			cmdExecuteLock.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					final progressThread pr = new progressThread();
					pr.start();
					// Channelplus3Application.getInstance().getMainWindow().open(
					// new ExternalResource(Channelplus3Application
					// .getInstance().getMainWindow().getURL()));

					cmdExecuteLock.setEnabled(false);
				}
			});
			cmdUnlock.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					Callback cb = new Callback() {

						public void onDialogResult(boolean resultIsYes) {
							if (resultIsYes) {
								try {
									utils
											.execSql(
													"begin period_unlock_to_close;end;",
													con);
									con.commit();
									initForm();
								} catch (SQLException e) {
									e.printStackTrace();

									try {
										con.rollback();
									} catch (SQLException e1) {
										e1.printStackTrace();
									}

								}

							}

						}

					};
					parentLayout.getWindow().addWindow(
							new YesNoDialog("Confirmation Dialog",
									"Are you sure you want to unlock upto "
											+ txtLastClosed.getValue(), cb));

				}
			});
			listnerAdded = true;
		}

	}

	public void init() {

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		if (data_details.getDbclass() == null) {
			try {
				data_details.createDBClassFromConnection(con);
			} catch (SQLException e) {

			}
		}

		load_data();

	}

	public void load_data() {
		try {
			PreparedStatement pst = con
					.prepareStatement(
							"select to_date(nvl(repair.getsetupvalue_2('PERIOD_CLOSE_DATE'),'01/01/2000') ,'dd/mm/rrrr'),"
									+ "to_date(nvl(repair.getsetupvalue_2('PERIOD_LOCK_DATE'),'01/01/2000') ,'dd/mm/rrrr') from dual",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ResultSet rst = pst.executeQuery();
			rst.first();
			((Date) txtLastClosed.getValue()).setTime(rst.getDate(1).getTime());
			((Date) txtLastLocked.getValue()).setTime(rst.getDate(2).getTime());
			((Date) txtUntilDate.getValue())
					.setTime(System.currentTimeMillis());
			chkLock.setValue(Boolean.FALSE);
			chkCost.setValue(Boolean.TRUE);
			chkOnlyQty.setValue(Boolean.FALSE);

			chkRepairData.setValue(Boolean.TRUE);
			compProgres.setValue(1.0f);
			compProgres.setPollingInterval(1000);
			compProgres.setVisible(false);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fetch_details() throws SQLException {
		data_details.clearALl();
		String sq = " select field1 kind,field2 arabic_kind,field3 descr, field4 ref,to_number(field5) pos,"
				+ " field6 suggest,field9 proc,field10 para1, field11 para2, field12 para3 , "
				+ " field13 para4, field14 para5 "
				+ " from temporary where idno=66101 and usernm='REPOST' order by to_number(field5)";
		data_details.executeQuery(sq, true);
		data_details.setMasterRowStatusAll(Row.ROW_QUERIED);
		fill_details();
	}

	public void fill_details() throws SQLException {
		tbl_data.removeAllItems();
		utilsVaadin.applyColumns("ADMIN.LOCK_UNLOCK_PERIOD", tbl_data,
				lstItemCols, con);
		utilsVaadin.query_data2(tbl_data, data_details, lstItemCols);
		tbl_data.setMultiSelect(true);
	}

	public void print_data() {

	}

	public void save_data() {

	}

	public void showInitView() {

		initForm();
	}

	class progressThread extends Thread {
		double current = 0.0;

		public void run() {
			tbl_data.focus();
			compProgres.setVisible(true);
			chkCost.setEnabled(false);
			chkLock.setEnabled(false);
			chkOnlyQty.setEnabled(false);
			chkRepairData.setEnabled(false);
			txtUntilDate.setEnabled(false);
			cmdUnlock.setEnabled(false);

			compProgres.setPollingInterval(500);
			compProgres.setValue(0);
			txtWorking.setCaption("Auditing started..");

			try {
				con.setAutoCommit(false);
				QueryExe qe1 = new QueryExe(
						"begin audit_data( :UNTIL_DATE ); end;", con);
				qe1.setParaValue("UNTIL_DATE", txtUntilDate.getValue());
				qe1.execute();
				con.commit();
				fetch_details();
				compProgres.setVisible(true);
				txtWorking.setCaption("Data fetched..");
				QueryExe qe = new QueryExe(con);
				if (chkRepairData.booleanValue()) {
					for (int i = 0; i < data_details.getRows().size(); i++) {
						if (data_details.getFieldValue(i, "PROC") != null
								&& data_details.getFieldValue(i, "PROC")
										.toString().length() > 0) {
							txtWorking.setCaption("executing .."
									+ (String) data_details.getFieldValue(i,
											"DESCR"));
							String pr = (String) data_details.getFieldValue(i,
									"PROC");
							qe.setSqlStr(pr);
							if (data_details.getFieldValue(i, "PARA1") != null) {
								qe.setParaValue("PARA1", data_details
										.getFieldValue(i, "PARA1"));
							}
							if (data_details.getFieldValue(i, "PARA2") != null) {
								qe.setParaValue("PARA2", data_details
										.getFieldValue(i, "PARA2"));
							}
							if (data_details.getFieldValue(i, "PARA3") != null) {
								qe.setParaValue("PARA3", data_details
										.getFieldValue(i, "PARA3"));
							}
							if (data_details.getFieldValue(i, "PARA4") != null) {
								qe.setParaValue("PARA4", data_details
										.getFieldValue(i, "PARA4"));
							}
							if (data_details.getFieldValue(i, "PARA5") != null) {
								qe.setParaValue("PARA5", data_details
										.getFieldValue(i, "PARA5"));
							}
							qe.execute(true);
							qe.close();
						}
					}
					txtWorking.setCaption("Reposting complete..");
					utils.execSql("begin repost_complete; end;", con);
				}
				con.commit();
				if (tbl_data.size() <= 0) {
					txtWorking
							.setCaption("Completed Successfully , Credits to Yusuf Jiruwala (System Architecture)..");
				} else {
					txtWorking.setCaption("Exception found # "
							+ tbl_data.size() + "");
				}
				if (chkLock.booleanValue()) {
					Callback cb = new Callback() {

						public void onDialogResult(boolean resultIsYes) {
							if (resultIsYes) {
								QueryExe qe = new QueryExe(
										"begin period_lock( :LOCK_DATE );end;",
										con);
								try {
									qe.setParaValue("LOCK_DATE", txtUntilDate
											.getValue());
									qe.execute();
									qe.close();
									con.commit();
									initForm();
								} catch (SQLException e) {
									e.printStackTrace();
									txtWorking.setCaption(e.getMessage());
								}
							}

						}
					};
					parentLayout.getWindow().addWindow(
							new YesNoDialog("Confirmation",
									"Do you want to Lock untill - "
											+ ((Date) txtUntilDate.getValue()),
									cb));
				}

				/*
				 * QueryExe qe1=new
				 * QueryExe("begin audit_data( :UNTIL_DATE ); end;", con);
				 * qe1.setParaValue("UNTIL_DATE", txtUntilDate.getValue());
				 * qe1.execute();
				 */

			} catch (SQLException e) {
				try {
					txtWorking.setCaption(e.getMessage());
					con.rollback();
				} catch (SQLException e1) {
				}
				e.printStackTrace();

			}

			compProgres.setValue(1);
			cmdExecuteLock.setEnabled(true);
			chkCost.setEnabled(true);
			chkLock.setEnabled(true);
			chkOnlyQty.setEnabled(true);
			chkRepairData.setEnabled(true);
			txtUntilDate.setEnabled(true);
			cmdUnlock.setEnabled(true);

		}
	}
}
