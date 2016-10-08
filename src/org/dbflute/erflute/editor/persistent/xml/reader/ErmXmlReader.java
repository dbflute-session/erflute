package org.dbflute.erflute.editor.persistent.xml.reader;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;
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
    protected final ReadNodeElementLoader nodeElementLoader;
    protected final ReadSequenceLoader sequenceLoader;
    protected final ReadTriggerLoader triggerLoader;
    protected final ReadColumnLoader columnLoader;
    protected final ReadSettingLoader settingLoader;
    protected final ReadGroupLoader groupLoader;
    protected final ReadTablespaceLoader tablespaceLoader;
    protected final ReadDictionaryLoader dictionaryLoader;
    protected final ReadIndexLoader indexLoader;
    protected final ReadUniqueKeyLoader uniqueKeyLoader;
    protected final ReadNoteLoader noteLoader;
    protected final ReadImageLoader imageLoader;
    protected final ReadTableLoader tableLoader;
    protected final ReadViewLoader viewLoader;
    protected final ReadERModelLoader ermodelLoader;

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
        this.nodeElementLoader = new ReadNodeElementLoader(persistentXml, assistLogic);
        this.sequenceLoader = new ReadSequenceLoader(persistentXml, assistLogic);
        this.triggerLoader = new ReadTriggerLoader(persistentXml, assistLogic);
        this.columnLoader = new ReadColumnLoader(persistentXml, assistLogic, sequenceLoader);
        this.settingLoader = new ReadSettingLoader(persistentXml, assistLogic, databaseLoader, tablePropertiesLoader, nodeElementLoader);
        this.groupLoader = new ReadGroupLoader(persistentXml, assistLogic, nodeElementLoader);
        this.tablespaceLoader = new ReadTablespaceLoader(persistentXml, assistLogic);
        this.dictionaryLoader = new ReadDictionaryLoader(persistentXml, assistLogic);
        this.indexLoader = new ReadIndexLoader(persistentXml, assistLogic);
        this.uniqueKeyLoader = new ReadUniqueKeyLoader(persistentXml, assistLogic);
        this.noteLoader = new ReadNoteLoader(persistentXml, assistLogic, nodeElementLoader);
        this.imageLoader = new ReadImageLoader(persistentXml, assistLogic, nodeElementLoader);
        this.tableLoader =
                new ReadTableLoader(persistentXml, assistLogic, nodeElementLoader, columnLoader, indexLoader, uniqueKeyLoader,
                        tablePropertiesLoader);
        this.viewLoader = new ReadViewLoader(persistentXml, assistLogic, nodeElementLoader, columnLoader, viewPropertiesLoader);
        this.ermodelLoader = new ReadERModelLoader(persistentXml, assistLogic, tableLoader, noteLoader, groupLoader);
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
        assistLogic.loadColor(diagram, root);
        assistLogic.loadDefaultColor(diagram, root);
        assistLogic.loadFont(diagram, root);
        final DiagramContents diagramContents = diagram.getDiagramContents();
        loadDiagramContents(diagramContents, root);
        diagram.setCurrentCategory(null, getIntValue(root, "category_index"));
        diagram.setCurrentErmodel(null, getStringValue(root, "current_ermodel"));
        final double zoom = getDoubleValue(root, "zoom");
        diagram.setZoom(zoom);
        final int x = getIntValue(root, "x");
        final int y = getIntValue(root, "y");
        diagram.setLocation(x, y);
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
        loadContents(diagramContents.getContents(), parent, context);
        diagramContents.getModelSet().addModels(loadErmodels(parent, context));
        sequenceLoader.loadSequenceSet(diagramContents.getSequenceSet(), parent);
        triggerLoader.loadTriggerSet(diagramContents.getTriggerSet(), parent);
        settingLoader.loadSettings(settings, parent, context);
        context.resolve();
    }

    // ===================================================================================
    //                                                                            Contents
    //                                                                            ========
    private void loadContents(NodeSet contents, Element parent, LoadContext context) {
        final Element element = getElement(parent, "contents");
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Node node = nodeList.item(i);
            if ("table".equals(node.getNodeName())) {
                final ERTable table = loadTable((Element) node, context);
                contents.addNodeElement(table);
            } else if ("view".equals(node.getNodeName())) {
                final ERView view = loadView((Element) node, context);
                contents.addNodeElement(view);
                // what? by jflute
                //			} else if ("note".equals(node.getNodeName())) {
                //				Note note = this.loadNote((Element) node, context);
                //				contents.addNodeElement(note);
            } else if ("image".equals(node.getNodeName())) {
                final InsertedImage insertedImage = imageLoader.loadInsertedImage((Element) node, context);
                contents.addNodeElement(insertedImage);
            } else if ("ermodel".equals(node.getNodeName())) {
                final ERModel ermodel = loadErmodel((Element) node, context);
                contents.addNodeElement(ermodel);
                //			} else if ("group".equals(node.getNodeName())) {
                //				VGroup group = this.loadGroup((Element) node, context);
                //				contents.addNodeElement(group);
            } else {
                throw new RuntimeException("not support " + node);
            }
        }
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    private ERTable loadTable(Element element, LoadContext context) {
        return tableLoader.loadTable(element, context, diagram, database);
    }

    // ===================================================================================
    //                                                                               View
    //                                                                              ======
    private ERView loadView(Element element, LoadContext context) {
        return viewLoader.loadView(element, context, diagram, database);
    }

    // ===================================================================================
    //                                                                             ERModel
    //                                                                             =======
    private ERModel loadErmodel(Element parent, LoadContext context) {
        return ermodelLoader.loadErmodel(parent, context, diagram);
    }

    // ===================================================================================
    //                                                                            ERModels
    //                                                                            ========
    private List<ERModel> loadErmodels(Element parent, LoadContext context) {
        return ermodelLoader.loadErmodels(parent, context, diagram);
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }

    private int getIntValue(Element element, String tagname) {
        return assistLogic.getIntValue(element, tagname);
    }

    private double getDoubleValue(Element element, String tagname) {
        return assistLogic.getDoubleValue(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}
