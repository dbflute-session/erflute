package org.dbflute.erflute.editor.persistent.xml.reader;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadViewPropertiesLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadViewPropertiesLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                     View Properties
    //                                                                     ===============
    public void loadViewProperties(ViewProperties viewProperties, Element parent, LoadContext context) {
        final Element element = getElement(parent, "view_properties");
        if (element != null) {
            final String tablespaceId = getStringValue(element, "tablespace_id");
            final Tablespace tablespace = context.tablespaceMap.get(tablespaceId);
            viewProperties.setTableSpace(tablespace);
            viewProperties.setSchema(getStringValue(element, "schema"));
        }
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}
