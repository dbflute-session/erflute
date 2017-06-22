package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;

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
        if (!(getSourceModel() instanceof WalkerNote) && !(getTargetModel() instanceof WalkerNote)) {
            return false;
        }
        return true;
    }

    @Override
    protected void doExecute() {
        // noteをoutgoingとしてconnectionに設定する。
        // outgoingに設定されたconnectionをXMLに書き出す仕組みになっている。
        // setSourceWalkerでoutgoingに設定される。
        connection.setSourceWalker(getNote());
        connection.setTargetWalker(getNotNote());
        ERModelUtil.refreshDiagram(getNotNote().getDiagram());
    }

    private WalkerNote getNote() {
        if ((getSourceModel() instanceof WalkerNote && getTargetModel() instanceof WalkerNote)) {
            return (WalkerNote) getSourceModel();
        } else if (getSourceModel() instanceof WalkerNote) {
            return (WalkerNote) getSourceModel();
        } else if (getTargetModel() instanceof WalkerNote) {
            return (WalkerNote) getTargetModel();
        } else {
            throw new IllegalStateException();
        }
    }

    private DiagramWalker getNotNote() {
        return (getNote() == getSourceModel() ? getTargetModel() : getSourceModel()).toMaterialize();
    }
}
