package org.dbflute.erflute.editor.persistent;

import java.io.IOException;
import java.io.InputStream;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class Persistent {

    private static final Persistent persistent = new PersistentXml();

    public static Persistent getInstance() {
        return persistent;
    }

    public abstract ERDiagram read(InputStream in) throws Exception;

    public abstract InputStream write(ERDiagram diagram) throws IOException;
}