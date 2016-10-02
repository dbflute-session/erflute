package org.insightech.er.editor.persistent.xml.writer;

import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.persistent.xml.PersistentXml;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenTriggerBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenTriggerBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                             Trigger
    //                                                                             =======
    public String buildTrigger(TriggerSet triggerSet) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<trigger_set>\n");
        for (final Trigger trigger : triggerSet) {
            xml.append(tab(doBuildTrigger(trigger)));
        }
        xml.append("</trigger_set>\n");
        return xml.toString();
    }

    private String doBuildTrigger(Trigger trigger) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<trigger>\n");
        xml.append("\t<name>").append(escape(trigger.getName())).append("</name>\n");
        xml.append("\t<schema>").append(escape(trigger.getSchema())).append("</schema>\n");
        xml.append("\t<sql>").append(escape(trigger.getSql())).append("</sql>\n");
        xml.append("\t<description>").append(escape(trigger.getDescription())).append("</description>\n");
        xml.append("</trigger>\n");
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