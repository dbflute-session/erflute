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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initComponent(Composite parent) {
        super.initComponent(parent);
        this.dbSettings = PreferenceInitializer.getDBSetting(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performOK() throws InputException {
        this.setCurrentSetting();

        Connection con = null;

        try {
            con = this.dbSettings.connect();

        } catch (InputException e) {
            throw e;

        } catch (Exception e) {
            Activator.error(e);
            Throwable cause = e.getCause();

            if (cause instanceof UnknownHostException) {
                throw new InputException("error.server.not.found");
            }

            Activator.showMessageDialog(e.getMessage());
            throw new InputException("error.database.not.found");

        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Activator.showExceptionDialog(e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTitle() {
        return "dialog.title.import.tables";
    }

}
