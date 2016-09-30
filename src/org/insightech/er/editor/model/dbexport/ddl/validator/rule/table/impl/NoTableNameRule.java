package org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.DisplayMessages;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class NoTableNameRule extends TableRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(ERTable table) {
        if (table.getPhysicalName() == null || table.getPhysicalName().trim().equals("")) {
            ValidateResult validateResult = new ValidateResult();
            validateResult.setMessage(DisplayMessages.getMessage("error.validate.no.table.name"));
            validateResult.setLocation(table.getLogicalName());
            validateResult.setSeverity(IMarker.SEVERITY_WARNING);
            validateResult.setObject(table);

            this.addError(validateResult);
        }

        return true;
    }
}
