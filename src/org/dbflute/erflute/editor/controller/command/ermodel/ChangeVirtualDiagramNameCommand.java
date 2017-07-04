package org.dbflute.erflute.editor.controller.command.ermodel;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;

public class ChangeVirtualDiagramNameCommand extends AbstractCommand {

    private final ERVirtualDiagram virtualDiagram;
    private final String oldName;
    private final String newName;

    public ChangeVirtualDiagramNameCommand(ERVirtualDiagram virtualDiagram, String newName) {
        this.virtualDiagram = virtualDiagram;
        this.oldName = virtualDiagram.getName();
        this.newName = newName;
    }

    private ERDiagram getDiagram() {
        return virtualDiagram.getDiagram();
    }

    @Override
    protected void doExecute() {
        virtualDiagram.setName(newName);
        getDiagram().getDiagramContents().getVirtualDiagramSet().changeVdiagram(virtualDiagram);

        if (virtualDiagram.getDiagram().getCurrentVirtualDiagram() == virtualDiagram) {
            virtualDiagram.getDiagram().getEditor().setPageText(newName);
        }
    }

    @Override
    protected void doUndo() {
        virtualDiagram.setName(oldName);
        getDiagram().getDiagramContents().getVirtualDiagramSet().changeVdiagram(virtualDiagram);

        if (virtualDiagram.getDiagram().getCurrentVirtualDiagram() == virtualDiagram) {
            virtualDiagram.getDiagram().getEditor().setPageText(oldName);
        }
    }
}
