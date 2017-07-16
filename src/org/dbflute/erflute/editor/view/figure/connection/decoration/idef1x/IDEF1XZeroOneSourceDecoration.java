package org.dbflute.erflute.editor.view.figure.connection.decoration.idef1x;

import org.dbflute.erflute.editor.view.figure.connection.decoration.ERDecoration;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.PointList;

public class IDEF1XZeroOneSourceDecoration extends ERDecoration {

    public IDEF1XZeroOneSourceDecoration() {
        super();

        final PointList pointList = new PointList();

        pointList.addPoint(-1, 0);
        pointList.addPoint(-8, -7);
        pointList.addPoint(-15, 0);
        pointList.addPoint(-8, 7);
        pointList.addPoint(-1, 0);

        setTemplate(pointList);
        setScale(1, 1);

        final Label label = new Label();
        label.setText("Z");

        add(label);
    }
}
