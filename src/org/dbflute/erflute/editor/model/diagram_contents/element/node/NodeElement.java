package org.dbflute.erflute.editor.model.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ObjectModel;
import org.dbflute.erflute.editor.model.ViewableModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class NodeElement extends ViewableModel implements ObjectModel {

    private static final long serialVersionUID = -5143984125818569247L;

    public static final String PROPERTY_CHANGE_RECTANGLE = "rectangle";
    public static final String PROPERTY_CHANGE_INCOMING = "incoming";
    public static final String PROPERTY_CHANGE_OUTGOING = "outgoing";

    private ERDiagram diagram; // null allowed: when virtual model element
    private Location location;
    private List<ConnectionElement> incomings = new ArrayList<ConnectionElement>();
    private List<ConnectionElement> outgoings = new ArrayList<ConnectionElement>();

    public abstract boolean needsUpdateOtherModel();

    public NodeElement() {
        this.location = new Location(0, 0, 0, 0);
    }

    public List<NodeElement> getReferringElementList() {
        final List<NodeElement> referringElementList = new ArrayList<NodeElement>();
        for (final ConnectionElement connectionElement : this.getOutgoings()) {
            final NodeElement targetElement = connectionElement.getTarget();
            referringElementList.add(targetElement);
        }
        return referringElementList;
    }

    public List<NodeElement> getReferedElementList() {
        final List<NodeElement> referedElementList = new ArrayList<NodeElement>();
        for (final ConnectionElement connectionElement : this.getIncomings()) {
            final NodeElement sourceElement = connectionElement.getSource();
            referedElementList.add(sourceElement);
        }
        return referedElementList;
    }

    @Override
    public NodeElement clone() {
        final NodeElement clone = (NodeElement) super.clone();
        clone.location = this.location.clone();
        clone.setIncoming(new ArrayList<ConnectionElement>());
        clone.setOutgoing(new ArrayList<ConnectionElement>());
        return clone;
    }

    public abstract int getPersistentOrder(); // #for_erflute

    public abstract boolean isUsePersistentId(); // #for_erflute

    public abstract boolean isIndenpendentOnModel(); // #for_erflute

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDiagram(ERDiagram diagram) {
        this.diagram = diagram;
    }

    public ERDiagram getDiagram() {
        return diagram;
    }

    public int getX() {
        return location.x;
    }

    public int getY() {
        return location.y;
    }

    public int getWidth() {
        return location.width;
    }

    public int getHeight() {
        return location.height;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.firePropertyChange(PROPERTY_CHANGE_RECTANGLE, null, null);
    }

    public List<ConnectionElement> getIncomings() {
        return incomings;
    }

    public List<ConnectionElement> getOutgoings() {
        return outgoings;
    }

    public void setIncoming(List<ConnectionElement> relations) {
        this.incomings = relations;
        this.firePropertyChange(PROPERTY_CHANGE_INCOMING, null, null);
    }

    public void setOutgoing(List<ConnectionElement> relations) {
        this.outgoings = relations;
        this.firePropertyChange(PROPERTY_CHANGE_OUTGOING, null, null);
    }

    public void addIncoming(ConnectionElement relation) {
        this.incomings.add(relation);
        this.firePropertyChange(PROPERTY_CHANGE_INCOMING, null, null);
    }

    public void removeIncoming(ConnectionElement relation) {
        this.incomings.remove(relation);
        this.firePropertyChange(PROPERTY_CHANGE_INCOMING, null, null);
    }

    public void addOutgoing(ConnectionElement relation) {
        this.outgoings.add(relation);
        this.firePropertyChange(PROPERTY_CHANGE_OUTGOING, null, null);
    }

    public void removeOutgoing(ConnectionElement relation) {
        this.outgoings.remove(relation);
        this.firePropertyChange(PROPERTY_CHANGE_OUTGOING, null, null);
    }
}
