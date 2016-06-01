package com.windows.clinics;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.doc.views.TableView;
import com.doc.views.YesNoDialog;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog.Callback;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.DBClass;
import com.generic.FieldInfo;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;
import com.windows.frmLists;

public class frmVisitRecords implements transactionalForm {
	private Connection con = null;
	private Window wndList = new Window();
	private VerticalLayout lstLayout = new VerticalLayout();

	private localTableModel data_ = new localTableModel();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout headLayout = new HorizontalLayout();
	private HorizontalLayout head1Layout = new HorizontalLayout();
	private VerticalLayout consultLayout = new VerticalLayout();
	private VerticalLayout planLayout = new VerticalLayout();
	private VerticalLayout clinicalLayout = new VerticalLayout();
	private VerticalLayout radioLayout = new VerticalLayout();
	private VerticalLayout labLayout = new VerticalLayout();
	private VerticalLayout medLayout = new VerticalLayout();
	private VerticalLayout otherLayout = new VerticalLayout();
	private Timer tm = null;

	private Label lbl_txts = new Label("", Label.CONTENT_XHTML);

	private List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();
	private List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	private List<TextRemark> lstConsultTxts = new ArrayList<TextRemark>();
	private List<TextRemark> lstPlanTxts = new ArrayList<TextRemark>();
	private Map<String, TextRemark> mapConsultTxts = new HashMap<String, TextRemark>();
	private Map<String, TextRemark> mapPlanTxts = new HashMap<String, TextRemark>();
	private NativeButton cmdList = ControlsFactory.CreateCustomButton("Visits",
			"img/details.png", "Show list", "");
	private NativeButton cmdItemHistory = ControlsFactory.CreateCustomButton(
			"Details", "img/details.png", "Show list", "");
	private NativeButton cmdPrintConsultation = ControlsFactory
			.CreateToolbarButton("img/pdf.png", "Print consultation..");
	private NativeButton cmdPrintPlan = ControlsFactory.CreateToolbarButton(
			"img/pdf.png", "Print Plan..");
	private TextField txtMedicalNo = ControlsFactory.CreateTextField(
			"Medical No", "MED_NO", lstfldinfo);

	private TextField txtPatientName = ControlsFactory.CreateTextField(
			"Patient Name", "PTNM", lstfldinfo);
	private TextField txtVisitDate = ControlsFactory.CreateTextField(
			"Visit Date", "", null);
	private TextField txtDrName = ControlsFactory.CreateTextField("Dr Name",
			"drname", null);
	private Label lbl = new Label();
	private Date varDt = new Date(System.currentTimeMillis());
	private TabSheet tbs = new TabSheet();

	public String QRYSES = "";
	private boolean listnerAdded = false;

	private ComboBox picsList[] = { new ComboBox(), new ComboBox(),
			new ComboBox(), new ComboBox() };
	private VerticalLayout picsMainlayout[] = { radioLayout, labLayout,
			medLayout, otherLayout };

	private HorizontalLayout picsBLayout[] = { new HorizontalLayout(),
			new HorizontalLayout(), new HorizontalLayout(),
			new HorizontalLayout() };

	private HorizontalLayout picsPages[] = { new HorizontalLayout(),
			new HorizontalLayout(), new HorizontalLayout(),
			new HorizontalLayout() };

	private HorizontalLayout picsFLayout[] = { new HorizontalLayout(),
			new HorizontalLayout(), new HorizontalLayout(),
			new HorizontalLayout() };

	private FileResource picsResource[] = new FileResource[4];
	private Embedded picsemb[] = new Embedded[4];

	private NativeButton picsCmdAdd[] = {
			ControlsFactory.CreateToolbarButton("img/newmenu.png", "Add pic.."),
			ControlsFactory.CreateToolbarButton("img/newmenu.png", "Add pic.."),
			ControlsFactory.CreateToolbarButton("img/newmenu.png", "Add pic.."),
			ControlsFactory.CreateToolbarButton("img/newmenu.png", "Add pic..") };
	private NativeButton picsCmdDel[] = {
			ControlsFactory.CreateToolbarButton("img/remove.png",
					"Delete image.."),
			ControlsFactory.CreateToolbarButton("img/remove.png",
					"Delete image.."),
			ControlsFactory.CreateToolbarButton("img/remove.png",
					"Delete image.."),
			ControlsFactory.CreateToolbarButton("img/remove.png",
					"Delete image..") };
	private NativeButton picsCmdZoomIn[] = {
			ControlsFactory.CreateToolbarButton("img/expand.png", "Zoom in.."),
			ControlsFactory.CreateToolbarButton("img/expand.png", "Zoom in.."),
			ControlsFactory.CreateToolbarButton("img/expand.png", "Zoom in.."),
			ControlsFactory.CreateToolbarButton("img/expand.png", "Zoom in..") };
	private NativeButton picsCmdZoomOut[] = {
			ControlsFactory.CreateToolbarButton("img/collapse.png",
					"Zoom Out.."),
			ControlsFactory.CreateToolbarButton("img/collapse.png",
					"Zoom Out.."),
			ControlsFactory.CreateToolbarButton("img/collapse.png",
					"Zoom Out.."),
			ControlsFactory.CreateToolbarButton("img/collapse.png",
					"Zoom Out..") };

	private NativeButton picsCmdLists[] = {
			ControlsFactory.CreateToolbarButton("img/details.png", "List"),
			ControlsFactory.CreateToolbarButton("img/details.png", "List"),
			ControlsFactory.CreateToolbarButton("img/details.png", "Lis"),
			ControlsFactory.CreateToolbarButton("img/details.png", "List") };

	private Panel picspnl[] = { new Panel(null, new VerticalLayout()),
			new Panel(null, new VerticalLayout()),
			new Panel(null, new VerticalLayout()),
			new Panel(null, new VerticalLayout()) };
	private PicPage picsCurrentPage[] = new PicPage[4];
	private String pics_group_name[] = { "RADIO", "LABS", "MEDICAL", "VARIOUS" };

	public frmVisitRecords() {

	}

	// -1 for all
	public void resetPicLayout(int pn) {
		int frompg = (pn < 0 ? 0 : pn);
		int topg = (pn < 0 ? 4 : pn + 1);
		for (int i = 0; i < picsCurrentPage.length; i++) {
			picsCurrentPage[i] = null;
		}

		for (int i = frompg; i < topg; i++) {

			picsPages[i].removeAllComponents();
			if (picsemb[i] != null) {
				try {
					picspnl[i].removeComponent(picsemb[i]);
				} catch (Exception ex) {

				}
			}
		}
	}

	public void resetFormLayout() {
		headLayout.removeAllComponents();
		mainLayout.removeAllComponents();
		consultLayout.removeAllComponents();
		planLayout.removeAllComponents();
		radioLayout.removeAllComponents();
		labLayout.removeAllComponents();
		medLayout.removeAllComponents();
		otherLayout.removeAllComponents();
		tbs.removeAllComponents();

	}

	public void createView() {
		utilsVaadin.showMessage(
				parentLayout
						.getWindow(),
				"great.",
				Notification.TYPE_ERROR_MESSAGE);

		System.gc();
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		mainLayout.setSpacing(true);
		mainLayout.setSizeFull();
		centralPanel.setSizeFull();
		tbs.setSizeFull();
		consultLayout.setWidth("100%");
		planLayout.setWidth("100%");
		headLayout.setWidth("80%");
		head1Layout.setWidth("100%");
		txtDrName.setWidth("100%");
		txtMedicalNo.setWidth("100%");
		txtPatientName.setWidth("100%");
		txtVisitDate.setWidth("100%");
		headLayout.addStyleName("headpanel");
		head1Layout.addStyleName("headpanel");
		txtMedicalNo.addStyleName("headpanel");
		txtVisitDate.addStyleName("headpanel");
		txtDrName.addStyleName("headpanel");
		txtPatientName.addStyleName("headpanel");
		radioLayout.setSizeFull();
		labLayout.setSizeFull();
		medLayout.setSizeFull();
		otherLayout.setSizeFull();

		tbs.setSizeFull();
		lbl.setContentMode(Label.CONTENT_XHTML);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, head1Layout);
		ResourceManager.addComponent(mainLayout, lbl_txts);
		ResourceManager.addComponent(head1Layout, headLayout);
		ResourceManager.addComponent(headLayout, txtDrName);
		ResourceManager.addComponent(headLayout, txtMedicalNo);
		ResourceManager.addComponent(headLayout, txtPatientName);
		ResourceManager.addComponent(headLayout, txtVisitDate);
		ResourceManager.addComponent(headLayout, cmdList);
		ResourceManager.addComponent(headLayout, cmdItemHistory);
		ResourceManager.addComponent(mainLayout, tbs);

		tbs.addTab(consultLayout, "Consultation", null);
		tbs.addTab(planLayout, "Plan", null);
		tbs.addTab(clinicalLayout, "Clinical Exam.", null);
		tbs.addTab(radioLayout, "Radiology", null);
		tbs.addTab(labLayout, "Lab. Investigation", null);
		tbs.addTab(medLayout, "Medical Reports", null);
		tbs.addTab(otherLayout, "Other Reports", null);

		mainLayout.setExpandRatio(head1Layout, .1f);
		mainLayout.setExpandRatio(tbs, 3.9f);

		headLayout.setExpandRatio(txtMedicalNo, .5f);
		headLayout.setExpandRatio(txtPatientName, 1.5f);
		headLayout.setExpandRatio(txtDrName, 1.5f);
		headLayout.setExpandRatio(txtVisitDate, .5f);

		headLayout.setComponentAlignment(cmdList, Alignment.BOTTOM_RIGHT);
		headLayout
				.setComponentAlignment(cmdItemHistory, Alignment.BOTTOM_RIGHT);

		mainLayout.addComponent(lbl);

		if (!listnerAdded) {
			cmdPrintConsultation.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						print_patientC(txtMedicalNo.getValue() + "",
								"/reports/rep_medicalreport_1.jasper", con);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});

			cmdPrintConsultation.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						print_patientC(txtMedicalNo.getValue() + "",
								"/reports/rep_medicalreport_1.jasper", con);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});
			cmdPrintPlan.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						print_patientC(txtMedicalNo.getValue() + "",
								"/reports/rep_medicalreport_plan.jasper", con);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});
			consultLayout.addListener(layoutClicker);
			planLayout.addListener(layoutClicker);

			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_list();
				}
			});
			cmdItemHistory.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_item_info("REP_HISTORY_OF_PRESENT_ILLNESS");
				}
			});
			try {
				createReportPanels();
			} catch (Exception ex) {
				utilsVaadin.showMessage(ex.getMessage());
				ex.printStackTrace();
			}
			listnerAdded = true;
		}
	}

	public void createReportPanels() throws Exception {
		for (int i = 0; i < 4; i++) {

			picspnl[i].setSizeFull();
			// picspnl[i].setWidth("100%");
			picspnl[i].getContent().setSizeUndefined();
			picspnl[i].setScrollable(true);
			picsBLayout[i].removeAllComponents();
			picsPages[i].setSpacing(true);
			// picsBLayout[i].setWidth("100%");
			picsList[i].setImmediate(true);

			// picsBLayout[i].addStyleName("toolpane_light");

			ResourceManager.addComponent(picsMainlayout[i], picsBLayout[i]);
			ResourceManager.addComponent(picsBLayout[i], picsFLayout[i]);
			ResourceManager.addComponent(picsFLayout[i], picsList[i]);
			ResourceManager.addComponent(picsFLayout[i], picsCmdLists[i]);
			ResourceManager.addComponent(picsFLayout[i], picsCmdAdd[i]);
			ResourceManager.addComponent(picsFLayout[i], picsCmdDel[i]);
			ResourceManager.addComponent(picsFLayout[i], picsCmdZoomIn[i]);
			ResourceManager.addComponent(picsFLayout[i], picsCmdZoomOut[i]);
			ResourceManager.addComponent(picsBLayout[i], picsPages[i]);
			ResourceManager.addComponent(picsMainlayout[i], picspnl[i]);

			picsMainlayout[i].setExpandRatio(picsBLayout[i], .1f);
			picsMainlayout[i].setExpandRatio(picspnl[i], 3.9f);

			final int pn = i;

			utilsVaadin.FillCombo(picsList[i],
					"select descr,descr d from relists where idlist='MEDICAL_REPORT_"
							+ (i + 1) + "' order by descr", con);
			picsCmdZoomIn[i].addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					zoom_pic(pn, 50);

				}
			});
			picsCmdZoomOut[i].addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					zoom_pic(pn, -50);
				}
			});
			picsCmdDel[i].addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.addWindow(
									new YesNoDialog("Channel Plus Warning",
											"Delete this image  ?",
											new Callback() {
												public void onDialogResult(
														boolean resultIsYes) {
													if (!resultIsYes) {
														return;
													}

													if (picsList[pn].getValue() == null) {
														utilsVaadin.showMessage(
																parentLayout
																		.getWindow(),
																"Select image page first..",
																Notification.TYPE_ERROR_MESSAGE);
														return;
													}
													double posno = -1;
													PicPage p = null;
													for (Iterator iterator = picsPages[pn]
															.getComponentIterator(); iterator
															.hasNext();) {
														NativeButton l = (NativeButton) iterator
																.next();
														if (l.getCaption()
																.toString()
																.contains("(")
																&& l.getCaption()
																		.toString()
																		.contains(
																				")")) {
															posno = ((PicPage) l
																	.getData()).pos;
															p = ((PicPage) l
																	.getData());

														}
													}
													if (posno == -1) {
														utilsVaadin.showMessage(
																parentLayout
																		.getWindow(),
																"Select image page first..",
																Notification.TYPE_ERROR_MESSAGE);
														return;
													} else {
														try {
															con.setAutoCommit(false);
															utils.execSql(
																	"delete from clq_pics where visit_keyfld='"
																			+ p.keyfld
																			+ "' and pos='"
																			+ posno
																			+ "' and descr='"
																			+ p.descr
																			+ "' and group_name='"
																			+ pics_group_name[pn]
																			+ "'",
																	con);
															con.commit();
															load_pics(pn);
															utilsVaadin
																	.showMessage(
																			parentLayout
																					.getWindow(),
																			"Deleted Successfully",
																			Notification.TYPE_HUMANIZED_MESSAGE);

														} catch (Exception ex) {
															ex.printStackTrace();
															try {
																con.rollback();
															} catch (SQLException ex2) {

															}

														}

													}

												}
											}));
				}
			});
			picsCmdAdd[i].addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					add_new_pic(pn);
				}
			});

			picsCmdLists[i].addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

					Window wnd = ControlsFactory.CreateWindow("500px", "600px",
							true, true);
					VerticalLayout l = (VerticalLayout) wnd.getContent();
					frmLists f = new frmLists();
					f.setParentLayout(l);
					f.showStandAlone("MEDICAL_REPORT_" + (pn + 1));
					wnd.addListener(new CloseListener() {

						@Override
						public void windowClose(CloseEvent e) {
							try {
								utilsVaadin
										.FillCombo(picsList[pn],
												"select descr,descr d from relists where idlist='MEDICAL_REPORT_"
														+ (pn + 1)
														+ "' order by descr",
												con);
							} catch (SQLException e1) {
								e1.printStackTrace();
							}

						}
					});

				}
			});

			/*
			 * picsPages[i].addListener(new LayoutClickListener() {
			 * 
			 * public void layoutClick(LayoutClickEvent event) { if
			 * (event.getChildComponent() == null && !(event.getChildComponent()
			 * instanceof Label)) { return; } Label l = (Label)
			 * event.getChildComponent(); PicPage pgdt = (PicPage) l.getData();
			 * for (Iterator iterator = ((HorizontalLayout) event
			 * .getChildComponent().getParent()) .getComponentIterator();
			 * iterator.hasNext();) { Label ll = (Label) iterator.next();
			 * ll.setValue(((PicPage) ll.getData()).no + ""); } l.setValue("(" +
			 * pgdt.no + ")"); try { show_pic(pgdt); } catch (Exception e) {
			 * utilsVaadin.showMessage(e.getMessage()); e.printStackTrace(); }
			 * 
			 * } });
			 */
			picsList[i].addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						load_pics(pn);
					} catch (Exception ex) {
						ex.printStackTrace();
						utilsVaadin.showMessage(ex.getMessage());
					}
				}
			});

		}
	}

	protected void delete_pic(int pn) {

	}

	protected void zoom_pic(int pn, int size) {
		if (picspnl[pn].getComponentIterator().hasNext()) {
			Object em = picspnl[pn].getComponentIterator().next();
			if (em instanceof Embedded) {
				float h = ((Embedded) em).getHeight();
				h += size;
				if (h < 100) {
					h = 100;
				}
				((Embedded) em).setHeight(h + "px");
			}

		}
	}

	public void load_pics(int pn) throws Exception {
		int frompg = (pn < 0 ? 0 : pn);
		int topg = (pn < 0 ? 4 : pn + 1);

		for (int i = frompg; i < topg; i++) {
			resetPicLayout(pn);
			if (picsList[i].getValue() == null) {
				continue;
			}
			ResultSet rspg = utils.getSqlRS(
					"select visit_keyfld,pos,dat,descr,rownum rn from clq_pics where descr='"
							+ ((dataCell) picsList[i].getValue()).getDisplay()
							+ "' and group_name='" + pics_group_name[pn]
							+ "' and medical_no='" + txtMedicalNo.getValue()
							+ "' order by dat,pos", con);
			if (rspg == null) {
				return;
			}
			rspg.beforeFirst();
			int rn = 0;
			NativeButton l = null;
			SimpleDateFormat df = new SimpleDateFormat(utils.FORMAT_SHORT_DATE);
			while (rspg.next()) {
				l = ControlsFactory.CreateCustomButton((++rn) + "", null,
						df.format(rspg.getDate("dat")), "toolbar");
				l.setData(new PicPage(rn, rspg.getDouble("visit_keyfld"), rspg
						.getDouble("pos"), rspg.getDate("dat"), rspg
						.getString("descr"), i));

				picsPages[i].addComponent(l);
				picsCurrentPage[i] = (PicPage) l.getData();
				picsPages[i].setComponentAlignment(l, Alignment.MIDDLE_LEFT);
				l.addListener(new ClickListener() {

					public void buttonClick(ClickEvent event) {

						NativeButton l = (NativeButton) event.getButton();
						PicPage pgdt = (PicPage) l.getData();
						for (Iterator iterator = ((HorizontalLayout) event
								.getComponent().getParent())
								.getComponentIterator(); iterator.hasNext();) {
							NativeButton ll = (NativeButton) iterator.next();
							ll.setCaption(((PicPage) ll.getData()).no + "");
						}
						l.setCaption("(" + pgdt.no + ")");
						try {
							show_pic(pgdt);
						} catch (Exception e) {
							utilsVaadin.showMessage(e.getMessage());
							e.printStackTrace();
						}

					}
				});
			}

			l.setCaption("(" + picsCurrentPage[i].no + ")");
			show_pic(picsCurrentPage[i]);
		}
	}

	private LayoutClickListener layoutClicker = new LayoutClickListener() {
		public void layoutClick(LayoutClickEvent event) {
			if (event.getChildComponent() != null
					&& event.getChildComponent() instanceof TextField
					&& event.isDoubleClick()) {
				TextField t = ((TextField) event.getChildComponent());
				List<TextRemark> lst = new ArrayList<TextRemark>();
				lst.addAll(lstConsultTxts);
				lst.addAll(lstPlanTxts);
				TextRemark tr = find_tr(lst, t);
				show_selectionlist(tr);

			}

		}
	};

	public void createViewTexts() {
		consultLayout.removeAllComponents();
		planLayout.removeAllComponents();
		consultLayout.addComponent(cmdPrintConsultation);
		planLayout.addComponent(cmdPrintPlan);
		try {
			fetch_texts("C", lstConsultTxts);
			fetch_texts("P", lstPlanTxts);
			mapConsultTxts = getMapOfList(lstConsultTxts);
			mapPlanTxts = getMapOfList(lstPlanTxts);
			List<TextRemark> lst = new ArrayList<TextRemark>();
			lst.addAll(lstConsultTxts);
			lst.addAll(lstPlanTxts);
			VerticalLayout l = null;
			for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
				final TextRemark tr = (TextRemark) iterator.next();
				TextField tf = new TextField(tr.caption);
				NativeButton bt = ControlsFactory.CreateCustomButton(null,
						"img/details.png", "Show list", "");

				tf.setValue(parse_texts(tr.default_value));
				if (tr.height > 25) {
					tf.setHeight(tr.height + "px");
				}
				tf.setWidth("100%");
				tr.obj = tf;
				tf.setTextChangeEventMode(TextChangeEventMode.LAZY);
				tf.setTextChangeTimeout(800);
				l = (tr.grp.equals("C") ? consultLayout : planLayout);
				HorizontalLayout h = new HorizontalLayout();
				h.setSpacing(false);
				h.setWidth("100%");
				h.addComponent(tf);
				h.addComponent(bt);
				h.setExpandRatio(tf, 3.9f);
				h.setExpandRatio(bt, .1f);
				l.addComponent(h);
				l.setComponentAlignment(h, Alignment.BOTTOM_CENTER);
				h.setComponentAlignment(bt, Alignment.BOTTOM_CENTER);
				h.addListener(layoutClicker);
				tf.addListener(tc);
				tf.setInputPrompt("Double click here to show list , check list "
						+ tr.list_name);
				tf.setImmediate(true);

				bt.addListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						Window wnd = ControlsFactory.CreateWindow("500px",
								"600px", true, true);
						VerticalLayout l = (VerticalLayout) wnd.getContent();
						frmLists f = new frmLists();
						f.setParentLayout(l);
						f.showStandAlone(tr.list_name);
					}
				});
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	protected void show_selectionlist(final TextRemark tr) {
		try {
			final TextField t = (TextField) tr.obj;
			VerticalLayout listlayout = new VerticalLayout();
			Window wndList = new Window("Select Columns here");
			wndList.setWidth("70%");
			wndList.setHeight("70%");
			wndList.setImmediate(true);
			wndList.setModal(true);
			wndList.setContent(listlayout);
			listlayout.setSizeFull();
			listlayout.removeAllComponents();
			final DBClass dbcx = new DBClass(con);			
			final TableView tbl = new TableView(dbcx);			
			tbl.setParentPanel(listlayout);
			tbl.setCheckDefaultSelection(false);
			tbl.setCheckSelectionField(true);
			tbl.setEditable(true);
			tbl.getListInputCols().add("DESCR2");
			tbl.setScannedStrUpdateList(t.getValue() + "");
			tbl.FetchSql("select  descr , '' descr2 from relists where idlist='"
					+ tr.list_name + "' order by descr");

			wndList.addListener(new CloseListener() {
				List<String> hd = new ArrayList<String>();

				public void windowClose(CloseEvent e) {
					String s = "";
					for (int i = 0; i < tbl.getLstCheckSelection().size(); i++) {
						if (tbl.getLstCheckSelection().get(i).booleanValue()) {
							String d2 = utils.nvl(
									tbl.getData().getFieldValue(i, "DESCR2"),
									"").trim();
							s = s + (s.isEmpty() ? "" : ",")
									+ tbl.getData().getFieldValue(i, "DESCR")
									+ (d2.isEmpty() ? "" : "--") + d2 + " ";
						}
					}
					if (!s.trim().isEmpty()) {
						t.setValue(s);
						call_timer(t, s);
					}
				}
			});

			tbl.createView();

			if (!Channelplus3Application.getInstance().getMainWindow()
					.getChildWindows().contains(wndList)) {
				Channelplus3Application.getInstance().getMainWindow()
						.addWindow(wndList);
			}
			wndList.setCaption(tbl.getData().getRows().size()
					+ " Rows selected");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public Map<String, TextRemark> getMapOfList(List<TextRemark> lst) {
		Map<String, TextRemark> tmap = new HashMap<String, TextRemark>();
		for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
			TextRemark tr = (TextRemark) iterator.next();
			tmap.put(tr.txtID, tr);
		}
		return tmap;

	}

	public void fetch_texts_values(String grp, Map<String, TextRemark> mp)
			throws SQLException {
		if (!QRYSES.isEmpty() || txtMedicalNo.getValue() != null) {
			PreparedStatement ps = con
					.prepareStatement(
							"select clq_texts_def.tb_id,"
									+ "clq_texts_values.tb_value "
									+ "from clq_texts_values,clq_texts_def "
									+ " where  clq_texts_def.tb_id(+)=clq_texts_values.tb_id and"
									+ " medical_no='" + txtMedicalNo.getValue()
									+ "' and " + "visit_keyfld='" + QRYSES
									+ "' and group_name='" + grp + "'",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = ps.executeQuery();
			rs.beforeFirst();
			while (rs.next()) {
				if (mp.get(rs.getString("TB_ID")).obj != null) {
					TextField tf = ((TextField) mp.get(rs.getString("TB_ID")).obj);
					String pv = parse_texts(mp.get(rs.getString("TB_ID")).default_value);
					tf.setValue(utils.nvl(rs.getString("TB_VALUE"), pv));
					mp.get(rs.getString("TB_ID")).value = utils.nvl(
							rs.getString("TB_VALUE"), pv);

				}
			}
			ps.close();

		}
	}

	public void fetch_texts(String grp, List<TextRemark> lst)
			throws SQLException {
		lst.clear();
		PreparedStatement ps = con.prepareStatement(
				"select *from clq_texts_def where " + " group_name='" + grp
						+ "' order by pos", ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = ps.executeQuery();
		rs.beforeFirst();

		while (rs.next()) {
			TextRemark tr = new TextRemark();
			tr.pos = rs.getInt("pos");
			tr.color = utils.nvl(rs.getString("tb_color"), "");
			tr.copy_last = rs.getString("copy_last");
			tr.default_value = utils.nvl(rs.getString("default_text"), "");
			tr.height = rs.getInt("tb_height");
			tr.txtID = rs.getString("tb_id");
			tr.caption = rs.getString("tb_caption");
			tr.list_name = rs.getString("list_name");
			tr.grp = grp;
			lst.add(tr);
		}
		ps.close();
	}

	private boolean running_timer = false;
	public TextChangeListener tc = new TextChangeListener() {

		public void textChange(TextChangeEvent event) {

			call_timer((TextField) event.getSource(), event.getText());

		}
	};

	public TextRemark find_tr(List<TextRemark> lst, TextField tf) {
		TextRemark txr = null;
		for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
			TextRemark tr = (TextRemark) iterator.next();
			if (tr.obj == tf) {
				txr = tr;
				break;
			}
		}
		if (txr == null) {
			return null;
		}
		return txr;
	}

	public TextRemark find_tr(List<TextRemark> lst, String id) {
		TextRemark txr = null;
		for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
			TextRemark tr = (TextRemark) iterator.next();
			if (tr.txtID.equals(id)) {
				txr = tr;
				break;
			}
		}
		if (txr == null) {
			return null;
		}
		return txr;
	}

	public void call_timer(TextField tf, final String vl) {
		lbl.setValue("<font color=green>Auto saving..(" + vl.length()
				+ ") : <b>" + vl + "'</b></font>");

		List<TextRemark> lst = new ArrayList<TextRemark>();
		lst.addAll(lstConsultTxts);
		lst.addAll(lstPlanTxts);
		TextRemark txr = find_tr(lst, tf);
		if (txr == null) {
			utilsVaadin.showMessage(parentLayout.getWindow(),
					"not saved changes", Notification.POSITION_CENTERED_TOP);
			return;
		}
		final TextRemark txc = txr;
		tm = new Timer();
		tm.schedule(new TimerTask() {

			@Override
			public void run() {
				save_text_value(txc, vl);

			}
		}, 100);

	}

	public void save_text_value(TextRemark tr, String vl) {
		running_timer = true;
		try {
			QueryExe qe = new QueryExe(
					"begin delete from clq_texts_values "
							+ " where medical_no=:MN and visit_keyfld=:KF and tb_id= :ID ; "
							+ " insert into clq_texts_values(MEDICAL_NO, TB_ID, VISIT_KEYFLD, VISIT_DT, TB_VALUE) values "
							+ "(:MN , :ID , :KF , :DT , :VL )" + "; end;", con);
			qe.setParaValue("MN", txtMedicalNo.getValue());
			qe.setParaValue("ID", tr.txtID);
			qe.setParaValue("KF", QRYSES);
			qe.setParaValue("DT", varDt);
			qe.setParaValue("VL", vl);
			qe.execute();
			con.commit();
			synchronized (lbl) {
				lbl.setValue("");
				lbl.requestRepaint();
			}
		} catch (SQLException ex) {
			utilsVaadin.showMessage(parentLayout.getWindow(), ex.getMessage(),
					Notification.TYPE_WARNING_MESSAGE);
			ex.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException exx) {

			}
		}
		running_timer = false;

	}

	public String fname = "";

	public void add_new_pic(final int pid) {
		VerticalLayout listlayout = new VerticalLayout();
		final Window wndList = new Window(txtPatientName.getValue().toString());
		wndList.setWidth("500px");
		wndList.setHeight("250px");
		wndList.setImmediate(true);
		wndList.setModal(true);
		wndList.setContent(listlayout);
		wndList.setCloseShortcut(KeyCode.ESCAPE, null);
		listlayout.setSizeFull();
		listlayout.removeAllComponents();
		final ComboBox lst = new ComboBox("Report Name");
		try {
			utilsVaadin.FillCombo(lst,
					"select descr,descr d from relists where idlist='MEDICAL_REPORT_"
							+ (pid + 1) + "' order by descr", con);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		lst.setImmediate(true);
		final Upload upload = new Upload("Upload  image here ", new Receiver() {

			public OutputStream receiveUpload(String filename, String mimeType) {
				fname = ((WebApplicationContext) Channelplus3Application
						.getInstance().getContext()).getHttpSession()
						.getServletContext().getRealPath("/WEB-INF/")
						+ "/tmp/uploads/tmp"
						+ System.currentTimeMillis()
						+ ".jpg";
				FileOutputStream fos = null; // Output stream to write to
				File file;
				file = new File(fname);
				try {
					fos = new FileOutputStream(file);
				} catch (final java.io.FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}

				return fos;
			}
		});
		upload.addListener(new SucceededListener() {

			public void uploadSucceeded(SucceededEvent event) {
				int i = 0;
				final File file = new File(fname);
				fname = fname.replace("\\", "/");
				try {
					final FileResource fis = new FileResource(file,
							Channelplus3Application.getInstance());
					fis.setCacheTime(0);
					con.setAutoCommit(false);
					PreparedStatement ps = con
							.prepareStatement("declare p number; "
									+ "begin select nvl(max(pos),0)+1 into p from clq_pics where medical_no='"
									+ txtMedicalNo.getValue()
									+ "';"
									+ " INSERT INTO CLQ_PICS(MEDICAL_NO,POS,DESCR,CREATE_DT,PIC,"
									+ "visit_keyfld,dat,group_name) VALUES "
									+ "(?,p,?,?,?,?,?,?); end;");

					ps.setString(1, txtMedicalNo.getValue().toString());
					ps.setString(2, lst.getValue().toString());
					ps.setTimestamp(3,
							new Timestamp(System.currentTimeMillis()));

					ps.setBinaryStream(4, fis.getStream().getStream(),
							(int) (file.length()));
					ps.setString(5, QRYSES);
					ps.setDate(6, new java.sql.Date(varDt.getTime()));
					ps.setString(7, pics_group_name[pid]);
					ps.execute();
					con.commit();
					ps.close();
					Timer tmx = new Timer();
					tmx.schedule(new TimerTask() {

						@Override
						public void run() {
							file.delete();
						}
					}, 3000);
					load_pics(pid);
					if (Channelplus3Application.getInstance().getMainWindow()
							.getChildWindows().contains(wndList)) {
						Channelplus3Application.getInstance().getMainWindow()
								.removeWindow(wndList);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					parentLayout.getWindow().showNotification(ex.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);

					try {
						con.rollback();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		});
		lst.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if (lst.getValue() != null) {
					upload.setEnabled(true);
				} else {
					upload.setEnabled(false);
				}

			}
		});
		TextField txtv = new TextField("Visit Date", txtVisitDate.getValue()
				.toString());
		txtv.setReadOnly(true);
		upload.setEnabled(false);
		listlayout.addComponent(txtv);
		listlayout.addComponent(lst);
		listlayout.addComponent(upload);
		if (picsList[pid].getValue() != null) {
			lst.setValue(utilsVaadin.findByValue(lst, picsList[pid].getValue()
					.toString()));

		}
		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wndList)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wndList);
		}

	}

	protected void show_item_info(final String fld) {
		Window wnd = ControlsFactory.CreateWindow("50%", "50%", true, true);

		final ComboBox cb = ControlsFactory.CreateListField("Group ", "s",
				"select TB_ID,tb_caption from clq_texts_def order by pos ",
				null);
		final Panel pnl = new Panel("Values");
		HorizontalLayout hz = new HorizontalLayout();
		pnl.setSizeFull();
		pnl.getContent().setSizeUndefined();
		pnl.getContent().setWidth("100%");
		pnl.setScrollable(true);
		hz.addComponent(cb);
		// cb.setWidth("100%");
		cb.setImmediate(true);
		hz.setSizeFull();
		wnd.getContent().addComponent(hz);
		wnd.getContent().addComponent(pnl);
		((VerticalLayout) wnd.getContent()).setExpandRatio(hz, .8f);
		((VerticalLayout) wnd.getContent()).setExpandRatio(pnl, 3.2f);
		hz.setComponentAlignment(cb, Alignment.MIDDLE_CENTER);
		cb.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try {
					pnl.getContent().removeAllComponents();
					ResultSet rst = utils.getSqlRS(
							"select *from CLQ_TEXTS_VALUES where tb_id='"
									+ ((dataCell) cb.getValue()).getValue()
									+ "' and medical_no='"
									+ txtMedicalNo.getValue()
									+ "' order  by visit_dt", con);
					if (rst == null) {
						return;
					}

					rst.beforeFirst();
					Label lx = null;
					Label lxt = null;
					SimpleDateFormat sdf = new SimpleDateFormat(
							utils.FORMAT_SHORT_DATE);
					while (rst.next()) {
						lx = new Label("  \"" + rst.getString("TB_VALUE")
								+ "\"", Label.CONTENT_XHTML);
						lxt = new Label("<b><u><font color=blue>"
								+ sdf.format(rst.getDate("visit_dt"))
								+ "</font></u></b>", Label.CONTENT_XHTML);
						pnl.addComponent(lxt);
						pnl.addComponent(lx);
					}
					rst.getStatement().close();
				} catch (Exception ex) {
					ex.printStackTrace();
					utilsVaadin.showMessage(ex.getMessage());
				}
			}
		});
		cb.setValue(utilsVaadin.findByValue(cb, fld));
	}

	protected void show_list() {
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
											QRYSES = tv
													.getData()
													.getFieldValue(rn, "KEYFLD")
													.toString();
											load_data();
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select TO_CHAR(v.date_of_arrival,'DD/MM/RRRR') DATE_OF_ARRIVALS, e_first_nm||' '||e_second_nm||' '||e_family_nm patient_name , "
									+ "p.medical_no,tel,mobile_no,v.keyfld from clq_patients p,clq_visits v"
									+ " where p.medical_no=v.medical_no and p.medical_no='"
									+ txtMedicalNo.getValue()
									+ "'  order by date_of_arrival desc", true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void delete_data() {

	}

	public void init() {

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {
			if (data_.getDbclass() == null) {
				data_.createDBClassFromConnection(con);
			}

		} catch (SQLException e) {
		}

		createView();
		load_data();

	}

	public void load_data() {
		txtDrName.setReadOnly(false);
		txtMedicalNo.setReadOnly(false);
		txtVisitDate.setReadOnly(false);
		txtPatientName.setReadOnly(false);
		utilsVaadin.resetValues(headLayout, false, false);
		txtMedicalNo.focus();
		if (!QRYSES.isEmpty()) {
			try {
				PreparedStatement ps = con
						.prepareStatement(
								"select v.*,s.name drname,"
										+ "e_first_nm||' '||e_second_nm||' '||e_last_nm pnm from "
										+ " clq_visits v,clq_patients p,salesp s "
										+ " where p.medical_no=v.medical_no and"
										+ " s.no=v.drno and  v.keyfld='"
										+ QRYSES + "'",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				ResultSet rs = ps.executeQuery();
				rs.first();
				txtMedicalNo.setValue(rs.getString("medical_no"));
				txtDrName.setValue(rs.getString("drname"));
				txtPatientName.setValue(rs.getString("pnm"));
				txtVisitDate.setValue((new SimpleDateFormat(
						utils.FORMAT_SHORT_DATE)).format(rs
						.getDate("date_of_arrival")));
				varDt.setTime(rs.getDate("date_of_arrival").getTime());
				txtDrName.setReadOnly(true);
				txtMedicalNo.setReadOnly(true);
				txtVisitDate.setReadOnly(true);
				txtPatientName.setReadOnly(true);
				createViewTexts();
				fetch_texts_values("C", mapConsultTxts);
				fetch_texts_values("P", mapPlanTxts);
				ps.close();
				String s = "";
				ResultSet rsx = utils.getSqlRS(
						"select group_name,descr,count(*) cnts from clq_pics where medical_no='"
								+ txtMedicalNo.getValue() + "'"
								+ " group by group_name,descr", con);
				if (rsx != null) {
					rsx.beforeFirst();
					while (rsx.next()) {
						s = s + "  <font size=1 color=red> " + rsx.getString(1)
								+ " - </font>  <font size=1 color=gray>"
								+ rsx.getString(2)
								+ "</font> <font color=red size=1> <b> ("
								+ rsx.getString(3) + ") , </b>  </font> ";
					}
					rsx.getStatement().close();
					lbl_txts.setValue(s);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				utilsVaadin.showMessage(ex.getMessage());
			}
		}

	}

	public void print_data() {

	}

	public void validate_data() throws Exception {
	}

	public void save_data() {
		try {
			validate_data();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}
	}

	public void showInitView() {
		if (!QRYSES.isEmpty()) {
			initForm();
		}

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void show_pic(final PicPage pg) throws Exception {
		if (pg.pic_pnl_id < 0) {
			return;
		}
		StreamResource.StreamSource source = new StreamResource.StreamSource() {
			public InputStream getStream() {
				byte[] bytes = null;
				try {
					PreparedStatement pst = con.prepareStatement(
							"select *from clq_pics where visit_keyfld='"
									+ pg.keyfld + "' and pos='" + pg.pos
									+ "' order by pos",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					ResultSet rst = pst.executeQuery();
					if (rst.first()) {
						Blob blob = rst.getBlob("pic");
						long blobLength = blob.length();
						int pos = 1; // position is 1-based
						bytes = blob.getBytes(pos, (int) blobLength);
						pst.close();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return new ByteArrayInputStream(bytes);
			}

		};
		StreamResource resource = new StreamResource(source, "tmp.jpeg",
				Channelplus3Application.getInstance());
		resource.setCacheTime(1);
		if (picsemb[pg.pic_pnl_id] != null) {
			picspnl[pg.pic_pnl_id].getContent().removeComponent(
					picsemb[pg.pic_pnl_id]);
		}
		picsemb[pg.pic_pnl_id] = new Embedded("", resource);
		picspnl[pg.pic_pnl_id].getContent()
				.addComponent(picsemb[pg.pic_pnl_id]);
		picsemb[pg.pic_pnl_id].setHeight("845px");
		picspnl[pg.pic_pnl_id].getContent().requestRepaintAll();
		picspnl[pg.pic_pnl_id].setScrollTop(0);
		picsemb[pg.pic_pnl_id].requestRepaint();
		// file.delete();
	}

	public String parse_texts(String sp) throws SQLException {
		if (sp.isEmpty()) {
			return sp;
		}
		String nsp = sp;
		Pattern pattern = Pattern.compile("(%%[A-Za-z_]*)");
		Matcher matcher = pattern.matcher(sp);
		while (matcher.find()) {
			String mtch = matcher.group();
			if (mtch.toUpperCase().contains("%%PATIENT_NAME")) {
				nsp = nsp.replace(mtch, txtPatientName.getValue().toString());
			}
			if (mtch.toUpperCase().contains("%%AGE")) {
				Calendar c = Calendar.getInstance();
				Calendar d = Calendar.getInstance();
				c.setTime(varDt);
				ResultSet rst = utils.getSqlRS(
						"select dofbirth from clq_patients where medical_no='"
								+ txtMedicalNo.getValue()
								+ "' and dofbirth is not null", con);
				String s = "";
				if (rst != null && rst.first() && rst.getDate(1) != null) {
					d.setTime(rst.getDate(1));
					int r = c.get(Calendar.YEAR) - d.get(Calendar.YEAR);
					s = r + "";
					rst.getStatement().close();
				}
				nsp = nsp.replace(mtch, utils.nvl(s, "00"));
			}

			if (mtch.toUpperCase().startsWith("%%PREVIOUS_TEXT_")) {
				String ss = mtch.replace("%%PREVIOUS_TEXT_", "");
				String s = utils
						.getSqlValue(
								"select t.tb_value from clq_visits v,clq_texts_values t where v.keyfld!='"
										+ QRYSES
										+ "' and v.medical_no='"
										+ txtMedicalNo.getValue()
										+ "' and t.visit_keyfld=v.keyfld and "
										+ " t.tb_id='"
										+ ss
										+ "' and t.tb_value is not null and t.visit_dt<"
										+ utils.getOraDateValue(varDt)
										+ " order by t.visit_dt desc", con);
				if (s.isEmpty()) {
					s = utils.getSqlValue("select " + ss
							+ " from clq_patients where medical_no='"
							+ txtMedicalNo.getValue() + "'", con);
				}
				nsp = nsp.replace(mtch, utils.nvl(s, ""));
			}
			if (!mtch.toUpperCase().startsWith("%%PREVIOUS_TEXT_")
					&& !mtch.toUpperCase().contains("%%PATIENT_NAME")
					&& !mtch.toUpperCase().contains("%%AGE")) {
				String ss = mtch.replace("%%", "");
				if (!ss.isEmpty()) {
					String s = utils.getSqlValue("select " + ss
							+ " from clq_patients where medical_no='"
							+ txtMedicalNo.getValue() + "'", con);
					nsp = nsp.replace(mtch, utils.nvl(s, ""));
				}
			}
		}

		return nsp;

	}

	public class TextRemark {
		public String grp = "C";
		public Object obj = null;
		public String txtID = "";
		public int pos = 0;
		public int height = 25;
		public String default_value = "";
		public String color = "";
		public String copy_last = "Y";
		public String list_name = "";
		public String caption = "";
		public String value = "";

	};

	public class PicPage {
		public double keyfld = -1;
		public double pos = 0;
		public Date dt = new Date(System.currentTimeMillis());
		public String descr = "";
		public int no = -1;
		public int pic_pnl_id = -1;

		public PicPage() {

		}

		public PicPage(double keyfld, double pos) {
			this.keyfld = keyfld;
			this.pos = pos;

		}

		public PicPage(int no, double keyfld, double pos, Date dt,
				String descr, int emb) {
			this.no = no;
			this.keyfld = keyfld;
			this.pos = pos;
			this.dt.setTime(dt.getTime());
			this.descr = descr;
			this.pic_pnl_id = emb;

		}
	}

	public void print_patientC(String medical_no, String repfilename,
			Connection con) throws SQLException {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		mapPara.put("COMPANY_NAME", utils.COMPANY_NAME);
		mapPara.put("COMPANY_SPECS", utils.COMPANY_SPECS);
		mapPara.put("MEDICAL_NO", medical_no);
		mapPara.put("VISIT_DATE", txtVisitDate.getValue());
		ResultSet rst = utils.getSqlRS("select  *from CLQ_TEXTS_DEF ", con);
		List<TextRemark> lst = new ArrayList<TextRemark>();
		lst.addAll(lstConsultTxts);
		lst.addAll(lstPlanTxts);

		if (rst != null) {
			rst.beforeFirst();
			while (rst.next()) {
				mapPara.put(
						rst.getString("tb_id"),
						((TextField) find_tr(lst, rst.getString("tb_id")).obj)
								.getValue() + "");
			}
			rst.getStatement().close();

			// /reports/rep_medicalreport_1
			utilsVaadin.showReport(repfilename, mapPara, con,
					Channelplus3Application.getInstance());
		}

	}
}
