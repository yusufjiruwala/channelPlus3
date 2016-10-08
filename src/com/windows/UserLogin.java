package com.windows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.generic.DBClass;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class UserLogin extends VerticalLayout {
	private Label lblCompanyName = new Label(utils.textCompanyName);
	private Label specifiecation = new Label(utils.textSpec1);
	private Label specifiecation2 = new Label(utils.textSpec2);
	public VerticalLayout listLayout = new VerticalLayout();
	private TextField txtUser = new TextField("Enter User Name :");
	private PasswordField txtPwd = new PasswordField("Enter Password :");
	private TextField txtDBServer = new TextField("Database Server :");
	private TextField txtOracleSID = new TextField("Oracle Database :", "ORCL");
	private Button cmdLogon = new Button("Login");
	private DBClass dbc = null;
	private DBClass dbcInit = null;
	private Map<String, String> mapVars = new HashMap<String, String>();
	private float profileno = 0;
	private moduleSelection selectLayout = new moduleSelection();
	private Window wndlist = new Window();
	private Panel pnl = new Panel();
	public int CURRENT_PROFILENO = 0;
	public static String FORMAT_MONEY = "#,##0.000;(#,##0.000)";
	public static String FORMAT_QTY = "#,##0.###;(#,##0.###)";
	public static String FORMAT_SHORT_DATE = "dd/MM/yyyy";

	public static Map<String, String> mapMsgs = new HashMap<String, String>();
	public String CURRENT_MENU_CODE = "";
	public String CURRENT_MENU_NAME = "";
	public String CURRENT_MENU_MODULE = "";
	public String COMPANY_NAME="";
	public String COMPANY_NAMEA="";
	public String COMPANY_SPECS="";
	public String COMPANY_SPECSA="";

	public Button getCmdLogon() {
		return cmdLogon;
	}

	public Map<String, String> getMapVars() {
		return mapVars;
	}

	public Label getLblCompanyName() {
		return lblCompanyName;
	}

	public Label getSpecifiecation() {
		return specifiecation;
	}

	public Label getSpecifiecation2() {
		return specifiecation2;
	}

	public DBClass getDbc() {
		return dbc;
	}

	public UserLogin() {
		super();
		mapMsgs.put("SECURITY_SHOW_BEFORE_INSERT",
				"You are not allow to INSERT new record here..! ");
		mapMsgs.put("SECURITY_SHOW_BEFORE_UPDATE",
				"You are not allow to UPDATE any record here..! ");
		mapMsgs.put("SECURITY_SHOW_ON_UPDATE",
				"Update denied: due to unauthorized access !");
		mapMsgs.put("SECURITY_SHOW_ON_INSERT",
				"Insert denied: due to unauthorized access !");
		mapMsgs.put("SECURITY_ACCESS_DENIED",
				"Access denied: Unauthorized access !");

		// initComponents();
	}

	public void initComponents() {
		removeAllComponents();
		pnl.removeAllComponents();
		setSizeFull();
		VerticalLayout vl = new VerticalLayout();
		GridLayout grd = new GridLayout(1, 3);
		GridLayout grd2 = new GridLayout(1, 3);
		grd2.addComponent(lblCompanyName);
		grd2.addComponent(specifiecation);
		grd2.addComponent(specifiecation2);
		grd.addComponent(txtUser);
		grd.addComponent(txtPwd);
		grd.addComponent(txtDBServer);
		grd.addComponent(txtOracleSID);
		pnl.addComponent(vl);
		vl.addComponent(grd2);
		vl.addComponent(grd);
		vl.addComponent(cmdLogon);
		addComponent(pnl);

		vl.setComponentAlignment(grd, Alignment.MIDDLE_CENTER);
		vl.setComponentAlignment(grd2, Alignment.MIDDLE_CENTER);
		vl.setComponentAlignment(cmdLogon, Alignment.MIDDLE_CENTER);
		setComponentAlignment(pnl, Alignment.MIDDLE_CENTER);

		lblCompanyName.setStyleName("loginHead");

		load_database_info();
		cmdLogon.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if (!login()) {
				} else {
					show_module_selection();
				}

			}

		});

	}

	public void show_module_selection(String module) {
		utilsVaadin.switchProfile(listLayout, module);
	}

	public void show_module_selection() {
		utilsVaadin.switchProfile(listLayout);
		/*
		 * save_databae_info(); wndlist.setWidth("50%");
		 * wndlist.setHeight("30%"); wndlist.setImmediate(true);
		 * wndlist.setModal(true); wndlist.setContent(selectLayout);
		 * wndlist.setClosable(false); wndlist.setCaption("Select Module here");
		 * selectLayout.createView();
		 * 
		 * if (!getWindow().getChildWindows().contains(wndlist)) {
		 * getWindow().addWindow(wndlist); }
		 */
	}

	private void load_database_info() {
		mapVars.clear();
		String fl = ((WebApplicationContext) getWindow().getApplication()
				.getContext()).getHttpSession().getServletContext()
				.getRealPath("/WEB-INF")
				+ "/dbinfo.ini";
		try {
			utils.readVars(mapVars, fl);

		} catch (Exception e) {
			Logger.getLogger(UserLogin.class.getName()).log(Level.SEVERE, null,
					e);
		}

		if (mapVars.get("dburl") == null) {
			mapVars.put("dburl", "jdbc:oracle:thin:@");
		}
		if (mapVars.get("server") == null) {
			mapVars.put("server", "localhost");
		}
		if (mapVars.get("sid") == null) {
			mapVars.put("sid", "orcl");
		}
		if (mapVars.get("port") == null) {
			mapVars.put("port", "1521");
		}
		if (mapVars.get("user") == null) {
			mapVars.put("user", "c55");
		}
		if (mapVars.get("password") == null) {
			mapVars.put("password", "abcd");
		}

		txtDBServer.setValue(mapVars.get("server"));
		txtOracleSID.setValue(mapVars.get("sid"));

	}

	private void save_databae_info() {
		String fl = ((WebApplicationContext) Channelplus3Application
				.getInstance().getContext()).getHttpSession()
				.getServletContext().getRealPath("/WEB-INF")
				+ "/dbinfo.ini";
		File outFile = new File(fl);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			writer.write("server=" + txtDBServer.getValue().toString());
			writer.newLine();
			writer.write("sid=" + txtOracleSID.getValue().toString());
			writer.newLine();
			writer.write("port=" + mapVars.get("port"));
			writer.newLine();
			writer.write("dburl=" + mapVars.get("dburl"));
			writer.newLine();
			writer.write("user=" + mapVars.get("user"));
			writer.newLine();
			writer.write("password=" + mapVars.get("password"));
			writer.close();
		} catch (IOException e) {
			Logger.getLogger(UserLogin.class.getName()).log(Level.SEVERE, null,
					e);
			getWindow().showNotification("Error in saving db info file", "",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void buildProfiles() {
		// mapVars.clear();
		if (dbc == null) {
			return;
		}
		try {
			PreparedStatement ps_prof = dbc
					.getDbConnection()
					.prepareStatement(
							"select variable,value from cp_user_profiles where (profileno=? OR PROFILENO=0) ORDER BY profileno,variable ",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps_prof.setFloat(1, profileno);
			ResultSet rs = ps_prof.executeQuery();
			rs.beforeFirst();
			while (rs.next()) {
				mapVars.put(rs.getString("VARIABLE"), rs.getString("VALUE"));
			}
			ResultSet rst = utils
					.getSqlRS(
							"select name,namea,SPECIFICATION,SPECIFICATIONA from company where crnt='X'",
							dbc.getDbConnection());
			utils.COMPANY_NAME = rst.getString("NAME");
			utils.COMPANY_NAMEA = rst.getString("NAMEA");
			utils.COMPANY_SPECS = rst.getString("SPECIFICATION");
			utils.COMPANY_SPECSA = rst.getString("SPECIFICATIONA");
			COMPANY_NAME = rst.getString("NAME");
			COMPANY_NAMEA = rst.getString("NAMEA");
			COMPANY_SPECS = rst.getString("SPECIFICATION");
			COMPANY_SPECSA = rst.getString("SPECIFICATIONA");

			rst.close();

		} catch (SQLException e) {
			Logger.getLogger(UserLogin.class.getName()).log(Level.SEVERE, null,
					e);
			getWindow().showNotification("Error in Building Profiles",
					e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public boolean login() {

		try {
			if (dbcInit == null) {
				dbcInit = new DBClass(mapVars.get("dburl")
						+ txtDBServer.getValue().toString() + ":"
						+ mapVars.get("port") + ":"
						+ txtOracleSID.getValue().toString(),
						mapVars.get("user"), mapVars.get("password"));
			}
			// check either user existed in system.bosusers.
			PreparedStatement ps = null;
			ResultSet rst = null;
			ps = dbcInit.getDbConnection().prepareStatement(
					"select username,cp_user from system.bosusers where username='"
							+ (String) txtUser.getValue() + "'",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rst = ps.executeQuery();
			if (rst.first()) {
				dbc = new DBClass(mapVars.get("dburl")
						+ txtDBServer.getValue().toString() + ":"
						+ mapVars.get("port") + ":"
						+ txtOracleSID.getValue().toString(),
						(String) txtUser.getValue(), (String) txtPwd.getValue());

				PreparedStatement ps2 = dbc.getDbConnection().prepareStatement(
						"select username,password from cp_users where username='"
								+ rst.getString("cp_user") + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst2 = ps2.executeQuery();
				if (!rst2.first()) {
					ps2.close();
					ps.close();
					throw new Exception(
							"User not defined as CHANNEL PLUS application user ");
				}
				// txtUser.setValue(rst.getString("cp_user"));
				// txtPwd.setValue(rst2.getString("password"));
				mapVars.put("CP_USER", rst.getString("cp_user"));
				mapVars.put("CP_USER_PASSWORD", rst2.getString("password"));
				ps.close();
				ps2.close();
			} else {
				ps.close();
				dbc = new DBClass(mapVars.get("dburl")
						+ txtDBServer.getValue().toString() + ":"
						+ mapVars.get("port") + ":"
						+ txtOracleSID.getValue().toString(),
						mapVars.get("user"), mapVars.get("password"));
				mapVars.put("CP_USER", (String) txtUser.getValue());
				mapVars.put("CP_USER_PASSWORD", (String) txtPwd.getValue());
			}
			ps = dbc.getDbConnection()
					.prepareStatement(
							"select username,"
									+ "location_code,profileno from cp_users where username=? and password=?",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, mapVars.get("CP_USER"));
			ps.setString(2, mapVars.get("CP_USER_PASSWORD"));
			rst = ps.executeQuery();
			if (!rst.first()) {
				throw new Exception("user or password is not valid");
			}
			profileno = rst.getFloat("profileno");
			buildProfiles();
			CURRENT_PROFILENO = rst.getInt("profileno");
			FORMAT_MONEY = utils.nvl(mapVars.get("FORMAT_MONEY_1"),
					FORMAT_MONEY);
			utils.DBUSER = txtUser.getValue().toString();
			utils.DBPWD = txtPwd.getValue().toString();
			utils.CPUSER = mapVars.get("CP_USER");
			utils.CPPWD = mapVars.get("CP_USER_PASSWORD");
			utils.DBURL = mapVars.get("dburl")
					+ txtDBServer.getValue().toString() + ":"
					+ mapVars.get("port") + ":"
					+ txtOracleSID.getValue().toString();

			if (mapVars.get("APP_VERSION") != null) {
				double db_ver = Double.valueOf(utils.nvl(
						mapVars.get("DB_VERSION"), "0"));
				double app_ver = Double.valueOf(utils.nvl(
						mapVars.get("APP_VERSION"), "0"));
				if (db_ver != app_ver)
					throw new Exception("Version mismatch with database.."
							+ app_ver);

			}

			return true;
		} catch (Exception e) {
			Logger.getLogger(UserLogin.class.getName()).log(Level.SEVERE, null,
					e);
			getWindow().showNotification("Logon not accessible :",
					e.toString(), Notification.TYPE_ERROR_MESSAGE);

		}
		return false;
	}

	public TextField getTxtUser() {
		return txtUser;
	}

	public void setTxtUser(TextField txtUser) {
		this.txtUser = txtUser;
	}

	public PasswordField getTxtPwd() {
		return txtPwd;
	}

	public void setTxtPwd(PasswordField txtPwd) {
		this.txtPwd = txtPwd;
	}
}
