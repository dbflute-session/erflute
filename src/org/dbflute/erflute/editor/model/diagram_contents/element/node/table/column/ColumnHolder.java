package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column;

public interface ColumnHolder {

    String getName();

    default boolean same(ColumnHolder columnHolder) {
        if (columnHolder == null) {
            return false;
        }
        return getName().equals(columnHolder.getName());
    }
}
