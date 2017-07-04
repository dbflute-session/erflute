package org.dbflute.erflute.wizard;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.wizard.page.NewDiagramWizardPage1;
import org.dbflute.erflute.wizard.page.NewDiagramWizardPage2;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class NewDiagramWizard extends Wizard implements INewWizard {

    private NewDiagramWizardPage1 page1;
    private NewDiagramWizardPage2 page2;
    private IStructuredSelection selection;
    private IWorkbench workbench;

    @Override
    public boolean performFinish() {
        try {
            final String database = page2.getDatabase();
            page1.createERDiagram(database);
            final IFile file = page1.createNewFile();
            if (file == null) {
                return false;
            }
            final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
            IDE.openEditor(page, file, true);
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }

    @Override
    public void addPages() {
        this.page1 = new NewDiagramWizardPage1(selection);
        addPage(page1);

        this.page2 = new NewDiagramWizardPage2(selection);
        addPage(page2);
    }
}
