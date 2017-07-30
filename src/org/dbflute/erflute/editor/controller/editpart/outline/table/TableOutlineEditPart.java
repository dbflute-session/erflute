package org.dbflute.erflute.editor.controller.editpart.outline.table;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERTableEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.DiagramWalkerComponentEditPolicy;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.dialog.table.TableDialog;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class TableOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    private final boolean quickMode;

    public TableOutlineEditPart(boolean quickMode) {
        this.quickMode = quickMode;
    }

    @Override
    protected List<AbstractModel> getModelChildren() {
        final ERTable table = getModel();
        final Category category = getCurrentCategory();
        final List<AbstractModel> children = new ArrayList<>();
        if (!quickMode) {
            for (final Relationship relation : table.getIncomingRelationshipList()) {
                if (category == null || category.contains(relation.getSourceWalker())) {
                    children.add(relation);
                }
            }
            children.addAll(table.getIndexes());
        }

        return children;
    }

    @Override
    public ERTable getModel() {
        return ((ERTable) super.getModel());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ERTable.PROPERTY_CHANGE_PHYSICAL_NAME)) {
            refreshName();
        } else if (evt.getPropertyName().equals(ERTable.PROPERTY_CHANGE_LOGICAL_NAME)) {
            refreshName();
        } else if (evt.getPropertyName().equals(ERTable.PROPERTY_CHANGE_COLUMNS)) {
            refresh();
        } else if (evt.getPropertyName().equals(IndexSet.PROPERTY_CHANGE_INDEXES)) {
            refresh();
        }
    }

    protected void refreshName() {
        final ERDiagram diagram = getDiagram();
        final int viewMode = diagram.getDiagramContents().getSettings().getOutlineViewMode();
        final ERTable model = getModel();
        String name = null;
        if (viewMode == DiagramSettings.VIEW_MODE_PHYSICAL) {
            if (model.getPhysicalName() != null) {
                name = model.getPhysicalName();
            } else {
                name = "";
            }
        } else if (viewMode == DiagramSettings.VIEW_MODE_LOGICAL) {
            if (model.getLogicalName() != null) {
                name = model.getLogicalName();
            } else {
                name = "";
            }
        } else {
            if (model.getLogicalName() != null) {
                name = model.getLogicalName();
            } else {
                name = "";
            }

            name += "/";

            if (model.getPhysicalName() != null) {
                name += model.getPhysicalName();
            }
        }

        setWidgetText(diagram.filter(name));
        setWidgetImage(Activator.getImage(ImageKey.TABLE));
    }

    @Override
    protected void refreshOutlineVisuals() {
        refreshName();

        for (final Object child : getChildren()) {
            final EditPart part = (EditPart) child;
            part.refresh();
        }
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new DiagramWalkerComponentEditPolicy());
    }

    @Override
    public void performRequest(Request request) {
        final ERTable table = getModel();
        final ERDiagram diagram = getDiagram();
        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final ERTable copyTable = table.copyData();

            final TableDialog dialog =
                    new TableDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getViewer(), copyTable, diagram
                            .getDiagramContents().getColumnGroupSet());

            if (dialog.open() == IDialogConstants.OK_ID) {
                final CompoundCommand command = ERTableEditPart.createChangeTablePropertyCommand(diagram, table, copyTable);
                execute(command.unwrap());
            }
        }

        super.performRequest(request);
    }

    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }
}
