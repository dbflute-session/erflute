package org.dbflute.erflute.editor.controller.editpolicy.element.connection;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.DeleteConnectionCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class CommentConnectionEditPolicy extends ConnectionEditPolicy {

    @Override
    protected Command getDeleteCommand(GroupRequest grouprequest) {
        WalkerConnection connection = (WalkerConnection) this.getHost().getModel();

        return new DeleteConnectionCommand(connection);
    }
}
