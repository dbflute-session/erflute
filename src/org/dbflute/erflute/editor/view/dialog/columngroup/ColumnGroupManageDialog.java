package org.dbflute.erflute.editor.view.dialog.columngroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GlobalColumnGroupSet;
import org.dbflute.erflute.editor.view.dialog.column.real.GroupColumnDialog;
import org.dbflute.erflute.editor.view.dialog.table.ERTableComposite;
import org.dbflute.erflute.editor.view.dialog.table.ERTableCompositeHolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ColumnGroupManageDialog extends AbstractDialog implements ERTableCompositeHolder {

    private static final int HEIGHT = 360;
    private static final int GROUP_LIST_HEIGHT = 230;

    private Text groupNameText;
    private org.eclipse.swt.widgets.List groupList;
    private Button groupUpdateButton;
    private Button groupCancelButton;
    private Button groupAddButton;
    private Button groupEditButton;
    private Button groupDeleteButton;
    private Button addToGlobalGroupButton;
    private final List<CopyColumnGroup> copyGroups;
    private int editTargetIndex = -1;
    private CopyColumnGroup copyData;
    private final ERDiagram diagram;
    private final boolean globalGroup;
    private ERTableComposite tableComposite;

    public ColumnGroupManageDialog(Shell parentShell, ColumnGroupSet columnGroups, ERDiagram diagram, boolean globalGroup,
            int editTargetIndex) {
        super(parentShell, 2);
        this.copyGroups = new ArrayList<CopyColumnGroup>();
        for (final ColumnGroup columnGroup : columnGroups) {
            this.copyGroups.add(new CopyColumnGroup(columnGroup));
        }
        this.diagram = diagram;
        this.globalGroup = globalGroup;
        this.editTargetIndex = editTargetIndex;
    }

    @Override
    protected void initComponent(Composite composite) {
        createGroupListComposite(composite);
        createGroupDetailComposite(composite);
        setGroupEditEnabled(false);
    }

    private void createGroupListComposite(Composite parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.verticalSpacing = 10;

        final GridData gridData = new GridData();
        gridData.heightHint = HEIGHT;

        final Composite composite = new Composite(parent, SWT.BORDER);
        composite.setLayoutData(gridData);
        composite.setLayout(gridLayout);
        createGroup(composite);

        groupAddButton = new Button(composite, SWT.NONE);
        groupAddButton.setText(DisplayMessages.getMessage("label.button.group.add"));

        groupEditButton = new Button(composite, SWT.NONE);
        groupEditButton.setText(DisplayMessages.getMessage("label.button.group.edit"));

        this.groupDeleteButton = new Button(composite, SWT.NONE);
        this.groupDeleteButton.setText(DisplayMessages.getMessage("label.button.group.delete"));

        this.addToGlobalGroupButton = new Button(composite, SWT.NONE);
        this.addToGlobalGroupButton.setText(DisplayMessages.getMessage("label.button.add.to.global.group"));

        final GridData gridData3 = new GridData();
        gridData3.horizontalSpan = 3;
        this.addToGlobalGroupButton.setLayoutData(gridData3);

        if (this.globalGroup) {
            this.addToGlobalGroupButton.setVisible(false);
        }

        setButtonEnabled(false);
    }

    private void createGroupDetailComposite(Composite parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        final GridData gridData = new GridData();
        gridData.heightHint = HEIGHT;
        final Composite composite = new Composite(parent, SWT.BORDER);
        composite.setLayout(gridLayout);
        composite.setLayoutData(gridData);
        this.groupNameText = CompositeFactory.createText(this, composite, "label.group.name", 1, 200, true);
        final GroupColumnDialog columnDialog =
                new GroupColumnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);
        this.tableComposite = new ERTableComposite(this, composite, this.diagram, null, null, columnDialog, this, 2, true, true);
        createComposite3(composite);
    }

    private void createGroup(Composite parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;

        final GridData gridData1 = new GridData();
        gridData1.horizontalSpan = 3;

        final GridData gridData2 = new GridData();
        gridData2.widthHint = 200;
        gridData2.horizontalSpan = 3;
        gridData2.heightHint = GROUP_LIST_HEIGHT;

        final Group group = new Group(parent, SWT.NONE);
        group.setText(DisplayMessages.getMessage("label.group.list"));
        group.setLayoutData(gridData1);
        group.setLayout(gridLayout);
        groupList = new org.eclipse.swt.widgets.List(group, SWT.BORDER | SWT.V_SCROLL);
        groupList.setLayoutData(gridData2);
        initGroupList();
    }

    private void initGroupList() {
        Collections.sort(copyGroups);
        groupList.removeAll();
        for (final ColumnGroup columnGroup : copyGroups) {
            this.groupList.add(columnGroup.getGroupName());
        }
    }

    @SuppressWarnings("unchecked")
    private void initColumnGroup() {
        String text = copyData.getGroupName();
        if (text == null) {
            text = "";
        }
        this.groupNameText.setText(text);
        @SuppressWarnings("rawtypes")
        final List columns = copyData.getColumns(); // to avoid generic headache
        this.tableComposite.setColumnList(columns);
    }

    private void setGroupEditEnabled(boolean enabled) {
        this.tableComposite.setEnabled(enabled);
        this.groupUpdateButton.setEnabled(enabled);
        this.groupCancelButton.setEnabled(enabled);
        this.groupNameText.setEnabled(enabled);
        this.groupList.setEnabled(!enabled);
        this.groupAddButton.setEnabled(!enabled);
        if (this.groupList.getSelectionIndex() != -1 && !enabled) {
            this.setButtonEnabled(true);
        } else {
            this.setButtonEnabled(false);
        }
        if (enabled) {
            this.groupNameText.setFocus();
        } else {
            this.groupList.setFocus();
        }
        this.enabledButton(!enabled);
    }

    private void createComposite3(Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(gridLayout);
        composite.setLayoutData(gridData);
        final GridData gridData1 = new GridData();
        gridData1.widthHint = 80;
        this.groupUpdateButton = new Button(composite, SWT.NONE);
        this.groupUpdateButton.setText(DisplayMessages.getMessage("label.button.update"));
        this.groupUpdateButton.setLayoutData(gridData1);
        this.groupCancelButton = new Button(composite, SWT.NONE);
        this.groupCancelButton.setText(DisplayMessages.getMessage("label.button.cancel"));
        this.groupCancelButton.setLayoutData(gridData1);
    }

    @Override
    protected String getTitle() {
        if (this.globalGroup) {
            return "dialog.title.manage.global.group";
        }
        return "dialog.title.manage.group";
    }

    @Override
    protected void setupData() {
        if (isEdit()) {
            this.groupList.setSelection(editTargetIndex);
            this.copyData = new CopyColumnGroup(copyGroups.get(editTargetIndex));
            this.initColumnGroup();
            this.setGroupEditEnabled(true);
        }
    }

    private boolean isEdit() {
        return this.editTargetIndex != -1;
    }

    public List<CopyColumnGroup> getCopyColumnGroups() {
        return copyGroups;
    }

    private void setButtonEnabled(boolean enabled) {
        this.groupEditButton.setEnabled(enabled);
        this.groupDeleteButton.setEnabled(enabled);
        this.addToGlobalGroupButton.setEnabled(enabled);
    }

    @Override
    public void selectGroup(ColumnGroup selectedColumn) {
        // do nothing
    }

    @Override
    protected void addListener() {
        super.addListener();
        this.groupAddButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editTargetIndex = -1;
                copyData = new CopyColumnGroup(new ColumnGroup());
                initColumnGroup();
                setGroupEditEnabled(true);
            }
        });
        this.groupEditButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editTargetIndex = groupList.getSelectionIndex();
                if (editTargetIndex == -1) {
                    return;
                }
                setGroupEditEnabled(true);
            }
        });
        this.groupDeleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editTargetIndex = groupList.getSelectionIndex();
                if (editTargetIndex == -1) {
                    return;
                }
                copyGroups.remove(editTargetIndex);
                initGroupList();
                if (copyGroups.size() == 0) {
                    editTargetIndex = -1;
                } else if (editTargetIndex >= copyGroups.size()) {
                    editTargetIndex = copyGroups.size() - 1;
                }
                if (editTargetIndex != -1) {
                    groupList.setSelection(editTargetIndex);
                    copyData = new CopyColumnGroup(copyGroups.get(editTargetIndex));
                    initColumnGroup();
                } else {
                    copyData = new CopyColumnGroup(new ColumnGroup());
                    initColumnGroup();
                    setButtonEnabled(false);
                }
            }
        });
        this.addToGlobalGroupButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editTargetIndex = groupList.getSelectionIndex();
                if (editTargetIndex == -1) {
                    return;
                }
                final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                final MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
                messageBox.setText(DisplayMessages.getMessage("label.button.add.to.global.group"));
                messageBox.setMessage(DisplayMessages.getMessage("dialog.message.add.to.global.group"));
                if (messageBox.open() == SWT.OK) {
                    final CopyColumnGroup columnGroup = copyGroups.get(editTargetIndex);
                    final ColumnGroupSet columnGroups = GlobalColumnGroupSet.load();
                    columnGroups.add(columnGroup);
                    GlobalColumnGroupSet.save(columnGroups);
                }
            }
        });
        this.groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                editTargetIndex = groupList.getSelectionIndex();
                if (editTargetIndex == -1) {
                    return;
                }
                setGroupEditEnabled(true);
            }
        });
        this.groupList.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    editTargetIndex = groupList.getSelectionIndex();
                    if (editTargetIndex == -1) {
                        return;
                    }
                    copyData = new CopyColumnGroup(copyGroups.get(editTargetIndex));
                    initColumnGroup();
                    setButtonEnabled(true);
                } catch (final Exception ex) {
                    Activator.showExceptionDialog(ex);
                }
            }
        });
        this.groupUpdateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (validate()) {
                        final String text = groupNameText.getText().trim();
                        copyData.setGroupName(text);
                        if (editTargetIndex == -1) {
                            copyGroups.add(copyData);
                        } else {
                            copyGroups.remove(editTargetIndex);
                            copyData = (CopyColumnGroup) copyData.restructure(null);
                            copyGroups.add(editTargetIndex, copyData);
                        }
                        setGroupEditEnabled(false);
                        initGroupList();
                        for (int i = 0; i < copyGroups.size(); i++) {
                            final ColumnGroup columnGroup = copyGroups.get(i);
                            if (columnGroup == copyData) {
                                groupList.setSelection(i);
                                copyData = new CopyColumnGroup(copyGroups.get(i));
                                initColumnGroup();
                                setButtonEnabled(true);
                                break;
                            }
                        }
                    }
                } catch (final Exception ex) {
                    Activator.showExceptionDialog(ex);
                }
            }
        });
        this.groupCancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setGroupEditEnabled(false);
                if (editTargetIndex != -1) {
                    copyData = new CopyColumnGroup(copyGroups.get(editTargetIndex));
                    initColumnGroup();
                }
            }
        });
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    protected String doValidate() {
        if (groupNameText.getEnabled()) {
            final String groupName = groupNameText.getText().trim();
            if (groupName.equals("")) {
                return "error.group.name.empty";
            }
            if (copyGroups != null && !isEdit()) { // just in case
                for (final CopyColumnGroup existingGroup : copyGroups) {
                    final String existingName = existingGroup.getGroupName();
                    if (existingName.equalsIgnoreCase(groupName)) {
                        return "The column group name already exists: " + groupName; // #for_erflute
                    }
                }
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    protected void performOK() {
    }
}
