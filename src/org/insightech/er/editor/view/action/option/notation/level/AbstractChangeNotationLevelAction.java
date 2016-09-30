package org.insightech.er.editor.view.action.option.notation.level;

import org.dbflute.erflute.core.DisplayMessages;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.common.notation.ChangeNotationLevelCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public abstract class AbstractChangeNotationLevelAction extends AbstractBaseAction {

    public AbstractChangeNotationLevelAction(String id, ERDiagramEditor editor) {
        super(id, null, IAction.AS_RADIO_BUTTON, editor);
        this.setText(DisplayMessages.getMessage("action.title.change.notation.level." + this.getLevel()));
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

        ChangeNotationLevelCommand command = new ChangeNotationLevelCommand(diagram, this.getLevel());

        this.execute(command);
    }

    protected abstract int getLevel();
}
