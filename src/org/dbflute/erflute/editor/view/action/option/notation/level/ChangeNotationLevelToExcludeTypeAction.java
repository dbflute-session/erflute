package org.dbflute.erflute.editor.view.action.option.notation.level;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeNotationLevelToExcludeTypeAction extends AbstractChangeNotationLevelAction {

    public static final String ID = ChangeNotationLevelToExcludeTypeAction.class.getName();

    public ChangeNotationLevelToExcludeTypeAction(MainDiagramEditor editor) {
        super(ID, editor);
    }

    @Override
    protected int getLevel() {
        return DiagramSettings.NOTATION_LEVLE_EXCLUDE_TYPE;
    }

}
