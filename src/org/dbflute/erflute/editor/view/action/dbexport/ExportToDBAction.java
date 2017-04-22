package org.dbflute.erflute.editor.view.action.dbexport;

import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.Validator;
import org.dbflute.erflute.editor.model.settings.DBSettings;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.dbexport.ExportDBSettingDialog;
import org.dbflute.erflute.editor.view.dialog.dbexport.ExportErrorDialog;
import org.dbflute.erflute.editor.view.dialog.dbexport.ExportToDBDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RetargetAction;

public class ExportToDBAction extends AbstractBaseAction {

    public static final String ID = ExportToDBAction.class.getName();

    private Validator validator;

    public ExportToDBAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.export.db"), editor);

        this.validator = new Validator();
    }

    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        List<ValidateResult> errorList = validator.validate(diagram);

        if (!errorList.isEmpty()) {
            ExportErrorDialog dialog = new ExportErrorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), errorList);
            dialog.open();
            return;
        }

        ExportDBSettingDialog dialog = new ExportDBSettingDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

        if (dialog.open() == IDialogConstants.OK_ID) {
            String ddl = dialog.getDdl();

            DBSettings dbSetting = dialog.getDbSetting();

            ExportToDBDialog exportToDBDialog =
                    new ExportToDBDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram, dbSetting, ddl);

            exportToDBDialog.open();
        }
    }

    public static class ExportToDBRetargetAction extends RetargetAction {

        public ExportToDBRetargetAction() {
            super(ID, DisplayMessages.getMessage("action.title.export.db"));

            this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.EXPORT_TO_DB));
            this.setToolTipText(this.getText());
        }
    }
}
