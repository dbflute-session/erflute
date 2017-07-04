package org.dbflute.erflute.editor.persistent.xml.reader;

import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadDatabaseLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadDatabaseLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                            Database
    //                                                                            ========
    public String loadDatabase(Element element) {
        final Element settings = extractDiagramSettingsElement(element);
        String database = getStringValue(settings, "database");
        if (database == null) { // basically no way
            database = DBManagerFactory.getAllDBList().get(0); // just in case
        }
        return database;
    }

    private Element extractDiagramSettingsElement(Element element) {
        Element settings = getElement(element, "settings"); // migration from ERMaster
        if (settings == null) {
            settings = getElement(element, "diagram_settings"); // #for_erflute
        }
        return settings;
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
