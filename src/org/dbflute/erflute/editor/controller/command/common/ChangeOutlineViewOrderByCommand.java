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
        this.settings = diagram.getDiagramContents().getSettings();
        this.newViewOrderBy = viewOrderBy;
        this.oldViewOrderBy = settings.getViewOrderBy();
    }

    @Override
    protected void doExecute() {
        settings.setViewOrderBy(newViewOrderBy);
        diagram.changeAll();
    }

    @Override
    protected void doUndo() {
        settings.setViewOrderBy(oldViewOrderBy);
        diagram.changeAll();
    }
}
