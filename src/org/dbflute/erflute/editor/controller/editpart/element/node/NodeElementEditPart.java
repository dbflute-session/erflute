package org.dbflute.erflute.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.AbstractModelEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.connection.ERDiagramConnectionEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.NodeElementGraphicalNodeEditPolicy;
import org.dbflute.erflute.editor.model.ViewableModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.figure.connection.ERDiagramConnection;
import org.dbflute.erflute.editor.view.figure.table.TableFigure;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public abstract class NodeElementEditPart extends AbstractModelEditPart implements NodeEditPart, DeleteableEditPart {

    private Font font;
    private Font largeFont;

    @Override
    public void deactivate() {
        this.disposeFont();
        super.deactivate();
    }

    protected void disposeFont() {
        if (this.font != null) {
            this.font.dispose();
        }
    }

    @Override
    public void doPropertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(NodeElement.PROPERTY_CHANGE_RECTANGLE)) {
            refreshVisuals();
        } else if (event.getPropertyName().equals(ViewableModel.PROPERTY_CHANGE_COLOR)) {
            refreshVisuals();
        } else if (event.getPropertyName().equals(ViewableModel.PROPERTY_CHANGE_FONT)) {
            this.changeFont(this.figure);
            refreshVisuals();
            //			if (getNodeModel().needsUpdateOtherModel()) {
            //				getNodeModel().getDiagram().refreshAllModel(event, getNodeModel());
            //			} else {
            //				refreshVisuals();
            //			}
            //
        } else if (event.getPropertyName().equals(NodeElement.PROPERTY_CHANGE_INCOMING)) {
            refreshTargetConnections();
        } else if (event.getPropertyName().equals(NodeElement.PROPERTY_CHANGE_OUTGOING)) {
            refreshSourceConnections();
        }
    }

    public NodeElement getNodeModel() {
        return (NodeElement) super.getModel();
    }

    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new NodeElementGraphicalNodeEditPolicy());
    }

    protected void setVisible() {
        final NodeElement element = (NodeElement) this.getModel();
        final Category category = this.getCurrentCategory();

        if (category != null) {
            this.figure.setVisible(category.isVisible(element, this.getDiagram()));

        } else {
            this.figure.setVisible(true);
        }
    }

    protected Font changeFont(IFigure figure) {
        this.disposeFont();

        final NodeElement nodeElement = (NodeElement) this.getModel();

        String fontName = nodeElement.getFontName();
        int fontSize = nodeElement.getFontSize();

        if (Check.isEmpty(fontName)) {
            final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
            fontName = fontData.getName();
            nodeElement.setFontName(fontName);
        }
        if (fontSize <= 0) {
            fontSize = ViewableModel.DEFAULT_FONT_SIZE;
            nodeElement.setFontSize(fontSize);
        }

        this.font = new Font(Display.getCurrent(), fontName, fontSize, SWT.NORMAL);

        if (getDiagram().getDiagramContents().getSettings().getTitleFontEm() != null) {
            final int largeFontSize =
                    getDiagram().getDiagramContents()
                            .getSettings()
                            .getTitleFontEm()
                            .multiply(new BigDecimal(nodeElement.getFontSize()))
                            .intValue();
            this.largeFont = new Font(Display.getCurrent(), fontName, largeFontSize, SWT.NORMAL);
        }

        figure.setFont(this.font);
        if (figure instanceof TableFigure) {
            ((TableFigure) figure).setLargeFont(this.largeFont);
        }

        return font;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshVisuals() {
        final NodeElement element = (NodeElement) this.getModel();
        this.setVisible();
        final Rectangle rectangle = this.getRectangle();
        final GraphicalEditPart parent = (GraphicalEditPart) this.getParent();
        final IFigure figure = this.getFigure();
        final int[] color = element.getColor();
        if (color != null) {
            final Color bgColor = DesignResources.getColor(color);
            figure.setBackgroundColor(bgColor);
        }
        parent.setLayoutConstraint(this, figure, rectangle);
    }

    public void refreshConnections() {
        for (final Object sourceConnection : this.getSourceConnections()) {
            final ConnectionEditPart editPart = (ConnectionEditPart) sourceConnection;
            final ConnectionElement connectinoElement = (ConnectionElement) editPart.getModel();
            connectinoElement.setParentMove();
        }
        for (final Object targetConnection : this.getTargetConnections()) {
            final ConnectionEditPart editPart = (ConnectionEditPart) targetConnection;
            final ConnectionElement connectinoElement = (ConnectionElement) editPart.getModel();

            connectinoElement.setParentMove();
        }
    }

    protected Rectangle getRectangle() {
        final NodeElement element = (NodeElement) this.getModel();

        final Point point = new Point(element.getX(), element.getY());

        final Dimension dimension = new Dimension(element.getWidth(), element.getHeight());

        final Dimension minimumSize = this.figure.getMinimumSize();

        if (dimension.width != -1 && dimension.width < minimumSize.width) {
            dimension.width = minimumSize.width;
        }
        if (dimension.height != -1 && dimension.height < minimumSize.height) {
            dimension.height = minimumSize.height;
        }

        return new Rectangle(point, dimension);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelSourceConnections() {
        final NodeElement element = (NodeElement) this.getModel();
        return element.getOutgoings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelTargetConnections() {
        final NodeElement element = (NodeElement) this.getModel();
        return element.getIncomings();
    }

    @Override
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart arg0) {
        return new ChopboxAnchor(this.getFigure());
    }

    @Override
    public ConnectionAnchor getSourceConnectionAnchor(Request arg0) {
        return new ChopboxAnchor(this.getFigure());
    }

    @Override
    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart arg0) {
        return new ChopboxAnchor(this.getFigure());
    }

    @Override
    public ConnectionAnchor getTargetConnectionAnchor(Request arg0) {
        return new ChopboxAnchor(this.getFigure());
    }

    public void changeSettings(Settings settings) {
        this.refresh();
        for (final Object object : this.getSourceConnections()) {
            final ERDiagramConnectionEditPart editPart = (ERDiagramConnectionEditPart) object;
            final ERDiagramConnection connection = (ERDiagramConnection) editPart.getFigure();
            connection.setBezier(settings.isUseBezierCurve());
            editPart.refresh();
        }
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    public void setSelected(int value) {
        if (value != 0 && getViewer() != null) {
            for (final Object editPartObject : this.getViewer().getSelectedEditParts()) {
                if (editPartObject instanceof ColumnEditPart) {
                    ((ColumnEditPart) editPartObject).setSelected(0);
                }
            }
        }
        super.setSelected(value);
    }

    @Override
    public void performRequest(Request request) {
        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            try {
                performRequestOpen();
            } catch (final Exception e) {
                Activator.showExceptionDialog(e);
            }
        }
        super.performRequest(request);
    }

    abstract protected void performRequestOpen();
}
