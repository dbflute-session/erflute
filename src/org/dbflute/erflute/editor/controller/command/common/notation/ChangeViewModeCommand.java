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
        this.settings = diagram.getDiagramContents().getSettings();
        this.newViewMode = viewMode;
        this.oldViewMode = settings.getViewMode();
    }

    @Override
    protected void doExecute() {
        settings.setViewMode(newViewMode);
        diagram.changeAll();
    }

    @Override
    protected void doUndo() {
        settings.setViewMode(oldViewMode);
        diagram.changeAll();
    }
}
