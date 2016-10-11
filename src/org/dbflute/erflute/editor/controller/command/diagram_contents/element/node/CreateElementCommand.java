package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
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
    private final DiagramWalker element;
    private final List<DiagramWalker> enclosedElementList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public CreateElementCommand(ERDiagram diagram, DiagramWalker element, int x, int y, Dimension size, List<DiagramWalker> enclosedElementList) {
        this.diagram = diagram;
        this.element = element;
        prepareDefaultLocation(x, y, size);
        prepareDefaultState(element);
        this.enclosedElementList = enclosedElementList;
    }

    private void prepareDefaultLocation(int x, int y, Dimension size) {
        if (element instanceof Category && size != null) {
            element.setLocation(new Location(x, y, size.width, size.height));
        } else {
            element.setLocation(new Location(x, y, ERTable.DEFAULT_WIDTH, ERTable.DEFAULT_HEIGHT));
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
        final ERVirtualDiagram currentErmodel = diagram.getCurrentErmodel();
        if (element instanceof WalkerGroup) {
            final WalkerGroup group = (WalkerGroup) element;
            group.setName("Your Group");
            group.setContents(enclosedElementList);
            if (currentErmodel != null) {
                currentErmodel.addGroup(group);
            } else { // Main Model
                diagram.addNewContent(group);
            }
        } else { // e.g. table, note
            if (currentErmodel != null) {
                currentErmodel.addNewContent(element);
            } else {
                diagram.addNewContent(element);
            }
        }
        if (currentErmodel != null) {
            ERModelUtil.refreshDiagram(diagram, element);
        }
    }

    @Override
    protected void doUndo() {
        if (!(element instanceof Category)) {
            diagram.removeContent(element);
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
