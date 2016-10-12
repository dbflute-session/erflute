package org.dbflute.erflute.editor.view.action.group;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.group.ChangeGroupCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.columngroup.ColumnGroupManageDialog;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class GroupManageAction extends AbstractBaseAction {

    public static final String ID = GroupManageAction.class.getName();

    public GroupManageAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.manage.group"), editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();
        ColumnGroupSet groupSet = diagram.getDiagramContents().getColumnGroupSet();

        ColumnGroupManageDialog dialog =
                new ColumnGroupManageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), groupSet, diagram, false, -1);

        if (dialog.open() == IDialogConstants.OK_ID) {
            List<CopyGroup> newColumnGroups = dialog.getCopyColumnGroups();

            Command command = new ChangeGroupCommand(diagram, groupSet, newColumnGroups);

            this.execute(command);
        }
    }
}
