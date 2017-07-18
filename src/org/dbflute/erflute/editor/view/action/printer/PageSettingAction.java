package org.dbflute.erflute.editor.view.action.printer;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.printer.PageSettingDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class PageSettingAction extends AbstractBaseAction {

    public static final String ID = PageSettingAction.class.getName();

    public PageSettingAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.page.setting"), editor);
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = getDiagram();
        final PageSettingDialog dialog = new PageSettingDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);
        dialog.open();
    }
}
