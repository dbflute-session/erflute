package org.dbflute.erflute.editor.view.tool;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateRelationshipByExistingColumnsCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.eclipse.gef.tools.ConnectionCreationTool;

public class RelationByExistingColumnsCreationTool extends ConnectionCreationTool {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleCreateConnection() {
        try {
            CreateRelationshipByExistingColumnsCommand command = (CreateRelationshipByExistingColumnsCommand) this.getCommand();

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

            this.eraseSourceFeedback();
            CreateRelationshipByExistingColumnsCommand endCommand = (CreateRelationshipByExistingColumnsCommand) this.getCommand();

            if (!endCommand.selectColumns()) {
                return false;
            }

            this.setCurrentCommand(endCommand);
            this.executeCurrentCommand();

        } catch (Exception e) {
            Activator.showExceptionDialog(e);
        }

        return true;
    }

}
