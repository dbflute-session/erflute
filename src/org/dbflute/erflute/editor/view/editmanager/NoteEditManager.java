package org.dbflute.erflute.editor.view.editmanager;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.Note;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;

public class NoteEditManager extends DirectEditManager {

    private Note note;

    public NoteEditManager(GraphicalEditPart source, Class editorType, CellEditorLocator locator) {
        super(source, editorType, locator);
        this.note = (Note) source.getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initCellEditor() {
        TextCellEditor editor = (TextCellEditor) this.getCellEditor();

        if (note.getText() != null) {
            editor.setValue(note.getText());
        }

        Text text = (Text) editor.getControl();

        text.selectAll();
    }
}
