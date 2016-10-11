package org.dbflute.erflute.editor.model.diagram_contents.element.node.note;

import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class Note extends DiagramWalker implements Comparable<Note> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = -8810455349879962852L;
    public static final String PROPERTY_CHANGE_NOTE = "note";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String text;
    private ERVirtualDiagram vdiagram; // null allowed: when main model

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public Note() { // created by ERDiagramLayoutEditPolicy@getCreateCommand()
    }

    // ===================================================================================
    //                                                                   Referring Element
    //                                                                   =================
    @Override
    public List<DiagramWalker> getReferringElementList() {
        final List<DiagramWalker> referringElementList = super.getReferringElementList();
        for (final ConnectionElement connectionElement : this.getIncomings()) {
            final DiagramWalker sourceElement = connectionElement.getSource();
            referringElementList.add(sourceElement);
        }
        return referringElementList;
    }

    // ===================================================================================
    //                                                                        Object Model
    //                                                                        ============
    @Override
    public String getObjectType() {
        return "note";
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
    public String getDescription() {
        return "";
    }

    // ===================================================================================
    //                                                                     Assist Override
    //                                                                     ===============
    @Override
    public boolean needsUpdateOtherModel() {
        return false;
    }

    @Override
    public int getPersistentOrder() {
        return 12;
    }

    @Override
    public boolean isUsePersistentId() {
        return false;
    }

    @Override
    public boolean isIndenpendentOnModel() {
        return true;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int compareTo(Note other) {
        return Format.null2blank(this.text).compareTo(Format.null2blank(other.text));
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.firePropertyChange(PROPERTY_CHANGE_NOTE, null, null);
    }

    public ERVirtualDiagram getVirtualDiagram() {
        return vdiagram;
    }

    public void setVirtualDiagram(ERVirtualDiagram vdiagram) {
        this.vdiagram = vdiagram;
    }
}
