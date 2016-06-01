package com.generic;

import java.io.Serializable;

import com.vaadin.ui.AbstractLayout;

public interface transactionalForm extends Serializable {
	public void save_data();

	public void load_data();

	public void print_data();

	public void init();

	public void initForm();

	public void createView();

	public void showInitView();

	public void setParentLayout(AbstractLayout parentLayout);

}
