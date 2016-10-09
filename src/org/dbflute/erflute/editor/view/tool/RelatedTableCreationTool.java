package org.dbflute.erflute.editor.view.tool;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateRelatedTableCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.gef.tools.ConnectionCreationTool;

public class RelatedTableCreationTool extends ConnectionCreationTool {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleCreateConnection() {
        CreateRelatedTableCommand command = (CreateRelatedTableCommand) this.getCommand();

        if (command != null) {
            ERTable target = (ERTable) command.getTargetModel();

            if (!target.isReferable()) {
                Activator.showErrorDialog("error.no.referenceable.column");

                this.eraseSourceFeedback();

                return false;
            }
        }

        return super.handleCreateConnection();
    }

}
