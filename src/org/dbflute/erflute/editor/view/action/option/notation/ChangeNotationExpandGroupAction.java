package org.dbflute.erflute.editor.view.action.option.notation;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.RealModelEditor;
import org.dbflute.erflute.editor.controller.command.common.notation.ChangeNotationExpandGroupCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class ChangeNotationExpandGroupAction extends AbstractBaseAction {

    public static final String ID = ChangeNotationExpandGroupAction.class.getName();

    public ChangeNotationExpandGroupAction(RealModelEditor editor) {
        super(ID, null, IAction.AS_CHECK_BOX, editor);
        this.setText(DisplayMessages.getMessage("action.title.notation.expand.group"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        ChangeNotationExpandGroupCommand command = new ChangeNotationExpandGroupCommand(diagram, this.isChecked());

        this.execute(command);
    }
}
