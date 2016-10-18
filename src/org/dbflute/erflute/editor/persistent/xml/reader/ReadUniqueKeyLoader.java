package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
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
    public List<CompoundUniqueKey> loadComplexUniqueKeyList(Element parent, ERTable table, LoadContext context) {
        final List<CompoundUniqueKey> compoundUniqueKeyList = new ArrayList<CompoundUniqueKey>();
        Element element = getElement(parent, "complex_unique_key_list"); // migration from ERMaster
        if (element == null || element.getChildNodes().getLength() == 0) {
            element = getElement(parent, "compound_unique_key_list"); // #for_erflute rename
            if (element == null || element.getChildNodes().getLength() == 0) {
                return compoundUniqueKeyList;
            }
        }
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element compoundUniqueKeyElement = (Element) nodeList.item(i);
            final String name = getStringValue(compoundUniqueKeyElement, "name");
            final CompoundUniqueKey compoundUniqueKey = new CompoundUniqueKey(name);
            loadComplexUniqueKeyColumns(compoundUniqueKey, compoundUniqueKeyElement, context);
            compoundUniqueKeyList.add(compoundUniqueKey);
            String id = getStringValue(compoundUniqueKeyElement, "id"); // migration from ERMaster
            if (Srl.is_Null_or_TrimmedEmpty(id)) {
                id = compoundUniqueKey.buildUniqueKeyId(table); // #for_erflute
            }
            context.compoundUniqueKeyMap.put(id, compoundUniqueKey);
        }
        return compoundUniqueKeyList;
    }

    private void loadComplexUniqueKeyColumns(CompoundUniqueKey compoundUniqueKey, Element parent, LoadContext context) {
        final Element element = getElement(parent, "columns");
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element columnElement = (Element) nodeList.item(i);
            String id = getStringValue(columnElement, "id"); // migration from ERMaster
            if (Srl.is_Null_or_TrimmedEmpty(id)) {
                id = getStringValue(columnElement, "column_id"); // #for_erflute
            }
            final NormalColumn column = context.columnMap.get(id);
            if (column == null) {
                final String msg = "Not found the column for compound unique key: column=" + id + ", uniqueKey=" + compoundUniqueKey;
                throw new PersistentXmlReadingFailureException(msg);
            }
            compoundUniqueKey.addColumn(column);
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