package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.controller.editpart.element.AbstractModelEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.eclipse.draw2d.geometry.Dimension;

public class PlaceElementCommand extends AbstractCommand {

    private ERDiagram diagram;

    private DiagramWalker element;

    private List<DiagramWalker> enclosedElementList;

    /** Model or Diagram */
    private AbstractModelEditPart editPart;

    public PlaceElementCommand(ERDiagram diagram, AbstractModelEditPart editPart, DiagramWalker element, int x, int y, Dimension size,
            List<DiagramWalker> enclosedElementList) {
        this.diagram = diagram;
        this.element = element;
        this.editPart = editPart;

        if (this.element instanceof Category && size != null) {
            this.element.setLocation(new Location(x, y, size.width, size.height));
        } else {
            this.element.setLocation(new Location(x, y, ERTable.DEFAULT_WIDTH, ERTable.DEFAULT_HEIGHT));
        }

        if (element instanceof ERTable) {
            ERTable table = (ERTable) element;
            table.setLogicalName(ERTable.NEW_LOGICAL_NAME);
            table.setPhysicalName(ERTable.NEW_PHYSICAL_NAME);

        } else if (element instanceof ERView) {
            ERView view = (ERView) element;
            view.setLogicalName(ERView.NEW_LOGICAL_NAME);
            view.setPhysicalName(ERView.NEW_PHYSICAL_NAME);
        }

        this.enclosedElementList = enclosedElementList;
    }

    @Override
    protected void doExecute() {
        if (!(this.element instanceof Category)) {
            this.diagram.addNewWalker(this.element);

        } else {
            Category category = (Category) this.element;
            category.setName(DisplayMessages.getMessage("label.category"));
            category.setContents(this.enclosedElementList);
            this.diagram.addCategory(category);
        }
    }

    @Override
    protected void doUndo() {
        if (!(this.element instanceof Category)) {
            this.diagram.removeContent(this.element);

        } else {
            Category category = (Category) this.element;
            category.getContents().clear();
            this.diagram.removeCategory(category);
        }
    }

    @Override
    public boolean canExecute() {
        if (this.element instanceof Category) {
            if (this.diagram.getCurrentCategory() != null) {
                return false;
            }
        }

        return super.canExecute();
    }
}
