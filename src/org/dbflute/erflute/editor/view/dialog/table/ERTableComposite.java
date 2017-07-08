package org.dbflute.erflute.editor.view.dialog.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.view.dialog.column.AbstractColumnDialog;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERTableComposite extends Composite {

    private static final int DEFAULT_HEIGHT = 200;
    private static final int KEY_WIDTH = 45;
    public static final int NAME_WIDTH = 150;
    private static final int TYPE_WIDTH = 100;
    private static final int NOT_NULL_WIDTH = 80;
    public static final int UNIQUE_KEY_WIDTH = 70;

    private Table table;
    private Button columnAddButton;
    private Button columnEditButton;
    private Button columnDeleteButton;
    private Button upButton;
    private Button downButton;
    private final ERDiagram diagram;
    private final ERTable ertable;
    private List<ERColumn> columnList;
    private final AbstractColumnDialog columnDialog;
    private final AbstractDialog parentDialog;
    private final Map<ERColumn, TableEditor[]> columnNotNullCheckMap = new HashMap<>();
    private final boolean buttonDisplay;
    private final boolean checkboxEnabled;
    private final int height;

    private final ERTableCompositeHolder holder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ERTableComposite(ERTableCompositeHolder holder, Composite parent, ERDiagram diagram, ERTable erTable, List<ERColumn> columnList,
            AbstractColumnDialog columnDialog, AbstractDialog parentDialog, int horizontalSpan, boolean buttonDisplay,
            boolean checkboxEnabled) {
        this(holder, parent, diagram, erTable, columnList, columnDialog, parentDialog, horizontalSpan, buttonDisplay, checkboxEnabled,
                DEFAULT_HEIGHT);
    }

    public ERTableComposite(ERTableCompositeHolder holder, Composite parent, ERDiagram diagram, ERTable erTable, List<ERColumn> columnList,
            AbstractColumnDialog columnDialog, AbstractDialog parentDialog, int horizontalSpan, boolean buttonDisplay,
            boolean checkboxEnabled, int height) {
        super(parent, SWT.NONE);

        this.holder = holder;
        this.height = height;
        this.buttonDisplay = buttonDisplay;
        this.checkboxEnabled = checkboxEnabled;

        this.diagram = diagram;
        this.ertable = erTable;
        this.columnList = columnList;

        this.columnDialog = columnDialog;
        this.parentDialog = parentDialog;

        final GridData gridData = new GridData();
        gridData.horizontalSpan = horizontalSpan;
        setLayoutData(gridData);

        createComposite();
        initComposite();
    }

    private void createComposite() {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;

        setLayout(gridLayout);

        createTable();

        if (buttonDisplay) {
            createButton();
            setButtonEnabled(false);
        }
    }

    //	private TableItem[] currentItems = null;

    private void createTable() {
        this.table = CompositeFactory.createTable(this, height, 3);
        CompositeFactory.createTableColumn(table, "PK", KEY_WIDTH, SWT.CENTER);
        CompositeFactory.createTableColumn(table, "FK", KEY_WIDTH, SWT.CENTER);
        CompositeFactory.createTableColumn(table, "label.physical.name", NAME_WIDTH, SWT.NONE);
        CompositeFactory.createTableColumn(table, "label.logical.name", NAME_WIDTH, SWT.NONE);
        CompositeFactory.createTableColumn(table, "label.column.type", TYPE_WIDTH, SWT.NONE);
        CompositeFactory.createTableColumn(table, "label.not.null", NOT_NULL_WIDTH, SWT.NONE);
        CompositeFactory.createTableColumn(table, "label.unique.key", UNIQUE_KEY_WIDTH, SWT.NONE);
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = table.getSelectionIndex();
                selectTable(index);
                final ERColumn selectedColumn = columnList.get(index);
                if (selectedColumn instanceof ColumnGroup) {
                    holder.selectGroup((ColumnGroup) selectedColumn);
                }
            }
        });
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.SPACE) {
                    final ERColumn targetColumn = getTargetColumn();
                    if (targetColumn == null || !(targetColumn instanceof CopyColumn)) {
                        return;
                    }
                    addOrEditColumn((CopyColumn) targetColumn, false);
                }
            }
        });
        if (buttonDisplay) {
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDoubleClick(MouseEvent e) {
                    final ERColumn targetColumn = getTargetColumn();
                    if (targetColumn == null || !(targetColumn instanceof CopyColumn)) {
                        return;
                    }
                    addOrEditColumn((CopyColumn) targetColumn, false);
                }
            });
        }
    }

    /**
     * This method initializes composite2
     *
     */
    private void createButton() {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 8;

        final GridData gridData = new GridData();
        gridData.horizontalSpan = 2;

        final Composite buttonComposite = new Composite(this, SWT.NONE);
        buttonComposite.setLayoutData(gridData);
        buttonComposite.setLayout(gridLayout);

        this.columnAddButton = CompositeFactory.createButton(buttonComposite, "label.button.add");
        columnAddButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                addOrEditColumn(null, true);
            }
        });

        this.columnEditButton = CompositeFactory.createButton(buttonComposite, "label.button.edit");
        columnEditButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final ERColumn targetColumn = getTargetColumn();

                if (targetColumn == null || !(targetColumn instanceof CopyColumn)) {
                    return;
                }

                addOrEditColumn((CopyColumn) targetColumn, false);
            }
        });

        this.columnDeleteButton = CompositeFactory.createButton(buttonComposite, "label.button.delete");
        columnDeleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = table.getSelectionIndex();

                removeColumn();

                if (index >= table.getItemCount()) {
                    index = table.getItemCount() - 1;
                }

                selectTable(index);
            }
        });

        CompositeFactory.filler(buttonComposite, 1, 30);

        this.upButton = CompositeFactory.createButton(buttonComposite, "label.up.arrow");
        upButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                upColumn();
            }
        });

        this.downButton = CompositeFactory.createButton(buttonComposite, "label.down.arrow");
        downButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                downColumn();
            }
        });

        CompositeFactory.filler(buttonComposite, 1, 30);
    }

    private void initComposite() {
        if (columnList != null) {
            for (final ERColumn column : columnList) {
                final TableItem tableItem = new TableItem(table, SWT.NONE);
                column2TableItem(column, tableItem);
            }
        }
    }

    private void disposeCheckBox(ERColumn column) {
        final TableEditor[] oldEditors = columnNotNullCheckMap.get(column);

        if (oldEditors != null) {
            for (final TableEditor oldEditor : oldEditors) {
                if (oldEditor.getEditor() != null) {
                    oldEditor.getEditor().dispose();
                    oldEditor.dispose();
                }
            }

            columnNotNullCheckMap.remove(column);
        }
    }

    private void column2TableItem(ERColumn column, TableItem tableItem) {
        disposeCheckBox(column);

        if (column instanceof NormalColumn) {
            tableItem.setBackground(ColorConstants.white);

            final NormalColumn normalColumn = (NormalColumn) column;

            if (normalColumn.isPrimaryKey()) {
                tableItem.setImage(0, Activator.getImage(ImageKey.PRIMARY_KEY));
            } else {
                tableItem.setImage(0, null);
            }

            if (normalColumn.isForeignKey()) {
                tableItem.setImage(1, Activator.getImage(ImageKey.FOREIGN_KEY));
            } else {
                tableItem.setImage(1, null);
            }

            tableItem.setText(2, Format.null2blank(normalColumn.getPhysicalName()));
            tableItem.setText(3, Format.null2blank(normalColumn.getLogicalName()));

            final SqlType sqlType = normalColumn.getType();

            tableItem.setText(4, Format.formatType(sqlType, normalColumn.getTypeData(), diagram.getDatabase()));

            setTableEditor(normalColumn, tableItem);
        } else {
            tableItem.setBackground(ColorConstants.white);
            tableItem.setImage(0, Activator.getImage(ImageKey.GROUP));
            tableItem.setImage(1, null);
            tableItem.setText(2, column.getName());
            tableItem.setText(3, "");
            tableItem.setText(4, "");
        }
    }

    private void setTableEditor(final NormalColumn normalColumn, TableItem tableItem) {

        final Button notNullCheckButton = new Button(table, SWT.CHECK);
        notNullCheckButton.pack();

        final Button uniqueCheckButton = new Button(table, SWT.CHECK);
        uniqueCheckButton.pack();

        final TableEditor[] editors = new TableEditor[2];

        editors[0] = new TableEditor(table);

        editors[0].minimumWidth = notNullCheckButton.getSize().x;
        editors[0].horizontalAlignment = SWT.CENTER;
        editors[0].setEditor(notNullCheckButton, tableItem, 5);

        editors[1] = new TableEditor(table);

        editors[1].minimumWidth = uniqueCheckButton.getSize().x;
        editors[1].horizontalAlignment = SWT.CENTER;
        editors[1].setEditor(uniqueCheckButton, tableItem, 6);

        if (normalColumn.isNotNull()) {
            notNullCheckButton.setSelection(true);
        } else {
            notNullCheckButton.setSelection(false);
        }
        if (normalColumn.isUniqueKey()) {
            uniqueCheckButton.setSelection(true);
        } else {
            uniqueCheckButton.setSelection(false);
        }

        if (normalColumn.isPrimaryKey()) {
            notNullCheckButton.setEnabled(false);
        }

        if (ertable != null) {
            if (normalColumn.isRefered()) {
                uniqueCheckButton.setEnabled(false);
            }
        }

        columnNotNullCheckMap.put(normalColumn, editors);

        if (checkboxEnabled) {
            notNullCheckButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    normalColumn.setNotNull(notNullCheckButton.getSelection());
                    super.widgetSelected(e);
                }
            });

            uniqueCheckButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    normalColumn.setUniqueKey(uniqueCheckButton.getSelection());
                    super.widgetSelected(e);
                }
            });
        } else {
            notNullCheckButton.setEnabled(false);
            uniqueCheckButton.setEnabled(false);
        }
    }

    private void addTableData(NormalColumn column, boolean add) {
        final int index = table.getSelectionIndex();
        TableItem tableItem = null;
        CopyColumn copyColumn = null;
        if (add) {
            tableItem = new TableItem(table, SWT.NONE);
            copyColumn = new CopyColumn(column);
            columnList.add(copyColumn);

        } else {
            tableItem = table.getItem(index);

            copyColumn = (CopyColumn) columnList.get(index);
            CopyColumn.copyData(column, copyColumn);
        }

        column2TableItem(copyColumn, tableItem);

        parentDialog.validate();
    }

    public void addTableData(ColumnGroup column) {
        TableItem tableItem = null;
        tableItem = new TableItem(table, SWT.NONE);

        columnList.add(column);
        column2TableItem(column, tableItem);

        parentDialog.validate();
    }

    private void removeColumn() {
        final int index = table.getSelectionIndex();

        if (index != -1) {
            final ERColumn column = columnList.get(index);

            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (normalColumn.isForeignKey()) {
                    setMessage(DisplayMessages.getMessage("error.foreign.key.not.deleteable"));
                } else {
                    if (ertable != null && normalColumn.isRefered()) {
                        setMessage(DisplayMessages.getMessage("error.reference.key.not.deleteable"));
                    } else {
                        removeColumn(index);
                    }
                }
            } else {
                removeColumn(index);
            }
        }

        parentDialog.validate();
    }

    public void removeColumn(int index) {
        ERColumn column = columnList.get(index);
        table.remove(index);
        columnList.remove(index);
        disposeCheckBox(column);

        for (int i = index; i < table.getItemCount(); i++) {
            final TableItem tableItem = table.getItem(i);
            column = columnList.get(i);

            disposeCheckBox(column);

            if (column instanceof NormalColumn) {
                setTableEditor((NormalColumn) column, tableItem);
            }
        }
    }

    private CopyColumn getTargetColumn() {
        CopyColumn column = null;

        final int index = table.getSelectionIndex();

        if (index != -1) {
            column = (CopyColumn) columnList.get(index);
        }

        return column;
    }

    private void setMessage(String message) {
        final MessageBox messageBox =
                new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.OK);
        messageBox.setText(DisplayMessages.getMessage("dialog.title.error"));
        messageBox.setMessage(message);
        messageBox.open();
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
        final ERColumn column1 = columnList.remove(index1);
        ERColumn column2 = null;

        if (index1 < index2) {
            column2 = columnList.remove(index2 - 1);
            columnList.add(index1, column2);
            columnList.add(index2, column1);

        } else if (index1 > index2) {
            column2 = columnList.remove(index2);
            columnList.add(index1 - 1, column2);
            columnList.add(index2, column1);
        }

        final TableItem[] tableItems = table.getItems();

        column2TableItem(column1, tableItems[index2]);
        column2TableItem(column2, tableItems[index1]);
    }

    private void addOrEditColumn(CopyColumn targetColumn, boolean add) {
        boolean foreignKey = false;
        boolean isRefered = false;
        if (targetColumn != null) {
            foreignKey = targetColumn.isForeignKey();
            if (ertable != null) {
                isRefered = targetColumn.isRefered();
            }
        }
        columnDialog.setTargetColumn(targetColumn, foreignKey, isRefered);
        if (columnDialog.open() == IDialogConstants.OK_ID) {
            final NormalColumn column = columnDialog.getColumn();
            addTableData(column, add);
        }
    }

    public Table getTable() {
        return table;
    }

    public void setColumnList(List<ERColumn> columnList) {
        table.removeAll();
        if (columnList != null) {
            for (final ERColumn column : columnList) {
                disposeCheckBox(column);
            }
        }
        this.columnList = columnList;
        initComposite();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (buttonDisplay) {
            columnAddButton.setEnabled(enabled);
            columnEditButton.setEnabled(false);
            columnDeleteButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        }
    }

    private void setButtonEnabled(boolean enabled) {
        if (buttonDisplay) {
            columnEditButton.setEnabled(enabled);
            columnDeleteButton.setEnabled(enabled);
            upButton.setEnabled(enabled);
            downButton.setEnabled(enabled);
        }
    }

    private void selectTable(int index) {
        table.select(index);
        if (index >= 0) {
            setButtonEnabled(true);
        } else {
            setButtonEnabled(false);
        }
    }
}
