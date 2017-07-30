package org.dbflute.erflute.editor.controller.command.common;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ChangeSettingsCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final DiagramSettings oldSettings;
    private final DiagramSettings settings;

    public ChangeSettingsCommand(ERDiagram diagram, DiagramSettings settings) {
        this.diagram = diagram;
        this.oldSettings = diagram.getDiagramContents().getSettings();
        this.settings = settings;
    }

    @Override
    protected void doExecute() {
        diagram.setSettings(settings);
    }

    @Override
    protected void doUndo() {
        diagram.setSettings(oldSettings);
    }
}
