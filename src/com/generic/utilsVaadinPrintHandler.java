package com.generic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperRunManager;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;

import com.doc.views.Query.QueryView;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

public class utilsVaadinPrintHandler {

	public static void printGatepass(int keyfld, Connection con)
			throws Exception {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		PreparedStatement psg = con
				.prepareStatement(
						"select cp_gatepass1.*,locations.name location_name,s.name sales_name "
								+ "from cp_gatepass1,locations,salesp s where cp_gatepass1.keyfld="
								+ keyfld
								+ " and cp_gatepass1.location_code=locations.code and s.no(+)=locations.salesp",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
		ResultSet rsg = psg.executeQuery();
		rsg.first();
		mapPara.put("DESCR", rsg.getString("DESCR"));
		mapPara.put("LOCATION_CODE", rsg.getString("LOCATION_CODE"));
		mapPara.put("LOCATION_NAME", rsg.getString("LOCATION_NAME"));
		mapPara.put("SALES_NAME", rsg.getString("SALES_NAME"));
		mapPara.put("TRANS_DATE", rsg.getTimestamp("TRANS_DATE"));
		mapPara.put("COMPANY_NAME", utils.COMPANY_NAME);
		mapPara.put("COMPANY_SPECS", utils.COMPANY_SPECS);
		mapPara.put("KEYFLD", keyfld);
		utilsVaadin.showReport("/reports/rptVouGatepass.jasper", mapPara, con,
				Channelplus3Application.getInstance());

	}

	public static void printLGJob(Connection con, String pno)
			throws SQLException {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		ResultSet rs = utils.getSqlRS(
				"select ord_type,lcno from order1 where ord_code=106 and ord_no="
						+ pno, con);

		if (rs == null || !rs.first())
			return;
		rs.first();
		String typ = rs.getString("ord_type");

		String cmp = utils.nvl(rs.getString("lcno"), utils.getSqlValue(
				"select code from company order by crnt desc", con));
		rs.close();
		getCompanyPara(con, mapPara, cmp);
		mapPara.put("PNO", pno);
		mapPara.put("OTYPE", typ);
		utilsVaadin.showReport("/reports/rptLGJob.jasper", mapPara, con,
				Channelplus3Application.getInstance());

	}

	public static void getCompanyPara(Connection con,
			Map<String, Object> mapPara, String cmp2) throws SQLException {

		String cmp = utils.nvl(cmp2, utils.getSqlValue(
				"select code from company order by crnt desc", con));
		ResultSet rs = utils.getSqlRS("select *from company where code='" + cmp
				+ "'", con);
		mapPara.put("COMPANY_NAME", rs.getString("NAME"));
		mapPara.put("COMPANY_SPECS", rs.getString("SPECIFICATION"));
		mapPara.put("COMPANY_NAMEA", rs.getString("NAMEA"));
		mapPara.put("COMPANY_SPECSA", rs.getString("SPECIFICATIONA"));
		String cmpf = rs.getString("filename");
		if (cmpf.indexOf(".") < 0) {
			cmpf = cmpf + ".png";
		}
		mapPara.put("COMPANY_LOGO", cmpf);

		rs.close();
	}

	public static void printAccVoucher(int keyfld, Connection con)
			throws SQLException {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		ResultSet rsx = utils
				.getSqlRS(
						"select acgrpjvs.name,acgrpjvs.namea,acgrpjvs.vou_type,acgrpjvs.vou_code,f_words(acvoucher1.dEBAMT) f_word from acvoucher1,acgrpjvs  "
								+ "where acgrpjvs.vou_code=acvoucher1.vou_code and acgrpjvs.vou_type=acvoucher1.type and acvoucher1.keyfld="
								+ keyfld, con);
		String typ = rsx.getString("VOU_TYPE");
		mapPara.put("COMPANY_NAME", utils.COMPANY_NAME);
		mapPara.put("COMPANY_SPECS", utils.COMPANY_SPECS);
		mapPara.put("COMPANY_NAMEA", utils.COMPANY_NAMEA);
		mapPara.put("COMPANY_SPECSA", utils.COMPANY_SPECSA);

		mapPara.put("VOU_TITLE", rsx.getString("NAME"));
		mapPara.put("VOU_TITLEA", rsx.getString("NAMEA"));
		mapPara.put("TOTAL_IN_WORDS", rsx.getString("f_word"));
		mapPara.put("DETAIL_REP", "rptVou_" + typ + "_details.jasper");
		mapPara.put("KEYFLD", keyfld);
		rsx.close();
		utilsVaadin.showReport("/reports/rptVou_" + typ + ".jasper", mapPara,
				con, Channelplus3Application.getInstance());

	}

	public static void printPurchase(int keyfld, Connection con)
			throws Exception {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		mapPara.put("COMPANY_NAME", utils.COMPANY_NAME);
		mapPara.put("COMPANY_SPECS", utils.COMPANY_SPECS);
		mapPara.put("INVOICE_KEYFLD", BigDecimal.valueOf(keyfld));
		mapPara.put("CURRENCY_FORMAT", Channelplus3Application.getInstance()
				.getFrmUserLogin().FORMAT_MONEY);
		utilsVaadin.showReport("/reports/rptVouPurCost.jasper", mapPara, con,
				Channelplus3Application.getInstance());
	}

	public static void printSO(double on, String templFile, Connection con)
			throws Exception {
		Map<String, Object> mapPara = new HashMap<String, Object>();		
		String cmp = utils
				.getSqlValue(
						"select lcno from order1 where ord_code=106 and "
								+ " ord_no = (select ord_reference from order1 where ord_code=111 and ord_no="
								+ on + ")", con);

		getCompanyPara(con, mapPara, cmp);
		mapPara.put("P_ORD", on + "");
		String fmt = QueryExe.getSqlValue("select max(FORMAT_J2EE) from "
				+ " currency c,order1 o where ord_code=111 and ord_no=" + on
				+ " and c.code=o.ord_fc_descr", con, "")
				+ "";		
		if (fmt == null || fmt.isEmpty())
			mapPara.put("CURRENCY_FORMAT", Channelplus3Application
					.getInstance().getFrmUserLogin().FORMAT_MONEY);
		else
			mapPara.put("CURRENCY_FORMAT", fmt);
		
		utilsVaadin.showReport("/reports/" + templFile + ".jasper", mapPara,
				con, Channelplus3Application.getInstance());
		
	}

	public static void printLGFRPayment(double kf, Connection con)
			throws Exception {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		getCompanyPara(con, mapPara, null);
		mapPara.put("PKEYFLD", kf + "");
		mapPara.put("CURRENCY_FORMAT", Channelplus3Application.getInstance()
				.getFrmUserLogin().FORMAT_MONEY);
		utilsVaadin.showReport("/reports/rptFRPayment.jasper", mapPara, con,
				Channelplus3Application.getInstance());
	}

	public static void printWorkshop(int on, String templFile, Connection con)
			throws Exception {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		getCompanyPara(con, mapPara, null);
		mapPara.put("PORD_NO", Integer.valueOf(on));
		utilsVaadin.showReport("/reports/" + templFile + ".jasper", mapPara,
				con, Channelplus3Application.getInstance());
	}

	public static void printClqVoucher(String keyfld, Connection con)
			throws Exception {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		mapPara.put("COMPANY_NAME", utils.COMPANY_NAME);
		mapPara.put("COMPANY_SPECS", utils.COMPANY_SPECS);
		mapPara.put("INVOICE_KEYFLD", keyfld);
		utilsVaadin.showReport("/reports/rptClqVoucher.jasper", mapPara, con,
				Channelplus3Application.getInstance());
	}

	public static void printClqOrder(String on, Connection con)
			throws Exception {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		mapPara.put("COMPANY_NAME", utils.COMPANY_NAME);
		mapPara.put("COMPANY_SPECS", utils.COMPANY_SPECS);
		mapPara.put("ORDER_NO", on);
		ResultSet rst = utils
				.getSqlRS(
						"select order1.*,locations.name location_name ,SALESP.NAME DR_NAME "
								+ "from order1,locations,salesp "
								+ " where ord_code=111 and ord_no="
								+ on
								+ " and location_code=locations.code AND salesp.no=ord_empno",
						con);
		if (rst != null && rst.first()) {
			mapPara.put("LOCATION_NAME", rst.getString("LOCATION_NAME"));
			mapPara.put("ORD_DATE", rst.getTimestamp("ord_date"));
			mapPara.put("ORD_REF", rst.getString("ORD_REF"));
			mapPara.put("ORD_REFNM", rst.getString("ORD_REFNM"));
			mapPara.put("DR_NAME", rst.getString("DR_NAME"));
			utilsVaadin.showReport("/reports/rptClqMedicalInvoice.jasper",
					mapPara, con, Channelplus3Application.getInstance());
			rst.close();
		}
	}

	public static void printSRV(int keyfld, double ordno, Connection con)
			throws Exception {
		PreparedStatement ps_order = con.prepareStatement(
				"select *from order1 where ord_code=103 and ord_no=" + ordno,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs_ord = ps_order.executeQuery();
		PreparedStatement ps_inv = con.prepareStatement(
				"select *from invoice1 where keyfld=" + keyfld,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs_inv = ps_inv.executeQuery();

		if (!rs_ord.first()) {
			throw new Exception("No Order Found");
		}
		if (!rs_inv.first()) {
			throw new Exception("No SRV Found");
		}

		Map<String, Object> mapPara = new HashMap<String, Object>();
		String locName = utils.getSqlValue(
				"select name from locations where code='"
						+ rs_ord.getString("LOCATION_CODE") + "'", con);
		SimpleDateFormat sdf = new SimpleDateFormat(utils.FORMAT_SHORT_DATE);
		java.util.Date ordt = new java.util.Date(rs_ord.getDate("ORD_DATE")
				.getTime());
		java.util.Date invdt = new java.util.Date(rs_inv
				.getDate("INVOICE_DATE").getTime());
		mapPara.put("LOCATION_NAME", locName);
		mapPara.put("ORD_DATE", sdf.format(ordt));
		mapPara.put("INVOICE_DATE", sdf.format(invdt));
		mapPara.put("ORDER_ACCOUNT", utils.getSqlValue(
				"select name||' - '||accno from acaccount where accno='"
						+ rs_ord.getString("ORDACC") + "'", con));
		mapPara.put("ORDER_NO", Double.valueOf(ordno));
		mapPara.put("SRV_NO", Double.valueOf(keyfld));
		mapPara.put(
				"STORE",
				utils.getSqlValue(
						"select name from store where no="
								+ rs_inv.getString("STRA"), con));
		mapPara.put("COMPANY_NAME", utils.COMPANY_NAME);
		mapPara.put("COMPANY_SPECS", utils.COMPANY_SPECS);
		mapPara.put("ORD_REFNAME", rs_ord.getString("ORD_REFNM") + "- "
				+ rs_ord.getString("ORD_REF"));
		mapPara.put("INVOICE_KEYFLD", keyfld);
		mapPara.put("IMAGE_PATH", "");

		utilsVaadin.showReport("/reports/rptVouSRV.jasper", mapPara, con,
				Channelplus3Application.getInstance());
	}

	public static DynamicReportBuilder build_report(final localTableModel data,
			final List<ColumnProperty> lstcols,
			final List<Parameter> listShowParams,
			final List<String> listGroupSum, final List<String> listGroupsBy,
			Connection con, final Map<String, Parameter> mapParameters)
			throws Exception {

		final DynamicReportSetting reportSetting = new DynamicReportSetting();
		reportSetting.doStandard();
		reportSetting.setOrientation(DynamicReportSetting.LANDSCAPE);

		for (Iterator iterator = listShowParams.iterator(); iterator.hasNext();) {
			Parameter p = (Parameter) iterator.next();
			mapParameters.put(p.getName(), p);
		}

		for (Iterator iterator = lstcols.iterator(); iterator.hasNext();) {
			ColumnProperty cp = (ColumnProperty) iterator.next();
			data.getColByName(cp.colname).setWidth(cp.display_width);
		}

		// --------STYLES
		DynamicReportBuilder drb = new DynamicReportBuilder();
		Map<String, AbstractColumn> mapCols = new HashMap<String, AbstractColumn>();

		for (int i = 0; i < lstcols.size(); i++) {
			String title = lstcols.get(i).descr;
			String name = lstcols.get(i).colname;
			Integer width = lstcols.get(i).display_width;
			if (width < 0) {
				width = 0;
			}
			AbstractColumn col1 = ColumnBuilder.getNew()
					.setColumnProperty(name, String.class.getName())
					.setTitle(title).setWidth(width).setFixedWidth(true)
					.build();
			if (data.getColByName(lstcols.get(i).colname).isNumber()) {
				col1 = ColumnBuilder.getNew()
						.setColumnProperty(name, BigDecimal.class.getName())
						.setTitle(title).build();
				if (data.getColByName(lstcols.get(i).colname).getNumberFormat()
						.length() > 0) {
					col1.setPattern(data.getColByName(lstcols.get(i).colname)
							.getNumberFormat());
					col1.setStyle(reportSetting.getStyleNumber());
				}
			}
			if (data.getColByName(lstcols.get(i).colname).isDateTime()) {
				col1 = ColumnBuilder
						.getNew()
						.setColumnProperty(name,
								java.sql.Timestamp.class.getName())
						.setTitle(title).build();
				col1.setPattern(utils.nvl(
						data.getColByName(lstcols.get(i).colname)
								.getDateFormat(), utils.FORMAT_SHORT_DATE));
			}
			drb.addColumn(col1);
			if (data.getColByName(lstcols.get(i).colname).isNumber()
					&& listGroupSum.contains(data.getColByName(
							lstcols.get(i).colname).getColname())) {
				drb.addGlobalFooterVariable(col1, DJCalculation.SUM,
						reportSetting.getStyleNumber2());
				drb.setGrandTotalLegend("Grand Total");
				drb.setGrandTotalLegendStyle(reportSetting.getStyleDetail());
			}
			mapCols.put(name, col1);
		}
		reportSetting.getListParams().clear();
		reportSetting.getListParams().addAll(listShowParams);

		reportSetting.wrapReport(drb, true);
		// GROUPING
		int cnt = 0;
		for (Iterator iterator = listGroupsBy.iterator(); iterator.hasNext();) {
			String fldname = (String) iterator.next();
			AbstractColumn col = mapCols.get(fldname);
			if (col == null) {
				mapCols.put(
						fldname,
						ColumnBuilder
								.getNew()
								.setColumnProperty(fldname,
										String.class.getName()).setTitle("")
								.build());
				col = mapCols.get(fldname);
				drb.addColumn(mapCols.get(fldname));
			}
			col.setStyle(reportSetting.getStyleGroup());
			GroupBuilder grp = new GroupBuilder();
			grp.setCriteriaColumn((PropertyColumn) col);
			grp.setHeaderHeight(new Integer(30));
			if (cnt == 0) {
				for (int k = 0; k < listGroupSum.size(); k++) {
					AbstractColumn numcol = mapCols.get(listGroupSum.get(k));
					if (data.getColByName(listGroupSum.get(k)).isNumber()) {
						grp.addFooterVariable(numcol, DJCalculation.SUM,
								reportSetting.getStyleNumber2());
					}
				}
			}
			grp.setGroupLayout(GroupLayout.VALUE_IN_HEADER);
			DJGroup g = grp.build();
			g.setDefaulHeaderVariableStyle(reportSetting.getStyleDetail());
			drb.addGroup(g);
			cnt++;
		}
		drb.setAllowDetailSplit(false);
		drb.setUseFullPageWidth(true);

		drb.setPrintColumnNames(true);

		return drb;
	}

	public static void print_voucher(final localTableModel data,
			final List<ColumnProperty> lstcols,
			final List<Parameter> listShowParams,
			final List<String> listGroupSum, final List<String> listGroupsBy,
			Connection con, final String filename) throws Exception {

		final Map<String, Parameter> mapParameters = new HashMap<String, Parameter>();
		String fl1 = ((WebApplicationContext) Channelplus3Application
				.getInstance().getContext()).getHttpSession()
				.getServletContext().getRealPath("/WEB-INF")
				+ "/" + utils.nvl(filename, "tmp");
		final String fl = fl1.replace("\\", "/");

		Map<String, String> parameter = new HashMap<String, String>();
		byte b[] = null;
		final JRDataSource jr = new JRDataSource() {
			private int irow = 0;

			public boolean next() throws JRException {
				irow++;
				if (irow > data.getRows().size()) {
					return false;
				}
				return true;
			}

			public Object getFieldValue(JRField j) throws JRException {
				Object val = data.getFieldValue(irow - 1, j.getName());
				if (val != null && val.toString().length() == 0) {
					val = null;
				}
				return val;
			}
		};

		DynamicReportBuilder drb = build_report(data, lstcols, listShowParams,
				listGroupSum, listGroupsBy, con, mapParameters);

		DynamicReport dr = drb.build();
		DynamicJasperHelper.generateJRXML(dr, new ClassicLayoutManager(),
				parameter, "UTF-8", fl + ".jrxml");

		JasperCompileManager.compileReportToFile(fl + ".jrxml", fl + ".jasper");

		StreamResource.StreamSource source = new StreamResource.StreamSource() {
			public InputStream getStream() {
				byte[] b = null;
				try {
					Map<String, Object> mpx = new HashMap<String, Object>();
					b = JasperRunManager
							.runReportToPdf(fl + ".jasper", mpx, jr);
				} catch (Exception ex) {
					Logger.getLogger(QueryView.class.getName()).log(
							Level.SEVERE, null, ex);
				}
				return new ByteArrayInputStream(b);
			}
		};
		String pdffile = fl + System.currentTimeMillis() + ".pdf";
		StreamResource resource = new StreamResource(source, pdffile,
				Channelplus3Application.getInstance());
		resource.setCacheTime(1);
		resource.setMIMEType("application/pdf");
		Channelplus3Application.getInstance().getMainWindow()
				.open(resource, "_blank");

	}

	public static void print_patient(String medical_no, String subrep,
			Connection con) {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		mapPara.put("COMPANY_NAME", utils.COMPANY_NAME);
		mapPara.put("COMPANY_SPECS", utils.COMPANY_SPECS);
		mapPara.put("MEDICAL_NO", medical_no);
		mapPara.put("SUBREPORT_DIR", subrep);

		utilsVaadin.showReport("/reports/rep_medicalreport.jasper", mapPara,
				con, Channelplus3Application.getInstance());

	}
}
