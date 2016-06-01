package com.main.channelplus3;

import java.util.Map;

import com.generic.utilsVaadin;
import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.windows.UserLogin;

public class Channelplus3Application extends Application implements
		ApplicationContext.TransactionListener {
	private static final ThreadLocal<Channelplus3Application> currentChnApp = new ThreadLocal<Channelplus3Application>();
	public Window loginWindow = null;
	private UserLogin frmUserLogin = null;
	private Window financialWnd = new Window();
	private Window purWnd = new Window();
	private Window warehouseWnd = new Window();

	public Window getFinancialWnd() {
		return financialWnd;
	}

	public Window getPurWnd() {
		return purWnd;
	}

	public Window getWareHouseWnd() {
		return warehouseWnd;

	}

	public void setFinancialWnd(Window financialWnd) {
		this.financialWnd = financialWnd;
	}

	public UserLogin getFrmUserLogin() {
		return frmUserLogin;
	}

	@Override
	public void init() {
		getContext().addTransactionListener(this);
		financialWnd.setName("FinancialWnd");
		purWnd.setName("purchaseWnd");
		warehouseWnd.setName("warehouseWnd");
		setTheme("theme1");
		frmUserLogin = new UserLogin();
		loginWindow = new Window("CHAINEL");
		loginWindow.setContent(new VerticalLayout());
		loginWindow.getContent().addComponent(frmUserLogin);
		setMainWindow(loginWindow);
		frmUserLogin.initComponents();
		getMainWindow().getContent().setSizeFull();
		getMainWindow().addParameterHandler(new ParameterHandler() {

			public void handleParameters(Map<String, String[]> parameters) {
				if (parameters.containsKey("user")) {
					frmUserLogin.getTxtUser().setValue(
							parameters.get("user")[0]);
				}
				if (parameters.containsKey("password")) {
					frmUserLogin.getTxtPwd().setValue( 
							parameters.get("password")[0]);
					if (frmUserLogin.login()) {
						if (parameters.containsKey("module")
								&& parameters.get("module")[0].length() > 0) {
							utilsVaadin.switchProfile(
									Channelplus3Application.getInstance()
											.getFrmUserLogin().listLayout,
									parameters.get("module")[0]);
						}
					}
					
					if (!parameters.containsKey("module")) {
						frmUserLogin.show_module_selection();
					}

				}

			}
		});

	}

	public static Channelplus3Application getInstance() {
		return currentChnApp.get();
	}

	public void transactionEnd(Application application, Object transactionData) {
		if (application == Channelplus3Application.this) {
			currentChnApp.set(null);
			currentChnApp.remove();
		}
	}

	public void transactionStart(Application application, Object transactionData) {
		if (application == Channelplus3Application.this) {
			currentChnApp.set(this);
		}

	}

	public static SystemMessages getSystemMessages() {
		Application.CustomizedSystemMessages messages = new Application.CustomizedSystemMessages();
		messages.setAuthenticationErrorNotificationEnabled(false);
		messages.setCommunicationErrorNotificationEnabled(true);
		messages.setCommunicationErrorURL(null);
		messages.setCommunicationErrorMessage(null);
		messages.setCommunicationErrorCaption(null);
		messages.setInternalErrorNotificationEnabled(false);
		messages.setOutOfSyncNotificationEnabled(false);
		messages.setSessionExpiredNotificationEnabled(false);

		return messages;
	}

}
