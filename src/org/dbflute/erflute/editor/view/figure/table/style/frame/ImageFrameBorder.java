package org.dbflute.erflute.editor.view.figure.table.style.frame;

import org.eclipse.draw2d.FrameBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SchemeBorder;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ImageFrameBorder extends FrameBorder {

    @Override
    protected void createBorders() {
        inner = new ImageTitleBarBorder();
        outer = new SchemeBorder(SCHEME_FRAME);
    }

    public int getTitleBarWidth(IFigure figure) {
        return ((ImageTitleBarBorder) inner).getWidth(figure);
    }
}
