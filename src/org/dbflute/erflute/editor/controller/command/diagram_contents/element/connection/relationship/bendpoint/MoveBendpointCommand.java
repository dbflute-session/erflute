package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.eclipse.gef.ConnectionEditPart;

public class MoveBendpointCommand extends AbstractCommand {

    private final ConnectionEditPart editPart;
    private final Bendpoint bendPoint;
    private Bendpoint oldBendpoint;
    private final int index;

    public MoveBendpointCommand(ConnectionEditPart editPart, int x, int y, int index) {
        this.editPart = editPart;
        this.bendPoint = new Bendpoint(x, y);
        this.index = index;
    }

    @Override
    protected void doExecute() {
        final WalkerConnection connection = (WalkerConnection) editPart.getModel();
        this.oldBendpoint = connection.getBendpoints().get(index);
        connection.replaceBendpoint(index, bendPoint);
    }

    @Override
    protected void doUndo() {
        final WalkerConnection connection = (WalkerConnection) editPart.getModel();
        connection.replaceBendpoint(index, oldBendpoint);
    }
}
