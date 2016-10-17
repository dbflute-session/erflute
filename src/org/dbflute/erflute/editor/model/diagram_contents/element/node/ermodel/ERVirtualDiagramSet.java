package org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERVirtualDiagramSet extends AbstractModel implements Iterable<ERVirtualDiagram> {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_MODEL_SET = "ModelSet";

    private final List<ERVirtualDiagram> vdiagrams;

    public ERVirtualDiagramSet() {
        vdiagrams = new ArrayList<ERVirtualDiagram>();
    }

    @Override
    public Iterator<ERVirtualDiagram> iterator() {
        Collections.sort(vdiagrams, new Comparator<ERVirtualDiagram>() {
            @Override
            public int compare(ERVirtualDiagram o1, ERVirtualDiagram o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return vdiagrams.iterator();
    }

    //	public void addModel(ERModel ermodel) {
    //		ermodels.add(ermodel);
    //		this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    //	}
    //
    public void addModels(List<ERVirtualDiagram> vdiagrams) {
        this.vdiagrams.addAll(vdiagrams);
        firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    }

    public void add(ERVirtualDiagram vdiagram) {
        vdiagrams.add(vdiagram);
        firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    }

    public int remove(ERVirtualDiagram vdiagram) {
        final int index = this.vdiagrams.indexOf(vdiagram);
        vdiagrams.remove(index);
        firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);

        return index;
    }

    public void changeModel(ERVirtualDiagram vdiagram) {
        firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    }

    public ERVirtualDiagram getModel(String modelName) {
        for (final ERVirtualDiagram vdiagram : vdiagrams) {
            if (vdiagram.getName().equals(modelName)) {
                return vdiagram;
            }
        }
        return null;
    }

    public void deleteRelationship(Relationship relationship) {
        for (final ERVirtualDiagram vdiagram : vdiagrams) {
            vdiagram.deleteRelationship(relationship);
        }
    }

    public void createRelationship(Relationship relationship) {
        for (final ERVirtualDiagram vdiagram : vdiagrams) {
            vdiagram.createRelationship(relationship);
        }
    }
}
