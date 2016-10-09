package org.dbflute.erflute.preference.jdbc;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.settings.JDBCDriverSetting;
import org.dbflute.erflute.preference.MultiFileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class JDBCPathDialog extends AbstractDialog {

    private MultiFileFieldEditor fileFieldEditor;
    private Combo databaseCombo;
    private Text driverClassNameText;
    private String database;
    private String driverClassName;
    private String path;
    private final List<JDBCDriverSetting> otherDriverSettingList;
    private boolean editable;

    public JDBCPathDialog(Shell parentShell, String database, String driverClassName, String path,
            List<JDBCDriverSetting> otherDriverSettingList, boolean editable) {
        super(parentShell, 3);

        this.database = database;
        this.driverClassName = driverClassName;
        this.path = path;

        this.otherDriverSettingList = otherDriverSettingList;
        this.editable = editable;
    }

    @Override
    protected Object createLayoutData() {
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = 600;
        gridData.heightHint = 180;
        gridData.horizontalIndent = 10;
        gridData.horizontalSpan = 10;
        return gridData;
    }

    @Override
    protected void initialize(Composite composite) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.heightHint = 50;

        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(gridData);
        label.setText(DisplayMessages.getMessage("label.jdbc.driver.message"));

        if (this.database != null) {
            final DBManager dbManager = DBManagerFactory.getDBManager(this.database);
            if (dbManager.getDriverClassName().equals(this.driverClassName) && !dbManager.getDriverClassName().equals("")) {
                this.editable = false;
            }
        }

        if (this.editable) {
            this.databaseCombo = CompositeFactory.createReadOnlyCombo(this, composite, "label.database", 2, -1);
            this.databaseCombo.setVisibleItemCount(10);
        } else {
            CompositeFactory.createLabel(composite, "label.database");
            CompositeFactory.createLabel(composite, this.database, 2);
        }

        this.driverClassNameText = CompositeFactory.createText(this, composite, "label.driver.class.name", 2, -1, SWT.BORDER, false);
        this.driverClassNameText.setEditable(editable);
        this.fileFieldEditor = new MultiFileFieldEditor("", DisplayMessages.getMessage("label.path"), composite);
        this.fileFieldEditor.setMultiple(true);
        this.fileFieldEditor.setFocus();
    }

    @Override
    protected String getTitle() {
        return "label.path";
    }

    @Override
    protected String doValidate() {
        String selectedDatabase = this.database;
        if (this.databaseCombo != null) {
            selectedDatabase = this.databaseCombo.getText();
            if (Check.isEmpty(selectedDatabase)) {
                return "error.database.name.is.empty";
            }
        }

        final String text = this.driverClassNameText.getText();
        if (Check.isEmpty(text)) {
            return "error.driver.class.name.is.empty";
        } else {
            final JDBCDriverSetting driverSetting = new JDBCDriverSetting(selectedDatabase, text, null);
            if (otherDriverSettingList.contains(driverSetting)) {
                return "error.driver.class.is.already.exist";
            }
        }
        return null;
    }

    @Override
    protected void performOK() throws InputException {
        this.path = fileFieldEditor.getStringValue();
        this.driverClassName = this.driverClassNameText.getText();
        if (this.databaseCombo != null) {
            this.database = this.databaseCombo.getText();
        }
    }

    @Override
    protected void setData() {
        this.fileFieldEditor.setStringValue(this.path);
        this.driverClassNameText.setText(Format.null2blank(this.driverClassName));
        if (this.databaseCombo != null) {
            for (final String db : DBManagerFactory.getAllDBList()) {
                this.databaseCombo.add(db);
            }
            this.databaseCombo.setText(Format.null2blank(this.database));
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public String getDatabase() {
        return database;
    }
}
