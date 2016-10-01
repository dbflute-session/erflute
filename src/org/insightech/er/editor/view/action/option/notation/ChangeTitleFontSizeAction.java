package org.insightech.er.editor.view.action.option.notation;

import org.dbflute.erflute.core.DisplayMessages;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.editor.MainModelEditor;
import org.insightech.er.editor.controller.command.common.notation.ChangeTitleFontSizeCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class ChangeTitleFontSizeAction extends AbstractBaseAction {

    public static final String ID = ChangeTitleFontSizeAction.class.getName();

    public ChangeTitleFontSizeAction(MainModelEditor editor) {
        super(ID, null, IAction.AS_CHECK_BOX, editor);
        this.setText(DisplayMessages.getMessage("action.title.display.titleFontLarge"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        ChangeTitleFontSizeCommand command = new ChangeTitleFontSizeCommand(diagram, this.isChecked());

        this.execute(command);
    }
}
