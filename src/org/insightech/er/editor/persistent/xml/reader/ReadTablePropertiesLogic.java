package org.insightech.er.editor.persistent.xml.reader;

import java.math.BigDecimal;
import java.util.Date;

import org.insightech.er.db.impl.mysql.MySQLTableProperties;
import org.insightech.er.db.impl.postgres.PostgresTableProperties;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadTablePropertiesLogic {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadTablePropertiesLogic(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                    Table Properties
    //                                                                    ================
    public void loadTableProperties(TableProperties tableProperties, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "table_properties");
        final String tablespaceId = this.getStringValue(element, "tablespace_id");
        final Tablespace tablespace = context.tablespaceMap.get(tablespaceId);
        tableProperties.setTableSpace(tablespace);
        tableProperties.setSchema(this.getStringValue(element, "schema"));
        if (tableProperties instanceof MySQLTableProperties) {
            this.loadTablePropertiesMySQL((MySQLTableProperties) tableProperties, element);
        } else if (tableProperties instanceof PostgresTableProperties) {
            this.loadTablePropertiesPostgres((PostgresTableProperties) tableProperties, element);
        }
    }

    private void loadTablePropertiesMySQL(MySQLTableProperties tableProperties, Element element) {
        tableProperties.setCharacterSet(this.getStringValue(element, "character_set"));
        tableProperties.setCollation(this.getStringValue(element, "collation"));
        tableProperties.setStorageEngine(this.getStringValue(element, "storage_engine"));
        tableProperties.setPrimaryKeyLengthOfText(this.getIntegerValue(element, "primary_key_length_of_text"));
    }

    private void loadTablePropertiesPostgres(PostgresTableProperties tableProperties, Element element) {
        tableProperties.setWithoutOIDs(this.getBooleanValue(element, "without_oids"));
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

    private boolean getBooleanValue(Element element, String tagname, boolean defaultValue) {
        return assistLogic.getBooleanValue(element, tagname, defaultValue);
    }

    private int getIntValue(Element element, String tagname) {
        return assistLogic.getIntValue(element, tagname);
    }

    private int getIntValue(Element element, String tagname, int defaultValue) {
        return assistLogic.getIntValue(element, tagname, defaultValue);
    }

    private Integer getIntegerValue(Element element, String tagname) {
        return assistLogic.getIntegerValue(element, tagname);
    }

    private Long getLongValue(Element element, String tagname) {
        return assistLogic.getLongValue(element, tagname);
    }

    private BigDecimal getBigDecimalValue(Element element, String tagname) {
        return assistLogic.getBigDecimalValue(element, tagname);
    }

    private double getDoubleValue(Element element, String tagname) {
        return assistLogic.getDoubleValue(element, tagname);
    }

    private Date getDateValue(Element element, String tagname) {
        return assistLogic.getDateValue(element, tagname);
    }

    private String[] getTagValues(Element element, String tagname) {
        return assistLogic.getTagValues(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }

    private void loadColor(ViewableModel model, Element element) {
        assistLogic.loadColor(model, element);
    }

    private void loadDefaultColor(ERDiagram diagram, Element element) {
        assistLogic.loadDefaultColor(diagram, element);
    }

    private void loadFont(ViewableModel viewableModel, Element element) {
        assistLogic.loadFont(viewableModel, element);
    }
}