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
