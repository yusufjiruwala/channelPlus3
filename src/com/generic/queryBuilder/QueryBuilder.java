package com.generic.queryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.doc.views.TableView;
import com.doc.views.Query.QueryView;
import com.doc.views.Query.tableDataListner;
import com.doc.views.TableView.SelectionListener;
import com.generic.ColumnProperty;
import com.generic.DynamicReportSetting;
import com.generic.Parameter;
import com.generic.qryColumn;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class QueryBuilder {
	private String sqlstr = "";
	private List<Parameter> listParams = new ArrayList<Parameter>();
	private Map<String, List<String>> sqlMap = new HashMap<String, List<String>>();
	private List<ColumnProperty> listcols = new ArrayList<ColumnProperty>();
	private Connection con = null;
	private String sqlId = "";
	private QueryView qv = null;
	private String sqlCols = "";
	private String sqlOrdby = "";
	private String sqlGroupby = "";
	private String sqlWhere = "";
	private String sqlView = "";
	private Window wndlist = new Window("Select Report here");
	private VerticalLayout layout = new VerticalLayout();
	private boolean fetchData = false;
	private int page_width = 595;
	private int page_height = 842;
	private String rep_des_file = "";
	public String title1 = "";
	public String title2 = "";
	public String exeBefore = "";
	public String exeAfter = "";

	public boolean isFetchData() {
		return fetchData;
	}

	public void setFetchData(boolean fetchData) {
		this.fetchData = fetchData;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public Connection getCon() {
		return con;
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

	public List<Parameter> getListParams() {
		return listParams;
	}

	public QueryView getQv() {
		return qv;
	}

	public QueryBuilder(Connection con) {
		this.con = con;
	}

	public QueryBuilder() {

	}

	public QueryBuilder(Connection con, String id) {
		this.con = con;
		sqlId = id;
	}

	// building sql query from database tables
	public void buildSql() throws Exception {
		buildSql(sqlId);
	}

	public void buildSql(Connection con, String no) throws Exception {
		this.con = con;
		buildSql(no);
	}

	public void buildSql(boolean selectSubReport, final AbstractLayout cp)
			throws Exception {
		buildSql(sqlId, selectSubReport, cp);
	}

	public void buildSql(String no, boolean selectSubReport,
			final AbstractLayout cp) throws Exception {
		wndlist.setContent(layout);
		float nos = Float.valueOf(utils.getSqlValue(
				"select nvl(count(*),0) cnts from INVQRYCOLS1 where parentrep='"
						+ no + "'", con));
		if (nos > 0) {
			if (nos > 1) {
				SelectionListener vc = new SelectionListener() {

					public void onSelection(TableView tv) {
						Channelplus3Application.getInstance().getMainWindow()
								.removeWindow(wndlist);
						String cd = (String) tv.getData().getFieldValue(
								tv.getSelectionValue(), "CODE");
						try {
							buildSql(cd);
							createView(cp);
						} catch (Exception e) {
							cp.getWindow().showNotification(e.getMessage());
						}
					}

				};
				utilsVaadin.showSearch(layout, vc, con,
						"select titlearb,code from invqrycols1 where parentrep='"
								+ no + "' order by rep_path", true);
			} else { // if not nos>1 then
				String idn = utils.getSqlValue(
						"select max(code) from invqrycols1 where parentrep='"
								+ no + "'", con);
				buildSql(idn);
				createView(cp);

			}

		} else {
			buildSql(no);
		}
	}

	public void buildSql(String idno) throws Exception {
		sqlId = idno;

		int summary_count = 0;

		if (sqlMap.size() > 0) {
			sqlMap.get("COLS").clear();
			sqlMap.get("WHERE").clear();
			sqlMap.get("GROUP").clear();
			sqlMap.get("SUBGROUP").clear();
			sqlMap.get("COLSDISP").clear();
			sqlMap.get("HIDECOL").clear();
			sqlMap.get("SUMMARY_COLS").clear();
		}
		sqlMap.clear();
		sqlMap.put("COLS", new ArrayList<String>());
		sqlMap.put("WHERE", new ArrayList<String>());
		sqlMap.put("GROUP", new ArrayList<String>());
		sqlMap.put("SUBGROUP", new ArrayList<String>());
		sqlMap.put("COLSDISP", new ArrayList<String>());
		sqlMap.put("HIDECOL", new ArrayList<String>());
		sqlMap.put("SUMMARY_COLS", new ArrayList<String>());
		String order_by[] = new String[] { "", "", "", "", "", "", "", "" };

		if (con == null) {
			throw new Exception("no connection to query builder");
		}
		PreparedStatement ps_1 = con.prepareStatement(
				"select *from INVQRYCOLS1 where code='" + idno + "'",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		PreparedStatement ps_2 = con.prepareStatement(
				"select *from INVQRYCOLS2 where code='" + idno
						+ "' ORDER BY INDEXNO",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs_2 = ps_2.executeQuery();
		// get counts for all from ps_2;
		rs_2.beforeFirst();
		while (rs_2.next()) {
			if ((rs_2.getString("SUMBY") != null && rs_2.getString("sumby")
					.length() > 0)
					|| ((rs_2.getString("formulahassum") != null && rs_2
							.getString("formulahassum").length() > 0))) {
				summary_count++;
			}

		}
		rs_2.beforeFirst();
		String whereclause = "";
		String col = "";
		String coldisp = "";
		String Stringquot = "'";
		listcols.clear();
		while (rs_2.next()) {

			col = utils.nvl(rs_2.getString("FORMULAFLD"), rs_2
					.getString("FIELD_NAME"));
			coldisp = utils.nvl(rs_2.getString("DISPLAY_NAME"), rs_2
					.getString("FIELD_NAME"));
			if (rs_2.getString("SUMBY") != null
					&& rs_2.getString("sumby").length() > 0) {
				col = rs_2.getString("SUMBY") + "(" + col + ")";
			}
			// if is where not 'Y'
			if ((rs_2.getString("ISWHERE") == null || rs_2.getString("ISWHERE")
					.length() == 0)) {
				ColumnProperty cp = new ColumnProperty();
				sqlMap.get("COLS").add(col + " \"" + coldisp + "\"");
				cp.col_class = String.class;
				cp.colname = coldisp;
				cp.descr = coldisp;
				cp.display_width = rs_2.getInt("COLWIDTH") * 3;
				cp.display_type = "";
				cp.pos = rs_2.getInt("indexno");
				cp.display_align = rs_2.getString("CP_ALIGN");
				cp.display_format = rs_2.getString("cp_format");
				listcols.add(cp);
			}

			// if whereoperator is not none
			if (!rs_2.getString("WHEREOPERATOR").equals("NONE")) {
				Stringquot = "";
				if (rs_2.getString("DATATYPEX").equals("VARCHAR2")) {
					Stringquot = "'";
				}
				whereclause = col + rs_2.getString("WHEREOPERATOR")
						+ Stringquot + rs_2.getString("WHERECLAUSE")
						+ Stringquot;
				sqlMap.get("WHERE").add(whereclause);
			}
			if (summary_count > 0
					&& (rs_2.getString("SUMBY") == null || rs_2.getString(
							"SUMBY").length() == 0)
					&& (rs_2.getString("formulahassum") == null || rs_2
							.getString("formulahassum").length() == 0)
					&& (rs_2.getString("ISWHERE") == null || rs_2.getString(
							"ISWHERE").length() == 0)) {
				sqlMap.get("GROUP").add(col);
			}
			if (rs_2.getString("orderno") != null
					&& rs_2.getString("orderno").length() > 0) {
				order_by[rs_2.getInt("orderno")] = col + " "
						+ rs_2.getString("ordertype");
			}
			if ((rs_2.getString("CP_HIDECOL") != null && rs_2.getString(
					"CP_HIDECOL").length() > 0)) {
				sqlMap.get("HIDECOL").add(coldisp);
			}
			if (rs_2.getString("HASGROSS") != null
					&& rs_2.getString("HASGROSS").equals("SUM")) {
				sqlMap.get("SUMMARY_COLS").add(coldisp);
			}
			if (rs_2.getString("CP_SUBGROUP") != null
					&& rs_2.getString("CP_SUBGROUP").length() > 0) {
				sqlMap.get("SUBGROUP").add(coldisp);
			}

		}
		ps_2.close();

		sqlstr = "";
		// iterate maps cols
		for (Iterator iterator = sqlMap.get("COLS").iterator(); iterator
				.hasNext();) {
			String str = (String) iterator.next();
			if (sqlCols.length() > 0) {
				sqlCols = sqlCols + "," + str;
			} else {
				sqlCols = str;
			}
		}
		// iterate maps where
		for (Iterator iterator = sqlMap.get("WHERE").iterator(); iterator
				.hasNext();) {
			String str = (String) iterator.next();
			if (sqlWhere.length() > 0) {
				sqlWhere = sqlWhere + " AND " + str;
			} else {
				sqlWhere = "Where " + str;
			}
		}
		// iterate maps group
		for (Iterator iterator = sqlMap.get("GROUP").iterator(); iterator
				.hasNext();) {
			String str = (String) iterator.next();
			if (sqlGroupby.length() > 0) {
				sqlGroupby = sqlGroupby + " , " + str;
			} else {
				sqlGroupby = "Group By " + str;
			}
		}
		for (int i = 0; i < order_by.length; i++) {
			String str = order_by[i];
			if (str.length() > 0) {
				if (sqlOrdby.length() > 0) {
					sqlOrdby = sqlOrdby + " , " + str;
				} else {
					sqlOrdby = "Order By " + str;
				}
			}
		}
		ResultSet rs_1 = ps_1.executeQuery();
		rs_1.first();

		page_height = rs_1.getInt("page_height");
		page_width = rs_1.getInt("page_width");
		exeBefore = rs_1.getString("exec_before");
		exeAfter = rs_1.getString("exec_after");
		rep_des_file = rs_1.getString("rep_des_file");
		title2 = rs_1.getString("TITLEARB");
		// sqlwhere to add with additional where
		sqlWhere = sqlWhere + " " + utils.nvl(rs_1.getString("REPORTNAME"), "");
		if ((rs_1.getString("HASSUBTOT") != null && rs_1.getString("HASSUBTOT")
				.length() > 0)
				&& sqlMap.get("SUBGROUP").size() == 0) {
			sqlMap.get("SUBGROUP").add(listcols.get(0).colname);
		}
		sqlView = rs_1.getString("rep_qryname");
		ps_1.close();
		sqlstr = "select " + sqlCols + " from " + sqlView + " " + sqlWhere
				+ " " + sqlGroupby + " " + sqlOrdby;
		listParams.clear();
		PreparedStatement ps_3 = con.prepareStatement(
				"select *from invqrycolspara where code='" + idno
						+ "' order by inddexno",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs_3 = ps_3.executeQuery();
		rs_3.beforeFirst();
		Calendar cln = Calendar.getInstance();
		while (rs_3.next()) {

			cln.setTimeInMillis(System.currentTimeMillis());
			cln.set(Calendar.HOUR, 0);
			cln.set(Calendar.MINUTE, 0);
			cln.set(Calendar.SECOND, 0);
			cln.set(Calendar.MILLISECOND, 0);

			Parameter par = new Parameter(rs_3.getString("PARAM_NAME"));
			par.setDescription(rs_3.getString("PARA_DESCRARB"));
			par.setValueType(Parameter.DATA_TYPE_STRING);
			if (rs_3.getString("para_datatype").equals("DATE")) {
				par.setValueType(Parameter.DATA_TYPE_DATE);
			}
			if (rs_3.getString("para_datatype").equals("NUMBER")) {
				par.setValueType(Parameter.DATA_TYPE_NUMBER);
			}
			if (rs_3.getString("para_datatype").equals("VARCHAR2")) {
				par.setValueType(Parameter.DATA_TYPE_STRING);
			}
			if (rs_3.getString("PARA_DEFAULT").equals("$FIRSTDATEOFYEAR")) {
				cln.set(Calendar.MONTH, 0);
				cln.set(Calendar.DAY_OF_MONTH, 1);
				par.setValue(new java.util.Date(cln.getTimeInMillis()));
			}
			if (rs_3.getString("PARA_DEFAULT").equals("$TODAY")) {
				par.setValue(new java.util.Date(cln.getTimeInMillis()));
			}
			if (rs_3.getString("PARA_DEFAULT").equals("$FIRSTDATEOFMONTH")) {
				cln.set(Calendar.DAY_OF_MONTH, 1);
				par.setValue(new java.util.Date(cln.getTimeInMillis()));
			}
			if (rs_3.getString("PARA_DEFAULT").equals("$DEFAULT_STORE")) {
				par.setValue((String) Channelplus3Application.getInstance()
						.getFrmUserLogin().getMapVars().get("DEFAULT_STORE"));
			}
			if (rs_3.getString("PARA_DEFAULT").equals("$DEFAULT_LOCATION")) {
				par
						.setValue((String) Channelplus3Application.getInstance()
								.getFrmUserLogin().getMapVars().get(
										"DEFAULT_LOCATION"));
			}

			listParams.add(par);
		}
	}

	public void createView(final AbstractLayout centralPanel)
			throws SQLException {
		qv = new QueryView();
		centralPanel.addComponent(qv);
		qv.setSizeFull();
		qv.createDBClassFromConnection(con);
		qv.setSqlquery(sqlstr);
		qv.reportSetting.doStandard();
		qv.reportSetting.setOrientation(DynamicReportSetting.PAGE_CUSTOM);
		qv.reportSetting.setPageHeight(page_height);
		qv.reportSetting.setPageWidth(page_width);
		qv.reportSetting.setTitle(title1 + "  " + title2);

		qv.getDataListners().add(new tableDataListner() {
			public String getCellStyle(qryColumn qc, int recordNo) {
				return null;
			}

			public String calcSummary(List<String> qcGroup, qryColumn qc) {
				return "";
			}

			public void afterQuery() {
				for (Iterator iterator = listcols.iterator(); iterator
						.hasNext();) {
					ColumnProperty cp = (ColumnProperty) iterator.next();

					if (cp.display_format != null
							&& cp.display_format
									.equals(ColumnProperty.SHORT_DATE_FORMAT)) {
						qv.getLctb().getColByName(cp.colname).setDateFormat(
								utils.FORMAT_SHORT_DATE);
					}
					if (cp.display_format != null
							&& cp.display_format
									.equals(ColumnProperty.MONEY_FORMAT)) {
						qv.getLctb().getColByName(cp.colname).setNumberFormat(
								Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
					}
					if (cp.display_format != null
							&& cp.display_format
									.equals(ColumnProperty.QTY_FORMAT)) {
						qv.getLctb().getColByName(cp.colname).setNumberFormat(
								utils.FORMAT_QTY);
					}
					qv.getLctb().getColByName(cp.colname).setTitle(cp.descr);

				}

			}

			public void afterVisual() {
				for (Iterator iterator = listcols.iterator(); iterator
						.hasNext();) {
					ColumnProperty cp = (ColumnProperty) iterator.next();
					qv.setColumnWidth(cp.colname, cp.display_width);
				}
				centralPanel.getWindow().showNotification(
						"Query Code: " + sqlId + " Tables :  " + sqlView
								+ " total rows shown :"
								+ qv.getTable().getItemIds().size());
			}

			public void beforeQuery() {
				qv.getTable().setHeight("100%");
			}

		});
		if (exeBefore != null && !exeBefore.isEmpty()) {
			qv.setBeforeQueryExecSql(exeBefore);
		}

		// qv.getListShowParams().addAll(listParams);
		for (Iterator iterator = listParams.iterator(); iterator.hasNext();) {
			Parameter p = (Parameter) iterator.next();
			qv.addParameter(p, true);
		}
		if (sqlMap.get("SUBGROUP").size() > 0) {
			qv.setShowGroupHead(true);
			qv.getListGroupsBy().addAll(sqlMap.get("SUBGROUP"));
		}
		qv.getListHideCols().clear();
		if (sqlMap.get("SUBGROUP").size() > 0) {
			qv.getListHideCols().addAll(sqlMap.get("HIDECOL"));
		}
		qv.getListGroupSum().clear();
		qv.getListGroupSum().addAll(sqlMap.get("SUMMARY_COLS"));
		qv.createView(fetchData);
	}

	public void createView(AbstractLayout centralPanel, String code)
			throws Exception {
		buildSql(code);
		createView(centralPanel);
	}
}