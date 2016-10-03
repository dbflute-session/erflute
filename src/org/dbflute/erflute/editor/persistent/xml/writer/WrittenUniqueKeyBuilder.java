package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenUniqueKeyBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenUniqueKeyBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                  Complex Unique Key
    //                                                                  ==================
    public String buildComplexUniqueKeyList(List<ComplexUniqueKey> complexUniqueKeyList, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<complex_unique_key_list>\n");
        for (final ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
            xml.append(tab(doBuildComplexUniqueKey(complexUniqueKey, context)));
        }
        xml.append("</complex_unique_key_list>\n");
        return xml.toString();
    }

    private String doBuildComplexUniqueKey(ComplexUniqueKey complexUniqueKey, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<complex_unique_key>\n");
        xml.append("\t<id>").append(context.complexUniqueKeyMap.get(complexUniqueKey)).append("</id>\n");
        xml.append("\t<name>").append(Format.null2blank(complexUniqueKey.getUniqueKeyName())).append("</name>\n");
        xml.append("\t<columns>\n");
        for (final NormalColumn column : complexUniqueKey.getColumnList()) {
            xml.append("\t\t<column>\n");
            xml.append("\t\t\t<id>").append(context.columnMap.get(column)).append("</id>\n");
            xml.append("\t\t</column>\n");
        }
        xml.append("\t</columns>\n");
        xml.append("</complex_unique_key>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String tab(String str) {
        return assistLogic.tab(str);
    }
}