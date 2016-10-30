package org.dbflute.erflute.editor.persistent.xml.reader;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.reader.ReadWalkerGroupLoader.WalkerGroupedTableViewProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ErmXmlReader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final DateFormat DATE_FORMAT = PersistentXml.DATE_FORMAT;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadDatabaseLoader databaseLoader;
    protected final ReadTablePropertiesLoader tablePropertiesLoader;
    protected final ReadViewPropertiesLoader viewPropertiesLoader;
    protected final ReadDiagramWalkerLoader diagramWalkerLoader;
    protected final ReadSequenceLoader sequenceLoader;
    protected final ReadTriggerLoader triggerLoader;
    protected final ReadColumnLoader columnLoader;
    protected final ReadSettingsLoader settingsLoader;
    protected final ReadTablespaceLoader tablespaceLoader;
    protected final ReadDictionaryLoader dictionaryLoader;
    protected final ReadIndexLoader indexLoader;
    protected final ReadUniqueKeyLoader uniqueKeyLoader;
    protected final ReadWalkerGroupLoader walkerGroupLoader;
    protected final ReadWalkerNoteLoader walkerNoteLoader;
    protected final ReadInsertedImageLoader insertedImageLoader;
    protected final ReadTableLoader tableLoader;
    protected final ReadViewLoader viewLoader;
    protected final ReadVirtualDiagramLoader ermodelLoader;

    // state
    protected ERDiagram diagram;
    protected String database;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ErmXmlReader(PersistentXml persistentXml) {
        this.persistentXml = persistentXml;
        this.assistLogic = new ReadAssistLogic(persistentXml);
        this.databaseLoader = new ReadDatabaseLoader(persistentXml, assistLogic);
        this.tablePropertiesLoader = new ReadTablePropertiesLoader(persistentXml, assistLogic);
        this.viewPropertiesLoader = new ReadViewPropertiesLoader(persistentXml, assistLogic);
        this.diagramWalkerLoader = new ReadDiagramWalkerLoader(persistentXml, assistLogic);
        this.sequenceLoader = new ReadSequenceLoader(persistentXml, assistLogic);
        this.triggerLoader = new ReadTriggerLoader(persistentXml, assistLogic);
        this.columnLoader = new ReadColumnLoader(persistentXml, assistLogic, sequenceLoader);
        this.settingsLoader =
                new ReadSettingsLoader(persistentXml, assistLogic, databaseLoader, tablePropertiesLoader, diagramWalkerLoader);
        this.tablespaceLoader = new ReadTablespaceLoader(persistentXml, assistLogic);
        this.dictionaryLoader = new ReadDictionaryLoader(persistentXml, assistLogic);
        this.indexLoader = new ReadIndexLoader(persistentXml, assistLogic);
        this.uniqueKeyLoader = new ReadUniqueKeyLoader(persistentXml, assistLogic);
        this.walkerGroupLoader = new ReadWalkerGroupLoader(persistentXml, assistLogic, diagramWalkerLoader);
        this.walkerNoteLoader = new ReadWalkerNoteLoader(persistentXml, assistLogic, diagramWalkerLoader);
        this.insertedImageLoader = new ReadInsertedImageLoader(persistentXml, assistLogic, diagramWalkerLoader);
        this.tableLoader =
                new ReadTableLoader(persistentXml, assistLogic, diagramWalkerLoader, columnLoader, indexLoader, uniqueKeyLoader,
                        tablePropertiesLoader);
        this.viewLoader = new ReadViewLoader(persistentXml, assistLogic, diagramWalkerLoader, columnLoader, viewPropertiesLoader);
        this.ermodelLoader = new ReadVirtualDiagramLoader(persistentXml, assistLogic, tableLoader, walkerNoteLoader, walkerGroupLoader);
    }

    // ===================================================================================
    //                                                                               Read
    //                                                                              ======
    public ERDiagram read(InputStream ins) throws Exception {
        final DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document document = parser.parse(ins);
        Node root = document.getFirstChild();
        while (root.getNodeType() == Node.COMMENT_NODE) {
            document.removeChild(root);
            root = document.getFirstChild();
        }
        load((Element) root);
        return this.diagram;
    }

    // ===================================================================================
    //                                                                               Root
    //                                                                              ======
    private void load(Element root) {
        database = databaseLoader.loadDatabase(root);
        diagram = new ERDiagram(database);
        settingsLoader.loadDBSettings(diagram, root);
        settingsLoader.loadPageSetting(diagram, root);
        // #for_erflute not keep category_index to immobilize XML (frequently changed by everybody)
        //diagram.setCurrentCategory(null, getIntValue(root, "category_index"));
        // #for_erflute not keep default_color to immobilize XML (frequently changed by everybody)
        //assistLogic.loadDefaultColor(diagram, root);
        assistLogic.loadColor(diagram, root);
        assistLogic.loadFont(diagram, root);
        final DiagramContents diagramContents = diagram.getDiagramContents();
        loadDiagramContents(diagramContents, root);
        // #for_erflute not keep current_ermodel to immobilize XML (frequently changed by everybody)
        //diagram.setCurrentErmodel(null, getStringValue(root, "current_ermodel"));
        // #for_erflute not keep zoom to immobilize XML (frequently changed by everybody)
        //final double zoom = getDoubleValue(root, "zoom");
        //diagram.setZoom(zoom);
        // #for_erflute not keep location to immobilize XML (frequently changed by everybody)
        //final int x = getIntValue(root, "x");
        //final int y = getIntValue(root, "y");
        //diagram.setLocation(x, y);
    }

    // ===================================================================================
    //                                                                    Diagram Contents
    //                                                                    ================
    private void loadDiagramContents(DiagramContents diagramContents, Element parent) {
        final Dictionary dictionary = diagramContents.getDictionary();
        final LoadContext context = new LoadContext(dictionary);
        dictionaryLoader.loadDictionary(dictionary, parent, context, database);
        final DiagramSettings settings = diagramContents.getSettings();
        settingsLoader.loadDiagramSettings(settings, parent, context, database);
        settingsLoader.loadEnvironmentSettings(settings.getEnvironmentSettings(), parent, context);
        tablespaceLoader.loadTablespaceSet(diagramContents.getTablespaceSet(), parent, context, database);
        final ColumnGroupSet columnGroups = diagramContents.getColumnGroupSet();
        columnGroups.clear();
        columnLoader.loadColumnGroups(columnGroups, parent, context, database);
        loadDiagramWalkers(diagramContents.getDiagramWalkers(), parent, context);
        diagramContents.getVirtualDiagramSet().addModels(loadErmodels(parent, context));
        sequenceLoader.loadSequenceSet(diagramContents.getSequenceSet(), parent);
        triggerLoader.loadTriggerSet(diagramContents.getTriggerSet(), parent);
        context.resolve();
    }

    // ===================================================================================
    //                                                                            Contents
    //                                                                            ========
    private void loadDiagramWalkers(DiagramWalkerSet contents, Element parent, LoadContext context) {
        Element walkersElement = getElement(parent, "contents"); // migration from ERMaster
        if (walkersElement == null) {
            walkersElement = getElement(parent, "diagram_walkers"); // #for_erflute
        }
        final NodeList walkersNodeList = walkersElement.getChildNodes();
        for (int i = 0; i < walkersNodeList.getLength(); i++) {
            if (walkersNodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Node walkerNode = walkersNodeList.item(i);
            final String walkerName = walkerNode.getNodeName();
            if ("table".equals(walkerName)) {
                final ERTable table = tableLoader.loadTable((Element) walkerNode, context, diagram, database);
                contents.addDiagramWalker(table);
            } else if ("view".equals(walkerName)) {
                final ERView view = viewLoader.loadView((Element) walkerNode, context, diagram, database);
                contents.addDiagramWalker(view);
            } else if ("walker_note".equals(walkerName) || "note".equals(walkerName)) { // #for_erflute
                final WalkerNote note = walkerNoteLoader.loadNote((Element) walkerNode, context);
                contents.addDiagramWalker(note);
            } else if ("walker_group".equals(walkerName) || "group".equals(walkerName)) { // #for_erflute
                final WalkerGroup group = walkerGroupLoader.loadGroup((Element) walkerNode, context, new WalkerGroupedTableViewProvider() {
                    @Override
                    public List<? extends TableView> provide() {
                        return diagram.getDiagramContents().getDiagramWalkers().getTableViewList();
                    }
                });
                contents.addDiagramWalker(group);
            } else if ("inserted_image".equals(walkerName) || "image".equals(walkerName)) { // #for_erflute
                final InsertedImage insertedImage = insertedImageLoader.loadInsertedImage((Element) walkerNode, context);
                contents.addDiagramWalker(insertedImage);
            } else {
                throw new IllegalStateException("*Unsupported contents: " + walkerNode);
            }
        }
    }

    // ===================================================================================
    //                                                                            ERModels
    //                                                                            ========
    private List<ERVirtualDiagram> loadErmodels(Element parent, LoadContext context) {
        return ermodelLoader.loadVirtualDiagram(parent, context, diagram);
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}
