package org.insightech.er.editor.view.action.search;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.MainModelEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.search.SearchDialog;

public class SearchAction extends AbstractBaseAction {

    public static final String ID = SearchAction.class.getName();

    public SearchAction(MainModelEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.find"), editor);
        this.setActionDefinitionId("org.eclipse.ui.edit.findReplace");
        this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.FIND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        SearchDialog dialog =
                new SearchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), this.getGraphicalViewer(),
                        getEditorPart(), diagram);

        dialog.open();
    }

}
