package org.dbflute.erflute.core.widgets.table;

public interface CellEditWorker {

    void addNewRow();

    void changeRowNum();

    boolean isModified(int row, int column);
}
