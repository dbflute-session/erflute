package org.dbflute.erflute.editor.view.dialog.outline.trigger;

import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TriggerDialog extends AbstractDialog {

    private Text nameText;

    private Text schemaText;

    private Text sqlText;

    private Text descriptionText;

    private Trigger trigger;

    private Trigger result;

    public TriggerDialog(Shell parentShell, Trigger trigger) {
        super(parentShell, 2);
        this.setShellStyle(this.getShellStyle() | SWT.RESIZE);

        this.trigger = trigger;
    }

    @Override
    protected void initialize(Composite composite) {
        this.nameText = CompositeFactory.createText(this, composite, "label.trigger.name", false);
        this.schemaText = CompositeFactory.createText(this, composite, "label.schema", false);
        this.sqlText = CompositeFactory.createTextArea(this, composite, "label.sql", DesignResources.DESCRIPTION_WIDTH, 300, 1, false);
        this.descriptionText = CompositeFactory.createTextArea(this, composite, "label.description", -1, 100, 1, true);
    }

    @Override
    protected String doValidate() {
        String text = nameText.getText().trim();
        if (text.equals("")) {
            return "error.trigger.name.empty";
        }

        if (!Check.isAlphabet(text)) {
            return "error.trigger.name.not.alphabet";
        }

        text = schemaText.getText();
        if (!Check.isAlphabet(text)) {
            return "error.schema.not.alphabet";
        }

        text = sqlText.getText();
        if (text.equals("")) {
            return "error.trigger.sql.empty";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.trigger";
    }

    @Override
    protected void performOK() throws InputException {
        this.result = new Trigger();

        this.result.setName(this.nameText.getText().trim());
        this.result.setSchema(this.schemaText.getText().trim());
        this.result.setSql(this.sqlText.getText().trim());
        this.result.setDescription(this.descriptionText.getText().trim());
    }

    @Override
    protected void setData() {
        if (this.trigger != null) {
            this.nameText.setText(Format.toString(this.trigger.getName()));
            this.schemaText.setText(Format.toString(this.trigger.getSchema()));
            this.sqlText.setText(Format.toString(this.trigger.getSql()));
            this.descriptionText.setText(Format.toString(this.trigger.getDescription()));
        }
    }

    public Trigger getResult() {
        return result;
    }

}
