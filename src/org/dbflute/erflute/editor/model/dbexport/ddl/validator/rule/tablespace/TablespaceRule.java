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

    private List<ValidateResult> errorList;

    private String database;

    public TablespaceRule() {
        this.errorList = new ArrayList<ValidateResult>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addError(ValidateResult errorMessage) {
        this.errorList.add(errorMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidateResult> getErrorList() {
        return this.errorList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        this.errorList.clear();
    }

    public boolean validate(ERDiagram diagram) {
        this.database = diagram.getDatabase();

        for (Tablespace tablespace : diagram.getDiagramContents().getTablespaceSet().getTablespaceList()) {
            for (Environment environment : diagram.getDiagramContents().getSettings().getEnvironmentSettings().getEnvironments()) {
                if (!this.validate(diagram, tablespace, environment)) {
                    return false;
                }
            }
        }

        return true;
    }

    protected DBManager getDBManager() {
        return DBManagerFactory.getDBManager(this.database);
    }

    abstract public boolean validate(ERDiagram diagram, Tablespace tablespace, Environment environment);
}
