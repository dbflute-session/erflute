package org.insightech.er.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relationship;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERModelSet extends AbstractModel implements Iterable<ERModel> {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_MODEL_SET = "ModelSet";

    private final List<ERModel> ermodels;

    public ERModelSet() {
        ermodels = new ArrayList<ERModel>();
    }

    @Override
    public Iterator<ERModel> iterator() {
        Collections.sort(ermodels, new Comparator<ERModel>() {
            @Override
            public int compare(ERModel o1, ERModel o2) {
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
    public void addModels(List<ERModel> models) {
        ermodels.addAll(models);
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    }

    public void add(ERModel ermodel) {
        this.ermodels.add(ermodel);
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    }

    public int remove(ERModel ermodel) {
        final int index = this.ermodels.indexOf(ermodel);
        this.ermodels.remove(index);
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);

        return index;
    }

    public void changeModel(ERModel ermodel) {
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
    }

    public ERModel getModel(String modelName) {
        for (final ERModel model : ermodels) {
            if (model.getName().equals(modelName)) {
                return model;
            }
        }
        return null;
    }

    public void deleteRelation(Relationship relation) {
        for (final ERModel model : ermodels) {
            model.deleteRelation(relation);
        }
    }

    public void createRelation(Relationship relation) {
        for (final ERModel model : ermodels) {
            model.createRelation(relation);
        }
    }
}
