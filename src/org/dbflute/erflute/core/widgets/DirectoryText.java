package org.dbflute.erflute.core.widgets;

import org.dbflute.erflute.Activator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * #analyzed ファイルシステム全体としてのディレクトリ入力テキスト、誰からも呼ばれていない？
 * @author ermaster
 * @author jflute
 */
public class DirectoryText {

    private final Text text;
    private final Button openBrowseButton;

    public DirectoryText(Composite parent, int style) {
        this.text = new Text(parent, style);

        openBrowseButton = new Button(parent, SWT.NONE);
        openBrowseButton.setText(JFaceResources.getString("openBrowse"));

        openBrowseButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final String filePath = Activator.showDirectoryDialog(text.getText());
                text.setText(filePath);
            }
        });
    }

    public void setLayoutData(Object layoutData) {
        text.setLayoutData(layoutData);
    }

    public void setText(String text) {
        this.text.setText(text);
        this.text.setSelection(text.length());
    }

    public boolean isBlank() {
        if (text.getText().trim().length() == 0) {
            return true;
        }

        return false;
    }

    public String getFilePath() {
        return text.getText().trim();
    }

    public void addModifyListener(ModifyListener listener) {
        text.addModifyListener(listener);
    }
}
