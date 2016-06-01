package com.generic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.main.channelplus3.Channelplus3Application;

public class SecurityParameter {
	public String form_type = "";
	public String form_name = "";
	public int profileno = Channelplus3Application.getInstance()
			.getFrmUserLogin().CURRENT_PROFILENO;
	public boolean canAccess = true;
	public boolean canUpdates = true;
	public boolean canInsert = true;
	public boolean canDelete = true;

	public List<String> hiddenFields = new ArrayList<String>();
	public List<String> disabledFields = new ArrayList<String>();

	public List<String> disabledCols = new ArrayList<String>();
	public List<String> hiddenCols = new ArrayList<String>();

	public static SecurityParameter getInstanceOf(String formtype,
			String formname, Connection con) {
		SecurityParameter p = new SecurityParameter();
		p.fetchData(formtype, formname, con);
		return p;
	}

	public static SecurityParameter getInstanceOf(String formtype,
			String formname) {
		SecurityParameter p = new SecurityParameter();
		Connection con = Channelplus3Application.getInstance()
				.getFrmUserLogin().getDbc().getDbConnection();
		p.fetchData(formtype, formname, con);
		return p;
	}

	public void fetchData(String formtype, String formname, Connection con) {
		this.form_type = formtype;
		this.form_name = formname;
		this.canAccess = true;
		this.canDelete = true;
		this.canInsert = true;
		this.canUpdates = true;
		try {
			ResultSet rs = utils.getSqlRS(
					"select *from cp_security where TYPEOFSCREN='" + form_type
							+ "' and id='" + form_name + "'", con);
			if (rs != null && rs.first()) {
				if (rs.getString("ACCESS_PROFILE").indexOf(
						"\"" + profileno + "\"") <= -1
						&& rs.getString("ACCESS_PROFILE").indexOf(
								"\"" + 0 + "\"") <= -1) {
					canAccess = false;
				}
				if (rs.getString("UPDATE_PROFILE").indexOf(
						"\"" + profileno + "\"") <= -1
						&& rs.getString("UPDATE_PROFILE").indexOf(
								"\"" + 0 + "\"") <= -1) {
					canUpdates = false;
				}
				if (rs.getString("DELETE_PROFILE").indexOf(
						"\"" + profileno + "\"") <= -1
						&& rs.getString("DELETE_PROFILE").indexOf(
								"\"" + 0 + "\"") <= -1) {
					canDelete = false;
				}
				if (rs.getString("INSERT_PROFILE").indexOf(
						"\"" + profileno + "\"") <= -1
						&& rs.getString("INSERT_PROFILE").indexOf(
								"\"" + 0 + "\"") <= -1) {
					canInsert = false;
				}
				rs.getStatement().close();
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
