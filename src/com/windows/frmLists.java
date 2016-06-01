package com.windows;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.generic.ColumnProperty;
import com.generic.Parameter;
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
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmLists implements transactionalForm {
	private Connection con = null;
	private Window wndList = new Window();
	VerticalLayout lstLayout = new VerticalLayout();
	private localTableModel data_lists = new localTableModel();

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();

	private NativeButton cmdSave = new NativeButton("Save & Post");
	private NativeButton cmdRevert = new NativeButton("Revert");
	private NativeButton cmdDel = new NativeButton("Delete");
	private NativeButton cmdAddDescr = new NativeButton("Add description");

	private ComboBox txtIDlist = new ComboBox("ID list");
	private List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();

	private Table tbl_list = new Table();

	private String QRYSES = "";
	private boolean listnerAdded = false;

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		commandLayout.removeAllComponents();
		data_lists.clearALl();
		tbl_list.removeAllItems();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();

		// centralPanel.setSizeFull();
		// mainLayout.setSizeFull();
		// basicLayout.setSizeFull();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		txtIDlist.setWidth("400px");
		tbl_list.setWidth("400px");
		tbl_list.setSelectable(true);

		txtIDlist.setImmediate(true);
		txtIDlist.setNewItemsAllowed(true);

		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, txtIDlist);
		ResourceManager.addComponent(mainLayout, tbl_list);

		ResourceManager.addComponent(commandLayout, cmdSave);
		ResourceManager.addComponent(commandLayout, cmdDel);
		ResourceManager.addComponent(commandLayout, cmdRevert);
		ResourceManager.addComponent(commandLayout, cmdAddDescr);

		try {
			utilsVaadin.FillCombo(txtIDlist,
					"select distinct idlist,idlist idlist2 from relists ", con);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (!listnerAdded) {
			txtIDlist.setNewItemHandler(new NewItemHandler() {

				public void addNewItem(String newItemCaption) {
					dataCell dc = new dataCell(newItemCaption, newItemCaption);
					txtIDlist.addItem(dc);
					txtIDlist.setValue(dc);
				}
			});
			cmdAddDescr.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					data_lists.addRecord();
					fill_table();
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
			txtIDlist.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if (txtIDlist.getValue() != null) {
						QRYSES = txtIDlist.getValue().toString().toUpperCase();
					} else {
						QRYSES = "";
					}
					load_data();

				}
			});
			listnerAdded = true;
		}
	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();

		if (data_lists.getDbclass() == null) {
			try {
				data_lists.createDBClassFromConnection(con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		createView();
		load_data();

	}

	public void load_data() {
		tbl_list.removeAllItems();
		data_lists.clearALl();
		if (txtIDlist.getValue() == null) {
			return;
		}
		try {
			boolean e = utils.findFieldInRS("RELISTS", "POS", con);
			if (!e)
				data_lists.executeQuery("select * from relists where idlist='"
						+ QRYSES + "' order by descr", true);
			else
				data_lists.executeQuery("select * from relists where idlist='"
						+ QRYSES + "' order by pos,descr", true);

			lstItemCols.clear();

			ColumnProperty cp = new ColumnProperty();
			lstItemCols.add(cp);
			cp.data_type = Parameter.DATA_TYPE_STRING;
			cp.col_class = TextField.class;
			cp.descr = "Descr";
			cp.display_width = 250;
			cp.colname = "DESCR";
			cp.pos = 1;
			cp.display_format = "NONE";
			tbl_list.addContainerProperty(cp.descr, cp.col_class, null);
			tbl_list.setColumnWidth(cp.descr, cp.display_width);

			if (e) {
				cp = new ColumnProperty();
				lstItemCols.add(cp);
				cp.data_type = Parameter.DATA_TYPE_NUMBER;
				cp.col_class = TextField.class;
				cp.descr = "POS";
				cp.display_width = 50;
				cp.colname = "POS";
				cp.pos = 2;
				cp.display_format = utils.FORMAT_QTY;
				tbl_list.addContainerProperty(cp.descr, cp.col_class, null);
				tbl_list.setColumnWidth(cp.descr, cp.display_width);

			}

			fill_table();
		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public void fill_table() {
		tbl_list.removeAllItems();
		utilsVaadin.query_data2(tbl_list, data_lists, lstItemCols);
	}

	public void print_data() {

	}

	public void save_data() {
		try {
			if (QRYSES.isEmpty()) {
				return;
			}
			boolean e = utils.findFieldInRS("RELISTS", "POS", con);

			String sqlIns = "INSERT INTO RELISTS (idlist,name,descr)  values (:IDLIST,:NAME,:DESCR)";
			if (e) {
				sqlIns = "INSERT INTO RELISTS (idlist,name,descr,pos)  values (:IDLIST,:NAME,:DESCR, :POS )";
			}
			try {
				con.setAutoCommit(false);
				utils.execSql("delete from relists where idlist='" + QRYSES
						+ "'", con);
				QueryExe qe = new QueryExe(con);
				qe.setSqlStr(sqlIns);
				qe.parse();
				for (int i = 0; i < data_lists.getRows().size(); i++) {
					if (data_lists.getFieldValue(i, "DESCR") != null
							&& !data_lists.getFieldValue(i, "DESCR").toString()
									.isEmpty()) {
						qe.setParaValue("NAME",
								data_lists.getFieldValue(i, "DESCR"));
						qe.setParaValue("DESCR",
								data_lists.getFieldValue(i, "DESCR"));
						qe.setParaValue("IDLIST", QRYSES);
						if (e)
							qe.setParaValue("POS",
									data_lists.getFieldValue(i, "POS"));

						qe.execute(false);
					}
				}

				qe.close();
				con.commit();
				parentLayout.getWindow().showNotification("Saved Successfully");
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
		initForm();
		txtIDlist.setValue(null);

	}

	public void showStandAlone(String id) {
		showInitView();
		if (id == null || id.isEmpty()) {
			txtIDlist.setValue(null);
			return;
		}

		txtIDlist.setValue(utilsVaadin.findByValue(txtIDlist, id));
		if (txtIDlist.getValue() == null) {
			dataCell dc = new dataCell(id, id);
			txtIDlist.addItem(dc);
			txtIDlist.setValue(dc);
		}
		QRYSES = txtIDlist.getValue().toString().toUpperCase();
		load_data();

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

}
