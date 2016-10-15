package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;

public class CreateConnectionCommand extends AbstractCreateConnectionCommand {

    protected WalkerConnection connection;

    public CreateConnectionCommand(WalkerConnection connection) {
        super();
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {

        DiagramWalker sourceTable = (DiagramWalker) this.source.getModel();
        DiagramWalker targetTable = (DiagramWalker) this.target.getModel();

        // Table���m�̃����[�V�����́ATable <=> Table �Ōq��

        if (sourceTable instanceof ERVirtualTable) {
            sourceTable = ((ERVirtualTable) sourceTable).getRawTable();
        }
        if (targetTable instanceof ERVirtualTable) {
            targetTable = ((ERVirtualTable) targetTable).getRawTable();
        }

        connection.setSourceWalker(sourceTable);
        connection.setTargetWalker(targetTable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        connection.setSourceWalker(null);
        connection.setTargetWalker(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String validate() {
        return null;
    }

}
