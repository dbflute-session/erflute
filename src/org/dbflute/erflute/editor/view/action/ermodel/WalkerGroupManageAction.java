package org.dbflute.erflute.editor.view.action.ermodel;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.VirtualDiagramEditor;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.group.ChangeMainWalkerGroupCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.group.ChangeVirtualWalkerGroupCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.walkergroup.MainWalkerGroupManageDialog;
import org.dbflute.erflute.editor.view.dialog.walkergroup.VirtualWalkerGroupManageDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WalkerGroupManageAction extends AbstractBaseAction {

    public static final String ID = WalkerGroupManageAction.class.getName();

    // TODO jflute for main diagram group (2016/10/12)
    public WalkerGroupManageAction(VirtualDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.vgroup.manage"), editor);
    }

    @Override
    public void execute(Event event) {
        final MainDiagramEditor editorPart = getEditorPart();
        if (editorPart instanceof VirtualDiagramEditor) {
            final VirtualDiagramEditor virtualDiagramEditor = (VirtualDiagramEditor) editorPart;
            final ERVirtualDiagram vdiagram = virtualDiagramEditor.getVirtualDiagram();
            final ERVirtualDiagram newVDiagram = (ERVirtualDiagram) vdiagram.clone();
            final VirtualWalkerGroupManageDialog dialog = createWalkerGroupVirtualManageDialog(newVDiagram);
            if (dialog.open() == IDialogConstants.OK_ID) {
                execute(createChangeVirtualWalkerGroupCommand(vdiagram, newVDiagram));
                // what is this? by jflute
                //ChangeSettingsCommand command = new ChangeSettingsCommand(diagram, settings);
                //this.execute(command);
            }
        } else {
            final ERDiagram diagram = editorPart.getDiagram();
            final ERDiagram newDiagram = (ERDiagram) diagram.clone();
            final MainWalkerGroupManageDialog dialog = createMainWalkerGroupManageDialog(newDiagram);
            if (dialog.open() == IDialogConstants.OK_ID) {
                execute(createChangeMainWalkerGroupCommand(diagram, newDiagram));
            }
        }
    }

    private VirtualWalkerGroupManageDialog createWalkerGroupVirtualManageDialog(ERVirtualDiagram newVDiagram) {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        return new VirtualWalkerGroupManageDialog(shell, newVDiagram);
    }

    private MainWalkerGroupManageDialog createMainWalkerGroupManageDialog(ERDiagram newDiagram) {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        return new MainWalkerGroupManageDialog(shell, newDiagram);
    }

    private ChangeVirtualWalkerGroupCommand createChangeVirtualWalkerGroupCommand(ERVirtualDiagram vdiagram, ERVirtualDiagram newVDiagram) {
        return new ChangeVirtualWalkerGroupCommand(vdiagram, newVDiagram.getWalkerGroups());
    }

    private ChangeMainWalkerGroupCommand createChangeMainWalkerGroupCommand(ERDiagram diagram, ERDiagram newVDiagram) {
        return new ChangeMainWalkerGroupCommand(diagram, newVDiagram.getDiagramContents().getDiagramWalkers().getWalkerGroupSet().getList());
    }
}
