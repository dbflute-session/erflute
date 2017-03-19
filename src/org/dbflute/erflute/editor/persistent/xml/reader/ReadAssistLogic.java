package org.dbflute.erflute.editor.persistent.xml.reader;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ViewableModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadAssistLogic {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadAssistLogic(PersistentXml persistentXml) {
        this.persistentXml = persistentXml;
    }

    // ===================================================================================
    //                                                                               Color
    //                                                                               =====
    public void loadColor(ViewableModel model, Element element) {
        final int[] rgb = new int[] { 255, 255, 255 };
        final Element color = this.getElement(element, "color");
        if (color != null) {
            rgb[0] = this.getIntValue(color, "r");
            rgb[1] = this.getIntValue(color, "g");
            rgb[2] = this.getIntValue(color, "b");
        }
        model.setColor(rgb[0], rgb[1], rgb[2]);
    }

    public void loadDefaultColor(ERDiagram diagram, Element element) {
        final int[] rgb = new int[] { 255, 255, 255 };
        final Element color = this.getElement(element, "default_color");
        if (color != null) {
            rgb[0] = this.getIntValue(color, "r");
            rgb[1] = this.getIntValue(color, "g");
            rgb[2] = this.getIntValue(color, "b");
        }
        diagram.setDefaultColor(rgb[0], rgb[1], rgb[2]);
    }

    // ===================================================================================
    //                                                                               Font
    //                                                                              ======
    public void loadFont(ViewableModel viewableModel, Element element) {
        if (getElement(element, "font_name") == null) {
            return;
        }
        final String fontName = this.getStringValue(element, "font_name");
        final int fontSize = this.getIntValue(element, "font_size");

        viewableModel.setFontName(fontName);
        viewableModel.setFontSize(fontSize);
    }

    // ===================================================================================
    //                                                                            Location
    //                                                                            ========
    public void loadLocation(DiagramWalker nodeElement, Element element) {
        final int x = getIntValue(element, "x");
        final int y = getIntValue(element, "y");
        final int width = getIntValue(element, "width", -1);
        final int height = getIntValue(element, "height", -1);
        nodeElement.setLocation(new Location(x, y, width, height));
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    public String getStringValue(Element element, String tagname) { // contains nested tags
        final NodeList nodeList = element.getElementsByTagName(tagname);
        if (nodeList.getLength() == 0) {
            return null;
        }
        final Node node = nodeList.item(0);
        if (node.getFirstChild() == null) {
            return "";
        }
        return node.getFirstChild().getNodeValue();
    }

    public String getStringValue(Element element, String tagname, String defaultValue) {
        final String str = getStringValue(element, tagname);
        return Srl.is_NotNull_and_NotEmpty(str) ? str : defaultValue;
    }

    public boolean getBooleanValue(Element element, String tagname) {
        return getBooleanValue(element, tagname, false);
    }

    public boolean getBooleanValue(Element element, String tagname, boolean defaultValue) {
        final NodeList nodeList = element.getElementsByTagName(tagname);
        if (nodeList.getLength() == 0) {
            return defaultValue;
        }
        final Node node = nodeList.item(0);
        final String value = node.getFirstChild().getNodeValue();
        return Boolean.valueOf(value).booleanValue();
    }

    public int getIntValue(Element element, String tagname) {
        return getIntValue(element, tagname, 0);
    }

    public int getIntValue(Element element, String tagname, int defaultValue) {
        final NodeList nodeList = element.getElementsByTagName(tagname);
        if (nodeList.getLength() == 0) {
            return defaultValue;
        }
        final Node node = nodeList.item(0);
        if (node.getFirstChild() == null) {
            return defaultValue;
        }
        final String value = node.getFirstChild().getNodeValue();
        return Integer.valueOf(value).intValue();
    }

    public Integer getIntegerValue(Element element, String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);
        if (nodeList.getLength() == 0) {
            return null;
        }
        final Node node = nodeList.item(0);
        if (node.getFirstChild() == null) {
            return null;
        }
        final String value = node.getFirstChild().getNodeValue();
        try {
            return Integer.valueOf(value);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    public Long getLongValue(Element element, String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);
        if (nodeList.getLength() == 0) {
            return null;
        }
        final Node node = nodeList.item(0);
        if (node.getFirstChild() == null) {
            return null;
        }
        final String value = node.getFirstChild().getNodeValue();
        try {
            return Long.valueOf(value);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    public BigDecimal getBigDecimalValue(Element element, String tagname) {
        final String value = this.getStringValue(element, tagname);
        try {
            return new BigDecimal(value);
        } catch (final Exception e) {}
        return null;
    }

    public double getDoubleValue(Element element, String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);
        if (nodeList.getLength() == 0) {
            return 0;
        }
        final Node node = nodeList.item(0);
        if (node.getFirstChild() == null) {
            return 0;
        }
        final String value = node.getFirstChild().getNodeValue();
        return Double.valueOf(value).doubleValue();
    }

    public Date getDateValue(Element element, String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);
        if (nodeList.getLength() == 0) {
            return null;
        }
        final Node node = nodeList.item(0);
        if (node.getFirstChild() == null) {
            return null;
        }
        final String value = node.getFirstChild().getNodeValue();
        try {
            return getDateFormat().parse(value);
        } catch (final ParseException e) {
            return null;
        }
    }

    public String[] getTagValues(Element element, String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);
        final String[] values = new String[nodeList.getLength()];
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            if (node.getFirstChild() != null) {
                values[i] = node.getFirstChild().getNodeValue();
            }
        }
        return values;
    }

    public Element getElement(Element element, String tagname) {
        final NodeList nodeList = element.getChildNodes();
        if (nodeList.getLength() == 0) {
            return null;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                final Element ele = (Element) nodeList.item(i);
                if (ele.getTagName().equals(tagname)) {
                    return ele;
                }
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DateFormat getDateFormat() {
        return PersistentXml.DATE_FORMAT;
    }
}