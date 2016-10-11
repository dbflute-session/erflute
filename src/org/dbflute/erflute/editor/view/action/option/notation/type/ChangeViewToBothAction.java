package org.dbflute.erflute.editor.view.action.option.notation.type;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.settings.Settings;

public class ChangeViewToBothAction extends AbstractChangeViewAction {

    public static final String ID = ChangeViewToBothAction.class.getName();

    public ChangeViewToBothAction(MainDiagramEditor editor) {
        super(ID, "both", editor);
    }

    @Override
    protected int getViewMode() {
        return Settings.VIEW_MODE_BOTH;
    }

}
