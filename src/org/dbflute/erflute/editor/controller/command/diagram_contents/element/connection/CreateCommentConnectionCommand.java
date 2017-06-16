package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
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
        if (!(getSourceModel() instanceof WalkerNote) && !(getTargetModel() instanceof WalkerNote)) {
            return false;
        }
        return true;
    }

    @Override
    protected void doExecute() {
        // noteのoutgoingとしてconnectionを設定するため。
        // outgoingに設定されたconnectionをXMLに書き出す仕組みになっている。
        connection.setSourceWalker(getNote());
        connection.setTargetWalker(getERTable());
        ERModelUtil.refreshDiagram(getERTable().getDiagram(), getERTable());
    }

    private WalkerNote getNote() {
        final WalkerNote note = getSourceModel() instanceof WalkerNote ? (WalkerNote) getSourceModel() : (WalkerNote) getTargetModel();
        return note;
    }

    private ERTable getERTable() {
        final ERTable erTable = getSourceModel() instanceof ERTable ? (ERTable) getSourceModel() : (ERTable) getTargetModel();
        final ERTable unwrapped = erTable instanceof ERVirtualTable ? ((ERVirtualTable) erTable).getRawTable() : erTable;
        return unwrapped;
    }
}
