package com.windows.Masters.General;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmCreateUser implements transactionalForm
{
	private AbstractLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();
	
	private Panel panel = new Panel();
	
	private HorizontalLayout userNameLayout = new HorizontalLayout();
	private HorizontalLayout passwordLayout = new HorizontalLayout();
	private HorizontalLayout confPasswordLayout = new HorizontalLayout();
	private HorizontalLayout profileLayout = new HorizontalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout(); 
	
	private NativeButton cmdCreateUser = new NativeButton("Create");
	private NativeButton cmdDropUser = new NativeButton("Drop");
	private NativeButton cmdReset = new NativeButton("Reset");
	
	private Label lblUserName = new Label("User Name : ");
	private Label lblPassword = new Label("Password : ");
	private Label lblConfirmPassword = new Label("Confirm Password : ");
	private Label lblProfile = new Label("Profile : ");
	
	private TextField txtUserName = ControlsFactory.CreateTextField("","username",lstfldinfo);
	private TextField txtPassword = ControlsFactory.CreateTextField("","password",lstfldinfo);
	private TextField txtConfPassword = ControlsFactory.CreateTextField("","",null);
	private TextField txtProfile = ControlsFactory.CreateTextField("","profileno", lstfldinfo);
	
	public void save_data() 
	{
		save_data(true);
	}
	public void save_data(boolean cls) 
	{
		try 
		{
			validataData();
			con.setAutoCommit(false);
			QueryExe qe = new QueryExe(con);
			for (Iterator iterator = lstfldinfo.iterator(); iterator.hasNext();) 
			{
				 FieldInfo fl = (FieldInfo) iterator.next();
				 if (!(fl.obj instanceof CheckBox))
				 {
					 qe.setParaValue(fl.fieldName, ((AbstractField) fl.obj)
					 .getValue());
				 } 
				 else 
				 {
					 qe .setParaValue(fl.fieldName, (((CheckBox) fl.obj) .booleanValue() ? fl.valueOnTrue : fl.valueOnFalse));
				 }
			}
			Date dt = new Date(System.currentTimeMillis());	 
			qe.setParaValue("IS_USER","Y");
			qe.setParaValue("IS_CUSTOMER", "Y");
			qe.setParaValue("IS_SUPPLIER","Y");
			qe.setParaValue("IS_ADMIN", "Y");
			qe.setParaValue("PARENTUSER","Y");
			qe.setParaValue("FLAG","0");
			qe.setParaValue("LOCATION_CODE","Y");
			if (QRYSES.isEmpty())
			{	
				qe.AutoGenerateInsertStatment("cp_users");  //if	
			} 
			else 
			{
				qe.AutoGenerateUpdateStatment("cp_users", "'username'"," WHERE username=:username");    //  if to update..
			}
			qe.execute();
			qe.close();
			con.commit();
			if (cls) 
			{
				QRYSES = "";
				parentLayout.getWindow().showNotification("Saved Successfully");
			} 
			else 
			{
				QRYSES = txtUserName.getValue().toString();
			}
			load_data();				
		}
		catch (Exception ex) 
		{

			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
			Notification.TYPE_ERROR_MESSAGE); 
			try 
			{
				con.rollback();
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public void load_data() 
	{
		try 
		{
			txtUserName.setReadOnly(false);
			txtPassword.setReadOnly(false);
			txtConfPassword.setReadOnly(false);
			txtProfile.setReadOnly(true);
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) 
			{
				PreparedStatement pstmt = con .prepareStatement(
								"Select * from cp_users  where upper(username)='"
										+ QRYSES + "'",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY); 
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				txtUserName.setReadOnly(true);
				txtPassword.setReadOnly(true);
				txtConfPassword.setReadOnly(true);
				txtProfile.setReadOnly(true);
			}
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void print_data() {
		
	}
	public void validataData() throws SQLException 
	{
		String pass = txtPassword.getValue().toString();
		String confPass = txtConfPassword.getValue().toString();
		if(!pass.equals(confPass))
		{
			throw new SQLException("Password Must Match!!!!");
		}		
	}
	public void init() 
	{
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc().getDbConnection();
	}

	public void initForm() 
	{
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc().getDbConnection();
		createView();
		load_data();
	}
	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}
	public void createView() 
	{
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		
		mainLayout.setWidth("500px"); 
		panel.setWidth("300px"); 
		userNameLayout.setWidth("100%");
		passwordLayout.setWidth("100%");
		confPasswordLayout.setWidth("100%");
		profileLayout.setWidth("100%");
		
		lblUserName.setWidth("120px");
		lblPassword.setWidth("120px");
		lblConfirmPassword.setWidth("120px");
		lblProfile.setWidth("120px");
		
		txtUserName.setWidth("100%");
		txtPassword.setWidth("100%");
		txtConfPassword.setWidth("100%");
		txtProfile.setWidth("100%");
		
		txtPassword.setSecret(true);
		txtConfPassword.setSecret(true);
				
		ResourceManager.addComponent(centralPanel, mainLayout);
		
		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, panel);
		
		ResourceManager.addComponent(panel, basicLayout);
		
		ResourceManager.addComponent(basicLayout, userNameLayout);
		ResourceManager.addComponent(basicLayout, passwordLayout);
		ResourceManager.addComponent(basicLayout, confPasswordLayout);
		ResourceManager.addComponent(basicLayout, profileLayout);
		
		ResourceManager.addComponent(buttonLayout, cmdCreateUser);
		ResourceManager.addComponent(buttonLayout, cmdDropUser);
		ResourceManager.addComponent(buttonLayout, cmdReset);
		
		ResourceManager.addComponent(userNameLayout, lblUserName);
		ResourceManager.addComponent(userNameLayout, txtUserName);
		
		ResourceManager.addComponent(passwordLayout, lblPassword);
		ResourceManager.addComponent(passwordLayout, txtPassword);
		
		ResourceManager.addComponent(confPasswordLayout, lblConfirmPassword);
		ResourceManager.addComponent(confPasswordLayout, txtConfPassword);
		
		ResourceManager.addComponent(profileLayout, lblProfile);
		ResourceManager.addComponent(profileLayout, txtProfile);
		
		userNameLayout.setComponentAlignment(lblUserName, Alignment.BOTTOM_CENTER);
		
		passwordLayout.setComponentAlignment(lblPassword, Alignment.BOTTOM_CENTER);
		
		confPasswordLayout.setComponentAlignment(lblConfirmPassword, Alignment.BOTTOM_CENTER);
		
		profileLayout.setComponentAlignment(lblProfile, Alignment.BOTTOM_CENTER);
		
		userNameLayout.setExpandRatio(lblUserName, 0.1f);
		userNameLayout.setExpandRatio(txtUserName, 3.9f);
		
		passwordLayout.setExpandRatio(lblPassword, 0.1f);
		passwordLayout.setExpandRatio(txtPassword, 3.9f);
		
		confPasswordLayout.setExpandRatio(lblConfirmPassword, 0.1f);
		confPasswordLayout.setExpandRatio(txtConfPassword, 3.9f);
		
		profileLayout.setExpandRatio(lblProfile, 0.1f);
		profileLayout.setExpandRatio(txtProfile, 3.9f);
		
		if (!listnerAdded) 
		{
			cmdCreateUser.addListener(new ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					save_data();
				}
			});
			cmdDropUser.addListener(new ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					
					if (txtUserName.getValue() != null) 
					{
						int fnd;
						try 
						{
							con.setAutoCommit(false);
							fnd = utils.execSql("delete from cp_users where upper(username) ='" + txtUserName.getValue().toString().toUpperCase() + "'", con);
							con.commit();
							if (fnd>0) 
							{
								parentLayout.getWindow().showNotification(fnd +"User Delete Successfully");
								QRYSES="";
								load_data();							
							}
							
						} catch (SQLException e) {
							parentLayout.getWindow().showNotification(e.getMessage());
						
							e.printStackTrace();
							
							try {
								con.rollback();
							} catch (SQLException e1) {
							}
						}
						
					}					
				}
			});
			cmdReset.addListener(new ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					QRYSES = "";
					load_data();
				}
			});
			txtConfPassword.addListener(new BlurListener() 
			{	
				public void blur(com.vaadin.event.FieldEvents.BlurEvent event) 
				{
					String pass = txtPassword.getValue().toString();
					String confPass = txtConfPassword.getValue().toString();
					if(pass.equals(confPass))
					{
						parentLayout.getWindow().showNotification("Password Match");
					}
					else
					{
						parentLayout.getWindow().showNotification("Password Not Match");
					}
					
				}
			});
			txtUserName.addListener(new BlurListener() 
			{	
				public void blur(com.vaadin.event.FieldEvents.BlurEvent event) 
				{
					if (!txtUserName.isReadOnly() && txtUserName.getValue() != null) 
					{
						String fnd = utils.getSqlValue ( "select * from cp_users where username ='" + txtUserName.getValue().toString() + "'", con);
						if (!fnd.isEmpty()) 
						{
							parentLayout.getWindow().showNotification( "Found # " + fnd);
							QRYSES = txtUserName.getValue().toString() .toUpperCase();
							load_data();
						}
					}					
				}
			});
			listnerAdded = true;
		}		
	}
	public void resetFormLayout() 
	{
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
	}
	public void showInitView() 
	{
		QRYSES = "";
		initForm();
	}
}
 