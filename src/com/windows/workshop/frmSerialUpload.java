package com.windows.workshop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import com.generic.QueryExe;
import com.generic.ResourceManager;
import com.generic.localTableModel;
import com.generic.transactionalForm;
import com.generic.utils;
import com.main.channelplus3.Channelplus3Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window.Notification;

public class frmSerialUpload implements transactionalForm {
	private Connection con = null;
	private localTableModel data_serials = new localTableModel();
	private AbstractLayout parentLayout = null;
	private VerticalLayout mainLayout = new VerticalLayout();
	private boolean listnerAdded = false;
	private Upload upload = new Upload("Upload XLS file for auto delivery: ",
			new Receiver() {
				public OutputStream receiveUpload(String filename,
						String mimeType) {
					FileOutputStream fos = null; // Output stream to write to
					File file;
					file = new File(
							((WebApplicationContext) Channelplus3Application
									.getInstance().getContext())
									.getHttpSession().getServletContext()
									.getRealPath("/WEB-INF/")
									+ "/tmp/uploads/" + "tmp.xls");
					try {
						// Open the file for writing.
						fos = new FileOutputStream(file);
					} catch (final java.io.FileNotFoundException e) {
						e.printStackTrace();
						return null;
					}
					return fos; // Return the output stream to write to
				}

			});

	public void setParentLayout(AbstractLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void resetFormLayout() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		centralPanel.removeAllComponents();
		mainLayout.removeAllComponents();

	}

	public void createView() {
		final VerticalLayout centralPanel = (VerticalLayout) this.parentLayout;
		resetFormLayout();
		ResourceManager.addComponent(centralPanel, mainLayout);
		ResourceManager.addComponent(mainLayout, upload);
		ResourceManager.addComponent(mainLayout, new Label(
				"Upload excel sheet in follwing format (EXCEL 2003)"));
		ResourceManager.addComponent(mainLayout, new Label("1. Serial Code"));
		ResourceManager.addComponent(mainLayout, new Label("2. Pur Date"));
		ResourceManager.addComponent(mainLayout, new Label("3. Sold date"));
		ResourceManager.addComponent(mainLayout, new Label("4. Item Code"));
		ResourceManager.addComponent(mainLayout, new Label("1. Cust Code"));

		if (!listnerAdded) {
			upload.addListener(new SucceededListener() {

				public void uploadSucceeded(SucceededEvent event) {
					String fn = ((WebApplicationContext) Channelplus3Application
							.getInstance().getContext()).getHttpSession()
							.getServletContext().getRealPath("/WEB-INF/")
							+ "/tmp/uploads/tmp.xls";// + event.getFilename();

					String formname = "STOCK_SERIALS";
					try {
						data_serials.getRows().clear();
						utils.fill_data_from_excel(fn, formname, data_serials,
								con, false);
						save_data();

					} catch (Exception e) {
						e.printStackTrace();
						parentLayout.getWindow()
								.showNotification(e.getMessage(),
										Notification.TYPE_ERROR_MESSAGE);
						data_serials.getRows().clear();
					}

				}
			});

			listnerAdded = true;
		}
	}

	public void init() {
		initForm();
	}

	public void initForm() {
		con = Channelplus3Application.getInstance().getFrmUserLogin().getDbc()
				.getDbConnection();
		if (data_serials.getDbclass() == null) {
			try {
				data_serials.createDBClassFromConnection(con);
			} catch (SQLException e) {
			}
		}

		createView();
		load_data();
	}

	public void load_data() {
		try {
			data_serials.executeQuery("select * "
					+ " from pur_stock_serials where 1=0", true);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void print_data() {

	}

	public void save_data() {
		String sr = "";
		try {
			String sqlStr = "insert into pur_stock_serials(keyfld,serial_code,pur_date,sold_date,item_code,cust_code) values "
					+ "(:KEYFLD,:SERIAL_CODE,:PUR_DATE,:SOLD_DATE,:ITEM_CODE,:CUST_CODE)";
			con.setAutoCommit(false);
			QueryExe qe = new QueryExe(sqlStr, con);
			qe.parse();
			double kfld = Double.valueOf(utils.getSqlValue(
					"select nvl(max(keyfld),0)+1 from pur_stock_serials", con));
			for (int i = 0; i < data_serials.getRows().size(); i++) {
				sr = data_serials.getFieldValue(i, "SERIAL_CODE").toString();
				qe.setParaValue("SERIAL_CODE", sr);
				qe.setParaValue("KEYFLD", kfld);
				qe.setParaValue("PUR_DATE", data_serials.getFieldValue(i,
						"PUR_DATE"));
				qe.setParaValue("SOLD_DATE", data_serials.getFieldValue(i,
						"SOLD_DATE"));
				qe.setParaValue("ITEM_CODE", data_serials.getFieldValue(i,
						"ITEM_CODE"));
				qe.setParaValue("CUST_CODE", data_serials.getFieldValue(i,
						"CUST_CODE"));

				qe.execute(false);
				kfld++;
			}
			qe.close();
			con.commit();
			parentLayout.getWindow().showNotification("Uploaded successfully");

		} catch (SQLException ex) {
			ex.printStackTrace();
			parentLayout.getWindow().showNotification(ex.getMessage(),
					Notification.TYPE_ERROR_MESSAGE);
			if (!sr.isEmpty()) {
				parentLayout.getWindow()
						.showNotification(sr + " serial found !",
								Notification.TYPE_ERROR_MESSAGE);
			}
			try {
				con.rollback();
			} catch (SQLException ex1) {
				ex1.printStackTrace();
			}
		}

	}

	public void showInitView() {
		init();

	}

}
