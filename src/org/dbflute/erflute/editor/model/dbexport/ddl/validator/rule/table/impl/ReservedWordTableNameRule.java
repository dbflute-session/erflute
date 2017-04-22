package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.impl;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.core.resources.IMarker;

public class ReservedWordTableNameRule extends TableRule {

    @Override
    public boolean validate(ERTable table) {
        if (table.getPhysicalName() != null) {
            if (this.getDBManager().isReservedWord(table.getPhysicalName())) {
                ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.reserved.table.name") + table.getPhysicalName());
                validateResult.setLocation(table.getLogicalName());
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(table);

                this.addError(validateResult);
            }
        }

        return true;
    }
}
