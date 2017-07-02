package org.dbflute.erflute.editor.controller.editpart.outline.table;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.ChangeRelationshipPropertyCommand;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.connection.RelationEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.dialog.relationship.RelationshipDialog;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class RelationOutlineEditPart extends AbstractOutlineEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ERTable.PROPERTY_CHANGE_PHYSICAL_NAME)) {
            refreshVisuals();
        } else if (evt.getPropertyName().equals(WalkerConnection.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE)) {
            refreshVisuals();
        }
    }

    @Override
    protected void refreshOutlineVisuals() {
        final Relationship model = (Relationship) getModel();
        final ERDiagram diagram = (ERDiagram) getRoot().getContents().getModel();
        final int viewMode = diagram.getDiagramContents().getSettings().getOutlineViewMode();

        boolean first = true;
        final StringBuilder sb = new StringBuilder();
        for (final NormalColumn foreignKeyColumn : model.getForeignKeyColumns()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            if (viewMode == DiagramSettings.VIEW_MODE_PHYSICAL) {
                sb.append(Format.null2blank(foreignKeyColumn.getPhysicalName()));
            } else if (viewMode == DiagramSettings.VIEW_MODE_LOGICAL) {
                sb.append(Format.null2blank(foreignKeyColumn.getLogicalName()));
            } else {
                sb.append(Format.null2blank(foreignKeyColumn.getLogicalName()));
                sb.append("/");
                sb.append(Format.null2blank(foreignKeyColumn.getPhysicalName()));
            }
        }

        setWidgetText(sb.toString());
        setWidgetImage(Activator.getImage(ImageKey.FOREIGN_KEY));
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.CONNECTION_ROLE, new RelationEditPolicy());
    }

    @Override
    public void performRequest(Request request) {
        final Relationship relation = (Relationship) getModel();
        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final Relationship copy = relation.copy();
            final RelationshipDialog dialog = new RelationshipDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), copy);
            if (dialog.open() == IDialogConstants.OK_ID) {
                final ChangeRelationshipPropertyCommand command = new ChangeRelationshipPropertyCommand(relation, copy);
                execute(command);
            }
        }

        super.performRequest(request);
    }

    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }
}
