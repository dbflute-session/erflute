package org.dbflute.erflute.editor.controller.command.category;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.CategorySetting;

public class ChangeFreeLayoutCommand extends AbstractCommand {

    private ERDiagram diagram;

    private boolean oldFreeLayout;

    private boolean newFreeLayout;

    private CategorySetting categorySettings;

    public ChangeFreeLayoutCommand(ERDiagram diagram, boolean isFreeLayout) {
        this.diagram = diagram;
        this.categorySettings = this.diagram.getDiagramContents().getSettings().getCategorySetting();

        this.newFreeLayout = isFreeLayout;
        this.oldFreeLayout = this.categorySettings.isFreeLayout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.categorySettings.setFreeLayout(this.newFreeLayout);
        this.diagram.changeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.categorySettings.setFreeLayout(this.oldFreeLayout);
        this.diagram.changeAll();
    }
}
