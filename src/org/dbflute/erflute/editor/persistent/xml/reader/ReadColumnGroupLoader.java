package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.List;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GroupSet;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadColumnGroupLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadColumnLoader columnLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadColumnGroupLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadColumnLoader columnLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.columnLoader = columnLoader;
    }

    // ===================================================================================
    //                                                                       Column Groups
    //                                                                       =============
    public void loadColumnGroups(GroupSet columnGroups, Element parent, LoadContext context, String database) {
        final Element element = this.getElement(parent, "column_groups");
        final NodeList nodeList = element.getElementsByTagName("column_group");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element columnGroupElement = (Element) nodeList.item(i);
            final ColumnGroup columnGroup = new ColumnGroup();
            columnGroup.setGroupName(getStringValue(columnGroupElement, "group_name"));
            final List<ERColumn> columns = columnLoader.loadColumns(columnGroupElement, context, database);
            for (final ERColumn column : columns) {
                columnGroup.addColumn((NormalColumn) column);
            }
            columnGroups.add(columnGroup);
            final String id = getStringValue(columnGroupElement, "id");
            context.columnGroupMap.put(id, columnGroup);
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