package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.view;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.BaseRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;

public abstract class ViewRule extends BaseRule {

    private final List<ValidateResult> errorList;

    private String database;

    public ViewRule() {
        this.errorList = new ArrayList<>();
    }

    @Override
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

    @Override
    public boolean validate(ERDiagram diagram) {
        this.database = diagram.getDatabase();

        for (final ERView view : diagram.getDiagramContents().getDiagramWalkers().getViewSet()) {
            if (!validate(view)) {
                return false;
            }
        }

        return true;
    }

    protected DBManager getDBManager() {
        return DBManagerFactory.getDBManager(database);
    }

    abstract public boolean validate(ERView view);
}
