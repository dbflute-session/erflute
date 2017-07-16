package org.dbflute.erflute.editor.controller.command.common.notation;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties.ModelProperties;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ChangeStampCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final boolean oldStamp;
    private final boolean newStamp;
    private final ModelProperties modelProperties;

    public ChangeStampCommand(ERDiagram diagram, boolean isDisplay) {
        this.diagram = diagram;
        this.modelProperties = diagram.getDiagramContents().getSettings().getModelProperties();
        this.newStamp = isDisplay;
        this.oldStamp = modelProperties.isDisplay();
    }

    @Override
    protected void doExecute() {
        modelProperties.setDisplay(newStamp);
        diagram.changeAll();
    }

    @Override
    protected void doUndo() {
        modelProperties.setDisplay(oldStamp);
        diagram.changeAll();
    }
}
