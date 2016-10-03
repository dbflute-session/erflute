package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.List;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenTableBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenNodeElementBuilder nodeElementBuilder;
    protected final WrittenColumnBuilder columnBuilder;
    protected final WrittenIndexBuilder indexBuilder;
    protected final WrittenUniqueKeyBuilder uniqueKeyBuilder;
    protected final WrittenTablePropertiesBuilder tablePropertiesBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenTableBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic, WrittenNodeElementBuilder nodeElementBuilder,
            WrittenColumnBuilder columnBuilder, WrittenIndexBuilder indexBuilder, WrittenUniqueKeyBuilder uniqueKeyBuilder,
            WrittenTablePropertiesBuilder tablePropertiesBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementBuilder = nodeElementBuilder;
        this.columnBuilder = columnBuilder;
        this.indexBuilder = indexBuilder;
        this.uniqueKeyBuilder = uniqueKeyBuilder;
        this.tablePropertiesBuilder = tablePropertiesBuilder;
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    public String buildTable(ERTable table, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<table>\n");
        xml.append(tab(nodeElementBuilder.buildNodeElement(table, context)));
        xml.append("\t<physical_name>").append(escape(table.getPhysicalName())).append("</physical_name>\n");
        xml.append("\t<logical_name>").append(escape(table.getLogicalName())).append("</logical_name>\n");
        xml.append("\t<description>").append(escape(table.getDescription())).append("</description>\n");
        xml.append("\t<constraint>").append(escape(table.getConstraint())).append("</constraint>\n");
        xml.append("\t<primary_key_name>").append(escape(table.getPrimaryKeyName())).append("</primary_key_name>\n");
        xml.append("\t<option>").append(escape(table.getOption())).append("</option>\n");
        final List<ERColumn> columns = table.getColumns();
        xml.append(tab(columnBuilder.buildColumns(columns, context)));
        final List<ERIndex> indexes = table.getIndexes();
        xml.append(tab(indexBuilder.buildIndexes(indexes, context)));
        final List<ComplexUniqueKey> complexUniqueKeyList = table.getComplexUniqueKeyList();
        xml.append(tab(uniqueKeyBuilder.buildComplexUniqueKeyList(complexUniqueKeyList, context)));
        final TableProperties tableProperties = (TableProperties) table.getTableViewProperties();
        xml.append(tab(tablePropertiesBuilder.buildTableProperties(tableProperties, context)));
        xml.append("</table>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String tab(String str) {
        return assistLogic.tab(str);
    }

    public String escape(String s) {
        return assistLogic.escape(s);
    }
}