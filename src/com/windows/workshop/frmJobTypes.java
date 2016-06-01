package com.windows.workshop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmJobTypes implements transactionalForm {
	private Connection con = null;
	private Window wndList = new Window();
	VerticalLayout lstLayout = new VerticalLayout();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private HorizontalLayout subLayout = new HorizontalLayout();
	private VerticalLayout basicLayout1 = new VerticalLayout();
	private VerticalLayout basicLayout2 = new VerticalLayout();

	private NativeButton cmdSave = new NativeButton("Save & Post");
	private NativeButton cmdRevert = new NativeButton("Revert");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdDel = new NativeButton("Delete");
	private NativeButton cmdCls = new NativeButton("Clear");

	private TextField txtTypeNo = new TextField("Type No");
	private TextField txtDescr = new TextField("Descr");
	private TextField txtDisc = new TextField("Discount on Sales %");
	private CheckBox chkSpare = new CheckBox("Have Spare Parts", true);
	private CheckBox chkService = new CheckBox("Have Services ", true);
	private DateField txtFromDate = new DateField("From Date");
	private DateField txtTodate = new DateField("To date");

	private String QRYSES = "";
	private boolean listnerAdded = false;

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout1.removeAllComponents();
		basicLayout2.removeAllComponents();
		commandLayout.removeAllComponents();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();

		// centralPanel.setSizeFull();
		// mainLayout.se
		// basicLayout.setSizeFull();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		txtTypeNo.setWidth("100px");
		txtDescr.setWidth("200px");
		txtDisc.setWidth("100px");
		txtFromDate.setWidth("100px");
		txtTodate.setWidth("100px");
		subLayout.setSizeFull();
		basicLayout1.setSizeFull();
		basicLayout1.setSizeFull();

		txtTypeNo.setImmediate(true);
		txtTypeNo.setReadOnly(false);
		txtDisc.setImmediate(true);

		txtFromDate.setResolution(DateField.RESOLUTION_DAY);
		txtFromDate.setDateFormat(utils.FORMAT_SHORT_DATE);
		txtTodate.setResolution(DateField.RESOLUTION_DAY);
		txtTodate.setDateFormat(utils.FORMAT_SHORT_DATE);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, subLayout);
		ResourceManager.addComponent(subLayout, basicLayout1);
		ResourceManager.addComponent(subLayout, basicLayout2);

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdList);
		ResourceManager.addComponent(commandLayout, cmdDel);
		ResourceManager.addComponent(commandLayout, cmdRevert);
		ResourceManager.addComponent(commandLayout, cmdCls);

		ResourceManager.addComponent(basicLayout1, txtTypeNo);
		ResourceManager.addComponent(basicLayout1, txtDescr);
		ResourceManager.addComponent(basicLayout1, txtDisc);
		ResourceManager.addComponent(basicLayout1, chkService);
		ResourceManager.addComponent(basicLayout1, chkSpare);
		ResourceManager.addComponent(basicLayout1, txtFromDate);
		ResourceManager.addComponent(basicLayout1, txtTodate);

		if (!listnerAdded) {
			cmdSave.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			cmdDel.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {

					delete_data();
				}
			});
			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_list();
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
			txtTypeNo.addListener(new BlurListener() {

				public void blur(BlurEvent event) {
					if (!txtTypeNo.isReadOnly() && txtTypeNo.getValue() != null) {
						String fnd = utils.getSqlValue(
								"select descr from ord_job_types where no='"
										+ txtTypeNo.getValue().toString()
												.toUpperCase() + "'", con);
						if (!fnd.isEmpty()) {
							parentLayout.getWindow().showNotification(
									"Found # " + fnd);
							QRYSES = txtTypeNo.getValue().toString()
									.toUpperCase();
							load_data();

						}
					}
				}
			});
			txtDisc.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						Double dbl = Double.valueOf(txtDisc.getValue()
								.toString());
						if (dbl < 0 || dbl > 100) {
							throw new Exception("Discount value is not valid");
						}

					} catch (Exception ex) {
						txtDisc.focus();
						parentLayout.getWindow().showNotification(
								ex.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);

					}
				}
			});
			listnerAdded = true;
		}
	}

	protected void show_list() {
		try {
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);

			utilsVaadin.showSearch(la, new SelectionListener() {

				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);

					if (tv.getSelectionValue() > -1) {
						try {
							int rn = tv.getSelectionValue();
							QRYSES = tv.getData().getFieldValue(rn, "NO")
									.toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select no,descr from ORD_job_types order by NO", true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	protected void delete_data() {

	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
		load_data();

	}

	public void load_data() {
		txtTypeNo.setReadOnly(false);
		txtTypeNo.setValue("");
		txtDescr.setValue("");
		txtFromDate.setValue(null);
		txtTodate.setValue(null);
		txtDisc.setValue("0");
		chkService.setValue(true);
		chkSpare.setValue(true);

		txtTypeNo.setReadOnly(false);

		try {
			PreparedStatement ps = con.prepareStatement(
					"select *from ord_job_types where no='" + QRYSES + "'",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rst = ps.executeQuery();

			if (rst.first()) {
				txtTypeNo.setValue(rst.getString("NO"));
				txtTypeNo.setReadOnly(true);
				txtDescr.setValue(rst.getString("DESCR"));
				if (rst.getTimestamp("FROMDATE") != null) {
					txtFromDate.setValue(rst.getTimestamp("FROMDATE"));
				}
				if (rst.getTimestamp("TODATE") != null) {
					txtTodate.setValue(rst.getTimestamp("TODATE"));
				}
				txtDisc.setValue(utils.nvl(rst.getString("DISCOUNT_ON_SALES"),
						"0"));
				if (rst.getString("HAVE_SPARE_PARTS").equals("N")) {
					chkSpare.setValue(false);
				}
				if (rst.getString("HAVE_SERVICES").equals("N")) {
					chkService.setValue(false);
				}

			}
			ps.close();

		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public void print_data() {

	}

	public void validate_data() throws Exception {
		Double dbl = Double.valueOf(txtDisc.getValue().toString());
		if (dbl < 0 || dbl > 100) {
			throw new Exception("Discount value is not valid");
		}
		Date dt = null;
		Date dt2 = null;
		if (txtFromDate.getValue() != null && txtTodate.getValue() != null) {
			if (((java.util.Date) txtFromDate.getValue()).getTime() > ((java.util.Date) txtTodate
					.getValue()).getTime()) {
				throw new Exception("FROM DATE is lower than TO DATE");
			}
		}
	}

	public void save_data() {
		try {
			validate_data();
			String sqlIns = "INSERT INTO ORD_JOB_TYPES (no,descr,discount_on_sales,have_spare_parts"
					+ ",HAVE_SERVICES,FROMDATE,TODATE) values ("
					+ " :NO , :DESCR ,:DISCOUNT_ON_SALES , :HAVE_SPARE_PARTS , :HAVE_SERVICES , :FROMDATE , :TODATE )";
			String sqlUpd = "update ORD_JOB_TYPES set DESCR = :DESCR"
					+ ", DISCOUNT_ON_SALES =:DISCOUNT_ON_SALES , HAVE_SPARE_PARTS = :HAVE_SPARE_PARTS , "
					+ "HAVE_SERVICES= :HAVE_SERVICES , FROMDATE= :FROMDATE, TODATE=:TODATE  where NO= :NO ";
			try {
				con.setAutoCommit(false);
				QueryExe qe = new QueryExe(con);
				if (QRYSES.equals("")) {
					qe.setSqlStr(sqlIns);
				} else {
					qe.setSqlStr(sqlUpd);
				}

				qe.setParaValue("NO", txtTypeNo.getValue());
				qe.setParaValue("DESCR", txtDescr.getValue());
				qe.setParaValue("DISCOUNT_ON_SALES", txtDisc.getValue());
				qe.setParaValue("FROMDATE", txtFromDate.getValue());
				qe.setParaValue("TODATE", txtTodate.getValue());
				qe.setParaValue("HAVE_SERVICES", "N");
				qe.setParaValue("HAVE_SPARE_PARTS", "N");
				if (chkService.booleanValue()) {
					qe.setParaValue("HAVE_SERVICES", "Y");
				}
				if (chkSpare.booleanValue()) {
					qe.setParaValue("HAVE_SPARE_PARTS", "Y");
				}

				qe.execute();
				qe.close();
				con.commit();
				parentLayout.getWindow().showNotification("Saved Successfully");
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

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}
	}

	public void showInitView() {
		QRYSES = "";
		init();
		initForm();

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

}
