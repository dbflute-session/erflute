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
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;

public class ChangeColumnGroupCommand extends AbstractCommand {

    private ColumnGroupSet groupSet;

    private List<CopyColumnGroup> oldCopyGroups;

    private List<CopyColumnGroup> newGroups;

    private Map<TableView, List<ERColumn>> oldColumnListMap;

    private ERDiagram diagram;

    public ChangeColumnGroupCommand(ERDiagram diagram, ColumnGroupSet groupSet, List<CopyColumnGroup> newGroups) {
        this.diagram = diagram;

        this.groupSet = groupSet;

        this.newGroups = newGroups;

        this.oldCopyGroups = new ArrayList<CopyColumnGroup>();
        this.oldColumnListMap = new HashMap<TableView, List<ERColumn>>();

        for (ColumnGroup columnGroup : groupSet) {
            CopyColumnGroup oldCopyGroup = new CopyColumnGroup(columnGroup);
            this.oldCopyGroups.add(oldCopyGroup);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        ERDiagram diagram = this.diagram;

        this.groupSet.clear();
        this.oldColumnListMap.clear();

        for (CopyColumnGroup oldCopyColumnGroup : oldCopyGroups) {
            for (NormalColumn column : oldCopyColumnGroup.getColumns()) {
                diagram.getDiagramContents().getDictionary().remove(((CopyColumn) column).getOriginalColumn());
            }
        }

        for (CopyColumnGroup newCopyColumnGroup : newGroups) {
            this.groupSet.add(newCopyColumnGroup.restructure(diagram));
        }

        for (TableView tableView : this.diagram.getDiagramContents().getDiagramWalkers().getTableViewList()) {
            List<ERColumn> columns = tableView.getColumns();
            List<ERColumn> oldColumns = new ArrayList<ERColumn>(columns);

            this.oldColumnListMap.put(tableView, oldColumns);

            for (Iterator<ERColumn> iter = columns.iterator(); iter.hasNext();) {
                ERColumn column = iter.next();

                if (column instanceof ColumnGroup) {
                    if (!this.groupSet.contains((ColumnGroup) column)) {
                        iter.remove();
                    }
                }
            }

            tableView.setColumns(columns);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        ERDiagram diagram = this.diagram;

        this.groupSet.clear();

        for (CopyColumnGroup newCopyColumnGroup : newGroups) {
            for (NormalColumn column : newCopyColumnGroup.getColumns()) {
                diagram.getDiagramContents().getDictionary().remove(((CopyColumn) column).getOriginalColumn());
            }
        }

        for (CopyColumnGroup copyGroup : oldCopyGroups) {
            ColumnGroup group = copyGroup.restructure(diagram);
            this.groupSet.add(group);
        }

        for (TableView tableView : this.oldColumnListMap.keySet()) {
            List<ERColumn> oldColumns = this.oldColumnListMap.get(tableView);
            tableView.setColumns(oldColumns);
        }
    }
}
