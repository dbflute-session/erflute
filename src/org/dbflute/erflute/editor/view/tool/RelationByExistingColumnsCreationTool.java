package org.dbflute.erflute.editor.view.tool;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateRelationshipByExistingColumnsCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.eclipse.gef.tools.ConnectionCreationTool;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class RelationByExistingColumnsCreationTool extends ConnectionCreationTool {

    @Override
    protected boolean handleCreateConnection() {
        try {
            final CreateRelationshipByExistingColumnsCommand command = (CreateRelationshipByExistingColumnsCommand) this.getCommand();
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
            eraseSourceFeedback();
            final CreateRelationshipByExistingColumnsCommand endCommand = (CreateRelationshipByExistingColumnsCommand) this.getCommand();
            if (!endCommand.selectColumns()) {
                return false;
            }
            setCurrentCommand(endCommand);
            executeCurrentCommand();
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
        return true;
    }
}
