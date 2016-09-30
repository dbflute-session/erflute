package org.insightech.er;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * #analyzed メッセージやラベルのproperties
 * @author modified by jflute (originated in ermaster)
 */
public class DisplayMessages {

    private static final ResourceBundle resource = ResourceBundle.getBundle("org.insightech.er.ERDiagram");

    public static String getMessage(String key) {
        try {
            return resource.getString(key);
        } catch (final MissingResourceException e) {
            return key;
        }
    }

    public static Map<String, String> getMessageMap(String prefix) {
        final Map<String, String> props = new TreeMap<String, String>(Collections.reverseOrder());
        final Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            if (key.startsWith(prefix)) {
                props.put(key, resource.getString(key));
            }
        }
        return props;
    }
}
