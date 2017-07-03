package org.dbflute.erflute.editor.view.action.outline;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

public abstract class AbstractOutlineBaseAction extends Action {

    private final TreeViewer treeViewer;

    public AbstractOutlineBaseAction(String id, String text, TreeViewer treeViewer) {
        this(id, text, SWT.NONE, treeViewer);
    }

    public AbstractOutlineBaseAction(String id, String text, int style, TreeViewer treeViewer) {
        super(text, style);
        setId(id);

        this.treeViewer = treeViewer;
    }

    @Override
    public final void runWithEvent(Event event) {
        try {
            execute(event);
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    protected void execute(Command command) {
        treeViewer.getEditDomain().getCommandStack().execute(command);
    }

    protected ERDiagram getDiagram() {
        final EditPart editPart = treeViewer.getContents();
        final ERDiagram diagram = (ERDiagram) editPart.getModel();

        return diagram;
    }

    protected TreeViewer getTreeViewer() {
        return treeViewer;
    }

    abstract public void execute(Event event) throws Exception;

}
