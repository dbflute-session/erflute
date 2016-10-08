package org.dbflute.erflute.editor.model.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;

public abstract class ConnectionElement extends AbstractModel {

    private static final long serialVersionUID = -5418951773059063716L;
    public static final String PROPERTY_CHANGE_CONNECTION = "connection";
    public static final String PROPERTY_CHANGE_BEND_POINT = "bendPoint";
    public static final String PROPERTY_CHANGE_CONNECTION_ATTRIBUTE = "connection_attribute";

    protected NodeElement source;
    protected NodeElement target;
    private List<Bendpoint> bendPoints = new ArrayList<Bendpoint>();

    public void delete() {
        source.removeOutgoing(this);
        target.removeIncoming(this);
    }

    public void connect() {
        if (this.source != null) {
            source.addOutgoing(this);
        }
        if (this.target != null) {
            target.addIncoming(this);
        }
    }

    public void addBendpoint(int index, Bendpoint point) {
        bendPoints.add(index, point);
        firePropertyChange(PROPERTY_CHANGE_BEND_POINT, null, null);
    }

    public void setBendpoints(List<Bendpoint> points) {
        bendPoints = points;
        firePropertyChange(PROPERTY_CHANGE_BEND_POINT, null, null);
    }

    public void removeBendpoint(int index) {
        bendPoints.remove(index);
        firePropertyChange(PROPERTY_CHANGE_BEND_POINT, null, null);
    }

    public void replaceBendpoint(int index, Bendpoint point) {
        bendPoints.set(index, point);
        firePropertyChange(PROPERTY_CHANGE_BEND_POINT, null, null);
    }

    public void setParentMove() {
        firePropertyChange(PROPERTY_CHANGE_CONNECTION_ATTRIBUTE, null, null);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public ConnectionElement clone() {
        final ConnectionElement clone = (ConnectionElement) super.clone();
        final List<Bendpoint> cloneBendPoints = new ArrayList<Bendpoint>();
        for (final Bendpoint bendPoint : bendPoints) {
            cloneBendPoints.add((Bendpoint) bendPoint.clone());
        }
        clone.bendPoints = cloneBendPoints;
        return clone;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public NodeElement getSource() {
        return source;
    }

    public void setSource(NodeElement source) {
        if (this.source != null) {
            this.source.removeOutgoing(this);
        }
        this.source = source;
        if (this.source != null) {
            this.source.addOutgoing(this);
        }
        firePropertyChange(PROPERTY_CHANGE_CONNECTION, null, source);
    }

    public NodeElement getTarget() {
        return target;
    }

    public void setTarget(NodeElement target) {
        if (this.target != null) {
            this.target.removeIncoming(this);
        }
        this.target = target;
        if (this.target != null) {
            this.target.addIncoming(this);
        }
        firePropertyChange(PROPERTY_CHANGE_CONNECTION, null, source);
    }

    public void setSourceAndTarget(NodeElement source, NodeElement target) {
        this.source = source;
        this.target = target;
    }

    public List<Bendpoint> getBendpoints() {
        return bendPoints;
    }
}
