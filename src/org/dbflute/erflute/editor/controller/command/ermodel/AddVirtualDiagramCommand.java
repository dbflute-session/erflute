package org.dbflute.erflute.editor.controller.command.ermodel;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class AddVirtualDiagramCommand extends AbstractCommand {

    private final String name;
    private final ERDiagram diagram;

    public AddVirtualDiagramCommand(ERDiagram diagram, String name) {
        this.diagram = diagram;
        this.name = name;
    }

    @Override
    protected void doExecute() {
        final ERVirtualDiagram vdiagram = new ERVirtualDiagram(diagram);
        vdiagram.setName(name);
        diagram.setCurrentVirtualDiagram(vdiagram);
        diagram.addVirtualDiagram(vdiagram);
    }

    @Override
    protected void doUndo() {
        // not support
    }
}
