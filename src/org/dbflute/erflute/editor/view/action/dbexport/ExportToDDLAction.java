package org.dbflute.erflute.editor.view.action.dbexport;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.ChangeSettingsCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.dbexport.ExportToDDLDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class ExportToDDLAction extends AbstractBaseAction {

    public static final String ID = ExportToDDLAction.class.getName();

    public ExportToDDLAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.export.ddl"), editor);
        this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.EXPORT_DDL));
    }

    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        ExportToDDLDialog dialog =
                new ExportToDDLDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram, this.getEditorPart(),
                        this.getGraphicalViewer());

        dialog.open();

        this.refreshProject();

        if (dialog.getExportSetting() != null
                && !diagram.getDiagramContents().getSettings().getExportSetting().equals(dialog.getExportSetting())) {
            Settings newSettings = (Settings) diagram.getDiagramContents().getSettings().clone();
            newSettings.setExportSetting(dialog.getExportSetting());

            ChangeSettingsCommand command = new ChangeSettingsCommand(diagram, newSettings);
            this.execute(command);
        }

    }

}
