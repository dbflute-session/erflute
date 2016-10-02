package org.insightech.er.editor.persistent.xml.writer;

import org.insightech.er.db.impl.mysql.MySQLTableProperties;
import org.insightech.er.db.impl.postgres.PostgresTableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.persistent.xml.PersistentXml;
import org.insightech.er.editor.persistent.xml.PersistentXml.PersistentContext;

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
        final Integer tablespaceId = context.tablespaceMap.get(tableProperties.getTableSpace());
        if (tablespaceId != null) {
            xml.append("\t<tablespace_id>").append(tablespaceId).append("</tablespace_id>\n");
        }
        xml.append("\t<schema>").append(escape(tableProperties.getSchema())).append("</schema>\n");
        if (tableProperties instanceof MySQLTableProperties) {
            xml.append(tab(doBuildMySQLTableProperties((MySQLTableProperties) tableProperties)));
        } else if (tableProperties instanceof PostgresTableProperties) {
            xml.append(tab(doBuildPostgresTableProperties((PostgresTableProperties) tableProperties)));
        }
        xml.append("</table_properties>\n");
        return xml.toString();
    }

    private String doBuildMySQLTableProperties(MySQLTableProperties tableProperties) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<character_set>").append(escape(tableProperties.getCharacterSet())).append("</character_set>\n");
        xml.append("<collation>").append(escape(tableProperties.getCollation())).append("</collation>\n");
        xml.append("<storage_engine>").append(escape(tableProperties.getStorageEngine())).append("</storage_engine>\n");
        xml.append("<primary_key_length_of_text>")
                .append(tableProperties.getPrimaryKeyLengthOfText())
                .append("</primary_key_length_of_text>\n");
        return xml.toString();
    }

    private String doBuildPostgresTableProperties(PostgresTableProperties tableProperties) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<without_oids>").append(tableProperties.isWithoutOIDs()).append("</without_oids>\n");
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