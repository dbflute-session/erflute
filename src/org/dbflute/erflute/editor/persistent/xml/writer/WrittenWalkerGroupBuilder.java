package org.dbflute.erflute.editor.persistent.xml.writer;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenWalkerGroupBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenDiagramWalkerBuilder walkerBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenWalkerGroupBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic, WrittenDiagramWalkerBuilder walkerBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.walkerBuilder = walkerBuilder;
    }

    // ===================================================================================
    //                                                                        Walker Group
    //                                                                        ============
    public String buildGroup(WalkerGroup group, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<walker_group>\n");
        xml.append(tab(walkerBuilder.buildWalker(group, context)));
        xml.append("\t<walker_group_name>").append(escape(group.getName())).append("</walker_group_name>\n");
        for (final DiagramWalker walker : group.getDiagramWalkerList()) {
            final String nodeId;
            if (walker instanceof ERVirtualTable) {
                nodeId = context.walkerMap.get(((ERVirtualTable) walker).getRawTable());
            } else {
                nodeId = context.walkerMap.get(walker);
            }
            xml.append("\t<diagram_walker>").append(nodeId).append("</diagram_walker>\n");
        }
        xml.append("</walker_group>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String tab(String str) {
        return assistLogic.tab(str);
    }

    private String escape(String s) {
        return assistLogic.escape(s);
    }
}