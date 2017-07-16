package org.dbflute.erflute.editor.view.tool;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateRelatedTableCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.gef.tools.ConnectionCreationTool;

public class RelatedTableCreationTool extends ConnectionCreationTool {

    @Override
    protected boolean handleCreateConnection() {
        final CreateRelatedTableCommand command = (CreateRelatedTableCommand) getCommand();

        if (command != null) {
            final ERTable target = (ERTable) command.getTargetModel();

            if (!target.isReferable()) {
                Activator.showErrorDialog("error.no.referenceable.column");

                eraseSourceFeedback();

                return false;
            }
        }

        return super.handleCreateConnection();
    }
}
