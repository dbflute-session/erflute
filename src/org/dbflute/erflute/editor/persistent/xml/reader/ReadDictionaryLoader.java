package org.dbflute.erflute.editor.persistent.xml.reader;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadDictionaryLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadDictionaryLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                          Dictionary
    //                                                                          ==========
    public void loadDictionary(Dictionary dictionary, Element parent, LoadContext context, String database) {
        // needs to migrate from ERMaster-b
        final Element element = this.getElement(parent, "dictionary");
        if (element != null) { // always false after ERFlute
            final NodeList nodeList = element.getElementsByTagName("word");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element wordElement = (Element) nodeList.item(i);
                this.loadWord(wordElement, context, database);
            }
        }
    }

    private Word loadWord(Element element, LoadContext context, String database) {
        final String id = getStringValue(element, "id");
        final String type = getStringValue(element, "type");
        final Integer length = getIntegerValue(element, "length");
        final Integer decimal = getIntegerValue(element, "decimal");
        final boolean array = getBooleanValue(element, "array");
        final Integer arrayDimension = getIntegerValue(element, "array_dimension");
        final boolean unsigned = getBooleanValue(element, "unsigned");
        final String args = getStringValue(element, "args");
        final boolean charSemantics = getBooleanValue(element, "charSemantics");
        final TypeData typeData = new TypeData(length, decimal, array, arrayDimension, unsigned, args, charSemantics);
        final String physicalName = Format.null2blank(getStringValue(element, "physical_name"));
        final String logicalName = Format.null2blank(getStringValue(element, "logical_name"));
        final String description = Format.null2blank(this.getStringValue(element, "description"));
        final SqlType sqlType = SqlType.valueOfId(type);
        final Word word = new Word(physicalName, logicalName, sqlType, typeData, description, database);
        context.wordMap.put(id, word);
        return word;
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