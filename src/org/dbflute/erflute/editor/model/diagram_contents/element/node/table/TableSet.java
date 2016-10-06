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

/**
 * @author modified by jflute (originated in ermaster)
 */
public class TableSet extends AbstractModel implements ObjectListModel, Iterable<ERTable> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 5264397678674390103L;
    public static final String PROPERTY_CHANGE_TABLE_SET = "TableSet";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private List<ERTable> tableList; // overridden in clone()

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TableSet() {
        this.tableList = new ArrayList<ERTable>();
    }

    // ===================================================================================
    //                                                                        Manipulation
    //                                                                        ============
    public void add(ERTable table) {
        tableList.add(table);
        firePropertyChange(PROPERTY_CHANGE_TABLE_SET, null, null);
    }

    public int remove(ERTable table) {
        final int index = tableList.indexOf(table);
        tableList.remove(index);
        firePropertyChange(PROPERTY_CHANGE_TABLE_SET, null, null);
        return index;
    }

    public void setDirty() {
        this.firePropertyChange(PROPERTY_CHANGE_TABLE_SET, null, null);
    }

    public List<ERTable> getList() {
        Collections.sort(tableList); // immobilize e.g. DDL order
        return tableList;
    }

    public List<String> getAutoSequenceNames(String database) {
        final List<String> autoSequenceNames = new ArrayList<String>();
        for (final ERTable table : tableList) {
            final String prefix = table.getNameWithSchema(database) + "_";
            for (final NormalColumn column : table.getNormalColumns()) {
                final SqlType sqlType = column.getType();
                if (isSerialType(sqlType)) {
                    autoSequenceNames.add((prefix + column.getPhysicalName() + "_seq").toUpperCase());
                }
            }
        }
        return autoSequenceNames;
    }

    private boolean isSerialType(final SqlType sqlType) {
        return SqlType.valueOfId(SqlType.SQL_TYPE_ID_SERIAL).equals(sqlType)
                || SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_SERIAL).equals(sqlType);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public Iterator<ERTable> iterator() {
        Collections.sort(tableList);
        return this.tableList.iterator();
    }

    @Override
    public TableSet clone() {
        final TableSet tableSet = (TableSet) super.clone();
        final List<ERTable> newTableList = new ArrayList<ERTable>();
        for (final ERTable table : tableList) {
            final ERTable newTable = table.clone();
            newTableList.add(newTable);
        }
        tableSet.tableList = newTableList;
        return tableSet;
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
    public String getObjectType() {
        return "list";
    }

    @Override
    public String getName() {
        return DisplayMessages.getMessage("label.object.type.table_list");
    }

    @Override
    public String getDescription() {
        return "";
    }
}
