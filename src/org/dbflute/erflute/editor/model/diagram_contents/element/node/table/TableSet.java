package org.dbflute.erflute.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectListModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class TableSet extends AbstractModel implements ObjectListModel, Iterable<ERTable> {

    private static final long serialVersionUID = 5264397678674390103L;

    public static final String PROPERTY_CHANGE_TABLE_SET = "TableSet";

    private List<ERTable> tableList;

    public TableSet() {
        this.tableList = new ArrayList<ERTable>();
    }

    public void add(ERTable table) {
        this.tableList.add(table);
        this.firePropertyChange(PROPERTY_CHANGE_TABLE_SET, null, null);
    }

    public int remove(ERTable table) {
        final int index = this.tableList.indexOf(table);
        this.tableList.remove(index);
        this.firePropertyChange(PROPERTY_CHANGE_TABLE_SET, null, null);

        return index;
    }

    public void setDirty() {
        this.firePropertyChange(PROPERTY_CHANGE_TABLE_SET, null, null);
    }

    public List<ERTable> getList() {
        Collections.sort(this.tableList);
        return this.tableList;
    }

    @Override
    public Iterator<ERTable> iterator() {
        Collections.sort(this.tableList);
        return this.tableList.iterator();
    }

    @Override
    public TableSet clone() {
        final TableSet tableSet = (TableSet) super.clone();
        final List<ERTable> newTableList = new ArrayList<ERTable>();
        for (final ERTable table : this.tableList) {
            final ERTable newTable = table.clone();
            newTableList.add(newTable);
        }
        tableSet.tableList = newTableList;
        return tableSet;
    }

    public List<String> getAutoSequenceNames(String database) {
        final List<String> autoSequenceNames = new ArrayList<String>();
        for (final ERTable table : this.tableList) {
            final String prefix = table.getNameWithSchema(database) + "_";
            for (final NormalColumn column : table.getNormalColumns()) {
                final SqlType sqlType = column.getType();

                if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_SERIAL).equals(sqlType)
                        || SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_SERIAL).equals(sqlType)) {
                    autoSequenceNames.add((prefix + column.getPhysicalName() + "_seq").toUpperCase());
                }
            }
        }
        return autoSequenceNames;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + (tableList != null ? tableList : null) + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return DisplayMessages.getMessage("label.object.type.table_list");
    }

    @Override
    public String getObjectType() {
        return "list";
    }
}
