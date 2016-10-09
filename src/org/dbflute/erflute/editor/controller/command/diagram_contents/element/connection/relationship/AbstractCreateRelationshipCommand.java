package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.AbstractCreateConnectionCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;

public abstract class AbstractCreateRelationshipCommand extends AbstractCreateConnectionCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    public String validate() {
        ERTable sourceTable = (ERTable) this.getSourceModel();

        if (!sourceTable.isReferable()) {
            return DisplayMessages.getMessage("error.no.referenceable.column");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        if (!super.canExecute()) {
            return false;
        }

        if (!(this.getSourceModel() instanceof ERTable) || !(this.getTargetModel() instanceof TableView)) {
            return false;
        }

        return true;
    }

}
