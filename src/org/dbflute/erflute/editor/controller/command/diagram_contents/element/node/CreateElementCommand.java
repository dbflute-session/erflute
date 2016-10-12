package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class CreateElementCommand extends AbstractCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final ERDiagram diagram;
    private final DiagramWalker walker;
    private final List<DiagramWalker> enclosedWalkerList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public CreateElementCommand(ERDiagram diagram, DiagramWalker element, int x, int y, Dimension size,
            List<DiagramWalker> enclosedElementList) {
        this.diagram = diagram;
        this.walker = element;
        prepareDefaultLocation(x, y, size);
        prepareDefaultState(element);
        this.enclosedWalkerList = enclosedElementList;
    }

    private void prepareDefaultLocation(int x, int y, Dimension size) {
        if (walker instanceof Category && size != null) {
            walker.setLocation(new Location(x, y, size.width, size.height));
        } else {
            walker.setLocation(new Location(x, y, ERTable.DEFAULT_WIDTH, ERTable.DEFAULT_HEIGHT));
        }
    }

    private void prepareDefaultState(DiagramWalker element) {
        if (element instanceof ERTable) {
            final ERTable table = (ERTable) element;
            table.setLogicalName(ERTable.NEW_LOGICAL_NAME);
            table.setPhysicalName(ERTable.NEW_PHYSICAL_NAME);
        } else if (element instanceof ERView) {
            final ERView view = (ERView) element;
            view.setLogicalName(ERView.NEW_LOGICAL_NAME);
            view.setPhysicalName(ERView.NEW_PHYSICAL_NAME);
        }
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    @Override
    protected void doExecute() {
        final ERVirtualDiagram vdiagram = diagram.getCurrentVirtualDiagram();
        if (walker instanceof WalkerGroup) {
            final WalkerGroup group = (WalkerGroup) walker;
            group.setName("Your Group"); // as default
            group.setWalkers(enclosedWalkerList);
            if (vdiagram != null) {
                vdiagram.addGroup(group);
            } else { // Main Model
                diagram.addNewWalker(group);
            }
        } else { // e.g. table, note
            if (vdiagram != null) {
                vdiagram.addNewContent(walker);
            } else {
                diagram.addNewWalker(walker);
            }
        }
        ERModelUtil.refreshDiagram(diagram, walker);
    }

    @Override
    protected void doUndo() {
        if (!(walker instanceof Category)) {
            diagram.removeContent(walker);
        } else {
            final Category category = (Category) walker;
            category.getContents().clear();
            diagram.removeCategory(category);
        }
    }

    @Override
    public boolean canExecute() {
        if (walker instanceof Category) {
            if (diagram.getCurrentCategory() != null) {
                return false;
            }
        }
        return super.canExecute();
    }
}
