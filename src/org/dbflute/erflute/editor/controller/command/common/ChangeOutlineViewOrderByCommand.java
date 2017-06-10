package org.dbflute.erflute.editor.controller.command.common;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeOutlineViewOrderByCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final int oldViewOrderBy;

    private final int newViewOrderBy;

    private final DiagramSettings settings;

    public ChangeOutlineViewOrderByCommand(ERDiagram diagram, int viewOrderBy) {
        this.diagram = diagram;
        this.settings = this.diagram.getDiagramContents().getSettings();
        this.newViewOrderBy = viewOrderBy;
        this.oldViewOrderBy = this.settings.getViewOrderBy();
    }

    @Override
    protected void doExecute() {
        this.settings.setViewOrderBy(this.newViewOrderBy);
        this.diagram.changeAll();
    }

    @Override
    protected void doUndo() {
        this.settings.setViewOrderBy(this.oldViewOrderBy);
        this.diagram.changeAll();
    }
}
