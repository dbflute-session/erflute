package org.dbflute.erflute.editor.view.action.ermodel;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.VirtualModelEditor;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.ChangeVGroupCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.category.VGroupManageDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class VGroupManageAction extends AbstractBaseAction {

    public static final String ID = VGroupManageAction.class.getName();

    public VGroupManageAction(VirtualModelEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.vgroup.manage"), editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        ERModel model = ((VirtualModelEditor) getEditorPart()).getModel();
        ERModel newModel = (ERModel) model.clone();

        VGroupManageDialog dialog = new VGroupManageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), newModel);

        if (dialog.open() == IDialogConstants.OK_ID) {
            System.out.println("ok");
            execute(new ChangeVGroupCommand(model, newModel.getGroups()));
            //			ChangeSettingsCommand command = new ChangeSettingsCommand(diagram,
            //					settings);
            //			this.execute(command);
        }
    }

}
