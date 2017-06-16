package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadIndexLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadIndexLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                               Index
    //                                                                               =====
    public List<ERIndex> loadIndexes(Element parent, ERTable table, LoadContext context) {
        final List<ERIndex> indexes = new ArrayList<>();
        final Element element = getElement(parent, "indexes");
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element indexElement = (Element) nodeList.item(i);
            String type = getStringValue(indexElement, "type");
            if ("null".equals(type)) {
                type = null;
            }
            final String name = getStringValue(indexElement, "name");
            final boolean nonUnique = getBooleanValue(indexElement, "non_unique");
            final String description = getStringValue(indexElement, "description");
            final ERIndex index = new ERIndex(table, name, nonUnique, type, description);
            index.setFullText(getBooleanValue(indexElement, "full_text"));
            this.loadIndexColumns(index, indexElement, context);
            indexes.add(index);
        }
        return indexes;
    }

    private void loadIndexColumns(ERIndex index, Element parent, LoadContext context) {
        final Element element = getElement(parent, "columns");
        final NodeList nodeList = element.getChildNodes();
        final List<Boolean> descs = new ArrayList<>();
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
            final Boolean desc = new Boolean(getBooleanValue(columnElement, "desc"));
            index.addColumn(column);
            descs.add(desc);
        }
        index.setDescs(descs);
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
