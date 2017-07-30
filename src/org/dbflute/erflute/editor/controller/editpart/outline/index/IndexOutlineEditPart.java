package org.dbflute.erflute.editor.controller.editpart.outline.index;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.index.ChangeIndexCommand;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.not_element.index.IndexComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.view.dialog.table.sub.IndexDialog;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class IndexOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    protected void refreshOutlineVisuals() {
        final ERIndex index = (ERIndex) getModel();

        setWidgetText(getDiagram().filter(index.getName()));
        setWidgetImage(Activator.getImage(ImageKey.INDEX));
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new IndexComponentEditPolicy());
    }

    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    public void performRequest(Request request) {
        final ERIndex index = (ERIndex) getModel();
        final ERDiagram diagram = getDiagram();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final IndexDialog dialog = new IndexDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), index, index.getTable());

            if (dialog.open() == IDialogConstants.OK_ID) {
                final ChangeIndexCommand command = new ChangeIndexCommand(diagram, index, dialog.getResultIndex());
                execute(command);
            }
        }

        super.performRequest(request);
    }
}
