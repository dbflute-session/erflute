package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
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
        if (tableView instanceof ERVirtualTable) {
            final ERVirtualTable vtable = (ERVirtualTable) tableView;

            // メインビューを更新（枠の再生成）
            this.newCopyTableView.restructureData(vtable.getRawTable());
            // TableView.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);

            // サブビューも更新
            vtable.changeTable();

            // テーブルの更新（線も含めた再生成）
            this.tableView.getDiagram().changeTable(newCopyTableView);
            // ERDiagram.firePropertyChange(PROPERTY_CHANGE_TABLE)

        } else {
            // メインビューを更新
            this.newCopyTableView.restructureData(tableView);
            this.tableView.getDiagram().changeTable(newCopyTableView);

            // サブビューも更新
            tableView.getDiagram().doChangeTable(newCopyTableView);
        }
    }

    @Override
    protected void doUndo() {
        this.oldCopyTableView.restructureData(tableView);
        this.tableView.getDiagram().changeAll();
    }
}
