package org.dbflute.erflute.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ObjectModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ColumnGroup extends ERColumn implements ObjectModel, Comparable<ColumnGroup>, ColumnHolder {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String groupName;
    private List<NormalColumn> columns;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ColumnGroup() {
        this.columns = new ArrayList<>();
    }

    // ===================================================================================
    //                                                                          Used Table
    //                                                                          ==========
    public List<TableView> getUsedTalbeList(ERDiagram diagram) {
        final List<TableView> usedTableList = new ArrayList<>();
        for (final TableView table : diagram.getDiagramContents().getDiagramWalkers().getTableViewList()) {
            for (final ERColumn tableColumn : table.getColumns()) {
                if (tableColumn == this) {
                    usedTableList.add(table);
                    break;
                }
            }
        }
        return usedTableList;
    }

    // ===================================================================================
    //                                                                        Object Model
    //                                                                        ============
    @Override
    public String getObjectType() {
        return "group";
    }

    @Override
    public String getName() {
        return getGroupName();
    }

    @Override
    public String getDescription() {
        return "";
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int compareTo(ColumnGroup other) {
        if (other == null) {
            return -1;
        }
        if (groupName == null) {
            return 1;
        }
        if (other.getGroupName() == null) {
            return -1;
        }
        return groupName.toUpperCase().compareTo(other.getGroupName().toUpperCase());
    }

    @Override
    public ColumnGroup clone() {
        final ColumnGroup clone = (ColumnGroup) super.clone();
        final List<NormalColumn> cloneColumns = new ArrayList<>();
        for (final NormalColumn column : columns) {
            final NormalColumn cloneColumn = column.clone();
            cloneColumns.add(cloneColumn);
        }
        clone.setColumns(cloneColumns);
        return clone;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", groupName:" + groupName);
        sb.append(", columns:" + columns);
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<NormalColumn> getColumns() {
        return columns;
    }

    public NormalColumn getColumn(int index) {
        return columns.get(index);
    }

    public void addColumn(NormalColumn column) {
        columns.add(column);
        column.setColumnHolder(this);
    }

    public void setColumns(List<NormalColumn> columns) {
        this.columns = columns;
        for (final ERColumn column : columns) {
            column.setColumnHolder(this);
        }
    }

    public void removeColumn(NormalColumn column) {
        columns.remove(column);
    }
}
