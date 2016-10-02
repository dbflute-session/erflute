package org.insightech.er.editor.persistent.xml.reader;

import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadImageLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadNodeElementLoader nodeElementLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadImageLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadNodeElementLoader nodeElementLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementLoader = nodeElementLoader;
    }

    // ===================================================================================
    //                                                                               Image
    //                                                                               =====
    public InsertedImage loadInsertedImage(Element element, LoadContext context) {
        final InsertedImage insertedImage = new InsertedImage();
        insertedImage.setBase64EncodedData(this.getStringValue(element, "data"));
        insertedImage.setHue(this.getIntValue(element, "hue"));
        insertedImage.setSaturation(this.getIntValue(element, "saturation"));
        insertedImage.setBrightness(this.getIntValue(element, "brightness"));
        insertedImage.setAlpha(this.getIntValue(element, "alpha", 255));
        insertedImage.setFixAspectRatio(this.getBooleanValue(element, "fix_aspect_ratio"));
        nodeElementLoader.loadNodeElement(insertedImage, element, context);
        return insertedImage;
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

    private int getIntValue(Element element, String tagname, int defaultValue) {
        return assistLogic.getIntValue(element, tagname, defaultValue);
    }
}