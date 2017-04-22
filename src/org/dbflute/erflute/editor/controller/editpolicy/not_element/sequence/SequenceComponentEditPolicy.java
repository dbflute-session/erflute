package org.dbflute.erflute.editor.controller.editpolicy.not_element.sequence;

import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.sequence.DeleteSequenceCommand;
import org.dbflute.erflute.editor.controller.editpolicy.not_element.NotElementComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.eclipse.gef.commands.Command;

public class SequenceComponentEditPolicy extends NotElementComponentEditPolicy {

    @Override
    protected Command createDeleteCommand(ERDiagram diagram, Object model) {
        return new DeleteSequenceCommand(diagram, (Sequence) model);
    }
}
