package org.dbflute.erflute.editor.controller.editpart.outline.view;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ViewEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.DiagramWalkerComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.dialog.view.ViewDialog;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class ViewOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
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
    public void performRequest(Request request) {
        final ERView view = (ERView) getModel();
        final ERDiagram diagram = getDiagram();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final ERView copyView = view.copyData();

            final ViewDialog dialog =
                    new ViewDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getViewer(), copyView, diagram
                            .getDiagramContents().getColumnGroupSet());

            if (dialog.open() == IDialogConstants.OK_ID) {
                final CompoundCommand command = ViewEditPart.createChangeViewPropertyCommand(diagram, view, copyView);

                execute(command.unwrap());
            }
        }

        super.performRequest(request);
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new DiagramWalkerComponentEditPolicy());
    }

    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    protected void refreshName() {
        final ERView model = (ERView) getModel();

        final ERDiagram diagram = getDiagram();

        String name = null;

        final int viewMode = diagram.getDiagramContents().getSettings().getOutlineViewMode();

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
        setWidgetImage(Activator.getImage(ImageKey.VIEW));
    }
}
