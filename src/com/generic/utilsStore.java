package com.generic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class utilsStore {
	private static Connection con = null;

	public static void setConnection(Connection vcon) {
		con = vcon;

	}

	public static boolean CanItemBelowZero_Absolute(int strno, String rfr,
			double allqty, Timestamp prd_dt, Timestamp exp_Dt, Connection vcon)
			throws SQLException {
		con = vcon;
		return CanItemBelowZero_Absolute(strno, rfr, allqty, prd_dt, exp_Dt);
	}

	public static boolean CanItemBelowZero_Absolute(int strno, String rfr,
			double allqty, Timestamp prd_dt, Timestamp exp_Dt)
			throws SQLException {
		double qtden = getQtyDenied(rfr);

		if (qtden == -1) {
			return true;
		}
		String sq = "select nvl(sum(allqty-qty_reserved),0) bal from stori where stra=? and refer=? and "
				+ " prd_dt=? and exp_dt=?";
		PreparedStatement ps = con.prepareStatement(sq,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);		
		ps.setInt(1, strno);
		ps.setString(2, rfr);
		ps.setTimestamp(3, prd_dt);
		ps.setTimestamp(4, exp_Dt);
		ResultSet rs = ps.executeQuery();
		rs.first();
		if (rs.getDouble("bal") - allqty < qtden) {
			ps.close();
			return false;
		}
		ps.close();
		return true;
	}

	public static double getQtyDenied(String rfr) throws SQLException {
		String vl = utils
				.getSqlValue(
						"select repair.getsetupvalue_2('ITEMBELOWZERO') from dual",
						con);
		String pr = rfr;
		double qtden = -1;
		PreparedStatement ps2 = con.prepareStatement(
				"select qtydenied,parentitem from items where reference=?",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rst = null;
		while ((pr != null && pr.length() > 0) && qtden == -1) {
			if (pr != null && pr.length() > 0) {
				ps2.setString(1, pr);
				rst = ps2.executeQuery();
				if (rst.first()) {
					pr = rst.getString("parentitem");
					qtden = rst.getDouble("qtydenied");
				}
			}
			if (qtden == -1 && vl.equals("FALSE")) {
				ps2.close();
				return 0;
			}

		}
		ps2.close();
		return qtden;
	}
}
