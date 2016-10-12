package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
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
    public WalkerGroup loadGroup(Element node, LoadContext context, WalkerGroupedTableViewProvider tablesProvider) {
        final WalkerGroup group = new WalkerGroup();
        nodeElementLoader.loadWalker(group, node, context);
        String groupName = getStringValue(node, "name"); // migration from ERMaster
        if (Srl.is_Null_or_Empty(groupName)) {
            groupName = getStringValue(node, "walker_group_name"); // #for_erflute
        }
        group.setName(groupName);
        String[] keys = getTagValues(node, "node_element"); // migration from ERMaster
        if (keys == null || keys.length == 0) {
            keys = getTagValues(node, "diagram_walker"); // #for_erflute
        }
        final List<? extends TableView> tableList = tablesProvider.provide();
        final List<DiagramWalker> walkerList = new ArrayList<DiagramWalker>();
        for (final String key : keys) {
            final DiagramWalker walker = context.walkerMap.get(key);
            if (walker != null) {
                for (final TableView table : tableList) {
                    if (table.equals(walker)) {
                        walkerList.add(table);
                        break;
                    }
                }
            }
        }
        group.setWalkers(walkerList);
        return group;
    }

    public static interface WalkerGroupedTableViewProvider {

        List<? extends TableView> provide();
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