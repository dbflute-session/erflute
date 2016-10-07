package org.dbflute.erflute.editor.view.action.edit;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.RealModelEditor;
import org.dbflute.erflute.editor.controller.command.common.PasteCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.edit.CopyManager;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * �\��t���A�N�V����
 *
 * @author nakajima
 *
 */
public class PasteAction extends SelectionAction {

    private RealModelEditor editor;

    /**
     * �R���X�g���N�^
     *
     * @param part
     */
    public PasteAction(IWorkbenchPart part) {
        super(part);

        this.setText(DisplayMessages.getMessage("action.title.paste"));
        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));

        this.setId(ActionFactory.PASTE.getId());

        RealModelEditor editor = (RealModelEditor) part;

        this.editor = editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean calculateEnabled() {
        return CopyManager.canCopy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            execute(createCommand());
        } catch (Exception e) {
            Activator.log(e);
        }
    }

    /**
     * �\��t���R�}���h���쐬���܂��B<br>
     * �R�s�[�̈�ɕ�������Ă���m�[�h������ɕ������ē\��t���܂�<br>
     *
     * @return �\��t���R�}���h
     */
    private Command createCommand() {

        // �\��t���s�̏ꍇ
        if (!calculateEnabled()) {
            return null;
        }

        // �\��t���Ώۂ̃m�[�h�ꗗ
        NodeSet pasteList = CopyManager.paste();

        int numberOfCopy = CopyManager.getNumberOfCopy();

        // �\��t���R�}���h���쐬���܂��B
        boolean first = true;
        int x = 0;
        int y = 0;

        for (NodeElement nodeElement : pasteList) {
            if (first || x > nodeElement.getX()) {
                x = nodeElement.getX();
            }
            if (first || y > nodeElement.getY()) {
                y = nodeElement.getY();
            }

            first = false;
        }

        EditPart editPart = this.editor.getGraphicalViewer().getContents();
        Object model = editPart.getModel();

        if (model instanceof ERDiagram) {
            ERDiagram diagram = (ERDiagram) model;

            Command command =
                    new PasteCommand(editor, pasteList, diagram.mousePoint.x - x + (numberOfCopy - 1) * 20, diagram.mousePoint.y - y
                            + (numberOfCopy - 1) * 20);

            return command;
        }
        if (model instanceof ERModel) {
            ERModel erModel = (ERModel) model;
            ERDiagram diagram = erModel.getDiagram();

            Command command =
                    new PasteCommand(editor, pasteList, diagram.mousePoint.x - x + (numberOfCopy - 1) * 20, diagram.mousePoint.y - y
                            + (numberOfCopy - 1) * 20);

            return command;
        }
        return null;
    }

}
