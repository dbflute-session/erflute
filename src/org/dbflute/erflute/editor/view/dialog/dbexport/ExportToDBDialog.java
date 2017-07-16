package org.dbflute.erflute.editor.view.dialog.dbexport;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.db.ExportToDBManager;
import org.dbflute.erflute.editor.model.settings.DBSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ExportToDBDialog extends AbstractDialog {

    private Text textArea;

    private final DBSettings dbSetting;

    private final String ddl;

    public ExportToDBDialog(Shell parentShell, ERDiagram diagram, DBSettings dbSetting, String ddl) {
        super(parentShell);

        this.dbSetting = dbSetting;
        this.ddl = ddl;
    }

    @Override
    protected void initComponent(Composite composite) {
        this.textArea = CompositeFactory.createTextArea(null, composite, "dialog.message.export.db.sql", 600, 400, 1, false, false);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, DisplayMessages.getMessage("label.button.execute"), true);
    }

    @Override
    protected String doValidate() {
        if ("".equals(textArea.getText().trim())) {
            return "";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.export.db";
    }

    @Override
    protected void performOK() throws InputException {
        String executeDDL = textArea.getSelectionText();
        if (Check.isEmpty(executeDDL)) {
            executeDDL = textArea.getText();
        }

        if (!Activator.showConfirmDialog("dialog.message.export.db.confirm")) {
            return;
        }

        Connection con = null;

        try {
            con = dbSetting.connect();

            final ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());

            final ExportToDBManager exportToDBManager = new ExportToDBManager();
            exportToDBManager.init(con, executeDDL);

            try {
                dialog.run(true, true, exportToDBManager);

                final Exception e = exportToDBManager.getException();
                if (e != null) {
                    Activator.showMessageDialog(e.getMessage());
                    throw new InputException(null);
                } else {
                    Activator.showMessageDialog("dialog.message.export.db.finish");
                }
            } catch (final InvocationTargetException e) {} catch (final InterruptedException e) {}
        } catch (final InputException e) {
            throw e;
        } catch (final Exception e) {
            Activator.error(e);
            final Throwable cause = e.getCause();

            if (cause instanceof UnknownHostException) {
                throw new InputException("error.server.not.found");
            }

            Activator.showMessageDialog(e.getMessage());
            throw new InputException("error.database.not.found");
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (final SQLException e) {
                    Activator.showExceptionDialog(e);
                }
            }
        }
    }

    @Override
    protected void setupData() {
        textArea.setText(ddl);
    }
}
