package org.dbflute.erflute.editor.view.action.option.notation;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.notation.ChangeTitleFontSizeCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class ChangeTitleFontSizeAction extends AbstractBaseAction {

    public static final String ID = ChangeTitleFontSizeAction.class.getName();

    public ChangeTitleFontSizeAction(MainDiagramEditor editor) {
        super(ID, null, IAction.AS_CHECK_BOX, editor);
        setText(DisplayMessages.getMessage("Display table title larger"));
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = getDiagram();
        final ChangeTitleFontSizeCommand command = new ChangeTitleFontSizeCommand(diagram, isChecked());
        execute(command);
    }
}
