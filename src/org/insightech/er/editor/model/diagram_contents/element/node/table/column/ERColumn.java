package org.insightech.er.editor.model.diagram_contents.element.node.table.column;

import org.insightech.er.editor.model.AbstractModel;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class ERColumn extends AbstractModel {

    private static final long serialVersionUID = -7808147996469841719L;

    private ColumnHolder columnHolder;

    public abstract String getName();

    public void setColumnHolder(ColumnHolder columnHolder) {
        this.columnHolder = columnHolder;
    }

    public ColumnHolder getColumnHolder() {
        return this.columnHolder;
    }
}
