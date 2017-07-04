package org.dbflute.erflute.editor.controller.editpolicy.element.connection;

import java.util.List;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint.CreateBendpointCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint.DeleteBendpointCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint.MoveBendpointCommand;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERVirtualDiagramEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.view.figure.connection.ERDiagramConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

public class ERDiagramBendpointEditPolicy extends BendpointEditPolicy {

    @Override
    protected Command getCreateBendpointCommand(BendpointRequest bendpointrequest) {
        final AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) getHost();
        final WalkerConnection connection = (WalkerConnection) connectionEditPart.getModel();

        if (connection.getSourceWalker() == connection.getTargetWalker()) {
            return null;
        }

        final Point point = bendpointrequest.getLocation();
        getConnection().translateToRelative(point);

        final CreateBendpointCommand createBendpointCommand =
                new CreateBendpointCommand(connection, point.x, point.y, bendpointrequest.getIndex());

        return createBendpointCommand;
    }

    @Override
    protected Command getDeleteBendpointCommand(BendpointRequest bendpointrequest) {
        final WalkerConnection connection = (WalkerConnection) getHost().getModel();

        if (connection.getSourceWalker() == connection.getTargetWalker()) {
            return null;
        }

        final DeleteBendpointCommand command = new DeleteBendpointCommand(connection, bendpointrequest.getIndex());

        return command;
    }

    @Override
    protected Command getMoveBendpointCommand(BendpointRequest bendpointrequest) {
        final ConnectionEditPart editPart = (ConnectionEditPart) getHost();

        final Point point = bendpointrequest.getLocation();
        getConnection().translateToRelative(point);

        final MoveBendpointCommand command = new MoveBendpointCommand(editPart, point.x, point.y, bendpointrequest.getIndex());

        return command;
    }

    @Override
    protected List<?> createSelectionHandles() {
        showSelectedLine();
        return super.createSelectionHandles();
    }

    @Override
    protected void showSelection() {
        final EditPart contents = getHost().getRoot().getContents();
        if (contents instanceof ERVirtualDiagramEditPart) {
            final ERVirtualDiagramEditPart part = (ERVirtualDiagramEditPart) contents;
            part.refreshVisuals();
        } else {
            final ERDiagramEditPart diagramEditPart = (ERDiagramEditPart) contents;
            diagramEditPart.refreshVisuals();
        }

        super.showSelection();
    }

    protected void showSelectedLine() {
        final ERDiagramConnection connection = (ERDiagramConnection) getHostFigure();
        connection.setSelected(true);
    }

    @Override
    protected void removeSelectionHandles() {
        final ERDiagramConnection connection = (ERDiagramConnection) getHostFigure();
        connection.setSelected(false);

        super.removeSelectionHandles();
    }
}
