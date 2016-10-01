package org.insightech.er.editor.view.action.option.notation.level;

import org.insightech.er.editor.MainModelEditor;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeNotationLevelToColumnAction extends AbstractChangeNotationLevelAction {

    public static final String ID = ChangeNotationLevelToColumnAction.class.getName();

    public ChangeNotationLevelToColumnAction(MainModelEditor editor) {
        super(ID, editor);
    }

    @Override
    protected int getLevel() {
        return Settings.NOTATION_LEVLE_COLUMN;
    }

}
