package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.dbflute.erflute.Activator;

public class ReservedWord {

    private static Set<String> reservedWords = new HashSet<>();

    static {
        final ResourceBundle bundle = ResourceBundle.getBundle(Activator.PLUGIN_ID + ".reserved_word");
        final Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            reservedWords.add(keys.nextElement());
        }
    }

    public static boolean isReservedWord(String str) {
        return reservedWords.contains(str);
    }
}
