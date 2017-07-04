package org.dbflute.erflute.db.impl.mysql.tablespace;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.db.impl.mysql.MySQLAdvancedComposite;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class MySQLTablespaceDialog extends TablespaceDialog {

    private Text dataFile;
    private Text logFileGroup;
    private Text extentSize;
    private Text initialSize;
    private Combo engine;

    @Override
    protected void initComponent(Composite composite) {
        super.initComponent(composite);

        this.dataFile = CompositeFactory.createText(this, composite, "label.tablespace.data.file", false);
        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.data.file.example");

        this.logFileGroup = CompositeFactory.createText(this, composite, "label.tablespace.log.file.group", false);
        this.extentSize = CompositeFactory.createText(this, composite, "label.tablespace.extent.size", 1, NUM_TEXT_WIDTH, false);
        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.size.example");
        this.initialSize = CompositeFactory.createText(this, composite, "label.tablespace.initial.size", 1, NUM_TEXT_WIDTH, false);
        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.size.example");
        this.engine = MySQLAdvancedComposite.createEngineCombo(composite, this);
    }

    @Override
    protected TablespaceProperties setTablespaceProperties() {
        final MySQLTablespaceProperties properties = new MySQLTablespaceProperties();

        properties.setDataFile(dataFile.getText().trim());
        properties.setLogFileGroup(logFileGroup.getText().trim());
        properties.setExtentSize(extentSize.getText().trim());
        properties.setInitialSize(initialSize.getText().trim());
        properties.setEngine(engine.getText().trim());

        return properties;
    }

    @Override
    protected void setData(TablespaceProperties tablespaceProperties) {
        if (tablespaceProperties instanceof MySQLTablespaceProperties) {
            final MySQLTablespaceProperties properties = (MySQLTablespaceProperties) tablespaceProperties;

            dataFile.setText(Format.toString(properties.getDataFile()));
            logFileGroup.setText(Format.toString(properties.getLogFileGroup()));
            extentSize.setText(Format.toString(properties.getExtentSize()));
            initialSize.setText(Format.toString(properties.getInitialSize()));
            engine.setText(Format.toString(properties.getEngine()));
        }
    }

    @Override
    protected String doValidate() {
        final String errorMessage = super.doValidate();
        if (errorMessage != null) {
            return errorMessage;
        }

        String text = dataFile.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.data.file.empty";
        }

        text = logFileGroup.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.log.file.group.empty";
        }

        text = initialSize.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.initial.size.empty";
        }

        text = engine.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.storage.engine.empty";
        }

        return null;
    }
}
