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

    public void propertyChange(PropertyChangeEvent evt) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        ERIndex index = (ERIndex) this.getModel();

        this.setWidgetText(this.getDiagram().filter(index.getName()));
        this.setWidgetImage(Activator.getImage(ImageKey.INDEX));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.COMPONENT_ROLE, new IndexComponentEditPolicy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }

    public boolean isDeleteable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(Request request) {
        ERIndex index = (ERIndex) this.getModel();
        ERDiagram diagram = this.getDiagram();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            IndexDialog dialog = new IndexDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), index, index.getTable());

            if (dialog.open() == IDialogConstants.OK_ID) {
                ChangeIndexCommand command = new ChangeIndexCommand(diagram, index, dialog.getResultIndex());

                this.execute(command);
            }
        }

        super.performRequest(request);
    }
}
