package org.dbflute.erflute.editor.view.action.option.notation.system;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeToIENotationAction extends AbstractChangeNotationAction {

    public static final String ID = ChangeToIENotationAction.class.getName();

    public ChangeToIENotationAction(MainDiagramEditor editor) {
        super(ID, "ie", editor);
    }

    @Override
    protected String getNotation() {
        return DiagramSettings.NOTATION_IE;
    }
}
