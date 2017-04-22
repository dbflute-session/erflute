package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;

public class CreateBendpointCommand extends AbstractCommand {

    private final WalkerConnection connection;

    int x;

    int y;

    private final int index;

    public CreateBendpointCommand(WalkerConnection connection, int x, int y, int index) {
        this.connection = connection;
        this.x = x;
        this.y = y;
        this.index = index;
    }

    @Override
    protected void doExecute() {
        final Bendpoint bendpoint = new Bendpoint(this.x, this.y);
        connection.addBendpoint(index, bendpoint);
    }

    @Override
    protected void doUndo() {
        connection.removeBendpoint(index);
    }
}
