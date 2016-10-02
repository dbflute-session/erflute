package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;

public class ChangeIndexCommand extends AbstractCommand {

    private ERTable table;

    private List<ERIndex> oldIndexList;

    private List<ERIndex> newIndexList;

    public ChangeIndexCommand(ERDiagram diagram, ERIndex oldIndex, ERIndex newIndex) {
        this.table = oldIndex.getTable();

        this.oldIndexList = oldIndex.getTable().getIndexes();
        this.newIndexList = new ArrayList<ERIndex>(oldIndexList);

        int i = this.newIndexList.indexOf(oldIndex);

        this.newIndexList.remove(i);
        this.newIndexList.add(i, newIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.table.setIndexes(this.newIndexList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.table.setIndexes(this.oldIndexList);
    }
}
