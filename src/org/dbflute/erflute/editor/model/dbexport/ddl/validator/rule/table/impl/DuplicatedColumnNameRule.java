package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.impl;

import java.util.HashSet;
import java.util.Set;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.eclipse.core.resources.IMarker;

public class DuplicatedColumnNameRule extends TableRule {

    @Override
    public boolean validate(ERTable table) {
        final Set<String> columnNameSet = new HashSet<>();

        for (final ERColumn column : table.getColumns()) {
            if (column instanceof ColumnGroup) {
                final ColumnGroup columnGroup = (ColumnGroup) column;

                for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                    final String columnName = Format.null2blank(normalColumn.getPhysicalName()).toLowerCase();

                    if (columnNameSet.contains(columnName)) {
                        final ValidateResult result = new ValidateResult();
                        result.setMessage(DisplayMessages.getMessage("error.validate.duplicated.column.name1")
                                + table.getPhysicalName() + DisplayMessages.getMessage("error.validate.duplicated.column.name2")
                                + columnName);
                        result.setLocation(table.getLogicalName());
                        result.setSeverity(IMarker.SEVERITY_WARNING);
                        result.setObject(table);

                        addError(result);
                    }

                    columnNameSet.add(columnName);
                }
            } else if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                final String columnName = normalColumn.getPhysicalName();

                if (columnNameSet.contains(columnName)) {
                    final ValidateResult result = new ValidateResult();
                    result.setMessage(DisplayMessages.getMessage("error.validate.duplicated.column.name1") + table.getPhysicalName()
                            + DisplayMessages.getMessage("error.validate.duplicated.column.name2") + columnName);
                    result.setLocation(table.getLogicalName());
                    result.setSeverity(IMarker.SEVERITY_WARNING);
                    result.setObject(table);

                    addError(result);
                }

                columnNameSet.add(columnName);
            }
        }

        return true;
    }
}
