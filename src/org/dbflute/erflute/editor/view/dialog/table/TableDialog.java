package org.dbflute.erflute.editor.view.dialog.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.view.dialog.table.tab.AdvancedTabWrapper;
import org.dbflute.erflute.editor.view.dialog.table.tab.CompoundUniqueKeyTabWrapper;
import org.dbflute.erflute.editor.view.dialog.table.tab.ConstraintTabWrapper;
import org.dbflute.erflute.editor.view.dialog.table.tab.DescriptionTabWrapper;
import org.dbflute.erflute.editor.view.dialog.table.tab.IndexTabWrapper;
import org.dbflute.erflute.editor.view.dialog.table.tab.TableAttributeTabWrapper;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class TableDialog extends AbstractDialog {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final ERTable copyData;
    private final EditPartViewer viewer;
    private final List<ValidatableTabWrapper> tabWrapperList;
    private TabFolder tabFolder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TableDialog(Shell parentShell, EditPartViewer viewer, ERTable copyData, ColumnGroupSet columnGroups) {
        super(parentShell);
        this.viewer = viewer;
        this.copyData = copyData;
        this.tabWrapperList = new ArrayList<>();
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    @Override
    protected void initComponent(Composite composite) {
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalAlignment = GridData.FILL;
        this.tabFolder = new TabFolder(composite, SWT.NONE);
        tabFolder.setLayoutData(gridData);
        final TableAttributeTabWrapper attributeTabWrapper = new TableAttributeTabWrapper(this, tabFolder, SWT.NONE, copyData);
        tabWrapperList.add(attributeTabWrapper);
        tabWrapperList.add(new DescriptionTabWrapper(this, tabFolder, SWT.NONE, copyData));
        tabWrapperList.add(new ConstraintTabWrapper(this, tabFolder, SWT.NONE, copyData));
        final CompoundUniqueKeyTabWrapper complexUniqueKeyTabWrapper =
                new CompoundUniqueKeyTabWrapper(this, tabFolder, SWT.NONE, copyData);
        tabWrapperList.add(complexUniqueKeyTabWrapper);
        final IndexTabWrapper indexTabWrapper = new IndexTabWrapper(this, tabFolder, SWT.NONE, copyData);
        tabWrapperList.add(indexTabWrapper);
        tabWrapperList.add(new AdvancedTabWrapper(this, tabFolder, SWT.NONE, copyData));
        tabFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                complexUniqueKeyTabWrapper.restruct();
                indexTabWrapper.restruct();
                final int index = tabFolder.getSelectionIndex();
                final ValidatableTabWrapper selectedTabWrapper = tabWrapperList.get(index);
                selectedTabWrapper.setInitFocus();
            }
        });
        attributeTabWrapper.setInitFocus();
    }

    @Override
    protected String getTitle() {
        return "dialog.title.table";
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
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

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    protected void performOK() throws InputException {
        final String physicalName = copyData.getPhysicalName();
        final int prefixPos = physicalName.indexOf('_');
        if (prefixPos < 0) {
            return;
        }
        final String prefix = physicalName.substring(0, prefixPos + 1);
        final DiagramWalkerSet nodeSet = copyData.getDiagram().getDiagramContents().getDiagramWalkers();
        final Map<MyColor, Integer> colors = new HashMap<>();
        int sum = 0;
        for (final ERTable table : nodeSet.getTableSet()) {
            if (table.getPhysicalName().startsWith(prefix)) {
                final MyColor mycolor = new MyColor(table.getColor());
                if (colors.containsKey(mycolor)) {
                    final Integer count = colors.get(mycolor);
                    colors.put(mycolor, count + 1);
                } else {
                    colors.put(mycolor, 1);
                }
                ++sum;
            }
        }
        int[] targetColor = null;
        for (final Entry<MyColor, Integer> entry : colors.entrySet()) {
            if (entry.getValue().intValue() >= sum - 1) {
                targetColor = entry.getKey().getColors();
            }
        }
        if (targetColor != null) {
            copyData.setColor(targetColor[0], targetColor[1], targetColor[2]);
        }
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
