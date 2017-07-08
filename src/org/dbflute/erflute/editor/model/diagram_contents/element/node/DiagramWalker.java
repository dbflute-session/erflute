package org.dbflute.erflute.editor.model.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.ObjectModel;
import org.dbflute.erflute.editor.model.ViewableModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.CommentConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class DiagramWalker extends ViewableModel implements ObjectModel, Materializable<DiagramWalker> {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_CHANGE_RECTANGLE = "rectangle";
    public static final String PROPERTY_CHANGE_INCOMING = "incoming";
    public static final String PROPERTY_CHANGE_OUTGOING = "outgoing";

    protected ERDiagram diagram; // null allowed: when virtual model element
    protected Location location;
    protected List<WalkerConnection> incomings = new ArrayList<>(); // target
    protected List<WalkerConnection> outgoings = new ArrayList<>(); // source

    public abstract boolean needsUpdateOtherModel();

    public DiagramWalker() {
        this.location = new Location(0, 0, 0, 0);
    }

    public List<DiagramWalker> getReferringElementList() {
        final List<DiagramWalker> referringElementList = new ArrayList<>();
        for (final WalkerConnection connectionElement : getOutgoings()) {
            final DiagramWalker targetElement = connectionElement.getTargetWalker();
            referringElementList.add(targetElement);
        }
        return referringElementList;
    }

    public List<DiagramWalker> getReferedElementList() {
        final List<DiagramWalker> referedElementList = new ArrayList<>();
        for (final WalkerConnection connectionElement : getIncomings()) {
            final DiagramWalker sourceElement = connectionElement.getSourceWalker();
            referedElementList.add(sourceElement);
        }
        return referedElementList;
    }

    @Override
    public DiagramWalker clone() {
        final DiagramWalker clone = (DiagramWalker) super.clone();
        clone.location = location.clone();
        clone.setIncoming(new ArrayList<WalkerConnection>());
        clone.setOutgoing(new ArrayList<WalkerConnection>());
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

    public void setWidth(final int width) {
        location.width = width;
    }

    public int getHeight() {
        return location.height;
    }

    public void setHeight(final int height) {
        location.height = height;
    }

    public void setLocation(Location location) {
        this.location = location;
        firePropertyChange(PROPERTY_CHANGE_RECTANGLE, null, null);
    }

    public List<WalkerConnection> getPersistentConnections() {
        final List<WalkerConnection> filteredList = new ArrayList<>();
        for (final WalkerConnection conn : incomings) {
            if (conn instanceof CommentConnection) {
                continue;
            }
            filteredList.add(conn);
        }
        return filteredList;
    }

    public List<WalkerConnection> getIncomings() { // target
        return incomings;
    }

    public void setIncoming(List<WalkerConnection> relations) { // target
        this.incomings = relations;
        firePropertyChange(PROPERTY_CHANGE_INCOMING, null, null);
    }

    public void addIncoming(WalkerConnection relation) { // called by e.g. setTarget()
        incomings.add(relation);
        firePropertyChange(PROPERTY_CHANGE_INCOMING, null, null);
    }

    public void removeIncoming(WalkerConnection relation) {
        incomings.remove(relation);
        firePropertyChange(PROPERTY_CHANGE_INCOMING, null, null);
    }

    private boolean containsIncoming(WalkerConnection relation) {
        return getIncomings().stream().anyMatch(i -> i.equals(relation));
    }

    public List<WalkerConnection> getOutgoings() { // source
        return outgoings;
    }

    public void setOutgoing(List<WalkerConnection> relations) { // source
        this.outgoings = relations;
        firePropertyChange(PROPERTY_CHANGE_OUTGOING, null, null);
    }

    public void addOutgoing(WalkerConnection relation) { // called by e.g. setSource()
        outgoings.add(relation);
        firePropertyChange(PROPERTY_CHANGE_OUTGOING, null, null);
    }

    public void removeOutgoing(WalkerConnection relation) {
        outgoings.remove(relation);
        firePropertyChange(PROPERTY_CHANGE_OUTGOING, null, null);
    }

    private boolean containsOutgoing(WalkerConnection relation) {
        return getOutgoings().stream().anyMatch(o -> o.equals(relation));
    }

    public boolean haveConnection(WalkerConnection relation) {
        return containsIncoming(relation) || containsOutgoing(relation);
    }

    public void refreshInVirtualDiagram(DiagramWalker... others) {
        if (inVirtualDiagram()) {
            refresh(others);
        }
    }

    private boolean inVirtualDiagram() {
        return getDiagram().isVirtual();
    }

    public void refresh(DiagramWalker... others) {
        final List<DiagramWalker> walkers = new ArrayList<>(Arrays.asList(others));
        walkers.add(this);
        ERModelUtil.refreshDiagram(getDiagram(), walkers);
    }
}
