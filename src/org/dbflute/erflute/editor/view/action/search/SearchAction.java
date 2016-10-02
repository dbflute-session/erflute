package org.dbflute.erflute.editor.view.action.search;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.MainModelEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.search.SearchDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

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
