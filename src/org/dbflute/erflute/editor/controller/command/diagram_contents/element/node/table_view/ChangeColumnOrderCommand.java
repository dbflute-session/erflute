package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;

public class ChangeColumnOrderCommand extends AbstractCommand {

    private final TableView tableView;
    private final ERColumn column;
    private int newIndex;
    private final int oldIndex;

    public ChangeColumnOrderCommand(TableView tableView, ERColumn column, int index) {
        this.tableView = tableView;
        this.column = column;
        this.newIndex = index;
        this.oldIndex = tableView.getColumns().indexOf(column);

        if (oldIndex < newIndex) {
            newIndex--;
        }
    }

    @Override
    protected void doExecute() {
        tableView.removeColumn(column);
        tableView.addColumn(newIndex, column);
        tableView.getDiagram().changeAll();
    }

    @Override
    protected void doUndo() {
        tableView.removeColumn(column);
        tableView.addColumn(oldIndex, column);
        tableView.getDiagram().changeAll();
    }
}
