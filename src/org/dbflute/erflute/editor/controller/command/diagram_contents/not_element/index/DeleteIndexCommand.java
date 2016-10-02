package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;

public class DeleteIndexCommand extends AbstractCommand {

    private ERTable table;

    private List<ERIndex> oldIndexList;

    private List<ERIndex> newIndexList;

    public DeleteIndexCommand(ERDiagram diagram, ERIndex index) {
        this.table = index.getTable();

        this.oldIndexList = index.getTable().getIndexes();
        this.newIndexList = new ArrayList<ERIndex>(oldIndexList);
        this.newIndexList.remove(index);
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
