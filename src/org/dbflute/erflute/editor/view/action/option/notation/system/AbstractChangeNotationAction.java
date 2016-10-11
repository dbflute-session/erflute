package org.dbflute.erflute.editor.view.action.option.notation.system;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.notation.ChangeNotationCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public abstract class AbstractChangeNotationAction extends AbstractBaseAction {

    public AbstractChangeNotationAction(String id, String type, MainDiagramEditor editor) {
        super(id, null, IAction.AS_RADIO_BUTTON, editor);
        this.setText(DisplayMessages.getMessage("action.title.change.notation." + type));
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

        ChangeNotationCommand command = new ChangeNotationCommand(diagram, this.getNotation());

        this.execute(command);
    }

    protected abstract String getNotation();
}
