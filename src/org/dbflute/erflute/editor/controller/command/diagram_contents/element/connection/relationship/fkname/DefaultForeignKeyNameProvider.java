package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.fkname;

import org.dbflute.erflute.db.impl.mysql.MySQLDBManager;
import org.dbflute.erflute.db.impl.oracle.OracleDBManager;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;

/**
 * @author jflute
 */
public class DefaultForeignKeyNameProvider {

    public String provide(TableView sourceTable, TableView targetTable) {
        // MySQL only for now for quick implementation (2014/10/23)
        final String standardName = buildDefaultForeingKeyName(sourceTable, targetTable);
        final int limitLength; // where do I define it?
        if (isDatabaseMySQL(targetTable)) {
            limitLength = 64;
        } else if (isDatabaseOracle(targetTable)) {
            limitLength = 30;
        } else {
            limitLength = 120;
        }
        final String defaultName = cutName(standardName, limitLength);
        return defaultName;
        // not use because of duplicate check at new relationship by jflute
        //final List<Relationship> relationshipList = targetTable.getIncomingRelationshipList();
        //int count = 0;
        //for (final Relationship relationship : relationshipList) {
        //    final String foreignKeyName = relationship.getForeignKeyName();
        //    if (defaultName.equalsIgnoreCase(foreignKeyName)) {
        //        ++count;
        //    }
        //}
        //final String suffix = count > 0 ? "_" + (count + 1) : "";
        //if (defaultName.length() + suffix.length() > limitLength) {
        //    return defaultName.substring(0, defaultName.length() - suffix.length()) + suffix;
        //} else {
        //    return defaultName + suffix;
        //}
    }

    private boolean isDatabaseMySQL(TableView targetTable) {
        return MySQLDBManager.ID.equals(targetTable.getDiagram().getDatabase()); // for now
    }

    private boolean isDatabaseOracle(TableView targetTable) {
        return OracleDBManager.ID.equals(targetTable.getDiagram().getDatabase()); // for now
    }

    private String buildDefaultForeingKeyName(TableView sourceTable, TableView targetTable) {
        // cannot get FK column yet here so table name only
        return "FK_" + targetTable.getPhysicalName() + "_" + sourceTable.getPhysicalName();
    }

    private String cutName(final String name, int limitLength) {
        return name.length() > limitLength ? name.substring(0, limitLength) : name;
    }
}
