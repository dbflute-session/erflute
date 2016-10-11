package org.dbflute.erflute.editor.view.figure;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Color;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WalkerNoteFigure extends Shape {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final int RETURN_WIDTH = 15;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TextFlow label;
    private Color foregroundColor;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WalkerNoteFigure() {
        this.create();
        this.setMinimumSize(new Dimension(RETURN_WIDTH * 2, RETURN_WIDTH * 2));
    }

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    public void create() {
        setBorder(new MarginBorder(RETURN_WIDTH));
        setLayoutManager(new BorderLayout());
        final FlowPage page = new FlowPage();
        label = new TextFlow();
        final ParagraphTextLayout layout = new ParagraphTextLayout(label, ParagraphTextLayout.WORD_WRAP_SOFT);
        label.setLayoutManager(layout);
        label.setOpaque(false);
        page.add(label);
        add(page, BorderLayout.CENTER);
    }

    // ===================================================================================
    //                                                                           Note Text
    //                                                                           =========
    public void setText(String text, int[] color) {
        this.decideColor(color);
        this.setForegroundColor(this.foregroundColor);
        this.label.setText(text);
    }

    // ===================================================================================
    //                                                                               Shape
    //                                                                               =====
    @Override
    protected void fillShape(Graphics graphics) {
        graphics.setAlpha(200);
        final Rectangle bounds = this.getBounds();
        final Point topRight1 = bounds.getTopRight().translate(0, RETURN_WIDTH);
        final Point topRight2 = bounds.getTopRight().translate(-RETURN_WIDTH, 0);
        final PointList pointList = new PointList();
        pointList.addPoint(bounds.getTopLeft());
        pointList.addPoint(bounds.getBottomLeft());
        pointList.addPoint(bounds.getBottomRight());
        pointList.addPoint(topRight1);
        pointList.addPoint(topRight2);
        pointList.addPoint(bounds.getTopLeft());
        graphics.fillPolygon(pointList);
    }

    @Override
    protected void outlineShape(Graphics graphics) {
        final Rectangle r = getBounds();
        final int x = r.x + getLineWidth() / 2;
        final int y = r.y + getLineWidth() / 2;
        final int w = r.width - Math.max(1, getLineWidth());
        final int h = r.height - Math.max(1, getLineWidth());
        final Rectangle bounds = new Rectangle(x, y, w, h);
        final Point topRight1 = bounds.getTopRight().translate(0, RETURN_WIDTH);
        final Point topRight2 = bounds.getTopRight().translate(-RETURN_WIDTH, 0);
        final Point topRight3 = bounds.getTopRight().translate(-RETURN_WIDTH, RETURN_WIDTH);
        graphics.drawLine(bounds.getTopLeft(), bounds.getBottomLeft());
        graphics.drawLine(bounds.getBottomLeft(), bounds.getBottomRight());
        graphics.drawLine(bounds.getBottomRight(), topRight1);
        graphics.drawLine(topRight1, topRight2);
        graphics.drawLine(topRight2, bounds.getTopLeft());
        graphics.drawLine(topRight2, topRight3);
        graphics.drawLine(topRight3, topRight1);
    }

    private void decideColor(int[] color) {
        if (color != null) {
            final int sum = color[0] + color[1] + color[2];
            if (sum > 255) {
                this.foregroundColor = ColorConstants.black;
            } else {
                this.foregroundColor = ColorConstants.white;
            }
        }
    }
}
