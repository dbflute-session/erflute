package org.insightech.er.editor.persistent.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.persistent.Persistent;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class PersistentSerialize extends Persistent { // unused

    @Override
    public InputStream write(ERDiagram diagram) throws IOException {
        InputStream inputStream = null;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(diagram);
        oos.close();
        inputStream = new ByteArrayInputStream(out.toByteArray());
        return inputStream;
    }

    @Override
    public ERDiagram read(InputStream in) throws Exception {
        return (ERDiagram) ((ObjectInputStream) in).readObject();
    }
}