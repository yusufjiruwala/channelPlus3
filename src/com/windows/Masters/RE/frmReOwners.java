package com.windows.Masters.RE;

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
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
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

public class frmReOwners implements transactionalForm  
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
	private HorizontalLayout nationalityLayout = new HorizontalLayout();
	private HorizontalLayout civilLayout = new HorizontalLayout();
	private HorizontalLayout dobLayout = new HorizontalLayout();
	private HorizontalLayout positionLayout = new HorizontalLayout();
	private HorizontalLayout typeLayout = new HorizontalLayout();
	private HorizontalLayout homeAddLayout = new HorizontalLayout();
	private HorizontalLayout workAddLayout = new HorizontalLayout();
	private HorizontalLayout content1Layout = new HorizontalLayout();
	private HorizontalLayout content2Layout = new HorizontalLayout();
	private HorizontalLayout content3Layout = new HorizontalLayout();
	
	private TextField txtCode = ControlsFactory.CreateTextField("","OWN_CODE",lstfldinfo);
	private TextField txtName = ControlsFactory.CreateTextField("","OWN_NAME",lstfldinfo);
	private TextField txtNationality = ControlsFactory.CreateTextField("","OWN_NATIONALITY",lstfldinfo);
	private TextField txtCivil = ControlsFactory.CreateTextField("","OWN_CIVILID", lstfldinfo);
	private DateField txtDob = ControlsFactory.CreateDateField("","OWN_DOFBIRTH",lstfldinfo);
	private TextField txtPosition = ControlsFactory.CreateTextField("","OWN_POSITION",lstfldinfo);
	private TextField txtType = ControlsFactory.CreateTextField("","OWN_TYPE",lstfldinfo);
	private TextField txtHAdd = ControlsFactory.CreateTextField("","OWN_HADDRESS",lstfldinfo);
	private TextField txtWAdd = ControlsFactory.CreateTextField("","OWN_WADDRESS",lstfldinfo);
	
	private Label lblCode = new Label("Code");
	private Label lblName = new Label("Name");
	private Label lblNationality = new Label("Nationality");
	private Label lblCivil = new Label("Civil");
	private Label lblDob = new Label("DOB");
	private Label lblPosition = new Label("Position");
	private Label lblType = new Label("Type");
	private Label lblHAdd = new Label("Home Add");
	private Label lblWAdd = new Label("Work Add");
	
	private NativeButton cmdSave = new NativeButton("Save");
	private NativeButton cmdDelete = new NativeButton("Delete");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdCls = new NativeButton("Clear");
		
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
					qe.AutoGenerateInsertStatment("reowners");  //if	
				} 
				else 
				{
					qe.AutoGenerateUpdateStatment("reowners", "'OWN_CODE'"," WHERE OWN_CODE=:OWN_CODE");    //  if to update..
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
	public void load_data() {
		try {
			txtCode.setReadOnly(false);
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) 
			{
				PreparedStatement pstmt = con
						.prepareStatement(
								"select * from  REOWNERS where OWN_CODE='"
										+ QRYSES + "'",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				txtCode.setReadOnly(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
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
		try
		{
			if (data_Items.getDbclass() == null) 
			{
				data_Items.createDBClassFromConnection(con);
			}
		} 
		catch (SQLException e) {
		}
		createView();
		load_data();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		mainLayout.setWidth("500px");
		content1Layout.setWidth("100%");
		content2Layout.setWidth("100%");
		content3Layout.setWidth("100%");         
		codeLayout.setWidth("100%");
		nameLayout.setWidth("100%");
		nationalityLayout.setWidth("100%");
		civilLayout.setWidth("100%");
		dobLayout.setWidth("100%");
		positionLayout.setWidth("100%");
		typeLayout.setWidth("100%");  
		homeAddLayout.setWidth("100%");
		workAddLayout.setWidth("100%");
		
		lblCode.setWidth("75px");
		lblName.setWidth("55px");
		lblNationality.setWidth("75px");
		lblCivil.setWidth("60px");
		lblDob.setWidth("60px");
		lblPosition.setWidth("75px");
		lblType.setWidth("55px");
		lblHAdd.setWidth("65px");
		lblWAdd.setWidth("65px");
		
		txtCode.setWidth("100%");
		txtName.setWidth("100%");
		txtNationality.setWidth("100%");
		txtCivil.setWidth("100%");
		txtDob.setWidth("100%");
		txtPosition.setWidth("100%");
		txtType.setWidth("100%");
		txtHAdd.setWidth("100%");
		txtWAdd.setWidth("100%");		
		
		basicLayout.addStyleName("formLayout");

		ResourceManager.addComponent(centralPanel, mainLayout);
		
		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);
		
		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdCls);
		
		ResourceManager.addComponent(basicLayout, content1Layout);
		ResourceManager.addComponent(basicLayout, content2Layout);
		ResourceManager.addComponent(basicLayout, content3Layout);
		ResourceManager.addComponent(basicLayout, homeAddLayout);
		ResourceManager.addComponent(basicLayout, workAddLayout);
		
		ResourceManager.addComponent(content1Layout, codeLayout);
		ResourceManager.addComponent(content1Layout, nameLayout);
		
		ResourceManager.addComponent(content2Layout, nationalityLayout);
		ResourceManager.addComponent(content2Layout, civilLayout);
		ResourceManager.addComponent(content2Layout, dobLayout);
		
		ResourceManager.addComponent(content3Layout, positionLayout);
		ResourceManager.addComponent(content3Layout, typeLayout);
		
		ResourceManager.addComponent(codeLayout, lblCode);
		ResourceManager.addComponent(codeLayout, txtCode);
		
		ResourceManager.addComponent(nameLayout, lblName);
		ResourceManager.addComponent(nameLayout, txtName);
		
		ResourceManager.addComponent(nationalityLayout, lblNationality);
		ResourceManager.addComponent(nationalityLayout, txtNationality);
		
		ResourceManager.addComponent(civilLayout, lblCivil);
		ResourceManager.addComponent(civilLayout, txtCivil);
		
		ResourceManager.addComponent(dobLayout, lblDob);
		ResourceManager.addComponent(dobLayout, txtDob);
		
		ResourceManager.addComponent(positionLayout, lblPosition);
		ResourceManager.addComponent(positionLayout, txtPosition);
		
		ResourceManager.addComponent(typeLayout, lblType);
		ResourceManager.addComponent(typeLayout, txtType);
		
		ResourceManager.addComponent(homeAddLayout, lblHAdd);
		ResourceManager.addComponent(homeAddLayout, txtHAdd);
		
		ResourceManager.addComponent(workAddLayout, lblWAdd);
		ResourceManager.addComponent(workAddLayout, txtWAdd);
		
		codeLayout.setComponentAlignment(lblCode, Alignment.BOTTOM_CENTER);
		
		nameLayout.setComponentAlignment(lblName, Alignment.BOTTOM_CENTER);
		
		nationalityLayout.setComponentAlignment(lblNationality, Alignment.BOTTOM_CENTER);
		
		civilLayout.setComponentAlignment(lblCivil, Alignment.BOTTOM_CENTER);
		
		dobLayout.setComponentAlignment(lblDob, Alignment.BOTTOM_CENTER);
		
		positionLayout.setComponentAlignment(lblPosition, Alignment.BOTTOM_CENTER);
		
		typeLayout.setComponentAlignment(lblType, Alignment.BOTTOM_CENTER);
		
		homeAddLayout.setComponentAlignment(lblHAdd, Alignment.BOTTOM_CENTER);
		
		workAddLayout.setComponentAlignment(lblWAdd, Alignment.BOTTOM_CENTER);
		
		content1Layout.setExpandRatio(codeLayout, 1.3f);
		content1Layout.setExpandRatio(nameLayout, 2.7f);
		
		content2Layout.setExpandRatio(nationalityLayout, 1.3f);
		content2Layout.setExpandRatio(civilLayout, 1.3f);
		content2Layout.setExpandRatio(dobLayout, 1.4f);
		
		content3Layout.setExpandRatio(positionLayout, 1.3f);
		content3Layout.setExpandRatio(typeLayout, 2.7f);
		
		codeLayout.setExpandRatio(lblCode, 0.1f);
		codeLayout.setExpandRatio(txtCode, 3.9f);
		
		nameLayout.setExpandRatio(lblName, 0.1f);
		nameLayout.setExpandRatio(txtName, 3.9f);
		
		nationalityLayout.setExpandRatio(lblNationality, 0.1f);
		nationalityLayout.setExpandRatio(txtNationality, 3.9f);
		
		civilLayout.setExpandRatio(lblCivil, 0.1f);
		civilLayout.setExpandRatio(txtCivil, 3.9f);
		
		dobLayout.setExpandRatio(lblDob, 0.1f);
		dobLayout.setExpandRatio(txtDob, 3.9f);
		
		positionLayout.setExpandRatio(lblPosition, 0.1f);
		positionLayout.setExpandRatio(txtPosition, 3.9f);
		
		typeLayout.setExpandRatio(lblType, 0.1f);
		typeLayout.setExpandRatio(txtType, 3.9f);
		
		homeAddLayout.setExpandRatio(lblHAdd, 0.1f);
		homeAddLayout.setExpandRatio(txtHAdd, 3.9f);
		
		workAddLayout.setExpandRatio(lblWAdd, 0.1f);
		workAddLayout.setExpandRatio(txtWAdd, 3.9f);
		if (!listnerAdded) {
			cmdSave.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			cmdDelete.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					
				}
			});
			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_list();
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
			listnerAdded = true;
		}

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = (AbstractOrderedLayout) parentLayout;
	}

	public void showInitView() {
		QRYSES = "";
		initForm();
	}
	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();
		basicLayout.removeAllComponents();
		buttonLayout.removeAllComponents();
		codeLayout.removeAllComponents();
		nameLayout.removeAllComponents();
		nationalityLayout.removeAllComponents();
		civilLayout.removeAllComponents();
		dobLayout.removeAllComponents();
		positionLayout.removeAllComponents();
		typeLayout.removeAllComponents();
		homeAddLayout.removeAllComponents();
		workAddLayout.removeAllComponents();
		content1Layout.removeAllComponents();
		content2Layout.removeAllComponents();
		content3Layout.removeAllComponents();
	}
	protected void show_list() {
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
							QRYSES = tv.getData()
									.getFieldValue(rn, "OWN_code").toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select * from REOWNERS", true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
}
