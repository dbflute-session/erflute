package org.dbflute.erflute.editor.view.action.group;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.group.ChangeColumnGroupCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyColumnGroup;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.columngroup.ColumnGroupManageDialog;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ColumnGroupManageAction extends AbstractBaseAction {

    public static final String ID = ColumnGroupManageAction.class.getName();

    public ColumnGroupManageAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.manage.group"), editor);
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = this.getDiagram();
        final ColumnGroupSet groupSet = diagram.getDiagramContents().getColumnGroupSet();
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final ColumnGroupManageDialog dialog = new ColumnGroupManageDialog(shell, groupSet, diagram, false, -1);
        if (dialog.open() == IDialogConstants.OK_ID) {
            final List<CopyColumnGroup> newColumnGroups = dialog.getCopyColumnGroups();
            final Command command = new ChangeColumnGroupCommand(diagram, groupSet, newColumnGroups);
            execute(command);
        }
    }
}
