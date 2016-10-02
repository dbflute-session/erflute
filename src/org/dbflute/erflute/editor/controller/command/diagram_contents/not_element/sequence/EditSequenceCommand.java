package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.sequence;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;

public class EditSequenceCommand extends AbstractCommand {

    private SequenceSet sequenceSet;

    private Sequence oldSequence;

    private Sequence newSequence;

    public EditSequenceCommand(ERDiagram diagram, Sequence oldSequence, Sequence newSequence) {
        this.sequenceSet = diagram.getDiagramContents().getSequenceSet();
        this.oldSequence = oldSequence;
        this.newSequence = newSequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.sequenceSet.remove(this.oldSequence);
        this.sequenceSet.addSequence(this.newSequence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        this.sequenceSet.remove(this.newSequence);
        this.sequenceSet.addSequence(this.oldSequence);
    }
}
