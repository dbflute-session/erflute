package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;

public class CreateConnectionCommand extends AbstractCreateConnectionCommand {

    protected WalkerConnection connection;

    public CreateConnectionCommand(WalkerConnection connection) {
        super();
        this.connection = connection;
    }

    @Override
    protected void doExecute() {
        final DiagramWalker sourceTable = ((DiagramWalker) source.getModel()).toMaterialize();
        final DiagramWalker targetTable = ((DiagramWalker) target.getModel()).toMaterialize();

        connection.setSourceWalker(sourceTable);
        connection.setTargetWalker(targetTable);
    }

    @Override
    protected void doUndo() {
        connection.setSourceWalker(null);
        connection.setTargetWalker(null);
    }

    @Override
    public String validate() {
        return null;
    }
}
