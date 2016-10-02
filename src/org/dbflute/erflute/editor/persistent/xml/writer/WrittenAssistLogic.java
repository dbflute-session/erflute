package org.dbflute.erflute.editor.persistent.xml.writer;

import java.text.DateFormat;

import org.dbflute.erflute.editor.persistent.xml.PersistentXml;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenAssistLogic {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenAssistLogic(PersistentXml persistentXml) {
        this.persistentXml = persistentXml;
    }

    // ===================================================================================
    //                                                                               Color
    //                                                                               =====
    public String buildColor(int[] colors) {
        final StringBuilder xml = new StringBuilder();
        if (colors != null) {
            xml.append("<color>\n");
            xml.append("\t<r>").append(colors[0]).append("</r>\n");
            xml.append("\t<g>").append(colors[1]).append("</g>\n");
            xml.append("\t<b>").append(colors[2]).append("</b>\n");
            xml.append("</color>\n");
        }
        return xml.toString();
    }

    public void appendColor(StringBuilder xml, String tagName, int[] defaultColor) {
        if (defaultColor == null) {
            return;
        }
        xml.append("\t<" + tagName + ">\n");
        xml.append("\t\t<r>").append(defaultColor[0]).append("</r>\n");
        xml.append("\t\t<g>").append(defaultColor[1]).append("</g>\n");
        xml.append("\t\t<b>").append(defaultColor[2]).append("</b>\n");
        xml.append("\t</" + tagName + ">\n");
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    public String tab(String str) {
        str = str.replaceAll("\n\t", "\n\t\t");
        str = str.replaceAll("\n<", "\n\t<");
        return "\t" + str;
    }

    public String escape(String s) {
        if (s == null) {
            return "";
        }

        final StringBuilder result = new StringBuilder(s.length() + 10);
        for (int i = 0; i < s.length(); ++i) {
            appendEscapedChar(result, s.charAt(i));
        }
        return result.toString();
    }

    private void appendEscapedChar(StringBuilder buffer, char c) {
        final String replacement = getReplacement(c);
        if (replacement != null) {
            buffer.append('&');
            buffer.append(replacement);
            buffer.append(';');
        } else {
            buffer.append(c);
        }
    }

    private String getReplacement(char c) {
        // Encode special XML characters into the equivalent character
        // references.
        // The first five are defined by default for all XML documents.
        // The next three (#xD, #xA, #x9) are encoded to avoid them
        // being converted to spaces on de-serialization
        switch (c) {
        case '<':
            return "lt"; //$NON-NLS-1$
        case '>':
            return "gt"; //$NON-NLS-1$
        case '"':
            return "quot"; //$NON-NLS-1$
        case '\'':
            return "apos"; //$NON-NLS-1$
        case '&':
            return "amp"; //$NON-NLS-1$
        case '\r':
            return "#x0D"; //$NON-NLS-1$
        case '\n':
            return "#x0A"; //$NON-NLS-1$
        case '\u0009':
            return "#x09"; //$NON-NLS-1$
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