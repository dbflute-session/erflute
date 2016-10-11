package org.dbflute.erflute.editor.view.action.printer;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class PrintAction extends AbstractBaseAction {

    public static final String ID = PrintAction.class.getName();

    public PrintAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.find"), editor);
        this.setActionDefinitionId("org.eclipse.ui.edit.findReplace");
    }

    @Override
    public void execute(Event event) throws Exception {
        PrintDialog dialog = new PrintDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 0);
        dialog.open();
    }

}
