package org.dbflute.erflute.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ObjectModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.CopyIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TablePropertiesHolder;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CopyCompoundUniqueKey;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERTable extends TableView implements TablePropertiesHolder, ColumnHolder, ObjectModel {

    private static final long serialVersionUID = 1L;

    public static final String NEW_PHYSICAL_NAME = DisplayMessages.getMessage("new.table.physical.name");
    public static final String NEW_LOGICAL_NAME = DisplayMessages.getMessage("new.table.logical.name");

    private String constraint;
    private String primaryKeyName;
    private String option;
    private List<ERIndex> indexes;
    private List<CompoundUniqueKey> compoundUniqueKeyList;

    public ERTable() {
        this.indexes = new ArrayList<>();
        this.compoundUniqueKeyList = new ArrayList<>();
    }

    public NormalColumn getAutoIncrementColumn() {
        for (final ERColumn column : columns) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;
                if (normalColumn.isAutoIncrement()) {
                    return normalColumn;
                }
            }
        }
        return null;
    }

    public void addIndex(ERIndex index) {
        indexes.add(index);
    }

    @Override
    public ERTable copyData() {
        final ERTable to = new ERTable();

        to.setConstraint(getConstraint());
        to.setPrimaryKeyName(getPrimaryKeyName());
        to.setOption(getOption());

        super.copyTableViewData(to);

        final List<ERIndex> indexes = new ArrayList<>();

        for (final ERIndex fromIndex : getIndexes()) {
            indexes.add(new CopyIndex(to, fromIndex, to.getColumns()));
        }

        to.setIndexes(indexes);

        final List<CompoundUniqueKey> complexUniqueKeyList = new ArrayList<>();

        for (final CompoundUniqueKey complexUniqueKey : getCompoundUniqueKeyList()) {
            complexUniqueKeyList.add(new CopyCompoundUniqueKey(complexUniqueKey, to.getColumns()));
        }

        to.compoundUniqueKeyList = complexUniqueKeyList;

        to.tableViewProperties = getTableViewProperties().clone();

        return to;
    }

    @Override
    public void restructureData(TableView to) {
        final ERTable table = (ERTable) to;

        table.setConstraint(getConstraint());
        table.setPrimaryKeyName(getPrimaryKeyName());
        table.setOption(getOption());

        super.restructureData(to);

        final List<ERIndex> indexes = new ArrayList<>();

        for (final ERIndex fromIndex : getIndexes()) {
            final CopyIndex copyIndex = (CopyIndex) fromIndex;
            final ERIndex restructuredIndex = copyIndex.getRestructuredIndex(table);
            indexes.add(restructuredIndex);
        }
        table.setIndexes(indexes);

        final List<CompoundUniqueKey> complexUniqueKeyList = new ArrayList<>();

        for (final CompoundUniqueKey complexUniqueKey : getCompoundUniqueKeyList()) {
            final CopyCompoundUniqueKey copyComplexUniqueKey = (CopyCompoundUniqueKey) complexUniqueKey;
            if (!copyComplexUniqueKey.isRemoved(getNormalColumns())) {
                final CompoundUniqueKey restructuredComplexUniqueKey = copyComplexUniqueKey.restructure();
                complexUniqueKeyList.add(restructuredComplexUniqueKey);
            }
        }
        table.compoundUniqueKeyList = complexUniqueKeyList;

        table.tableViewProperties = tableViewProperties.clone();
    }

    public int getPrimaryKeySize() {
        int count = 0;

        for (final ERColumn column : columns) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (normalColumn.isPrimaryKey()) {
                    count++;
                }
            }
        }

        return count;
    }

    public List<NormalColumn> getPrimaryKeys() {
        final List<NormalColumn> primaryKeys = new ArrayList<>();

        for (final ERColumn column : columns) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (normalColumn.isPrimaryKey()) {
                    primaryKeys.add(normalColumn);
                }
            }
        }

        return primaryKeys;
    }

    public boolean isReferable() {
        if (getPrimaryKeySize() > 0) {
            return true;
        }

        if (compoundUniqueKeyList.size() > 0) {
            return true;
        }

        for (final ERColumn column : columns) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (normalColumn.isUniqueKey()) {
                    return true;
                }
            }
        }

        return false;
    }

    public ERIndex getIndex(int index) {
        return indexes.get(index);
    }

    public void removeIndex(int index) {
        indexes.remove(index);
    }

    public List<ERIndex> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<ERIndex> indexes) {
        this.indexes = indexes;

        if (getDiagram() != null) {
            firePropertyChange(IndexSet.PROPERTY_CHANGE_INDEXES, null, null);
            getDiagram().getDiagramContents().getIndexSet().update();
        }
    }

    public static boolean isRecursive(TableView source, TableView target) {
        for (final Relationship relation : source.getIncomingRelationshipList()) {
            final TableView temp = relation.getSourceTableView();
            if (temp.equals(source)) {
                continue;
            }

            if (temp.equals(target)) {
                return true;
            }

            if (isRecursive(temp, target)) {
                return true;
            }
        }

        return false;
    }

    public Relationship createRelation() {
        boolean referenceForPK = false;
        CompoundUniqueKey referencedComplexUniqueKey = null;
        NormalColumn referencedColumn = null;
        if (getPrimaryKeySize() > 0) {
            referenceForPK = true;
        } else if (getCompoundUniqueKeyList().size() > 0) {
            referencedComplexUniqueKey = getCompoundUniqueKeyList().get(0);
        } else {
            for (final NormalColumn normalColumn : getNormalColumns()) {
                if (normalColumn.isUniqueKey()) {
                    referencedColumn = normalColumn;
                    break;
                }
            }
        }
        return new Relationship(referenceForPK, referencedComplexUniqueKey, referencedColumn);
    }

    @Override
    public ERTable toMaterialize() {
        return this;
    }

    // ===================================================================================
    //                                                                        TableView ID
    //                                                                        ============
    @Override
    protected String getIdPrefix() {
        return "table";
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public ERTable clone() {
        final ERTable clone = (ERTable) super.clone();
        final TableProperties cloneTableProperties = (TableProperties) getTableViewProperties().clone();
        clone.tableViewProperties = cloneTableProperties;
        return clone;
    }

    @Override
    public String toString() {
        return getPhysicalName();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    @Override
    public String getObjectType() {
        return "table";
    }

    @Override
    public boolean needsUpdateOtherModel() {
        return true;
    }

    @Override
    public int getPersistentOrder() {
        return 2;
    }

    public void setComplexUniqueKeyList(List<CompoundUniqueKey> complexUniqueKeyList) {
        this.compoundUniqueKeyList = complexUniqueKeyList;
    }

    public List<CompoundUniqueKey> getCompoundUniqueKeyList() {
        return compoundUniqueKeyList;
    }

    @Override
    public TableViewProperties getTableViewProperties() {
        this.tableViewProperties =
                DBManagerFactory.getDBManager(getDiagram()).createTableProperties((TableProperties) tableViewProperties);
        return tableViewProperties;
    }

    public TableViewProperties getTableViewProperties(String database) {
        this.tableViewProperties =
                DBManagerFactory.getDBManager(database).createTableProperties((TableProperties) tableViewProperties);
        return tableViewProperties;
    }

    public void setTableViewProperties(TableProperties tableProperties) {
        this.tableViewProperties = tableProperties;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public DiagramSettings getDiagramSettings() {
        return getDiagram().getDiagramContents().getSettings();
    }
}
