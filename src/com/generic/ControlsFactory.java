package com.generic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.doc.views.TableView;
import com.doc.views.TableView.SelectionListener;
import com.example.components.SearchField;
import com.example.components.SearchField.ButtonPress;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class ControlsFactory {

	public static OptionGroup CreateOptionGroup(String caption, String fldname,
			List<FieldInfo> lstFlds, ValueChangeListener vc, String width,
			String height, dataCell... opt) {
		OptionGroup op = new OptionGroup(caption);
		for (int i = 0; i < opt.length; i++) {
			op.addItem(opt[i]);
		}

		FieldInfo fld = new FieldInfo(fldname.toUpperCase());
		fld.obj = op;
		fld.fieldCaption = caption;

		op.setWidth(width);
		op.setHeight(height);
		if (lstFlds != null)
			lstFlds.add(fld);
		op.addListener(vc);
		return op;
	}

	public static TextField CreateTextField(String caption, FieldInfo fld,
			List<FieldInfo> lstFlds) {
		TextField txt = new TextField(caption);
		fld.obj = txt;
		fld.fieldCaption = caption;

		if (lstFlds != null) {
			lstFlds.add(fld);
		}
		return txt;
	}

	public static TextField CreateTextField(String caption, String fldname,
			List<FieldInfo> lstFlds, Object defaultValue, String dataType,
			String format, boolean required, ValueChangeListener vlc) {
		TextField txt = CreateTextField(caption, fldname, lstFlds,
				defaultValue, dataType, format, required);
		txt.addListener(vlc);
		txt.setImmediate(true);
		return txt;
	}

	public static TextField CreateTextField(String caption, String fldname,
			List<FieldInfo> lstFlds, Object defaultValue, String dataType,
			String format, boolean required) {
		final TextField txt = CreateTextField(caption, fldname, lstFlds);
		txt.setRequired(required);
		txt.setRequiredError("You must enter this field !");

		if (lstFlds != null) {
			final FieldInfo fld = utils.findFieldInfoByObject(txt, lstFlds);
			fld.defaultValue = utils.nvl(defaultValue, "");
			fld.fieldType = dataType;
			fld.format = format;
			txt.setImmediate(true);
			if (fld.fieldType.equals(Parameter.DATA_TYPE_NUMBER)
					&& !ResourceManager.isRTL())
				txt.addStyleName("numberStyle");
			txt.addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						utilsVaadin.setText(fld);
					} catch (Exception ex) {
						ex.printStackTrace();
						txt.focus();
						Channelplus3Application
								.getInstance()
								.getMainWindow()
								.showNotification("", ex.getMessage(),
										Notification.TYPE_ERROR_MESSAGE);
					}
				}
			});
		}

		return txt;
	}

	public static HorizontalSplitPanel CreateHorizontalSplitPanel(Component c1,
			Component c2, int pos) {
		HorizontalSplitPanel h = new HorizontalSplitPanel();
		h.setFirstComponent(c1);
		h.setSecondComponent(c2);
		h.setSplitPosition(pos);
		c1.setSizeFull();
		c2.setSizeFull();
		return h;
	}

	public static VerticalSplitPanel CreateVerticalSplitPanel(Component c1,
			Component c2, int pos) {
		VerticalSplitPanel h = new VerticalSplitPanel();
		h.setFirstComponent(c1);
		h.setSecondComponent(c2);
		h.setSplitPosition(pos);
		return h;
	}

	public static TextField CreateTextField(String caption, String fldname,
			List<FieldInfo> lstFlds) {
		TextField txt = new TextField(caption);
		FieldInfo fld = new FieldInfo(fldname.toUpperCase());
		fld.obj = txt;
		fld.fieldCaption = caption;
		if (lstFlds != null) {
			lstFlds.add(fld);
		}

		return txt;
	}

	public static TextField CreateTextField(String caption, String fldname,
			List<FieldInfo> lstFlds, String width, Object defaultValue) {
		TextField txt = CreateTextField(caption, fldname, lstFlds);
		if (!width.isEmpty())
			txt.setWidth(width);
		if (defaultValue != null && lstFlds != null) {
			FieldInfo fld = utils.findFieldInfoByObject(txt, lstFlds);
			fld.defaultValue = defaultValue;
		}
		return txt;
	}

	public static CheckBox CreateCheckField(String caption, String fldname,
			String valueOnFalse, String valueOnTrue, List<FieldInfo> lstFlds) {
		CheckBox txt = new CheckBox(caption);
		FieldInfo fld = new FieldInfo(fldname.toUpperCase());
		fld.obj = txt;
		fld.valueOnFalse = valueOnFalse;
		fld.valueOnTrue = valueOnTrue;
		if (lstFlds != null) {
			lstFlds.add(fld);
		}

		return txt;
	}

	public static CheckBox CreateCheckField(String caption, String fldname,
			String valueOnFalse, String valueOnTrue, List<FieldInfo> lstFlds,
			String width, Object defaultValue) {
		CheckBox txt = CreateCheckField(caption, fldname, valueOnFalse,
				valueOnTrue, lstFlds);
		
		FieldInfo fld = null;

		if (lstFlds != null)
			fld = utils.findFieldInfoByObject(txt, lstFlds);

		Boolean dv = false;
		if (defaultValue instanceof Boolean)
			dv = (Boolean) defaultValue;
		if (lstFlds != null && fld != null)
			if (defaultValue.toString().equals(fld.valueOnTrue))
				fld.defaultValue = true;
			else
				fld.defaultValue = false;
		txt.setWidth(width);
		return txt;
	}

	public static DateField CreateDateField(String caption, String fldname,
			List<FieldInfo> lstFlds) {
		DateField txt = new DateField(caption);
		FieldInfo fld = new FieldInfo(fldname.toUpperCase());
		fld.obj = txt;
		if (lstFlds != null) {
			lstFlds.add(fld);
		}
		txt.setValue(null);
		txt.setResolution(DateField.RESOLUTION_DAY);
		txt.setDateFormat(utils.FORMAT_SHORT_DATE);
		return txt;
	}

	public static DateField CreateDateField(String caption, String fldname,
			List<FieldInfo> lstFlds, String width) {
		DateField txt = CreateDateField(caption, fldname, lstFlds);
		txt.setWidth(width);
		return txt;
	}

	public static DateField CreateDateField(String caption, String fldname,
			List<FieldInfo> lstFlds, String width, String format,
			ValueChangeListener vlc) {
		DateField txt = CreateDateField(caption, fldname, lstFlds, width);
		txt.setDateFormat(format);
		if (vlc != null) {
			txt.setImmediate(true);
			txt.addListener(vlc);
		}
		return txt;
	}

	public static ComboBox CreateListField(String caption, String fldname,
			String sqlString, List<FieldInfo> lstFlds, String width,
			Object defaultValue) {
		ComboBox txt = CreateListField(caption, fldname, sqlString, lstFlds);
		if (!width.isEmpty())
			txt.setWidth(width);

		if (defaultValue != null) {
			FieldInfo fld = utils.findFieldInfoByObject(txt, lstFlds);
			fld.defaultValue = defaultValue;
		}

		return txt;

	}

	public static ComboBox CreateListField(String caption, String fldname,
			String sqlString, List<FieldInfo> lstFlds, String width,
			Object defaultValue, boolean canAdd) {
		final ComboBox txt = CreateListField(caption, fldname, sqlString,
				lstFlds);
		if (!width.isEmpty())
			txt.setWidth(width);

		if (defaultValue != null) {
			FieldInfo fld = utils.findFieldInfoByObject(txt, lstFlds);
			fld.defaultValue = defaultValue;
		}
		if (canAdd) {
			txt.setImmediate(true);
			txt.setNewItemsAllowed(true);
			txt.setNewItemHandler(new NewItemHandler() {

				public void addNewItem(String newItemCaption) {
					dataCell dc = utilsVaadin.findByValue(txt, newItemCaption);
					if (dc != null)
						return;
					dc = new dataCell(newItemCaption, newItemCaption);
					txt.addItem(dc);
					txt.setValue(dc);
				}
			});
		}

		return txt;

	}

	public static ComboBox CreateListField(String caption, String fldname,
			String sqlString, List<FieldInfo> lstFlds) {
		ComboBox txt = new ComboBox(caption);
		FieldInfo fld = new FieldInfo(fldname.toUpperCase());
		fld.obj = txt;
		if (lstFlds != null) {
			lstFlds.add(fld);
		}
		if (sqlString != null && sqlString.length() > 0) {
			String sql = sqlString;
			if (sqlString.startsWith("#")) {
				sql = "select name,descr from relists where idlist='"
						+ sqlString.replace("#", "") + "' order by name";
			}

			try {
				utilsVaadin.FillCombo(txt, sql, Channelplus3Application
						.getInstance().getFrmUserLogin().getDbc()
						.getDbConnection());
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return txt;
	}

	public static NativeButton CreateToolbarButton(String iconfile, String descr) {
		return CreateCustomButton("", iconfile, descr, "toolbar");
	}

	public static NativeButton CreateCustomButton(String caption,
			String iconfile, String descr, String stylename) {
		NativeButton bc = new NativeButton();
		if (iconfile != null && !iconfile.isEmpty()) {
			bc.setIcon(new ThemeResource(iconfile));
		}
		bc.setDescription(descr);
		bc.setStyleName(stylename);
		bc.setCaption(caption);
		return bc;

	}

	public static NativeButton CreateCustomButton(String caption,
			String iconfile, String descr, String stylename, ClickListener clk) {
		NativeButton btn = CreateCustomButton(caption, iconfile, descr,
				stylename);
		btn.addListener(clk);
		return btn;
	}

	public static Window CreateWindow(String width, String height,
			boolean isModal, boolean showwnd) {
		Window wnd = new Window();
		wnd.setModal(isModal);
		wnd.setContent(new VerticalLayout());
		wnd.getContent().setSizeFull();
		wnd.setHeight(height);
		wnd.setWidth(width);
		wnd.setCloseShortcut(KeyCode.ESCAPE, null);
		if (showwnd) {

			if (!Channelplus3Application.getInstance().getMainWindow()
					.getChildWindows().contains(wnd)) {
				Channelplus3Application.getInstance().getMainWindow()
						.addWindow(wnd);
			}
		}
		return wnd;

	}

	public static Table CreatTable(String caption, String colID,
			List<ColumnProperty> lstcols) {
		Table tbl = new Table("");
		if (!caption.isEmpty()) {
			tbl.setCaption(caption);
		}
		if (!colID.isEmpty()) {
			Connection con = Channelplus3Application.getInstance()
					.getFrmUserLogin().getDbc().getDbConnection();
			try {
				utilsVaadin.applyColumns(colID, tbl, lstcols, con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tbl;
	}

	public static transactionalForm CreateForm(transactionalForm frm,
			AbstractOrderedLayout layout, Map<String, transactionalForm> mp,
			String frm_name) {
		frm.setParentLayout(layout);
		mp.put(frm_name, frm);
		return frm;

	}

	public static SearchField CreateSearchField(String caption, String fldname,
			List<FieldInfo> lstFlds, String width, final String sqlstr,
			final String codeField, final String nameField) {
		final SearchField txt = new SearchField();

		final Connection con = Channelplus3Application.getInstance()
				.getFrmUserLogin().getDbc().getDbConnection();
		if (!width.isEmpty())
			txt.setWidth(width);
		txt.setShowDisplayWithValue(true);
		txt.setCaption(caption);
		if (lstFlds != null) {
			FieldInfo fld = new FieldInfo(fldname.toUpperCase());
			fld.obj = txt;
			fld.fieldCaption = caption;
			lstFlds.add(fld);
		}

		if (!sqlstr.isEmpty()) {
			txt.setButtonPress(new ButtonPress() {

				public void onButton(String button) {
					if (button.equals("SEARCH")) {
						final Window wnd = new Window();
						final VerticalLayout la = new VerticalLayout();
						wnd.setContent(la);

						try {
							utilsVaadin.showSearch(la, new SelectionListener() {
								public void onSelection(TableView tv) {
									Channelplus3Application.getInstance()
											.getMainWindow().removeWindow(wnd);
									if (tv.getSelectionValue() > -1
											&& !txt.isReadOnly()) {
										try {
											int rn = tv.getSelectionValue();

											txt.setDisplayValue(
													tv.getData()
															.getFieldValue(rn,
																	codeField)
															.toString(),
													tv.getData()
															.getFieldValue(rn,
																	nameField)
															.toString());
											txt.setData(new dataCell(tv
													.getData()
													.getFieldValue(rn,
															nameField)
													.toString(), tv
													.getData()
													.getFieldValue(rn,
															codeField)
													.toString()));
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							}, con, sqlstr, true);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}

				}
			});
		}

		return txt;
	}

	public static QueryExe autoGenerateSql(List<FieldInfo> lstfldinfo,
			Connection con) {
		QueryExe qe = new QueryExe(con);
		for (Iterator iterator = lstfldinfo.iterator(); iterator.hasNext();) {
			FieldInfo fl = (FieldInfo) iterator.next();
			if (!(fl.obj instanceof CheckBox)) {
				qe.setParaValue(fl.fieldName,
						((AbstractField) fl.obj).getValue());
			} else {
				qe.setParaValue(fl.fieldName, (((CheckBox) fl.obj)
						.booleanValue() ? fl.valueOnTrue : fl.valueOnFalse));
			}
		}

		return qe;
	}

	public static QueryExe autoGenerateSql(List<FieldInfo> lstfldinfo,
			Connection con, Parameter... pms) {
		QueryExe qe = autoGenerateSql(lstfldinfo, con);
		for (int i = 0; i < pms.length; i++)
			qe.setParaValue(pms[i].getName(), pms[i].getValue());

		return qe;
	}

}
