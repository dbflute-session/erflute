package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyColumnGroup;

public class ChangeColumnGroupCommand extends AbstractCommand {

    private final ColumnGroupSet groupSet;
    private final List<CopyColumnGroup> oldCopyGroups;
    private final List<CopyColumnGroup> newGroups;
    private final Map<TableView, List<ERColumn>> oldColumnListMap;
    private final ERDiagram diagram;

    public ChangeColumnGroupCommand(ERDiagram diagram, ColumnGroupSet groupSet, List<CopyColumnGroup> newGroups) {
        this.diagram = diagram;
        this.groupSet = groupSet;
        this.newGroups = newGroups;
        this.oldCopyGroups = new ArrayList<>();
        this.oldColumnListMap = new HashMap<>();

        for (final ColumnGroup columnGroup : groupSet) {
            final CopyColumnGroup oldCopyGroup = new CopyColumnGroup(columnGroup);
            oldCopyGroups.add(oldCopyGroup);
        }
    }

    @Override
    protected void doExecute() {
        final ERDiagram diagram = this.diagram;

        groupSet.clear();
        oldColumnListMap.clear();

        for (final CopyColumnGroup oldCopyColumnGroup : oldCopyGroups) {
            for (final NormalColumn column : oldCopyColumnGroup.getColumns()) {
                diagram.getDiagramContents().getDictionary().remove(((CopyColumn) column).getOriginalColumn());
            }
        }

        for (final CopyColumnGroup newCopyColumnGroup : newGroups) {
            groupSet.add(newCopyColumnGroup.restructure(diagram));
        }

        for (final TableView tableView : diagram.getDiagramContents().getDiagramWalkers().getTableViewList()) {
            final List<ERColumn> columns = tableView.getColumns();
            final List<ERColumn> oldColumns = new ArrayList<>(columns);

            oldColumnListMap.put(tableView, oldColumns);

            for (final Iterator<ERColumn> iter = columns.iterator(); iter.hasNext();) {
                final ERColumn column = iter.next();

                if (column instanceof ColumnGroup) {
                    if (!groupSet.contains((ColumnGroup) column)) {
                        iter.remove();
                    }
                }
            }

            tableView.setColumns(columns);
        }
    }

    @Override
    protected void doUndo() {
        final ERDiagram diagram = this.diagram;

        groupSet.clear();

        for (final CopyColumnGroup newCopyColumnGroup : newGroups) {
            for (final NormalColumn column : newCopyColumnGroup.getColumns()) {
                diagram.getDiagramContents().getDictionary().remove(((CopyColumn) column).getOriginalColumn());
            }
        }

        for (final CopyColumnGroup copyGroup : oldCopyGroups) {
            final ColumnGroup group = copyGroup.restructure(diagram);
            groupSet.add(group);
        }

        for (final TableView tableView : oldColumnListMap.keySet()) {
            final List<ERColumn> oldColumns = oldColumnListMap.get(tableView);
            tableView.setColumns(oldColumns);
        }
    }
}
