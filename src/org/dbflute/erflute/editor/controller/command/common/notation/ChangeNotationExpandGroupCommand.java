package org.dbflute.erflute.editor.controller.command.common.notation;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeNotationExpandGroupCommand extends AbstractCommand {

    private ERDiagram diagram;

    private boolean oldNotationExpandGroup;

    private boolean newNotationExpandGroup;

    private DiagramSettings settings;

    public ChangeNotationExpandGroupCommand(ERDiagram diagram, boolean notationExpandGroup) {
        this.diagram = diagram;
        this.settings = this.diagram.getDiagramContents().getSettings();
        this.newNotationExpandGroup = notationExpandGroup;
        this.oldNotationExpandGroup = this.settings.isNotationExpandGroup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.settings.setNotationExpandGroup(this.newNotationExpandGroup);

        for (TableView tableView : this.diagram.getDiagramContents().getDiagramWalkers().getTableViewList()) {
            tableView.setDirty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.settings.setNotationExpandGroup(this.oldNotationExpandGroup);
        for (TableView tableView : this.diagram.getDiagramContents().getDiagramWalkers().getTableViewList()) {
            tableView.setDirty();
        }
    }
}
