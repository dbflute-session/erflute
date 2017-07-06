package org.dbflute.erflute.editor.model.diagram_contents.element.node.image;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectListModel;

public class InsertedImageSet extends AbstractModel implements ObjectListModel, Iterable<InsertedImage> {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_INSERTED_IMAGE_SET = "InsertedImageSet";

    private List<InsertedImage> insertedImageList;

    public InsertedImageSet() {
        this.insertedImageList = new ArrayList<>();
    }

    public void add(InsertedImage insertedImage) {
        insertedImageList.add(insertedImage);
        firePropertyChange(PROPERTY_CHANGE_INSERTED_IMAGE_SET, null, null);
    }

    public int remove(InsertedImage insertedImage) {
        final int index = insertedImageList.indexOf(insertedImage);
        insertedImageList.remove(index);
        firePropertyChange(PROPERTY_CHANGE_INSERTED_IMAGE_SET, null, null);

        return index;
    }

    public List<InsertedImage> getList() {
        return insertedImageList;
    }

    @Override
    public Iterator<InsertedImage> iterator() {
        return insertedImageList.iterator();
    }

    @Override
    public InsertedImageSet clone() {
        final InsertedImageSet insertedImageSet = (InsertedImageSet) super.clone();
        final List<InsertedImage> newInsertedImageList = new ArrayList<>();

        for (final InsertedImage insertedImage : insertedImageList) {
            final InsertedImage newInsertedImage = (InsertedImage) insertedImage.clone();
            newInsertedImageList.add(newInsertedImage);
        }

        insertedImageSet.insertedImageList = newInsertedImageList;

        return insertedImageSet;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getObjectType() {
        return "list";
    }
}
