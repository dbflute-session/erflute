package org.dbflute.erflute.editor.view.editmanager;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;

public class WalkerNoteEditManager extends DirectEditManager {

    private final WalkerNote note;

    public WalkerNoteEditManager(GraphicalEditPart source, Class<?> editorType, CellEditorLocator locator) {
        super(source, editorType, locator);
        this.note = (WalkerNote) source.getModel();
    }

    @Override
    protected void initCellEditor() {
        final TextCellEditor editor = (TextCellEditor) getCellEditor();
        if (note.getNoteText() != null) {
            editor.setValue(note.getNoteText());
        }
        final Text text = (Text) editor.getControl();
        text.selectAll();
    }
}
