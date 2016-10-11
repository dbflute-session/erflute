package org.dbflute.erflute.editor.controller.editpart.outline.ermodel;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.command.ermodel.OpenERModelCommand;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.DiagramWalkerComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;

public class ERVirtualDiagramOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    public void propertyChange(PropertyChangeEvent evt) {
        //		if (evt.getPropertyName().equals(TableSet.PROPERTY_CHANGE_TABLE_SET)) {
        //			refresh();
        //		}
        if (evt.getPropertyName().equals(ERVirtualDiagram.PROPERTY_CHANGE_VTABLES)) {
            refresh();
        }
    }

    @Override
    public void refresh() {
        //		if (ERDiagramEditPart.isUpdateable()) {
        refreshChildren();
        refreshVisuals();
        //		}
    }

    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }

    public boolean isDeleteable() {
        return true;
    }

    @Override
    protected void refreshOutlineVisuals() {
        this.refreshName();

        for (Object child : this.getChildren()) {
            EditPart part = (EditPart) child;
            part.refresh();
        }
    }

    private void refreshName() {
        ERVirtualDiagram model = (ERVirtualDiagram) this.getModel();
        //		ERModelSet modelSet = (ERModelSet) this.getModel();

        //		ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();
        //
        //		String name = null;
        //
        //		int viewMode = diagram.getDiagramContents().getSettings()
        //				.getOutlineViewMode();
        //
        //		if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
        //			if (model.getPhysicalName() != null) {
        //				name = model.getPhysicalName();
        //
        //			} else {
        //				name = "";
        //			}
        //
        //		} else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
        //			if (model.getLogicalName() != null) {
        //				name = model.getLogicalName();
        //
        //			} else {
        //				name = "";
        //			}
        //
        //		} else {
        //			if (model.getLogicalName() != null) {
        //				name = model.getLogicalName();
        //
        //			} else {
        //				name = "";
        //			}
        //
        //			name += "/";
        //
        //			if (model.getPhysicalName() != null) {
        //				name += model.getPhysicalName();
        //
        //			}
        //		}

        this.setWidgetText(model.getName());
        this.setWidgetImage(Activator.getImage(ImageKey.DIAGRAM));
    }

    @Override
    public void performRequest(Request request) {
        ERVirtualDiagram model = (ERVirtualDiagram) this.getModel();
        ERDiagram diagram = this.getDiagram();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            OpenERModelCommand command = new OpenERModelCommand(diagram, model);
            this.execute(command);
        }

        super.performRequest(request);
    }

    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.COMPONENT_ROLE, new DiagramWalkerComponentEditPolicy());
    }

}
