package org.dbflute.erflute.editor.view.dialog.table.tab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.CopyIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.view.dialog.table.ERTableComposite;
import org.dbflute.erflute.editor.view.dialog.table.sub.IndexDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

public class IndexTabWrapper extends ValidatableTabWrapper {

    private static final int BUTTON_WIDTH = 60;

    private final ERTable copyData;
    private Table indexTable;
    private final List<Button> checkButtonList;
    private final List<TableEditor> editorList;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    public IndexTabWrapper(AbstractDialog dialog, TabFolder parent, int style, ERTable copyData) {
        super(dialog, parent, style, "label.index");
        this.copyData = copyData;
        this.checkButtonList = new ArrayList<Button>();
        this.editorList = new ArrayList<TableEditor>();
        this.init();
    }

    @Override
    public void validatePage() throws InputException {
        resutuctIndexData();
    }

    @Override
    public void initComposite() {
        this.setLayout(new GridLayout());
        final Composite content = new Composite(this, SWT.BORDER);
        this.createBody(content);
    }

    private void createBody(Composite content) {
        final GridData contentGridData = new GridData();
        contentGridData.horizontalAlignment = GridData.FILL;
        contentGridData.grabExcessHorizontalSpace = true;
        content.setLayoutData(contentGridData);
        content.setLayout(new GridLayout(3, false));
        this.initTable(content);
        this.initTableButton(content);
        this.setTableData();
    }

    private void initTable(Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.heightHint = 200;

        this.indexTable = new Table(parent, SWT.BORDER | SWT.HIDE_SELECTION);

        this.indexTable.setHeaderVisible(true);
        this.indexTable.setLayoutData(gridData);
        this.indexTable.setLinesVisible(true);

        CompositeFactory.createTableColumn(this.indexTable, "label.column.name", ERTableComposite.NAME_WIDTH, SWT.NONE);
        final TableColumn separatorColumn = CompositeFactory.createTableColumn(this.indexTable, "", 3, SWT.NONE);
        separatorColumn.setResizable(false);
    }

    private void initTableButton(Composite parent) {
        final GridData gridData = new GridData();
        gridData.widthHint = BUTTON_WIDTH;

        this.addButton = new Button(parent, SWT.NONE);
        this.addButton.setText(DisplayMessages.getMessage("label.button.add"));
        this.addButton.setLayoutData(gridData);

        this.editButton = new Button(parent, SWT.NONE);
        this.editButton.setText(DisplayMessages.getMessage("label.button.edit"));
        this.editButton.setLayoutData(gridData);

        this.deleteButton = new Button(parent, SWT.NONE);
        this.deleteButton.setText(DisplayMessages.getMessage("label.button.delete"));
        this.deleteButton.setLayoutData(gridData);
    }

    @Override
    protected void addListener() {
        this.addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                final IndexDialog dialog = new IndexDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, copyData);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    addIndexData(dialog.getResultIndex(), true);
                }
            }
        });
        this.editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                final ERIndex targetIndex = getTargetIndex();
                if (targetIndex == null) {
                    return;
                }
                final IndexDialog dialog =
                        new IndexDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), targetIndex, copyData);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    addIndexData(dialog.getResultIndex(), false);
                }
            }
        });

        this.deleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                removeIndex();
            }
        });
    }

    private void setTableData() {
        final List<ERIndex> indexes = this.copyData.getIndexes();

        final TableItem radioTableItem = new TableItem(this.indexTable, SWT.NONE);

        for (int i = 0; i < indexes.size(); i++) {
            final TableColumn tableColumn = new TableColumn(this.indexTable, SWT.CENTER);
            tableColumn.setWidth(60);
            tableColumn.setResizable(false);
            tableColumn.setText("Index" + (i + 1));
            final TableEditor editor = new TableEditor(this.indexTable);
            final Button radioButton = new Button(this.indexTable, SWT.RADIO);
            radioButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    setButtonEnabled(true);
                }
            });
            radioButton.pack();
            editor.minimumWidth = radioButton.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(radioButton, radioTableItem, i + 2);
            this.checkButtonList.add(radioButton);
            this.editorList.add(editor);
        }

        for (final NormalColumn normalColumn : this.copyData.getExpandedColumns()) {
            final TableItem tableItem = new TableItem(this.indexTable, SWT.NONE);
            tableItem.setText(0, Format.null2blank(normalColumn.getName()));

            for (int i = 0; i < indexes.size(); i++) {
                final ERIndex index = indexes.get(i);

                final List<NormalColumn> indexColumns = index.getColumns();
                for (int j = 0; j < indexColumns.size(); j++) {
                    final NormalColumn indexColumn = indexColumns.get(j);

                    if (normalColumn.equals(indexColumn)) {
                        tableItem.setText(i + 2, String.valueOf(j + 1));
                        break;
                    }
                }
            }
        }

        setButtonEnabled(false);
    }

    public void addIndexData(ERIndex index, boolean add) {
        int selectedIndex = -1;
        for (int i = 0; i < this.checkButtonList.size(); i++) {
            final Button checkButton = this.checkButtonList.get(i);
            if (checkButton.getSelection()) {
                selectedIndex = i;
                break;
            }
        }
        ERIndex copyIndex = null;
        if (add || selectedIndex == -1) {
            copyIndex = new CopyIndex(copyData, index, null);
            this.copyData.addIndex(copyIndex);
        } else {
            copyIndex = this.copyData.getIndex(selectedIndex);
            CopyIndex.copyData(index, copyIndex);
        }
        this.restruct();
    }

    public void removeIndex() {
        int selectedIndex = -1;
        for (int i = 0; i < this.checkButtonList.size(); i++) {
            final Button checkButton = this.checkButtonList.get(i);
            if (checkButton.getSelection()) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex == -1) {
            return;
        }
        this.copyData.removeIndex(selectedIndex);
        this.restruct();
    }

    public void restruct() {
        this.clearButtonAndEditor();
        while (this.indexTable.getColumnCount() > 2) {
            final TableColumn tableColumn = this.indexTable.getColumn(2);
            tableColumn.dispose();
        }
        this.indexTable.removeAll();
        this.resutuctIndexData();
        this.setTableData();
    }

    private void resutuctIndexData() {
        for (final ERIndex index : this.copyData.getIndexes()) {
            final List<NormalColumn> indexColumns = index.getColumns();
            final Iterator<NormalColumn> columnIterator = indexColumns.iterator();
            final Iterator<Boolean> descIterator = index.getDescs().iterator();
            while (columnIterator.hasNext()) {
                final NormalColumn indexColumn = columnIterator.next();
                descIterator.next();
                if (!this.copyData.getExpandedColumns().contains(indexColumn)) {
                    columnIterator.remove();
                    descIterator.remove();
                }
            }
        }
    }

    private void clearButtonAndEditor() {
        for (final Button checkButton : this.checkButtonList) {
            checkButton.dispose();
        }
        this.checkButtonList.clear();
        for (final TableEditor editor : this.editorList) {
            editor.dispose();
        }
        this.editorList.clear();
    }

    public ERIndex getTargetIndex() {
        int selectedIndex = -1;
        for (int i = 0; i < this.checkButtonList.size(); i++) {
            final Button checkButton = this.checkButtonList.get(i);
            if (checkButton.getSelection()) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex == -1) {
            return null;
        }
        return this.copyData.getIndex(selectedIndex);
    }

    private void setButtonEnabled(boolean enabled) {
        this.editButton.setEnabled(enabled);
        this.deleteButton.setEnabled(enabled);
    }

    @Override
    public void setInitFocus() {
    }

    @Override
    public void perfomeOK() {
    }
}
