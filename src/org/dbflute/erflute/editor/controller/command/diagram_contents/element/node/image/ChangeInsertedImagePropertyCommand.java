package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.image;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;

public class ChangeInsertedImagePropertyCommand extends AbstractCommand {

    protected InsertedImage insertedImage;
    protected InsertedImage oldInsertedImage;
    protected InsertedImage newInsertedImage;

    public ChangeInsertedImagePropertyCommand(ERDiagram diagram,
            InsertedImage insertedImage, InsertedImage newInsertedImage, InsertedImage oldInsertedImage) {
        this.insertedImage = insertedImage;
        this.oldInsertedImage = oldInsertedImage;
        this.newInsertedImage = newInsertedImage;
    }

    @Override
    protected void doExecute() {
        insertedImage.setHue(newInsertedImage.getHue());
        insertedImage.setSaturation(newInsertedImage.getSaturation());
        insertedImage.setBrightness(newInsertedImage.getBrightness());
        insertedImage.setFixAspectRatio(newInsertedImage.isFixAspectRatio());
        insertedImage.setAlpha(newInsertedImage.getAlpha());

        insertedImage.setDirty();
    }

    @Override
    protected void doUndo() {
        insertedImage.setHue(oldInsertedImage.getHue());
        insertedImage.setSaturation(oldInsertedImage.getSaturation());
        insertedImage.setBrightness(oldInsertedImage.getBrightness());
        insertedImage.setFixAspectRatio(oldInsertedImage.isFixAspectRatio());
        insertedImage.setAlpha(oldInsertedImage.getAlpha());

        insertedImage.setDirty();
    }
}
