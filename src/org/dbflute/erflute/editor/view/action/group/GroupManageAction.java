package org.dbflute.erflute.editor.view.action.group;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.RealModelEditor;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.group.ChangeGroupCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GroupSet;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.group.GroupManageDialog;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class GroupManageAction extends AbstractBaseAction {

    public static final String ID = GroupManageAction.class.getName();

    public GroupManageAction(RealModelEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.manage.group"), editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();
        GroupSet groupSet = diagram.getDiagramContents().getGroups();

        GroupManageDialog dialog =
                new GroupManageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), groupSet, diagram, false, -1);

        if (dialog.open() == IDialogConstants.OK_ID) {
            List<CopyGroup> newColumnGroups = dialog.getCopyColumnGroups();

            Command command = new ChangeGroupCommand(diagram, groupSet, newColumnGroups);

            this.execute(command);
        }
    }
}
