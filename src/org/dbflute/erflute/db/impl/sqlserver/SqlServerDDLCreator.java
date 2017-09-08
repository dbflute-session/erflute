package org.dbflute.erflute.db.impl.sqlserver;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.db.impl.sqlserver.tablespace.SqlServerTablespaceProperties;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class SqlServerDDLCreator extends DDLCreator {

    public SqlServerDDLCreator(ERDiagram diagram, boolean semicolon) {
        super(diagram, semicolon);
    }

    @Override
    protected String buildColumnPart(NormalColumn normalColumn) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append(super.buildColumnPart(normalColumn));

        if (normalColumn.isAutoIncrement()) {
            ddl.append(" IDENTITY ");

            final Sequence sequence = normalColumn.getAutoIncrementSetting();

            if (sequence.getIncrement() != null || sequence.getStart() != null) {
                ddl.append("(");
                if (sequence.getStart() != null) {
                    ddl.append(sequence.getStart());
                } else {
                    ddl.append("1");
                }

                if (sequence.getIncrement() != null) {
                    ddl.append(", ");
                    ddl.append(sequence.getIncrement());
                }

                ddl.append(")");
            }
        }

        return ddl.toString();
    }

    @Override
    protected String doBuildCreateTablespace(Tablespace tablespace) {
        final SqlServerTablespaceProperties tablespaceProperties =
                (SqlServerTablespaceProperties) tablespace.getProperties(environment, getDiagram());

        final StringBuilder ddl = new StringBuilder();

        ddl.append("CREATE ");
        if (!Check.isEmpty(tablespaceProperties.getType())) {
            ddl.append(tablespaceProperties.getType());
            ddl.append(" ");
        }

        ddl.append("TABLESPACE ");
        ddl.append(filter(tablespace.getName()));
        ddl.append("\r\n");

        if (!Check.isEmpty(tablespaceProperties.getPageSize())) {
            ddl.append(" PAGESIZE ");
            ddl.append(tablespaceProperties.getPageSize());
            ddl.append("\r\n");
        }

        ddl.append(" MANAGED BY ");
        ddl.append(tablespaceProperties.getManagedBy());
        ddl.append(" USING(");
        ddl.append(tablespaceProperties.getContainer());
        ddl.append(")\r\n");

        if (!Check.isEmpty(tablespaceProperties.getExtentSize())) {
            ddl.append(" EXTENTSIZE ");
            ddl.append(tablespaceProperties.getExtentSize());
            ddl.append("\r\n");
        }

        if (!Check.isEmpty(tablespaceProperties.getPrefetchSize())) {
            ddl.append(" PREFETCHSIZE ");
            ddl.append(tablespaceProperties.getPrefetchSize());
            ddl.append("\r\n");
        }

        if (!Check.isEmpty(tablespaceProperties.getBufferPoolName())) {
            ddl.append(" BUFFERPOOL ");
            ddl.append(tablespaceProperties.getBufferPoolName());
            ddl.append("\r\n");
        }

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    @Override
    protected List<String> doBuildCreateComment(ERTable table) {
        final List<String> ddlList = new ArrayList<>();

        final String tableComment = filterComment(table.getLogicalName(), table.getDescription(), false);

        if (!Check.isEmpty(tableComment)) {
            final StringBuilder ddl = new StringBuilder();

            ddl.append("EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'");
            ddl.append(tableComment.replaceAll("'", "''"));
            ddl.append("', @level0type=N'schema', @level0name=N'");
            ddl.append(table.getSchema());
            ddl.append("', @level1type=N'table', @level1name=N'");
            ddl.append(filter(table.getPhysicalName()));
            ddl.append("'");
            if (semicolon) {
                ddl.append(";");
            }

            ddlList.add(ddl.toString());
        }

        for (final ERColumn column : table.getColumns()) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                final String comment = filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);

                if (!Check.isEmpty(comment)) {
                    final StringBuilder ddl = new StringBuilder();

                    ddl.append("EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'");
                    ddl.append(comment.replaceAll("'", "''"));
                    ddl.append("', @level0type=N'schema', @level0name=N'");
                    ddl.append(table.getSchema());
                    ddl.append("', @level1type=N'table', @level1name=N'");
                    ddl.append(filter(table.getPhysicalName()));
                    ddl.append("', @level2type=N'column' , @level2name=N'");
                    ddl.append(filter(normalColumn.getPhysicalName()));
                    ddl.append("'");
                    if (semicolon) {
                        ddl.append(";");
                    }

                    ddlList.add(ddl.toString());
                }
            } else {
                final ColumnGroup columnGroup = (ColumnGroup) column;

                for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                    final String comment = filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);

                    if (!Check.isEmpty(comment)) {
                        final StringBuilder ddl = new StringBuilder();

                        ddl.append("EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'");
                        ddl.append(comment.replaceAll("'", "''"));
                        ddl.append("', @level0type=N'schema', @level0name=N'");
                        ddl.append(table.getSchema());
                        ddl.append("', @level1type=N'table', @level1name=N'");
                        ddl.append(filter(table.getPhysicalName()));
                        ddl.append("', @level2type=N'column' , @level2name=N'");
                        ddl.append(filter(normalColumn.getPhysicalName()));
                        ddl.append("'");
                        if (semicolon) {
                            ddl.append(";");
                        }

                        ddlList.add(ddl.toString());
                    }
                }
            }
        }

        return ddlList;
    }
}
