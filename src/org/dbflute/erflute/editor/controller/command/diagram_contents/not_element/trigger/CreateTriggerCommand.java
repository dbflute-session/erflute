package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.trigger;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public class CreateTriggerCommand extends AbstractCommand {

    private final TriggerSet triggerSet;
    private final Trigger trigger;

    public CreateTriggerCommand(ERDiagram diagram, Trigger trigger) {
        this.triggerSet = diagram.getDiagramContents().getTriggerSet();
        this.trigger = trigger;
    }

    @Override
    protected void doExecute() {
        triggerSet.addTrigger(trigger);
    }

    @Override
    protected void doUndo() {
        triggerSet.remove(trigger);
    }
}
