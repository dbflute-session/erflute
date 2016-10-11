package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.reader.ReadColumnLoader.ColumnIdBuilder;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadTableLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadDiagramWalkerLoader nodeElementLoader;
    protected final ReadColumnLoader columnLoader;
    protected final ReadIndexLoader indexLoader;
    protected final ReadUniqueKeyLoader uniqueKeyLoader;
    protected final ReadTablePropertiesLoader tablePropertiesLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadTableLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadDiagramWalkerLoader nodeElementLoader,
            ReadColumnLoader columnLoader, ReadIndexLoader indexLoader, ReadUniqueKeyLoader uniqueKeyLoader,
            ReadTablePropertiesLoader tablePropertiesLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementLoader = nodeElementLoader;
        this.columnLoader = columnLoader;
        this.indexLoader = indexLoader;
        this.uniqueKeyLoader = uniqueKeyLoader;
        this.tablePropertiesLoader = tablePropertiesLoader;
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    public ERTable loadTable(Element element, LoadContext context, ERDiagram diagram, String database) {
        final ERTable table = new ERTable();
        table.setDiagram(diagram);
        table.setPhysicalName(getStringValue(element, "physical_name"));
        table.setLogicalName(getStringValue(element, "logical_name"));
        table.setDescription(getStringValue(element, "description"));
        nodeElementLoader.loadNodeElement(table, element, context);
        table.setConstraint(getStringValue(element, "constraint"));
        table.setPrimaryKeyName(getStringValue(element, "primary_key_name"));
        table.setOption(getStringValue(element, "option"));
        final List<ERColumn> columns = columnLoader.loadColumns(element, context, database, new ColumnIdBuilder() {
            @Override
            public String build(NormalColumn column) {
                return column.buildColumnId(table);
            }
        });
        table.setColumns(columns);
        final List<ERIndex> indexes = indexLoader.loadIndexes(element, table, context);
        table.setIndexes(indexes);
        final List<ComplexUniqueKey> complexUniqueKeyList = uniqueKeyLoader.loadComplexUniqueKeyList(element, table, context);
        table.setComplexUniqueKeyList(complexUniqueKeyList);
        tablePropertiesLoader.loadTableProperties((TableProperties) table.getTableViewProperties(), element, context);
        return table;
    }

    public ERVirtualTable loadVirtualTable(ERVirtualDiagram model, Element element, LoadContext context) {
        final String tableId = getStringValue(element, "id");
        final ERTable rawTable = (ERTable) context.nodeElementMap.get(tableId);
        final ERVirtualTable vtable = new ERVirtualTable(model, rawTable);
        assistLogic.loadLocation(vtable, element);
        assistLogic.loadFont(vtable, element);
        return vtable;
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }
}