package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;

public class CreateIndexCommand extends AbstractCommand {

    private ERTable table;

    private List<ERIndex> oldIndexList;

    private List<ERIndex> newIndexList;

    public CreateIndexCommand(ERDiagram diagram, ERIndex newIndex) {
        this.table = newIndex.getTable();

        this.oldIndexList = newIndex.getTable().getIndexes();
        this.newIndexList = new ArrayList<ERIndex>(oldIndexList);

        this.newIndexList.add(newIndex);
    }

    @Override
    protected void doExecute() {
        this.table.setIndexes(this.newIndexList);
    }

    @Override
    protected void doUndo() {
        this.table.setIndexes(this.oldIndexList);
    }
}
