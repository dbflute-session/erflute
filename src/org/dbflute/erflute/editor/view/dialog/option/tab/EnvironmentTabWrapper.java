package org.dbflute.erflute.editor.view.dialog.option.tab;

import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.dbflute.erflute.editor.view.dialog.option.OptionSettingDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class EnvironmentTabWrapper extends ValidatableTabWrapper {

    private List environmentList;
    private Text nameText;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private final DiagramSettings settings;

    private static final int LIST_HEIGHT = 230;

    public EnvironmentTabWrapper(OptionSettingDialog dialog, TabFolder parent, int style, DiagramSettings settings) {
        super(dialog, parent, style, "label.tablespace.environment");

        this.settings = settings;

        init();
    }

    @Override
    public void initComposite() {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        setLayout(layout);

        createEnvironmentGroup(this);

        final GridData gridData = new GridData();
        gridData.widthHint = 200;
        gridData.horizontalSpan = 3;

        this.nameText = new Text(this, SWT.BORDER);
        nameText.setLayoutData(gridData);

        final GridData buttonGridData = new GridData();
        buttonGridData.widthHint = DesignResources.BUTTON_WIDTH;

        this.addButton = new Button(this, SWT.NONE);
        addButton.setLayoutData(buttonGridData);
        addButton.setText(DisplayMessages.getMessage("label.button.add"));

        this.editButton = new Button(this, SWT.NONE);
        editButton.setLayoutData(buttonGridData);
        editButton.setText(DisplayMessages.getMessage("label.button.edit"));

        this.deleteButton = new Button(this, SWT.NONE);
        deleteButton.setLayoutData(buttonGridData);
        deleteButton.setText(DisplayMessages.getMessage("label.button.delete"));

        buttonEnabled(false);
        addButton.setEnabled(false);
    }

    private void createEnvironmentGroup(Composite parent) {
        final GridData gridData = new GridData();
        gridData.widthHint = 200;
        gridData.horizontalSpan = 3;
        gridData.heightHint = LIST_HEIGHT;

        this.environmentList = new List(parent, SWT.BORDER | SWT.V_SCROLL);
        environmentList.setLayoutData(gridData);
    }

    @Override
    protected void addListener() {
        environmentList.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final int targetIndex = environmentList.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }

                final Environment environment = settings.getEnvironmentSettings().getEnvironments().get(targetIndex);
                nameText.setText(environment.getName());
                buttonEnabled(true);
            }
        });

        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final String name = nameText.getText().trim();
                if (!Check.isEmpty(name)) {
                    settings.getEnvironmentSettings().getEnvironments().add(new Environment(name));
                    setupData();
                    environmentList.select(environmentList.getItemCount() - 1);
                }
            }
        });

        editButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final int targetIndex = environmentList.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }

                final String name = nameText.getText().trim();
                if (!Check.isEmpty(name)) {
                    final Environment environment = settings.getEnvironmentSettings().getEnvironments().get(targetIndex);
                    environment.setName(name);
                    setupData();
                    environmentList.select(targetIndex);
                }
            }
        });

        deleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final int targetIndex = environmentList.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }

                settings.getEnvironmentSettings().getEnvironments().remove(targetIndex);
                setupData();

                if (settings.getEnvironmentSettings().getEnvironments().size() > targetIndex) {
                    environmentList.select(targetIndex);
                    final Environment environment = settings.getEnvironmentSettings().getEnvironments().get(targetIndex);
                    nameText.setText(environment.getName());

                } else {
                    nameText.setText("");
                    buttonEnabled(false);
                }
            }
        });

        nameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                final String name = nameText.getText().trim();
                if (name.length() == 0) {
                    addButton.setEnabled(false);
                    editButton.setEnabled(false);

                } else {
                    addButton.setEnabled(true);
                    if (environmentList.getSelectionIndex() != -1) {
                        editButton.setEnabled(true);
                    } else {
                        editButton.setEnabled(false);
                    }
                }
            }
        });
    }

    private void buttonEnabled(boolean enabled) {
        editButton.setEnabled(enabled);

        if (environmentList.getItemCount() <= 1) {
            enabled = false;
        }
        deleteButton.setEnabled(enabled);
    }

    @Override
    public void validatePage() throws InputException {
    }

    @Override
    public void setInitFocus() {
        environmentList.setFocus();
    }

    @Override
    protected void setupData() {
        super.setupData();

        environmentList.removeAll();

        for (final Environment environment : settings.getEnvironmentSettings().getEnvironments()) {
            environmentList.add(environment.getName());
        }
    }

    @Override
    public void perfomeOK() {
    }
}
