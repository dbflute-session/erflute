package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;

public class DeleteElementCommand extends AbstractCommand {

    private ERDiagram container;

    private DiagramWalker element;

    public DeleteElementCommand(ERDiagram container, DiagramWalker element) {
        this.container = container;
        this.element = element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.container.removeContent(this.element);
        ERModelUtil.refreshDiagram(element.getDiagram()); // TODO ���܂����t���b�V���������Ȃ�

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.container.addContent(this.element);
    }
}
