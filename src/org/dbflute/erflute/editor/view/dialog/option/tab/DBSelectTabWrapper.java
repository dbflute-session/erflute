package org.dbflute.erflute.editor.view.dialog.option.tab;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.dialog.option.OptionSettingDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.PlatformUI;

public class DBSelectTabWrapper extends ValidatableTabWrapper {

    private Combo databaseCombo;
    private final DiagramSettings settings;
    private final OptionSettingDialog dialog;

    public DBSelectTabWrapper(OptionSettingDialog dialog, TabFolder parent, int style, DiagramSettings settings) {
        super(dialog, parent, style, "label.database");

        this.settings = settings;
        this.dialog = dialog;

        init();
    }

    @Override
    public void initComposite() {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        setLayout(layout);

        this.databaseCombo = CompositeFactory.createReadOnlyCombo(null, this, "label.database");
        databaseCombo.setVisibleItemCount(10);

        for (final String db : DBManagerFactory.getAllDBList()) {
            databaseCombo.add(db);
        }

        databaseCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeDatabase();
            }
        });

        databaseCombo.setFocus();
    }

    @Override
    public void setupData() {
        for (int i = 0; i < databaseCombo.getItemCount(); i++) {
            final String database = databaseCombo.getItem(i);
            if (database.equals(settings.getDatabase())) {
                databaseCombo.select(i);
                break;
            }
        }
    }

    @Override
    public void validatePage() throws InputException {
        settings.setDatabase(databaseCombo.getText());
    }

    private void changeDatabase() {
        final MessageBox messageBox =
                new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
        messageBox.setText(DisplayMessages.getMessage("dialog.title.change.database"));
        messageBox.setMessage(DisplayMessages.getMessage("dialog.message.change.database"));

        if (messageBox.open() == SWT.OK) {
            final String database = databaseCombo.getText();
            settings.setDatabase(database);
            dialog.initTab();
        } else {
            setupData();
        }
    }

    @Override
    public void setInitFocus() {
        databaseCombo.setFocus();
    }

    @Override
    public void perfomeOK() {
    }
}
