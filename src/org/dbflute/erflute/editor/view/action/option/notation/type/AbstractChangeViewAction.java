package org.dbflute.erflute.editor.view.action.option.notation.type;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.notation.ChangeViewModeCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public abstract class AbstractChangeViewAction extends AbstractBaseAction {

    public AbstractChangeViewAction(String id, String type, MainDiagramEditor editor) {
        super(id, null, IAction.AS_RADIO_BUTTON, editor);
        setText(DisplayMessages.getMessage("action.title.change.mode.to." + type));
    }

    @Override
    public void execute(Event event) {
        if (!isChecked()) {
            return;
        }

        final ERDiagram diagram = getDiagram();
        final ChangeViewModeCommand command = new ChangeViewModeCommand(diagram, getViewMode());
        execute(command);
    }

    protected abstract int getViewMode();
}
