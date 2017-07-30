package org.dbflute.erflute.editor.view.action.search;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.search.SearchDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class SearchAction extends AbstractBaseAction {

    public static final String ID = SearchAction.class.getName();

    public SearchAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.find"), editor);
        setActionDefinitionId("org.eclipse.ui.edit.findReplace");
        setImageDescriptor(Activator.getImageDescriptor(ImageKey.FIND));
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = getDiagram();
        final SearchDialog dialog =
                new SearchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        getGraphicalViewer(), getEditorPart(), diagram);
        dialog.open();
    }
}
