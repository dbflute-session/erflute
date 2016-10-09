package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import java.util.List;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.fkname.DefaultForeignKeyNameProvider;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class CreateRelationshipByNewColumnCommand extends AbstractCreateRelationshipCommand {

    private final Relationship relationship;
    private final List<NormalColumn> foreignKeyColumnList;

    public CreateRelationshipByNewColumnCommand(Relationship relation) {
        this(relation, null);
    }

    public CreateRelationshipByNewColumnCommand(Relationship relation, List<NormalColumn> foreignKeyColumnList) {
        super();
        this.relationship = relation;
        this.foreignKeyColumnList = foreignKeyColumnList;
    }

    @Override
    protected void doExecute() {
        ERDiagramEditPart.setUpdateable(false);
        final TableView sourceTable = (TableView) source.getModel();
        final TableView targetTable = (TableView) target.getModel();
        relationship.setSource(sourceTable);
        ERDiagramEditPart.setUpdateable(true);
        relationship.setTargetTableView(targetTable, this.foreignKeyColumnList);
        if (relationship.getSource() instanceof ERTable || relationship.getTarget() instanceof ERTable) {
            final ERModelSet modelSet = this.relationship.getSource().getDiagram().getDiagramContents().getModelSet();
            modelSet.createRelation(relationship);
        }
        final String foreignKeyName = provideDefaultForeignKeyName(sourceTable, targetTable);
        relationship.setForeignKeyName(foreignKeyName);
    }

    public String provideDefaultForeignKeyName(TableView sourceTable, TableView targetTable) {
        return new DefaultForeignKeyNameProvider().provide(sourceTable, targetTable);
    }

    @Override
    protected void doUndo() {
        ERDiagramEditPart.setUpdateable(false);
        relationship.setSource(null);
        ERDiagramEditPart.setUpdateable(true);
        relationship.setTargetTableView(null);
        final TableView targetTable = (TableView) this.target.getModel();
        targetTable.setDirty();
    }
}
