package org.dbflute.erflute.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.editor.controller.editpolicy.element.node.DiagramWalkerComponentEditPolicy;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.note.WalkerNoteDirectEditPolicy;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.view.editmanager.WalkerNoteCellEditor;
import org.dbflute.erflute.editor.view.editmanager.WalkerNoteEditManager;
import org.dbflute.erflute.editor.view.editmanager.WalkerNoteEditorLocator;
import org.dbflute.erflute.editor.view.figure.WalkerNoteFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

public class WalkerNoteEditPart extends DiagramWalkerEditPart implements IResizable {

    private WalkerNoteEditManager editManager = null;

    @Override
    protected IFigure createFigure() {
        final WalkerNoteFigure noteFigure = new WalkerNoteFigure();
        changeFont(noteFigure);
        return noteFigure;
    }

    @Override
    public void doPropertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(WalkerNote.PROPERTY_CHANGE_WALKER_NOTE)) {
            refreshVisuals();
        }
        super.doPropertyChange(event);
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new DiagramWalkerComponentEditPolicy());
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new WalkerNoteDirectEditPolicy());
        super.createEditPolicies();
    }

    @Override
    public void refreshVisuals() {
        final WalkerNote note = (WalkerNote) getModel();
        final WalkerNoteFigure figure = (WalkerNoteFigure) getFigure();
        figure.setText(note.getNoteText(), note.getColor());
        super.refreshVisuals();
    }

    @Override
    public void performRequest(Request request) {
        if (request.getType().equals(RequestConstants.REQ_DIRECT_EDIT) || request.getType().equals(RequestConstants.REQ_OPEN)) {
            performDirectEdit();
        }
    }

    private void performDirectEdit() {
        if (editManager == null) {
            this.editManager = new WalkerNoteEditManager(this, WalkerNoteCellEditor.class, new WalkerNoteEditorLocator(getFigure()));
        }
        editManager.show();
    }

    @Override
    protected void performRequestOpen() {
    }
}
