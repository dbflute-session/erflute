package org.dbflute.erflute.editor.model.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class WalkerConnection extends AbstractModel {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_CONNECTION = "connection";
    public static final String PROPERTY_CHANGE_BEND_POINT = "bendPoint";
    public static final String PROPERTY_CHANGE_CONNECTION_ATTRIBUTE = "connection_attribute";

    protected DiagramWalker ownerWalker; // e.g. ERTable, WalkerNote
    protected DiagramWalker sourceWalker; // e.g. MEMBER_STATUS, note
    protected DiagramWalker targetWalker; // e.g. MEMBER, noted table
    private List<Bendpoint> bendPoints = new ArrayList<>();
    private boolean deleted = false;

    public void delete() {
        this.deleted = true;
        sourceWalker.removeOutgoing(this);
        targetWalker.removeIncoming(this);
    }

    public void connect() {
        this.deleted = false;
        if (sourceWalker != null) {
            sourceWalker.addOutgoing(this);
        }
        if (targetWalker != null) {
            targetWalker.addIncoming(this);
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
    //                                                                       Determination
    //                                                                       =============
    public boolean isVirtualDiagramOnly() {
        return ownerWalker instanceof WalkerNote && ((WalkerNote) ownerWalker).isVirtualDiagramNote();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public WalkerConnection clone() {
        final WalkerConnection clone = (WalkerConnection) super.clone();
        final List<Bendpoint> cloneBendPoints = new ArrayList<>();
        for (final Bendpoint bendPoint : bendPoints) {
            cloneBendPoints.add((Bendpoint) bendPoint.clone());
        }
        clone.bendPoints = cloneBendPoints;
        return clone;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DiagramWalker getOwnerWalker() {
        return ownerWalker;
    }

    public void setOwnerWalker(DiagramWalker ownerWalker) {
        this.ownerWalker = ownerWalker;
    }

    public DiagramWalker getWalkerSource() {
        return sourceWalker;
    }

    public void setSourceWalker(DiagramWalker sourceWalker) {
        if (sourceWalker != null) {
            sourceWalker.removeOutgoing(this);
        }
        this.sourceWalker = sourceWalker;
        if (sourceWalker != null) {
            sourceWalker.addOutgoing(this);
        }
        firePropertyChange(PROPERTY_CHANGE_CONNECTION, null, sourceWalker);
    }

    public DiagramWalker getWalkerTarget() {
        return targetWalker;
    }

    public void setTargetWalker(DiagramWalker targetWalker) {
        if (targetWalker != null) {
            targetWalker.removeIncoming(this);
        }
        this.targetWalker = targetWalker;
        if (targetWalker != null) {
            targetWalker.addIncoming(this);
        }
        firePropertyChange(PROPERTY_CHANGE_CONNECTION, null, targetWalker);
    }

    public List<Bendpoint> getBendpoints() {
        return bendPoints;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
