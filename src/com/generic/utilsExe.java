package com.generic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.doc.views.YesNoDialog;
import com.doc.views.YesNoDialog.Callback;
import com.generic.queryBuilder.QueryBuilder;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.windows.frmMainMenus;
import com.windows.frmOraApp;
import com.windows.frmQuickRep;

public class utilsExe {
	private static final String COMMAND_QUERY = "QUERY";
	private static final String COMMAND_QUICK_REP = "QUICKREP";
	private static final String COMMAND_FORM = "FORM";
	private static final String COMMAND_PROC = "PROC";
	private static final String COMMAND_EXIT = "EXIT";
	private static final String COMMAND_SWITCH = "SWITCH";
	private static final String SUBCOMMAND_WINDOW = "WINDOW";
	private static final String SUBCOMMAND_CENTRAL_PANEL = "CENTRAL_PANEL";
	private static final String SUBCOMMAND_PRINT = "PRINT";
	private static final String SUBCOMMAND_EXPORT = "EXPORT";

	private static final String QUICKFRM_GROUP_1 = "OBJECT_GROUP_1";
	private static final String QUICKFRM_GROUP_2 = "OBJECT_GROUP_2";
	private static final String QUICKFRM_FILTER_1 = "OBJECT_FILTER_1";
	private static final String QUICKFRM_FILTER_2 = "OBJECT_FILTER_1";
	private static final String QUICKFRM_WHERE_CLAUSE = "OBJECT_WHERE_CLAUSE";
	private static final String QUICKFRM_ORDER_CLAUSE = "OBJECT_ORDER_CLAUSE";

	private static List<String> lastParseList = new ArrayList<String>();
	private static Map<String, String> mapParas = new HashMap<String, String>();
	private static Map<String, String> mapFilters = new HashMap<String, String>();

	public static List<Parameter> added_query_parameter = new ArrayList<Parameter>();
	public static List<Parameter> added_filter_parameter = new ArrayList<Parameter>();

	private static QueryBuilder qb = null;
	private static frmQuickRep qf = null;
	private static AbstractLayout centralPanel = null;
	private static String lastCommand = "";
	private static Window window = new Window();
	private static AbstractLayout layout = null;
	private static VerticalLayout windowlayout = new VerticalLayout();
	private static Window wndx = new Window();
	public static Map<String, transactionalForm> mapForms = new HashMap<String, transactionalForm>();

	public static AbstractLayout getCentralPanel() {
		return centralPanel;
	}

	public static void setCentralPanel(AbstractLayout centralPanel) {
		utilsExe.centralPanel = centralPanel;
	}

	public static List<String> getLastParseList() {
		return lastParseList;
	}

	public static QueryBuilder getQb() {
		return qb;
	}

	public static String getLastCommand() {
		return lastCommand;
	}

	public static Window getWindow() {
		return window;
	}

	public static AbstractLayout getLayout() {
		return layout;
	}

	public static AbstractLayout getWindowlayout() {
		return windowlayout;
	}

	public static void Execute(String cmd, AbstractLayout l) {
		centralPanel = l;
		execute(cmd);
	}

	public static boolean parse(String cmd) {
		String[] temp = null;
		lastParseList.clear();
		mapParas.clear();
		mapFilters.clear();
		temp = cmd.split(" ");
		for (int i = 0; i < temp.length; i++) {
			if (temp[i].indexOf("\"") > -1) {
				boolean otherquotfound = false;
				String s = temp[i].replaceAll("\"", "") + " ";
				if (temp[i].length() > 1
						&& temp[i].substring(temp[i].indexOf("\"") + 1)
								.indexOf("\"") > -1) {
					s = temp[i].replaceAll("\"", "");
					otherquotfound = true;
				}
				for (int j = i + 1; j < temp.length; j++) {
					if (otherquotfound) {
						break;
					}
					i = j;
					if (temp[j].indexOf("\"") > -1) {
						otherquotfound = true;
						s = s + temp[j].replaceAll("\"", "");
						break;
					}
					s = s + temp[j] + " ";
				}
				if (s.trim().length() > 0) {
					lastParseList.add(s);
					addToFilter(s);
					addToParameter(s);
				}
			} else {
				if (temp[i].trim().length() > 0) {
					lastParseList.add(temp[i]);
					addToFilter(temp[i]);
					addToParameter(temp[i]);

				}

			}
		}
		return true;
	}

	public static void addToParameter(String par) {
		if (par.indexOf("=") <= -1) {
			return;
		}
		if (par.substring(0, 1).equals("#")) {
			return;
		}
		Scanner scan = new Scanner(par);
		scan.useDelimiter("\\s*=\\s*");
		String fld = scan.next();
		String val = scan.next();
		val = val.replaceAll("'", "");
		while (scan.hasNext()) {
			val = val + "=" + scan.next();
		}
		mapParas.put(fld.toUpperCase(), val);
	}

	public static void addToFilter(String par) {
		if (par.indexOf("=") <= -1) {
			return;
		}

		if (!par.substring(0, 1).equals("#")) {
			return;
		}
		Scanner scan = new Scanner(par);
		scan.useDelimiter("\\s*=\\s*");
		String fld = scan.next();
		String val = scan.next();
		val = val.replaceAll("'", "");
		mapFilters.put(fld.substring(1).toUpperCase(), val);
	}

	public static String do_function(String vl) {
		String val = vl;
		if (vl.toUpperCase().trim().startsWith("GET_ITEM_PATH")
				&& vl.contains("(") && vl.contains(")")) {
			val = vl.substring(vl.indexOf("(") + 1, vl.indexOf(")"));
			if (val.startsWith(":")) {
				val = utils
						.findParaByName(val.replace(":", "").trim(),
								added_query_parameter).getValue().toString();
			}
			val = utils.getSqlValue(
					"select descr2 from items where reference='" + val + "'",
					Channelplus3Application.getInstance().getFrmUserLogin()
							.getDbc().getDbConnection());
		}
		return val;
	}

	public static boolean execute(String cmd) {
		if (centralPanel == null) {
			return false;
		}
		Connection con = Channelplus3Application.getInstance()
				.getFrmUserLogin().getDbc().getDbConnection();
		parse(cmd);

		// QUICKREP QUERY IDNO [OBJECT_GROUP_1=STRING_ID,
		// OBJECT_GROUP_2=STRING_ID2,
		// OBJECT_FILTER_1=STRING_ID3,OBJECT_FILTER2=STRING_ID4,
		// OBJECT_WHERE_CLAUSE=STRING_ID5
		// OBJECT_ORDER_CLAUSE=STRING_ID6

		if (lastParseList.get(0).equalsIgnoreCase(COMMAND_QUICK_REP)) {
			wndx.setContent(new VerticalLayout());
			wndx.setWidth("90%");
			wndx.setHeight("80%");
			wndx.getContent().setSizeFull();
			wndx.center();
			if (!Channelplus3Application.getInstance().getMainWindow()
					.getChildWindows().contains(wndx)) {
				Channelplus3Application.getInstance().getMainWindow()
						.addWindow(wndx);
			}

			String idno = lastParseList.get(2);

			try {
				qf = new frmQuickRep();
				qf.con = con;
				qf.setParentLayout((AbstractLayout) wndx.getContent());
				qf.setQRYSTR(utils.getSqlValue(
						"select parentrep from invqrycols1 where code='" + idno
								+ "'", con));
				utilsVaadin.FillCombo(qf.getTxtReport(),
						"Select code,titlearb from invqrycols1 where CODE='"
								+ idno + "' order by code", con);
				qf.getTxtReport().setValue(null);
				qf.showInitView();
				qf.getTxtReport().setValue(
						utilsVaadin.findByValue(qf.getTxtReport(), idno));

				if (lastParseList.size() > 2) {
					boolean sys_para = false;
					for (int i = 3; i < lastParseList.size(); i++) {
						sys_para = false;
						if (lastParseList.get(i).toUpperCase()
								.startsWith(QUICKFRM_GROUP_1)) {
							qf.getTxtGroup1().setValue(
									utilsVaadin.findByValue(qf.getTxtGroup1(),
											mapParas.get(QUICKFRM_GROUP_1)));
							sys_para = true;
						}
						if (lastParseList.get(i).toUpperCase()
								.startsWith(QUICKFRM_GROUP_2)) {
							qf.getTxtGroup2().setValue(
									utilsVaadin.findByValue(qf.getTxtGroup2(),
											mapParas.get(QUICKFRM_GROUP_2)));
							sys_para = true;
						}
						if (lastParseList.get(i).toUpperCase()
								.startsWith(QUICKFRM_FILTER_1)) {
							qf.getTxtFilter1().setValue(
									mapParas.get(QUICKFRM_FILTER_1));
							sys_para = true;
						}
						if (lastParseList.get(i).toUpperCase()
								.startsWith(QUICKFRM_FILTER_2)) {
							qf.getTxtFilter2().setValue(
									mapParas.get(QUICKFRM_FILTER_2));
							sys_para = true;
						}
						if (lastParseList.get(i).toUpperCase()
								.startsWith(QUICKFRM_WHERE_CLAUSE)) {
							qf.added_where_clause = mapParas
									.get(QUICKFRM_WHERE_CLAUSE);
							sys_para = true;
						}

						if (!sys_para) {
							String[] p = lastParseList.get(i).split("=");
							added_query_parameter.add(new Parameter(p[0],
									do_function(p[1])));

						}
					}

				}

				qf.added_filter_parameters.clear();
				qf.added_filter_parameters.addAll(added_filter_parameter);
				qf.added_query_parameters.clear();
				qf.added_query_parameters.addAll(added_query_parameter);
				qf.showTreePanel = false;
				qf.RunQuery(idno, false, false);

			} catch (Exception e) {
				e.printStackTrace();
				window.showNotification(e.getMessage(),
						Notification.TYPE_ERROR_MESSAGE);
			}

		}

		// QUERY [WINDOW | CENTRAL_PANEL] idno
		if (lastParseList.get(0).equalsIgnoreCase(COMMAND_QUERY)) {
			layout = centralPanel;
			if (lastParseList.get(1).equalsIgnoreCase(SUBCOMMAND_WINDOW)) {
				layout = windowlayout;
				layout.removeAllComponents();
				window.setContent(layout);
				window.setWidth("90%");
				window.setHeight("80%");
				window.center();
				if (!Channelplus3Application.getInstance().getMainWindow()
						.getChildWindows().contains(window)) {
					Channelplus3Application.getInstance().getMainWindow()
							.addWindow(window);
				}
			}
			qb = new QueryBuilder(con, lastParseList.get(3));
			try {
				layout.setSizeFull();
				qb.setFetchData(false);
				qb.buildSql(true, layout);
				for (Iterator iterator = qb.getQv().getListShowParams()
						.iterator(); iterator.hasNext();) {
					Parameter pm = (Parameter) iterator.next();
					if (mapParas.get(pm.getName().toUpperCase()) != null) {
						String vl = mapParas.get(pm.getName().toUpperCase());
						if (pm.getValueType().equals(Parameter.DATA_TYPE_DATE)) {
							java.util.Date dt = (java.util.Date) pm.getValue();
							if (pm.getValue() == null) {
								dt = new java.util.Date();
							}
							dt.setTime(((new SimpleDateFormat(
									utils.FORMAT_SHORT_DATE)).parse(vl))
									.getTime());
							pm.setValue(mapParas
									.get(pm.getName().toUpperCase()));

						}
						if (pm.getValueType()
								.equals(Parameter.DATA_TYPE_STRING)) {
							pm.setValue(vl);
						}
						if (pm.getValueType()
								.equals(Parameter.DATA_TYPE_NUMBER)) {
							pm.setValue(vl);
						}
					}
				}
				qb.getQv().updateParameterData();
				qb.getQv().fetchData();
				qb.getQv().fill_table();
				qb.getQv().getTable().requestRepaintAll();

				// filtering
				List<Parameter> pmlist = utils.convertFromColumns(qb.getQv()
						.getLctb().getQrycols());
				int no_of_filter = 0;
				for (Iterator iterator = pmlist.iterator(); iterator.hasNext();) {
					Parameter pm = (Parameter) iterator.next();
					if (mapFilters.get(pm.getName().toUpperCase()) != null) {
						String vl = mapFilters.get(pm.getName().toUpperCase());
						if (pm.getValueType().equals(Parameter.DATA_TYPE_DATE)) {
							java.util.Date dt = (java.util.Date) pm.getValue();
							if (pm.getValue() == null) {
								dt = new java.util.Date();
							}
							dt.setTime(((new SimpleDateFormat(
									utils.FORMAT_SHORT_DATE)).parse(vl))
									.getTime());
							pm.setValue(mapFilters.get(pm.getName()
									.toUpperCase()));
							no_of_filter++;
						}
						if (pm.getValueType()
								.equals(Parameter.DATA_TYPE_STRING)) {
							pm.setValue(vl);
							no_of_filter++;
						}
						if (pm.getValueType()
								.equals(Parameter.DATA_TYPE_NUMBER)) {
							pm.setValue(vl);
							no_of_filter++;
						}

					}
				}
				if (no_of_filter > 0) {
					qb.getQv().getLctb().setColumnsFilter(pmlist);
					qb.getQv().fill_table();
					qb.getQv().getTable().requestRepaintAll();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// on exit
		if (lastParseList.get(0).equalsIgnoreCase(COMMAND_EXIT)) {
			try {
				con.rollback();
				con.close();
			} catch (SQLException e) {
			}

			Channelplus3Application.getInstance().close();
			Channelplus3Application
					.getInstance()
					.getMainWindow()
					.open(new ExternalResource(Channelplus3Application
							.getInstance().loginWindow.getURL()));

		}
		// on swtich
		if (lastParseList.get(0).equalsIgnoreCase(COMMAND_SWITCH)) {
			if (lastParseList.size() <= 1) {
				Channelplus3Application.getInstance().getFrmUserLogin()
						.show_module_selection();
			} else {
				Channelplus3Application.getInstance().getFrmUserLogin()
						.show_module_selection(lastParseList.get(1).toString());

			}
		}
		if (lastParseList.get(0).equalsIgnoreCase(COMMAND_FORM)) {
			if (lastParseList.get(1).equalsIgnoreCase("NEW")) {
				mapForms.get(lastParseList.get(2)).initForm();

			}
		}
		return true;
	}

	public static void deleteMenu(final String group, final String code) {
		final Connection con = Channelplus3Application.getInstance()
				.getFrmUserLogin().getDbc().getDbConnection();

		Callback cb = new Callback() {

			public void onDialogResult(boolean resultIsYes) {
				if (!resultIsYes) {
					return;
				}
				try {
					con.setAutoCommit(false);
					String pth = utils.getSqlValue(
							"select menu_path||'%' from cp_main_menus where group_code='"
									+ group + "' and menu_code='" + code + "'",
							con);

					if (!pth.isEmpty()) {
						int r = utils.execSql(
								"delete from cp_main_menus where group_code='"
										+ group + "' and menu_path like '"
										+ pth + "'", con);
						Channelplus3Application.getInstance().getMainWindow()
								.showNotification(r + " row(s) removed.");
					}
					con.commit();
					((frmMainMenus) Channelplus3Application.getInstance()
							.getPurWnd().getContent())
							.change_profile(Channelplus3Application
									.getInstance().getFrmUserLogin().CURRENT_MENU_NAME);
				} catch (SQLException ex) {
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification(ex.getMessage(),
									Notification.TYPE_ERROR_MESSAGE);
					ex.printStackTrace();
					try {
						con.rollback();
					} catch (SQLException e) {

					}
				}

			}
		};
		Channelplus3Application
				.getInstance()
				.getMainWindow()
				.addWindow(
						new YesNoDialog("Channel Plus Warning",
								"Delete Menus for group # "
										+ group
										+ "  , "
										+ (utils.getSqlValue("select name+",
												con)), cb, "40%", "-1px"));

	}

	public static void copyMenu(String group, String code) {
		final Connection con = Channelplus3Application.getInstance()
				.getFrmUserLogin().getDbc().getDbConnection();
		final ComboBox txtFromGroup = new ComboBox("Source Group");
		final ComboBox txtFromCode = new ComboBox("Source Item ");
		final ComboBox txtToGroup = new ComboBox("Copy to Under Group");
		final ComboBox txtToCode = new ComboBox("Copy to Under Item");
		final Button but = new Button("Copy...");
		final Button butMove = new Button("Move...");
		VerticalLayout vl = new VerticalLayout();
		HorizontalLayout hl = new HorizontalLayout();

		final Window wnd = new Window("Copy menu...", vl);
		try {
			utilsVaadin.FillCombo(txtFromGroup,
					"select code,title from cp_main_groups order by code", con);
			utilsVaadin.FillCombo(txtToGroup,
					"select code,title from cp_main_groups order by code", con);
		} catch (SQLException ex) {
			ex.printStackTrace();
			Channelplus3Application
					.getInstance()
					.getMainWindow()
					.showNotification(ex.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
		}
		txtFromGroup.setImmediate(true);
		txtToGroup.setImmediate(true);
		txtFromGroup.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				try {
					utilsVaadin
							.FillCombo(
									txtFromCode,
									"select menu_path,menu_title||'-'||menu_code from cp_main_menus where group_code= '"
											+ ((dataCell) txtFromGroup
													.getValue()).getValue()
													.toString()
											+ "' order by MENU_PATH", con);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		txtToGroup.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				try {
					utilsVaadin
							.FillCombo(
									txtToCode,
									"select menu_path,menu_title||'-'||menu_code from cp_main_menus where childcount>0 and group_code='"
											+ ((dataCell) txtToGroup.getValue())
													.getValue().toString()
											+ "' order by MENU_PATH", con);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		txtFromGroup.setValue(utilsVaadin.findByValue(txtFromGroup, group));
		txtFromCode.setValue(utilsVaadin.findByValue(txtFromCode, utils
				.getSqlValue(
						"select menu_path from cp_main_menus where menu_code='"
								+ code + "'", con)));

		vl.setMargin(true);
		wnd.setWidth("30%");
		wnd.setHeight("40%");
		wnd.center();
		wnd.setModal(true);
		ResourceManager.addComponent(vl, txtFromGroup);
		ResourceManager.addComponent(vl, txtFromCode);
		ResourceManager.addComponent(vl, txtToGroup);
		ResourceManager.addComponent(vl, txtToCode);
		ResourceManager.addComponent(vl, hl);
		ResourceManager.addComponent(hl, but);
		ResourceManager.addComponent(hl, butMove);

		butMove.setData("move");
		but.setData("copy");
		ClickListener clklistner = new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if (txtFromGroup.getValue() == null
						|| txtFromCode.getValue() == null
						|| txtToGroup.getValue() == null) {
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification(
									"Must enter source data Or Destination group",
									Notification.TYPE_ERROR_MESSAGE);
					return;
				}
				String strFromGroup = ((dataCell) txtFromGroup.getValue())
						.getValue().toString();
				String strFromCodePath = ((dataCell) txtFromCode.getValue())
						.getValue().toString();
				String strFromCode = utils.getSqlValue(
						"select menu_code from cp_main_menus where menu_path='"
								+ ((dataCell) txtFromCode.getValue())
										.getValue().toString() + "'", con);
				String strToGroup = ((dataCell) txtToGroup.getValue())
						.getValue().toString();
				String strToItem = "";
				String strToItemPath = "";
				if (txtToCode.getValue() != null) {
					strToItem = utils.getSqlValue(
							"select menu_code from cp_main_menus where menu_path='"
									+ ((dataCell) txtToCode.getValue())
											.getValue().toString() + "'", con);
					strToItemPath = ((dataCell) txtToCode.getValue())
							.getValue().toString();
				}

				try {
					PreparedStatement pst = con.prepareStatement(
							"select *from cp_main_menus where menu_path like '"
									+ strFromCodePath + "%'",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					ResultSet rst = pst.executeQuery();
					rst.beforeFirst();
					QueryExe qe = new QueryExe(
							"declare cnt NUMBER; BEGIN insert into cp_main_menus(GROUP_CODE,MENU_CODE,MENU_TITLE,TYPE_OF_EXEC,EXEC_LINE,MENU_PATH,PARENT_MENUCODE,CHILDCOUNT,RESERVED1,RESERVED2) VALUES "
									+ " (:GROUP_CODE,:MENU_CODE,:MENU_TITLE,:TYPE_OF_EXEC,:EXEC_LINE,:MENU_PATH,:PARENT_MENUCODE , :CHILDCOUNT ,:RESERVED1 , :RESERVED2 ) ; "
									+ "select nvl(count(*),0) into cnt from cp_main_menus where group_code=:GROUP_CODE and menu_code=:PARENT_MENUCODE ; "
									+ "update cp_main_menus set childcount=cnt where group_code=:GROUP_CODE and menu_code=:PARENT_MENUCODE ;"
									+ "END;", con);
					qe.parse();
					String strCode = "";
					String strPath = "";
					String strParent = "";
					String strChildcount = "0";
					int usecount = 0;
					while (rst.next()) {
						if (usecount == 0) {
							strParent = strToItem;
						} else {
							strParent = rst.getString("PARENT_MENUCODE");
							strParent = utils.getSqlValue(
									"select MENU_CODE from CP_MAIN_MENUS WHERE menu_path like '"
											+ strToItemPath + "%' and "
											+ " reserved2='" + strParent + "'",
									con);

						}
						String strtmp = "select nvl(max(to_number(menu_code)),'"
								+ strParent
								+ "'||'00')+1 from cp_main_menus where group_code='"
								+ strToGroup
								+ "' and parent_menucode='"
								+ strParent + "'";
						if (strParent.isEmpty()) {
							strtmp = "	select nvl(max(to_number(menu_code)),0)+1 from cp_main_menus where group_code='"
									+ strToGroup
									+ "' and parent_menucode is null";
						}
						strCode = utils.getSqlValue(strtmp, con);
						if (strCode.equals("1")) {
							strCode = strToGroup + "01";
						}
						strChildcount = rst.getString("childcount");
						strPath = utils.generateMenuPath(strToGroup, strParent,
								strCode);
						qe.setParaValue("GROUP_CODE", strToGroup);
						qe.setParaValue("MENU_CODE", strCode);
						qe.setParaValue("MENU_TITLE",
								rst.getString("MENU_TITLE"));
						qe.setParaValue("TYPE_OF_EXEC",
								rst.getString("TYPE_OF_EXEC"));
						qe.setParaValue("EXEC_LINE", rst.getString("EXEC_LINE"));
						qe.setParaValue("MENU_PATH", strPath);
						qe.setParaValue("PARENT_MENUCODE", strParent);
						qe.setParaValue("CHILDCOUNT", strChildcount);
						qe.setParaValue("RESERVED1", strFromGroup);
						qe.setParaValue("RESERVED2", rst.getString("MENU_CODE"));
						qe.execute();
						usecount++;
					}
					qe.close();
					pst.close();
					if (event.getButton().getData().toString().equals("move")
							&& strFromCodePath.isEmpty() == false) {
						utils.execSql(
								"delete from cp_main_menus where menu_path like '"
										+ strFromCodePath + "%'", con);

					}

					con.commit();
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification(
									"copied successfully under " + strToItem);
					((frmMainMenus) Channelplus3Application.getInstance()
							.getPurWnd().getContent())
							.change_profile(Channelplus3Application
									.getInstance().getFrmUserLogin().CURRENT_MENU_NAME);

				} catch (SQLException e) {
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification(e.getMessage(),
									Notification.TYPE_ERROR_MESSAGE);

					e.printStackTrace();
					try {
						con.rollback();
					} catch (SQLException ex) {

					}
				}
				if (!Channelplus3Application.getInstance().getWindows()
						.contains(wnd)) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);
				}

			}
		};
		but.addListener(clklistner);
		butMove.addListener(clklistner);
		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wnd)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wnd);
		}

	}

	public static void createMenu(String group, String parentMenu) {
		final Connection con = Channelplus3Application.getInstance()
				.getFrmUserLogin().getDbc().getDbConnection();
		final TextField txtCode = new TextField("Code");
		final TextField txtDescr = new TextField("Descr");
		final ComboBox txtExeLine = new ComboBox("Execution Line: ");
		final ComboBox txtTypeOfExe = new ComboBox("Execution Type: ");
		final ComboBox txtParent = new ComboBox("Parent Code");
		final ComboBox txtGroup = new ComboBox("Group");
		
		final Window wnd = ControlsFactory
				.CreateWindow("40%", "60%", true, true);
		VerticalLayout vl =(VerticalLayout) wnd.getContent();
		try {
			utilsVaadin.FillCombo(txtGroup,
					"select code,title from cp_main_groups order by code", con);
			utilsVaadin
					.FillCombo(
							txtParent,
							"select menu_code,menu_title||'-'||menu_code from cp_main_menus where childcount>0 order by MENU_CODE",
							con);
			utilsVaadin
					.FillCombo(
							txtExeLine,
							"select distinct exec_line,exec_line from cp_main_menus where exec_line is not null",
							con);

			utilsVaadin
					.FillCombo(
							txtTypeOfExe,
							"select distinct type_of_exec,type_of_exec from cp_main_menus where type_of_exec is not null",
							con);
		} catch (SQLException ex) {
			ex.printStackTrace();
			Channelplus3Application
					.getInstance()
					.getMainWindow()
					.showNotification(ex.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
		}
		txtTypeOfExe.setNullSelectionAllowed(false);

		txtTypeOfExe.setValue(utilsVaadin.findByValue(txtTypeOfExe, "FORM"));
		txtTypeOfExe.setRequired(true);
		txtGroup.setRequired(true);

		Button but = new Button("Add this menu");

		ResourceManager.addComponent(vl, txtGroup);
		ResourceManager.addComponent(vl, txtParent);
		ResourceManager.addComponent(vl, txtCode);
		ResourceManager.addComponent(vl, txtDescr);
		ResourceManager.addComponent(vl, txtTypeOfExe);
		ResourceManager.addComponent(vl, txtExeLine);
		ResourceManager.addComponent(vl, but);

		txtGroup.setWidth("100%");
		txtCode.setWidth("100%");
		txtExeLine.setWidth("100%");
		txtDescr.setWidth("100%");
		txtParent.setWidth("100%");
		txtTypeOfExe.setWidth("100%");

		txtExeLine.setImmediate(true);
		txtExeLine.setNewItemsAllowed(true);
		txtExeLine.setNewItemHandler(new NewItemHandler() {

			public void addNewItem(String newItemCaption) {
				dataCell dc = new dataCell(newItemCaption, newItemCaption);
				txtExeLine.addItem(dc);
				txtExeLine.setValue(dc);
			}
		});
		txtTypeOfExe.setImmediate(true);
		txtTypeOfExe.setNewItemsAllowed(true);
		txtTypeOfExe.setNewItemHandler(new NewItemHandler() {

			public void addNewItem(String newItemCaption) {
				dataCell dc = new dataCell(newItemCaption, newItemCaption);
				txtTypeOfExe.addItem(dc);
				txtTypeOfExe.setValue(dc);
			}
		});
		if (!group.isEmpty()) {
			txtGroup.setValue(utilsVaadin.findByValue(txtGroup, group));
		}
		if (!parentMenu.isEmpty()) {
			txtParent.setValue(utilsVaadin.findByValue(txtParent, parentMenu));
		}

		String strtmp = "	select nvl(max(to_number(menu_code)),'" + parentMenu
				+ "'||'00')+1 from cp_main_menus where group_code='" + group
				+ "' and parent_menucode='" + parentMenu + "'";
		if (parentMenu.isEmpty()) {
			strtmp = "	select nvl(max(to_number(menu_code)),0)+1 from cp_main_menus where group_code='"
					+ group + "' and parent_menucode is null";
		}
		txtCode.setValue(utils.getSqlValue(strtmp, con));
		strtmp = txtCode.getValue().toString();
		if (strtmp.equals("1")) {
			txtCode.setValue(group + "01");
		}

		txtDescr.focus();
		wnd.setCaption("Adding sub menu : " + txtParent.getValue().toString());
		vl.setMargin(true);

		but.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					con.setAutoCommit(false);
					if (txtGroup.getValue() == null
							|| txtGroup.getValue().toString().isEmpty()) {
						wnd.showNotification("Group must enter",
								Notification.TYPE_ERROR_MESSAGE);
						return;
					}
					if (txtGroup.getValue() == null
							|| txtGroup.getValue().toString().isEmpty()) {
						wnd.showNotification(" must enter",
								Notification.TYPE_ERROR_MESSAGE);
						return;
					}
					String strParent = "";
					String strDescr = "";
					String strCode = "";
					String strTypeOfExe = "";
					String strExeLine = "";
					String strChildcount = "0";

					String strGroup = ((dataCell) (txtGroup.getValue()))
							.getValue().toString();

					if (txtDescr.getValue() != null
							&& txtDescr.getValue().toString().length() > 0) {
						strDescr = txtDescr.getValue().toString();
					}
					if (txtExeLine.getValue() != null
							&& txtExeLine.getValue().toString().length() > 0) {
						strExeLine = txtExeLine.getValue().toString();
					}

					if (txtParent.getValue() != null
							&& !txtParent.getValue().toString().isEmpty()) {
						strParent = ((dataCell) (txtParent.getValue()))
								.getValue().toString();
					}

					if (txtTypeOfExe.getValue() != null
							&& !txtTypeOfExe.getValue().toString().isEmpty()) {
						strTypeOfExe = ((dataCell) (txtTypeOfExe.getValue()))
								.getValue().toString();
					}

					if (txtCode.getValue() == null
							|| txtCode.getValue().toString().isEmpty()) {
						String strtmp = "	select nvl(max(to_number(menu_code)),'"
								+ strParent
								+ "'||'00')+1 from cp_main_menus where group_code='"
								+ strGroup
								+ "' and parent_menucode='"
								+ strParent + "'";
						if (strParent.isEmpty()) {
							strtmp = "	select nvl(max(to_number(menu_code)),0)+1 from cp_main_menus where group_code='"
									+ strGroup
									+ "' and parent_menucode is null";
						}
						txtCode.setValue(utils.getSqlValue(strtmp, con));
						strCode = txtCode.getValue().toString();
						if (strCode.equals("1")) {
							txtCode.setValue(strGroup + "01");
							strCode = strGroup + "01";
						}
					} else {
						strCode = txtCode.getValue().toString();
					}
					String strPath = utils.generateMenuPath(strGroup,
							strParent, strCode);

					if (strTypeOfExe.equals("PARENT")) {
						strChildcount = "1";
					}
					QueryExe qe = new QueryExe(
							"declare cnt NUMBER; BEGIN insert into cp_main_menus(GROUP_CODE,MENU_CODE,MENU_TITLE,TYPE_OF_EXEC,EXEC_LINE,MENU_PATH,PARENT_MENUCODE,CHILDCOUNT) VALUES "
									+ " (:GROUP_CODE,:MENU_CODE,:MENU_TITLE,:TYPE_OF_EXEC,:EXEC_LINE,:MENU_PATH,:PARENT_MENUCODE , :CHILDCOUNT ) ; "
									+ "select nvl(count(*),0) into cnt from cp_main_menus where group_code=:GROUP_CODE and menu_code=:PARENT_MENUCODE ; "
									+ "update cp_main_menus set childcount=cnt where group_code=:GROUP_CODE and menu_code=:PARENT_MENUCODE ;"
									+ "END;", con);
					qe.setParaValue("GROUP_CODE", strGroup);
					qe.setParaValue("MENU_CODE", strCode);
					qe.setParaValue("MENU_TITLE", strDescr);
					qe.setParaValue("TYPE_OF_EXEC", strTypeOfExe);
					qe.setParaValue("EXEC_LINE", strExeLine);
					qe.setParaValue("MENU_PATH", strPath);
					qe.setParaValue("PARENT_MENUCODE", strParent);
					qe.setParaValue("CHILDCOUNT", strChildcount);
					qe.execute();
					qe.close();
					con.commit();
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification(
									"Added successfully under " + strParent);
					((frmMainMenus) Channelplus3Application.getInstance()
							.getPurWnd().getContent())
							.change_profile(Channelplus3Application
									.getInstance().getFrmUserLogin().CURRENT_MENU_NAME);
				} catch (Exception ex) {
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification(ex.getMessage(),
									Notification.TYPE_ERROR_MESSAGE);
					ex.printStackTrace();
					try {
						con.rollback();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!Channelplus3Application.getInstance().getWindows()
						.contains(wnd)) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);
				}
			}
		});

		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wnd)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wnd);
		}

	}

	public static void editMenu(String group, String menu) throws SQLException {

		final Connection con = Channelplus3Application.getInstance()
				.getFrmUserLogin().getDbc().getDbConnection();
		final TextField txtCode = new TextField("Code");
		final TextField txtDescr = new TextField("Descr");
		final ComboBox txtExeLine = new ComboBox("Execution Line: ");
		final ComboBox txtTypeOfExe = new ComboBox("Execution Type: ");
		final ComboBox txtParent = new ComboBox("Parent Code");
		final ComboBox txtGroup = new ComboBox("Group");
		final CheckBox chkExpand = new CheckBox("Expand", false);
		final CheckBox chkShort = new CheckBox("Add to Shortcut", false);
		final Window wnd = ControlsFactory
				.CreateWindow("40%", "60%", true, true);		
		VerticalLayout vl =(VerticalLayout) wnd.getContent();
		utilsVaadin.FillCombo(txtGroup,
				"select code,title from cp_main_groups order by code", con);
		utilsVaadin
				.FillCombo(
						txtParent,
						"select menu_code,menu_title||'-'||menu_code from cp_main_menus where childcount>0 order by MENU_CODE",
						con);
		utilsVaadin
				.FillCombo(
						txtExeLine,
						"select distinct exec_line,exec_line from cp_main_menus where exec_line is not null",
						con);

		utilsVaadin
				.FillCombo(
						txtTypeOfExe,
						"select distinct type_of_exec,type_of_exec from cp_main_menus where type_of_exec is not null",
						con);

		txtTypeOfExe.setNullSelectionAllowed(false);

		txtTypeOfExe.setValue(utilsVaadin.findByValue(txtTypeOfExe, "FORM"));
		txtTypeOfExe.setRequired(true);
		txtGroup.setRequired(true);

		Button but = new Button("Update & Close");

		ResourceManager.addComponent(vl, txtGroup);
		ResourceManager.addComponent(vl, txtParent);
		ResourceManager.addComponent(vl, txtCode);
		ResourceManager.addComponent(vl, txtDescr);
		ResourceManager.addComponent(vl, txtTypeOfExe);
		ResourceManager.addComponent(vl, txtExeLine);
		ResourceManager.addComponent(vl, chkExpand);
		ResourceManager.addComponent(vl, chkShort);
		ResourceManager.addComponent(vl, but);

		txtGroup.setWidth("100%");
		txtCode.setWidth("100%");
		txtExeLine.setWidth("100%");
		txtDescr.setWidth("100%");
		txtParent.setWidth("100%");
		txtTypeOfExe.setWidth("100%");

		txtExeLine.setImmediate(true);
		txtExeLine.setNewItemsAllowed(true);
		txtExeLine.setNewItemHandler(new NewItemHandler() {

			public void addNewItem(String newItemCaption) {
				dataCell dc = new dataCell(newItemCaption, newItemCaption);
				txtExeLine.addItem(dc);
				txtExeLine.setValue(dc);
			}
		});
		txtTypeOfExe.setImmediate(true);
		txtTypeOfExe.setNewItemsAllowed(true);
		txtTypeOfExe.setNewItemHandler(new NewItemHandler() {

			public void addNewItem(String newItemCaption) {
				dataCell dc = new dataCell(newItemCaption, newItemCaption);
				txtTypeOfExe.addItem(dc);
				txtTypeOfExe.setValue(dc);
			}
		});

		String strtmp = "	select *from cp_main_menus where group_code='"
				+ group + "' and menu_code='" + menu + "'";
		ResultSet rst = utils.getSqlRS(strtmp, con);
		if (rst == null || !rst.first())
			throw new SQLException("Some user have deleted");
		txtCode.setValue(rst.getString("menu_code"));
		txtCode.setEnabled(false);
		txtDescr.setValue(rst.getString("MENU_TITLE"));
		if (rst.getString("exec_line") != null)
			txtExeLine.setValue(utilsVaadin.findByValue(txtExeLine,
					rst.getString("exec_line")));
		txtGroup.setValue(utilsVaadin.findByValue(txtGroup,
				rst.getString("group_code")));
		txtParent.setValue(utilsVaadin.findByValue(txtParent,
				rst.getString("PARENT_MENUCODE")));
		txtTypeOfExe.setValue(utilsVaadin.findByValue(txtTypeOfExe,
				rst.getString("TYPE_OF_EXEC")));
		if (rst.getString("EXPAND_NODE").equals("Y"))
			chkExpand.setValue(true);
		else
			chkExpand.setValue(false);

		if (rst.getString("SHORTCUT").equals("Y"))
			chkShort.setValue(true);
		else
			chkShort.setValue(false);

		rst.close();

		txtGroup.setEnabled(false);
		txtParent.setEnabled(false);
		wnd.setCaption("Adding sub menu : " + txtDescr.getValue());
		txtDescr.focus();
		vl.setMargin(true);

		but.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					con.setAutoCommit(false);
					if (txtGroup.getValue() == null
							|| txtGroup.getValue().toString().isEmpty()) {
						wnd.showNotification("Group must enter",
								Notification.TYPE_ERROR_MESSAGE);
						return;
					}
					if (txtGroup.getValue() == null
							|| txtGroup.getValue().toString().isEmpty()) {
						wnd.showNotification(" must enter",
								Notification.TYPE_ERROR_MESSAGE);
						return;
					}
					String strParent = "";
					String strDescr = "";
					String strCode = "";
					String strTypeOfExe = "";
					String strExeLine = "";
					String strChildcount = "0";

					String strGroup = ((dataCell) (txtGroup.getValue()))
							.getValue().toString();

					if (txtDescr.getValue() != null
							&& txtDescr.getValue().toString().length() > 0) {
						strDescr = txtDescr.getValue().toString();
					}
					if (txtExeLine.getValue() != null
							&& txtExeLine.getValue().toString().length() > 0) {
						strExeLine = txtExeLine.getValue().toString();
					}

					if (txtParent.getValue() != null
							&& !txtParent.getValue().toString().isEmpty()) {
						strParent = ((dataCell) (txtParent.getValue()))
								.getValue().toString();
					}

					if (txtTypeOfExe.getValue() != null
							&& !txtTypeOfExe.getValue().toString().isEmpty()) {
						strTypeOfExe = ((dataCell) (txtTypeOfExe.getValue()))
								.getValue().toString();
					}

					strCode = txtCode.getValue().toString();
					String strPath = utils.generateMenuPath(strGroup,
							strParent, strCode);

					if (strTypeOfExe.equals("PARENT")) {
						strChildcount = "1";
					}
					String strExpand = (chkExpand.booleanValue() ? "Y" : "N");
					String strShort = (chkShort.booleanValue() ? "Y" : "N");
					QueryExe qe = new QueryExe(
							"declare cnt NUMBER; BEGIN delete from cp_main_menus where group_code=:GROUP_CODE and menu_code=:MENU_CODE ; "
									+ "insert into cp_main_menus(GROUP_CODE,MENU_CODE,MENU_TITLE,TYPE_OF_EXEC,EXEC_LINE,MENU_PATH,PARENT_MENUCODE,CHILDCOUNT ,EXPAND_NODE , SHORTCUT ) VALUES "
									+ " (:GROUP_CODE,:MENU_CODE,:MENU_TITLE,:TYPE_OF_EXEC,:EXEC_LINE,:MENU_PATH,:PARENT_MENUCODE , :CHILDCOUNT , :EXPAND_NODE , :SHORTCUT ) ; "
									+ "select nvl(count(*),0) into cnt from cp_main_menus where group_code=:GROUP_CODE and menu_code=:PARENT_MENUCODE ; "
									+ "update cp_main_menus set childcount=cnt where group_code=:GROUP_CODE and menu_code=:PARENT_MENUCODE ;"
									+ "END;", con);
					qe.setParaValue("GROUP_CODE", strGroup);
					qe.setParaValue("MENU_CODE", strCode);
					qe.setParaValue("MENU_TITLE", strDescr);
					qe.setParaValue("TYPE_OF_EXEC", strTypeOfExe);
					qe.setParaValue("EXEC_LINE", strExeLine);
					qe.setParaValue("MENU_PATH", strPath);
					qe.setParaValue("PARENT_MENUCODE", strParent);
					qe.setParaValue("CHILDCOUNT", strChildcount);
					qe.setParaValue("EXPAND_NODE", strExpand);
					qe.setParaValue("SHORTCUT", strShort);
					qe.execute();
					qe.close();
					con.commit();
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification(
									"Added successfully under " + strParent);
					((frmMainMenus) Channelplus3Application.getInstance()
							.getPurWnd().getContent())
							.change_profile(Channelplus3Application
									.getInstance().getFrmUserLogin().CURRENT_MENU_NAME);
				} catch (Exception ex) {
					Channelplus3Application
							.getInstance()
							.getMainWindow()
							.showNotification(ex.getMessage(),
									Notification.TYPE_ERROR_MESSAGE);
					ex.printStackTrace();
					try {
						con.rollback();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!Channelplus3Application.getInstance().getWindows()
						.contains(wnd)) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);
				}
			}
		});

		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wnd)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wnd);
		}

	}

	public static boolean OpenForm(AbstractLayout l, menuItem menusel,
			boolean createForm) throws Exception {
		frmMainMenus f = (frmMainMenus) Channelplus3Application.getInstance()
				.getPurWnd().getContent();
		Map<String, transactionalForm> frms = f.mapForms;

		Map<String, transactionalForm> frms2 = new HashMap<String, transactionalForm>();
		for (Iterator iterator = frms.keySet().iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			Class c = frms.get(s).getClass();
			transactionalForm t = (transactionalForm) c.newInstance();
			frms2.put(s, t);
			t.setParentLayout(l);
		}

		String s = menusel.getId();
		menuItem cm = menusel;
		int profileno = Channelplus3Application.getInstance().getFrmUserLogin().CURRENT_PROFILENO;
		if (cm.getPara4Val() != null
				&& !cm.getPara4Val().toString().isEmpty()
				&& (cm.getPara4Val().toString()
						.indexOf("\"" + profileno + "\"") <= -1 && cm
						.getPara4Val().toString().indexOf("\"" + 0 + "\"") <= -1)) {
			l.getWindow().showNotification("Access Denied !",
					Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		l.removeAllComponents();
		l.setWidth("100%");
		l.setHeight("-1px"); // Undefined height

		if (l.getWindow() != Channelplus3Application.getInstance()
				.getMainWindow())
			l.getWindow().setCaption(
					menusel.getDisplay() + " - "
							+ menusel.getPara2Val().toString());
		if (menusel.getPara1Val().toString().equals(frmMainMenus.TYPE_FORM))
			if (!createForm)
				frms.get(menusel.getPara2Val().toString()).showInitView();
			else
				frms2.get(menusel.getPara2Val().toString()).showInitView();

		if (menusel.getPara1Val().toString().equals(frmMainMenus.QUICK_REP)) {
			l.setCaption(menusel.getDisplay() + " - "
					+ menusel.getPara2Val().toString());

			frmQuickRep qcfrm = (frmQuickRep) frms2
					.get(frmMainMenus.FORM_QUICKREP);
			qcfrm.setQRYSTR(menusel.getPara2Val().toString());
			qcfrm.showInitView();
		}
		if (menusel.getPara1Val().toString().equals("ORA_APP")) {
			frmOraApp frmor = (frmOraApp) frms2.get("ORA_APP");
			frmor.setQRYSES(menusel.getPara2Val().toString());
			frmor.showInitView();
		}

		if (menusel.getPara1Val().toString().equals(frmMainMenus.TYPE_COMMAND)) {
			l.removeAllComponents();
			l.setSizeFull();
			utilsExe.setCentralPanel(l);
			String title = menusel.getDisplay();
			utilsExe.getWindow().setCaption(title);
			utilsExe.execute(menusel.getPara2Val().toString());

		}
		if (menusel.getPara1Val().toString().equals(frmMainMenus.TYPE_QUERY)) {
			String title = menusel.getDisplay();
			f.showQuery(menusel.getPara2Val().toString(), title);
		}
		if (menusel.getPara1Val().toString().equals(frmMainMenus.TYPE_SWITCH)) {
			if (menusel.getPara1Val() != null
					&& menusel.getPara1Val().toString().length() > 0) {
				Channelplus3Application
						.getInstance()
						.getFrmUserLogin()
						.show_module_selection(menusel.getPara1Val().toString());
			} else {
				Channelplus3Application.getInstance().getFrmUserLogin()
						.show_module_selection();
			}
		}
		return true;

	}
}