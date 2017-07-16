package org.dbflute.erflute.editor.view.figure.table;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;

public class IndexFigure extends Figure {

    public IndexFigure() {
        final FlowLayout layout = new FlowLayout();
        setLayoutManager(layout);
    }

    public void clearLabel() {
        removeAll();
    }

    @Override
    protected void paintFigure(Graphics graphics) {
        if (graphics.getBackgroundColor().equals(getParent().getBackgroundColor())) {
            graphics.setAlpha(0);
        }

        super.paintFigure(graphics);
    }
}
