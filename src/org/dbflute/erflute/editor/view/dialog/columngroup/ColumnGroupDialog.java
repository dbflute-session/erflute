package org.dbflute.erflute.editor.view.dialog.columngroup;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyColumnGroup;
import org.dbflute.erflute.editor.view.dialog.column.real.GroupColumnDialog;
import org.dbflute.erflute.editor.view.dialog.table.ERTableComposite;
import org.dbflute.erflute.editor.view.dialog.table.ERTableCompositeHolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ColumnGroupDialog extends AbstractDialog implements ERTableCompositeHolder {

    private Text groupNameText;
    private final List<CopyColumnGroup> copyColumnGroups;
    private int editTargetIndex = -1;
    private CopyColumnGroup copyData;
    private final ERDiagram diagram;

    public ColumnGroupDialog(Shell parentShell, ColumnGroupSet columnGroups, ERDiagram diagram, int editTargetIndex) {
        super(parentShell, 2);
        this.copyColumnGroups = new ArrayList<>();
        for (final ColumnGroup columnGroup : columnGroups) {
            copyColumnGroups.add(new CopyColumnGroup(columnGroup));
        }
        this.diagram = diagram;
        this.editTargetIndex = editTargetIndex;
        if (editTargetIndex != -1) {
            copyData = copyColumnGroups.get(editTargetIndex);
        }
    }

    @Override
    public void selectGroup(ColumnGroup selectedColumn) {
        // do nothing
    }

    @Override
    protected String getTitle() {
        return "dialog.title.group";
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void initComponent(Composite composite) {
        this.groupNameText = CompositeFactory.createText(this, composite, "label.group.name", 1, 200, true);
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final GroupColumnDialog columnDialog = new GroupColumnDialog(shell, diagram);
        new ERTableComposite(this, composite, diagram, null, (List) copyData.getColumns(), columnDialog, this, 2, true, true);
        groupNameText.setFocus();
    }

    @Override
    protected void setupData() {
        if (editTargetIndex != -1) {
            String text = copyData.getGroupName();
            if (text == null) {
                text = "";
            }
            groupNameText.setText(text);
        }
    }

    @Override
    protected String doValidate() {
        if (groupNameText.getEnabled()) {
            final String text = groupNameText.getText().trim();
            if (text.equals("")) {
                return "error.group.name.empty";
            }
        }
        return null;
    }

    @Override
    protected void performOK() {
        if (copyColumnGroups.isEmpty()) {
            return;
        }

        if (editTargetIndex == -1) {
            copyColumnGroups.get(0).setGroupName(groupNameText.getText());
        } else {
            copyColumnGroups.get(editTargetIndex).setGroupName(groupNameText.getText());
        }
    }

    public List<CopyColumnGroup> getCopyColumnGroups() {
        return copyColumnGroups;
    }
}
