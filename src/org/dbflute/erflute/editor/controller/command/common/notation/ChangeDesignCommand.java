package org.dbflute.erflute.editor.controller.command.common.notation;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeDesignCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final String oldDesign;
    private final String newDesign;
    private final DiagramSettings settings;

    public ChangeDesignCommand(ERDiagram diagram, String design) {
        this.diagram = diagram;
        this.settings = diagram.getDiagramContents().getSettings();
        this.newDesign = design;
        this.oldDesign = settings.getTableStyle();
    }

    @Override
    protected void doExecute() {
        settings.setTableStyle(newDesign);
        diagram.change();
        diagram.refreshVirtualDiagram();
    }

    @Override
    protected void doUndo() {
        settings.setTableStyle(oldDesign);
        diagram.change();
        diagram.refreshVirtualDiagram();
    }
}
