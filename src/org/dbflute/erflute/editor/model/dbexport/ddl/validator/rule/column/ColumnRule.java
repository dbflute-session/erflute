package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.column;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;

public abstract class ColumnRule extends TableRule {

    private final List<ValidateResult> errorList;

    public ColumnRule() {
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
    public boolean validate(ERTable table) {
        for (final ERColumn column : table.getColumns()) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (!validate(table, normalColumn)) {
                    return false;
                }
            } else {
                final ColumnGroup columnGroup = (ColumnGroup) column;

                for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                    if (!validate(table, normalColumn)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    abstract public boolean validate(ERTable table, NormalColumn column);
}
