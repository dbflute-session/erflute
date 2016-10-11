package org.dbflute.erflute.editor.view.editmanager;

import org.dbflute.erflute.editor.view.figure.WalkerNoteFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

public class WalkerNoteEditorLocator implements CellEditorLocator {

    private final IFigure figure;

    public WalkerNoteEditorLocator(IFigure figure) {
        this.figure = figure;
    }

    @Override
    public void relocate(CellEditor cellEditor) {
        final Text text = (Text) cellEditor.getControl();
        final Rectangle rect = this.figure.getBounds().getCopy();
        this.figure.translateToAbsolute(rect);
        text.setBounds(rect.x + WalkerNoteFigure.RETURN_WIDTH, rect.y + WalkerNoteFigure.RETURN_WIDTH, rect.width
                - WalkerNoteFigure.RETURN_WIDTH * 2, rect.height - WalkerNoteFigure.RETURN_WIDTH * 2);
    }
}
