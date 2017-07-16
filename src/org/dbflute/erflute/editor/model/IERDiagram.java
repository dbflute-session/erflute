package org.dbflute.erflute.editor.model;

import org.dbflute.erflute.editor.ERFluteMultiPageEditor;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.eclipse.draw2d.geometry.Point;

/*
 * TODO ymd ERDiagramとERVirtualDiagramを抽象化するために導入したインターフェース。
 * リファクタリングが進んで、ERDiagramの2つの債務(ERDiagramのTODO参照)が分割されたら削除すること。
 */
public interface IERDiagram extends Materializable {

    Point getMousePoint();

    void setMousePoint(Point mousePoint);

    ERFluteMultiPageEditor getEditor();

    boolean contains(DiagramWalker... models);

    default ERDiagram toMaterializedDiagram() {
        return (ERDiagram) toMaterialize();
    }
}
