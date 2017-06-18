package org.dbflute.erflute.editor.view.figure;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.NameValue;
import org.dbflute.erflute.editor.view.figure.layout.TableLayout;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;

public class ModelPropertiesFigure extends RectangleFigure {

    private Color foregroundColor;

    public ModelPropertiesFigure() {
        final TableLayout layout = new TableLayout(2);

        this.setLayoutManager(layout);
    }

    private void addRow(String name, String value, String tableStyle) {
        final Border border = new MarginBorder(5);
        final ToolbarLayout layout = new ToolbarLayout();
        layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
        layout.setStretchMinorAxis(true);
        final Label nameLabel = new Label();
        final Label valueLabel = new Label();
        nameLabel.setBorder(border);
        nameLabel.setText(name);
        nameLabel.setLabelAlignment(PositionConstants.LEFT);
        nameLabel.setForegroundColor(this.foregroundColor);
        this.add(nameLabel);
        if (!DisplayMessages.getMessage("action.title.change.design.simple").equals(tableStyle)
                && !DisplayMessages.getMessage("action.title.change.design.frame").equals(tableStyle)) {
            valueLabel.setBackgroundColor(ColorConstants.white);
            valueLabel.setOpaque(true);
            valueLabel.setForegroundColor(ColorConstants.black);
        } else {
            valueLabel.setOpaque(false);
            valueLabel.setForegroundColor(this.foregroundColor);
        }
        valueLabel.setBorder(border);
        valueLabel.setText(value);
        valueLabel.setLabelAlignment(PositionConstants.LEFT);
        this.add(valueLabel);
    }

    public void setData(List<NameValue> properties, String tableStyle, int[] color) {
        this.removeAll();
        this.decideColor(color);
        for (final NameValue property : properties) {
            this.addRow(property.getName(), property.getValue(), tableStyle);
        }
    }

    private void decideColor(int[] color) {
        if (color != null) {
            final int sum = color[0] + color[1] + color[2];
            if (sum > 255) {
                this.foregroundColor = ColorConstants.black;
            } else {
                this.foregroundColor = ColorConstants.white;
            }
        }
    }
}
