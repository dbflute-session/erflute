package org.dbflute.erflute.editor.view.action.option.notation;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class LockEditAction extends AbstractBaseAction {

    public static final String ID = LockEditAction.class.getName();

    public LockEditAction(MainDiagramEditor editor) {
        super(ID, null, IAction.AS_CHECK_BOX, editor);
        setText(DisplayMessages.getMessage("action.title.lock.edit"));
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = getDiagram();
        diagram.setDisableSelectColumn(isChecked());
    }
}
