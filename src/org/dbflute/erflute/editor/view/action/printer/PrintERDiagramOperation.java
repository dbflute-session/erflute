package org.dbflute.erflute.editor.view.action.printer;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.PageSettings;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

public class PrintERDiagramOperation extends PrintGraphicalViewerOperation {

    public PrintERDiagramOperation(Printer p, GraphicalViewer g) {
        super(p, g);
    }

    protected ERDiagram getDiagram() {
        final EditPart editPart = getViewer().getContents();
        final ERDiagram diagram = (ERDiagram) editPart.getModel();

        return diagram;
    }

    @Override
    public Rectangle getPrintRegion() {
        final ERDiagram diagram = getDiagram();
        final PageSettings pageSetting = diagram.getPageSetting();

        final org.eclipse.swt.graphics.Rectangle trim = getPrinter().computeTrim(0, 0, 0, 0);
        final org.eclipse.swt.graphics.Point printerDPI = getPrinter().getDPI();

        final Insets notAvailable = new Insets(-trim.y, -trim.x, trim.height + trim.y, trim.width + trim.x);

        final Insets userPreferred =
                new Insets((pageSetting.getTopMargin() * printerDPI.x) / 72, (pageSetting.getLeftMargin() * printerDPI.x) / 72,
                        (pageSetting.getBottomMargin() * printerDPI.x) / 72, (pageSetting.getRightMargin() * printerDPI.x) / 72);

        final Rectangle paperBounds = new Rectangle(getPrinter().getBounds());
        final Rectangle printRegion = shrink(paperBounds, notAvailable);
        printRegion.intersect(shrink(paperBounds, userPreferred));
        printRegion.translate(trim.x, trim.y);

        return printRegion;
    }

    private Rectangle shrink(Rectangle bounds, Insets insets) {
        final Rectangle shrinked = bounds.getCopy();

        shrinked.x += insets.left;
        shrinked.y += insets.top;
        shrinked.width -= insets.getWidth();
        shrinked.height -= insets.getHeight();

        return shrinked;
    }

    @Override
    protected void setupPrinterGraphicsFor(Graphics graphics, IFigure figure) {
        final ERDiagram diagram = getDiagram();
        final PageSettings pageSetting = diagram.getPageSetting();

        final double dpiScale = (double) getPrinter().getDPI().x / Display.getCurrent().getDPI().x * pageSetting.getScale() / 100;

        final Rectangle printRegion = getPrintRegion();
        // put the print region in display coordinates
        printRegion.width /= dpiScale;
        printRegion.height /= dpiScale;

        final Rectangle bounds = figure.getBounds();
        final double xScale = (double) printRegion.width / bounds.width;
        final double yScale = (double) printRegion.height / bounds.height;
        switch (getPrintMode()) {
        case FIT_PAGE:
            graphics.scale(Math.min(xScale, yScale) * dpiScale);
            break;
        case FIT_WIDTH:
            graphics.scale(xScale * dpiScale);
            break;
        case FIT_HEIGHT:
            graphics.scale(yScale * dpiScale);
            break;
        default:
            graphics.scale(dpiScale);
        }
        graphics.setForegroundColor(figure.getForegroundColor());
        graphics.setBackgroundColor(figure.getBackgroundColor());
        graphics.setFont(figure.getFont());
    }

    @Override
    protected void printPages() {
        final Graphics graphics = getFreshPrinterGraphics();
        final IFigure figure = getPrintSource();
        setupPrinterGraphicsFor(graphics, figure);
        final Rectangle bounds = figure.getBounds();
        int x = bounds.x, y = bounds.y;
        final Rectangle clipRect = new Rectangle();
        while (y < bounds.y + bounds.height) {
            while (x < bounds.x + bounds.width) {
                graphics.pushState();
                getPrinter().startPage();
                graphics.translate(-x, -y);
                graphics.getClip(clipRect);
                clipRect.setLocation(x, y);
                graphics.clipRect(clipRect);
                figure.paint(graphics);
                getPrinter().endPage();
                graphics.popState();
                x += clipRect.width;
            }
            x = bounds.x;
            y += clipRect.height;
        }
    }
}
