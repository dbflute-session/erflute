package org.dbflute.erflute.editor.view.dialog.dbsetting;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.ListenerAppender;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.db.impl.standard_sql.StandardSQLDBManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DBSettings;
import org.dbflute.erflute.preference.PreferenceInitializer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractDBSettingDialog extends AbstractDialog {

    private Text userName;
    private Text password;
    private Combo dbList;
    private Text serverName;
    private Text port;
    private Text dbName;
    private Text url;
    private Button useDefaultDriverButton;
    private Text driverClassName;
    private Button settingListButton;
    protected Button settingAddButton;
    protected DBSettings dbSettings;
    protected ERDiagram diagram;

    public AbstractDBSettingDialog(Shell parentShell, ERDiagram diagram) {
        super(parentShell, 2);
        this.diagram = diagram;
    }

    @Override
    protected void initComponent(Composite parent) {
        final Composite group = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;

        group.setLayout(layout);
        final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);

        initializeBody(group);

        setDBList();
    }

    private void setDBList() {
        if (isOnlyCurrentDatabase()) {
            dbList.add(diagram.getDatabase());
            dbList.select(0);
        } else {
            for (final String db : DBManagerFactory.getAllDBList()) {
                dbList.add(db);
            }

            dbList.setVisibleItemCount(20);
        }
    }

    protected void initializeBody(Composite group) {
        this.dbList = CompositeFactory.createReadOnlyCombo(this, group, "label.database");
        dbList.setFocus();

        this.serverName = CompositeFactory.createText(this, group, "label.server.name", false);
        this.port = CompositeFactory.createText(this, group, "label.port", false);
        this.dbName = CompositeFactory.createText(this, group, "label.database.name", false);
        this.userName = CompositeFactory.createText(this, group, "label.user.name", false);
        this.password = CompositeFactory.createText(this, group, "label.user.password", false);
        password.setEchoChar('*');

        CompositeFactory.filler(group, 2);

        this.useDefaultDriverButton = CompositeFactory.createCheckbox(this, group, "label.use.default.driver", 2);

        this.url = CompositeFactory.createText(null, group, "label.url", 1, -1, SWT.BORDER | SWT.READ_ONLY, false);

        this.driverClassName =
                CompositeFactory.createText(null, group, "label.driver.class.name", 1, -1, SWT.BORDER | SWT.READ_ONLY, false);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, 0, IDialogConstants.NEXT_LABEL, true);
        createButton(parent, 1, IDialogConstants.CANCEL_LABEL, false);
        this.settingListButton =
                createButton(parent, IDialogConstants.OPEN_ID, DisplayMessages.getMessage("label.load.database.setting"), false);
        this.settingAddButton =
                createButton(parent, IDialogConstants.YES_ID, DisplayMessages.getMessage("label.load.database.setting.add"), false);
    }

    public String getDBSName() {
        return dbList.getText().trim();
    }

    public String getDBName() {
        return dbName.getText().trim();
    }

    public String getServerName() {
        return serverName.getText().trim();
    }

    public int getPort() {
        final String port = this.port.getText().trim();

        try {
            return Integer.parseInt(port);
        } catch (final Exception e) {
            return 0;
        }
    }

    public String getUserName() {
        return userName.getText().trim();
    }

    public String getPassword() {
        return password.getText().trim();
    }

    @Override
    protected String doValidate() {
        DBManager manager = null;

        final String database = getDBSName();

        if (!Check.isEmpty(database)) {
            if (!useDefaultDriverButton.getSelection()) {
                if (isBlank(url)) {
                    return "error.url.is.empty";
                }
                if (isBlank(driverClassName)) {
                    return "error.driver.class.name.is.empty";
                }
            } else {
                manager = DBManagerFactory.getDBManager(getDBSName());
                final String url = manager.getURL(getServerName(), getDBName(), getPort());
                this.url.setText(url);

                if (isBlank(serverName) && manager.doesNeedURLServerName()) {
                    return "error.server.is.empty";
                }

                if (isBlank(port) && manager.doesNeedURLServerName()) {
                    return "error.port.is.empty";
                }

                if (isBlank(dbName) && manager.doesNeedURLDatabaseName()) {
                    return "error.database.name.is.empty";
                }
            }
        }

        if (settingAddButton != null) {
            settingAddButton.setEnabled(false);
        }

        if (isBlank(dbList)) {
            return "error.database.not.selected";
        }

        final String text = port.getText();
        if (!text.equals("")) {
            try {
                final int port = Integer.parseInt(text);
                if (port < 0) {
                    return "error.port.zero";
                }
            } catch (final NumberFormatException e) {
                return "error.port.degit";
            }
        }

        if (isBlank(userName)) {
            return "error.user.name.is.empty";
        }

        if (settingAddButton != null) {
            settingAddButton.setEnabled(true);
        }

        return null;
    }

    @Override
    protected void setupData() {
        if (dbSettings != null) {
            final String database = dbSettings.getDbsystem();
            dbList.setText(database);

            enableUseDefaultDriver();
            enableField();

            this.serverName.setText(Format.null2blank(dbSettings.getServer()));
            this.port.setText(String.valueOf(dbSettings.getPort()));
            this.dbName.setText(Format.null2blank(dbSettings.getDatabase()));
            this.userName.setText(Format.null2blank(dbSettings.getUser()));
            this.password.setText(Format.null2blank(dbSettings.getPassword()));
            this.url.setText(Format.null2blank(dbSettings.getUrl()));
            this.driverClassName.setText(Format.null2blank(dbSettings.getDriverClassName()));

            if (!Check.isEmpty(database) && useDefaultDriverButton.getSelection()) {
                final DBManager manager = DBManagerFactory.getDBManager(getDBSName());
                final String url = manager.getURL(getServerName(), getDBName(), getPort());
                this.url.setText(url);

                final String driverClassName = manager.getDriverClassName();
                this.driverClassName.setText(driverClassName);
            }
        }
    }

    @Override
    protected int getErrorLine() {
        return 2;
    }

    public DBSettings getDbSetting() {
        return dbSettings;
    }

    private void enableUseDefaultDriver() {
        final String database = getDBSName();

        if (!Check.isEmpty(database)) {
            final DBManager dbManager = DBManagerFactory.getDBManager(database);

            if (StandardSQLDBManager.ID.equals(dbManager.getId())) {
                useDefaultDriverButton.setSelection(false);
                useDefaultDriverButton.setEnabled(false);
            } else {
                useDefaultDriverButton.setSelection(true);
                useDefaultDriverButton.setEnabled(true);
            }
        }
    }

    private void enableField() {
        final String database = getDBSName();

        if (useDefaultDriverButton.getSelection()) {
            final DBManager dbManager = DBManagerFactory.getDBManager(database);

            dbName.setEnabled(true);
            url.setEditable(false);
            driverClassName.setEditable(false);
            driverClassName.setText(dbManager.getDriverClassName());

            if (dbManager.doesNeedURLServerName()) {
                port.setText(String.valueOf(dbManager.getDefaultPort()));
                port.setEnabled(true);
                serverName.setEnabled(true);
            } else {
                port.setEnabled(false);
                serverName.setEnabled(false);
            }
        } else {
            port.setEnabled(false);
            serverName.setEnabled(false);
            dbName.setEnabled(false);
            url.setEditable(true);
            driverClassName.setEditable(true);
        }
    }

    @Override
    protected void addListener() {
        super.addListener();

        dbList.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                enableUseDefaultDriver();
                enableField();
                validate();
            }
        });

        useDefaultDriverButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent selectionevent) {
                enableField();
                validate();
            }
        });

        ListenerAppender.addModifyListener(serverName, this);
        ListenerAppender.addModifyListener(port, this);
        ListenerAppender.addModifyListener(dbName, this);
        ListenerAppender.addModifyListener(userName, this);
        ListenerAppender.addModifyListener(driverClassName, this);

        url.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (!useDefaultDriverButton.getSelection()) {
                    validate();
                }
            }
        });

        settingListButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    String database = null;
                    if (isOnlyCurrentDatabase()) {
                        database = diagram.getDatabase();
                    }
                    final DBSettingListDialog dialog =
                            new DBSettingListDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), database);
                    if (dialog.open() == IDialogConstants.OK_ID) {
                        dbSettings = dialog.getResult();
                        setupData();
                    }
                } catch (final Exception ex) {
                    Activator.showExceptionDialog(ex);
                }
            }
        });

        settingAddButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (validate()) {
                        setCurrentSetting();

                        PreferenceInitializer.addDBSetting(dbSettings);

                        Activator.showMessageDialog("dialog.message.add.to.connection.list");
                    }
                } catch (final Exception ex) {
                    Activator.showExceptionDialog(ex);
                }
            }
        });
    }

    protected boolean isOnlyCurrentDatabase() {
        return false;
    }

    protected void setCurrentSetting() {
        final String database = getDBSName();
        final String url = this.url.getText().trim();
        final String driverClassName = this.driverClassName.getText().trim();
        String serverName = getServerName();
        int port = getPort();
        String dbName = getDBName();
        final boolean useDefaultDriver = useDefaultDriverButton.getSelection();

        if (!useDefaultDriver) {
            serverName = null;
            port = 0;
            dbName = null;
        }

        this.dbSettings = new DBSettings(database, serverName, port, dbName,
                getUserName(), getPassword(), useDefaultDriver, url, driverClassName);

        PreferenceInitializer.saveSetting(0, dbSettings);
    }
}
