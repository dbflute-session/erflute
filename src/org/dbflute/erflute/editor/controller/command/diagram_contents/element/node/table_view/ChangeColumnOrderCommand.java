package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;

public class ChangeColumnOrderCommand extends AbstractCommand {

    private TableView tableView;

    private ERColumn column;

    private int newIndex;

    private int oldIndex;

    public ChangeColumnOrderCommand(TableView tableView, ERColumn column, int index) {
        this.tableView = tableView;
        this.column = column;
        this.newIndex = index;
        this.oldIndex = this.tableView.getColumns().indexOf(column);

        if (this.oldIndex < this.newIndex) {
            this.newIndex--;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.tableView.removeColumn(column);
        this.tableView.addColumn(newIndex, column);
        this.tableView.getDiagram().changeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.tableView.removeColumn(column);
        this.tableView.addColumn(oldIndex, column);
        this.tableView.getDiagram().changeAll();
    }

}
