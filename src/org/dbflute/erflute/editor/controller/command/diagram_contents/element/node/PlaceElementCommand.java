package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.controller.editpart.element.AbstractModelEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.eclipse.draw2d.geometry.Dimension;

public class PlaceElementCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final DiagramWalker element;
    private final List<DiagramWalker> enclosedElementList;

    public PlaceElementCommand(ERDiagram diagram, AbstractModelEditPart editPart, DiagramWalker element,
            int x, int y, Dimension size, List<DiagramWalker> enclosedElementList) {
        this.diagram = diagram;
        this.element = element;

        if (element instanceof Category && size != null) {
            element.setLocation(new Location(x, y, size.width, size.height));
        } else {
            element.setLocation(new Location(x, y, ERTable.DEFAULT_WIDTH, ERTable.DEFAULT_HEIGHT));
        }

        if (element instanceof ERTable) {
            final ERTable table = (ERTable) element;
            table.setLogicalName(ERTable.NEW_LOGICAL_NAME);
            table.setPhysicalName(ERTable.NEW_PHYSICAL_NAME);
        } else if (element instanceof ERView) {
            final ERView view = (ERView) element;
            view.setLogicalName(ERView.NEW_LOGICAL_NAME);
            view.setPhysicalName(ERView.NEW_PHYSICAL_NAME);
        }

        this.enclosedElementList = enclosedElementList;
    }

    @Override
    protected void doExecute() {
        if (!(element instanceof Category)) {
            diagram.addNewWalker(element);
        } else {
            final Category category = (Category) element;
            category.setName(DisplayMessages.getMessage("label.category"));
            category.setContents(enclosedElementList);
            diagram.addCategory(category);
        }
    }

    @Override
    protected void doUndo() {
        if (!(element instanceof Category)) {
            diagram.removeWalker(element);
        } else {
            final Category category = (Category) element;
            category.getContents().clear();
            diagram.removeCategory(category);
        }
    }

    @Override
    public boolean canExecute() {
        if (element instanceof Category) {
            if (diagram.getCurrentCategory() != null) {
                return false;
            }
        }

        return super.canExecute();
    }
}
