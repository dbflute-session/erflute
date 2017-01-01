package org.dbflute.erflute.editor.controller.command.category;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.CategorySettings;

public class ChangeShowReferredTablesCommand extends AbstractCommand {

    private ERDiagram diagram;

    private boolean oldShowReferredTables;

    private boolean newShowReferredTables;

    private CategorySettings categorySettings;

    public ChangeShowReferredTablesCommand(ERDiagram diagram, boolean isShowReferredTables) {
        this.diagram = diagram;
        this.categorySettings = this.diagram.getDiagramContents().getSettings().getCategorySetting();

        this.newShowReferredTables = isShowReferredTables;
        this.oldShowReferredTables = this.categorySettings.isFreeLayout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.categorySettings.setShowReferredTables(this.newShowReferredTables);
        this.diagram.changeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.categorySettings.setShowReferredTables(this.oldShowReferredTables);
        this.diagram.changeAll();
    }
}
