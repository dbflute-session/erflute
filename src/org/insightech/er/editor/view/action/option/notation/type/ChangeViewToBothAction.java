package org.insightech.er.editor.view.action.option.notation.type;

import org.insightech.er.editor.MainModelEditor;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeViewToBothAction extends AbstractChangeViewAction {

    public static final String ID = ChangeViewToBothAction.class.getName();

    public ChangeViewToBothAction(MainModelEditor editor) {
        super(ID, "both", editor);
    }

    @Override
    protected int getViewMode() {
        return Settings.VIEW_MODE_BOTH;
    }

}
