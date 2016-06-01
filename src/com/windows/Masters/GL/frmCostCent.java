package com.windows.Masters.GL;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.example.components.SearchField;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.InfoBox;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.localTableModel;
import com.generic.menuItem;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmCostCent implements transactionalForm {

	private AbstractLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	private localTableModel data_Items = new localTableModel();
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();

	private HorizontalLayout m_mainLayout = new HorizontalLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout codeLayout = new HorizontalLayout();
	private HorizontalLayout titleLayout = new HorizontalLayout();
	private HorizontalLayout titleLayout2 = new HorizontalLayout();
	private HorizontalLayout parentCostLayout = new HorizontalLayout();

	private NativeButton cmdSave = ControlsFactory.CreateCustomButton("Save",
			"img/save.png", "Save", "");
	private NativeButton cmdDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/remove.png", "Delete current order", "");

	private NativeButton cmdList = ControlsFactory.CreateCustomButton("List",
			"img/details.png", "List", "");
	private NativeButton cmdCls = ControlsFactory.CreateCustomButton("CLS",
			"img/cls.png", "Clear the screen", "");

	private Panel basicPanel = new Panel();

	private Label lblMessage = new Label("");
	private Label lblCode = new Label("Code :");
	private Label lblTitle = new Label("Title :");
	private Label lblTitleA = new Label("TitleE :");
	private Label lblParent = new Label("Parent :");
	private Label empryLabel = new Label();
	public boolean show_tree_panel = true;
	public InfoBox costBrowse = null;
	private java.util.Map<String, String> mapActAccBrows = new HashMap<String, String>();

	private TextField txtCode = ControlsFactory.CreateTextField("", "code",
			lstfldinfo);
	private TextField txtTitle = ControlsFactory.CreateTextField("", "title",
			lstfldinfo);
	private TextField txtTitleA = ControlsFactory.CreateTextField("", "titleA",
			lstfldinfo);
	private SearchField txtParents = ControlsFactory
			.CreateSearchField("", "parentCostCent", lstfldinfo, "100%",
					"select code,title from accostcent1 order by path", "code",
					"title");

	public void save_data() {
		save_data(true);
	}

	public void save_data(boolean cls) {
		try {
			// validate_data();
			con.setAutoCommit(false);
			QueryExe qe = new QueryExe(con);
			for (Iterator iterator = lstfldinfo.iterator(); iterator.hasNext();) {
				FieldInfo fl = (FieldInfo) iterator.next();
				if (!(fl.obj instanceof CheckBox)) {
					qe.setParaValue(fl.fieldName,
							((AbstractField) fl.obj).getValue());
				} else {
					qe.setParaValue(fl.fieldName, (((CheckBox) fl.obj)
							.booleanValue() ? fl.valueOnTrue : fl.valueOnFalse));
				}
			}

			Date dt = new Date(System.currentTimeMillis());

			String pt = utils
					.generatePath(txtParents.getValue() + "",
							txtCode.getValue() + "", "ACCOSTCENT1", "CODE",
							"PATH", con);

			qe.setParaValue("path", pt);
			qe.setParaValue("LEVELNO", utils.countString(pt, "\\") - 1);
			qe.setParaValue("EXPAMT", "0");
			qe.setParaValue("EARAMT", "0");
			qe.setParaValue("TYPE", "0");
			qe.setParaValue("CLOSECTG", "Y");
			if (QRYSES.isEmpty()) {
				qe.AutoGenerateInsertStatment("accostcent1"); // if
			} else {
				qe.AutoGenerateUpdateStatment("accostcent1", "'CODE'",
						" WHERE CODE=:code"); // if to update..
			}
			qe.execute();
			qe.close();
			con.commit();
			if (show_tree_panel && costBrowse != null) {
				/*
				 * menuItem mn = new menuItem(txtCode.getValue().toString(),
				 * txtTitle.getValue() + "-" + txtCode.getValue().toString());
				 * mn.setParentID(utils.nvl(txtParen.getValue(), ""));
				 * costBrowse.updateTree(mn);
				 */
				costBrowse.refreshTree(true);
			}
			if (cls) {
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} else {
				QRYSES = txtCode.getValue().toString();
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

	public void load_data() {

		try {
			lblMessage.setValue("Adding....");
			txtCode.setReadOnly(false);
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con
						.prepareStatement(
								"Select * from accostcent1 where code='"
										+ QRYSES + "'",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				txtCode.setReadOnly(true);
				lblMessage.setValue("Editing : " + txtTitle.getValue());
				fetchAccountInfo();
			}
			if (costBrowse != null && show_tree_panel) {
				costBrowse.locateTree(QRYSES);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	protected void fetchAccountInfo() {
		txtParents.setDisplayValue(txtParents.getValue(), utils.getSqlValue(
				"Select title from Accostcent1 where code = '"
						+ txtParents.getValue().toString() + "'", con));
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

		mapActAccBrows.clear();
		mapActAccBrows.put("open", "Open..");
		mapActAccBrows.put("open_n", "Open New Window..");
		mapActAccBrows.put("create_branch", "Create Branch..");
		mapActAccBrows.put("create_branch_n", "Create Branch New Window..");

		createView();
		load_data();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.setSizeFull();
		resetFormLayout();
		m_mainLayout.setSizeFull();

		mainLayout.setSpacing(true);
		mainLayout.setSizeFull();
		mainLayout.setWidth("600px");
		mainLayout.setMargin(true);

		codeLayout.setWidth("100%");
		titleLayout.setWidth("100%");
		titleLayout2.setWidth("100%");
		parentCostLayout.setWidth("100%");

		basicPanel.setScrollable(true);
		basicPanel.setSizeFull();
		basicPanel.getContent().setHeight("-1px");
		basicPanel.setHeight("-1px");
		basicPanel.setWidth("600px");

		empryLabel.setWidth("100%");

		lblCode.setWidth("100px");
		lblTitle.setWidth("100px");
		lblTitleA.setWidth("100px");
		lblParent.setWidth("100px");

		txtCode.setWidth("100%");
		txtTitle.setWidth("100%");
		txtTitleA.setWidth("100%");

		basicLayout.setMargin(true);

		ResourceManager.addComponent(centralPanel, m_mainLayout);
		ResourceManager.addComponent(m_mainLayout, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicPanel);
		ResourceManager.addComponent(basicPanel, basicLayout);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdCls);
		ResourceManager.addComponent(buttonLayout, lblMessage);

		ResourceManager.addComponent(basicLayout, codeLayout);
		ResourceManager.addComponent(basicLayout, titleLayout);
		ResourceManager.addComponent(basicLayout, titleLayout2);
		ResourceManager.addComponent(basicLayout, parentCostLayout);

		ResourceManager.addComponent(codeLayout, lblCode);
		ResourceManager.addComponent(codeLayout, txtCode);
		ResourceManager.addComponent(codeLayout, empryLabel);

		ResourceManager.addComponent(titleLayout, lblTitle);
		ResourceManager.addComponent(titleLayout, txtTitle);

		ResourceManager.addComponent(titleLayout2, lblTitleA);
		ResourceManager.addComponent(titleLayout2, txtTitleA);

		ResourceManager.addComponent(parentCostLayout, lblParent);
		// ResourceManager.addComponent(parentCostLayout, txtParen);
		ResourceManager.addComponent(parentCostLayout, txtParents);
		// ResourceManager.addComponent(parentCostLayout, cmdClearParentAcc);
		// ResourceManager.addComponent(parentCostLayout, cmdParentAcc);
		// ResourceManager.addComponent(parentCostLayout, txtParentName);

		codeLayout.setComponentAlignment(lblCode, Alignment.BOTTOM_CENTER);

		titleLayout.setComponentAlignment(lblTitle, Alignment.BOTTOM_CENTER);

		titleLayout2.setComponentAlignment(lblTitleA, Alignment.BOTTOM_CENTER);

		mainLayout.setExpandRatio(buttonLayout, 0.1f);
		mainLayout.setExpandRatio(basicPanel, 3.9f);

		basicLayout.addStyleName("formLayout");

		parentCostLayout.setComponentAlignment(lblParent,
				Alignment.BOTTOM_CENTER);
		codeLayout.setExpandRatio(lblCode, 0.1f);
		codeLayout.setExpandRatio(txtCode, 1f);
		codeLayout.setExpandRatio(empryLabel, 2.9f);

		titleLayout.setExpandRatio(lblTitle, 0.1f);
		titleLayout.setExpandRatio(txtTitle, 3.9f);

		titleLayout2.setExpandRatio(lblTitleA, 0.1f);
		titleLayout2.setExpandRatio(txtTitleA, 3.9f);

		parentCostLayout.setExpandRatio(lblParent, 0.1f);
		parentCostLayout.setExpandRatio(txtParents, 3.9f);
		// parentCostLayout.setExpandRatio(cmdClearParentAcc, 0.1f);
		// parentCostLayout.setExpandRatio(cmdParentAcc, 0.1f);
		// parentCostLayout.setExpandRatio(txtParentName, 2.5f);
		createInfoBox();
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

	private void createInfoBox() {

		try {
			if (show_tree_panel) {
				if (costBrowse == null) {
					costBrowse = InfoBox.createInstance(data_Items, "CODE",
							"PARENTCOSTCENT", "TITLE", "CHILDCOUNT",
							"Cost Centers", "USECOUNT", "");

					m_mainLayout.addComponent(costBrowse);
					costBrowse.setHeight("100%");
					costBrowse.setWidth("300px");
					costBrowse.tree.addActionHandler(new Handler() {

						public void handleAction(Action action, Object sender,
								Object target) {
							menuItem m = (menuItem) target;

							// open
							if (action.getCaption().equals(
									mapActAccBrows.get("open"))) {

								QRYSES = m.getId();
								load_data();
							}

							// opne-n
							if (action.getCaption().equals(
									mapActAccBrows.get("open_n"))) {
								Window wnd = ControlsFactory.CreateWindow(
										"600px", "350px", true, true);
								frmCostCent frm = new frmCostCent();
								frm.setParentLayout((AbstractLayout) wnd
										.getContent());
								frm.show_tree_panel = false;
								frm.showInitView();
								frm.QRYSES = m.getId();
								frm.load_data();

								wnd.setScrollable(true);
							}

							// create_branch
							if (action.getCaption().equals(
									mapActAccBrows.get("create_branch"))) {
								createAcc(m.getId());
							}

							if (action.getCaption().equals(
									mapActAccBrows.get("create_branch_n"))) {
								Window wnd = ControlsFactory.CreateWindow(
										"600px", "350px", true, true);
								frmCostCent frm = new frmCostCent();
								frm.setParentLayout((AbstractLayout) wnd
										.getContent());
								frm.show_tree_panel = false;
								frm.showInitView();
								frm.createAcc(m.getId());
							}

						}

						public Action[] getActions(Object target, Object sender) {
							if (target == null)
								return null;

							List<Action> acts = new ArrayList<Action>();
							acts.add(new Action(mapActAccBrows.get("open")));
							acts.add(new Action(mapActAccBrows.get("open_n")));
							menuItem m = (menuItem) target;
							if (((BigDecimal) m.getPara2Val()).intValue() == 0) {
								acts.add(new Action(mapActAccBrows
										.get("create_branch")));
								acts.add(new Action(mapActAccBrows
										.get("create_branch_n")));
							}
							Action act[] = new Action[acts.size()];
							return acts.toArray(act);
						}
					});

					new ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							if (costBrowse.tree.getValue() != null) {
								menuItem itm = (menuItem) costBrowse.tree
										.getValue();
								QRYSES = itm.getId();
								txtTitle.focus();
								load_data();
							} else {
								QRYSES = "";
								load_data();
								txtTitle.focus();
							}
						}
					};
				}
				data_Items.executeQuery(
						"select *from accostcent1 order by path", true);
				m_mainLayout.removeComponent(costBrowse);
				m_mainLayout.addComponent(costBrowse);
				costBrowse.initView();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createAcc(String cod) {
		if (!txtCode.isReadOnly()) {
			txtCode.setValue(utils
					.getNewNo(
							"select to_number(nvl(max(code),'0')) from accostcent1 where  (parentcostcent='"
									+ cod + "')", cod, con));
			txtParents.setDisplayValue(
					cod,
					utils.getSqlValue("select title from"
							+ " accostcent1 where code='" + cod + "'", con));

		}
	}

	public void showInitView() {
		QRYSES = "";
		initForm();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
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
							QRYSES = tv.getData().getFieldValue(rn, "code")
									.toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select code, title from accostcent1 order by path", true);

			// "select o.ord_no,o.ord_date,o.ordacc,items.descr,o.lcno serial_no, o.remarks from order1 o ,items"
			// "where ord_code=106 and ordacc=items.reference order by ord_no desc"
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		m_mainLayout.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		codeLayout.removeAllComponents();
		titleLayout.removeAllComponents();
		titleLayout2.removeAllComponents();
		parentCostLayout.removeAllComponents();
	}
}
