package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.tablespace.impl;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.tablespace.TablespaceRule;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.eclipse.core.resources.IMarker;

public class UninputTablespaceRule extends TablespaceRule {

    @Override
    public boolean validate(ERDiagram diagram, Tablespace tablespace, Environment environment) {
        final TablespaceProperties tablespaceProperties = tablespace.getProperties(environment, diagram);

        for (final String errorMessage : tablespaceProperties.validate()) {
            final ValidateResult validateResult = new ValidateResult();
            validateResult.setMessage(DisplayMessages.getMessage(errorMessage) + getMessageSuffix(tablespace, environment));
            validateResult.setLocation(tablespace.getName());
            validateResult.setSeverity(IMarker.SEVERITY_WARNING);
            validateResult.setObject(tablespace);

            addError(validateResult);
        }

        return true;
    }

    protected String getMessageSuffix(Tablespace tablespace, Environment environment) {
        final StringBuilder suffix = new StringBuilder();
        suffix.append(" ");
        suffix.append(DisplayMessages.getMessage("error.tablespace.suffix.1"));
        suffix.append(tablespace.getName());
        suffix.append(DisplayMessages.getMessage("error.tablespace.suffix.2"));
        suffix.append(environment.getName());

        return suffix.toString();
    }
}
