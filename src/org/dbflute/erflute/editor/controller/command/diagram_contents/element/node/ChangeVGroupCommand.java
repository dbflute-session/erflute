package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.VGroup;

public class ChangeVGroupCommand extends AbstractCommand {

    private ERVirtualDiagram model;

    private List<VGroup> oldVgroups;

    private List<VGroup> vgroups;

    public ChangeVGroupCommand(ERVirtualDiagram model, List<VGroup> vgroups) {
        this.model = model;
        this.oldVgroups = model.getGroups();
        this.vgroups = vgroups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        model.setGroups(vgroups);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        model.setGroups(oldVgroups);
    }

}
