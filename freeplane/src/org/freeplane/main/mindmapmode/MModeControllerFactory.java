/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Dimitry Polivaev
 *
 *  This file author is Dimitry Polivaev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.main.mindmapmode;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.freeplane.core.controller.Controller;
import org.freeplane.core.frame.ToggleToolbarAction;
import org.freeplane.core.frame.ViewController;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.DelayedMouseListener;
import org.freeplane.core.ui.IEditHandler;
import org.freeplane.core.ui.MenuBuilder;
import org.freeplane.core.ui.SetAcceleratorOnNextClickAction;
import org.freeplane.core.ui.components.FButtonBar;
import org.freeplane.core.ui.components.FreeplaneToolBar;
import org.freeplane.core.ui.components.JResizer;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.ui.components.JResizer.Direction;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.common.attribute.AttributeController;
import org.freeplane.features.common.clipboard.ClipboardController;
import org.freeplane.features.common.cloud.CloudController;
import org.freeplane.features.common.edge.EdgeController;
import org.freeplane.features.common.filter.FilterController;
import org.freeplane.features.common.icon.HierarchicalIcons;
import org.freeplane.features.common.icon.IconController;
import org.freeplane.features.common.link.LinkController;
import org.freeplane.features.common.map.MapController;
import org.freeplane.features.common.map.UnfoldAll;
import org.freeplane.features.common.nodelocation.LocationController;
import org.freeplane.features.common.nodestyle.NodeStyleController;
import org.freeplane.features.common.note.NoteController;
import org.freeplane.features.common.styles.LogicalStyleController;
import org.freeplane.features.common.styles.MapStyle;
import org.freeplane.features.common.text.TextController;
import org.freeplane.features.common.time.CreationModificationPlugin;
import org.freeplane.features.common.url.UrlManager;
import org.freeplane.features.mindmapmode.LoadAcceleratorPresetsAction;
import org.freeplane.features.mindmapmode.MModeController;
import org.freeplane.features.mindmapmode.attribute.MAttributeController;
import org.freeplane.features.mindmapmode.clipboard.MClipboardController;
import org.freeplane.features.mindmapmode.cloud.MCloudController;
import org.freeplane.features.mindmapmode.edge.AutomaticEdgeColorHook;
import org.freeplane.features.mindmapmode.edge.MEdgeController;
import org.freeplane.features.mindmapmode.encrypt.MEncryptionController;
import org.freeplane.features.mindmapmode.export.ExportController;
import org.freeplane.features.mindmapmode.export.ImportMindmanagerFiles;
import org.freeplane.features.mindmapmode.file.MFileManager;
import org.freeplane.features.mindmapmode.file.SaveAll;
import org.freeplane.features.mindmapmode.icon.IconSelectionPlugin;
import org.freeplane.features.mindmapmode.icon.MIconController;
import org.freeplane.features.mindmapmode.link.MLinkController;
import org.freeplane.features.mindmapmode.map.ChangeNodeLevelController;
import org.freeplane.features.mindmapmode.map.MMapController;
import org.freeplane.features.mindmapmode.map.NewParentNode;
import org.freeplane.features.mindmapmode.map.RevisionPlugin;
import org.freeplane.features.mindmapmode.nodelocation.MLocationController;
import org.freeplane.features.mindmapmode.nodestyle.MNodeStyleController;
import org.freeplane.features.mindmapmode.note.MNoteController;
import org.freeplane.features.mindmapmode.ortho.SpellCheckerController;
import org.freeplane.features.mindmapmode.styles.AutomaticLayout;
import org.freeplane.features.mindmapmode.styles.MLogicalStyleController;
import org.freeplane.features.mindmapmode.styles.MUIFactory;
import org.freeplane.features.mindmapmode.styles.ShowFormatPanelAction;
import org.freeplane.features.mindmapmode.styles.StyleEditorPanel;
import org.freeplane.features.mindmapmode.text.MTextController;
import org.freeplane.features.mindmapmode.text.SortNodes;
import org.freeplane.features.mindmapmode.text.SplitNode;
import org.freeplane.features.mindmapmode.time.ReminderHook;
import org.freeplane.main.mindmapmode.stylemode.SModeControllerFactory;
import org.freeplane.view.swing.addins.BlinkingNodeHook;
import org.freeplane.view.swing.addins.FitToPage;
import org.freeplane.view.swing.addins.filepreview.ViewerController;
import org.freeplane.view.swing.addins.nodehistory.NodeHistory;
import org.freeplane.view.swing.map.MainView;
import org.freeplane.view.swing.map.ShowNotesInMapAction;
import org.freeplane.view.swing.map.attribute.AttributePanelManager;
import org.freeplane.view.swing.map.attribute.EditAttributesAction;
import org.freeplane.view.swing.ui.DefaultMapMouseListener;
import org.freeplane.view.swing.ui.DefaultNodeKeyListener;
import org.freeplane.view.swing.ui.DefaultNodeMouseMotionListener;
import org.freeplane.view.swing.ui.UserInputListenerFactory;
import org.freeplane.view.swing.ui.mindmapmode.MNodeDragListener;
import org.freeplane.view.swing.ui.mindmapmode.MMouseMotionListener;
import org.freeplane.view.swing.ui.mindmapmode.MNodeDropListener;
import org.freeplane.view.swing.ui.mindmapmode.MNodeMotionListener;

/**
 * @author Dimitry Polivaev 24.11.2008
 */
public class MModeControllerFactory {
	private static MModeControllerFactory instance;

	public static MModeController createModeController() {
		return MModeControllerFactory.getInstance().createModeControllerImpl();
	}

	private static MModeControllerFactory getInstance() {
		if (instance == null) {
			instance = new MModeControllerFactory();
		}
		return instance;
	}

// // 	private Controller controller;
 	private MModeController modeController;
	private MUIFactory uiFactory;

	private void createAddIns() {
		final StyleEditorPanel panel = new StyleEditorPanel(uiFactory, true);
		panel.init();
		final JScrollPane styleScrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		UITools.setScrollbarIncrement(styleScrollPane);
		final JComponent tabs = (JComponent) modeController.getUserInputListenerFactory().getToolBar("/format").getComponent(1);
		tabs.add(TextUtils.getText("format_panel"), styleScrollPane);
		new AttributePanelManager(modeController);
		new HierarchicalIcons();
		new AutomaticLayout();
		new BlinkingNodeHook();
		new CreationModificationPlugin();
		new ReminderHook();
		new AutomaticEdgeColorHook();
		new ViewerController();
		final MenuBuilder menuBuilder = modeController.getUserInputListenerFactory().getMenuBuilder();
		menuBuilder.addAnnotatedAction(new ShowFormatPanelAction());
		menuBuilder.addAnnotatedAction(new FitToPage());
		MEncryptionController.install(new MEncryptionController());
		menuBuilder.addAnnotatedAction(new IconSelectionPlugin());
		menuBuilder.addAnnotatedAction(new NewParentNode());
		menuBuilder.addAnnotatedAction(new SaveAll());
		menuBuilder.addAnnotatedAction(new SortNodes());
		menuBuilder.addAnnotatedAction(new SplitNode());
		new ChangeNodeLevelController().addActionsAtMenuBuilder(menuBuilder);
		NodeHistory.install(modeController);
		menuBuilder.addAnnotatedAction(new ImportMindmanagerFiles());
		LoadAcceleratorPresetsAction.install();
	}

	private MModeController createModeControllerImpl() {
//		this.controller = controller;
		createStandardControllers();
		createAddIns();
		return modeController;
	}

	private void createStandardControllers() {
		final Controller controller = Controller.getCurrentController();
		modeController = new MModeController(controller);
		final UserInputListenerFactory userInputListenerFactory = new UserInputListenerFactory(modeController);
		userInputListenerFactory.setNodeMouseMotionListener(new DelayedMouseListener( new DefaultNodeMouseMotionListener() {
			public void mouseClicked(final MouseEvent e) {
				if (wasFocused() && e.getModifiers() == InputEvent.BUTTON1_MASK) {
					/* perform action only if one selected node. */
					final MapController mapController = modeController.getMapController();
					if (mapController.getSelectedNodes().size() != 1) {
						return;
					}
					final MainView component = (MainView) e.getComponent();
					if (component.isInFollowLinkRegion(e.getX())) {
						LinkController.getController().loadURL(e);
					}
					else {
						if (e.getClickCount() == 2 && !e.isAltDown() && !e.isControlDown() && !e.isShiftDown() && !e.isMetaDown()
								&& !e.isPopupTrigger() && e.getButton() == MouseEvent.BUTTON1) {
							((MTextController) TextController.getController()).edit(null, false, false);
							return;
						}
						final Component selectedComponent = controller.getViewController().getSelectedComponent();
						final boolean isFocused = SwingUtilities.isDescendingFrom(e.getComponent(), selectedComponent);
						if(isFocused){
							mapController.toggleFolded(mapController.getSelectedNodes().listIterator());
						}
					}
					e.consume();
				}
			}
		}, 2, MouseEvent.BUTTON1));
		modeController.setUserInputListenerFactory(userInputListenerFactory);
		controller.addModeController(modeController);
		controller.selectModeForBuild(modeController);
		modeController.setMapController(new MMapController());
		final MFileManager fileManager = new MFileManager();
		UrlManager.install(fileManager);
		controller.getMapViewManager().addMapViewChangeListener(fileManager);
		IconController.install(new MIconController(modeController));
		final MapController mapController = modeController.getMapController();
		uiFactory = new MUIFactory();
		mapController.addNodeChangeListener(uiFactory);
		mapController.addNodeSelectionListener(uiFactory);
		mapController.addMapChangeListener(uiFactory);
		controller.getMapViewManager().addMapSelectionListener(uiFactory);
		final MToolbarContributor menuContributor = new MToolbarContributor(uiFactory);
		modeController.addMenuContributor(menuContributor);
		EdgeController.install(new MEdgeController(modeController));
		CloudController.install(new MCloudController(modeController));
		NoteController.install(new MNoteController(modeController));
		LinkController.install(new MLinkController());
		userInputListenerFactory.setMapMouseListener(new DefaultMapMouseListener(new MMouseMotionListener()));
		final MTextController textController = new MTextController(modeController);
		TextController.install(textController);
		userInputListenerFactory.setNodeKeyListener(new DefaultNodeKeyListener(new IEditHandler() {
			public void edit(final KeyEvent e, final boolean addNew, final boolean editLong) {
				textController.edit(e, addNew, editLong);
			}
		}));
		NodeStyleController.install(new MNodeStyleController(modeController));
		ClipboardController.install(new MClipboardController());
		userInputListenerFactory.setNodeDragListener(new MNodeDragListener());
		userInputListenerFactory.setNodeDropTargetListener(new MNodeDropListener());
		LocationController.install(new MLocationController());
		LogicalStyleController.install(new MLogicalStyleController());
		userInputListenerFactory.setNodeMotionListener(new MNodeMotionListener());
		AttributeController.install(new MAttributeController(modeController));
		modeController.addAction(new EditAttributesAction());
		SpellCheckerController.install();
		ExportController.install(new ExportController("/xml/ExportWithXSLT.xml"));
		new MapStyle(true);
		final JPopupMenu popupmenu = new JPopupMenu();
		userInputListenerFactory.setNodePopupMenu(popupmenu);
		final FreeplaneToolBar toolbar = new FreeplaneToolBar("main_toolbar", SwingConstants.HORIZONTAL);
		toolbar.putClientProperty(ViewController.VISIBLE_PROPERTY_KEY, "toolbarVisible");
		userInputListenerFactory.addToolBar("/main_toolbar", ViewController.TOP, toolbar);
		userInputListenerFactory.addToolBar("/filter_toolbar", ViewController.TOP, FilterController.getController(
		    controller).getFilterToolbar());
		userInputListenerFactory.addToolBar("/status", ViewController.BOTTOM, controller.getViewController()
		    .getStatusBar());
		final JTabbedPane tabs = new JTabbedPane();
		Box resisableTabs = Box.createHorizontalBox();
		resisableTabs.add(new JResizer(Direction.RIGHT));
		resisableTabs.add(tabs);
		resisableTabs.putClientProperty(ViewController.VISIBLE_PROPERTY_KEY, "styleScrollPaneVisible");
		modeController.getUserInputListenerFactory().addToolBar("/format", ViewController.RIGHT, resisableTabs);
		final FButtonBar fButtonToolBar = new FButtonBar();
		fButtonToolBar.putClientProperty(ViewController.VISIBLE_PROPERTY_KEY, "fbarVisible");
		fButtonToolBar.setVisible(ResourceController.getResourceController().getBooleanProperty("fbarVisible"));
		userInputListenerFactory.addToolBar("/fbuttons", ViewController.TOP, fButtonToolBar);
		controller.addAction(new ToggleToolbarAction("ToggleFBarAction", "/fbuttons"));
		SModeControllerFactory.install();
		modeController.addAction(new SetAcceleratorOnNextClickAction());
		modeController.addAction(new ShowNotesInMapAction());
		userInputListenerFactory.getMenuBuilder().setAcceleratorChangeListener(fButtonToolBar);
		userInputListenerFactory.addToolBar("/icon_toolbar", ViewController.LEFT, ((MIconController) IconController
		    .getController()).getIconToolBarScrollPane());
		modeController.addAction(new ToggleToolbarAction("ToggleLeftToolbarAction", "/icon_toolbar"));
		new RevisionPlugin();
		new UnfoldAll();
		userInputListenerFactory.setMenuStructure("/xml/mindmapmodemenu.xml");
		userInputListenerFactory.updateMenus(modeController);
		final MenuBuilder builder = modeController.getUserInputListenerFactory().getMenuBuilder();
		((MIconController) IconController.getController()).updateIconToolbar();
		((MIconController) IconController.getController()).updateMenus(builder);
		modeController.updateMenus();
	}
}
