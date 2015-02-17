package org.insightech.er;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * #analyzed メッセージやラベルのproperties
 * @author ermaster
 * @author jflute
 */
public class ResourceString {

    private static final ResourceBundle resource = ResourceBundle.getBundle("org.insightech.er.ERDiagram");;

    public static String getResourceString(String key) {
        try {
            return resource.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static Map<String, String> getResources(String prefix) {
        final Map<String, String> props = new TreeMap<String, String>(Collections.reverseOrder());
        final Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (key.startsWith(prefix)) {
                props.put(key, resource.getString(key));
            }
        }
        return props;
    }
}
