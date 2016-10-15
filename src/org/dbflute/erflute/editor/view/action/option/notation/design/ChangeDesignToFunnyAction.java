package org.dbflute.erflute.editor.view.action.option.notation.design;

import org.dbflute.erflute.editor.MainDiagramEditor;

public class ChangeDesignToFunnyAction extends AbstractChangeDesignAction {

    public static final String ID = ChangeDesignToFunnyAction.class.getName();

    public static final String TYPE = "funny";

    public ChangeDesignToFunnyAction(MainDiagramEditor editor) {
        super(ID, TYPE, editor);
    }

}
