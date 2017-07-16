package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.sequence;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;

public class EditSequenceCommand extends AbstractCommand {

    private final SequenceSet sequenceSet;
    private final Sequence oldSequence;
    private final Sequence newSequence;

    public EditSequenceCommand(ERDiagram diagram, Sequence oldSequence, Sequence newSequence) {
        this.sequenceSet = diagram.getDiagramContents().getSequenceSet();
        this.oldSequence = oldSequence;
        this.newSequence = newSequence;
    }

    @Override
    protected void doExecute() {
        sequenceSet.remove(oldSequence);
        sequenceSet.addSequence(newSequence);
    }

    @Override
    protected void doUndo() {
        sequenceSet.remove(newSequence);
        sequenceSet.addSequence(oldSequence);
    }
}
