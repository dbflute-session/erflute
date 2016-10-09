package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.gef.EditPart;

public class CreateSelfRelationshipCommand extends AbstractCreateRelationshipCommand {

    private Relationship relation;

    public CreateSelfRelationshipCommand(Relationship relation) {
        super();
        this.relation = relation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSource(EditPart source) {
        this.source = source;
        this.target = source;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        ERDiagramEditPart.setUpdateable(false);

        boolean anotherSelfRelation = false;

        ERTable sourceTable = (ERTable) this.source.getModel();

        for (Relationship otherRelation : sourceTable.getOutgoingRelationshipList()) {
            if (otherRelation.getSource() == otherRelation.getTarget()) {
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

        Bendpoint bendpoint0 = new Bendpoint(rate, rate);
        bendpoint0.setRelative(true);

        int xp = 100 - (rate / 2);
        int yp = 100 - (rate / 2);

        relation.setSourceLocationp(100, yp);
        relation.setTargetLocationp(xp, 100);

        relation.addBendpoint(0, bendpoint0);

        relation.setSource((ERTable) sourceTable);

        ERDiagramEditPart.setUpdateable(true);

        relation.setTargetTableView((ERTable) this.target.getModel());

        sourceTable.setDirty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        ERDiagramEditPart.setUpdateable(false);

        relation.setSource(null);

        ERDiagramEditPart.setUpdateable(true);

        relation.setTargetTableView(null);

        this.relation.removeBendpoint(0);

        ERTable targetTable = (ERTable) this.target.getModel();
        targetTable.setDirty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        return source != null && target != null;
    }

}
