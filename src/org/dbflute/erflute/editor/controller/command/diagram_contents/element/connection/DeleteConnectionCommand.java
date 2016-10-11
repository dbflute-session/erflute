package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;

public class DeleteConnectionCommand extends AbstractCommand {

    private WalkerConnection connection;

    public DeleteConnectionCommand(WalkerConnection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.connection.delete();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.connection.connect();
    }
}
