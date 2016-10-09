package org.dbflute.erflute.editor.controller.editpart.outline.table;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.ChangeRelationshipPropertyCommand;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.connection.RelationEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.dialog.relationship.RelationshipDialog;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class RelationOutlineEditPart extends AbstractOutlineEditPart {

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ERTable.PROPERTY_CHANGE_PHYSICAL_NAME)) {
            refreshVisuals();

        } else if (evt.getPropertyName().equals(ConnectionElement.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE)) {
            refreshVisuals();

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        Relationship model = (Relationship) this.getModel();

        ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

        int viewMode = diagram.getDiagramContents().getSettings().getOutlineViewMode();

        boolean first = true;
        StringBuilder sb = new StringBuilder();

        for (NormalColumn foreignKeyColumn : model.getForeignKeyColumns()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
                sb.append(Format.null2blank(foreignKeyColumn.getPhysicalName()));

            } else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
                sb.append(Format.null2blank(foreignKeyColumn.getLogicalName()));

            } else {
                sb.append(Format.null2blank(foreignKeyColumn.getLogicalName()));
                sb.append("/");
                sb.append(Format.null2blank(foreignKeyColumn.getPhysicalName()));
            }
        }

        this.setWidgetText(sb.toString());
        this.setWidgetImage(Activator.getImage(ImageKey.FOREIGN_KEY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.CONNECTION_ROLE, new RelationEditPolicy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(Request request) {
        Relationship relation = (Relationship) this.getModel();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            Relationship copy = relation.copy();

            RelationshipDialog dialog = new RelationshipDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), copy);

            if (dialog.open() == IDialogConstants.OK_ID) {
                ChangeRelationshipPropertyCommand command = new ChangeRelationshipPropertyCommand(relation, copy);
                this.execute(command);
            }
        }

        super.performRequest(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }
}
