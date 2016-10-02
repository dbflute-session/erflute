package org.insightech.er.editor.persistent.xml.writer;

import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.insightech.er.editor.persistent.xml.PersistentXml;
import org.insightech.er.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenIndexBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenIndexBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                             Indexes
    //                                                                             =======
    public String buildIndexes(List<ERIndex> indexes, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<indexes>\n");
        for (final ERIndex index : indexes) {
            xml.append(tab(doBuildIndex(index, context)));
        }
        xml.append("</indexes>\n");
        return xml.toString();
    }

    private String doBuildIndex(ERIndex index, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<inidex>\n"); // typo?
        xml.append("\t<full_text>").append(index.isFullText()).append("</full_text>\n");
        xml.append("\t<non_unique>").append(index.isNonUnique()).append("</non_unique>\n");
        xml.append("\t<name>").append(escape(index.getName())).append("</name>\n");
        xml.append("\t<type>").append(escape(index.getType())).append("</type>\n");
        xml.append("\t<description>").append(escape(index.getDescription())).append("</description>\n");
        xml.append("\t<columns>\n");
        final List<Boolean> descs = index.getDescs();
        int count = 0;
        for (final ERColumn column : index.getColumns()) {
            xml.append("\t\t<column>\n");
            xml.append("\t\t\t<id>").append(context.columnMap.get(column)).append("</id>\n");
            Boolean desc = Boolean.FALSE;
            if (descs.size() > count) {
                desc = descs.get(count);
            }
            xml.append("\t\t\t<desc>").append(desc).append("</desc>\n");
            xml.append("\t\t</column>\n");
            count++;
        }
        xml.append("\t</columns>\n");
        xml.append("</inidex>\n");
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