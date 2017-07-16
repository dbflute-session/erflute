package org.dbflute.erflute.editor.model;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.VirtualDiagramEditor;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.eclipse.core.resources.IResource;
import org.eclipse.gef.EditPart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ERModelUtil {

    public static IEditorPart getActiveEditor() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        final IWorkbenchPage page = window.getActivePage();
        final IEditorPart editorPart = page.getActiveEditor();
        return editorPart;
    }

    public static ERDiagram getDiagram(EditPart editPart) {
        final Object model = editPart.getModel();
        return ((IERDiagram) model).toMaterializedDiagram();
    }

    /**
     * @deprecated {@link ERDiagram#refresh()}または{@link DiagramWalker#refresh()}を使って下さい。
     * @param diagram ER図のモデル。
     * @param elements 再描画対象のDiagramWalker。
     * @return diagramをrefreshできた場合true、それ以外の場合false。
     */
    @Deprecated
    public static boolean refreshDiagram(ERDiagram diagram, DiagramWalker... elements) {
        return refreshDiagram(diagram, Arrays.asList(elements));
    }

    /**
     * @deprecated {@link ERDiagram#refresh()}または{@link DiagramWalker#refresh()}を使って下さい。
     * @param diagram ER図のモデル。
     * @param elements 再描画対象のDiagramWalker。
     * @return diagramをrefreshできた場合true、それ以外の場合false。
     */
    @Deprecated
    public static boolean refreshDiagram(ERDiagram diagram, List<DiagramWalker> elements) {
        if (refreshDiagram(diagram)) {
            elements.stream().forEach(element -> {
                final IEditorPart activeEditor = diagram.getEditor().getActiveEditor();
                if (activeEditor instanceof VirtualDiagramEditor) {
                    final VirtualDiagramEditor editor = (VirtualDiagramEditor) activeEditor;
                    editor.reveal(element);
                }
            });
            return true;
        }
        return false;
    }

    /**
     * @deprecated {@link ERDiagram#refresh()}を使って下さい。
     * @param diagram ER図のモデル。
     * @return diagramをrefreshできた場合true、それ以外の場合false。
     */
    @Deprecated
    public static boolean refreshDiagram(ERDiagram diagram) {
        if (diagram == null) {
            return false;
        }
        final IEditorPart activeEditor = diagram.getEditor().getActiveEditor();
        if (activeEditor instanceof VirtualDiagramEditor) {
            final VirtualDiagramEditor editor = (VirtualDiagramEditor) activeEditor;
            editor.setContents(diagram.getCurrentVirtualDiagram());
            diagram.changeAll();
            return true;
        } else {
            diagram.changeAll();
            return true;
        }
    }

    public static void openDirectory(IResource resource) {
        final String directory = resource.getLocation().toString().replaceAll("/", "\\\\");
        String target = "c:\\windows\\explorer.exe" + " " + "/n, /select, {0}";
        target = MessageFormat.format(target, new Object[] { directory });
        try {
            Runtime.getRuntime().exec(target);
        } catch (final IOException e) {
            Activator.error(e);
        }
    }
}
