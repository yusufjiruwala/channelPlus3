package com.generic;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class TableLayoutVaadin extends VerticalLayout {
	protected Table table = new Table();
	protected HorizontalLayout hzToolbar1 = new HorizontalLayout();
	protected HorizontalLayout hzToolbar = new HorizontalLayout();
	public localTableModel data = new localTableModel();
	public List<ColumnProperty> listFields = new ArrayList<ColumnProperty>();
	public String sql = "";
	public Connection con = Channelplus3Application.getInstance()
			.getFrmUserLogin().getDbc().getDbConnection();

	public NativeButton cmdAdd = ControlsFactory.CreateToolbarButton(
			"img/newmenu.png", "Add new record");
	public NativeButton cmdDel = ControlsFactory.CreateToolbarButton(
			"img/remove.png", "Del record");
	public NativeButton cmdRevert = ControlsFactory.CreateToolbarButton(
			"img/revert.png", "Revert");
	public boolean isEditablel = true;
	private boolean listnerAdded = false;
	private List<String> listSummaryFields = new ArrayList<String>();
	private Map<String, TextField> mapSummaryFields = new HashMap<String, TextField>();
	private rowTriggerListner rowlistner = null;
	private String numberFormat = Channelplus3Application.getInstance()
			.getFrmUserLogin().FORMAT_MONEY;
	public Triggers trig = null;
	public ClickListener cmd_add_listner = new ClickListener() {

		public void buttonClick(ClickEvent event) {
			add_record();
		}
	};

	public void addSummaryField(String fldname, TextField txt) {
		listSummaryFields.add(fldname);
		mapSummaryFields.put(fldname, txt);
		if (rowlistner == null) {
			rowlistner = new rowTriggerListner() {

				public void onCalc(int cursorNo) {
					if (trig != null)
						trig.beforeSumOnCalc(cursorNo);
					DecimalFormat dc = new DecimalFormat(numberFormat);
					for (int i = 0; i < listSummaryFields.size(); i++) {
						String fld = listSummaryFields.get(i);
						double d = data.getSummaryOf(fld,
								localTableModel.SUMMARY_SUM);
						utilsVaadin.setValueByForce(mapSummaryFields.get(fld),
								dc.format(d));

					}
				}
			};
			data.setRowlistner(rowlistner);
		}
	}

	public TableLayoutVaadin(AbstractOrderedLayout parentLayout) {
		init(parentLayout);
	}

	public void refreshRow(int rowno) {
		utilsVaadin.refreshRowFromData(data, getTable(), rowno, listFields);
	}

	public Table getTable() {
		return this.table;
	}

	public TableLayoutVaadin(AbstractOrderedLayout parentLayout,
			boolean showToolbar) {
		init(parentLayout);
		if (!showToolbar) {
			removeComponent(hzToolbar);
		}
	}

	public TableLayoutVaadin(AbstractOrderedLayout parentLayout,
			boolean showToolbar, boolean isEditable) {
		this.isEditablel = isEditable;
		init(parentLayout);
		if (!showToolbar) {
			removeComponent(hzToolbar);
		}

	}

	public void init(AbstractOrderedLayout parentLayout) {
		if (parentLayout != null) {
			ResourceManager.addComponent(parentLayout, this);
			createView();
		}
		try {
			this.data.createDBClassFromConnection(con);
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public void resetLayout() {
		removeAllComponents();
		table.removeAllItems();
		data.clearALl();
		listSummaryFields.clear();
		mapSummaryFields.clear();
		hzToolbar.removeAllComponents();
	}

	public void createView() {
		resetLayout();
		table.setSizeFull();
		table.setSelectable(true);
		
		hzToolbar1.addStyleName("toolpanel");
		hzToolbar1.setWidth("100%");
		setWidth("100%");
		ResourceManager.addComponent(this, hzToolbar1);
		ResourceManager.addComponent(this, table);
		ResourceManager.addComponent(hzToolbar1, hzToolbar);
		ResourceManager.addComponent(hzToolbar, cmdAdd);
		ResourceManager.addComponent(hzToolbar, cmdDel);
		ResourceManager.addComponent(hzToolbar, cmdRevert);

		if (!listnerAdded) {
			cmdAdd.addListener(cmd_add_listner);
			cmdRevert.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					fill_table();

				}
			});

			cmdDel.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					Integer i = (Integer) table.getValue();
					boolean bb = true;

					if (trig != null)
						bb = trig.beforeDelRecord();
					if (!bb)
						return;
					if (i != null) {
						data.deleteRow(i);
						fill_table();
						if (trig != null)
							trig.afterDelRecord(i);
					}
				}
			});

			listnerAdded = true;
		}

	}

	public void add_record() {
		if (trig != null)
			if (!trig.beforeAddRecord())
				return;
		int r = data.addRecord();
		do_default_values(r);
		fill_table();
		if (trig != null)
			trig.AfterAddRecord(r);

	}

	public void do_default_values(int r) {
		for (Iterator iterator = listFields.iterator(); iterator.hasNext();) {
			ColumnProperty cp = (ColumnProperty) iterator.next();
			if (cp.defaultValue != null) {
				data.setFieldValue(r, cp.colname, cp.defaultValue);
				if (cp.defaultValue.toString().equals("#AUTONUMBER_")) {
					data.setFieldValue(r, cp.colname, BigDecimal.valueOf(r + 1));
				}

			}
		}
	}

	public void executeQuery(String sql, List<ColumnProperty> lst)
			throws SQLException {
		data.executeQuery(sql, true);
		if (lst == null) {
			createColumns();
		} else {
			listFields.addAll(lst);
		}
	}

	public void fill_table() {
		table.removeAllItems();
		for (Iterator iterator = listFields.iterator(); iterator.hasNext();) {
			ColumnProperty cp = (ColumnProperty) iterator.next();
			table.addContainerProperty(cp.descr, cp.col_class, null);
			table.setColumnWidth(cp.descr, cp.display_width);
		}

		utilsVaadin.query_data2(table, data, listFields);
		table.requestRepaintAll();
	}

	public void createColumns() {
		listFields.clear();
		int pos = 1;
		for (Iterator iterator = data.getQrycols().iterator(); iterator
				.hasNext();) {
			qryColumn qc = (qryColumn) iterator.next();
			ColumnProperty cp = new ColumnProperty();
			listFields.add(cp);
			Class cs = Label.class;

			if (isEditablel == true) {
				cs = TextField.class;
			} else {
				cs = Label.class;
			}
			if (qc.isDateTime() && isEditablel) {
				cs = DateField.class;
			}
			cp.col_class = cs;
			cp.colname = qc.getColname();
			cp.descr = qc.getTitle();
			cp.display_width = qc.getWidth();
			cp.display_type = "NONE";
			cp.pos = pos++;
			cp.display_align = "ALIGN_LEFT";
			cp.display_format = "NONE";
			cp.other_styles = "";
		}
	}

	public void hideToolbar() {
		removeComponent(hzToolbar1);
	}

	public void disableToolbar() {
		hzToolbar1.setEnabled(false);
	}

	public void enableToolbar() {
		hzToolbar1.setEnabled(true);
	}

	public void hideAddCommand() {
		hzToolbar1.removeComponent(cmdAdd);
	}

	public void hideDelCommand() {
		hzToolbar1.removeComponent(cmdDel);
	}

	public void hideRevertCommand() {
		hzToolbar1.removeComponent(cmdRevert);
	}

	public void addColumn(ColumnProperty cp, boolean fill_table) {
		boolean fnd = false;
		for (Iterator iterator = listFields.iterator(); iterator.hasNext();) {
			ColumnProperty c = (ColumnProperty) iterator.next();
			if (cp.colname.equals(c.colname)) {
				fnd = true;
				return;
			}

		}

		cp.pos = listFields.size() + 1;
		listFields.add(cp);
		if (fill_table) {
			fill_table();
		}

	}

	public void insert_to_table(String tablename,
			Map<String, Object> fixedValues, String cols, String excludeCols)
			throws SQLException {

		String cols2 = cols;
		if (cols.isEmpty()) {
			PreparedStatement pstt = con.prepareStatement("select *from "
					+ tablename + " where 1=2",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = pstt.executeQuery();
			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++)
				cols2 = cols2 + (i == 0 ? "" : ",")
						+ rs.getMetaData().getColumnName(i + 1);
			pstt.close();
		}

		String[] cola = cols2.split(",");
		String valcols = "";
		String inscols = "";

		for (int i = 0; i < cola.length; i++)
			cola[i] = cola[i].replace("\"", "").toUpperCase();

		for (int i = 0; i < cola.length; i++)
			if (!excludeCols.toUpperCase().contains(cola[i]))
				inscols = inscols + (inscols.isEmpty() ? "" : ",") + cola[i];

		for (int i = 0; i < cola.length; i++)
			if (!excludeCols.toUpperCase().contains(cola[i]))
				valcols = valcols + (valcols.isEmpty() ? "" : ",") + ":"
						+ cola[i];

		QueryExe qe = new QueryExe(con);
		qe.setSqlStr("insert into " + tablename + " (" + inscols + ") values ("
				+ valcols + ")");

		qe.parse();
		for (int i = 0; i < data.getRows().size(); i++) {
			for (int j = 0; j < cola.length; j++)
				qe.setParaValue(cola[j], data.getFieldValue(i, cola[j]));

			if (fixedValues != null) {
				List<String> lstcol = new ArrayList<String>(
						fixedValues.keySet());
				for (Iterator iterator = lstcol.iterator(); iterator.hasNext();) {
					String col = (String) iterator.next();
					qe.setParaValue(col, fixedValues.get(col));
				}
			}

			qe.execute(false);
		}
		qe.close();

	}

	public HorizontalLayout getHzLayout() {
		return hzToolbar;
	}

	public interface Triggers {
		public void AfterAddRecord(int recno);

		public boolean beforeAddRecord();

		public void beforeSumOnCalc(int recno);

		public boolean beforeDelRecord();

		public void afterDelRecord(int recno);

	}

}
