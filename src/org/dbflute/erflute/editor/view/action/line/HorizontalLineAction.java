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
import org.dbflute.erflute.editor.controller.editpart.element.AbstractModelEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERTableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.WalkerNoteEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.view.action.AbstractBaseSelectionAction;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.LabelRetargetAction;

public class HorizontalLineAction extends AbstractBaseSelectionAction {

    public static final String ID = HorizontalLineAction.class.getName();

    public HorizontalLineAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.horizontal.line"), editor);

        setImageDescriptor(Activator.getImageDescriptor(ImageKey.HORIZONTAL_LINE));
        //		this.setDisabledImageDescriptor(Activator
        //				.getImageDescriptor(ImageKey.HORIZONTAL_LINE_DISABLED));
        setToolTipText(DisplayMessages.getMessage("action.title.horizontal.line"));
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
        final int start = firstRectangle.x;
        final int left = firstRectangle.x + firstRectangle.width;

        final Rectangle lastRectangle = list.remove(list.size() - 1).getFigure().getBounds();
        final int right = lastRectangle.x;

        if (left > right) {
            command = alignToStart(start, list);
        } else {
            command = adjustSpace(start, left, right, list);
        }

        return command;
    }

    private Command alignToStart(int start, List<DiagramWalkerEditPart> list) {
        final CompoundCommand command = new CompoundCommand();

        final ERDiagram diagram = getDiagram();

        for (final AbstractModelEditPart editPart : list) {
            final DiagramWalker nodeElement = (DiagramWalker) editPart.getModel();

            final MoveElementCommand moveCommand =
                    new MoveElementCommand(diagram, editPart.getFigure().getBounds(), start, nodeElement.getY(), nodeElement.getWidth(),
                            nodeElement.getHeight(), nodeElement);

            command.add(moveCommand);
        }

        return command.unwrap();
    }

    private Command adjustSpace(int start, int left, int right, List<DiagramWalkerEditPart> list) {
        final CompoundCommand command = new CompoundCommand();

        final ERDiagram diagram = getDiagram();

        int totalWidth = 0;
        for (final AbstractModelEditPart editPart : list) {
            totalWidth += editPart.getFigure().getBounds().width;
        }

        final int space = (right - left - totalWidth) / (list.size() + 1);
        int x = left;
        for (final AbstractModelEditPart editPart : list) {
            final DiagramWalker nodeElement = (DiagramWalker) editPart.getModel();

            x += space;

            final int nextX = x + editPart.getFigure().getBounds().width;

            if (x < start) {
                x = start;
            }

            final MoveElementCommand moveCommand =
                    new MoveElementCommand(diagram, editPart.getFigure().getBounds(), x, nodeElement.getY(), nodeElement.getWidth(),
                            nodeElement.getHeight(), nodeElement);
            command.add(moveCommand);

            x = nextX;
        }

        return command.unwrap();
    }

    private DiagramWalkerEditPart getFirstEditPart(List<DiagramWalkerEditPart> list) {
        DiagramWalkerEditPart firstEditPart = null;

        for (final DiagramWalkerEditPart editPart : list) {
            if (firstEditPart == null) {
                firstEditPart = editPart;

            } else {
                if (firstEditPart.getFigure().getBounds().x > editPart.getFigure().getBounds().x) {
                    firstEditPart = editPart;
                }
            }
        }

        return firstEditPart;
    }

    private static final Comparator<AbstractModelEditPart> comparator = new AbstractModelEditPartHorizontalComparator();

    private static class AbstractModelEditPartHorizontalComparator implements Comparator<AbstractModelEditPart> {

        @Override
        public int compare(AbstractModelEditPart o1, AbstractModelEditPart o2) {
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            final Rectangle bounds1 = o1.getFigure().getBounds();
            final Rectangle bounds2 = o2.getFigure().getBounds();

            final int rightX1 = bounds1.x + bounds1.width;
            final int rightX2 = bounds2.x + bounds2.width;

            return rightX1 - rightX2;
        }
    }

    public static class HorizontalLineRetargetAction extends LabelRetargetAction {
        public HorizontalLineRetargetAction() {
            super(ID, DisplayMessages.getMessage("action.title.horizontal.line"));

            setImageDescriptor(Activator.getImageDescriptor(ImageKey.HORIZONTAL_LINE));
            //			this.setDisabledImageDescriptor(Activator
            //					.getImageDescriptor(ImageKey.HORIZONTAL_LINE_DISABLED));
            setToolTipText(DisplayMessages.getMessage("action.title.horizontal.line"));
        }
    }

    @Override
    protected List<Command> getCommand(EditPart editPart, Event event) {
        return null;
    }
}
