package org.dbflute.erflute.editor.controller.command.common;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeOutlineViewOrderByCommand extends AbstractCommand {

    private ERDiagram diagram;

    private int oldViewOrderBy;

    private int newViewOrderBy;

    private DiagramSettings settings;

    public ChangeOutlineViewOrderByCommand(ERDiagram diagram, int viewOrderBy) {
        this.diagram = diagram;
        this.settings = this.diagram.getDiagramContents().getSettings();
        this.newViewOrderBy = viewOrderBy;
        this.oldViewOrderBy = this.settings.getViewOrderBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.settings.setViewOrderBy(this.newViewOrderBy);
        this.diagram.changeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.settings.setViewOrderBy(this.oldViewOrderBy);
        this.diagram.changeAll();
    }
}
