package org.dbflute.erflute.editor.controller.editpart.element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public abstract class AbstractModelEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

    private static Logger logger = Logger.getLogger(AbstractModelEditPart.class.getName());

    private static final boolean DEBUG = false;

    @Override
    public void activate() {
        super.activate();
        final AbstractModel model = (AbstractModel) this.getModel();
        model.addPropertyChangeListener(this);
    }

    @Override
    public void deactivate() {
        final AbstractModel model = (AbstractModel) this.getModel();
        model.removePropertyChangeListener(this);
        super.deactivate();
    }

    protected ERDiagram getDiagram() {
        final Object model = this.getRoot().getContents().getModel();
        if (model instanceof ERVirtualDiagram) {
            return ((ERVirtualDiagram) model).getDiagram();
        }
        return (ERDiagram) model;
    }

    protected Category getCurrentCategory() {
        return this.getDiagram().getCurrentCategory();
    }

    protected void executeCommand(Command command) {
        this.getViewer().getEditDomain().getCommandStack().execute(command);
    }

    @Override
    public final void propertyChange(PropertyChangeEvent event) {
        try {
            if (DEBUG) {
                logger.log(Level.INFO, this.getClass().getName() + ":" + event.getPropertyName() + ":" + event.toString());
            }
            this.doPropertyChange(event);
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    protected void doPropertyChange(PropertyChangeEvent event) {
    }
}
