package org.dbflute.erflute.core.widgets.table;

public interface CellEditWorker {

    public void addNewRow();

    public void changeRowNum();

    public boolean isModified(int row, int column);

}
