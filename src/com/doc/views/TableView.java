package com.doc.views;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.generic.DBClass;
import com.generic.Row;
import com.generic.localTableModel;
import com.generic.qryColumn;
import com.generic.utils;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class TableView implements Views, Serializable {

	public interface checkDefaultselectionListner {
		public void onInitialize(List<Boolean> lst);
	}

	public static final String SELECT_TITLE = "Select";
	
	private localTableModel data = null;
	private boolean filtering = false;
	private AbstractLayout parentPanel = null;
	private GridLayout filterLayout = new GridLayout(4, 1);
	private Panel vl = new Panel();
	private Table table = new Table();
	private Button cmdFilter = new Button("Filter");
	private Button cmdCancelFilter = new Button(" Cancel Filter");
	private Button cmdSelectAll = new Button(" Select All");
	private Button cmdUnSelectAll = new Button(" Un-Select All");
	private Button cmdClose = new Button("Close");
	private TextField txtFilter = new TextField();
	private static int allid = 0;
	private DBClass dbc = null;
	private String sqlstring = "";
	private PreparedStatement ps = null;
	private int selectionValue = -1;
	private int idno = 0;
	private boolean listener_added = false;
	private Property.ValueChangeListener onselection = null;
	private SelectionListener onselection2 = null;
	private boolean checkSelectionField = false;
	private List<Boolean> listCheckSelection = new ArrayList<Boolean>();
	private boolean checkDefaultSelection = false;
	private checkDefaultselectionListner listnerDefaultSelection = null;
	private List<String> listHiddenCols = new ArrayList<String>();
	private List<String> listInputCols = new ArrayList<String>();
	private String strScannedUpdateList = "";
	public String strScannedColSeperator = "--";
	public String strScannedRowSeperator = ",";

	public String getStrScannedUpdateList() {
		return strScannedUpdateList;
	}

	public void setScannedStrUpdateList(String strUpdateList) {
		this.strScannedUpdateList = strUpdateList;
	}

	public List<String> getListInputCols() {

		return listInputCols;
	}

	private boolean editable = false;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Table getTable() {
		return table;
	}

	private boolean autoSelection = false;

	public List<String> getListHiddenCols() {
		return listHiddenCols;
	}

	public static interface SelectionListener {
		public void onSelection(TableView tv);
	};
	
	public checkDefaultselectionListner getListnerDefaultSelection() {
		return listnerDefaultSelection;
	}

	public void setListnerDefaultSelection(
			checkDefaultselectionListner listnerDefaultSelection) {
		this.listnerDefaultSelection = listnerDefaultSelection;
	}

	public boolean isCheckDefaultSelection() {
		return checkDefaultSelection;
	}

	public void setCheckDefaultSelection(boolean checkDefaultSelection) {
		this.checkDefaultSelection = checkDefaultSelection;
	}

	public List<Boolean> getLstCheckSelection() {
		return listCheckSelection;
	}

	public boolean isCheckSelectionField() {
		return checkSelectionField;
	}

	public void setCheckSelectionField(boolean checkSelectionField) {
		this.checkSelectionField = checkSelectionField;
	}

	public void setOnseleciton(Property.ValueChangeListener v) {
		this.onselection = v;
	}

	public void setOnseleciton2(SelectionListener sel) {
		this.onselection2 = sel;
	}

	public Property.ValueChangeListener getOnselection() {
		return onselection;
	}

	public int getSelectionValue() {
		return selectionValue;
	}

	public TableView() {
		setIdno(allid++);
	}

	public TableView(DBClass dbc) {
		this.dbc = dbc;
	}

	public void setDbc(DBClass dbc) {
		this.dbc = dbc;
	}

	public void setConnection(Connection c) {
		this.dbc.setDbConnection(c);
	}

	public int FetchSql(String sql) throws SQLException {
		if (dbc == null) {
			return -1;
		}
		sqlstring = sql;

		ps = dbc.getDbConnection().prepareStatement(sqlstring,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		return fetchSql();
	}

	public int fetchSql() {
		int rows = 0;
		if (ps == null) {
			return -1;
		}
		try {
			if (data != null) {
				data.clearALl();
			} else {
				data = new localTableModel();

			}
			List<Row> r = utils.convertRows(ps, null, "");
			data.appendRows(r);
			data.setMasterRowsAsRows();
			data.getQrycols().clear();
			data.getQrycols().addAll(utils.getColumnsList(ps));
			data.getVisbleQrycols().clear();
			data.getVisbleQrycols().addAll(data.getQrycols());
			for (Iterator iterator = listHiddenCols.iterator(); iterator
					.hasNext();) {
				String nm = (String) iterator.next();
				qryColumn qc = data.getColByName(nm);
				if (qc != null) {
					data.getVisbleQrycols().remove(
							data.getVisbleQrycols().indexOf(qc));
				}
			}
			if (checkSelectionField) {
				listCheckSelection.clear();
				for (int i = 0; i < data.getRows().size(); i++) {
					listCheckSelection.add((Boolean) checkDefaultSelection);
				}
			}
			ps.close();
		} catch (SQLException e) {
			Logger.getLogger(TableView.class.getName()).log(Level.SEVERE, null,
					e);
		}
		updateScannedRows();
		return rows;
	}

	public void createView() {
		createView(false);
	}

	public void updateScannedRows() {
		if (strScannedUpdateList.trim().isEmpty())
			return;

		String[] rw = strScannedUpdateList.split(strScannedRowSeperator);
		for (String r : rw) {
			String rr = r.trim();
			// if (rr.contains(strScannedColSeperator)) {
			String[] cs = rr.split(strScannedColSeperator);
			String colname = data.getColByPos(0).getColname();
			int rowno = data.locate(colname, cs[0].trim(),
					localTableModel.FIND_EXACT);
			for (int i = 0; i < cs.length; i++) {
				colname = data.getColByPos(i).getColname();
				if (rowno >= 0) {
					if (i == 0)
						listCheckSelection.set(rowno, (Boolean) true);
					if (listInputCols.indexOf(colname) >= 0 && i > 0)
						data.setFieldValue(rowno, colname, cs[i].trim());

				}
				// if rowno>=0...
			}
			// for i...
			// } else
			// if r.contains ......
		}
		// for r:rw ....
	}

	@SuppressWarnings("serial")
	public void createView(boolean update) {
		if (parentPanel == null) {
			return;
		}
		if (data == null) {
			return;
		}
		if (update == false) {
			parentPanel.removeAllComponents();
			filterLayout.removeAllComponents();
			vl.removeAllComponents();

			filterLayout.addComponent(txtFilter);
			filterLayout.addComponent(cmdFilter);
			filterLayout.addComponent(cmdCancelFilter);
			if (checkSelectionField) {
				filterLayout.addComponent(cmdSelectAll);
				filterLayout.addComponent(cmdUnSelectAll);
			}
			parentPanel.addComponent(filterLayout);
			parentPanel.addComponent(table);
			if (parentPanel.getWindow() != null)
				parentPanel.getWindow().setCloseShortcut(KeyCode.ESCAPE, null);
			table.setWidth("100%");
			table.setHeight("330px");

			txtFilter.setImmediate(true);
			table.setImmediate(true);
			table.setSelectable(true);

			if (listener_added == false) {
				final TableView tbl = this;
				txtFilter.addListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							data.setDynamicFilter(event.getProperty()
									.getValue().toString());
							createView(true);
							if (table.getWindow() != null)
								table.getWindow().requestRepaintAll();
						} catch (Exception e) {
							Logger.getLogger(TableView.class.getName()).log(
									Level.SEVERE, null, e);
						}

					}
				});
				cmdSelectAll.addListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						listCheckSelection.clear();
						for (int i = 0; i < data.getRows().size(); i++) {
							listCheckSelection.add(Boolean.TRUE);
						}
						createView();
					}
				});
				cmdUnSelectAll.addListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						listCheckSelection.clear();
						for (int i = 0; i < data.getRows().size(); i++) {
							listCheckSelection.add(Boolean.FALSE);
						}
						createView();
					}
				});

				cmdFilter.addListener(new ClickListener() {

					public void buttonClick(ClickEvent event) {
						try {
							data.setDynamicFilter(txtFilter.getValue()
									.toString());
							createView(true);
							if (table.getWindow() != null)
								table.getWindow().requestRepaintAll();
						} catch (Exception e) {
							Logger.getLogger(TableView.class.getName()).log(
									Level.SEVERE, null, e);
						}
					}
				});

				cmdCancelFilter.addListener(new ClickListener() {
					public void buttonClick(ClickEvent event) {
						try {
							txtFilter.setValue("");
							data.setDynamicFilter(txtFilter.getValue()
									.toString());
							createView(true);
							if (table.getWindow() != null)
								table.getWindow().requestRepaintAll();
						} catch (Exception e) {
							Logger.getLogger(TableView.class.getName()).log(
									Level.SEVERE, null, e);
						}
					}
				});
				table.addListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						if (table.getValue() != null)
							selectionValue = (Integer) table.getValue();

						if (onselection != null) {
							onselection.valueChange(event);
						}
						if (onselection2 != null) {
							onselection2.onSelection(tbl);
						}
					}
				});

				listener_added = true;
			}

		}

		table.removeAllItems();

		selectionValue = -1;

		for (int i = 0; i < data.getVisbleQrycols().size(); i++) {
			if (checkSelectionField && i == 0) {
				table.addContainerProperty(SELECT_TITLE, CheckBox.class, null);
			}
			String fldname = data.getVisbleQrycols().get(i).getColname();
			if (editable && listInputCols.indexOf(fldname) > -1)
				table.addContainerProperty(data.getVisbleQrycols().get(i)
						.getTitle(), TextField.class, null);
			else
				table.addContainerProperty(data.getVisbleQrycols().get(i)
						.getTitle(), data.getVisbleQrycols().get(i)
						.getColClass(), null);
		}

		// LISTNER FOR INITIALIZING.
		if (checkSelectionField && listnerDefaultSelection != null) {
			listnerDefaultSelection.onInitialize(listCheckSelection);
		}

		for (int i = 0; i < data.getRows().size(); i++) {
			table.addItem(getVisibleArrayOfRowData(i), Integer.valueOf(i));
		}

		if (update == false) {
			final TableView tbl = this;
			cmdClose.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (parentPanel.getWindow() != null)
						((Window) parentPanel.getParent())
								.removeWindow(parentPanel.getWindow());
				}
			});
		}
		txtFilter.focus();

	}

	public Object[] getVisibleArrayOfRowData(final int i) {
		List<Object> ar = new ArrayList<Object>();
		if (checkSelectionField) {
			final CheckBox chk = new CheckBox();
			chk.setValue(listCheckSelection.get(i));
			ar.add(chk);
			chk.setImmediate(true);
			chk.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					listCheckSelection.set(i, (Boolean) chk.booleanValue());
				}
			});
		}
		for (int j = 0; j < data.getVisbleQrycols().size(); j++) {
			String s = data.getVisbleQrycols().get(j).getColname();
			if (listInputCols.indexOf(s) > -1) {
				final TextField t = new TextField();
				t.setImmediate(true);
				t.setValue(data.getDisplayAt(i, j));
				ar.add(t);
				final qryColumn qc = data.getVisbleQrycols().get(j);
				t.addListener(new ValueChangeListener() {
					@Override
					public void valueChange(ValueChangeEvent event) {
						data.setFieldValue(i, qc.getColname(), event
								.getProperty().getValue());

					}
				});
			} else {
				ar.add(data.getDisplayAt(i, j));
			}

		}
		return ar.toArray();
	}

	public qryColumn addColumn(String s) {
		if (data == null) {
			data = new localTableModel();
		}
		qryColumn qc = new qryColumn(data.getQrycols().size(), s.toUpperCase());
		data.getQrycols().add(qc);
		data.getVisbleQrycols().add(qc);
		for (int i = 0; i < data.getRows().size(); i++) {
			data.setFieldValue(s.toUpperCase(), "");
		}
		return qc;
	}

	public int addRow(Row r) {
		data.getRows().add(r);
		if (checkSelectionField) {
			listCheckSelection.add(checkDefaultSelection);
		}
		return data.getRows().size();
	}

	public void setIdno(int idno) {
		this.idno = idno;
	}

	public int getIdno() {
		return idno;
	}

	public void setParentPanel(AbstractLayout parentPanel) {
		this.parentPanel = parentPanel;
	}

	public AbstractLayout getParentPanel() {
		return parentPanel;
	}

	public void setFiltering(boolean filtering) {
		this.filtering = filtering;
	}

	public boolean isFiltering() {
		return filtering;
	}

	public void setData(localTableModel data) {
		this.data = data;
	}

	public localTableModel getData() {
		return data;
	}

	public TextField getTxtFilter() {
		return txtFilter;
	}

	public Button getCmdClose() {
		return cmdClose;
	}

	public Button getCmdCancelFilter() {
		return cmdCancelFilter;
	}

	public void setAutoSelection(boolean b) {
		autoSelection = true;

	}

	public boolean getAutoSelection() {
		return autoSelection;
	}
}