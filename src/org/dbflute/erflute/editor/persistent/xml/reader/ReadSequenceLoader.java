package org.dbflute.erflute.editor.persistent.xml.reader;

import java.math.BigDecimal;

import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadSequenceLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadSequenceLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    public void loadSequenceSet(SequenceSet sequenceSet, Element parent) {
        final Element element = getElement(parent, "sequence_set");
        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("sequence");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element sequenceElemnt = (Element) nodeList.item(i);
                final Sequence sequence = this.loadSequence(sequenceElemnt);
                sequenceSet.addSequence(sequence);
            }
        }
    }

    public Sequence loadSequence(Element element) { // called by normal column
        final Sequence sequence = new Sequence();
        sequence.setName(getStringValue(element, "name"));
        sequence.setSchema(getStringValue(element, "schema"));
        sequence.setIncrement(getIntegerValue(element, "increment"));
        sequence.setMinValue(getLongValue(element, "min_value"));
        sequence.setMaxValue(getBigDecimalValue(element, "max_value"));
        sequence.setStart(getLongValue(element, "start"));
        sequence.setCache(getIntegerValue(element, "cache"));
        sequence.setCycle(getBooleanValue(element, "cycle"));
        sequence.setOrder(getBooleanValue(element, "order"));
        sequence.setDescription(getStringValue(element, "description"));
        sequence.setDataType(getStringValue(element, "data_type"));
        sequence.setDecimalSize(getIntValue(element, "decimal_size"));
        return sequence;
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }

    private boolean getBooleanValue(Element element, String tagname) {
        return assistLogic.getBooleanValue(element, tagname);
    }

    private int getIntValue(Element element, String tagname) {
        return assistLogic.getIntValue(element, tagname);
    }

    private Integer getIntegerValue(Element element, String tagname) {
        return assistLogic.getIntegerValue(element, tagname);
    }

    private Long getLongValue(Element element, String tagname) {
        return assistLogic.getLongValue(element, tagname);
    }

    private BigDecimal getBigDecimalValue(Element element, String tagname) {
        return assistLogic.getBigDecimalValue(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}
