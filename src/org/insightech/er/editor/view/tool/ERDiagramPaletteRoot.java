package org.insightech.er.editor.view.tool;

import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.DisplayMessages;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;
import org.insightech.er.editor.model.diagram_contents.element.connection.RelatedTable;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.connection.RelationByExistingColumns;
import org.insightech.er.editor.model.diagram_contents.element.connection.SelfRelation;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;

public class ERDiagramPaletteRoot extends PaletteRoot {

    public ERDiagramPaletteRoot() {
        PaletteGroup group = new PaletteGroup("");

        PanningSelectionToolEntry selectionToolEntry = new PanningSelectionToolEntry(DisplayMessages.getMessage("label.select"));
        selectionToolEntry.setToolClass(MovablePanningSelectionTool.class);
        selectionToolEntry.setLargeIcon(Activator.getImageDescriptor(ImageKey.ARROW));
        selectionToolEntry.setSmallIcon(Activator.getImageDescriptor(ImageKey.ARROW));

        group.add(selectionToolEntry);
        // group.add(new MarqueeToolEntry());

        group.add(new CreationToolEntry(DisplayMessages.getMessage("label.table"), DisplayMessages
                .getMessage("label.create.table"), new SimpleFactory(ERTable.class), Activator
                .getImageDescriptor(ImageKey.TABLE_NEW), Activator.getImageDescriptor(ImageKey.TABLE_NEW)));

        group.add(new CreationToolEntry(DisplayMessages.getMessage("label.view"), DisplayMessages
                .getMessage("label.create.view"), new SimpleFactory(View.class), Activator.getImageDescriptor(ImageKey.VIEW),
                Activator.getImageDescriptor(ImageKey.VIEW)));

        ConnectionCreationToolEntry toolEntry1 =
                new ConnectionCreationToolEntry(DisplayMessages.getMessage("label.relation.one.to.many"),
                        DisplayMessages.getMessage("label.create.relation.one.to.many"), new SimpleFactory(Relation.class),
                        Activator.getImageDescriptor(ImageKey.RELATION_1_N), Activator.getImageDescriptor(ImageKey.RELATION_1_N));
        toolEntry1.setToolClass(RelationCreationTool.class);
        group.add(toolEntry1);

        ConnectionCreationToolEntry toolEntry2 =
                new ConnectionCreationToolEntry(DisplayMessages.getMessage("label.relation.by.existing.columns"),
                        DisplayMessages.getMessage("label.create.relation.by.existing.columns"), new SimpleFactory(
                                RelationByExistingColumns.class), Activator.getImageDescriptor(ImageKey.RELATION_1_N),
                        Activator.getImageDescriptor(ImageKey.RELATION_1_N));
        toolEntry2.setToolClass(RelationByExistingColumnsCreationTool.class);
        group.add(toolEntry2);

        ConnectionCreationToolEntry toolEntry3 =
                new ConnectionCreationToolEntry(DisplayMessages.getMessage("label.relation.many.to.many"),
                        DisplayMessages.getMessage("label.create.relation.many.to.many"), new SimpleFactory(RelatedTable.class),
                        Activator.getImageDescriptor(ImageKey.RELATION_N_N), Activator.getImageDescriptor(ImageKey.RELATION_N_N));
        toolEntry3.setToolClass(RelatedTableCreationTool.class);
        group.add(toolEntry3);

        ConnectionCreationToolEntry toolEntry4 =
                new ConnectionCreationToolEntry(DisplayMessages.getMessage("label.relation.self"),
                        DisplayMessages.getMessage("label.create.relation.self"), new SimpleFactory(SelfRelation.class),
                        Activator.getImageDescriptor(ImageKey.RELATION_SELF), Activator.getImageDescriptor(ImageKey.RELATION_SELF));
        toolEntry4.setToolClass(SelfRelationCreationTool.class);
        group.add(toolEntry4);

        group.add(new PaletteSeparator());

        CreationToolEntry toolEntry5 =
                new CreationToolEntry(DisplayMessages.getMessage("label.note"),
                        DisplayMessages.getMessage("label.create.note"), new SimpleFactory(Note.class),
                        Activator.getImageDescriptor(ImageKey.NOTE), Activator.getImageDescriptor(ImageKey.NOTE));
        group.add(toolEntry5);

        ConnectionCreationToolEntry commentConnectionToolEntry =
                new ConnectionCreationToolEntry(DisplayMessages.getMessage("label.relation.note"),
                        DisplayMessages.getMessage("label.create.relation.note"), new SimpleFactory(CommentConnection.class),
                        Activator.getImageDescriptor(ImageKey.COMMENT_CONNECTION),
                        Activator.getImageDescriptor(ImageKey.COMMENT_CONNECTION));
        group.add(commentConnectionToolEntry);

        group.add(new PaletteSeparator());

        group.add(new CreationToolEntry(DisplayMessages.getMessage("label.vgroup"), DisplayMessages.getMessage("label.vgroup"),
                new SimpleFactory(VGroup.class), Activator.getImageDescriptor(ImageKey.CATEGORY), Activator
                        .getImageDescriptor(ImageKey.CATEGORY)));

        //		group.add(new CreationToolEntry(ResourceString
        //				.getResourceString("label.category"), ResourceString
        //				.getResourceString("label.category"), new SimpleFactory(
        //				Category.class), Activator
        //				.getImageDescriptor(ImageKey.CATEGORY), Activator
        //				.getImageDescriptor(ImageKey.CATEGORY)));

        group.add(new PaletteSeparator());

        group.add(new InsertImageTool());

        this.add(group);

        this.setDefaultEntry(selectionToolEntry);
    }

}
