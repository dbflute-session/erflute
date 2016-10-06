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
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;

public abstract class TableView extends NodeElement implements ObjectModel, ColumnHolder, Comparable<TableView> {

    private static final long serialVersionUID = -4492787972500741281L;

    public static final String PROPERTY_CHANGE_PHYSICAL_NAME = "table_view_physicalName";

    public static final String PROPERTY_CHANGE_LOGICAL_NAME = "table_view_logicalName";

    public static final String PROPERTY_CHANGE_COLUMNS = "columns";

    public static final int DEFAULT_WIDTH = 120;

    public static final int DEFAULT_HEIGHT = 75;

    public static final Comparator<TableView> PHYSICAL_NAME_COMPARATOR = new TableViewPhysicalNameComparator();

    public static final Comparator<TableView> LOGICAL_NAME_COMPARATOR = new TableViewLogicalNameComparator();

    private String physicalName;

    private String logicalName;

    private String description;

    protected List<ERColumn> columns;

    protected TableViewProperties tableViewProperties;

    public TableView() {
        this.columns = new ArrayList<ERColumn>();
    }

    public String getPhysicalName() {
        return physicalName;
    }

    public void setPhysicalName(String physicalName) {
        final String old = this.physicalName;
        this.physicalName = physicalName;

        this.firePropertyChange(PROPERTY_CHANGE_PHYSICAL_NAME, old, physicalName);
    }

    public String getLogicalName() {
        return logicalName;
    }

    public void setLogicalName(String logicalName) {
        final String old = this.logicalName;
        this.logicalName = logicalName;
        this.firePropertyChange(PROPERTY_CHANGE_LOGICAL_NAME, old, logicalName);
    }

    @Override
    public String getName() {
        return this.getPhysicalName(); // #for_erflute change logical to physical for fixed sort
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ERColumn> getColumns() {
        return this.columns;
    }

    public TableViewProperties getTableViewProperties() {
        return this.tableViewProperties;
    }

    public List<NormalColumn> getExpandedColumns() {
        final List<NormalColumn> expandedColumns = new ArrayList<NormalColumn>();
        for (final ERColumn column : this.getColumns()) {
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

    public List<Relationship> getIncomingRelations() {
        final List<Relationship> relations = new ArrayList<Relationship>();
        for (final ConnectionElement connection : this.getIncomings()) {
            if (connection instanceof Relationship) {
                relations.add((Relationship) connection);
            }
        }
        return relations;
    }

    public List<Relationship> getOutgoingRelations() {
        final List<Relationship> relations = new ArrayList<Relationship>();
        for (final ConnectionElement connection : this.getOutgoings()) {
            if (connection instanceof Relationship) {
                relations.add((Relationship) connection);
            }
        }
        Collections.sort(relations);
        return relations;
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        if (this.getDiagram() != null) {
            for (final Relationship relation : this.getOutgoingRelations()) {
                relation.setParentMove();
            }
            for (final Relationship relation : this.getIncomingRelations()) {
                relation.setParentMove();
            }
        }
    }

    public List<NormalColumn> getNormalColumns() {
        final List<NormalColumn> normalColumns = new ArrayList<NormalColumn>();

        for (final ERColumn column : this.columns) {
            if (column instanceof NormalColumn) {
                normalColumns.add((NormalColumn) column);
            }
        }
        return normalColumns;
    }

    public ERColumn getColumn(int index) {
        return this.columns.get(index);
    }

    public void setColumns(List<ERColumn> columns) {
        this.columns = columns;

        for (final ERColumn column : columns) {
            column.setColumnHolder(this);
        }
        this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    public void setDirty() {
        this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    public void addColumn(ERColumn column) {
        this.columns.add(column);
        column.setColumnHolder(this);

        this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    public void addColumn(int index, ERColumn column) {
        this.columns.add(index, column);
        column.setColumnHolder(this);

        this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    public void removeColumn(ERColumn column) {
        this.columns.remove(column);
        this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    public TableView copyTableViewData(TableView to) {
        to.setDiagram(this.getDiagram());

        to.setPhysicalName(this.getPhysicalName());
        to.setLogicalName(this.getLogicalName());
        to.setDescription(this.getDescription());

        final List<ERColumn> columns = new ArrayList<ERColumn>();

        for (final ERColumn fromColumn : this.getColumns()) {
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

        to.setOutgoing(this.getOutgoings());
        to.setIncoming(this.getIncomings());

        return to;
    }

    public void restructureData(TableView to) {
        final Dictionary dictionary = this.getDiagram().getDiagramContents().getDictionary();

        to.setPhysicalName(this.getPhysicalName());
        to.setLogicalName(this.getLogicalName());
        to.setDescription(this.getDescription());
        if (getColor() != null) {
            to.setColor(this.getColor()[0], this.getColor()[1], this.getColor()[2]);
        }

        for (final NormalColumn toColumn : to.getNormalColumns()) {
            dictionary.remove(toColumn);
        }

        final List<ERColumn> columns = new ArrayList<ERColumn>();

        final List<NormalColumn> newPrimaryKeyColumns = new ArrayList<NormalColumn>();

        for (final ERColumn fromColumn : this.getColumns()) {
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

        this.setTargetTableRelation(to, newPrimaryKeyColumns);

        to.setColumns(columns);
    }

    private void setTargetTableRelation(TableView sourceTable, List<NormalColumn> newPrimaryKeyColumns) {
        for (final Relationship relation : sourceTable.getOutgoingRelations()) {

            // �֘A��PK���Q�Ƃ��Ă���ꍇ
            if (relation.isReferenceForPK()) {
                // �Q�Ƃ���e�[�u��
                final TableView targetTable = relation.getTargetTableView();

                // �O���L�[���X�g
                final List<NormalColumn> foreignKeyColumns = relation.getForeignKeyColumns();

                boolean isPrimary = true;
                boolean isPrimaryChanged = false;

                // �Q�Ƃ����e�[�u����PK�ɑ΂��ď������s��
                for (final NormalColumn primaryKeyColumn : newPrimaryKeyColumns) {
                    boolean isReferenced = false;

                    for (final Iterator<NormalColumn> iter = foreignKeyColumns.iterator(); iter.hasNext();) {

                        // �O���L�[
                        final NormalColumn foreignKeyColumn = iter.next();

                        if (isPrimary) {
                            isPrimary = foreignKeyColumn.isPrimaryKey();
                        }

                        // �O���L�[�̎Q�Ɨ�PK��Ɠ����ꍇ
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
                        final NormalColumn foreignKeyColumn = new NormalColumn(primaryKeyColumn, primaryKeyColumn, relation, isPrimary);

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

                    this.setTargetTableRelation(targetTable, nextNewPrimaryKeyColumns);
                }

                targetTable.setDirty();
            }
        }
    }

    @Override
    public int compareTo(TableView other) {
        return PHYSICAL_NAME_COMPARATOR.compare(this, other);
    }

    public void replaceColumnGroup(ColumnGroup oldColumnGroup, ColumnGroup newColumnGroup) {
        final int index = this.columns.indexOf(oldColumnGroup);
        if (index != -1) {
            this.columns.remove(index);
            this.columns.add(index, newColumnGroup);
        }
    }

    public String getNameWithSchema(String database) {
        final StringBuilder sb = new StringBuilder();

        final DBManager dbManager = DBManagerFactory.getDBManager(database);

        if (!dbManager.isSupported(DBManager.SUPPORT_SCHEMA)) {
            return Format.null2blank(this.getPhysicalName());
        }

        final TableViewProperties commonTableViewProperties = this.getDiagram().getDiagramContents().getSettings().getTableViewProperties();

        String schema = this.tableViewProperties.getSchema();

        if (schema == null || schema.equals("")) {
            schema = commonTableViewProperties.getSchema();
        }

        if (schema != null && !schema.equals("")) {
            sb.append(schema);
            sb.append(".");
        }

        sb.append(this.getPhysicalName());

        return sb.toString();
    }

    public abstract TableView copyData();

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

            final int compareTo =
                    Format.null2blank(o1.getTableViewProperties().getSchema())
                            .toUpperCase()
                            .compareTo(Format.null2blank(o2.getTableViewProperties().getSchema()).toUpperCase());

            if (compareTo != 0) {
                return compareTo;
            }

            int value = 0;

            value = Format.null2blank(o1.physicalName).toUpperCase().compareTo(Format.null2blank(o2.physicalName).toUpperCase());
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

            final int compareTo =
                    Format.null2blank(o1.getTableViewProperties().getSchema())
                            .toUpperCase()
                            .compareTo(Format.null2blank(o2.getTableViewProperties().getSchema()).toUpperCase());

            if (compareTo != 0) {
                return compareTo;
            }

            int value = 0;

            value = Format.null2blank(o1.logicalName).toUpperCase().compareTo(Format.null2blank(o2.logicalName).toUpperCase());
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
}
