package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.view.impl;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.view.ViewRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.eclipse.core.resources.IMarker;

public class NoViewNameRule extends ViewRule {

    @Override
    public boolean validate(ERView view) {
        if (view.getPhysicalName() == null || view.getPhysicalName().trim().equals("")) {
            final ValidateResult validateResult = new ValidateResult();
            validateResult.setMessage(DisplayMessages.getMessage("error.validate.no.view.name"));
            validateResult.setLocation(view.getLogicalName());
            validateResult.setSeverity(IMarker.SEVERITY_WARNING);
            validateResult.setObject(view);

            addError(validateResult);
        }

        return true;
    }
}
