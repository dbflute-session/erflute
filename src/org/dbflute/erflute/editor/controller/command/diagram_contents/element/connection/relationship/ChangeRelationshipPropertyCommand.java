package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;

public class ChangeRelationshipPropertyCommand extends AbstractCommand {

    private Relationship oldCopyRelation;

    private Relationship newCopyRelation;

    private Relationship relation;

    private TableView oldTargetTable;

    public ChangeRelationshipPropertyCommand(Relationship relation, Relationship newCopyRelation) {
        this.relation = relation;
        this.oldCopyRelation = relation.copy();
        this.newCopyRelation = newCopyRelation;

        this.oldTargetTable = relation.getTargetTableView().copyData();
    }

    @Override
    protected void doExecute() {
        this.newCopyRelation.restructureRelationData(this.relation);

        if (this.newCopyRelation.isReferenceForPK()) {
            this.relation.setForeignKeyColumnForPK();

        } else if (this.newCopyRelation.getReferredCompoundUniqueKey() != null) {
            this.relation.setForeignKeyForComplexUniqueKey(this.newCopyRelation.getReferredCompoundUniqueKey());

        } else {
            this.relation.setForeignKeyColumn(this.newCopyRelation.getReferredSimpleUniqueColumn());
        }
    }

    @Override
    protected void doUndo() {
        this.oldCopyRelation.restructureRelationData(this.relation);

        this.relation.setReferenceForPK(this.oldCopyRelation.isReferenceForPK());
        this.relation.setReferredCompoundUniqueKey(this.oldCopyRelation.getReferredCompoundUniqueKey());
        this.relation.setReferredSimpleUniqueColumn(this.oldCopyRelation.getReferredSimpleUniqueColumn());

        this.oldTargetTable.restructureData(this.relation.getTargetTableView());
    }
}
