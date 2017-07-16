package org.dbflute.erflute.editor.view.dialog.dbimport;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.dialog.dbsetting.AbstractDBSettingDialog;
import org.dbflute.erflute.preference.PreferenceInitializer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ImportDBSettingDialog extends AbstractDBSettingDialog {

    public ImportDBSettingDialog(Shell parentShell, ERDiagram diagram) {
        super(parentShell, diagram);
    }

    @Override
    protected void initComponent(Composite parent) {
        super.initComponent(parent);
        this.dbSettings = PreferenceInitializer.getDBSetting(0);
    }

    @Override
    protected void performOK() throws InputException {
        setCurrentSetting();

        Connection con = null;
        try {
            con = dbSettings.connect();
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
    protected String getTitle() {
        return "dialog.title.import.tables";
    }
}
