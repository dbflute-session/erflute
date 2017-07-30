package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.AbstractCreateConnectionCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;

public abstract class AbstractCreateRelationshipCommand extends AbstractCreateConnectionCommand {

    @Override
    public String validate() {
        final ERTable sourceTable = (ERTable) getSourceModel();

        if (!sourceTable.isReferable()) {
            return DisplayMessages.getMessage("error.no.referenceable.column");
        }

        return null;
    }

    @Override
    public boolean canExecute() {
        if (!super.canExecute()) {
            return false;
        }

        if (!(getSourceModel() instanceof ERTable) || !(getTargetModel() instanceof TableView)) {
            return false;
        }

        return true;
    }
}
