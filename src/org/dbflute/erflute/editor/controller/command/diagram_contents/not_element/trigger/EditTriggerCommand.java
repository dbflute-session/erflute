package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.trigger;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public class EditTriggerCommand extends AbstractCommand {

    private final TriggerSet triggerSet;
    private final Trigger oldTrigger;
    private final Trigger newTrigger;

    public EditTriggerCommand(ERDiagram diagram, Trigger oldTrigger, Trigger newTrigger) {
        this.triggerSet = diagram.getDiagramContents().getTriggerSet();
        this.oldTrigger = oldTrigger;
        this.newTrigger = newTrigger;
    }

    @Override
    protected void doExecute() {
        triggerSet.remove(oldTrigger);
        triggerSet.addTrigger(newTrigger);
    }

    @Override
    protected void doUndo() {
        triggerSet.remove(newTrigger);
        triggerSet.addTrigger(oldTrigger);
    }
}
