package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column;

public interface ColumnHolder {

    String getName();

    default boolean same(ColumnHolder columnHolder) {
        return getName().equals(columnHolder.getName());
    }
}
