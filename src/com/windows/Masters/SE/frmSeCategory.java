package com.windows.Masters.SE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmSeCategory implements transactionalForm 
{
	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	Tree tree = new Tree();
	private localTableModel data_Items = new localTableModel();

	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();
	
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout codeLayout = new HorizontalLayout();
	private HorizontalLayout nameLayout = new HorizontalLayout();
	private HorizontalLayout cashBankLayout = new HorizontalLayout();
	private HorizontalLayout revenueLayout = new HorizontalLayout();
	
	private Label lblCode = new Label("Group Code");
	private Label lblName = new Label("Group Name");
	private Label lblCashBank = new Label("Cash/Bank Acc");
	private Label lblRevenue = new Label("Revenue Acc");
	
	private TextField txtCode = ControlsFactory.CreateTextField("","CODE",lstfldinfo);
	private TextField txtName = ControlsFactory.CreateTextField("","NAME",lstfldinfo);
	private TextField txtCashBankAcc = ControlsFactory.CreateTextField("","EXP_ACCNO",lstfldinfo);
	private TextField txtRevenue = ControlsFactory.CreateTextField("","REV_ACCNO",lstfldinfo);
	private TextField txtCashBankAccName = ControlsFactory.CreateTextField("","",null);
	private TextField txtRevenueAccName = ControlsFactory.CreateTextField("","",null);
	
	private NativeButton cmdSave = new NativeButton("Save");
	private NativeButton cmdDelete = new NativeButton("Delete");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdCls = new NativeButton("Clear");

	private NativeButton cmdCashBank = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdRevenue = ControlsFactory.CreateToolbarButton(
			"img/find.png", "Find...");
	private NativeButton cmdClearCashBank = ControlsFactory
			.CreateToolbarButton("img/clear.png", "Clear...");
	private NativeButton cmdClearRevenue = ControlsFactory.CreateToolbarButton(
			"img/clear.png", "Clear...");
	
	public void save_data() {
		save_data(true);
	}
	public void save_data(boolean cls) 
	{
		try 
		{
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
				if (QRYSES.isEmpty())
				{	
					qe.AutoGenerateInsertStatment("segroup");  //if	
				} 
				else 
				{
					qe.AutoGenerateUpdateStatment("segroup", "'code'"," WHERE code=:code");    //  if to update..
				}
				qe.execute();
				con.commit();
				if (cls) 
				{
					QRYSES = "";
					parentLayout.getWindow().showNotification("Saved Successfully");
				} 
				else 
				{
					QRYSES = txtCode.getValue().toString();
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
			txtCode.setReadOnly(false);
			txtCashBankAcc.setReadOnly(false);
			txtRevenue.setReadOnly(false);
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) 
			{
				PreparedStatement pstmt = con.prepareStatement(
						"select * from segroup  where  code='" + QRYSES + "'",
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				fetchLocation();
				pstmt.close();
				txtCode.setReadOnly(true);
				txtCashBankAcc.setReadOnly(true);
				txtRevenue.setReadOnly(true);
			}

		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public void fetchLocation()
	{
		txtCashBankAccName.setReadOnly(false);
		txtRevenueAccName.setReadOnly(false);
		txtRevenueAccName.setValue(
				utils.getSqlValue("Select Name from Acaccount where AccNo = '"
		            +txtRevenue.getValue().toString()+"'", con));
		
		txtCashBankAccName.setValue(
				utils.getSqlValue("Select Name from Acaccount where AccNo = '"
		            +txtCashBankAcc.getValue().toString()+"'", con));
		
	}
	public void print_data() {
		
	}

	public void init() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		try {
			if (data_Items.getDbclass() == null) {
				data_Items.createDBClassFromConnection(con);
			}

		} catch (SQLException e) {
		}
		createView();
		load_data();
	}

	public void createView() 
	{
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		mainLayout.setWidth("400px");
		codeLayout.setWidth("100%");
		nameLayout.setWidth("100%");
		cashBankLayout.setWidth("100%");
		revenueLayout.setWidth("100%");
		
		lblCode.setWidth("100px");
		lblName.setWidth("100px");
		lblCashBank.setWidth("100px");
		lblRevenue.setWidth("100px");
		
		txtCode.setWidth("100%");
		txtName.setWidth("100%");
		txtCashBankAcc.setWidth("100%");
		txtCashBankAccName.setWidth("100%");
		txtRevenue.setWidth("100%");
		txtRevenueAccName.setWidth("100%");
		
		basicLayout.addStyleName("formLayout");

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);
		
		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdCls);
		
		ResourceManager.addComponent(basicLayout, codeLayout);
		ResourceManager.addComponent(basicLayout, nameLayout);
		ResourceManager.addComponent(basicLayout, cashBankLayout);
		ResourceManager.addComponent(basicLayout, revenueLayout);
		
		ResourceManager.addComponent(codeLayout, lblCode);
		ResourceManager.addComponent(codeLayout, txtCode);
		
		ResourceManager.addComponent(nameLayout, lblName);
		ResourceManager.addComponent(nameLayout, txtName);
		
		ResourceManager.addComponent(cashBankLayout, lblCashBank);
		ResourceManager.addComponent(cashBankLayout, txtCashBankAcc);
		ResourceManager.addComponent(cashBankLayout, cmdCashBank);
		ResourceManager.addComponent(cashBankLayout, cmdClearCashBank);
		ResourceManager.addComponent(cashBankLayout, txtCashBankAccName);
		
		ResourceManager.addComponent(revenueLayout, lblRevenue);
		ResourceManager.addComponent(revenueLayout, txtRevenue);
		ResourceManager.addComponent(revenueLayout, cmdRevenue);
		ResourceManager.addComponent(revenueLayout, cmdClearRevenue);
		ResourceManager.addComponent(revenueLayout, txtRevenueAccName);
		
		codeLayout.setComponentAlignment(lblCode, Alignment.BOTTOM_CENTER);
		
		nameLayout.setComponentAlignment(lblName, Alignment.BOTTOM_CENTER);
		
		cashBankLayout.setComponentAlignment(lblCashBank, Alignment.BOTTOM_CENTER);
		cashBankLayout.setComponentAlignment(cmdCashBank, Alignment.BOTTOM_CENTER);
		cashBankLayout.setComponentAlignment(cmdClearCashBank, Alignment.BOTTOM_CENTER);
		
		revenueLayout.setComponentAlignment(lblRevenue, Alignment.BOTTOM_CENTER);
		revenueLayout.setComponentAlignment(cmdRevenue, Alignment.BOTTOM_CENTER);
		revenueLayout.setComponentAlignment(cmdClearRevenue, Alignment.BOTTOM_CENTER);
		
		codeLayout.setExpandRatio(lblCode, 0.1f);
		codeLayout.setExpandRatio(txtCode, 3.9f);
		
		nameLayout.setExpandRatio(lblName, 0.1f);
		nameLayout.setExpandRatio(txtName, 3.9f);
		
		cashBankLayout.setExpandRatio(lblCashBank, 0.1f);
		cashBankLayout.setExpandRatio(txtCashBankAcc, 1f);
		cashBankLayout.setExpandRatio(cmdCashBank, 0.1f);
		cashBankLayout.setExpandRatio(cmdClearCashBank, 0.1f);
		cashBankLayout.setExpandRatio(txtCashBankAccName, 2.7f);
		
		revenueLayout.setExpandRatio(lblRevenue, 0.1f);
		revenueLayout.setExpandRatio(txtRevenue, 1f);
		revenueLayout.setExpandRatio(cmdRevenue, 0.1f);
		revenueLayout.setExpandRatio(cmdClearRevenue, 0.1f);
		revenueLayout.setExpandRatio(txtRevenueAccName, 2.7f);
		
		if (!listnerAdded) 
		{			
			cmdList.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					show_list();
				}
			});
			cmdSave.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					save_data();
				}
			});
			cmdCls.addListener(new ClickListener() 
			{
				public void buttonClick(ClickEvent event)
				{
					QRYSES = "";
					load_data();
				}
			});
			cmdCashBank.addListener(new ClickListener() 
			{
				public void buttonClick(ClickEvent event)
				{
					show_CashBankAccountList();
				}
			});
			cmdRevenue.addListener(new ClickListener() 
			{
				public void buttonClick(ClickEvent event)
				{
					show_RevenueAccountList();
				}
			});
			cmdClearCashBank.addListener(new ClickListener() 
			{
				public void buttonClick(ClickEvent event)
				{
					txtCashBankAcc.setReadOnly(false);
					txtCashBankAccName.setReadOnly(false);
					txtCashBankAcc.setValue("");
					txtCashBankAccName.setValue("");
					txtCashBankAcc.setReadOnly(true);
					txtCashBankAccName.setReadOnly(true);
				}
			});
			cmdClearRevenue.addListener(new ClickListener() 
			{
				public void buttonClick(ClickEvent event)
				{
					txtRevenue.setReadOnly(false);
					txtRevenueAccName.setReadOnly(false);
					txtRevenue.setValue("");
					txtRevenueAccName.setValue("");
					txtRevenue.setReadOnly(true);
					txtRevenueAccName.setReadOnly(true);
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
	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractOrderedLayout) parentLayout;
	}

	public void showInitView() {
		QRYSES = "";
		initForm();
	}
	protected void show_list()
	{
		try 
		{
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);

			utilsVaadin.showSearch(la, new SelectionListener()
			{
				public void onSelection(TableView tv) 
				{
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);
					if (tv.getSelectionValue() > -1) 
					{
						try 
						{
							int rn = tv.getSelectionValue();
							QRYSES = tv.getData().getFieldValue(rn, "CODE")
									.toString();
							load_data();
						} 
						catch (Exception ex) 
						{
							ex.printStackTrace();
						}
					}
				}
			}, con, "select code, name from segroup", true);
		} 
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	protected void show_CashBankAccountList() {
		try {
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);

			utilsVaadin.showSearch(la, new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);

					if (tv.getSelectionValue() > -1) {
						try {
							int rn = tv.getSelectionValue();
							txtCashBankAccName.setValue(tv.getData()
									.getFieldValue(rn, "Name").toString() );
							txtCashBankAcc.setValue(tv.getData().getFieldValue(rn, "AccNo").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select AccNo, Name from Acaccount order by path", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
	protected void show_RevenueAccountList() {
		try {
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);

			utilsVaadin.showSearch(la, new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);

					if (tv.getSelectionValue() > -1) {
						try {
							int rn = tv.getSelectionValue();
							txtRevenueAccName.setValue(tv.getData()
									.getFieldValue(rn, "Name").toString() );
							txtRevenue.setValue(tv.getData().getFieldValue(rn, "AccNo").toString());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select AccNo, Name from Acaccount order by path", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

}
