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
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERTableEditPart;
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

        setImageDescriptor(Activator.getImageDescriptor(ImageKey.VERTICAL_LINE));
        //		this.setDisabledImageDescriptor(Activator
        //				.getImageDescriptor(ImageKey.VERTICAL_LINE_DISABLED));
        setToolTipText(DisplayMessages.getMessage("action.title.vertical.line"));
    }

    @Override
    protected boolean calculateEnabled() {
        final Command cmd = createCommand();
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
            final List<DiagramWalkerEditPart> list = new ArrayList<>();

            for (final Object object : getSelectedObjects()) {
                if (object instanceof ERTableEditPart || object instanceof WalkerNoteEditPart) {
                    list.add((DiagramWalkerEditPart) object);
                }
            }

            if (list.size() < 3) {
                return null;
            }

            final DiagramWalkerEditPart firstEditPart = getFirstEditPart(list);
            list.remove(firstEditPart);

            Collections.sort(list, comparator);

            final Rectangle firstRectangle = firstEditPart.getFigure().getBounds();
            final int start = firstRectangle.y;
            final int top = firstRectangle.y + firstRectangle.height;

            final Rectangle lastRectangle = list.remove(list.size() - 1).getFigure().getBounds();
            final int bottom = lastRectangle.y;

            if (top > bottom) {
                command = alignToStart(start, list);
            } else {
                command = adjustSpace(start, top, bottom, list);
            }
        } catch (final Exception e) {
            Activator.error(e);
        }

        return command;
    }

    private Command alignToStart(int start, List<DiagramWalkerEditPart> list) {
        final CompoundCommand command = new CompoundCommand();

        for (final DiagramWalkerEditPart editPart : list) {
            final DiagramWalker nodeElement = (DiagramWalker) editPart.getModel();

            final MoveElementCommand moveCommand =
                    new MoveElementCommand(getDiagram(), editPart.getFigure().getBounds(), nodeElement.getX(), start,
                            nodeElement.getWidth(), nodeElement.getHeight(), nodeElement);

            command.add(moveCommand);
        }

        return command.unwrap();
    }

    private Command adjustSpace(int start, int top, int bottom, List<DiagramWalkerEditPart> list) {
        final CompoundCommand command = new CompoundCommand();

        int totalHeight = 0;
        for (final DiagramWalkerEditPart editPart : list) {
            totalHeight += editPart.getFigure().getBounds().height;
        }

        final int space = (bottom - top - totalHeight) / (list.size() + 1);
        int y = top;
        for (final DiagramWalkerEditPart editPart : list) {
            final DiagramWalker nodeElement = (DiagramWalker) editPart.getModel();

            y += space;

            final int nextY = y + editPart.getFigure().getBounds().height;

            if (y < start) {
                y = start;
            }

            final MoveElementCommand moveCommand =
                    new MoveElementCommand(getDiagram(), editPart.getFigure().getBounds(), nodeElement.getX(), y,
                            nodeElement.getWidth(), nodeElement.getHeight(), nodeElement);

            command.add(moveCommand);

            y = nextY;
        }

        return command.unwrap();
    }

    private DiagramWalkerEditPart getFirstEditPart(List<DiagramWalkerEditPart> list) {
        DiagramWalkerEditPart firstEditPart = null;
        for (final DiagramWalkerEditPart editPart : list) {
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

        @Override
        public int compare(DiagramWalkerEditPart o1, DiagramWalkerEditPart o2) {
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            final Rectangle bounds1 = o1.getFigure().getBounds();
            final Rectangle bounds2 = o2.getFigure().getBounds();

            final int rightY1 = bounds1.y + bounds1.height;
            final int rightY2 = bounds2.y + bounds2.height;

            return rightY1 - rightY2;
        }
    }

    public static class VerticalLineRetargetAction extends LabelRetargetAction {
        public VerticalLineRetargetAction() {
            super(ID, DisplayMessages.getMessage("action.title.vertical.line"));

            setImageDescriptor(Activator.getImageDescriptor(ImageKey.VERTICAL_LINE));
            //			this.setDisabledImageDescriptor(Activator
            //					.getImageDescriptor(ImageKey.VERTICAL_LINE_DISABLED));
            setToolTipText(DisplayMessages.getMessage("action.title.vertical.line"));
        }
    }

    @Override
    protected List<Command> getCommand(EditPart editPart, Event event) {
        return null;
    }
}
