package org.dbflute.erflute.editor.view.action.option.notation.design;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.notation.ChangeDesignCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class AbstractChangeDesignAction extends AbstractBaseAction {

    private final String type;

    public AbstractChangeDesignAction(String ID, String type, MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.change.design." + type), IAction.AS_RADIO_BUTTON, editor);

        this.type = type;
    }

    @Override
    public void execute(Event event) {
        if (!isChecked()) {
            return;
        }

        final ERDiagram diagram = getDiagram();
        final ChangeDesignCommand command = new ChangeDesignCommand(diagram, type);
        execute(command);
    }
}
