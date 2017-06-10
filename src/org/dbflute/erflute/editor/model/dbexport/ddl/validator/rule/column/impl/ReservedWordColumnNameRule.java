package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.column.impl;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.column.ColumnRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.eclipse.core.resources.IMarker;

public class ReservedWordColumnNameRule extends ColumnRule {

    @Override
    public boolean validate(ERTable table, NormalColumn column) {
        if (column.getPhysicalName() != null) {
            if (this.getDBManager().isReservedWord(column.getPhysicalName())) {
                ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.reserved.column.name1")
                        + table.getPhysicalName() + DisplayMessages.getMessage("error.validate.reserved.column.name2")
                        + column.getPhysicalName());
                validateResult.setLocation(table.getLogicalName());
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(table);

                this.addError(validateResult);
            }
        }

        return true;
    }
}
