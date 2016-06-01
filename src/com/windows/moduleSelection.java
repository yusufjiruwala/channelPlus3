package com.windows;

import java.sql.Connection;

import com.generic.localTableModel;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class moduleSelection extends VerticalLayout {
	private GridLayout selectionGrid = new GridLayout(3, 2);
	private ClickListener onselection = null;
	private Connection con = null;
	private localTableModel data = new localTableModel();

	public void setOnseleciton(ClickListener v) {
		this.onselection = v;
	}

	public ClickListener getOnselection() {
		return onselection;
	}

	public moduleSelection() {
	}

	public void createView() {
				
		removeAllComponents();
		setSizeFull();

		addComponent(selectionGrid);
		selectionGrid.removeAllComponents();
		selectionGrid.setSizeFull();

		String fl = ((WebApplicationContext) Channelplus3Application
				.getInstance().getContext()).getHttpSession()
				.getServletContext().getRealPath("/WEB-INF")
				+ "/images/financial.png";
		Embedded em = new Embedded("Financial", new ThemeResource(
				"../theme1/img/financial.jpg"));
		em.setType(Embedded.TYPE_IMAGE);

		em.addListener(new ClickListener() {
			public void click(ClickEvent event) {
				((Window) getWindow().getParent()).removeWindow(getWindow());

				if (onselection != null) {
					onselection.click(event);
				} else {
					Channelplus3Application.getInstance().setMainWindow(
							Channelplus3Application.getInstance()
									.getFinancialWnd());
					Channelplus3Application.getInstance().loginWindow
							.open(new ExternalResource(Channelplus3Application
									.getInstance().getFinancialWnd().getURL()));
					Channelplus3Application.getInstance().getFinancialWnd()
							.addComponent(
									new frmMain(Channelplus3Application
											.getInstance().getFinancialWnd()));

				}
			}
		});
		selectionGrid.addComponent(em);

		// purchase window
		em = new Embedded("Purchase", new ThemeResource(
				"../theme1/img/purchase.JPG"));
		em.setType(Embedded.TYPE_IMAGE);

		em.addListener(new ClickListener() {
			public void click(ClickEvent event) {
				((Window) getWindow().getParent()).removeWindow(getWindow());

				if (onselection != null) {
					onselection.click(event);
				} else {
					Channelplus3Application.getInstance().setMainWindow(
							Channelplus3Application.getInstance().getPurWnd());
					Channelplus3Application.getInstance().loginWindow
							.open(new ExternalResource(Channelplus3Application
									.getInstance().getPurWnd().getURL()));
					Channelplus3Application.getInstance().getPurWnd()
							.setContent(
									new frmMainMenus(Channelplus3Application
											.getInstance().getPurWnd()));

				}
			}
		});

		selectionGrid.addComponent(em);

		em = new Embedded("Warehouse Management", new ThemeResource(
				"../theme1/img/warehouse.jpg"));
		em.setType(Embedded.TYPE_IMAGE);
		em.addListener(new ClickListener() {
			public void click(ClickEvent event) {
				((Window) getWindow().getParent()).removeWindow(getWindow());

				if (onselection != null) {
					onselection.click(event);
				} else {
					Channelplus3Application.getInstance().setMainWindow(
							Channelplus3Application.getInstance()
									.getWareHouseWnd());
					Channelplus3Application.getInstance().loginWindow
							.open(new ExternalResource(Channelplus3Application
									.getInstance().getWareHouseWnd().getURL()));
					Channelplus3Application.getInstance().getWareHouseWnd()
							.addComponent(
									new frmMainWareHousing(
											Channelplus3Application
													.getInstance()
													.getWareHouseWnd()));

				}
			}
		});

		selectionGrid.addComponent(em);

		em.requestRepaint();

	}
}
