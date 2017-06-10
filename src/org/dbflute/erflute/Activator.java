package org.dbflute.erflute; 

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.core.dialog.InternalDirectoryDialog;
import org.dbflute.erflute.core.dialog.InternalFileDialog;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.dbexport.ExportToImageAction;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @author modified by jflute (originated in ermaster)
 */
public class Activator extends AbstractUIPlugin {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PLUGIN_ID = "org.dbflute.erflute";
    private static Activator plugin; // shared instance

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public Activator() {
        plugin = this;
    }

    // ===================================================================================
    //                                                                             Default
    //                                                                             =======
    public static Activator getDefault() { // shared instance
        return plugin;
    }

    // ===================================================================================
    //                                                                                Stop
    //                                                                                ====
    @Override
    public void stop(BundleContext context) throws Exception {
        DesignResources.PINK.dispose();
        DesignResources.ADDED_COLOR.dispose();
        DesignResources.UPDATED_COLOR.dispose();
        DesignResources.REMOVED_COLOR.dispose();
        DesignResources.GRID_COLOR.dispose();
        DesignResources.DEFAULT_TABLE_COLOR.dispose();
        DesignResources.SELECTED_REFERENCED_COLUMN.dispose();
        DesignResources.SELECTED_FOREIGNKEY_COLUMN.dispose();
        DesignResources.SELECTED_REFERENCED_AND_FOREIGNKEY_COLUMN.dispose();
        DesignResources.VERY_LIGHT_GRAY.dispose();
        DesignResources.LINE_COLOR.dispose();

        DesignResources.TEST_COLOR.dispose();
        DesignResources.NOT_NULL_COLOR.dispose();
        DesignResources.PRIMARY_COLOR.dispose();
        DesignResources.FOREIGN_COLOR.dispose();

        DesignResources.disposeColorMap();

        plugin = null;
        super.stop(context);
    }

    // ===================================================================================
    //                                                                              Dialog
    //                                                                              ======
    public static void showExceptionDialog(Throwable e) {
        showExceptionDialog("(no message)", e);
    }

    public static void showExceptionDialog(String msg, Throwable e) {
        final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, e.toString(), e);
        Activator.error(e);
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final String dialogTitle = DisplayMessages.getMessage("dialog.title.error");
        final String message = msg + "\n" + buildUserExceptionMessage(e);
        ErrorDialog.openError(shell, dialogTitle, message, status);
    }

    public static String buildUserExceptionMessage(Throwable e) {
        final StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getName());
        sb.append(": ").append(e.getMessage());
        final StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            sb.append(toStackTraceRow(stackTrace[0]));
            if (stackTrace.length > 1) {
                sb.append(toStackTraceRow(stackTrace[1]));
                if (stackTrace.length > 2) {
                    sb.append(toStackTraceRow(stackTrace[2]));
                }
            }
        }
        return sb.toString();
    }

    private static String toStackTraceRow(StackTraceElement first) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n\tat ").append(first.getClassName()).append(".").append(first.getMethodName());
        sb.append("(").append(first.getFileName()).append(":").append(first.getLineNumber()).append(")");
        return sb.toString();
    }

    public static void showErrorDialog(String message) {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
        messageBox.setText(DisplayMessages.getMessage("dialog.title.error"));
        messageBox.setMessage(DisplayMessages.getMessage(message));
        messageBox.open();
    }

    public static void showMessageDialog(String message) {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
        messageBox.setText(DisplayMessages.getMessage("dialog.title.information"));
        messageBox.setMessage(DisplayMessages.getMessage(Format.null2blank(message)));
        messageBox.open();
    }

    public static boolean showConfirmDialog(String message) {
        return showConfirmDialog(message, SWT.OK, SWT.CANCEL);
    }

    public static boolean showConfirmDialog(String message, int ok, int cancel) {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | ok | cancel);
        messageBox.setText(DisplayMessages.getMessage("dialog.title.confirm"));
        messageBox.setMessage(DisplayMessages.getMessage(message));
        final int result = messageBox.open();
        return result == ok;
    }

    public static String showSaveDialog(String filePath, String[] filterExtensions) {
        String dir = null;
        String fileName = null;
        if (filePath != null && !"".equals(filePath.trim())) {
            final File file = new File(filePath.trim());
            dir = file.getParent();
            fileName = file.getName();
        }
        final FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
        fileDialog.setFilterPath(dir);
        fileDialog.setFileName(fileName);
        fileDialog.setFilterExtensions(filterExtensions);
        return fileDialog.open();
    }

    /**
     * #analyzed ファイルの保存ダイアログを、workspace内部領域として表示する。e.g. DDLの出力先ファイルなど <br>
     * @param defaultFilePath デフォルトのファイルパス (NotNull)
     * @param filterExtensions ファイルの拡張子の配列 (NotNull)
     * @return ファイルパスの文字列表現 (NullAllowed: OK状態でなければ)
     */
    public static String showSaveDialogInternal(String defaultFilePath, String[] filterExtensions) {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final InternalFileDialog fileDialog = new InternalFileDialog(shell, defaultFilePath, filterExtensions[0].substring(1));
        if (fileDialog.open() == Window.OK) {
            final IPath path = fileDialog.getResourcePath();
            return path.toString();
        }
        return null;
    }

    /**
     * #analyzed ディレクトリ指定ダイアログを、workspace内部領域として表示する <br>
     * @param filePath デフォルトのディレクトリパス (NotNull)
     * @return ファイルパスの文字列表現 (NullAllowed: OK状態でなければ)
     */
    public static String showDirectoryDialogInternal(String filePath) {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final InternalDirectoryDialog fileDialog = new InternalDirectoryDialog(shell, filePath);
        if (fileDialog.open() == Window.OK) {
            final IPath path = fileDialog.getResourcePath();
            return path.toString();
        }
        return null;
    }

    public static String showDirectoryDialog(String filePath) {
        String fileName = null;
        if (filePath != null && !"".equals(filePath.trim())) {
            final File file = new File(filePath.trim());
            fileName = file.getPath();
        }
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final DirectoryDialog dialog = new DirectoryDialog(shell, SWT.NONE);
        dialog.setFilterPath(fileName);
        return dialog.open();
    }

    // ===================================================================================
    //                                                                             Logging
    //                                                                             =======
    public static void warn(Object caller, String methodName, String msg) {
        if (isWarnEnabled()) {
            doLog("WARN", caller, methodName, msg);
        }
    }

    public static void debug(Object caller, String methodName, String msg) {
        if (isDebugEnabled()) {
            doLog("DEBUG", caller, methodName, msg);
        }
    }

    public static void trace(Object caller, String methodName, String msg) {
        if (isTraceEnabled()) {
            doLog("TRACE", caller, methodName, msg);
        }
    }

    private static void doLog(String logLevel, Object caller, String methodName, String msg) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        final String currentDate = format.format(new Date());
        final String className = caller.getClass().getSimpleName();
        System.out.println(currentDate + " " + logLevel + " (" + className + "@" + methodName + ") - " + msg);
    }

    private static boolean isWarnEnabled() {
        return true;
    }

    private static boolean isDebugEnabled() {
        return true; // change if debug
    }

    private static boolean isTraceEnabled() {
        return false; // change if trace
    }

    public static void error(Throwable e) {
        e.printStackTrace();
        final Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, e.getMessage(), e);
        Activator.getDefault().getLog().log(status);
    }

    // ===================================================================================
    //                                                                              Viewer
    //                                                                              ======
    // nobody calls
    //public static GraphicalViewer createGraphicalViewer(final ERDiagram diagram) {
    //    Display display = PlatformUI.getWorkbench().getDisplay();
    //    GraphicalViewerCreator runnable = new GraphicalViewerCreator(display, diagram);
    //    display.syncExec(runnable);
    //    return runnable.viewer;
    //}
    //private static class GraphicalViewerCreator implements Runnable {
    //
    //    private final Display display;
    //    private final ERDiagram diagram;
    //    private GraphicalViewer viewer;
    //
    //    private GraphicalViewerCreator(Display display, ERDiagram diagram) {
    //        this.display = display;
    //        this.diagram = diagram;
    //    }
    //
    //    @Override
    //    public void run() {
    //        final Shell shell = new Shell(display);
    //        shell.setLayout(new GridLayout(1, false));
    //
    //        final ERDiagramEditPartFactory editPartFactory = new ERDiagramEditPartFactory();
    //        viewer = new ScrollingGraphicalViewer();
    //        viewer.setControl(new FigureCanvas(shell));
    //        final ScalableFreeformRootEditPart rootEditPart = new PagableFreeformRootEditPart(diagram);
    //        viewer.setRootEditPart(rootEditPart);
    //
    //        viewer.setEditPartFactory(editPartFactory);
    //        viewer.setContents(diagram);
    //    }
    //}

    // ===================================================================================
    //                                                                               Image
    //                                                                               =====
    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);

        reg.put(ImageKey.ALIGN_BOTTOM, loadImageDescriptor("icons/alignbottom.gif"));
        reg.put(ImageKey.ALIGN_CENTER, loadImageDescriptor("icons/aligncenter.gif"));
        reg.put(ImageKey.ALIGN_LEFT, loadImageDescriptor("icons/alignleft.gif"));
        reg.put(ImageKey.ALIGN_MIDDLE, loadImageDescriptor("icons/alignmid.gif"));
        reg.put(ImageKey.ALIGN_RIGHT, loadImageDescriptor("icons/alignright.gif"));
        reg.put(ImageKey.ALIGN_TOP, loadImageDescriptor("icons/aligntop.gif"));
        reg.put(ImageKey.ARROW, loadImageDescriptor("icons/arrow16.gif"));
        reg.put(ImageKey.BLANK_WHITE, loadImageDescriptor("icons/blank_white.gif"));
        reg.put(ImageKey.CATEGORY, loadImageDescriptor("icons/category.gif"));
        reg.put(ImageKey.CHANGE_BACKGROUND_COLOR, loadImageDescriptor("icons/color.gif"));
        reg.put(ImageKey.CHANGE_BACKGROUND_COLOR_DISABLED, loadImageDescriptor("icons/square.gif"));
        reg.put(ImageKey.CHECK, loadImageDescriptor("icons/tick.png"));
        reg.put(ImageKey.CHECK_GREY, loadImageDescriptor("icons/tick_grey.png"));
        reg.put(ImageKey.COMMENT_CONNECTION, loadImageDescriptor("icons/comment_connection.gif"));
        reg.put(ImageKey.DATABASE, loadImageDescriptor("icons/database2.png"));
        reg.put(ImageKey.DATABASE_CONNECT, loadImageDescriptor("icons/database_connect.png"));
        reg.put(ImageKey.DICTIONARY, loadImageDescriptor("icons/dictionary.gif"));
        reg.put(ImageKey.DICTIONARY_OPEN, loadImageDescriptor("icons/dictionary_open.gif"));
        reg.put(ImageKey.EDIT, loadImageDescriptor("icons/pencil.png"));
        reg.put(ImageKey.ERROR, loadImageDescriptor("icons/error.gif"));
        reg.put(ImageKey.EXPORT_DDL, loadImageDescriptor("icons/document-attribute-d.png"));
        reg.put(ImageKey.EXPORT_TO_CSV, loadImageDescriptor("icons/document-excel-csv.png"));
        reg.put(ImageKey.EXPORT_TO_DB, loadImageDescriptor("icons/database_connect.png"));
        reg.put(ImageKey.EXPORT_TO_EXCEL, loadImageDescriptor("icons/document-excel.png"));
        reg.put(ImageKey.EXPORT_TO_HTML, loadImageDescriptor("icons/document-globe.png"));
        reg.put(ImageKey.EXPORT_TO_IMAGE, loadImageDescriptor("icons/document-image.png"));
        reg.put(ImageKey.EXPORT_TO_JAVA, loadImageDescriptor("icons/page_white_cup.png"));
        reg.put(ImageKey.EXPORT_TO_TEST_DATA, loadImageDescriptor("icons/tables--arrow.png"));
        reg.put(ImageKey.FIND, loadImageDescriptor("icons/binocular.png"));
        reg.put(ImageKey.FOREIGN_KEY, loadImageDescriptor("icons/foreign_key.gif"));
        reg.put(ImageKey.NON_NULL, loadImageDescriptor("icons/non_null.gif"));
        reg.put(ImageKey.GRID, loadImageDescriptor("icons/grid.png"));
        reg.put(ImageKey.GRID_SNAP, loadImageDescriptor("icons/grid-snap.png"));
        reg.put(ImageKey.GROUP, loadImageDescriptor("icons/group.gif"));
        reg.put(ImageKey.HORIZONTAL_LINE, loadImageDescriptor("icons/horizontal_line.gif"));
        reg.put(ImageKey.HORIZONTAL_LINE_DISABLED, loadImageDescriptor("icons/horizontal_line_disabled.gif"));
        reg.put(ImageKey.IMAGE, loadImageDescriptor("icons/image--plus.png"));
        reg.put(ImageKey.INDEX, loadImageDescriptor("icons/index.gif"));
        reg.put(ImageKey.LOCK_EDIT, loadImageDescriptor("icons/lock--pencil.png"));
        reg.put(ImageKey.MATCH_HEIGHT, loadImageDescriptor("icons/matchheight.gif"));
        reg.put(ImageKey.MATCH_WIDTH, loadImageDescriptor("icons/matchwidth.gif"));
        reg.put(ImageKey.NOTE, loadImageDescriptor("icons/note.gif"));
        reg.put(ImageKey.OPTION, loadImageDescriptor("icons/wrench.png"));
        reg.put(ImageKey.PAGE_SETTING_H, loadImageDescriptor("images/h.png"));
        reg.put(ImageKey.PAGE_SETTING_V, loadImageDescriptor("images/v.png"));
        reg.put(ImageKey.PALETTE, loadImageDescriptor("icons/palette.png"));
        reg.put(ImageKey.PRIMARY_KEY, loadImageDescriptor("icons/pkey.gif"));
        reg.put(ImageKey.RELATION_1_N, loadImageDescriptor("icons/relation_1_n.gif"));
        reg.put(ImageKey.RELATION_N_N, loadImageDescriptor("icons/relation_n_n.gif"));
        reg.put(ImageKey.RELATION_SELF, loadImageDescriptor("icons/relation_self.gif"));
        reg.put(ImageKey.RESIZE, loadImageDescriptor("icons/application-resize-actual.png"));
        reg.put(ImageKey.SEQUENCE, loadImageDescriptor("icons/sequence.gif"));
        reg.put(ImageKey.TITLEBAR_BACKGROUND, loadImageDescriptor("images/aqua-bg.gif"));
        reg.put(ImageKey.TABLE, loadImageDescriptor("icons/table.gif"));
        reg.put(ImageKey.TABLE_NEW, loadImageDescriptor("icons/table_new.gif"));
        reg.put(ImageKey.TABLESPACE, loadImageDescriptor("icons/database.png"));
        reg.put(ImageKey.TEST_DATA, loadImageDescriptor("icons/tables--pencil.png"));
        reg.put(ImageKey.TOOLTIP, loadImageDescriptor("icons/ui-tooltip.png"));
        reg.put(ImageKey.MAIN_COLUMN, loadImageDescriptor("icons/ui-main-column.png"));
        reg.put(ImageKey.TRIGGER, loadImageDescriptor("icons/arrow-turn-000-left.png"));
        reg.put(ImageKey.VERTICAL_LINE, loadImageDescriptor("icons/vertical_line.gif"));
        reg.put(ImageKey.VERTICAL_LINE_DISABLED, loadImageDescriptor("icons/vertical_line_disabled.gif"));
        reg.put(ImageKey.VIEW, loadImageDescriptor("icons/view.gif"));
        reg.put(ImageKey.WORD, loadImageDescriptor("icons/word_3.gif"));
        reg.put(ImageKey.ZOOM_IN, loadImageDescriptor("icons/magnifier-zoom.png"));
        reg.put(ImageKey.ZOOM_OUT, loadImageDescriptor("icons/magnifier-zoom-out.png"));
        reg.put(ImageKey.ZOOM_ADJUST, loadImageDescriptor("icons/magnifier-zoom-actual.png"));
        reg.put(ImageKey.DIAGRAM, loadImageDescriptor("icons/diagram.png"));
        reg.put(ImageKey.EDIT_EXCEL, loadImageDescriptor("icons/edit_excel.png"));
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * @param path the path
     * @return the image descriptor
     */
    private static ImageDescriptor loadImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static Image getImage(String key) {
        return getDefault().getImageRegistry().get(key);
    }

    public static ImageDescriptor getImageDescriptor(String key) {
        return getDefault().getImageRegistry().getDescriptor(key);
    }

    private static class ImageCreator implements Runnable {

        private final GraphicalViewer viewer;
        private Image img = null;

        private ImageCreator(GraphicalViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public void run() {
            GC figureCanvasGC = null;
            GC imageGC = null;
            try {
                final ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) viewer.getRootEditPart();
                rootEditPart.refresh();
                final IFigure rootFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS);
                final EditPart editPart = viewer.getContents();
                editPart.refresh();
                final ERDiagram diagram = (ERDiagram) editPart.getModel();
                final Rectangle rootFigureBounds = ExportToImageAction.getBounds(diagram, rootEditPart, rootFigure.getBounds());
                final Control figureCanvas = viewer.getControl();
                figureCanvasGC = new GC(figureCanvas);

                img = new Image(Display.getCurrent(), rootFigureBounds.width + 20, rootFigureBounds.height + 20);
                imageGC = new GC(img);
                imageGC.setBackground(figureCanvasGC.getBackground());
                imageGC.setForeground(figureCanvasGC.getForeground());
                imageGC.setFont(figureCanvasGC.getFont());
                imageGC.setLineStyle(figureCanvasGC.getLineStyle());
                imageGC.setLineWidth(figureCanvasGC.getLineWidth());
                imageGC.setAntialias(SWT.OFF);
                // imageGC.setInterpolation(SWT.HIGH);

                final Graphics imgGraphics = new SWTGraphics(imageGC);
                imgGraphics.setBackgroundColor(figureCanvas.getBackground());
                imgGraphics.fillRectangle(0, 0, rootFigureBounds.width + 20, rootFigureBounds.height + 20);
                imgGraphics.translate(ExportToImageAction.translateX(rootFigureBounds.x),
                        ExportToImageAction.translateY(rootFigureBounds.y));
                rootFigure.paint(imgGraphics);
            } finally {
                if (figureCanvasGC != null) {
                    figureCanvasGC.dispose();
                }
                if (imageGC != null) {
                    imageGC.dispose();
                }
            }
        }
    }

    public static Image createImage(GraphicalViewer viewer) {
        final Display display = PlatformUI.getWorkbench().getDisplay();
        final ImageCreator runnable = new ImageCreator(viewer);
        display.syncExec(runnable);
        return runnable.img;
    }
}
