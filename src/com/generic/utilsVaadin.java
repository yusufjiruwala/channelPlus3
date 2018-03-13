package com.generic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.doc.views.Query.QueryView;
import com.example.components.SearchField;
import com.example.components.SearchField.ButtonPress;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.windows.frmMainMenus;

public class utilsVaadin {
	public static class xx {
		Map<String, String> mp = new HashMap<String, String>();

		public xx() {
		}

		public Map<String, String> getMp() {
			return mp;
		}

		public String getMp(String x) {
			return mp.get(x);
		}
	}

	private static Window wndlist = null;
	private static int DIALOG_YESNO = 1;
	private static int DIALOG_YESNOCANCEL = 2;
	private static int DIALOG_OKCANCEL = 3;
	private static int DIALOG_BUTTON_YES = 1;
	private static int DIALOG_BUTTON_NO = 2;

	public interface FilterListner {
		public void doFilter();

		public void cancelFilter();
	};

	// public static Channelplus3Application app = null;

	public static void FillCombo(ComboBox c, String sqlString, Connection con)
			throws SQLException {
		c.removeAllItems();
		ResultSet rs = utils.getSqlRS(sqlString, con);
		if (rs == null) {
			return;
		}
		rs.beforeFirst();
		while (rs.next()) {
			c.addItem(new dataCell(rs.getString(2), rs.getString(1)));
		}
		if (rs != null) {
			rs.getStatement().close();
		}
	}

	public static void fillParmeterList(int rn, localTableModel data,
			List<Parameter> pms) {

		if (rn < 0) {
			return;
		}

		for (int i = 0; i < data.getQrycols().size(); i++) {
			String colname = data.getQrycols().get(i).getColname()
					.toUpperCase();
			Parameter pm = new Parameter(colname, data.getFieldValue(rn,
					colname));
			if (data.getQrycols().get(i).getDatatype() == Types.DATE) {

			}
			pms.add(pm);

		}
	}

	public static String generateFieldClause(QueryView qv) {
		if (qv == null)
			return "";
		String s = "";
		String fld = " upper(query_field_in) like ";
		for (int i = 0; i < qv.getLctb().getQrycols().size(); i++) {
			String fnm = utils.nvl(
					qv.getLctb().getQrycols().get(i).getColname(), "")
					.toUpperCase();
			if (i > 0) {
				s = s + " or " + fld + " '%\"'||'" + fnm + "'||'\"%' ";
			} else {
				s = fld + " '%\"'||'" + fnm + "'||'\"%' ";
			}
		}
		return s;
	}

	public static void FillComboFromList(ComboBox c, List<dataCell> lst) {

		c.removeAllItems();
		if (lst == null) {
			return;
		}
		for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
			dataCell dc = (dataCell) iterator.next();
			c.addItem(dc);
		}
	}

	public static void FillComboStrings(ComboBox c, String sqlString,
			Connection con) throws SQLException {
		c.removeAllItems();
		ResultSet rs = utils.getSqlRS(sqlString, con);
		if (rs == null) {
			return;
		}
		rs.beforeFirst();
		while (rs.next()) {
			c.addItem(rs.getString(1));
		}
		if (rs != null) {
			rs.getStatement().close();
		}
	}

	public static Map<String, String> getMapVars() {
		return Channelplus3Application.getInstance().getFrmUserLogin()
				.getMapVars();
	}

	public static boolean CanIAddNew(String str) {
		if (getMapVars().get("FORM_OPEN_" + str) == null
				|| getMapVars().get("FORM_OPEN_" + str).equals("FALSE")) {
			return false;
		}

		if (getMapVars().get("FORM_INSERT_" + str) == null
				|| getMapVars().get("FORM_INSERT_" + str).equals("FALSE")) {
			return false;
		}

		return true;
	}

	public static boolean CanIOpen(String str) {
		if (getMapVars().get("FORM_OPEN_" + str) == null
				|| getMapVars().get("FORM_OPEN_" + str).equals("FALSE")) {
			return false;
		}
		return true;
	}

	public static boolean CanIUpdate(String str) {
		if (getMapVars().get("FORM_OPEN_" + str) == null
				|| getMapVars().get("FORM_OPEN_" + str).equals("FALSE")) {
			return false;
		}

		if (getMapVars().get("FORM_UPDATE_" + str) == null
				|| getMapVars().get("FORM_UPDATE_" + str).equals("FALSE")) {
			return false;
		}

		return true;
	}

	public static boolean CanIDelete(String str) {
		if (getMapVars().get("FORM_OPEN_" + str) == null
				|| getMapVars().get("FORM_OPEN_" + str).equals("FALSE")) {
			return false;
		}
		if (getMapVars().get("FORM_DELETE_" + str) == null
				|| getMapVars().get("FORM_DELETE_" + str).equals("FALSE")) {
			return false;
		}

		return true;
	}

	public static dataCell findByValue(ComboBox c, String val) {
		Object d[] = c.getItemIds().toArray();
		for (int i = 0; i < d.length; i++) {
			dataCell dx = null;
			if (d[i] instanceof dataCell) {
				dx = (dataCell) d[i];
			}
			if (dx != null && dx.getValue() != null
					&& dx.getValue().toString().equals(val)
					&& !(dx.getValue() instanceof Number)) {
				return dx;
			}
			if (dx != null
					&& dx.getValue() != null
					&& (dx.getValue() instanceof Number)
					&& ((Number) dx.getValue()).doubleValue() == Double
							.valueOf(val)) {
				return dx;
			}

		}
		return null;

	}

	public static dataCell findByDisplay(ComboBox c, String dispval) {
		dataCell d[] = (dataCell[]) c.getItemIds().toArray();
		for (int i = 0; i < d.length; i++) {
			if (d[i] != null && d[i].getDisplay() != null
					&& d[i].getDisplay().toString().equals(dispval)) {
				return d[i];
			}
		}
		return null;

	}

	private static class IntegerStorage {
		int value = 0;

		public void setValue(int vl) {
			this.value = vl;
		}

		public int getValue() {
			return this.value;
		}
	};

	public static void showReport(final String filename, final Map map,
			final Connection con, final Application app) {
		showReport(filename, map, con, app, true);
	}

	public static void showReport(final String filename, final Map map,
			final Connection con, final Application app, boolean useTimestamp) {
		try {
			StreamResource.StreamSource source = new StreamResource.StreamSource() {
				public InputStream getStream() {
					byte[] b = null;
					String fl = ((WebApplicationContext) app.getContext())
							.getHttpSession().getServletContext()
							.getRealPath("/WEB-INF");
					fl = fl.replace("\\", "/");
					try {
						b = JasperRunManager.runReportToPdf(fl + filename, map,
								con);
					} catch (Exception ex) {
						Logger.getLogger(utilsVaadin.class.getName()).log(
								Level.SEVERE, null, ex);
						app.getMainWindow().showNotification(ex.getMessage(),
								fl, Notification.TYPE_ERROR_MESSAGE);
					}
					return new ByteArrayInputStream(b);
				}
			};

			String pdffile = filename.substring(0, filename.indexOf("."))
					+ ".pdf";
			if (useTimestamp) {
				pdffile = filename.substring(0, filename.indexOf("."))
						+ System.currentTimeMillis() + ".pdf";
			}

			StreamResource resource = new StreamResource(source, pdffile, app);
			resource.setCacheTime(1);
			resource.setMIMEType("application/pdf");
			Channelplus3Application.getInstance().getMainWindow()
					.open(resource, "_new");
			/*
			 * Window w = new Window("Token Form"); w.setSizeFull();
			 * app.getMainWindow().addWindow(w); Embedded e = new Embedded();
			 * e.setMimeType("application/pdf");
			 * e.setType(Embedded.TYPE_OBJECT); e.setSizeFull();
			 * e.setSource(resource); e.setParameter("Content-Disposition",
			 * "attachment; filename=" + resource.getFilename());
			 * w.addComponent(e); e.requestRepaint();
			 */
		} catch (Exception ex) {
			Logger.getLogger(utilsVaadin.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public static void showFilter(AbstractLayout layout,
			final List<Parameter> lst) {
		showFilter(layout, lst, null);
	}

	public static void pdfQuery(QueryView qv, Connection con) {
		String fl1 = ((WebApplicationContext) Channelplus3Application
				.getInstance().getContext()).getHttpSession()
				.getServletContext().getRealPath("/WEB-INF")
				+ "/tmp";
		final String fl = fl1.replace("\\", "/");
		Map<String, String> parameter = new HashMap<String, String>();
		// FastReportBuilder drb = new FastReportBuilder();

		try {
			DynamicReportBuilder drb = new DynamicReportBuilder();
			drb.setMargins(30, 20, 30, 15);
			AbstractColumn col1 = ColumnBuilder.getNew()
					.setColumnProperty("mp1", String.class.getName())
					.setTitle("mp1").build();
			drb.addColumn(col1);
			final List<xx> y = new ArrayList<xx>();
			xx X = new xx();
			X.mp.put("mp1", "q");
			y.add(X);
			X = new xx();
			X.mp.put("mp1", "s");
			y.add(X);
			final JRDataSource jr = new JRDataSource() {
				private int i = 0;

				public boolean next() throws JRException {
					i++;
					if (i >= y.size()) {
						return false;
					}
					return true;
				}

				public Object getFieldValue(JRField j) throws JRException {

					return y.get(i - 1).mp.get(j.getName());
				}
			};
			DynamicReport dr = drb.build();
			DynamicJasperHelper.generateJRXML(dr, new ClassicLayoutManager(),
					parameter, "UTF-8", fl + ".jrxml");
			JasperCompileManager.compileReportToFile(fl + ".jrxml", fl
					+ ".jasper");
			byte b[] = null;

			StreamResource.StreamSource source = new StreamResource.StreamSource() {
				public InputStream getStream() {
					byte[] b = null;
					try {
						Map<String, Object> mpx = new HashMap<String, Object>();
						b = JasperRunManager.runReportToPdf(fl + ".jasper",
								mpx, jr);
					} catch (Exception ex) {
						Logger.getLogger(utilsVaadin.class.getName()).log(
								Level.SEVERE, null, ex);
					}
					return new ByteArrayInputStream(b);
				}
			};

			String pdffile = fl + ".pdf";
			StreamResource resource = new StreamResource(source, pdffile,
					Channelplus3Application.getInstance());
			resource.setMIMEType("application/pdf");
			Channelplus3Application.getInstance().getMainWindow()
					.open(resource, "_new");

			/*
			 * DynamicReport dr = drb.addColumn("Reference", "reference",
			 * String.class.getName(), 30).setQuery(
			 * "select reference from items",
			 * DJConstants.QUERY_LANGUAGE_SQL).setTitle("Item List")
			 * .setSubtitle("This report was generated at " + new Date())
			 * .setPrintBackgroundOnOddRows(true)
			 * .setUseFullPageWidth(true).build();
			 * DynamicJasperHelper.generateJRXML(dr, new ClassicLayoutManager(),
			 * parameter, "UTF-8", fl + ".jrxml");
			 * JasperCompileManager.compileReportToFile(fl + ".jrxml", fl +
			 * ".jasper");
			 */
			// showReport("/tmp.jasper", parameter, con, app, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void showFilter(AbstractLayout layout,
			final List<Parameter> lst, final FilterListner listner) {
		Panel pnl = new Panel();
		layout.addComponent(pnl);
		pnl.setSizeFull();
		GridLayout grd = new GridLayout();
		grd.setColumns(1);
		grd.setSizeUndefined();
		pnl.setScrollable(true);
		pnl.addComponent(grd);
		AbstractField c = null;
		for (int i = 0; i < lst.size(); i++) {
			if (lst.get(i).getLovList().size() > 0) {
				c = new ComboBox(lst.get(i).getDescription());
				for (Iterator<dataCell> it = lst.get(i).getLovList().iterator(); it
						.hasNext();) {
					dataCell dc = it.next();
					((ComboBox) c).addItem(dc);
				}
			} else {
				if (lst.get(i).getValueType().equals(Parameter.DATA_TYPE_DATE)) {
					c = new DateField(lst.get(i).getDescription());
					((DateField) c).setDateFormat(utils.FORMAT_SHORT_DATE);
					((DateField) c).setResolution(DateField.RESOLUTION_DAY);
				} else {
					c = new TextField(lst.get(i).getDescription());
				}
			}
			lst.get(i).setUIObject(c);
			grd.addComponent(c);

			// Listener
			final Parameter pr = lst.get(i);
			final AbstractField cc = c;
			c.setImmediate(true);
			c.setWidth("100%");

			c.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					pr.setValue(null);
					if (cc instanceof TextField) {
						pr.setValue((String) cc.getValue());
					}
					if (cc.getValue() != null && cc instanceof ComboBox) {
						pr.setValue((String) ((dataCell) cc.getValue())
								.getValue());
					}
					if (cc.getValue() != null && cc instanceof DateField) {
						pr.setValue((java.util.Date) cc.getValue());
					}
				}
			});

		}
		HorizontalLayout hz = new HorizontalLayout();
		hz.setSizeUndefined();
		Button bc = new Button("Filter");
		Button bc1 = new Button("Cancel Filter");
		bc.setClickShortcut(KeyCode.ENTER);
		bc1.setClickShortcut(KeyCode.ESCAPE);
		pnl.addComponent(hz);
		hz.addComponent(bc);
		hz.addComponent(bc1);
		if (listner != null) {
			bc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					listner.doFilter();
				}
			});
			bc1.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					listner.cancelFilter();
				}
			});
		}

	}

	public static void applyColumns(String col_group, Table tbl,
			List<ColumnProperty> lstItemCols, Connection con)
			throws SQLException {
		lstItemCols.clear();
		PreparedStatement ps_ic = con
				.prepareStatement(
						"select *FROM CP_SETCOLS WHERE "
								+ " PROFILE=0 AND SETGRPCODE='"
								+ col_group
								+ "' AND DISPLAY_TYPE NOT IN ('INVISIBLE') order by POSITION",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
		ResultSet rs_ic = ps_ic.executeQuery();
		rs_ic.beforeFirst();
		boolean exist_lookup = utils.findFieldInRS(rs_ic, "LOOKUP_COLUMN");
		boolean exist_default_vl = utils.findFieldInRS(rs_ic, "DEFAULT_VALUE");
		boolean exist_returnValues = utils
				.findFieldInRS(rs_ic, "RETURN_VALUES");
		while (rs_ic.next()) {
			ColumnProperty cp = new ColumnProperty();
			lstItemCols.add(cp);
			Class cs = Label.class;
			if (rs_ic.getString("EDITOR_CLASS").equals("TEXTFIELD")) {
				cs = TextField.class;
			}
			if (rs_ic.getString("EDITOR_CLASS").equals("DATEFIELD")) {
				cs = DateField.class;
			}
			if (rs_ic.getString("EDITOR_CLASS").equals("COMBOBOX")) {
				cs = ComboBox.class;
			}

			if (rs_ic.getString("EDITOR_CLASS").equals("CHECKBOX")) {
				cs = CheckBox.class;
			}
			if (rs_ic.getString("EDITOR_CLASS").equals("SEARCHFIELD")) {
				cs = SearchField.class;
			}

			cp.col_class = cs;
			cp.colname = rs_ic.getString("item_name");
			cp.descr = rs_ic.getString("descr");
			cp.display_width = rs_ic.getInt("display_width");
			cp.display_type = rs_ic.getString("display_type");
			cp.pos = rs_ic.getInt("POSITION");
			cp.display_align = rs_ic.getString("ALIGN");
			cp.display_format = rs_ic.getString("use_format");
			cp.other_styles = utils.nvl(rs_ic.getString("other_styles"), "");

			if (cp.display_format.equals(ColumnProperty.SHORT_DATE_FORMAT)) {
				cp.display_format = utils.FORMAT_SHORT_DATE;
			}

			if (cp.display_format.equals(ColumnProperty.QTY_FORMAT)) {
				cp.display_format = utils.FORMAT_QTY;
			}

			if (cp.display_format.equals(ColumnProperty.MONEY_FORMAT)) {
				cp.display_format = Channelplus3Application.getInstance()
						.getFrmUserLogin().FORMAT_MONEY;
			}

			if (rs_ic.getString("lov_sql") != null
					&& !rs_ic.getString("lov_sql").isEmpty()) {
				cp.searchListSQL = rs_ic.getString("lov_sql");
				cp.lov_sql = new ArrayList<dataCell>();
				utils.FillLov(cp.lov_sql, rs_ic.getString("lov_sql"), con);
			}

			if (exist_lookup && rs_ic.getString("lookup_column") != null)
				cp.searchLookUpColumn = rs_ic.getString("lookup_column");

			if (exist_default_vl && rs_ic.getString("DEFAULT_VALUE") != null) {
				String df = rs_ic.getString("default_value");
				cp.defaultValue = df;
				if (df.startsWith("#DATE_")) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							utils.FORMAT_SHORT_DATE);
					try {
						cp.defaultValue = sdf.parse(df.replace("#DATE_", ""));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				if (df.startsWith("#NUMBER_")) {
					cp.defaultValue = new BigDecimal(df.replace("#NUMBER_", ""));
				}
			}

			if (exist_returnValues && rs_ic.getString("RETURN_VALUES") != null) {
				cp.mapReturns = new HashMap<String, String>();
				utils.readVarsFromString(cp.mapReturns,
						rs_ic.getString("RETURN_VALUES"), ",");
			}

			tbl.addContainerProperty(cp.descr, cs, null);
			tbl.setColumnWidth(cp.descr, cp.display_width);
		}
		ps_ic.close();
	}

	public static String system_status = "QUERY";

	public static Object add_record_table(final Integer i, final Table tbl,
			final localTableModel data, final List<ColumnProperty> lstItemCols) {
		// system_status = "QUERY";

		final Object ar[] = new Object[lstItemCols.size()];
		for (int j = 0; j < lstItemCols.size(); j++) {
			// asinging cell component
			if (lstItemCols.get(j).col_class == Label.class) {
				ar[j] = new Label();
			}
			if (lstItemCols.get(j).col_class == TextField.class) {
				ar[j] = new TextField();
			}
			if (lstItemCols.get(j).col_class == ComboBox.class) {
				ar[j] = new ComboBox();
				if (lstItemCols.get(j).lov_sql != null) {
					utilsVaadin.FillComboFromList((ComboBox) ar[j],
							lstItemCols.get(j).lov_sql);
				}
			}
			if (lstItemCols.get(j).col_class == CheckBox.class) {
				ar[j] = new CheckBox();
			}
			if (lstItemCols.get(j).col_class == SearchField.class) {
				ar[j] = new SearchField();
				final SearchField txt = (SearchField) ar[j];
				final int jj = j;
				((SearchField) ar[j]).setShowDisplayWithValue(true);
				if (lstItemCols.get(jj).searchLookUpColumn.isEmpty())
					txt.setShowValueOnly(true);

				((SearchField) ar[j]).setButtonPress(new ButtonPress() {
					public void onButton(String button) {
						if (lstItemCols.get(jj).searchListSQL.isEmpty())
							return;
						if (button.equals("CLEAR")
								&& !lstItemCols.get(jj).searchLookUpColumn
										.isEmpty()) {
							data.setFieldValue(i,
									lstItemCols.get(jj).searchLookUpColumn, "");
						}
						if (button.equals("SEARCH")) {
							final Window wnd = new Window();
							final VerticalLayout la = new VerticalLayout();
							wnd.setContent(la);
							try {
								utilsVaadin.showSearch(
										la,
										new SelectionListener() {
											public void onSelection(TableView tv) {
												Channelplus3Application
														.getInstance()
														.getMainWindow()
														.removeWindow(wnd);

												if (tv.getSelectionValue() > -1) {
													try {
														String codeField = tv
																.getData()
																.getQrycols()
																.get(0)
																.getColname();
														String nameField = tv
																.getData()
																.getQrycols()
																.get(1)
																.getColname();
														int rn = tv
																.getSelectionValue();
														txt.setDisplayValue(
																tv.getData()
																		.getFieldValue(
																				rn,
																				codeField)
																		.toString(),
																tv.getData()
																		.getFieldValue(
																				rn,
																				nameField)
																		.toString());

														if (!lstItemCols
																.get(jj).searchLookUpColumn
																.isEmpty())
															data.setFieldValue(
																	i,
																	nameField,
																	tv.getData()
																			.getFieldValue(
																					rn,
																					nameField)
																			.toString());
														utils.do_assign_return_values(
																lstItemCols
																		.get(jj),
																data,
																tv.getData(),
																tv.getSelectionValue(),
																i);
														refreshRowFromData(
																data, tbl, i,
																lstItemCols);
													} catch (Exception ex) {
														ex.printStackTrace();
													}
												}
											}

										}, data.getDbclass().getDbConnection(),
										lstItemCols.get(jj).searchListSQL, true);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}

					}
				});

			}

			if (lstItemCols.get(j).col_class == DateField.class) {
				if (data.getFieldValue(i, lstItemCols.get(j).colname) != null
						&& !data.getFieldValue(i, lstItemCols.get(j).colname)
								.toString().isEmpty()) {
					ar[j] = new DateField("", new java.util.Date(
							((java.util.Date) data.getFieldValue(i,
									lstItemCols.get(j).colname)).getTime()));
				} else {
					ar[j] = new DateField("");
				}
				((DateField) ar[j]).setResolution(DateField.RESOLUTION_DAY);
				if (lstItemCols.get(j).display_format
						.equals("SHORT_DATE_FORMAT")) {
					((DateField) ar[j]).setDateFormat(utils.FORMAT_SHORT_DATE);
				} else {
					((DateField) ar[j])
							.setDateFormat(lstItemCols.get(j).display_format);
				}
			}

			((AbstractComponent) ar[j]).setWidth("100%");

			// alignments of cell component
			((AbstractComponent) ar[j]).addStyleName("leftalign");
			if (lstItemCols.get(j).display_align
					.equals(ColumnProperty.ALIGN_CENTER)) {
				((AbstractComponent) ar[j]).addStyleName("centeralign");
			}
			if (lstItemCols.get(j).display_align
					.equals(ColumnProperty.ALIGN_RIGHT)) {
				((AbstractComponent) ar[j]).addStyleName("rightalign");
			}
			// if not data is Date then..
			if (data.getFieldValue(i, lstItemCols.get(j).colname) != null) {
				if (!data.getColByName(lstItemCols.get(j).colname).isDateTime()
						&& !(ar[j] instanceof CheckBox)) {
					// setting string value.
					if (ar[j] instanceof ComboBox) {
						((Property) ar[j])
								.setValue(utilsVaadin.findByValue(
										(ComboBox) ar[j],
										data.getFieldValue(i,
												lstItemCols.get(j).colname)
												.toString()));
					} else {

						if (!lstItemCols.get(j).searchLookUpColumn.isEmpty()
								&& ((Property) ar[j]) instanceof SearchField)
							((SearchField) (Property) ar[j])
									.setDisplayValue(
											data.getFieldValue(i,
													lstItemCols.get(j).colname)
													.toString(),
											utils.nvl(
													data.getFieldValue(
															i,
															lstItemCols.get(j).searchLookUpColumn),
													""));
						else
							((Property) ar[j]).setValue(data.getFieldValue(i,
									lstItemCols.get(j).colname).toString());

					}
				}

				// if there is money format then
				if (!lstItemCols.get(j).display_format.equals("NONE")
						&& !data.getColByName(lstItemCols.get(j).colname)
								.isDateTime() && !(ar[j] instanceof CheckBox)) {
					Number n = (Number) (Double.valueOf(utils.nvl(
							data.getFieldValue(i, lstItemCols.get(j).colname),
							BigDecimal.valueOf(0))).doubleValue());
					if (n != null) {
						((Property) ar[j]).setValue((new DecimalFormat(
								lstItemCols.get(j).display_format)).format(n
								.doubleValue()));
					}
				}

				if (!lstItemCols.get(j).display_format.equals("NONE")
						&& data.getColByName(lstItemCols.get(j).colname)
								.isDateTime()
						&& lstItemCols.get(j).col_class != DateField.class
						&& !(ar[j] instanceof CheckBox)) {
					Date n = (Date) data.getFieldValue(i,
							lstItemCols.get(j).colname);
					if (n != null) {
						((Property) ar[j]).setValue((new SimpleDateFormat(
								lstItemCols.get(j).display_format)).format(n));
					}
				}

				// if checkbox and string
				if (lstItemCols.get(j).col_class == CheckBox.class
						&& !data.getColByName(lstItemCols.get(j).colname)
								.isNumber()) {
					((Property) ar[j]).setValue(false);
					if (data.getFieldValue(i, lstItemCols.get(j).colname)
							.equals(lstItemCols.get(j).onCheckValue)) {
						((Property) ar[j]).setValue(true);

					}

				}

				// if checkbox and number
				if (lstItemCols.get(j).col_class == CheckBox.class
						&& data.getColByName(lstItemCols.get(j).colname)
								.isNumber()) {
					((Property) ar[j]).setValue(false);
					if ((Double.valueOf(
							data.getFieldValue(i, lstItemCols.get(j).colname)
									.toString()).doubleValue() == Double
							.valueOf(lstItemCols.get(j).onCheckValue)
							.doubleValue())) {
						((Property) ar[j]).setValue(true);
					}
				}

				((AbstractComponent) ar[j])
						.addStyleName(lstItemCols.get(j).other_styles);
			}

			if (ar[j] instanceof AbstractField) {
				final localTableModel datax = data;
				final int rowno = i;
				final int colno = j;
				final String colname = lstItemCols.get(j).colname;
				((AbstractField) ar[j]).setReadOnly(false);
				if (lstItemCols.get(j).display_type.equals("DISABLED")) {
					((AbstractField) ar[j]).setReadOnly(true);
				}
				((AbstractField) ar[j]).setImmediate(true);
				if (ar[j] instanceof TextField) {
					((TextField) ar[j]).addListener(new FocusListener() {

						public void focus(FocusEvent event) {
							((TextField) event.getComponent()).selectAll();
						}
					});
				}
				((AbstractField) ar[colno]).setData(true);

				((AbstractField) ar[j]).addListener(new ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						Object vl = null;
						if (((AbstractField) ar[colno]).getValue() != null) {
							if (((AbstractField) ar[colno]).getValue() instanceof dataCell) {
								vl = ((dataCell) ((AbstractField) ar[colno])
										.getValue()).getValue().toString();
							} else {
								vl = ((AbstractField) ar[colno]).getValue()
										.toString();
							}
						}

						// if number column then....
						if (datax.getColByName(colname).isNumber()) {
							DecimalFormat dc = new DecimalFormat(lstItemCols
									.get(colno).display_format);
							if ((((AbstractField) ar[colno]).getValue() != null || !((AbstractField) ar[colno])
									.getValue().toString().isEmpty())) {
								try {
									if (!lstItemCols.get(colno).display_format
											.equals("NONE")) {
										vl = BigDecimal.valueOf(dc.parse(
												((AbstractField) ar[colno])
														.getValue().toString())
												.doubleValue());
									} else {
										if ((((AbstractField) ar[colno])
												.getValue()) instanceof dataCell) {
											dataCell dcx = (dataCell) (((AbstractField) ar[colno])
													.getValue());
											vl = BigDecimal.valueOf(Double
													.valueOf(dcx.getValue()
															.toString()));
										}
										if (!((((AbstractField) ar[colno])
												.getValue()) instanceof dataCell)
												&& (ar[colno] instanceof CheckBox)) {

											if (((CheckBox) ar[colno])
													.booleanValue()) {
												vl = BigDecimal.valueOf(Double.valueOf(lstItemCols
														.get(colno).onCheckValue));
											} else {
												vl = BigDecimal.valueOf(Double.valueOf(lstItemCols
														.get(colno).onUnCheckValue));

											}

										}

									}
								} catch (ParseException p) {
									Channelplus3Application
											.getInstance()
											.getMainWindow()
											.showNotification(
													lstItemCols.get(colno).display_format,
													p.getMessage(),
													Notification.TYPE_ERROR_MESSAGE);
									Logger.getLogger(
											utilsVaadin.class.getName()).log(
											Level.SEVERE, null, p);
								}
							}

						}
						// if datefield then....
						if (ar[colno] instanceof DateField) {
							vl = new Timestamp(System.currentTimeMillis());
							if (((DateField) ar[colno]).getValue() != null)
								((Timestamp) vl)
										.setTime(((Date) ((DateField) ar[colno])
												.getValue()).getTime());
							else
								vl = null;

						}
						if (ar[colno] instanceof CheckBox) {
							vl = lstItemCols.get(colno).onUnCheckValue;
							if (((CheckBox) ar[colno]).booleanValue()) {
								vl = lstItemCols.get(colno).onCheckValue;
							}
						}

						// user value
						if (lstItemCols.get(colno).action != null) {
							Object vlx = datax.getFieldValue(rowno,
									lstItemCols.get(colno).colname);
							if (!utils.nvl(vl, "").equals(utils.nvl(vlx, ""))) {
								Object vl2 = lstItemCols.get(colno).action
										.onValueChange(rowno,
												lstItemCols.get(colno).colname,
												vl);
								if (vl2 != null) {
									vl = vl2;
								} else {
									event.getProperty().setValue(null);
									vl = vl2;
								}

							}
						}
						// set value to data model
						Boolean b = (Boolean) ((AbstractField) ar[colno])
								.getData();
						if (b.booleanValue()) {
							datax.setFieldValue(rowno, colname, vl);

							if (lstItemCols.get(colno).actionAfterUpdate != null) {
								lstItemCols.get(colno).actionAfterUpdate
										.onValueChange(rowno,
												lstItemCols.get(colno).colname,
												vl);
							}

						}
						/*
						 * if (refresh_row) { refresh_row = false;
						 * refreshRowFromData(datax, tbl, rowno, lstItemCols);
						 * refresh_row = true; }
						 */
					}
				});
			}
		}
		Object id = tbl.addItem(ar,
				Integer.valueOf(data.getRows().get(i).rowno));
		return id;
	}

	public static void query_data2(final Table tbl, localTableModel data,
			final List<ColumnProperty> lstItemCols) {
		system_status = "QUERY";
		try {
			tbl.removeAllItems();
			for (int i = 0; i < data.getRows().size(); i++) {
				add_record_table(i, tbl, data, lstItemCols);
			}
		} finally {
			system_status = "NORMAL";
		}

	}

	public static void query_data(final Table tbl, localTableModel data,
			final List<ColumnProperty> lstItemCols) {
		for (int i = 0; i < data.getRows().size(); i++) {
			final Object ar[] = new Object[lstItemCols.size()];
			for (int j = 0; j < lstItemCols.size(); j++) {
				// asinging cell component
				if (lstItemCols.get(j).col_class == Label.class) {
					ar[j] = new Label();
				}
				if (lstItemCols.get(j).col_class == TextField.class) {
					ar[j] = new TextField();
				}
				if (lstItemCols.get(j).col_class == ComboBox.class) {
					ar[j] = new ComboBox();
				}
				if (lstItemCols.get(j).col_class == DateField.class) {
					ar[j] = new DateField("", new java.util.Date(
							((Timestamp) data.getFieldValue(i,
									lstItemCols.get(j).colname)).getTime()));
					((DateField) ar[j]).setResolution(DateField.RESOLUTION_DAY);
					if (lstItemCols.get(j).display_format
							.equals("SHORT_DATE_FORMAT")) {
						((DateField) ar[j])
								.setDateFormat(utils.FORMAT_SHORT_DATE);
					} else {
						((DateField) ar[j])
								.setDateFormat(lstItemCols.get(j).display_format);
					}
				}

				((AbstractComponent) ar[j]).setWidth("100%");

				// alignments of cell component
				((AbstractComponent) ar[j]).addStyleName("leftalign");
				if (lstItemCols.get(j).display_align
						.equals(ColumnProperty.ALIGN_CENTER)) {
					((AbstractComponent) ar[j]).addStyleName("centeralign");
				}
				if (lstItemCols.get(j).display_align
						.equals(ColumnProperty.ALIGN_RIGHT)) {
					((AbstractComponent) ar[j]).addStyleName("rightalign");
				}
				// if not data is Date then..
				if (data.getFieldValue(i, lstItemCols.get(j).colname) != null) {
					if (!data.getColByName(lstItemCols.get(j).colname)
							.isDateTime()) {
						// setting string value.
						((Property) ar[j]).setValue(data.getFieldValue(i,
								lstItemCols.get(j).colname).toString());
					}

					// if there is money format then
					if (!lstItemCols.get(j).display_format.equals("NONE")
							&& !data.getColByName(lstItemCols.get(j).colname)
									.isDateTime()) {
						Number n = (Number) data.getFieldValue(i,
								lstItemCols.get(j).colname);
						if (n != null) {
							((Property) ar[j]).setValue((new DecimalFormat(
									lstItemCols.get(j).display_format))
									.format(n.floatValue()));
						}
					}
					if (!lstItemCols.get(j).display_format.equals("NONE")
							&& data.getColByName(lstItemCols.get(j).colname)
									.isDateTime()
							&& lstItemCols.get(j).col_class != DateField.class) {
						Timestamp n = (Timestamp) data.getFieldValue(i,
								lstItemCols.get(j).colname);
						if (n != null) {
							((Property) ar[j]).setValue((new SimpleDateFormat(
									lstItemCols.get(j).display_format))
									.format(n));
						}
					}

					((AbstractComponent) ar[j])
							.addStyleName(lstItemCols.get(j).other_styles);
				}
				if (ar[j] instanceof AbstractField) {
					final localTableModel datax = data;
					final int rowno = i;
					final int colno = j;
					final String colname = lstItemCols.get(j).colname;
					((AbstractField) ar[j]).setReadOnly(false);
					if (lstItemCols.get(j).display_type.equals("DISABLED")) {
						((AbstractField) ar[j]).setReadOnly(true);
					}
					((AbstractField) ar[j]).setImmediate(false);
					if (ar[j] instanceof TextField) {
						((TextField) ar[j]).addListener(new FocusListener() {

							public void focus(FocusEvent event) {
								((TextField) event.getComponent()).selectAll();
							}
						});
					}
					((AbstractField) ar[j])
							.addListener(new ValueChangeListener() {
								private boolean refresh_row = true;

								public void valueChange(ValueChangeEvent event) {
									if (!refresh_row) {
										return;
									}
									Object vl = null;
									// take it as string
									if (((AbstractField) ar[colno]).getValue() != null) {
										vl = ((AbstractField) ar[colno])
												.getValue().toString();
									}
									// if number column then....
									if (datax.getColByName(colname).isNumber()) {
										DecimalFormat dc = new DecimalFormat(
												lstItemCols.get(colno).display_format);
										if (((AbstractField) ar[colno])
												.getValue() != null) {
											try {
												if (!lstItemCols.get(colno).display_format
														.equals("NONE")) {
													vl = BigDecimal
															.valueOf(dc
																	.parse(((AbstractField) ar[colno])
																			.getValue()
																			.toString())
																	.floatValue());
												} else {
													vl = BigDecimal.valueOf(Float
															.valueOf((((AbstractField) ar[colno])
																	.getValue())
																	.toString()));
												}
											} catch (ParseException p) {
												Channelplus3Application
														.getInstance()
														.getMainWindow()
														.showNotification(
																lstItemCols
																		.get(colno).display_format,
																p.getMessage(),
																Notification.TYPE_ERROR_MESSAGE);
												Logger.getLogger(
														utilsVaadin.class
																.getName())
														.log(Level.SEVERE,
																null, p);
											}
										}

									}
									// if datefield then....
									if (ar[colno] instanceof DateField) {
										vl = new Timestamp(System
												.currentTimeMillis());
										((Timestamp) vl)
												.setTime(((Date) ((DateField) ar[colno])
														.getValue()).getTime());
									}
									if (ar[colno] instanceof CheckBox) {
										vl = lstItemCols.get(colno).onUnCheckValue;
										if (((CheckBox) ar[colno])
												.booleanValue()) {
											vl = lstItemCols.get(colno).onCheckValue;
										}
									}
									// user value
									if (lstItemCols.get(colno).action != null) {
										Object vl2 = lstItemCols.get(colno).action
												.onValueChange(
														rowno,
														lstItemCols.get(colno).colname,
														vl);
										if (vl2 != null) {
											vl = vl2;
										}
									}
									// set value to data model
									datax.setFieldValue(rowno, colname, vl);

									if (refresh_row) {
										refresh_row = false;
										refreshRowFromData(datax, tbl, rowno,
												lstItemCols);
										refresh_row = true;
									}

								}
							});
				}
			}
			tbl.addItem(ar, Integer.valueOf(i));
		}
	}

	public static void refreshRowFromData(localTableModel data, Table tbl,
			int rowno, List<ColumnProperty> lstItemCols) {

		for (Iterator<ColumnProperty> iterator = lstItemCols.iterator(); iterator
				.hasNext();) {
			ColumnProperty columnProperty = (ColumnProperty) iterator.next();
			Item itm = tbl.getItem(rowno);
			if (itm == null) {
				return;
			}

			Object o = itm.getItemProperty(columnProperty.descr).getValue();

			if (columnProperty.col_class == DateField.class) {
				boolean old_readonly = ((DateField) o).isReadOnly();
				((DateField) o).setData(false);
				try {
					if (data.getFieldValue(rowno, columnProperty.colname) != null) {
						((DateField) o).setReadOnly(false);
						((Date) ((DateField) o).getValue())
								.setTime((((Date) data.getFieldValue(rowno,
										columnProperty.colname)).getTime()));
					}

					((DateField) o).setResolution(DateField.RESOLUTION_DAY);
					if (columnProperty.display_format
							.equals("SHORT_DATE_FORMAT")) {
						((DateField) o).setDateFormat(utils.FORMAT_SHORT_DATE);

					} else {
						((DateField) o)
								.setDateFormat(columnProperty.display_format);
					}
				} finally {
					((DateField) o).setReadOnly(old_readonly);
					((DateField) o).setData(true);
				}
			}
			/*
			 * if (o instanceof ComboBox) {
			 * 
			 * dataCell vl = null; if (((ComboBox) o).getValue() != null) { vl =
			 * (dataCell) ((ComboBox) o).getValue(); } if
			 * (columnProperty.lov_sql != null) {
			 * utilsVaadin.FillComboFromList((ComboBox) o,
			 * columnProperty.lov_sql); } ((ComboBox) o).setValue(vl); }
			 */

			if (data.getFieldValue(rowno, columnProperty.colname) != null) {
				boolean old_readonly = ((Property) o).isReadOnly();

				if (o instanceof AbstractField) {
					((AbstractField) o).setData(false);
				}
				try {
					((Property) o).setReadOnly(false);
					if (!data.getColByName(columnProperty.colname).isDateTime()
							&& !(o instanceof CheckBox)
							&& !(o instanceof ComboBox)) {

						((Property) o).setValue(data.getFieldValue(rowno,
								columnProperty.colname).toString());
					}
					if (o instanceof CheckBox) {
						if (data.getFieldValue(rowno, columnProperty.colname)
								.toString().equals(columnProperty.onCheckValue)) {
							((Property) o).setValue(true);
						} else {
							((Property) o).setValue(false);
						}
					}
					if (o instanceof ComboBox) {
						((Property) o).setValue(utilsVaadin.findByValue(
								((ComboBox) o),
								data.getFieldValue(rowno,
										columnProperty.colname).toString()));
					}
					if (o instanceof SearchField
							&& !columnProperty.searchLookUpColumn.isEmpty()) {

						((SearchField) (Property) o).setDisplayValue(data
								.getFieldValue(rowno, columnProperty.colname)
								.toString(), utils.nvl(data.getFieldValue(
								rowno, columnProperty.searchLookUpColumn), ""));

					}

					// if there is other format then
					if (!columnProperty.display_format.equals("NONE")
							&& !data.getColByName(columnProperty.colname)
									.isDateTime() && !(o instanceof CheckBox)) {
						Number n = (Number) data.getFieldValue(rowno,
								columnProperty.colname);
						if (n != null) {
							((Property) o).setValue((new DecimalFormat(
									columnProperty.display_format)).format(n
									.doubleValue()));
						}
					}
				} finally {
					((Property) o).setReadOnly(old_readonly);
					if (o instanceof AbstractField) {
						((AbstractField) o).setData(true);
					}

				}
			}

		}
	}

	public static String getStyleFromCol(List<ColumnProperty> lstItemCols,
			String propertyId) {
		if (propertyId == null) {
			return null;
		}
		int rn = -1;
		for (int i = 0; i < lstItemCols.size(); i++) {
			if (propertyId.toString().equals(lstItemCols.get(i).descr)) {
				rn = i;
				break;
			}

		}
		if (rn < 0) {
			return null;
		}
		if (lstItemCols.get(rn).display_align.equals("ALIGN_RIGHT")) {
			return "rightalign";
		}
		if (lstItemCols.get(rn).display_align.equals("ALIGN_LEFT")) {
			return "leftalign";
		}
		if (lstItemCols.get(rn).display_align.equals("ALIGN_CENTER")) {
			return "centeralign";
		}

		return null;
	}

	public static ColumnProperty findColByDescr(String des,
			List<ColumnProperty> lstItemCols) {
		for (Iterator iterator = lstItemCols.iterator(); iterator.hasNext();) {
			ColumnProperty prop = (ColumnProperty) iterator.next();
			if (prop.descr.equals(des)) {
				return prop;
			}
		}
		return null;
	}

	public static ColumnProperty findColByCol(String des,
			List<ColumnProperty> lstItemCols) {
		for (Iterator iterator = lstItemCols.iterator(); iterator.hasNext();) {
			ColumnProperty prop = (ColumnProperty) iterator.next();
			if (prop.colname.toUpperCase().equals(des.toUpperCase())) {
				return prop;
			}
		}
		return null;
	}

	public static ColumnProperty findColByPos(int des,
			List<ColumnProperty> lstItemCols) {
		for (Iterator iterator = lstItemCols.iterator(); iterator.hasNext();) {
			ColumnProperty prop = (ColumnProperty) iterator.next();
			if (prop.pos == des) {
				return prop;
			}
		}
		return null;
	}

	public static TableView showSearchWithSelection(VerticalLayout listlayout,
			TableView.SelectionListener vc, Connection con, String sql,
			String[] hidecols) throws SQLException {
		Window wndList = listlayout.getWindow();
		if (wndList != null && (listlayout.getParent() instanceof Window)) {
			wndList.setWidth("70%");
			wndList.setHeight("70%");
			wndList.setImmediate(true);
			wndList.setModal(true);
		}
		listlayout.setSizeFull();
		final DBClass dbcx = new DBClass(con);
		listlayout.removeAllComponents();
		final TableView tblview = new TableView(dbcx);

		for (int i = 0; i < hidecols.length; i++) {
			tblview.getListHiddenCols().add(hidecols[i]);
		}
		tblview.setCheckDefaultSelection(false);
		tblview.setCheckSelectionField(true);
		tblview.FetchSql(sql);
		tblview.setParentPanel(listlayout);
		tblview.setOnseleciton2(vc);
		tblview.setAutoSelection(false);
		tblview.createView();
		if (wndList != null
				&& !Channelplus3Application.getInstance().getMainWindow()
						.getChildWindows().contains(wndList)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wndList);
		}
		if (wndList != null && listlayout.getParent() instanceof Window)
			wndList.setCaption(tblview.getData().getRows().size()
					+ " Rows selected");
		return tblview;
	}

	public static TableView showSearch(VerticalLayout listlayout,
			TableView.SelectionListener vc, Connection con, String sql,
			boolean createView) throws SQLException {
		return showSearch(listlayout, vc, con, sql, createView, new String[] {});
	}

	public static TableView showSearch(VerticalLayout listlayout,
			TableView.SelectionListener vc, Connection con, String sql,
			boolean createView, String[] hidecols) throws SQLException {
		Window wndList = listlayout.getWindow();
		if (wndList != null && (listlayout.getParent() instanceof Window)) {
			wndList.setWidth("70%");
			wndList.setHeight("70%");
			wndList.setImmediate(true);
			wndList.setModal(true);
		}
		listlayout.setSizeFull();
		final DBClass dbcx = new DBClass(con);
		listlayout.removeAllComponents();
		final TableView tblview = new TableView(dbcx);

		for (int i = 0; i < hidecols.length; i++) {
			tblview.getListHiddenCols().add(hidecols[i]);
		}

		tblview.FetchSql(sql);
		tblview.setParentPanel(listlayout);
		tblview.setOnseleciton2(vc);
		tblview.setAutoSelection(false);
		if (createView) {
			tblview.createView();
		}
		if (wndList != null
				&& !Channelplus3Application.getInstance().getMainWindow()
						.getChildWindows().contains(wndList)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wndList);
		}
		if (wndList != null && listlayout.getParent() instanceof Window)
			wndList.setCaption(tblview.getData().getRows().size()
					+ " Rows selected");
		return tblview;
	}

	public static void fillTree(Tree tree, localTableModel data,
			String parentCol, String codeCol, String titleCol, String ChildCol) {
		fillTree(tree, data, parentCol, codeCol, titleCol, ChildCol, "", "",
				"", "", "");
	}

	public static void fillTree(Tree tree, localTableModel data,
			String parentCol, String codeCol, String titleCol, String ChildCol,
			String paraCol1, String paraCol2, String paraCol3, String paraCol4,
			String iconCol) {
		String lastParent = "";
		menuItem root_node = null, lastparentnode = null, ndx = null;
		tree.removeAllItems();
		for (int i = 0; i < data.getRows().size(); i++) {
			String current_parent = "";
			String current_code = utils.nvl(data.getFieldValue(i, codeCol), "");
			String current_title = utils.nvl(data.getFieldValue(i, titleCol),
					"");
			current_parent = utils.nvl(data.getFieldValue(i, parentCol), "");
			BigDecimal current_childcount = (BigDecimal) data.getFieldValue(i,
					ChildCol);
			if (current_parent.length() != 0) {
				if (lastParent.equals(current_parent)) {
					root_node = lastparentnode;
				} else {
					root_node = findNodeByValue(tree, current_parent, null);
					lastParent = current_parent;
					lastparentnode = root_node;
				}
			} else {
				root_node = null;
			}
			ndx = new menuItem(current_code, current_title);

			if (paraCol1.length() > 0) {
				ndx.setPara1Val(data.getFieldValue(i, paraCol1));
			}
			if (paraCol2.length() > 0) {
				ndx.setPara2Val(data.getFieldValue(i, paraCol2));
			}
			if (paraCol3.length() > 0) {
				ndx.setPara3Val(data.getFieldValue(i, paraCol3));
			}
			if (paraCol4.length() > 0) {
				ndx.setPara4Val(data.getFieldValue(i, paraCol4));
			}
			if (iconCol.length() > 0)
				ndx.setIconfile(data.getFieldValue(i, iconCol).toString());

			tree.addItem(ndx);

			if (ndx.getIconfile() != null && ndx.getIconfile().length() > 0)
				tree.setItemIcon(ndx, new ThemeResource(ndx.getIconfile()));

			if (root_node != null) {
				tree.setParent(ndx, root_node);
			}
			if (current_childcount.intValue() == 0) {
				tree.setChildrenAllowed(ndx, false);
			} else {
				tree.setChildrenAllowed(ndx, true);
			}

		}
	}

	public static menuItem findNodeByValue(Tree tree, String vl, menuItem itm) {
		int cnt = 0;
		for (Iterator<?> it = tree.getItemIds().iterator(); it.hasNext();) {
			menuItem mn = (menuItem) it.next();
			if (mn.getId().equals(vl)) {
				return mn;
			}
		}
		return null;
	}

	public static void switchProfile(VerticalLayout listlayout) {
		switchProfile(listlayout, "");
	}

	public static void switchProfile(VerticalLayout listlayout,
			String modulename) {
		Connection con = Channelplus3Application.getInstance()
				.getFrmUserLogin().getDbc().getDbConnection();

		TableView.SelectionListener sl = new SelectionListener() {
			public void onSelection(TableView tv) {
				Channelplus3Application.getInstance().getMainWindow()
						.removeWindow(wndlist);
				if (tv.getSelectionValue() < 0) {
					return;
				}
				Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_MENU_CODE = tv
						.getData()
						.getFieldValue(tv.getSelectionValue(), "CODE")
						.toString();
				Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_MENU_NAME = tv
						.getData()
						.getFieldValue(tv.getSelectionValue(), "TITLE")
						.toString();
				Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_MENU_MODULE = tv
						.getData()
						.getFieldValue(tv.getSelectionValue(), "MODULE_NAME")
						.toString();

				if (tv.getData()
						.getFieldValue(tv.getSelectionValue(), "MENU_CLASS")
						.equals("MAIN_MENUS")) {
					Window wndx = Channelplus3Application.getInstance()
							.getMainWindow();
					Channelplus3Application.getInstance().setMainWindow(
							Channelplus3Application.getInstance().getPurWnd());
					wndx.open(new ExternalResource(Channelplus3Application
							.getInstance().getPurWnd().getURL()));
					Channelplus3Application
							.getInstance()
							.getPurWnd()
							.setContent(
									new frmMainMenus(Channelplus3Application
											.getInstance().getPurWnd()));

				}
			}
		};

		try {
			if (listlayout.getParent() == null) {
				wndlist = new Window();
				wndlist.setContent(listlayout);
			}
			int cp = Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_PROFILENO;
			String sq = "select nvl(count(*),0) from cp_main_groups "
					+ " where (module_name='" + modulename.toUpperCase()
					+ "' or nvl('" + modulename.toUpperCase()
					+ "','') IS NULL) and  (profiles like '%\"" + cp
					+ "\"%'  or " + cp + " =0)  order by code";
			String sq2 = "select TITLE,TITLEA,CODE,PIC_NAME,FLAG,MENU_CLASS,MODULE_NAME from cp_main_groups "
					+ " where (module_name='"
					+ modulename.toUpperCase()
					+ "' or nvl('"
					+ modulename.toUpperCase()
					+ "','') IS NULL) and  (profiles like '%\""
					+ cp
					+ "\"%'  or " + cp + "=0)   order by code";

			int vl = Integer.valueOf(utils.getSqlValue(sq, con));
			if (vl > 1) {
				final TableView tblview = utilsVaadin.showSearch(listlayout,
						sl, con, sq2, true, new String[] { "PROFILES", "CODE",
								"PIC_NAME", "FLAG", "MENU_CLASS" });
			} else {
				PreparedStatement psx = con.prepareStatement(sq2,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rsx = psx.executeQuery();
				if (rsx.first()) {
					Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_MENU_CODE = rsx
							.getString("CODE");
					Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_MENU_NAME = rsx
							.getString("TITLE");
					Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_MENU_MODULE = rsx
							.getString("MODULE_NAME");

					if (rsx.getString("MENU_CLASS").equals("MAIN_MENUS")) {
						Window wndx = Channelplus3Application.getInstance()
								.getMainWindow();
						Channelplus3Application.getInstance().setMainWindow(
								Channelplus3Application.getInstance()
										.getPurWnd());
						wndx.open(new ExternalResource(Channelplus3Application
								.getInstance().getPurWnd().getURL()));
						Channelplus3Application
								.getInstance()
								.getPurWnd()
								.setContent(
										new frmMainMenus(
												Channelplus3Application
														.getInstance()
														.getPurWnd()));

					}
					psx.close();
				}

			}
		} catch (Exception e) {
			Logger.getLogger(utilsVaadin.class.getName()).log(Level.SEVERE,
					null, e);
			listlayout.getWindow().showNotification("Error filling table",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public static void assignValues(ResultSet rst, List<FieldInfo> lstflds)
			throws SQLException {
		if (rst == null || !rst.first()) {
			return;
		}

		for (Iterator iterator = lstflds.iterator(); iterator.hasNext();) {
			FieldInfo fi = (FieldInfo) iterator.next();
			try {
				if (fi.obj instanceof TextField) {
					TextField t = ((TextField) fi.obj);
					if (fi.fieldName != null && !fi.fieldName.isEmpty())
						setValueByForce(t,
								utils.nvl(rst.getString(fi.fieldName), ""));
					if (fi.fieldType.equals(Parameter.DATA_TYPE_NUMBER)
							&& !fi.format.isEmpty()) {
						DecimalFormat dc = new DecimalFormat(fi.format);
						if (fi.fieldName != null && !fi.fieldName.isEmpty()
								&& rst.getString(fi.fieldName) != null) {
							setValueByForce(t,
									dc.format(rst.getDouble(fi.fieldName)));
						} else {
							setValueByForce(t, "");
						}
					}
				}

				if (fi.obj instanceof SearchField) {
					SearchField c = (SearchField) fi.obj;
					if (rst.getString(fi.fieldName) == null)
						c.setValue("");
					else
						c.setValue(rst.getString(fi.fieldName));

				}

				if (fi.obj instanceof ComboBox) {
					ComboBox c = (ComboBox) fi.obj;
					if (rst.getString(fi.fieldName) == null)
						setValueByForce(c, null);
					else
						setValueByForce(
								c,
								utilsVaadin.findByValue(c,
										rst.getString(fi.fieldName)));

				}

				if (fi.obj instanceof DateField) {
					DateField c = (DateField) fi.obj;
					boolean oldr = c.isReadOnly();
					c.setReadOnly(false);
					try {
						if (rst.getTimestamp(fi.fieldName) == null) {
							c.setValue(null);
						} else {
							if (c.getValue() == null) {
								c.setValue(new Date(rst.getTimestamp(
										fi.fieldName).getTime()));
							} else {
								((java.util.Date) c.getValue()).setTime(rst
										.getTimestamp(fi.fieldName).getTime());
							}
						}
					} finally {
						c.setReadOnly(oldr);
					}
				}

				if (fi.obj instanceof CheckBox) {
					CheckBox c = (CheckBox) fi.obj;
					boolean oldr = c.isReadOnly();
					c.setReadOnly(false);
					try {
						if (rst.getString(fi.fieldName) == null) {
							c.setValue(false);
						} else {
							if (rst.getString(fi.fieldName).equals(
									fi.valueOnTrue)) {
								c.setValue(true);
							} else {
								c.setValue(false);
							}

						}
					} finally {
						c.setReadOnly(oldr);
					}

				}
			} catch (SQLException ex) {
				if (fi != null && fi.obj != null)
					System.out.println(fi.fieldName
							+ " <--- raised error on fetching field");

				throw ex;
			}
		}
	}

	public static void setText(FieldInfo fld) throws ParseException {
		if (fld.obj == null)
			return;
		TextField txt = null;
		if (!(fld.obj instanceof TextField))
			return;
		txt = (TextField) fld.obj;
		if (txt.getValue() == null
				|| txt.getValue().toString().trim().isEmpty())
			return;

		double d = 0;

		if (fld.fieldType.equals(Parameter.DATA_TYPE_NUMBER)
				&& (fld.format == null || fld.format.isEmpty())) {
			d = Double.valueOf(txt.getValue().toString());
			dataCell dc = new dataCell(txt.getValue().toString(),
					BigDecimal.valueOf(d));
			txt.setData(dc);
		}
		if ((fld.format != null && !fld.format.isEmpty())
				&& fld.fieldType.equals(Parameter.DATA_TYPE_NUMBER)) {
			DecimalFormat df = new DecimalFormat(fld.format);
			String t = df.format(df.parse(txt.getValue().toString()));
			dataCell dc = new dataCell(t, BigDecimal.valueOf(df.parse(
					txt.getValue().toString()).doubleValue()));
			txt.setData(dc);
			txt.setValue(t);
		}
	}

	public static void setDefaultValues(List<FieldInfo> lstfld,
			boolean nullOthers) {
		setDefaultValues(lstfld, nullOthers, false);
	}

	public static void setDefaultValues(List<FieldInfo> lstfld,
			boolean nullOthers, boolean nullDates) {
		for (Iterator iterator = lstfld.iterator(); iterator.hasNext();) {
			FieldInfo fi = (FieldInfo) iterator.next();
			if (fi.obj instanceof TextField)
				setValueByForce(fi.obj, "");

			if (fi.obj instanceof DateField && nullDates)
				setValueByForce(fi.obj, null);

			if (fi.obj instanceof SearchField)
				setValueByForce(fi.obj, null);

			if (fi.obj instanceof ComboBox)
				setValueByForce(fi.obj, null);

			if (fi.obj instanceof TextField && fi.defaultValue != null) {
				setValueByForce(fi.obj, fi.defaultValue);
			}
			if (fi.obj instanceof ComboBox && fi.defaultValue != null
					&& !(fi.defaultValue instanceof dataCell))
				setValueByForce(fi.obj, utilsVaadin.findByValue(
						(ComboBox) fi.obj, fi.defaultValue.toString()));
			else
				setValueByForce(fi.obj, fi.defaultValue);
			if (fi.obj instanceof CheckBox && fi.defaultValue != null) {
				if (fi.defaultValue.equals(fi.valueOnTrue))
					setValueByForce(fi.obj, true);
				else
					setValueByForce(fi.obj, false);
			}

		}
	}

	public static void resetLayout(AbstractLayout layout,
			boolean resetTableLayout) {
		for (Iterator iterator = layout.getComponentIterator(); iterator
				.hasNext();) {
			Component c = (Component) iterator.next();
			if (c instanceof AbstractLayout
					&& !(c instanceof TableLayoutVaadin)) {
				resetLayout((AbstractLayout) c, resetTableLayout);
				((AbstractLayout) c).removeAllComponents();
			}
			if (resetTableLayout && c instanceof TableLayoutVaadin)
				((TableLayoutVaadin) c).resetLayout();

		}

	}

	public static void resetValues(AbstractLayout layout, boolean todayDate,
			boolean default_check) {

		for (Iterator iterator = layout.getComponentIterator(); iterator
				.hasNext();) {
			Component c = (Component) iterator.next();
			boolean old_rd = c.isReadOnly();
			try {
				c.setReadOnly(false);
				if (c instanceof TextField) {
					TextField t = (TextField) c;
					t.setValue("");
				}
				if (c instanceof SearchField) {
					SearchField t = (SearchField) c;
					t.setValue("");
					t.setDisplay("");
				}

				if (c instanceof ComboBox) {
					ComboBox l = (ComboBox) c;
					if (l.isNullSelectionAllowed()) {
						l.setValue(null);
					} else {
						if (l.getItemIds().size() > 0)
							l.setValue(l.getItemIds().toArray()[0]);
					}

				}
				if (c instanceof OptionGroup) {
					OptionGroup l = (OptionGroup) c;
					if (l.isNullSelectionAllowed()) {
						l.setValue(null);
					} else {
						if (l.getItemIds().size() > 0)
							l.setValue(l.getItemIds().toArray()[0]);
					}

				}

				if (c instanceof DateField) {
					DateField t = (DateField) c;
					if (!todayDate) {
						t.setValue(null);
					} else {
						if (t.getValue() != null) {
							((Date) t.getValue()).setTime(System
									.currentTimeMillis());
						} else {
							Date dt = new Date(System.currentTimeMillis());
							t.setValue(dt);
						}
					}

				}

				if (c instanceof CheckBox) {
					CheckBox t = (CheckBox) c;
					t.setValue(default_check);
				}
				if (c instanceof AbstractLayout) {
					utilsVaadin.resetValues((AbstractLayout) c, todayDate,
							default_check);
				}
			}

			finally {
				c.setReadOnly(old_rd);
			}

		}
	}

	public static void showMessage(String mess) {
		if (Channelplus3Application.getInstance() != null
				&& Channelplus3Application.getInstance().getMainWindow() != null) {
			showMessage(Channelplus3Application.getInstance().getMainWindow(),
					mess, Notification.TYPE_TRAY_NOTIFICATION);
		}
	}

	public static void showMessage(Window wnd, String mess, int not) {
		String msg = utils.nvl(mess, "");
		if (mess.startsWith("$")) {
			msg = Channelplus3Application.getInstance().getFrmUserLogin().mapMsgs
					.get(mess.substring(1));

		}
		wnd.showNotification(msg, not);
	}

	public static String getMessage(String mess) {
		String msg = utils.nvl(mess, "");
		if (mess.startsWith("$")) {
			msg = Channelplus3Application.getInstance().getFrmUserLogin().mapMsgs
					.get(mess.substring(1));

		}
		return msg;
	}

	public static void treeCollpae(Tree tree) {

		for (Iterator iterator = tree.getItemIds().iterator(); iterator
				.hasNext();) {
			Object m = iterator.next();
			if (tree.hasChildren(m)) {
				tree.collapseItemsRecursively(m);
			}

		}
	}

	public static void treeExpand(Tree tree) {
		for (Iterator iterator = tree.getItemIds().iterator(); iterator
				.hasNext();) {
			Object m = iterator.next();
			if (tree.hasChildren(m)) {
				tree.expandItemsRecursively(m);
			}

		}
	}

	public static void setValueByForce(Object txt1, Object val) {
		AbstractField txt = (AbstractField) txt1;
		boolean oldreadonly = txt.isReadOnly();
		SimpleDateFormat sd = new SimpleDateFormat(Channelplus3Application
				.getInstance().getFrmUserLogin().FORMAT_SHORT_DATE);
		txt.setReadOnly(false);
		try {
			if (txt instanceof TextField)
				txt.setValue(utils.nvl(val, ""));
			if (txt instanceof ComboBox)
				txt.setValue(val);
			if (txt instanceof CheckBox)
				txt.setValue(val);
			if (txt instanceof SearchField)
				((SearchField) txt).setDisplayValue("", "");
			if (txt instanceof DateField && val == null)
				txt.setValue(val);
			if (txt instanceof DateField && val != null
					&& !val.toString().isEmpty())
				txt.setValue(sd.parse(val.toString()));
		} catch (Exception e) {
			e.printStackTrace();

		}
		txt.setReadOnly(oldreadonly);
	}

	public static void format_money(Object txt1, List<FieldInfo> lst,
			String addStyle) {
		if (utils.findFieldInfoByObject(txt1, lst) != null)
			utils.findFieldInfoByObject(txt1, lst).format = Channelplus3Application
					.getInstance().getFrmUserLogin().FORMAT_MONEY;
		((AbstractField) txt1).addStyleName("netAmtStyle " + addStyle);
	}

	public static void update_para_val_to_objects(List<Parameter> listProps) {
		for (Iterator iterator = listProps.iterator(); iterator.hasNext();) {
			Parameter par = (Parameter) iterator.next();
			if (par.getUIObject() != null) {
				AbstractField c = (AbstractField) par.getUIObject();
				if (c instanceof CheckBox) {
					c.setValue(false);
					if (utils.nvl(par.getValue(), par.val_on_uncheck).equals(
							par.val_on_check))
						c.setValue(true);
				} else
					c.setValue(utils.nvl(par.getValue(), ""));
				if (c instanceof ComboBox)
					((ComboBox) c).setValue(utilsVaadin.findByValue(
							(ComboBox) c, utils.nvl(par.getValue(), "")));
			}
		}

	}

	public static void update_objects_to_para_val(List<Parameter> listProps) {
		for (Iterator iterator = listProps.iterator(); iterator.hasNext();) {
			Parameter par = (Parameter) iterator.next();
			if (par.getUIObject() != null) {
				AbstractField c = (AbstractField) par.getUIObject();
				Object value = c.getValue();

				if (c.getValue() instanceof dataCell)
					value = ((dataCell) c.getValue()).getValue();

				if (c instanceof CheckBox) {
					CheckBox ch = (CheckBox) c;
					if (ch.booleanValue())
						value = par.val_on_check;
					else
						value = par.val_on_uncheck;
				}

				if (value != null
						&& par.getValueType()
								.equals(Parameter.DATA_TYPE_NUMBER))
					value = BigDecimal
							.valueOf(Double.valueOf(value.toString()));

				if (value != null
						&& (par.getValueType().equals(Parameter.DATA_TYPE_DATE) || par
								.getValueType().equals(
										Parameter.DATA_TYPE_DATETIME))) {
					SimpleDateFormat df = new SimpleDateFormat(
							utils.FORMAT_SHORT_DATE);
					Timestamp tm = null;
					try {
						tm = new Timestamp(
								(df.parse(value.toString())).getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					value = tm;
				}
				par.setValue(value);
			}
		}

	}

	public static void removeAllTreeWindows() {
		for (Iterator iterator = Channelplus3Application.getInstance()
				.getMainWindow().getChildWindows().iterator(); iterator
				.hasNext();) {
			Window wnd = (Window) iterator.next();
			if (wnd.getName() != null && wnd.getName().startsWith("treewnd_")) {
				Channelplus3Application.getInstance().getMainWindow()
						.removeWindow(wnd);
			}

		}
	}

	public static void setListToQueryExe(List<FieldInfo> lstfldinfo,
			QueryExe qe, Map<String, Object> paras) {
		for (Iterator iterator = lstfldinfo.iterator(); iterator.hasNext();) {
			FieldInfo fl = (FieldInfo) iterator.next();
			if (fl == null || fl.fieldName.isEmpty())
				continue;
			if (!(fl.obj instanceof CheckBox)) {
				Object dc = (((AbstractField) fl.obj).getData() == null ? ((AbstractField) fl.obj)
						.getValue() : ((AbstractField) fl.obj).getData());
				qe.setParaValue(fl.fieldName, dc);
			} else {
				qe.setParaValue(fl.fieldName, (((CheckBox) fl.obj)
						.booleanValue() ? fl.valueOnTrue : fl.valueOnFalse));
			}

		}
		if (paras == null)
			return;
		for (Iterator iterator = paras.keySet().iterator(); iterator.hasNext();) {
			String str = (String) iterator.next();
			qe.setParaValue(str, paras.get(str));
		}

	}

	public static void settFormattedValue(Object txt, List<FieldInfo> lstfld,
			Object val) {
		FieldInfo fl = utils.findFieldInfoByObject(txt, lstfld);
		if (fl == null) {
			setValueByForce(txt, val);
			return;
		}
		Object valx = val;
		if (fl.fieldType.equals(Parameter.DATA_TYPE_NUMBER)
				&& !fl.format.isEmpty())
			valx = (new DecimalFormat(fl.format)).format((Number) valx);
		if ((fl.fieldType.equals(Parameter.DATA_TYPE_DATE) || fl.fieldType
				.equals(Parameter.DATA_TYPE_DATETIME)) && !fl.format.isEmpty())
			valx = (new SimpleDateFormat(fl.format)).format((Date) valx);

		setValueByForce(txt, valx);
	}

	public static double getFieldInfoDoubleValue(Object obj,
			List<FieldInfo> lstItemCols) throws ParseException {
		FieldInfo fld = utils.findFieldInfoByObject(obj, lstItemCols);
		AbstractTextField ab = (AbstractTextField) obj;
		if (ab.getValue() == null || ab.getValue().toString().isEmpty())
			return 0;
		if (((!fld.fieldType.equals(Parameter.DATA_TYPE_NUMBER)) || fld.format
				.isEmpty())
				|| (fld.fieldType.equals(Parameter.DATA_TYPE_NUMBER) && fld.format
						.isEmpty()))
			return Double.valueOf(ab.getValue().toString());
		double vl = 0;
		if (!fld.format.isEmpty()) {
			vl = (new DecimalFormat(fld.format))
					.parse(ab.getValue().toString()).doubleValue();
		}

		return vl;
	}

	public static  boolean canDeleteTrans(String frm, Connection con) {
		String f = getMapVars().get("LG_ALL_DEL_ALLOW");
		if (f.equals("FALSE"))
			return false;

		return true;
	}

	public static boolean canEditTrans(String frm, Connection con) {
		String f = getMapVars().get("LG_ALL_EDIT_ALLOW");
		if (f.equals("FALSE"))
			return false;

		return true;
	}
	
	
	
}
