package org.dbflute.erflute.editor.controller.command.common;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ViewableModel;

public class ChangeFontCommand extends AbstractCommand {

    private final ViewableModel viewableModel;
    private final String oldFontName;
    private final String newFontName;
    private final int oldFontSize;
    private final int newFontSize;

    public ChangeFontCommand(ViewableModel viewableModel, String fontName, int fontSize) {
        this.viewableModel = viewableModel;

        this.oldFontName = viewableModel.getFontName();
        this.oldFontSize = viewableModel.getFontSize();

        this.newFontName = fontName;
        this.newFontSize = fontSize;
    }

    @Override
    protected void doExecute() {
        viewableModel.setFontName(newFontName);
        viewableModel.setFontSize(newFontSize);

        //		if (viewableModel instanceof ERVirtualTable) {
        //			ERTable table = ((ERVirtualTable)viewableModel).getRawTable();
        //			for (ERModel model : ((ERVirtualTable) viewableModel).getDiagram().getDiagramContents().getModelSet()) {
        //				ERVirtualTable vtable = model.findVirtualTable(table);
        //				if (!vtable.equals(viewableModel)) {
        //					vtable.setFontName(fontName)
        ////					vtable.firePropertyChange(vtable.PROPERTY_CHANGE_FONT, null, null);
        //				}
        //			}
        //		}
    }

    @Override
    protected void doUndo() {
        viewableModel.setFontName(oldFontName);
        viewableModel.setFontSize(oldFontSize);
    }
}
