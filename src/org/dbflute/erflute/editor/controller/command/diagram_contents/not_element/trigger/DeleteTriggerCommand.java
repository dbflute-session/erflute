package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.trigger;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public class DeleteTriggerCommand extends AbstractCommand {

    private TriggerSet triggerSet;

    private Trigger trigger;

    public DeleteTriggerCommand(ERDiagram diagram, Trigger trigger) {
        this.triggerSet = diagram.getDiagramContents().getTriggerSet();
        this.trigger = trigger;
    }

    @Override
    protected void doExecute() {
        this.triggerSet.remove(this.trigger);
    }

    @Override
    protected void doUndo() {
        this.triggerSet.addTrigger(this.trigger);
    }
}
