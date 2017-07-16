package org.dbflute.erflute.editor.view.dialog.outline.tablespace;

import java.util.List;

import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public abstract class TablespaceDialog extends AbstractDialog {

    private Combo environmentCombo;
    private Text nameText;
    private Tablespace result;
    protected ERDiagram diagram;
    private Environment currentEnvironment;
    protected static final int NUM_TEXT_WIDTH = 60;

    public TablespaceDialog() {
        this(2);
    }

    public TablespaceDialog(int numColumns) {
        super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), numColumns);
    }

    public void init(Tablespace tablespace, ERDiagram diagram) {
        if (tablespace == null) {
            this.result = new Tablespace();
        } else {
            this.result = tablespace.clone();
        }

        this.diagram = diagram;
    }

    @Override
    protected void initComponent(Composite composite) {
        this.environmentCombo =
                CompositeFactory.createReadOnlyCombo(this, composite, "label.tablespace.environment", getNumColumns() - 1, -1);
        this.nameText =
                CompositeFactory.createText(this, composite, "label.tablespace.name", getNumColumns() - 1,
                        DesignResources.DESCRIPTION_WIDTH, false);
    }

    @Override
    protected String doValidate() {
        final String text = nameText.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.name.empty";
        }

        if (!Check.isAlphabet(text)) {
            return "error.tablespace.name.not.alphabet";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.tablespace";
    }

    @Override
    protected void performOK() {
        result.setName(nameText.getText().trim());
        final TablespaceProperties tablespaceProperties = setTablespaceProperties();
        result.putProperties(currentEnvironment, tablespaceProperties);
    }

    protected abstract TablespaceProperties setTablespaceProperties();

    @Override
    protected void setupData() {
        final List<Environment> environmentList = diagram.getDiagramContents().getSettings().getEnvironmentSettings().getEnvironments();

        for (final Environment environment : environmentList) {
            environmentCombo.add(environment.getName());
        }

        environmentCombo.select(0);
        currentEnvironment = environmentList.get(0);

        if (result.getName() != null) {
            nameText.setText(result.getName());
        }

        setPropertiesData();
    }

    private void setPropertiesData() {
        currentEnvironment = getSelectedEnvironment();
        final TablespaceProperties tablespaceProperties = result.getProperties(currentEnvironment, diagram);
        setData(tablespaceProperties);
    }

    protected abstract void setData(TablespaceProperties tablespaceProperties);

    public Tablespace getResult() {
        return result;
    }

    protected Environment getSelectedEnvironment() {
        final int index = environmentCombo.getSelectionIndex();
        final List<Environment> environmentList = diagram.getDiagramContents().getSettings().getEnvironmentSettings().getEnvironments();
        return environmentList.get(index);
    }

    @Override
    protected void addListener() {
        environmentCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                performOK();
                setPropertiesData();
            }
        });
    }
}
