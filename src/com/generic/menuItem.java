package com.generic;

import java.io.Serializable;

public class menuItem implements Serializable {
	private String Id = "";
	private String Display = "";
	private String iconfile = "";
	private Object para1Val = null;
	private String parentID = "";
	private menuItem parentNode = null;

	public String getIconfile() {
		return iconfile;
	}

	public void setIconfile(String iconfile) {
		this.iconfile = iconfile;
	}

	public menuItem getParentNode() {
		return parentNode;
	}

	public void setParentNode(menuItem parentNode) {
		this.parentNode = parentNode;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public Object getPara1Val() {
		return para1Val;
	}

	public void setPara1Val(Object para1Val) {
		this.para1Val = para1Val;
	}

	public Object getPara2Val() {
		return para2Val;
	}

	public void setPara2Val(Object para2Val) {
		this.para2Val = para2Val;
	}

	public Object getPara3Val() {
		return para3Val;
	}

	public void setPara3Val(Object para3Val) {
		this.para3Val = para3Val;
	}

	public Object getPara4Val() {
		return para4Val;
	}

	public void setPara4Val(Object para4Val) {
		this.para4Val = para4Val;
	}

	private Object para2Val = null;
	private Object para3Val = null;
	private Object para4Val = null;

	public String getId() {
		return Id;
	}

	public String getDisplay() {
		return Display;
	}

	public void setId(String id) {
		Id = id;
	}

	public void setDisplay(String display) {
		Display = display;
	}

	public menuItem(String id, String disp) {
		this.Id = id;
		this.Display = disp;
	}

	@Override
	public String toString() {

		return Display;
	}

}
