package org.dbflute.erflute.editor.view.tool;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateRelationshipByNewColumnCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.eclipse.gef.tools.ConnectionCreationTool;

public class RelationCreationTool extends ConnectionCreationTool {

    @Override
    protected boolean handleCreateConnection() {
        final CreateRelationshipByNewColumnCommand command = (CreateRelationshipByNewColumnCommand) this.getCommand();

        if (command == null) {
            return false;
        }

        final TableView source = (TableView) command.getSourceModel();
        final TableView target = (TableView) command.getTargetModel();

        if (ERTable.isRecursive(source, target)) {
            Activator.showErrorDialog("error.recursive.relation");

            this.eraseSourceFeedback();

            return false;
        }

        return super.handleCreateConnection();
    }
}
