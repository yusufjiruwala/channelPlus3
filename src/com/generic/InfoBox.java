package com.generic;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import org.vaadin.sasha.portallayout.PortalLayout;

import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

public class InfoBox extends Panel implements ValueChangeListener {
	private VerticalLayout treeLayout = new VerticalLayout();
	private VerticalLayout treeCompLayout = new VerticalLayout();
	private HorizontalLayout treeFindlayout = new HorizontalLayout();
	private TextField txtFindTree = new TextField("");
	private Button cmdFindTree = ControlsFactory.CreateToolbarButton(
			"img/find.png", "find");
	public Panel pnl = new Panel();
	public Tree tree = new Tree();
	public localTableModel data = null;
	public Connection con = null;
	private FillTree ft = new FillTree();

	public String codeParentCol = "";
	public String codeCol = "";
	public String codeColTitle = "";
	public String codeChildCount = "";
	public String codePara2 = "";
	public String codePara3 = "";
	public String codeParentTitleCol = "";
	public String windowTitle = "";

	public String varHeight = "90%";
	public String varWidth = "240px";
	public ValueChangeListener codeChangeValue = null;
	public Timer tm = new Timer();
	public boolean running_thread_tree = false;
	public ProgressIndicator prog = new ProgressIndicator();
	public PortalLayout portalLayout = new PortalLayout();
	public HorizontalLayout hzLayout = new HorizontalLayout();

	public static InfoBox createInstance(localTableModel lctb, String codeCol,
			String codeParentCol, String codeColTitle, String childcountfld,
			String windowTitle, String param2Field, String param3Field) {
		InfoBox tw = new InfoBox();
		tw.codeCol = codeCol;
		tw.codeColTitle = codeColTitle;
		tw.codeParentCol = codeParentCol;
		tw.codeChildCount = childcountfld;
		tw.codePara2 = param2Field;
		tw.codePara3 = param3Field;
		tw.data = lctb;
		tw.windowTitle = windowTitle;
		tw.initView();
		return tw;
	}

	public InfoBox() {
		super();
		initView();
	}

	public void reset() {
		if (running_thread_tree) {
			return;
		}
		removeAllComponents();
		pnl.removeAllComponents();
		portalLayout.removeAllComponents();
		treeLayout.removeAllComponents();
		treeCompLayout.removeAllComponents();
		treeFindlayout.removeAllComponents();
		tree.removeAllItems();
	}

	public void initView() {

		if (data != null) {
			con = data.getDbclass().getDbConnection();
		}
		createView();
		load_data();

	}

	public void refreshTree(boolean dataMode) throws SQLException {
		data.executeQuery(data.getSqlString(), true);
		initView();
	}

	public void createView() {
		reset();
		hzLayout.setMargin(true);

		pnl.setSizeFull();
		tree.setSizeFull();
		treeLayout.setSizeFull();
		treeCompLayout.setSizeFull();
		treeFindlayout.setSizeFull();
		hzLayout.setSizeFull();
		portalLayout.setSizeFull();
		treeLayout.setCaption(windowTitle);
		pnl.getContent().setWidth("-1px");
		tree.setImmediate(true);

		// addStyleName("borderpanel");
		pnl.addStyleName("light");

		// setCloseShortcut(KeyCode.ESCAPE, null);

		ResourceManager.addComponent(hzLayout, portalLayout);
		portalLayout.removeAllComponents();
		ResourceManager.addComponent(portalLayout, treeLayout);
		ResourceManager.addComponent(treeLayout, treeFindlayout);
		ResourceManager.addComponent(treeLayout, treeCompLayout);
		ResourceManager.addComponent(treeCompLayout, pnl);
		ResourceManager.addComponent(pnl.getContent(), tree);
		ResourceManager.addComponent(treeFindlayout, txtFindTree);
		ResourceManager.addComponent(treeFindlayout, cmdFindTree);
		ResourceManager.addComponent(treeLayout, prog);

		treeFindlayout.setExpandRatio(txtFindTree, 3f);
		treeFindlayout.setExpandRatio(cmdFindTree, 1f);
		treeLayout.setExpandRatio(treeFindlayout, .35f);
		treeLayout.setExpandRatio(treeCompLayout, 3.55f);
		treeLayout.setExpandRatio(prog, .1f);

		treeFindlayout.setComponentAlignment(cmdFindTree,
				Alignment.BOTTOM_CENTER);
		setWidth(varWidth);
		setHeight(varHeight);
		setContent(hzLayout);
		portalLayout.setClosable(treeLayout, false);
		portalLayout.setCollapsible(treeLayout, true);

		int lx = 0;
		if (!ResourceManager.isRTL()) {
			lx = (int) (Channelplus3Application.getInstance().getMainWindow()
					.getWidth() - 250);
		}

		prog.setPollingInterval(1000);
		prog.setValue(0);
	}

	public void load_data() {
		if (data == null) {
			return;
		}

		if (running_thread_tree) {
			ft.interrupt();
		}
		System.gc();

		tm.schedule(new TimerTask() {

			@Override
			public void run() {
				execute_timer();
			}
		}, 100);
		// ft = new FillTree();
		// ft.start();
	}

	public void valueChange(ValueChangeEvent event) {

		if (codeChangeValue != null) {
			codeChangeValue.valueChange(event);
		}
	}

	public void updateTree(menuItem mn) {
		menuItem om = utilsVaadin.findNodeByValue(tree, mn.getId(), null);
		menuItem p = null;
		if (!mn.getParentID().isEmpty()) {
			p = utilsVaadin.findNodeByValue(tree, mn.getParentID(), null);
		}

		if (om == null) {
			om = utilsVaadin.findNodeByValue(tree, mn.getParentID(), null);
			if (om != null) {
				tree.addItem(mn);
				if (p != null)
					tree.setParent(mn, p);
			}
		} else {
			if (!om.getParentID().equals(mn.getParentID())) {
				tree.setChildrenAllowed(mn.getParentNode(), true);
				tree.setParent(om, p);
			}
		}

	}

	public void locateTree(String id) {
		if (running_thread_tree) {
			return;
		}
		if (id == null || id.isEmpty()) {
			tree.select(null);
			return;
		}
		menuItem m = utilsVaadin.findNodeByValue(tree, id, null);
		if (m == null) {
			tree.select(null);
			utilsVaadin.treeCollpae(tree);
		} else {
			tree.select(m);
			while (m != null) {
				m = m.getParentNode();
				tree.expandItem(m);
			}
		}
	}

	public void execute_timer() {

		if (running_thread_tree) {
			return;
		}
		running_thread_tree = true;
		try {
			prog.setValue(0);
			String lastParent = "";
			menuItem root_node = null, lastparentnode = null, ndx = null;
			tree.removeAllItems();
			for (int i = 0; i < data.getRows().size(); i++) {
				String current_parent = "";
				String current_code = utils.nvl(data.getFieldValue(i, codeCol),
						"");
				String current_title = utils.nvl(data.getFieldValue(i,
						codeColTitle), "");
				current_parent = utils.nvl(
						data.getFieldValue(i, codeParentCol), "");
				BigDecimal current_childcount = (BigDecimal) data
						.getFieldValue(i, codeChildCount);
				if (current_childcount == null) {
					break;
				}

				if (current_parent.length() != 0) {
					if (lastParent.equals(current_parent)) {
						root_node = lastparentnode;
					} else {
						root_node = utilsVaadin.findNodeByValue(tree,
								current_parent, null);
						lastParent = current_parent;
						lastparentnode = root_node;
					}
				} else {
					root_node = null;
				}
				ndx = new menuItem(current_code, current_title + "-"
						+ current_code);
				ndx.setPara1Val(current_childcount);
				if (!codePara2.isEmpty())
					ndx.setPara2Val(data.getFieldValue(i, codePara2));
				if (!codePara3.isEmpty())
					ndx.setPara3Val(data.getFieldValue(i, codePara3));

				if (tree == null || ndx == null)
					break;

				tree.addItem(ndx);
				if (root_node != null && ndx != null) {
					tree.setChildrenAllowed(root_node, true);
					tree.setParent(ndx, root_node);
					ndx.setParentID(root_node.getId());
					ndx.setParentNode(root_node);
				}
				if (current_childcount.intValue() == 0) {
					tree.setChildrenAllowed(ndx, false);
				} else {
					tree.setChildrenAllowed(ndx, true);
				}
				tree.collapseItem(root_node);
			}

			running_thread_tree = false;
			synchronized (tree) {
				InfoBox.this.requestRepaintAll();
				tree.removeListener(InfoBox.this);
				tree.addListener(InfoBox.this);
			}// synchronized
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		prog.setValue(1);
		running_thread_tree = false;

	}

	public class FillTree extends Thread {

		public void run() {
			if (running_thread_tree) {
				return;
			}
			running_thread_tree = true;
			try {
				prog.setValue(0);
				String lastParent = "";
				menuItem root_node = null, lastparentnode = null, ndx = null;
				tree.removeAllItems();
				for (int i = 0; i < data.getRows().size(); i++) {
					String current_parent = "";
					String current_code = utils.nvl(data.getFieldValue(i,
							codeCol), "");
					String current_title = utils.nvl(data.getFieldValue(i,
							codeColTitle), "");
					current_parent = utils.nvl(data.getFieldValue(i,
							codeParentCol), "");
					BigDecimal current_childcount = (BigDecimal) data
							.getFieldValue(i, codeChildCount);
					if (current_childcount == null) {
						break;
					}

					if (current_parent.length() != 0) {
						if (lastParent.equals(current_parent)) {
							root_node = lastparentnode;
						} else {
							root_node = utilsVaadin.findNodeByValue(tree,
									current_parent, null);
							lastParent = current_parent;
							lastparentnode = root_node;
						}
					} else {
						root_node = null;
					}
					ndx = new menuItem(current_code, current_title + "-"
							+ current_code);
					ndx.setPara1Val(current_childcount);
					if (!codePara2.isEmpty())
						ndx.setPara2Val(data.getFieldValue(i, codePara2));
					if (!codePara3.isEmpty())
						ndx.setPara3Val(data.getFieldValue(i, codePara3));
					tree.addItem(ndx);
					if (root_node != null && ndx != null) {
						tree.setParent(ndx, root_node);
						ndx.setParentID(root_node.getId());
						ndx.setParentNode(root_node);
					}
					if (current_childcount.intValue() == 0) {
						tree.setChildrenAllowed(ndx, false);
					} else {
						tree.setChildrenAllowed(ndx, true);
					}
					tree.collapseItem(root_node);
				}

				running_thread_tree = false;
				synchronized (tree) {
					InfoBox.this.requestRepaintAll();
					tree.removeListener(InfoBox.this);
					tree.addListener(InfoBox.this);
				}// synchronized
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			prog.setValue(1);
			running_thread_tree = false;
		}
	}

}
