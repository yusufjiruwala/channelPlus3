package com.windows.clinics;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.doc.views.TableView;
import com.doc.views.YesNoDialog;
import com.doc.views.YesNoDialog.Callback;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.FormLayoutManager;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.SecurityParameter;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.utilsVaadinPrintHandler;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.windows.frmMainMenus;
import com.windows.Masters.Purchase.frmPurchaseCost;

public class frmClinicMain implements transactionalForm {

	private Connection con = null;
	private localTableModel data_visit = new localTableModel();
	private localTableModel data_old_medi = new localTableModel();
	private localTableModel data_medical_items = new localTableModel();
	private Map<String, String> mapActionStrs = new HashMap<String, String>();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout cmdvisitLayout = new HorizontalLayout();
	private HorizontalLayout cmdMediLayout = new HorizontalLayout();
	private HorizontalLayout cmdOldMediLayout = new HorizontalLayout();
	private HorizontalLayout cmdMediLayout1 = new HorizontalLayout();
	private HorizontalLayout cmdOldMediLayout1 = new HorizontalLayout();
	private HorizontalLayout cmdvisitLayout1 = new HorizontalLayout();

	private HorizontalLayout basicLayout = new HorizontalLayout();
	private HorizontalLayout basic1Layout = new HorizontalLayout();
	private VerticalLayout basic2Layout = new VerticalLayout();

	private HorizontalLayout detailsLayout = new HorizontalLayout();
	private VerticalLayout details1Layout = new VerticalLayout();
	private VerticalLayout details2Layout = new VerticalLayout();

	private NativeButton cmdAddMedi = ControlsFactory.CreateToolbarButton(
			"img/newmenu.png", "Add medicine here");
	private NativeButton cmdPatientBasic = ControlsFactory.CreateToolbarButton(
			"img/patient.png", "Open Patient file");

	private NativeButton cmdDelMedi = ControlsFactory.CreateToolbarButton(
			"img/remove.png", "Remove selected medicine");
	private NativeButton cmdRefreshMedi = ControlsFactory.CreateToolbarButton(
			"img/revert.png", "Refresh data table..");

	private NativeButton cmdAddvisit = ControlsFactory.CreateToolbarButton(
			"img/newmenu.png", "Add new visiting..");
	private NativeButton cmdManyAdd = ControlsFactory.CreateToolbarButton(
			"img/manyadd.png", "Add other visiting..");

	private NativeButton cmdDelvisit = ControlsFactory.CreateToolbarButton(
			"img/remove.png", "Remove selected visiting recs");
	private NativeButton cmdRefreshvisit = ControlsFactory.CreateToolbarButton(
			"img/revert.png", "Refresh data table..");
	private ComboBox txtDrNo = ControlsFactory.CreateListField("Doctor",
			"DRNO", "select no,name from salesp where type='DR' order by no",
			null);
	private DateField txtTodayDate = ControlsFactory.CreateDateField("Date",
			"DT", null);
	private NativeButton cmdPatient = ControlsFactory.CreateToolbarButton(
			"img/board.png", "Open patient medical records..");
	private NativeButton cmdPost = ControlsFactory.CreateToolbarButton(
			"img/ok.png", "Post & Generate Invoice");
	private NativeButton cmdPrintVisit = ControlsFactory.CreateToolbarButton(
			"img/print.png", "Post & Generate Invoice");
	private NativeButton cmdPrintOrder = ControlsFactory.CreateToolbarButton(
			"img/print.png", "Post & Generate Invoice");
	private NativeButton cmdUpdvisit = ControlsFactory.CreateToolbarButton(
			"img/edit.png", "Update visiting..");

	private NativeButton cmdActive = ControlsFactory.CreateToolbarButton(
			"img/point.png", "Active/Deactivate....");

	private NativeButton cmdNewPatient = new NativeButton("New Patient");
	private Table tbl_old_med = new Table();
	private Table tbl_visit = new Table();
	private Table tbl_medical_items = new Table();

	private String QRYSES = "";
	private boolean listnerAdded = false;
	private List<ColumnProperty> lstvisitCols = new ArrayList<ColumnProperty>();
	private List<ColumnProperty> lstMediCols = new ArrayList<ColumnProperty>();
	private List<ColumnProperty> lstOldMediCols = new ArrayList<ColumnProperty>();

	public frmClinicMain() {
		txtTodayDate.setValue(new java.util.Date(System.currentTimeMillis()));
		init();
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		basic1Layout.removeAllComponents();
		basic2Layout.removeAllComponents();
		details1Layout.removeAllComponents();
		details2Layout.removeAllComponents();
		detailsLayout.removeAllComponents();

		cmdvisitLayout.removeAllComponents();
		cmdMediLayout.removeAllComponents();
		cmdOldMediLayout.removeAllComponents();

		tbl_visit.removeAllItems();
		tbl_medical_items.removeAllItems();
		tbl_old_med.removeAllItems();

		data_visit.clearALl();
		data_medical_items.clearALl();
		data_old_medi.clearALl();

	}

	public void createView() {

		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();

		// centralPanel.setSizeFull();
		// mainLayout.se
		// basicLayout.setSizeFull();

		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		txtDrNo.setWidth("100px");
		basicLayout.setHeight("100%");
		detailsLayout.setHeight("100%");
		cmdvisitLayout.setSizeFull();
		cmdMediLayout.setSizeFull();
		cmdOldMediLayout.setSizeFull();
		cmdvisitLayout1.setHeight("100%");
		cmdMediLayout1.setHeight("100%");
		cmdOldMediLayout1.setHeight("100%");

		basic1Layout.setWidth("100%");
		basicLayout.setSizeFull();
		details1Layout.setSizeFull();
		details2Layout.setSizeFull();
		basicLayout.setSizeFull();
		detailsLayout.setSizeFull();
		mainLayout.setSizeFull();
		centralPanel.setSizeFull();

		tbl_visit.setSizeFull();
		tbl_medical_items.setSizeFull();
		tbl_old_med.setSizeFull();

		txtDrNo.setImmediate(true);
		txtTodayDate.setImmediate(true);
		txtDrNo.setWidth("200px");
		txtTodayDate.setWidth("200px");

		tbl_visit.setSelectable(true);
		tbl_visit.setColumnReorderingAllowed(false);
		tbl_visit.setSortDisabled(true);
		tbl_medical_items.setSelectable(true);
		tbl_medical_items.setColumnReorderingAllowed(false);
		tbl_medical_items.setSortDisabled(true);
		tbl_old_med.setSelectable(true);
		tbl_old_med.setColumnReorderingAllowed(false);
		tbl_old_med.setSortDisabled(true);
		tbl_visit.setImmediate(true);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);
		ResourceManager.addComponent(mainLayout, detailsLayout);

		ResourceManager.addComponent(basicLayout, basic1Layout);
		ResourceManager.addComponent(basicLayout, basic2Layout);

		ResourceManager.addComponent(detailsLayout, details1Layout);
		ResourceManager.addComponent(detailsLayout, details2Layout);

		ResourceManager.addComponent(details1Layout, cmdvisitLayout);
		ResourceManager.addComponent(details2Layout, cmdMediLayout);
		ResourceManager.addComponent(cmdMediLayout, cmdMediLayout1);
		ResourceManager.addComponent(cmdvisitLayout, cmdvisitLayout1);
		ResourceManager.addComponent(cmdOldMediLayout, cmdOldMediLayout1);

		ResourceManager.addComponent(basic1Layout, txtTodayDate);
		ResourceManager.addComponent(basic1Layout, txtDrNo);

		ResourceManager.addComponent(basic2Layout, tbl_old_med);
		ResourceManager.addComponent(details1Layout, tbl_visit);
		ResourceManager.addComponent(details2Layout, tbl_medical_items);

		ResourceManager.addComponent(cmdvisitLayout1, cmdAddvisit);
		ResourceManager.addComponent(cmdvisitLayout1, cmdManyAdd);
		ResourceManager.addComponent(cmdvisitLayout1, cmdDelvisit);
		ResourceManager.addComponent(cmdvisitLayout1, cmdRefreshvisit);
		ResourceManager.addComponent(cmdvisitLayout1, cmdPatient);
		ResourceManager.addComponent(cmdvisitLayout1, cmdPatientBasic);
		ResourceManager.addComponent(cmdvisitLayout1, cmdPost);
		ResourceManager.addComponent(cmdvisitLayout1, cmdPrintVisit);
		ResourceManager.addComponent(cmdvisitLayout1, cmdUpdvisit);
		ResourceManager.addComponent(cmdvisitLayout1, cmdActive);

		ResourceManager.addComponent(cmdMediLayout1, cmdAddMedi);
		ResourceManager.addComponent(cmdMediLayout1, cmdDelMedi);
		ResourceManager.addComponent(cmdMediLayout1, cmdRefreshMedi);
		ResourceManager.addComponent(cmdMediLayout1, cmdPrintOrder);

		mainLayout.setExpandRatio(basicLayout, .5f);
		mainLayout.setExpandRatio(detailsLayout, 3.5f);

		detailsLayout.setExpandRatio(details1Layout, 2.8f);
		detailsLayout.setExpandRatio(details2Layout, 1.2f);

		details1Layout.setExpandRatio(cmdvisitLayout, .2f);
		details1Layout.setExpandRatio(tbl_visit, 3.8f);

		details2Layout.setExpandRatio(cmdMediLayout, .2f);
		details2Layout.setExpandRatio(tbl_medical_items, 3.8f);

		cmdvisitLayout.setStyleName("toolpanel");
		cmdMediLayout.setStyleName("toolpanel");

		cmdAddMedi.setEnabled(false);
		cmdDelMedi.setEnabled(false);

		if (!listnerAdded) {
			tbl_visit.addActionHandler(new Handler() {
				@Override
				public void handleAction(Action action, Object sender,
						Object target) {
					if (target == null)
						return;
					int rowno = Integer.valueOf(target.toString());
					double kfld = Double.valueOf(utils.nvl(
							data_visit.getFieldValue(rowno, "KEYFLD"), "-1"));
					String slv = "", slvM = "";
					try {
						slv = utils.nvl(QueryExe.getSqlValue(
								"select SALEINV FROM CLQ_VISITS WHERE KEYFLD='"
										+ kfld + "'", con, ""), "");
						slvM = utils.nvl(QueryExe.getSqlValue(
								"select SALEINV_MEDICAL FROM CLQ_VISITS WHERE KEYFLD='"
										+ kfld + "'", con, ""), "");
						if (action.getCaption().equals(
								mapActionStrs.get("unpost"))
								&& !(slv + slvM).isEmpty()) {
							unpost(kfld, rowno);
						}

						if (action.getCaption().equals(
								mapActionStrs.get("remove"))
								&& (slv + slvM).isEmpty()) {
							delete_visit(kfld, rowno);
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				}

				@Override
				public Action[] getActions(Object target, Object sender) {
					if (target == null)
						return null;
					int rowno = Integer.valueOf(target.toString());
					List<Action> acts = new ArrayList<Action>();

					if (rowno < 0)
						return null;
					if (data_visit.getFieldValue(rowno, "SALEINV") != null
							&& !data_visit.getFieldValue(rowno, "SALEINV")
									.toString().isEmpty())
						acts.add(new Action(mapActionStrs.get("unpost")));
					if (data_visit.getFieldValue(rowno, "SALEINV") == null
							|| data_visit.getFieldValue(rowno, "SALEINV")
									.toString().isEmpty())
						acts.add(new Action(mapActionStrs.get("remove")));
					Action[] ac_ar = new Action[acts.size()];
					return acts.toArray(ac_ar);

				}
			});

			cmdManyAdd.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					select_visit();
				}
			});

			cmdUpdvisit.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					add_update_visit(false);

				}
			});

			cmdActive.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					do_select();

				}
			});

			cmdPrintOrder.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (tbl_visit.getValue() == null
							|| ((Integer) tbl_visit.getValue()) < 0) {
						parentLayout.getWindow().showNotification(
								"Must select visited patient");
						return;
					}
					final Integer rn = (Integer) tbl_visit.getValue();

					try {
						if (data_visit.getFieldValue(rn, "ORD_NO") != null
								&& !data_visit.getFieldValue(rn, "ORD_NO")
										.toString().isEmpty()) {
							utilsVaadinPrintHandler.printClqOrder(data_visit
									.getFieldValue(rn, "ORD_NO").toString(),
									con);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});

			cmdPrintVisit.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (tbl_visit.getValue() == null
							|| ((Integer) tbl_visit.getValue()) < 0) {
						parentLayout.getWindow().showNotification(
								"Must select visited patient");
						return;
					}
					final Integer rn = (Integer) tbl_visit.getValue();
					String saleinv = utils.getSqlValue(
							"select saleinv||saleinv_medical from clq_visits where keyfld="
									+ data_visit.getFieldValue(rn, "KEYFLD"),
							con);

					if (!saleinv.isEmpty()) {
						parentLayout.getWindow().showNotification("",
								"Invoice not generated !");
					}
					try {
						utilsVaadinPrintHandler.printClqVoucher(data_visit
								.getFieldValue(rn, "KEYFLD").toString(), con);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			});

			tbl_visit.setCellStyleGenerator(new CellStyleGenerator() {

				public String getStyle(Object itemId, Object propertyId) {

					if (itemId != null) {
						Integer rn = (Integer) itemId;
						if (data_visit.getFieldValue(rn, "FLAG") != null
								&& data_visit.getFieldValue(rn, "FLAG")
										.toString().equals("1_ACTIVE")) {
							return "blinkrow";
						}

						if (data_visit.getFieldValue(rn, "PAY_KEYFLD") != null
								&& !data_visit.getFieldValue(rn, "PAY_KEYFLD")
										.toString().isEmpty()) {
							return "posted"
									+ data_visit.getFieldValue(rn, "KOV");
						} else {
							return "unposted"
									+ data_visit.getFieldValue(rn, "KOV");
						}
					}
					return null;
				}
			});

			cmdRefreshvisit.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					load_data();

				}
			});
			cmdRefreshMedi.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					load_medical_items();

				}
			});
			cmdPatient.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					// open_add_patient(false);
					open_visits();
				}
			});
			cmdPatientBasic.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

					open_add_patient(false);
				}
			});
			txtDrNo.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					load_data();
				}

			});
			txtTodayDate.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					load_data();

				}
			});
			cmdAddvisit.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					// select_visit();
					add_update_visit(true);

				}
			});

			tbl_visit.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					load_medical_items();
				}
			});
			cmdAddMedi.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					add_medical_item();

				}
			});
			cmdPost.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						show_payment_method2(false);
					} catch (Exception e) {
						e.printStackTrace();
						parentLayout.getWindow()
								.showNotification("", e.getMessage(),
										Notification.TYPE_ERROR_MESSAGE);
					}
				}
			});
			cmdDelvisit.addListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (tbl_visit.getValue() == null) {
						return;
					}

					int rowno = Integer.valueOf(tbl_visit.getValue() + "");
					double kfld = Double.valueOf(utils.nvl(
							data_visit.getFieldValue(rowno, "KEYFLD"), "-1"));
					String slv = "", slvM = "";
					try {
						slv = utils.nvl(QueryExe.getSqlValue(
								"select SALEINV FROM CLQ_VISITS WHERE KEYFLD='"
										+ kfld + "'", con, ""), "");
						slvM = utils.nvl(QueryExe.getSqlValue(
								"select SALEINV_MEDICAL FROM CLQ_VISITS WHERE KEYFLD='"
										+ kfld + "'", con, ""), "");
						if (!(slv + slvM).isEmpty())
							unpost(kfld, rowno);

						if ((slv + slvM).isEmpty())
							delete_visit(kfld, rowno);

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				}
			});
			cmdDelMedi.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					delete_medical_item();
				}
			});

			listnerAdded = true;
		}

		if (txtDrNo.getItemIds().size() > 0)
			txtDrNo.setValue(txtDrNo.getItemIds().toArray()[0]);
	}

	protected void do_select() {
		if (tbl_visit.getValue() == null
				|| ((Integer) tbl_visit.getValue()) < 0) {
			parentLayout.getWindow().showNotification("Must select  patient !");
			return;
		}

		final Integer rn = (Integer) tbl_visit.getValue();

		String saleinv = utils.getSqlValue(
				"select saleinv||saleinv_medical from clq_visits where keyfld="
						+ data_visit.getFieldValue(rn, "KEYFLD"), con);

		if (!saleinv.isEmpty()) {
			parentLayout.getWindow().showNotification(
					"Invoice created , can not remove medical items !",
					Notification.TYPE_ERROR_MESSAGE);
			return;
		}
		String curr = data_visit.getFieldValue(rn, "FLAG").toString();
		try {
			con.setAutoCommit(false);
			String s = (curr.equals("1_CREATED") ? "1_ACTIVE" : "1_CREATED");

			data_visit.setFieldValue(rn, "FLAG", s);
			QueryExe qe = new QueryExe(
					"begin update clq_visits set flag='1_CREATED' where date_of_arrival=:DT and drno=:DR ;"
							+ " update clq_visits set flag= :FLG where keyfld= :KF; end;",
					con);
			qe.setParaValue("DT", txtTodayDate.getValue());
			qe.setParaValue("KF", data_visit.getFieldValue(rn, "KEYFLD"));
			qe.setParaValue("FLG", s);
			qe.setParaValue("DR", txtDrNo.getValue());

			qe.execute();
			qe.close();
			con.commit();
			load_data();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
			}
		}
	}

	public void load_medical_items() {
		cmdAddMedi.setEnabled(false);
		cmdDelMedi.setEnabled(false);
		data_medical_items.getRows().clear();
		fill_medical_table();
		if (tbl_visit.getValue() != null) {
			cmdAddMedi.setEnabled(true);
			cmdDelMedi.setEnabled(true);
			fetch_medical_items();
		}

	}

	public void delete_medical_item() {
		if (tbl_visit.getValue() == null
				|| ((Integer) tbl_visit.getValue()) < 0) {
			parentLayout.getWindow().showNotification(
					"Must select visited patient");
			return;
		}

		final Integer rn = (Integer) tbl_visit.getValue();

		String saleinv = utils.getSqlValue(
				"select saleinv||saleinv_medical from clq_visits where keyfld="
						+ data_visit.getFieldValue(rn, "KEYFLD"), con);

		if (!saleinv.isEmpty()) {
			parentLayout.getWindow().showNotification(
					"Invoice created , can not remove medical items !",
					Notification.TYPE_ERROR_MESSAGE);
			return;
		}

		if (tbl_medical_items.getValue() == null) {
			parentLayout.getWindow().showNotification(
					"must select item to remove ! ",
					Notification.TYPE_ERROR_MESSAGE);
		}

		final Integer rnm = (Integer) tbl_medical_items.getValue();
		Callback cb = new Callback() {

			public void onDialogResult(boolean resultIsYes) {
				if (resultIsYes) {
					try {
						con.setAutoCommit(false);
						double posx = 0;
						String sq1 = "declare s number; begin"
								+ " delete from order2 where ord_no=:ORDER_NO and ord_code=111 and ord_pos=:ORD_POS;"
								+ " select nvl(sum((ord_price/ord_pack)*ord_allqty),0) into s from order2 where ord_code=111 and "
								+ " ord_no=:ORDER_NO; UPDATE CLQ_VISITS set ord_amt=s where keyfld=:VK ; "
								+ " end; ";
						if (data_medical_items.getRows().size() == 1) {
							sq1 = "begin delete from order2 where ord_no=:ORDER_NO and ord_code=111"
									+ " and (ord_pos=:ORD_POS or ord_pos is not null ); "
									+ "delete from order1 where ord_no=:ORDER_NO and ord_code=111 ; "
									+ "UPDATE CLQ_VISITS set ord_amt=0 where keyfld=:VK ;"
									+ "   end;";
						}

						QueryExe qe = new QueryExe(sq1, con);
						qe.setParaValue("ORDER_NO",
								data_visit.getFieldValue(rn, "ORD_NO"));

						qe.setParaValue("VK",
								data_visit.getFieldValue(rn, "KEYFLD"));
						qe.setParaValue("ORD_POS", data_medical_items
								.getFieldValue(rnm, "ORD_POS"));
						qe.execute();
						qe.close();
						con.commit();
						load_medical_items();
						double totamt = Double.valueOf(utils.getSqlValue(
								"select nvl(sum((ord_price/ord_pack)*ord_allqty),0) from "
										+ " order2 where ord_no="
										+ data_visit
												.getFieldValue(rn, "ORD_NO")
										+ " and ord_code=111", con));
						data_visit.setFieldValue(rn, "ORD_AMT",
								BigDecimal.valueOf(totamt));
						utilsVaadin.refreshRowFromData(data_visit, tbl_visit,
								rn, lstvisitCols);
					} catch (SQLException ex) {
						Logger.getLogger(frmPurchaseCost.class.getName()).log(
								Level.SEVERE, null, ex);
						parentLayout.getWindow().showNotification(
								"Error in deletion ", ex.toString(),
								Notification.TYPE_ERROR_MESSAGE);

						try {
							con.rollback();
						} catch (SQLException e) {
						}

					}

				}
			}
		};

		parentLayout.getWindow().addWindow(
				new YesNoDialog("Channel Plus Warning",
						"Delete medical Item \""
								+ data_medical_items
										.getFieldValue(rnm, "DESCR")
								+ "\" from selected patient ? ", cb));

	}

	public void open_visits() {
		if (tbl_visit.getValue() == null) {
			parentLayout.getWindow().showNotification("select patient first !",
					Notification.TYPE_ERROR_MESSAGE);
			return;
		}
		Integer rowno = (Integer) tbl_visit.getValue();
		Window wnd = ControlsFactory.CreateWindow("90%", "100%", false, true);
		Panel pnl = new Panel();
		pnl.setContent(new VerticalLayout());
		pnl.setSizeFull();
		pnl.getContent().setWidth("100%");
		pnl.getContent().setHeight("-1px");
		((VerticalLayout) pnl.getContent()).setMargin(true);
		wnd.setContent(pnl);
		frmVisitRecords v = new frmVisitRecords();
		v.setParentLayout((VerticalLayout) pnl.getContent());
		v.QRYSES = data_visit.getFieldValue(rowno, "KEYFLD").toString();
		v.showInitView();
	}

	public void open_add_patient(boolean addnew) {
		SecurityParameter sec = SecurityParameter.getInstanceOf("FORM",
				frmMainMenus.FORM_PATIENTS);
		if (!sec.canAccess) {
			utilsVaadin.showMessage("$SECURITY_ACCESS_DENIED");
			return;
		}
		if (!sec.canUpdates && !addnew) {
			utilsVaadin.showMessage("$SECURITY_SHOW_BEFORE_UPDATE");
			return;
		}
		if (!sec.canInsert && addnew) {
			utilsVaadin.showMessage("$SECURITY_SHOW_BEFORE_INSERT");
			return;
		}

		if (!addnew && tbl_visit.getValue() == null) {
			parentLayout.getWindow().showNotification("select patient first !",
					Notification.TYPE_ERROR_MESSAGE);
			return;
		}

		Integer rowno = (Integer) tbl_visit.getValue();
		Window wnd = ControlsFactory.CreateWindow("100%", "95%", false, true);
		Panel pnl = new Panel();
		pnl.setContent(new VerticalLayout());
		pnl.setSizeFull();
		pnl.getContent().setWidth("100%");
		pnl.getContent().setHeight("-1px");
		((VerticalLayout) pnl.getContent()).setMargin(true);
		wnd.setContent(pnl);
		frmPatients pt = new frmPatients();
		pt.setParentLayout((VerticalLayout) pnl.getContent());
		pt.showInitView();
		if (!addnew && pt.sec_para.canUpdates) {
			pt.QRYSES = data_visit.getFieldValue(rowno, "MEDICAL_NO")
					.toString();
			pt.load_data();
		}
	}

	protected void delete_data() {

	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {
			utilsVaadin.applyColumns("CLQ.ARRIVALS", tbl_visit, lstvisitCols,
					con);
			utilsVaadin.applyColumns("CLQ.ARRIVALS_M", tbl_medical_items,
					lstMediCols, con);
			utilsVaadin.applyColumns("CLQ.ARRIVALS_O", tbl_old_med,
					lstOldMediCols, con);

			mapActionStrs.put("unpost", "Un-post..");
			mapActionStrs.put("remove", "Remove Visit..");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();

		try {
			if (data_visit.getDbclass() == null) {
				data_visit.createDBClassFromConnection(con);
				data_medical_items.createDBClassFromConnection(con);
				data_old_medi.createDBClassFromConnection(con);
			}

		} catch (SQLException e) {

		}

		createView();
		load_data();
	}

	public void load_data() {
		if (txtTodayDate.getValue() == null) {
			parentLayout.getWindow().showNotification("",
					"Must date have value ..", Notification.TYPE_ERROR_MESSAGE);
			return;
		}

		if (txtDrNo.getValue() != null) {
			QRYSES = ((dataCell) txtDrNo.getValue()).getValue().toString();
		} else {
			QRYSES = "";
		}

		data_visit.clearALl();
		data_medical_items.clearALl();
		data_old_medi.clearALl();

		tbl_visit.removeAllItems();
		tbl_medical_items.removeAllItems();
		tbl_old_med.removeAllItems();

		try {
			fetch_visits();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}
	}

	public void fetch_visits() {
		try {
			data_visit
					.executeQuery(
							"SELECT V.*,p.e_first_nm||' '||e_second_nm||' '||e_family_nm PATIENTS_NAME,"
									+ " T.DESCR INV_TYPEDESCR_P, TM.DESCR INV_TYPEDESCR_M,"
									+ " i.descr FOLLOWUP_ITEM_DESCR,p.location_code, p.invoice_type , "
									+ " v.followup_disc, (v.FOLLOWUP_FEES - v.followup_disc)+V.ORD_AMT  net_amt,"
									+ " pay_amt_1+pay_amt_2 total_payment "
									+ "from clq_visits V,CLQ_PATIENTS P,invoicetype t,invoicetype tm,items i WHERE "
									+ " V.MEDICAL_NO=P.MEDICAL_NO and v.LOCATION_CODE=t.LOCATION_CODE AND "
									+ " v.drno='"
									+ ((txtDrNo.getValue()) == null ? ""
											: ((dataCell) txtDrNo.getValue())
													.getValue().toString())
									+ "' and v.date_of_arrival ="
									+ utils.getOraDateValue((Date) txtTodayDate
											.getValue())
									+ "  and v.FOLLOWUP_INV_TYPE=T.NO and "
									+ " V.MEDICAL_INV_TYPE=TM.NO AND "
									+ " i.reference=V.FOLLOWUP_ITEM order by v.time_of_arrival",
							true);
			fill_visits_table();
		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(
					"Error fetching data visits table..", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void add_medical_item() {
		if (tbl_visit.getValue() == null
				|| ((Integer) tbl_visit.getValue()) < 0) {
			parentLayout.getWindow().showNotification(
					"Must select visited patient");
			return;
		}

		final Integer rn = (Integer) tbl_visit.getValue();

		String saleinv = utils.getSqlValue(
				"select saleinv||saleinv_medical from clq_visits where keyfld="
						+ data_visit.getFieldValue(rn, "KEYFLD"), con);

		if (!saleinv.isEmpty()) {
			parentLayout.getWindow().showNotification(
					"Invoice created , can not add medical items !",
					Notification.TYPE_ERROR_MESSAGE);
			return;
		}
		final Window wnd = ControlsFactory.CreateWindow("500px", "250px", true,
				true);

		wnd.setCaption(data_visit.getFieldValue(rn, "PATIENTS_NAME").toString());

		HorizontalLayout pt = new HorizontalLayout();
		HorizontalLayout pt1 = new HorizontalLayout();
		HorizontalLayout bts = new HorizontalLayout();
		final TextField txtItemCode = new TextField("Item code");
		final TextField txtItemName = new TextField("Item Descr");
		final TextField txtPrice = new TextField("Price");
		final TextField txtQty = new TextField("Qty");
		final TextField txtAmount = new TextField("Amount");
		final Double varPrice = Double.valueOf(0);

		NativeButton bt = ControlsFactory.CreateToolbarButton("img/find.png",
				"");

		Button btAdd = new Button("Add");
		Button btClose = new Button("Close");
		txtPrice.setImmediate(true);
		txtQty.setImmediate(true);
		txtQty.setValue("1");
		txtPrice.setValue("0.000");
		txtPrice.setStyleName("netAmtStyle");
		txtQty.setStyleName("netAmtStyle");

		((VerticalLayout) wnd.getContent()).setMargin(true);
		((VerticalLayout) wnd.getContent()).setSpacing(true);
		ResourceManager.addComponent(wnd.getContent(), pt);
		ResourceManager.addComponent(pt, txtItemCode);
		ResourceManager.addComponent(pt, bt);
		ResourceManager.addComponent(pt, txtItemName);
		ResourceManager.addComponent(wnd.getContent(), pt1);
		ResourceManager.addComponent(pt1, txtPrice);
		ResourceManager.addComponent(pt1, txtQty);
		ResourceManager.addComponent(pt1, txtAmount);
		ResourceManager.addComponent(wnd.getContent(), bts);
		ResourceManager.addComponent(bts, btAdd);
		ResourceManager.addComponent(bts, btClose);

		pt.setComponentAlignment(bt, Alignment.BOTTOM_CENTER);

		ValueChangeListener vlc = new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if (txtPrice.getValue() == null
						|| txtPrice.getValue().toString().isEmpty()) {
					txtPrice.setValue("0.000");
				}
				DecimalFormat dc = new DecimalFormat(Channelplus3Application
						.getInstance().getFrmUserLogin().FORMAT_MONEY);
				double pr = Double.valueOf(txtPrice.getValue().toString());
				double qt = Double.valueOf(txtQty.getValue().toString());
				double amt = pr * qt;
				txtPrice.setValue(dc.format(pr));
				txtAmount.setValue(dc.format(amt));
			}
		};

		txtPrice.addListener(vlc);
		txtQty.addListener(vlc);

		bt.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					final Window wnd2 = ControlsFactory.CreateWindow("70%",
							"70%", true, true);

					utilsVaadin.showSearch(
							(VerticalLayout) wnd2.getContent(),
							new TableView.SelectionListener() {
								public void onSelection(TableView tv) {
									if (tv.getSelectionValue() < 0) {
										return;
									}
									txtItemCode.setReadOnly(false);
									txtItemName.setReadOnly(false);

									txtItemCode.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"REFERENCE"));
									txtItemName.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"DESCR"));
									txtPrice.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"PRICE1"));
									txtItemCode.setReadOnly(true);
									txtItemName.setReadOnly(true);

									if (Channelplus3Application.getInstance()
											.getMainWindow().getChildWindows()
											.contains(wnd2)) {
										Channelplus3Application.getInstance()
												.getMainWindow()
												.removeWindow(wnd2);
									}

								}
							}, con,
							"select reference,descr,price1 from items where childcounts=0"
									+ " and itprice4=0 order by descr2 ", true);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
		btAdd.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if (tbl_visit.getValue() == null
						|| ((Integer) tbl_visit.getValue()) < 0) {
					return;
				}
				int rowno = ((Integer) tbl_visit.getValue());
				double pr = Double.valueOf(txtPrice.getValue().toString());
				double qt = Double.valueOf(txtQty.getValue().toString());
				double amt = pr * qt;

				double on = -1;
				String sqlIns = "insert into order1(PERIODCODE,LOCATION_CODE,ORD_NO,ORD_CODE,ORD_TYPE,ORD_DISCAMT,ORD_SHPDT,"
						+ "LCNO, ORD_EMPNO, REMARKS, SALEINV , LAST_MODIFIED_TIME ,ORD_FLAG ,ORD_DATE ,ORD_REF, ORD_REFNM ,ORD_AMT)  VALUES "
						+ "( :PERIODCODE,:LOCATION_CODE,:ORD_NO , :ORD_CODE , :ORD_TYPE, :ORD_DISCAMT, :ORD_SHPDT, "
						+ " :LCNO, :ORD_EMPNO, :REMARKS, :SALEINV , :LAST_MODIFIED_DATE , :ORD_FLAG , :ORD_DATE, :ORD_REF, :ORD_REFNM, :ORD_AMT )";

				String sqlItems = "insert into order2 (PERIODCODE,LOCATION_CODE, ORD_DATE, ORD_NO,ORD_CODE,ORD_TYPE,ORD_POS,ORD_REFER,DESCR,ORD_PRICE,"
						+ "ORD_PKQTY, ORD_ALLQTY, ORD_PACK, ORD_PACKD, ORD_UNITD, ORD_FLAG ,SALEINV) VALUES "
						+ "(:PERIODCODE,:LOCATION_CODE, :ORD_DATE, :ORD_NO, :ORD_CODE,:ORD_TYPE,"
						+ "(select nvl(max(ord_pos),0)+1 from order2 where ord_code=111 and ord_no=:ORD_NO and location_code=:LOCATION_CODE) ,"
						+ ":ORD_REFER,:DESCR,:ORD_PRICE,"
						+ ":ORD_PKQTY, :ORD_ALLQTY, :ORD_PACK, :ORD_PACKD, :ORD_UNITD, :ORD_FLAG ,:SALEINV)";

				try {
					con.setAutoCommit(false);
					String pcod = utils
							.getSqlValue(
									"select repair.getsetupvalue_2('CURRENT_PERIOD') from dual",
									con);
					String loc = data_visit.getFieldValue(rowno,
							"LOCATION_CODE").toString();

					if (data_visit.getFieldValue(rowno, "ORD_NO") != null
							&& !data_visit.getFieldValue(rowno, "ORD_NO")
									.toString().isEmpty()) {
						on = Double.valueOf(data_visit.getFieldValue(rowno,
								"ORD_NO").toString());
					} else {
						on = Double.valueOf(utils.getSqlValue(
								"select nvl(max(ord_no),0)+1 from order1 "
										+ "where ord_code=111 ", con));
						QueryExe qe = new QueryExe(sqlIns, con);
						qe.setParaValue("PERIODCODE", pcod);
						qe.setParaValue("LOCATION_CODE", loc);
						qe.setParaValue("ORD_NO", on);
						qe.setParaValue("ORD_CODE", 111);
						qe.setParaValue("ORD_DATE", txtTodayDate.getValue());
						qe.setParaValue("ORD_TYPE", 1);
						qe.setParaValue("ORD_DISCAMT", 0);
						qe.setParaValue("ORD_SHPDT", txtTodayDate.getValue());
						qe.setParaValue("ORDACC", data_visit.getFieldValue(
								rowno, "FOLLOWUP_ITEM"));
						qe.setParaValue("ORD_REF",
								data_visit.getFieldValue(rowno, "MEDICAL_NO"));
						qe.setParaValue("LCNO",
								data_visit.getFieldValue(rowno, "KEYFLD"));
						qe.setParaValue("ORD_REFNM", data_visit.getFieldValue(
								rowno, "PATIENTS_NAME"));
						qe.setParaValue("ORD_AMT", amt);
						qe.setParaValue("ORD_EMPNO", txtDrNo.getValue());
						qe.setParaValue("REMARKS", "");
						qe.setParaValue("SALEINV", null);
						qe.setParaValue("LAST_MODIFIED_DATE",
								new java.util.Date(System.currentTimeMillis()));
						qe.setParaValue("ORD_FLAG", 2);
						qe.execute();
						qe.close();
						data_visit.setFieldValue(rowno, "ORD_NO",
								BigDecimal.valueOf(on));

					}
					QueryExe qeItem = new QueryExe(sqlItems, con);
					qeItem.setParaValue("PERIODCODE", pcod);
					qeItem.setParaValue("ORD_CODE", 111);
					qeItem.setParaValue("LOCATION_CODE", loc);
					qeItem.setParaValue("ORD_NO", on);
					qeItem.setParaValue("ORD_TYPE", 1);
					qeItem.setParaValue("ORD_DATE", txtTodayDate.getValue());
					qeItem.setParaValue("ORD_REFER", txtItemCode.getValue());
					qeItem.setParaValue("DESCR", txtItemName.getValue());
					qeItem.setParaValue("ORD_PKQTY", qt);
					qeItem.setParaValue("ORD_ALLQTY", qt);
					qeItem.setParaValue("ORD_PRICE", pr);
					qeItem.setParaValue("ORD_PACK", 1);
					qeItem.setParaValue("ORD_PACKD", "PCS");
					qeItem.setParaValue("ORD_UNITD", "PCS");
					qeItem.setParaValue("ORD_FLAG", 2);
					qeItem.setParaValue("SALEINV", null);
					qeItem.execute();
					qeItem.close();
					utils.execSql(
							"update clq_visits set ord_no="
									+ on
									+ ",ord_amt= (select nvl(sum((ord_price/ord_pack)*ord_allqty),0) from "
									+ " order2 where ord_no=" + on
									+ " and ord_code=111 "
									+ ")  where keyfld='"
									+ data_visit.getFieldValue(rowno, "KEYFLD")
									+ "'", con);
					double totamt = Double.valueOf(utils.getSqlValue(
							"select nvl(sum((ord_price/ord_pack)*ord_allqty),0) from "
									+ " order2 where ord_no=" + on
									+ " and ord_code=111 ", con));

					data_visit.setFieldValue(rowno, "ORD_AMT",
							BigDecimal.valueOf(totamt));
					utilsVaadin.refreshRowFromData(data_visit, tbl_visit,
							rowno, lstvisitCols);
					con.commit();
					if (Channelplus3Application.getInstance().getMainWindow()
							.getChildWindows().contains(wnd)) {
						Channelplus3Application.getInstance().getMainWindow()
								.removeWindow(wnd);
					}

					fetch_medical_items();

				} catch (SQLException ex) {
					ex.printStackTrace();

					try {
						con.rollback();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void select_visit() {
		final Window wx = ControlsFactory.CreateWindow("500px", "100px", true,
				true);
		Button btDRVisit = new Button("Doctor's Visit");
		Button btMedication = new Button("Medication");
		Button btDuePay = new Button("Due Payment");
		HorizontalLayout hz = new HorizontalLayout();
		hz.addComponent(btDRVisit);
		hz.addComponent(btDuePay);
		hz.addComponent(btMedication);
		((VerticalLayout) wx.getContent()).addComponent(hz);
		hz.setSizeFull();
		hz.setComponentAlignment(btDRVisit, Alignment.MIDDLE_CENTER);
		hz.setComponentAlignment(btDuePay, Alignment.MIDDLE_CENTER);
		hz.setComponentAlignment(btMedication, Alignment.MIDDLE_CENTER);
		hz.setSpacing(true);
		hz.setMargin(true);

		btDRVisit.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				Channelplus3Application.getInstance().getMainWindow()
						.removeWindow(wx);
				add_update_visit(true);
			}
		});

		btMedication.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Channelplus3Application.getInstance().getMainWindow()
						.removeWindow(wx);
				add_medication_visit();
			}
		});
		btDuePay.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Channelplus3Application.getInstance().getMainWindow()
						.removeWindow(wx);
				try {
					add_duepay_visit();
				} catch (Exception e) {
					utilsVaadin.showMessage(Channelplus3Application
							.getInstance().getMainWindow(), e.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
					e.printStackTrace();
				}

			}
		});
	}

	private void add_medication_visit() {
		final SimpleDateFormat sdf = new SimpleDateFormat(
				utils.FORMAT_SHORT_DATE);

		final Window wx = ControlsFactory.CreateWindow("500px", "450px", true,
				true);
		wx.setCaption("Medication visit");
		FormLayoutManager frm = new FormLayoutManager("100%", "-1px");
		wx.setContent(frm);
		frm.setSpacing(true);
		frm.setMargin(true);
		Date dt = new Date();

		dt.setTime(System.currentTimeMillis());
		Calendar cl = Calendar.getInstance();
		cl.setTimeInMillis(((Date) txtTodayDate.getValue()).getTime());
		Calendar cl2 = Calendar.getInstance();
		cl2.setTimeInMillis(dt.getTime());
		cl.set(Calendar.HOUR_OF_DAY, cl2.get(Calendar.HOUR_OF_DAY));
		cl.set(Calendar.MINUTE, cl2.get(Calendar.MINUTE));
		Date dtx = new Date();

		dtx.setTime(cl.getTimeInMillis());

		final Double varPrice = Double.valueOf(0);
		final Double varAmt = Double.valueOf(0);

		final DateField txtArrival = new DateField("Arrival Date", dtx);

		final TextField txtpatientName = new TextField("Patient Name");
		final TextField txtMedicalno = new TextField("Medical #");
		final TextField txtpatientTel = new TextField("Patient's Tel");

		final TextField txtItemCode = new TextField("Item code");
		final TextField txtItemName = new TextField("Item Descr");
		final TextField txtPrice = new TextField("Price");
		final TextField txtQty = new TextField("Qty");
		final TextField txtAmount = new TextField("Amount");
		final TextField txtBalance = new TextField("Due Balance");
		final TextField txtLastVisit = new TextField("Last visit");
		final Label txtDaysAgo = new Label();
		final ComboBox txtProcedure = ControlsFactory
				.CreateListField(
						"Procedure",
						"proc",
						"select reference,descr from items where itprice4=1 order by descr2",
						null);

		NativeButton bt0 = ControlsFactory.CreateToolbarButton("img/find.png",
				"");

		NativeButton bt = ControlsFactory.CreateToolbarButton("img/find.png",
				"");

		Button btClose = new Button("Save & Close");
		txtPrice.setImmediate(true);
		txtQty.setImmediate(true);
		txtQty.setValue("1");
		txtPrice.setValue("0.000");

		txtArrival.setDateFormat("dd/mm/rrrr h:m a");
		txtPrice.setStyleName("netAmtStyle");
		txtQty.setStyleName("netAmtStyle");
		txtLastVisit.addStyleName("yellowField");

		frm.addComponentsRow(txtArrival, "width=100px");
		frm.addComponentsRow(txtMedicalno,
				"caption=Medical No,width=100%,expand=0.8,readonly=true", bt0,
				"width=100%,expand=0.2", txtpatientName,
				"width=100%,expand=2,readonly=true", txtpatientTel,
				"expand=1,width=100%,readonly=true");
		frm.addComponentsRow(txtBalance,
				"caption=Balance,width=100%,expand=1,readonly=true",
				txtLastVisit, "width=100%,expand=1,readonly=true", txtDaysAgo,
				"width=100%,expand=2,style=yellowLabel");
		frm.addComponentsRow(txtProcedure,
				"caption=Procedure,width=200px,expand=1,enable=false");

		frm.addLine();
		frm.addComponentsRow(new Label("Enter here Item"),
				"height=40px,style=title1");
		frm.addComponentsRow(txtItemCode,
				"caption=Item Code,width=100%,expand=0.8", bt,
				"width=100%,expand=0.2", txtItemName, "width=100%,expand=3");
		frm.addComponentsRow(txtPrice, "caption=Price,width=100%,expand=1",
				txtQty, "caption=Qty,width=100%,expand=1", txtAmount,
				"expand=2,width=100%,caption=Amount");
		frm.addComponentsRow(btClose, "width=120px");

		ValueChangeListener vlc = new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (txtPrice.getValue() == null
						|| txtPrice.getValue().toString().isEmpty()) {
					txtPrice.setValue("0.000");
				}
				DecimalFormat dc = new DecimalFormat(Channelplus3Application
						.getInstance().getFrmUserLogin().FORMAT_MONEY);
				double pr = Double.valueOf(txtPrice.getValue().toString());
				double qt = Double.valueOf(txtQty.getValue().toString());
				double amt = pr * qt;
				txtPrice.setValue(dc.format(pr));
				utilsVaadin.setValueByForce(txtAmount, dc.format(amt));
				utilsVaadin.setValueByForce(txtPrice, dc.format(pr));
			}
		};

		txtPrice.addListener(vlc);
		txtQty.addListener(vlc);
		txtProcedure.setValue(utilsVaadin.findByValue(txtProcedure,
				Channelplus3Application.getInstance().getFrmUserLogin()
						.getMapVars().get("MEDICATION_VISIT_PROC")));
		txtProcedure.setEnabled(false);

		bt0.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				try {
					final Window wnd2 = ControlsFactory.CreateWindow("70%",
							"70%", true, true);
					utilsVaadin.showSearch(
							(VerticalLayout) wnd2.getContent(),
							new TableView.SelectionListener() {
								public void onSelection(TableView tv) {
									if (tv.getSelectionValue() < 0) {
										return;
									}
									txtMedicalno.setReadOnly(false);
									txtpatientName.setReadOnly(false);
									txtpatientTel.setReadOnly(false);
									txtBalance.setReadOnly(false);
									txtLastVisit.setReadOnly(false);
									double bal = 0;
									String lastvisit = "";

									String mn = tv.getData().getFieldValue(
											tv.getSelectionValue(),
											"MEDICAL_NO")
											+ "";
									try {
										bal = Double.valueOf(QueryExe
												.getSqlValue(
														"select "
																+ "nvl(sum(debit-credit),0) from"
																+ " acvoucher2 where cust_code='"
																+ mn + "'",
														con, "0")
												+ "");
										lastvisit = QueryExe
												.getSqlValue(
														"select nvl(max(to_char(date_of_arrival,'dd/mm/rrrr')),'') from clq_visits "
																+ " where kov='DR' and  medical_no='"
																+ mn + "'",
														con, "")
												+ "";
									} catch (NumberFormatException
											| SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									txtMedicalno.setValue(mn);
									txtpatientName.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"PATIENT_NAME"));
									txtpatientTel.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"TEL")
											+ "");
									txtBalance
											.setValue((new DecimalFormat(
													Channelplus3Application
															.getInstance()
															.getFrmUserLogin().FORMAT_MONEY))
													.format(bal));
									txtLastVisit.setValue(lastvisit);
									txtDaysAgo.setValue("");
									if (!lastvisit.isEmpty()) {
										try {
											Date dt = sdf.parse(lastvisit);
											Date nowdt = new Date(System
													.currentTimeMillis());
											int d = utils
													.daysBetween(dt, nowdt);
											txtDaysAgo.setValue(d
													+ "  days ago !");
											System.out.println(d);
										} catch (Exception e) {
											e.printStackTrace();
										}

									}

									utilsVaadin.showMessage("Balance " + bal
											+ " ");
									txtMedicalno.setReadOnly(true);
									txtpatientName.setReadOnly(true);
									txtpatientTel.setReadOnly(true);
									txtBalance.setReadOnly(true);
									txtLastVisit.setReadOnly(true);

									if (Channelplus3Application.getInstance()
											.getMainWindow().getChildWindows()
											.contains(wnd2)) {
										Channelplus3Application.getInstance()
												.getMainWindow()
												.removeWindow(wnd2);
									}

								}
							},
							con,
							"select e_first_nm||' '||e_second_nm||' '||e_family_nm patient_name , "
									+ "medical_no,tel,mobile_no,id_no,e_mother_nm from clq_patients order by medical_no",
							true);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}

			}
		});
		bt.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					final Window wnd2 = ControlsFactory.CreateWindow("70%",
							"70%", true, true);

					utilsVaadin.showSearch(
							(VerticalLayout) wnd2.getContent(),
							new TableView.SelectionListener() {
								public void onSelection(TableView tv) {
									if (tv.getSelectionValue() < 0) {
										return;
									}
									txtItemCode.setReadOnly(false);
									txtItemName.setReadOnly(false);

									txtItemCode.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"REFERENCE"));
									txtItemName.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"DESCR"));
									txtPrice.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"PRICE1"));
									txtItemCode.setReadOnly(true);
									txtItemName.setReadOnly(true);

									if (Channelplus3Application.getInstance()
											.getMainWindow().getChildWindows()
											.contains(wnd2)) {
										Channelplus3Application.getInstance()
												.getMainWindow()
												.removeWindow(wnd2);
									}

								}
							}, con,
							"select reference,descr,price1 from items where childcounts=0"
									+ " and itprice4=0 order by descr2 ", true);
				} catch (SQLException ex) {
					ex.printStackTrace();
					utilsVaadin.showMessage(wx, ex.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
		btClose.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (txtMedicalno.getValue() == null
							|| txtMedicalno.getValue().toString().isEmpty())
						throw new Exception("Patient not selected ! ");
					if (txtItemCode.getValue() == null
							|| txtItemCode.getValue().toString().isEmpty())
						throw new Exception("Item not selected ! ");

					String mn = QueryExe.getSqlValue(
							"select medical_no from clq_patients where medical_no='"
									+ txtMedicalno.getValue() + "'", con, "")
							+ "";

					double kf = Double.valueOf(QueryExe.getSqlValue(
							"select nvl(max(keyfld),0)+1 from clq_visits", con,
							"1")
							+ "");

					if (mn.isEmpty())
						throw new Exception(
								"Patient may have been removed by another user !");
					if (txtProcedure.getValue() == null)
						throw new Exception(
								"Must have assign medication visit item in SETUP");

					String sqlClq = "begin INSERT INTO CLQ_VISITS  (KEYFLD, TIME_OF_ARRIVAL, MEDICAL_NO, "
							+ "DATE_OF_ARRIVAL, DRNO, FOLLOWUP_ITEM, FLAG, "
							+ " FOLLOWUP_FEES,FOLLOWUP_INV_TYPE,MEDICAL_INV_TYPE , FOLLOWUP_DISC, KOV ) values ("
							+ " :KEYFLD  , :TIME_OF_ARRIVAL, :MEDICAL_NO, "
							+ ":DATE_OF_ARRIVAL, :DRNO, :FOLLOWUP_ITEM, '1_CREATED', "
							+ " :PRICE ,:FOLLOWUP_INV_TYPE , :MEDICAL_INV_TYPE  , :FOLLOWUP_DISC , :KOV ); "
							+ "update clq_patients set last_visit_date=:DATE_OF_ARRIVAL,"
							+ "last_doc=:DRNO where medical_no=:MEDICAL_NO ;  end; ";

					String sqlIns = "insert into order1(PERIODCODE,LOCATION_CODE,ORD_NO,ORD_CODE,ORD_TYPE,ORD_DISCAMT,ORD_SHPDT,"
							+ "LCNO, ORD_EMPNO, REMARKS, SALEINV , LAST_MODIFIED_TIME ,ORD_FLAG ,ORD_DATE ,ORD_REF, ORD_REFNM ,ORD_AMT)  VALUES "
							+ "( :PERIODCODE,:LOCATION_CODE,:ORD_NO , :ORD_CODE , :ORD_TYPE, :ORD_DISCAMT, :ORD_SHPDT, "
							+ " :LCNO, :ORD_EMPNO, :REMARKS, :SALEINV , :LAST_MODIFIED_DATE , :ORD_FLAG , :ORD_DATE, :ORD_REF, :ORD_REFNM, :ORD_AMT )";

					String sqlItems = "insert into order2 (PERIODCODE,LOCATION_CODE, ORD_DATE, ORD_NO,ORD_CODE,ORD_TYPE,ORD_POS,ORD_REFER,DESCR,ORD_PRICE,"
							+ "ORD_PKQTY, ORD_ALLQTY, ORD_PACK, ORD_PACKD, ORD_UNITD, ORD_FLAG ,SALEINV) VALUES "
							+ "(:PERIODCODE,:LOCATION_CODE, :ORD_DATE, :ORD_NO, :ORD_CODE,:ORD_TYPE,"
							+ "(select nvl(max(ord_pos),0)+1 from order2 where ord_code=111 and ord_no=:ORD_NO and location_code=:LOCATION_CODE) ,"
							+ ":ORD_REFER,:DESCR,:ORD_PRICE,"
							+ ":ORD_PKQTY, :ORD_ALLQTY, :ORD_PACK, :ORD_PACKD, :ORD_UNITD, :ORD_FLAG ,:SALEINV)";

					QueryExe qe = new QueryExe(sqlClq, con);
					qe.setParaValue("KEYFLD", kf);
					qe.setParaValue("TIME_OF_ARRIVAL", new java.sql.Timestamp(
							((Date) txtArrival.getValue()).getTime()));
					qe.setParaValue("MEDICAL_NO", txtMedicalno.getValue());
					qe.setParaValue("DATE_OF_ARRIVAL", txtArrival.getValue());
					qe.setParaValue("DRNO", txtDrNo.getValue());
					qe.setParaValue("FOLLOWUP_ITEM", txtProcedure.getValue());
					qe.setParaValue("PRICE", 0);
					qe.setParaValue("FOLLOWUP_INV_TYPE", 1);
					qe.setParaValue("MEDICAL_INV_TYPE", 1);
					qe.setParaValue("FOLLOWUP_DISC", 0);
					qe.setParaValue("KOV", "MEDICATION_VISIT");
					qe.execute();
					qe.close();

					double on = 0;
					String pcod = QueryExe
							.getSqlValue(
									"select repair.getsetupvalue_2('CURRENT_PERIOD') from dual",
									con, "")
							+ "";

					String loc = QueryExe
							.getSqlValue(
									"select repair.getsetupvalue_2('DEFAULT_LOCATION') from dual",
									con, "")
							+ "";

					on = Double.valueOf(utils.getSqlValue(
							"select nvl(max(ord_no),0)+1 from order1 "
									+ "where ord_code=111 ", con));
					qe = new QueryExe(sqlIns, con);
					qe.setParaValue("PERIODCODE", pcod);
					qe.setParaValue("LOCATION_CODE", loc);
					qe.setParaValue("ORD_NO", on);
					qe.setParaValue("ORD_CODE", 111);
					qe.setParaValue("ORD_DATE", txtTodayDate.getValue());
					qe.setParaValue("ORD_TYPE", 1);
					qe.setParaValue("ORD_DISCAMT", 0);
					qe.setParaValue("ORD_SHPDT", txtTodayDate.getValue());
					qe.setParaValue("ORDACC", txtProcedure.getValue());
					qe.setParaValue("ORD_REF", mn);
					qe.setParaValue("LCNO", kf);
					qe.setParaValue("ORD_REFNM", txtpatientName.getValue());
					qe.setParaValue("ORD_AMT", txtAmount.getValue());
					qe.setParaValue("ORD_EMPNO", txtDrNo.getValue());
					qe.setParaValue("REMARKS", "");
					qe.setParaValue("SALEINV", null);
					qe.setParaValue("LAST_MODIFIED_DATE", new java.util.Date(
							System.currentTimeMillis()));
					qe.setParaValue("ORD_FLAG", 2);
					qe.execute();
					qe.close();
					QueryExe qeItem = new QueryExe(sqlItems, con);
					qeItem.setParaValue("PERIODCODE", pcod);
					qeItem.setParaValue("ORD_CODE", 111);
					qeItem.setParaValue("LOCATION_CODE", loc);
					qeItem.setParaValue("ORD_NO", on);
					qeItem.setParaValue("ORD_TYPE", 1);
					qeItem.setParaValue("ORD_DATE", txtTodayDate.getValue());
					qeItem.setParaValue("ORD_REFER", txtItemCode.getValue());
					qeItem.setParaValue("DESCR", txtItemName.getValue());
					qeItem.setParaValue("ORD_PKQTY", txtQty.getValue());
					qeItem.setParaValue("ORD_ALLQTY", txtQty.getValue());
					qeItem.setParaValue("ORD_PRICE", txtPrice.getValue());
					qeItem.setParaValue("ORD_PACK", 1);
					qeItem.setParaValue("ORD_PACKD", "PCS");
					qeItem.setParaValue("ORD_UNITD", "PCS");
					qeItem.setParaValue("ORD_FLAG", 2);
					qeItem.setParaValue("SALEINV", null);
					qeItem.execute();
					qeItem.close();
					utils.execSql(
							"update clq_visits set ord_no="
									+ on
									+ ",ord_amt= (select nvl(sum((ord_price/ord_pack)*ord_allqty),0) from "
									+ " order2 where ord_no=" + on
									+ " and ord_code=111 "
									+ ")  where keyfld='" + kf + "'", con);
					double totamt = Double.valueOf(utils.getSqlValue(
							"select nvl(sum((ord_price/ord_pack)*ord_allqty),0) from "
									+ " order2 where ord_no=" + on
									+ " and ord_code=111 ", con));

					con.commit();
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wx);
					load_data();
					int loc1 = data_visit.locate("KEYFLD", ((int) kf) + "",
							localTableModel.FIND_EXACT);
					tbl_visit.setValue(loc1);
					if (tbl_visit.getValue() != null)
						cmdPost.click();

					utilsVaadin.showMessage(Channelplus3Application
							.getInstance().getMainWindow(),
							"Successfully generated , post visit to pay",
							Notification.TYPE_HUMANIZED_MESSAGE);

				} catch (Exception ex) {
					ex.printStackTrace();
					utilsVaadin.showMessage(wx, ex.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
				}

			}
		});
	}

	private void add_duepay_visit() throws Exception {
		final SimpleDateFormat sdf = new SimpleDateFormat(
				utils.FORMAT_SHORT_DATE);
		final DecimalFormat df = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);

		final Window wx = ControlsFactory.CreateWindow("500px", "450px", true,
				true);
		wx.setCaption("Due Payment");
		FormLayoutManager frm = new FormLayoutManager("100%", "-1px");
		wx.setContent(frm);
		frm.setSpacing(true);
		frm.setMargin(true);
		Date dt = new Date();

		dt.setTime(System.currentTimeMillis());
		Calendar cl = Calendar.getInstance();
		cl.setTimeInMillis(((Date) txtTodayDate.getValue()).getTime());
		Calendar cl2 = Calendar.getInstance();
		cl2.setTimeInMillis(dt.getTime());
		cl.set(Calendar.HOUR_OF_DAY, cl2.get(Calendar.HOUR_OF_DAY));
		cl.set(Calendar.MINUTE, cl2.get(Calendar.MINUTE));
		Date dtx = new Date();

		dtx.setTime(cl.getTimeInMillis());

		final String varLocation = QueryExe.getSqlValue(
				"select repair.getsetupvalue_2('DEFAULT_LOCATION') from dual",
				con, "")
				+ "";

		final List<FieldInfo> flds = new ArrayList<FieldInfo>();

		final DateField txtArrival = new DateField("Arrival Date", dtx);
		final Label lblDue = new Label(df.format(0));
		final TextField txtpatientName = new TextField("Patient Name");
		final TextField txtMedicalno = new TextField("Medical #");
		final TextField txtpatientTel = new TextField("Patient's Tel");

		final TextField txtBalance = ControlsFactory.CreateTextField(null,
				"DUE", flds, "0.000", Parameter.DATA_TYPE_NUMBER,
				utils.FORMAT_MONEY, true);
		final TextField txtLastVisit = new TextField("Last visit");

		final Label txtDaysAgo = new Label();
		final ComboBox txtProcedure = ControlsFactory
				.CreateListField(
						"Procedure",
						"proc",
						"select reference,descr from items where itprice4=1 order by descr2",
						null);
		final ComboBox txtType1 = ControlsFactory.CreateListField(null,
				"TYPE_1",
				"SELECT NO,DESCR FROM INVOICETYPE WHERE LOCATION_CODE='"
						+ varLocation + "' and accno is not null"
						+ " order  by no", flds);
		final ComboBox txtType2 = ControlsFactory.CreateListField(null,
				"TYPE_2",
				"SELECT NO,DESCR FROM INVOICETYPE WHERE LOCATION_CODE='"
						+ varLocation + "' and accno is not null"
						+ " order  by no", flds);

		final TextField txtAmt1 = ControlsFactory.CreateTextField(null,
				"AMT_1", flds, "0.000", Parameter.DATA_TYPE_NUMBER,
				utils.FORMAT_MONEY, true);

		final TextField txtAmt2 = ControlsFactory.CreateTextField(null,
				"AMT_2", flds, "0.000", Parameter.DATA_TYPE_NUMBER,
				utils.FORMAT_MONEY, true);

		Button btClose = new Button("Update..");

		NativeButton bt0 = ControlsFactory.CreateToolbarButton("img/find.png",
				"");

		txtArrival.setDateFormat("dd/mm/rrrr h:m a");
		txtLastVisit.addStyleName("yellowField");

		frm.addComponentsRow(txtArrival, "width=100px");

		frm.addComponentsRow(txtMedicalno,
				"caption=Medical No,width=100%,expand=0.8,readonly=true", bt0,
				"width=100%,expand=0.2", txtpatientName,
				"width=100%,expand=2,readonly=true", txtpatientTel,
				"expand=1,width=100%,readonly=true");
		frm.addComponentsRow(txtBalance,
				"caption=Due Balance,width=100%,expand=1,readonly=true",
				txtLastVisit, "width=100%,expand=1,readonly=true", txtDaysAgo,
				"width=100%,expand=2,style=yellowLabel");

		frm.addComponentsRow(txtProcedure,
				"caption=Procedure,width=200px,expand=1,enable=false", lblDue,
				"caption=Keep Balance,expand=2");

		frm.addLine();

		frm.addComponentsRow(new Label("Enter here payment.."),
				"height=40px,style=title1");

		frm.addComponentsRow("caption=Payment 1,width=100%,expand=.5",
				txtType1, "expand=1.5", "caption=Amount,expand=.5,", txtAmt1,
				"width=100%,expand=1.5");

		frm.addComponentsRow("caption=Payment 2,width=100%,expand=.5",
				txtType2, "expand=1.5", "caption=Amount,expand=.5,", txtAmt2,
				"width=100%,expand=1.5");

		utilsVaadin.setDefaultValues(flds, true);

		frm.addComponentsRow("caption=.,expand=3", btClose,
				"width=100%,expand=1");

		txtType1.setImmediate(true);
		txtType2.setImmediate(true);
		txtAmt1.setImmediate(true);
		txtAmt2.setImmediate(true);

		txtType1.focus();

		txtProcedure.setValue(utilsVaadin.findByValue(txtProcedure,
				Channelplus3Application.getInstance().getFrmUserLogin()
						.getMapVars().get("DUE_PAY_VISIT")));
		txtProcedure.setEnabled(false);
		txtType1.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				try {
					double amt_tot = utilsVaadin.getFieldInfoDoubleValue(
							txtBalance, flds);
					txtType2.setValue(null);
					txtAmt2.setValue("0.000");
					txtAmt1.setValue(df.format(amt_tot));

					double due = amt_tot
							- utilsVaadin
									.getFieldInfoDoubleValue(txtAmt1, flds);
					lblDue.setValue(df.format(due));
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});
		txtType2.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (txtType2.getValue() == null) {
					txtAmt2.setValue("0.000");
					return;
				}

				if (txtType1.getValue() == null) {
					txtType2.setValue(null);
					return;
				}

				try {
					double amt_tot = utilsVaadin.getFieldInfoDoubleValue(
							txtBalance, flds);
					double due = amt_tot
							- utilsVaadin
									.getFieldInfoDoubleValue(txtAmt1, flds);
					lblDue.setValue(df.format(due));
					txtAmt2.setValue(df.format(due));
					lblDue.setValue(df.format(0.0f));
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});
		txtAmt2.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					double amt_tot = utilsVaadin.getFieldInfoDoubleValue(
							txtBalance, flds);

					double due = amt_tot
							- (utilsVaadin.getFieldInfoDoubleValue(txtAmt1,
									flds) + utilsVaadin
									.getFieldInfoDoubleValue(txtAmt2, flds));
					lblDue.setValue(df.format(due));
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});
		txtAmt1.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					double amt_tot = utilsVaadin.getFieldInfoDoubleValue(
							txtBalance, flds);
					txtType2.setValue(null);
					txtAmt2.setValue("0.000");
					txtAmt2.setValue(df.format(0));

					double due = amt_tot
							- utilsVaadin
									.getFieldInfoDoubleValue(txtAmt1, flds);
					lblDue.setValue(df.format(due));

				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});

		bt0.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				try {
					final Window wnd2 = ControlsFactory.CreateWindow("70%",
							"70%", true, true);
					utilsVaadin.showSearch(
							(VerticalLayout) wnd2.getContent(),
							new TableView.SelectionListener() {
								public void onSelection(TableView tv) {
									if (tv.getSelectionValue() < 0) {
										return;
									}
									txtMedicalno.setReadOnly(false);
									txtpatientName.setReadOnly(false);
									txtpatientTel.setReadOnly(false);
									txtBalance.setReadOnly(false);
									txtLastVisit.setReadOnly(false);
									double bal = 0;
									String lastvisit = "";

									String mn = tv.getData().getFieldValue(
											tv.getSelectionValue(),
											"MEDICAL_NO")
											+ "";
									try {
										bal = Double.valueOf(QueryExe
												.getSqlValue(
														"select "
																+ "nvl(sum(debit-credit),0) from"
																+ " acvoucher2 where cust_code='"
																+ mn + "'",
														con, "0")
												+ "");
										lastvisit = QueryExe
												.getSqlValue(
														"select nvl(max(to_char(date_of_arrival,'dd/mm/rrrr')),'') from clq_visits "
																+ " where kov='DR' and  medical_no='"
																+ mn + "'",
														con, "")
												+ "";
									} catch (NumberFormatException
											| SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									txtMedicalno.setValue(mn);
									txtpatientName.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"PATIENT_NAME"));
									txtpatientTel.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"TEL")
											+ "");
									txtBalance
											.setValue((new DecimalFormat(
													Channelplus3Application
															.getInstance()
															.getFrmUserLogin().FORMAT_MONEY))
													.format(bal));
									txtLastVisit.setValue(lastvisit);
									txtDaysAgo.setValue("");
									if (!lastvisit.isEmpty()) {
										try {
											Date dt = sdf.parse(lastvisit);
											Date nowdt = new Date(System
													.currentTimeMillis());
											int d = utils
													.daysBetween(dt, nowdt);
											txtDaysAgo.setValue(d
													+ "  days ago !");
											System.out.println(d);
										} catch (Exception e) {
											e.printStackTrace();
										}

									}

									utilsVaadin.showMessage("Balance " + bal
											+ " ");
									txtMedicalno.setReadOnly(true);
									txtpatientName.setReadOnly(true);
									txtpatientTel.setReadOnly(true);
									txtBalance.setReadOnly(true);
									txtLastVisit.setReadOnly(true);

									if (Channelplus3Application.getInstance()
											.getMainWindow().getChildWindows()
											.contains(wnd2)) {
										Channelplus3Application.getInstance()
												.getMainWindow()
												.removeWindow(wnd2);
									}

								}
							},
							con,
							"select p.e_first_nm||' '||p.e_second_nm||' '||p.e_family_nm patient_name , "
									+ "p.medical_no,p.tel,p.mobile_no,p.id_no,p.e_mother_nm,  "
									+ "sum(v.debit-v.credit) Due from clq_patients p,acvoucher2 v"
									+ " where  v.cust_code=p.medical_no "
									+ " group by "
									+ " p.e_first_nm||' '||p.e_second_nm||' '||p.e_family_nm , "
									+ " p.medical_no,p.tel,p.mobile_no,p.id_no,p.e_mother_nm  "
									+ " having sum(v.debit-v.credit)>0 "
									+ " order by p.medical_no", true);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}

			}
		});

		btClose.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (txtMedicalno.getValue() == null
							|| txtMedicalno.getValue().toString().isEmpty())
						throw new Exception("Patient not selected ! ");

					double amt1 = utilsVaadin.getFieldInfoDoubleValue(txtAmt1,
							flds);
					double amt2 = utilsVaadin.getFieldInfoDoubleValue(txtAmt2,
							flds);
					if (txtType1.getValue() == null)
						throw new Exception("Pay Type not selected ! ");
					if (amt1 <= 0)
						throw new Exception("Amount must be >0  !");

					String mn = QueryExe.getSqlValue(
							"select medical_no from clq_patients where medical_no='"
									+ txtMedicalno.getValue() + "'", con, "")
							+ "";

					double kf = Double.valueOf(QueryExe.getSqlValue(
							"select nvl(max(keyfld),0)+1 from clq_visits", con,
							"1")
							+ "");

					if (mn.isEmpty())
						throw new Exception(
								"Patient may have been removed by another user !");
					if (txtProcedure.getValue() == null)
						throw new Exception(
								"Must have assign medication visit item in SETUP");

					String sqlClq = "begin INSERT INTO CLQ_VISITS  (KEYFLD, TIME_OF_ARRIVAL, MEDICAL_NO, "
							+ "DATE_OF_ARRIVAL, DRNO, FOLLOWUP_ITEM, FLAG, "
							+ " FOLLOWUP_FEES,FOLLOWUP_INV_TYPE,MEDICAL_INV_TYPE , FOLLOWUP_DISC, KOV ) values ("
							+ " :KEYFLD  , :TIME_OF_ARRIVAL, :MEDICAL_NO, "
							+ ":DATE_OF_ARRIVAL, :DRNO, :FOLLOWUP_ITEM, '1_CREATED', "
							+ " :PRICE ,:FOLLOWUP_INV_TYPE , :MEDICAL_INV_TYPE  , :FOLLOWUP_DISC , :KOV ); "
							+ "update clq_patients set last_visit_date=:DATE_OF_ARRIVAL,"
							+ "last_doc=:DRNO where medical_no=:MEDICAL_NO ;  end; ";

					String sqlIns = "insert into order1(PERIODCODE,LOCATION_CODE,ORD_NO,ORD_CODE,ORD_TYPE,ORD_DISCAMT,ORD_SHPDT,"
							+ "LCNO, ORD_EMPNO, REMARKS, SALEINV , LAST_MODIFIED_TIME ,ORD_FLAG ,ORD_DATE ,ORD_REF, ORD_REFNM ,ORD_AMT)  VALUES "
							+ "( :PERIODCODE,:LOCATION_CODE,:ORD_NO , :ORD_CODE , :ORD_TYPE, :ORD_DISCAMT, :ORD_SHPDT, "
							+ " :LCNO, :ORD_EMPNO, :REMARKS, :SALEINV , :LAST_MODIFIED_DATE , :ORD_FLAG , :ORD_DATE, :ORD_REF, :ORD_REFNM, :ORD_AMT )";

					String sqlItems = "insert into order2 (PERIODCODE,LOCATION_CODE, ORD_DATE, ORD_NO,ORD_CODE,ORD_TYPE,ORD_POS,ORD_REFER,DESCR,ORD_PRICE,"
							+ "ORD_PKQTY, ORD_ALLQTY, ORD_PACK, ORD_PACKD, ORD_UNITD, ORD_FLAG ,SALEINV) VALUES "
							+ "(:PERIODCODE,:LOCATION_CODE, :ORD_DATE, :ORD_NO, :ORD_CODE,:ORD_TYPE,"
							+ "(select nvl(max(ord_pos),0)+1 from order2 where ord_code=111 and ord_no=:ORD_NO and location_code=:LOCATION_CODE) ,"
							+ ":ORD_REFER,:DESCR,:ORD_PRICE,"
							+ ":ORD_PKQTY, :ORD_ALLQTY, :ORD_PACK, :ORD_PACKD, :ORD_UNITD, :ORD_FLAG ,:SALEINV)";

					QueryExe qe = new QueryExe(sqlClq, con);
					qe.setParaValue("KEYFLD", kf);
					qe.setParaValue("TIME_OF_ARRIVAL", new java.sql.Timestamp(
							((Date) txtArrival.getValue()).getTime()));
					qe.setParaValue("MEDICAL_NO", txtMedicalno.getValue());
					qe.setParaValue("DATE_OF_ARRIVAL", txtArrival.getValue());
					qe.setParaValue("DRNO", txtDrNo.getValue());
					qe.setParaValue("FOLLOWUP_ITEM", txtProcedure.getValue());
					qe.setParaValue("PRICE", 0);
					qe.setParaValue("FOLLOWUP_INV_TYPE", 1);
					qe.setParaValue("MEDICAL_INV_TYPE", 1);
					qe.setParaValue("FOLLOWUP_DISC", 0);
					qe.setParaValue("KOV", "DUEPAY_VISIT");
					qe.execute();
					qe.close();

					qe = new QueryExe("update clq_visits set PAY_TYPE_1= :P1 ,"
							+ "PAY_TYPE_2= :P2 ," + "PAY_AMT_1= :A1 , "
							+ "PAY_AMT_2= :A2 where keyfld= :KF", con);

					qe.setParaValue("P1", txtType1.getValue());
					qe.setParaValue("P2", txtType2.getValue());
					qe.setParaValue("A1",
							utilsVaadin.getFieldInfoDoubleValue(txtAmt1, flds));
					qe.setParaValue("A2",
							utilsVaadin.getFieldInfoDoubleValue(txtAmt2, flds));
					qe.setParaValue("KF", kf);
					qe.execute();
					qe.close();

					event.getButton().setEnabled(false);
					load_data();
					int loc1 = data_visit.locate("KEYFLD", ((int) kf) + "",
							localTableModel.FIND_EXACT);
					tbl_visit.setValue(loc1);

					save_data(false);
					String pk = utils.nvl(QueryExe.getSqlValue(
							"select pay_keyfld from clq_visits where keyfld='"
									+ kf + "'", con, ""), "");
					if (pk.isEmpty())
						throw new Exception(
								"Unable to create JV for RECEIPT...");

					QueryExe.execute(
							"update clq_visits set saleinv=-1 where keyfld='"
									+ kf + "'", con);
					con.commit();
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wx);
					load_data();

					utilsVaadin.showMessage(Channelplus3Application
							.getInstance().getMainWindow(),
							"Successfully paid & posted..",
							Notification.TYPE_HUMANIZED_MESSAGE);

				} catch (Exception ex) {
					ex.printStackTrace();
					utilsVaadin.showMessage(wx, ex.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
					try {
						con.rollback();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		});

	}

	public void add_update_visit(final boolean isadd) {

		if (!isadd && tbl_visit.getValue() == null) {
			parentLayout.getWindow().showNotification(
					"must select patient first !");
			return;
		}
		final DecimalFormat df = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);
		final int rn = (tbl_visit.getValue() != null ? (Integer) tbl_visit
				.getValue() : 0);
		final double kf = (tbl_visit.getValue() != null ? ((BigDecimal) data_visit
				.getFieldValue(rn, "KEYFLD")).doubleValue() : -1);
		if (!isadd) {
			String saleinv = utils.getSqlValue(
					"select saleinv||saleinv_medical from clq_visits where keyfld="
							+ kf, con);

			if (!saleinv.isEmpty()) {
				parentLayout.getWindow().showNotification(
						"Invoice created ,can not edit record.. !",
						Notification.TYPE_ERROR_MESSAGE);
				return;
			}
		}

		final Window wnd = ControlsFactory.CreateWindow("500px", "450px", true,
				true);
		Date dt = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat(
				utils.FORMAT_SHORT_DATE);
		dt.setTime(System.currentTimeMillis());
		Calendar cl = Calendar.getInstance();
		cl.setTimeInMillis(((Date) txtTodayDate.getValue()).getTime());
		Calendar cl2 = Calendar.getInstance();
		cl2.setTimeInMillis(dt.getTime());
		cl.set(Calendar.HOUR_OF_DAY, cl2.get(Calendar.HOUR_OF_DAY));
		cl.set(Calendar.MINUTE, cl2.get(Calendar.MINUTE));
		Date dtx = new Date();

		dtx.setTime(cl.getTimeInMillis());
		final DateField txtArrival = new DateField("Arrival Date", dtx);
		HorizontalLayout pt = new HorizontalLayout();
		HorizontalLayout bts = new HorizontalLayout();
		HorizontalLayout p = new HorizontalLayout();
		HorizontalLayout bl = new HorizontalLayout();
		p.setWidth("100%");
		final NativeButton btAddpat = new NativeButton("New Patient");
		final TextField txtpatientName = new TextField("Patient Name");
		final TextField txtMedicalno = new TextField("Medical #");
		final TextField txtpatientTel = new TextField("Patient's Tel");
		final TextField txtPrice = new TextField("Fees");
		final TextField txtBalance = new TextField("Due Balance");
		final TextField txtLastVisit = new TextField("Last visit");
		final TextField txtDiscount = new TextField("Discount");
		final TextField txtNetAmt = new TextField("Net amount");
		final Label txtDaysAgo = new Label();
		HorizontalLayout typeLayout = new HorizontalLayout();
		final String varLocation = "001";

		final ComboBox txtPInvType = ControlsFactory.CreateListField(
				"Procedure Invoice type", "FOLLOWUP_INV_TYPE",
				"SELECT NO,DESCR FROM INVOICETYPE WHERE LOCATION_CODE='"
						+ varLocation + "' order  by no", null);
		final ComboBox txtMInvType = ControlsFactory.CreateListField(
				"Medical Invoice type", "FOLLOWUP_INV_TYPE",
				"SELECT NO,DESCR FROM INVOICETYPE WHERE LOCATION_CODE='"
						+ varLocation + "' order  by no", null);
		if (txtPInvType.getItemIds().size() > 0)
			txtPInvType.setValue(txtPInvType.getItemIds().toArray()[0]);
		if (txtMInvType.getItemIds().size() > 0)
			txtMInvType.setValue(txtMInvType.getItemIds().toArray()[0]);

		NativeButton bt = ControlsFactory.CreateToolbarButton("img/find.png",
				"");
		final ComboBox txtProcedure = ControlsFactory
				.CreateListField(
						"Procedure",
						"proc",
						"select reference,descr from items where itprice4=1 order by descr2",
						null);

		Button btAdd = new Button((isadd ? "Add" : "Update"));

		Button btClose = new Button("Close");
		txtArrival.setDateFormat("H:m a");
		txtArrival.setResolution(DateField.RESOLUTION_MIN);
		((VerticalLayout) wnd.getContent()).setMargin(true);
		((VerticalLayout) wnd.getContent()).setSpacing(true);
		wnd.getContent().addStyleName((isadd ? "" : "toolpanel"));

		ResourceManager.addComponent(wnd.getContent(), btAddpat);
		ResourceManager.addComponent(wnd.getContent(), txtArrival);
		ResourceManager.addComponent(wnd.getContent(), pt);
		ResourceManager.addComponent(pt, txtMedicalno);
		ResourceManager.addComponent(pt, bt);
		ResourceManager.addComponent(pt, txtpatientName);
		ResourceManager.addComponent(pt, txtpatientTel);
		ResourceManager.addComponent(wnd.getContent(), bl);
		ResourceManager.addComponent(bl, txtBalance);
		ResourceManager.addComponent(bl, txtLastVisit);
		ResourceManager.addComponent(bl, txtDaysAgo);
		ResourceManager.addComponent(wnd.getContent(), txtProcedure);
		ResourceManager.addComponent(wnd.getContent(), p);
		ResourceManager.addComponent(wnd.getContent(), typeLayout);
		ResourceManager.addComponent(wnd.getContent(), bts);

		ResourceManager.addComponent(bts, btAdd);
		ResourceManager.addComponent(bts, btClose);
		ResourceManager.addComponent(typeLayout, txtPInvType);
		ResourceManager.addComponent(typeLayout, txtMInvType);
		ResourceManager.addComponent(p, txtPrice);
		ResourceManager.addComponent(p, txtDiscount);
		ResourceManager.addComponent(p, txtNetAmt);

		bl.setComponentAlignment(txtDaysAgo, Alignment.BOTTOM_CENTER);
		txtPrice.addStyleName("netAmtStyle");
		txtBalance.addStyleName("netAmtStyle");
		txtBalance.addStyleName("redField");
		txtNetAmt.addStyleName("netAmtStyle");
		txtDiscount.addStyleName("netAmtStyle");
		txtLastVisit.addStyleName("yellowField");

		txtArrival.focus();

		utilsVaadin.setValueByForce(txtDiscount, "0.000");
		utilsVaadin.setValueByForce(txtNetAmt, "0.000");
		wnd.setCaption((isadd ? "Add patient here.." : "Edit : "
				+ data_visit.getFieldValue(rn, "PATIENTS_NAME") + " ..."));
		if (!isadd) {

			try {
				PreparedStatement psl = con.prepareStatement(
						"select *from clq_visits where keyfld='" + kf + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = psl.executeQuery();
				rst.first();
				txtArrival.setValue(new Date(rst
						.getTimestamp("time_of_arrival").getTime()));
				txtMedicalno.setValue(rst.getString("MEDICAL_NO"));
				txtMInvType.setValue(utilsVaadin.findByValue(txtMInvType,
						rst.getString("MEDICAL_INV_TYPE")));
				txtPInvType.setValue(utilsVaadin.findByValue(txtPInvType,
						rst.getString("FOLLOWUP_INV_TYPE")));
				txtpatientName.setValue(utils.getSqlValue(
						"select e_first_nm||' '||e_second_nm||' '||e_last_nm "
								+ "from clq_patients where medical_no='"
								+ rst.getString("MEDICAL_NO") + "'", con));
				txtpatientTel.setValue(utils.getSqlValue(
						"select tel " + "from clq_patients where medical_no='"
								+ rst.getString("MEDICAL_NO") + "'", con));
				txtPrice.setValue((new DecimalFormat(Channelplus3Application
						.getInstance().getFrmUserLogin().FORMAT_MONEY))
						.format(rst.getDouble("followup_fees")));
				txtProcedure.setValue(utilsVaadin.findByValue(txtProcedure,
						rst.getString("FOLLOWUP_ITEM")));
				utilsVaadin.setValueByForce(txtDiscount,
						df.format(rst.getDouble("FOLLOWUP_DISC")));
				double na = rst.getDouble("followup_fees")
						- rst.getDouble("FOLLOWUP_DISC");
				utilsVaadin.setValueByForce(txtNetAmt, df.format(na));
				psl.close();
			} catch (SQLException e) {
				wnd.getWindow().showNotification(
						"Error in loading" + e.getMessage(),
						Notification.TYPE_ERROR_MESSAGE);
				e.printStackTrace();
			}

		}
		txtMedicalno.setReadOnly(true);
		txtpatientName.setReadOnly(true);
		txtpatientTel.setReadOnly(true);
		txtBalance.setReadOnly(true);
		txtLastVisit.setReadOnly(true);
		txtProcedure.setImmediate(true);
		txtDiscount.setImmediate(true);
		txtNetAmt.setReadOnly(true);

		pt.setComponentAlignment(bt, Alignment.BOTTOM_CENTER);

		txtDiscount.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					double pr = df.parse(txtPrice.getValue().toString())
							.doubleValue();
					double disc = df.parse(txtDiscount.getValue().toString())
							.doubleValue();
					double na = pr - disc;
					utilsVaadin.setValueByForce(txtNetAmt, df.format(na));

				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});
		btAddpat.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				open_add_patient(true);
			}
		});
		bt.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					final Window wnd2 = ControlsFactory.CreateWindow("70%",
							"70%", true, true);
					utilsVaadin.showSearch(
							(VerticalLayout) wnd2.getContent(),
							new TableView.SelectionListener() {
								public void onSelection(TableView tv) {
									if (tv.getSelectionValue() < 0) {
										return;
									}
									txtMedicalno.setReadOnly(false);
									txtpatientName.setReadOnly(false);
									txtpatientTel.setReadOnly(false);
									txtBalance.setReadOnly(false);
									txtLastVisit.setReadOnly(false);
									double bal = 0;
									String lastvisit = "";
									String daysAgo = "";
									String mn = tv.getData().getFieldValue(
											tv.getSelectionValue(),
											"MEDICAL_NO")
											+ "";
									try {
										bal = Double.valueOf(QueryExe
												.getSqlValue(
														"select "
																+ "nvl(sum(debit-credit),0) from"
																+ " acvoucher2 where cust_code='"
																+ mn + "'",
														con, "0")
												+ "");
										lastvisit = QueryExe
												.getSqlValue(
														"select nvl(max(to_char(date_of_arrival,'dd/mm/rrrr')),'') from clq_visits where kov='DR' and medical_no='"
																+ mn + "'",
														con, "")
												+ "";
									} catch (NumberFormatException
											| SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									txtMedicalno.setValue(mn);
									txtpatientName.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"PATIENT_NAME"));
									txtpatientTel.setValue(tv.getData()
											.getFieldValue(
													tv.getSelectionValue(),
													"TEL")
											+ "");
									txtBalance
											.setValue((new DecimalFormat(
													Channelplus3Application
															.getInstance()
															.getFrmUserLogin().FORMAT_MONEY))
													.format(bal));
									txtLastVisit.setValue(lastvisit);
									txtDaysAgo.setValue("");
									if (!lastvisit.isEmpty()) {
										try {
											Date dt = sdf.parse(lastvisit);
											Date nowdt = new Date(System
													.currentTimeMillis());
											int d = utils
													.daysBetween(dt, nowdt);
											txtDaysAgo.setValue(d
													+ "  days ago !");
											System.out.println(d);
										} catch (Exception e) {
											e.printStackTrace();
										}

									}

									utilsVaadin.showMessage("Balance " + bal
											+ " ");
									txtMedicalno.setReadOnly(true);
									txtpatientName.setReadOnly(true);
									txtpatientTel.setReadOnly(true);
									txtBalance.setReadOnly(true);
									txtLastVisit.setReadOnly(true);

									if (Channelplus3Application.getInstance()
											.getMainWindow().getChildWindows()
											.contains(wnd2)) {
										Channelplus3Application.getInstance()
												.getMainWindow()
												.removeWindow(wnd2);
									}

								}
							},
							con,
							"select e_first_nm||' '||e_second_nm||' '||e_family_nm patient_name , "
									+ "medical_no,tel,mobile_no,id_no,e_mother_nm from clq_patients order by medical_no",
							true);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		txtProcedure.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if (txtProcedure.getValue() == null) {
					return;
				}
				double varPrice = Double.valueOf(utils.getSqlValue(
						"select price1 from items where reference='"
								+ ((dataCell) txtProcedure.getValue())
										.getValue() + "'", con));
				// txtPrice.setReadOnly(false);
				txtPrice.setValue((new DecimalFormat(Channelplus3Application
						.getInstance().getFrmUserLogin().FORMAT_MONEY))
						.format(varPrice));
				utilsVaadin.setValueByForce(txtDiscount, "0.000");
				utilsVaadin.setValueByForce(txtNetAmt, df.format(varPrice));
				// txtPrice.setReadOnly(true);

			}
		});

		btClose.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if (Channelplus3Application.getInstance().getMainWindow()
						.getChildWindows().contains(wnd)) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);
				}
				load_data();
			}
		});

		btAdd.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				String s = do_patient_exist_today(txtMedicalno,
						(Date) txtArrival.getValue());

				String sql = "begin INSERT INTO CLQ_VISITS  (KEYFLD, TIME_OF_ARRIVAL, MEDICAL_NO, "
						+ "DATE_OF_ARRIVAL, DRNO, FOLLOWUP_ITEM, FLAG, "
						+ " FOLLOWUP_FEES,FOLLOWUP_INV_TYPE,MEDICAL_INV_TYPE , FOLLOWUP_DISC) values "
						+ "((select nvl(max(keyfld),0)+1 from clq_visits), :TIME_OF_ARRIVAL, :MEDICAL_NO, "
						+ ":DATE_OF_ARRIVAL, :DRNO, :FOLLOWUP_ITEM, '1_CREATED', "
						+ " :PRICE ,:FOLLOWUP_INV_TYPE , :MEDICAL_INV_TYPE  , :FOLLOWUP_DISC ); "
						+ "update clq_patients set last_visit_date=:DATE_OF_ARRIVAL,"
						+ "last_doc=:DRNO where medical_no=:MEDICAL_NO ;  end; ";
				if (!isadd) {
					sql = " begin update clq_visits set time_of_arrival= :TIME_OF_ARRIVAL, "
							+ "MEDICAL_NO = :MEDICAL_NO , DATE_OF_ARRIVAL = :DATE_OF_ARRIVAL , "
							+ "DRNO= :DRNO , FOLLOWUP_ITEM = :FOLLOWUP_ITEM , FOLLOWUP_FEES= :PRICE , FOLLOWUP_DISC = :FOLLOWUP_DISC ,"
							+ " FOLLOWUP_INV_TYPE = :FOLLOWUP_INV_TYPE , MEDICAL_INV_TYPE = :MEDICAL_INV_TYPE "
							+ " WHERE KEYFLD =  '"
							+ kf
							+ "'; update clq_patients set last_visit_date=:DATE_OF_ARRIVAL,"
							+ "last_doc=:DRNO where medical_no=:MEDICAL_NO ;  end; ";
				}

				QueryExe qe = new QueryExe(sql, con);

				try {
					if (txtPInvType.getValue() == null
							|| txtPInvType.getValue().toString().isEmpty()) {
						throw new SQLException(
								"Must have value for procedure invoice type");
					}
					if (txtMInvType.getValue() == null
							|| txtMInvType.getValue().toString().isEmpty()) {
						throw new SQLException(
								"Must have value for medical invoice type");
					}
					con.setAutoCommit(false);
					qe.setParaValue("TIME_OF_ARRIVAL", new java.sql.Timestamp(
							((Date) txtArrival.getValue()).getTime()));
					qe.setParaValue("MEDICAL_NO", txtMedicalno.getValue());
					qe.setParaValue("DATE_OF_ARRIVAL", txtArrival.getValue());
					qe.setParaValue("DRNO", txtDrNo.getValue());
					qe.setParaValue("FOLLOWUP_ITEM", txtProcedure.getValue());
					qe.setParaValue("PRICE", txtPrice.getValue());
					qe.setParaValue("FOLLOWUP_INV_TYPE", txtPInvType.getValue());
					qe.setParaValue("MEDICAL_INV_TYPE", txtMInvType.getValue());
					qe.setParaValue("FOLLOWUP_DISC", txtDiscount.getValue());
					qe.execute();
					qe.close();
					con.commit();
					if (Channelplus3Application.getInstance().getMainWindow()
							.getChildWindows().contains(wnd)) {
						Channelplus3Application.getInstance().getMainWindow()
								.removeWindow(wnd);
					}
					load_data();

				} catch (SQLException ex) {
					ex.printStackTrace();
					parentLayout.getWindow().showNotification("",
							ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);

					try {
						con.rollback();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	protected String do_patient_exist_today(TextField txtMedicalno, Date value) {

		return "";
	}

	public void fill_visits_table() {
		utilsVaadin.query_data2(tbl_visit, data_visit, lstvisitCols);
	}

	public void fill_medical_table() {
		utilsVaadin.query_data2(tbl_medical_items, data_medical_items,
				lstMediCols);
	}

	public void fetch_medical_items() {
		try {
			if (tbl_visit.getValue() == null
					|| ((Integer) tbl_visit.getValue()) < 0) {
				return;
			}
			int rowno = ((Integer) tbl_visit.getValue());
			double on = -1;
			if (data_visit.getFieldValue(rowno, "ORD_NO") != null
					&& !data_visit.getFieldValue(rowno, "ORD_NO").toString()
							.isEmpty()) {
				on = Double.valueOf(data_visit.getFieldValue(rowno, "ORD_NO")
						.toString());
			}

			data_medical_items
					.executeQuery(
							"select order2.* ,(ord_price/ord_pack)* ord_allqty amount,"
									+ " items.descr from order2 , items where items.reference=ord_refer and "
									+ "ord_no='" + on
									+ "' and ord_code=111 order by ord_pos ",
							true);
			fill_medical_table();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	public void print_data() {

	}

	public void validate_data() throws Exception {
	}

	public void show_payment_method2(final boolean printInv) throws Exception {

		if (tbl_visit.getValue() == null)
			return;
		final Integer rn = (Integer) tbl_visit.getValue();
		if (data_visit.getFieldValue(rn, "PAY_KEYFLD") != null
				&& !data_visit.getFieldValue(rn, "PAY_KEYFLD").toString()
						.isEmpty())
			throw new SQLException("Posted invoice !");

		final Window wnd = ControlsFactory.CreateWindow("500px", "350px", true,
				true);
		FormLayoutManager frm = new FormLayoutManager("100%");
		wnd.setContent(frm);
		frm.setMargin(true);
		frm.setSpacing(true);

		final double kf = ((BigDecimal) data_visit.getFieldValue(rn, "KEYFLD"))
				.doubleValue();

		String cus = data_visit.getFieldValue(rn, "MEDICAL_NO").toString();
		wnd.setCaption(data_visit.getFieldValue(rn, "PATIENTS_NAME").toString());
		final List<FieldInfo> flds = new ArrayList<FieldInfo>();

		final String varLocation = "001";

		final DecimalFormat df = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);

		final double amt_tf = Double.valueOf(QueryExe.getSqlValue(
				"select FOLLOWUP_FEES from clq_visits where keyfld=" + kf, con,
				"")
				+ "");

		final double amt_tfdi = Double.valueOf(QueryExe.getSqlValue(
				"select FOLLOWUP_DISC from clq_visits where keyfld=" + kf, con,
				"")
				+ "");

		final double amt_tm = Double.valueOf(QueryExe.getSqlValue(
				"select ord_amt from clq_visits where keyfld=" + kf, con, "")
				+ "");
		final double amt_prv = Double.valueOf(QueryExe.getSqlValue(
				"select nvl(sum(debit-credit),0) from acvoucher2 "
						+ "where cust_code='" + cus + "'", con, "")
				+ "");

		final double amt_tot = (amt_tf - amt_tfdi) + amt_tm + amt_prv;

		final ComboBox txtType1 = ControlsFactory.CreateListField(null,
				"TYPE_1",
				"SELECT NO,DESCR FROM INVOICETYPE WHERE LOCATION_CODE='"
						+ varLocation + "' and accno is not null"
						+ " order  by no", flds);
		final ComboBox txtType2 = ControlsFactory.CreateListField(null,
				"TYPE_2",
				"SELECT NO,DESCR FROM INVOICETYPE WHERE LOCATION_CODE='"
						+ varLocation + "' and accno is not null"
						+ " order  by no", flds);

		final TextField txtAmt1 = ControlsFactory.CreateTextField(null,
				"AMT_1", flds, "0.000", Parameter.DATA_TYPE_NUMBER,
				utils.FORMAT_MONEY, true);

		final TextField txtAmt2 = ControlsFactory.CreateTextField(null,
				"AMT_2", flds, "0.000", Parameter.DATA_TYPE_NUMBER,
				utils.FORMAT_MONEY, true);

		final Label lblDue = new Label(df.format(amt_tot));
		lblDue.addStyleName("yellowLabel");

		Button bt = new Button("Update..");
		frm.removeAll();
		frm.addComponentsRow("caption=.,expand=3", bt, "width=100%,expand=1");

		frm.addComponentsRow("caption=Final payments,width=100px,align=center");

		frm.addComponentsRow("expand=1,caption=Fees,width=100px,align=left",
				"width=200px,expand=2,caption=" + df.format(amt_tf - amt_tfdi),
				"caption=.,expand=1");
		frm.addComponentsRow("expand=1,caption=Medical,width=100px,align=left",
				"width=200px,expand=2,caption=" + df.format(amt_tm),
				"caption=.,expand=1,align=right");
		frm.addComponentsRow(
				"expand=1,caption=Previous,width=100px,align=left",
				"width=200px,expand=2,caption=" + df.format(amt_prv),
				"caption=.,expand=1,align=right");

		frm.addLine();
		frm.addComponentsRow("expand=1,caption=Total,width=100px,align=left",
				"width=200px,expand=2,caption=" + df.format(amt_tot),
				"caption=.,expand=1,align=right");
		frm.addComponentsRow("caption=Due,width=100px,expand=1,align=left",
				lblDue, "expand=2,width=200px", "expand=1");

		frm.addLine();
		frm.addComponentsRow("caption=Payment 1,width=100%,expand=.5",
				txtType1, "expand=1.5", "caption=Amount,expand=.5,", txtAmt1,
				"width=100%,expand=1.5");

		frm.addComponentsRow("caption=Payment 2,width=100%,expand=.5",
				txtType2, "expand=1.5", "caption=Amount,expand=.5,", txtAmt2,
				"width=100%,expand=1.5");
		utilsVaadin.setDefaultValues(flds, true);
		txtType1.setImmediate(true);
		txtType2.setImmediate(true);
		txtAmt1.setImmediate(true);
		txtAmt2.setImmediate(true);

		txtType1.focus();

		txtType1.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				txtType2.setValue(null);
				txtAmt2.setValue("0.000");
				txtAmt1.setValue(df.format(amt_tot));
				try {
					double due = amt_tot
							- utilsVaadin
									.getFieldInfoDoubleValue(txtAmt1, flds);
					lblDue.setValue(df.format(due));
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});
		txtType2.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (txtType2.getValue() == null) {
					txtAmt2.setValue("0.000");
					return;
				}

				if (txtType1.getValue() == null) {
					txtType2.setValue(null);
					return;
				}

				try {
					double due = amt_tot
							- utilsVaadin
									.getFieldInfoDoubleValue(txtAmt1, flds);
					lblDue.setValue(df.format(due));
					txtAmt2.setValue(df.format(due));
					lblDue.setValue(df.format(0.0f));
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});
		txtAmt2.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					double due = amt_tot
							- (utilsVaadin.getFieldInfoDoubleValue(txtAmt1,
									flds) + utilsVaadin
									.getFieldInfoDoubleValue(txtAmt2, flds));
					lblDue.setValue(df.format(due));
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});
		txtAmt1.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				txtType2.setValue(null);
				txtAmt2.setValue("0.000");
				txtAmt2.setValue(df.format(0));
				try {
					double due = amt_tot
							- utilsVaadin
									.getFieldInfoDoubleValue(txtAmt1, flds);
					lblDue.setValue(df.format(due));

				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});
		bt.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					con.setAutoCommit(false);
					QueryExe qe = new QueryExe(
							"update clq_visits set PAY_TYPE_1= :P1 ,"
									+ "PAY_TYPE_2= :P2 ," + "PAY_AMT_1= :A1 , "
									+ "PAY_AMT_2= :A2 where keyfld= :KF", con);

					qe.setParaValue("P1", txtType1.getValue());
					qe.setParaValue("P2", txtType2.getValue());
					qe.setParaValue("A1",
							utilsVaadin.getFieldInfoDoubleValue(txtAmt1, flds));
					qe.setParaValue("A2",
							utilsVaadin.getFieldInfoDoubleValue(txtAmt2, flds));
					qe.setParaValue("KF", kf);

					qe.execute();
					qe.close();
					con.commit();

					event.getButton().setEnabled(false);
					if (Channelplus3Application.getInstance().getMainWindow()
							.getChildWindows().contains(wnd)) {
						Channelplus3Application.getInstance().getMainWindow()
								.removeWindow(wnd);
					}

					save_data(printInv);
				} catch (Exception e) {
					e.printStackTrace();
					wnd.showNotification("", e.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);

					try {
						con.rollback();
					} catch (SQLException e1) {
					}
				}
			}
		});
		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wnd)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wnd);
		}

	}

	public void show_payment_method(final boolean printInv) {

		final Integer rn = (Integer) tbl_visit.getValue();
		final Window wnd = ControlsFactory.CreateWindow("350px", "300px", true,
				true);
		final double kf = ((BigDecimal) data_visit.getFieldValue(rn, "KEYFLD"))
				.doubleValue();
		wnd.setCaption(data_visit.getFieldValue(rn, "PATIENTS_NAME").toString());

		final String varLocation = "001";
		DecimalFormat df = new DecimalFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_MONEY);
		double tf = ((BigDecimal) data_visit.getFieldValue(rn, "FOLLOWUP_FEES"))
				.doubleValue();
		double tm = ((BigDecimal) data_visit.getFieldValue(rn, "ORD_AMT"))
				.doubleValue();
		String fi = ((BigDecimal) data_visit.getFieldValue(rn,
				"FOLLOWUP_INV_TYPE")).toString();
		String mi = ((BigDecimal) data_visit.getFieldValue(rn,
				"MEDICAL_INV_TYPE")).toString();
		String s = "<html><b>Final Payment</b>"
				+ "<br><b>Total Fees = <font color=blue> " + df.format(tf)
				+ "</font>" + "<br> Total Medical = <font color=blue>"
				+ df.format(tm) + "</font></b></html>";
		final Label lbl_1 = new Label();
		final ComboBox txtPInvType = ControlsFactory.CreateListField(
				"Procedure Invoice type", "FOLLOWUP_INV_TYPE",
				"SELECT NO,DESCR FROM INVOICETYPE WHERE LOCATION_CODE='"
						+ varLocation + "' order  by no", null);
		final ComboBox txtMInvType = ControlsFactory.CreateListField(
				"Medical Invoice type", "FOLLOWUP_INV_TYPE",
				"SELECT NO,DESCR FROM INVOICETYPE WHERE LOCATION_CODE='"
						+ varLocation + "' order  by no", null);

		Button bt = new Button("Update..");

		lbl_1.setContentMode(Label.CONTENT_XHTML);
		lbl_1.setValue(s);
		txtPInvType.setValue(utilsVaadin.findByValue(txtPInvType, fi));
		txtMInvType.setValue(utilsVaadin.findByValue(txtMInvType, mi));

		((VerticalLayout) wnd.getContent()).setSpacing(true);
		((VerticalLayout) wnd.getContent()).setMargin(true);

		ResourceManager.addComponent(wnd.getContent(), lbl_1);
		ResourceManager.addComponent(wnd.getContent(), txtPInvType);
		ResourceManager.addComponent(wnd.getContent(), txtMInvType);
		ResourceManager.addComponent(wnd.getContent(), bt);

		txtMInvType.focus();

		bt.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					con.setAutoCommit(false);
					QueryExe qe = new QueryExe(
							"update clq_visits set followup_inv_type= :FI ,"
									+ "MEDICAL_INV_TYPE= :MI where keyfld= :KF",
							con);
					qe.setParaValue("FI", txtPInvType.getValue());
					qe.setParaValue("MI", txtMInvType.getValue());
					qe.setParaValue("KF", kf);
					qe.execute();
					qe.close();
					con.commit();
					event.getButton().setEnabled(false);

					if (Channelplus3Application.getInstance().getMainWindow()
							.getChildWindows().contains(wnd)) {
						Channelplus3Application.getInstance().getMainWindow()
								.removeWindow(wnd);
					}

					save_data(printInv);

				} catch (SQLException e) {
					e.printStackTrace();
					wnd.showNotification("", e.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
					try {
						con.rollback();
					} catch (SQLException e1) {
					}

				}
			}
		});
		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wnd)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wnd);
		}

	}

	public void save_data() {
		save_data(false);
	}

	public void save_data(boolean print) {
		if (tbl_visit.getValue() == null
				|| ((Integer) tbl_visit.getValue()) < 0) {
			parentLayout.getWindow().showNotification(
					"Must select visited patient");
			return;
		}

		final Integer rn = (Integer) tbl_visit.getValue();
		String kf = data_visit.getFieldValue(rn, "KEYFLD").toString();

		String saleinv = utils.getSqlValue(
				"select saleinv||saleinv_medical from clq_visits where keyfld="
						+ kf, con);

		if (!saleinv.isEmpty()) {
			parentLayout.getWindow().showNotification(
					"Invoice already created !",
					Notification.TYPE_ERROR_MESSAGE);
			return;
		}
		try {
			con.setAutoCommit(false);
			utils.execSql("begin post_visit('" + kf + "');end;", con);
			con.commit();
			parentLayout.getWindow().showNotification("Invoice posted !");
			load_data();
		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification("", ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			try {
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void save_data_items() throws SQLException {

	}

	public void showInitView() {
		QRYSES = "";
		initForm();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	private void unpost(final double kf, int rn) throws SQLException {
		final Window wndx = ControlsFactory.CreateWindow("400px", "300px",
				true, true);
		String pn = utils
				.nvl(data_visit.getFieldValue(rn, "PATIENTS_NAME"), "");

		wndx.setCaption(pn);
		double rk = -.1, s_m = -.1, s = -.1; // .......s.keyflds..for..recipt..sale.inv..sale..inv..medical..invoice
		double ramt, s_mamt, samt = 0;// amounts
		TextField txtrk = new TextField();
		TextField txts_m = new TextField();
		TextField txts = new TextField();
		Button bt = new Button("Delete..");

		FormLayoutManager fm = new FormLayoutManager();
		fm.setMargin(true);
		fm.setSpacing(true);
		wndx.setContent(fm);
		fm.addComponentsRow("caption=Following entries will be removed !");
		fm.addComponentsRow("caption=Recipt JV,expand=1.5", txtrk,
				"expand=2.5,width=100%");
		fm.addComponentsRow("caption=Medical Amt,expand=1.5", txts_m,
				"expand=2.5,width=100%");
		fm.addComponentsRow("caption=Procedure Amt,expand=1.5", txts,
				"expand=2.5,width=100%");
		fm.addComponentsRow("height=30px");
		fm.addComponentsRow(bt,
				"align_text=left,caption=Delete/Unpost,width=200px");

		txtrk.addStyleName("netAmtStyle yellowField");
		txts.addStyleName("netAmtStyle yellowField");
		txts_m.addStyleName("netAmtStyle yellowField");

		txts.setReadOnly(true);
		txtrk.setReadOnly(true);
		txts_m.setReadOnly(true);

		utilsVaadin.setValueByForce(txts, "0.000");
		utilsVaadin.setValueByForce(txtrk, "0.000");
		utilsVaadin.setValueByForce(txts_m, "0.000");

		ResultSet rs = QueryExe.getSqlRS(
				"select *from clq_visits where keyfld=" + kf, con);
		if (rs == null)
			throw new SQLException("Visit may be removed ! keyfld=" + kf);

		rs.first();
		rk = Double.valueOf(QueryExe.getSqlValue(
				"select nvl(max(keyfld),-.1) from acvoucher2 where "
						+ "refercode=20009 and referkeyfld=" + kf, con, "-.1")
				+ "");
		if (rs.getString("SALEINV") != null
				&& !rs.getString("SALEINV").isEmpty())
			s = rs.getDouble("SALEINV");
		if (rs.getString("ORD_NO") != null && !rs.getString("ORD_NO").isEmpty())
			s_m = rs.getDouble("ORD_NO");
		rs.close();

		ramt = Double.valueOf(QueryExe.getSqlValue(
				"select sum(debit) from acvoucher2 where keyfld='" + rk + "'",
				con, "0")
				+ "");
		samt = Double.valueOf(QueryExe.getSqlValue(
				"select inv_amt-disc_amt from pur1 where keyfld='" + s + "'",
				con, "0")
				+ "");
		s_mamt = Double.valueOf(QueryExe.getSqlValue(
				"select ORD_AMT-ORD_DISCAMT from ORDER1 where ORD_NO='" + s_m
						+ "' AND ORD_CODE=111", con, "0")
				+ "");
		DecimalFormat df = new DecimalFormat(utils.FORMAT_MONEY);
		utilsVaadin.setValueByForce(txtrk, df.format(ramt));
		utilsVaadin.setValueByForce(txts, df.format(samt));
		utilsVaadin.setValueByForce(txts_m, df.format(s_mamt));

		final double frkf = rk, fs = s, fs_m = s_m;

		bt.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					con.setAutoCommit(false);
					QueryExe.execute("begin unpost_visit(" + kf + "); end;",
							con);
					con.commit();
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wndx);
					load_data();
					utilsVaadin.showMessage("Unposted successfully");
				} catch (SQLException e) {

					e.printStackTrace();
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification("", e.getMessage(),
									Notification.TYPE_ERROR_MESSAGE);
					try {
						con.rollback();
					} catch (SQLException e1) {
					}
				}
			}
		});
		//
		// parentLayout.getWindow().addWindow(
		// new YesNoDialog("Confirmation",
		// "Do you want to remove sales and unpost ?  # " + kf
		// + " #", cb, "350px", "-1px"));

	}

	private void delete_visit(final double kf, int rn) {
		Callback cb = new Callback() {

			@Override
			public void onDialogResult(boolean resultIsYes) {
				if (resultIsYes) {
					ResultSet rs;
					try {
						double on = -.1;
						rs = QueryExe.getSqlRS(
								"select *from clq_visits where keyfld=" + kf,
								con);

						if (rs == null)
							throw new SQLException(
									"Visit may be removed ! keyfld=" + kf);
						rs.first();
						if (rs.getString("SALEINV") != null
								&& !rs.getString("SALEINV").isEmpty())
							throw new SQLException("Invoice existed " + kf);

						if (rs.getString("ORD_NO") != null
								&& !rs.getString("ORD_NO").isEmpty())
							on = rs.getDouble("ord_no");
						con.setAutoCommit(false);
						QueryExe.execute(
								" begin "
										+ " delete from order1 where ord_no= :ON and ord_code=111 ;"
										+ " delete from order2 where ord_no = :ON and ord_code=111 ; "
										+ " delete from CLQ_TEXTS_VALUES where visit_keyfld= :KF ;"
										+ " delete from CLQ_PICS where visit_keyfld = :KF; "
										+ " delete from clq_visits where keyfld = :KF ; "
										+ " end;  ", con, new Parameter("KF",
										kf), new Parameter("ON", on));

						rs.close();
						con.commit();
						load_data();
						utilsVaadin.showMessage("Deleted successfully !");
					} catch (SQLException e) {
						e.printStackTrace();
						Channelplus3Application
								.getInstance()
								.getMainWindow()
								.showNotification("", e.getMessage(),
										Notification.TYPE_ERROR_MESSAGE);
						try {
							con.rollback();
						} catch (SQLException e1) {
						}
					}

				}
			}
		};
		String pn = utils
				.nvl(data_visit.getFieldValue(rn, "PATIENTS_NAME"), "");
		parentLayout.getWindow().addWindow(
				new YesNoDialog("Confirmation",
						"Do you want to remove visit for ' " + pn + " '  ?  # "
								+ kf + " #", cb, "350px", "-1px"));
	}
}
