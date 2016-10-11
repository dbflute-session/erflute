package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class CreateCommentConnectionCommand extends CreateConnectionCommand {

    public CreateCommentConnectionCommand(WalkerConnection connection) {
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
        connection.setSourceWalker(source);
        connection.setTargetWalker(target);
        if (source instanceof WalkerNote) {
            final WalkerNote note = (WalkerNote) source;
            if (note != null) {
                final ERVirtualDiagram vdiagram = note.getVirtualDiagram();
                if (vdiagram != null) { // in virtual diagram
                    vdiagram.changeAll();
                }
            }
        }
    }
}
