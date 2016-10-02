package org.dbflute.erflute.editor.model.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class Relationship extends ConnectionElement implements Comparable<Relationship> {

    private static final long serialVersionUID = 4456694342537711599L;

    private String name;
    private String onUpdateAction;
    private String onDeleteAction;
    private String parentCardinality;
    private String childCardinality;
    private boolean referenceForPK;
    private ComplexUniqueKey referencedComplexUniqueKey;
    private NormalColumn referencedColumn;
    private int sourceXp;
    private int sourceYp;
    private int targetXp;
    private int targetYp;

    public Relationship() {
        this(false, null, null);
    }

    public Relationship(boolean referenceForPK, ComplexUniqueKey referencedComplexUniqueKey, NormalColumn referencedColumn) {
        this.onUpdateAction = "RESTRICT";
        this.onDeleteAction = "RESTRICT";

        this.referenceForPK = referenceForPK;
        this.referencedComplexUniqueKey = referencedComplexUniqueKey;
        this.referencedColumn = referencedColumn;

        this.sourceXp = -1;
        this.sourceYp = -1;
        this.targetXp = -1;
        this.targetYp = -1;

        this.parentCardinality = "1";
        this.childCardinality = "0..n";
    }

    public TableView getSourceTableView() {
        return (TableView) this.getSource();
    }

    public TableView getTargetTableView() {
        return (TableView) this.getTarget();
    }

    public void setTargetTableView(TableView target) {
        this.setTargetTableView(target, null);
    }

    public void setTargetTableView(TableView target, List<NormalColumn> foreignKeyColumnList) {

        if (this.getTargetTableView() != null) {
            removeAllForeignKey();
        }

        super.setTarget(target);

        if (target != null) {
            final TableView sourceTable = (TableView) this.getSource();

            int i = 0;

            if (this.isReferenceForPK()) {
                for (final NormalColumn sourceColumn : ((ERTable) sourceTable).getPrimaryKeys()) {
                    final NormalColumn foreignKeyColumn = this.createForeiKeyColumn(sourceColumn, foreignKeyColumnList, i++);

                    target.addColumn(foreignKeyColumn);
                }

            } else if (this.referencedComplexUniqueKey != null) {
                for (final NormalColumn sourceColumn : referencedComplexUniqueKey.getColumnList()) {
                    final NormalColumn foreignKeyColumn = this.createForeiKeyColumn(sourceColumn, foreignKeyColumnList, i++);

                    target.addColumn(foreignKeyColumn);
                }

            } else {
                for (final NormalColumn sourceColumn : sourceTable.getNormalColumns()) {
                    if (sourceColumn == this.referencedColumn) {
                        final NormalColumn foreignKeyColumn = this.createForeiKeyColumn(sourceColumn, foreignKeyColumnList, i++);

                        target.addColumn(foreignKeyColumn);
                        break;
                    }
                }
            }
        }

        this.firePropertyChange("target", null, target);
    }

    private NormalColumn createForeiKeyColumn(NormalColumn referencedColumn, List<NormalColumn> foreignKeyColumnList, int index) {
        final NormalColumn foreignKeyColumn = new NormalColumn(referencedColumn, referencedColumn, this, false);

        if (foreignKeyColumnList != null) {
            final NormalColumn data = foreignKeyColumnList.get(index);
            data.copyForeikeyData(foreignKeyColumn);
        }

        return foreignKeyColumn;
    }

    public void setTargetWithoutForeignKey(TableView target) {
        super.setTarget(target);
    }

    public void setTargetTableWithExistingColumns(ERTable target, List<NormalColumn> referencedColumnList,
            List<NormalColumn> foreignKeyColumnList) {

        super.setTarget(target);

        this.firePropertyChange("target", null, target);
    }

    public void delete(boolean removeForeignKey, Dictionary dictionary) {
        super.delete();

        for (final NormalColumn foreignKeyColumn : this.getForeignKeyColumns()) {
            foreignKeyColumn.removeReference(this);

            if (removeForeignKey) {
                if (foreignKeyColumn.getRelationshipList().isEmpty()) {
                    this.getTargetTableView().removeColumn(foreignKeyColumn);
                }

            } else {
                dictionary.add(foreignKeyColumn);
            }
        }
    }

    public List<NormalColumn> getForeignKeyColumns() {
        final List<NormalColumn> list = new ArrayList<NormalColumn>();

        if (this.getTargetTableView() != null) {
            for (final NormalColumn column : this.getTargetTableView().getNormalColumns()) {
                if (column.isForeignKey()) {
                    final NormalColumn foreignKeyColumn = column;
                    for (final Relationship relation : foreignKeyColumn.getRelationshipList()) {
                        if (relation == this) {
                            list.add(column);
                            break;
                        }
                    }
                }
            }
        }

        return list;
    }

    public String getName() {
        return name;
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
        return this.childCardinality;
    }

    public void setChildCardinality(String childCardinality) {
        this.childCardinality = childCardinality;
        firePropertyChange(ConnectionElement.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE, null, null);
    }

    public String getParentCardinality() {
        return parentCardinality;
    }

    public void setParentCardinality(String parentCardinality) {
        this.parentCardinality = parentCardinality;
        firePropertyChange(ConnectionElement.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE, null, null);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Relationship copy() {
        final Relationship to = new Relationship(this.isReferenceForPK(), this.getReferencedComplexUniqueKey(), this.getReferencedColumn());

        to.setName(this.getName());
        to.setOnDeleteAction(this.getOnDeleteAction());
        to.setOnUpdateAction(this.getOnUpdateAction());
        to.setChildCardinality(this.getChildCardinality());
        to.setParentCardinality(this.getParentCardinality());

        to.source = this.getSourceTableView();
        to.target = this.getTargetTableView();

        return to;
    }

    public Relationship restructureRelationData(Relationship to) {
        to.setName(this.getName());
        to.setOnDeleteAction(this.getOnDeleteAction());
        to.setOnUpdateAction(this.getOnUpdateAction());
        to.setChildCardinality(this.getChildCardinality());
        to.setParentCardinality(this.getParentCardinality());

        return to;
    }

    public boolean isReferenceForPK() {
        return this.referenceForPK;
    }

    public void setReferenceForPK(boolean referenceForPK) {
        this.referenceForPK = referenceForPK;
    }

    public void setForeignKeyColumn(NormalColumn sourceColumn) {
        if (this.referencedColumn == sourceColumn) {
            return;
        }

        this.removeAllForeignKey();

        final NormalColumn foreignKeyColumn = new NormalColumn(sourceColumn, sourceColumn, this, false);

        this.getTargetTableView().addColumn(foreignKeyColumn);

        this.referenceForPK = false;
        this.referencedColumn = sourceColumn;
        this.referencedComplexUniqueKey = null;
    }

    public void setForeignKeyForComplexUniqueKey(ComplexUniqueKey complexUniqueKey) {
        if (this.referencedComplexUniqueKey == complexUniqueKey) {
            return;
        }

        this.removeAllForeignKey();

        for (final NormalColumn sourceColumn : complexUniqueKey.getColumnList()) {
            final NormalColumn foreignKeyColumn = new NormalColumn(sourceColumn, sourceColumn, this, false);

            this.getTargetTableView().addColumn(foreignKeyColumn);
        }

        this.referenceForPK = false;
        this.referencedColumn = null;
        this.referencedComplexUniqueKey = complexUniqueKey;
    }

    public void setForeignKeyColumnForPK() {
        if (this.referenceForPK) {
            return;
        }

        this.removeAllForeignKey();

        for (final NormalColumn sourceColumn : ((ERTable) this.getSourceTableView()).getPrimaryKeys()) {
            final NormalColumn foreignKeyColumn = new NormalColumn(sourceColumn, sourceColumn, this, false);

            this.getTargetTableView().addColumn(foreignKeyColumn);
        }

        this.referenceForPK = true;
        this.referencedColumn = null;
        this.referencedComplexUniqueKey = null;
    }

    private void removeAllForeignKey() {
        for (final Iterator iter = this.getTargetTableView().getColumns().iterator(); iter.hasNext();) {
            final ERColumn column = (ERColumn) iter.next();

            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (normalColumn.isForeignKey()) {
                    if (normalColumn.getRelationshipList().size() == 1 && normalColumn.getRelationshipList().get(0) == this) {
                        iter.remove();
                    }
                }
            }
        }

        this.getTargetTableView().setDirty();
    }

    public void setReferencedColumn(NormalColumn referencedColumn) {
        this.referencedColumn = referencedColumn;
    }

    public NormalColumn getReferencedColumn() {
        return this.referencedColumn;
    }

    public void setReferencedComplexUniqueKey(ComplexUniqueKey referencedComplexUniqueKey) {
        this.referencedComplexUniqueKey = referencedComplexUniqueKey;
    }

    public ComplexUniqueKey getReferencedComplexUniqueKey() {
        return this.referencedComplexUniqueKey;
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
        for (final NormalColumn column : this.getForeignKeyColumns()) {
            if (column.isReferedStrictly()) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship clone() {
        final Relationship clone = (Relationship) super.clone();

        return clone;
    }

    @Override
    public int compareTo(Relationship otherRelation) {
        return this.getTargetTableView().compareTo(otherRelation.getTargetTableView());
    }
}
