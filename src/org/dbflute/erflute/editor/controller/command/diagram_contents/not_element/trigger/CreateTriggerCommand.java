package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.trigger;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public class CreateTriggerCommand extends AbstractCommand {

    private TriggerSet triggerSet;

    private Trigger trigger;

    public CreateTriggerCommand(ERDiagram diagram, Trigger trigger) {
        this.triggerSet = diagram.getDiagramContents().getTriggerSet();
        this.trigger = trigger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.triggerSet.addTrigger(this.trigger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.triggerSet.remove(this.trigger);
    }
}
