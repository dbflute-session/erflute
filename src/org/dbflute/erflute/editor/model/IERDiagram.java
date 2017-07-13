package org.dbflute.erflute.editor.model;

import org.dbflute.erflute.editor.ERFluteMultiPageEditor;
import org.eclipse.draw2d.geometry.Point;

public interface IERDiagram {

    Point getMousePoint();

    void setMousePoint(Point mousePoint);

    ERFluteMultiPageEditor getEditor();
}
