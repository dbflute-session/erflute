package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.controller.editpart.element.connection.RelationEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;

public class MoveRelationBendpointCommand extends AbstractCommand {

    private final RelationEditPart editPart;
    private final Bendpoint bendPoint;
    private Bendpoint oldBendpoint;
    private final int index;

    public MoveRelationBendpointCommand(RelationEditPart editPart, int x, int y, int index) {
        this.editPart = editPart;
        this.bendPoint = new Bendpoint(x, y);
        this.index = index;
    }

    @Override
    protected void doExecute() {
        final Relationship relation = (Relationship) editPart.getModel();
        final boolean relative = relation.getBendpoints().get(0).isRelative();
        if (relative) {
            this.oldBendpoint = relation.getBendpoints().get(0);

            bendPoint.setRelative(true);

            final float rateX = (100f - (bendPoint.getX() / 2)) / 100;
            final float rateY = (100f - (bendPoint.getY() / 2)) / 100;

            relation.setSourceLocationp(100, (int) (100 * rateY));
            relation.setTargetLocationp((int) (100 * rateX), 100);

            relation.setParentMove();

            relation.replaceBendpoint(0, bendPoint);
        } else {
            this.oldBendpoint = relation.getBendpoints().get(index);
            relation.replaceBendpoint(index, bendPoint);
        }
    }

    @Override
    protected void doUndo() {
        final Relationship relation = (Relationship) editPart.getModel();
        final boolean relative = relation.getBendpoints().get(0).isRelative();
        if (relative) {
            final float rateX = (100f - (oldBendpoint.getX() / 2)) / 100;
            final float rateY = (100f - (oldBendpoint.getY() / 2)) / 100;

            relation.setSourceLocationp(100, (int) (100 * rateY));
            relation.setTargetLocationp((int) (100 * rateX), 100);

            relation.setParentMove();

            relation.replaceBendpoint(0, oldBendpoint);
        } else {
            relation.replaceBendpoint(index, oldBendpoint);
        }
    }
}
