package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;

public class DeleteConnectionCommand extends AbstractCommand {

    private final WalkerConnection connection;
    private final DiagramWalker sourceWalker;
    private final DiagramWalker targetWalker;

    public DeleteConnectionCommand(WalkerConnection connection) {
        this.connection = connection;
        this.sourceWalker = connection.getSourceWalker();
        this.targetWalker = connection.getTargetWalker();
    }

    @Override
    protected void doExecute() {
        connection.delete();
    }

    @Override
    protected void doUndo() {
        connection.connect(sourceWalker, targetWalker);
    }
}
