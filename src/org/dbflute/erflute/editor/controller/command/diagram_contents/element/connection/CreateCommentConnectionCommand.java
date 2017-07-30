package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class CreateCommentConnectionCommand extends CreateConnectionCommand {

    private WalkerNote note;

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
        // undo用
        this.note = getNote();

        // noteをoutgoingとしてconnectionに設定する。
        // outgoingに設定されたconnectionをXMLに書き出す仕組みになっている。
        // setSourceWalkerでoutgoingに設定される。
        connection.setSourceWalker(getNote());
        connection.setTargetWalker(getNotNote());
        // TODO ymd diagramがnullだから設定している。本来ノートにも最初からdiagramが設定されているべき。
        // ファイルロード時や生成時にdiagramを設定すると、メインダイアグラムにも表示されてしまう。なのでnullにしている。
        note.setDiagram(getNotNote().getDiagram());
        note.refreshInVirtualDiagram();
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

    @Override
    protected void doUndo() {
        super.doUndo();
        note.refreshInVirtualDiagram();
    }
}
