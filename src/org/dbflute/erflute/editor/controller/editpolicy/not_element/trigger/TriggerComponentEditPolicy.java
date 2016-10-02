package org.dbflute.erflute.editor.controller.editpolicy.not_element.trigger;

import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.trigger.DeleteTriggerCommand;
import org.dbflute.erflute.editor.controller.editpolicy.not_element.NotElementComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.eclipse.gef.commands.Command;

public class TriggerComponentEditPolicy extends NotElementComponentEditPolicy {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createDeleteCommand(ERDiagram diagram, Object model) {
        return new DeleteTriggerCommand(diagram, (Trigger) model);
    }

}
