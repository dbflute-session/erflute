package org.dbflute.erflute.editor.view.dialog.table.tab;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.group.ChangeGroupCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.dbflute.erflute.editor.view.dialog.column.real.ColumnDialog;
import org.dbflute.erflute.editor.view.dialog.columngroup.ColumnGroupManageDialog;
import org.dbflute.erflute.editor.view.dialog.table.ERTableComposite;
import org.dbflute.erflute.editor.view.dialog.table.ERTableCompositeHolder;
import org.dbflute.erflute.editor.view.dialog.table.TableDialog;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class TableAttributeTabWrapper extends ValidatableTabWrapper implements ERTableCompositeHolder {

    private static final int GROUP_TABLE_HEIGHT = 75;

    private final ERTable table;
    private Text physicalNameText;
    private Text logicalNameText;
    private String previousPhysicalName;
    private Combo groupCombo;
    private Button groupAddButton;
    private Button groupManageButton;
    private final TableDialog tableDialog;
    private ERTableComposite tableComposite;
    private ERTableComposite groupTableComposite;

    public TableAttributeTabWrapper(TableDialog tableDialog, TabFolder parent, int style, ERTable table) {
        super(tableDialog, parent, style, "label.table.attribute");
        this.table = table;
        this.tableDialog = tableDialog;
        this.init();
    }

    @Override
    public void initComposite() {
        setLayout(new GridLayout());
        createHeader(this);
        createBody(this);
        createFooter(this);
    }

    private void createHeader(Composite parent) {
        final Composite header = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout(4, false);
        gridLayout.horizontalSpacing = 20;
        header.setLayout(gridLayout);
        physicalNameText = CompositeFactory.createText(tableDialog, header, "label.physical.name", 1, 200, false);
        logicalNameText = CompositeFactory.createText(tableDialog, header, "label.logical.name", 1, 200, true);
        physicalNameText.setText(Format.null2blank(table.getPhysicalName()));
        logicalNameText.setText(Format.null2blank(table.getLogicalName()));
        previousPhysicalName = physicalNameText.getText();
    }

    private void createBody(Composite parent) {
        final Composite content = new Composite(parent, SWT.BORDER);
        final GridData contentGridData = new GridData();
        contentGridData.horizontalAlignment = GridData.FILL;
        contentGridData.grabExcessHorizontalSpace = true;
        content.setLayoutData(contentGridData);
        content.setLayout(new GridLayout(6, false));
        this.initTable(content);
    }

    private void initTable(Composite parent) {
        final ColumnDialog columnDialog = new ColumnDialog(getShell(), this.table);
        this.tableComposite =
                new ERTableComposite(this, parent, this.table.getDiagram(), this.table, this.table.getColumns(), columnDialog,
                        this.tableDialog, 2, true, true);
    }

    private void createFooter(Composite parent) {
        final Composite footer = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        footer.setLayout(gridLayout);
        this.createGroupCombo(footer);
        this.groupAddButton = new Button(footer, SWT.NONE);
        this.groupAddButton.setText(DisplayMessages.getMessage("label.button.add.group.to.table"));
        this.groupAddButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int targetIndex = groupCombo.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }
                final ColumnGroup columnGroup = getColumnGroups().get(targetIndex);
                tableComposite.addTableData(columnGroup);
                groupAddButton.setEnabled(false);
            }
        });
        this.groupAddButton.setEnabled(false);
        this.createGroup(footer);
        this.initGroupCombo();
    }

    /**
     * This method initializes combo
     */
    private void createGroupCombo(Composite parent) {
        final GridData gridData = new GridData();
        gridData.widthHint = 200;
        this.groupCombo = new Combo(parent, SWT.READ_ONLY);
        this.groupCombo.setLayoutData(gridData);
        this.groupCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int targetIndex = groupCombo.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }
                selectGroup(targetIndex);
            }
        });
    }

    private void initGroupCombo() {
        this.groupCombo.removeAll();
        for (final ColumnGroup columnGroup : this.getColumnGroups()) {
            this.groupCombo.add(columnGroup.getGroupName());
        }
        this.groupTableComposite.setColumnList(null);
    }

    private void restructGroup() {
        this.initGroupCombo();
        int index = 0;
        for (final ERColumn column : this.table.getColumns()) {
            if (column instanceof ColumnGroup) {
                if (!this.getColumnGroups().contains((ColumnGroup) column)) {
                    this.tableComposite.removeColumn(index);
                    continue;
                }
            }
            index++;
        }

        this.tableDialog.validate();
    }

    /**
     * This method initializes group
     *
     */
    private void createGroup(Composite parent) {
        final GridData gridData1 = new GridData();
        gridData1.heightHint = 100;
        gridData1.widthHint = -1;
        final GridData gridData = new GridData();
        gridData.heightHint = -1;
        gridData.horizontalSpan = 4;

        // FormToolkit toolkit = new FormToolkit(this.getDisplay());
        // Form root = toolkit.createForm(parent);
        // root.getBody().setLayout(new GridLayout());
        //
        // ExpandableComposite expandableComposite = toolkit
        // .createExpandableComposite(root.getBody(),
        // ExpandableComposite.TWISTIE);
        //
        // Composite inner = toolkit.createComposite(expandableComposite);
        // inner.setLayout(new GridLayout());
        // expandableComposite.setClient(inner);
        // toolkit.createLabel(inner, "aaa");

        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout());
        group.setLayoutData(gridData);

        this.groupTableComposite =
                new ERTableComposite(this, group, this.table.getDiagram(), null, null, null, null, 2, false, false, GROUP_TABLE_HEIGHT);

        this.groupManageButton = new Button(group, SWT.NONE);
        this.groupManageButton.setText(DisplayMessages.getMessage("label.button.group.manage"));

        this.groupManageButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                final ColumnGroupSet groupSet = getColumnGroups();

                final ColumnGroupManageDialog dialog =
                        new ColumnGroupManageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), groupSet,
                                table.getDiagram(), false, -1);

                if (dialog.open() == IDialogConstants.OK_ID) {
                    final List<CopyGroup> newColumnGroups = dialog.getCopyColumnGroups();

                    final Command command = new ChangeGroupCommand(tableDialog.getDiagram(), groupSet, newColumnGroups);

                    tableDialog.getViewer().getEditDomain().getCommandStack().execute(command);

                    restructGroup();

                    groupAddButton.setEnabled(false);
                }
            }

        });
    }

    private ColumnGroupSet getColumnGroups() {
        return this.table.getDiagram().getDiagramContents().getColumnGroupSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitFocus() {
        this.physicalNameText.setFocus();
    }

    @Override
    public void selectGroup(ColumnGroup selectedColumn) {
        final int targetIndex = this.getColumnGroups().indexOf(selectedColumn);

        this.groupCombo.select(targetIndex);
        this.selectGroup(targetIndex);

        this.groupAddButton.setEnabled(false);
    }

    @SuppressWarnings("unchecked")
    private void selectGroup(int targetIndex) {
        final ColumnGroup columnGroup = getColumnGroups().get(targetIndex);
        if (this.table.getColumns().contains(columnGroup)) {
            this.groupAddButton.setEnabled(false);
        } else {
            this.groupAddButton.setEnabled(true);
        }
        @SuppressWarnings("rawtypes")
        final List columns = columnGroup.getColumns(); // to avoid generic headache
        this.groupTableComposite.setColumnList(columns);
    }

    @Override
    protected void addListener() {
        super.addListener();
        // #for_erflute quit physical name and logical name linkage
        //this.physicalNameText.addModifyListener(new ModifyListener() {
        //    @Override
        //    public void modifyText(ModifyEvent e) {
        //        final String logicalName = logicalNameText.getText();
        //        final String physicalName = physicalNameText.getText();
        //        if (oldPhysicalName.equals(logicalName) || logicalName.equals("")) {
        //            logicalNameText.setText(physicalName);
        //            oldPhysicalName = physicalName;
        //        }
        //    }
        //});
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    public void validatePage() throws InputException {
        final String physicalName = physicalNameText.getText().trim();
        if (physicalName.isEmpty()) {
            throw new InputException("error.table.physical.name.empty");
        }
        final String defaultPhysicalName = DisplayMessages.getMessage("new.table.physical.name");
        if (defaultPhysicalName.equalsIgnoreCase(physicalName)) {
            throw new InputException("error.table.physical.name.empty");
        }
        if (!Check.isAlphabet(physicalName)) {
            if (table.getDiagram().getDiagramContents().getSettings().isValidatePhysicalName()) {
                throw new InputException("error.table.physical.name.not.alphabet");
            }
        }
        final List<TableView> tableViewList = table.getDiagram().getDiagramContents().getDiagramWalkers().getTableViewList();
        for (final TableView tableView : tableViewList) {
            final String currentName = tableView.getPhysicalName();
            if (previousPhysicalName != null && !previousPhysicalName.equalsIgnoreCase(currentName)) { // other tables
                if (currentName.equalsIgnoreCase(physicalName)) {
                    throw new InputException("error.table.physical.name.already.exists");
                }
            }
        }
        table.setPhysicalName(physicalName);

        final String logicalName = logicalNameText.getText().trim();
        table.setLogicalName(logicalName);

        boolean needPrimaryKey = false;
        for (final Relationship relationship : table.getOutgoingRelationshipList()) {
            if (relationship.isReferenceForPK()) {
                needPrimaryKey = true;
                break;
            }
        }
        if (needPrimaryKey) {
            if (table.getPrimaryKeySize() == 0) {
                throw new InputException("error.primary.key.is.referenced");
            }
        }
    }

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    public void perfomeOK() {
    }
}
