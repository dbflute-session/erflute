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
        this.settings = this.diagram.getDiagramContents().getSettings();
        this.newDesign = design;
        this.oldDesign = this.settings.getTableStyle();
    }

    @Override
    protected void doExecute() {
        this.settings.setTableStyle(this.newDesign);
        this.diagram.change();
    }

    @Override
    protected void doUndo() {
        this.settings.setTableStyle(this.oldDesign);
        this.diagram.change();
    }
}
