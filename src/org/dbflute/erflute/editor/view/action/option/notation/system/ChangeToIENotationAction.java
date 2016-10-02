package org.dbflute.erflute.editor.view.action.option.notation.system;

import org.dbflute.erflute.editor.MainModelEditor;
import org.dbflute.erflute.editor.model.settings.Settings;

public class ChangeToIENotationAction extends AbstractChangeNotationAction {

    public static final String ID = ChangeToIENotationAction.class.getName();

    public ChangeToIENotationAction(MainModelEditor editor) {
        super(ID, "ie", editor);
    }

    @Override
    protected String getNotation() {
        return Settings.NOTATION_IE;
    }

}
