package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule;

import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;

public interface Rule {

    List<ValidateResult> getErrorList();

    void clear();

    boolean validate(ERDiagram diagram);
}
