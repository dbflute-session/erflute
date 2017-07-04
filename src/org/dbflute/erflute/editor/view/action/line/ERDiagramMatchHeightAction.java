package org.dbflute.erflute.editor.view.action.line;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.MatchHeightAction;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramMatchHeightAction extends MatchHeightAction {

    public ERDiagramMatchHeightAction(IWorkbenchPart part) {
        super(part);
        this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.MATCH_HEIGHT));
        this.setDisabledImageDescriptor(null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected List getSelectedObjects() {
        final List<?> objects = new ArrayList<Object>(super.getSelectedObjects());
        boolean first = true;
        for (final Iterator<?> iter = objects.iterator(); iter.hasNext();) {
            final Object object = iter.next();

            if (!(object instanceof EditPart)) {
                iter.remove();
            } else {
                final EditPart editPart = (EditPart) object;
                if (editPart instanceof NormalColumnEditPart) {
                    iter.remove();
                } else {
                    if (first) {
                        editPart.setSelected(2);
                        first = false;
                    }
                }
            }
        }
        return objects;
    }
}
