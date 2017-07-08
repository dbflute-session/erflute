package org.dbflute.erflute.editor.view.dialog.view.tab;

import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class AdvancedComposite extends Composite {

    private Combo tableSpaceCombo;
    private Text schemaText;
    protected ViewProperties viewProperties;
    private ERDiagram diagram;

    public AdvancedComposite(Composite parent) {
        super(parent, SWT.NONE);
    }

    public final void initialize(ViewProperties viewProperties, ERDiagram diagram) {
        this.viewProperties = viewProperties;
        this.diagram = diagram;

        initComposite();
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

    private void initTablespaceCombo() {
        tableSpaceCombo.add("");

        for (final Tablespace tablespace : diagram.getDiagramContents().getTablespaceSet()) {
            tableSpaceCombo.add(tablespace.getName());
        }
    }

    protected void setData() {
        final Tablespace tablespace = viewProperties.getTableSpace();

        if (tablespace != null) {
            final int index = diagram.getDiagramContents().getTablespaceSet().getTablespaceList().indexOf(tablespace);
            tableSpaceCombo.select(index + 1);
        }

        if (viewProperties.getSchema() != null && schemaText != null) {
            schemaText.setText(viewProperties.getSchema());
        }
    }

    public boolean validate() {
        if (tableSpaceCombo != null) {
            final int tablespaceIndex = tableSpaceCombo.getSelectionIndex();
            if (tablespaceIndex > 0) {
                final Tablespace tablespace = diagram.getDiagramContents().getTablespaceSet().getTablespaceList().get(tablespaceIndex - 1);
                viewProperties.setTableSpace(tablespace);

            } else {
                viewProperties.setTableSpace(null);
            }
        }

        if (schemaText != null) {
            viewProperties.setSchema(schemaText.getText());
        }

        return true;
    }

    public void setInitFocus() {
        tableSpaceCombo.setFocus();
    }
}
