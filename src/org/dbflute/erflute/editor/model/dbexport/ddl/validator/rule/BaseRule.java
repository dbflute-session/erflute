package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.Validator;

public abstract class BaseRule implements Rule {

    private final List<ValidateResult> errorList;

    public BaseRule() {
        this.errorList = new ArrayList<>();
        Validator.addRule(this);
    }

    protected void addError(ValidateResult errorMessage) {
        errorList.add(errorMessage);
    }

    @Override
    public List<ValidateResult> getErrorList() {
        return errorList;
    }

    @Override
    public void clear() {
        errorList.clear();
    }
}
