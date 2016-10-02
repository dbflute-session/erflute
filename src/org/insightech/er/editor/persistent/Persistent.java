package org.insightech.er.editor.persistent;

import java.io.IOException;
import java.io.InputStream;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.persistent.xml.PersistentXml;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class Persistent {

    private static Persistent persistent = new PersistentXml();

    public static Persistent getInstance() {
        return persistent;
    }

    public abstract ERDiagram read(InputStream in) throws Exception;

    public abstract InputStream write(ERDiagram diagram) throws IOException;
}