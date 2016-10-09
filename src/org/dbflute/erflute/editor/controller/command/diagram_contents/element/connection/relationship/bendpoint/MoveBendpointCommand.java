package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.eclipse.gef.ConnectionEditPart;

public class MoveBendpointCommand extends AbstractCommand {

    private ConnectionEditPart editPart;

    private Bendpoint bendPoint;

    private Bendpoint oldBendpoint;

    private int index;

    public MoveBendpointCommand(ConnectionEditPart editPart, int x, int y, int index) {
        this.editPart = editPart;
        this.bendPoint = new Bendpoint(x, y);
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        ConnectionElement connection = (ConnectionElement) editPart.getModel();

        this.oldBendpoint = connection.getBendpoints().get(index);
        connection.replaceBendpoint(index, this.bendPoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        ConnectionElement connection = (ConnectionElement) editPart.getModel();
        connection.replaceBendpoint(index, this.oldBendpoint);
    }

}
