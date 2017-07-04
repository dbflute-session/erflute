package org.dbflute.erflute.core.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CenteredContentCellPaint implements Listener {
    private final int colIndex;

    public CenteredContentCellPaint(Table tbl, int colIndex) {
        this.colIndex = colIndex;
        tbl.addListener(SWT.EraseItem, this);
        tbl.addListener(SWT.PaintItem, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.index == colIndex) {
            if (event.type == SWT.EraseItem) {
                event.detail &= (Integer.MAX_VALUE ^ SWT.FOREGROUND);

            } else if (event.type == SWT.PaintItem) {
                final TableItem item = (TableItem) event.item;
                final Image img = item.getImage(colIndex);
                if (img != null) {
                    final Rectangle size = img.getBounds();
                    final Table tbl = (Table) event.widget;
                    event.gc.drawImage(img, event.x + (tbl.getColumn(colIndex).getWidth() - size.width) / 2,
                            event.y + (tbl.getItemHeight() - size.height) / 2);
                }
            }
        }
    }
}
