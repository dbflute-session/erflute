package org.dbflute.erflute.editor.view.action.line;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERTableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.IResizable;
import org.dbflute.erflute.editor.controller.editpart.element.node.WalkerNoteEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.view.action.AbstractBaseSelectionAction;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.widgets.Event;

public class ResizeModelAction extends AbstractBaseSelectionAction {

    public static final String ID = ResizeModelAction.class.getName();

    public ResizeModelAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.auto.resize"), editor);
        setImageDescriptor(Activator.getImageDescriptor(ImageKey.RESIZE));
    }

    @Override
    protected List<Command> getCommand(EditPart editPart, Event event) {
        final List<Command> commandList = new ArrayList<>();

        if (editPart instanceof IResizable) {
            final DiagramWalker nodeElement = (DiagramWalker) editPart.getModel();

            final MoveElementCommand command =
                    new MoveElementCommand(getDiagram(), ((DiagramWalkerEditPart) editPart).getFigure().getBounds(),
                            nodeElement.getX(), nodeElement.getY(), -1, -1, nodeElement);

            commandList.add(command);
        }

        return commandList;
    }

    @Override
    protected boolean calculateEnabled() {
        final GraphicalViewer viewer = getGraphicalViewer();

        for (final Object object : viewer.getSelectedEditParts()) {
            if (object instanceof DiagramWalkerEditPart) {
                final DiagramWalkerEditPart nodeElementEditPart = (DiagramWalkerEditPart) object;

                if (nodeElementEditPart instanceof ERTableEditPart || nodeElementEditPart instanceof WalkerNoteEditPart) {
                    return true;
                }
            }
        }

        return false;
    }
}
