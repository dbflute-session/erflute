package org.dbflute.erflute.editor.view.tool;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateSelfRelationshipCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.swt.SWT;

public class SelfRelationCreationTool extends ConnectionCreationTool {

    @Override
    protected boolean handleButtonDown(int button) {
        if (button == SWT.KeyDown) {
            return handleCreateConnection();
        }

        return super.handleButtonDown(button);
    }

    @Override
    protected boolean handleCreateConnection() {
        final CreateSelfRelationshipCommand command = (CreateSelfRelationshipCommand) getCommand();

        final ERTable target = (ERTable) command.getSourceModel();

        if (!target.isReferable()) {
            Activator.showErrorDialog("error.no.referenceable.column");

            eraseSourceFeedback();

            return false;
        }

        return super.handleCreateConnection();
    }
}
