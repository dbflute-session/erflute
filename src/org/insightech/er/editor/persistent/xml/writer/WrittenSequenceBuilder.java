package org.insightech.er.editor.persistent.xml.writer;

import org.dbflute.erflute.core.util.Format;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.persistent.xml.PersistentXml;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenSequenceBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenSequenceBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                        Sequence Set
    //                                                                        ============
    public String buildSequenceSet(SequenceSet sequenceSet) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<sequence_set>\n");
        for (final Sequence sequence : sequenceSet) {
            xml.append(tab(buildSequence(sequence)));
        }
        xml.append("</sequence_set>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    public String buildSequence(Sequence sequence) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<sequence>\n");
        xml.append("\t<name>").append(escape(sequence.getName())).append("</name>\n");
        xml.append("\t<schema>").append(escape(sequence.getSchema())).append("</schema>\n");
        xml.append("\t<increment>").append(Format.toString(sequence.getIncrement())).append("</increment>\n");
        xml.append("\t<min_value>").append(Format.toString(sequence.getMinValue())).append("</min_value>\n");
        xml.append("\t<max_value>").append(Format.toString(sequence.getMaxValue())).append("</max_value>\n");
        xml.append("\t<start>").append(Format.toString(sequence.getStart())).append("</start>\n");
        xml.append("\t<cache>").append(Format.toString(sequence.getCache())).append("</cache>\n");
        xml.append("\t<cycle>").append(sequence.isCycle()).append("</cycle>\n");
        xml.append("\t<order>").append(sequence.isOrder()).append("</order>\n");
        xml.append("\t<description>").append(escape(sequence.getDescription())).append("</description>\n");
        xml.append("\t<data_type>").append(escape(sequence.getDataType())).append("</data_type>\n");
        xml.append("\t<decimal_size>").append(Format.toString(sequence.getDecimalSize())).append("</decimal_size>\n");
        xml.append("</sequence>\n");
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