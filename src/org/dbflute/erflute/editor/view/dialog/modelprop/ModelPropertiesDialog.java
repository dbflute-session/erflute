package org.dbflute.erflute.editor.view.dialog.modelprop;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.util.NameValue;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ModelPropertiesDialog extends AbstractDialog {

    private static final int BUTTON_WIDTH = 60;
    private Table table;
    private Button addButton;
    private Button deleteButton;
    private Button upButton;
    private Button downButton;
    private final ModelProperties modelProperties;
    private TableEditor tableEditor;
    private int targetColumn = -1;

    public ModelPropertiesDialog(Shell parentShell, ModelProperties modelProperties) {
        super(parentShell, 2);

        this.modelProperties = modelProperties;
    }

    @Override
    protected void initComponent(Composite composite) {
        createTableComposite(composite);
        createButtonComposite(composite);
    }

    /**
     * This method initializes composite1
     */
    private void createTableComposite(Composite parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;

        final GridData gridData = new GridData();
        gridData.heightHint = 320;

        final GridData tableGridData = new GridData();
        tableGridData.horizontalSpan = 3;
        tableGridData.heightHint = 185;

        final Composite composite = new Composite(parent, SWT.BORDER);
        composite.setLayout(gridLayout);
        composite.setLayoutData(gridData);

        table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLayoutData(tableGridData);
        table.setLinesVisible(true);

        final TableColumn tableColumn0 = new TableColumn(table, SWT.NONE);
        tableColumn0.setWidth(200);
        tableColumn0.setText(DisplayMessages.getMessage("label.property.name"));
        final TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
        tableColumn1.setWidth(200);
        tableColumn1.setText(DisplayMessages.getMessage("label.property.value"));

        tableEditor = new TableEditor(table);
        tableEditor.grabHorizontal = true;

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent event) {
                final int index = table.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                final TableItem item = table.getItem(index);
                final Point selectedPoint = new Point(event.x, event.y);

                targetColumn = -1;

                for (int i = 0; i < table.getColumnCount(); i++) {
                    final Rectangle rect = item.getBounds(i);
                    if (rect.contains(selectedPoint)) {
                        targetColumn = i;
                        break;
                    }
                }

                if (targetColumn == -1) {
                    return;
                }

                edit(item, tableEditor);
            }
        });
    }

    private void edit(final TableItem item, final TableEditor tableEditor) {
        final Text text = new Text(table, SWT.NONE);
        text.setText(item.getText(targetColumn));

        text.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                item.setText(targetColumn, text.getText());
                text.dispose();
            }
        });

        tableEditor.setEditor(text, item, targetColumn);
        text.setFocus();
        text.selectAll();
    }

    private void addRow() {
        final TableItem item = new TableItem(table, SWT.NULL);
        item.setText(0, "");
        item.setText(1, "");
        targetColumn = 0;

        edit(item, tableEditor);
    }

    /**
     * This method initializes composite2
     */
    private void createButtonComposite(Composite parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;

        final GridData gridData = new GridData();
        gridData.horizontalSpan = 2;

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(gridData);
        composite.setLayout(gridLayout);

        final GridData buttonGridData = new GridData();
        buttonGridData.widthHint = BUTTON_WIDTH;

        this.addButton = new Button(composite, SWT.NONE);
        addButton.setText(DisplayMessages.getMessage("label.button.add"));
        addButton.setLayoutData(buttonGridData);
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                addRow();
            }
        });

        this.deleteButton = new Button(composite, SWT.NONE);
        deleteButton.setText(DisplayMessages.getMessage("label.button.delete"));
        deleteButton.setLayoutData(buttonGridData);
        deleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                removeColumn();
            }
        });

        final Label filler = new Label(composite, SWT.NONE);
        final GridData fillerGridData = new GridData();
        fillerGridData.widthHint = 30;
        filler.setLayoutData(fillerGridData);

        this.upButton = new Button(composite, SWT.NONE);
        upButton.setText(DisplayMessages.getMessage("label.up.arrow"));
        upButton.setLayoutData(buttonGridData);
        upButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                upColumn();
            }
        });

        this.downButton = new Button(composite, SWT.NONE);
        downButton.setText(DisplayMessages.getMessage("label.down.arrow"));
        downButton.setLayoutData(buttonGridData);
        downButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                downColumn();
            }
        });
    }

    private void removeColumn() {
        final int index = table.getSelectionIndex();

        if (index != -1) {
            table.remove(index);
        }

        validate();
    }

    private void upColumn() {
        final int index = table.getSelectionIndex();

        if (index != -1 && index != 0) {
            changeColumn(index - 1, index);
            table.setSelection(index - 1);
        }
    }

    private void downColumn() {
        final int index = table.getSelectionIndex();

        if (index != -1 && index != table.getItemCount() - 1) {
            changeColumn(index, index + 1);
            table.setSelection(index + 1);
        }
    }

    private void changeColumn(int index1, int index2) {
        final TableItem item1 = table.getItem(index1);
        final TableItem item2 = table.getItem(index2);

        final String name1 = item1.getText(0);
        final String value1 = item1.getText(1);

        item1.setText(0, item2.getText(0));
        item1.setText(1, item2.getText(1));

        item2.setText(0, name1);
        item2.setText(1, value1);
    }

    @Override
    protected String doValidate() {
        return null;
    }

    @Override
    protected void performOK() {
        modelProperties.clear();

        for (int i = 0; i < table.getItemCount(); i++) {
            final TableItem item = table.getItem(i);

            if (Check.isEmpty(item.getText(0)) && Check.isEmpty(item.getText(1))) {
                continue;
            }

            final NameValue property = new NameValue(item.getText(0), item.getText(1));
            modelProperties.addProperty(property);
        }
    }

    @Override
    protected String getTitle() {
        return "label.search.range.model.property";
    }

    @Override
    protected void setupData() {
        for (final NameValue property : modelProperties.getProperties()) {
            final TableItem item = new TableItem(table, SWT.NULL);
            item.setText(0, Format.null2blank(property.getName()));
            item.setText(1, Format.null2blank(property.getValue()));
        }
    }
}
