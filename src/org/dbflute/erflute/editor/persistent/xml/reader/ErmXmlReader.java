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
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GroupSet;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
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
    protected final ReadDiagramWalkerLoader nodeElementLoader;
    protected final ReadSequenceLoader sequenceLoader;
    protected final ReadTriggerLoader triggerLoader;
    protected final ReadColumnLoader columnLoader;
    protected final ReadSettingLoader settingLoader;
    protected final ReadGroupLoader groupLoader;
    protected final ReadTablespaceLoader tablespaceLoader;
    protected final ReadDictionaryLoader dictionaryLoader;
    protected final ReadIndexLoader indexLoader;
    protected final ReadUniqueKeyLoader uniqueKeyLoader;
    protected final ReadWalkerNoteLoader noteLoader;
    protected final ReadImageLoader imageLoader;
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
        this.nodeElementLoader = new ReadDiagramWalkerLoader(persistentXml, assistLogic);
        this.sequenceLoader = new ReadSequenceLoader(persistentXml, assistLogic);
        this.triggerLoader = new ReadTriggerLoader(persistentXml, assistLogic);
        this.columnLoader = new ReadColumnLoader(persistentXml, assistLogic, sequenceLoader);
        this.settingLoader = new ReadSettingLoader(persistentXml, assistLogic, databaseLoader, tablePropertiesLoader, nodeElementLoader);
        this.groupLoader = new ReadGroupLoader(persistentXml, assistLogic, nodeElementLoader);
        this.tablespaceLoader = new ReadTablespaceLoader(persistentXml, assistLogic);
        this.dictionaryLoader = new ReadDictionaryLoader(persistentXml, assistLogic);
        this.indexLoader = new ReadIndexLoader(persistentXml, assistLogic);
        this.uniqueKeyLoader = new ReadUniqueKeyLoader(persistentXml, assistLogic);
        this.noteLoader = new ReadWalkerNoteLoader(persistentXml, assistLogic, nodeElementLoader);
        this.imageLoader = new ReadImageLoader(persistentXml, assistLogic, nodeElementLoader);
        this.tableLoader =
                new ReadTableLoader(persistentXml, assistLogic, nodeElementLoader, columnLoader, indexLoader, uniqueKeyLoader,
                        tablePropertiesLoader);
        this.viewLoader = new ReadViewLoader(persistentXml, assistLogic, nodeElementLoader, columnLoader, viewPropertiesLoader);
        this.ermodelLoader = new ReadVirtualDiagramLoader(persistentXml, assistLogic, tableLoader, noteLoader, groupLoader);
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
        final Element settings = getElement(root, "settings");
        database = databaseLoader.loadDatabase(settings);
        diagram = new ERDiagram(database);
        settingLoader.loadDBSetting(diagram, root);
        settingLoader.loadPageSetting(diagram, root);
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
        final Settings settings = diagramContents.getSettings();
        settingLoader.loadEnvironmentSetting(settings.getEnvironmentSetting(), parent, context);
        tablespaceLoader.loadTablespaceSet(diagramContents.getTablespaceSet(), parent, context, database);
        final GroupSet columnGroups = diagramContents.getGroups();
        columnGroups.clear();
        columnLoader.loadColumnGroups(columnGroups, parent, context, database);
        loadContents(diagramContents.getDiagramWalkers(), parent, context);
        diagramContents.getModelSet().addModels(loadErmodels(parent, context));
        sequenceLoader.loadSequenceSet(diagramContents.getSequenceSet(), parent);
        triggerLoader.loadTriggerSet(diagramContents.getTriggerSet(), parent);
        settingLoader.loadSettings(settings, parent, context);
        context.resolve();
    }

    // ===================================================================================
    //                                                                            Contents
    //                                                                            ========
    private void loadContents(DiagramWalkerSet contents, Element parent, LoadContext context) {
        final Element element = getElement(parent, "contents");
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Node content = nodeList.item(i);
            if ("table".equals(content.getNodeName())) {
                final ERTable table = tableLoader.loadTable((Element) content, context, diagram, database);
                contents.addNodeElement(table);
            } else if ("view".equals(content.getNodeName())) {
                final ERView view = viewLoader.loadView((Element) content, context, diagram, database);
                contents.addNodeElement(view);
            } else if ("note".equals(content.getNodeName())) {
                final WalkerNote note = noteLoader.loadNote((Element) content, context);
                contents.addNodeElement(note);
            } else if ("image".equals(content.getNodeName())) {
                final InsertedImage insertedImage = imageLoader.loadInsertedImage((Element) content, context);
                contents.addNodeElement(insertedImage);
                // #analyzed unused, virtual models in ermodels by jflute
                //} else if ("ermodel".equals(content.getNodeName())) {
                //    final ERModel ermodel = ermodelLoader.loadErmodel((Element) content, context, diagram);
                //    contents.addNodeElement(ermodel);
            } else if ("group".equals(content.getNodeName())) {
                continue; // not use here, saved in ermodel
            } else {
                throw new IllegalStateException("*Unsupported contents: " + content);
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
