package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.reader.ReadColumnLoader.ColumnIdBuilder;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadViewLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadDiagramWalkerLoader nodeElementLoader;
    protected final ReadColumnLoader columnLoader;
    protected final ReadViewPropertiesLoader viewPropertiesLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadViewLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadDiagramWalkerLoader nodeElementLoader,
            ReadColumnLoader columnLoader, ReadViewPropertiesLoader viewPropertiesLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementLoader = nodeElementLoader;
        this.columnLoader = columnLoader;
        this.viewPropertiesLoader = viewPropertiesLoader;
    }

    // ===================================================================================
    //                                                                               View
    //                                                                              ======
    public ERView loadView(Element element, LoadContext context, ERDiagram diagram, String database) {
        final ERView view = new ERView();
        view.setDiagram(diagram);
        view.setPhysicalName(getStringValue(element, "physical_name"));
        view.setLogicalName(getStringValue(element, "logical_name"));
        view.setDescription(getStringValue(element, "description"));
        nodeElementLoader.loadNodeElement(view, element, context);
        view.setSql(getStringValue(element, "sql"));
        final List<ERColumn> columns = columnLoader.loadColumns(element, context, database, new ColumnIdBuilder() {
            @Override
            public String build(NormalColumn column) {
                return column.buildColumnId(view);
            }
        });
        view.setColumns(columns);
        viewPropertiesLoader.loadViewProperties((ViewProperties) view.getTableViewProperties(), element, context);
        return view;
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }
}