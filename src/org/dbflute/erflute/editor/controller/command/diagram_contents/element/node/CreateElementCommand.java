package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.view.dialog.dbexport.ErrorDialog;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class CreateElementCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final NodeElement element;

    private final List<NodeElement> enclosedElementList;

    public CreateElementCommand(ERDiagram diagram, NodeElement element, int x, int y, Dimension size, List<NodeElement> enclosedElementList) {
        this.diagram = diagram;
        this.element = element;

        if (this.element instanceof Category && size != null) {
            this.element.setLocation(new Location(x, y, size.width, size.height));
        } else {
            this.element.setLocation(new Location(x, y, ERTable.DEFAULT_WIDTH, ERTable.DEFAULT_HEIGHT));
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
        final ERModel currentErmodel = diagram.getCurrentErmodel();
        if (element instanceof VGroup) {
            final VGroup group = (VGroup) this.element;
            group.setName(DisplayMessages.getMessage("label.vgroup"));
            group.setContents(enclosedElementList);
            if (currentErmodel != null) {
                currentErmodel.addGroup(group);
            } else {
                final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                final String message = "Cannot use group at real model (use at virtual model)";
                final ErrorDialog dialog = new ErrorDialog(shell, message);
                dialog.open();
            }
        } else {
            if (currentErmodel != null) {
                currentErmodel.addNewContent(this.element);
                ERModelUtil.refreshDiagram(diagram, element);
            } else {
                this.diagram.addNewContent(this.element);
            }
        }
        //		if (!(this.element instanceof VGroup)) {
        //		} else {
        //			VGroup group = (VGroup) this.element;
        //			group.setName(ResourceString.getResourceString("label.vgroup"));
        //			group.setContents(this.enclosedElementList);
        //			this.diagram.addGroup(group);
        //		}
    }

    @Override
    protected void doUndo() {
        if (!(this.element instanceof Category)) {
            this.diagram.removeContent(this.element);

        } else {
            final Category category = (Category) this.element;
            category.getContents().clear();
            this.diagram.removeCategory(category);
        }
    }

    /**
     * {@inheritDoc}
     */
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
