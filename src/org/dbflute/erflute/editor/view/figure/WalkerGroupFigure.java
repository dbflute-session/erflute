package org.dbflute.erflute.editor.view.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;

public class WalkerGroupFigure extends RectangleFigure {

    private final Label label;

    public WalkerGroupFigure(String name) {
        setOpaque(true);

        final ToolbarLayout layout = new ToolbarLayout();
        setLayoutManager(layout);

        this.label = new Label();
        label.setText(name);
        label.setBorder(new MarginBorder(7));
        add(label);
    }

    @Override
    protected void fillShape(Graphics graphics) {
        graphics.setAlpha(100);
        super.fillShape(graphics);
    }
}
