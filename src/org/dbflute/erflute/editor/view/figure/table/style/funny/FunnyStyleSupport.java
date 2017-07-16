package org.dbflute.erflute.editor.view.figure.table.style.funny;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.figure.table.IndexFigure;
import org.dbflute.erflute.editor.view.figure.table.TableFigure;
import org.dbflute.erflute.editor.view.figure.table.column.GroupColumnFigure;
import org.dbflute.erflute.editor.view.figure.table.column.NormalColumnFigure;
import org.dbflute.erflute.editor.view.figure.table.style.AbstractStyleSupport;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Font;

public class FunnyStyleSupport extends AbstractStyleSupport {

    private Label nameLabel;

    public FunnyStyleSupport(TableFigure tableFigure, DiagramSettings settings) {
        super(tableFigure, settings);
    }

    @Override
    public void init(TableFigure tableFigure) {
        tableFigure.setCornerDimensions(new Dimension(10, 10));
        tableFigure.setForegroundColor(ColorConstants.black);
        tableFigure.setBorder(null);
    }

    @Override
    public void initTitleBar(Figure top) {
        top.setLayoutManager(new BorderLayout());

        final FlowLayout layout = new FlowLayout();
        layout.setStretchMinorAxis(true);
        final Figure title = new Figure();
        top.add(title, BorderLayout.TOP);
        title.setLayoutManager(layout);

        title.setBorder(new MarginBorder(new Insets(2, 2, 2, 2)));

        final ImageFigure image = new ImageFigure();
        image.setBorder(new MarginBorder(new Insets(0, 0, 0, 0)));
        image.setImage(Activator.getImage(getTableFigure().getImageKey()));
        title.add(image);

        this.nameLabel = new Label();
        nameLabel.setBorder(new MarginBorder(new Insets(0, 0, 0, 20)));
        title.add(nameLabel);

        final Figure separater = new Figure();
        separater.setSize(-1, 1);
        separater.setBackgroundColor(ColorConstants.black);
        separater.setOpaque(true);

        top.add(separater, BorderLayout.BOTTOM);
    }

    @Override
    public void createColumnArea(IFigure columns) {
        initColumnArea(columns);

        columns.setBorder(new MarginBorder(new Insets(1, 0, 1, 0)));
        columns.setBackgroundColor(ColorConstants.white);
        columns.setOpaque(true);

        final Figure centerFigure = new Figure();
        centerFigure.setLayoutManager(new BorderLayout());
        centerFigure.setBorder(new MarginBorder(new Insets(0, 2, 0, 2)));

        centerFigure.add(columns, BorderLayout.CENTER);
        getTableFigure().add(centerFigure, BorderLayout.CENTER);
    }

    @Override
    public void createFooter() {
        final IFigure footer = new Figure();
        final BorderLayout footerLayout = new BorderLayout();
        footer.setLayoutManager(footerLayout);
        footer.setBorder(new MarginBorder(new Insets(0, 0, 0, 0)));

        final IFigure footer1 = new Figure();
        footer1.setSize(-1, 1);
        footer1.setBackgroundColor(ColorConstants.black);
        footer1.setOpaque(true);
        footer.add(footer1, BorderLayout.TOP);

        final IFigure footer2 = new Figure();
        footer2.setSize(-1, 6);
        footer.add(footer2, BorderLayout.BOTTOM);

        getTableFigure().add(footer, BorderLayout.BOTTOM);
    }

    @Override
    public void setName(String name) {
        nameLabel.setForegroundColor(getTextColor());
        nameLabel.setFont(getTableFigure().getLargeFont());
        nameLabel.setText(name);
    }

    @Override
    public void setFont(Font font, Font titleFont) {
        nameLabel.setFont(titleFont);
    }

    @Override
    protected Label createColumnLabel() {
        final Label label = new Label();
        label.setBorder(new MarginBorder(new Insets(1, 5, 1, 5)));
        label.setLabelAlignment(PositionConstants.LEFT);
        return label;
    }

    @Override
    public void addColumn(ERTable table, NormalColumn normalColumn, NormalColumnFigure columnFigure, int viewMode, String physicalName,
            String logicalName, String type, boolean primaryKey, boolean foreignKey, boolean isNotNull, boolean uniqueKey,
            boolean displayKey, boolean displayDetail, boolean displayType, boolean isSelectedReferenced, boolean isSelectedForeignKey,
            boolean isAdded, boolean isUpdated, boolean isRemoved) {
        columnFigure.setBorder(new MarginBorder(new Insets(1, 0, 1, 0)));

        final Label label = createColumnLabel();
        label.setForegroundColor(ColorConstants.black);

        final StringBuilder text = new StringBuilder();
        text.append(getColumnText(table, normalColumn, viewMode, physicalName, logicalName, type, isNotNull, uniqueKey, displayDetail,
                displayType));

        if (displayKey) {
            if (primaryKey) {
                final ImageFigure image = new ImageFigure();
                image.setBorder(new MarginBorder(new Insets(0, 4, 0, 0)));
                image.setImage(Activator.getImage(ImageKey.PRIMARY_KEY));
                columnFigure.add(image);
            } else if (foreignKey) {
                final ImageFigure image = new ImageFigure();
                image.setBorder(new MarginBorder(new Insets(0, 4, 0, 0)));
                image.setImage(Activator.getImage(ImageKey.FOREIGN_KEY));
                columnFigure.add(image);
            } else {
                final ImageFigure image = new ImageFigure();
                image.setBorder(new MarginBorder(new Insets(0, 4, 0, 15)));
                image.setImage(Activator.getImage(ImageKey.BLANK_WHITE));
                image.setOpaque(true);
                columnFigure.add(image);
            }
            //			if (foreignKey){
            //				ImageFigure image = new ImageFigure();
            //				image.setBorder(new MarginBorder(new Insets(0, 0, 0, 0)));
            //				image.setImage(Activator.getImage(ImageKey.FOREIGN_KEY));
            //				columnFigure.add(image);
            //			} else {
            //				Label filler = new Label();
            //				filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
            //				columnFigure.add(filler);
            //			}
            if (isNotNull) {
                final ImageFigure image = new ImageFigure();
                image.setBorder(new MarginBorder(new Insets(0, 1, 0, 0)));
                image.setImage(Activator.getImage(ImageKey.NON_NULL));
                columnFigure.add(image);
            } else {
                final ImageFigure image = new ImageFigure();
                image.setBorder(new MarginBorder(new Insets(0, 1, 0, 6)));
                image.setImage(Activator.getImage(ImageKey.BLANK_WHITE));
                columnFigure.add(image);
            }
            if (primaryKey && foreignKey) {
                label.setForegroundColor(ColorConstants.blue);

            } else if (primaryKey) {
                label.setForegroundColor(ColorConstants.red);

            } else if (foreignKey) {
                label.setForegroundColor(ColorConstants.darkGreen);
            }
        }

        label.setBorder(new MarginBorder(new Insets(0, 2, 0, 3)));
        label.setText(text.toString());

        setColumnFigureColor(columnFigure, isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, isRemoved);

        columnFigure.add(label);
    }

    @Override
    public void addColumnGroup(GroupColumnFigure columnFigure, int viewMode,
            String name, boolean isAdded, boolean isUpdated, boolean isRemoved) {
        columnFigure.setBorder(new MarginBorder(new Insets(1, 0, 1, 0)));

        final ImageFigure image = new ImageFigure();
        image.setBorder(new MarginBorder(new Insets(0, 4, 0, 7)));
        image.setImage(Activator.getImage(ImageKey.GROUP));
        columnFigure.add(image);

        //		Label filler = new Label();
        //		filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
        //		filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 6)));
        //		columnFigure.add(filler);

        //		filler = new Label();
        //		filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
        //		columnFigure.add(filler);

        final StringBuilder text = new StringBuilder();
        text.append(name);
        text.append(" (GROUP)");

        setColumnFigureColor(columnFigure, false, false, isAdded, isUpdated, isRemoved);

        final Label label = createColumnLabel();

        label.setForegroundColor(ColorConstants.black);
        label.setLabelAlignment(PositionConstants.RIGHT);
        label.setBorder(new MarginBorder(new Insets(1, 3, 0, 4)));

        label.setText(text.toString());

        columnFigure.add(label);
    }

    @Override
    public void addIndex(IndexFigure indexFigure, String name, boolean isFirst) {
        final ImageFigure image = new ImageFigure();
        image.setBorder(new MarginBorder(new Insets(0, 0, 0, 19)));
        image.setImage(Activator.getImage(ImageKey.BLANK_WHITE));
        image.setOpaque(true);
        indexFigure.add(image);
        //		Label filler = new Label();
        //		filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
        //		filler.setBorder(new MarginBorder(new Insets(1, 4, 0, 16)));
        //		indexFigure.add(filler);

        final StringBuilder text = new StringBuilder();
        text.append(name);
        final Label label = createColumnLabel();
        label.setBorder(new MarginBorder(new Insets(1, 0, 0, 4)));
        label.setForegroundColor(ColorConstants.black);
        label.setText(text.toString());

        indexFigure.add(label);
    }
}
