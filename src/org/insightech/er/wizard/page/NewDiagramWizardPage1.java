package org.insightech.er.wizard.page;

import java.io.IOException;
import java.io.InputStream;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.persistent.Persistent;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class NewDiagramWizardPage1 extends WizardNewFileCreationPage {

    private static final String EXTENSION = ".erm";
    private ERDiagram diagram;

    public NewDiagramWizardPage1(IStructuredSelection selection) {
        super(DisplayMessages.getMessage("wizard.new.diagram.title"), selection);
        this.setTitle(DisplayMessages.getMessage("wizard.new.diagram.title"));
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        this.setFileName("newfile");
    }

    @Override
    protected boolean validatePage() {
        boolean valid = super.validatePage();
        if (valid) {
            final String fileName = this.getFileName();
            if (fileName.indexOf(".") != -1 && !fileName.endsWith(EXTENSION)) {
                this.setErrorMessage(DisplayMessages.getMessage("error.erm.extension"));
                valid = false;
            }
        }
        if (valid) {
            String fileName = this.getFileName();
            if (fileName.indexOf(".") == -1) {
                fileName = fileName + EXTENSION;
            }
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            final IWorkspaceRoot root = workspace.getRoot();
            final IPath containerPath = this.getContainerFullPath();
            final IPath newFilePath = containerPath.append(fileName);
            if (root.getFile(newFilePath).exists()) {
                this.setErrorMessage("'" + fileName + "' " + DisplayMessages.getMessage("error.file.already.exists"));
                valid = false;
            }
        }
        if (valid) {
            this.setMessage(DisplayMessages.getMessage("wizard.new.diagram.message"));
        }
        return valid;
    }

    public void createERDiagram(String database) {
        this.diagram = new ERDiagram(database);
        this.diagram.init();
    }

    @Override
    protected InputStream getInitialContents() {
        final Persistent persistent = Persistent.getInstance();
        try {
            return persistent.createInputStream(this.diagram);
        } catch (final IOException e) {
            Activator.showExceptionDialog(e);
        }
        return null;
    }

    @Override
    public IFile createNewFile() {
        final String fileName = this.getFileName();
        if (fileName.indexOf(".") == -1) {
            this.setFileName(fileName + EXTENSION);
        }
        return super.createNewFile();
    }
}
