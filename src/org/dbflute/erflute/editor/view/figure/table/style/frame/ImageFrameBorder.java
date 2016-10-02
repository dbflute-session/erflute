package org.dbflute.erflute.editor.view.figure.table.style.frame;

import org.eclipse.draw2d.FrameBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SchemeBorder;

public class ImageFrameBorder extends FrameBorder {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createBorders() {
        inner = new ImageTitleBarBorder();
        outer = new SchemeBorder(SCHEME_FRAME);
    }

    /**
     * �^�C�g���̈�̕���Ԃ��܂�
     * 
     * @return �^�C�g���̈�̕�
     */
    public int getTitleBarWidth(IFigure figure) {
        return ((ImageTitleBarBorder) this.inner).getWidth(figure);
    }
}
