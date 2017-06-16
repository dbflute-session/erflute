package org.dbflute.erflute.editor.controller.command.ermodel;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;

/**
 * @author kajiku
 */
public class DeleteVirtualDiagramCommand extends AbstractCommand {

    private final String name;
    private final ERDiagram diagram;

    public DeleteVirtualDiagramCommand(ERDiagram diagram, String name) {
        this.diagram = diagram;
        this.name = name;
    }

    @Override
    protected void doExecute() {
        final ERVirtualDiagram vdiagram = new ERVirtualDiagram(diagram);
        vdiagram.setName(name);
        diagram.removeVirtualDiagram(vdiagram);
        ERModelUtil.refreshDiagram(diagram);
    }

    @Override
    protected void doUndo() {
        // ??? by jflute
    }
}
