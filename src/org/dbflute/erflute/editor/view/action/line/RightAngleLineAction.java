package org.dbflute.erflute.editor.view.action.line;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.RightAngleLineCommand;
import org.dbflute.erflute.editor.controller.editpart.element.connection.RelationEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.IResizable;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.view.action.AbstractBaseSelectionAction;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.widgets.Event;

public class RightAngleLineAction extends AbstractBaseSelectionAction {

    public static final String ID = RightAngleLineAction.class.getName();

    public RightAngleLineAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.right.angle.line"), editor);
    }

    @Override
    protected List<Command> getCommand(EditPart editPart, Event event) {
        final List<Command> commandList = new ArrayList<>();
        if (editPart instanceof IResizable) {
            final DiagramWalkerEditPart nodeElementEditPart = (DiagramWalkerEditPart) editPart;
            for (final Object obj : nodeElementEditPart.getSourceConnections()) {
                final AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) obj;
                if (connectionEditPart.getSource() != connectionEditPart.getTarget()) {
                    commandList.add(getCommand(connectionEditPart));
                }
            }
        } else if (editPart instanceof AbstractConnectionEditPart) {
            final AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) editPart;
            if (connectionEditPart.getSource() != connectionEditPart.getTarget()) {
                commandList.add(getCommand(connectionEditPart));
            }
        }
        return commandList;
    }

    public static Command getCommand(AbstractConnectionEditPart connectionEditPart) {
        int sourceX = -1;
        int sourceY = -1;
        int targetX = -1;
        int targetY = -1;
        if (connectionEditPart instanceof RelationEditPart) {
            final RelationEditPart relationEditPart = (RelationEditPart) connectionEditPart;
            final Relationship relation = (Relationship) relationEditPart.getModel();
            if (relation.getSourceXp() != -1) {
                final NodeEditPart editPart = (NodeEditPart) relationEditPart.getSource();
                final Rectangle bounds = editPart.getFigure().getBounds();
                sourceX = bounds.x + (bounds.width * relation.getSourceXp() / 100);
                sourceY = bounds.y + (bounds.height * relation.getSourceYp() / 100);
            }
            if (relation.getTargetXp() != -1) {
                final NodeEditPart editPart = (NodeEditPart) relationEditPart.getTarget();
                final Rectangle bounds = editPart.getFigure().getBounds();
                targetX = bounds.x + (bounds.width * relation.getTargetXp() / 100);
                targetY = bounds.y + (bounds.height * relation.getTargetYp() / 100);
            }
        }
        if (sourceX == -1) {
            final DiagramWalkerEditPart sourceEditPart = (DiagramWalkerEditPart) connectionEditPart.getSource();
            final Point sourcePoint = sourceEditPart.getFigure().getBounds().getCenter();
            sourceX = sourcePoint.x;
            sourceY = sourcePoint.y;
        }
        if (targetX == -1) {
            final DiagramWalkerEditPart targetEditPart = (DiagramWalkerEditPart) connectionEditPart.getTarget();
            final Point targetPoint = targetEditPart.getFigure().getBounds().getCenter();
            targetX = targetPoint.x;
            targetY = targetPoint.y;
        }
        final RightAngleLineCommand command = new RightAngleLineCommand(sourceX, sourceY, targetX, targetY, connectionEditPart);
        return command;
    }

    @Override
    protected boolean calculateEnabled() {
        final GraphicalViewer viewer = this.getGraphicalViewer();
        for (final Object object : viewer.getSelectedEditParts()) {
            if (object instanceof ConnectionEditPart) {
                return true;
            } else if (object instanceof DiagramWalkerEditPart) {
                final DiagramWalkerEditPart nodeElementEditPart = (DiagramWalkerEditPart) object;
                if (!nodeElementEditPart.getSourceConnections().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
}
