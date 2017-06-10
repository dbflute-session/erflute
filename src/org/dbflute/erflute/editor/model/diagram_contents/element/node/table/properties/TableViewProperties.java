package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties;

import java.io.Serializable;

import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public abstract class TableViewProperties implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private String schema;

    private Tablespace tableSpace;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Tablespace getTableSpace() {
        return tableSpace;
    }

    public void setTableSpace(Tablespace tableSpace) {
        this.tableSpace = tableSpace;
    }

    @Override
    public TableViewProperties clone() {
        TableViewProperties clone = null;

        try {
            clone = (TableViewProperties) super.clone();

        } catch (CloneNotSupportedException e) {}

        return clone;
    }
}
