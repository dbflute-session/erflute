package org.dbflute.erflute.editor.controller.command.category;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;

public class ChangeVGroupNameCommand extends AbstractCommand {

    private ERDiagram diagram;

    private String oldName;

    private String newName;

    private WalkerGroup category;

    public ChangeVGroupNameCommand(ERDiagram diagram, WalkerGroup category, String newName) {
        this.diagram = diagram;
        this.category = category;
        this.newName = newName;

        this.oldName = category.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.category.setName(this.newName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.category.setName(this.oldName);
    }

}
