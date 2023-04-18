package org.dbflute.erflute.db.impl.oracleidentity;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.impl.oracle.OracleDDLCreator;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class OracleIdentityDDLCreator extends OracleDDLCreator {

    public OracleIdentityDDLCreator(ERDiagram diagram, boolean semicolon) {
        super(diagram, semicolon);
    }

    // ===================================================================================
    //                                                                        Create Table
    //                                                                        ============:
    @Override
    protected String buildColumnPart(NormalColumn normalColumn) {
        final StringBuilder ddl = new StringBuilder();
        final String description = normalColumn.getDescription();
        if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineColumnComment) {
            ddl.append("\t-- ");
            ddl.append(description.replaceAll("\n", "\n\t-- "));
            ddl.append(LN);
        }
        ddl.append("\t");
        ddl.append(filter(normalColumn.getPhysicalName()));
        ddl.append(" ");
        ddl.append(filter(Format.formatType(normalColumn.getType(), normalColumn.getTypeData(), getDiagram().getDatabase())));
        if (!Check.isEmpty(normalColumn.getDefaultValue())) {
            String defaultValue = normalColumn.getDefaultValue();
            if (DisplayMessages.getMessage("label.current.date.time").equals(defaultValue)) {
                defaultValue = getDBManager().getCurrentTimeValue()[0];
            }
            ddl.append(" DEFAULT ");
            if (doesNeedQuoteDefaultValue(normalColumn)) {
                ddl.append("'");
                ddl.append(Format.escapeSQL(defaultValue));
                ddl.append("'");
            } else {
                ddl.append(defaultValue);
            }
        }
        // additional Oracle Identity Columns
        if (normalColumn.isAutoIncrement()) {
            ddl.append(" GENERATED BY DEFAULT AS IDENTITY");
        }
        if (normalColumn.isNotNull()) {
            ddl.append(" NOT NULL");
        }
        if (normalColumn.isUniqueKey()) {
            if (!Check.isEmpty(normalColumn.getUniqueKeyName())) {
                // #thinking jflute all right? (e.g. bad in MySQL) (2020/05/16)
                // all right. See https://docs.oracle.com/cd/E82638_01/sqlrf/constraint.html#GUID-1055EA97-BA6F-4764-A15F-1024FD5B6DFE
                ddl.append(" CONSTRAINT ");
                ddl.append(normalColumn.getUniqueKeyName());
            }
            ddl.append(" UNIQUE");
        }
        final String constraint = Format.null2blank(normalColumn.getConstraint());
        if (!"".equals(constraint)) {
            ddl.append(" ");
            ddl.append(constraint);
        }
        return ddl.toString();
    }
}
