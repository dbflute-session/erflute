package org.dbflute.erflute.editor.view.action.dbexport;

import java.io.File;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class AbstractExportAction extends AbstractBaseAction {

    public AbstractExportAction(String id, String label, MainDiagramEditor editor) {
        super(id, label, editor);
    }

    @Override
    public void execute(Event event) throws Exception {
        save(getEditorPart(), getGraphicalViewer());
    }

    protected void save(IEditorPart editorPart, GraphicalViewer viewer) throws Exception {
        final String saveFilePath = getSaveFilePath(editorPart, viewer);
        if (saveFilePath == null) {
            return;
        }
        final File file = new File(saveFilePath);
        if (file.exists()) {
            final MessageBox messageBox =
                    new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
            messageBox.setText(DisplayMessages.getMessage("dialog.title.warning"));
            messageBox.setMessage(DisplayMessages.getMessage(getConfirmOverrideMessage()));
            if (messageBox.open() == SWT.CANCEL) {
                return;
            }
        }
        save(editorPart, viewer, saveFilePath);
        refreshProject();
    }

    protected String getConfirmOverrideMessage() {
        return "dialog.message.update.file";
    }

    protected String getSaveFilePath(IEditorPart editorPart, GraphicalViewer viewer) {
        final IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();
        final FileDialog fileDialog = new FileDialog(editorPart.getEditorSite().getShell(), SWT.SAVE);
        final IProject project = file.getProject();
        fileDialog.setFilterPath(project.getLocation().toString());
        final String[] filterExtensions = getFilterExtensions();
        fileDialog.setFilterExtensions(filterExtensions);
        final String fileName = getDiagramFileName(editorPart, viewer);
        fileDialog.setFileName(fileName);
        return fileDialog.open();
    }

    protected String getDiagramFileName(IEditorPart editorPart, GraphicalViewer viewer) {
        final IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();
        final String fileName = file.getName();
        final String pureName = fileName.substring(0, fileName.lastIndexOf("."));
        final Object diagram = extractDiagram(viewer);
        final String suffix;
        if (isUseVirtualDiagramSuffix() && diagram instanceof ERVirtualDiagram) {
            final String diagramName = ((ERVirtualDiagram) diagram).getName();
            suffix = "-" + diagramName;
        } else {
            suffix = "";
        }
        return pureName + suffix + getDefaultExtension();
    }

    protected boolean isUseVirtualDiagramSuffix() {
        return false;
    }

    protected abstract String getDefaultExtension();

    protected String getSaveDirPath(IEditorPart editorPart, GraphicalViewer viewer) {
        final IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();
        final DirectoryDialog directoryDialog = new DirectoryDialog(editorPart.getEditorSite().getShell(), SWT.SAVE);
        final IProject project = file.getProject();
        directoryDialog.setFilterPath(project.getLocation().toString());
        return directoryDialog.open();
    }

    protected abstract String[] getFilterExtensions();

    protected abstract void save(IEditorPart editorPart, GraphicalViewer viewer, String saveFilePath) throws Exception;

    protected static Object extractDiagram(GraphicalViewer viewer) {
        final EditPart editPart = viewer.getContents();
        editPart.refresh();
        return editPart.getModel();
    }
}
