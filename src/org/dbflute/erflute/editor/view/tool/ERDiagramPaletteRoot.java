package org.dbflute.erflute.editor.view.tool;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.CommentConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.RelationByExistingColumns;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.SelfRelation;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.requests.SimpleFactory;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramPaletteRoot extends PaletteRoot {

    public ERDiagramPaletteRoot() {
        final PaletteGroup group = new PaletteGroup("");

        final PanningSelectionToolEntry selectionToolEntry = setupSelectionTool(group);
        // what is this? by jflute
        // group.add(new MarqueeToolEntry());

        setupTableViewTool(group);

        // #deleted relationship creating new column is implemented as option of "1:n relationship"
        //final ConnectionCreationToolEntry toolEntry1 =
        //        new ConnectionCreationToolEntry("1:n new column", DisplayMessages.getMessage("label.create.relation.one.to.many"),
        //                new SimpleFactory(Relationship.class), Activator.getImageDescriptor(ImageKey.RELATION_1_N),
        //                Activator.getImageDescriptor(ImageKey.RELATION_1_N));
        //toolEntry1.setToolClass(RelationCreationTool.class);
        //group.add(toolEntry1);

        final ConnectionCreationToolEntry toolEntry2 =
                new ConnectionCreationToolEntry("1:n Relationship", "Make relationship of tables", new SimpleFactory(
                        RelationByExistingColumns.class), Activator.getImageDescriptor(ImageKey.RELATION_1_N),
                        Activator.getImageDescriptor(ImageKey.RELATION_1_N));
        toolEntry2.setToolClass(RelationByExistingColumnsCreationTool.class);
        group.add(toolEntry2);

        // #deleted unneeded by jflute
        //final ConnectionCreationToolEntry toolEntry3 =
        //        new ConnectionCreationToolEntry(DisplayMessages.getMessage("label.relation.many.to.many"),
        //                DisplayMessages.getMessage("label.create.relation.many.to.many"), new SimpleFactory(RelatedTable.class),
        //                Activator.getImageDescriptor(ImageKey.RELATION_N_N), Activator.getImageDescriptor(ImageKey.RELATION_N_N));
        //toolEntry3.setToolClass(RelatedTableCreationTool.class);
        //group.add(toolEntry3);

        final ConnectionCreationToolEntry seflRelationshipToolEntry =
                new ConnectionCreationToolEntry("Self Relationship", "Make relationship in same table", new SimpleFactory(
                        SelfRelation.class), Activator.getImageDescriptor(ImageKey.RELATION_SELF),
                        Activator.getImageDescriptor(ImageKey.RELATION_SELF));
        seflRelationshipToolEntry.setToolClass(SelfRelationCreationTool.class);
        group.add(seflRelationshipToolEntry);
        setupSeparator(group);

        setupNoteTool(group);
        setupSeparator(group);

        setupWalkerGroupTool(group);
        setupSeparator(group);

        setupImageTool(group);
        add(group);

        setDefaultEntry(selectionToolEntry);
    }

    private void setupTableViewTool(final PaletteGroup group) {
        group.add(new CreationToolEntry("Table", "Make new table object", new SimpleFactory(ERTable.class),
                Activator.getImageDescriptor(ImageKey.TABLE_NEW), Activator.getImageDescriptor(ImageKey.TABLE_NEW)));

        // #deleted unsupported, view is unneeded in ERD tool by jflute
        //group.add(new CreationToolEntry(DisplayMessages.getMessage("label.view"), DisplayMessages.getMessage("label.create.view"),
        //        new SimpleFactory(ERView.class), Activator.getImageDescriptor(ImageKey.VIEW), Activator.getImageDescriptor(ImageKey.VIEW)));
    }

    private PanningSelectionToolEntry setupSelectionTool(final PaletteGroup group) {
        final PanningSelectionToolEntry entry = new PanningSelectionToolEntry(DisplayMessages.getMessage("label.select"));
        entry.setToolClass(MovablePanningSelectionTool.class);
        entry.setLargeIcon(Activator.getImageDescriptor(ImageKey.ARROW));
        entry.setSmallIcon(Activator.getImageDescriptor(ImageKey.ARROW));
        group.add(entry);
        return entry;
    }

    private void setupNoteTool(final PaletteGroup group) {
        final CreationToolEntry noteToolEntry =
                new CreationToolEntry("Note", "Make new note for tables", new SimpleFactory(WalkerNote.class),
                        Activator.getImageDescriptor(ImageKey.NOTE), Activator.getImageDescriptor(ImageKey.NOTE));
        group.add(noteToolEntry);
        final ConnectionCreationToolEntry relationNoteTool =
                new ConnectionCreationToolEntry("Note Connection", "Connect note to tables", new SimpleFactory(CommentConnection.class),
                        Activator.getImageDescriptor(ImageKey.COMMENT_CONNECTION),
                        Activator.getImageDescriptor(ImageKey.COMMENT_CONNECTION));
        group.add(relationNoteTool);
    }

    private void setupWalkerGroupTool(final PaletteGroup group) {
        group.add(new CreationToolEntry("Table Group", "Make new group for tables by border", new SimpleFactory(WalkerGroup.class),
                Activator.getImageDescriptor(ImageKey.CATEGORY), Activator.getImageDescriptor(ImageKey.CATEGORY)));
    }

    private void setupImageTool(final PaletteGroup group) {
        group.add(new InsertImageTool());
    }

    private void setupSeparator(final PaletteGroup group) {
        group.add(new PaletteSeparator());
    }
}
