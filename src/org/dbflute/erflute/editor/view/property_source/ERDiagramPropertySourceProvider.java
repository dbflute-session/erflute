package org.dbflute.erflute.editor.view.property_source;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERTableEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

public class ERDiagramPropertySourceProvider implements IPropertySourceProvider {

    @Override
    public IPropertySource getPropertySource(Object object) {
        if (object instanceof ERDiagramEditPart) {
            final ERDiagram diagram = (ERDiagram) ((ERDiagramEditPart) object).getModel();
            return new ERDiagramPropertySource(diagram);
        } else if (object instanceof ERTableEditPart) {
            final ERTable table = (ERTable) ((ERTableEditPart) object).getModel();
            return new ERTablePropertySource(table);
        }

        return null;
    }
}
