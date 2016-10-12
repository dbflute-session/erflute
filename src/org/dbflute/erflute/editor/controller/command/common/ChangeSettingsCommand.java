package org.dbflute.erflute.editor.controller.command.common;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.Settings;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ChangeSettingsCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final Settings oldSettings;
    private final Settings settings;

    public ChangeSettingsCommand(ERDiagram diagram, Settings settings) {
        this.diagram = diagram;
        this.oldSettings = this.diagram.getDiagramContents().getSettings();
        this.settings = settings;
    }

    @Override
    protected void doExecute() {
        this.diagram.setSettings(settings);
    }

    @Override
    protected void doUndo() {
        this.diagram.setSettings(oldSettings);
    }
}
