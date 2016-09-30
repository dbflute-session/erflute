package org.insightech.er.wizard;

import org.dbflute.erflute.Activator;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.insightech.er.wizard.page.NewDiagramWizardPage1;
import org.insightech.er.wizard.page.NewDiagramWizardPage2;

/**
 * #analyzed 新規ER図の作成の入り口画面 (New - ERMaster)
 * @author ermaster
 * @author jflute
 */
public class NewDiagramWizard extends Wizard implements INewWizard {

    private NewDiagramWizardPage1 page1;
    private NewDiagramWizardPage2 page2;
    private IStructuredSelection selection;
    private IWorkbench workbench;

    @Override
    public boolean performFinish() {
        try {
            String database = this.page2.getDatabase();
            this.page1.createERDiagram(database);
            IFile file = this.page1.createNewFile();
            if (file == null) {
                return false;
            }
            IWorkbenchPage page = this.workbench.getActiveWorkbenchWindow().getActivePage();
            IDE.openEditor(page, file, true);
        } catch (Exception e) {
            Activator.showExceptionDialog(e);
        }
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }

    @Override
    public void addPages() {
        this.page1 = new NewDiagramWizardPage1(this.selection);
        this.addPage(this.page1);

        this.page2 = new NewDiagramWizardPage2(this.selection);
        this.addPage(this.page2);
    }
}
