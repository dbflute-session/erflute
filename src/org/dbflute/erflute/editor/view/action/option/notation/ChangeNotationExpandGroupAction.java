package org.dbflute.erflute.editor.view.action.option.notation;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.notation.ChangeNotationExpandGroupCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class ChangeNotationExpandGroupAction extends AbstractBaseAction {

    public static final String ID = ChangeNotationExpandGroupAction.class.getName();

    public ChangeNotationExpandGroupAction(MainDiagramEditor editor) {
        super(ID, null, IAction.AS_CHECK_BOX, editor);
        setText(DisplayMessages.getMessage("action.title.notation.expand.group"));
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = getDiagram();
        final ChangeNotationExpandGroupCommand command = new ChangeNotationExpandGroupCommand(diagram, isChecked());
        execute(command);
    }
}
