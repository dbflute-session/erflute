package org.dbflute.erflute.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.editor.InputDiagramValidator;
import org.dbflute.erflute.editor.controller.command.category.ChangeWalkerGroupNameCommand;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.DiagramWalkerComponentEditPolicy;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.view.figure.WalkerGroupFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 * @author kajiku
 */
public class WalkerGroupEditPart extends DiagramWalkerEditPart implements IResizable {

    public WalkerGroupEditPart() {
        super();
    }

    @Override
    protected IFigure createFigure() {
        final WalkerGroup group = (WalkerGroup) getModel();
        return new WalkerGroupFigure(group.getName());
    }

    @Override
    public void doPropertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(WalkerGroup.PROPERTY_CHANGE_WALKER_GROUP)) {
            refreshChildren();
            refreshVisuals();
        }
        super.doPropertyChange(event);
    }

    @Override
    protected Rectangle getRectangle() {
        final Rectangle rectangle = super.getRectangle();
        final WalkerGroup group = (WalkerGroup) this.getModel();
        final EditPart contents = getRoot().getContents();
        for (final Object child : contents.getChildren()) {
            if (child instanceof DiagramWalkerEditPart) {
                final DiagramWalkerEditPart editPart = (DiagramWalkerEditPart) child;
                if (group.contains((DiagramWalker) editPart.getModel())) {
                    final Rectangle bounds = editPart.getFigure().getBounds();
                    if (bounds.x + bounds.width > rectangle.x + rectangle.width) {
                        rectangle.width = bounds.x + bounds.width - rectangle.x;
                    }
                    if (bounds.y + bounds.height > rectangle.y + rectangle.height) {
                        rectangle.height = bounds.y + bounds.height - rectangle.y;
                    }
                    if (rectangle.width != group.getWidth() || rectangle.height != group.getHeight()) {
                        group.setLocation(new Location(group.getX(), group.getY(), rectangle.width, rectangle.height));
                    }
                }
            }
        }
        return rectangle;
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new DiagramWalkerComponentEditPolicy());
        super.createEditPolicies();
    }

    @Override
    public void performRequestOpen() {
        final WalkerGroup group = (WalkerGroup) this.getModel();
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final InputDiagramValidator validator = new InputDiagramValidator();
        final InputDialog dialog = new InputDialog(shell, "Group Name Setting", "Input group name", group.getName(), validator);
        if (dialog.open() == IDialogConstants.OK_ID) {
            final CompoundCommand command = new CompoundCommand();
            command.add(new ChangeWalkerGroupNameCommand(group, dialog.getValue()));
            executeCommand(command.unwrap());
        }
    }
}
