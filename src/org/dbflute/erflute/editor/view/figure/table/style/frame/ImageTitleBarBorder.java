package org.dbflute.erflute.editor.view.figure.table.style.frame;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class ImageTitleBarBorder extends TitleBarBorder {

    private int width;

    @Override
    public void paint(IFigure figure, Graphics g, Insets insets) {
        tempRect.setBounds(getPaintRectangle(figure, insets));
        final Rectangle rec = tempRect;

        // 幅を取得
        this.width = getTextExtents(figure).width + getPadding().getWidth();

        rec.height = Math.min(rec.height, getTextExtents(figure).height + getPadding().getHeight());
        g.clipRect(rec);
        g.fillRectangle(rec);

        // 背景イメージを描画
        final Image image = Activator.getImage(ImageKey.TITLEBAR_BACKGROUND);
        g.drawImage(image, 0, 0, image.getImageData().width, image.getImageData().height, rec.x, rec.y, rec.width, rec.height);

        int x = rec.x + getPadding().left;
        final int y = rec.y + getPadding().top;
        final int textWidth = getTextExtents(figure).width;
        int freeSpace = rec.width - getPadding().getWidth() - textWidth;
        if (getTextAlignment() == 2) {
            freeSpace /= 2;
        }
        if (getTextAlignment() != 1) {
            x += freeSpace;
        }
        g.setFont(getFont(figure));
        g.setForegroundColor(getTextColor());
        g.drawString(getLabel(), x, y);

    }

    public int getWidth(IFigure figure) {
        if (getFont(figure) != null) {
            this.width = getTextExtents(figure).width + getPadding().getWidth();
        }

        return width;
    }
}
