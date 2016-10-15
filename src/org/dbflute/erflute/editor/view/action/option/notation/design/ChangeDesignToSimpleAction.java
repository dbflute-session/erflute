package org.dbflute.erflute.editor.view.action.option.notation.design;

import org.dbflute.erflute.editor.MainDiagramEditor;

public class ChangeDesignToSimpleAction extends AbstractChangeDesignAction {

    public static final String ID = ChangeDesignToSimpleAction.class.getName();

    public static final String TYPE = "simple";

    public ChangeDesignToSimpleAction(MainDiagramEditor editor) {
        super(ID, TYPE, editor);
    }

}
