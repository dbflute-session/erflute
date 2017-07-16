package org.dbflute.erflute.editor.view.tool;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

public class InsertImageTool extends CreationToolEntry {

    public InsertImageTool() {
        super("Image on Diagram", "Insert image on diagram", new SimpleFactory(InsertedImage.class),
                Activator.getImageDescriptor(ImageKey.IMAGE), Activator.getImageDescriptor(ImageKey.IMAGE));
    }

    @Override
    public Tool createTool() {
        final InsertedImageTool tool = new InsertedImageTool();
        tool.setProperties(getToolProperties());

        return tool;
    }

    private class InsertedImageTool extends CreationTool {

        @Override
        protected void performCreation(int button) {
            final String path = getLoadFilePath();

            if (path != null) {
                final InsertedImage insertedImage = (InsertedImage) getCreateRequest().getNewObject();
                insertedImage.setImageFilePath(path);

                super.performCreation(button);
            }
        }

        private String getLoadFilePath() {
            final FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN);
            final String[] filterExtensions = { "*.bmp;*.jpg;*.jpeg;*.gif;*.png;*.tif;*.tiff" };
            fileDialog.setFilterExtensions(filterExtensions);

            return fileDialog.open();
        }
    }
}
