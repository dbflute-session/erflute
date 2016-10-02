package org.dbflute.erflute.editor.view.action.option.notation.type;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainModelEditor;
import org.dbflute.erflute.editor.controller.command.common.notation.ChangeViewModeCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public abstract class AbstractChangeViewAction extends AbstractBaseAction {

    public AbstractChangeViewAction(String id, String type, MainModelEditor editor) {
        super(id, null, IAction.AS_RADIO_BUTTON, editor);
        this.setText(DisplayMessages.getMessage("action.title.change.mode.to." + type));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        if (!this.isChecked()) {
            return;
        }

        ERDiagram diagram = this.getDiagram();

        ChangeViewModeCommand command = new ChangeViewModeCommand(diagram, this.getViewMode());

        this.execute(command);
    }

    protected abstract int getViewMode();
}
