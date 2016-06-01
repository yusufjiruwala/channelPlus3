package com.windows.workshop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmJobServices implements transactionalForm {
	private Connection con = null;
	private Window wndList = new Window();
	VerticalLayout lstLayout = new VerticalLayout();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();

	private NativeButton cmdSave = new NativeButton("Save & Post");
	private NativeButton cmdRevert = new NativeButton("Revert");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdDel = new NativeButton("Delete");
	private NativeButton cmdCls = new NativeButton("Clear");

	private TextField txtCustCode = new TextField("Customer Code");
	private TextField txtCustName1 = new TextField(" Name 1");
	private TextField txtCustName2 = new TextField("Name 2");
	private TextField txtAc = new TextField("A/c");
	private TextField txtAddress = new TextField("Address");
	private TextField txtEmail = new TextField("Email");
	private TextField txtTel = new TextField("Tel");

	private String QRYSES = "";
	private boolean listnerAdded = false;

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
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

		txtCustCode.setWidth("100px");
		txtCustName1.setWidth("200px");
		txtCustName2.setWidth("200px");
		txtEmail.setWidth("200px");
		txtTel.setWidth("100px");
		txtAc.setWidth("100px");
		txtAddress.setWidth("200px");
		txtAddress.setHeight("100px");
		txtCustCode.setImmediate(true);
		txtCustCode.setReadOnly(false);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdList);
		ResourceManager.addComponent(commandLayout, cmdDel);
		ResourceManager.addComponent(commandLayout, cmdRevert);
		ResourceManager.addComponent(commandLayout, cmdCls);

		ResourceManager.addComponent(basicLayout, txtCustCode);
		ResourceManager.addComponent(basicLayout, txtCustName1);
		ResourceManager.addComponent(basicLayout, txtCustName2);
		ResourceManager.addComponent(basicLayout, txtAc);
		ResourceManager.addComponent(basicLayout, txtAddress);
		ResourceManager.addComponent(basicLayout, txtEmail);
		ResourceManager.addComponent(basicLayout, txtTel);

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
			txtCustCode.addListener(new BlurListener() {

				public void blur(BlurEvent event) {
					if (!txtCustCode.isReadOnly()
							&& txtCustCode.getValue() != null) {
						String fnd = utils.getSqlValue(
								"select name from c_ycust where upper(code)='"
										+ txtCustCode.getValue().toString()
												.toUpperCase() + "'", con);
						if (!fnd.isEmpty()) {
							parentLayout.getWindow().showNotification(
									"Found # " + fnd);
							QRYSES = txtCustCode.getValue().toString()
									.toUpperCase();
							load_data();

						}
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
													.getFieldValue(rn, "CODE")
													.toString();
											load_data();
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							},
							con,
							"select code,name from c_ycust where iscust='Y' order by path",
							true);

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
		txtCustCode.setReadOnly(false);
		txtAc
				.setValue(utils
						.getSqlValue(
								"select repair.getsetupvalue_2('MAIN_CUST_ACC') from dual",
								con));
		txtCustCode.setValue("");
		txtCustName1.setValue("");
		txtCustName2.setValue("");
		txtAddress.setValue("");
		txtEmail.setValue("");
		txtTel.setValue("");
		txtCustCode.setReadOnly(false);

		try {
			PreparedStatement ps = con.prepareStatement(
					"select *from C_YCUST where CODE='" + QRYSES + "'",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rst = ps.executeQuery();

			if (rst.first()) {
				txtCustCode.setValue(rst.getString("CODE"));
				txtCustCode.setReadOnly(true);
				txtCustName1.setValue(rst.getString("NAME"));
				txtCustName2.setValue(utils.nvl(rst.getString("NAMEA"), ""));
				txtAddress.setValue(utils.nvl(rst.getString("ADDR"), ""));
				txtAc.setValue(utils.nvl(rst.getString("AC_NO"), ""));
				txtTel.setValue(utils.nvl(rst.getString("TEL"), ""));
				txtEmail.setValue(utils.nvl(rst.getString("EMAIL"), ""));
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
		String chld = utils.getSqlValue(
				"select max(childcount) from acaccount where accno="
						+ txtAc.getValue() + " and actype=0 ", con);
		if (chld == null || chld.isEmpty()) {
			throw new Exception("A/C not found or not valid ");
		}
		if (Double.valueOf(chld).doubleValue() > 0) {
			throw new Exception("A/c is not valid, select child a/c");
		}

	}

	public void save_data() {
		try {
			validate_data();
			String sqlIns = "INSERT INTO c_ycust(code,name,namea,ac_no,addr,tel,email,path) values ("
					+ " :CODE , :NAME ,:NAMEA , :AC_NO , :ADDR , :TEL , :EMAIL , :PATH )";
			String sqlUpd = "update c_ycust set name =:NAME ,namea = :NAMEA, path = :PATH"
					+ ", ac_no =:AC_NO ,addr = :ADDR , tel= :TEL , email=:EMAIL where code=:CODE";
			try {

				con.setAutoCommit(false);
				QueryExe qe = new QueryExe(con);
				if (QRYSES.equals("")) {
					qe.setSqlStr(sqlIns);
				} else {
					qe.setSqlStr(sqlUpd);
				}

				qe.setParaValue("CODE", txtCustCode.getValue());
				qe.setParaValue("NAME", txtCustName1.getValue());
				qe.setParaValue("NAMEA", txtCustName2.getValue());
				qe.setParaValue("AC_NO", txtAc.getValue());
				qe.setParaValue("ADDR", txtAddress.getValue());
				qe.setParaValue("TEL", txtTel.getValue());
				qe.setParaValue("EMAIL", txtEmail.getValue());
				qe.setParaValue("PATH", "XXX\\" + txtCustCode + "\\");
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
