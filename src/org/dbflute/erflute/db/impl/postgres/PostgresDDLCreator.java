package org.dbflute.erflute.db.impl.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;

public class PostgresDDLCreator extends DDLCreator {

    private static final Pattern DROP_TRIGGER_TABLE_PATTERN = Pattern.compile(".*\\s[oO][nN]\\s+(.+)\\s.*");

    public PostgresDDLCreator(ERDiagram diagram, boolean semicolon) {
        super(diagram, semicolon);
    }

    @Override
    public String buildTableOptionPart(ERTable table) {
        final PostgresTableProperties commonTableProperties =
                (PostgresTableProperties) getDiagram().getDiagramContents().getSettings().getTableViewProperties();

        final PostgresTableProperties tableProperties = (PostgresTableProperties) table.getTableViewProperties();

        boolean isWithoutOIDs = tableProperties.isWithoutOIDs();
        if (!isWithoutOIDs) {
            isWithoutOIDs = commonTableProperties.isWithoutOIDs();
        }

        final StringBuilder postDDL = new StringBuilder();

        if (isWithoutOIDs) {
            postDDL.append(" WITHOUT OIDS");
        }

        postDDL.append(super.buildTableOptionPart(table));

        return postDDL.toString();
    }

    @Override
    public List<String> doBuildCreateComment(ERTable table) {
        final List<String> ddlList = new ArrayList<>();

        final String tableComment = filterComment(table.getLogicalName(), table.getDescription(), false);

        if (!Check.isEmpty(tableComment)) {
            final StringBuilder ddl = new StringBuilder();

            ddl.append("COMMENT ON TABLE ");
            ddl.append(filter(table.getNameWithSchema(getDiagram().getDatabase())));
            ddl.append(" IS '");
            ddl.append(tableComment.replaceAll("'", "''"));
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

                    ddl.append("COMMENT ON COLUMN ");
                    ddl.append(filter(table.getNameWithSchema(getDiagram().getDatabase())));
                    ddl.append(".");
                    ddl.append(filter(normalColumn.getPhysicalName()));
                    ddl.append(" IS '");
                    ddl.append(comment.replaceAll("'", "''"));
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

                        ddl.append("COMMENT ON COLUMN ");
                        ddl.append(filter(table.getNameWithSchema(getDiagram().getDatabase())));
                        ddl.append(".");
                        ddl.append(filter(normalColumn.getPhysicalName()));
                        ddl.append(" IS '");
                        ddl.append(comment.replaceAll("'", "''"));
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

    @Override
    protected String doBuildCreateTablespace(Tablespace tablespace) {
        final PostgresTablespaceProperties tablespaceProperties =
                (PostgresTablespaceProperties) tablespace.getProperties(environment, getDiagram());

        final StringBuilder ddl = new StringBuilder();

        ddl.append("CREATE TABLESPACE ");
        ddl.append(filter(tablespace.getName()));
        ddl.append("\r\n");

        if (!Check.isEmpty(tablespaceProperties.getOwner())) {
            ddl.append(" OWNER ");
            ddl.append(tablespaceProperties.getOwner());
            ddl.append("\r\n");
        }

        ddl.append(" LOCATION '");
        ddl.append(tablespaceProperties.getLocation());
        ddl.append("'\r\n");

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    private String getAutoIncrementSettingDDL(ERTable table, NormalColumn column) {
        final StringBuilder ddl = new StringBuilder();

        final Sequence sequence = column.getAutoIncrementSetting();

        if (sequence.getIncrement() != null || sequence.getMinValue() != null || sequence.getMaxValue() != null
                || sequence.getStart() != null || sequence.getCache() != null || sequence.isCycle()) {
            ddl.append("ALTER SEQUENCE ");
            ddl.append(filter(table.getNameWithSchema(getDiagram().getDatabase()) + "_" + column.getPhysicalName() + "_SEQ"));

            if (sequence.getIncrement() != null) {
                ddl.append(" INCREMENT ");
                ddl.append(sequence.getIncrement());
            }
            if (sequence.getMinValue() != null) {
                ddl.append(" MINVALUE ");
                ddl.append(sequence.getMinValue());
            }
            if (sequence.getMaxValue() != null) {
                ddl.append(" MAXVALUE ");
                ddl.append(sequence.getMaxValue());
            }
            if (sequence.getStart() != null) {
                ddl.append(" START ");
                ddl.append(sequence.getStart());
            }
            if (sequence.getCache() != null) {
                ddl.append(" CACHE ");
                ddl.append(sequence.getCache());
            }
            if (sequence.isCycle()) {
                ddl.append(" CYCLE");
            }
            if (semicolon) {
                ddl.append(";");
            }
        }

        return ddl.toString();
    }

    @Override
    protected String getTableSettingDDL(ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final NormalColumn column : table.getNormalColumns()) {
            if (SqlType.SQL_TYPE_ID_SERIAL.equals(column.getType().getId())
                    || SqlType.SQL_TYPE_ID_BIG_SERIAL.equals(column.getType().getId())) {
                final String autoIncrementSettingDDL = getAutoIncrementSettingDDL(table, column);
                if (!Check.isEmpty(autoIncrementSettingDDL)) {
                    ddl.append(autoIncrementSettingDDL);
                    ddl.append("\r\n");
                    first = false;
                }
            }
        }

        if (!first) {
            ddl.append("\r\n");
            ddl.append("\r\n");
        }

        return ddl.toString();
    }

    @Override
    public String doBuildDropTrigger(Trigger trigger) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("DROP TRIGGER ");
        ddl.append(getIfExistsOption());
        ddl.append(filter(trigger.getName()));
        ddl.append(" ON ");

        final Matcher matcher = DROP_TRIGGER_TABLE_PATTERN.matcher(trigger.getSql());
        if (matcher.find()) {
            ddl.append(matcher.group(1));
        }

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    @Override
    public String getIfExistsOption() {
        return "IF EXISTS ";
    }
}
