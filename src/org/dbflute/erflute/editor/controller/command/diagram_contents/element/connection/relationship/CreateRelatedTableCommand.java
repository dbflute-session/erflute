package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.TableViewEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;

public class CreateRelatedTableCommand extends AbstractCreateRelationshipCommand {

    private Relationship relation1;
    private Relationship relation2;
    private final ERTable relatedTable;
    private ERDiagram diagram;
    private int sourceX;
    private int sourceY;
    private int targetX;
    private int targetY;

    public CreateRelatedTableCommand() {
        super();

        this.relatedTable = new ERTable();
    }

    public void setSourcePoint(int x, int y) {
        this.sourceX = x;
        this.sourceY = y;
    }

    private void setTargetPoint(int x, int y) {
        this.targetX = x;
        this.targetY = y;
    }

    @Override
    public void setTarget(EditPart target) {
        super.setTarget(target);

        if (target != null) {
            if (target instanceof TableViewEditPart) {
                final TableViewEditPart tableEditPart = (TableViewEditPart) target;

                final Point point = tableEditPart.getFigure().getBounds().getCenter();
                setTargetPoint(point.x, point.y);
            }
        }
    }

    @Override
    protected void doExecute() {
        ERDiagramEditPart.setUpdateable(false);

        init();

        diagram.addNewWalker(relatedTable);

        relation1.setSourceWalker((ERTable) source.getModel());
        relation1.setTargetTableView(relatedTable);

        relation2.setSourceWalker((ERTable) target.getModel());
        relation2.setTargetTableView(relatedTable);

        ERDiagramEditPart.setUpdateable(true);

        diagram.getDiagramContents().getDiagramWalkers().getTableSet().setDirty();
    }

    @Override
    protected void doUndo() {
        ERDiagramEditPart.setUpdateable(false);

        diagram.removeWalker(relatedTable);

        relation1.setSourceWalker(null);
        relation1.setTargetTableView(null);

        relation2.setSourceWalker(null);
        relation2.setTargetTableView(null);

        ERDiagramEditPart.setUpdateable(true);

        diagram.getDiagramContents().getDiagramWalkers().getTableSet().setDirty();
    }

    private void init() {
        final ERTable sourceTable = (ERTable) getSourceModel();

        this.diagram = sourceTable.getDiagram();

        this.relation1 = sourceTable.createRelation();

        final ERTable targetTable = (ERTable) this.getTargetModel();
        this.relation2 = targetTable.createRelation();

        relatedTable.setLocation(new Location((sourceX + targetX - ERTable.DEFAULT_WIDTH) / 2,
                (sourceY + targetY - ERTable.DEFAULT_HEIGHT) / 2, ERTable.DEFAULT_WIDTH, ERTable.DEFAULT_HEIGHT));

        relatedTable.setLogicalName(ERTable.NEW_LOGICAL_NAME);
        relatedTable.setPhysicalName(ERTable.NEW_PHYSICAL_NAME);

    }

    @Override
    public boolean canExecute() {
        if (!super.canExecute()) {
            return false;
        }

        if (!(getSourceModel() instanceof ERTable) || !(getTargetModel() instanceof ERTable)) {
            return false;
        }

        return true;
    }
}
