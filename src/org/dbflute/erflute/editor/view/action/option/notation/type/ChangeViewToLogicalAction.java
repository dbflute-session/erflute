package org.dbflute.erflute.editor.view.action.option.notation.type;

import org.dbflute.erflute.editor.MainModelEditor;
import org.dbflute.erflute.editor.model.settings.Settings;

public class ChangeViewToLogicalAction extends AbstractChangeViewAction {

    public static final String ID = ChangeViewToLogicalAction.class.getName();

    public ChangeViewToLogicalAction(MainModelEditor editor) {
        super(ID, "logical", editor);
    }

    @Override
    protected int getViewMode() {
        return Settings.VIEW_MODE_LOGICAL;
    }
}