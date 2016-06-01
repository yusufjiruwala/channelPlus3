package com.generic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;

public class PracticeSearchField extends AbstractField {

	private Object keyValue = null;
	private String lookupValue = "";
	private String display = "";
	public String keyColumn = "";
	public String lookupColumn = "";
	public localTableModel data = null;
	public String listSQL = "";
	public String searchSQL = "";
	public HorizontalLayout hzLayout = new HorizontalLayout();

	public Connection con = null;

	public Label lblText = new Label();
	public NativeButton searchCmd = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Search..");
	public NativeButton clearCmd = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "clear..");

	public PracticeSearchField() {
		super();
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		createView();
	}

	public PracticeSearchField(localTableModel data, String keyColumn,
			String lookupColumn) {
		this.lookupColumn = lookupColumn;
		this.keyColumn = keyColumn;
		createView();
	}

	public void createView() {
		setWidth("100%");
		lblText.setWidth("100%");
		clearCmd.setWidth("100%");
		searchCmd.setWidth("100%");

		ResourceManager.addComponent(hzLayout, lblText);
		ResourceManager.addComponent(hzLayout, clearCmd);
		ResourceManager.addComponent(hzLayout, searchCmd);
		hzLayout.setExpandRatio(lblText, 3.8f);
		hzLayout.setExpandRatio(clearCmd, .1f);
		hzLayout.setExpandRatio(searchCmd, .1f);
	}

	public Object getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(Object keyValue) {
		setKeyValue(null, -1);
	}

	public void setValue(Object keyValue) {
		setKeyValue(null, -1);
	}

	public void setKeyValue(Object keyValue, int rowno) {
		this.keyValue = keyValue;
		lookupValue = "";
		display = "";
		if (data != null && keyValue != null && rowno >= 0) {
			lookupValue = utils
					.nvl(data.getFieldValue(rowno, lookupColumn), "");
			if (lookupValue.isEmpty()) {
				getDBLookupValue();
			}
			display = keyValue + " - " + lookupValue;
		}
		if (rowno < 0) {
			getDBLookupValue();
			display = keyValue + " - " + lookupValue;
		}
	}

	public void getDBLookupValue() {
		lookupValue = "";
		if (lookupColumn.isEmpty() || keyValue == null) {
			return;
		}
		QueryExe qe = new QueryExe(searchSQL, con);
		qe.setParaValue("KEYVALUE", keyValue);
		try {
			ResultSet rs = qe.executeRS();
			if (rs != null && rs.first()) {
				if (rs.getMetaData().getColumnCount() > 1) {
					lookupValue = rs.getString(2);
				} else {
					lookupValue = rs.getString(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getLookupValue() {
		return lookupValue;
	}

	public String getDisplay() {
		return display;
	}

	@Override
	public Class<?> getType() {
		return null;
	}

}
