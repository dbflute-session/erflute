package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;

public class DeleteBendpointCommand extends AbstractCommand {

    private WalkerConnection connection;

    private Bendpoint oldBendpoint;

    private int index;

    public DeleteBendpointCommand(WalkerConnection connection, int index) {
        this.connection = connection;
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.oldBendpoint = this.connection.getBendpoints().get(index);
        this.connection.removeBendpoint(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.connection.addBendpoint(index, oldBendpoint);
    }
}
