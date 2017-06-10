package org.dbflute.erflute.editor.view.action.line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERTableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.WalkerNoteEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.view.action.AbstractBaseSelectionAction;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.LabelRetargetAction;

public class VerticalLineAction extends AbstractBaseSelectionAction {

    public static final String ID = VerticalLineAction.class.getName();

    public VerticalLineAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.vertical.line"), editor);

        this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.VERTICAL_LINE));
        //		this.setDisabledImageDescriptor(Activator
        //				.getImageDescriptor(ImageKey.VERTICAL_LINE_DISABLED));
        this.setToolTipText(DisplayMessages.getMessage("action.title.vertical.line"));
    }

    @Override
    protected boolean calculateEnabled() {
        Command cmd = this.createCommand();
        if (cmd == null) {
            return false;
        }
        return cmd.canExecute();
    }

    @Override
    protected void execute(Event event) {
        execute(createCommand());
    }

    private Command createCommand() {
        Command command = null;
        try {
            List<DiagramWalkerEditPart> list = new ArrayList<DiagramWalkerEditPart>();

            for (Object object : this.getSelectedObjects()) {
                if (object instanceof ERTableEditPart || object instanceof WalkerNoteEditPart) {
                    list.add((DiagramWalkerEditPart) object);
                }
            }

            if (list.size() < 3) {
                return null;
            }

            DiagramWalkerEditPart firstEditPart = this.getFirstEditPart(list);
            list.remove(firstEditPart);

            Collections.sort(list, comparator);

            Rectangle firstRectangle = firstEditPart.getFigure().getBounds();
            int start = firstRectangle.y;
            int top = firstRectangle.y + firstRectangle.height;

            Rectangle lastRectangle = list.remove(list.size() - 1).getFigure().getBounds();
            int bottom = lastRectangle.y;

            if (top > bottom) {
                command = this.alignToStart(start, list);

            } else {
                command = this.adjustSpace(start, top, bottom, list);
            }
        } catch (Exception e) {
            Activator.error(e);
        }

        return command;
    }

    private Command alignToStart(int start, List<DiagramWalkerEditPart> list) {
        CompoundCommand command = new CompoundCommand();

        for (DiagramWalkerEditPart editPart : list) {
            DiagramWalker nodeElement = (DiagramWalker) editPart.getModel();

            MoveElementCommand moveCommand =
                    new MoveElementCommand(this.getDiagram(), editPart.getFigure().getBounds(), nodeElement.getX(), start,
                            nodeElement.getWidth(), nodeElement.getHeight(), nodeElement);

            command.add(moveCommand);
        }

        return command.unwrap();
    }

    private Command adjustSpace(int start, int top, int bottom, List<DiagramWalkerEditPart> list) {
        CompoundCommand command = new CompoundCommand();

        int totalHeight = 0;

        for (DiagramWalkerEditPart editPart : list) {
            totalHeight += editPart.getFigure().getBounds().height;
        }

        int space = (bottom - top - totalHeight) / (list.size() + 1);

        int y = top;

        for (DiagramWalkerEditPart editPart : list) {
            DiagramWalker nodeElement = (DiagramWalker) editPart.getModel();

            y += space;

            int nextY = y + editPart.getFigure().getBounds().height;

            if (y < start) {
                y = start;
            }

            MoveElementCommand moveCommand =
                    new MoveElementCommand(this.getDiagram(), editPart.getFigure().getBounds(), nodeElement.getX(), y,
                            nodeElement.getWidth(), nodeElement.getHeight(), nodeElement);

            command.add(moveCommand);

            y = nextY;
        }

        return command.unwrap();
    }

    private DiagramWalkerEditPart getFirstEditPart(List<DiagramWalkerEditPart> list) {
        DiagramWalkerEditPart firstEditPart = null;

        for (DiagramWalkerEditPart editPart : list) {
            if (firstEditPart == null) {
                firstEditPart = editPart;

            } else {
                if (firstEditPart.getFigure().getBounds().y > editPart.getFigure().getBounds().y) {
                    firstEditPart = editPart;
                }
            }
        }

        return firstEditPart;
    }

    private static final Comparator<DiagramWalkerEditPart> comparator = new NodeElementEditPartVerticalComparator();

    private static class NodeElementEditPartVerticalComparator implements Comparator<DiagramWalkerEditPart> {

        public int compare(DiagramWalkerEditPart o1, DiagramWalkerEditPart o2) {
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            Rectangle bounds1 = o1.getFigure().getBounds();
            Rectangle bounds2 = o2.getFigure().getBounds();

            int rightY1 = bounds1.y + bounds1.height;
            int rightY2 = bounds2.y + bounds2.height;

            return rightY1 - rightY2;
        }
    }

    public static class VerticalLineRetargetAction extends LabelRetargetAction {
        public VerticalLineRetargetAction() {
            super(ID, DisplayMessages.getMessage("action.title.vertical.line"));

            this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.VERTICAL_LINE));
            //			this.setDisabledImageDescriptor(Activator
            //					.getImageDescriptor(ImageKey.VERTICAL_LINE_DISABLED));
            this.setToolTipText(DisplayMessages.getMessage("action.title.vertical.line"));
        }
    }

    @Override
    protected List<Command> getCommand(EditPart editPart, Event event) {
        return null;
    }
}
