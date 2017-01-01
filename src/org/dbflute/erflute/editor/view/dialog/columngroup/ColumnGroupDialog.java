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
        this.copyColumnGroups = new ArrayList<CopyColumnGroup>();
        for (final ColumnGroup columnGroup : columnGroups) {
            this.copyColumnGroups.add(new CopyColumnGroup(columnGroup));
        }
        this.diagram = diagram;
        this.editTargetIndex = editTargetIndex;
        if (this.editTargetIndex != -1) {
            this.copyData = copyColumnGroups.get(editTargetIndex);
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
        new ERTableComposite(this, composite, this.diagram, null, (List) this.copyData.getColumns(), columnDialog, this, 2, true, true);
        this.groupNameText.setFocus();
    }

    @Override
    protected void setupData() {
        if (this.editTargetIndex != -1) {
            String text = this.copyData.getGroupName();
            if (text == null) {
                text = "";
            }
            this.groupNameText.setText(text);
        }
    }

    @Override
    protected String doValidate() {
        if (this.groupNameText.getEnabled()) {
            final String text = this.groupNameText.getText().trim();
            if (text.equals("")) {
                return "error.group.name.empty";
            }
        }
        return null;
    }

    @Override
    protected void performOK() {
    }

    public List<CopyColumnGroup> getCopyColumnGroups() {
        return copyColumnGroups;
    }
}
