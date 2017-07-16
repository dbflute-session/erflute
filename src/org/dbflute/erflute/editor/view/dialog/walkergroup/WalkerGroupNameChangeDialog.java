package org.dbflute.erflute.editor.view.dialog.walkergroup;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WalkerGroupNameChangeDialog extends AbstractDialog {

    private final WalkerGroup walkerGroup;
    private Text categoryNameText;
    private String categoryName;

    public WalkerGroupNameChangeDialog(Shell parentShell, WalkerGroup walkerGroup) {
        super(parentShell, 2);
        this.walkerGroup = walkerGroup;
    }

    @Override
    protected void initComponent(Composite composite) {
        this.categoryNameText = CompositeFactory.createText(this, composite, "Table Group Name", true);
    }

    @Override
    protected String getTitle() {
        return "dialog.title.change.vgroup.name";
    }

    @Override
    protected void performOK() throws InputException {
    }

    @Override
    protected void setupData() {
        categoryNameText.setText(walkerGroup.getName());
    }

    @Override
    protected String doValidate() {
        final String text = categoryNameText.getText().trim();
        if ("".equals(text)) {
            return "error.category.name.empty";
        }
        this.categoryName = text;
        return null;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
