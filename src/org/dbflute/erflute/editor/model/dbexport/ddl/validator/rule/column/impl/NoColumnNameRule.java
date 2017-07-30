package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.column.impl;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.column.ColumnRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.eclipse.core.resources.IMarker;

public class NoColumnNameRule extends ColumnRule {

    @Override
    public boolean validate(ERTable table, NormalColumn column) {
        if (column.getPhysicalName() == null || column.getPhysicalName().trim().equals("")) {
            final ValidateResult validateResult = new ValidateResult();
            validateResult.setMessage(DisplayMessages.getMessage("error.validate.no.column.name") + table.getPhysicalName());
            validateResult.setLocation(table.getLogicalName());
            validateResult.setSeverity(IMarker.SEVERITY_WARNING);
            validateResult.setObject(table);

            addError(validateResult);
        }

        return true;
    }
}
