package org.dbflute.erflute.editor.controller.editpart.element.connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class ERDiagramConnectionEditPart extends AbstractConnectionEditPart implements PropertyChangeListener {

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

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        try {
            if (event.getPropertyName().equals(WalkerConnection.PROPERTY_CHANGE_BEND_POINT)) {
                refreshBendpoints();
            } else if (event.getPropertyName().equals(WalkerConnection.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE)) {
                refreshVisuals();
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    protected ERDiagram getDiagram() {
        return ERModelUtil.getDiagram(getRoot().getContents());
    }

    protected Category getCurrentCategory() {
        return getDiagram().getCurrentCategory();
    }

    @Override
    public WalkerConnection getModel() {
        return (WalkerConnection) super.getModel();
    }

    @Override
    protected void refreshVisuals() {
        if (getModel().isDeleted()) {
            figure.setVisible(false);
            return;
        }

        if (getDiagram() != null) {
            final ERVirtualDiagram vdiagram = getDiagram().getCurrentVirtualDiagram();
            final EditPart sourceEditPart = getSource();
            final EditPart targetEditPart = getTarget();
            if (vdiagram != null) {
                if (sourceEditPart != null && vdiagram.contains(sourceEditPart.getModel())
                        && targetEditPart != null && vdiagram.contains(targetEditPart.getModel())) {
                    figure.setVisible(true);
                    return;
                } else {
                    figure.setVisible(false);
                    return;
                }
            }

            figure.setVisible(true);
        } else {
            figure.setVisible(false);
        }
    }

    abstract protected void refreshBendpoints();
}
