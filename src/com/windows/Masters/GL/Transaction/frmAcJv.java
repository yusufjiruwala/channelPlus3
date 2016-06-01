package com.windows.Masters.GL.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmAcJv implements transactionalForm {
	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	Tree tree = new Tree();
	private localTableModel data_Items = new localTableModel();

	private VerticalLayout tableLayout = new VerticalLayout();

	private final List<ColumnProperty> lstBranchCols = new ArrayList<ColumnProperty>();

	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();

	private HorizontalLayout buttonLayout = new HorizontalLayout();

	private HorizontalLayout content1Layout = new HorizontalLayout();
	private HorizontalLayout noLayout = new HorizontalLayout();
	private HorizontalLayout dateLayout = new HorizontalLayout();
	private HorizontalLayout fcLayout = new HorizontalLayout();
	private HorizontalLayout rateLayout = new HorizontalLayout();

	private HorizontalLayout descrLayout = new HorizontalLayout();

	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout debitLayout = new HorizontalLayout();
	private HorizontalLayout creditLayout = new HorizontalLayout();
	private HorizontalLayout userLayout = new HorizontalLayout();

	private HorizontalLayout unMatchLayout = new HorizontalLayout();

	private TextField txtNo = ControlsFactory.CreateTextField(null, "NO",
			lstfldinfo);
	private DateField txtDate = ControlsFactory.CreateDateField(null,
			"VOU_DATE", lstfldinfo);
	private TextField txtFc = ControlsFactory.CreateTextField(null, "FCCODE",
			lstfldinfo);
	private TextField txtRate = ControlsFactory.CreateTextField(null, "FCRATE",
			lstfldinfo);
	private TextField txtDescr = ControlsFactory.CreateTextField(null, "DESCR",
			lstfldinfo);
	
	private TextField txtDebit = ControlsFactory
			.CreateTextField(null, "", null);
	private TextField txtCredit = ControlsFactory.CreateTextField(null, "",
			null);
	private TextField txtUser = ControlsFactory.CreateTextField(null, "USERNM",
			lstfldinfo);
	private TextField txtUnMatch = ControlsFactory.CreateTextField(null, "",
			null);

	private Label lblNo = new Label("No");
	private Label lblDate = new Label("Date");
	private Label lblFC = new Label("F.C.");
	private Label lblRate = new Label("Rate");
	private Label lblDebit = new Label("Total Debit");
	private Label lblCredit = new Label("Total Credit");
	private Label lblUser = new Label("User");
	private Label lblD = new Label("Descr");
	private Label lblUMatch = new Label("UnMatch");
	private Label emptyLabel = new Label("");

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");
	private NativeButton cmdPrint = ControlsFactory.CreateCustomButton("Print",
			"img/pdf.png", "Save", "");
	private NativeButton cmdCopyVou = ControlsFactory.CreateCustomButton(
			"Copy", "img/copy.png", "Exporting to pdf", "");
	private NativeButton cmdColumn = ControlsFactory.CreateCustomButton(
			"Columns", "img/columns.png", "Columns", "");

	TableLayoutVaadin table = new TableLayoutVaadin(tableLayout);

	public void save_data() {
		save_data(true);
	}

	public void save_data(boolean cls) {
		try {
			con.setAutoCommit(false);
			QueryExe qe = new QueryExe(con);
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
			qe.setParaValue("KEYFLD", "0");
			qe.setParaValue("PERIODCODE", "Y");
			qe.setParaValue("TYPE", "0");
			qe.setParaValue("VOU_CODE", "0");
			qe.setParaValue("FLAG", "0");
			qe.setParaValue("CREATDT", dt);
			qe.setParaValue("ISCHANGE", "Y");
			qe.setParaValue("ISNEW", "Y");
			qe.setParaValue("INVOICE_CODE", "Y");
			qe.setParaValue("PRINTCOUNT", "0");
			qe.setParaValue("FCDEBAMT", "0");
			qe.setParaValue("FCCRDAMT", "0");
			qe.setParaValue("FCCODEAMT", "0");

			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("acvoucher1"); // if
			} else {
				qe.AutoGenerateUpdateStatment("acvoucher1", "'NO'",
						" WHERE NO=:NO"); // if to update..
			}
			qe.execute();
			con.commit();
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtNo.getValue().toString();
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
		}
	}

	public void applyColumnsOnBranch() {
		lstBranchCols.clear();
		try {
			utilsVaadin.applyColumns("FRMACJV.ACVOUCHER2", table.getTable(),
					table.listFields, con);

		} catch (SQLException e) {
		}
	}

	public void load_data() {
		try {
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con.prepareStatement(
						"Select * from acvoucher1 where KEYFLD = '" + QRYSES
								+ "'", ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
			}

			table.data
					.executeQuery(
							" Select  acvoucher2.*,accostcent1.title csname  from  "
									+ "acvoucher2,accostcent1 where Acvoucher2.costcent=accostcent1.code(+)"
									+ " and acvoucher2.keyfld='" + QRYSES
									+ "' order by pos", true);
			table.fill_table();

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void print_data() {

	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {
			if (data_Items.getDbclass() == null) {
				data_Items.createDBClassFromConnection(con);
			}
		} catch (SQLException e) {
		}
		createView();
		load_data();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		mainLayout.setWidth("700px");
		basicLayout.setWidth("100%");
		noLayout.setWidth("100%");
		dateLayout.setWidth("100%");
		fcLayout.setWidth("100%");
		rateLayout.setWidth("100%");
		descrLayout.setWidth("100%");
		debitLayout.setWidth("100%");
		creditLayout.setWidth("100%");
		userLayout.setWidth("100%");
		unMatchLayout.setWidth("100%");
		content1Layout.setWidth("100%");
		content2Layout.setWidth("100%");
		tableLayout.setWidth("100%");

		txtNo.setWidth("100%");
		txtDate.setWidth("100%");
		txtFc.setWidth("100%");
		txtRate.setWidth("100%");
		txtDescr.setWidth("100%");
		txtDebit.setWidth("100%");
		txtCredit.setWidth("100%");
		txtUser.setWidth("100%");
		txtUnMatch.setWidth("100%");

		lblNo.setWidth("60px");
		lblDate.setWidth("50px");
		lblFC.setWidth("50px");
		lblRate.setWidth("50px");
		lblDebit.setWidth("60px");
		lblCredit.setWidth("50px");
		lblUser.setWidth("50px");
		lblD.setWidth("50px");
		lblUMatch.setWidth("50px");

		tableLayout.setHeight("400px");
		table.getTable().setHeight("375px");

		basicLayout.addStyleName("formLayout");

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);

		ResourceManager.addComponent(buttonLayout, cmdCls);
		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdCopyVou);
		ResourceManager.addComponent(buttonLayout, cmdPrint);
		ResourceManager.addComponent(buttonLayout, cmdColumn);

		ResourceManager.addComponent(basicLayout, content1Layout);
		ResourceManager.addComponent(basicLayout, descrLayout);
		ResourceManager.addComponent(basicLayout, tableLayout);
		ResourceManager.addComponent(basicLayout, content2Layout);
		ResourceManager.addComponent(basicLayout, unMatchLayout);

		ResourceManager.addComponent(content1Layout, noLayout);
		ResourceManager.addComponent(content1Layout, dateLayout);
		ResourceManager.addComponent(content1Layout, fcLayout);
		ResourceManager.addComponent(content1Layout, rateLayout);

		ResourceManager.addComponent(descrLayout, lblD);
		ResourceManager.addComponent(descrLayout, txtDescr);

		ResourceManager.addComponent(tableLayout, table);

		ResourceManager.addComponent(content2Layout, debitLayout);
		ResourceManager.addComponent(content2Layout, creditLayout);
		ResourceManager.addComponent(content2Layout, userLayout);

		ResourceManager.addComponent(unMatchLayout, lblUMatch);
		ResourceManager.addComponent(unMatchLayout, txtUnMatch);
		ResourceManager.addComponent(unMatchLayout, emptyLabel);

		ResourceManager.addComponent(debitLayout, lblDebit);
		ResourceManager.addComponent(debitLayout, txtDebit);

		ResourceManager.addComponent(creditLayout, lblCredit);
		ResourceManager.addComponent(creditLayout, txtCredit);

		ResourceManager.addComponent(userLayout, lblUser);
		ResourceManager.addComponent(userLayout, txtUser);

		ResourceManager.addComponent(noLayout, lblNo);
		ResourceManager.addComponent(noLayout, txtNo);

		ResourceManager.addComponent(dateLayout, lblDate);
		ResourceManager.addComponent(dateLayout, txtDate);

		ResourceManager.addComponent(fcLayout, lblFC);
		ResourceManager.addComponent(fcLayout, txtFc);

		ResourceManager.addComponent(rateLayout, lblRate);
		ResourceManager.addComponent(rateLayout, txtRate);

		noLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);

		dateLayout.setComponentAlignment(lblDate, Alignment.BOTTOM_CENTER);

		fcLayout.setComponentAlignment(lblFC, Alignment.BOTTOM_CENTER);

		rateLayout.setComponentAlignment(lblRate, Alignment.BOTTOM_CENTER);

		descrLayout.setComponentAlignment(lblD, Alignment.BOTTOM_CENTER);

		debitLayout.setComponentAlignment(lblDebit, Alignment.BOTTOM_CENTER);

		creditLayout.setComponentAlignment(lblCredit, Alignment.BOTTOM_CENTER);

		userLayout.setComponentAlignment(lblUser, Alignment.BOTTOM_CENTER);

		unMatchLayout.setComponentAlignment(lblUMatch, Alignment.BOTTOM_CENTER);

		content1Layout.setExpandRatio(noLayout, 1f);
		content1Layout.setExpandRatio(dateLayout, 1f);
		content1Layout.setExpandRatio(fcLayout, 1f);
		content1Layout.setExpandRatio(rateLayout, 1f);

		content2Layout.setExpandRatio(debitLayout, 1.3f);
		content2Layout.setExpandRatio(creditLayout, 1.3f);
		content2Layout.setExpandRatio(userLayout, 1.4f);

		noLayout.setExpandRatio(lblNo, 0.1f);
		noLayout.setExpandRatio(txtNo, 3.9f);

		dateLayout.setExpandRatio(lblDate, 0.1f);
		dateLayout.setExpandRatio(txtDate, 3.9f);

		fcLayout.setExpandRatio(lblFC, 0.1f);
		fcLayout.setExpandRatio(txtFc, 3.9f);

		rateLayout.setExpandRatio(lblRate, 0.1f);
		rateLayout.setExpandRatio(txtRate, 3.9f);

		descrLayout.setExpandRatio(lblD, 0.1f);
		descrLayout.setExpandRatio(txtDescr, 3.9f);

		debitLayout.setExpandRatio(lblDebit, 0.1f);
		debitLayout.setExpandRatio(txtDebit, 3.9f);

		creditLayout.setExpandRatio(lblCredit, 0.1f);
		creditLayout.setExpandRatio(txtCredit, 3.9f);

		userLayout.setExpandRatio(lblUser, 0.1f);
		userLayout.setExpandRatio(txtUser, 3.9f);

		unMatchLayout.setExpandRatio(lblUMatch, 0.1f);
		unMatchLayout.setExpandRatio(txtUnMatch, 0.9f);
		unMatchLayout.setExpandRatio(emptyLabel, 3f);

		applyColumnsOnBranch();

		if (!listnerAdded) {
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
			cmdCls.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					QRYSES = "";
					load_data();
				}
			});
			listnerAdded = true;
		}
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractOrderedLayout) parentLayout;
	}

	public void showInitView() {
		QRYSES = "";
		initForm();
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
	}

	protected void show_list() {
		try {
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);

			utilsVaadin
					.showSearch(la,
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
							"select no, descr, vou_date, keyfld from acvoucher1 where vou_code=1 and type=1 ORDER BY VOU_DATE DESC,NO DESC",

							true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

}
