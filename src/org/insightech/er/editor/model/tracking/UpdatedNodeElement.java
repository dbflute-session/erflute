package org.insightech.er.editor.model.tracking;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

// #willdeleted
public class UpdatedNodeElement implements Serializable {

    private static final long serialVersionUID = -1547406607441505291L;

    private final NodeElement nodeElement;

    private final Set<ERColumn> addedColumns;

    private final Set<ERColumn> updatedColumns;

    private final Set<ERColumn> removedColumns;

    public UpdatedNodeElement(NodeElement nodeElement) {
        this.nodeElement = nodeElement;

        this.addedColumns = new HashSet<ERColumn>();
        this.updatedColumns = new HashSet<ERColumn>();
        this.removedColumns = new HashSet<ERColumn>();
    }

    public NodeElement getNodeElement() {
        return nodeElement;
    }

    public void setAddedColumns(Collection<NormalColumn> columns) {
        this.addedColumns.clear();
        this.addedColumns.addAll(columns);
    }

    public void setUpdatedColumns(Collection<NormalColumn> columns) {
        this.updatedColumns.clear();
        this.updatedColumns.addAll(columns);
    }

    public void setRemovedColumns(Collection<NormalColumn> columns) {
        this.removedColumns.clear();
        this.removedColumns.addAll(columns);
    }

    public boolean isAdded(ERColumn column) {
        if (this.addedColumns.contains(column)) {
            return true;
        }

        return false;
    }

    public boolean isUpdated(ERColumn column) {
        if (this.updatedColumns.contains(column)) {
            return true;
        }

        return false;
    }

    public Set<ERColumn> getRemovedColumns() {
        return removedColumns;
    }

}
