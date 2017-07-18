package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;

public class ChangeIndexCommand extends AbstractCommand {

    private final ERTable table;
    private final List<ERIndex> oldIndexList;
    private final List<ERIndex> newIndexList;

    public ChangeIndexCommand(ERDiagram diagram, ERIndex oldIndex, ERIndex newIndex) {
        this.table = oldIndex.getTable();

        this.oldIndexList = oldIndex.getTable().getIndexes();
        this.newIndexList = new ArrayList<>(oldIndexList);

        final int i = newIndexList.indexOf(oldIndex);
        newIndexList.remove(i);
        newIndexList.add(i, newIndex);
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
