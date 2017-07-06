package org.dbflute.erflute.editor.controller.editpart.outline.trigger;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.trigger.EditTriggerCommand;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.not_element.trigger.TriggerComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.view.dialog.outline.trigger.TriggerDialog;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class TriggerOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    protected void refreshOutlineVisuals() {
        final Trigger trigger = (Trigger) getModel();

        setWidgetText(getDiagram().filter(trigger.getName()));
        setWidgetImage(Activator.getImage(ImageKey.TRIGGER));
    }

    @Override
    public void performRequest(Request request) {
        final Trigger trigger = (Trigger) getModel();
        final ERDiagram diagram = (ERDiagram) getRoot().getContents().getModel();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final TriggerDialog dialog = new TriggerDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), trigger);

            if (dialog.open() == IDialogConstants.OK_ID) {
                final EditTriggerCommand command = new EditTriggerCommand(diagram, trigger, dialog.getResult());
                getViewer().getEditDomain().getCommandStack().execute(command);
            }
        }

        super.performRequest(request);
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new TriggerComponentEditPolicy());
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
