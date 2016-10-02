package org.insightech.er.editor.persistent.xml.writer;

import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.persistent.xml.PersistentXml;
import org.insightech.er.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenDictionaryBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenDictionaryBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                          Dictionary
    //                                                                          ==========
    public String buildDictionary(Dictionary dictionary, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<dictionary>\n");
        for (final Word word : dictionary.getWordList()) {
            xml.append(tab(buildWord(word, context)));
        }
        xml.append("</dictionary>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                               Word
    //                                                                              ======
    private String buildWord(Word word, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<word>\n");
        if (context != null) {
            xml.append("\t<id>").append(context.wordMap.get(word)).append("</id>\n");
        }
        xml.append("\t<length>").append(word.getTypeData().getLength()).append("</length>\n");
        xml.append("\t<decimal>").append(word.getTypeData().getDecimal()).append("</decimal>\n");
        final Integer arrayDimension = word.getTypeData().getArrayDimension();
        xml.append("\t<array>").append(word.getTypeData().isArray()).append("</array>\n");
        xml.append("\t<array_dimension>").append(arrayDimension).append("</array_dimension>\n");
        xml.append("\t<unsigned>").append(word.getTypeData().isUnsigned()).append("</unsigned>\n");
        xml.append("\t<args>").append(escape(word.getTypeData().getArgs())).append("</args>\n");
        xml.append("\t<description>").append(escape(word.getDescription())).append("</description>\n");
        xml.append("\t<logical_name>").append(escape(word.getLogicalName())).append("</logical_name>\n");
        xml.append("\t<physical_name>").append(escape(word.getPhysicalName())).append("</physical_name>\n");
        String type = "";
        if (word.getType() != null) {
            type = word.getType().getId();
        }
        xml.append("\t<type>").append(type).append("</type>\n");
        xml.append("</word>\n");
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