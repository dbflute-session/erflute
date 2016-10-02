package org.insightech.er.editor.persistent.xml.writer;

import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.persistent.xml.PersistentXml;
import org.insightech.er.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenImageBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenNodeElementBuilder nodeElementBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenImageBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic, WrittenNodeElementBuilder nodeElementBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementBuilder = nodeElementBuilder;
    }

    // ===================================================================================
    //                                                                               Image
    //                                                                               =====
    public String buildImage(InsertedImage insertedImage, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<image>\n");
        xml.append(tab(nodeElementBuilder.buildNodeElement(insertedImage, context)));
        xml.append("\t<data>").append(insertedImage.getBase64EncodedData()).append("</data>\n");
        xml.append("\t<hue>").append(insertedImage.getHue()).append("</hue>\n");
        xml.append("\t<saturation>").append(insertedImage.getSaturation()).append("</saturation>\n");
        xml.append("\t<brightness>").append(insertedImage.getBrightness()).append("</brightness>\n");
        xml.append("\t<alpha>").append(insertedImage.getAlpha()).append("</alpha>\n");
        xml.append("\t<fix_aspect_ratio>").append(insertedImage.isFixAspectRatio()).append("</fix_aspect_ratio>\n");
        xml.append("</image>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String tab(String str) {
        return assistLogic.tab(str);
    }
}