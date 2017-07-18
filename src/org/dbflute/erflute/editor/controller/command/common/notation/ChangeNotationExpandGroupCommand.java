package org.dbflute.erflute.editor.controller.command.common.notation;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeNotationExpandGroupCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final boolean oldNotationExpandGroup;
    private final boolean newNotationExpandGroup;
    private final DiagramSettings settings;

    public ChangeNotationExpandGroupCommand(ERDiagram diagram, boolean notationExpandGroup) {
        this.diagram = diagram;
        this.settings = diagram.getDiagramContents().getSettings();
        this.newNotationExpandGroup = notationExpandGroup;
        this.oldNotationExpandGroup = settings.isNotationExpandGroup();
    }

    @Override
    protected void doExecute() {
        settings.setNotationExpandGroup(newNotationExpandGroup);
        for (final TableView tableView : diagram.getDiagramContents().getDiagramWalkers().getTableViewList()) {
            tableView.setDirty();
        }
    }

    @Override
    protected void doUndo() {
        settings.setNotationExpandGroup(oldNotationExpandGroup);
        for (final TableView tableView : diagram.getDiagramContents().getDiagramWalkers().getTableViewList()) {
            tableView.setDirty();
        }
    }
}
