package org.dbflute.erflute.editor.view.dialog.printer;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.PageSettings;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class PageSettingDialog extends AbstractDialog {

    private PageSettings pageSetting;
    private Button vButton;
    private Button hButton;
    private Spinner scaleSpinner;
    private Combo sizeCombo;
    private Spinner topMarginSpinner;
    private Spinner rightMarginSpinner;
    private Spinner bottomMarginSpinner;
    private Spinner leftMarginSpinner;
    private final ERDiagram diagram;

    public PageSettingDialog(Shell parentShell, ERDiagram diagram) {
        super(parentShell, 1);

        this.pageSetting = diagram.getPageSetting();
        this.diagram = diagram;
    }

    @Override
    protected void createErrorComposite(Composite parent) {
    }

    @Override
    protected void initComponent(Composite parent) {
        parent.setBackground(ColorConstants.white);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        parent.setLayout(layout);

        initDirectionGroup(parent);
        initScaleGroup(parent);
        initSizeGroup(parent);
    }

    private void initDirectionGroup(Composite parent) {
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;

        final Group directionGroup = new Group(parent, SWT.NONE);
        directionGroup.setLayoutData(gridData);
        directionGroup.setBackground(ColorConstants.white);
        directionGroup.setText(DisplayMessages.getMessage("label.page.direction"));

        final GridLayout directionGroupLayout = new GridLayout();
        directionGroupLayout.marginWidth = 20;
        directionGroupLayout.horizontalSpacing = 20;
        directionGroupLayout.numColumns = 4;

        directionGroup.setLayout(directionGroupLayout);

        final Label vImage = new Label(directionGroup, SWT.NONE);
        vImage.setImage(Activator.getImage(ImageKey.PAGE_SETTING_V));

        vButton = new Button(directionGroup, SWT.RADIO);
        vButton.setBackground(ColorConstants.white);
        vButton.setText(DisplayMessages.getMessage("label.page.direction.v"));

        final Label hImage = new Label(directionGroup, SWT.NONE);
        hImage.setImage(Activator.getImage(ImageKey.PAGE_SETTING_H));

        hButton = new Button(directionGroup, SWT.RADIO);
        hButton.setBackground(ColorConstants.white);
        hButton.setText(DisplayMessages.getMessage("label.page.direction.h"));
    }

    private void initScaleGroup(Composite parent) {
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;

        final Group scaleGroup = new Group(parent, SWT.NONE);
        scaleGroup.setLayoutData(gridData);
        scaleGroup.setBackground(ColorConstants.white);
        scaleGroup.setText(DisplayMessages.getMessage("label.page.scale.printing"));

        final GridLayout scaleGroupLayout = new GridLayout();
        scaleGroupLayout.marginWidth = 20;
        scaleGroupLayout.horizontalSpacing = 20;
        scaleGroupLayout.numColumns = 3;
        scaleGroup.setLayout(scaleGroupLayout);

        Label label = new Label(scaleGroup, SWT.NONE);
        label.setBackground(ColorConstants.white);
        label.setText(DisplayMessages.getMessage("label.page.scale"));

        scaleSpinner = new Spinner(scaleGroup, SWT.BORDER);
        scaleSpinner.setIncrement(5);
        scaleSpinner.setMinimum(10);
        scaleSpinner.setMaximum(400);
        scaleSpinner.setSelection(100);

        label = new Label(scaleGroup, SWT.NONE);
        label.setBackground(ColorConstants.white);
        label.setText("%");
    }

    private void initSizeGroup(Composite parent) {
        final GridData sizeGroupGridData = new GridData();
        sizeGroupGridData.grabExcessHorizontalSpace = true;
        sizeGroupGridData.horizontalAlignment = GridData.FILL;

        final Group sizeGroup = new Group(parent, SWT.NONE);
        sizeGroup.setLayoutData(sizeGroupGridData);
        sizeGroup.setBackground(ColorConstants.white);

        final GridLayout sizeGroupLayout = new GridLayout();
        sizeGroupLayout.marginWidth = 20;
        sizeGroupLayout.horizontalSpacing = 20;
        sizeGroupLayout.numColumns = 2;
        sizeGroup.setLayout(sizeGroupLayout);

        Label label = new Label(sizeGroup, SWT.NONE);
        label.setBackground(ColorConstants.white);
        label.setText(DisplayMessages.getMessage("label.page.size"));

        sizeCombo = new Combo(sizeGroup, SWT.READ_ONLY | SWT.BORDER);
        sizeCombo.setBackground(ColorConstants.white);
        setPaperSize(sizeCombo);

        label = new Label(sizeGroup, SWT.NONE);
        label.setBackground(ColorConstants.white);
        label.setText(DisplayMessages.getMessage("label.page.margin"));

        final Composite marginComposite = new Composite(sizeGroup, SWT.NONE);
        marginComposite.setBackground(ColorConstants.white);

        final GridLayout marginCompositeLayout = new GridLayout();
        marginCompositeLayout.marginWidth = 10;
        marginCompositeLayout.horizontalSpacing = 10;
        marginCompositeLayout.numColumns = 6;
        marginComposite.setLayout(marginCompositeLayout);

        label = new Label(marginComposite, SWT.NONE);
        label = new Label(marginComposite, SWT.NONE);

        label = new Label(marginComposite, SWT.NONE);
        label.setBackground(ColorConstants.white);
        label.setText(DisplayMessages.getMessage("label.page.margin.top"));

        topMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
        setMarginSpinner(topMarginSpinner);

        label = new Label(marginComposite, SWT.NONE);
        label = new Label(marginComposite, SWT.NONE);

        label = new Label(marginComposite, SWT.NONE);
        label.setBackground(ColorConstants.white);
        label.setText(DisplayMessages.getMessage("label.page.margin.left"));

        leftMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
        setMarginSpinner(leftMarginSpinner);

        label = new Label(marginComposite, SWT.NONE);
        label = new Label(marginComposite, SWT.NONE);

        label = new Label(marginComposite, SWT.NONE);
        label.setBackground(ColorConstants.white);
        label.setText(DisplayMessages.getMessage("label.page.margin.right"));

        rightMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
        setMarginSpinner(rightMarginSpinner);

        label = new Label(marginComposite, SWT.NONE);
        label = new Label(marginComposite, SWT.NONE);

        label = new Label(marginComposite, SWT.NONE);
        label.setBackground(ColorConstants.white);
        label.setText(DisplayMessages.getMessage("label.page.margin.bottom"));

        bottomMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
        setMarginSpinner(bottomMarginSpinner);

        label = new Label(marginComposite, SWT.NONE);
        label = new Label(marginComposite, SWT.NONE);
    }

    private void setMarginSpinner(Spinner spinner) {
        spinner.setDigits(1);
        spinner.setIncrement(5);
        spinner.setMinimum(0);
        spinner.setMaximum(1000);
        spinner.setSelection(20);
    }

    private void setPaperSize(Combo combo) {
        for (final String paperSize : PageSettings.getAllPaperSize()) {
            combo.add(paperSize);
        }

        combo.select(0);
    }

    @Override
    protected String getTitle() {
        return "dialog.title.page.setting";
    }

    @Override
    protected void performOK() throws InputException {
        this.pageSetting =
                new PageSettings(hButton.getSelection(), scaleSpinner.getSelection(), sizeCombo.getText(),
                        topMarginSpinner.getSelection(), rightMarginSpinner.getSelection(),
                        bottomMarginSpinner.getSelection(), leftMarginSpinner.getSelection());
        diagram.setPageSetting(pageSetting);
    }

    @Override
    protected void setupData() {
        if (pageSetting.isDirectionHorizontal()) {
            hButton.setSelection(true);
        } else {
            vButton.setSelection(true);
        }

        scaleSpinner.setSelection(pageSetting.getScale());
        sizeCombo.setText(pageSetting.getPaperSize());
        topMarginSpinner.setSelection(pageSetting.getTopMargin());
        rightMarginSpinner.setSelection(pageSetting.getRightMargin());
        bottomMarginSpinner.setSelection(pageSetting.getBottomMargin());

        leftMarginSpinner.setSelection(pageSetting.getLeftMargin());
    }

    @Override
    protected String doValidate() {
        return null;
    }
}
