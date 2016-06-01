package com.windows.wizards.dev;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.components.SearchField;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.FormLayoutManager;
import com.generic.Parameter;
import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.dataCell;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmWzDesignQuery implements transactionalForm {
	private Connection con = null;
	private Window wz_window = null;

	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout[] = { new VerticalLayout(),
			new VerticalLayout(), new VerticalLayout(), new VerticalLayout(),
			new VerticalLayout() };
	private FormLayoutManager basicLayout = new FormLayoutManager("100%");
	private FormLayoutManager paraFormLayout = new FormLayoutManager("100%");
	private HorizontalLayout paraHzLayout = new HorizontalLayout();
	private HorizontalLayout colHzLayout = new HorizontalLayout();

	private Panel propPanel = new Panel();
	private FormLayoutManager propForm = new FormLayoutManager("100%");

	private Label vl1 = new Label();
	private Label vl2 = new Label();
	private Panel pnl_sel = new Panel("Selection");

	private ComboBox txtNewCol = ControlsFactory.CreateListField(
			"Add new Column", "", "SELECT COLUMN_NAME n1,column_name n2 FROM"
					+ " USER_TAB_COLS WHERE TABLE_NAME='' order by column_id",
			null, "100%", null, true);

	NativeButton cmdAddC = ControlsFactory.CreateCustomButton("Add",
			"img/add.png", "add column", "");

	private TextField txtSelNewGrp = ControlsFactory.CreateTextField(
			"Enter New Group ", "", null, "200px", null);
	private TextField txtSelNewGrpName = ControlsFactory.CreateTextField(
			"Group Name ", "", null, "200px", null);
	private SearchField txtSelNewExistGrp = ControlsFactory
			.CreateSearchField(
					"Select Group ",
					"",
					null,
					"200px",
					"select code,titlearb from invqrycols1 where code in (select distinct parentrep from invqrycols1) ",
					"CODE", "TITLEARB");

	private ComboBox txtSelEditQuery = ControlsFactory.CreateListField(
			"Select Query", "", "", null);
	private SearchField txSelEditGroup = ControlsFactory
			.CreateSearchField(
					"Select Group ",
					null,
					null,
					"200px",
					"select code,titlearb from invqrycols1 where code in (select parentrep from invqrycols1) order by 1 ",
					"CODE", "TITLEARB");

	private ValueChangeListener vc_sel = new ValueChangeListener() {

		public void valueChange(ValueChangeEvent event) {
			dataCell dc = (dataCell) event.getProperty().getValue();
			pnl_sel.removeAllComponents();
			txtSelNewGrp.setValue("");
			txtSelNewGrpName.setValue("");
			txtSelNewExistGrp.setValue("");
			cmdNext.setEnabled(true);

			if (dc.getValue().equals("1")) {
				ResourceManager.addComponent(pnl_sel, txtSelNewGrp);
				ResourceManager.addComponent(pnl_sel, txtSelNewGrpName);
				int n = Integer
						.valueOf(utils
								.getSqlValue(
										"select NVL(max(to_NUMBER(parentrep)),0) from invqrycols1",
										con));
				n += 10;
				txtSelNewGrp.setValue(String.valueOf(n));
			}

			if (dc.getValue().equals("2")) {
				ResourceManager.addComponent(pnl_sel, txtSelNewExistGrp);
			}
			if (dc.getValue().equals("3")) {

				txSelEditGroup.setImmediate(true);
				ResourceManager.addComponent(pnl_sel, txSelEditGroup);
				ResourceManager.addComponent(pnl_sel, txtSelEditQuery);

			}

		}
	};

	private HorizontalLayout commandLayout = new HorizontalLayout();

	private List<ColumnProperty> lstParaCols = new ArrayList<ColumnProperty>();

	private List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();

	private NativeButton cmdNext = ControlsFactory.CreateCustomButton("Next",
			"img/forward.png", "Next ", "");
	private NativeButton cmdBack = ControlsFactory.CreateCustomButton("Back",
			"img/back.png", "Prior ", "");
	private NativeButton cmdCancel = ControlsFactory.CreateCustomButton(
			"Cancel", "img/remove.png", "Cancel ", "");

	private OptionGroup sel_rpt_opt = ControlsFactory.CreateOptionGroup(null,
			"", null, vc_sel, "100%", "100%", new dataCell(
					"Create Under new Group", "1"), new dataCell(
					"Create Under Existed Group", "2"), new dataCell(
					"Edit Existed Query", "3"));

	private TextField txtTitleArb = ControlsFactory.CreateTextField("",
			"TITLEARB", lstfldinfo, "100%", "");
	private TextField txtTitleEng = ControlsFactory.CreateTextField("",
			"TITLEENG", lstfldinfo, "100%", "");

	private CheckBox chk_isCross = ControlsFactory.CreateCheckField("",
			"IS_CROSS_TAB", "N", "Y", lstfldinfo);
	private CheckBox chk_ShowGroupQuery = ControlsFactory.CreateCheckField("",
			"SHOW_GROUPS", "N", "Y", lstfldinfo,"100%","Y");
	private CheckBox chk_ShowGroup1 = ControlsFactory.CreateCheckField("",
			"SHOW_GROUP_1", "N", "Y", lstfldinfo,"100%","Y");
	private CheckBox chk_ShowGroup2 = ControlsFactory.CreateCheckField("",
			"SHOW_GROUP_2", "N", "Y", lstfldinfo,"100%","Y");
	private CheckBox chk_ShowColSel = ControlsFactory.CreateCheckField("",
			"SHOW_COL_SELECTION", "N", "Y", lstfldinfo);
	private CheckBox chk_ShowAdvance = ControlsFactory.CreateCheckField("",
			"SHOW_ADVANCE", "N", "Y", lstfldinfo,"100%",true);
	private CheckBox chk_ctColSort = ControlsFactory.CreateCheckField("",
			"CT_COL_SORT", "N", "Y", lstfldinfo, "100%", "N");

	private TextField txtPageHeight = ControlsFactory.CreateTextField("",
			"PAGE_HEIGHT", lstfldinfo, "100%", "842");
	private TextField txtPageWidth = ControlsFactory.CreateTextField("",
			"PAGE_WIDTH", lstfldinfo, "100%", "595");
	private TextField txtWhereClause = ControlsFactory.CreateTextField("",
			"REPORTNAME", lstfldinfo, "100%", "");
	private Map<dataCell, String> mapProps = new HashMap<dataCell, String>();
	private List<Parameter> listProps = new ArrayList<Parameter>();
	private ComboBox txtQueryName = ControlsFactory
			.CreateListField(
					"",
					"REP_QRYNAME",
					"SELECT VIEW_NAME,VIEW_NAME V2 FROM USER_VIEWS "
							+ " UNION ALL "
							+ " SELECT TABLE_NAME,TABLE_NAME T1 FROM USER_TABLES ORDER BY 1",
					lstfldinfo, "100%", "", true);
	private TextField txtExeBefore = ControlsFactory.CreateTextField("",
			"EXEC_BEFORE", lstfldinfo, "100%", "");
	private TextField txtExeAfter = ControlsFactory.CreateTextField("",
			"EXEC_AFTER", lstfldinfo, "100%", "");
	private TextField txtRepDesFile = ControlsFactory.CreateTextField("",
			"REP_DES_FILE", lstfldinfo, "100%", "");

	private TextField txtParaIndexNo = ControlsFactory.CreateTextField(null,
			"", null, "100%", "1");
	private TextField txtParaName = ControlsFactory.CreateTextField(null, "",
			null, "100%", "");
	private ComboBox txtParaType = ControlsFactory.CreateListField(null, "",
			"#QUERY_PARA_TYPE", null, "100%", null, true);
	private ComboBox txtParaDefault = ControlsFactory.CreateListField(null, "",
			"#QUERY_PARA_DEFAULT", null, "100%", null, true);
	private TextField txtParaDescrArb = ControlsFactory.CreateTextField(null,
			"", null, "100%", "");
	private TextField txtParaDescrEng = ControlsFactory.CreateTextField(null,
			"", null, "100%", "");
	private TextField txtParaListName = ControlsFactory.CreateTextField(null,
			"", null, "100%", "");

	private String QRYSES = "";
	private String QRYSES_PARENT = "";
	private String varRepPath = "XXX\\";
	private String varGrpName = "";

	private boolean listnerAdded = false;
	public String wz_width = "60%";
	public String wz_height = "75%";
	public int cno = 0;
	private dataCell varSelection = null;
	private TableLayoutVaadin tbl_cols = new TableLayoutVaadin(colHzLayout);
	private TableLayoutVaadin tbl_para = new TableLayoutVaadin(paraHzLayout);

	private NativeButton cmdParaAddUpdate = ControlsFactory.CreateCustomButton(
			"Add/Update", "img/save.png", "Add update", "");
	private NativeButton cmdParaDelete = ControlsFactory.CreateCustomButton(
			"Delete", "img/delete.png", "Add update", "");

	public frmWzDesignQuery() {

		listProps.clear();
		listProps.add(new Parameter("FIELD_NAME", null, "Field Name"));
		listProps.add(new Parameter("INDEXNO", null, "Pos"));
		listProps.add(new Parameter("DISPLAY_NAME", null, "Display Name"));
		listProps.add(new Parameter("CP_COL_TITLE_ENG", null, "Eng Title"));
		listProps.add(new Parameter("CP_COL_TITLE_ARB", null, "Arb Title"));
		listProps.add(new Parameter("WHEREOPERATOR", null, "Where Operator",
				new dataCell("NONE"), new dataCell("="), new dataCell(">="),
				new dataCell("<="), new dataCell("<"), new dataCell(">")));

		listProps.add(new Parameter("WHERECLAUSE", null, "Where Clause"));
		listProps.add(new Parameter("COLWIDTH", null, "Width"));
		listProps.add(new Parameter("ISWHERE", null, "Only in where clause"));
		listProps.add(new Parameter("FORMULAFLD", null, "Formula"));
		listProps.add(new Parameter("DATATYPEX", null, "Data Type",
				new dataCell("DATE"), new dataCell("VARCHAR2"), new dataCell(
						"NUMBER")));

		listProps.add(new Parameter("FORMULAHASSUM", null, "Formula Has Sum"));
		listProps.add(new Parameter("HASGROSS", null, "Has gross",
				new dataCell("SUM"), new dataCell("MAX")));
		listProps.add(new Parameter("LISTNAME", null, "List Name"));
		listProps.add(new Parameter("HASSUBTOT", null, "Has sub total"));
		listProps.add(new Parameter("CP_FORMAT", null, "Format", new dataCell(
				"SHORT_DATE_FORMAT"), new dataCell("QTY_FORMAT"), new dataCell(
				"MONEY_FORMAT"), new dataCell("QTY_FORMAT"), new dataCell(
				"##0.00'%'")));
		listProps.add(new Parameter("CP_ALIGN", null, "Align", new dataCell(
				"ALIGN_LEFT"), new dataCell("ALIGN_RIGHT"), new dataCell(
				"ALIGN_CENTER")));
		listProps.add(new Parameter("CP_HIDECOL", null, "Hide Column"));
		listProps.add(new Parameter("CP_STYLENAMES", null, "Style"));
		listProps.add(new Parameter("CT_ROW", null, "CT ROW"));
		listProps.add(new Parameter("CT_COL", null, "CT Col"));
		listProps.add(new Parameter("CT_VAL", null, "CT Val col"));
		listProps.add(new Parameter("CT_VAL_TOT_LABEL", null,
				"CT Val Tot label"));

		for (int i = 0; i < listProps.size(); i++) {
			Parameter pm = listProps.get(i);
			if (pm.getLov().size() > 0) {
				ComboBox c = ControlsFactory.CreateListField(null, "", "",
						null, "100%", null, true);
				pm.setUIObject(c);
				utilsVaadin.FillComboFromList(c, pm.getLov());
			} else
				pm.setUIObject(ControlsFactory.CreateTextField(null, "", null,
						"100%", ""));
		}
		utils.findParaByName("FORMULAHASSUM", listProps).setUIObject(
				new CheckBox());
		utils.findParaByName("CP_HIDECOL", listProps).setUIObject(
				new CheckBox());
		utils.findParaByName("CT_ROW", listProps).setUIObject(new CheckBox());
		utils.findParaByName("CT_COL", listProps).setUIObject(new CheckBox());
		utils.findParaByName("CT_VAL", listProps).setUIObject(new CheckBox());
		utils.findParaByName("ISWHERE", listProps).setUIObject(new CheckBox());

		utils.findParaByName("CP_ALIGN", listProps).setDefaultValue(
				"ALIGN_LEFT");
		utils.findParaByName("WHEREOPERATOR", listProps)
				.setDefaultValue("NONE");
		for (Iterator iterator = listProps.iterator(); iterator.hasNext();) {
			final Parameter pms = (Parameter) iterator.next();
			final AbstractField p = (AbstractField) pms.getUIObject();

			p.setImmediate(true);
			p.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if (tbl_cols.getTable().getValue() == null)
						return;
					int ix = (Integer) tbl_cols.getTable().getValue();
					Object v = p.getValue();
					Object vl = v;
					if (!(p.getValue() instanceof Boolean))
						vl = (String) ((v instanceof dataCell) ? ((dataCell) v)
								.getValue() : v);
					if (pms.getValueType().equals(Parameter.DATA_TYPE_NUMBER))
						vl = BigDecimal.valueOf(Double.valueOf(vl.toString()));

					if (pms.getValueType().equals(Parameter.DATA_TYPE_DATETIME)
							|| pms.getValueType().equals(
									Parameter.DATA_TYPE_DATE)) {
						SimpleDateFormat df = new SimpleDateFormat(
								utils.FORMAT_SHORT_DATE);
						Timestamp tm = null;
						try {
							tm = new Timestamp((df.parse(vl.toString()))
									.getTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						vl = tm;
					}
					if (vl instanceof Boolean) {
						if ((Boolean) vl)
							vl = pms.val_on_check;
						else
							vl = pms.val_on_uncheck;
					}
					tbl_cols.data.setFieldValue(ix, pms.getName(), vl);
				}
			});
		}

	}

	public void resetFormLayout() {

		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;

		// for (int i = 0; i < mainLayout.length; i++) {
		// mainLayout[i].removeAllComponents();
		// }

		centralPanel.removeAllComponents();
		utilsVaadin.resetLayout(mainLayout[cno], false);
		mainLayout[cno].removeAllComponents();
		commandLayout.removeAllComponents();

		/*
		 * commandLayout.removeAllComponents();
		 * 
		 * paraHzLayout.removeAllComponents(); basicLayout.removeAll();
		 * paraFormLayout.removeAll();
		 */
	}

	public void createView_back() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		ResourceManager.addComponent(centralPanel, mainLayout[cno]);
		ResourceManager.addComponent(centralPanel, commandLayout);

		centralPanel.setComponentAlignment(commandLayout,
				Alignment.MIDDLE_RIGHT);
		centralPanel.setExpandRatio(mainLayout[cno], 3.5f);
		centralPanel.setExpandRatio(commandLayout, .5f);
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		pnl_sel.setSizeFull();
		mainLayout[cno].setSizeFull();
		vl1.setSizeFull();
		mainLayout[cno].setMargin(true);
		paraHzLayout.setSizeFull();
		colHzLayout.setSizeFull();
		centralPanel.addStyleName("formLayout");

		cmdNext.setCaption("Next");

		sel_rpt_opt.setImmediate(true);
		txtSelEditQuery.setImmediate(true);
		txtSelNewExistGrp.setImmediate(true);
		txtSelNewGrp.setImmediate(true);
		txSelEditGroup.setImmediate(true);
		tbl_para.getTable().setImmediate(true);
		tbl_cols.getTable().setImmediate(true);
		tbl_cols.getTable().setColumnReorderingAllowed(false);
		tbl_cols.getTable().setSortDisabled(true);

		cmdBack.setEnabled(true);
		cmdNext.setEnabled(true);
		cmdCancel.setEnabled(true);

		ResourceManager.addComponent(centralPanel, mainLayout[cno]);
		ResourceManager.addComponent(centralPanel, commandLayout);

		ResourceManager.addComponent(commandLayout, cmdNext);
		ResourceManager.addComponent(commandLayout, cmdBack);
		ResourceManager.addComponent(commandLayout, cmdCancel);

		centralPanel.setComponentAlignment(commandLayout,
				Alignment.MIDDLE_RIGHT);
		centralPanel.setExpandRatio(mainLayout[cno], 3.5f);
		centralPanel.setExpandRatio(commandLayout, .5f);

		if (cno == 1) {
			basicLayout.setWidth("100%");
			basicLayout.removeAll();
			basicLayout.setComponentsRow(1, "caption=Query,width=100%,"
					+ utils.O2_1, txtQueryName, "width=100%," + utils.O2_2,
					"caption=Title 1,width=100%," + utils.O2_3, txtTitleArb,
					"width=100%," + utils.O2_4);
			basicLayout.setComponentsRow(2, "width=100%," + utils.O2_1,
					"width=100%," + utils.O2_2, "caption=Title 2,width=100%,"
							+ utils.O2_3, txtTitleEng, "width=100%,"
							+ utils.O2_4);
			basicLayout.setComponentsRow(3, "caption=Pg Height,width=100%,"
					+ utils.O2_1, txtPageHeight, "width=50%," + utils.O2_2,
					"caption=Pg Width,width=100%," + utils.O2_3, txtPageWidth,
					"width=50%," + utils.O2_4);
			basicLayout.setComponentsRow(4, "caption=Where Clause,width=100%,"
					+ utils.O1_1, txtWhereClause, "width=100%," + utils.O1_2);
			basicLayout.setComponentsRow(5, "caption=Design File,width=100%,"
					+ utils.O1_1, txtRepDesFile, "width=100%," + utils.O1_2);
			basicLayout.setComponentsRow(6, "caption=Exec before,width=100%,"
					+ utils.O1_1, txtExeBefore, "width=100%," + utils.O1_2);
			basicLayout.setComponentsRow(7, "caption=Exec After,width=100%,"
					+ utils.O1_1, txtExeAfter, "width=100%," + utils.O1_2);
			basicLayout.setComponentsRow(8, "caption=Cross Tab,width=100%,"
					+ utils.O3_1, chk_isCross, "width=100%," + utils.O3_2,
					"caption=Sort Column,width=100%," + utils.O3_3,
					chk_ctColSort, "width=100%," + utils.O3_4,
					"caption=Show Advance,width=100%," + utils.O3_5,
					chk_ShowAdvance, "width=100%," + utils.O3_6);
			basicLayout.setComponentsRow(9,
					"caption=Group In Query,width=100%," + utils.O3_1,
					chk_ShowGroupQuery, "width=100%," + utils.O3_2,
					"caption=Group 1,width=100%," + utils.O3_3, chk_ShowGroup1,
					"width=100%," + utils.O3_4, "caption=Group 2,width=100%,"
							+ utils.O3_5, chk_ShowGroup2, "width=100%,"
							+ utils.O3_6);

			ResourceManager.addComponent(mainLayout[cno], basicLayout);

		}

		if (cno == 0) {
			ResourceManager.addComponent(mainLayout[cno], vl1);
			ResourceManager.addComponent(mainLayout[cno], sel_rpt_opt);
			ResourceManager.addComponent(mainLayout[cno], pnl_sel);
			ResourceManager.addComponent(mainLayout[cno], vl2);
			mainLayout[cno].setExpandRatio(vl1, .7f);
			mainLayout[cno].setExpandRatio(sel_rpt_opt, 1f);
			mainLayout[cno].setExpandRatio(pnl_sel, 1.8f);
			mainLayout[cno].setExpandRatio(vl2, .5f);
			cmdBack.setEnabled(false);
			if (sel_rpt_opt.getValue() == null) {
				cmdNext.setEnabled(false);
			}

		}

		if (cno == 2) {

			mainLayout[cno].setMargin(true);
			paraHzLayout.setMargin(true);

			ResourceManager.addComponent(mainLayout[cno], paraHzLayout);
			ResourceManager.addComponent(paraHzLayout, paraFormLayout);
			ResourceManager.addComponent(paraHzLayout, tbl_para);
			tbl_para.init(paraHzLayout);

			tbl_para.setSizeFull();
			tbl_para.setHeight("300px");
			tbl_para.getTable().setHeight("275px");
			tbl_para.hideToolbar();
			tbl_para.getTable().setSelectable(true);

			paraFormLayout.setSizeFull();
			paraHzLayout.setExpandRatio(tbl_para, 2);
			paraHzLayout.setExpandRatio(paraFormLayout, 2);

			paraFormLayout.removeAll();
			paraFormLayout.setWidth("100%");

			paraFormLayout.setComponentsRow(1, "caption=Index No,width=100%,"
					+ "expand=1", txtParaIndexNo, "width=50%,expand=3");
			paraFormLayout.setComponentsRow(2, "caption=Para Name,width=100%,"
					+ "expand=1", txtParaName, "width=50%,expand=3");
			paraFormLayout.setComponentsRow(3, "caption=Type,width=100%,"
					+ "expand=1", txtParaType, "width=50%,expand=3");
			paraFormLayout.setComponentsRow(4, "caption=Descr 1,width=100%,"
					+ "expand=1", txtParaDescrArb, "width=50%,expand=3");
			paraFormLayout.setComponentsRow(5, "caption=Descr 2,width=100%,"
					+ "expand=1", txtParaDescrEng, "width=50%,expand=3");
			paraFormLayout.setComponentsRow(6, "caption=Default,width=100%,"
					+ "expand=1", txtParaDefault, "width=50%,expand=3");
			paraFormLayout.setComponentsRow(7, "caption=List,width=100%,"
					+ "expand=1", txtParaListName, "width=50%,expand=3");
			paraFormLayout.setComponentsRow(8, "width=100%,expand=.5",
					cmdParaAddUpdate, "width=100%,expand=1.5", cmdParaDelete,
					"width=100%,expand=1.5", "width=100%,expand=.5");
		}

		if (cno == 3) {
			mainLayout[cno].setMargin(true);
			colHzLayout.removeAllComponents();
			colHzLayout.setMargin(true);
			colHzLayout.setSizeFull();
			colHzLayout.setSpacing(true);
			cmdNext.setCaption("Finish & Save");

			tbl_cols.setSizeFull();
			propForm.removeAll();
			propPanel.removeAllComponents();
			propPanel.setHeight("300px");
			propPanel.setWidth("100%");
			propPanel.getContent().setHeight("-1px");
			propForm.setWidth("100%");
			propForm.setEnabled(false);
			tbl_cols.init(colHzLayout);
			tbl_cols.setHeight("300px");
			tbl_cols.getTable().setHeight("250px");
			tbl_cols.cmdAdd.setEnabled(false);

			HorizontalLayout hzCmds = new HorizontalLayout();
			hzCmds.setSizeFull();
			ResourceManager.addComponent(mainLayout[cno], colHzLayout);
			ResourceManager.addComponent(colHzLayout, tbl_cols);
			ResourceManager.addComponent(colHzLayout, propPanel);
			ResourceManager.addComponent(propPanel, propForm);
			ResourceManager.addComponent(mainLayout[cno], hzCmds);
			ResourceManager.addComponent(hzCmds, txtNewCol);
			ResourceManager.addComponent(hzCmds, cmdAddC);

			hzCmds.setComponentAlignment(txtNewCol, Alignment.BOTTOM_LEFT);
			hzCmds.setComponentAlignment(cmdAddC, Alignment.BOTTOM_LEFT);

			mainLayout[cno].setExpandRatio(colHzLayout, 3.6f);
			mainLayout[cno].setExpandRatio(hzCmds, .4f);

			int r = 1;
			for (Iterator iterator = listProps.iterator(); iterator.hasNext();) {
				Parameter pm = (Parameter) iterator.next();
				propForm.setComponentsRow(
						r,
						"caption="
								+ utils.nvl(pm.getDescription(), pm.getName())
								+ ",width=100%,expand=1.5", pm.getUIObject(),
						"width=100%,expand=2.5");
				r++;
			}

		}

		if (!listnerAdded) {
			cmdNext.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					do_next();
				}
			});
			cmdBack.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					do_prior();
				}
			});
			cmdCancel.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					do_exit();
				}
			});

			txSelEditGroup.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					on_selection();
				}
			});

			tbl_para.getTable().addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					assign_para_field();
				}
			});

			tbl_cols.getTable().addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					propForm.setEnabled(false);
					if (tbl_cols.getTable().getValue() == null)
						return;
					propForm.setEnabled(true);
					int rn = (Integer) tbl_cols.getTable().getValue();
					assign_prop_val_to_field(rn);

				}
			});

			cmdParaDelete.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (tbl_para.getTable().getValue() == null)
						return;
					Integer n = (Integer) tbl_para.getTable().getValue();
					tbl_para.data.deleteRow(n);
					tbl_para.fill_table();
					if (tbl_para.getTable().getValue() != null)
						tbl_para.getTable().setValue(null);
				}
			});
			cmdParaAddUpdate.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					assign_para_table();
					tbl_para.fill_table();
					if (tbl_para.getTable().getValue() != null)
						tbl_para.getTable().setValue(null);

					assign_para_field();

				}
			});

			cmdAddC.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					if (txtNewCol.getValue() == null)
						return;
					String colname = ((dataCell) txtNewCol.getValue())
							.getValue().toString();
					if (tbl_cols.data.locate("FIELD_NAME", colname,
							localTableModel.FIND_EXACT) > -1)
						return;
					double mx = tbl_cols.data.getSummaryOf("INDEXNO",
							localTableModel.SUMMARY_MAX) + 10;
					int r = tbl_cols.data.addRecord();

					for (int i = 0; i < listProps.size(); i++)
						tbl_cols.data.setFieldValue(r, listProps.get(i)
								.getName(), listProps.get(i).getDefaultValue());
					String tit = utils.nvl(
							utils.getSqlValue(
									"select max(CP_COL_TITLE_ENG) from invqrycols2 where field_name='"
											+ colname
											+ "' AND CP_COL_TITLE_ENG IS NOT NULL",
									con), colname);
					tbl_cols.data.setFieldValue(r, "CODE", QRYSES);
					tbl_cols.data.setFieldValue(r, "FIELD_NAME", colname);
					tbl_cols.data.setFieldValue(r, "DISPLAY_NAME", colname);
					tbl_cols.data.setFieldValue(r, "CP_COL_TITLE_ENG", tit);
					tbl_cols.data.setFieldValue(r, "INDEXNO",
							BigDecimal.valueOf(mx));
					tbl_cols.data.setFieldValue(r, "COLWIDTH",
							BigDecimal.valueOf(10));
					tbl_cols.data.setFieldValue(r, "DATATYPEX", "VARCHAR2");
					tbl_cols.data.setFieldValue(r, "CP_ALIGN", "ALIGN_LEFT");
					tbl_cols.fill_table();
					tbl_cols.getTable().setValue(r);
					assign_prop_val_to_field(r);
					try {
						fill_add_col_list();
					} catch (SQLException e) {

						e.printStackTrace();
					}

				}
			});
			listnerAdded = true;

		}
	}

	protected void on_selection() {
		try {
			utilsVaadin.FillCombo(txtSelEditQuery,
					"select code,titlearb from invqrycols1 where parentrep='"
							+ txSelEditGroup.getValue() + "'", con);
			if (txtSelEditQuery.getItemIds().size() > 0) {
				txtSelEditQuery
						.setValue(txtSelEditQuery.getItemIds().toArray()[0]);
				txtSelEditQuery.setNullSelectionAllowed(false);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void assign_prop_val_to_field(int rn) {
		for (Iterator iterator = listProps.iterator(); iterator.hasNext();) {
			Parameter par = (Parameter) iterator.next();
			par.setValue(tbl_cols.data.getFieldValue(rn, par.getName()), false);
		}
		utilsVaadin.update_para_val_to_objects(listProps);
	}

	private void assign_para_field() {
		txtParaIndexNo.setValue("1");
		txtParaDefault.setValue(null);
		txtParaDescrArb.setValue("");
		txtParaDescrEng.setValue("");
		txtParaListName.setValue("");
		txtParaName.setValue("");
		txtParaType.setValue(null);

		if (tbl_para.getTable().getValue() != null) {
			Integer in = (Integer) tbl_para.getTable().getValue();
			txtParaDefault
					.setValue(utilsVaadin.findByValue(txtParaDefault,
							tbl_para.data.getFieldValue(in, "PARA_DEFAULT")
									.toString()));
			txtParaType.setValue(utilsVaadin
					.findByValue(txtParaType,
							tbl_para.data.getFieldValue(in, "PARA_DATATYPE")
									.toString()));
			txtParaDescrArb.setValue(utils
					.nvl(tbl_para.data.getFieldValue(in, "PARA_DESCRARB")
							.toString(), ""));

			txtParaDescrEng.setValue(utils.nvl(
					tbl_para.data.getFieldValue(in, "PARA_DESCRENG"), ""));

			txtParaListName.setValue(utils.nvl(
					tbl_para.data.getFieldValue(in, "LISTNAME"), ""));

			txtParaName.setValue(tbl_para.data.getFieldValue(in, "PARAM_NAME")
					.toString());
			txtParaIndexNo.setValue(tbl_para.data.getFieldValue(in, "INDDEXNO")
					.toString());

		} else {
			double ni = tbl_para.data.getSummaryOf("INDDEXNO",
					localTableModel.SUMMARY_MAX) + 1;
			txtParaIndexNo.setValue(String.valueOf(ni));
		}
	}

	private void assign_para_table() {
		try {
			int rn = -1;
			double ni = 1;
			if (tbl_para.getTable().getValue() == null) {
				rn = tbl_para.data.addRecord();
			} else {
				rn = (Integer) tbl_para.getTable().getValue();
			}
			if (txtParaIndexNo.getValue() == null
					|| txtParaIndexNo.getValue().toString().isEmpty())
				throw new Exception("Index no must have value !");

			if (txtParaType.getValue() == null
					|| txtParaType.getValue().toString().isEmpty())
				throw new Exception("Data Type must have value !");

			if (txtParaName.getValue() == null
					|| txtParaName.getValue().toString().isEmpty())
				throw new Exception("Name must have value !");

			tbl_para.data.setFieldValue(rn, "PARAM_NAME",
					txtParaName.getValue());
			tbl_para.data.setFieldValue(rn, "PARA_DATATYPE",
					((dataCell) txtParaType.getValue()).getValue());
			if (txtParaDefault.getValue() != null)
				tbl_para.data.setFieldValue(rn, "PARA_DEFAULT",
						((dataCell) txtParaDefault.getValue()).getValue());
			tbl_para.data.setFieldValue(rn, "PARA_DESCRARB",
					txtParaDescrArb.getValue());
			tbl_para.data.setFieldValue(rn, "PARA_DESCRENG",
					txtParaDescrEng.getValue());
			tbl_para.data.setFieldValue(rn, "LISTNAME",
					txtParaListName.getValue());
			ni = Double.valueOf(txtParaIndexNo.getValue().toString());
			tbl_para.data.setFieldValue(rn, "INDDEXNO", BigDecimal.valueOf(ni));
			tbl_para.fill_table();
			tbl_para.getTable().setValue(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}

	}

	protected void show_list() {
	}

	protected void delete_data() {

	}

	public void init() {

	}

	private void resetPropsMap() {
		propForm.removeAll();
	}

	public void applyColsProps() {
		tbl_cols.listFields.clear();
		ColumnProperty cp = new ColumnProperty();
		cp.colname = "INDEXNO";
		cp.descr = "Pos";
		cp.col_class = Label.class;
		cp.data_type = Parameter.DATA_TYPE_NUMBER;
		cp.display_format = "NONE";
		cp.pos = 1;
		cp.display_width = 30;
		tbl_cols.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "FIELD_NAME";
		cp.descr = "Field Name";
		cp.col_class = Label.class;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_format = "NONE";
		cp.pos = 2;
		cp.display_width = 100;
		tbl_cols.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "ORDERNO";
		cp.descr = "SortOrder no";
		cp.col_class = TextField.class;
		cp.data_type = Parameter.DATA_TYPE_NUMBER;
		cp.display_format = "NONE";
		cp.pos = 3;
		cp.display_width = 30;
		tbl_cols.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "GROUP_NAME";
		cp.descr = "First Group";
		cp.col_class = TextField.class;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_format = "NONE";
		cp.pos = 4;
		cp.display_width = 70;
		tbl_cols.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "GROUP_NAME2";
		cp.descr = "Under Group";
		cp.col_class = TextField.class;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_format = "NONE";
		cp.pos = 5;
		cp.display_width = 70;
		tbl_cols.listFields.add(cp);

	}

	public void applyParaCols() {
		tbl_para.listFields.clear();

		ColumnProperty cp = new ColumnProperty();
		cp.colname = "INDDEXNO";
		cp.descr = "Pos";
		cp.col_class = Label.class;
		cp.data_type = Parameter.DATA_TYPE_NUMBER;
		cp.display_format = "NONE";
		cp.pos = 1;
		cp.display_width = 30;
		tbl_para.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "PARAM_NAME";
		cp.descr = "Name";
		cp.col_class = Label.class;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_format = "NONE";
		cp.pos = 2;
		cp.display_width = 100;
		tbl_para.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "PARA_DATATYPE";
		cp.descr = "Type";
		cp.col_class = Label.class;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_format = "NONE";
		cp.pos = 3;
		cp.display_width = 100;
		tbl_para.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "PARA_DEFAULT";
		cp.descr = "Default";
		cp.col_class = Label.class;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_format = "NONE";
		cp.pos = 4;
		cp.display_width = 100;
		tbl_para.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "PARA_DESCRARB";
		cp.descr = "Descr";
		cp.col_class = Label.class;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_format = "NONE";
		cp.pos = 5;
		cp.display_width = 100;
		tbl_para.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "PARA_DESCRENG";
		cp.descr = "Descr Eng";
		cp.col_class = Label.class;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_format = "NONE";
		cp.pos = 6;
		cp.display_width = 100;
		tbl_para.listFields.add(cp);

		cp = new ColumnProperty();
		cp.colname = "LISTNAME";
		cp.descr = "List Name";
		cp.col_class = Label.class;
		cp.data_type = Parameter.DATA_TYPE_STRING;
		cp.display_format = "NONE";
		cp.pos = 7;
		cp.display_width = 100;
		tbl_para.listFields.add(cp);
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		if (wz_window == null) {
			wz_window = ControlsFactory.CreateWindow(wz_width, wz_height, true,
					true);
			parentLayout = (VerticalLayout) wz_window.getContent();
			wz_window.setCaption("Wizard for design Query ");
		}

		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wz_window)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(wz_window);
		}
		cno = 0;
		createView();
		load_data();

	}

	public void load_data_for_back() {
		try {
			if (cno == 1) {
			}
			if (cno == 2) {
				tbl_para.fill_table();
				assign_para_field();
			}
			if (cno == 3) {
				// applyColsProps();
				tbl_cols.fill_table();
				fill_add_col_list();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void load_data() {
		try {
			if (cno == 1) {
				if (sel_rpt_opt.getValue() == null) {
					throw new Exception("Must select report ! ");
				}
				utilsVaadin.resetValues(basicLayout, false, false);
				utilsVaadin.setDefaultValues(lstfldinfo, true);
				varRepPath = "XXX\\";
				varSelection = (dataCell) sel_rpt_opt.getValue();
				dataCell dc = new dataCell("", "");
				if (varSelection.getValue().equals("1")) {
					QRYSES_PARENT = txtSelNewGrp.getValue() + "";
					QRYSES = QRYSES_PARENT + "1";
					dc.setValue("New Query #  " + txtSelNewGrpName.getValue(),
							QRYSES);
				}

				if (varSelection.getValue().equals("2")) {
					QRYSES_PARENT = txtSelNewExistGrp.getValue() + "";
					int n = Integer.valueOf(utils.getSqlValue(
							"select nvl(count(*),1) from invqrycols1 where parentrep='"
									+ txtSelNewExistGrp.getValue() + "'", con));
					QRYSES = QRYSES_PARENT + (++n);
					dc.setValue(
							"New  Query # " + txtSelNewExistGrp.getDisplay(),
							QRYSES);

				}

				if (varSelection.getValue().equals("3")) {
					QRYSES_PARENT = txSelEditGroup.getValue() + "";
					QRYSES = ((dataCell) txtSelEditQuery.getValue()).getValue()
							+ "";
					dc.setValue(" Edit Group : " + txSelEditGroup.getDisplay(),
							((dataCell) txtSelEditQuery.getValue())
									.getDisplay());
					PreparedStatement pst = con.prepareStatement(
							"select *from  invqrycols1 where code='" + QRYSES
									+ "'", ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					ResultSet rst = pst.executeQuery();
					utilsVaadin.assignValues(rst, lstfldinfo);
					pst.close();
				}

				wz_window.setCaption("Query Designer : " + dc.getDisplay()
						+ " " + dc.getValue());

			}
			if (cno == 2) {
				applyParaCols();
				tbl_para.data.executeQuery(
						"select *from invqrycolspara where code='" + QRYSES
								+ "' order by inddexno", true);
				tbl_para.fill_table();
				tbl_para.getTable().setValue(null);
				double ni = tbl_para.data.getSummaryOf("INDDEXNO",
						localTableModel.SUMMARY_MAX) + 1;
				txtParaIndexNo.setValue(String.valueOf(ni));

			}
			if (cno == 3) {
				tbl_cols.data.executeQuery(
						"select *from invqrycols2 where code='" + QRYSES
								+ "' order by indexno", true);
				applyColsProps();
				tbl_cols.fill_table();
				fill_add_col_list();
				for (Iterator iterator = listProps.iterator(); iterator
						.hasNext();) {
					Parameter pm = (Parameter) iterator.next();
					if (tbl_cols.data.getColByName(pm.getName()).isNumber())
						pm.setValueType(Parameter.DATA_TYPE_NUMBER);
					if (tbl_cols.data.getColByName(pm.getName()).isDateTime())
						pm.setValueType(Parameter.DATA_TYPE_DATETIME);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void fill_add_col_list() throws SQLException {
		String s = "";
		for (int i = 0; i < tbl_cols.data.getRowCount(); i++)
			s = s + (s.isEmpty() ? "" : ",") + "'"
					+ tbl_cols.data.getFieldValue(i, "FIELD_NAME") + "'";
		if (s.trim().isEmpty())
			s = "' '";
		utilsVaadin.FillCombo(
				txtNewCol,
				"SELECT COLUMN_NAME n1,column_name n2 FROM"
						+ " USER_TAB_COLS WHERE TABLE_NAME='"
						+ txtQueryName.getValue()
						+ "' and COLUMN_NAME NOT IN (" + s
						+ ") order by column_id", con);
	}

	public void print_data() {

	}

	public void validate_data() throws Exception {
	}

	public void save_data() {
		try {
			validate_data();
			varSelection = (dataCell) sel_rpt_opt.getValue();
			if (varSelection.getValue().equals("1")) {
				con.setAutoCommit(false);
				String sq = "insert into invqrycols1(CODE, TITLEARB, TITLEENG,"
						+ " PARENTREP, REP_PATH, REP_LEVELNO,"
						+ " REP_QRYNAME, REP_USECOUNT, REP_CHILDCOUNT) values "
						+ " (:CODE, :TITLEARB, :TITLEENG,"
						+ " :PARENTREP, :REP_PATH, :REP_LEVELNO,"
						+ " :REP_QRYNAME, :REP_USECOUNT, :REP_CHILDCOUNT) ";

				QueryExe qe = new QueryExe(sq, con);
				qe.setParaValue("CODE", QRYSES_PARENT);
				qe.setParaValue("TITLEARB", txtSelNewGrpName.getValue());
				qe.setParaValue("TITLEENG", txtSelNewGrpName.getValue());
				qe.setParaValue("PARENTREP", "");
				qe.setParaValue("REP_PATH", "XXX\\" + QRYSES_PARENT + "\\");
				qe.setParaValue("REP_LEVELNO", 1);
				qe.setParaValue("REP_QRYNAME", "PARENT");
				qe.setParaValue("REP_USECOUNT", 0);
				qe.setParaValue("REP_CHILDCOUNT", 0);
				qe.execute();
				qe.close();
			}

			utils.execSql("begin " + "delete from invqrycols1 where code='"
					+ QRYSES + "';" + "delete from invqrycols2 where code='"
					+ QRYSES + "'; "
					+ "delete from invqrycolspara where code='" + QRYSES + "';"
					+ "end;", con);
			QueryExe qe = ControlsFactory.autoGenerateSql(lstfldinfo, con,
					new Parameter("CODE", QRYSES), new Parameter("REP_PATH",
							"XXX\\" + QRYSES_PARENT + "\\" + QRYSES + "\\"),
					new Parameter("PARENTREP", QRYSES_PARENT));
			qe.AutoGenerateInsertStatment("INVQRYCOLS1");
			qe.execute();
			qe.close();
			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("CODE", QRYSES);
			tbl_para.insert_to_table("INVQRYCOLSPARA", mp,
					tbl_para.data.getColumnsString(), "");

			Map<String, Object> mpP = new HashMap<String, Object>();
			mpP.put("CODE", QRYSES);
			tbl_cols.insert_to_table("INVQRYCOLS2", mpP,
					tbl_cols.data.getColumnsString(), "");

			con.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void showInitView() {
		QRYSES = "";
		initForm();

	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void do_prior() {
		try {
			cno--;
			createView_back();
			load_data_for_back();
		} catch (Exception ex) {
			ex.printStackTrace();
			Channelplus3Application
					.getInstance()
					.getMainWindow()
					.showNotification("", ex.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void do_next() {
		try {

			if (cno == 0)
				do_validate_page_0();

			if (cno == 1)
				do_validate_page_1();

			if (cno == 2)
				do_validate_page_2();

			if (cno == 3) {
				save_data();
				do_exit();
				return;
			}

			cno++;
			createView();
			load_data();

		} catch (Exception ex) {
			ex.printStackTrace();
			Channelplus3Application
					.getInstance()
					.getMainWindow()
					.showNotification("", ex.getMessage(),
							Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void do_validate_page_0() throws Exception {
		if (sel_rpt_opt.getValue() == null)
			throw new Exception("Select action type ");
		dataCell dc = ((dataCell) sel_rpt_opt.getValue());
		if (dc.getValue().equals("1")) {
			if (txtSelNewGrp.getValue() == null
					|| txtSelNewGrp.getValue().toString().isEmpty())
				throw new Exception("Enter value for new group !");

			String fnd = utils.getSqlValue(
					"select max(titlearb) from invqrycols1 where code='"
							+ txtSelNewGrp.getValue() + "'", con);
			if (!fnd.isEmpty())
				throw new Exception("Found group / Query # "
						+ txtSelNewGrp.getValue() + " - " + fnd);
		}
		if (dc.getValue().equals("2")) {
			if (txtSelNewExistGrp.getValue() == null
					|| txtSelNewExistGrp.getValue().toString().isEmpty())
				throw new Exception("Enter value for existed group !");
		}
		if (dc.getValue().equals("3")) {
			if (txtSelEditQuery.getValue() == null)
				throw new Exception("Enter value for existed query !");
		}
	}

	public void do_exit() {
		if (Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(wz_window)) {
			Channelplus3Application.getInstance().getMainWindow()
					.removeWindow(wz_window);
		}
	}

	public void do_validate_page_1() throws Exception {

	}

	public void do_validate_page_2() throws Exception {

	}

	public void do_validate_page_3() throws Exception {

	}

	public void init_redesign(String sel, String pq, String qs) {
		initForm();
		List<dataCell> ls = new ArrayList<dataCell>();
		for (Iterator iterator = sel_rpt_opt.getItemIds().iterator(); iterator
				.hasNext();) {
			Object dc = (Object) iterator.next();
			if (dc != null && (dc instanceof dataCell))
				ls.add((dataCell) dc);
		}
		sel_rpt_opt.setValue(utils.findByValue(ls, sel));
		if (sel.equals("1")) {

		}
		if (sel.equals("2")) {
			cno = 0;
			String s = utils.getSqlValue(
					"select titlearb from invqrycols1 where code='" + pq + "'",
					con);
			txtSelNewExistGrp.setDisplayValue(pq, s);
			do_next();
		}
		if (sel.equals("3")) {
			cno = 0;
			String s = utils.getSqlValue(
					"select titlearb from invqrycols1 where code='" + pq + "'",
					con);
			txSelEditGroup.setDisplayValue(pq, s);
			on_selection();
			txtSelEditQuery.setValue(utilsVaadin.findByValue(txtSelEditQuery,
					qs));
			do_next();
		}
	}
}