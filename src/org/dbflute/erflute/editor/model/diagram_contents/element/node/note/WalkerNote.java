package org.dbflute.erflute.editor.model.diagram_contents.element.node.note;

import java.util.List;

import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WalkerNote extends DiagramWalker implements Comparable<WalkerNote> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = -8810455349879962852L;
    public static final String PROPERTY_CHANGE_WALKER_NOTE = "walker_note";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String noteText;
    private ERVirtualDiagram vdiagram; // null allowed: when main model

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WalkerNote() { // created by ERDiagramLayoutEditPolicy@getCreateCommand()
    }

    // ===================================================================================
    //                                                                   Referring Element
    //                                                                   =================
    @Override
    public List<DiagramWalker> getReferringElementList() {
        final List<DiagramWalker> referringElementList = super.getReferringElementList();
        for (final WalkerConnection connectionElement : this.getIncomings()) {
            final DiagramWalker sourceElement = connectionElement.getWalkerSource();
            referringElementList.add(sourceElement);
        }
        return referringElementList;
    }

    // ===================================================================================
    //                                                                        Object Model
    //                                                                        ============
    @Override
    public String getObjectType() {
        return "walker_note";
    }

    @Override
    public String getName() {
        String name = noteText;
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

    public boolean isVirtualDiagramNote() {
        return vdiagram != null;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int compareTo(WalkerNote other) {
        return Format.null2blank(this.noteText).compareTo(Format.null2blank(other.noteText));
    }

    @Override
    public String toString() {
        final String textExp = noteText != null ? Srl.cut(noteText, 10, "...") : "";
        return getClass().getSimpleName() + ":{" + textExp + ", " + vdiagram + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
        this.firePropertyChange(PROPERTY_CHANGE_WALKER_NOTE, null, null);
    }

    public ERVirtualDiagram getVirtualDiagram() {
        return vdiagram;
    }

    public void setVirtualDiagram(ERVirtualDiagram vdiagram) {
        vdiagram.setDefaultColor(DesignResources.NOTE_DEFAULT_COLOR);
        this.vdiagram = vdiagram;
    }

    @Override
    public List<WalkerConnection> getPersistentConnections() {
        return outgoings;
    }
}
