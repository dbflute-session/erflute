package org.insightech.er.editor.view.action.option.notation.design;

import org.insightech.er.editor.MainModelEditor;

public class ChangeDesignToFunnyAction extends AbstractChangeDesignAction {

    public static final String ID = ChangeDesignToFunnyAction.class.getName();

    public static final String TYPE = "funny";

    public ChangeDesignToFunnyAction(MainModelEditor editor) {
        super(ID, TYPE, editor);
    }

}
