package org.dbflute.erflute.editor.view.dialog.vdiagram;

import org.dbflute.erflute.core.util.Check;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 * @author kajiku
 */
public class InputVirtualDiagramNameValidator implements IInputValidator {

    @Override
    public String isValid(String paramString) {
        if (Check.isEmptyTrim(paramString)) {
            return "";
        }
        return null;
    }
}