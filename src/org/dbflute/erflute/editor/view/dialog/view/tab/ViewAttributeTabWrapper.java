package org.dbflute.erflute.editor.view.dialog.view.tab;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.group.ChangeColumnGroupCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyColumnGroup;
import org.dbflute.erflute.editor.view.dialog.column.ViewColumnDialog;
import org.dbflute.erflute.editor.view.dialog.columngroup.ColumnGroupManageDialog;
import org.dbflute.erflute.editor.view.dialog.table.ERTableComposite;
import org.dbflute.erflute.editor.view.dialog.table.ERTableCompositeHolder;
import org.dbflute.erflute.editor.view.dialog.view.ViewDialog;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ViewAttributeTabWrapper extends ValidatableTabWrapper implements ERTableCompositeHolder {

    private static final int GROUP_TABLE_HEIGHT = 75;

    private final ERView view;
    private Text physicalNameText;
    private Text logicalNameText;
    private String previousPhysicalName;
    private Combo groupCombo;
    private Button groupAddButton;
    private Button groupManageButton;
    private final ViewDialog viewDialog;
    private ERTableComposite tableComposite;
    private ERTableComposite groupTableComposite;

    public ViewAttributeTabWrapper(ViewDialog viewDialog, TabFolder parent, int style, ERView copyData) {
        super(viewDialog, parent, style, "label.table.attribute");
        this.view = copyData;
        this.viewDialog = viewDialog;
        init();
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
        physicalNameText = CompositeFactory.createText(viewDialog, header, "label.physical.name", 1, 200, false);
        logicalNameText = CompositeFactory.createText(viewDialog, header, "label.logical.name", 1, 200, true);
        physicalNameText.setText(Format.null2blank(view.getPhysicalName()));
        logicalNameText.setText(Format.null2blank(view.getLogicalName()));
        previousPhysicalName = physicalNameText.getText();
    }

    private void createBody(Composite parent) {
        final Composite content = new Composite(parent, SWT.BORDER);
        final GridData contentGridData = new GridData();
        contentGridData.horizontalAlignment = GridData.FILL;
        contentGridData.grabExcessHorizontalSpace = true;

        content.setLayoutData(contentGridData);

        content.setLayout(new GridLayout(6, false));

        initTable(content);
    }

    private void initTable(Composite parent) {
        final ViewColumnDialog columnDialog =
                new ViewColumnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), view);

        tableComposite = new ERTableComposite(this, parent, view.getDiagram(), null,
                view.getColumns(), columnDialog, viewDialog, 2, true, false);
    }

    private void createFooter(Composite parent) {
        final Composite footer = new Composite(parent, SWT.NONE);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        footer.setLayout(gridLayout);

        createGroupCombo(footer);

        this.groupAddButton = new Button(footer, SWT.NONE);
        groupAddButton.setText(DisplayMessages.getMessage("label.button.add.group.to.view"));
        groupAddButton.addSelectionListener(new SelectionAdapter() {

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
        groupAddButton.setEnabled(false);

        createGroup(footer);

        initGroupCombo();
    }

    /**
     * This method initializes combo
     */
    private void createGroupCombo(Composite parent) {
        final GridData gridData = new GridData();
        gridData.widthHint = 200;

        this.groupCombo = new Combo(parent, SWT.READ_ONLY);
        groupCombo.setLayoutData(gridData);
        groupCombo.addSelectionListener(new SelectionAdapter() {

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
        groupCombo.removeAll();
        for (final ColumnGroup columnGroup : getColumnGroups()) {
            groupCombo.add(columnGroup.getGroupName());
        }

        groupTableComposite.setColumnList(null);
    }

    private void restructGroup() {
        initGroupCombo();
        int index = 0;
        for (final ERColumn column : view.getColumns()) {
            if (column instanceof ColumnGroup) {
                if (!getColumnGroups().contains((ColumnGroup) column)) {
                    tableComposite.removeColumn(index);
                    continue;
                }
            }
            index++;
        }
        viewDialog.validate();
    }

    /**
     * This method initializes group
     */
    private void createGroup(Composite parent) {
        final GridData gridData1 = new GridData();
        gridData1.heightHint = 100;
        gridData1.widthHint = -1;
        final GridData gridData = new GridData();
        gridData.heightHint = -1;
        gridData.horizontalSpan = 2;

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
        groupTableComposite = new ERTableComposite(this, group, view.getDiagram(),
                null, null, null, null, 2, false, false, GROUP_TABLE_HEIGHT);
        groupManageButton = new Button(group, SWT.NONE);
        groupManageButton.setText(DisplayMessages.getMessage("label.button.group.manage"));
        groupManageButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final ColumnGroupSet groupSet = getColumnGroups();
                final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                final ColumnGroupManageDialog dialog = new ColumnGroupManageDialog(shell, groupSet, view.getDiagram(), false, -1);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    final List<CopyColumnGroup> newColumnGroups = dialog.getCopyColumnGroups();
                    final Command command = new ChangeColumnGroupCommand(viewDialog.getDiagram(), groupSet, newColumnGroups);
                    viewDialog.getViewer().getEditDomain().getCommandStack().execute(command);
                    restructGroup();
                    groupAddButton.setEnabled(false);
                }
            }
        });
    }

    private ColumnGroupSet getColumnGroups() {
        return view.getDiagram().getDiagramContents().getColumnGroupSet();
    }

    @Override
    public void setInitFocus() {
        physicalNameText.setFocus();
    }

    @Override
    public void selectGroup(ColumnGroup selectedColumn) {
        final int targetIndex = getColumnGroups().indexOf(selectedColumn);
        groupCombo.select(targetIndex);
        selectGroup(targetIndex);
        groupAddButton.setEnabled(false);
    }

    @SuppressWarnings("unchecked")
    private void selectGroup(int targetIndex) {
        final ColumnGroup columnGroup = getColumnGroups().get(targetIndex);
        if (view.getColumns().contains(columnGroup)) {
            groupAddButton.setEnabled(false);
        } else {
            groupAddButton.setEnabled(true);
        }
        @SuppressWarnings("rawtypes")
        final List columns = columnGroup.getColumns(); // to avoid generic headache
        groupTableComposite.setColumnList(columns);
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    public void validatePage() throws InputException {
        final String physicalName = physicalNameText.getText().trim();
        if (physicalName.isEmpty()) {
            throw new InputException("error.view.physical.name.empty");
        }
        final String defaultPhysicalName = DisplayMessages.getMessage("new.view.physical.name");
        if (defaultPhysicalName.equalsIgnoreCase(physicalName)) {
            throw new InputException("error.view.physical.name.empty");
        }
        if (!Check.isAlphabet(physicalName)) {
            if (view.getDiagram().getDiagramContents().getSettings().isValidatePhysicalName()) {
                throw new InputException("error.view.physical.name.not.alphabet");
            }
        }
        final List<TableView> tableViewList = view.getDiagram().getDiagramContents().getDiagramWalkers().getTableViewList();
        for (final TableView tableView : tableViewList) {
            final String currentName = tableView.getPhysicalName();
            if (previousPhysicalName != null && !previousPhysicalName.equalsIgnoreCase(currentName)) { // other tables
                if (currentName.equalsIgnoreCase(physicalName)) {
                    throw new InputException("error.view.physical.name.already.exists");
                }
            }
        }
        view.setPhysicalName(physicalName);

        final String logicalName = logicalNameText.getText().trim();
        view.setLogicalName(logicalName);
    }

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    public void perfomeOK() {
    }
}
