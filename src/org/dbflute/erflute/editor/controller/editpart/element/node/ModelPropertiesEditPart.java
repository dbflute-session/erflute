package org.dbflute.erflute.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.editor.controller.command.common.ChangeModelPropertiesCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.dialog.element.ModelPropertiesDialog;
import org.dbflute.erflute.editor.view.figure.ModelPropertiesFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class ModelPropertiesEditPart extends NodeElementEditPart implements IResizable {

    public ModelPropertiesEditPart() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        final ERDiagram diagram = this.getDiagram();
        final Settings settings = diagram.getDiagramContents().getSettings();

        final ModelPropertiesFigure figure = new ModelPropertiesFigure();

        this.changeFont(figure);

        figure.setVisible(settings.getModelProperties().isDisplay());

        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doPropertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(ModelProperties.PROPERTY_CHANGE_MODEL_PROPERTIES)) {
            refreshVisuals();
        }

        super.doPropertyChange(event);
    }

    @Override
    public void refreshVisuals() {
        final ERDiagram diagram = this.getDiagram();
        final ModelProperties modelProperties = (ModelProperties) this.getModel();
        final ModelPropertiesFigure figure = (ModelPropertiesFigure) this.getFigure();
        figure.setData(modelProperties.getProperties(), diagram.getDiagramContents().getSettings().getTableStyle(),
                modelProperties.getColor());
        super.refreshVisuals();
    }

    @Override
    public void changeSettings(Settings settings) {
        this.figure.setVisible(settings.getModelProperties().isDisplay());
        super.changeSettings(settings);
    }

    @Override
    protected void setVisible() {
        final ERDiagram diagram = this.getDiagram();
        final Settings settings = diagram.getDiagramContents().getSettings();
        this.figure.setVisible(settings.getModelProperties().isDisplay());
    }

    @Override
    public void performRequestOpen() {
        final ERDiagram diagram = this.getDiagram();
        final ModelProperties copyModelProperties = diagram.getDiagramContents().getSettings().getModelProperties().clone();
        final ModelPropertiesDialog dialog =
                new ModelPropertiesDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), copyModelProperties);
        if (dialog.open() == IDialogConstants.OK_ID) {
            final ChangeModelPropertiesCommand command = new ChangeModelPropertiesCommand(diagram, copyModelProperties);
            this.executeCommand(command);
        }
    }

    @Override
    public boolean isDeleteable() {
        return false;
    }
}
