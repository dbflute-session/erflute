package org.dbflute.erflute.editor.view.dialog.table.tab;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public abstract class AdvancedComposite extends Composite {

    private Combo tableSpaceCombo;
    private Text schemaText;
    protected TableProperties tableProperties;
    protected ERDiagram diagram;
    protected AbstractDialog dialog;
    protected ERTable table;

    public AdvancedComposite(Composite parent) {
        super(parent, SWT.NONE);
    }

    public final void initialize(AbstractDialog dialog, TableProperties tableProperties, ERDiagram diagram, ERTable table) {
        this.dialog = dialog;
        this.tableProperties = tableProperties;
        this.diagram = diagram;
        this.table = table;

        initComposite();
        addListener();
        setData();
    }

    protected void initComposite() {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        setLayout(gridLayout);

        this.tableSpaceCombo = CompositeFactory.createReadOnlyCombo(null, this, "label.tablespace");
        this.schemaText = CompositeFactory.createText(null, this, "label.schema", 1, 120, false);

        initTablespaceCombo();
    }

    protected void addListener() {
    }

    private void initTablespaceCombo() {
        tableSpaceCombo.add("");

        for (final Tablespace tablespace : diagram.getDiagramContents().getTablespaceSet()) {
            tableSpaceCombo.add(tablespace.getName());
        }
    }

    protected void setData() {
        final Tablespace tablespace = tableProperties.getTableSpace();

        if (tablespace != null) {
            final int index = diagram.getDiagramContents().getTablespaceSet().getTablespaceList().indexOf(tablespace);
            tableSpaceCombo.select(index + 1);
        }

        if (tableProperties.getSchema() != null && schemaText != null) {
            schemaText.setText(tableProperties.getSchema());
        }
    }

    public void validate() throws InputException {
        if (tableSpaceCombo != null) {
            final int tablespaceIndex = tableSpaceCombo.getSelectionIndex();
            if (tablespaceIndex > 0) {
                final Tablespace tablespace = diagram
                        .getDiagramContents()
                        .getTablespaceSet()
                        .getTablespaceList()
                        .get(tablespaceIndex - 1);
                tableProperties.setTableSpace(tablespace);
            } else {
                tableProperties.setTableSpace(null);
            }
        }

        if (schemaText != null) {
            tableProperties.setSchema(schemaText.getText());
        }
    }

    public void setInitFocus() {
        tableSpaceCombo.setFocus();
    }
}
