package org.dbflute.erflute.core.dialog;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.misc.ResourceAndContainerGroup;

/**
 * @author modified by jflute (originated in ermaster)
 */
@SuppressWarnings("restriction")
public class InternalFileDialog extends TitleAreaDialog implements Listener {

    private ResourceAndContainerGroup resourceGroup;
    private IPath fullPath;
    private final String initialFolder;
    private final String fileExtension;

    public InternalFileDialog(Shell parentShell, String initialFolder, String fileExtension) {
        super(parentShell);
        this.initialFolder = initialFolder;
        this.fileExtension = fileExtension;
    }

    @Override
    protected Control createContents(Composite parent) {
        return super.createContents(parent);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite topLevel = new Composite(parent, SWT.NONE);
        topLevel.setLayout(new GridLayout());
        topLevel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        topLevel.setFont(parent.getFont());
        resourceGroup = new ResourceAndContainerGroup(topLevel, this, "File name:",
                IDEWorkbenchMessages.WizardNewFileCreationPage_file, false, 250);
        resourceGroup.setResourceExtension(fileExtension);
        resourceGroup.setContainerFullPath(new Path(initialFolder).removeLastSegments(1));
        if (new Path(initialFolder).lastSegment() != null) {
            resourceGroup.setResource(new Path(initialFolder).lastSegment());
            resourceGroup.setFocus();
        }
        setTitle("File");
        return super.createDialogArea(parent);
    }

    @Override
    public void handleEvent(Event event) {
    }

    @Override
    protected void okPressed() {
        if (resourceGroup.getContainerFullPath() == null) {
            setErrorMessage("Select output file location");
        } else {
            fullPath = resourceGroup.getContainerFullPath().append(resourceGroup.getResource());
            super.okPressed();
        }
    }

    public IPath getResourcePath() {
        return fullPath;
    }
}
