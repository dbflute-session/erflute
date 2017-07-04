package org.dbflute.erflute.preference;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GlobalColumnGroupSet;
import org.dbflute.erflute.editor.view.dialog.columngroup.ColumnGroupManageDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class PreferenceTopPage extends PreferencePage implements IWorkbenchPreferencePage {

    @Override
    protected Control createContents(Composite parent) {
        noDefaultAndApplyButton();
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        initialize(composite);
        return composite;
    }

    private void initialize(Composite parent) {
        final Button button = new Button(parent, SWT.NONE);
        button.setText(DisplayMessages.getMessage("action.title.manage.global.group"));
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final ColumnGroupSet columnGroups = GlobalColumnGroupSet.load();
                final ERDiagram diagram = new ERDiagram(columnGroups.getDatabase());

                final ColumnGroupManageDialog dialog = new ColumnGroupManageDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), columnGroups, diagram, true, -1);

                if (dialog.open() == IDialogConstants.OK_ID) {
                    final List<CopyColumnGroup> newColumnGroups = dialog.getCopyColumnGroups();
                    columnGroups.clear();
                    for (final CopyColumnGroup copyColumnGroup : newColumnGroups) {
                        columnGroups.add(copyColumnGroup.restructure(null));
                    }
                    GlobalColumnGroupSet.save(columnGroups);
                }
            }
        });
    }

    @Override
    public void init(IWorkbench workbench) {
    }
}
