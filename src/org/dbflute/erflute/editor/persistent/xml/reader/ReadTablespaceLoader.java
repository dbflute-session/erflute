package org.dbflute.erflute.editor.persistent.xml.reader;

import org.dbflute.erflute.db.impl.db2.DB2DBManager;
import org.dbflute.erflute.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.dbflute.erflute.db.impl.mysql.MySQLDBManager;
import org.dbflute.erflute.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.dbflute.erflute.db.impl.oracle.OracleDBManager;
import org.dbflute.erflute.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.dbflute.erflute.db.impl.postgres.PostgresDBManager;
import org.dbflute.erflute.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadTablespaceLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadTablespaceLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                          Tablespace
    //                                                                          ==========
    public void loadTablespaceSet(TablespaceSet tablespaceSet, Element parent, LoadContext context, String database) {
        final Element element = getElement(parent, "tablespace_set");
        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("tablespace");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element tablespaceElemnt = (Element) nodeList.item(i);
                final Tablespace tablespace = loadTablespace(tablespaceElemnt, context, database);
                if (tablespace != null) {
                    tablespaceSet.addTablespace(tablespace);
                }
            }
        }
    }

    private Tablespace loadTablespace(Element element, LoadContext context, String database) {
        final Tablespace tablespace = new Tablespace();
        tablespace.setName(getStringValue(element, "name"));
        final String id = getStringValue(element, "id");
        final NodeList nodeList = element.getElementsByTagName("properties");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element propertiesElemnt = (Element) nodeList.item(i);
            final String environmentId = getStringValue(propertiesElemnt, "environment_id");
            final Environment environment = context.environmentMap.get(environmentId);
            TablespaceProperties tablespaceProperties = null;
            if (DB2DBManager.ID.equals(database)) {
                tablespaceProperties = loadTablespacePropertiesDB2(propertiesElemnt);
            } else if (MySQLDBManager.ID.equals(database)) {
                tablespaceProperties = loadTablespacePropertiesMySQL(propertiesElemnt);
            } else if (OracleDBManager.ID.equals(database)) {
                tablespaceProperties = loadTablespacePropertiesOracle(propertiesElemnt);
            } else if (PostgresDBManager.ID.equals(database)) {
                tablespaceProperties = loadTablespacePropertiesPostgres(propertiesElemnt);
            }
            tablespace.putProperties(environment, tablespaceProperties);
        }
        if (id != null) {
            context.tablespaceMap.put(id, tablespace);
        }
        return tablespace;
    }

    private TablespaceProperties loadTablespacePropertiesDB2(Element element) {
        final DB2TablespaceProperties properties = new DB2TablespaceProperties();
        properties.setBufferPoolName(getStringValue(element, "buffer_pool_name"));
        properties.setContainer(getStringValue(element, "container"));
        properties.setExtentSize(getStringValue(element, "extent_size"));
        properties.setManagedBy(getStringValue(element, "managed_by"));
        properties.setPageSize(getStringValue(element, "page_size"));
        properties.setPrefetchSize(getStringValue(element, "prefetch_size"));
        properties.setType(getStringValue(element, "type"));
        return properties;
    }

    private TablespaceProperties loadTablespacePropertiesMySQL(Element element) {
        final MySQLTablespaceProperties properties = new MySQLTablespaceProperties();
        properties.setDataFile(getStringValue(element, "data_file"));
        properties.setEngine(getStringValue(element, "engine"));
        properties.setExtentSize(getStringValue(element, "extent_size"));
        properties.setInitialSize(getStringValue(element, "initial_size"));
        properties.setLogFileGroup(getStringValue(element, "log_file_group"));
        return properties;
    }

    private TablespaceProperties loadTablespacePropertiesOracle(Element element) {
        final OracleTablespaceProperties properties = new OracleTablespaceProperties();
        properties.setAutoExtend(getBooleanValue(element, "auto_extend"));
        properties.setAutoExtendMaxSize(getStringValue(element, "auto_extend_max_size"));
        properties.setAutoExtendSize(getStringValue(element, "auto_extend_size"));
        properties.setAutoSegmentSpaceManagement(getBooleanValue(element, "auto_segment_space_management"));
        properties.setDataFile(getStringValue(element, "data_file"));
        properties.setFileSize(getStringValue(element, "file_size"));
        properties.setInitial(getStringValue(element, "initial"));
        properties.setLogging(getBooleanValue(element, "logging"));
        properties.setMaxExtents(getStringValue(element, "max_extents"));
        properties.setMinExtents(getStringValue(element, "min_extents"));
        properties.setMinimumExtentSize(getStringValue(element, "minimum_extent_size"));
        properties.setNext(getStringValue(element, "next"));
        properties.setOffline(getBooleanValue(element, "offline"));
        properties.setPctIncrease(getStringValue(element, "pct_increase"));
        properties.setTemporary(getBooleanValue(element, "temporary"));
        return properties;
    }

    private TablespaceProperties loadTablespacePropertiesPostgres(Element element) {
        final PostgresTablespaceProperties properties = new PostgresTablespaceProperties();
        properties.setLocation(getStringValue(element, "location"));
        properties.setOwner(getStringValue(element, "owner"));
        return properties;
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

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}
