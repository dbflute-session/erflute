package org.dbflute.erflute.editor.controller.editpolicy.not_element.tablespace;

import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.tablespace.DeleteTablespaceCommand;
import org.dbflute.erflute.editor.controller.editpolicy.not_element.NotElementComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.eclipse.gef.commands.Command;

public class TablespaceComponentEditPolicy extends NotElementComponentEditPolicy {

    @Override
    protected Command createDeleteCommand(ERDiagram diagram, Object model) {
        return new DeleteTablespaceCommand(diagram, (Tablespace) model);
    }
}
