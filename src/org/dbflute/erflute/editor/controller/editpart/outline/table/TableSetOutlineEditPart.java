package org.dbflute.erflute.editor.controller.editpart.outline.table;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.eclipse.gef.EditPart;

public class TableSetOutlineEditPart extends AbstractOutlineEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TableSet.PROPERTY_CHANGE_TABLE_SET)) {
            refresh();
        }
    }

    public static List<EditPart> tableEditParts;

    @Override
    protected List<ERTable> getModelChildren() {
        final TableSet tableSet = (TableSet) getModel();

        final List<ERTable> list = new ArrayList<>();

        final Category category = getCurrentCategory();
        final String filterText = getFilterText();
        for (final ERTable table : tableSet) {
            if (category == null || category.contains(table)) {
                if (filterText != null) {
                    if (table.getPhysicalName().toLowerCase().indexOf(filterText.toLowerCase()) < 0) {
                        continue;
                    }
                }
                list.add(table);
            }
        }

        if (getDiagram().getDiagramContents().getSettings().getViewOrderBy() == DiagramSettings.VIEW_MODE_LOGICAL) {
            Collections.sort(list, TableView.LOGICAL_NAME_COMPARATOR);
        } else {
            Collections.sort(list, TableView.PHYSICAL_NAME_COMPARATOR);
        }

        if (filterText != null) {

            final Iterator<ERTable> iterator = list.iterator();
            while (iterator.hasNext()) {
                final ERTable table = iterator.next();
                if (table.getPhysicalName().equalsIgnoreCase(filterText)) {
                    iterator.remove();
                    list.add(0, table);
                    break;
                }
            }
        }

        return list;
    }

    @Override
    protected void refreshOutlineVisuals() {
        setWidgetText(DisplayMessages.getMessage("label.table") + " (" + getModelChildren().size() + ")");
        setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
    }

    @Override
    protected void refreshChildren() {
        super.refreshChildren();

        tableEditParts = new ArrayList<>();
        for (final Object child : getChildren()) {
            final EditPart part = (EditPart) child;
            tableEditParts.add(part);
            part.refresh();
        }
    }
}
