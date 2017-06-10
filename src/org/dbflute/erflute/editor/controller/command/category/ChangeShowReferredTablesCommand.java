package org.dbflute.erflute.editor.controller.command.category;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.CategorySettings;

public class ChangeShowReferredTablesCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final boolean oldShowReferredTables;

    private final boolean newShowReferredTables;

    private final CategorySettings categorySettings;

    public ChangeShowReferredTablesCommand(ERDiagram diagram, boolean isShowReferredTables) {
        this.diagram = diagram;
        this.categorySettings = this.diagram.getDiagramContents().getSettings().getCategorySetting();

        this.newShowReferredTables = isShowReferredTables;
        this.oldShowReferredTables = this.categorySettings.isFreeLayout();
    }

    @Override
    protected void doExecute() {
        this.categorySettings.setShowReferredTables(this.newShowReferredTables);
        this.diagram.changeAll();
    }

    @Override
    protected void doUndo() {
        this.categorySettings.setShowReferredTables(this.oldShowReferredTables);
        this.diagram.changeAll();
    }
}
