package org.dbflute.erflute.editor.view.dialog.view.tab;

import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.view.dialog.view.ViewDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class SqlTabWrapper extends ValidatableTabWrapper {

    private ERView copyData;

    private Text sqlText;

    private ViewDialog viewDialog;

    public SqlTabWrapper(ViewDialog viewDialog, TabFolder parent, int style, ERView copyData) {
        super(viewDialog, parent, style, "label.sql");

        this.viewDialog = viewDialog;
        this.copyData = copyData;

        this.init();
    }

    @Override
    public void initComposite() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        this.setLayout(gridLayout);

        this.sqlText = CompositeFactory.createTextArea(this.viewDialog, this, "label.sql", 400, 400, 1, true);

        this.sqlText.setText(Format.null2blank(copyData.getSql()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        String text = sqlText.getText().trim();

        if (text.equals("")) {
            throw new InputException("error.view.sql.empty");
        }

        this.copyData.setSql(text);
    }

    @Override
    public void setInitFocus() {
        this.sqlText.setFocus();
    }

    @Override
    public void perfomeOK() {
    }

}
