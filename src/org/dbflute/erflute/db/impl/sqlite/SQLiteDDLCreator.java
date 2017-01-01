package org.dbflute.erflute.db.impl.sqlite;

import java.util.LinkedHashSet;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class SQLiteDDLCreator extends DDLCreator {

    public SQLiteDDLCreator(ERDiagram diagram, boolean semicolon) {
        super(diagram, semicolon);
    }

    @Override
    protected String doBuildCreateTablespace(Tablespace tablespace) {
        return null;
    }

    @Override
    protected String buildColumnPart(NormalColumn normalColumn) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append(super.buildColumnPart(normalColumn));

        if (normalColumn.isAutoIncrement()) {
            ddl.append(" PRIMARY KEY AUTOINCREMENT");
        }

        return ddl.toString();
    }

    @Override
    protected String buildPrimaryKeyPart(ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        for (final Relationship relation : table.getIncomingRelationshipList()) {
            ddl.append(",\r\n\tFOREIGN KEY (");

            boolean first = true;

            for (final NormalColumn column : relation.getForeignKeyColumns()) {
                if (!first) {
                    ddl.append(", ");

                }
                ddl.append(filter(column.getPhysicalName()));
                first = false;
            }

            ddl.append(")\r\n");
            ddl.append("\tREFERENCES ");
            ddl.append(filter(relation.getSourceTableView().getNameWithSchema(this.getDiagram().getDatabase())));
            ddl.append(" (");

            first = true;

            for (final NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
                if (!first) {
                    ddl.append(", ");

                }

                ddl.append(filter(foreignKeyColumn.getReferredColumn(relation).getPhysicalName()));
                first = false;
            }

            ddl.append(")");
        }

        return ddl.toString();
    }

    @Override
    protected Iterable<ERTable> getTablesForCreateDDL() {
        final LinkedHashSet<ERTable> results = new LinkedHashSet<ERTable>();

        for (final ERTable table : this.getDiagram().getDiagramContents().getDiagramWalkers().getTableSet()) {
            if (!results.contains(table)) {
                this.getReferedTables(results, table);
                results.add(table);
            }
        }

        return results;
    }

    private void getReferedTables(LinkedHashSet<ERTable> referedTables, ERTable table) {
        for (final DiagramWalker walker : table.getReferedElementList()) {
            if (walker instanceof ERTable) {
                if (walker != table) {
                    final ERTable referedTable = (ERTable) walker;
                    if (!referedTables.contains(referedTable)) {
                        this.getReferedTables(referedTables, referedTable);
                        referedTables.add(referedTable);
                    }
                }
            }
        }
    }

    @Override
    protected String buildCreateForeignKeys(ERDiagram diagram) {
        return "";
    }

}
