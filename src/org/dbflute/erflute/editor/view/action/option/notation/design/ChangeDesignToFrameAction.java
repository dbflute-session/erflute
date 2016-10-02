package org.dbflute.erflute.editor.view.action.option.notation.design;

import org.dbflute.erflute.editor.MainModelEditor;

public class ChangeDesignToFrameAction extends AbstractChangeDesignAction {

    public static final String ID = ChangeDesignToFrameAction.class.getName();

    public static final String TYPE = "frame";

    public ChangeDesignToFrameAction(MainModelEditor editor) {
        super(ID, TYPE, editor);
    }

}
