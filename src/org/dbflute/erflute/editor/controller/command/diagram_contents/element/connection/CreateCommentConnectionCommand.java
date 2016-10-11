package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class CreateCommentConnectionCommand extends CreateConnectionCommand {

    public CreateCommentConnectionCommand(ConnectionElement connection) {
        super(connection);
    }

    @Override
    public boolean canExecute() {
        if (!super.canExecute()) {
            return false;
        }
        if (!(this.getSourceModel() instanceof WalkerNote) && !(this.getTargetModel() instanceof WalkerNote)) {
            return false;
        }
        return true;
    }

    @Override
    protected void doExecute() {
        DiagramWalker source = (DiagramWalker) this.source.getModel();
        DiagramWalker target = (DiagramWalker) this.target.getModel();
        if (source instanceof ERVirtualTable) {
            source = ((ERVirtualTable) source).getRawTable();
        }
        if (target instanceof ERVirtualTable) {
            target = ((ERVirtualTable) target).getRawTable();
        }
        connection.setSource(source);
        connection.setTarget(target);
        if (source instanceof WalkerNote) {
            final WalkerNote note = (WalkerNote) source;
            if (note != null) { // in virtual diagram
                note.getVirtualDiagram().changeAll();
            }
        }
    }
}
