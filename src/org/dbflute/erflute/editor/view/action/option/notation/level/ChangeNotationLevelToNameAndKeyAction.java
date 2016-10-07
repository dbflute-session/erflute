package org.dbflute.erflute.editor.view.action.option.notation.level;

import org.dbflute.erflute.editor.RealModelEditor;
import org.dbflute.erflute.editor.model.settings.Settings;

public class ChangeNotationLevelToNameAndKeyAction extends AbstractChangeNotationLevelAction {

    public static final String ID = ChangeNotationLevelToNameAndKeyAction.class.getName();

    public ChangeNotationLevelToNameAndKeyAction(RealModelEditor editor) {
        super(ID, editor);
    }

    @Override
    protected int getLevel() {
        return Settings.NOTATION_LEVLE_NAME_AND_KEY;
    }

}
