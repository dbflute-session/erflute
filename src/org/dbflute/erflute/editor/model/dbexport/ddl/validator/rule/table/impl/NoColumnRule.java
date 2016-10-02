package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.impl;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.core.resources.IMarker;

public class NoColumnRule extends TableRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(ERTable table) {
        if (table.getColumns().isEmpty()) {
            ValidateResult validateResult = new ValidateResult();
            validateResult.setMessage(DisplayMessages.getMessage("error.validate.no.column") + table.getPhysicalName());
            validateResult.setLocation(table.getLogicalName());
            validateResult.setSeverity(IMarker.SEVERITY_WARNING);
            validateResult.setObject(table);

            this.addError(validateResult);
        }

        return true;
    }
}
