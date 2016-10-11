package org.dbflute.erflute.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.editor.controller.editpolicy.ERDiagramLayoutEditPolicy;
import org.dbflute.erflute.editor.model.ViewableModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.view.drag_drop.ERDiagramTransferDragSourceListener;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Color;

public class ERVirtualDiagramEditPart extends DiagramWalkerEditPart {

    @Override
    public void doPropertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(ERVirtualDiagram.PROPERTY_CHANGE_VTABLES)) {
            refreshChildren();
            refresh();
        } else if (event.getPropertyName().equals(WalkerConnection.PROPERTY_CHANGE_CONNECTION)) {
            // ?
        } else if (event.getPropertyName().equals(ViewableModel.PROPERTY_CHANGE_COLOR)) {
            refreshVisuals();
        }
    }

    @Override
    public void refresh() {
        super.refresh();
    }

    @Override
    protected List<Object> getModelChildren() {
        final List<Object> modelChildren = new ArrayList<Object>();
        final ERVirtualDiagram vdiagram = (ERVirtualDiagram) getModel();
        modelChildren.addAll(vdiagram.getWalkerGroups());
        modelChildren.addAll(vdiagram.getVirtualTables());
        modelChildren.addAll(vdiagram.getWalkerNotes());
        return modelChildren;
    }

    @Override
    protected IFigure createFigure() {
        final FreeformLayer layer = new FreeformLayer();
        layer.setLayoutManager(new FreeformLayout());
        return layer;
    }

    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.LAYOUT_ROLE, new ERDiagramLayoutEditPolicy());
    }

    @Override
    public void refreshVisuals() {
        final ERVirtualDiagram element = (ERVirtualDiagram) this.getModel();
        final int[] color = element.getColor();
        if (color != null) {
            final Color bgColor = DesignResources.getColor(color);
            this.getViewer().getControl().setBackground(bgColor);
        }
        for (final Object child : this.getChildren()) {
            if (child instanceof DiagramWalkerEditPart) {
                final DiagramWalkerEditPart part = (DiagramWalkerEditPart) child;
                part.refreshVisuals();
            }
        }
    }

    public void refreshRelations() {
        for (final Object child : getChildren()) {
            if (child instanceof DiagramWalkerEditPart) {
                final DiagramWalkerEditPart part = (DiagramWalkerEditPart) child;
                part.refreshConnections();
            }
        }
    }

    @Override
    protected void performRequestOpen() {
    }

    @Override
    public EditPart getTargetEditPart(Request request) {
        if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_PLACE_TABLE.equals(request.getType())) {
            return this;
        }
        return super.getTargetEditPart(request);
    }

}