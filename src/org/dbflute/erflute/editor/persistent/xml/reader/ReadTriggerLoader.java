package org.dbflute.erflute.editor.persistent.xml.reader;

import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadTriggerLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadTriggerLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                             Trigger
    //                                                                             =======
    public void loadTriggerSet(TriggerSet triggerSet, Element parent) {
        final Element element = getElement(parent, "trigger_set");
        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("trigger");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element triggerElemnt = (Element) nodeList.item(i);
                final Trigger trigger = this.loadTrigger(triggerElemnt);
                triggerSet.addTrigger(trigger);
            }
        }
    }

    private Trigger loadTrigger(Element element) {
        final Trigger trigger = new Trigger();
        trigger.setName(getStringValue(element, "name"));
        trigger.setSchema(getStringValue(element, "schema"));
        trigger.setSql(getStringValue(element, "sql"));
        trigger.setDescription(getStringValue(element, "description"));
        return trigger;
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}