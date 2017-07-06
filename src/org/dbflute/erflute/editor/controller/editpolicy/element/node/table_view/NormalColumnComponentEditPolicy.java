package org.dbflute.erflute.editor.controller.editpolicy.element.node.table_view;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class NormalColumnComponentEditPolicy extends ComponentEditPolicy {

    @Override
    protected Command createDeleteCommand(GroupRequest request) {
        try {
            if (request.getEditParts().size() == 1) {
                if (getHost().getModel() instanceof NormalColumn) {
                    final NormalColumn normalColumn = (NormalColumn) getHost().getModel();

                    if (normalColumn.getColumnHolder() instanceof TableView) {
                        if (!normalColumn.isForeignKey() && !normalColumn.isReferedStrictly()) {

                            final TableView table = (TableView) normalColumn.getColumnHolder();

                            final TableView newCopyTable = table.copyData();
                            for (final NormalColumn copyColumn : newCopyTable.getNormalColumns()) {
                                final CopyColumn targetColumn = (CopyColumn) copyColumn;
                                if (targetColumn.getOriginalColumn() == normalColumn) {
                                    newCopyTable.removeColumn(targetColumn);
                                    break;
                                }
                            }

                            final ChangeTableViewPropertyCommand command = new ChangeTableViewPropertyCommand(table, newCopyTable);

                            return command;
                        }
                    } else if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
                        final ColumnGroup columnGroup = (ColumnGroup) normalColumn.getColumnHolder();

                        // ColumnGroup の ColumnHolder からはテーブルは取得できないので注意
                        final TableView table = (TableView) getHost().getParent().getModel();

                        final TableView newCopyTable = table.copyData();

                        for (final ERColumn copyColumn : newCopyTable.getColumns()) {
                            if (copyColumn == columnGroup) {
                                newCopyTable.removeColumn(copyColumn);
                                break;
                            }
                        }

                        final ChangeTableViewPropertyCommand command = new ChangeTableViewPropertyCommand(table, newCopyTable);

                        return command;
                    }
                } else if (getHost().getModel() instanceof ColumnGroup) {
                    final ColumnGroup columnGroup = (ColumnGroup) getHost().getModel();

                    // ColumnGroup の ColumnHolder からはテーブルは取得できないので注意
                    final TableView table = (TableView) getHost().getParent().getModel();

                    final TableView newCopyTable = table.copyData();

                    for (final ERColumn copyColumn : newCopyTable.getColumns()) {
                        if (copyColumn == columnGroup) {
                            newCopyTable.removeColumn(copyColumn);
                            break;
                        }
                    }

                    final ChangeTableViewPropertyCommand command = new ChangeTableViewPropertyCommand(table, newCopyTable);

                    return command;
                }
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }

        return null;
    }
}
