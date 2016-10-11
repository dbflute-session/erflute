package org.dbflute.erflute.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERModelEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERVirtualTableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.VGroupEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.view.ERDiagramGotoMarker;
import org.dbflute.erflute.editor.view.ERDiagramOnePopupMenuManager;
import org.dbflute.erflute.editor.view.action.ermodel.PlaceTableAction;
import org.dbflute.erflute.editor.view.action.ermodel.VGroupManageAction;
import org.dbflute.erflute.editor.view.outline.ERDiagramOutlinePage;
import org.dbflute.erflute.editor.view.outline.ERDiagramOutlinePopupMenuManager;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class VirtualDiagramEditor extends MainDiagramEditor { // created by ERFluteMultiPageEditor

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private ERVirtualDiagram model; // may be changed

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public VirtualDiagramEditor(ERDiagram diagram, ERVirtualDiagram model, ERDiagramEditPartFactory editPartFactory,
            ZoomComboContributionItem zoomComboContributionItem, ERDiagramOutlinePage outlinePage) {
        super(diagram, editPartFactory, zoomComboContributionItem, outlinePage);
        this.model = model;
    }

    // ===================================================================================
    //                                                                    Default Override
    //                                                                    ================
    @Override
    public DefaultEditDomain getDefaultEditDomain() {
        return getEditDomain();
    }

    @Override
    public ActionRegistry getDefaultActionRegistry() {
        return getActionRegistry();
    }

    // ===================================================================================
    //                                                                       Create Action
    //                                                                       =============
    @Override
    protected void createActions() {
        super.createActions();
        final ActionRegistry registry = this.getActionRegistry();
        final List<IAction> actionList =
                new ArrayList<IAction>(Arrays.asList(new IAction[] { new PlaceTableAction(this), new VGroupManageAction(this), }));
        for (final IAction action : actionList) {
            registry.registerAction(action);
        }
    }

    // ===================================================================================
    //                                                                     GraphicalViewer
    //                                                                     ===============
    @Override
    protected void initializeGraphicalViewer() {
        final GraphicalViewer viewer = this.getGraphicalViewer();
        viewer.setEditPartFactory(editPartFactory);

        this.initViewerAction(viewer);
        this.initDragAndDrop(viewer);

        viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), MouseWheelZoomHandler.SINGLETON);
        viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, true);
        viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, true);
        viewer.setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, true);

        final MenuManager menuMgr = new ERDiagramOnePopupMenuManager(this.getActionRegistry(), this.model);
        this.extensionLoader.addERDiagramPopupMenu(menuMgr, this.getActionRegistry());
        viewer.setContextMenu(menuMgr);
        viewer.setContents(model);
        this.outlineMenuMgr =
                new ERDiagramOutlinePopupMenuManager(this.diagram, this.getActionRegistry(), this.outlinePage.getOutlineActionRegistory(),
                        this.outlinePage.getViewer());
        this.gotoMaker = new ERDiagramGotoMarker(this);
    }

    // ===================================================================================
    //                                                                        Change Model
    //                                                                        ============
    public void setContents(ERVirtualDiagram newModel) {
        model = newModel;
        getGraphicalViewer().setContents(newModel);
        newModel.changeAll();
    }

    public void refresh() {
        model.changeAll();
    }

    // ===================================================================================
    //                                                                              Reveal
    //                                                                              ======
    @Override
    public void reveal(ERTable table) {
        final ERModelEditPart editPart = (ERModelEditPart) getGraphicalViewer().getContents();
        final List<?> tableParts = editPart.getChildren();
        for (final Object tableEditPart : tableParts) {
            if (tableEditPart instanceof ERVirtualTableEditPart) {
                final ERVirtualTableEditPart vtableEditPart = (ERVirtualTableEditPart) tableEditPart;
                if (((ERVirtualTable) vtableEditPart.getModel()).getRawTable().equals(table)) {
                    getGraphicalViewer().reveal(vtableEditPart);
                    return;
                }
            }
            if (tableEditPart instanceof VGroupEditPart) {
                // do nothing
                //VGroupEditPart groupEditPart = (VGroupEditPart) tableEditPart;
                //List children = groupEditPart.getChildren();
            }
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ERVirtualDiagram getModel() {
        return model;
    }
}
