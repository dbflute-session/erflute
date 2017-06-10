package org.dbflute.erflute.editor.view.action.option.notation.level;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeNotationLevelToColumnAction extends AbstractChangeNotationLevelAction {

    public static final String ID = ChangeNotationLevelToColumnAction.class.getName();

    public ChangeNotationLevelToColumnAction(MainDiagramEditor editor) {
        super(ID, editor);
    }

    @Override
    protected int getLevel() {
        return DiagramSettings.NOTATION_LEVLE_COLUMN;
    }
}
