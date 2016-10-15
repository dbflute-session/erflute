package org.dbflute.erflute.editor.view.action.category;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.category.ChangeFreeLayoutCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class ChangeFreeLayoutAction extends AbstractBaseAction {

    public static final String ID = ChangeFreeLayoutAction.class.getName();

    public ChangeFreeLayoutAction(MainDiagramEditor editor) {
        super(ID, null, IAction.AS_CHECK_BOX, editor);
        this.setText(DisplayMessages.getMessage("action.title.category.free.layout"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        ChangeFreeLayoutCommand command = new ChangeFreeLayoutCommand(diagram, this.isChecked());

        this.execute(command);
    }
}
