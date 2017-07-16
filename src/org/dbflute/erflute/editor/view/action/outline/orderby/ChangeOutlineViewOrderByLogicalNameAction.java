package org.dbflute.erflute.editor.view.action.outline.orderby;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.command.common.ChangeOutlineViewOrderByCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.action.outline.AbstractOutlineBaseAction;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class ChangeOutlineViewOrderByLogicalNameAction extends AbstractOutlineBaseAction {

    public static final String ID = ChangeOutlineViewOrderByLogicalNameAction.class.getName();

    public ChangeOutlineViewOrderByLogicalNameAction(TreeViewer treeViewer) {
        super(ID, null, IAction.AS_RADIO_BUTTON, treeViewer);
        setText(DisplayMessages.getMessage("label.logical.name"));
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = getDiagram();
        final ChangeOutlineViewOrderByCommand command = new ChangeOutlineViewOrderByCommand(diagram, DiagramSettings.VIEW_MODE_LOGICAL);
        execute(command);
    }
}
