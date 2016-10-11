package org.dbflute.erflute.editor.view.tool;

import java.util.List;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.connection.RelationEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ModelPropertiesEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.WalkerNoteEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.TableViewEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.tools.PanningSelectionTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class MovablePanningSelectionTool extends PanningSelectionTool {

    public static boolean shift = false;

    @Override
    protected boolean handleKeyUp(KeyEvent event) {
        if (event.keyCode == SWT.SHIFT) {
            shift = true;
        }
        return super.handleKeyUp(event);
    }

    @Override
    protected boolean handleKeyDown(KeyEvent event) {
        int dx = 0;
        int dy = 0;
        if (event.keyCode == SWT.SHIFT) {
            shift = true;
        }
        if (event.keyCode == SWT.ARROW_DOWN) {
            dy = 1;
        } else if (event.keyCode == SWT.ARROW_LEFT) {
            dx = -1;
        } else if (event.keyCode == SWT.ARROW_RIGHT) {
            dx = 1;
        } else if (event.keyCode == SWT.ARROW_UP) {
            dy = -1;
        }
        final Object model = this.getCurrentViewer().getContents().getModel();
        AbstractGraphicalEditPart targetEditPart = null;
        ERDiagram diagram = null;
        if (model instanceof ERVirtualDiagram) {
            diagram = ((ERVirtualDiagram) model).getDiagram();
        }
        if (model instanceof ERDiagram) {
            diagram = (ERDiagram) model;
        }
        if (diagram != null) {
            final List<?> selectedObject = getCurrentViewer().getSelectedEditParts();
            if (!selectedObject.isEmpty()) {
                final CompoundCommand command = new CompoundCommand();
                for (final Object obj : selectedObject) {
                    if (isNodeElementEditPart(obj)) {
                        final DiagramWalkerEditPart editPart = (DiagramWalkerEditPart) obj;
                        targetEditPart = editPart;
                        final DiagramWalker nodeElement = (DiagramWalker) editPart.getModel();
                        command.add(createMoveElementCommand(dx, dy, diagram, editPart, nodeElement));
                    } else if (obj instanceof RelationEditPart) {
                        final RelationEditPart editPart = (RelationEditPart) obj;
                        targetEditPart = editPart;
                        // #thining needed? by jflute
                        //final NodeElement nodeElement = (NodeElement) editPart.getModel();
                        //command.add(createMoveElementCommand(dx, dy, diagram, editPart, nodeElement));
                    }
                }
                getCurrentViewer().getEditDomain().getCommandStack().execute(command.unwrap());
            }
        }
        openDetailByKeyCode(event, targetEditPart);
        return super.handleKeyDown(event);
    }

    private boolean isNodeElementEditPart(final Object obj) {
        return obj instanceof TableViewEditPart || obj instanceof WalkerNoteEditPart || obj instanceof ModelPropertiesEditPart;
    }

    private MoveElementCommand createMoveElementCommand(int dx, int dy, ERDiagram diagram, final DiagramWalkerEditPart editPart,
            final DiagramWalker nodeElement) {
        final Rectangle bounds = editPart.getFigure().getBounds();
        final int width = nodeElement.getWidth();
        final int height = nodeElement.getHeight();
        return new MoveElementCommand(diagram, bounds, nodeElement.getX() + dx, nodeElement.getY() + dy, width, height, nodeElement);
    }

    private void openDetailByKeyCode(KeyEvent event, AbstractGraphicalEditPart targetEditPart) {
        if (targetEditPart != null && isOpenDetailKeyCode(event)) {
            final Request request = new Request();
            request.setType(RequestConstants.REQ_OPEN);
            targetEditPart.performRequest(request);
        }
    }

    private boolean isOpenDetailKeyCode(KeyEvent event) {
        return event.keyCode == SWT.CR || event.keyCode == SWT.SPACE;
    }

    @Override
    public void mouseDown(MouseEvent e, EditPartViewer viewer) {
        if (viewer.getContents() instanceof ERDiagramEditPart) {
            final ERDiagramEditPart editPart = (ERDiagramEditPart) viewer.getContents();
            final ERDiagram diagram = (ERDiagram) editPart.getModel();
            diagram.mousePoint = new Point(e.x, e.y);
            editPart.getFigure().translateToRelative(diagram.mousePoint);
        }
        super.mouseDown(e, viewer);
    }
}
