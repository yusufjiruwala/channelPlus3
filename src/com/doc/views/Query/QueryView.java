package com.doc.views.Query;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DJChartBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.chart.DJChart;
import ar.com.fdvs.dj.domain.chart.DJChartOptions;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;

import com.doc.views.TableView;
import com.doc.views.TableView.checkDefaultselectionListner;
import com.generic.ColumnProperty;
import com.generic.DBClass;
import com.generic.DataFilter;
import com.generic.DynamicReportSetting;
import com.generic.Parameter;
import com.generic.ResourceManager;
import com.generic.Row;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.menuItem;
import com.generic.qryColumn;
import com.generic.utils;
import com.generic.utilsExe;
import com.generic.utilsVaadin;
import com.generic.utilsVaadin.FilterListner;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;

public class QueryView extends VerticalLayout {
	// data structure
	private String sqlquery = "";
	private String graphID = "";
	private DBClass dbc = null;
	private localTableModel data = new localTableModel();
	private PreparedStatement ps_data = null;
	private ResultSet rs_data = null;
	Map<String, Parameter> mapParameters = new HashMap<String, Parameter>();
	List<Parameter> listShowParams = new ArrayList<Parameter>();
	private String beforeQueryExecSql = "";
	private List<String> listHideCols = new ArrayList<String>();
	private List<String> listGroupsBy = new ArrayList<String>();
	private List<String> listGroupSum = new ArrayList<String>();
	private List<Button> listUserButtons = new ArrayList<Button>();
	private HorizontalSplitPanel panelSplit = new HorizontalSplitPanel();
	private VerticalSplitPanel panelGraphSplit = new VerticalSplitPanel();
	
	Panel menuContainer = new Panel(Channelplus3Application.getInstance()
			.getFrmUserLogin().CURRENT_MENU_NAME);

	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout treeLayout = new VerticalLayout();
	private HorizontalLayout treeFindlayout = new HorizontalLayout();
	private VerticalLayout WndgraphLayout = new VerticalLayout();
	private Window graphWnd = new Window("Graph");

	private Window graphPanelWnd = new Window();
	private VerticalLayout treeCompLayout = new VerticalLayout();
	private QueryViewGraphs graphLayout = null;

	private TextField txtFindTree = new TextField("");

	private Button cmdFindTree = new NativeButton();

	public List<String> listCtRowsCol = new ArrayList<String>();
	public List<String> listCtHeaderCol = new ArrayList<String>();

	public String CtValueCol = "";
	public String CtValueColTotTitle = "";

	public List<ColumnProperty> listcols = new ArrayList<ColumnProperty>();
	public List<String> listTreeFields = new ArrayList<String>();

	public boolean isCt = false;
	public boolean buildTreeData = false;

	private boolean showSubtotal = true;
	// behavioural setup variables

	private boolean showParameter = true;

	private boolean showGroupHead = false;
	private boolean searchField = true;

	private boolean showCommandPrint = true;
	private boolean showCommandFilter = true;
	private boolean showCommandPrinter = true;
	private boolean showCommandExport = true;
	private boolean showCommandColumns = true;
	private boolean showCommandPanel = true;
	private boolean showSavedParmeter = false;
	private boolean showGraphsPanel = false;

	public localTableModel dataTree = new localTableModel();

	private String rep_des_file_name = "";

	public byte graphType = DJChart.BAR_CHART;
	public int graphHeight = 400;
	public String graphGroupCol = "";
	public String graphSumCol = "";
	public String parentCol = "";
	public String codeCol = "";
	public String codeColTitle = "";
	public String parentColTitle = "";

	public String queryCode = "";

	public DynamicReportSetting reportSetting = new DynamicReportSetting();
	// UI
	private GridLayout parameters = new GridLayout();
	private Table table = new Table();
	private Tree tree = new Tree();

	private List<Action> lstActions = new ArrayList<Action>();

	// listeners
	private List<tableDataListner> datalistners = new ArrayList<tableDataListner>();
	private boolean executeOnParameterChage = false;
	public boolean ctSortColumnNames = false;

	public ContextMenu menus = new ContextMenu();
	public int selectedMenuRow = -1;
	public Map<String, ContextMenuData> mapContext = new HashMap<String, ContextMenuData>();
	private ComboBox txtGraphType = new ComboBox();
	private ComboBox txtGraphName = new ComboBox();

	private String html_before = "";
	private String html_after = "";
	private String html_apply_col = "";

	public List<Parameter> listConditionalFormatting = new ArrayList<Parameter>();

	private ValueChangeListener table_value_change = new ValueChangeListener() {

		public void valueChange(ValueChangeEvent event) {
			if (graphLayout != null
					&& showGraphsPanel
					&& Channelplus3Application.getInstance().getMainWindow()
							.getChildWindows().contains(graphPanelWnd)) {
				graphLayout.build_selected_graph();
			}
		}
	};

	public class ContextMenuData {
		public boolean isParent = false;
		public String command_typ = "COMMAND";
		public String code = "";
		public String command = "";
		public String descr = "";
		public ContextMenuItem menu_item = null;
	};

	public void setShowGraphPanel(boolean sh) {		
		this.showGraphsPanel = sh;
	}

	public boolean isShowGraphPanel() {
		return this.showGraphsPanel;
	}

	public void setShowSavedParameter(boolean t) {
		this.showSavedParmeter = t;
	}

	public void setRep_des_file_name(String f) {
		this.rep_des_file_name = f;
	}

	public boolean isShowCommandColumns() {
		return showCommandColumns;
	}

	public void setShowCommandColumns(boolean showCommandColumns) {
		this.showCommandColumns = showCommandColumns;
	}

	public boolean isShowCommandPrint() {
		return showCommandPrint;
	}

	public void setShowCommandPrint(boolean showCommandPrint) {
		this.showCommandPrint = showCommandPrint;
	}

	public boolean isShowCommandFilter() {
		return showCommandFilter;
	}

	public void setShowCommandFilter(boolean showCommandFilter) {
		this.showCommandFilter = showCommandFilter;
	}

	public boolean isShowCommandPrinter() {
		return showCommandPrinter;
	}

	public void setShowCommandPrinter(boolean showCommandPrinter) {
		this.showCommandPrinter = showCommandPrinter;
	}

	public boolean isShowCommandExport() {
		return showCommandExport;
	}

	public void setShowCommandExport(boolean showCommandExport) {
		this.showCommandExport = showCommandExport;
	}

	public boolean isShowCommandPanel() {
		return showCommandPanel;
	}

	public void setShowCommandPanel(boolean showCommandPanel) {
		this.showCommandPanel = showCommandPanel;
	}

	private boolean notificationAfterQuery = false;

	public List<Button> getListUserButtons() {
		return listUserButtons;
	}

	public List<String> getListHideCols() {
		return listHideCols;
	}

	public boolean isSearchField() {
		return searchField;
	}

	public void setSearchField(boolean searchField) {
		this.searchField = searchField;
	}

	// procdures
	public List<String> getListGroupSum() {
		return this.listGroupSum;

	}

	public void setShowGroupHead(boolean showGroupHead) {
		this.showGroupHead = showGroupHead;
	}

	public boolean isShowGroupHead() {
		return this.showGroupHead;
	}

	public List<Action> getLstActions() {
		return lstActions;
	}

	public void setHideCols(String[] hd) {
		listHideCols.clear();
		for (int i = 0; i < hd.length; i++) {
			listHideCols.add(hd[i]);
		}
	}

	public boolean isShowSubtotal() {
		return showSubtotal;
	}

	public void setShowSubtotal(boolean showSubtotal) {
		this.showSubtotal = showSubtotal;
	}

	public List<String> getListGroupsBy() {
		return listGroupsBy;
	}

	public void setHideCols(List<String> hd) {
		listHideCols.clear();
		listHideCols.addAll(hd);
	}

	public List<tableDataListner> getDataListners() {
		return datalistners;
	}

	public String getBeforeQueryExecSql() {
		return beforeQueryExecSql;
	}

	public void setBeforeQueryExecSql(String beforeQueryExecSql) {
		this.beforeQueryExecSql = beforeQueryExecSql;
	}

	public List<Parameter> getListShowParams() {
		return listShowParams;
	}

	public Table getTable() {
		return this.table;
	}

	public Map<String, Parameter> getMapParameters() {
		return mapParameters;
	}

	public String getSqlquery() {
		return sqlquery;
	}

	public void setSqlquery(String sqlquery) {
		this.sqlquery = sqlquery;
	}

	public DBClass getDbc() {
		return dbc;
	}

	public void createDBClassFromConnection(Connection c) throws SQLException {
		this.dbc = new DBClass(c);
	}

	public localTableModel getLctb() {
		return data;
	}

	public void setLctb(localTableModel lctb) {
		this.data = lctb;
	}

	public boolean isShowParameter() {
		return showParameter;
	}

	public void setShowParameter(boolean showParameter) {
		this.showParameter = showParameter;

	}

	public QueryView() {
		super();
		tree.addListener(tvc);
	}

	public QueryView(Connection c) throws SQLException {
		super();
		this.dbc = new DBClass(c);
	}

	public QueryView(String sqlstr) {
		this.sqlquery = sqlstr;
	}

	public QueryView(Connection c, String sqlstr) throws SQLException {
		this.dbc = new DBClass(c);
		this.sqlquery = sqlstr;
	}

	public void fetchData() throws SQLException {
		fetchData(null);
	}

	public void fetchData(List<Parameter> pms) throws SQLException {
		String fltstr = "";

		if (sqlquery.length() <= 0) {
			return;
		}
		if (pms != null && pms.size() > 0) {
			DataFilter df = new DataFilter(data);
			df.buildFilterString(pms, "&&");
			fltstr = df.filterStr.trim();
		}
		String s = utils.replaceParameters(sqlquery);
		if (ps_data != null) {
			ps_data.close();
		}

		if (beforeQueryExecSql.length() > 0) {
			PreparedStatement ps = dbc.getDbConnection().prepareStatement(
					utils.replaceParameters(beforeQueryExecSql));
			utils.setParams(beforeQueryExecSql, ps, mapParameters);
			ps.executeUpdate();
			ps.close();
		}

		// datalistner: beforeQuery()
		if (datalistners.size() > 0) {
			for (int i = 0; i < datalistners.size(); i++) {
				datalistners.get(i).beforeQuery();
			}
		}

		ps_data = dbc.getDbConnection().prepareStatement(s,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		utils.setParams(sqlquery, ps_data, mapParameters);
		rs_data = ps_data.executeQuery();

		data.clearALl();
		if (!isCt) {
			data.setCols(rs_data.getMetaData().getColumnCount());
			data.getQrycols().addAll(utils.getColumnsList(rs_data));
			data.getVisbleQrycols().addAll(data.getQrycols());
			data.appendRows(utils.convertRows(ps_data, rs_data, fltstr));
			ps_data.close();
		} else {
			try {
				do_cross_tab_query(rs_data, listcols);
				ps_data.close();
				if (fltstr != null && fltstr.length() > 0) {
					data.setAdvanceColumnsFilter(fltstr);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		data.setMasterRowsAsRows();
		data.sethideCols(listHideCols);
		for (Iterator iterator = data.getQrycols().iterator(); iterator
				.hasNext();) {
			qryColumn qc = (qryColumn) iterator.next();
			qc.setColClass(Label.class);
		}

		if (datalistners.size() > 0) {
			for (int i = 0; i < datalistners.size(); i++) {
				datalistners.get(i).afterQuery();
			}
		}
	}

	public void createView() throws SQLException {
		createView(true);
	}

	public void createView(boolean fetchdata) throws SQLException {
		if (fetchdata) {
			fetchData();
		}
		mainLayout.setSizeFull();
		treeLayout.setSizeFull();
		tree.setImmediate(true);
		removeAllComponents();
		mainLayout.removeAllComponents();
		treeLayout.removeAllComponents();
		treeFindlayout.removeAllComponents();
		treeCompLayout.removeAllComponents();
		menuContainer.removeAllComponents();

		fill_table();

		if (showParameter) {
			showParams();
			ResourceManager.addComponent(mainLayout, parameters);
			// setExpandRatio(parameters, 0.3f);
		}
		HorizontalLayout buttons = new HorizontalLayout();
		HorizontalLayout search = new HorizontalLayout();
		HorizontalLayout hl0 = new HorizontalLayout();
		ResourceManager.addComponent(mainLayout, hl0);
		hl0.setStyleName("toolpanel");
		if (showCommandPanel) {
			ResourceManager.addComponent(hl0, buttons);
			ResourceManager.addComponent(hl0, search);

			hl0.setExpandRatio(buttons, 2f);
			hl0.setExpandRatio(search, 2f);
			hl0.setWidth("100%");
			mainLayout.setExpandRatio(hl0, 0.2f);
			search.setWidth("100%");

		}

		addButtons(buttons);
		addSearch(search);

		ResourceManager.addComponent(mainLayout, table);
		table.setSizeFull();
		table.setSelectable(true);
		table.setSortDisabled(true);
		table.setFooterVisible(true);
		table.setImmediate(true);
		table.removeListener(table_value_change);
		table.addListener(table_value_change);
		graphPanelWnd.setName("graphwnd");
		hl0.setHeight("100%");
		parameters.setHeight("-1px");
		mainLayout.setExpandRatio(table, 3.8f);
		addTree();
		addGraphPanel();

	}

	private Property.ValueChangeListener tvc = new Property.ValueChangeListener() {

		public void valueChange(ValueChangeEvent event) {
			try {
				if (tree.getValue() == null) {
					data.getRows().clear();
					data.getRows().addAll(data.getMasterRows());
					fill_table();
				} else {
					menuItem mn = (menuItem) tree.getValue();
					if (mn.getPara1Val() != null) {
						String flt = parentCol + "="
								+ mn.getPara1Val().toString() + " && "
								+ codeCol + "=" + mn.getPara2Val().toString();
						data.setAdvanceColumnsFilter(flt);
						fill_table();
					} else {
						String flt = parentCol + "=" + mn.getId();
						data.setAdvanceColumnsFilter(flt);
						fill_table();
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	};

	public void addTree() {
		if (!buildTreeData
				|| (parentCol.length() == 0 || codeCol.length() == 0)
				|| ResourceManager.isRTL()) {
			ResourceManager.addComponent(this, mainLayout);
			return;
		}
		panelSplit.setSizeFull();
		if (!ResourceManager.isRTL()) {
			panelSplit.setFirstComponent(mainLayout);
			panelSplit.setSecondComponent(treeLayout);
			panelSplit.setSplitPosition(90);
		} else {
			panelSplit.setFirstComponent(mainLayout);
			panelSplit.setSecondComponent(treeLayout);
			panelSplit.setSplitPosition(90);

		}
		treeFindlayout.setSizeFull();
		treeCompLayout.setSizeFull();
		txtFindTree.setSizeFull();
		cmdFindTree.setSizeFull();

		cmdFindTree.setIcon(new ThemeResource("img/find.png"));
		cmdFindTree.setStyleName("toolbar");

		menuContainer.addStyleName("menucontainer");
		menuContainer.addStyleName("light"); // No border
		menuContainer.setWidth("100%"); // Undefined width
		menuContainer.setHeight("100%");
		menuContainer.getContent().setWidth("-1px"); // Undefined width

		ResourceManager.addComponent(this, panelSplit);
		ResourceManager.addComponent(treeLayout, treeFindlayout);
		ResourceManager.addComponent(treeLayout, treeCompLayout);
		ResourceManager.addComponent(treeCompLayout, menuContainer);
		ResourceManager.addComponent(menuContainer.getContent(), tree);
		ResourceManager.addComponent(treeFindlayout, txtFindTree);
		ResourceManager.addComponent(treeFindlayout, cmdFindTree);

		treeFindlayout.setExpandRatio(txtFindTree, 3f);
		treeFindlayout.setExpandRatio(cmdFindTree, 1f);
		treeLayout.setExpandRatio(treeFindlayout, .25f);
		treeLayout.setExpandRatio(treeCompLayout, 3.75f);

		tree.setSelectable(true);
		tree.setNullSelectionAllowed(true);
		tree.setSizeUndefined();

		if (ResourceManager.isRTL()) {
			this.removeStyleName("rtl");
			mainLayout.removeStyleName("rtl");
			treeLayout.removeStyleName("rtl");
			treeCompLayout.removeStyleName("rtl");
			menuContainer.getContent().removeStyleName("rtl");
			treeFindlayout.removeStyleName("rtl");
			panelSplit.removeStyleName("rtl");
			menuContainer.removeStyleName("rtl");

		}
		FillTree ft = new FillTree();
		ft.start();

	}

	public void addGraphPanel() {
		addGraphPanel("");
	}

	public void addGraphPanel(String initStr) {
		if (showGraphsPanel) {
			if (!Channelplus3Application.getInstance().getMainWindow()
					.getChildWindows().contains(graphPanelWnd)) {
				Channelplus3Application.getInstance().getMainWindow()
						.addWindow(graphPanelWnd);

			}
			if (graphLayout == null) {
				graphPanelWnd.setHeight("300px");
				graphPanelWnd.setWidth("60%");
				WebApplicationContext ctx = (WebApplicationContext) getApplication()
						.getContext();
				int height = ctx.getBrowser().getScreenHeight();
				graphPanelWnd.setPositionY(height - 500);
				graphPanelWnd.setPositionX(250);
				graphLayout = new QueryViewGraphs(this);
				graphPanelWnd.setContent(graphLayout);
				graphPanelWnd.addStyleName("light");
				graphLayout.initAllGraphs(queryCode, initStr);
				graphLayout.setSizeFull();
				graphLayout.requestRepaintAll();
			} else {
				if (!initStr.isEmpty()) {
					graphLayout.graphCode = initStr;
					graphLayout.initAllGraphs(queryCode, initStr);
				} else {
					graphLayout.build_selected_graph();
				}
			}

		}
	}

	public void addSearch(AbstractLayout hl) {
		if (searchField) {
			final TextField txtsearch = new TextField();
			final Button but = new NativeButton();
			but.setStyleName("toolbar");
			but.setIcon(new ThemeResource("img/find.png"));
			ResourceManager.addComponent(hl, txtsearch);
			ResourceManager.addComponent(hl, but);
			txtsearch.setWidth("100%");
			but.setWidth("100%");
			txtsearch.setImmediate(true);
			((HorizontalLayout) hl).setExpandRatio(but, 0.3f);
			((HorizontalLayout) hl).setExpandRatio(txtsearch, 3.7f);
			((HorizontalLayout) hl).setComponentAlignment(txtsearch,
					Alignment.TOP_LEFT);
			((HorizontalLayout) hl).setComponentAlignment(but,
					Alignment.TOP_RIGHT);

			txtsearch.addListener(new FocusListener() {
				public void focus(FocusEvent event) {
					but.setClickShortcut(KeyCode.ENTER);
				}
			});
			txtsearch.addListener(new BlurListener() {

				public void blur(BlurEvent event) {
					but.removeClickShortcut();
				}
			});

			but.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (txtsearch.getValue() == null) {
						return;
					}

					String srch = txtsearch.getValue().toString();
					boolean fnd = false;
					List<Object> mns = new ArrayList<Object>(table.getItemIds());
					List<Object> mns2 = mns;
					if (table.getValue() != null) {
						// && (Integer.valueOf(table.getValue().toString())) >=
						// 0) {
						mns2 = mns.subList(mns.indexOf(table.getValue()) + 1,
								mns.size());
					}
					for (Iterator i = mns2.iterator(); i.hasNext();) {
						Object iid = i.next();
						Item item = table.getItem(iid);
						for (Iterator j = item.getItemPropertyIds().iterator(); j
								.hasNext();) {
							String vlCol = (String) j.next();
							Object vlObj = table.getItem(iid)
									.getItemProperty(vlCol).getValue();
							String val = "";
							if (vlObj instanceof String && vlObj != null) {
								val = vlObj.toString();
							}
							if (vlObj instanceof Label && vlObj != null
									&& ((Label) vlObj).getValue() != null) {
								val = ((Label) vlObj).getValue().toString();
							}
							if (val.toUpperCase().contains(srch.toUpperCase())) {
								table.setValue(iid);
								table.setCurrentPageFirstItemId(iid);
								fnd = true;
								break;
							}
						}
						if (fnd) {
							break;
						}
					}
					if (!fnd && table.getValue() != null) {
						// && (Integer.valueOf(table.getValue().toString())) >=
						// 0) {
						mns2 = mns.subList(0, mns.indexOf(table.getValue()) - 1);
						for (Iterator i = mns2.iterator(); i.hasNext();) {
							Object iid = i.next();
							Item item = table.getItem(iid);
							for (Iterator j = item.getItemPropertyIds()
									.iterator(); j.hasNext();) {
								String vlCol = (String) j.next();
								Object vlObj = table.getItem(iid)
										.getItemProperty(vlCol).getValue();
								String val = "";
								if (vlObj instanceof String && vlObj != null) {
									val = vlObj.toString();
								}
								if (vlObj instanceof Label && vlObj != null
										&& ((Label) vlObj).getValue() != null) {
									val = ((Label) vlObj).getValue().toString();
								}
								if (val.toUpperCase().contains(
										srch.toUpperCase())) {
									table.setValue(iid);
									table.setCurrentPageFirstItemId(iid);
									fnd = true;
									break;
								}
							}
							if (fnd) {
								break;
							}
						}
					}
				}

			});
		}

	}

	public void addButtons(AbstractLayout hl) {
		hl.addComponent(menus);
		Button bc = new NativeButton();
		bc.setIcon(new ThemeResource("img/details.png"));
		bc.setDescription("Show query..");
		bc.setStyleName("toolbar");
		ResourceManager.addComponent(hl, bc);
		bc.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					updateParameterData();
					fetchData();
					fill_table();
					table.requestRepaintAll();
				} catch (SQLException e) {
					Logger.getLogger(QueryView.class.getName()).log(
							Level.SEVERE, null, e + " \n\n\n\n" + sqlquery);
					getWindow().showNotification("Error loading query:",
							e.getMessage() + sqlquery,
							Notification.TYPE_HUMANIZED_MESSAGE);

				}

			}
		});
		// columns
		bc = new NativeButton();
		bc.setIcon(new ThemeResource("img/columns.png"));
		bc.setStyleName("toolbar");
		bc.setDescription("Show/Hide Columns..");
		bc.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				VerticalLayout listlayout = new VerticalLayout();
				Window wndList = new Window("Select Columns here");
				wndList.setWidth("70%");
				wndList.setHeight("70%");
				wndList.setImmediate(true);
				wndList.setModal(true);
				wndList.setContent(listlayout);
				listlayout.setSizeFull();
				listlayout.removeAllComponents();
				TableView tbl = new TableView();
				tbl.setCheckDefaultSelection(false);
				tbl.setCheckSelectionField(true);
				tbl.setListnerDefaultSelection(new checkDefaultselectionListner() {
					public void onInitialize(List<Boolean> lst) {
						for (int i = 0; i < data.getVisbleQrycols().size(); i++) {
							lst.set(data.getVisbleQrycols().get(i).getColpos(),
									(Boolean) true);

						}
					}
				});

				tbl.addColumn("Column");
				for (int i = 0; i < data.getQrycols().size(); i++) {
					qryColumn qc = data.getQrycols().get(i);
					Row r = new Row(1);
					r.lst.get(0).setValue(qc.getTitle(), qc.getColname());
					tbl.addRow(r);
				}

				final TableView tblfinal = tbl;
				tbl.setParentPanel(listlayout);

				wndList.addListener(new CloseListener() {
					List<String> hd = new ArrayList<String>();

					public void windowClose(CloseEvent e) {
						for (int i = 0; i < data.getQrycols().size(); i++) {
							if (!tblfinal.getLstCheckSelection().get(i)
									.booleanValue()) {
								hd.add(data.getQrycols().get(i).getColname());
							}
						}
						setHideCols(hd);
						try {
							table.removeAllItems();
							table = new Table();
							createView();
							requestRepaintAll();
						} catch (Exception e1) {
							Logger.getLogger(QueryView.class.getName()).log(
									Level.SEVERE, null, e1);
							getWindow().showNotification(
									"Error loading query:", e1.getMessage(),
									Notification.TYPE_HUMANIZED_MESSAGE);

						}
					}
				});
				tbl.createView();
				if (!Channelplus3Application.getInstance().getMainWindow()
						.getChildWindows().contains(wndList)) {
					Channelplus3Application.getInstance().getMainWindow()
							.addWindow(wndList);
				}
			}
		});
		ResourceManager.addComponent(hl, bc);
		// filter
		bc = new NativeButton();
		bc.setIcon(new ThemeResource("img/filter.png"));
		bc.setStyleName("toolbar");
		bc.setDescription("filter data by each filed");

		bc.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				final List<Parameter> pmlist = utils.convertFromColumns(data
						.getQrycols());
				VerticalLayout listlayout = new VerticalLayout();
				final Window wndList = new Window("Filter");
				wndList.setWidth("30%");
				wndList.setHeight("70%");
				wndList.setImmediate(true);
				wndList.setModal(true);
				wndList.setContent(listlayout);
				listlayout.setSizeFull();
				FilterListner flt = new FilterListner() {

					public void doFilter() {
						data.setColumnsFilter(pmlist);
						fill_table();
						table.requestRepaintAll();
						Channelplus3Application.getInstance().getMainWindow()
								.removeWindow(wndList);
					}

					public void cancelFilter() {
						data.getRows().clear();
						data.getRows().addAll(data.getMasterRows());
						fill_table();
						Channelplus3Application.getInstance().getMainWindow()
								.removeWindow(wndList);
					}
				};
				utilsVaadin.showFilter(listlayout, pmlist, flt);
				if (!Channelplus3Application.getInstance().getMainWindow()
						.getChildWindows().contains(wndList)) {
					Channelplus3Application.getInstance().getMainWindow()
							.addWindow(wndList);

				}
			}
		});
		ResourceManager.addComponent(hl, bc);
		// print
		bc = new NativeButton();
		bc.setIcon(new ThemeResource("img/pdf.png"));
		bc.setStyleName("toolbar");
		bc.setDescription("save to PDF doc");

		ResourceManager.addComponent(hl, bc);
		bc.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				// utilsVaadin.pdfQuery(qv, dbc.getDbConnection());
				try {
					for (int i = 0; i < data.getVisbleQrycols().size(); i++) {
						int w = table.getColumnWidth(data.getVisbleQrycols()
								.get(i).getTitle());
						if (w >= 0) {
							data.getVisbleQrycols().get(i).setWidth(w);
						}
					}

					printData();
				} catch (Exception ex) {
					Logger.getLogger(QueryView.class.getName()).log(
							Level.SEVERE, null, ex);
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification(ex.toString(),
									Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
		// print
		bc = new NativeButton();
		bc.setIcon(new ThemeResource("img/excel.png"));
		bc.setStyleName("toolbar");
		bc.setDescription("Exporting to excel sheet");

		ResourceManager.addComponent(hl, bc);
		bc.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				// utilsVaadin.pdfQuery(qv, dbc.getDbConnection());
				try {
					exportData();
				} catch (Exception ex) {
					Logger.getLogger(QueryView.class.getName()).log(
							Level.SEVERE, null, ex);
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification(ex.toString(),
									Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

		bc = new NativeButton();
		bc.setIcon(new ThemeResource("img/chart.png"));
		bc.setStyleName("toolbar");
		bc.setDescription("Showing available graphs");

		ResourceManager.addComponent(hl, bc);
		bc.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				showGraphsPanel = true;
				addGraphPanel();
			}

		});

		for (Iterator iterator = listUserButtons.iterator(); iterator.hasNext();) {
			bc = (Button) iterator.next();
			ResourceManager.addComponent(hl, bc);
		}

	}

	public void exportData() throws Exception {

		String fl1 = ((WebApplicationContext) Channelplus3Application
				.getInstance().getContext()).getHttpSession()
				.getServletContext().getRealPath("/WEB-INF")
				+ "/tmp";
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
				return val;
			}
		};

		// --------report columns setiup
		DynamicReportBuilder drb = new DynamicReportBuilder();
		Map<String, AbstractColumn> mapCols = new HashMap<String, AbstractColumn>();
		for (int i = 0; i < data.getVisbleQrycols().size(); i++) {
			String title = data.getVisbleQrycols().get(i).getTitle();
			String name = data.getVisbleQrycols().get(i).getColname();
			Integer width = data.getVisbleQrycols().get(i).getWidth();
			AbstractColumn col1 = ColumnBuilder.getNew()
					.setColumnProperty(name, String.class.getName())
					.setTitle(title).setWidth(width).setFixedWidth(true)
					.build();
			if (data.getVisbleQrycols().get(i).isNumber()) {
				col1 = ColumnBuilder.getNew()
						.setColumnProperty(name, BigDecimal.class.getName())
						.setTitle(title).build();
				if (data.getVisbleQrycols().get(i).getNumberFormat().length() > 0) {
					col1.setPattern(data.getVisbleQrycols().get(i)
							.getNumberFormat());
					col1.setStyle(reportSetting.getStyleNumber());
				}
			}
			if (data.getVisbleQrycols().get(i).getDateFormat().length() > 0) {
				col1 = ColumnBuilder.getNew()
						.setColumnProperty(name, java.sql.Date.class.getName())
						.setTitle(title).build();
				col1.setPattern(data.getVisbleQrycols().get(i).getDateFormat());
			}

			drb.addColumn(col1);
			if (data.getVisbleQrycols().get(i).isNumber()) {
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
								reportSetting.getStyleNumber());
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
		drb.setIgnorePagination(true);
		DynamicReport dr = drb.build();
		DynamicJasperHelper.generateJRXML(dr, new ClassicLayoutManager(),
				parameter, "UTF-8", fl + ".jrxml");
		JasperCompileManager.compileReportToFile(fl + ".jrxml", fl + ".jasper");
		StreamResource.StreamSource source = new StreamResource.StreamSource() {

			public InputStream getStream() {
				Map<String, Object> mpx = new HashMap<String, Object>();
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				try {
					JasperPrint print = JasperFillManager.fillReport(fl
							+ ".jasper", mpx, jr);
					OutputStream ouputStream = new FileOutputStream(new File(fl
							+ ".xls"));

					JRXlsExporter exporterXLS = new JRXlsExporter();
					exporterXLS.setParameter(
							JRXlsExporterParameter.JASPER_PRINT, print);
					exporterXLS.setParameter(
							JRXlsExporterParameter.OUTPUT_STREAM,
							byteArrayOutputStream);
					exporterXLS.setParameter(
							JRXlsExporterParameter.IS_DETECT_CELL_TYPE,
							Boolean.TRUE);
					exporterXLS
							.setParameter(
									JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
									Boolean.TRUE);
					exporterXLS.setParameter(
							JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET,
							10000);

					exporterXLS.exportReport();
					ouputStream.write(byteArrayOutputStream.toByteArray());
					ouputStream.flush();
					ouputStream.close();
				} catch (Exception e) {
					Logger.getLogger(QueryView.class.getName()).log(
							Level.SEVERE, null, e);

				}
				return new ByteArrayInputStream(
						byteArrayOutputStream.toByteArray());
			}

		};
		StreamResource resource = new StreamResource(source, fl + ".xls",
				Channelplus3Application.getInstance());
		resource.setCacheTime(1);
		resource.setMIMEType("application/x-msexcel");
		Channelplus3Application.getInstance().getMainWindow()
				.open(resource, "_new");
	}

	public void printGraph() throws Exception {
		String fl1 = ((WebApplicationContext) Channelplus3Application
				.getInstance().getContext()).getHttpSession()
				.getServletContext().getRealPath("/WEB-INF")
				+ "/tmp";
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
				return val;
			}
		};

		DynamicReportBuilder drb = new DynamicReportBuilder();
		reportSetting.wrapReport(drb);
		AbstractColumn gropCol = null;
		AbstractColumn sumCol = null;
		for (int i = 0; i < 2; i++) {
			int colIn = -1;
			if (i == 0) {
				colIn = data.getVisbleQrycols().indexOf(
						data.getColByName(graphGroupCol));
			} else {
				colIn = data.getVisbleQrycols().indexOf(
						data.getColByName(graphSumCol));
			}

			if (colIn < 0) {
				return;
			}
			String title = data.getVisbleQrycols().get(colIn).getTitle();
			String name = data.getVisbleQrycols().get(colIn).getColname();
			Integer width = data.getVisbleQrycols().get(colIn).getWidth();
			if (width < 0) {
				width = 0;
			}
			AbstractColumn col1 = ColumnBuilder.getNew()
					.setColumnProperty(name, String.class.getName())
					.setTitle(title).setWidth(width).setFixedWidth(true)
					.build();
			if (data.getVisbleQrycols().get(colIn).isNumber()) {
				col1 = ColumnBuilder.getNew()
						.setColumnProperty(name, BigDecimal.class.getName())
						.setTitle(title).build();
				if (data.getVisbleQrycols().get(colIn).getNumberFormat()
						.length() > 0) {
					col1.setPattern(data.getVisbleQrycols().get(colIn)
							.getNumberFormat());
					col1.setStyle(reportSetting.getStyleNumber());
				}
			}
			if (data.getVisbleQrycols().get(colIn).getDateFormat().length() > 0) {
				col1 = ColumnBuilder.getNew()
						.setColumnProperty(name, java.sql.Date.class.getName())
						.setTitle(title).build();
				col1.setPattern(data.getVisbleQrycols().get(colIn)
						.getDateFormat());
			}
			drb.addColumn(col1);
			if (i == 0) {
				gropCol = col1;
			} else {
				sumCol = col1;
			}
		}
		GroupBuilder grp = new GroupBuilder();
		grp.setCriteriaColumn((PropertyColumn) gropCol);
		DJGroup g = grp.build();
		g.setDefaulHeaderVariableStyle(reportSetting.getStyleDetail());
		drb.addGroup(g);
		DJChartBuilder cb = new DJChartBuilder();

		ar.com.fdvs.dj.domain.DJChart chart = cb.setType(graphType)
				.setOperation(DJChart.CALCULATION_SUM).addColumnsGroup(g)
				.addColumn(sumCol).setPosition(DJChartOptions.POSITION_HEADER)
				.setShowLegend(true).setShowLabels(true).setHeight(graphHeight)
				.build();
		drb.addChart(chart);

		drb.setAllowDetailSplit(false);
		drb.setUseFullPageWidth(true);
		drb.setPrintColumnNames(true);
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
				.open(resource, "_new");

	}// printGraph

	public void printData() throws Exception {

		String fl1 = ((WebApplicationContext) Channelplus3Application
				.getInstance().getContext()).getHttpSession()
				.getServletContext().getRealPath("/WEB-INF")
				+ "/tmp";

		// ********************************* if has design file then print
		// designed file.
		if (rep_des_file_name.length() > 0) {
			fl1 = ((WebApplicationContext) Channelplus3Application
					.getInstance().getContext()).getHttpSession()
					.getServletContext().getRealPath("/WEB-INF")
					+ "reports/" + rep_des_file_name;
			Map<String, Object> mapPara = new HashMap<String, Object>();
			for (Iterator iterator = mapParameters.keySet().iterator(); iterator
					.hasNext();) {
				String par = (String) iterator.next();
				mapPara.put(par, mapParameters.get(par).getValue());
			}
			utilsVaadin.showReport(fl1, mapPara, dbc.getDbConnection(),
					Channelplus3Application.getInstance());
			return;

		}
		// *********************************

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

		// --------STYLES
		DynamicReportBuilder drb = new DynamicReportBuilder();
		Map<String, AbstractColumn> mapCols = new HashMap<String, AbstractColumn>();

		for (int i = 0; i < data.getVisbleQrycols().size(); i++) {
			String title = data.getVisbleQrycols().get(i).getTitle();
			String name = data.getVisbleQrycols().get(i).getColname();
			Integer width = data.getVisbleQrycols().get(i).getWidth();
			if (width < 0) {
				width = 0;
			}
			AbstractColumn col1 = ColumnBuilder.getNew()
					.setColumnProperty(name, String.class.getName())
					.setTitle(title).setWidth(width).setFixedWidth(true)
					.build();
			if (data.getVisbleQrycols().get(i).isNumber()) {
				col1 = ColumnBuilder.getNew()
						.setColumnProperty(name, BigDecimal.class.getName())
						.setTitle(title).build();
				if (data.getVisbleQrycols().get(i).getNumberFormat().length() > 0) {
					col1.setPattern(data.getVisbleQrycols().get(i)
							.getNumberFormat());
					col1.setStyle(reportSetting.getStyleNumber());
				}
			}
			if (data.getVisbleQrycols().get(i).getDateFormat().length() > 0) {
				col1 = ColumnBuilder.getNew()
						.setColumnProperty(name, java.sql.Date.class.getName())
						.setTitle(title).build();
				col1.setPattern(data.getVisbleQrycols().get(i).getDateFormat());
			}
			drb.addColumn(col1);
			if (data.getVisbleQrycols().get(i).isNumber()
					&& listGroupSum.contains(data.getVisbleQrycols().get(i)
							.getColname())) {
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

	public void setgroupByRows(Map<String, Object> mp, int rown) {
		mp.clear();
		for (int i = 0; i < listGroupsBy.size(); i++) {
			mp.put(listGroupsBy.get(i),
					data.getFieldValue(rown, listGroupsBy.get(i)));

		}
	}

	public boolean isOldRowDiff(Map<String, Object> mp, int rown) {
		boolean fnd = false;
		for (int i = 0; i < listGroupsBy.size(); i++) {
			Object o = data.getFieldValue(rown, listGroupsBy.get(i));
			if (o == null && mp.get(listGroupsBy.get(i)) != null) {
				return true;
			}
			if (o != null
					&& !o.toString().equals(
							mp.get(listGroupsBy.get(i)).toString())) {
				return true;
			}
		}
		return false;
	}

	public void fill_table() {
		table.removeAllItems();
		table.removeAllActionHandlers();
		for (int i = 0; i < data.getVisbleQrycols().size(); i++) {
			table.addContainerProperty(data.getVisbleQrycols().get(i)
					.getTitle(), data.getVisbleQrycols().get(i).getColClass(),
					null);
		}
		Map<String, Object> mp = new HashMap<String, Object>();
		List<Object> objListx = new ArrayList<Object>();
		float vl = 0;
		int itmgroupsum = -2;
		if (data.getRows().size() > 0) {
			setgroupByRows(mp, 0);
		}
		boolean groupstart = true;
		for (int i = 0; i < data.getRows().size(); i++) {
			html_after = "";
			html_before = "";
			html_apply_col = "";
			if (listConditionalFormatting.size() > 0) {
				applyConditionHTML(i);
			}
			if (listGroupsBy.size() > 0 && datalistners.size() > 0
					&& isOldRowDiff(mp, i)) {
				setgroupByRows(mp, i);

				for (int j2 = 0; j2 < data.getVisbleQrycols().size(); j2++) {
					if (data.getVisbleQrycols().get(j2).isNumber()) {
						vl = 0;
						String stmp = "";
						for (int j = 0; j < datalistners.size(); j++) {
							data.setCursorNo(i - 1);
							stmp = datalistners.get(j).calcSummary(
									listGroupsBy,
									data.getVisbleQrycols().get(j2));
							if (stmp.trim().length() == 0
									&& listGroupSum.size() > 0
									&& utils.upperCaseAllListValue(listGroupSum)
											.indexOf(
													data.getVisbleQrycols()
															.get(j2)
															.getColname()
															.toUpperCase()) > -1) {
								stmp = String.valueOf(data.getSummaryOf(
										listGroupsBy, data.getVisbleQrycols()
												.get(j2).getColname(),
										localTableModel.SUMMARY_SUM));
							}
							if (stmp.length() > 0) {
								vl = vl + Float.valueOf(stmp);
							}

						}
						String strvl = String.valueOf(vl);
						if (data.getVisbleQrycols().get(j2).getNumberFormat() != null
								&& data.getVisbleQrycols().get(j2)
										.getNumberFormat().length() > 0) {
							strvl = new DecimalFormat(data.getVisbleQrycols()
									.get(j2).getNumberFormat()).format(vl);
						}
						if (vl == 0 && stmp.length() == 0) {
							strvl = "";
						}
						objListx.add(String.valueOf(strvl));
					} else {
						objListx.add(null);
					}
				}
				boolean allnull = true;
				for (int j = 0; j < objListx.size(); j++) {
					if (objListx.get(j) != null
							&& objListx.get(j).toString().length() > 0) {
						allnull = false;
						break;
					}
				}
				if (!allnull) {
					table.addItem(objListx.toArray(),
							String.valueOf(itmgroupsum--));

				}
				if (i + 1 < data.getRows().size()) {
					groupstart = true;
				} else {
					groupstart = false;
				}
				objListx.clear();
			} else {
				// for heading
				if (i + 1 > data.getRows().size() && listGroupsBy.size() > 0
						&& isOldRowDiff(mp, i + 1)) {
					groupstart = true;
				} else {
					groupstart = false;
				}
				if (i == 0) {
					groupstart = true;
				}
			}
			// if showgrouphead true and start of group then display heading.
			if (groupstart && showGroupHead && listGroupsBy.size() > 0) {
				List<Object> objListx1 = new ArrayList<Object>();
				for (int j = 0; j < listGroupsBy.size(); j++) {
					objListx1.add(data.getFieldValue(i, listGroupsBy.get(j)));
				}
				for (int j2 = 0; j2 < data.getVisbleQrycols().size()
						- listGroupsBy.size(); j2++) {
					objListx1.add(null);
				}
				table.addItem(objListx1.toArray(),
						String.valueOf(itmgroupsum--));
			}

			Object iid = table.addItem(getVisibleArrayOfRowData(i),
					Integer.valueOf(i));

			// html conditional formating of label which is added recently.
			if (!(html_after + html_before).isEmpty()) {
				Item item = table.getItem(iid);
				for (Iterator j = item.getItemPropertyIds().iterator(); j
						.hasNext();) {
					String vlCol = (String) j.next();
					// check apply column to all or some columns
					if (html_apply_col.isEmpty()
							|| html_apply_col.toUpperCase().contains(
									("\"" + vlCol + "\"").toUpperCase())) {
						Object vlObj = item.getItemProperty(vlCol).getValue();
						if (vlObj instanceof Label) {
							Label ll = (Label) vlObj;
							ll.setContentMode(Label.CONTENT_XHTML);
							ll.setValue(html_before + ll.getValue()
									+ html_after);
						}
					}
				}

			}

		}

		// -----LAST ROW FOR GROUP BY ....
		int ix = data.getRows().size();
		if (listGroupsBy.size() > 0 && datalistners.size() > 0 && ix > 0) {
			for (int j2 = 0; j2 < data.getVisbleQrycols().size(); j2++) {
				if (data.getVisbleQrycols().get(j2).isNumber()) {
					vl = 0;
					String stmp = "";
					for (int j = 0; j < datalistners.size(); j++) {
						data.setCursorNo(ix - 1);
						stmp = utils.nvl(
								datalistners.get(j).calcSummary(listGroupsBy,
										data.getVisbleQrycols().get(j2)), "");
						if (stmp.trim().length() == 0
								&& listGroupSum.size() > 0
								&& utils.upperCaseAllListValue(listGroupSum)
										.indexOf(
												data.getVisbleQrycols().get(j2)
														.getColname()
														.toUpperCase()) > -1) {
							stmp = String.valueOf(data.getSummaryOf(
									listGroupsBy,
									data.getVisbleQrycols().get(j2)
											.getColname(),
									localTableModel.SUMMARY_SUM));
						}
						if (stmp.length() > 0) {
							vl = vl + Float.valueOf(stmp);
						}
					}
					String strvl = String.valueOf(vl);
					if (data.getVisbleQrycols().get(j2).getNumberFormat() != null
							&& data.getVisbleQrycols().get(j2)
									.getNumberFormat().length() > 0) {
						strvl = new DecimalFormat(data.getVisbleQrycols()
								.get(j2).getNumberFormat()).format(vl);
					}
					if (vl == 0 && stmp.length() == 0) {
						strvl = "";
					}
					objListx.add(String.valueOf(strvl));
				} else {
					objListx.add(null);
				}
			}
			boolean allnull = true;
			for (int j = 0; j < objListx.size(); j++) {
				if (objListx.get(j) != null
						&& objListx.get(j).toString().length() > 0) {
					allnull = false;
					break;
				}
			}
			if (!allnull) {
				table.addItem(objListx.toArray(), String.valueOf(itmgroupsum--));
			}
			objListx.clear();
		}
		// setting column width
		// listner cell style generator
		table.setCellStyleGenerator(new CellStyleGenerator() {
			public String getStyle(Object itemId, Object propertyId) {
				String st = "";
				for (int i = 0; i < data.getVisbleQrycols().size(); i++) {
					if (propertyId != null
							&& propertyId.toString().equals(
									data.getVisbleQrycols().get(i).getTitle())) {
						for (int j = 0; j < datalistners.size(); j++) {

							st = datalistners.get(j).getCellStyle(
									data.getVisbleQrycols().get(i),
									Integer.valueOf(itemId.toString()));
							if (Integer.valueOf(itemId.toString()) <= -1
									&& Integer.valueOf(itemId.toString()) >= -1000000
									&& (table.getItem(itemId)
											.getItemProperty(propertyId)
											.getValue() != null && table
											.getItem(itemId)
											.getItemProperty(propertyId)
											.getValue().toString().length() > 0)) {
								st = st + " subGroupStyle";
							}
							if (Integer.valueOf(itemId.toString()) <= -1000000
									&& (table.getItem(itemId)
											.getItemProperty(propertyId)
											.getValue() != null && table
											.getItem(itemId)
											.getItemProperty(propertyId)
											.getValue().toString().length() > 0)) {
								st = utils.nvl(st, "") + " subGroupHeadStyle";
							}

							if (data.getVisbleQrycols().get(i).isNumber()) {
								table.setColumnAlignment(data
										.getVisbleQrycols().get(i).getTitle(),
										Table.ALIGN_RIGHT);
							}

						}
						if (listConditionalFormatting.size() > 0) {
							String ss = applyConditions(Integer.valueOf(itemId
									.toString()));
							st = (utils.nvl(st, "") + " " + ss).trim();
						}
						break;
					}
				}
				return st;
			}
		});

		// listner for summary

		List<Object> objList = new ArrayList<Object>();
		if (datalistners.size() > 0) {
			for (int i = 0; i < data.getVisbleQrycols().size(); i++) {
				vl = 0;
				String stmp = "";
				for (int j = 0; j < datalistners.size(); j++) {
					if (data.getVisbleQrycols().get(i).isNumber()) {
						stmp = datalistners.get(j).calcSummary(null,
								data.getVisbleQrycols().get(i));
						if (stmp.trim().length() == 0
								&& listGroupSum.size() > 0
								&& utils.upperCaseAllListValue(listGroupSum)
										.indexOf(
												data.getVisbleQrycols().get(i)
														.getColname()
														.toUpperCase()) > -1) {
							stmp = String.valueOf(data.getSummaryOf(data
									.getVisbleQrycols().get(i).getColname(),
									localTableModel.SUMMARY_SUM));
						}
						if (stmp.length() > 0) {
							vl = vl + Float.valueOf(stmp);
						}
					}
				}
				if (data.getVisbleQrycols().get(i).isNumber()) {
					objList.add(vl);
					String strvl = String.valueOf(vl);
					if (data.getVisbleQrycols().get(i).getNumberFormat() != null
							&& data.getVisbleQrycols().get(i).getNumberFormat()
									.length() > 0) {
						strvl = new DecimalFormat(data.getVisbleQrycols()
								.get(i).getNumberFormat()).format(vl);
					}
					if (vl == 0 && stmp.length() == 0) {
						strvl = "";
					}
					table.setColumnFooter(data.getVisbleQrycols().get(i)
							.getTitle(), String.valueOf(strvl));
				}
			}
		}
		if (datalistners.size() > 0) {
			for (int i = 0; i < datalistners.size(); i++) {
				datalistners.get(i).afterVisual();
			}
		}
		if (notificationAfterQuery) {
			getWindow().showNotification(
					"Total rows fetched #" + data.getRows().size()
							+ " and added to table # "
							+ table.getItemIds().size());
		}

	}

	@SuppressWarnings("unchecked")
	public String applyConditions(int rowno) {
		String st = "";
		for (int i = 0; i < listConditionalFormatting.size(); i++) {
			Parameter pm = listConditionalFormatting.get(i);
			Map<String, String> mp = (Map<String, String>) pm.getUIObject();

			// assigning values and query column

			qryColumn qc = data.getColByName(pm.getName());
			if (qc == null || pm.getValue() == null
					|| pm.getValue().toString().isEmpty()) {
				continue;
			}
			double valueDblPar = 0;
			double valueDblFld = 0.0;
			String valueStrPar = "";
			String valueStrFld = "";
			if (qc.isNumber()) {
				valueDblPar = Double.parseDouble(pm.getValue().toString());
				valueDblFld = ((BigDecimal) data.getFieldValue(rowno,
						qc.getColname())).doubleValue();
			} else {
				valueStrFld = utils.nvl(
						data.getFieldValue(rowno, qc.getColname()), "");
				valueStrPar = utils.nvl(pm.getValue(), "");
			}

			// operation if "="
			if (pm.operator.equals("=") && qc.isNumber()
					&& valueDblFld == valueDblPar) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

			if (pm.operator.equals("=") && !qc.isNumber() && !qc.isDateTime()
					&& valueStrPar.equals(valueDblPar)) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

			if (pm.operator.equals(">=") && qc.isNumber()
					&& valueDblFld >= valueDblPar) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

			if (pm.operator.equals(">=") && !qc.isNumber() && !qc.isDateTime()
					&& valueStrFld.compareTo(valueStrPar) <= 0) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

			if (pm.operator.equals("<=") && qc.isNumber()
					&& valueDblFld <= valueDblPar) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

			if (pm.operator.equals("<=") && !qc.isNumber() && !qc.isDateTime()
					&& valueStrFld.compareTo(valueStrPar) <= 0) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

			if (pm.operator.equals(">") && qc.isNumber()
					&& valueDblFld > valueDblPar) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

			if (pm.operator.equals(">") && !qc.isNumber() && !qc.isDateTime()
					&& valueStrFld.compareTo(valueStrPar) > 0) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

			if (pm.operator.equals("<") && qc.isNumber()
					&& valueDblFld < valueDblPar) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

			if (pm.operator.equals("<") && !qc.isNumber() && !qc.isDateTime()
					&& valueStrFld.compareTo(valueStrPar) < 0) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

			if (pm.operator.equals("%%")
					&& !qc.isNumber()
					&& !qc.isDateTime()
					&& valueStrFld.toUpperCase().contains(
							valueStrPar.toUpperCase())) {
				st = st + " " + utils.nvl(mp.get("STYLE_NAME"), "") + " ";
			}

		}

		return st;
	}

	public void applyConditionHTML(int rowno) {

		String st = "";
		String ste = "";
		for (int i = 0; i < listConditionalFormatting.size(); i++) {
			Parameter pm = listConditionalFormatting.get(i);
			Map<String, String> mp = (Map<String, String>) pm.getUIObject();

			// assigning values and query column

			qryColumn qc = data.getColByName(pm.getName());
			if (qc == null || pm.getValue() == null
					|| pm.getValue().toString().isEmpty()) {
				continue;
			}
			double valueDblPar = 0;
			double valueDblFld = 0.0;
			String valueStrPar = "";
			String valueStrFld = "";
			if (qc.isNumber()) {
				valueDblPar = Double.parseDouble(pm.getValue().toString());
				valueDblFld = ((BigDecimal) data.getFieldValue(rowno,
						qc.getColname())).doubleValue();
			} else {
				valueStrFld = utils.nvl(
						data.getFieldValue(rowno, qc.getColname()), "");
				valueStrPar = utils.nvl(pm.getValue(), "");
			}
			html_apply_col = utils.nvl(pm.getDefaultValue(), "");

			// operation if "="
			if (pm.operator.equals("=") && qc.isNumber()
					&& valueDblFld == valueDblPar) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

			if (pm.operator.equals("=") && !qc.isNumber() && !qc.isDateTime()
					&& valueStrPar.equals(valueDblPar)) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

			if (pm.operator.equals(">=") && qc.isNumber()
					&& valueDblFld >= valueDblPar) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

			if (pm.operator.equals(">=") && !qc.isNumber() && !qc.isDateTime()
					&& valueStrFld.compareTo(valueStrPar) <= 0) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

			if (pm.operator.equals("<=") && qc.isNumber()
					&& valueDblFld <= valueDblPar) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

			if (pm.operator.equals("<=") && !qc.isNumber() && !qc.isDateTime()
					&& valueStrFld.compareTo(valueStrPar) <= 0) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

			if (pm.operator.equals(">") && qc.isNumber()
					&& valueDblFld > valueDblPar) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

			if (pm.operator.equals(">") && !qc.isNumber() && !qc.isDateTime()
					&& valueStrFld.compareTo(valueStrPar) > 0) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

			if (pm.operator.equals("<") && qc.isNumber()
					&& valueDblFld < valueDblPar) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

			if (pm.operator.equals("<") && !qc.isNumber() && !qc.isDateTime()
					&& valueStrFld.compareTo(valueStrPar) < 0) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

			if (pm.operator.equals("%%")
					&& !qc.isNumber()
					&& !qc.isDateTime()
					&& valueStrFld.toUpperCase().contains(
							valueStrPar.toUpperCase())) {
				st = st + " " + utils.nvl(mp.get("HTML_TAG_START"), "") + " ";
				ste = ste + " " + utils.nvl(mp.get("HTML_TAG_END"), "") + " ";
			}

		}

		html_before = st.trim();
		html_after = ste.trim();

	}

	public void setColumnWidth(String colname, int width) {
		if (getTable() == null
				|| getTable().getContainerPropertyIds().size() == 0) {
			return;
		}
		getTable().setColumnWidth(getLctb().getColByName(colname).getTitle(),
				width);
		data.getColByName(colname).setWidth(width);
	}

	public Object[] getVisibleArrayOfRowData(int i) {
		List<Object> ar = new ArrayList<Object>();
		for (int j = 0; j < data.getVisbleQrycols().size(); j++) {
			ar.add(data.getDisplayAt(i, j));
		}
		return ar.toArray();
	}

	public void resetAllParameters() {
		List<String> lstp = new ArrayList(mapParameters.keySet());
		for (int i = 0; i < lstp.size(); i++) {
			mapParameters.get(lstp.get(i)).getLov().clear();
		}
		mapParameters.clear();
		listShowParams.clear();

	}

	public void resetAll() {

		resetAllParameters();
		listGroupsBy.clear();
		listcols.clear();
		listCtHeaderCol.clear();
		listCtRowsCol.clear();
		listGroupSum.clear();
		listHideCols.clear();
		listTreeFields.clear();
		listUserButtons.clear();
		lstActions.clear();
		datalistners.clear();
		getLctb().clearALl();
		removeAllComponents();
		table.removeAllItems();

		graphPanelWnd.removeAllComponents();
		if (graphLayout != null) {
			graphLayout.removeAllComponents();
		}

		if (Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(graphPanelWnd)) {
			Channelplus3Application.getInstance().getMainWindow()
					.removeWindow(graphPanelWnd);
		}

	}

	private void showParams() {
		parameters.removeAllComponents();

		if (listShowParams.size() <= 0) {
			return;
		}
		parameters.setColumns(3);
		Component c = null;
		ValueChangeListener vlc = new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try {
					updateParameterData();
					fetchData();
					fill_table();
					table.requestRepaintAll();
				} catch (Exception ex) {

				}
			}
		};

		for (int i = 0; i < listShowParams.size(); i++) {
			if (listShowParams.get(i).getLovList().size() > 0) {
				c = new ComboBox(listShowParams.get(i).getDescription());
				for (Iterator<dataCell> it = listShowParams.get(i).getLovList()
						.iterator(); it.hasNext();) {
					dataCell dc = it.next();
					((ComboBox) c).addItem(dc);
				}
				if (executeOnParameterChage) {
					((ComboBox) c).addListener(vlc);
					((ComboBox) c).setImmediate(true);
				}

			} else {
				if (listShowParams.get(i).getValueType()
						.equals(Parameter.DATA_TYPE_DATE)) {
					c = new DateField(listShowParams.get(i).getDescription());
					((DateField) c).setDateFormat(utils.FORMAT_SHORT_DATE);
					((DateField) c).setResolution(DateField.RESOLUTION_DAY);
					if (executeOnParameterChage) {
						((DateField) c).addListener(vlc);
						((DateField) c).setImmediate(true);
					}
				} else {
					c = new TextField(listShowParams.get(i).getDescription());
					if (executeOnParameterChage) {
						((TextField) c).addListener(vlc);
						((TextField) c).setImmediate(true);
					}
				}

			}

			listShowParams.get(i).setUIObject(c);
			ResourceManager.addComponent(parameters, c);
		}
		updateParameterUI();

	}

	public void updateParameterUI() {
		for (int i = 0; i < listShowParams.size(); i++) {
			if (listShowParams.get(i).getUIObject() instanceof ComboBox) {
				ComboBox c = (ComboBox) listShowParams.get(i).getUIObject();
				c.setValue(null);
				if (listShowParams.get(i).getValue() != null) {
					c.setValue(utilsVaadin.findByValue(c, listShowParams.get(i)
							.getValue().toString()));
				}
			}
			if (listShowParams.get(i).getUIObject() instanceof TextField) {
				TextField c = (TextField) listShowParams.get(i).getUIObject();
				c.setValue(null);
				if (listShowParams.get(i).getValue() != null) {
					c.setValue(listShowParams.get(i).getValue().toString());
				}
			}
			if (listShowParams.get(i).getUIObject() instanceof DateField) {
				DateField c = (DateField) listShowParams.get(i).getUIObject();
				c.setValue(null);
				if (listShowParams.get(i).getValue() != null) {
					c.setValue((java.util.Date) listShowParams.get(i)
							.getValue());
				}
			}

		}
	}

	public void updateParameterData() {
		Object strVal = null;
		for (int i = 0; i < listShowParams.size(); i++) {
			if (listShowParams.get(i).getUIObject() instanceof ComboBox) {
				ComboBox c = (ComboBox) listShowParams.get(i).getUIObject();
				listShowParams.get(i).setValue("");
				listShowParams.get(i).setValueDescription("");
				if (c.getValue() != null) {
					listShowParams.get(i).setValue(
							((dataCell) c.getValue()).getValue().toString());
					listShowParams.get(i).setValueDescription(
							((dataCell) c.getValue()).getDisplay().toString());
					strVal = ((dataCell) c.getValue()).getValue().toString();
				}

			}
			if (listShowParams.get(i).getUIObject() instanceof TextField) {
				TextField c = (TextField) listShowParams.get(i).getUIObject();
				listShowParams.get(i).setValue("");
				if (c.getValue() != null) {
					listShowParams.get(i).setValue(c.getValue());
				}
				strVal = c.getValue();
			}
			if (listShowParams.get(i).getUIObject() instanceof DateField) {
				DateField c = (DateField) listShowParams.get(i).getUIObject();
				listShowParams.get(i).setValue(new java.util.Date());
				if (c.getValue() != null) {
					listShowParams.get(i).setValue(c.getValue());
				}
				strVal = c.getValue();
			}
			if (strVal != null && showSavedParmeter) {
				utils.mapParaSave.put(listShowParams.get(i).getName(), strVal);
				strVal = null;
			}

		}
	}

	public void addParameter(Parameter pm) throws SQLException {
		addParameter(pm, true);
	}

	public void addParameter(Parameter pm, boolean showParam)
			throws SQLException {
		mapParameters.put(pm.getName(), pm);
		if (pm.getValueType().equals(Parameter.DATA_TYPE_DATE)) {

		}

		if (showParam) {
			listShowParams.add(pm);
		}
		if (pm.getLovsql().length() > 0) {
			pm.getLovList().clear();
			pm.getLovList()
					.addAll(utils.getListOfValues(pm.getLovsql(),
							dbc.getDbConnection()));
		}
		if (showSavedParmeter) {
			utils.accessSavedParas(pm);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		if (ps_data != null) {
			ps_data.close();
		}
		data.clearALl();
		super.finalize();
	}

	public void doExecuteOnParameterChange(boolean b) {
		executeOnParameterChage = b;
	}

	public void setNotificationAfterQuery(boolean b) {
		this.notificationAfterQuery = b;
	}

	public void do_cross_tab_query(ResultSet rs_data,
			List<ColumnProperty> colProps) throws Exception {
		listGroupSum.clear();
		Map<String, Integer> mapRows = new HashMap<String, Integer>();
		Map<String, Integer> mapCols = new HashMap<String, Integer>();
		List<dataCell> listCols = new ArrayList<dataCell>();
		List<String> listRows = new ArrayList<String>();
		Map<String, Map<String, BigDecimal>> mapValRows = new HashMap<String, Map<String, BigDecimal>>();
		Map<String, Map<String, Object>> mapRowsCol = new HashMap<String, Map<String, Object>>();

		BigDecimal val = null;
		// finding unique values
		rs_data.beforeFirst();
		while (rs_data.next()) {
			String rowString = "";
			String colString = "";
			HashMap<String, Object> mp = new HashMap<String, Object>();
			for (int i = 0; i < listCtRowsCol.size(); i++) {
				rowString = rowString + rs_data.getString(listCtRowsCol.get(i));
				mp.put(listCtRowsCol.get(i),
						rs_data.getObject(listCtRowsCol.get(i)));
			}
			if (!mapRows.containsKey(rowString)) {
				listRows.add(rowString);
				mapRows.put(rowString, listRows.size() - 1);
			}

			mapRowsCol.put(rowString, mp);

			// for (int i = 0; i < listCtHeaderCol.size(); i++) {
			colString = rs_data.getString(listCtHeaderCol.get(1));
			Object col = rs_data.getString(listCtHeaderCol.get(0));
			if (!mapCols.containsKey(col.toString())) {
				listCols.add(new dataCell(colString, col));
				mapCols.put(col.toString(),
						((listCols.size()) + listCtHeaderCol.size()) - 1);
			}

			// }
			// int rn = mapRows.get(rowString);
			// int cn = mapCols.get(colString);
			val = rs_data.getBigDecimal(CtValueCol);

			if (mapValRows.get(rowString) == null) {
				mapValRows.put(rowString, new HashMap<String, BigDecimal>());
				mapValRows.get(rowString).put(colString, BigDecimal.valueOf(0));
			}

			if (mapValRows.get(rowString).get(colString) == null) {
				mapValRows.get(rowString).put(colString, BigDecimal.valueOf(0));
			}

			val = BigDecimal.valueOf(val.doubleValue()
					+ mapValRows.get(rowString).get(colString).doubleValue());
			mapValRows.get(rowString).put(colString, val);

		}

		// formatting columns
		// if (ctSortColumnNames) {
		Collections.sort(listCols, new Comparator<dataCell>() {
			public int compare(dataCell othis, dataCell o2) {
				return utils.comparDataCell(othis, o2);
			}
		});

		// Arrays.sort(listCols.toArray());
		// }
		List<ColumnProperty> tmpProps = new ArrayList<ColumnProperty>();
		ColumnProperty tot = null;
		int max = 0;
		for (int i = 0; i < colProps.size(); i++) {
			if (colProps.get(i).pos > max) {
				max = colProps.get(i).pos;
			}
			if (colProps.get(i).colname.equals(CtValueCol)) {
				continue;
			}
			if (colProps.get(i).equals(listCtHeaderCol.get(0))) {
				continue;
			}

			for (int j = 1; j < listCtHeaderCol.size(); j++) {
				if (listCtHeaderCol.get(j).equals(colProps.get(i).colname)) {
					for (Iterator iterator = listCols.iterator(); iterator
							.hasNext();) {
						String colnm = ((dataCell) iterator.next()).toString();
						ColumnProperty cpn = (ColumnProperty) colProps.get(i)
								.getClone();
						cpn.colname = colnm;
						cpn.descr = colnm;
						cpn.pos = max + 1;
						if (tot == null) {
							tot = (ColumnProperty) cpn.getClone();
						}
						tmpProps.add(cpn);
						max++;
					}

				} else {
					colProps.get(i).pos = max;
					tmpProps.add(colProps.get(i));
					max++;
				}
			}

		}
		if (tot != null) {
			tot.pos = max + 1;
			tot.colname = CtValueColTotTitle;
			tot.descr = CtValueColTotTitle;
			tmpProps.add(tot);
		}

		colProps.clear();
		colProps.addAll(tmpProps);

		// / adding columns
		data.getQrycols().clear();
		for (int i = 0; i < colProps.size(); i++) {

			ColumnProperty cp = colProps.get(i);
			qryColumn q = new qryColumn();
			q.setWidth(cp.display_width);
			q.setColpos(i);
			q.setColname(cp.colname);
			q.setTitle(cp.descr);
			q.setDatabaseCol(true);
			q.setDisplaySize(cp.display_width);
			q.setDatatype(Types.VARCHAR);
			if (cp.data_type.equals("NUMBER") || cp.data_type.equals("FLOAT")) {
				q.setDatatype(Types.NUMERIC);
				q.setAlignmnet(JLabel.RIGHT);
			}
			if (cp.data_type.equals("DATE")) {
				q.setDatatype(Types.TIMESTAMP);
			}
			if (cp.summary.equals("SUM")) {
				listGroupSum.add(cp.colname);
			}
			data.getQrycols().add(q);
		}
		data.getVisbleQrycols().clear();
		data.getVisbleQrycols().addAll(data.getQrycols());
		data.setCols(data.getQrycols().size());

		// fill table
		rs_data.beforeFirst();
		double total = 0.0;
		for (int i = 0; i < listRows.size(); i++) {
			int rowno = data.addRecord();
			String rowString = listRows.get(i);
			for (int j = 0; j < listCtRowsCol.size(); j++) {
				Object colval = mapRowsCol.get(rowString).get(
						listCtRowsCol.get(j));
				data.setFieldValue(rowno, listCtRowsCol.get(j), colval);
			}
			total = 0;
			for (int j = 0; j < listCols.size(); j++) {
				val = mapValRows.get(rowString).get(listCols.get(j).toString());
				data.setFieldValue(rowno, listCols.get(j).toString(), val);
				if (val != null) {
					total = total + val.doubleValue();
				}
			}
			if (CtValueColTotTitle != null && !CtValueColTotTitle.isEmpty())
				data.setFieldValue(rowno, CtValueColTotTitle,
						BigDecimal.valueOf(total));
		}
		data.setMasterRowsAsRows();
	}

	public boolean running_thread_tree = false;

	public class FillTree extends Thread {
		public void run() {
			if (running_thread_tree) {
				return;
			}
			running_thread_tree = true;
			String lastParent = "";
			menuItem root_node = null, lastparentnode = null, ndx = null;
			synchronized (getApplication()) {
				tree.removeAllItems();
			}
			for (int i = 0; i < data.getRows().size(); i++) {
				String current_parent = "";
				String current_code = utils.nvl(data.getFieldValue(i, codeCol),
						"");
				String current_title = current_code;
				if (!codeColTitle.isEmpty()) {
					current_title = utils.nvl(
							data.getFieldValue(i, codeColTitle), "");
				}
				current_parent = utils
						.nvl(data.getFieldValue(i, parentCol), "");
				if (lastParent.equals(current_parent)) {
					root_node = lastparentnode;

				} else {
					root_node = utilsVaadin.findNodeByValue(tree,
							current_parent, null);
					if (root_node == null) {
						root_node = new menuItem(current_parent, current_parent);
						synchronized (getApplication()) {
							tree.addItem(root_node);
							tree.setChildrenAllowed(root_node, true);
						}
					}
					lastParent = current_parent;
					lastparentnode = root_node;
				}

				ndx = utilsVaadin.findNodeByValue(tree, current_parent + "-"
						+ current_code, null);

				if (ndx == null) {
					ndx = new menuItem(current_parent + "-" + current_code,
							current_title);
					ndx.setPara1Val(current_parent);
					ndx.setPara2Val(current_code);
				}
				synchronized (getApplication()) {
					tree.addItem(ndx);
					tree.setChildrenAllowed(ndx, false);
					if (root_node != null) {
						tree.setParent(ndx, root_node);
					}
					tree.collapseItem(root_node);
					tree.removeListener(tvc);
					tree.addListener(tvc);
				}

			}
			running_thread_tree = false;
		}
	}

	public void fill_menus(final String idno, final AbstractLayout pl)
			throws SQLException {
		String gn = utilsVaadin.generateFieldClause(this);
		String sq = "select code,nvl(title,descr) descr,sqlstring,group_title,Command_type from CP_SUB_COMMANDS"
				+ " where query_code_in like '%\"'||'"
				+ idno
				+ "'||'\"%' AND (query_field_in is null or "
				+ gn
				+ ")   order by POSX ";

		String sq2 = "select distinct group_title from CP_SUB_COMMANDS"
				+ " where query_code_in like '%\"'||'" + idno
				+ "'||'\"%' AND (query_field_in is null or " + gn + ") ";

		List<ContextMenuData> lst = new ArrayList<ContextMenuData>(
				mapContext.values());
		for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
			ContextMenuData contextMenuData = (ContextMenuData) iterator.next();
			menus.removeItem(contextMenuData.menu_item);

		}

		mapContext.clear();

		ResultSet rst = utils.getSqlRS(sq2, dbc.getDbConnection());
		if (rst == null) {
			return;
		}
		rst.beforeFirst();
		while (rst.next()) {
			ContextMenuItem mi = menus.addItem(rst.getString("GROUP_TITLE"));
			ContextMenuData data = new ContextMenuData();
			data.code = rst.getString("GROUP_TITLE");
			data.command_typ = "PARENT";
			data.isParent = true;
			data.menu_item = mi;
			mapContext.put(data.code, data);

		}
		rst.close();

		rst = utils.getSqlRS(sq, dbc.getDbConnection());
		if (rst == null) {
			return;
		}
		rst.beforeFirst();
		while (rst.next()) {
			ContextMenuItem mi = mapContext.get(rst.getString("GROUP_TITLE")).menu_item;
			ContextMenuItem mi1 = mi.addItem(rst.getString("DESCR"));
			ContextMenuData data = new ContextMenuData();
			data.isParent = false;
			data.command = rst.getString("SQLSTRING");
			data.command_typ = rst.getString("COMMAND_TYPE");
			data.menu_item = mi1;
			data.code = rst.getString("CODE");
			data.descr = rst.getString("DESCR");
			mapContext.put(rst.getString("DESCR"), data);
			if (data.command_typ.equals("GRAPH")) {
				mi1.setIcon(new ThemeResource("img/chart.png"));
			}
			if (data.command_typ.equals("COMMAND")) {
				mi1.setIcon(new ThemeResource("img/details.png"));
			}

		}
		rst.close();
		getTable().addListener(new ItemClickListener() {

			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == ItemClickEvent.BUTTON_RIGHT) {
					selectedMenuRow = Integer.valueOf(event.getItemId()
							.toString());
					menus.show(event.getClientX(), event.getClientY());
				}

			}
		});

		menus.addListener(new ContextMenu.ClickListener() {

			public void contextItemClick(
					org.vaadin.peter.contextmenu.ContextMenu.ClickEvent event) {
				ContextMenuItem clickedItem = event.getClickedItem();
				ContextMenuData data = mapContext.get(clickedItem.getName());
				if (data.isParent) {
					return;
				}
				if (data.command_typ.equals("COMMAND")) {
					int rowno = selectedMenuRow;
					utilsExe.added_query_parameter.clear();
					utilsVaadin.fillParmeterList(rowno,
							QueryView.this.getLctb(),
							utilsExe.added_query_parameter);
					for (Iterator iterator = QueryView.this.getListShowParams()
							.iterator(); iterator.hasNext();) {
						utilsExe.added_query_parameter.add((Parameter) iterator
								.next());
					}
					utilsExe.Execute(data.command, pl);

				}
				if (data.command_typ.equals("GRAPH")) {
					showGraphsPanel = true;
					addGraphPanel(data.code);
				}
			}
		});
	}
}