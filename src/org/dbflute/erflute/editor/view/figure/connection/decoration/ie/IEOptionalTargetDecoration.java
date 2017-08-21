package org.dbflute.erflute.editor.view.figure.connection.decoration.ie;

import org.dbflute.erflute.editor.view.figure.connection.decoration.ERDecoration;
import org.eclipse.draw2d.geometry.PointList;

public class IEOptionalTargetDecoration extends ERDecoration {

    public IEOptionalTargetDecoration() {
        super();

        final PointList pointList = new PointList();

        pointList.addPoint(-30, 2);
        pointList.addPoint(-30, -2);
        pointList.addPoint(-29, -3);
        pointList.addPoint(-29, -4);
        pointList.addPoint(-27, -6);
        pointList.addPoint(-26, -6);
        pointList.addPoint(-25, -7);
        pointList.addPoint(-21, -7);
        pointList.addPoint(-20, -6);
        pointList.addPoint(-19, -6);
        pointList.addPoint(-17, -4);
        pointList.addPoint(-17, -3);
        pointList.addPoint(-16, -2);

        pointList.addPoint(-16, 0);
        pointList.addPoint(-13, 0);
        pointList.addPoint(-1, -12);
        pointList.addPoint(-13, 0);
        pointList.addPoint(-1, 12);
        pointList.addPoint(-13, 0);
        pointList.addPoint(-16, 0);

        pointList.addPoint(-16, 2);
        pointList.addPoint(-17, 3);
        pointList.addPoint(-17, 4);
        pointList.addPoint(-19, 6);
        pointList.addPoint(-20, 6);
        pointList.addPoint(-21, 7);
        pointList.addPoint(-25, 7);
        pointList.addPoint(-26, 6);
        pointList.addPoint(-27, 6);
        pointList.addPoint(-29, 4);
        pointList.addPoint(-29, 3);
        pointList.addPoint(-30, 2);
        pointList.addPoint(-30, -2);

        setTemplate(pointList);
        setScale(0.66, 0.66);
    }
}
