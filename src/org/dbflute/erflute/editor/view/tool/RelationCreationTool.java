package org.dbflute.erflute.editor.view.tool;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateRelationshipByNewColumnCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.eclipse.gef.tools.ConnectionCreationTool;

public class RelationCreationTool extends ConnectionCreationTool {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleCreateConnection() {
        CreateRelationshipByNewColumnCommand command = (CreateRelationshipByNewColumnCommand) this.getCommand();

        if (command == null) {
            return false;
        }

        TableView source = (TableView) command.getSourceModel();
        TableView target = (TableView) command.getTargetModel();

        if (ERTable.isRecursive(source, target)) {
            Activator.showErrorDialog("error.recursive.relation");

            this.eraseSourceFeedback();

            return false;
        }

        return super.handleCreateConnection();
    }

}
