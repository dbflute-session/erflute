package org.dbflute.erflute.editor.view.figure.anchor;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

public class XYChopboxAnchor extends ChopboxAnchor {

    private Point location;

    public XYChopboxAnchor(IFigure owner) {
        super(owner);
    }

    public void setLocation(Point location) {
        this.location = location;
        fireAnchorMoved();
    }

    @Override
    public Point getLocation(Point reference) {
        if (location != null) {
            final Point point = new Point(location);
            getOwner().translateToAbsolute(point);
            return point;
        }

        return super.getLocation(reference);
    }

    @Override
    public Point getReferencePoint() {
        if (location != null) {
            final Point point = new Point(location);
            getOwner().translateToAbsolute(point);
            return point;
        }

        return super.getReferencePoint();
    }
}
