package org.dbflute.erflute.editor;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * @author kajiku
 */
public class InputDiagramValidator implements IInputValidator {

    @Override
    public String isValid(String paramString) {
        if (!String.valueOf(paramString).isEmpty()) {
            return null;
        }
        return "";
    }
}