package org.dbflute.erflute.editor.view.action.outline.notation.type;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.command.common.ChangeOutlineViewModeCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.action.outline.AbstractOutlineBaseAction;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class ChangeOutlineViewToPhysicalAction extends AbstractOutlineBaseAction {

    public static final String ID = ChangeOutlineViewToPhysicalAction.class.getName();

    public ChangeOutlineViewToPhysicalAction(TreeViewer treeViewer) {
        super(ID, null, IAction.AS_RADIO_BUTTON, treeViewer);
        this.setText(DisplayMessages.getMessage("action.title.change.mode.to.physical"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        ChangeOutlineViewModeCommand command = new ChangeOutlineViewModeCommand(diagram, Settings.VIEW_MODE_PHYSICAL);

        this.execute(command);
    }

}
