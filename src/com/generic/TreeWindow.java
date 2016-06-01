package com.generic;

import java.math.BigDecimal;
import java.sql.Connection;

import com.main.channelplus3.Channelplus3Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TreeWindow extends Window implements ValueChangeListener {
	private VerticalLayout treeLayout = new VerticalLayout();
	private VerticalLayout treeCompLayout = new VerticalLayout();
	private HorizontalLayout treeFindlayout = new HorizontalLayout();
	private TextField txtFindTree = new TextField("");
	private Button cmdFindTree = ControlsFactory.CreateToolbarButton(
			"find.png", "find");
	public Panel pnl = new Panel();
	public Tree tree = new Tree();
	public localTableModel data = null;
	public Connection con = null;

	public String codeParentCol = "";
	public String codeCol = "";
	public String codeColTitle = "";
	public String codeChildCount = "";
	public String codeParentTitleCol = "";
	public String windowTitle = "";

	public String varHeight = "90%";
	public String varWidth = "240px";
	public ValueChangeListener codeChangeValue = null;

	public boolean running_thread_tree = false;
	public ProgressIndicator prog = new ProgressIndicator();

	public static TreeWindow createInstance(localTableModel lctb,
			String codeCol, String codeParentCol, String codeColTitle,
			String childcountfld, String windowTitle) {
		TreeWindow tw = new TreeWindow();
		tw.codeCol = codeCol;
		tw.codeColTitle = codeColTitle;
		tw.codeParentCol = codeParentCol;
		tw.codeChildCount = childcountfld;
		tw.data = lctb;
		tw.windowTitle = windowTitle;
		tw.initView();
		return tw;
	}

	public TreeWindow() {
		super();
		setName("treewnd_" + System.currentTimeMillis());
		initView();
	}

	public void reset() {
		if (running_thread_tree) {
			return;
		}
		removeAllComponents();
		pnl.removeAllComponents();
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

	public void createView() {
		reset();
		pnl.setSizeFull();
		tree.setSizeFull();
		treeLayout.setSizeFull();
		treeCompLayout.setSizeFull();
		treeFindlayout.setSizeFull();
		setClosable(false);
		setBorder(Window.BORDER_NONE);
		pnl.addStyleName("menucontainer");
		pnl.addStyleName("light");
		pnl.getContent().setWidth("-1px");
		addStyleName("light");
		// setCloseShortcut(KeyCode.ESCAPE, null);

		ResourceManager.addComponent(treeLayout, treeFindlayout);
		ResourceManager.addComponent(treeLayout, treeCompLayout);
		ResourceManager.addComponent(treeCompLayout, pnl);
		ResourceManager.addComponent(pnl.getContent(), tree);
		ResourceManager.addComponent(treeFindlayout, txtFindTree);
		ResourceManager.addComponent(treeFindlayout, cmdFindTree);
		ResourceManager.addComponent(treeLayout, prog);

		treeFindlayout.setExpandRatio(txtFindTree, 3f);
		treeFindlayout.setExpandRatio(cmdFindTree, 1f);
		treeLayout.setExpandRatio(treeFindlayout, .25f);
		treeLayout.setExpandRatio(treeCompLayout, 3.65f);
		treeLayout.setExpandRatio(prog, .1f);

		setWidth(varWidth);
		setHeight(varHeight);
		setContent(treeLayout);

		if (!Channelplus3Application.getInstance().getMainWindow()
				.getChildWindows().contains(this)) {
			Channelplus3Application.getInstance().getMainWindow()
					.addWindow(this);
		}
		int lx = 0;
		if (!ResourceManager.isRTL()) {
			lx = (int) (Channelplus3Application.getInstance().getMainWindow()
					.getWidth() - 250);
		}
		setPositionX(lx);
		setPositionY(80);
		prog.setPollingInterval(1000);
		prog.setValue(0);
	}

	public void load_data() {
		if (data == null) {
			return;
		}
		FillTree ft = new FillTree();
		ft.start();
	}

	public void valueChange(ValueChangeEvent event) {

		if (codeChangeValue != null) {
			codeChangeValue.valueChange(event);
		}
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
					ndx = new menuItem(current_code, current_title);
					tree.addItem(ndx);
					if (root_node != null) {
						tree.setParent(ndx, root_node);
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
					TreeWindow.this.requestRepaintAll();
					tree.removeListener(TreeWindow.this);
					tree.addListener(TreeWindow.this);
					TreeWindow.this.setCaption(tree.size() + " "
							+ TreeWindow.this.windowTitle + "(s) founds");
				}// synchronized
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			prog.setValue(1);
			running_thread_tree = false;
		}
	}

}
