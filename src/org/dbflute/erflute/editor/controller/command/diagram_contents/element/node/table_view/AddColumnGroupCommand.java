package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class AddColumnGroupCommand extends AbstractCommand {

    private final TableView tableView;
    private final ColumnGroup columnGroup;
    private final int index;

    public AddColumnGroupCommand(TableView tableView, ColumnGroup columnGroup, int index) {
        this.tableView = tableView;
        this.columnGroup = columnGroup;
        this.index = index;
    }

    @Override
    protected void doExecute() {
        if (index != -1) {
            tableView.addColumn(index, columnGroup);
        }

        tableView.getDiagram().changeAll();
    }

    @Override
    protected void doUndo() {
        tableView.removeColumn(columnGroup);
        tableView.getDiagram().changeAll();
    }
}
