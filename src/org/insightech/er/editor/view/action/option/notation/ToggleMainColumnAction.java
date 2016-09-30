package org.insightech.er.editor.view.action.option.notation;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.DisplayMessages;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class ToggleMainColumnAction extends AbstractBaseAction {

    public static final String ID = ToggleMainColumnAction.class.getName();

    public ToggleMainColumnAction(ERDiagramEditor editor) {
        super(ID, null, IAction.AS_CHECK_BOX, editor);
        this.setText(DisplayMessages.getMessage("action.title.display.mainColumn"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();
        diagram.setShowMainColumn(this.isChecked());
    }
}
