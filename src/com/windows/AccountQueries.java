package com.windows;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.doc.views.Query.QueryView;
import com.doc.views.Query.tableDataListner;
import com.generic.DynamicReportSetting;
import com.generic.Parameter;
import com.generic.localTableModel;
import com.generic.qryColumn;
import com.generic.utils;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Window.Notification;

public class AccountQueries {
	private AbstractLayout parentLayout = null;

	public AbstractLayout getParentLayout() {
		return parentLayout;
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	
	public void showTrialbalance(AbstractLayout abs) {
		this.parentLayout = abs;
		showTrialbalance();
	}

	public void showTrialbalance() {
		AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		try {
			Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
					.getDbConnection().setAutoCommit(false);
			final QueryView qv = new QueryView(Channelplus3Application
					.getInstance().getFrmUserLogin().getDbc().getDbConnection());
			qv.setBeforeQueryExecSql("begin " + " cp_acc.plevelno:=:levelno;"
					+ "cp_acc.pfromdt:=:fromdate;" + "cp_acc.ptodt:=:todate;"
					+ "cp_acc.pfromacc:=:paccno;"
					+ "cp_acc.build_gl('01');commit;end;");

			qv
					.setSqlquery("select g.accno,acname,bdr_bal,bcr_bal,"
							+ "tdr_bal,tcr_bal,edr_bal,ecr_bal,g.levelno,g.childcount,path from cp_gl_1 g where "
							+ "idno=66601 and g.usernm='01' order by g.path");

			qv.setHideCols(new String[] { "LEVELNO", "CHILDCOUNT", "PATH" });

			Parameter pm = new Parameter("paccno", "1");
			pm.setLovsql("select accno,name from acaccount "
					+ "where actype=0 and childcount>0 ORDER BY PATH");
			qv.addParameter(pm);
			pm = new Parameter("levelno", "0");
			qv.addParameter(pm);

			pm = new Parameter("fromdate");
			pm.setDescription("From date");
			pm.setValueType(Parameter.DATA_TYPE_DATE);
			qv.addParameter(pm);

			pm = new Parameter("todate");
			pm.setDescription("To date");
			pm.setValueType(Parameter.DATA_TYPE_DATE);
			qv.addParameter(pm);

			centralPanel.addComponent(qv);
			qv.getTable().setSizeFull();
			qv.getDataListners().add(new tableDataListner() {
				public String calcSummary(List<String> qcGroup, qryColumn qc) {
					double f = 0;
					if (qc.getColname().toUpperCase().equals("LEVELNO")) {
						f = qv.getLctb().getSummaryOf("LEVELNO",
								localTableModel.SUMMARY_SUM);
					}
					return String.valueOf(f);
				}

				public void beforeQuery() {

				}

				public void afterQuery() {
					qv.getLctb().getColByName("TDR_BAL").setNumberFormat(
							Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb().getColByName("BDR_BAL").setNumberFormat(
							Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb().getColByName("BCR_BAL").setNumberFormat(
							Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb().getColByName("TCR_BAL").setNumberFormat(
							Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb().getColByName("EDR_BAL").setNumberFormat(
							Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
					qv.getLctb().getColByName("ECR_BAL").setNumberFormat(
							Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);

				}

				public String getCellStyle(qryColumn qc, int recordNo) {
					String st = "";

					// if (qc.getColname().toUpperCase().endsWith("_BAL")) {
					// st = "rightalign";
					// }

					if (recordNo >= 0
							&& recordNo < qv.getLctb().getRows().size()
							&& ((BigDecimal) qv.getLctb().getFieldValue(
									recordNo, "CHILDCOUNT")).floatValue() > 0) {
						st = st + " v-table-cell-content-trialbalMain";
					}
					return st;
				}

				public void afterVisual() {

				}
			});
			qv.reportSetting.setTitle("Trial Balance");
			qv.reportSetting.setOrientation(DynamicReportSetting.LANDSCAPE);
			qv.createView();
			centralPanel.requestRepaintAll();

		} catch (SQLException e) {
			Logger.getLogger(frmMain.class.getName())
					.log(Level.SEVERE, null, e);
			centralPanel.getWindow().showNotification("Error loading report ",
					e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		}

	}
}
