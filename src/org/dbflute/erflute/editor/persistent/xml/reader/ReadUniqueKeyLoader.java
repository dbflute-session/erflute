package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.reader.exception.PersistentXmlReadingFailureException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadUniqueKeyLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadUniqueKeyLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                  Complex Unique Key
    //                                                                  ==================
    public List<ComplexUniqueKey> loadComplexUniqueKeyList(Element parent, ERTable table, LoadContext context) {
        final List<ComplexUniqueKey> complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();
        final Element element = getElement(parent, "complex_unique_key_list");
        if (element == null) {
            return complexUniqueKeyList;
        }
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element complexUniqueKeyElement = (Element) nodeList.item(i);
            final String id = getStringValue(complexUniqueKeyElement, "id");
            final String name = getStringValue(complexUniqueKeyElement, "name");
            final ComplexUniqueKey complexUniqueKey = new ComplexUniqueKey(name);
            loadComplexUniqueKeyColumns(complexUniqueKey, complexUniqueKeyElement, context);
            complexUniqueKeyList.add(complexUniqueKey);
            context.complexUniqueKeyMap.put(id, complexUniqueKey);
        }
        return complexUniqueKeyList;
    }

    private void loadComplexUniqueKeyColumns(ComplexUniqueKey complexUniqueKey, Element parent, LoadContext context) {
        final Element element = getElement(parent, "columns");
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element columnElement = (Element) nodeList.item(i);
            final String id = getStringValue(columnElement, "id");
            final NormalColumn column = context.columnMap.get(id);
            if (column == null) {
                final String msg = "Not found the column for complex unique key: column=" + id + ", uniqueKey=" + complexUniqueKey;
                throw new PersistentXmlReadingFailureException(msg);
            }
            complexUniqueKey.addColumn(column);
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