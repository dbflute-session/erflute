package org.dbflute.erflute.editor.view.action.option.notation.type;

import org.dbflute.erflute.editor.RealModelEditor;
import org.dbflute.erflute.editor.model.settings.Settings;

public class ChangeViewToPhysicalAction extends AbstractChangeViewAction {

    public static final String ID = ChangeViewToPhysicalAction.class.getName();

    public ChangeViewToPhysicalAction(RealModelEditor editor) {
        super(ID, "physical", editor);
    }

    @Override
    protected int getViewMode() {
        return Settings.VIEW_MODE_PHYSICAL;
    }
}
