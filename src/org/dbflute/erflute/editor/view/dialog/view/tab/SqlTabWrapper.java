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

    private final ERView copyData;
    private Text sqlText;
    private final ViewDialog viewDialog;

    public SqlTabWrapper(ViewDialog viewDialog, TabFolder parent, int style, ERView copyData) {
        super(viewDialog, parent, style, "label.sql");

        this.viewDialog = viewDialog;
        this.copyData = copyData;

        init();
    }

    @Override
    public void initComposite() {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        setLayout(gridLayout);

        this.sqlText = CompositeFactory.createTextArea(viewDialog, this, "label.sql", 400, 400, 1, true);
        sqlText.setText(Format.null2blank(copyData.getSql()));
    }

    @Override
    public void validatePage() throws InputException {
        final String text = sqlText.getText().trim();

        if (text.equals("")) {
            throw new InputException("error.view.sql.empty");
        }

        copyData.setSql(text);
    }

    @Override
    public void setInitFocus() {
        sqlText.setFocus();
    }

    @Override
    public void perfomeOK() {
    }
}
