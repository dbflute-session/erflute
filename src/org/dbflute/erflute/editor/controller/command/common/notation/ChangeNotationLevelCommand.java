package org.dbflute.erflute.editor.controller.command.common.notation;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeNotationLevelCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final int oldNotationLevel;

    private final int newNotationLevel;

    private final DiagramSettings settings;

    public ChangeNotationLevelCommand(ERDiagram diagram, int notationLevel) {
        this.diagram = diagram;
        this.settings = diagram.getDiagramContents().getSettings();
        this.newNotationLevel = notationLevel;
        this.oldNotationLevel = this.settings.getNotationLevel();
    }

    @Override
    protected void doExecute() {
        this.settings.setNotationLevel(this.newNotationLevel);
        this.diagram.changeAll();
        ERModelUtil.refreshDiagram(diagram);
    }

    @Override
    protected void doUndo() {
        this.settings.setNotationLevel(this.oldNotationLevel);
        this.diagram.changeAll();
    }
}
