package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.category;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.settings.CategorySettings;

public class DeleteCategoryCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final CategorySettings categorySettings;
    private final Category category;
    private List<Category> oldAllCategories;
    private List<Category> oldSelectedCategories;

    public DeleteCategoryCommand(ERDiagram diagram, Category category) {
        this.diagram = diagram;
        this.categorySettings = diagram.getDiagramContents().getSettings().getCategorySetting();
        this.category = category;
    }

    @Override
    protected void doExecute() {
        this.oldAllCategories = new ArrayList<>(categorySettings.getAllCategories());
        this.oldSelectedCategories = new ArrayList<>(categorySettings.getSelectedCategories());

        diagram.removeCategory(category);
    }

    @Override
    protected void doUndo() {
        categorySettings.setAllCategories(oldAllCategories);
        categorySettings.setSelectedCategories(oldSelectedCategories);
        diagram.restoreCategories();
    }
}
