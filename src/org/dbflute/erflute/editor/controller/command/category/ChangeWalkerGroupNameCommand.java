package org.dbflute.erflute.editor.controller.command.category;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ChangeWalkerGroupNameCommand extends AbstractCommand {

    private final WalkerGroup group;
    private final String oldName;
    private final String newName;

    public ChangeWalkerGroupNameCommand(WalkerGroup group, String newName) {
        this.group = group;
        this.newName = newName;
        this.oldName = group.getName();
    }

    @Override
    protected void doExecute() {
        group.setName(newName);
    }

    @Override
    protected void doUndo() {
        group.setName(oldName);
    }
}
