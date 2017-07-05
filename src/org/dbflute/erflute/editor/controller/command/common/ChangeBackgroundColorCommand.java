package org.dbflute.erflute.editor.controller.command.common;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.ViewableModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;

public class ChangeBackgroundColorCommand extends AbstractCommand {

    private final ViewableModel model;
    private final int red;
    private final int green;
    private final int blue;
    private int[] oldColor;

    public ChangeBackgroundColorCommand(ViewableModel model, int red, int green, int blue) {
        this.model = model;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    protected void doExecute() {
        this.oldColor = model.getColor();
        model.setColor(red, green, blue);
        if (model instanceof ERVirtualTable) {
            ERModelUtil.refreshDiagram(((ERVirtualTable) model).getDiagram(), ((ERVirtualTable) model).getRawTable());
        }
    }

    @Override
    protected void doUndo() {
        if (oldColor == null) {
            oldColor = new int[3];
            oldColor[0] = 255;
            oldColor[1] = 255;
            oldColor[2] = 255;
        }

        model.setColor(oldColor[0], oldColor[1], oldColor[2]);
    }
}
