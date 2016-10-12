package org.dbflute.erflute.editor.view.action;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.ChangeSettingsCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IFileEditorInput;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class AbstractBaseAction extends Action {

    private final MainDiagramEditor editor;

    public AbstractBaseAction(String id, String text, MainDiagramEditor editor) {
        this(id, text, SWT.NONE, editor);
    }

    public AbstractBaseAction(String id, String text, int style, MainDiagramEditor editor) {
        super(text, style);
        this.setId(id);

        this.editor = editor;
    }

    protected void refreshProject() {
        final IFile iFile = ((IFileEditorInput) this.getEditorPart().getEditorInput()).getFile();
        final IProject project = iFile.getProject();

        try {
            project.refreshLocal(IResource.DEPTH_INFINITE, null);

        } catch (final CoreException e) {
            Activator.showExceptionDialog(e);
        }
    }

    protected ERDiagram getDiagram() {
        final EditPart editPart = this.editor.getGraphicalViewer().getContents();
        final Object model = editPart.getModel();
        if (model instanceof ERDiagram) {
            return (ERDiagram) model;
        }
        return ((ERVirtualDiagram) model).getDiagram();
    }

    protected GraphicalViewer getGraphicalViewer() {
        return this.editor.getGraphicalViewer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void runWithEvent(Event event) {
        try {
            execute(event);

        } catch (final Exception e) {
            Activator.showExceptionDialog(e);

        } finally {
            final Settings newSettings = this.getChangedSettings();

            if (newSettings != null && !this.getDiagram().getDiagramContents().getSettings().equals(newSettings)) {
                final ChangeSettingsCommand command = new ChangeSettingsCommand(this.getDiagram(), newSettings);

                this.execute(command);
            }
        }
    }

    abstract public void execute(Event event) throws Exception;

    protected void execute(Command command) {
        this.editor.getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
    }

    protected Settings getChangedSettings() {
        return null;
    }

    protected MainDiagramEditor getEditorPart() {
        return this.editor;
    }
}
