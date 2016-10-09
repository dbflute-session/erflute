package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;

public class CreateBendpointCommand extends AbstractCommand {

    private ConnectionElement connection;

    int x;

    int y;

    private int index;

    public CreateBendpointCommand(ConnectionElement connection, int x, int y, int index) {
        this.connection = connection;
        this.x = x;
        this.y = y;
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        Bendpoint bendpoint = new Bendpoint(this.x, this.y);
        connection.addBendpoint(index, bendpoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        connection.removeBendpoint(index);
    }
}
