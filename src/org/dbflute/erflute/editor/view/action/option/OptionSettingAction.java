package org.dbflute.erflute.editor.view.action.option;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.ChangeSettingsCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.option.OptionSettingDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class OptionSettingAction extends AbstractBaseAction {

    public static final String ID = OptionSettingAction.class.getName();

    public OptionSettingAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.option"), editor);
        setImageDescriptor(Activator.getImageDescriptor(ImageKey.OPTION));
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = getDiagram();
        final DiagramSettings settings = (DiagramSettings) diagram.getDiagramContents().getSettings().clone();

        final OptionSettingDialog dialog =
                new OptionSettingDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), settings, diagram);
        if (dialog.open() == IDialogConstants.OK_ID) {
            final ChangeSettingsCommand command = new ChangeSettingsCommand(diagram, settings);
            execute(command);
        }
    }
}
