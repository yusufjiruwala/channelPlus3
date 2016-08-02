package com.windows;

import java.math.BigDecimal;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.doc.views.Query.QueryView;
import com.doc.views.Query.tableDataListner;
import com.generic.ColumnProperty;
import com.generic.DynamicReportSetting;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.Row;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.qryColumn;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsExe;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.windows.wizards.dev.frmWzDesignQuery;

public class frmQuickRep implements transactionalForm {
	private final String PAR_GROUP_1 = "GROUP_1";
	private final String PAR_GROUP_2 = "GROUP_2";
	private final String PAR_GROUP_1_FILTER = "GROUP_1_FILTER";
	private final String PAR_GROUP_2_FILTER = "GROUP_2_FILTER";
	private final String PAR_PAGE_WIDTH = "PAGE_WIDTH";
	private final String PAR_PAGE_HEIGHT = "PAGE_HEIGHT";
	private final String PAR_DATA_FILTER = "DATA_FILTER_";
	private final String PAR_DATA_SELECT = "DATA_SELECT_";

	private String titleStr = "Quick Report: ";

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout commandLayout = new HorizontalLayout();
	private GridLayout parameterLayout = new GridLayout();
	private GridLayout groupLayout = new GridLayout(2, 3);
	private GridLayout printLayout = new GridLayout(4, 1);
	private HorizontalLayout reportLayout = new HorizontalLayout();
	private VerticalLayout windowlayout = new VerticalLayout();
	private Window window = new Window();

	private OptionGroup page_option = new OptionGroup("Page Size");
	private Label txtTitle = new Label(titleStr);
	private String QRYSTR = "";
	private String savedFilterStr = "";
	private String savedGroup1Str = "";
	private String savedGroup2Str = "";

	private Button cmdPreview = new Button("Preview");
	private Button cmdQuery = new Button("Query");
	private Button cmdGraph = new Button("Re-Design wizard");
	private CheckBox chkSaveProfile = new CheckBox("Save Profile", false);
	private CheckBox chkWindowQry = new CheckBox("Query On Window", true);
	private CheckBox chkGraph = new CheckBox("Show Graphs", false);

	private ComboBox txtReport = new ComboBox("Report");
	private ComboBox txtProfile = new ComboBox("Profiles");
	private ComboBox txtGroup1 = new ComboBox("Group 1");
	private ComboBox txtGroup2 = new ComboBox("Group 2");
	private TextField txtFilter1 = new TextField("Filter");
	private TextField txtFilter2 = new TextField("Filter");

	private Table tbl_cols = new Table("Columns");

	private TextField txt_pgWidth = new TextField("Page Width", "595");
	private TextField txt_pgHeight = new TextField("Page Height", "842");
	private TextField txt_graphHeight = new TextField("Graph Height", "400");

	public Connection con = null;
	private boolean isListnerAdded = false;
	public boolean showTreePanel = true;

	public localTableModel data_cols = new localTableModel();
	private final List<ColumnProperty> lstItemCols = new ArrayList<ColumnProperty>();
	private List<Parameter> listParams = new ArrayList<Parameter>();
	private Map<String, List<String>> sqlMap = new HashMap<String, List<String>>();
	private List<ColumnProperty> listcols = new ArrayList<ColumnProperty>();
	private List<ColumnProperty> listcolstemp = new ArrayList<ColumnProperty>();
	public Map<String, Object> mapParaSave = new HashMap<String, Object>();
	public List<String> ctRowsCol = new ArrayList<String>();
	public List<String> ctHeaderCol = new ArrayList<String>();
	public boolean isCt = false;
	public boolean showColSection = true;
	public boolean showGroup1 = true;
	public boolean showGroup2 = true;
	private String sqlstr = "";
	private String sqlCols = "";
	private String sqlOrdby = "";
	private String sqlGroupby = "";
	private String sqlWhere = "";
	private String sqlView = "";
	public QueryView qv = new QueryView();
	public String sqlCPDistinctRow = "";
	public String sqlCPDistinctCol = "";
	public String CtValueCol = "";
	public String CtValueColTotTitle = "";
	public String strExecbefore = "";
	public boolean ctSort = false;
	private boolean mode_query = false;
	public boolean showSavedParams = true;
	private String parentCol = "";
	private String codeCol = "";
	private List<Parameter> listParas = new ArrayList<Parameter>();
	public String added_sql_clause = "";
	public String added_where_clause = "";
	public List<Parameter> added_filter_parameters = new ArrayList<Parameter>();
	public List<Parameter> added_query_parameters = new ArrayList<Parameter>();
	public Map<String, dataCell> actionMap = new HashMap<String, dataCell>();
	public List<dataCell> actionList = new ArrayList<dataCell>();
	public List<Parameter> listConditions = new ArrayList<Parameter>();

	private boolean showGroup = true;
	private boolean showAdvance = true;

	public Handler action_handler = new Handler() {

		public void handleAction(Action action, Object sender, Object target) {
			dataCell dc = actionMap.get(action.getCaption());
			String comand = utils.getSqlValue(
					"select SQLSTRING from CP_SUB_COMMANDS where code='"
							+ dc.getValue() + "'", con);
			int rowno = Integer.valueOf(target.toString());
			utilsExe.added_query_parameter.clear();
			utilsVaadin.fillParmeterList(rowno, qv.getLctb(),
					utilsExe.added_query_parameter);
			for (Iterator iterator = qv.getListShowParams().iterator(); iterator
					.hasNext();) {
				utilsExe.added_query_parameter.add((Parameter) iterator.next());
			}
			utilsExe.Execute(comand, parentLayout);

		}

		public Action[] getActions(Object target, Object sender) {
			List<Action> lstact = new ArrayList<Action>();
			for (Iterator iterator = actionList.iterator(); iterator.hasNext();) {
				dataCell dc = (dataCell) iterator.next();
				lstact.add(new Action(dc.getDisplay()));
			}
			Action[] acts = new Action[lstact.size()];
			acts = lstact.toArray(acts);
			return acts;
		}
	};

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public CheckBox getChkWindowQry() {
		return chkWindowQry;
	}

	public void setChkWindowQry(CheckBox chkWindowQry) {
		this.chkWindowQry = chkWindowQry;
	}

	public ComboBox getTxtReport() {
		return txtReport;
	}

	public ComboBox getTxtProfile() {
		return txtProfile;
	}

	public ComboBox getTxtGroup1() {
		return txtGroup1;
	}

	public ComboBox getTxtGroup2() {
		return txtGroup2;
	}

	public TextField getTxtFilter1() {
		return txtFilter1;
	}

	public TextField getTxtFilter2() {
		return txtFilter2;
	}

	public TextField getTxt_pgWidth() {
		return txt_pgWidth;
	}

	public TextField getTxt_pgHeight() {
		return txt_pgHeight;
	}

	public TextField getTxt_graphHeight() {
		return txt_graphHeight;
	}

	public List<Parameter> getListParams() {
		return listParams;
	}

	public void resetFormLayout() {
		cmdGraph.setEnabled(true);
		txtGroup1.setVisible(true);
		txtFilter1.setVisible(true);
		txtGroup1.setVisible(true);
		txtFilter1.setVisible(true);
		tbl_cols.setEnabled(true);
		savedFilterStr = "FILTER_QR_GROUP_" + QRYSTR + "_";
		savedGroup1Str = "QR_G1_" + QRYSTR + "_";
		savedGroup2Str = "QR_G2_" + QRYSTR + "_";
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		mainLayout.setWidth("500px");
		mainLayout.setHeight("400px");

		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		reportLayout.removeAllComponents();
		parameterLayout.removeAllComponents();
		mainLayout.removeAllComponents();
		groupLayout.removeAllComponents();
		commandLayout.removeAllComponents();
		printLayout.removeAllComponents();
		page_option.removeAllItems();
		updateColList();

	}

	public void updateColList() {
		lstItemCols.clear();
		ColumnProperty cp = new ColumnProperty();
		cp.col_class = Label.class;
		cp.colname = "FIELD_NAME";
		cp.descr = "Column Name";
		cp.display_align = ColumnProperty.ALIGN_LEFT;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_type = "NONE";
		cp.display_format = "NONE";
		cp.pos = 1;
		cp.display_width = 100;
		lstItemCols.add(cp);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "DISPLAY_WIDTH";
		cp.descr = "Width";
		cp.display_align = ColumnProperty.ALIGN_LEFT;
		cp.data_type = Parameter.DATA_TYPE_NUMBER;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_format = utils.FORMAT_QTY;
		cp.display_width = 60;
		lstItemCols.add(cp);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "FILTER_TEXT";
		cp.descr = "Filter";
		cp.display_align = ColumnProperty.ALIGN_LEFT;
		cp.display_type = "NONE";
		cp.pos = 3;
		cp.display_format = "NONE";
		cp.display_width = 100;
		lstItemCols.add(cp);

		cp = new ColumnProperty();
		cp.col_class = CheckBox.class;
		cp.colname = "SELECTION";
		cp.descr = "Select";
		cp.display_align = ColumnProperty.ALIGN_LEFT;
		cp.display_type = "NONE";
		cp.pos = 4;
		cp.display_format = "NONE";
		cp.display_width = 30;
		cp.onCheckValue = "Y";
		cp.onUnCheckValue = "N";

		lstItemCols.add(cp);

	}

	public void setQRYSTR(String s) {
		this.QRYSTR = s;
	}

	public void createView() {
		final AbstractOrderedLayout centralPanel = (AbstractOrderedLayout) this.parentLayout;
		resetFormLayout();

		ResourceManager.addComponent(commandLayout, cmdPreview);
		ResourceManager.addComponent(commandLayout, cmdQuery);
		ResourceManager.addComponent(commandLayout, cmdGraph);
		ResourceManager.addComponent(commandLayout, chkSaveProfile);
		ResourceManager.addComponent(commandLayout, chkWindowQry);
		ResourceManager.addComponent(commandLayout, chkGraph);

		ResourceManager.addComponent(mainLayout, txtTitle);
		ResourceManager.addComponent(reportLayout, txtReport);
		ResourceManager.addComponent(reportLayout, txtProfile);
		ResourceManager.addComponent(groupLayout, txtGroup1);
		ResourceManager.addComponent(groupLayout, txtFilter1);
		ResourceManager.addComponent(groupLayout, txtGroup2);
		ResourceManager.addComponent(groupLayout, txtFilter2);
		ResourceManager.addComponent(printLayout, txt_pgWidth);
		ResourceManager.addComponent(printLayout, txt_pgHeight);
		ResourceManager.addComponent(printLayout, page_option);
		ResourceManager.addComponent(printLayout, txt_graphHeight);

		ResourceManager.addComponent(mainLayout, commandLayout);
		ResourceManager.addComponent(mainLayout, reportLayout);
		ResourceManager.addComponent(mainLayout, parameterLayout);
		ResourceManager.addComponent(mainLayout, groupLayout);
		ResourceManager.addComponent(mainLayout, tbl_cols);
		ResourceManager.addComponent(mainLayout, printLayout);
		ResourceManager.addComponent(centralPanel, mainLayout);

		groupLayout.setSizeFull();
		commandLayout.setHeight("100%");
		reportLayout.setHeight("100%");
		parameterLayout.setWidth("100%");
		parameterLayout.setHeight("-1px");
		parameterLayout.setColumns(3);

		tbl_cols.setSizeFull();
		printLayout.setHeight("100%");
		txtGroup1.setWidth("100%");
		txtGroup2.setWidth("100%");
		txtFilter1.setWidth("100%");
		txtFilter2.setWidth("100%");

		mainLayout.setExpandRatio(commandLayout, .3f);
		mainLayout.setExpandRatio(reportLayout, .5f);
		// mainLayout.setExpandRatio(parameterLayout, .5f);
		mainLayout.setExpandRatio(groupLayout, 1.3f);
		mainLayout.setExpandRatio(tbl_cols, 1.4f);
		mainLayout.setExpandRatio(printLayout, .5f);

		tbl_cols.setSelectable(true);
		tbl_cols.setMultiSelect(false);
		tbl_cols.setNullSelectionAllowed(false);
		txtReport.setImmediate(true);
		txtGroup1.setImmediate(true);
		txtGroup2.setImmediate(true);
		txtFilter1.setImmediate(true);
		txtFilter2.setImmediate(true);
		chkSaveProfile.setImmediate(true);
		txtProfile.setFilteringMode(ComboBox.FILTERINGMODE_OFF);
		txtProfile.setImmediate(true);
		txtProfile.setNewItemsAllowed(true);
		txtProfile.setInputPrompt("input new profile and press enter key");

		page_option.addItem("Portrait");
		page_option.addItem("Landscape");
		page_option.setImmediate(true);

		try {
			utilsVaadin.FillCombo(txtReport,
					"Select code,titlearb from invqrycols1 where parentrep='"
							+ QRYSTR + "' order by code", con);
			txtReport.setValue(null);
			load_data();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		if (!isListnerAdded) {
			txtProfile.setNewItemHandler(new NewItemHandler() {

				public void addNewItem(String newItemCaption) {
					txtProfile.addItem(newItemCaption);
					txtProfile.setValue(newItemCaption);
				}
			});
			txtProfile.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if (txtProfile.getValue() == null
							|| txtProfile.getValue().toString().length() == 0) {
						load_data();
						return;
					}
					try {
						load_profile(false);
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			});
			page_option.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if (page_option.getValue().toString().equals("Portrait")) {
						txt_pgWidth.setValue("595");
						txt_pgHeight.setValue("842");
					} else {
						txt_pgWidth.setValue("842");
						txt_pgHeight.setValue("595");

					}

				}
			});
			txtReport.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					load_data();
				}
			});

			txtGroup1.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if (mode_query) {
						return;
					}
					try {
						if (txtGroup1.getValue() == null) {
							mapParaSave.remove(savedGroup1Str);
							txtGroup2.removeAllItems();
							fetchColsData();
							return;
						}

						utilsVaadin.FillCombo(
								txtGroup2,
								"select distinct group_name,group_name nm2 from invqrycols2 where code='"
										+ ((dataCell) txtReport.getValue())
												.getValue()
										+ "' and group_name!='"
										+ ((dataCell) txtGroup1.getValue())
												.getValue() + "'", con);
						mapParaSave.put(savedGroup1Str,
								((dataCell) txtGroup1.getValue()).getValue());
						fetchColsData();

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});
			txtGroup2.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if (mode_query) {
						return;
					}
					try {
						if (txtGroup2.getValue() == null) {
							mapParaSave.remove(savedGroup2Str);
							fetchColsData();
							return;
						}
						mapParaSave.put(savedGroup2Str,
								((dataCell) txtGroup2.getValue()).getValue());

						fetchColsData();
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			});

			txtFilter1.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if (txtGroup1.getValue() == null) {
						return;
					}
					if (mode_query) {
						return;
					}

					mapParaSave.remove(savedFilterStr
							+ ((dataCell) txtGroup1.getValue()).getValue());

					putSavedParas(
							savedFilterStr
									+ ((dataCell) txtGroup1.getValue())
											.getValue(), txtFilter1.getValue());
				}
			});

			txtFilter2.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if (txtGroup2.getValue() == null) {
						return;
					}
					if (mode_query) {
						return;
					}

					mapParaSave.remove(savedFilterStr
							+ ((dataCell) txtGroup2.getValue()).getValue());
					putSavedParas(
							savedFilterStr
									+ ((dataCell) txtGroup2.getValue())
											.getValue(), txtFilter2.getValue());
				}
			});

			cmdQuery.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						updateParameterData();
						RunQuery(((dataCell) txtReport.getValue()).getValue()
								.toString(), false, false);
						if (chkSaveProfile.booleanValue()) {
							save_profile();
							chkSaveProfile.setValue(false);
						}
						showParams();
						fillColsData();
						initValues();
					} catch (Exception ex) {
						ex.printStackTrace();
						parentLayout.getWindow().showNotification("Error: ",
								ex.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);
					}

				}
			});

			cmdPreview.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {
						updateParameterData();
						RunQuery(((dataCell) txtReport.getValue()).getValue()
								.toString(), true, false);
						if (chkSaveProfile.booleanValue()) {
							save_profile();
							chkSaveProfile.setValue(false);
						}
						showParams();
						fillColsData();
						initValues();
					} catch (Exception ex) {
						ex.printStackTrace();
						parentLayout.getWindow().showNotification("Error: ",
								ex.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);
					}
				}
			});
			cmdGraph.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					frmWzDesignQuery wz = new frmWzDesignQuery();
					wz.init();
					wz.init_redesign("3", QRYSTR, ((dataCell) txtReport
							.getValue()).getValue().toString());
				}
			});

			isListnerAdded = true;
		}

	}

	public void init() {

	}

	public void showParams() throws SQLException {

		listParams.clear();
		ValueChangeListener vlc = new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				updateParameterData();

			}
		};
		PreparedStatement ps_3 = con.prepareStatement(
				"select *from invqrycolspara where code='"
						+ ((dataCell) txtReport.getValue()).getValue()
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
				par.setValue(BigDecimal.valueOf(Double.valueOf(rs_3
						.getString("para_default"))));
			}
			if (rs_3.getString("para_datatype").equals("VARCHAR2")) {
				par.setValueType(Parameter.DATA_TYPE_STRING);
				par.setValue(rs_3.getString("para_default"));

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
				par.setValue((String) Channelplus3Application.getInstance()
						.getFrmUserLogin().getMapVars().get("DEFAULT_LOCATION"));
			}
			if (rs_3.getString("LISTNAME") != null) {
				par.setLovsql(rs_3.getString("LISTNAME"));
				utils.FillLov(par.getLovList(), rs_3.getString("LISTNAME"), con);
			}
			if (showSavedParams) {
				accessSavedParas(par);
			}
			listParams.add(par);
		}
		ps_3.close();
		parameterLayout.removeAllComponents();
		Component c = null;
		for (int i = 0; i < listParams.size(); i++) {
			if (listParams.get(i).getLovList().size() > 0) {
				c = new ComboBox(listParams.get(i).getDescription());
				for (Iterator<dataCell> it = listParams.get(i).getLovList()
						.iterator(); it.hasNext();) {
					dataCell dc = it.next();
					((ComboBox) c).addItem(dc);
				}

			} else {
				if (listParams.get(i).getValueType()
						.equals(Parameter.DATA_TYPE_DATE)) {
					c = new DateField(listParams.get(i).getDescription());
					((DateField) c).setDateFormat(utils.FORMAT_SHORT_DATE);
					((DateField) c).setResolution(DateField.RESOLUTION_DAY);
				} else {
					c = new TextField(listParams.get(i).getDescription());
				}

			}
			c.setWidth("100%");
			listParams.get(i).setUIObject(c);
			ResourceManager.addComponent(parameterLayout, c);
		}
		updateParameterUI();
		for (Iterator iterator = listParams.iterator(); iterator.hasNext();) {
			Parameter pm = (Parameter) iterator.next();
			if (pm.getUIObject() != null
					&& pm.getUIObject() instanceof AbstractField) {
				((AbstractField) pm.getUIObject()).setImmediate(true);
				((AbstractField) pm.getUIObject()).addListener(vlc);
			}
		}
	}

	public void updateParameterData() {
		Object strVal = null;
		for (int i = 0; i < listParams.size(); i++) {
			if (listParams.get(i).getUIObject() instanceof ComboBox) {
				ComboBox c = (ComboBox) listParams.get(i).getUIObject();
				listParams.get(i).setValue("");
				listParams.get(i).setValueDescription("");
				if (c.getValue() != null) {
					listParams.get(i).setValue(
							((dataCell) c.getValue()).getValue().toString());
					listParams.get(i).setValueDescription(
							((dataCell) c.getValue()).getDisplay().toString());
					strVal = ((dataCell) c.getValue()).getValue().toString();
				}
			}
			if (listParams.get(i).getUIObject() instanceof TextField) {
				TextField c = (TextField) listParams.get(i).getUIObject();
				listParams.get(i).setValue("");
				if (c.getValue() != null) {
					listParams.get(i).setValue(c.getValue());
				}
				strVal = c.getValue();
			}
			if (listParams.get(i).getUIObject() instanceof DateField) {
				DateField c = (DateField) listParams.get(i).getUIObject();
				listParams.get(i).setValue(new java.util.Date());
				if (c.getValue() != null) {
					listParams.get(i).setValue(c.getValue());
				}
				strVal = c.getValue();
			}
			if (strVal != null) {
				mapParaSave.put(listParams.get(i).getName(), strVal);
				strVal = null;
			}

		}
	}

	public void updateParameterUI() {
		for (int i = 0; i < listParams.size(); i++) {
			if (listParams.get(i).getUIObject() instanceof ComboBox) {
				ComboBox c = (ComboBox) listParams.get(i).getUIObject();
				c.setValue(null);
				if (listParams.get(i).getValue() != null) {
					c.setValue(utilsVaadin.findByValue(c, listParams.get(i)
							.getValue().toString()));
				}
			}
			if (listParams.get(i).getUIObject() instanceof TextField) {
				TextField c = (TextField) listParams.get(i).getUIObject();
				c.setValue(null);
				if (listParams.get(i).getValue() != null) {
					c.setValue(listParams.get(i).getValue().toString());
				}
			}
			if (listParams.get(i).getUIObject() instanceof DateField) {
				DateField c = (DateField) listParams.get(i).getUIObject();
				c.setValue(null);
				if (listParams.get(i).getValue() != null) {
					c.setValue((java.util.Date) listParams.get(i).getValue());
				}
			}

		}
	}

	public void fillColsData() throws SQLException {
		tbl_cols.removeAllItems();
		for (Iterator iterator = lstItemCols.iterator(); iterator.hasNext();) {
			ColumnProperty cp = (ColumnProperty) iterator.next();
			tbl_cols.addContainerProperty(cp.descr, cp.col_class, null);
			tbl_cols.setColumnWidth(cp.descr, cp.display_width);

		}

		utilsVaadin.query_data2(tbl_cols, data_cols, lstItemCols);

		load_profile(true);

		tbl_cols.requestRepaint();
	}

	public void fetchColsData() throws SQLException {
		String strgroup1 = "";
		if (txtGroup1.getValue() != null) {
			strgroup1 = ((dataCell) txtGroup1.getValue()).getValue().toString();
		}
		String strgroup2 = "";
		if (txtGroup2.getValue() != null) {
			strgroup2 = ((dataCell) txtGroup2.getValue()).getValue().toString();
		}
		String strg = strgroup1;
		// String strg2 = strgroup2;
		if (!strgroup2.isEmpty()) {
			strg = strgroup2;
		}

		String sq = "select indexno,initcap(nvl(DISPLAY_NAME,FIELD_NAME)) FIELD_NAME,'Y' SELECTION ,COLWIDTH DISPLAY_WIDTH ,'' FILTER_TEXT,datatypex "
				+ "FROM INVQRYCOLS2 WHERE CODE='"
				+ ((dataCell) txtReport.getValue()).getValue()
				+ "' and  group_name is null and iswhere is null and cp_hidecol is null"
				+ " and  (nvl(group_name2,'ALL')='ALL'  or  group_name2 ='"
				+ strg + "'" + " )  order by indexno ";
		data_cols.executeQuery(sq, true);
		data_cols.setMasterRowStatusAll(Row.ROW_QUERIED);
		fillColsData();

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		if (data_cols.getDbclass() == null) {
			try {
				data_cols.createDBClassFromConnection(con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		createView();
		load_data();

	}

	public void load_data() {

		if (txtReport.getValue() == null && txtReport.size() > 0) {
			titleStr = utils.getSqlValue(
					"select titlearb from invqrycols1 where code='" + QRYSTR
							+ "'", con);
			txtReport.setValue(txtReport.getItemIds().toArray()[0]);
		}

		data_cols.clearALl();
		tbl_cols.removeAllItems();
		mode_query = true;
		txtGroup1.setValue(null);
		txtGroup2.setValue(null);
		txtFilter1.setValue("");
		txtFilter2.setValue("");
		mode_query = false;

		if (txtReport.getValue() != null) {
			try {
				utilsVaadin.FillCombo(txtGroup1,
						"select distinct group_name,group_name nm2 from invqrycols2 where code='"
								+ ((dataCell) txtReport.getValue()).getValue()
								+ "'", con);
				utilsVaadin.FillComboStrings(txtProfile,
						"Select PROFILE_NAME,PROFILE_NAME P2 from cp_rep_profiles where report_name='"
								+ ((dataCell) txtReport.getValue()).getValue()
								+ "' and usernames like '%\"ALL\"%'", con);
				PreparedStatement pst = con.prepareStatement(
						"select *from invqrycols1 where code='"
								+ ((dataCell) txtReport.getValue()).getValue()
								+ "'", ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pst.executeQuery();
				rst.first();
				txt_pgHeight.setValue(rst.getString("Page_Height"));
				txt_pgWidth.setValue(rst.getString("Page_width"));

				if (rst.getString("is_cross_tab").equals("Y")) {
					isCt = true;
					cmdGraph.setEnabled(false);
					tbl_cols.setEnabled(false);
				}

				if (!rst.getString("show_col_selection").equals("Y")) {
					showColSection = false;
					mainLayout.removeComponent(tbl_cols);
					mainLayout.removeComponent(printLayout);
				}

				if (!rst.getString("show_group_1").equals("Y")) {
					showGroup1 = false;
					mainLayout.removeComponent(groupLayout);
					mainLayout.setSizeUndefined();
					parameterLayout.setSizeUndefined();
					parameterLayout.setColumns(2);
				}

				pst.close();
				showParams();
				fetchColsData();
				initValues();

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	public void initValues() {
		String strgroup1 = "";
		if (txtGroup1.getValue() != null) {
			strgroup1 = ((dataCell) txtGroup1.getValue()).getValue().toString();
		}
		String strgroup2 = "";
		if (txtGroup2.getValue() != null) {
			strgroup2 = ((dataCell) txtGroup2.getValue()).getValue().toString();
		}
		if (mapParaSave.get(savedGroup1Str) != null) {
			strgroup1 = mapParaSave.get(savedGroup1Str).toString();
			txtGroup1.setValue(utilsVaadin.findByValue(txtGroup1, strgroup1));
			// strgroup1 = ((dataCell)
			// txtGroup1.getValue()).getValue().toString();
		}
		if (mapParaSave.get(savedGroup2Str) != null) {
			strgroup2 = mapParaSave.get(savedGroup2Str).toString();
			txtGroup2.setValue(utilsVaadin.findByValue(txtGroup2, strgroup2));

		}
		if (txtGroup1.getValue() != null
				&& mapParaSave.get(savedFilterStr + txtGroup1.getValue()) != null) {
			txtFilter1.setValue(mapParaSave.get(savedFilterStr
					+ txtGroup1.getValue()));
		}
		if (txtGroup2.getValue() != null
				&& mapParaSave.get(savedFilterStr + txtGroup2.getValue()) != null) {
			txtFilter2.setValue(mapParaSave.get(savedFilterStr
					+ txtGroup2.getValue()));
		}

	}

	public void print_data() {

	}

	public void save_data() {

	}

	public void showInitView() {
		initForm();

	}

	public List<Parameter> loadFilterData(String idno, boolean execute)
			throws SQLException {
		// check either should return
		boolean ret = true;
		for (int i = 0; i < data_cols.getRows().size(); i++) {
			if (data_cols.getFieldValue(i, "FILTER_TEXT") != null
					&& data_cols.getFieldValue(i, "FILTER_TEXT").toString()
							.length() > 0) {
				ret = false;
				break;
			}
		}
		if ((txtFilter1.getValue() != null && txtFilter1.getValue().toString()
				.length() > 0)
				|| (txtFilter2.getValue() != null && txtFilter2.getValue()
						.toString().length() > 0)) {
			ret = false;
		}
		if (ret) {
			return null;
		}
		// end of check either should return

		String strgroup1 = "";
		if (txtGroup1.getValue() != null) {
			strgroup1 = ((dataCell) txtGroup1.getValue()).getValue().toString();
		}
		String strgroup2 = "";
		if (txtGroup2.getValue() != null) {
			strgroup2 = ((dataCell) txtGroup2.getValue()).getValue().toString();
		}
		List<Parameter> pms = new ArrayList<Parameter>();
		PreparedStatement ps = con
				.prepareStatement(
						"select nvl(display_name,field_name)  field_name, group_name ,indexno from invqrycols2 where group_name in ('"
								+ strgroup1
								+ "','"
								+ strgroup2
								+ "') and code='" + idno + "' order by indexno",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
		ResultSet rst = ps.executeQuery();
		Parameter pm = null;
		Parameter pm1 = null;
		Parameter pm2 = null;
		int grp1cnt = 0, grp2cnt = 0;
		while (rst.next()) {
			if (strgroup1.equals(rst.getString("GROUP_NAME"))
					&& txtFilter1.getValue() != null
					&& txtFilter1.getValue().toString().length() > 0) {
				String strFilter = txtFilter1.getValue().toString();
				Pattern ptn = Pattern.compile(utils.regx_word);
				Matcher mtch = ptn.matcher(strFilter);
				for (int i = 0; (grp1cnt != 0 && i < grp1cnt); i++) {
					mtch.find();
				}
				if (mtch.find() && mtch.group(grp1cnt) != null) {
					pm = new Parameter(rst.getString("FIELD_NAME"), mtch.group(
							grp1cnt).replace("\"", ""));
					pms.add(pm);
					grp1cnt++;

				}
			}
			if (strgroup2.equals(rst.getString("GROUP_NAME"))
					&& txtFilter2.getValue() != null
					&& txtFilter2.getValue().toString().length() > 0) {
				String strFilter = txtFilter2.getValue().toString();
				Pattern ptn = Pattern.compile(utils.regx_word);
				Matcher mtch = ptn.matcher(strFilter);
				for (int i = 0; (grp2cnt > 0 && i < grp2cnt); i++) {
					mtch.find();
				}
				if (mtch.find() && mtch.group(grp2cnt) != null) {
					pm = new Parameter(rst.getString("FIELD_NAME"), mtch.group(
							grp2cnt).replace("\"", ""));
					pms.add(pm);
					grp2cnt++;

				}
			}

		}
		ps.close();
		if (pm1 != null) {
			pm1.setValue("");
		}
		if (pm2 != null) {
			pm2.setValue("");
		}

		for (int i = 0; i < data_cols.getRows().size(); i++) {
			String fl = "";
			if (!data_cols.getFieldValue(i, "SELECTION").toString().equals("Y")) {
				continue;
			}
			if (data_cols.getFieldValue(i, "FILTER_TEXT") != null
					&& data_cols.getFieldValue(i, "FILTER_TEXT").toString()
							.length() > 0) {
				fl = data_cols.getFieldValue(i, "FILTER_TEXT").toString();
				String typ = Parameter.DATA_TYPE_STRING;
				if (data_cols.getFieldValue(i, "DATATYPEX").toString()
						.equals("NUMBER")) {
					typ = Parameter.DATA_TYPE_NUMBER;
				}
				if (data_cols.getFieldValue(i, "DATATYPEX").toString()
						.equals("DATE")) {
					typ = Parameter.DATA_TYPE_DATE;
				}

				pm = new Parameter(data_cols.getFieldValue(i, "FIELD_NAME")
						.toString(), fl);
				pm.setValueType(typ);
				pms.add(pm);
			}

		}
		if (execute) {
			qv.getLctb().setColumnsFilter(pms);
		}
		return pms;
	}

	public String getIndexNoIn(String idno) throws SQLException {
		String indexin = "";
		List<String> ltmp = new ArrayList<String>();
		String sq = "select *from invqrycols2 where code='" + idno
				+ "' and group_name is not null  order by indexno";
		String strgroup1 = "";
		if (txtGroup1.getValue() != null) {
			strgroup1 = ((dataCell) txtGroup1.getValue()).getValue().toString();
		}
		String strgroup2 = "";
		if (txtGroup2.getValue() != null) {
			strgroup2 = ((dataCell) txtGroup2.getValue()).getValue().toString();
		}

		ltmp.clear();
		PreparedStatement psq = con.prepareStatement(sq,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rsq = psq.executeQuery();
		while (rsq.next()) {
			if (strgroup1.equals(rsq.getString("group_name"))) {
				ltmp.add(rsq.getString("indexno"));
			}
			if (strgroup2.equals(rsq.getString("group_name"))) {
				ltmp.add(rsq.getString("indexno"));
			}

		}
		psq.close();
		for (int i = 0; i < data_cols.getRows().size(); i++) {
			if (data_cols.getFieldValue(i, "SELECTION").toString().equals("Y")) {
				ltmp.add(data_cols.getFieldValue(i, "INDEXNO").toString());
			}
		}
		String com = "";
		for (int i = 0; i < ltmp.size(); i++) {
			if (i > 0) {
				com = ",";
			}
			indexin = indexin + com + ltmp.get(i);
		}
		return indexin;
	}

	public void buildSql(String idno) throws Exception {
		isCt = false;
		ctHeaderCol.clear();
		ctRowsCol.clear();

		sqlstr = "";
		String s1 = "";
		String strgroup1 = "";
		parentCol = "";
		codeCol = "";
		if (txtGroup1.getValue() != null
				&& txtGroup1.getValue().toString().length() > 0) {
			strgroup1 = ((dataCell) txtGroup1.getValue()).getValue().toString();
		}
		String strgroup2 = "";
		if (txtGroup2.getValue() != null
				&& txtGroup2.getValue().toString().length() > 0) {
			strgroup2 = ((dataCell) txtGroup2.getValue()).getValue().toString();
		}
		int summary_count = 0;
		String indexin = "";

		if (sqlMap.size() > 0) {
			sqlMap.get("COLS").clear();
			sqlMap.get("WHERE").clear();
			sqlMap.get("GROUP").clear();
			sqlMap.get("SUBGROUP").clear();
			sqlMap.get("COLSDISP").clear();
			sqlMap.get("HIDECOL").clear();
			sqlMap.get("SUMMARY_COLS").clear();
			sqlMap.get("COLS_TEMP").clear();
		}

		sqlMap.clear();
		sqlMap.put("COLS", new ArrayList<String>());
		sqlMap.put("WHERE", new ArrayList<String>());
		sqlMap.put("GROUP", new ArrayList<String>());
		sqlMap.put("SUBGROUP", new ArrayList<String>());
		sqlMap.put("COLSDISP", new ArrayList<String>());
		sqlMap.put("HIDECOL", new ArrayList<String>());
		sqlMap.put("COLS_TEMP", new ArrayList<String>());
		sqlMap.put("SUMMARY_COLS", new ArrayList<String>());
		String order_by[] = new String[] { "", "", "", "", "", "", "", "" };

		if (con == null) {
			throw new Exception("no connection to query builder");
		}
		indexin = getIndexNoIn(idno);
		PreparedStatement ps_1 = con.prepareStatement(
				"select *from INVQRYCOLS1 where code='" + idno + "'",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		PreparedStatement ps_2 = con
				.prepareStatement(
						"select *from INVQRYCOLS2 where code='"
								+ idno
								+ "' and (indexno in ("
								+ indexin
								+ ") or (iswhere='Y' or cp_hidecol='Y')  ) ORDER BY INDEXNO",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
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
		listcolstemp.clear();
		listcols.clear();
		List<String> cols = new ArrayList<String>();

		while (rs_2.next()) {

			col = utils.nvl(rs_2.getString("FORMULAFLD"),
					rs_2.getString("FIELD_NAME"));
			coldisp = utils.nvl(rs_2.getString("DISPLAY_NAME"),
					rs_2.getString("FIELD_NAME"));
			if (rs_2.getString("SUMBY") != null
					&& rs_2.getString("sumby").length() > 0) {
				col = rs_2.getString("SUMBY") + "(" + col + ")";
			}
			// if is where not 'Y'
			if ((rs_2.getString("ISWHERE") == null || rs_2.getString("ISWHERE")
					.length() == 0)) {
				ColumnProperty cp = new ColumnProperty();
				if ((strgroup1.length() > 0 || strgroup2.length() > 0)
						&& (strgroup1.equals(rs_2.getString("group_name")))) {
					if (strgroup1.equals(rs_2.getString("group_name"))) {
						if (sqlMap.get("COLS_TEMP").size() > 0) {
							sqlMap.get("COLS_TEMP").add(
									col + " \"" + coldisp + "\"");
						} else {
							sqlMap.get("COLS_TEMP").add(0,
									col + " \"" + coldisp + "\"");
						}
					} else {
						sqlMap.get("COLS_TEMP").add(
								col + " \"" + coldisp + "\"");
					}

				} else {
					sqlMap.get("COLS").add(col + " \"" + coldisp + "\"");
				}

				cp.col_class = String.class;
				cp.colname = coldisp;
				cp.descr = utils.nvl(rs_2.getString("CP_COL_TITLE_ENG"),
						coldisp);
				int fnd_col = data_cols.locate("INDEXNO",
						rs_2.getString("INDEXNO"), localTableModel.FIND_EXACT);
				if (fnd_col >= 0) {
					cp.display_width = ((BigDecimal) data_cols.getFieldValue(
							fnd_col, "DISPLAY_WIDTH")).intValue();
				} else {
					cp.display_width = rs_2.getInt("COLWIDTH");
				}
				cp.display_type = "";
				cp.pos = rs_2.getInt("indexno");
				cp.display_align = rs_2.getString("CP_ALIGN");
				cp.display_format = rs_2.getString("cp_format");
				cp.data_type = rs_2.getString("DATATYPEX");
				cp.summary = "";
				if (rs_2.getString("HASGROSS") != null) {
					if (rs_2.getString("HASGROSS_COLUMN") == null) {
						cp.summary = rs_2.getString("HASGROSS");
					}
				}
				if ((strgroup1.length() > 0 && strgroup2.length() > 0)
						&& (strgroup1.equals(rs_2.getString("group_name")))) {
					if (listcolstemp.size() > 0) {
						listcolstemp.add(cp);
					} else {
						listcolstemp.add(0, cp);
					}

				} else {
					listcols.add(cp);
				}
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

			if ((strgroup1.length() > 0 || strgroup2.length() > 0)
					&& (strgroup1.equals(rs_2.getString("group_name")))) {
				if (s1.length() > 0) {
					s1 = s1 + ",\"" + coldisp + "\"";
				} else {
					s1 = "\"" + coldisp + "\"";
				}
			}
			/*
			 * if ((strgroup1.length() > 0 || strgroup2.length() > 0) &&
			 * (strgroup1.equals(rs_2.getString("group_name")) || strgroup2
			 * .equals(rs_2.getString("group_name")))) { if (s1.length() > 0) {
			 * s1 = s1 + "," + "\"" + coldisp + "\""; } else { s1 = "\"" +
			 * coldisp + "\""; } }
			 */
			if (strgroup1.equals(rs_2.getString("group_name"))) {
				parentCol = coldisp;
			}
			if (strgroup2.equals(rs_2.getString("group_name"))) {
				codeCol = coldisp;
			}

			if (rs_2.getString("CT_COL") != null
					&& rs_2.getString("CT_COL").trim().length() > 0) {
				ctHeaderCol.add(coldisp);
			}
			if (rs_2.getString("CT_ROW") != null
					&& rs_2.getString("CT_ROW").trim().length() > 0) {
				ctRowsCol.add(coldisp);
			}
			if (rs_2.getString("CT_VAL") != null
					&& rs_2.getString("CT_VAL").trim().length() > 0) {
				CtValueCol = coldisp;
				CtValueColTotTitle = rs_2.getString("CT_VAL_TOT_LABEL");
			}

		}// while rs2

		for (int i = sqlMap.get("COLS_TEMP").size() - 1; i > -1; i--) {
			sqlMap.get("COLS").add(0, sqlMap.get("COLS_TEMP").get(i));
		}
		for (int i = listcolstemp.size() - 1; i > -1; i--) {
			listcols.add(0, listcolstemp.get(i));
		}
		for (int i = 0; i < listcolstemp.size(); i++) {
			coldisp = listcolstemp.get(i).colname;
			sqlMap.get("SUBGROUP").add(coldisp);
		}

		if (ctHeaderCol.size() == 1) {
			ctHeaderCol.add(ctHeaderCol.get(0).toString());
		}
		ps_2.close();

		sqlstr = "";
		sqlCols = "";
		sqlWhere = "";
		sqlGroupby = "";
		sqlOrdby = "";
		sqlView = "";
		sqlCPDistinctRow = "";
		sqlCPDistinctCol = "";

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
		for (Iterator iterator = sqlMap.get("SUBGROUP").iterator(); iterator
				.hasNext();) {
			String strSub = (String) iterator.next();
			if (s1.length() > 0) {
				s1 = s1 + ",\"" + strSub + "\"";
			} else {
				s1 = "\"" + strSub + "\"";
			}
		}

		for (int i = 0; i < order_by.length; i++) {
			String str = order_by[i];
			if (str.length() > 0) {
				if (sqlOrdby.length() > 0) {
					sqlOrdby = sqlOrdby + " , " + str;
				} else {
					// first add sub group as order by
					sqlOrdby = "Order By " + (s1.length() > 0 ? s1 + "," : "")
							+ str;
				}// ..else..first add sub group as order by
			}
		}

		if (sqlOrdby.length() == 0 && s1.length() > 0) {
			sqlOrdby = "Order by " + s1;
		}

		ResultSet rs_1 = ps_1.executeQuery();
		rs_1.first();

		// sqlwhere to add with additional where
		sqlWhere = sqlWhere + " " + utils.nvl(rs_1.getString("REPORTNAME"), "");
		if (sqlWhere.trim().length() > 0 && added_where_clause.length() > 0) {
			added_where_clause = " and " + added_where_clause;
		}
		if (sqlWhere.trim().length() == 0 && added_where_clause.length() > 0) {
			added_where_clause = " where " + added_where_clause;
		}

		sqlWhere = sqlWhere + added_where_clause;
		if ((rs_1.getString("HASSUBTOT") != null && rs_1.getString("HASSUBTOT")
				.length() > 0) && sqlMap.get("SUBGROUP").size() == 0) {
			sqlMap.get("SUBGROUP").add(listcols.get(0).colname);
		}

		if (rs_1.getString("is_cross_tab").equals("Y")) {
			isCt = true;
		}
		if (rs_1.getString("CT_COL_SORT") != null
				&& rs_1.getString("CT_COL_SORT").equals("Y")) {
			ctSort = true;
		}
		if (rs_1.getString("exec_before") != null
				&& rs_1.getString("exec_before").length() > 0) {
			strExecbefore = rs_1.getString("exec_before");
		}
		sqlView = rs_1.getString("rep_qryname");

		ps_1.close();

		sqlstr = "select " + sqlCols + " from " + sqlView + " " + sqlWhere
				+ " " + sqlGroupby + " " + sqlOrdby;

		/**
		 * later on to add this.. for qucik rows and columns if (isCt) { for
		 * (int i = 0; i < ctHeaderCol.size(); i++) {
		 * 
		 * } }
		 */
	}

	public void load_data_condition(String idno) throws SQLException {
		ResultSet rst = utils.getSqlRS(
				"select *from  invqrycols_cond_format where code='" + idno
						+ "' order by posno", con);
		if (rst == null) {
			return;
		}
		listConditions.clear();
		rst.beforeFirst();
		while (rst.next()) {
			Parameter pm = new Parameter(rst.getString("COLUMN_NAME"),
					rst.getString("COLUMN_VALUE"));
			pm.operator = rst.getString("LOGICAL_OPERATOR");
			pm.setDefaultValue(utils.nvl(rst.getString("APPLY_COLUMN"), ""));
			Map<String, String> mp = new HashMap<String, String>();
			mp.put("STYLE_NAME", utils.nvl(rst.getString("STYLE_NAME"), ""));
			mp.put("HTML_TAG_START",
					utils.nvl(rst.getString("HTML_TAG_START"), ""));
			mp.put("HTML_TAG_END", utils.nvl(rst.getString("HTML_TAG_END"), ""));
			pm.setUIObject(mp);

			listConditions.add(pm);
		}
		rst.getStatement().close();
	}

	public void getActions(String idno) throws SQLException {

		utils.FillLov(actionList,
				"select code,nvl(title,descr) descr from CP_SUB_COMMANDS"
						+ " where query_code_in like '%\"'||'" + idno
						+ "'||'\"%' AND (query_field_in is null or "
						+ utilsVaadin.generateFieldClause(qv)
						+ ")  and command_type='COMMAND' order by POSX ", con);
		actionMap.clear();
		for (Iterator iterator = actionList.iterator(); iterator.hasNext();) {
			dataCell dc = (dataCell) iterator.next();
			actionMap.put(dc.getDisplay(), dc);
		}

	}

	public void RunQuery(final String idno, boolean report, boolean graph)
			throws Exception {
		if (qv != null) {
			qv.resetAll();
		}
		qv = new QueryView();
		buildSql(idno);
		load_data_condition(idno);
		int page_height = Integer.valueOf(txt_pgHeight.getValue().toString());
		int page_width = Integer.valueOf(txt_pgWidth.getValue().toString());
		qv.createDBClassFromConnection(con);
		qv.setSqlquery(sqlstr);
		qv.setShowSavedParameter(false);
		qv.reportSetting.doStandard();
		qv.reportSetting.setOrientation(DynamicReportSetting.PAGE_CUSTOM);
		qv.reportSetting.setPageHeight(page_height);
		qv.reportSetting.setPageWidth(page_width);
		qv.reportSetting.setTitle(titleStr + " -  "
				+ txtReport.getValue().toString());
		qv.listConditionalFormatting.clear();
		qv.listConditionalFormatting.addAll(listConditions);
		qv.setBeforeQueryExecSql(strExecbefore);
		qv.getDataListners().add(new tableDataListner() {
			public String getCellStyle(qryColumn qc, int recordNo) {
				return null;
			}

			public String calcSummary(List<String> qcGroup, qryColumn qc) {
				return "";
			}

			public void afterQuery() {
				for (Iterator iterator = qv.listcols.iterator(); iterator
						.hasNext();) {
					ColumnProperty cp = (ColumnProperty) iterator.next();
					if (cp.display_format != null
							&& cp.display_format.length() > 0
							&& qv.getLctb().getColByName(cp.colname).isNumber()) {
						qv.getLctb().getColByName(cp.colname)
								.setNumberFormat(cp.display_format);
					}
					if (cp.display_format != null
							&& cp.display_format
									.equals(ColumnProperty.SHORT_DATE_FORMAT)) {
						qv.getLctb().getColByName(cp.colname)
								.setDateFormat(utils.FORMAT_SHORT_DATE);
					}
					if (cp.display_format != null
							&& cp.display_format
									.equals(ColumnProperty.MONEY_FORMAT)) {
						qv.getLctb()
								.getColByName(cp.colname)
								.setNumberFormat(
										Channelplus3Application.getInstance()
												.getFrmUserLogin().FORMAT_MONEY);
					}
					if (cp.display_format != null
							&& cp.display_format
									.equals(ColumnProperty.QTY_FORMAT)) {
						qv.getLctb().getColByName(cp.colname)
								.setNumberFormat(utils.FORMAT_QTY);
					}
					if (cp.colname != null) {
						qv.getLctb().getColByName(cp.colname)
								.setTitle(cp.descr);
					}
				}
			}

			public void afterVisual() {
				for (Iterator iterator = qv.listcols.iterator(); iterator
						.hasNext();) {
					ColumnProperty cp = (ColumnProperty) iterator.next();
					qv.setColumnWidth(cp.colname, cp.display_width);
				}
				try {
					qv.fill_menus(idno, parentLayout);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			public void beforeQuery() {

			}

		});
		// added parameters from other objects like utilExe...
		for (Iterator iterator = added_query_parameters.iterator(); iterator
				.hasNext();) {
			Parameter p = (Parameter) iterator.next();
			qv.addParameter(p, false);
		}
		// qv.getListShowParams().addAll(listParams);
		for (Iterator iterator = listParams.iterator(); iterator.hasNext();) {
			Parameter p = (Parameter) iterator.next();
			Parameter pp = utils.getParam(p.getName(), added_query_parameters);
			if (pp != null) {
				p.setValue(pp.getValue());
			}			
			qv.addParameter(p, true);
		}

		if (sqlMap.get("SUBGROUP").size() > 0) {
			qv.setShowGroupHead(true);
			qv.getListGroupsBy().addAll(sqlMap.get("SUBGROUP"));
		}
		qv.getListHideCols().clear();
		if (sqlMap.get("HIDECOL").size() > 0) {
			qv.getListHideCols().addAll(sqlMap.get("HIDECOL"));
		}
		qv.getListGroupSum().clear();
		qv.getListGroupSum().addAll(sqlMap.get("SUMMARY_COLS"));

		qv.isCt = isCt;
		qv.listCtHeaderCol.clear();
		qv.listCtHeaderCol.addAll(ctHeaderCol);
		qv.listCtRowsCol.clear();
		qv.listCtRowsCol.addAll(ctRowsCol);
		qv.listcols.clear();
		qv.listcols.addAll(listcols);
		qv.CtValueCol = CtValueCol;
		qv.CtValueColTotTitle = CtValueColTotTitle;
		qv.buildTreeData = showTreePanel;
		qv.listTreeFields.clear();
		qv.parentCol = parentCol;
		qv.codeCol = codeCol;

		qv.queryCode = idno;

		qv.setShowGraphPanel(chkGraph.booleanValue());

		List<Parameter> pms = loadFilterData(idno, false);
		if (added_filter_parameters.size() > 0) {
			if (pms == null) {
				pms = new ArrayList<Parameter>();
			}
			pms.addAll(added_filter_parameters);
		}

		qv.fetchData(pms);
		getActions(idno);

		for (Iterator iterator = qv.listcols.iterator(); iterator.hasNext();) {
			ColumnProperty cp = (ColumnProperty) iterator.next();
			if (cp.colname != null)
				qv.getLctb().getColByName(cp.colname)
						.setWidth(cp.display_width);
		}

		if (report && !graph) {
			qv.printData();
		}
		if (!graph && !report) {
			show_query();

		}
		if (graph && report) {
			show_graph(idno);
		}
	}

	public void show_query() throws SQLException {
		if (chkWindowQry.booleanValue()) {
			windowlayout.removeAllComponents();
			window.setContent(windowlayout);
			window.setWidth("90%");
			window.setHeight("90%");
			window.center();
			window.setCloseShortcut(KeyCode.ESCAPE, null);
			qv.getTable().setSizeFull();
			windowlayout.setSizeFull();
			ResourceManager.addComponent(windowlayout, qv);
			if (!Channelplus3Application.getInstance().getMainWindow()
					.getChildWindows().contains(window)) {
				Channelplus3Application.getInstance().getMainWindow()
						.addWindow(window);
			}

		} else {
			resetFormLayout();
			parentLayout.setSizeFull();
			ResourceManager.addComponent(parentLayout, qv);
			qv.getListUserButtons().clear();
			Button bc = new NativeButton();
			bc.setIcon(new ThemeResource("img/back.png"));
			bc.setStyleName("toolbar");
			bc.setDescription("back to parameter view..");

			qv.getListUserButtons().add(bc);
			qv.getTable().setSizeFull();
			bc.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					initForm();
				}
			});
		}

		qv.setShowParameter(false);
		qv.setSizeFull();
		qv.createView(false);

		qv.getTable().addActionHandler(null);

	}

	public void putSavedParas(String nm, Object vl) {
		if (vl != null) {
			mapParaSave.put(nm, vl);
		} else {
			mapParaSave.remove(nm);
		}
	}

	public void show_graph(String idno) throws Exception {
		String strgroup1 = "";
		if (txtGroup1.getValue() != null) {
			strgroup1 = ((dataCell) txtGroup1.getValue()).getValue().toString();
		}
		PreparedStatement ps = con
				.prepareStatement(
						"select nvl(display_name,field_name)  field_name, group_name,indexno from invqrycols2 where group_name in ('"
								+ strgroup1
								+ "') and code='"
								+ idno
								+ "' order by indexno",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
		ResultSet rst = ps.executeQuery();
		String s1 = "";
		String s2 = "";

		while (rst.next()) {
			if (strgroup1.equals(rst.getString("GROUP_NAME"))) {
				String tmp = rst.getString("FIELD_NAME");
				if (s1.length() == 0) {
					s1 = tmp;
				} else {
					s2 = tmp;
				}

			}
		}
		qv.graphGroupCol = s2;
		qv.graphSumCol = "";
		for (int i = 0; i < data_cols.getRows().size(); i++) {
			String fl = "";
			if (data_cols.getFieldValue(i, "SELECTION").toString().equals("Y")
					&& data_cols.getFieldValue(i, "DATATYPEX").toString()
							.equals("NUMBER")) {
				qv.graphSumCol = data_cols.getFieldValue(i, "FIELD_NAME")
						.toString();
			}
		}
		if (qv.graphSumCol.length() == 0) {
			return;
		}
		qv.graphHeight = Integer.valueOf(txt_graphHeight.getValue().toString());
		qv.printGraph();
	}

	public void load_profile(boolean load_only_data_cols) throws SQLException {

		if (txtProfile.getValue() == null
				|| txtProfile.getValue().toString().length() == 0) {
			return;
		}
		String pRep = ((dataCell) txtReport.getValue()).getValue() + "";
		String pUn = "\"ALL\"";
		String pPn = txtProfile.getValue().toString();

		PreparedStatement psx = con.prepareStatement(
				"select *from cp_rep_profiles where profile_name='" + pPn
						+ "' and report_name='" + pRep + "'",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rsx = psx.executeQuery();
		rsx.beforeFirst();
		while (rsx.next()) {
			String par = rsx.getString("para_name");
			String val = rsx.getString("para_value");
			if (val == null) {
				val = "";
			}

			if (!load_only_data_cols && par.equals(PAR_GROUP_1)) {
				txtGroup1.setValue(utilsVaadin.findByValue(txtGroup1, val));
			}

			if (!load_only_data_cols && par.equals(PAR_GROUP_2)) {
				txtGroup1.setValue(utilsVaadin.findByValue(txtGroup2, val));
			}
			if (!load_only_data_cols && par.equals(PAR_GROUP_1_FILTER)) {
				txtFilter1.setValue(val);
			}
			if (!load_only_data_cols && par.equals(PAR_GROUP_2_FILTER)) {
				txtFilter2.setValue(val);
			}
			if (!load_only_data_cols && par.equals(PAR_PAGE_HEIGHT)) {
				txt_pgHeight.setValue(val);
			}

			if (!load_only_data_cols && par.equals(PAR_PAGE_WIDTH)) {
				txt_pgWidth.setValue(val);
			}

			if (par.startsWith(PAR_DATA_FILTER)) {
				int rn = data_cols.locate("FIELD_NAME",
						par.replaceAll(PAR_DATA_FILTER, ""),
						localTableModel.FIND_EXACT);
				if (rn >= 0) {
					data_cols.setFieldValue(rn, "FILTER_TEXT", val);
					utilsVaadin.refreshRowFromData(data_cols, tbl_cols, rn,
							lstItemCols);
				}
			}

			if (par.startsWith(PAR_DATA_SELECT)) {
				int rn = data_cols.locate("FIELD_NAME",
						par.replaceAll(PAR_DATA_SELECT, ""),
						localTableModel.FIND_EXACT);
				if (rn >= 0) {
					data_cols.setFieldValue(rn, "SELECTION", val);
					utilsVaadin.refreshRowFromData(data_cols, tbl_cols, rn,
							lstItemCols);
				}
			}

		}
		psx.close();
	}

	public void save_profile() throws SQLException {

		if (txtProfile.getValue() == null
				|| txtProfile.getValue().toString().length() == 0) {
			return;
		}

		String strgroup1 = "";
		if (txtGroup1.getValue() != null) {
			strgroup1 = ((dataCell) txtGroup1.getValue()).getValue().toString();
		}
		String strgroup2 = "";
		if (txtGroup2.getValue() != null) {
			strgroup2 = ((dataCell) txtGroup2.getValue()).getValue().toString();
		}
		String strFilter1 = "";
		if (txtFilter1.getValue() != null
				&& txtFilter1.getValue().toString().length() > 0) {
			strFilter1 = txtFilter1.getValue().toString();
		}
		String strFilter2 = "";
		if (txtFilter1.getValue() != null
				&& txtFilter1.getValue().toString().length() > 0) {
			strFilter1 = txtFilter1.getValue().toString();
		}

		String strReport = ((dataCell) txtReport.getValue()).getValue()
				.toString();

		String strUn = "\"ALL\"";
		String strProfile = txtProfile.getValue().toString();
		con.setAutoCommit(false);

		utils.execSql("delete from cp_rep_profiles where usernames='" + strUn
				+ "' and report_name='" + strReport + "' and profile_name='"
				+ strProfile + "'", con);

		QueryExe qe = new QueryExe(
				"insert into cp_rep_profiles(usernames,report_name,profile_name,para_name,para_value,flag) values "
						+ "( '"
						+ strUn
						+ "', '"
						+ strReport
						+ "' ,'"
						+ strProfile + "', :PARA_NAME, :PARA_VALUE,1 )", con);
		qe.parse();

		if (strgroup1.length() > 0) {
			qe.setParaValue("PARA_NAME", PAR_GROUP_1);
			qe.setParaValue("PARA_VALUE", strgroup1);
			qe.execute(false);
		}

		if (strgroup2.length() > 0) {
			qe.setParaValue("PARA_NAME", PAR_GROUP_2);
			qe.setParaValue("PARA_VALUE", strgroup2);
			qe.execute(false);
		}
		if (strFilter1.length() > 0) {
			qe.setParaValue("PARA_NAME", PAR_GROUP_1_FILTER);
			qe.setParaValue("PARA_VALUE", strFilter1);
			qe.execute(false);
		}
		if (strFilter2.length() > 0) {
			qe.setParaValue("PARA_NAME", PAR_GROUP_2_FILTER);
			qe.setParaValue("PARA_VALUE", strFilter2);
			qe.execute(false);
		}

		for (int i = 0; i < data_cols.getRows().size(); i++) {
			String selval = "";
			String fltval = "";
			if (data_cols.getFieldValue(i, "SELECTION") != null) {
				selval = data_cols.getFieldValue(i, "SELECTION").toString();
			}

			if (data_cols.getFieldValue(i, "FILTER_TEXT") != null) {
				fltval = data_cols.getFieldValue(i, "FILTER_TEXT").toString();
			}

			qe.setParaValue("PARA_NAME",
					PAR_DATA_SELECT + data_cols.getFieldValue(i, "FIELD_NAME"));
			qe.setParaValue("PARA_VALUE", selval);
			qe.execute(false);

			qe.setParaValue("PARA_NAME",
					PAR_DATA_FILTER + data_cols.getFieldValue(i, "FIELD_NAME"));
			qe.setParaValue("PARA_VALUE", fltval);
			qe.execute(false);

		}
		qe.close();
		con.commit();
	}

	public void accessSavedParas(Parameter pm) {
		if (mapParaSave.get(pm.getName()) != null) {
			Object toval = mapParaSave.get(pm.getName());
			if (toval instanceof java.util.Date
					&& pm.getValueType().equals(Parameter.DATA_TYPE_DATE)) {
				pm.setValue(toval);
			}
			if (toval instanceof java.util.Date
					&& pm.getValueType().equals(Parameter.DATA_TYPE_DATETIME)) {
				pm.setValue(toval);
			}

			if (toval instanceof java.lang.String
					&& pm.getValueType().equals(Parameter.DATA_TYPE_STRING)) {
				pm.setValue(toval);
			}
			if (toval instanceof Number
					&& pm.getValueType().equals(Parameter.DATA_TYPE_NUMBER)) {
				pm.setValue(toval);
			}

		}
	}
}