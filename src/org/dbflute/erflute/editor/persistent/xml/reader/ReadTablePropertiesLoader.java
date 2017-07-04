package org.dbflute.erflute.editor.persistent.xml.reader;

import org.dbflute.erflute.db.impl.mysql.MySQLTableProperties;
import org.dbflute.erflute.db.impl.postgres.PostgresTableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadTablePropertiesLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadTablePropertiesLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                    Table Properties
    //                                                                    ================
    public void loadTableProperties(TableProperties tableProperties, Element parent, LoadContext context) {
        final Element element = getElement(parent, "table_properties");
        final String tablespaceId = getStringValue(element, "tablespace_id");
        final Tablespace tablespace = context.tablespaceMap.get(tablespaceId);
        tableProperties.setTableSpace(tablespace);
        tableProperties.setSchema(getStringValue(element, "schema"));
        if (tableProperties instanceof MySQLTableProperties) {
            this.loadTablePropertiesMySQL((MySQLTableProperties) tableProperties, element);
        } else if (tableProperties instanceof PostgresTableProperties) {
            this.loadTablePropertiesPostgres((PostgresTableProperties) tableProperties, element);
        }
    }

    private void loadTablePropertiesMySQL(MySQLTableProperties tableProperties, Element element) {
        tableProperties.setCharacterSet(getStringValue(element, "character_set"));
        tableProperties.setCollation(getStringValue(element, "collation"));
        tableProperties.setStorageEngine(getStringValue(element, "storage_engine"));
        tableProperties.setPrimaryKeyLengthOfText(getIntegerValue(element, "primary_key_length_of_text"));
    }

    private void loadTablePropertiesPostgres(PostgresTableProperties tableProperties, Element element) {
        tableProperties.setWithoutOIDs(getBooleanValue(element, "without_oids"));
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }

    private boolean getBooleanValue(Element element, String tagname) {
        return assistLogic.getBooleanValue(element, tagname);
    }

    private Integer getIntegerValue(Element element, String tagname) {
        return assistLogic.getIntegerValue(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}
