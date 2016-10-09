package org.dbflute.erflute.editor.view.dialog.column;

import java.util.List;

import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.view.dialog.word.AbstractWordDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class AbstractColumnDialog extends AbstractWordDialog {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // #for_erflute not use word linkage
    //protected Combo wordCombo;
    //protected Text wordFilterText;
    protected CopyColumn targetColumn;
    protected NormalColumn returnColumn;
    protected Word returnWord;
    protected List<Word> wordList;
    protected boolean foreignKey;
    protected boolean isRefered;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractColumnDialog(Shell parentShell, ERDiagram diagram) {
        super(parentShell, diagram);
    }

    public void setTargetColumn(CopyColumn targetColumn, boolean foreignKey, boolean isRefered) {
        this.targetColumn = targetColumn;
        this.foreignKey = foreignKey;
        this.isRefered = isRefered;
        if (this.targetColumn == null) {
            this.setAdd(true);
        } else {
            this.setAdd(false);
        }
    }

    // #for_erflute not use word linkage
    //private void createWordFilter(Composite composite) {
    //    final Composite filterComposite = new Composite(composite, SWT.NONE);
    //    final GridData gridData = new GridData();
    //    gridData.horizontalSpan = 4;
    //    gridData.horizontalAlignment = GridData.FILL;
    //    gridData.grabExcessHorizontalSpace = true;
    //    filterComposite.setLayoutData(gridData);
    //    final GridLayout layout = new GridLayout();
    //    layout.numColumns = 2;
    //    filterComposite.setLayout(layout);
    //    final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
    //    final Font font = new Font(Display.getCurrent(), fontData.getName(), 7, SWT.NORMAL);
    //    final Label label = new Label(filterComposite, SWT.NONE);
    //    label.setText(DisplayMessages.getMessage("label.filter"));
    //    label.setFont(font);
    //    final GridData textGridData = new GridData();
    //    textGridData.widthHint = 50;
    //    this.wordFilterText = new Text(filterComposite, SWT.BORDER);
    //    this.wordFilterText.setLayoutData(textGridData);
    //    this.wordFilterText.setFont(font);
    //}

    // #for_erflute not use word linkage
    //@Override
    //protected void initializeComposite(Composite composite) {
    //    final int numColumns = getCompositeNumColumns();
    //    this.wordCombo = CompositeFactory.createReadOnlyCombo(null, composite, "label.word", numColumns - 1 - 4, -1);
    //    this.createWordFilter(composite);
    //    this.wordCombo.setVisibleItemCount(20);
    //    this.initializeWordCombo(null);
    //    this.wordCombo.addSelectionListener(new SelectionAdapter() {
    //        @Override
    //        public void widgetSelected(SelectionEvent event) {
    //            final int index = wordCombo.getSelectionIndex();
    //            if (index != 0) {
    //                final Word word = wordList.get(index - 1);
    //                setWordData(word);
    //            }
    //            validate();
    //            setEnabledBySqlType();
    //        }
    //    });
    //    super.initializeComposite(composite);
    //}
    //
    //protected void createWordCombo(Composite composite, GridData gridData) {
    //}
    //
    //private void setWordData(Word word) {
    //    this.setData(word.getPhysicalName(), word.getLogicalName(), word.getType(), word.getTypeData(), word.getDescription());
    //}

    @Override
    protected void setWordData() {
        setData(this.targetColumn.getPhysicalName(), this.targetColumn.getLogicalName(), this.targetColumn.getType(),
                this.targetColumn.getTypeData(), this.targetColumn.getDescription());
        // #for_erflute not use word linkage
        //setWordValue();
    }

    // #for_erflute not use word linkage
    //private void initializeWordCombo(String filterString) {
    //    this.wordCombo.removeAll();
    //    this.wordCombo.add("");
    //    this.wordList = this.diagram.getDiagramContents().getDictionary().getWordList();
    //    for (final Iterator<Word> iter = this.wordList.iterator(); iter.hasNext();) {
    //        final Word word = iter.next();
    //
    //        final String name = Format.null2blank(word.getLogicalName());
    //
    //        if (filterString != null && name.indexOf(filterString) == -1) {
    //            iter.remove();
    //
    //        } else {
    //            this.wordCombo.add(name);
    //
    //        }
    //    }
    //}
    //private void setWordValue() {
    //    Word word = this.targetColumn.getWord();
    //    while (word instanceof CopyWord) {
    //        word = ((CopyWord) word).getOriginal();
    //    }
    //    if (word != null) {
    //        final int index = wordList.indexOf(word);
    //        this.wordCombo.select(index + 1);
    //    }
    //}

    // #for_erflute not use word linkage
    //@Override
    //protected void addListener() {
    //    super.addListener();
    //    this.wordFilterText.addModifyListener(new ModifyListener() {
    //        @Override
    //        public void modifyText(ModifyEvent modifyevent) {
    //            final String filterString = wordFilterText.getText();
    //            initializeWordCombo(filterString);
    //        }
    //    });
    //}

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    protected void performOK() {
        String text = lengthText.getText();
        Integer length = null;
        if (!text.equals("")) {
            final int len = Integer.parseInt(text);
            length = Integer.valueOf(len);
        }
        text = decimalText.getText();
        Integer decimal = null;
        if (!text.equals("")) {
            final int len = Integer.parseInt(text);
            decimal = Integer.valueOf(len);
        }
        boolean array = false;
        Integer arrayDimension = null;
        if (this.arrayDimensionText != null) {
            text = arrayDimensionText.getText();
            if (!text.equals("")) {
                final int len = Integer.parseInt(text);
                arrayDimension = Integer.valueOf(len);
            }
            array = this.arrayCheck.getSelection();
        }
        boolean unsigned = false;
        if (this.unsignedCheck != null) {
            unsigned = this.unsignedCheck.getSelection();
        }
        final String physicalName = physicalNameText.getText();
        final String logicalName = logicalNameText.getText();
        final String description = descriptionText.getText();
        String args = null;
        if (argsText != null) {
            args = argsText.getText();
        }
        final String database = this.diagram.getDatabase();
        final SqlType selectedType = SqlType.valueOf(database, typeCombo.getText());
        final TypeData typeData = new TypeData(length, decimal, array, arrayDimension, unsigned, args);

        // #for_erflute not use word linkage
        //final int wordIndex = this.wordCombo.getSelectionIndex();
        //CopyWord word = null;
        //if (wordIndex > 0) {
        //    word = new CopyWord(wordList.get(wordIndex - 1));
        //    if (!"".equals(physicalName)) {
        //        word.setPhysicalName(physicalName);
        //    }
        //    if (!"".equals(logicalName)) {
        //        word.setLogicalName(logicalName);
        //    }
        //    word.setDescription(description);
        //    word.setType(selectedType, typeData, database);
        //} else {
        //    word = new CopyWord(new Word(physicalName, logicalName, selectedType, typeData, description, database));
        //}
        final CopyWord word = new CopyWord(new Word(physicalName, logicalName, selectedType, typeData, description, database));
        returnWord = word;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public NormalColumn getColumn() {
        return this.returnColumn;
    }
}
