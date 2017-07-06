package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.impl;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.eclipse.core.resources.IMarker;

public class FullTextIndexRule extends TableRule {

    @Override
    public boolean validate(ERTable table) {
        for (final ERIndex index : table.getIndexes()) {
            if (index.isFullText()) {
                for (final NormalColumn indexColumn : index.getColumns()) {
                    if (!indexColumn.isFullTextIndexable()) {
                        final ValidateResult validateResult = new ValidateResult();
                        validateResult.setMessage(DisplayMessages.getMessage("error.validate.fulltext.index1")
                                + table.getPhysicalName() + DisplayMessages.getMessage("error.validate.fulltext.index2")
                                + index.getName() + DisplayMessages.getMessage("error.validate.fulltext.index3")
                                + indexColumn.getPhysicalName());
                        validateResult.setLocation(table.getLogicalName());
                        validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                        validateResult.setObject(index);

                        addError(validateResult);
                    }
                }
            }
        }

        return true;
    }
}
