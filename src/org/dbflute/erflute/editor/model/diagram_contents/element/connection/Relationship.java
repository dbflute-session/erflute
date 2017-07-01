package org.dbflute.erflute.editor.model.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class Relationship extends WalkerConnection implements Comparable<Relationship> {

    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String foreignKeyName; // null allowed (not required)
    private String onUpdateAction;
    private String onDeleteAction;
    private String parentCardinality;
    private String childCardinality;
    private boolean referenceForPK;
    private CompoundUniqueKey referredCompoundUniqueKey;
    private NormalColumn referredSimpleUniqueColumn;
    private int sourceXp;
    private int sourceYp;
    private int targetXp;
    private int targetYp;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public Relationship() {
        this(false, null, null);
    }

    public Relationship(boolean referenceForPK, CompoundUniqueKey referredComplexUniqueKey, NormalColumn referredSimpleUniqueColumn) {
        this.onUpdateAction = "RESTRICT";
        this.onDeleteAction = "RESTRICT";

        this.referenceForPK = referenceForPK;
        this.referredCompoundUniqueKey = referredComplexUniqueKey;
        this.referredSimpleUniqueColumn = referredSimpleUniqueColumn;

        this.sourceXp = -1;
        this.sourceYp = -1;
        this.targetXp = -1;
        this.targetYp = -1;

        this.parentCardinality = "1";
        this.childCardinality = "0..n";
    }

    // ===================================================================================
    //                                                                           TableView
    //                                                                           =========
    public TableView getSourceTableView() {
        return (TableView) getWalkerSource();
    }

    public TableView getTargetTableView() {
        return (TableView) getWalkerTarget();
    }

    public void setTargetTableView(TableView target) {
        setTargetTableView(target, null);
    }

    public void setTargetTableView(TableView target, List<NormalColumn> foreignKeyColumnList) {
        if (getTargetTableView() != null) {
            removeAllForeignKey();
        }
        super.setTargetWalker(target);
        if (target != null) {
            final TableView sourceTable = (TableView) getWalkerSource();
            int i = 0;
            if (isReferenceForPK()) {
                for (final NormalColumn sourceColumn : ((ERTable) sourceTable).getPrimaryKeys()) {
                    final NormalColumn foreignKeyColumn = createForeignKeyColumn(sourceColumn, foreignKeyColumnList, i++);
                    target.addColumn(foreignKeyColumn);
                }
            } else if (referredCompoundUniqueKey != null) {
                for (final NormalColumn sourceColumn : referredCompoundUniqueKey.getColumnList()) {
                    final NormalColumn foreignKeyColumn = createForeignKeyColumn(sourceColumn, foreignKeyColumnList, i++);
                    target.addColumn(foreignKeyColumn);
                }
            } else {
                for (final NormalColumn sourceColumn : sourceTable.getNormalColumns()) {
                    if (sourceColumn == referredSimpleUniqueColumn) {
                        final NormalColumn foreignKeyColumn = createForeignKeyColumn(sourceColumn, foreignKeyColumnList, i++);
                        target.addColumn(foreignKeyColumn);
                        break;
                    }
                }
            }
        }
    }

    private NormalColumn createForeignKeyColumn(NormalColumn referencedColumn, List<NormalColumn> foreignKeyColumnList, int index) {
        final NormalColumn foreignKeyColumn = new NormalColumn(referencedColumn, referencedColumn, this, false);
        if (foreignKeyColumnList != null) {
            final NormalColumn data = foreignKeyColumnList.get(index);
            data.copyForeikeyData(foreignKeyColumn);
        }
        return foreignKeyColumn;
    }

    // ===================================================================================
    //                                                                           FK Column
    //                                                                           =========
    public void setTargetWithoutForeignKey(TableView target) {
        super.setTargetWalker(target);
    }

    public void setTargetTableWithExistingColumns(ERTable target, List<NormalColumn> referencedColumnList,
            List<NormalColumn> foreignKeyColumnList) {
        super.setTargetWalker(target);
    }

    public List<NormalColumn> getForeignKeyColumns() {
        final List<NormalColumn> columnList = new ArrayList<>();
        if (getTargetTableView() != null) {
            for (final NormalColumn column : getTargetTableView().getNormalColumns()) {
                if (column.isForeignKey()) {
                    final NormalColumn foreignKeyColumn = column;
                    for (final Relationship relation : foreignKeyColumn.getRelationshipList()) {
                        if (relation == this) {
                            columnList.add(column);
                            break;
                        }
                    }
                }
            }
        }
        return columnList;
    }

    // ===================================================================================
    //                                                                              Delete
    //                                                                              ======
    public void delete(boolean removeForeignKey, Dictionary dictionary) {
        super.delete();
        for (final NormalColumn foreignKeyColumn : getForeignKeyColumns()) {
            foreignKeyColumn.removeReference(this);
            if (removeForeignKey) {
                if (foreignKeyColumn.getRelationshipList().isEmpty()) {
                    getTargetTableView().removeColumn(foreignKeyColumn);
                }
            } else {
                dictionary.add(foreignKeyColumn);
            }
        }
    }

    public Relationship copy() {
        final Relationship to =
                new Relationship(isReferenceForPK(), getReferredCompoundUniqueKey(), getReferredSimpleUniqueColumn());

        to.setForeignKeyName(getForeignKeyName());
        to.setOnDeleteAction(getOnDeleteAction());
        to.setOnUpdateAction(getOnUpdateAction());
        to.setChildCardinality(getChildCardinality());
        to.setParentCardinality(getParentCardinality());

        to.sourceWalker = getSourceTableView();
        to.targetWalker = getTargetTableView();

        return to;
    }

    public Relationship restructureRelationData(Relationship to) {
        to.setForeignKeyName(getForeignKeyName());
        to.setOnDeleteAction(getOnDeleteAction());
        to.setOnUpdateAction(getOnUpdateAction());
        to.setChildCardinality(getChildCardinality());
        to.setParentCardinality(getParentCardinality());

        return to;
    }

    public boolean isReferenceForPK() {
        return referenceForPK;
    }

    public void setReferenceForPK(boolean referenceForPK) {
        this.referenceForPK = referenceForPK;
    }

    public void setForeignKeyColumn(NormalColumn sourceColumn) {
        if (referredSimpleUniqueColumn == sourceColumn) {
            return;
        }
        removeAllForeignKey();
        final NormalColumn foreignKeyColumn = new NormalColumn(sourceColumn, sourceColumn, this, false);
        getTargetTableView().addColumn(foreignKeyColumn);
        this.referenceForPK = false;
        this.referredSimpleUniqueColumn = sourceColumn;
        this.referredCompoundUniqueKey = null;
    }

    public void setForeignKeyForComplexUniqueKey(CompoundUniqueKey compoundUniqueKey) {
        if (referredCompoundUniqueKey == compoundUniqueKey) {
            return;
        }
        removeAllForeignKey();
        for (final NormalColumn sourceColumn : compoundUniqueKey.getColumnList()) {
            final NormalColumn foreignKeyColumn = new NormalColumn(sourceColumn, sourceColumn, this, false);
            getTargetTableView().addColumn(foreignKeyColumn);
        }
        this.referenceForPK = false;
        this.referredSimpleUniqueColumn = null;
        this.referredCompoundUniqueKey = compoundUniqueKey;
    }

    public void setForeignKeyColumnForPK() {
        if (referenceForPK) {
            return;
        }
        removeAllForeignKey();
        for (final NormalColumn sourceColumn : ((ERTable) getSourceTableView()).getPrimaryKeys()) {
            final NormalColumn foreignKeyColumn = new NormalColumn(sourceColumn, sourceColumn, this, false);
            getTargetTableView().addColumn(foreignKeyColumn);
        }
        this.referenceForPK = true;
        this.referredSimpleUniqueColumn = null;
        this.referredCompoundUniqueKey = null;
    }

    private void removeAllForeignKey() {
        for (final Iterator<ERColumn> iter = getTargetTableView().getColumns().iterator(); iter.hasNext();) {
            final ERColumn column = iter.next();
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;
                if (normalColumn.isForeignKey()) {
                    if (normalColumn.getRelationshipList().size() == 1 && normalColumn.getRelationshipList().get(0) == this) {
                        iter.remove();
                    }
                }
            }
        }
        getTargetTableView().setDirty();
    }

    public String buildRelationshipId() { // for complete state e.g. when writing
        final TableView targetTable = getTargetTableView();
        final List<NormalColumn> foreignKeyColumns = getForeignKeyColumns();
        final List<String> physicalColumnNameList = new ArrayList<>();
        for (final NormalColumn column : foreignKeyColumns) {
            physicalColumnNameList.add(column.getPhysicalName());
        }
        return doBuildRelationshipId(targetTable, physicalColumnNameList);
    }

    public String buildRelationshipId(TableView targetTable, List<String> physicalColumnNameList) { // for making state e.g. when reading
        return doBuildRelationshipId(targetTable, physicalColumnNameList);
    }

    private String doBuildRelationshipId(TableView targetTable, List<String> physicalColumnNameList) {
        if (Srl.is_NotNull_and_NotTrimmedEmpty(foreignKeyName)) { // e.g. FK_MEMBER_MEMBER_STATUS
            return foreignKeyName; // should be unique
        } else { // when no name FK
            // while FK constraint name should be required as possible
            final String pk = referenceForPK ? "PK" : "UQ"; // to be unique
            final StringBuilder sb = new StringBuilder();
            for (final String fkColumn : physicalColumnNameList) {
                if (sb.length() > 0) {
                    sb.append("/");
                }
                sb.append(fkColumn);
            }
            return "relationship." + targetTable.buildTableViewId() + "." + "[" + sb.toString() + "]." + pk;
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public Relationship clone() {
        final Relationship clone = (Relationship) super.clone();
        return clone;
    }

    @Override
    public int compareTo(Relationship otherRelation) {
        return getTargetTableView().compareTo(otherRelation.getTargetTableView());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + foreignKeyName + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getForeignKeyName() {
        return foreignKeyName;
    }

    public void setForeignKeyName(String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
    }

    public String getOnDeleteAction() {
        return onDeleteAction;
    }

    public void setOnDeleteAction(String onDeleteAction) {
        this.onDeleteAction = onDeleteAction;
    }

    public String getOnUpdateAction() {
        return onUpdateAction;
    }

    public void setOnUpdateAction(String onUpdateAction) {
        this.onUpdateAction = onUpdateAction;
    }

    public String getChildCardinality() {
        return childCardinality;
    }

    public void setChildCardinality(String childCardinality) {
        this.childCardinality = childCardinality;
        firePropertyChange(WalkerConnection.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE, null, null);
    }

    public String getParentCardinality() {
        return parentCardinality;
    }

    public void setParentCardinality(String parentCardinality) {
        this.parentCardinality = parentCardinality;
        firePropertyChange(WalkerConnection.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE, null, null);
    }

    public void setReferredCompoundUniqueKey(CompoundUniqueKey referredCompoundUniqueKey) {
        this.referredCompoundUniqueKey = referredCompoundUniqueKey;
    }

    public CompoundUniqueKey getReferredCompoundUniqueKey() {
        return referredCompoundUniqueKey;
    }

    public void setReferredSimpleUniqueColumn(NormalColumn referredSimpleUniqueColumn) {
        this.referredSimpleUniqueColumn = referredSimpleUniqueColumn;
    }

    public NormalColumn getReferredSimpleUniqueColumn() {
        return referredSimpleUniqueColumn;
    }

    public int getSourceXp() {
        return sourceXp;
    }

    public void setSourceLocationp(int sourceXp, int sourceYp) {
        this.sourceXp = sourceXp;
        this.sourceYp = sourceYp;
    }

    public int getSourceYp() {
        return sourceYp;
    }

    public int getTargetXp() {
        return targetXp;
    }

    public void setTargetLocationp(int targetXp, int targetYp) {
        this.targetXp = targetXp;
        this.targetYp = targetYp;
    }

    public int getTargetYp() {
        return targetYp;
    }

    public boolean isReferedStrictly() {
        for (final NormalColumn column : getForeignKeyColumns()) {
            if (column.isReferedStrictly()) {
                return true;
            }
        }

        return false;
    }

    public DiagramSettings getDiagramSettings() {
        return getSourceTableView().getDiagram().getDiagramContents().getSettings();
    }
}
