package org.dbflute.erflute.editor.view.action.option.notation.level;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.notation.ChangeNotationLevelCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public abstract class AbstractChangeNotationLevelAction extends AbstractBaseAction {

    public AbstractChangeNotationLevelAction(String id, MainDiagramEditor editor) {
        super(id, null, IAction.AS_RADIO_BUTTON, editor);
        this.setText(DisplayMessages.getMessage("action.title.change.notation.level." + this.getLevel()));
    }

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
