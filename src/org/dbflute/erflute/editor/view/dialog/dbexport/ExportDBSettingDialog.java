package org.dbflute.erflute.editor.view.dialog.dbexport;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.db.PreTableExportManager;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.dbflute.erflute.editor.view.dialog.dbsetting.AbstractDBSettingDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ExportDBSettingDialog extends AbstractDBSettingDialog {

    private Combo environmentCombo;

    private String ddl;

    public ExportDBSettingDialog(Shell parentShell, ERDiagram diagram) {
        super(parentShell, diagram);
        this.dbSettings = this.diagram.getDbSettings();
    }

    @Override
    protected void initializeBody(Composite group) {
        final GridData labelLayoutData = new GridData();
        // labelLayoutData.widthHint = 130;

        // DB
        final Label label = new Label(group, SWT.NONE);
        label.setLayoutData(labelLayoutData);
        label.setText(DisplayMessages.getMessage("label.tablespace.environment"));
        label.setEnabled(true);

        this.environmentCombo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
        final GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = 200;
        environmentCombo.setLayoutData(data);
        environmentCombo.setVisibleItemCount(20);
        environmentCombo.setEnabled(true);

        super.initializeBody(group);
    }

    @Override
    protected void initComponent(Composite parent) {
        super.initComponent(parent);
    }

    @Override
    protected String doValidate() {
        if (settingAddButton != null) {
            settingAddButton.setEnabled(false);
        }

        if (isBlank(environmentCombo)) {
            return "error.tablespace.environment.empty";
        }

        if (!diagram.getDatabase().equals(getDBSName())) {
            return "error.database.not.correct";
        }

        return super.doValidate();
    }

    @Override
    protected void performOK() throws InputException {
        setCurrentSetting();

        final String db = getDBSName();
        final DBManager manager = DBManagerFactory.getDBManager(db);

        Connection con = null;

        try {
            diagram.setDbSettings(dbSettings);

            con = dbSettings.connect();

            final int index = environmentCombo.getSelectionIndex();
            final Environment environment =
                    diagram.getDiagramContents().getSettings().getEnvironmentSettings().getEnvironments().get(index);

            final PreTableExportManager exportToDBManager = manager.getPreTableExportManager();
            exportToDBManager.init(con, dbSettings, diagram, environment);

            exportToDBManager.run();

            final Exception e = exportToDBManager.getException();
            if (e != null) {
                Activator.error(e);
                String message = e.getMessage();
                final String errorSql = exportToDBManager.getErrorSql();

                if (errorSql != null) {
                    message += "\r\n\r\n" + errorSql;
                }
                final ErrorDialog errorDialog = new ErrorDialog(getShell(), message);
                errorDialog.open();

                throw new InputException("error.jdbc.version");
            }

            this.ddl = exportToDBManager.getDdl();
        } catch (final InputException e) {
            throw e;
        } catch (final Exception e) {
            Activator.error(e);
            final Throwable cause = e.getCause();

            if (cause instanceof UnknownHostException) {
                throw new InputException("error.server.not.found");
            }

            Activator.showExceptionDialog(e);
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
        return "dialog.title.export.db";
    }

    @Override
    protected void setupData() {
        super.setupData();

        final DiagramSettings settings = diagram.getDiagramContents().getSettings();
        for (final Environment environment : settings.getEnvironmentSettings().getEnvironments()) {
            environmentCombo.add(environment.getName());
        }
        environmentCombo.select(0);
    }

    @Override
    protected boolean isOnlyCurrentDatabase() {
        return true;
    }

    public String getDdl() {
        return ddl;
    }

    @Override
    protected void addListener() {
        super.addListener();

        environmentCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                validate();
            }
        });
    }
}
