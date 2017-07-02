package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column;

import org.dbflute.erflute.editor.model.AbstractModel;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class ERColumn extends AbstractModel {

    private static final long serialVersionUID = 1L;

    private ColumnHolder columnHolder;

    public abstract String getName();

    public void setColumnHolder(ColumnHolder columnHolder) {
        this.columnHolder = columnHolder;
    }

    public ColumnHolder getColumnHolder() {
        return this.columnHolder;
    }

    public boolean same(ERColumn erColumn) {
        return this.columnHolder.same(erColumn.columnHolder) && getName().equals(erColumn.getName());
    }
}
