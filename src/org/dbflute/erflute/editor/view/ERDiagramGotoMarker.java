package org.dbflute.erflute.editor.view;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.eclipse.core.resources.IMarker;
import org.eclipse.gef.EditPart;
import org.eclipse.ui.ide.IGotoMarker;

public class ERDiagramGotoMarker implements IGotoMarker {

    private final MainDiagramEditor editor;

    public ERDiagramGotoMarker(MainDiagramEditor editor) {
        this.editor = editor;
    }

    @Override
    public void gotoMarker(IMarker marker) {
        focus(editor.getMarkedObject(marker));
    }

    private void focus(Object object) {
        final EditPart editPart = (EditPart) editor.getGraphicalViewer().getEditPartRegistry().get(object);

        if (editPart != null) {
            editor.getGraphicalViewer().select(editPart);
            editor.getGraphicalViewer().reveal(editPart);
        }
    }
}
