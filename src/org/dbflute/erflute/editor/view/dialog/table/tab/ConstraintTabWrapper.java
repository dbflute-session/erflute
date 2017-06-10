package org.dbflute.erflute.editor.view.dialog.table.tab;

import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.view.dialog.table.TableDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class ConstraintTabWrapper extends ValidatableTabWrapper {

    private final ERTable copyData;
    private Text constraintText;
    private Text primaryKeyNameText;
    private Text optionText;
    private final TableDialog tableDialog;

    public ConstraintTabWrapper(TableDialog tableDialog, TabFolder parent, int style, ERTable copyData) {
        super(tableDialog, parent, style, "label.constraint.and.option");

        this.copyData = copyData;
        this.tableDialog = tableDialog;

        this.init();
    }

    @Override
    public void validatePage() throws InputException {
        String text = constraintText.getText().trim();
        this.copyData.setConstraint(text);

        text = primaryKeyNameText.getText().trim();
        if (!Check.isAlphabet(text)) {
            throw new InputException("error.primary.key.name.not.alphabet");
        }
        this.copyData.setPrimaryKeyName(text);

        text = optionText.getText().trim();
        this.copyData.setOption(text);
    }

    @Override
    public void initComposite() {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        this.setLayout(gridLayout);

        CompositeFactory.createLabel(this, "label.table.constraint", 1);

        this.constraintText = CompositeFactory.createTextArea(tableDialog, this, null, -1, 100, 1, false);

        this.constraintText.setText(Format.null2blank(copyData.getConstraint()));

        CompositeFactory.filler(this, 1);

        this.primaryKeyNameText = CompositeFactory.createText(tableDialog, this, "label.primary.key.name", 1, false);
        this.primaryKeyNameText.setText(Format.null2blank(copyData.getPrimaryKeyName()));

        CompositeFactory.filler(this, 1);

        CompositeFactory.createLabel(this, "label.option", 1);

        this.optionText = CompositeFactory.createTextArea(tableDialog, this, null, -1, 100, 1, false);

        this.optionText.setText(Format.null2blank(copyData.getOption()));
    }

    @Override
    public void setInitFocus() {
        this.constraintText.setFocus();
    }

    @Override
    public void perfomeOK() {
    }
}
