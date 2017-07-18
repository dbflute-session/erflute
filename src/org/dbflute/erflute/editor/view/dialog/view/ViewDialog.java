package org.dbflute.erflute.editor.view.dialog.view;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.view.dialog.view.tab.AdvancedTabWrapper;
import org.dbflute.erflute.editor.view.dialog.view.tab.DescriptionTabWrapper;
import org.dbflute.erflute.editor.view.dialog.view.tab.SqlTabWrapper;
import org.dbflute.erflute.editor.view.dialog.view.tab.ViewAttributeTabWrapper;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

public class ViewDialog extends AbstractDialog {

    private final ERView copyData;
    private TabFolder tabFolder;
    private final EditPartViewer viewer;
    private final List<ValidatableTabWrapper> tabWrapperList;

    public ViewDialog(Shell parentShell, EditPartViewer viewer, ERView copyData, ColumnGroupSet columnGroups) {
        super(parentShell);

        this.viewer = viewer;
        this.copyData = copyData;

        this.tabWrapperList = new ArrayList<>();
    }

    @Override
    protected void initComponent(Composite composite) {
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalAlignment = GridData.FILL;

        this.tabFolder = new TabFolder(composite, SWT.NONE);
        tabFolder.setLayoutData(gridData);

        final ViewAttributeTabWrapper attributeTabWrapper = new ViewAttributeTabWrapper(this, tabFolder, SWT.NONE, copyData);
        tabWrapperList.add(attributeTabWrapper);

        tabWrapperList.add(new SqlTabWrapper(this, tabFolder, SWT.NONE, copyData));
        tabWrapperList.add(new DescriptionTabWrapper(this, tabFolder, SWT.NONE, copyData));
        tabWrapperList.add(new AdvancedTabWrapper(this, tabFolder, SWT.NONE, copyData));

        tabFolder.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = tabFolder.getSelectionIndex();

                final ValidatableTabWrapper selectedTabWrapper = tabWrapperList.get(index);
                selectedTabWrapper.setInitFocus();
            }
        });

        attributeTabWrapper.setInitFocus();
    }

    @Override
    protected String doValidate() {
        try {
            for (final ValidatableTabWrapper tabWrapper : tabWrapperList) {
                tabWrapper.validatePage();
            }
        } catch (final InputException e) {
            return e.getMessage();
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.view";
    }

    @Override
    protected void performOK() throws InputException {
    }

    @Override
    protected void setupData() {
    }

    public EditPartViewer getViewer() {
        return viewer;
    }

    public ERDiagram getDiagram() {
        return (ERDiagram) viewer.getContents().getModel();
    }
}
