package org.insightech.er.editor.model.dbexport.image;

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
     * exception ���擾���܂�.
     * 
     * @return exception
     */
    public Exception getException() {
        return exception;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

        monitor.beginTask(DisplayMessages.getMessage("dialog.message.export.image"), img.getBounds().width * img.getBounds().height);

        try {
            this.monitor = monitor;
            doProcess();

        } catch (InterruptedException e) {
            throw e;

        } catch (Exception e) {
            this.exception = e;
        }

        monitor.done();
    }

    @Override
    protected void doPostTask() throws InterruptedException {
        this.monitor.worked(1);

        if (this.monitor.isCanceled()) {
            throw new InterruptedException("Cancel has been requested.");
        }
    }

}
