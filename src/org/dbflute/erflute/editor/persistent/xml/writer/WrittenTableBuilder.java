package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.List;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
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
    protected final WrittenDiagramWalkerBuilder walkerBuilder;
    protected final WrittenColumnBuilder columnBuilder;
    protected final WrittenIndexBuilder indexBuilder;
    protected final WrittenUniqueKeyBuilder uniqueKeyBuilder;
    protected final WrittenTablePropertiesBuilder tablePropertiesBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenTableBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic, WrittenDiagramWalkerBuilder walkerBuilder,
            WrittenColumnBuilder columnBuilder, WrittenIndexBuilder indexBuilder, WrittenUniqueKeyBuilder uniqueKeyBuilder,
            WrittenTablePropertiesBuilder tablePropertiesBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.walkerBuilder = walkerBuilder;
        this.columnBuilder = columnBuilder;
        this.indexBuilder = indexBuilder;
        this.uniqueKeyBuilder = uniqueKeyBuilder;
        this.tablePropertiesBuilder = tablePropertiesBuilder;
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    public String buildTable(ERTable table, PersistentContext context) {
        // name first to be read-able XML
        final StringBuilder xml = new StringBuilder();
        xml.append("<table>\n");
        setupName(table, xml); // name first to be read-able XML e.g. physical_name
        setupComment(table, xml); // me too
        xml.append(tab(walkerBuilder.buildWalker(table, context))); // should be before columns
        final String constraint = table.getConstraint();
        if (Srl.is_NotNull_and_NotEmpty(constraint)) {
            xml.append("\t<table_constraint>").append(escape(constraint)).append("</table_constraint>\n");
        }
        final String primaryKeyName = table.getPrimaryKeyName();
        if (Srl.is_NotNull_and_NotEmpty(primaryKeyName)) {
            xml.append("\t<primary_key_name>").append(escape(primaryKeyName)).append("</primary_key_name>\n");
        }
        final String option = table.getOption();
        if (Srl.is_NotNull_and_NotEmpty(option)) {
            xml.append("\t<option>").append(escape(option)).append("</option>\n");
        }
        final List<ERColumn> columns = table.getColumns();
        xml.append(tab(columnBuilder.buildColumns(columns, context)));
        final List<ERIndex> indexes = table.getIndexes();
        xml.append(tab(indexBuilder.buildIndexes(indexes, context)));
        final List<CompoundUniqueKey> complexUniqueKeyList = table.getCompoundUniqueKeyList();
        xml.append(tab(uniqueKeyBuilder.buildComplexUniqueKeyList(complexUniqueKeyList, context)));
        final TableProperties tableProperties = (TableProperties) table.getTableViewProperties();
        xml.append(tab(tablePropertiesBuilder.buildTableProperties(tableProperties, context)));
        xml.append("</table>\n");
        return xml.toString();
    }

    private void setupName(ERTable table, final StringBuilder xml) {
        xml.append("\t<physical_name>").append(escape(table.getPhysicalName())).append("</physical_name>\n");
        xml.append("\t<logical_name>").append(escape(table.getLogicalName())).append("</logical_name>\n");
    }

    private void setupComment(ERTable table, final StringBuilder xml) {
        xml.append("\t<description>").append(escape(table.getDescription())).append("</description>\n");
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
