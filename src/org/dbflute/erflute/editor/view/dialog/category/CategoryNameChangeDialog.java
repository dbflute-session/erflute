package org.dbflute.erflute.editor.view.dialog.category;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CategoryNameChangeDialog extends AbstractDialog {

    private Text categoryNameText = null;

    private Category targetCategory;

    private String categoryName;

    public CategoryNameChangeDialog(Shell parentShell, Category category) {
        super(parentShell, 2);
        this.targetCategory = category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initComponent(Composite composite) {
        this.categoryNameText = CompositeFactory.createText(this, composite, "label.category.name", true);
    }

    @Override
    protected String getTitle() {
        return "dialog.title.change.category.name";
    }

    @Override
    protected void performOK() throws InputException {
    }

    @Override
    protected void setupData() {
        this.categoryNameText.setText(this.targetCategory.getName());
    }

    @Override
    protected String doValidate() {
        String text = categoryNameText.getText().trim();

        if ("".equals(text)) {
            return "error.category.name.empty";
        }

        this.categoryName = text;

        return null;
    }

    public String getCategoryName() {
        return this.categoryName;
    }
}
