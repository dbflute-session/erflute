package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.gef.EditPart;

public class CreateSelfRelationshipCommand extends AbstractCreateRelationshipCommand {

    private final Relationship relation;

    public CreateSelfRelationshipCommand(Relationship relation) {
        super();
        this.relation = relation;
    }

    @Override
    public void setSource(EditPart source) {
        this.source = source;
        this.target = source;
    }

    @Override
    protected void doExecute() {
        ERDiagramEditPart.setUpdateable(false);

        boolean anotherSelfRelation = false;

        final ERTable sourceTable = ((ERTable) source.getModel()).toMaterialize();

        for (final Relationship otherRelation : sourceTable.getOutgoingRelationshipList()) {
            if (otherRelation.getSourceWalker() == otherRelation.getTargetWalker()) {
                anotherSelfRelation = true;
                break;
            }
        }

        int rate = 0;

        if (anotherSelfRelation) {
            rate = 50;
        } else {
            rate = 100;
        }

        final Bendpoint bendpoint0 = new Bendpoint(rate, rate);
        bendpoint0.setRelative(true);

        final int xp = 100 - (rate / 2);
        final int yp = 100 - (rate / 2);

        relation.setSourceLocationp(100, yp);
        relation.setTargetLocationp(xp, 100);

        relation.addBendpoint(0, bendpoint0);

        relation.setSourceWalker(sourceTable);

        ERDiagramEditPart.setUpdateable(true);

        relation.setTargetTableView(((ERTable) target.getModel()).toMaterialize());

        sourceTable.setDirty();
        ERModelUtil.refreshDiagram(sourceTable.getDiagram(), sourceTable);
    }

    @Override
    protected void doUndo() {
        ERDiagramEditPart.setUpdateable(false);

        relation.delete(true, null);

        ERDiagramEditPart.setUpdateable(true);

        final ERTable targetTable = ((ERTable) target.getModel()).toMaterialize();
        targetTable.setDirty();
        ERModelUtil.refreshDiagram(targetTable.getDiagram(), targetTable);
    }

    @Override
    public boolean canExecute() {
        return source != null && target != null;
    }
}
