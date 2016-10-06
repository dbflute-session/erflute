package org.dbflute.erflute.editor.model.diagram_contents.element.node.note;

import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;

public class Note extends NodeElement implements Comparable<Note> {

    private static final long serialVersionUID = -8810455349879962852L;
    public static final String PROPERTY_CHANGE_NOTE = "note";

    private ERModel model;
    private String text;

    public Note() {
    }

    public ERModel getModel() {
        return model;
    }

    public void setModel(ERModel model) {
        this.model = model;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.firePropertyChange(PROPERTY_CHANGE_NOTE, null, null);
    }

    @Override
    public List<NodeElement> getReferringElementList() {
        final List<NodeElement> referringElementList = super.getReferringElementList();
        for (final ConnectionElement connectionElement : this.getIncomings()) {
            final NodeElement sourceElement = connectionElement.getSource();
            referringElementList.add(sourceElement);
        }
        return referringElementList;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int compareTo(Note other) {
        return Format.null2blank(this.text).compareTo(Format.null2blank(other.text));
    }

    @Override
    public String getName() {
        String name = text;
        if (name == null) {
            name = "";
        } else if (name.length() > 20) {
            name = name.substring(0, 20);
        }
        return name;
    }

    @Override
    public String getObjectType() {
        return "note";
    }

    @Override
    public boolean needsUpdateOtherModel() {
        return false;
    }

    @Override
    public int getPersistentOrder() {
        return 12;
    }
}
