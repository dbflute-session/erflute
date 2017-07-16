package org.dbflute.erflute.editor.controller.editpolicy.element.connection;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.DeleteRelationshipCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class RelationEditPolicy extends ConnectionEditPolicy {

    @Override
    protected Command getDeleteCommand(GroupRequest grouprequest) {
        final Relationship relation = (Relationship) getHost().getModel();
        return new DeleteRelationshipCommand(relation, null);
    }
}
