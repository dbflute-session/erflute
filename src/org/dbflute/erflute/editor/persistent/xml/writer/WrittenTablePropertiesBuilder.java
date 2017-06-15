package org.dbflute.erflute.editor.persistent.xml.writer;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.db.impl.mysql.MySQLTableProperties;
import org.dbflute.erflute.db.impl.postgres.PostgresTableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenTablePropertiesBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenTablePropertiesBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                    Table Properties
    //                                                                    ================
    public String buildTableProperties(TableProperties tableProperties, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<table_properties>\n");
        setupTablespace(tableProperties, context, xml);
        setupSchema(tableProperties, xml);
        if (tableProperties instanceof MySQLTableProperties) {
            xml.append(tab(doBuildMySQLTableProperties((MySQLTableProperties) tableProperties)));
        } else if (tableProperties instanceof PostgresTableProperties) {
            xml.append(tab(doBuildPostgresTableProperties((PostgresTableProperties) tableProperties)));
        }
        xml.append("</table_properties>\n");
        return xml.toString();
    }

    private void setupTablespace(TableProperties tableProperties, PersistentContext context, final StringBuilder xml) {
        final Integer tablespaceId = context.tablespaceMap.get(tableProperties.getTableSpace());
        if (tablespaceId != null) {
            xml.append("\t<tablespace_id>").append(tablespaceId).append("</tablespace_id>\n");
        }
    }

    private void setupSchema(TableProperties tableProperties, final StringBuilder xml) {
        // not write if empty or false to slim XML
        final String schema = tableProperties.getSchema();
        if (Srl.is_NotNull_and_NotEmpty(schema)) {
            xml.append("\t<schema>").append(escape(schema)).append("</schema>\n");
        }
    }

    private String doBuildMySQLTableProperties(MySQLTableProperties tableProperties) {
        // not write if empty or false to slim XML
        final StringBuilder xml = new StringBuilder();
        final String characterSet = tableProperties.getCharacterSet();
        if (Srl.is_NotNull_and_NotEmpty(characterSet)) {
            xml.append("<character_set>").append(escape(characterSet)).append("</character_set>\n");
        }
        final String collation = tableProperties.getCollation();
        if (Srl.is_NotNull_and_NotEmpty(collation)) {
            xml.append("<collation>").append(escape(collation)).append("</collation>\n");
        }
        final String storageEngine = tableProperties.getStorageEngine();
        if (Srl.is_NotNull_and_NotEmpty(storageEngine)) {
            xml.append("<storage_engine>").append(escape(storageEngine)).append("</storage_engine>\n");
        }
        final Integer primaryKeyLengthOfText = tableProperties.getPrimaryKeyLengthOfText();
        if (primaryKeyLengthOfText != null) {
            xml.append("<primary_key_length_of_text>").append(primaryKeyLengthOfText).append("</primary_key_length_of_text>\n");
        }
        return xml.toString();
    }

    private String doBuildPostgresTableProperties(PostgresTableProperties tableProperties) {
        // not write if empty or false to slim XML
        final StringBuilder xml = new StringBuilder();
        final boolean withoutOIDs = tableProperties.isWithoutOIDs();
        if (withoutOIDs) {
            xml.append("<without_oids>").append(withoutOIDs).append("</without_oids>\n");
        }
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String tab(String str) {
        return assistLogic.tab(str);
    }

    private String escape(String s) {
        return assistLogic.escape(s);
    }
}
