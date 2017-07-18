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

    private final ERView copyData;
    private Text descriptionText;

    public DescriptionTabWrapper(AbstractDialog dialog, TabFolder parent, int style, ERView copyData) {
        super(dialog, parent, style, "label.table.description");

        this.copyData = copyData;

        init();
    }

    @Override
    public void initComposite() {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        setLayout(gridLayout);

        this.descriptionText = CompositeFactory.createTextArea(null, this, "label.table.description", -1, 400, 1, true);
        descriptionText.setText(Format.null2blank(copyData.getDescription()));
    }

    @Override
    public void validatePage() throws InputException {
        final String text = descriptionText.getText().trim();
        copyData.setDescription(text);
    }

    @Override
    public void setInitFocus() {
        descriptionText.setFocus();
    }

    @Override
    public void perfomeOK() {
    }
}
