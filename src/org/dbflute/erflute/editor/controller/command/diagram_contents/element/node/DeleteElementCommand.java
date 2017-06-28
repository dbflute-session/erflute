package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class DeleteElementCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final DiagramWalker element;

    public DeleteElementCommand(ERDiagram diagram, DiagramWalker element) {
        this.diagram = diagram;
        this.element = element;
    }

    @Override
    protected void doExecute() {
        diagram.removeWalker(element);
        // 以下コメントを外すと要素削除時、別要素の接続線が画面左上を指す不具合が発生する。
        // ERModelUtil.refreshDiagram(diagram);
    }

    @Override
    protected void doUndo() {
        diagram.addWalkerPlainly(element);
    }
}
