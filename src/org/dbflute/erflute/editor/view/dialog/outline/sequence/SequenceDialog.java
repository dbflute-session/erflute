package org.dbflute.erflute.editor.view.dialog.outline.sequence;

import java.math.BigDecimal;

import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.db.impl.db2.DB2DBManager;
import org.dbflute.erflute.db.impl.h2.H2DBManager;
import org.dbflute.erflute.db.impl.hsqldb.HSQLDBDBManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SequenceDialog extends AbstractDialog {

    private static final int TEXT_SIZE = 200;

    private Text nameText;
    private Text schemaText;
    private Text incrementText;
    private Text minValueText;
    private Text maxValueText;
    private Text startText;
    private Text cacheText;
    private Button cycleCheckBox;
    private Button orderCheckBox;
    private Text descriptionText;
    private Combo dataTypeCombo;
    private Text decimalSizeText;
    private final Sequence sequence;
    private Sequence result;
    private final ERDiagram diagram;

    public SequenceDialog(Shell parentShell, Sequence sequence, ERDiagram diagram) {
        super(parentShell, 5);

        this.sequence = sequence;
        this.diagram = diagram;
    }

    @Override
    protected void initComponent(Composite composite) {
        this.nameText = CompositeFactory.createText(this, composite, "label.sequence.name", 4, false);
        this.schemaText = CompositeFactory.createText(this, composite, "label.schema", 4, false);

        if (DB2DBManager.ID.equals(diagram.getDatabase()) || HSQLDBDBManager.ID.equals(diagram.getDatabase())) {
            this.dataTypeCombo = CompositeFactory.createReadOnlyCombo(this, composite, "Data Type", 1, TEXT_SIZE);
            dataTypeCombo.add("BIGINT");
            dataTypeCombo.add("INTEGER");

            if (DB2DBManager.ID.equals(diagram.getDatabase())) {
                dataTypeCombo.add("SMALLINT");
                dataTypeCombo.add("DECIMAL(p)");

                this.decimalSizeText = CompositeFactory.createNumText(this, composite, "Size", 30);
                decimalSizeText.setEnabled(false);
            } else {
                CompositeFactory.filler(composite, 2);
            }

            CompositeFactory.filler(composite, 1);
        }

        this.incrementText = CompositeFactory.createNumText(this, composite, "Increment", TEXT_SIZE);
        CompositeFactory.filler(composite, 3);

        this.startText = CompositeFactory.createNumText(this, composite, "Start", TEXT_SIZE);
        CompositeFactory.filler(composite, 3);

        this.minValueText = CompositeFactory.createNumText(this, composite, "MinValue", TEXT_SIZE);
        CompositeFactory.filler(composite, 3);

        this.maxValueText = CompositeFactory.createNumText(this, composite, "MaxValue", TEXT_SIZE);
        CompositeFactory.filler(composite, 3);

        if (!H2DBManager.ID.equals(diagram.getDatabase())) {
            this.startText = CompositeFactory.createNumText(this, composite, "Start", 4, TEXT_SIZE);
            this.minValueText = CompositeFactory.createNumText(this, composite, "MinValue", 4, TEXT_SIZE);
            this.maxValueText = CompositeFactory.createNumText(this, composite, "MaxValue", 4, TEXT_SIZE);
        }

        if (!HSQLDBDBManager.ID.equals(diagram.getDatabase())) {
            this.cacheText = CompositeFactory.createNumText(this, composite, "Cache", TEXT_SIZE);
            CompositeFactory.filler(composite, 3);
        }

        this.cycleCheckBox = CompositeFactory.createCheckbox(this, composite, "Cycle", 2);
        CompositeFactory.filler(composite, 3);

        if (DB2DBManager.ID.equals(diagram.getDatabase())) {
            this.orderCheckBox = CompositeFactory.createCheckbox(this, composite, "Order", 2);
            CompositeFactory.filler(composite, 3);
        }

        this.descriptionText =
                CompositeFactory.createTextArea(this, composite, "label.description", DesignResources.DESCRIPTION_WIDTH, 100, 4, true);
    }

    @Override
    protected String doValidate() {
        if (!DBManagerFactory.getDBManager(diagram).isSupported(DBManager.SUPPORT_SEQUENCE)) {
            return "error.sequence.not.supported";
        }

        String text = nameText.getText().trim();
        if (text.equals("")) {
            return "error.sequence.name.empty";
        }

        if (!Check.isAlphabet(text)) {
            if (diagram.getDiagramContents().getSettings().isValidatePhysicalName()) {
                return "error.sequence.name.not.alphabet";
            }
        }

        text = schemaText.getText();

        if (!Check.isAlphabet(text)) {
            return "error.schema.not.alphabet";
        }

        text = incrementText.getText();
        if (!text.equals("")) {
            try {
                Integer.parseInt(text);
            } catch (final NumberFormatException e) {
                return "error.sequence.increment.degit";
            }
        }

        if (minValueText != null) {
            text = minValueText.getText();
            if (!text.equals("")) {
                try {
                    Long.parseLong(text);
                } catch (final NumberFormatException e) {
                    return "error.sequence.minValue.degit";
                }
            }
        }

        if (maxValueText != null) {
            text = maxValueText.getText();
            if (!text.equals("")) {
                try {
                    new BigDecimal(text);
                } catch (final NumberFormatException e) {
                    return "error.sequence.maxValue.degit";
                }
            }
        }

        text = startText.getText();
        if (!text.equals("")) {
            try {
                Long.parseLong(text);
            } catch (final NumberFormatException e) {
                return "error.sequence.start.degit";
            }
        }

        if (cacheText != null) {
            text = cacheText.getText();
            if (!text.equals("")) {
                try {
                    final int cache = Integer.parseInt(text);
                    if (DB2DBManager.ID.equals(this.diagram.getDatabase())) {
                        if (cache < 2) {
                            return "error.sequence.cache.min2";
                        }
                    } else {
                        if (cache < 1) {
                            return "error.sequence.cache.min1";
                        }
                    }
                } catch (final NumberFormatException e) {
                    return "error.sequence.cache.degit";
                }
            }
        }

        if (decimalSizeText != null) {
            text = decimalSizeText.getText();
            if (!text.equals("")) {
                try {
                    final int size = Integer.parseInt(text);
                    if (size < 0) {
                        return "error.sequence.size.zero";
                    }
                } catch (final NumberFormatException e) {
                    return "error.sequence.size.degit";
                }
            }
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.sequence";
    }

    @Override
    protected void performOK() throws InputException {
        this.result = new Sequence();
        this.result.setName(nameText.getText().trim());
        this.result.setSchema(schemaText.getText().trim());

        Integer increment = null;
        Long minValue = null;
        BigDecimal maxValue = null;
        Long start = null;
        Integer cache = null;

        String text = incrementText.getText();
        if (!text.equals("")) {
            increment = Integer.valueOf(text);
        }

        if (minValueText != null) {
            text = minValueText.getText();
            if (!text.equals("")) {
                minValue = Long.valueOf(text);
            }
        }

        if (maxValueText != null) {
            text = maxValueText.getText();
            if (!text.equals("")) {
                maxValue = new BigDecimal(text);
            }
        }

        text = startText.getText();
        if (!text.equals("")) {
            start = Long.valueOf(text);
        }

        if (cacheText != null) {
            text = cacheText.getText();
            if (!text.equals("")) {
                cache = Integer.valueOf(text);
            }
        }

        result.setIncrement(increment);
        result.setMinValue(minValue);
        result.setMaxValue(maxValue);
        result.setStart(start);
        result.setCache(cache);

        if (cycleCheckBox != null) {
            result.setCycle(cycleCheckBox.getSelection());
        }

        if (orderCheckBox != null) {
            result.setOrder(orderCheckBox.getSelection());
        }

        result.setDescription(descriptionText.getText().trim());

        if (dataTypeCombo != null) {
            result.setDataType(dataTypeCombo.getText());
            int decimalSize = 0;
            try {
                decimalSize = Integer.parseInt(decimalSizeText.getText().trim());
            } catch (final NumberFormatException e) {}
            result.setDecimalSize(decimalSize);
        }
    }

    @Override
    protected void setupData() {
        if (sequence != null) {
            nameText.setText(Format.toString(sequence.getName()));
            schemaText.setText(Format.toString(sequence.getSchema()));
            incrementText.setText(Format.toString(sequence.getIncrement()));
            if (minValueText != null) {
                minValueText.setText(Format.toString(sequence.getMinValue()));
            }
            if (maxValueText != null) {
                maxValueText.setText(Format.toString(sequence.getMaxValue()));
            }
            startText.setText(Format.toString(sequence.getStart()));
            if (cacheText != null) {
                cacheText.setText(Format.toString(sequence.getCache()));
            }
            if (cycleCheckBox != null) {
                cycleCheckBox.setSelection(sequence.isCycle());
            }
            if (orderCheckBox != null) {
                orderCheckBox.setSelection(sequence.isOrder());
            }

            descriptionText.setText(Format.toString(sequence.getDescription()));

            if (dataTypeCombo != null) {
                final String dataType = Format.toString(sequence.getDataType());
                dataTypeCombo.setText(dataType);
                if (dataType.equals("DECIMAL(p)") && decimalSizeText != null) {
                    decimalSizeText.setEnabled(true);
                    decimalSizeText.setText(Format.toString(sequence.getDecimalSize()));
                }
            }
        }
    }

    public Sequence getResult() {
        return result;
    }

    @Override
    protected void addListener() {
        super.addListener();

        if (dataTypeCombo != null && decimalSizeText != null) {
            dataTypeCombo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    final String dataType = dataTypeCombo.getText();

                    if (dataType.equals("DECIMAL(p)")) {
                        decimalSizeText.setEnabled(true);

                    } else {
                        decimalSizeText.setEnabled(false);
                    }
                }
            });
        }
    }
}
