package com.doc.views.Query;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.vaadin.addon.JFreeChartWrapper;

import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.dataCell;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.windows.frmQuickRep;

public class QueryViewGraphs extends VerticalLayout {
	private Connection con = null;
	private HorizontalLayout lHzHeader = new HorizontalLayout();
	private VerticalLayout lVGraph = new VerticalLayout();
	public QueryView qv = null;
	public ProgressIndicator cmdProgress = new ProgressIndicator();
	private CheckBox chkShowValue = new CheckBox("Show Values", false);
	public Button cmdFresh = new Button("Refresh");
	public Button cmdPrint = new Button("Full Screen");
	public static String GRAPH_BAR = "BAR";
	public static String GRAPH_LINE = "LINE";
	public static String GRAPH_PIE = "PIE";
	public String queryCode = "";
	public String graphCode = "";
	public String comparisonCode = "";
	public String graphViewType = "BAR";
	public String field_row = "";
	public String field_value = "";
	public String field_time = "";
	public String field_ctg = "";
	public String field_descr = "";
	public String sqlString = "";
	private GraphGenerator gg = null;
	public JFreeChart chart = null;
	public Map<String, JFreeChartWrapper> mapVal = new HashMap<String, JFreeChartWrapper>();
	public ComboBox txtGraph = new ComboBox();

	QueryExe qe = new QueryExe();
	Integer rn = -1;
	DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	public QueryViewGraphs(QueryView qv) {
		super();
		this.qv = qv;
		txtGraph.setImmediate(true);
		chkShowValue.setImmediate(true);
		cmdProgress.setPollingInterval(1000);
		ValueChangeListener vlc = new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				try {
					if (txtGraph.getValue() == null) {
						return;
					}
					graphCode = ((dataCell) txtGraph.getValue()).getValue()
							.toString();
					// comparisonCode = ((dataCell) txtComparison.getValue())
					// .toString();
					load_selected_data();
					build_selected_graph();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}

			}
		};
		chkShowValue.addListener(vlc);
		cmdFresh.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				requestRepaintAll();

			}
		});

		cmdPrint.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					/*
					 * printGrpah();
					 */
					if (cmdPrint.getCaption().equals("Restore")) {
						cmdPrint.setCaption("Full screen");
						getWindow().setHeight("300px");
						getWindow().setWidth("60%");
						WebApplicationContext ctx = (WebApplicationContext) getApplication()
								.getContext();
						int height = ctx.getBrowser().getScreenHeight();
						getWindow().setPositionY(height - 500);
						getWindow().setPositionX(250);

					} else {

						getWindow().setSizeFull();
						getWindow().setPositionX(0);
						getWindow().setPositionY(0);
						cmdPrint.setCaption("Restore");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					getWindow().showNotification(ex.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
				}

			}
		});
		txtGraph.addListener(vlc);

	}

	public void build_selected_graph() {
		createView();
		gg = new GraphGenerator(this);
		gg.start();
		// view_graph();
	}

	public void load_selected_data() throws SQLException {
		PreparedStatement pst = con.prepareStatement(
				"select *from CP_SUB_COMMANDS where code='" + graphCode
						+ "' and command_type='GRAPH'",
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rst = pst.executeQuery();

		if (!rst.first()) {
			pst.close();
			throw new SQLException("Not found graph "
					+ ((dataCell) txtGraph.getValue()).getDisplay());
		}

		sqlString = rst.getString("SQLSTRING");
		field_time = rst.getString("FIELD_TIMESERIES");
		field_row = rst.getString("FIELD_ROW");
		field_value = rst.getString("FIELD_VALUE");
		graphViewType = rst.getString("TYPEGRAPH");
		field_ctg = rst.getString("FIELD_CTG");
		field_descr = rst.getString("DESCR");
		pst.close();

	}

	public void initAllGraphs(String queryCode, String graphCode) {
		this.queryCode = queryCode;
		this.graphCode = graphCode;
		initAllGraphs();
	}

	public void initAllGraphs() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {
			// write function for generating like clause automatically

			utilsVaadin
					.FillCombo(
							txtGraph,
							"select code,nvl(title,descr) from CP_SUB_COMMANDS"
									+ " where query_code_in like '%\"'||'"
									+ queryCode
									+ "'||'\"%' AND (query_field_in is null or "
									+ utilsVaadin.generateFieldClause(qv)
									+ ")  and command_type='GRAPH' order by POSX ",
							con);
			if (txtGraph.getItemIds().size() <= 0) {
				getWindow().showNotification("No graph found");
				return;
			}

			if (graphCode.length() <= 0) {
				graphCode = ((dataCell) txtGraph.getItemIds().toArray()[0])
						.getValue().toString();
			}
			txtGraph.setValue(utilsVaadin.findByValue(txtGraph, graphCode));
			load_selected_data();

			build_selected_graph();

		} catch (SQLException ex) {
			ex.printStackTrace();
			getWindow().showNotification(ex.getMessage());

		}

	}

	public void createView() {
		removeAllComponents();
		lVGraph.removeAllComponents();
		lHzHeader.removeAllComponents();
		lVGraph.setSizeFull();
		lHzHeader.setHeight("100%");
		setSizeFull();

		ResourceManager.addComponent(this, lHzHeader);
		ResourceManager.addComponent(this, lVGraph);
		ResourceManager.addComponent(lHzHeader, txtGraph);
		ResourceManager.addComponent(lHzHeader, chkShowValue);
		ResourceManager.addComponent(lHzHeader, cmdFresh);
		ResourceManager.addComponent(lHzHeader, cmdPrint);
		ResourceManager.addComponent(lHzHeader, cmdProgress);

		lHzHeader.setComponentAlignment(cmdProgress, Alignment.MIDDLE_CENTER);

		this.setExpandRatio(lHzHeader, 0.5f);
		this.setExpandRatio(lVGraph, 3.5f);
		cmdProgress.setVisible(true);
		cmdProgress.setImmediate(true);

	}

	public static JFreeChartWrapper getSampleChart() {
		final XYSeries series = new XYSeries("Random Data");
		series.add(1.0, 400.2);
		series.add(5.0, 294.1);
		series.add(4.0, 100.0);
		series.add(12.5, 734.4);
		series.add(17.3, 453.2);
		series.add(21.2, 500.2);
		series.add(21.9, null);
		series.add(25.6, 734.4);
		series.add(30.0, 453.2);

		final XYSeriesCollection dataset = new XYSeriesCollection(series);

		// Generate the graph

		final JFreeChart chart = ChartFactory.createXYBarChart(
				"XY Series Demo", "X", false, "Y", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		final IntervalMarker target = new IntervalMarker(400.0, 700.0);
		target.setLabel("Target Range");
		target.setLabelFont(new Font("SansSerif", Font.ITALIC, 9));
		target.setLabelAnchor(RectangleAnchor.LEFT);
		target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
		target.setPaint(new Color(222, 222, 255, 128));
		plot.addRangeMarker(target, Layer.BACKGROUND);
		JFreeChartWrapper wrapper = new JFreeChartWrapper(chart);
		// ///////////////////////////////////////////
		/*
		 * VerticalLayout v = new VerticalLayout(); this.addComponent(v);
		 * v.addComponent(wrapper); v.setWidth("100%");
		 */
		wrapper.setSizeFull();
		return wrapper;
	}

	public void view_graph() {
		QueryViewGraphs qvg = this;
		qvg.lVGraph.removeAllComponents();
		qe.setCon(con);
		try {
			if (qvg.sqlString.isEmpty()) {
				return;
			}
			qe.setSqlStr(qvg.sqlString);
			if (qvg.qv != null) {
				rn = Integer.valueOf(utils.nvl((Integer) qvg.qv.getTable()
						.getValue(), Integer.valueOf(-1)));
				fillParmeterList();
			}
			ResultSet rs = qe.executeRS();
			if (rs == null) {
				qe.close();
				return;
			}
			rs.beforeFirst();

			dataset.clear();
			synchronized (qvg.getApplication()) {
				qvg.cmdProgress.setPollingInterval(500);
				qvg.cmdProgress.setValue(0);
				qvg.cmdProgress.setVisible(true);
			}

			while (rs.next()) {
				String ctg = rs.getString(qvg.field_ctg);
				String row = rs.getString(qvg.field_row);
				BigDecimal dc = rs.getBigDecimal(qvg.field_value);
				dataset.addValue(dc, row, ctg);
			}
			qe.close();
			JFreeChart chart = ChartFactory.createBarChart(qvg.txtGraph
					.getValue().toString(), // chart
					// title
					qvg.field_row, // domain axis label
					qvg.field_value, // range axis label
					dataset, // data
					PlotOrientation.VERTICAL, // orientation
					true, // include legend
					qvg.chkShowValue.booleanValue(), // tooltips?
					false // URLs?
					);
			JFreeChartWrapper wrapper = new JFreeChartWrapper(chart);
			wrapper.setImmediate(true);

			wrapper.setSizeFull();
			qvg.lVGraph.addComponent(wrapper);
			qvg.lVGraph.requestRepaintAll();
			wrapper.requestRepaint();
			wrapper.setVisible(true);
			qvg.cmdProgress.setValue(1);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void fillParmeterList() throws SQLException {
		if (qv == null) {
			return;
		}

		for (int i = 0; i < qv.listShowParams.size(); i++) {
			qe.setParaValue(qv.listShowParams.get(i).getName(),
					qv.listShowParams.get(i).getValue());
		}

		if (rn < 0) {
			return;
		}

		for (int i = 0; i < qv.getLctb().getQrycols().size(); i++) {
			String colname = qv.getLctb().getQrycols().get(i).getColname();
			qe.setParaValue(colname, qv.getLctb().getFieldValue(rn, colname));
		}
	}

	public void printGrpah() throws Exception {
		final String fl1 = ((WebApplicationContext) Channelplus3Application
				.getInstance().getContext()).getHttpSession()
				.getServletContext().getRealPath("/WEB-INF")
				+ "/tmpGraph";
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(fl1 + ".pdf"));
		DefaultFontMapper mapper = new DefaultFontMapper();
		// ummm.. problematic?
		mapper.insertDirectory("c:\\windows\\fonts");
		// mapper.insertDirectory("/System/Library/Fonts");
		mapper.insertDirectory("/Library/Fonts");
		// mapper.insertDirectory("/Users/fry/Library/Fonts");

		DefaultFontMapper.BaseFontParameters pp = mapper
				.getBaseFontParameters("Arial");
		if (pp != null) {
			pp.encoding = BaseFont.IDENTITY_H;
			pp.embedded = true;
		}

		writeChartAsPDF(out, chart, 842, 595, mapper);
		out.close();
		StreamResource.StreamSource source = new StreamResource.StreamSource() {
			public InputStream getStream() {
				byte[] b = null;

				try {
					File file = new File(fl1 + ".pdf");
					FileInputStream fin = new FileInputStream(file);
					b = new byte[(int) file.length()];
					fin.read(b);
				} catch (Exception ex) {
					Logger.getLogger(utilsVaadin.class.getName()).log(
							Level.SEVERE, null, ex);
					QueryViewGraphs.this.getWindow().showNotification(
							ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);

				}
				return new ByteArrayInputStream(b);
			}
		};

		StreamResource resource = new StreamResource(source, fl1 + ".pdf",
				Channelplus3Application.getInstance());
		resource.setCacheTime(1);
		resource.setMIMEType("application/pdf");
		Channelplus3Application.getInstance().getMainWindow().open(resource,
				"_new");

	}

	public static void writeChartAsPDF(BufferedOutputStream out,
			JFreeChart chart, int width, int height, FontMapper mapper)
			throws IOException {
		Rectangle pagesize = new Rectangle(width, height);
		Document document = new Document(pagesize, 50, 50, 50, 50);
		try {
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.addAuthor("CHANNEL- Application");
			// document.addSubject("Demonstration");
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			PdfTemplate tp = cb.createTemplate(width, height);
			Graphics2D g2 = tp.createGraphics(width, height, mapper);
			Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
			chart.draw(g2, r2D);
			g2.dispose();
			cb.addTemplate(tp, 0, 0);
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		}
		document.close();
	}

	class GraphGenerator extends Thread {
		private QueryViewGraphs qvg = null;
		Thread thisThread = this;
		QueryExe qe = new QueryExe();
		Integer rn = -1;
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		DefaultPieDataset dataset_pie = new DefaultPieDataset();
		private String strTitle = "";

		public GraphGenerator(QueryViewGraphs qvg) {
			super();
			this.qvg = qvg;
			qe.setCon(qvg.con);
		}

		public void fillParmeterList() throws SQLException {
			if (qv == null) {
				return;
			}

			for (int i = 0; i < qv.listShowParams.size(); i++) {
				qe.setParaValue(qv.listShowParams.get(i).getName(),
						qv.listShowParams.get(i).getValue());
			}

			if (rn < 0) {
				return;
			}

			for (int i = 0; i < qv.getLctb().getQrycols().size(); i++) {
				String colname = qv.getLctb().getQrycols().get(i).getColname()
						.toUpperCase();
				qe.setParaValue(colname, qv.getLctb()
						.getFieldValue(rn, colname));
			}
		}

		private JFreeChart createLineChart(CategoryDataset categorydataset) {
			JFreeChart jfreechart = ChartFactory.createLineChart(strTitle,
					qvg.field_row, qvg.field_value, categorydataset,
					PlotOrientation.VERTICAL, true, qvg.chkShowValue
							.booleanValue(), false);
			/*
			 * jfreechart.addSubtitle(new TextTitle("")); TextTitle texttitle =
			 * new TextTitle(""); texttitle.setFont(new Font("SansSerif", 0,
			 * 10)); texttitle.setPosition(RectangleEdge.BOTTOM);
			 * texttitle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			 * jfreechart.addSubtitle(texttitle);
			 */

			jfreechart.getLegend().setItemFont(new Font("SansSerif", 0, 7));
			jfreechart.setBackgroundPaint(Color.white);
			jfreechart.setBorderVisible(true);

			CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
			categoryplot.setBackgroundPaint(Color.white);
			categoryplot.setRangeGridlinesVisible(true);
			categoryplot.setRangeGridlinePaint(Color.gray);

			CategoryAxis domainAxis = categoryplot.getDomainAxis();
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
			domainAxis.setLabelFont(new Font("SansSerif", 0, 7));
			/*
			 * java.net.URL url = (demo.LineChartDemo1.class).getClassLoader()
			 * .getResource("OnBridge11small.png"); if (url != null) { ImageIcon
			 * imageicon = new ImageIcon(url);
			 * jfreechart.setBackgroundImage(imageicon.getImage());
			 * categoryplot.setBackgroundPaint(new Color(0, 0, 0, 0)); }
			 */
			NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
			numberaxis
					.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot
					.getRenderer();
			DecimalFormat decimalformat1 = new DecimalFormat(
					Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
			lineandshaperenderer
					.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
							"{2}", decimalformat1));
			lineandshaperenderer.setDrawOutlines(true);
			lineandshaperenderer.setUseFillPaint(true);
			lineandshaperenderer.setBaseItemLabelsVisible(qvg.chkShowValue
					.booleanValue());
			lineandshaperenderer.setBaseItemLabelFont(new Font("SansSerif", 0,
					7));

			for (int i = 0; i < 20; i++) {
				lineandshaperenderer.setSeriesShapesVisible(i, true);
				lineandshaperenderer.setBaseFillPaint(Color.white);
				lineandshaperenderer.setSeriesStroke(i, new BasicStroke(3F));
				lineandshaperenderer.setSeriesItemLabelsVisible(i,
						qvg.chkShowValue.booleanValue());
				lineandshaperenderer.setSeriesOutlineStroke(i, new BasicStroke(
						2.0F));
				lineandshaperenderer.setSeriesShape(i,
						new java.awt.geom.Ellipse2D.Double(-3D, -3D, 3D, 3D));
				lineandshaperenderer.setSeriesPositiveItemLabelPosition(i,
						new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
								TextAnchor.BASELINE_LEFT));
			}
			return jfreechart;
		}

		private JFreeChart createBarChart(CategoryDataset categorydataset) {
			JFreeChart chart = ChartFactory.createBarChart(strTitle, // chart
					// title
					qvg.field_row, // domain axis label
					qvg.field_value, // range axis label
					categorydataset, // data
					PlotOrientation.VERTICAL, // orientation
					true, // include legend
					qvg.chkShowValue.booleanValue(), // tooltips?
					false // URLs?
					);

			chart.getLegend().setItemFont(new Font("SansSerif", 0, 7));
			chart.setBackgroundPaint(Color.white);
			CategoryPlot p = chart.getCategoryPlot();
			p.setBackgroundPaint(Color.white);
			p.setRangeGridlinePaint(Color.gray);
			NumberAxis rangeAxis = (NumberAxis) p.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			BarRenderer renderer = (BarRenderer) p.getRenderer();
			DecimalFormat decimalformat1 = new DecimalFormat(
					Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY);
			renderer
					.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
							"{2}", decimalformat1));
			renderer.setBaseItemLabelsVisible(qvg.chkShowValue.booleanValue());
			renderer.setBaseItemLabelFont(new Font("SansSerif", 0, 7));
			chart.getCategoryPlot().setRenderer(renderer);

			CategoryAxis domainAxis = p.getDomainAxis();
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
			domainAxis.setLabelFont(new Font("SansSerif", 0, 7));

			return chart;
		}

		private JFreeChart createPieChart(PieDataset categorydataset) {
			return ChartFactory.createPieChart3D(strTitle, categorydataset,
					true, qvg.chkShowValue.booleanValue(), false);
		}

		@Override
		public void run() {
			if (thisThread != qvg.gg) {
				return;
			}
			try {
				super.run();
				boolean built = false;
				synchronized (QueryViewGraphs.this.getApplication()) {
					qvg.lVGraph.removeAllComponents();
				}

				if (qvg.sqlString.isEmpty()) {
					return;
				}
				qe.setSqlStr(qvg.sqlString);
				rn = -1;
				if (qvg.qv != null) {

					rn = ((qvg.qv.getTable().getValue() instanceof Integer) ? Integer
							.valueOf(utils.nvl((Integer) qvg.qv.getTable()
									.getValue(), Integer.valueOf(-1)))
							: -1);
					fillParmeterList();
				}
				strTitle = "";
				if (txtGraph.getValue() != null) {
					strTitle = qvg.field_descr;
					strTitle = replaceWithParameters(strTitle, qe.getMapParas());
				}

				ResultSet rs = qe.executeRS();
				if (rs == null) {
					qe.close();
					return;
				}
				rs.beforeFirst();

				synchronized (QueryViewGraphs.this.getApplication()) {
					qvg.cmdProgress.setPollingInterval(1000);
					qvg.cmdProgress.setValue(0);
				}

				// if not pie set
				if (!qvg.graphViewType.equals(QueryViewGraphs.GRAPH_PIE)) {
					dataset.clear();
					while (this.thisThread == qvg.gg && built == false
							&& rs.next()) {
						String ctg = rs.getString(qvg.field_ctg);
						String row = rs.getString(qvg.field_row);
						BigDecimal dc = rs.getBigDecimal(qvg.field_value);
						if (dc == null) {
							dc = BigDecimal.valueOf(0.0);
						}
						if (ctg == null || ctg.isEmpty()) {
							ctg = "<NONE>";
						}
						if (row == null || row.isEmpty()) {
							row = "<NONE>";
						}
						dataset.addValue(dc, row, ctg);
					}
				} else {
					// if pie set then
					dataset_pie.clear();
					while (this.thisThread == qvg.gg && built == false
							&& rs.next()) {
						String ctg = rs.getString(qvg.field_ctg);
						BigDecimal dc = rs.getBigDecimal(qvg.field_value);

						if (dc == null) {
							dc = BigDecimal.valueOf(0.0);
						}
						if (ctg == null || ctg.isEmpty()) {
							ctg = "<NONE>";
						}
						dataset_pie.setValue(ctg, dc);
					}
				}
				qe.close();
				if (this.thisThread != qvg.gg) {
					return;
				}
				JFreeChart chart = null;
				if (qvg.graphViewType.equals(QueryViewGraphs.GRAPH_BAR)) {
					chart = createBarChart(dataset);
				}
				if (qvg.graphViewType.equals(QueryViewGraphs.GRAPH_LINE)) {
					chart = createLineChart(dataset);
				}

				if (qvg.graphViewType.equals(QueryViewGraphs.GRAPH_PIE)) {
					chart = createPieChart(dataset_pie);
				}

				JFreeChartWrapper wrapper = new JFreeChartWrapper(chart);
				// wrapper.setImmediate(true);

				synchronized (QueryViewGraphs.this.getApplication()) {
					wrapper.setSizeFull();
					qvg.lVGraph.addComponent(wrapper);
					wrapper.setVisible(true);
					qvg.cmdProgress.setValue(1);
					qvg.chart = chart;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public String replaceWithParameters(String str,
				Map<String, Parameter> mapVars) {
			String pnm = utils.getParamName(str, 1).toUpperCase();
			String s = str;
			int cnt = 1;
			while (pnm.length() > 0) {
				String vl = "";
				if (mapVars.get(pnm) != null) {
					vl = mapVars.get(pnm).getValue().toString();

					if (mapVars.get(pnm).getValueType().equals(
							Parameter.DATA_TYPE_DATE)) {
						vl = (new SimpleDateFormat(utils.FORMAT_SHORT_DATE))
								.format(mapVars.get(pnm).getValue());
					}
					if (mapVars.get(pnm) != null
							&& mapVars.get(pnm).getValue() != null) {
						s = s.replace(":" + pnm.toUpperCase(), vl);
					}
				}
				cnt++;
				pnm = utils.getParamName(str, cnt);
			}
			return s;
		}
	}

	class QueryGenerator extends Thread {
		private frmQuickRep rep = new frmQuickRep();
		private QueryViewGraphs qvg = null;
		Thread thisThread = this;
		QueryExe qe = new QueryExe();

		@Override
		public void run() {
			if (thisThread != qvg.gg) {
				return;
			}
			try {
				super.run();
			} catch (Exception ex) {

			}
		}
	}
}