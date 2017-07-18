package org.dbflute.erflute.editor.controller.command.common;

import java.util.List;

import org.dbflute.erflute.core.util.NameValue;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties.ModelProperties;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ChangeModelPropertiesCommand extends AbstractCommand {

    private final List<NameValue> oldProperties;
    private final List<NameValue> newProperties;
    private final ModelProperties modelProperties;

    public ChangeModelPropertiesCommand(ERDiagram diagram, ModelProperties properties) {
        this.modelProperties = diagram.getDiagramContents().getSettings().getModelProperties();
        this.oldProperties = modelProperties.getProperties();
        this.newProperties = properties.getProperties();
    }

    @Override
    protected void doExecute() {
        modelProperties.setProperties(newProperties);

    }

    @Override
    protected void doUndo() {
        modelProperties.setProperties(oldProperties);
    }
}
