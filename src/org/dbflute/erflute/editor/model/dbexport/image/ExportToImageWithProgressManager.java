package org.dbflute.erflute.editor.model.dbexport.image;

import java.lang.reflect.InvocationTargetException;

import org.dbflute.erflute.core.DisplayMessages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.Image;

public class ExportToImageWithProgressManager extends ExportToImageManager implements IRunnableWithProgress {

    private Exception exception;
    private IProgressMonitor monitor;

    public ExportToImageWithProgressManager(Image img, int format, String saveFilePath) {
        super(img, format, saveFilePath);
    }

    /**
     * exception を取得します。
     * @return exception
     */
    public Exception getException() {
        return exception;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

        monitor.beginTask(DisplayMessages.getMessage("dialog.message.export.image"), img.getBounds().width * img.getBounds().height);

        try {
            this.monitor = monitor;
            doProcess();

        } catch (final InterruptedException e) {
            throw e;

        } catch (final Exception e) {
            this.exception = e;
        }

        monitor.done();
    }

    @Override
    protected void doPostTask() throws InterruptedException {
        monitor.worked(1);
        if (monitor.isCanceled()) {
            throw new InterruptedException("Cancel has been requested.");
        }
    }
}
