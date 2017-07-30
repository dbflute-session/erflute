package org.dbflute.erflute.editor.controller.command.category;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.CategorySettings;

public class ChangeFreeLayoutCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final boolean oldFreeLayout;
    private final boolean newFreeLayout;
    private final CategorySettings categorySettings;

    public ChangeFreeLayoutCommand(ERDiagram diagram, boolean isFreeLayout) {
        this.diagram = diagram;
        this.categorySettings = diagram.getDiagramContents().getSettings().getCategorySetting();

        this.newFreeLayout = isFreeLayout;
        this.oldFreeLayout = categorySettings.isFreeLayout();
    }

    @Override
    protected void doExecute() {
        categorySettings.setFreeLayout(newFreeLayout);
        diagram.changeAll();
    }

    @Override
    protected void doUndo() {
        categorySettings.setFreeLayout(oldFreeLayout);
        diagram.changeAll();
    }
}
