package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
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
    public String buildComplexUniqueKeyList(List<CompoundUniqueKey> compoundUniqueKeyList, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<compound_unique_key_list>\n");
        for (final CompoundUniqueKey compoundUniqueKey : compoundUniqueKeyList) {
            xml.append(tab(doBuildCompoundUniqueKey(compoundUniqueKey, context)));
        }
        xml.append("</compound_unique_key_list>\n");
        return xml.toString();
    }

    private String doBuildCompoundUniqueKey(CompoundUniqueKey compoundUniqueKey, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<compound_unique_key>\n");
        // #for_erflute unneeded ID for unique key
        //xml.append("\t<id>").append(context.compoundUniqueKeyMap.get(compoundUniqueKey)).append("</id>\n");
        xml.append("\t<name>").append(Format.null2blank(compoundUniqueKey.getUniqueKeyName())).append("</name>\n");
        xml.append("\t<columns>\n");
        for (final NormalColumn column : compoundUniqueKey.getColumnList()) {
            xml.append("\t\t<column>\n");
            final String columnId = context.columnMap.get(column);
            xml.append("\t\t\t<column_id>").append(columnId).append("</column_id>\n"); // #for_erflute change id to column_id
            xml.append("\t\t</column>\n");
        }
        xml.append("\t</columns>\n");
        xml.append("</compound_unique_key>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String tab(String str) {
        return assistLogic.tab(str);
    }
}