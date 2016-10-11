package org.dbflute.erflute.editor.controller.editpart.element.connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class ERDiagramConnectionEditPart extends AbstractConnectionEditPart implements PropertyChangeListener {

    private static final Logger logger = Logger.getLogger(ERDiagramConnectionEditPart.class.getName());
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

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        try {
            if (DEBUG) {
                logger.log(Level.INFO, this.getClass().getName() + ":" + event.getPropertyName() + ":" + event.toString());
            }
            if (event.getPropertyName().equals(WalkerConnection.PROPERTY_CHANGE_BEND_POINT)) {
                this.refreshBendpoints();
            } else if (event.getPropertyName().equals(WalkerConnection.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE)) {
                this.refreshVisuals();
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    protected ERDiagram getDiagram() {
        return ERModelUtil.getDiagram(this.getRoot().getContents());
    }

    protected Category getCurrentCategory() {
        return this.getDiagram().getCurrentCategory();
    }

    @Override
    protected void refreshVisuals() {
        final ERDiagram diagram = this.getDiagram();
        if (diagram != null) {
            this.figure.setVisible(false);
            //			Category category = this.getCurrentCategory();
            //
            //			if (category != null) {
            //				CategorySetting categorySettings = this.getDiagram()
            //						.getDiagramContents().getSettings()
            //						.getCategorySetting();
            //				if (sourceEditPart != null && targetEditPart != null) {
            //					NodeElement sourceModel = (NodeElement) sourceEditPart
            //							.getModel();
            //					NodeElement targetModel = (NodeElement) targetEditPart
            //							.getModel();
            //					boolean containsSource = false;
            //					if (category.contains(sourceModel)) {
            //						containsSource = true;
            //					} else if (categorySettings.isShowReferredTables()) {
            //						for (NodeElement referringElement : sourceModel
            //								.getReferringElementList()) {
            //							if (category.contains(referringElement)) {
            //								containsSource = true;
            //								break;
            //							}
            //						}
            //					}
            //					if (containsSource) {
            //						if (category.contains(targetModel)) {
            //							this.figure.setVisible(true);
            //						} else if (categorySettings.isShowReferredTables()) {
            //							for (NodeElement referringElement : targetModel
            //									.getReferringElementList()) {
            //								if (category.contains(referringElement)) {
            //									this.figure.setVisible(true);
            //									break;
            //								}
            //							}
            //						}
            //					}
            //				}
            //			} else {
            this.figure.setVisible(true);
            //			}
        }
    }

    abstract protected void refreshBendpoints();
}
