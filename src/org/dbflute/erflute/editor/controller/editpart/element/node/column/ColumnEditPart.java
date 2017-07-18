package org.dbflute.erflute.editor.controller.editpart.element.node.column;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.editor.controller.editpart.element.AbstractModelEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.table_view.ColumnSelectionHandlesEditPolicy;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.table_view.NormalColumnComponentEditPolicy;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;

public abstract class ColumnEditPart extends AbstractModelEditPart {

    public abstract void refreshTableColumns();

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ColumnSelectionHandlesEditPolicy());
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new NormalColumnComponentEditPolicy());
    }

    @Override
    public void doPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public EditPart getTargetEditPart(Request request) {
        final EditPart editPart = super.getTargetEditPart(request);
        if (!getDiagram().isDisableSelectColumn()) {
            return editPart;
        }
        if (editPart != null) {
            return editPart.getParent();
        }
        return null;
    }
}
