package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.tablespace;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.BaseRule;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.settings.Environment;

public abstract class TablespaceRule extends BaseRule {

    private final List<ValidateResult> errorList;

    private String database;

    public TablespaceRule() {
        this.errorList = new ArrayList<>();
    }

    @Override
    protected void addError(ValidateResult errorMessage) {
        this.errorList.add(errorMessage);
    }

    @Override
    public List<ValidateResult> getErrorList() {
        return errorList;
    }

    @Override
    public void clear() {
        errorList.clear();
    }

    @Override
    public boolean validate(ERDiagram diagram) {
        this.database = diagram.getDatabase();

        for (final Tablespace tablespace : diagram.getDiagramContents().getTablespaceSet().getTablespaceList()) {
            for (final Environment environment : diagram.getDiagramContents().getSettings().getEnvironmentSettings().getEnvironments()) {
                if (!validate(diagram, tablespace, environment)) {
                    return false;
                }
            }
        }

        return true;
    }

    protected DBManager getDBManager() {
        return DBManagerFactory.getDBManager(database);
    }

    abstract public boolean validate(ERDiagram diagram, Tablespace tablespace, Environment environment);
}
