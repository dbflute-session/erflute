package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadWalkerGroupLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadDiagramWalkerLoader nodeElementLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadWalkerGroupLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadDiagramWalkerLoader nodeElementLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementLoader = nodeElementLoader;
    }

    // ===================================================================================
    //                                                                        Walker Group
    //                                                                        ============
    public WalkerGroup loadWalkerGroup(ERVirtualDiagram model, Element node, LoadContext context) {
        final WalkerGroup group = new WalkerGroup();
        nodeElementLoader.loadNodeElement(group, node, context);
        group.setName(getStringValue(node, "name"));
        final List<ERVirtualTable> vtables = model.getTables();
        final String[] keys = getTagValues(node, "node_element");
        final List<DiagramWalker> walkerList = new ArrayList<DiagramWalker>();
        for (final String key : keys) {
            final DiagramWalker walker = context.walkerMap.get(key);
            if (walker != null) {
                for (final ERVirtualTable vtable : vtables) {
                    if (vtable.getRawTable().equals(walker)) {
                        walkerList.add(vtable);
                        break;
                    }
                }
            }
        }
        group.setContents(walkerList);
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