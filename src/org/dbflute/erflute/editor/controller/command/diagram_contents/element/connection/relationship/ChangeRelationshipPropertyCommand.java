package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;

public class ChangeRelationshipPropertyCommand extends AbstractCommand {

    private final Relationship oldCopyRelation;
    private final Relationship newCopyRelation;
    private final Relationship relation;
    private final TableView oldTargetTable;

    public ChangeRelationshipPropertyCommand(Relationship relation, Relationship newCopyRelation) {
        this.relation = relation;
        this.oldCopyRelation = relation.copy();
        this.newCopyRelation = newCopyRelation;
        this.oldTargetTable = relation.getTargetTableView().copyData();
    }

    @Override
    protected void doExecute() {
        newCopyRelation.restructureRelationData(relation);

        if (newCopyRelation.isReferenceForPK()) {
            relation.setForeignKeyColumnForPK();
        } else if (newCopyRelation.getReferredCompoundUniqueKey() != null) {
            relation.setForeignKeyForComplexUniqueKey(newCopyRelation.getReferredCompoundUniqueKey());
        } else {
            relation.setForeignKeyColumn(newCopyRelation.getReferredSimpleUniqueColumn());
        }
    }

    @Override
    protected void doUndo() {
        oldCopyRelation.restructureRelationData(relation);

        relation.setReferenceForPK(oldCopyRelation.isReferenceForPK());
        relation.setReferredCompoundUniqueKey(oldCopyRelation.getReferredCompoundUniqueKey());
        relation.setReferredSimpleUniqueColumn(oldCopyRelation.getReferredSimpleUniqueColumn());

        oldTargetTable.restructureData(relation.getTargetTableView());
    }
}
