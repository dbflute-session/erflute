package org.dbflute.erflute.editor.view.dialog.word.word;

import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.view.dialog.word.AbstractWordDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WordDialog extends AbstractWordDialog {

    private final Word targetWord;
    private Word returnWord;

    public WordDialog(Shell parentShell, Word targetWord, boolean add, ERDiagram diagram) {
        super(parentShell, diagram);
        this.targetWord = targetWord;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.word";
    }

    @Override
    protected void setWordData() {
        setData(targetWord.getPhysicalName(), targetWord.getLogicalName(),
                targetWord.getType(), targetWord.getTypeData(), targetWord.getDescription());
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    protected String doValidate() {
        final String text = logicalNameText.getText().trim();
        if (text.isEmpty()) {
            return "error.column.logical.name.empty";
        }
        return super.doValidate();
    }

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    protected void performOK() {
        String text = lengthText.getText();
        Integer length = null;
        if (!text.equals("")) {
            final int len = Integer.parseInt(text);
            length = new Integer(len);
        }
        text = decimalText.getText();
        Integer decimal = null;
        if (!text.equals("")) {
            final int len = Integer.parseInt(text);
            decimal = new Integer(len);
        }
        boolean array = false;
        Integer arrayDimension = null;
        if (arrayDimensionText != null) {
            text = arrayDimensionText.getText();
            if (!text.equals("")) {
                final int len = Integer.parseInt(text);
                arrayDimension = new Integer(len);
            }
            array = arrayCheck.getSelection();
        }
        boolean unsigned = false;
        if (unsignedCheck != null) {
            unsigned = unsignedCheck.getSelection();
        }
        text = physicalNameText.getText();
        final String database = diagram.getDatabase();
        final SqlType selectedType = SqlType.valueOf(database, typeCombo.getText());
        String args = null;
        if (argsText != null) {
            args = argsText.getText();
        }
        final TypeData typeData = new TypeData(length, decimal, array, arrayDimension, unsigned, args, false);
        returnWord = new Word(physicalNameText.getText(), logicalNameText.getText(),
                selectedType, typeData, descriptionText.getText(), database);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Word getWord() {
        return returnWord;
    }
}
