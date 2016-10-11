package org.dbflute.erflute.editor.view.action.ermodel;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.VirtualDiagramEditor;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.ChangeVGroupCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.category.WalkerGroupManageDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class WalkerGroupManageAction extends AbstractBaseAction {

    public static final String ID = WalkerGroupManageAction.class.getName();

    public WalkerGroupManageAction(VirtualDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.vgroup.manage"), editor);
    }

    @Override
    public void execute(Event event) {
        final ERVirtualDiagram model = ((VirtualDiagramEditor) getEditorPart()).getModel();
        final ERVirtualDiagram newModel = (ERVirtualDiagram) model.clone();
        final WalkerGroupManageDialog dialog = new WalkerGroupManageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), newModel);
        if (dialog.open() == IDialogConstants.OK_ID) {
            execute(new ChangeVGroupCommand(model, newModel.getGroups()));
            //			ChangeSettingsCommand command = new ChangeSettingsCommand(diagram,
            //					settings);
            //			this.execute(command);
        }
    }
}
