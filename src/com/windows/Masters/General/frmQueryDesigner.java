package com.windows.Masters.General;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.generic.ColumnProperty;
import com.generic.ControlsFactory;
import com.generic.FieldInfo;
import com.generic.ResourceManager;
import com.generic.TableLayoutVaadin;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.generic.utilsVaadin;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class frmQueryDesigner implements transactionalForm {
	private AbstractOrderedLayout parentLayout = null;
	private boolean listnerAdded = false;
	public String QRYSES = "";
	private Connection con = null;
	List<FieldInfo> lstfldinfo = new ArrayList<FieldInfo>();
	TabSheet tbsheet = new TabSheet();
	Tree tree = new Tree();
	private localTableModel data_Items = new localTableModel();

	private final List<ColumnProperty> lstColumnCols = new ArrayList<ColumnProperty>();
	private final List<ColumnProperty> lstTextPropCols = new ArrayList<ColumnProperty>();
	private final List<ColumnProperty> lstParaCols = new ArrayList<ColumnProperty>();
	private final List<ColumnProperty> lstClauseCols = new ArrayList<ColumnProperty>();

	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout basicLayout = new VerticalLayout();
	private VerticalLayout colTableLayout = new VerticalLayout();
	private VerticalLayout textPropTableLayout = new VerticalLayout();
	private VerticalLayout paraTableLayout = new VerticalLayout();
	private VerticalLayout clauseTableLayout = new VerticalLayout();

	TableLayoutVaadin tableColumn = new TableLayoutVaadin(colTableLayout);
	TableLayoutVaadin tableTextProp = new TableLayoutVaadin(textPropTableLayout);
	TableLayoutVaadin tablePara = new TableLayoutVaadin(paraTableLayout);
	TableLayoutVaadin tableClause = new TableLayoutVaadin(clauseTableLayout);

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private HorizontalLayout content1Layout = new HorizontalLayout();
	private HorizontalLayout comboLayout = new HorizontalLayout();
	private HorizontalLayout codeLayout = new HorizontalLayout();
	private HorizontalLayout table1Layout = new HorizontalLayout();
	private HorizontalLayout table2Layout = new HorizontalLayout();
	private HorizontalLayout txtSQLGenLayout = new HorizontalLayout();

	private ComboBox cmbTableViews = ControlsFactory
			.CreateListField(
					"Tables / Views",
					"TXTTABLEANDUSE",
					"select table_name,table_name t1 from user_tables union all select view_name,view_name v1 from user_views order by 1",
					lstfldinfo);

	private TextField txtCode = ControlsFactory.CreateTextField("Code",
			"TXTCODE", lstfldinfo);
	private TextField txtSQLGen = ControlsFactory.CreateTextField(
			"SQL Generator", "SQL", lstfldinfo);

	private NativeButton cmdSave = new NativeButton("Save");
	private NativeButton cmdDelete = new NativeButton("Delete");
	private NativeButton cmdList = new NativeButton("List");
	private NativeButton cmdCls = new NativeButton("Clear");

	public void save_data() {

	}

	public void applyColumnsOnColumn() {
		lstColumnCols.clear();

		ColumnProperty cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "TEXTCOLNAMES";
		cp.descr = "Columns Names";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		tableColumn.addColumn(cp, false);
	}

	public void applyColumnsOnTextProp() {
		lstTextPropCols.clear();

		ColumnProperty cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "VAR";
		cp.descr = "Var";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableTextProp.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "VALUE";
		cp.descr = "Value";
		cp.display_width = 150;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableTextProp.addColumn(cp, false);
	}

	public void applyColumnsOnPara() {
		lstParaCols.clear();

		ColumnProperty cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "NAME";
		cp.descr = "Name";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		tablePara.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "TYPE";
		cp.descr = "Type";
		cp.display_width = 150;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tablePara.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "DESCR";
		cp.descr = "Descr";
		cp.display_width = 150;
		cp.display_type = "NONE";
		cp.pos = 3;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tablePara.addColumn(cp, false);
	}

	public void applyColumnsOnClause() {
		lstClauseCols.clear();

		ColumnProperty cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "OPERATOR";
		cp.descr = "Operator";
		cp.display_width = 50;
		cp.display_type = "NONE";
		cp.pos = 1;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = Channelplus3Application.getInstance().getFrmUserLogin().FORMAT_MONEY;
		cp.other_styles = "";
		tableClause.addColumn(cp, false);

		cp = new ColumnProperty();
		cp.col_class = TextField.class;
		cp.colname = "VALUE";
		cp.descr = "Value";
		cp.display_width = 150;
		cp.display_type = "NONE";
		cp.pos = 2;
		cp.display_align = "ALIGN_LEFT";
		cp.display_format = "NONE";
		cp.other_styles = "";
		tableClause.addColumn(cp, false);
	}

	public void load_data() {
		try {
			tableTextProp.data.executeQuery(
					"select '' var,'' value from dual where 1=2", true);
			add_default_data();
			txtCode.setReadOnly(false);
			utilsVaadin.resetValues(basicLayout, false, false);
			if (!QRYSES.isEmpty()) {
				PreparedStatement pstmt = con
						.prepareStatement(
								"select * from invqrycols1 where code='"
										+ QRYSES + "'",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
				ResultSet rst = pstmt.executeQuery();
				utilsVaadin.assignValues(rst, lstfldinfo);
				pstmt.close();
				txtCode.setReadOnly(true);
				assign_data_from_table();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void add_default_data() {
		int r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "FIELD_NAME");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "INDEXNO");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "DISPLAY_NAME");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "FORMATMASK");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "ORDERNO");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "ORDERTYPE");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "COLWIDTH");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "SUMBY");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "ISWHERE");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "FORMULAFLD");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "DATATYPEX");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "FORMULAHASSUM");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "HASGROSS");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "LISTNAME");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "HASSUBTOT");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CP_FORMAT");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CP_STYLENAMES");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CP_ALIGN");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CP_HIDECOL");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CP_SUBGROUP");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "GROUP_NAME");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "GROUP_NAME2");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CT_ROW");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CT_COL");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CT_VAL");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CT_VAL_TOT_LABEL");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CP_COL_TITLE_ENG");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
		tableTextProp.data.setFieldValue(r, "VAR", "CP_COL_TITLE_ARB");
		tableTextProp.data.setFieldValue(r, "VALUE", "");
		r = tableTextProp.data.addRecord();
	}

	public void assign_data_from_table() throws SQLException {
		int rowNo = (Integer) tableColumn.getTable().getValue();
		PreparedStatement ps = con
				.prepareStatement("select *from invqrycols2 where code= '"
						+ QRYSES
						+ "' and field_name="
						+ tableTextProp.data
								.getFieldValue(rowNo, "column_name"));
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int record = tableTextProp.data.locate("VAR", "FIELD_NAME",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("FIELD_NAME"));

			record = tableTextProp.data.locate("VAR", "INDEXNO",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("INDEXNO"));

			record = tableTextProp.data.locate("VAR", "DISPLAY_NAME",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("DISPLAY_NAME"));

			record = tableTextProp.data.locate("VAR", "FORMATMASK",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("FORMATMASK"));

			record = tableTextProp.data.locate("VAR", "ORDERNO",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("ORDERNO"));

			record = tableTextProp.data.locate("VAR", "ORDERTYPE",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("ORDERTYPE"));

			record = tableTextProp.data.locate("VAR", "COLWIDTH",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("COLWIDTH"));

			record = tableTextProp.data.locate("VAR", "SUMBY",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("SUMBY"));

			record = tableTextProp.data.locate("VAR", "ISWHERE",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("ISWHERE"));

			record = tableTextProp.data.locate("VAR", "FORMULAFLD",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("FORMULAFLD"));

			record = tableTextProp.data.locate("VAR", "DATATYPEX",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("DATATYPEX"));

			record = tableTextProp.data.locate("VAR", "FORMULAHASSUM",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("FIELD_NAME"));

			record = tableTextProp.data.locate("VAR", "HASGROSS",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("HASGROSS"));

			record = tableTextProp.data.locate("VAR", "LISTNAME",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("LISTNAME"));

			record = tableTextProp.data.locate("VAR", "HASSUBTOT",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("HASSUBTOT"));

			record = tableTextProp.data.locate("VAR", "CP_FORMAT",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CP_FORMAT"));

			record = tableTextProp.data.locate("VAR", "CP_STYLENAMES",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CP_STYLENAMES"));

			record = tableTextProp.data.locate("VAR", "CP_ALIGN",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CP_ALIGN"));

			record = tableTextProp.data.locate("VAR", "CP_HIDECOL",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CP_HIDECOL"));

			record = tableTextProp.data.locate("VAR", "CP_SUBGROUP",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CP_SUBGROUP"));

			record = tableTextProp.data.locate("VAR", "GROUP_NAME",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("GROUP_NAME"));

			record = tableTextProp.data.locate("VAR", "GROUP_NAME2",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("GROUP_NAME2"));

			record = tableTextProp.data.locate("VAR", "CT_ROW",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CT_ROW"));

			record = tableTextProp.data.locate("VAR", "CT_COL",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CT_COL"));

			record = tableTextProp.data.locate("VAR", "CT_VAL",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CT_VAL"));

			record = tableTextProp.data.locate("VAR", "CT_VAL_TOT_LABEL",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CT_VAL_TOT_LABEL"));

			record = tableTextProp.data.locate("VAR", "CP_COL_TITLE_ENG",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CP_COL_TITLE_ENG"));

			record = tableTextProp.data.locate("VAR", "CP_COL_TITLE_ARB",
					data_Items.FIND_EXACT);
			tableTextProp.data.setFieldValue(record, "VALUE", rs
					.getString("CP_COL_TITLE_ARB"));
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
		try {
			if (data_Items.getDbclass() == null) {
				data_Items.createDBClassFromConnection(con);
			}

		} catch (SQLException e) {
		}
		createView();
		applyColumnsOnClause();
		applyColumnsOnColumn();
		applyColumnsOnPara();
		applyColumnsOnTextProp();
		load_data();
	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		centralPanel.setSizeFull();
		mainLayout.setWidth("600px");
		basicLayout.setWidth("100%");
		colTableLayout.setWidth("100%");
		textPropTableLayout.setWidth("100%");
		paraTableLayout.setWidth("100%");
		clauseTableLayout.setWidth("100%");
		content1Layout.setWidth("100%");
		comboLayout.setWidth("100%");
		codeLayout.setWidth("100%");
		table1Layout.setWidth("100%");
		table2Layout.setWidth("100%");
		txtSQLGenLayout.setWidth("100%");

		cmbTableViews.setWidth("100%");
		txtCode.setWidth("100%");
		txtSQLGen.setWidth("100%");

		colTableLayout.setHeight("150px");
		tableColumn.getTable().setHeight("140px");

		textPropTableLayout.setHeight("150px");
		tableTextProp.getTable().setHeight("140px");

		paraTableLayout.setHeight("150px");
		tablePara.getTable().setHeight("140px");

		clauseTableLayout.setHeight("150px");
		tableClause.getTable().setHeight("140px");

		txtSQLGen.setRows(5);

		basicLayout.addStyleName("formLayout");

		ResourceManager.addComponent(centralPanel, mainLayout);

		ResourceManager.addComponent(mainLayout, buttonLayout);
		ResourceManager.addComponent(mainLayout, basicLayout);

		ResourceManager.addComponent(buttonLayout, cmdSave);
		ResourceManager.addComponent(buttonLayout, cmdDelete);
		ResourceManager.addComponent(buttonLayout, cmdList);
		ResourceManager.addComponent(buttonLayout, cmdCls);

		ResourceManager.addComponent(basicLayout, content1Layout);
		ResourceManager.addComponent(basicLayout, table1Layout);
		ResourceManager.addComponent(basicLayout, table2Layout);
		ResourceManager.addComponent(basicLayout, txtSQLGenLayout);

		ResourceManager.addComponent(content1Layout, comboLayout);
		ResourceManager.addComponent(content1Layout, codeLayout);

		ResourceManager.addComponent(table1Layout, colTableLayout);
		ResourceManager.addComponent(table1Layout, textPropTableLayout);

		ResourceManager.addComponent(table2Layout, paraTableLayout);
		ResourceManager.addComponent(table2Layout, clauseTableLayout);

		ResourceManager.addComponent(txtSQLGenLayout, txtSQLGen);

		ResourceManager.addComponent(comboLayout, cmbTableViews);

		ResourceManager.addComponent(txtSQLGenLayout, txtSQLGen);

		ResourceManager.addComponent(codeLayout, txtCode);

		content1Layout.setExpandRatio(comboLayout, 2f);
		content1Layout.setExpandRatio(codeLayout, 2f);

		table1Layout.setExpandRatio(colTableLayout, 2f);
		table1Layout.setExpandRatio(textPropTableLayout, 2f);

		table2Layout.setExpandRatio(paraTableLayout, 2f);
		table2Layout.setExpandRatio(clauseTableLayout, 2f);

		txtSQLGenLayout.setExpandRatio(txtSQLGen, 4f);
		if (!listnerAdded) {
			cmdList.addListener(new ClickListener() {

				public void buttonClick(ClickEvent event) {
					show_list();
				}
			});
			cmdSave.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					save_data();
				}
			});
			cmdCls.addListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
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
							QRYSES = tv.getData().getFieldValue(rn, "code")
									.toString();
							load_data();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, con, "select code,titlearb from invqrycols1 order by rep_path",
					true);

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

}
