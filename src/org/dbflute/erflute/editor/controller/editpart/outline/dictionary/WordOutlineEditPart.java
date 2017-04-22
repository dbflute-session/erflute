package org.dbflute.erflute.editor.controller.editpart.outline.dictionary;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.dictionary.EditWordCommand;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.dialog.word.word.WordDialog;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class WordOutlineEditPart extends AbstractOutlineEditPart {

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Dictionary.PROPERTY_CHANGE_DICTIONARY)) {
            refreshVisuals();
        }
    }

    @Override
    protected List getModelChildren() {
        List<ColumnHolder> wordHolderList = new ArrayList<ColumnHolder>();

        List<ERTable> wordHolderList1 = new ArrayList<ERTable>();
        List<ERView> wordHolderList2 = new ArrayList<ERView>();
        List<ColumnGroup> wordHolderList3 = new ArrayList<ColumnGroup>();

        ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();
        Word word = (Word) this.getModel();

        List<NormalColumn> normalColumns = diagram.getDiagramContents().getDictionary().getColumnList(word);

        Category category = this.getCurrentCategory();

        for (NormalColumn normalColumn : normalColumns) {
            ColumnHolder columnHolder = normalColumn.getColumnHolder();

            if (columnHolder instanceof ERTable) {
                ERTable table = (ERTable) columnHolder;
                if (wordHolderList1.contains(table)) {
                    continue;
                }

                if (category != null && !category.contains(table)) {
                    continue;
                }

                wordHolderList1.add(table);

            } else if (columnHolder instanceof ERView) {
                ERView view = (ERView) columnHolder;
                if (wordHolderList2.contains(view)) {
                    continue;
                }

                if (category != null && !category.contains(view)) {
                    continue;
                }

                wordHolderList2.add(view);

            } else if (columnHolder instanceof ColumnGroup) {
                if (wordHolderList3.contains(columnHolder)) {
                    continue;
                }
                wordHolderList3.add((ColumnGroup) columnHolder);
            }
        }

        Collections.sort(wordHolderList1);
        Collections.sort(wordHolderList2);
        Collections.sort(wordHolderList3);

        wordHolderList.addAll(wordHolderList1);
        wordHolderList.addAll(wordHolderList2);
        wordHolderList.addAll(wordHolderList3);

        return wordHolderList;
    }

    @Override
    protected void refreshOutlineVisuals() {
        Word word = (Word) this.getModel();

        ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

        int viewMode = diagram.getDiagramContents().getSettings().getOutlineViewMode();

        String name = null;

        if (viewMode == DiagramSettings.VIEW_MODE_PHYSICAL) {
            if (word.getPhysicalName() != null) {
                name = word.getPhysicalName();

            } else {
                name = "";
            }
        } else if (viewMode == DiagramSettings.VIEW_MODE_LOGICAL) {
            if (word.getLogicalName() != null) {
                name = word.getLogicalName();

            } else {
                name = "";
            }
        } else {
            if (word.getLogicalName() != null) {
                name = word.getLogicalName();

            } else {
                name = "";
            }

            name += "/";

            if (word.getPhysicalName() != null) {
                name += word.getPhysicalName();

            }
        }

        this.setWidgetText(diagram.filter(name));

        this.setWidgetImage(Activator.getImage(ImageKey.WORD));
    }

    @Override
    public void performRequest(Request request) {
        Word word = (Word) this.getModel();
        ERDiagram diagram = this.getDiagram();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            WordDialog dialog = new WordDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), word, false, diagram);

            if (dialog.open() == IDialogConstants.OK_ID) {
                EditWordCommand command = new EditWordCommand(word, dialog.getWord(), diagram);
                this.execute(command);
            }
        }

        super.performRequest(request);
    }

    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }
}
