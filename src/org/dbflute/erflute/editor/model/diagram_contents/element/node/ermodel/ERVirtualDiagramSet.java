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

    private final List<ERVirtualDiagram> ermodels;

    public ERVirtualDiagramSet() {
        ermodels = new ArrayList<ERVirtualDiagram>();
    }

    @Override
    public Iterator<ERVirtualDiagram> iterator() {
        Collections.sort(ermodels, new Comparator<ERVirtualDiagram>() {
            @Override
            public int compare(ERVirtualDiagram o1, ERVirtualDiagram o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return ermodels.iterator();
    }

    //	public void addModel(ERModel ermodel) {
    //		ermodels.add(ermodel);
    //		this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    //	}
    //
    public void addModels(List<ERVirtualDiagram> models) {
        ermodels.addAll(models);
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    }

    public void add(ERVirtualDiagram ermodel) {
        this.ermodels.add(ermodel);
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    }

    public int remove(ERVirtualDiagram ermodel) {
        final int index = this.ermodels.indexOf(ermodel);
        this.ermodels.remove(index);
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);

        return index;
    }

    public void changeModel(ERVirtualDiagram ermodel) {
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    }

    public ERVirtualDiagram getModel(String modelName) {
        for (final ERVirtualDiagram model : ermodels) {
            if (model.getName().equals(modelName)) {
                return model;
            }
        }
        return null;
    }

    public void deleteRelation(Relationship relation) {
        for (final ERVirtualDiagram model : ermodels) {
            model.deleteRelation(relation);
        }
    }

    public void createRelation(Relationship relation) {
        for (final ERVirtualDiagram model : ermodels) {
            model.createRelation(relation);
        }
    }
}
