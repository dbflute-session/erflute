package org.dbflute.erflute.editor.controller.editpart.element;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.ERDiagramLayoutEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ViewableModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.property_source.ERDiagramPropertySource;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.SelectionManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramEditPart extends AbstractModelEditPart {

    private static boolean updateable = true;

    @Override
    public void deactivate() {
        try {
            super.deactivate();
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    protected IFigure createFigure() {
        final FreeformLayer layer = new FreeformLayer();
        layer.setLayoutManager(new FreeformLayout());
        return layer;
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new ERDiagramLayoutEditPolicy());
    }

    @Override
    protected List<Object> getModelChildren() {
        final List<Object> modelChildren = new ArrayList<Object>();
        final ERDiagram diagram = (ERDiagram) getModel();

        // #willanalyze selected category handling? by jflute
        //		modelChildren.addAll(diagram.getDiagramContents().getSettings()
        //				.getCategorySetting().getSelectedCategories());

        modelChildren.add(diagram.getDiagramContents().getSettings().getModelProperties());
        final List<DiagramWalker> nodeElementList = diagram.getDiagramContents().getDiagramWalkers().getDiagramWalkerList();
        for (final DiagramWalker element : nodeElementList) {
            modelChildren.add(element);
        }
        return modelChildren;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doPropertyChange(PropertyChangeEvent event) {
        if ("consumed".equals(event.getPropagationId())) {
            return;
        }
        if (event.getPropertyName().equals(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER)) {
            refreshChildren();
        } else if (event.getPropertyName().equals(ERVirtualDiagram.PROPERTY_CHANGE_VTABLES)) {
            refresh();
            refreshRelations();
        } else if (event.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_ALL)) {
            this.refresh();
            this.refreshRelations();
            final List<DiagramWalker> nodeElementList = (List<DiagramWalker>) event.getNewValue();
            if (nodeElementList != null) {
                this.getViewer().deselectAll();
                final SelectionManager selectionManager = this.getViewer().getSelectionManager();
                final Map<DiagramWalker, EditPart> modelToEditPart = getModelToEditPart();
                for (final DiagramWalker walker : nodeElementList) {
                    selectionManager.appendSelection(modelToEditPart.get(walker));
                }
            }
        } else if (event.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_ADD)) {
            final Object newValue = event.getNewValue();
            if (newValue instanceof ERVirtualDiagram) {
                refresh();
                refreshVisuals();
            }
        } else if (event.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_TABLE)) {
            final TableView newTable = (TableView) event.getNewValue();
            this.internalRefreshTable(newTable);
        } else if (event.getPropertyName().equals(ViewableModel.PROPERTY_CHANGE_COLOR)) {
            this.refreshVisuals();
        } else if (event.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_DATABASE)) {
            this.changeDatabase(event);
        } else if (event.getPropertyName().equals(ERDiagramPropertySource.PROPERTY_INIT_DATABASE)) {
            final ERDiagram diagram = (ERDiagram) this.getModel();
            diagram.restoreDatabase(DBManagerFactory.getAllDBList().get(0));
        } else if (event.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_SETTINGS)) {
            this.changeSettings();
        }
    }

    private void internalRefreshTable(TableView tableView) {
        final Set<Entry<DiagramWalker, EditPart>> entrySet = getModelToEditPart().entrySet();
        for (final Entry<DiagramWalker, EditPart> entry : entrySet) {
            if (entry.getKey().equals(tableView)) {
                entry.getValue().refresh();
            }
            if (tableView.getName().equals(entry.getKey().getName())) {
                entry.getValue().refresh();
            }
        }
    }

    public void refreshRelations() {
        for (final Object child : this.getChildren()) {
            if (child instanceof DiagramWalkerEditPart) {
                final DiagramWalkerEditPart part = (DiagramWalkerEditPart) child;
                part.refreshConnections();
            }
        }
    }

    @Override
    public void refreshVisuals() {
        final ERDiagram element = (ERDiagram) this.getModel();
        final int[] color = element.getColor();
        if (color != null) {
            final Color bgColor = DesignResources.getColor(color);
            this.getViewer().getControl().setBackground(bgColor);
        }
        for (final Object child : this.getChildren()) {
            if (child instanceof DiagramWalkerEditPart) {
                final DiagramWalkerEditPart part = (DiagramWalkerEditPart) child;
                part.refreshVisuals();
            }
        }
    }

    private void changeSettings() {
        final ERDiagram diagram = (ERDiagram) getModel();
        final DiagramSettings settings = diagram.getDiagramContents().getSettings();
        for (final Object child : getChildren()) {
            if (child instanceof DiagramWalkerEditPart) {
                final DiagramWalkerEditPart part = (DiagramWalkerEditPart) child;
                part.changeSettings(settings);
            }
        }
    }

    private void changeDatabase(PropertyChangeEvent event) {
        final MessageBox messageBox =
                new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
        messageBox.setText(DisplayMessages.getMessage("dialog.title.change.database"));
        messageBox.setMessage(DisplayMessages.getMessage("dialog.message.change.database"));
        if (messageBox.open() == SWT.OK) {
            event.setPropagationId("consumed");
        } else {
            final ERDiagram diagram = (ERDiagram) this.getModel();
            diagram.restoreDatabase(String.valueOf(event.getOldValue()));
        }
    }

    private Map<DiagramWalker, EditPart> getModelToEditPart() {
        final Map<DiagramWalker, EditPart> modelToEditPart = new HashMap<DiagramWalker, EditPart>();
        @SuppressWarnings("unchecked")
        final List<Object> children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            final EditPart editPart = (EditPart) children.get(i);
            modelToEditPart.put((DiagramWalker) editPart.getModel(), editPart);
        }
        return modelToEditPart;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public static void setUpdateable(boolean enabled) {
        updateable = enabled;
    }

    public static boolean isUpdateable() {
        return updateable;
    }
}
