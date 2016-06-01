package com.windows;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.Scanner;

import org.vaadin.sasha.portallayout.PortalLayout;

import com.generic.ResourceManager;
import com.generic.transactionalForm;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

public class frmOraApp implements transactionalForm {
	private Connection con = null;
	private AbstractLayout parentLayout = null;
	private String QRYSES = "";
	private boolean listnerAdded = false;
	private PortalLayout plLayout = new PortalLayout();

	public frmOraApp() {

	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		plLayout.removeAllComponents();

	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.setSizeFull();
		resetFormLayout();
		plLayout.setSizeFull();
		ResourceManager.addComponent(centralPanel, plLayout);

		if (!listnerAdded) {
			listnerAdded = true;
		}
	}

	protected void show_list() {
	}

	protected void delete_data() {

	}

	public void init() {

	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();

		createView();
		load_data();

	}

	public void load_data() {
		URL url;
		try {
			QRYSES = QRYSES.replace("$$ORACLE_APP_CP_USER$$",
					Channelplus3Application.getInstance().getFrmUserLogin()
							.getTxtUser().getValue().toString());

			QRYSES = QRYSES.replace("$$ORACLE_APP_CP_PASSWORD$$",
					Channelplus3Application.getInstance().getFrmUserLogin()
							.getTxtPwd().getValue().toString());

			String q = QRYSES;
			int fnd = QRYSES.indexOf("$$");
			while (fnd > -1 && !q.trim().isEmpty()) {
				String tk = q.substring(q.indexOf("$$") + 2);
				tk = tk.substring(0, tk.indexOf("$$"));
				q = q.substring(q.indexOf(tk) + (tk.length() + 2));
				QRYSES = QRYSES.replace("$$" + tk + "$$",
						Channelplus3Application.getInstance().getFrmUserLogin()
								.getMapVars().get(tk));

				fnd = q.indexOf("$$");

			}

			url = new URL(QRYSES);
			Embedded e = new Embedded(null, new ExternalResource(url));
			e.setType(Embedded.TYPE_BROWSER);
			plLayout.addComponent(e);
			e.setSizeFull();
			plLayout.setClosable(e, false);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void print_data() {

	}

	public void validate_data() throws Exception {
	}

	public void save_data() {
		try {
			validate_data();
		} catch (Exception ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void showInitView() {
		initForm();
	}

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void setQRYSES(String qryses) {
		this.QRYSES = qryses;
	}
}
