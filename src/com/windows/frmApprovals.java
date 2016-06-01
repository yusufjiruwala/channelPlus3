package com.windows;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.doc.views.Query.QueryView;
import com.doc.views.Query.tableDataListner;
import com.generic.Parameter;
import com.generic.ResourceManager;
import com.generic.qryColumn;
import com.generic.transactionalForm;
import com.generic.utils;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class frmApprovals implements transactionalForm {
	Connection con = null;
	private AbstractLayout parentLayout = null;
	String fl = "";
	private Panel panel = new Panel();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private CustomLayout layOut = null;

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void createView() {

	}

	public void init() {

	}

	public void initForm() {

	}

	public void load_data() {
		// TODO Auto-generated method stub

	}

	public void print_data() {
		// TODO Auto-generated method stub

	}

	public void save_data() {

	}

	public void resetFormLayout() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		commandLayout.removeAllComponents();
		centralPanel.setHeight("-1");
		centralPanel.setWidth("100%");
		centralPanel.setHeight("100%");
		System.gc();

	}

	public void showInitView() {
		showUsersApprovalCounts();
	}

	public void showUsersApprovalCounts() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		fl = ((WebApplicationContext) Channelplus3Application.getInstance()
				.getContext())
				.getHttpSession()
				.getServletContext()
				.getRealPath(
						"/VAADIN/themes/"
								+ Channelplus3Application.getInstance()
										.getTheme());
		fl = fl.replace("\\", "/");
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		resetFormLayout();
		try {
			final QueryView qv = new QueryView(con);
			qv.setSqlquery("select keyfld,posted_user,posted_date_time Posted_Time,kind, action_type, user_descr1,long_data from approval_all where reaction='STAND_BY'"
					+ " and (posted_user=:USERNM or nvl(:USERNM ,'ALL') ='ALL') order by posted_date_time asc");
			qv.doExecuteOnParameterChange(true);
			Parameter pm = new Parameter("USERNM");
			pm.setDescription("User");
			pm.setLovsql("select distinct posted_user,posted_user u2 from approval_all where reaction='STAND_BY'");

			qv.addParameter(pm);
			qv.reportSetting.doStandard();
			qv.reportSetting.setTitle("Approval Management");
			qv.setSearchField(false);
			qv.setShowCommandPanel(false);
			qv.getListHideCols().add("KEYFLD");
			qv.getListHideCols().add("LONG_DATA");

			qv.getDataListners().add(new tableDataListner() {
				public String getCellStyle(qryColumn qc, int recordNo) {
					return null;
				}

				public String calcSummary(List<String> qcGroup, qryColumn qc) {
					return "";
				}

				public void beforeQuery() {

				}

				public void afterVisual() {
					qv.getTable().setImmediate(true);
					qv.getTable().addListener(new ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {
							if (qv.getTable().getValue() == null
									|| !(qv.getTable().getValue() instanceof Integer)
									|| ((Integer) qv.getTable().getValue()) < 0) {
								return;
							}

							int rowno = ((Integer) qv.getTable().getValue());
							String s = qv.getLctb()
									.getFieldValue(rowno, "LONG_DATA")
									.toString();
							try {
								panel.removeAllComponents();
								layOut = new CustomLayout(
										new ByteArrayInputStream(s
												.getBytes("UTF-8")));
								panel.addComponent(layOut);								
								System.gc();
								/*
								 * Channelplus3Application .getInstance()
								 * .getMainWindow() .open( new ExternalResource(
								 * Channelplus3Application .getInstance()
								 * .getMainWindow() .getURL()));
								 */
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					});
					qv.getTable().setValue(qv.getTable().firstItemId());

				}

				public void afterQuery() {

				}
			});
			/*
			 * CustomLayout cl = new CustomLayout("temp"); cl.setSizeFull();
			 */
			// centralPanel.addComponent(cl);
			qv.createView();
			layOut = new CustomLayout(new ByteArrayInputStream(
					"<br><br>".getBytes()));
			ResourceManager.addComponent(centralPanel, qv);
			ResourceManager.addComponent(centralPanel, commandLayout);
			ResourceManager.addComponent(centralPanel, panel);
			ResourceManager.addComponent(panel, layOut);
			qv.setSizeFull();
			panel.setSizeFull();
			layOut.setSizeFull();
			centralPanel.setExpandRatio(panel, 2.4f);
			centralPanel.setExpandRatio(commandLayout, 0.1f);
			centralPanel.setExpandRatio(qv, 1.5f);

			Button but = new Button("Approv");
			ResourceManager.addComponent(commandLayout, but);
			but.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					if (qv.getTable().getValue() == null
							|| !(qv.getTable().getValue() instanceof Integer)
							|| ((Integer) qv.getTable().getValue()) < 0) {
						return;
					}					
					int rowno = ((Integer) qv.getTable().getValue());
					String s = qv.getLctb().getFieldValue(rowno, "LONG_DATA")
							.toString();
					String keyid = qv.getLctb().getFieldValue(rowno, "KEYFLD")
							.toString();					
					try {
						panel.removeAllComponents();
						layOut = new CustomLayout(new ByteArrayInputStream(
								"<br><br>".getBytes("UTF-8")));
						panel.addComponent(layOut);
						utils.execSql(
								"update approval_all set "
										+ "reaction='APPROVED' , approved_date_time=sysdate,"
										+ "approved_user_by='" + utils.CPUSER
										+ "'  where keyfld=" + keyid, con);						
						con.commit();						
						qv.getWindow()
								.showNotification("Approved successfully");
						showUsersApprovalCounts();
					} catch (Exception e) {
						e.printStackTrace();
						try {
							con.rollback();
						} catch (SQLException e1) {
						}
					}
					System.gc();
				}
			});			
			but = new Button("Cancel Approv");
			ResourceManager.addComponent(commandLayout, but);
			but.addListener(new ClickListener() {
				
				public void buttonClick(ClickEvent event) {
					if (qv.getTable().getValue() == null
							|| !(qv.getTable().getValue() instanceof Integer)
							|| ((Integer) qv.getTable().getValue()) < 0) {
						return;
					}
					int rowno = ((Integer) qv.getTable().getValue());
					String s = qv.getLctb().getFieldValue(rowno, "LONG_DATA")
							.toString();
					String keyid = qv.getLctb().getFieldValue(rowno, "KEYFLD")
							.toString();
					try {
						panel.removeAllComponents();
						layOut = new CustomLayout(new ByteArrayInputStream(
								"<br><br>".getBytes("UTF-8")));
						panel.addComponent(layOut);
						utils.execSql("update approval_all set "
								+ "reaction='CANCELLED' ,"
								+ "approved_date_time=sysdate,"
								+ "approved_user_by='" + utils.CPUSER
								+ "'  where keyfld=" + keyid, con);
						con.commit();
						qv.getWindow().showNotification("Approved Cancelled");
						showUsersApprovalCounts();
					} catch (Exception e) {
						e.printStackTrace();
						try {
							con.rollback();
						} catch (SQLException e1) {
						}
					}
					System.gc();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void writeToFile(String s, String filenm) throws IOException {
		OutputStream fout = new FileOutputStream(filenm);
		OutputStreamWriter out = new OutputStreamWriter(fout, "UTF8");
		out.write(s);
		out.close();

	}
}
