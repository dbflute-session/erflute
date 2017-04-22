package org.dbflute.erflute.editor.view.action.option.notation.system;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeToIDEF1XNotationAction extends AbstractChangeNotationAction {

    public static final String ID = ChangeToIDEF1XNotationAction.class.getName();

    public ChangeToIDEF1XNotationAction(MainDiagramEditor editor) {
        super(ID, "idef1x", editor);
    }

    @Override
    protected String getNotation() {
        return DiagramSettings.NOTATION_IDEF1X;
    }
}
