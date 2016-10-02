package org.dbflute.erflute.editor.view.action.option.notation.design;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainModelEditor;
import org.dbflute.erflute.editor.controller.command.common.notation.ChangeDesignCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class AbstractChangeDesignAction extends AbstractBaseAction {

    private String type;

    public AbstractChangeDesignAction(String ID, String type, MainModelEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.change.design." + type), IAction.AS_RADIO_BUTTON, editor);

        this.type = type;
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

        ChangeDesignCommand command = new ChangeDesignCommand(diagram, type);

        this.execute(command);
    }

}
