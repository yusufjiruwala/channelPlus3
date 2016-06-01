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

public class frmReBuildings implements transactionalForm 
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
	private HorizontalLayout content1Layout = new HorizontalLayout();
	private HorizontalLayout codeLayout = new HorizontalLayout();
	private HorizontalLayout nameLayout = new HorizontalLayout();
	private HorizontalLayout content2Layout =new HorizontalLayout();
	private HorizontalLayout typeLayout = new HorizontalLayout();
	private HorizontalLayout countryLayout = new HorizontalLayout();
	private HorizontalLayout content3Layout = new HorizontalLayout();
	private HorizontalLayout cityLayout = new HorizontalLayout();
	private HorizontalLayout areaLayout = new HorizontalLayout();
	private HorizontalLayout content4Layout = new HorizontalLayout();
	private HorizontalLayout blkLayout = new HorizontalLayout();
	private HorizontalLayout stLayout = new HorizontalLayout();
	private HorizontalLayout avenLayout = new HorizontalLayout();
	private HorizontalLayout noLayout = new HorizontalLayout();
	private HorizontalLayout addDescr1Layout = new HorizontalLayout();
	private HorizontalLayout addDescr2Layout = new HorizontalLayout();
	private HorizontalLayout content5Layout = new HorizontalLayout();
	private HorizontalLayout floorsLayout = new HorizontalLayout();
	private HorizontalLayout flatsLayout = new HorizontalLayout();
	private HorizontalLayout shopsLayout = new HorizontalLayout();
	private HorizontalLayout otherUnitsLayout = new HorizontalLayout();
	private HorizontalLayout contactLayout = new HorizontalLayout();
	private HorizontalLayout content6Layout = new HorizontalLayout();
	private HorizontalLayout telLayout = new HorizontalLayout();
	private HorizontalLayout rentAmtLayout = new HorizontalLayout();
	private HorizontalLayout remarksLayout = new HorizontalLayout();
	private HorizontalLayout ownerCodeLayout = new HorizontalLayout();
	
	private TextField txtCode = ControlsFactory.CreateTextField("","BLDG_CODE", lstfldinfo);
	private TextField txtName = ControlsFactory.CreateTextField("","BLDG_NAME", lstfldinfo);
	private TextField txtType = ControlsFactory.CreateTextField("","BLDG_TYPE", lstfldinfo);
	private TextField txtCountry = ControlsFactory.CreateTextField("","BLDG_COUNTRY", lstfldinfo);
	private TextField txtCity = ControlsFactory.CreateTextField("","BLDG_CITY", lstfldinfo);
	private TextField txtArea = ControlsFactory.CreateTextField("","BLDG_AREA", lstfldinfo);
	private TextField txtBlk = ControlsFactory.CreateTextField("","BLDG_BLK", lstfldinfo);
	private TextField txtSt = ControlsFactory.CreateTextField("","BLDG_ST", lstfldinfo);
	private TextField txtAven = ControlsFactory.CreateTextField("","BLDG_AVEN", lstfldinfo);
	private TextField txtNo = ControlsFactory.CreateTextField("","BLDG_NO", lstfldinfo);
	private TextField txtAddDescr1 = ControlsFactory.CreateTextField("","BLDG_ADDRESSDESC1", lstfldinfo);
	private TextField txtAddDescr2 = ControlsFactory.CreateTextField("","BLDG_ADDRESSDESC2", lstfldinfo);
	private TextField txtFloors = ControlsFactory.CreateTextField("","BLDG_FLOORS", lstfldinfo);
	private TextField txtFlats = ControlsFactory.CreateTextField("","BLDG_FLATS", lstfldinfo);
	private TextField txtShops = ControlsFactory.CreateTextField("","BLDG_SHOPS", lstfldinfo);
	private TextField txtOtherUnits = ControlsFactory.CreateTextField("","BLDG_OTHERUNITS", lstfldinfo);
	private TextField txtContact = ControlsFactory.CreateTextField("","BLDG_CONTACT", lstfldinfo);
	private TextField txtTel = ControlsFactory.CreateTextField("","BLDG_TEL", lstfldinfo);
	private TextField txtRentAmt = ControlsFactory.CreateTextField("","BLDG_RENTAMT", lstfldinfo);
	private TextField txtRemarks = ControlsFactory.CreateTextField("","BLDG_REMARKS", lstfldinfo);
	private TextField txtOwnerCode = ControlsFactory.CreateTextField("","BLDG_OWNERCODE", lstfldinfo);
	private TextField txtOwnerCodeName = ControlsFactory.CreateTextField("","",null);
	
	private Label lblCode = new Label("Code");
	private Label lblName = new Label("Name");
	private Label lblType = new Label("Type");
	private Label lblCountry = new Label("Country");
	private Label lblCity = new Label("City");
	private Label lblArea = new Label("Area");
	private Label lblBlk = new Label("Blk");
	private Label lblSt = new Label("St");
	private Label lblAven = new Label("Aven");
	private Label lblNo = new Label("No");
	private Label lblAddDescr1 = new Label("Add Descr1");
	private Label lblAddDecsr2 = new Label("Add Descr2");
	private Label lblFloors = new Label("Floors");
	private Label lblFlats = new Label("Flats");
	private Label lblShops = new Label("Shops");
	private Label lblOtherUnits = new Label("Other Unit");
	private Label lblContact = new Label("Contact");
	private Label lblTel = new Label("Tel");
	private Label lblRentAmt = new Label("Rent Amt");
	private Label lblRemarks = new Label("Remarks");
	private Label lblOwnerCode = new Label("Owner Code");
	
	private NativeButton cmdSave = new NativeButton("Save");
	private NativeButton cmdDelete = new NativeButton("Delete");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdCls = new NativeButton("Clear");
	
	private NativeButton cmdOwnerCode = ControlsFactory.CreateToolbarButton("img/find.png", "Find...");
	private NativeButton cmdClearOwnerCode = ControlsFactory.CreateToolbarButton("img/clear.png", "Clear...");
	
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
			qe.setParaValue("BLDG_SN", "0");
			qe.setParaValue("FLAG", "0");
			if (QRYSES.isEmpty())
			{	
				qe.AutoGenerateInsertStatment("REBUILDINGS");
			} 
			else 
			{
				qe.AutoGenerateUpdateStatment("REBUILDINGS", "'BLDG_CODE'"," WHERE BLDG_CODE=:BLDG_CODE");    //  if to update..
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
								"select * from  REBUILDINGS where BLDG_CODE='"
										+ QRYSES + "'",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				fetchAccountInfo();
				pstmt.close();
				txtCode.setReadOnly(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	protected void fetchAccountInfo() {
		txtOwnerCodeName.setReadOnly(false);
		txtOwnerCodeName.setValue(utils.getSqlValue(
				"SELECT own_code, OWN_NAME  FROM REOWNERS WHERE OWN_CODE= '"
						+ txtOwnerCode.getValue().toString() + "'", con));
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
		mainLayout.setWidth("600px");
		basicLayout.setWidth("100%");
		content1Layout.setWidth("100%");
		codeLayout.setWidth("100%");
		nameLayout.setWidth("100%");	
		content2Layout.setWidth("100%");
		typeLayout.setWidth("100%");
		countryLayout.setWidth("100%");
		content3Layout.setWidth("100%");
		cityLayout.setWidth("100%");
		areaLayout.setWidth("100%");
		content4Layout.setWidth("100%");
		blkLayout.setWidth("100%");
		stLayout.setWidth("100%");
		avenLayout.setWidth("100%");
		noLayout.setWidth("100%");
		addDescr1Layout.setWidth("100%");
		addDescr2Layout.setWidth("100%");
		content5Layout.setWidth("100%");
		floorsLayout.setWidth("100%");
		flatsLayout.setWidth("100%");
		shopsLayout.setWidth("100%");
		otherUnitsLayout.setWidth("100%");
		contactLayout.setWidth("100%");
		content6Layout.setWidth("100%");
		telLayout.setWidth("100%");
		rentAmtLayout.setWidth("100%");
		remarksLayout.setWidth("100%");
		ownerCodeLayout.setWidth("100%");
		
		txtCode.setWidth("100%");
		txtName.setWidth("100%");
		txtType.setWidth("100%");
		txtCountry.setWidth("100%");
		txtCity.setWidth("100%");
		txtArea.setWidth("100%");
		txtBlk.setWidth("100%");
		txtSt.setWidth("100%");
		txtAven.setWidth("100%");
		txtNo.setWidth("100%");
		txtAddDescr1.setWidth("100%");
		txtAddDescr2.setWidth("100%");
		txtFloors.setWidth("100%");
		txtFlats.setWidth("100%");
		txtShops.setWidth("100%");
		txtOtherUnits.setWidth("100%");
		txtContact.setWidth("100%");
		txtTel.setWidth("100%");
		txtRentAmt.setWidth("100%");
		txtRemarks.setWidth("100%");
		txtOwnerCode.setWidth("100%");
		txtOwnerCodeName.setWidth("100%");
		
		lblCode.setWidth("80px");
		lblName.setWidth("70px");
		lblType.setWidth("80px");
		lblCountry.setWidth("70px");
		lblCity.setWidth("80px");
		lblArea.setWidth("70px");
		lblBlk.setWidth("80px");
		lblSt.setWidth("40px");
		lblAven.setWidth("40px");
		lblNo.setWidth("40px");
		lblAddDescr1.setWidth("70px");
		lblAddDecsr2.setWidth("70px");
		lblFloors.setWidth("80px"); 
		lblFlats.setWidth("40px"); 
		lblShops.setWidth("40px"); 
		lblOtherUnits.setWidth("40px");  
		lblContact.setWidth("70px"); 
		lblTel.setWidth("80px");
		lblRentAmt.setWidth("70px");
		lblRemarks.setWidth("70px");
		lblOwnerCode.setWidth("70px");

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
		ResourceManager.addComponent(basicLayout, content4Layout);
		ResourceManager.addComponent(basicLayout, addDescr1Layout);
		ResourceManager.addComponent(basicLayout, addDescr2Layout);
		ResourceManager.addComponent(basicLayout, content5Layout);
		ResourceManager.addComponent(basicLayout, contactLayout);
		ResourceManager.addComponent(basicLayout, content6Layout);
		ResourceManager.addComponent(basicLayout, remarksLayout);
		ResourceManager.addComponent(basicLayout, ownerCodeLayout);
		
		ResourceManager.addComponent(content1Layout, codeLayout);
		ResourceManager.addComponent(content1Layout, nameLayout);
		
		ResourceManager.addComponent(content2Layout, typeLayout);
		ResourceManager.addComponent(content2Layout, countryLayout);
		
		ResourceManager.addComponent(content3Layout, cityLayout);
		ResourceManager.addComponent(content3Layout, areaLayout);
		
		ResourceManager.addComponent(content4Layout, blkLayout);
		ResourceManager.addComponent(content4Layout, stLayout);
		ResourceManager.addComponent(content4Layout, avenLayout);
		ResourceManager.addComponent(content4Layout, noLayout);
		
		ResourceManager.addComponent(content5Layout, floorsLayout);
		ResourceManager.addComponent(content5Layout, flatsLayout);
		ResourceManager.addComponent(content5Layout, shopsLayout);
		ResourceManager.addComponent(content5Layout, otherUnitsLayout);
		
		ResourceManager.addComponent(content6Layout, telLayout);
		ResourceManager.addComponent(content6Layout, rentAmtLayout);
		
		ResourceManager.addComponent(codeLayout, lblCode);
		ResourceManager.addComponent(codeLayout, txtCode);
		
		ResourceManager.addComponent(nameLayout, lblName);
		ResourceManager.addComponent(nameLayout, txtName);
		
		ResourceManager.addComponent(typeLayout, lblType);
		ResourceManager.addComponent(typeLayout, txtType);
		
		ResourceManager.addComponent(countryLayout, lblCountry);
		ResourceManager.addComponent(countryLayout, txtCountry);
		
		ResourceManager.addComponent(cityLayout, lblCity);
		ResourceManager.addComponent(cityLayout, txtCity);
		
		ResourceManager.addComponent(areaLayout, lblArea);
		ResourceManager.addComponent(areaLayout, txtArea);
		
		ResourceManager.addComponent(blkLayout, lblBlk);
		ResourceManager.addComponent(blkLayout, txtBlk);
		
		ResourceManager.addComponent(stLayout, lblSt);
		ResourceManager.addComponent(stLayout, txtSt);
		
		ResourceManager.addComponent(avenLayout, lblAven);
		ResourceManager.addComponent(avenLayout, txtAven);
		
		ResourceManager.addComponent(noLayout, lblNo);
		ResourceManager.addComponent(noLayout, txtNo);
		
		ResourceManager.addComponent(addDescr1Layout, lblAddDescr1);
		ResourceManager.addComponent(addDescr1Layout, txtAddDescr1);
		
		ResourceManager.addComponent(addDescr2Layout, lblAddDecsr2); 
		ResourceManager.addComponent(addDescr2Layout, txtAddDescr2);
		
		ResourceManager.addComponent(floorsLayout, lblFloors);
		ResourceManager.addComponent(floorsLayout, txtFloors);
		
		ResourceManager.addComponent(flatsLayout, lblFlats);
		ResourceManager.addComponent(flatsLayout, txtFlats);
		
		ResourceManager.addComponent(shopsLayout, lblShops);
		ResourceManager.addComponent(shopsLayout, txtShops);
		
		ResourceManager.addComponent(otherUnitsLayout, lblOtherUnits);
		ResourceManager.addComponent(otherUnitsLayout, txtOtherUnits);
		
		ResourceManager.addComponent(contactLayout, lblContact);
		ResourceManager.addComponent(contactLayout, txtContact);
		
		ResourceManager.addComponent(telLayout, lblTel);
		ResourceManager.addComponent(telLayout, txtTel);
		
		ResourceManager.addComponent(rentAmtLayout, lblRentAmt);
		ResourceManager.addComponent(rentAmtLayout, txtRentAmt);
		
		ResourceManager.addComponent(remarksLayout, lblRemarks);
		ResourceManager.addComponent(remarksLayout, txtRemarks);
				
		ResourceManager.addComponent(ownerCodeLayout, lblOwnerCode);
		ResourceManager.addComponent(ownerCodeLayout, txtOwnerCode);
		ResourceManager.addComponent(ownerCodeLayout, cmdClearOwnerCode);
		ResourceManager.addComponent(ownerCodeLayout, cmdOwnerCode);
		ResourceManager.addComponent(ownerCodeLayout, txtOwnerCodeName);
		
		codeLayout.setComponentAlignment(lblCode, Alignment.BOTTOM_CENTER);	
		nameLayout.setComponentAlignment(lblName, Alignment.BOTTOM_CENTER);
		typeLayout.setComponentAlignment(lblType, Alignment.BOTTOM_CENTER);
		countryLayout.setComponentAlignment(lblCountry, Alignment.BOTTOM_CENTER);
		cityLayout.setComponentAlignment(lblCity, Alignment.BOTTOM_CENTER);
		areaLayout.setComponentAlignment(lblArea, Alignment.BOTTOM_CENTER);
		blkLayout.setComponentAlignment(lblBlk, Alignment.BOTTOM_CENTER);
		stLayout.setComponentAlignment(lblSt, Alignment.BOTTOM_CENTER);
		avenLayout.setComponentAlignment(lblAven, Alignment.BOTTOM_CENTER);
		noLayout.setComponentAlignment(lblNo, Alignment.BOTTOM_CENTER);
		addDescr1Layout.setComponentAlignment(lblAddDescr1, Alignment.BOTTOM_CENTER);
		addDescr2Layout.setComponentAlignment(lblAddDecsr2, Alignment.BOTTOM_CENTER);
		floorsLayout.setComponentAlignment(lblFloors, Alignment.BOTTOM_CENTER);
		flatsLayout.setComponentAlignment(lblFlats, Alignment.BOTTOM_CENTER);
		shopsLayout.setComponentAlignment(lblShops, Alignment.BOTTOM_CENTER);
		otherUnitsLayout.setComponentAlignment(lblOtherUnits, Alignment.BOTTOM_CENTER);
		telLayout.setComponentAlignment(lblTel, Alignment.BOTTOM_CENTER);
		rentAmtLayout.setComponentAlignment(lblRentAmt, Alignment.BOTTOM_CENTER);      
		remarksLayout.setComponentAlignment(lblRemarks, Alignment.BOTTOM_CENTER);
		ownerCodeLayout.setComponentAlignment(lblOwnerCode, Alignment.BOTTOM_CENTER);
		ownerCodeLayout.setComponentAlignment(cmdOwnerCode, Alignment.BOTTOM_CENTER);
		ownerCodeLayout.setComponentAlignment(cmdClearOwnerCode, Alignment.BOTTOM_CENTER);
		contactLayout.setComponentAlignment(lblContact, Alignment.BOTTOM_CENTER);
		
		content1Layout.setExpandRatio(codeLayout, 1.3f);
		content1Layout.setExpandRatio(nameLayout, 2.7f);
		
		content2Layout.setExpandRatio(typeLayout, 1.3f);
		content2Layout.setExpandRatio(countryLayout, 2.7f);
		
		content3Layout.setExpandRatio(cityLayout, 1.3f);
		content3Layout.setExpandRatio(areaLayout, 2.7f);
		
		content4Layout.setExpandRatio(blkLayout, 1f);
		content4Layout.setExpandRatio(stLayout, 1f);
		content4Layout.setExpandRatio(avenLayout, 1f);
		content4Layout.setExpandRatio(noLayout, 1f);
		
		content5Layout.setExpandRatio(floorsLayout, 1f);
		content5Layout.setExpandRatio(flatsLayout, 1f);
		content5Layout.setExpandRatio(shopsLayout, 1f);
		content5Layout.setExpandRatio(otherUnitsLayout, 1f);
		
		content6Layout.setExpandRatio(telLayout, 1.3f);
		content6Layout.setExpandRatio(rentAmtLayout, 2.7f);
		
		codeLayout.setExpandRatio(lblCode, 0.1f);
		codeLayout.setExpandRatio(txtCode, 3.9f);
		
		nameLayout.setExpandRatio(lblName, 0.1f);
		nameLayout.setExpandRatio(txtName, 3.9f);
		
		typeLayout.setExpandRatio(lblType, 0.1f);
		typeLayout.setExpandRatio(txtType, 3.9f);
		
		countryLayout.setExpandRatio(lblCountry, 0.1f);
		countryLayout.setExpandRatio(txtCountry, 3.9f);
		
		cityLayout.setExpandRatio(lblCity, 0.1f);
		cityLayout.setExpandRatio(txtCity, 3.9f);
		
		areaLayout.setExpandRatio(lblArea, 0.1f);
		areaLayout.setExpandRatio(txtArea, 3.9f);
		
		blkLayout.setExpandRatio(lblBlk, 0.1f);
		blkLayout.setExpandRatio(txtBlk, 3.9f);
		
		stLayout.setExpandRatio(lblSt, 0.1f);
		stLayout.setExpandRatio(txtSt, 3.9f);
		
		avenLayout.setExpandRatio(lblAven, 0.1f);
		avenLayout.setExpandRatio(txtAven, 3.9f);
		
		noLayout.setExpandRatio(lblNo, 0.1f);
		noLayout.setExpandRatio(txtNo, 3.9f);
		
		addDescr1Layout.setExpandRatio(lblAddDescr1, 0.1f);
		addDescr1Layout.setExpandRatio(txtAddDescr1, 3.9f);
		
		addDescr2Layout.setExpandRatio(lblAddDecsr2, 0.1f);
		addDescr2Layout.setExpandRatio(txtAddDescr2, 3.9f);
		
		floorsLayout.setExpandRatio(lblFloors, 0.1f);
		floorsLayout.setExpandRatio(txtFloors, 3.9f);
		
		flatsLayout.setExpandRatio(lblFlats, 0.1f);
		flatsLayout.setExpandRatio(txtFlats, 3.9f);
		
		shopsLayout.setExpandRatio(lblShops, 0.1f);
		shopsLayout.setExpandRatio(txtShops, 3.9f);
		
		otherUnitsLayout.setExpandRatio(lblOtherUnits, 0.1f);
		otherUnitsLayout.setExpandRatio(txtOtherUnits, 3.9f);
		
		contactLayout.setExpandRatio(lblContact, 0.1f);
		contactLayout.setExpandRatio(txtContact, 3.9f);
		
		telLayout.setExpandRatio(lblTel, 0.1f);
		telLayout.setExpandRatio(txtTel, 3.9f);
		
		rentAmtLayout.setExpandRatio(lblRentAmt, 0.1f);
		rentAmtLayout.setExpandRatio(txtRentAmt, 3.9f);
		
		remarksLayout.setExpandRatio(lblRemarks, 0.1f);
		remarksLayout.setExpandRatio(txtRemarks, 3.9f);
		
		ownerCodeLayout.setExpandRatio(lblOwnerCode, 0.1f);
		ownerCodeLayout.setExpandRatio(txtOwnerCode, 1f);
		ownerCodeLayout.setExpandRatio(cmdOwnerCode, 0.1f);
		ownerCodeLayout.setExpandRatio(cmdClearOwnerCode, 0.1f);
		ownerCodeLayout.setExpandRatio(txtOwnerCodeName, 2.7f);
		
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
			cmdOwnerCode.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					show_OwnerCodeList();
				}
			});
			cmdClearOwnerCode.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					txtOwnerCode.setReadOnly(false);
					txtOwnerCodeName.setReadOnly(false);
					txtOwnerCode.setValue("");
					txtOwnerCodeName.setValue("");
					txtOwnerCode.setReadOnly(true);
					txtOwnerCodeName.setReadOnly(true);
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
		content1Layout.removeAllComponents();
		codeLayout.removeAllComponents();
		nameLayout.removeAllComponents();	
		content2Layout.removeAllComponents();
		typeLayout.removeAllComponents();
		countryLayout.removeAllComponents();
		content3Layout.removeAllComponents();
		cityLayout.removeAllComponents();
		areaLayout.removeAllComponents();
		content4Layout.removeAllComponents();
		blkLayout.removeAllComponents();
		stLayout.removeAllComponents();
		avenLayout.removeAllComponents();
		noLayout.removeAllComponents();
		addDescr1Layout.removeAllComponents();
		addDescr2Layout.removeAllComponents();
		content5Layout.removeAllComponents();
		floorsLayout.removeAllComponents();
		flatsLayout.removeAllComponents();
		shopsLayout.removeAllComponents();
		otherUnitsLayout.removeAllComponents();
		contactLayout.removeAllComponents();
		content6Layout.removeAllComponents();
		telLayout.removeAllComponents();
		rentAmtLayout.removeAllComponents();
		remarksLayout.removeAllComponents();
		ownerCodeLayout.removeAllComponents();
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
									.getFieldValue(rn, "bldg_code").toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select * from REBUILDINGS order by bldg_code", true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
	protected void show_OwnerCodeList() {
		try {
			final Window wnd = new Window();
			final VerticalLayout la = new VerticalLayout();
			wnd.setContent(la);
			txtOwnerCode.setReadOnly(false);
			txtOwnerCodeName.setReadOnly(false);
			utilsVaadin.showSearch(la, new SelectionListener() {
				public void onSelection(TableView tv) {
					Channelplus3Application.getInstance().getMainWindow()
							.removeWindow(wnd);

					if (tv.getSelectionValue() > -1) {
						try {
							int rn = tv.getSelectionValue();
							txtOwnerCodeName.setValue(tv.getData()
									.getFieldValue(rn, "OWN_NAME").toString() );
							txtOwnerCode.setValue(tv.getData().getFieldValue(rn, "own_code").toString());
							txtOwnerCode.setReadOnly(true);
							txtOwnerCodeName.setReadOnly(true);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "SELECT own_code, OWN_NAME  FROM REOWNERS", true);
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

}
