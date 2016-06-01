package com.windows.clinics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.doc.views.TableView;
import com.doc.views.YesNoDialog;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.YesNoDialog.Callback;
import com.generic.ControlsFactory;
import com.generic.DBClass;
import com.generic.FieldInfo;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.SecurityParameter;
import com.generic.dataCell;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.generic.utilsVaadinPrintHandler;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
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
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;
import com.windows.frmMainMenus;

public class frmPatients implements transactionalForm {

	private SaveData savedata = new SaveData();
	private AbstractLayout parentLayout = null;
	private String formname = frmMainMenus.FORM_PATIENTS;
	public String QRYSES = "";
	private Connection con = null;

	public SecurityParameter sec_para = new SecurityParameter();
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();
	private VerticalLayout consultLayout = new VerticalLayout();
	private VerticalLayout planLayout = new VerticalLayout();
	private VerticalLayout[] medical_reps = new VerticalLayout[30];

	private GridLayout namesLayout = new GridLayout(4, 2);
	private HorizontalLayout[] otherLayouts = new HorizontalLayout[10];

	private int[] medical_rep_ids = new int[30];
	private List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();

	private TextField txtMedicalNo = ControlsFactory.CreateTextField(
			"Medical No", "medical_no", lstfldinfo);
	private TextField txtEFirstName = ControlsFactory.CreateTextField(
			"First Name -L", "e_first_nm", lstfldinfo);
	private TextField txtESecondName = ControlsFactory.CreateTextField(
			"Middle Name- L", "e_second_nm", lstfldinfo);
	private TextField txtELastName = ControlsFactory.CreateTextField(
			"Last name -L", "e_last_nm", lstfldinfo);
	private TextField txtEFamilyName = ControlsFactory.CreateTextField(
			"Family Name -L", "e_family_nm", lstfldinfo);
	private TextField txtAFirstName = ControlsFactory.CreateTextField(
			"First Name -A", "a_first_nm", lstfldinfo);
	private TextField txtASecondName = ControlsFactory.CreateTextField(
			"Middle Name- A", "a_second_nm", lstfldinfo);
	private TextField txtALastName = ControlsFactory.CreateTextField(
			"Last name -A", "a_last_nm", lstfldinfo);
	private TextField txtAFamilyName = ControlsFactory.CreateTextField(
			"Family Name -A", "a_family_nm", lstfldinfo);

	private TextField txtEMotherName = ControlsFactory.CreateTextField(
			"Mother Name -E", "e_mother_nm", lstfldinfo);
	private TextField txtAMotherName = ControlsFactory.CreateTextField(
			"Mother Name -A", "a_mother_nm", lstfldinfo);

	private NativeButton cmdPrintPlan = ControlsFactory.CreateToolbarButton(
			"img/print.png", "Print...");
	private NativeButton cmdPrintCons = ControlsFactory.CreateToolbarButton(
			"img/print.png", "Print...");

	private NativeButton cmdSave = new NativeButton("Save & Post");
	private NativeButton cmdRevert = new NativeButton("Revert");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdDel = new NativeButton("Delete");
	private NativeButton cmdCls = new NativeButton("Clear");
	private NativeButton cmdPMR = new NativeButton("Medical records");

	private NativeButton cmdNewPic = new NativeButton("Save & Add Report");
	private Table tbl_pics = new Table();

	private ComboBox lstSexCode = ControlsFactory.CreateListField("Sex",
			"SEX_CODE", "#SEX", lstfldinfo);
	private ComboBox lstLocation = ControlsFactory.CreateListField("Location",
			"location_code", "select code,name from locations order by code",
			lstfldinfo);
	private ComboBox lstInvType = ControlsFactory.CreateListField(
			"Invoice Type", "INVOICE_TYPE", "", lstfldinfo);

	private DateField txtDob = ControlsFactory.CreateDateField("Birth Date",
			"DOFBIRTH", lstfldinfo);

	private TextField txtAddress1 = ControlsFactory.CreateTextField("Address",
			"local_address", lstfldinfo);
	private ComboBox lstCountry = ControlsFactory.CreateListField("Country",
			"country", "#COUNTRY", lstfldinfo);
	private ComboBox lstNationality = ControlsFactory.CreateListField(
			"Nationality", "nationality", "#NATIONALITY", lstfldinfo);
	private TextField txtPostalCode = ControlsFactory.CreateTextField(
			"Postal Code", "POSTAL_CODE", lstfldinfo);

	private CheckBox chkVip = ControlsFactory.CreateCheckField("V.I.P.",
			"IS_VIP", "N", "Y", lstfldinfo);
	private CheckBox chkSmoker = ControlsFactory.CreateCheckField("Smoker.",
			"IS_SMOKER", "N", "Y", lstfldinfo);

	private TextField txtTel = ControlsFactory.CreateTextField("Patient's Tel",
			"TEL", lstfldinfo);
	private TextField txtExt = ControlsFactory.CreateTextField("Extension",
			"EXTENSION", lstfldinfo);
	private TextField txtMobile = ControlsFactory.CreateTextField("Mobile",
			"MOBILE_NO", lstfldinfo);

	private TextField txtBirthPlace = ControlsFactory.CreateTextField(
			"Birth Place", "BIRTH_PLACE", lstfldinfo);
	private ComboBox lstReligion = ControlsFactory.CreateListField("Religion",
			"religion", "#RELIGION", lstfldinfo);
	private ComboBox lstIdType = ControlsFactory.CreateListField("ID Type",
			"ID_TYPE", "#PERSON_ID_TYPE", lstfldinfo);
	private TextField txtId = ControlsFactory.CreateTextField("ID No ",
			"ID_NO", lstfldinfo);

	private CheckBox chkMarital = ControlsFactory.CreateCheckField("Marital",
			"MARITAL_STATUS", "N", "Y", lstfldinfo);
	private TextField txtOldMedical = ControlsFactory.CreateTextField(
			"Old Medical", "OLD_MEDICAL", lstfldinfo);

	private TextField txtEmail = ControlsFactory.CreateTextField("Email",
			"EMAIL", lstfldinfo);

	private TextField txtCReferedBy = ControlsFactory.CreateTextField(
			"Refered By", "REP_REFERED_BY", lstfldinfo);
	private TextField txtCAllergies = ControlsFactory.CreateTextField(
			"Allergies", "REP_ALLERGIES", lstfldinfo);
	private TextField txtCMedicalReport = ControlsFactory.CreateTextField(
			"Medical History", "REP_MEDICAL_HISTORY", lstfldinfo);
	private TextField txtCSurgicalHisory = ControlsFactory.CreateTextField(
			"Surgical History", "REP_SURGICAL_HISTORY", lstfldinfo);
	private TextField txtCInvestDone = ControlsFactory.CreateTextField(
			"Investigations Done", "REP_INVESTIGATION_DONE", lstfldinfo);
	private TextField txtCHOPIllness = ControlsFactory.CreateTextField(
			"History Of Presenting Illness", "REP_HISTORY_OF_PRESENT_ILLNESS",
			lstfldinfo);

	private TextField txtPDiag = ControlsFactory.CreateTextField(
			"Prov Diagnosis", "REP_PROV_DIAGNOSIS", lstfldinfo);
	private TextField txtPNewInvest = ControlsFactory.CreateTextField(
			"New Investigation", "REP_NEW_INVESTIGATION", lstfldinfo);
	private TextField txtPNewMed = ControlsFactory.CreateTextField(
			"New Medication", "REP_NEW_MEDICATION", lstfldinfo);
	private TextField txtPProc = ControlsFactory.CreateTextField("Procedure",
			"REP_PROCEDURE", lstfldinfo);
	private TextField txtPPt = ControlsFactory.CreateTextField(
			"Physio Therapy", "REP_PHYSIOTHERAPY", lstfldinfo);
	private TextField txtPRecom = ControlsFactory.CreateTextField(
			"Recommendation", "REP_RECOMMENDATION", lstfldinfo);
	private Label lblmsg = new Label();
	private TabSheet tbsheet = new TabSheet();
	private boolean listnerAdded = false;
	private boolean found_error_on_save = false;
	private int picheight = 845;
	private int picheight_orign = 845;
	private String varLastVisitkf = "";
	private String varMedicalNo = "";
	private LayoutClickListener layoutClicker = new LayoutClickListener() {
		public void layoutClick(LayoutClickEvent event) {
			if (event.getChildComponent() != null
					&& event.getChildComponent() instanceof TextField
					&& event.isDoubleClick()) {
				TextField t = ((TextField) event.getChildComponent());
				FieldInfo f = utils.findFieldInfoByObject(t, lstfldinfo);
				show_selectionlist(f);
			}

		}
	};

	public frmPatients() {
		init();
		for (Iterator iterator = lstfldinfo.iterator(); iterator.hasNext();) {
			final FieldInfo fld = (FieldInfo) iterator.next();
			if (fld.fieldName.startsWith("REP_")) {
				((TextField) fld.obj).setImmediate(true);
				((TextField) fld.obj).addListener(new FocusListener() {

					public void focus(FocusEvent event) {
						lblmsg.setValue(fld.fieldCaption
								+ " - "
								+ ((TextField) fld.obj).getValue().toString()
										.length());
						lblmsg.getParent().requestRepaint();

					}
				});
				((TextField) fld.obj).addListener(new TextChangeListener() {
					public void textChange(TextChangeEvent event) {

						lblmsg.setValue(fld.fieldCaption
								+ " - "
								+ ((TextField) fld.obj).getValue().toString()
										.length());
						lblmsg.setCaption("");
						lblmsg.getParent().requestRepaint();
					}
				});
			}
		}
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		consultLayout.removeAllComponents();
		planLayout.removeAllComponents();
		commandLayout.removeAllComponents();
		namesLayout.removeAllComponents();
		tbsheet.removeAllComponents();
		for (int i = 0; i < medical_reps.length; i++) {
			if (medical_reps[i] != null) {
				medical_reps[i].removeAllComponents();
				medical_reps[i] = null;
			}
		}
		for (int i = 0; i < otherLayouts.length; i++) {
			if (otherLayouts[i] == null) {
				otherLayouts[i] = new HorizontalLayout();
				otherLayouts[i].setSpacing(true);

			}
			otherLayouts[i].removeAllComponents();
		}

	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		mainLayout.setSpacing(true);
		mainLayout.setSizeFull();
		centralPanel.setSizeFull();
		basicLayout.setSpacing(true);
		namesLayout.setSpacing(true);
		lblmsg.setWidth("100%");
		consultLayout.setWidth("100%");
		tbsheet.setSizeFull();
		txtEMotherName.setWidth("300px");
		txtAMotherName.setWidth("300px");
		txtAddress1.setWidth("600px");
		txtAddress1.setHeight("50px");
		chkMarital.setWidth("100px");
		chkSmoker.setWidth("100px");
		chkVip.setWidth("100px");
		txtMedicalNo.setImmediate(true);
		lstLocation.setImmediate(true);
		lblmsg.setImmediate(true);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, tbsheet);

		tbsheet.addTab(basicLayout, "Basic Info", null);
		// tbsheet.addTab(consultLayout, "Consultation Notes", null);
		// tbsheet.addTab(planLayout, "Plan", null);

		ResourceManager.addComponent(commandLayout, txtMedicalNo);
		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdList);
		ResourceManager.addComponent(commandLayout, cmdDel);
		ResourceManager.addComponent(commandLayout, cmdRevert);
		ResourceManager.addComponent(commandLayout, cmdCls);
		ResourceManager.addComponent(commandLayout, cmdPMR);
		// ResourceManager.addComponent(commandLayout, cmdNewPic);
		ResourceManager.addComponent(commandLayout, lblmsg);

		ResourceManager.addComponent(basicLayout, namesLayout);
		ResourceManager.addComponent(namesLayout, txtEFirstName);
		ResourceManager.addComponent(namesLayout, txtESecondName);
		ResourceManager.addComponent(namesLayout, txtELastName);
		ResourceManager.addComponent(namesLayout, txtEFamilyName);
		ResourceManager.addComponent(namesLayout, txtAFirstName);
		ResourceManager.addComponent(namesLayout, txtASecondName);
		ResourceManager.addComponent(namesLayout, txtALastName);
		ResourceManager.addComponent(namesLayout, txtAFamilyName);

		ResourceManager.addComponent(basicLayout, txtEMotherName);
		ResourceManager.addComponent(basicLayout, txtAMotherName);

		ResourceManager.addComponent(basicLayout, otherLayouts[0]);
		ResourceManager.addComponent(otherLayouts[0], lstLocation);
		ResourceManager.addComponent(otherLayouts[0], lstInvType);

		ResourceManager.addComponent(basicLayout, otherLayouts[1]);
		ResourceManager.addComponent(otherLayouts[1], txtDob);
		ResourceManager.addComponent(otherLayouts[1], lstCountry);
		ResourceManager.addComponent(otherLayouts[1], txtPostalCode);

		ResourceManager.addComponent(basicLayout, otherLayouts[2]);
		ResourceManager.addComponent(otherLayouts[2], txtAddress1);

		ResourceManager.addComponent(basicLayout, otherLayouts[3]);
		ResourceManager.addComponent(otherLayouts[3], lstNationality);
		ResourceManager.addComponent(otherLayouts[3], chkVip);
		ResourceManager.addComponent(otherLayouts[3], chkSmoker);

		ResourceManager.addComponent(basicLayout, otherLayouts[4]);
		ResourceManager.addComponent(otherLayouts[4], txtTel);
		ResourceManager.addComponent(otherLayouts[4], txtExt);
		ResourceManager.addComponent(otherLayouts[4], txtMobile);

		ResourceManager.addComponent(basicLayout, otherLayouts[5]);
		ResourceManager.addComponent(otherLayouts[5], txtBirthPlace);
		ResourceManager.addComponent(otherLayouts[5], lstReligion);
		ResourceManager.addComponent(otherLayouts[5], lstSexCode);

		ResourceManager.addComponent(basicLayout, otherLayouts[6]);
		ResourceManager.addComponent(otherLayouts[6], chkMarital);
		ResourceManager.addComponent(otherLayouts[6], txtOldMedical);

		ResourceManager.addComponent(basicLayout, otherLayouts[7]);

		ResourceManager.addComponent(basicLayout, otherLayouts[8]);
		ResourceManager.addComponent(otherLayouts[8], lstIdType);
		ResourceManager.addComponent(otherLayouts[8], txtId);

		otherLayouts[3].setComponentAlignment(chkVip, Alignment.BOTTOM_CENTER);
		otherLayouts[3].setComponentAlignment(chkSmoker,
				Alignment.BOTTOM_CENTER);
		otherLayouts[6].setComponentAlignment(chkMarital,
				Alignment.BOTTOM_CENTER);

		ResourceManager.addComponent(consultLayout, cmdPrintCons);
		ResourceManager.addComponent(consultLayout, txtCReferedBy);
		ResourceManager.addComponent(consultLayout, txtCAllergies);
		ResourceManager.addComponent(consultLayout, txtCMedicalReport);
		ResourceManager.addComponent(consultLayout, txtCSurgicalHisory);
		ResourceManager.addComponent(consultLayout, txtCInvestDone);
		ResourceManager.addComponent(consultLayout, txtCHOPIllness);

		ResourceManager.addComponent(planLayout, cmdPrintPlan);
		ResourceManager.addComponent(planLayout, txtPDiag);
		ResourceManager.addComponent(planLayout, txtPNewMed);
		ResourceManager.addComponent(planLayout, txtPNewInvest);
		ResourceManager.addComponent(planLayout, txtPProc);
		ResourceManager.addComponent(planLayout, txtPPt);
		ResourceManager.addComponent(planLayout, txtPRecom);

		commandLayout.setComponentAlignment(cmdCls, Alignment.BOTTOM_CENTER);
		commandLayout.setComponentAlignment(cmdDel, Alignment.BOTTOM_CENTER);
		commandLayout.setComponentAlignment(cmdList, Alignment.BOTTOM_CENTER);
		commandLayout.setComponentAlignment(cmdRevert, Alignment.BOTTOM_CENTER);
		commandLayout.setComponentAlignment(cmdSave, Alignment.BOTTOM_CENTER);
		commandLayout.setComponentAlignment(cmdPMR, Alignment.BOTTOM_CENTER);
		commandLayout.setComponentAlignment(lblmsg, Alignment.BOTTOM_RIGHT);

		mainLayout.setExpandRatio(commandLayout, .1f);
		mainLayout.setExpandRatio(tbsheet, 3.9f);

		for (Iterator iterator = consultLayout.getComponentIterator(); iterator
				.hasNext();) {
			Component c = (Component) iterator.next();
			if (c instanceof TextField) {
				c.setWidth("100%");
				c.setHeight("25px");
				((TextField) c)
						.setInputPrompt("Double click here to show list , check list "
								+ utils.findFieldInfoByObject(c, lstfldinfo).fieldName
										.replace("REP_", ""));
			}
		}
		for (Iterator iterator = planLayout.getComponentIterator(); iterator
				.hasNext();) {
			Component c = (Component) iterator.next();
			if (c instanceof TextField) {
				c.setWidth("100%");
				c.setHeight("25px");
				((TextField) c)
						.setInputPrompt("Double click here to show list , check list "
								+ utils.findFieldInfoByObject(c, lstfldinfo).fieldName
										.replace("REP_", ""));
			}
		}

		txtCMedicalReport.setHeight("50px");
		txtCSurgicalHisory.setHeight("50px");
		txtCInvestDone.setHeight("50px");
		txtCHOPIllness.setHeight("150px");
		txtPNewMed.setHeight("50px");
		txtPProc.setHeight("50px");
		txtPPt.setHeight("50px");
		txtPRecom.setHeight("50px");

		if (!listnerAdded) {
			cmdPMR.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					if (QRYSES.isEmpty() || varLastVisitkf.isEmpty()) {

						utilsVaadin.showMessage(parentLayout.getWindow(),
								"Must have atleast one visit !",
								Notification.TYPE_ERROR_MESSAGE);
						return;
					}

					Window wnd = ControlsFactory.CreateWindow("90%", "100%",
							false, true);
					Panel pnl = new Panel();
					pnl.setContent(new VerticalLayout());
					pnl.setSizeFull();
					pnl.getContent().setWidth("100%");
					pnl.getContent().setHeight("-1px");
					((VerticalLayout) pnl.getContent()).setMargin(true);
					wnd.setContent(pnl);
					frmVisitRecords v = new frmVisitRecords();
					v.setParentLayout((VerticalLayout) pnl.getContent());
					v.QRYSES = varLastVisitkf;
					v.showInitView();
				}
			});
			cmdPrintCons.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data(false);
					utilsVaadinPrintHandler.print_patient(txtMedicalNo
							.getValue().toString(),
							"rep_medicalreport_consultation.jasper", con);
					tbsheet.setSelectedTab(consultLayout);

				}
			});
			cmdPrintPlan.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data(false);
					utilsVaadinPrintHandler.print_patient(txtMedicalNo
							.getValue().toString(),
							"rep_medicalreport_plan.jasper", con);
					tbsheet.setSelectedTab(planLayout);
				}
			});
			consultLayout.addListener(layoutClicker);
			planLayout.addListener(layoutClicker);
			cmdNewPic.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					save_data(false);
					add_new_report();
				}
			});
			lstLocation.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (lstLocation.getValue() != null) {
							utilsVaadin.FillCombo(lstInvType,
									"select no,descr from invoicetype where location_code='"
											+ ((dataCell) lstLocation
													.getValue()).getValue()
											+ "' order by no", con);
							lstInvType
									.setValue(utilsVaadin
											.findByValue(
													lstInvType,
													utils
															.getSqlValue(
																	"select repair.getsetupvalue_2('DEFAULT_TYPE') from dual",
																	con)));
						} else {
							lstInvType.removeAllItems();
						}
					} catch (SQLException ex) {
						ex.printStackTrace();
					}

				}
			});

			txtMedicalNo.addListener(new BlurListener() {
				public void blur(BlurEvent event) {
					if (!txtMedicalNo.isReadOnly()
							&& txtMedicalNo.getValue() != null) {
						String fnd = utils.getSqlValue(
								"select e_first_nm from clq_patients where medical_no='"
										+ txtMedicalNo.getValue().toString()
										+ "'", con);
						if (!fnd.isEmpty()) {
							parentLayout.getWindow().showNotification(
									"Found # " + fnd);
							QRYSES = txtMedicalNo.getValue().toString()
									.toUpperCase();
							load_data();
						}
					}
				}
			});

			cmdRevert.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					load_data();
				}
			});

			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_list();

				}
			});
			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();

				}
			});
			cmdRevert.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					load_data();

				}
			});
			cmdCls.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					load_data();

				}
			});
			listnerAdded = true;

		}
	}

	protected void add_new_report() {
		if (QRYSES.isEmpty()) {
			return;
		}
		VerticalLayout listlayout = new VerticalLayout();
		final Window wndList = new Window("Select Columns here");
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
			utilsVaadin
					.FillCombo(
							lst,
							"select descr,descr d from relists where idlist='MEDICAL_REPORT' order by descr",
							con);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		lst.setImmediate(true);
		final Upload upload = new Upload("Upload  image here ", new Receiver() {

			public OutputStream receiveUpload(String filename, String mimeType) {

				FileOutputStream fos = null; // Output stream to write to
				File file;
				file = new File(((WebApplicationContext) Channelplus3Application
						.getInstance().getContext()).getHttpSession()
						.getServletContext().getRealPath("/WEB-INF/")
						+ "/tmp/uploads/" + "tmp.jpeg");
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
				tbsheet.setSizeFull();
				for (i = 0; i < medical_reps.length; i++) {
					if (medical_reps[i] == null) {
						medical_reps[i] = new VerticalLayout();
						tbsheet.addTab(medical_reps[i], lst.getValue()
								.toString(), null);
						break;
					}
				}
				File file = new File(
						((WebApplicationContext) Channelplus3Application
								.getInstance().getContext()).getHttpSession()
								.getServletContext().getRealPath("/WEB-INF/")
								+ "/tmp/uploads/tmp.jpeg");
				final FileResource imageResource = new FileResource(file,
						Channelplus3Application.getInstance());
				Panel pnl = new Panel(null, new VerticalLayout());
				pnl.setSizeFull();
				pnl.getContent().setSizeUndefined();
				pnl.setScrollable(true);
				medical_reps[i].setSizeFull();
				medical_reps[i].addComponent(pnl);
				imageResource.setCacheTime(0);
				final int ii = i;
				HorizontalLayout hz = new HorizontalLayout();
				NativeButton btn = ControlsFactory.CreateToolbarButton(
						"img/remove.png", "Remove this report..");
				NativeButton btn_in = ControlsFactory.CreateToolbarButton(
						"img/expand.png", "zoon in..");
				NativeButton btn_out = ControlsFactory.CreateToolbarButton(
						"img/collapse.png", "zoon out..");
				add_remove_pic_listner(btn, ii);
				add_remove_zoom_in_zoomo_out(btn_in, btn_out);
				pnl.getContent().addComponent(hz);
				hz.addComponent(btn);
				hz.addComponent(btn_in);
				hz.addComponent(btn_out);

				Embedded em = new Embedded("", imageResource);
				pnl.getContent().addComponent(em);
				em.setHeight((picheight_orign - 50) + "px");

				try {
					con.setAutoCommit(false);
					PreparedStatement ps = con
							.prepareStatement("declare p number; "
									+ "begin select nvl(max(pos),0)+1 into p from clq_pics where medical_no='"
									+ QRYSES
									+ "';"
									+ " INSERT INTO CLQ_PICS(MEDICAL_NO,POS,DESCR,CREATE_DT,PIC) VALUES "
									+ "(?,p,?,?,?); end;");

					ps.setString(1, QRYSES);
					ps.setString(2, lst.getValue().toString());
					ps.setTimestamp(3,
							new Timestamp(System.currentTimeMillis()));
					/*
					 * file = new File( ((WebApplicationContext)
					 * Channelplus3Application .getInstance().getContext())
					 * .getHttpSession().getServletContext()
					 * .getRealPath("/WEB-INF/") + "/tmp/uploads/" +
					 * "tmp.jpeg"); //
					 */
					// FileInputStream inpstrm = new FileInputStream(file);

					ps.setBinaryStream(4,
							imageResource.getStream().getStream(), (int) (file
									.length()));
					ps.execute();
					ps.close();
					con.commit();
					medical_rep_ids[i] = Integer.valueOf(utils.getSqlValue(
							"select max(pos) from clq_pics where medical_no='"
									+ QRYSES + "'", con));
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

		upload.setEnabled(false);
		listlayout.addComponent(lst);
		listlayout.addComponent(upload);
		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wndList)) {
			Channelplus3Application.getInstance().getMainWindow().addWindow(
					wndList);
		}

	}

	protected void add_remove_pic_listner(NativeButton btn, final int ii) {
		btn.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				parentLayout.getWindow().addWindow(
						new YesNoDialog("Channel Plus Warning",
								"Delete this image  ?", new Callback() {

									public void onDialogResult(
											boolean resultIsYes) {
										if (!resultIsYes) {
											return;
										}
										try {

											con.setAutoCommit(false);
											utils
													.execSql(
															"delete from clq_pics where medical_no='"
																	+ txtMedicalNo
																			.getValue()
																	+ "' and pos="
																	+ medical_rep_ids[ii],
															con);

											con.commit();
											load_data();

										} catch (SQLException ex) {

											try {
												con.rollback();
											} catch (SQLException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}

										}

									}
								}

						));
			}

		});
	}

	protected void show_selectionlist(final FieldInfo f) {
		try {
			final TextField t = (TextField) f.obj;
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

			tbl.FetchSql("select  descr from relists where idlist='"
					+ f.fieldName.replace("REP_", "").replace("rep_", "")
					+ "' order by descr");

			wndList.addListener(new CloseListener() {
				List<String> hd = new ArrayList<String>();

				public void windowClose(CloseEvent e) {
					String s = "";
					for (int i = 0; i < tbl.getLstCheckSelection().size(); i++) {
						if (tbl.getLstCheckSelection().get(i).booleanValue()) {
							s = s + (s.isEmpty() ? "" : ",")
									+ tbl.getData().getFieldValue(i, "DESCR")
									+ " ";
						}
					}
					if (!s.trim().isEmpty()) {
						t.setValue(s);
					}
				}
			});

			tbl.createView();

			if (!Channelplus3Application.getInstance().getMainWindow()
					.getChildWindows().contains(wndList)) {
				Channelplus3Application.getInstance().getMainWindow().addWindow(
						wndList);
			}
			wndList.setCaption(tbl.getData().getRows().size()
					+ " Rows selected");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

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
											QRYSES = tv.getData()
													.getFieldValue(rn,
															"MEDICAL_NO")
													.toString();
											load_data();
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select e_first_nm||' '||e_second_nm||' '||e_family_nm patient_name , "
									+ "medical_no,tel,mobile_no from clq_patients order by medical_no",
							true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();

		sec_para.fetchData("FORM", formname, con);
		if (!sec_para.canAccess) {
			utilsVaadin.showMessage("$SECURITY_ACCESS_DENIED");
			resetFormLayout();
			parentLayout.addComponent(new Label(utilsVaadin
					.getMessage("$SECURITY_ACCESS_DENIED")));
			return;
		}
		createView();
		load_data();

	}

	private Timer tm = null;

	public void exec_timer() {

		if (running_thread_saving == false && !QRYSES.isEmpty()) {
			tm = new Timer();
			tm.schedule(new TimerTask() {

				@Override
				public void run() {
					if (QRYSES.isEmpty()) {
						cancel();
					}
					savedata.run_sch();
					cancel();
				}
			}, 500);
		} else {
			running_thread_saving = false;
		}

	}

	private TextChangeListener txtls = new TextChangeListener() {

		public void textChange(TextChangeEvent event) {
			exec_timer();
		}
	};
	private ValueChangeListener valuec = new ValueChangeListener() {

		public void valueChange(ValueChangeEvent event) {
			exec_timer();

		}
	};

	public void validate_secure_ui() {
		cmdSave.setEnabled(true);
		txtMedicalNo.setEnabled(true);
		if (!QRYSES.isEmpty() && !sec_para.canUpdates) {
			cmdSave.setEnabled(false);
			utilsVaadin.showMessage(parentLayout.getWindow(),
					"$SECURITY_SHOW_BEFORE_UPDATE",
					Notification.TYPE_ERROR_MESSAGE);

		}
		if (QRYSES.isEmpty() && !sec_para.canInsert) {
			cmdSave.setEnabled(false);
			txtMedicalNo.setEnabled(false);
			utilsVaadin.showMessage(parentLayout.getWindow(),
					"$SECURITY_SHOW_BEFORE_INSERT",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void load_data() {
		System.gc();
		validate_secure_ui();
		for (int i = 0; i < medical_reps.length; i++) {
			if (medical_reps[i] != null) {
				medical_reps[i].removeAllComponents();
				tbsheet.removeTab(tbsheet.getTab(medical_reps[i]));
				medical_reps[i] = null;
				medical_rep_ids[i] = 0;
			}
		}
		lblmsg.setValue("");
		varLastVisitkf = "";
		found_error_on_save = false;
		picheight = picheight_orign;
		txtMedicalNo.setReadOnly(false);
		txtMedicalNo.setValue("");
		utilsVaadin.resetValues(basicLayout, false, false);
		utilsVaadin.resetValues(consultLayout, false, false);
		utilsVaadin.resetValues(planLayout, false, false);
		tbsheet.setSelectedTab(basicLayout);
		txtMedicalNo.focus();
		try {
			if (QRYSES.length() > 0) {
				if (running_thread_saving) {
					tm.wait();
				}
				System.out.println("preparing statment to load");
				PreparedStatement ps = con
						.prepareStatement(
								"select clq_patients.*,"
										+ "(select max(keyfld) from clq_visits"
										+ " where clq_visits.medical_no=clq_patients.medical_no) last_visit_kf"
										+ " from clq_patients where medical_no='"
										+ QRYSES + "'",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = ps.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				varLastVisitkf = rst.getString("last_visit_kf");
				ps.close();
				txtMedicalNo.setReadOnly(true);
				// load_pics();
				for (int i = 0; i < lstfldinfo.size(); i++) {
					FieldInfo fl = lstfldinfo.get(i);
					if (fl.fieldName.startsWith("REP_")) {
						((TextField) fl.obj).addListener(txtls);
						((TextField) fl.obj).setImmediate(true);
						((TextField) fl.obj).addListener(valuec);
					}

				}
			} else {
				lstLocation
						.setValue(utilsVaadin
								.findByValue(
										lstLocation,
										utils
												.getSqlValue(
														"select repair.getsetupvalue_2('DEFAULT_LOCATION') from dual",
														con)));
				for (int i = 0; i < lstfldinfo.size(); i++) {
					FieldInfo fl = lstfldinfo.get(i);
					if (fl.fieldName.startsWith("REP_")) {
						((TextField) fl.obj).removeListener(txtls);
						// ((TextField) fl.obj).setImmediate(false);
						((TextField) fl.obj).removeListener(valuec);

					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}

	}

	private void load_pics() throws Exception {
		if (QRYSES.isEmpty()) {
			return;
		}
		PreparedStatement pst = con.prepareStatement(
				"select *from clq_pics where medical_no='" + QRYSES
						+ "' order by pos", ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet rst = pst.executeQuery();
		rst.beforeFirst();
		while (rst.next()) {
			File file = new File(
					((WebApplicationContext) Channelplus3Application
							.getInstance().getContext()).getHttpSession()
							.getServletContext().getRealPath("/WEB-INF/")
							+ "/tmp/uploads/tmp"
							+ rst.getString("pos")
							+ ".jpeg");
			Blob blob = rst.getBlob("pic");
			long blobLength = blob.length();
			int pos = 1; // position is 1-based
			byte[] bytes = blob.getBytes(pos, (int) blobLength);
			FileOutputStream f = new FileOutputStream(file);
			f.write(bytes);
			f.close();
			int i = 0;
			for (i = 0; i < medical_reps.length; i++) {
				if (medical_reps[i] == null) {
					medical_reps[i] = new VerticalLayout();
					medical_rep_ids[i] = rst.getInt("pos");
					tbsheet.addTab(medical_reps[i], rst.getString("descr"),
							null);
					break;
				}
			}
			medical_reps[i].setSizeFull();
			final FileResource imageResource = new FileResource(file,
					Channelplus3Application.getInstance());
			Panel pnl = new Panel();
			pnl.setHeight("100%");
			pnl.setContent(new VerticalLayout());
			pnl.getContent().setSizeUndefined();
			pnl.setScrollable(true);
			imageResource.setCacheTime(0);
			final int ii = i;
			NativeButton btn = ControlsFactory.CreateToolbarButton(
					"img/remove.png", "Remove this report..");
			medical_reps[i].addComponent(pnl);
			HorizontalLayout hz = new HorizontalLayout();
			NativeButton btn_in = ControlsFactory.CreateToolbarButton(
					"img/expand.png", "zoon in..");
			NativeButton btn_out = ControlsFactory.CreateToolbarButton(
					"img/collapse.png", "zoon out..");
			add_remove_pic_listner(btn, ii);
			add_remove_zoom_in_zoomo_out(btn_in, btn_out);
			pnl.getContent().addComponent(hz);
			hz.addComponent(btn);
			hz.addComponent(btn_in);
			hz.addComponent(btn_out);

			Embedded em = new Embedded("", imageResource);
			pnl.getContent().addComponent(em);
			em.setHeight((picheight_orign - 50) + "px");
			medical_reps[i].requestRepaintAll();
		}
		pst.close();
	}

	private void add_remove_zoom_in_zoomo_out(NativeButton btnIn,
			NativeButton btnOut) {
		btnIn.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				VerticalLayout v = (VerticalLayout) event.getButton()
						.getParent().getParent();

				for (int i = 0; i < v.getComponentCount(); i++) {
					if (v.getComponent(i) instanceof Embedded) {
						picheight = (int) v.getComponent(i).getHeight();
						picheight += 100;
						v.getComponent(i).setHeight((picheight - 50) + "px");
						return;
					}

				}
			}
		});
		btnOut.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				VerticalLayout v = (VerticalLayout) event.getButton()
						.getParent().getParent();
				for (int i = 0; i < v.getComponentCount(); i++) {
					if (v.getComponent(i) instanceof Embedded) {
						picheight = (int) v.getComponent(i).getHeight();
						picheight = (picheight < 100 ? 100 : picheight - 50);
						v.getComponent(i).setHeight((picheight - 50) + "px");
						return;
					}

				}
			}
		});
	}

	public void print_data() {

	}

	public void validate_data() throws SQLException {
		if (!QRYSES.isEmpty() && !sec_para.canUpdates) {
			throw new SQLException(utilsVaadin
					.getMessage("$SECURITY_SHOW_ON_UPDATE"));
		}

		if (QRYSES.isEmpty() && !sec_para.canInsert) {
			throw new SQLException(utilsVaadin
					.getMessage("$SECURITY_SHOW_ON_INSERT"));
		}

	}

	public void save_data() {
		save_data(true);
	}

	public void save_data(boolean cls) {
		try {
			validate_data();
			con.setAutoCommit(false);
			String sqlIns = "begin INSERT INTO c_ycust(code,name,ac_no,addr,tel,email,path) values ("
					+ " :CODE , :NAME  , (select repair.getsetupvalue_2('MAIN_CUST_ACC') from dual), :ADDR , :TEL , :EMAIL , :PATH ); "
					+ "insert into cbranch(code,brno,b_name,accno) values"
					+ " ( :CODE ,1,:NAME, (select repair.getsetupvalue_2('MAIN_CUST_ACC') from dual)); end;";

			QueryExe qe = new QueryExe(con);

			if (QRYSES.isEmpty()) {
				QueryExe qec = new QueryExe(sqlIns, con);
				qec.setParaValue("CODE", txtMedicalNo.getValue());
				qec.setParaValue("NAME", txtEFirstName.getValue() + " "
						+ txtEFamilyName.getValue());
				qec.setParaValue("PATH", "XXX\\" + txtMedicalNo.getValue()
						+ "\\");
				qec.setParaValue("TEL", txtTel.getValue());
				qec.setParaValue("EMAIL", txtEmail.getValue());
				qec.setParaValue("ADDR", txtMobile.getValue());
				qec.execute();
			}
			for (Iterator iterator = lstfldinfo.iterator(); iterator.hasNext();) {
				FieldInfo fl = (FieldInfo) iterator.next();
				if (!(fl.obj instanceof CheckBox)) {
					qe.setParaValue(fl.fieldName, ((AbstractField) fl.obj)
							.getValue());
				} else {
					qe
							.setParaValue(fl.fieldName, (((CheckBox) fl.obj)
									.booleanValue() ? fl.valueOnTrue
									: fl.valueOnFalse));
				}
			}
			Date dt = new Date(System.currentTimeMillis());
			qe.setParaValue("CREATED_DATE", dt);
			qe.setParaValue("CREATED_USER_BY", utils.CPUSER);
			qe.setParaValue("LAST_MODIFIED_DATE", dt);
			qe.setParaValue("CUST_CODE", txtMedicalNo.getValue());

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("CLQ_PATIENTS");
			} else {
				qe.AutoGenerateUpdateStatment("CLQ_PATIENTS", "'MEDICAL_NO'",
						" WHERE MEDICAL_NO=:MEDICAL_NO");
			}

			qe.execute();
			con.commit();

			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtMedicalNo.getValue().toString();
			}

			load_data();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

			try {
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			found_error_on_save = true;
		}

	}

	public void showInitView() {
		QRYSES = "";
		initForm();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public boolean running_thread_saving = false;

	public class SaveData {
		public void run_sch() {

			if (running_thread_saving) {
				return;
			}
			running_thread_saving = true;
			if (!QRYSES.isEmpty()) {
				String cols = "", colnames = "";
				String txts = "", values = "";
				int cnt = 0;
				for (int i = 0; i < lstfldinfo.size(); i++) {
					FieldInfo fld = lstfldinfo.get(i);
					if (fld.fieldName.startsWith("REP_")) {
						cols = cols + (cnt == 0 ? "" : "||") + fld.fieldName;
						colnames = colnames
								+ (cnt == 0 ? "" : ",")
								+ fld.fieldName
								+ "='"
								+ ((((TextField) fld.obj).getValue() != null ? ((TextField) fld.obj)
										.getValue()
										: "")).toString().replace("'", "''''")
								+ "'";

						txts = txts
								+ (((TextField) fld.obj).getValue() != null ? ((TextField) fld.obj)
										.getValue()
										: "").toString().replace("'", "''''");
						cnt++;
					}

				}
				cols = utils.getSqlValue("select " + cols
						+ " from clq_patients where medical_no='" + QRYSES
						+ "'", con);
				if (!txts.equals(cols)) {
					try {
						con.setAutoCommit(false);
						utils.execSql("begin update clq_patients set "
								+ colnames + " where medical_no='" + QRYSES
								+ "'; end;", con);
						System.out.println("updated " + colnames);
						con.commit();
					} catch (SQLException ex) {
						ex.printStackTrace();
						try {
							con.rollback();
						} catch (SQLException e) {
						}
						;
					}

				}

			}

			running_thread_saving = false;
		}
	}
}