package org.dbflute.erflute.editor.view.action;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.ChangeSettingsCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.IERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
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
        setId(id);
        this.editor = editor;
    }

    protected void refreshProject() {
        final IFile iFile = ((IFileEditorInput) getEditorPart().getEditorInput()).getFile();
        final IProject project = iFile.getProject();
        try {
            project.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (final CoreException e) {
            Activator.showExceptionDialog(e);
        }
    }

    protected ERDiagram getDiagram() {
        final EditPart editPart = editor.getGraphicalViewer().getContents();
        final IERDiagram erd = (IERDiagram) editPart.getModel();
        return erd.toMaterializedDiagram();
    }

    protected GraphicalViewer getGraphicalViewer() {
        return editor.getGraphicalViewer();
    }

    @Override
    public final void runWithEvent(Event event) {
        try {
            execute(event);
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        } finally {
            final DiagramSettings newSettings = getChangedSettings();
            if (newSettings != null && !getDiagram().getDiagramContents().getSettings().equals(newSettings)) {
                final ChangeSettingsCommand command = new ChangeSettingsCommand(getDiagram(), newSettings);
                execute(command);
            }
        }
    }

    abstract public void execute(Event event) throws Exception;

    protected void execute(Command command) {
        editor.getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
    }

    protected DiagramSettings getChangedSettings() {
        return null;
    }

    protected MainDiagramEditor getEditorPart() {
        return editor;
    }
}
