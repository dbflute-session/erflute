package org.dbflute.erflute.editor.view.action.edit;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.PasteCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.edit.CopyManager;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * 貼り付けアクション
 * @author nakajima
 */
public class PasteAction extends SelectionAction {

    private final MainDiagramEditor editor;

    /**
     * コンストラクタ
     * @param part
     */
    public PasteAction(IWorkbenchPart part) {
        super(part);

        this.setText(DisplayMessages.getMessage("action.title.paste"));
        final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));

        this.setId(ActionFactory.PASTE.getId());

        final MainDiagramEditor editor = (MainDiagramEditor) part;

        this.editor = editor;
    }

    @Override
    protected boolean calculateEnabled() {
        return CopyManager.canCopy();
    }

    @Override
    public void run() {
        try {
            execute(createCommand());
        } catch (final Exception e) {
            Activator.error(e);
        }
    }

    /**
     * 貼り付けコマンドを作成します。<br>
     * コピー領域に複製されているノードをさらに複製して貼り付けます<br>
     * @return 貼り付けコマンド
     */
    private Command createCommand() {

        // 貼り付け不可の場合ꍇ
        if (!calculateEnabled()) {
            return null;
        }

        // 貼り付け対象のノード一覧
        final DiagramWalkerSet pasteList = CopyManager.paste();

        final int numberOfCopy = CopyManager.getNumberOfCopy();

        // 貼り付けコマンドを作成します。
        boolean first = true;
        int x = 0;
        int y = 0;

        for (final DiagramWalker nodeElement : pasteList) {
            if (first || x > nodeElement.getX()) {
                x = nodeElement.getX();
            }
            if (first || y > nodeElement.getY()) {
                y = nodeElement.getY();
            }

            first = false;
        }

        final EditPart editPart = this.editor.getGraphicalViewer().getContents();
        final Object model = editPart.getModel();

        if (model instanceof ERDiagram) {
            final ERDiagram diagram = (ERDiagram) model;

            final Command command = new PasteCommand(editor, pasteList, diagram.mousePoint.x - x + (numberOfCopy - 1) * 20,
                    diagram.mousePoint.y - y + (numberOfCopy - 1) * 20);

            return command;
        }
        if (model instanceof ERVirtualDiagram) {
            final ERVirtualDiagram erModel = (ERVirtualDiagram) model;
            final ERDiagram diagram = erModel.getDiagram();

            final Command command = new PasteCommand(editor, pasteList, diagram.mousePoint.x - x + (numberOfCopy - 1) * 20,
                    diagram.mousePoint.y - y + (numberOfCopy - 1) * 20);

            return command;
        }
        return null;
    }
}
