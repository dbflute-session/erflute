package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.List;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

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
        xml.append("<index>\n");
        xml.append("\t<name>").append(escape(index.getName())).append("</name>\n");
        xml.append("\t<type>").append(escape(index.getType())).append("</type>\n");
        final String description = index.getDescription();
        if (Srl.is_NotNull_and_NotEmpty(description)) {
            xml.append("\t<description>").append(escape(description)).append("</description>\n");
        }
        final boolean fullText = index.isFullText();
        if (fullText) {
            xml.append("\t<full_text>").append(fullText).append("</full_text>\n");
        }
        final boolean nonUnique = index.isNonUnique();
        if (nonUnique) {
            xml.append("\t<non_unique>").append(nonUnique).append("</non_unique>\n");
        }
        xml.append("\t<columns>\n");
        final List<Boolean> descs = index.getDescs();
        int count = 0;
        for (final ERColumn column : index.getColumns()) {
            xml.append("\t\t<column>\n");
            final String columnId = context.columnMap.get(column);
            xml.append("\t\t\t<id>").append(columnId).append("</id>\n");
            Boolean desc = Boolean.FALSE;
            if (descs.size() > count) {
                desc = descs.get(count);
            }
            if (desc) {
                xml.append("\t\t\t<desc>").append(desc).append("</desc>\n");
            }
            xml.append("\t\t</column>\n");
            count++;
        }
        xml.append("\t</columns>\n");
        xml.append("</index>\n");
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