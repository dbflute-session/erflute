package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.sequence;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;

public class DeleteSequenceCommand extends AbstractCommand {

    private final SequenceSet sequenceSet;
    private final Sequence sequence;

    public DeleteSequenceCommand(ERDiagram diagram, Sequence sequence) {
        this.sequenceSet = diagram.getDiagramContents().getSequenceSet();
        this.sequence = sequence;
    }

    @Override
    protected void doExecute() {
        sequenceSet.remove(sequence);
    }

    @Override
    protected void doUndo() {
        sequenceSet.addSequence(sequence);
    }
}
