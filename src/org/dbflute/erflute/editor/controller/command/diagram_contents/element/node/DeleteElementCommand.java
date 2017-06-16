package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
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
        diagram.removeContent(element);
        ERModelUtil.refreshDiagram(diagram);
    }

    @Override
    protected void doUndo() {
        diagram.addWalkerPlainly(element);
    }
}
