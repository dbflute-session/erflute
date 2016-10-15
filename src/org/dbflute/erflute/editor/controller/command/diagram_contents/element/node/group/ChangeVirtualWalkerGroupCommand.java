package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.group;

import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ChangeVirtualWalkerGroupCommand extends AbstractCommand {

    private final ERVirtualDiagram vdiagram;
    private final List<WalkerGroup> oldGroups;
    private final List<WalkerGroup> walkerGroups;

    public ChangeVirtualWalkerGroupCommand(ERVirtualDiagram vdiagram, List<WalkerGroup> walkerGroups) {
        this.vdiagram = vdiagram;
        this.oldGroups = vdiagram.getWalkerGroups();
        this.walkerGroups = walkerGroups;
    }

    @Override
    protected void doExecute() {
        vdiagram.setWalkerGroups(walkerGroups);
    }

    @Override
    protected void doUndo() {
        vdiagram.setWalkerGroups(oldGroups);
    }
}
