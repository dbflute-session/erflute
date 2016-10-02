package org.insightech.er.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadGroupLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadNodeElementLoader nodeElementLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadGroupLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadNodeElementLoader nodeElementLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementLoader = nodeElementLoader;
    }

    // ===================================================================================
    //                                                                               Group
    //                                                                               =====
    public VGroup loadGroup(ERModel model, Element node, LoadContext context) {
        final VGroup group = new VGroup();
        nodeElementLoader.loadNodeElement(group, node, context);
        group.setName(getStringValue(node, "name"));
        final List<ERVirtualTable> vtables = model.getTables();
        final String[] keys = getTagValues(node, "node_element");
        final List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        for (final String key : keys) {
            final NodeElement nodeElement = context.nodeElementMap.get(key);
            if (nodeElement != null) {
                for (final ERVirtualTable vtable : vtables) {
                    if (vtable.getRawTable().equals(nodeElement)) {
                        nodeElementList.add(vtable);
                        break;
                    }
                }
            }
        }
        group.setContents(nodeElementList);
        return group;
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }

    private String[] getTagValues(Element element, String tagname) {
        return assistLogic.getTagValues(element, tagname);
    }
}