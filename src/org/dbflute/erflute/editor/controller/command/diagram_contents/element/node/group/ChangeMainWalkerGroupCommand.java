package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.group;

import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroupSet;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ChangeMainWalkerGroupCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final List<WalkerGroup> oldGroups;
    private final List<WalkerGroup> walkerGroups;

    public ChangeMainWalkerGroupCommand(ERDiagram diagram, List<WalkerGroup> walkerGroups) {
        this.diagram = diagram;
        this.oldGroups = prepareGroupSet(diagram).getList();
        this.walkerGroups = walkerGroups;
    }

    @Override
    protected void doExecute() {
        prepareGroupSet(diagram).overrideAll(walkerGroups);
    }

    @Override
    protected void doUndo() {
        prepareGroupSet(diagram).overrideAll(oldGroups);
    }

    private WalkerGroupSet prepareGroupSet(ERDiagram diagram) {
        return diagram.getDiagramContents().getDiagramWalkers().getWalkerGroupSet();
    }
}
