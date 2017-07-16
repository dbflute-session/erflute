package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;

public class DeleteBendpointCommand extends AbstractCommand {

    private final WalkerConnection connection;
    private Bendpoint oldBendpoint;
    private final int index;

    public DeleteBendpointCommand(WalkerConnection connection, int index) {
        this.connection = connection;
        this.index = index;
    }

    @Override
    protected void doExecute() {
        this.oldBendpoint = connection.getBendpoints().get(index);
        connection.removeBendpoint(index);
    }

    @Override
    protected void doUndo() {
        connection.addBendpoint(index, oldBendpoint);
    }
}
