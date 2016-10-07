package org.dbflute.erflute.editor.view.action.ermodel;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.RealModelEditor;
import org.dbflute.erflute.editor.controller.command.ermodel.AddERModelCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class ERModelAddAction extends AbstractBaseAction {

    public static final String ID = ERModelAddAction.class.getName();

    public ERModelAddAction(RealModelEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.ermodel.add"), editor);
    }

    @Override
    public void execute(Event event) throws Exception {
        ERDiagram diagram = this.getDiagram();

        Settings settings = (Settings) diagram.getDiagramContents().getSettings().clone();

        InputDialog dialog =
                new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "�_�C�A�O�����쐬",
                        "�_�C�A�O����������͂��ĉ������B", "", null);
        if (dialog.open() == IDialogConstants.OK_ID) {
            AddERModelCommand command = new AddERModelCommand(diagram, dialog.getValue());
            this.execute(command);
        }

        //		CategoryManageDialog dialog = new CategoryManageDialog(PlatformUI
        //				.getWorkbench().getActiveWorkbenchWindow().getShell(),
        //				settings, diagram);
        //
        //		if (dialog.open() == IDialogConstants.OK_ID) {
        //			ChangeSettingsCommand command = new ChangeSettingsCommand(diagram,
        //					settings);
        //			this.execute(command);
        //		}
    }

}
