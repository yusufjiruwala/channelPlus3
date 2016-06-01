package com.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class FormLayoutManager extends VerticalLayout {
	public List<HorizontalLayout> listrows = new ArrayList<HorizontalLayout>();
	public List<FieldInfo> listfields = null;

	public FormLayoutManager() {
		super();
	}

	public FormLayoutManager(String width) {
		super();
		setWidth(width);
	}

	public FormLayoutManager(String width, String height) {
		super();
		setWidth(width);
		setHeight(height);
	}

	public void addComponentsRow(Object... obj) {
		int sz = listrows.size() + 1;
		setComponentsRow(sz, obj);
	}

	public void addLine() {
		HorizontalLayout l = new HorizontalLayout();
		l.addStyleName("top_line");
		l.setSpacing(true);
		l.setWidth("100%");
		listrows.add(l);
		ResourceManager.addComponent(this, l);

	}

	public static void setObjProp(AbstractComponent c, String prop) {
		Map<String, String> mp = new HashMap<String, String>();
		utils.readVarsFromString(mp, prop, ",");

		Alignment al_right = Alignment.MIDDLE_RIGHT;
		Alignment al_left = Alignment.MIDDLE_LEFT;
		Alignment al_center = Alignment.MIDDLE_CENTER;
		// if label then move to bottom of text field

		if (!(c instanceof Label)) {
			al_right = Alignment.BOTTOM_RIGHT;
			al_left = Alignment.BOTTOM_LEFT;
			al_center = Alignment.BOTTOM_CENTER;
		}

		if (ResourceManager.isRTL())
			((AbstractOrderedLayout) c.getParent()).setComponentAlignment(c,
					al_right);
		else
			((AbstractOrderedLayout) c.getParent()).setComponentAlignment(c,
					al_left);

		for (Iterator iterator = mp.keySet().iterator(); iterator.hasNext();) {
			String var = ((String) iterator.next()).trim();
			if (var.equals("width"))
				c.setWidth(mp.get(var));

			if (var.equals("height"))
				c.setHeight(mp.get(var));

			if (var.equals("caption"))
				c.setCaption(mp.get(var));

			if (var.equals("style"))
				c.addStyleName(mp.get(var));

			if (var.equals("align")) {
				if (mp.get(var).toUpperCase().equals("RIGHT"))
					((AbstractOrderedLayout) c.getParent())
							.setComponentAlignment(c, al_right);
				if (mp.get(var).toUpperCase().equals("END")
						&& !ResourceManager.isRTL())
					((AbstractOrderedLayout) c.getParent())
							.setComponentAlignment(c, al_right);

				if (mp.get(var).toUpperCase().equals("START")
						&& !ResourceManager.isRTL())
					((AbstractOrderedLayout) c.getParent())
							.setComponentAlignment(c, al_left);

				if (mp.get(var).toUpperCase().equals("END")
						&& ResourceManager.isRTL())
					((AbstractOrderedLayout) c.getParent())
							.setComponentAlignment(c, al_left);
				if (mp.get(var).toUpperCase().equals("START")
						&& ResourceManager.isRTL())
					((AbstractOrderedLayout) c.getParent())
							.setComponentAlignment(c, al_right);

				if (mp.get(var).toUpperCase().equals("CENTER"))
					((AbstractOrderedLayout) c.getParent())
							.setComponentAlignment(c, al_center);
			}

			if (var.equals("align_text")) {
				if (mp.get(var).toUpperCase().equals("RIGHT"))
					c.addStyleName("text_right");
				if (mp.get(var).toUpperCase().equals("END")
						&& !ResourceManager.isRTL())
					c.addStyleName("text_right");

				if (mp.get(var).toUpperCase().equals("START")
						&& !ResourceManager.isRTL())
					c.addStyleName("text_left");

				if (mp.get(var).toUpperCase().equals("END")
						&& ResourceManager.isRTL())
					c.addStyleName("text_left");
				if (mp.get(var).toUpperCase().equals("START")
						&& ResourceManager.isRTL())
					c.addStyleName("text_right");

				if (mp.get(var).toUpperCase().equals("CENTER"))
					c.addStyleName("text_center");
			}

			if (var.equals("value"))
				((AbstractField) c).setValue(mp.get(var));

			if (var.equals("expand"))
				((AbstractOrderedLayout) c.getParent()).setExpandRatio(c,
						Float.valueOf(mp.get(var)));

			if (var.equals("immediate"))
				if (mp.get(var).toUpperCase().startsWith("T"))
					c.setImmediate(true);
				else
					c.setImmediate(false);

			if (var.equals("read") || var.equals("readonly"))
				if (mp.get(var).toUpperCase().startsWith("T"))
					c.setReadOnly(true);
				else
					c.setReadOnly(false);

			if (var.equals("enable"))
				if (mp.get(var).toUpperCase().startsWith("T"))
					c.setEnabled(true);
				else
					c.setEnabled(false);

		}
	}

	public void setComponentsRow(int rowno, Object... obj) {
		int sz = listrows.size();
		if (rowno > sz)
			for (int i = sz; i < rowno; i++) {
				HorizontalLayout l = new HorizontalLayout();
				l.setSpacing(true);
				l.setWidth("100%");
				listrows.add(l);
				ResourceManager.addComponent(this, l);
			}
		HorizontalLayout hz = listrows.get(rowno - 1);
		AbstractComponent prev = null;
		for (int i = 0; i < obj.length; i++) {
			AbstractComponent c = null;
			if (obj[i] instanceof AbstractComponent) {
				c = (AbstractComponent) obj[i];
				c.setWidth("100%");
				prev = c;
				ResourceManager.addComponent(hz, c);
				if ((c instanceof Label)
						&& c.getParent() instanceof AbstractOrderedLayout)
					((AbstractOrderedLayout) c.getParent())
							.setComponentAlignment(c, Alignment.BOTTOM_LEFT);

			}
			if (obj[i] instanceof String && prev != null) {
				setObjProp(prev, (String) obj[i]);
				prev = null;
				continue;
			}
			if (obj[i] instanceof String && prev == null) {
				c = new Label();
				c.setWidth("100%");
				ResourceManager.addComponent(hz, c);
				hz.setComponentAlignment(c, Alignment.BOTTOM_LEFT);
				setObjProp(c, (String) obj[i]);
				prev = null;
				continue;

			}

		}
	}

	public void removeAll() {
		for (Iterator iterator = listrows.iterator(); iterator.hasNext();) {
			HorizontalLayout hz = (HorizontalLayout) iterator.next();
			hz.removeAllComponents();
		}
		listrows.clear();
		removeAllComponents();
	}
}
