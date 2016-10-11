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
import org.dbflute.erflute.editor.model.settings.Settings;
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

    public void propertyChange(PropertyChangeEvent evt) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        this.refreshName();

        for (Object child : this.getChildren()) {
            EditPart part = (EditPart) child;
            part.refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(Request request) {
        ERView view = (ERView) this.getModel();
        ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            ERView copyView = view.copyData();

            ViewDialog dialog =
                    new ViewDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), this.getViewer(), copyView, diagram
                            .getDiagramContents().getGroups());

            if (dialog.open() == IDialogConstants.OK_ID) {
                CompoundCommand command = ViewEditPart.createChangeViewPropertyCommand(diagram, view, copyView);

                this.execute(command.unwrap());
            }
        }

        super.performRequest(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.COMPONENT_ROLE, new DiagramWalkerComponentEditPolicy());
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

    protected void refreshName() {
        ERView model = (ERView) this.getModel();

        ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

        String name = null;

        int viewMode = diagram.getDiagramContents().getSettings().getOutlineViewMode();

        if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
            if (model.getPhysicalName() != null) {
                name = model.getPhysicalName();

            } else {
                name = "";
            }

        } else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
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

        this.setWidgetText(diagram.filter(name));
        this.setWidgetImage(Activator.getImage(ImageKey.VIEW));
    }
}
