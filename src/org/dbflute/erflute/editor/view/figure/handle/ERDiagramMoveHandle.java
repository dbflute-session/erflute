package org.dbflute.erflute.editor.view.figure.handle;

import org.dbflute.erflute.editor.view.figure.border.ERDiagramLineBorder;
import org.eclipse.draw2d.Cursors;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.MoveHandle;

public class ERDiagramMoveHandle extends MoveHandle {

    public ERDiagramMoveHandle(GraphicalEditPart owner) {
        super(owner);
    }

    @Override
    protected void initialize() {
        setOpaque(false);
        setBorder(new ERDiagramLineBorder());
        setCursor(Cursors.SIZEALL);
    }
}
