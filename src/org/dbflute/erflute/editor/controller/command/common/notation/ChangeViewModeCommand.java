package org.dbflute.erflute.editor.controller.command.common.notation;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeViewModeCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final int oldViewMode;

    private final int newViewMode;

    private final DiagramSettings settings;

    public ChangeViewModeCommand(ERDiagram diagram, int viewMode) {
        this.diagram = diagram;
        this.settings = this.diagram.getDiagramContents().getSettings();
        this.newViewMode = viewMode;
        this.oldViewMode = this.settings.getViewMode();
    }

    @Override
    protected void doExecute() {
        this.settings.setViewMode(this.newViewMode);
        this.diagram.changeAll();
    }

    @Override
    protected void doUndo() {
        this.settings.setViewMode(this.oldViewMode);
        this.diagram.changeAll();
    }
}
