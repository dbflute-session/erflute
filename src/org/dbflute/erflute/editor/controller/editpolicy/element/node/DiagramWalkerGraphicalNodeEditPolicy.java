package org.dbflute.erflute.editor.controller.editpolicy.element.node;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.AbstractCreateConnectionCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.CreateCommentConnectionCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.CreateConnectionCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.AbstractCreateRelationshipCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateRelatedTableCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateRelationshipByExistingColumnsCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateRelationshipByNewColumnCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateSelfRelationshipCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.ReconnectSourceCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.ReconnectTargetCommand;
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERTableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.TableViewEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.CommentConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.RelatedTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.RelationByExistingColumns;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.SelfRelation;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

public class DiagramWalkerGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

    @Override
    protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
        final AbstractCreateConnectionCommand command = (AbstractCreateConnectionCommand) request.getStartCommand();
        final DiagramWalkerEditPart targetEditPart = (DiagramWalkerEditPart) request.getTargetEditPart();
        if (command instanceof AbstractCreateRelationshipCommand) {
            if (!(targetEditPart instanceof TableViewEditPart)) {
                return null;
            }
        }
        final String validatedMessage = command.validate();
        if (validatedMessage != null) {
            Activator.showErrorDialog(validatedMessage);
            return null;
        }
        command.setTarget(targetEditPart);
        if (!command.canExecute()) {
            return null;
        }
        return command;
    }

    @Override
    protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
        final EditPart editPart = request.getTargetEditPart();
        final Object object = request.getNewObject();
        if (editPart instanceof ERTableEditPart) {
            final Command command = this.getRelationCreateCommand(request, object);
            if (command != null) {
                return command;
            }
        }
        if (object instanceof CommentConnection) {
            final CommentConnection connection = (CommentConnection) object;
            final CreateConnectionCommand command = new CreateCommentConnectionCommand(connection);
            command.setSource(request.getTargetEditPart());
            request.setStartCommand(command);
            return command;
        }
        return null;
    }

    private Command getRelationCreateCommand(CreateConnectionRequest request, Object object) {
        if (object instanceof Relationship) {
            final Relationship relationship = (Relationship) object;
            final CreateRelationshipByNewColumnCommand command = new CreateRelationshipByNewColumnCommand(relationship);
            final EditPart source = request.getTargetEditPart();
            command.setSource(source);
            final ERTable sourceTable = (ERTable) source.getModel(); // e.g. MEMBER_STATUS
            final Relationship temp = sourceTable.createRelation();
            relationship.setReferenceForPK(temp.isReferenceForPK());
            relationship.setReferencedComplexUniqueKey(temp.getReferredComplexUniqueKey());
            relationship.setReferredSimpleUniqueColumn(temp.getReferredSimpleUniqueColumn());
            request.setStartCommand(command);
            return command;
        } else if (object instanceof RelatedTable) {
            final CreateRelatedTableCommand command = new CreateRelatedTableCommand();
            final ERTableEditPart sourceEditPart = (ERTableEditPart) request.getTargetEditPart();
            command.setSource(sourceEditPart);
            if (sourceEditPart != null) {
                final Point point = sourceEditPart.getFigure().getBounds().getCenter();
                command.setSourcePoint(point.x, point.y);
            }
            request.setStartCommand(command);
            return command;
        } else if (object instanceof SelfRelation) {
            final ERTableEditPart sourceEditPart = (ERTableEditPart) request.getTargetEditPart();
            final ERTable sourceTable = (ERTable) sourceEditPart.getModel();
            final CreateSelfRelationshipCommand command = new CreateSelfRelationshipCommand(sourceTable.createRelation());
            command.setSource(sourceEditPart);
            request.setStartCommand(command);
            return command;
        } else if (object instanceof RelationByExistingColumns) {
            final CreateRelationshipByExistingColumnsCommand command = new CreateRelationshipByExistingColumnsCommand();
            final EditPart source = request.getTargetEditPart();
            command.setSource(source);
            request.setStartCommand(command);
            return command;
        }
        return null;
    }

    @Override
    protected Command getReconnectSourceCommand(ReconnectRequest reconnectrequest) {
        final WalkerConnection connection = (WalkerConnection) reconnectrequest.getConnectionEditPart().getModel();
        if (!(connection instanceof Relationship)) {
            return null;
        }
        final Relationship relation = (Relationship) connection;
        if (relation.getWalkerSource() == relation.getWalkerTarget()) {
            return null;
        }
        final DiagramWalker newSource = (DiagramWalker) reconnectrequest.getTarget().getModel();
        if (!relation.getWalkerSource().equals(newSource)) {
            return null;
        }
        final DiagramWalkerEditPart sourceEditPart = (DiagramWalkerEditPart) reconnectrequest.getConnectionEditPart().getSource();
        final Point location = new Point(reconnectrequest.getLocation());
        final IFigure sourceFigure = sourceEditPart.getFigure();
        sourceFigure.translateToRelative(location);
        int xp = -1;
        int yp = -1;
        final Rectangle bounds = sourceFigure.getBounds();
        final Rectangle centerRectangle =
                new Rectangle(bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 4), bounds.width / 2, bounds.height / 2);
        if (!centerRectangle.contains(location)) {
            final Point point = ERTableEditPart.getIntersectionPoint(location, sourceFigure);
            xp = 100 * (point.x - bounds.x) / bounds.width;
            yp = 100 * (point.y - bounds.y) / bounds.height;
        }
        final ReconnectSourceCommand command = new ReconnectSourceCommand(relation, xp, yp);
        return command;
    }

    @Override
    protected Command getReconnectTargetCommand(ReconnectRequest reconnectrequest) {
        final WalkerConnection connection = (WalkerConnection) reconnectrequest.getConnectionEditPart().getModel();
        if (!(connection instanceof Relationship)) {
            return null;
        }
        final Relationship relation = (Relationship) connection;
        if (relation.getWalkerSource() == relation.getWalkerTarget()) {
            return null;
        }
        final DiagramWalker newTarget = (DiagramWalker) reconnectrequest.getTarget().getModel();
        if (!relation.getWalkerTarget().equals(newTarget)) {
            return null;
        }
        final DiagramWalkerEditPart targetEditPart = (DiagramWalkerEditPart) reconnectrequest.getConnectionEditPart().getTarget();
        final Point location = new Point(reconnectrequest.getLocation());
        final IFigure targetFigure = targetEditPart.getFigure();
        targetFigure.translateToRelative(location);
        int xp = -1;
        int yp = -1;
        final Rectangle bounds = targetFigure.getBounds();
        final Rectangle centerRectangle =
                new Rectangle(bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 4), bounds.width / 2, bounds.height / 2);
        if (!centerRectangle.contains(location)) {
            final Point point = ERTableEditPart.getIntersectionPoint(location, targetFigure);
            xp = 100 * (point.x - bounds.x) / bounds.width;
            yp = 100 * (point.y - bounds.y) / bounds.height;
        }
        final ReconnectTargetCommand command = new ReconnectTargetCommand(relation, xp, yp);
        return command;
    }
}
