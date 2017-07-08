package org.dbflute.erflute.editor.view.action.outline.notation.type;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.command.common.ChangeOutlineViewModeCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.action.outline.AbstractOutlineBaseAction;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class ChangeOutlineViewToBothAction extends AbstractOutlineBaseAction {

    public static final String ID = ChangeOutlineViewToBothAction.class.getName();

    public ChangeOutlineViewToBothAction(TreeViewer treeViewer) {
        super(ID, null, IAction.AS_RADIO_BUTTON, treeViewer);
        setText(DisplayMessages.getMessage("action.title.change.mode.to.both"));
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = getDiagram();
        final ChangeOutlineViewModeCommand command = new ChangeOutlineViewModeCommand(diagram, DiagramSettings.VIEW_MODE_BOTH);
        execute(command);
    }
}
