package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;

public class DeleteIndexCommand extends AbstractCommand {

    private final ERTable table;
    private final List<ERIndex> oldIndexList;
    private final List<ERIndex> newIndexList;

    public DeleteIndexCommand(ERDiagram diagram, ERIndex index) {
        this.table = index.getTable();
        this.oldIndexList = index.getTable().getIndexes();
        this.newIndexList = new ArrayList<>(oldIndexList);
        newIndexList.remove(index);
    }

    @Override
    protected void doExecute() {
        table.setIndexes(newIndexList);
    }

    @Override
    protected void doUndo() {
        table.setIndexes(oldIndexList);
    }
}
