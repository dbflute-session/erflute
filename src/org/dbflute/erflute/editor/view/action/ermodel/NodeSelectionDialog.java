package org.dbflute.erflute.editor.view.action.ermodel;

import java.util.Comparator;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

public class NodeSelectionDialog extends FilteredItemsSelectionDialog {

    private final ERDiagram diagram;

    public NodeSelectionDialog(Shell shell, ERDiagram diagram) {
        super(shell);
        this.diagram = diagram;
    }

    @Override
    protected Control createExtendedContentArea(Composite parent) {
        return null;
    }

    @Override
    protected IDialogSettings getDialogSettings() {
        final IDialogSettings result = new DialogSettings("NodeSelectionDialog"); //$NON-NLS-1$
        return result;
    }

    @Override
    protected IStatus validateItem(Object item) {
        return Status.OK_STATUS;
    }

    @Override
    protected ItemsFilter createFilter() {
        return new ItemsFilter() {
            @Override
            public boolean matchItem(Object item) {
                if (item instanceof ERTable) {
                    final ERTable table = (ERTable) item;
                    return this.patternMatcher.matches(table.getPhysicalName());
                }
                return false;
            }

            @Override
            public boolean isConsistentItem(Object item) {
                return true;
            }
        };
    }

    @Override
    protected Comparator<Object> getItemsComparator() {
        return new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return 0;
            }
        };
    }

    @Override
    protected void applyFilter() {
        super.applyFilter();
    }

    @Override
    protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
            throws CoreException {
        for (final ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            if (itemsFilter.matchItem(table)) {
                contentProvider.add(table, itemsFilter);
            }
        }
    }

    @Override
    public String getElementName(Object item) {
        if (item instanceof ERTable) {
            final ERTable table = (ERTable) item;
            return table.getLogicalName();
        }
        return null;
    }
}
