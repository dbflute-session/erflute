package org.dbflute.erflute.editor.controller.editpart.element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.IERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public abstract class AbstractModelEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

    private static final Logger logger = Logger.getLogger(AbstractModelEditPart.class.getName());
    private static final boolean DEBUG = false;

    @Override
    public void activate() {
        super.activate();
        final AbstractModel model = (AbstractModel) getModel();
        model.addPropertyChangeListener(this);
    }

    @Override
    public void deactivate() {
        final AbstractModel model = (AbstractModel) getModel();
        model.removePropertyChangeListener(this);
        super.deactivate();
    }

    protected ERDiagram getDiagram() {
        final Object model = getRoot().getContents().getModel();
        return ((IERDiagram) model).toMaterializedDiagram();
    }

    protected Category getCurrentCategory() {
        return getDiagram().getCurrentCategory();
    }

    protected void executeCommand(Command command) {
        getViewer().getEditDomain().getCommandStack().execute(command);
    }

    @Override
    public final void propertyChange(PropertyChangeEvent event) {
        try {
            if (DEBUG) {
                logger.log(Level.INFO, getClass().getName() + ":" + event.getPropertyName() + ":" + event.toString());
            }
            doPropertyChange(event);
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    protected void doPropertyChange(PropertyChangeEvent event) {
    }
}
