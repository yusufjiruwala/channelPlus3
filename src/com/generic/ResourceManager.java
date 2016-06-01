package com.generic;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ResourceManager {
	private static boolean RTL = false;
	private static Map<String, String> bundle = new HashMap<String, String>();

	public static boolean isRTL() {
		return RTL;
	}

	public static void setRTL(boolean rTL) {
		RTL = rTL;
	}

	public static void addComponent(ComponentContainer layout,
			com.vaadin.ui.Component component) {
		boolean added = false;
		if (RTL) {
			if (layout instanceof HorizontalLayout) {
				((HorizontalLayout) layout).addComponentAsFirst(component);
				((HorizontalLayout) layout).setComponentAlignment(component,
						Alignment.TOP_RIGHT);
				((HorizontalLayout) layout).addStyleName("rtl");
				added = true;
			}
			if (layout instanceof VerticalLayout) {
				((VerticalLayout) layout).addStyleName("rtl");
				((VerticalLayout) layout).addComponent(component);
				((VerticalLayout) layout).setComponentAlignment(component,
						Alignment.TOP_RIGHT);
				added = true;
			}
			if (layout instanceof GridLayout) {
				((GridLayout) layout).addStyleName("rtl");
				((GridLayout) layout).addComponent(component);
				added = true;
			}
			/*
			 * if (layout instanceof Panel) { ((Panel)
			 * layout).addStyleName("rtl"); ((Panel)
			 * layout).addComponent(component); added = true; }
			 */
			if (component instanceof Label) {
				component.addStyleName("rtl");
			}
			if (component instanceof TextField) {
				component.addStyleName("rtl");
			}

		} else {
			layout.addComponent(component);
		}
	}
}