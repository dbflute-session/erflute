package org.dbflute.erflute.editor.view.dialog.vdiagram;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 * @author kajiku
 */
public class InputVirtualDiagramNameValidator implements IInputValidator {

    private final ERDiagram diagram;

    public InputVirtualDiagramNameValidator(ERDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public String isValid(String paramString) {
        if (Check.isEmptyTrim(paramString)) {
            return "";
        }
        if (isDuplicateName(paramString)) {
            return "Duplicate name is not allowed.";
        }
        return null;
    }

    private boolean isDuplicateName(String paramString) {
        boolean isDuplicate = false;
        for (final ERVirtualDiagram vdiagram : diagram.getDiagramContents().getVirtualDiagramSet()) {
            if (vdiagram.getName().equals(paramString)) {
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }
}