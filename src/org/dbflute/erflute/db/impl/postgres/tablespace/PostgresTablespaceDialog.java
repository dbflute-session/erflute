package org.dbflute.erflute.db.impl.postgres.tablespace;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class PostgresTablespaceDialog extends TablespaceDialog {

    private Text location;
    private Text owner;

    @Override
    protected void initComponent(Composite composite) {
        super.initComponent(composite);

        this.location = CompositeFactory.createText(this, composite, "label.tablespace.location", false);
        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.data.file.example");
        this.owner = CompositeFactory.createText(this, composite, "label.tablespace.owner", false);
    }

    @Override
    protected TablespaceProperties setTablespaceProperties() {
        final PostgresTablespaceProperties properties = new PostgresTablespaceProperties();

        properties.setLocation(location.getText().trim());
        properties.setOwner(owner.getText().trim());

        return properties;
    }

    @Override
    protected void setData(TablespaceProperties tablespaceProperties) {
        if (tablespaceProperties instanceof PostgresTablespaceProperties) {
            final PostgresTablespaceProperties properties = (PostgresTablespaceProperties) tablespaceProperties;

            location.setText(Format.toString(properties.getLocation()));
            owner.setText(Format.toString(properties.getOwner()));
        }
    }

    @Override
    protected String doValidate() {
        final String errorMessage = super.doValidate();
        if (errorMessage != null) {
            return errorMessage;
        }

        final String text = location.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.location.empty";
        }

        return null;
    }
}
