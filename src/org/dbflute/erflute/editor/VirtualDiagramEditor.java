package org.dbflute.erflute.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.view.ERVirtualDiagramPopupMenuManager;
import org.dbflute.erflute.editor.view.action.ermodel.PlaceTableAction;
import org.dbflute.erflute.editor.view.action.ermodel.WalkerGroupManageAction;
import org.dbflute.erflute.editor.view.outline.ERDiagramOutlinePage;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class VirtualDiagramEditor extends MainDiagramEditor { // created by ERFluteMultiPageEditor

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private ERVirtualDiagram vdiagram; // may be changed

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public VirtualDiagramEditor(ERDiagram diagram, ERVirtualDiagram vdiagram, ERDiagramEditPartFactory editPartFactory,
            ZoomComboContributionItem zoomComboContributionItem, ERDiagramOutlinePage outlinePage) {
        super(diagram, editPartFactory, zoomComboContributionItem, outlinePage);
        this.vdiagram = vdiagram;
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
        final ActionRegistry registry = getActionRegistry();
        final List<IAction> actionList =
                new ArrayList<>(Arrays.asList(new IAction[] { new PlaceTableAction(this), new WalkerGroupManageAction(this), }));
        for (final IAction action : actionList) {
            registry.registerAction(action);
        }
    }

    // ===================================================================================
    //                                                                     GraphicalViewer
    //                                                                     ===============
    @Override
    protected void prepareERDiagramPopupMenu(final GraphicalViewer viewer) {
        final MenuManager menuMgr = new ERVirtualDiagramPopupMenuManager(getActionRegistry(), vdiagram);
        extensionLoader.addERDiagramPopupMenu(menuMgr, getActionRegistry());
        viewer.setContextMenu(menuMgr);
        viewer.setContents(vdiagram);
    }

    // ===================================================================================
    //                                                                        Change Model
    //                                                                        ============
    public void setContents(ERVirtualDiagram vdiagram) {
        this.vdiagram = vdiagram;
        getGraphicalViewer().setContents(vdiagram);
        vdiagram.changeAll();
    }

    public void refresh() {
        vdiagram.changeAll();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ERVirtualDiagram getVirtualDiagram() {
        return vdiagram;
    }
}
