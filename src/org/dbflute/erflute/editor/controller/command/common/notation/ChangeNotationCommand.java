package org.dbflute.erflute.editor.controller.command.common.notation;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeNotationCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final String oldNotation;
    private final String newNotation;
    private final DiagramSettings settings;

    public ChangeNotationCommand(ERDiagram diagram, String notation) {
        this.diagram = diagram;
        this.settings = diagram.getDiagramContents().getSettings();
        this.newNotation = notation;
        this.oldNotation = settings.getNotation();
    }

    @Override
    protected void doExecute() {
        settings.setNotation(newNotation);
        diagram.changeAll();
    }

    @Override
    protected void doUndo() {
        settings.setNotation(oldNotation);
        diagram.changeAll();
    }
}
