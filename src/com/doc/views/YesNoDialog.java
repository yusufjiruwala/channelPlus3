package com.doc.views;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class YesNoDialog extends Window implements Button.ClickListener {

	Callback callback;
	Button yes = new Button("Yes", this);
	Button no = new Button("No", this);
	public String wnd_width = "50%";
	public String wnd_height = "-1px";

	public YesNoDialog(String caption, String question, Callback callback,
			String width, String height) {
		super(caption);
		this.wnd_height = height;
		this.wnd_width = width;
		showWindow(caption, question, callback);
	}

	public YesNoDialog(String caption, String question, Callback callback) {
		super(caption);
		showWindow(caption, question, callback);
	}

	public void showWindow(String caption, String question, Callback callback) {

		setModal(true);
		((VerticalLayout) getContent()).setSpacing(true);
		this.callback = callback;

		if (question != null) {
			Label lbl = new Label(question);
			lbl.setSizeFull();
			addComponent(lbl);
		}
		setWidth(wnd_width);
		setHeight(wnd_height);
		HorizontalLayout hl = new HorizontalLayout();
		// hl.setWidth("100%");
		hl.addComponent(yes);
		hl.addComponent(no);
		hl.setSpacing(true);
		addComponent(hl);
	}

	public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {

		if (getParent() != null) {
			((Window) getParent()).removeWindow(this);
		}
		callback.onDialogResult(event.getSource() == yes);

	}

	public interface Callback {

		public void onDialogResult(boolean resultIsYes);
	}

}