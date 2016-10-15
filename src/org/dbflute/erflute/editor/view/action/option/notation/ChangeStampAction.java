package org.dbflute.erflute.editor.view.action.option.notation;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.notation.ChangeStampCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class ChangeStampAction extends AbstractBaseAction {

    public static final String ID = ChangeStampAction.class.getName();

    public ChangeStampAction(MainDiagramEditor editor) {
        super(ID, null, IAction.AS_CHECK_BOX, editor);
        this.setText(DisplayMessages.getMessage("action.title.display.stamp"));
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = this.getDiagram();
        final ChangeStampCommand command = new ChangeStampCommand(diagram, this.isChecked());
        this.execute(command);
    }
}
