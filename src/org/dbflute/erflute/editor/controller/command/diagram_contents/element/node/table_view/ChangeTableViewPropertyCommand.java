package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;

public class ChangeTableViewPropertyCommand extends AbstractCommand {

    private final TableView oldCopyTableView;

    private final TableView tableView;

    private final TableView newCopyTableView;

    public ChangeTableViewPropertyCommand(TableView tableView, TableView newCopyTableView) {
        this.tableView = tableView;
        this.oldCopyTableView = tableView.copyData();
        this.newCopyTableView = newCopyTableView;
    }

    @Override
    protected void doExecute() {
        tableView.changeTableViewProperty(newCopyTableView);
    }

    @Override
    protected void doUndo() {
        tableView.changeTableViewProperty(oldCopyTableView);
    }
}
