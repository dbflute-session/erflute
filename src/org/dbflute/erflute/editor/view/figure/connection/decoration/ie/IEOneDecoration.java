package org.dbflute.erflute.editor.view.figure.connection.decoration.ie;

import org.dbflute.erflute.editor.view.figure.connection.decoration.ERDecoration;
import org.eclipse.draw2d.geometry.PointList;

public class IEOneDecoration extends ERDecoration {

    public IEOneDecoration() {
        super();

        final PointList pointList = new PointList();

        pointList.addPoint(-13, -12);
        pointList.addPoint(-13, 12);

        setTemplate(pointList);
        setScale(0.66, 0.66);
        //		this.setScale(1, 1);
    }
}
