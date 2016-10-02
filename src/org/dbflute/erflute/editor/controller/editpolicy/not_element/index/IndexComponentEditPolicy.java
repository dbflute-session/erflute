package org.dbflute.erflute.editor.controller.editpolicy.not_element.index;

import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.index.DeleteIndexCommand;
import org.dbflute.erflute.editor.controller.editpolicy.not_element.NotElementComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.eclipse.gef.commands.Command;

public class IndexComponentEditPolicy extends NotElementComponentEditPolicy {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createDeleteCommand(ERDiagram diagram, Object model) {
        return new DeleteIndexCommand(diagram, (ERIndex) model);
    }

}
