package org.dbflute.erflute.editor.controller.editpart.outline.sequence;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.sequence.EditSequenceCommand;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.not_element.sequence.SequenceComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.view.dialog.outline.sequence.SequenceDialog;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

public class SequenceOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    protected void refreshOutlineVisuals() {
        final Sequence sequence = (Sequence) getModel();

        if (!DBManagerFactory.getDBManager(getDiagram()).isSupported(DBManager.SUPPORT_SEQUENCE)) {
            ((TreeItem) getWidget()).setForeground(ColorConstants.lightGray);

        } else {
            ((TreeItem) getWidget()).setForeground(ColorConstants.black);
        }

        setWidgetText(getDiagram().filter(sequence.getName()));
        setWidgetImage(Activator.getImage(ImageKey.SEQUENCE));
    }

    @Override
    public void performRequest(Request request) {
        try {
            final Sequence sequence = (Sequence) getModel();
            final ERDiagram diagram = getDiagram();

            if (request.getType().equals(RequestConstants.REQ_OPEN)) {
                final SequenceDialog dialog =
                        new SequenceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), sequence, diagram);

                if (dialog.open() == IDialogConstants.OK_ID) {
                    final EditSequenceCommand command = new EditSequenceCommand(diagram, sequence, dialog.getResult());
                    execute(command);
                }
            }

            super.performRequest(request);

        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new SequenceComponentEditPolicy());
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
