package org.dbflute.erflute.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ObjectModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class TableView extends DiagramWalker implements ObjectModel, ColumnHolder, Comparable<TableView> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_PHYSICAL_NAME = "table_view_physicalName";
    public static final String PROPERTY_CHANGE_LOGICAL_NAME = "table_view_logicalName";
    public static final String PROPERTY_CHANGE_COLUMNS = "columns";
    public static final int DEFAULT_WIDTH = 120;
    public static final int DEFAULT_HEIGHT = 75;
    public static final Comparator<TableView> PHYSICAL_NAME_COMPARATOR = new TableViewPhysicalNameComparator();
    public static final Comparator<TableView> LOGICAL_NAME_COMPARATOR = new TableViewLogicalNameComparator();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String physicalName;
    private String logicalName;
    private String description;
    protected List<ERColumn> columns;
    protected TableViewProperties tableViewProperties;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TableView() {
        this.columns = new ArrayList<>();
    }

    // ===================================================================================
    //                                                                            Location
    //                                                                            ========
    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        if (getDiagram() != null) {
            for (final Relationship relationship : getOutgoingRelationshipList()) {
                relationship.setParentMove();
            }
            for (final Relationship relation : getIncomingRelationshipList()) {
                relation.setParentMove();
            }
        }
    }

    // ===================================================================================
    //                                                                              Column
    //                                                                              ======
    public List<NormalColumn> getNormalColumns() {
        final List<NormalColumn> normalColumns = new ArrayList<>();
        for (final ERColumn column : columns) {
            if (column instanceof NormalColumn) {
                normalColumns.add((NormalColumn) column);
            }
        }
        return normalColumns;
    }

    public List<NormalColumn> getExpandedColumns() {
        final List<NormalColumn> expandedColumns = new ArrayList<>();
        for (final ERColumn column : getColumns()) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;
                expandedColumns.add(normalColumn);
            } else if (column instanceof ColumnGroup) {
                final ColumnGroup groupColumn = (ColumnGroup) column;
                expandedColumns.addAll(groupColumn.getColumns());
            }
        }
        return expandedColumns;
    }

    public void setDirty() {
        firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    public void addColumn(ERColumn column) {
        getColumns().add(column);
        column.setColumnHolder(this);
        firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    public void addColumn(int index, ERColumn column) {
        getColumns().add(index, column);
        column.setColumnHolder(this);
        firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    public void removeColumn(ERColumn column) {
        getColumns().remove(column);
        firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    public NormalColumn findColumnByPhysicalName(String physicalName) {
        final List<NormalColumn> normalColumns = getNormalColumns();
        for (final NormalColumn normalColumn : normalColumns) {
            if (physicalName.equalsIgnoreCase(normalColumn.getPhysicalName())) {
                return normalColumn;
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                                               Copy
    //                                                                              ======
    public TableView copyTableViewData(TableView to) {
        to.setDiagram(getDiagram());
        to.setPhysicalName(getPhysicalName());
        to.setLogicalName(getLogicalName());
        to.setDescription(getDescription());
        final List<ERColumn> columns = new ArrayList<>();
        for (final ERColumn fromColumn : getColumns()) {
            if (fromColumn instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) fromColumn;
                final NormalColumn copyColumn = new CopyColumn(normalColumn);
                if (normalColumn.getWord() != null) {
                    copyColumn.setWord(new CopyWord(normalColumn.getWord()));
                }
                columns.add(copyColumn);
            } else {
                columns.add(fromColumn);
            }
        }
        to.setColumns(columns);
        to.setOutgoing(getOutgoings());
        to.setIncoming(getIncomings());
        return to;
    }

    public abstract TableView copyData();

    // ===================================================================================
    //                                                                         Restructure
    //                                                                         ===========
    public void restructureData(TableView to) {
        final Dictionary dictionary = getDiagram().getDiagramContents().getDictionary();
        to.setPhysicalName(getPhysicalName());
        to.setLogicalName(getLogicalName());
        to.setDescription(getDescription());
        if (getColor() != null) {
            to.setColor(getColor()[0], getColor()[1], getColor()[2]);
        }
        for (final NormalColumn toColumn : to.getNormalColumns()) {
            dictionary.remove(toColumn);
        }
        final List<ERColumn> columns = new ArrayList<>();
        final List<NormalColumn> newPrimaryKeyColumns = new ArrayList<>();
        for (final ERColumn fromColumn : getColumns()) {
            if (columns.stream().anyMatch(c -> c.same(fromColumn))) {
                // 同一テーブル同一カラムだった場合、無視する。
                continue;
            }
            if (fromColumn instanceof NormalColumn) {
                final CopyColumn copyColumn = (CopyColumn) fromColumn;
                CopyWord copyWord = copyColumn.getWord();
                if (copyColumn.isForeignKey()) {
                    copyWord = null;
                }
                if (copyWord != null) {
                    final Word originalWord = copyColumn.getOriginalWord();
                    dictionary.copyTo(copyWord, originalWord);
                }
                final NormalColumn restructuredColumn = copyColumn.getRestructuredColumn();
                restructuredColumn.setColumnHolder(this);
                if (copyWord == null) {
                    restructuredColumn.setWord(null);
                }
                columns.add(restructuredColumn);
                if (restructuredColumn.isPrimaryKey()) {
                    newPrimaryKeyColumns.add(restructuredColumn);
                }
                dictionary.add(restructuredColumn);
            } else {
                columns.add(fromColumn);
            }
        }
        setTargetTableRelation(to, newPrimaryKeyColumns);
        to.setColumns(columns);
    }

    // ===================================================================================
    //                                                                        Relationship
    //                                                                        ============
    private void setTargetTableRelation(TableView sourceTable, List<NormalColumn> newPrimaryKeyColumns) {
        for (final Relationship relationship : sourceTable.getOutgoingRelationshipList()) {
            if (relationship.isReferenceForPK()) {
                final TableView targetTable = relationship.getTargetTableView();
                final List<NormalColumn> foreignKeyColumns = relationship.getForeignKeyColumns();
                boolean isPrimary = true;
                boolean isPrimaryChanged = false;
                for (final NormalColumn primaryKeyColumn : newPrimaryKeyColumns) {
                    boolean isReferenced = false;
                    for (final Iterator<NormalColumn> iter = foreignKeyColumns.iterator(); iter.hasNext();) {
                        final NormalColumn foreignKeyColumn = iter.next();
                        if (isPrimary) {
                            isPrimary = foreignKeyColumn.isPrimaryKey();
                        }
                        for (final NormalColumn referencedColumn : foreignKeyColumn.getReferencedColumnList()) {
                            if (referencedColumn == primaryKeyColumn) {
                                isReferenced = true;
                                iter.remove();
                                break;
                            }
                        }
                        if (isReferenced) {
                            break;
                        }
                    }
                    if (!isReferenced) {
                        if (isPrimary) {
                            isPrimaryChanged = true;
                        }
                        final NormalColumn foreignKeyColumn = new NormalColumn(primaryKeyColumn, primaryKeyColumn, relationship, isPrimary);
                        targetTable.addColumn(foreignKeyColumn);
                    }
                }
                for (final NormalColumn removedColumn : foreignKeyColumns) {
                    if (removedColumn.isPrimaryKey()) {
                        isPrimaryChanged = true;
                    }
                    targetTable.removeColumn(removedColumn);
                }
                if (isPrimaryChanged) {
                    final List<NormalColumn> nextNewPrimaryKeyColumns = ((ERTable) targetTable).getPrimaryKeys();
                    setTargetTableRelation(targetTable, nextNewPrimaryKeyColumns);
                }
                targetTable.setDirty();
            }
        }
    }

    public List<Relationship> getIncomingRelationshipList() {
        final List<Relationship> relations = new ArrayList<>();
        for (final WalkerConnection connection : getIncomings()) {
            if (connection instanceof Relationship) {
                relations.add((Relationship) connection);
            }
        }
        return relations;
    }

    public List<Relationship> getOutgoingRelationshipList() {
        final List<Relationship> relations = new ArrayList<>();
        for (final WalkerConnection connection : getOutgoings()) {
            if (connection instanceof Relationship) {
                relations.add((Relationship) connection);
            }
        }
        Collections.sort(relations);
        return relations;
    }

    // ===================================================================================
    //                                                                        Column Group
    //                                                                        ============
    public void replaceColumnGroup(ColumnGroup oldColumnGroup, ColumnGroup newColumnGroup) {
        final int index = columns.indexOf(oldColumnGroup);
        if (index != -1) {
            columns.remove(index);
            columns.add(index, newColumnGroup);
        }
    }

    // ===================================================================================
    //                                                                    Name with Schema
    //                                                                    ================
    public String getNameWithSchema(String database) {
        final StringBuilder sb = new StringBuilder();
        final DBManager dbManager = DBManagerFactory.getDBManager(database);
        if (!dbManager.isSupported(DBManager.SUPPORT_SCHEMA)) {
            return Format.null2blank(getPhysicalName());
        }
        final TableViewProperties tableViewProperties = getDiagram().getDiagramContents().getSettings().getTableViewProperties();
        String schema = tableViewProperties.getSchema();
        if (schema == null || schema.equals("")) {
            schema = tableViewProperties.getSchema();
        }
        if (schema != null && !schema.equals("")) {
            sb.append(schema);
            sb.append(".");
        }
        sb.append(getPhysicalName());
        return sb.toString();
    }

    // ===================================================================================
    //                                                                        TableView ID
    //                                                                        ============
    public String buildTableViewId() {
        return getIdPrefix() + "." + getPhysicalName();
    }

    protected abstract String getIdPrefix();

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int compareTo(TableView other) {
        return PHYSICAL_NAME_COMPARATOR.compare(this, other);
    }

    private static class TableViewPhysicalNameComparator implements Comparator<TableView> {

        @Override
        public int compare(TableView o1, TableView o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1 == null) {
                return 1;
            }
            final String schema1 = o1.getTableViewProperties().getSchema();
            final String schema2 = o2.getTableViewProperties().getSchema();
            final int compareTo = Format.null2blank(schema1).toUpperCase().compareTo(Format.null2blank(schema2).toUpperCase());
            if (compareTo != 0) {
                return compareTo;
            }
            int value = Format.null2blank(o1.physicalName).toUpperCase().compareTo(Format.null2blank(o2.physicalName).toUpperCase());
            if (value != 0) {
                return value;
            }
            value = Format.null2blank(o1.logicalName).toUpperCase().compareTo(Format.null2blank(o2.logicalName).toUpperCase());
            if (value != 0) {
                return value;
            }
            return 0;
        }
    }

    private static class TableViewLogicalNameComparator implements Comparator<TableView> {

        @Override
        public int compare(TableView o1, TableView o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1 == null) {
                return 1;
            }
            final String schema1 = o1.getTableViewProperties().getSchema();
            final String schema2 = o2.getTableViewProperties().getSchema();
            final int compareTo = Format.null2blank(schema1).toUpperCase().compareTo(Format.null2blank(schema2).toUpperCase());
            if (compareTo != 0) {
                return compareTo;
            }
            int value = Format.null2blank(o1.logicalName).toUpperCase().compareTo(Format.null2blank(o2.logicalName).toUpperCase());
            if (value != 0) {
                return value;
            }
            value = Format.null2blank(o1.physicalName).toUpperCase().compareTo(Format.null2blank(o2.physicalName).toUpperCase());
            if (value != 0) {
                return value;
            }
            return 0;
        }
    }

    @Override
    public boolean isUsePersistentId() {
        return false;
    }

    @Override
    public boolean isIndenpendentOnModel() {
        return false;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getPhysicalName() {
        return physicalName;
    }

    public void setPhysicalName(String physicalName) {
        final String old = physicalName;
        this.physicalName = physicalName;
        firePropertyChange(PROPERTY_CHANGE_PHYSICAL_NAME, old, physicalName);
    }

    public String getLogicalName() {
        return logicalName;
    }

    public void setLogicalName(String logicalName) {
        final String old = logicalName;
        this.logicalName = logicalName;
        firePropertyChange(PROPERTY_CHANGE_LOGICAL_NAME, old, logicalName);
    }

    @Override
    public String getName() {
        return getPhysicalName(); // #for_erflute change logical to physical for fixed sort
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ERColumn> getColumns() {
        return columns;
    }

    public ERColumn getColumn(int index) {
        return columns.get(index);
    }

    public void setColumns(List<ERColumn> columns) {
        this.columns = columns;
        for (final ERColumn column : columns) {
            column.setColumnHolder(this);
        }
        firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    public TableViewProperties getTableViewProperties() {
        return tableViewProperties;
    }

    public void changeTableViewProperty(final TableView sourceTableView) {
        // メインビューを更新
        sourceTableView.restructureData(this);
        getDiagram().changeTable(sourceTableView);

        // サブビューも更新
        getDiagram().doChangeTable(sourceTableView);
    }
}
