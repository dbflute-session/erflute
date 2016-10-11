package org.dbflute.erflute.editor.view.action.line;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.DefaultLineCommand;
import org.dbflute.erflute.editor.controller.editpart.element.node.IResizable;
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.view.action.AbstractBaseSelectionAction;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.widgets.Event;

public class DefaultLineAction extends AbstractBaseSelectionAction {

    public static final String ID = DefaultLineAction.class.getName();

    public DefaultLineAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.default"), editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Command> getCommand(EditPart editPart, Event event) {
        List<Command> commandList = new ArrayList<Command>();

        if (editPart instanceof IResizable) {
            DiagramWalkerEditPart nodeElementEditPart = (DiagramWalkerEditPart) editPart;

            for (Object obj : nodeElementEditPart.getSourceConnections()) {
                AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) obj;

                if (connectionEditPart.getSource() != connectionEditPart.getTarget()) {
                    commandList.add(new DefaultLineCommand(this.getDiagram(), (WalkerConnection) connectionEditPart.getModel()));
                }
            }

        } else if (editPart instanceof AbstractConnectionEditPart) {
            AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) editPart;

            if (connectionEditPart.getSource() != connectionEditPart.getTarget()) {
                commandList.add(new DefaultLineCommand(this.getDiagram(), (WalkerConnection) connectionEditPart.getModel()));
            }
        }

        return commandList;
    }

    @Override
    protected boolean calculateEnabled() {
        GraphicalViewer viewer = this.getGraphicalViewer();

        for (Object object : viewer.getSelectedEditParts()) {
            if (object instanceof ConnectionEditPart) {
                return true;

            } else if (object instanceof DiagramWalkerEditPart) {
                DiagramWalkerEditPart nodeElementEditPart = (DiagramWalkerEditPart) object;

                if (!nodeElementEditPart.getSourceConnections().isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }
}