package org.dbflute.erflute.editor.view.dialog.view.tab;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class DescriptionTabWrapper extends ValidatableTabWrapper {

    private ERView copyData;

    private Text descriptionText;

    public DescriptionTabWrapper(AbstractDialog dialog, TabFolder parent, int style, ERView copyData) {
        super(dialog, parent, style, "label.table.description");

        this.copyData = copyData;

        this.init();
    }

    @Override
    public void initComposite() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        this.setLayout(gridLayout);

        this.descriptionText = CompositeFactory.createTextArea(null, this, "label.table.description", -1, 400, 1, true);

        this.descriptionText.setText(Format.null2blank(copyData.getDescription()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        String text = descriptionText.getText().trim();
        this.copyData.setDescription(text);
    }

    @Override
    public void setInitFocus() {
        this.descriptionText.setFocus();
    }

    @Override
    public void perfomeOK() {
    }

}
