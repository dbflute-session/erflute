package org.dbflute.erflute.wizard.page;

import java.io.InputStream;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.persistent.Persistent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class NewDiagramWizardPage1 extends WizardNewFileCreationPage {

    private static final String EXTENSION = ".erm";
    private ERDiagram diagram;

    public NewDiagramWizardPage1(IStructuredSelection selection) {
        super(DisplayMessages.getMessage("wizard.new.diagram.title"), selection);
        setTitle(DisplayMessages.getMessage("wizard.new.diagram.title"));
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        setFileName("newfile");
    }

    @Override
    protected boolean validatePage() {
        boolean valid = super.validatePage();
        if (valid) {
            final String fileName = getFileName();
            if (fileName.indexOf(".") != -1 && !fileName.endsWith(EXTENSION)) {
                setErrorMessage(DisplayMessages.getMessage("error.erm.extension"));
                valid = false;
            }
        }
        if (valid) {
            String fileName = getFileName();
            if (fileName.indexOf(".") == -1) {
                fileName = fileName + EXTENSION;
            }
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            final IWorkspaceRoot root = workspace.getRoot();
            final IPath containerPath = getContainerFullPath();
            final IPath newFilePath = containerPath.append(fileName);
            if (root.getFile(newFilePath).exists()) {
                setErrorMessage("'" + fileName + "' " + DisplayMessages.getMessage("error.file.already.exists"));
                valid = false;
            }
        }
        if (valid) {
            setMessage(DisplayMessages.getMessage("wizard.new.diagram.message"));
        }
        return valid;
    }

    public void createERDiagram(String database) {
        this.diagram = new ERDiagram(database);
        diagram.init();
    }

    @Override
    protected InputStream getInitialContents() {
        final Persistent persistent = Persistent.getInstance();
        return persistent.write(diagram);
    }

    @Override
    public IFile createNewFile() {
        final String fileName = getFileName();
        if (fileName.indexOf(".") == -1) {
            setFileName(fileName + EXTENSION);
        }
        return super.createNewFile();
    }
}
