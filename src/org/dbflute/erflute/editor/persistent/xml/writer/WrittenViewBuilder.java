package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.List;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenViewBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenDiagramWalkerBuilder nodeElementBuilder;
    protected final WrittenColumnBuilder columnBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenViewBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic, WrittenDiagramWalkerBuilder nodeElementBuilder,
            WrittenColumnBuilder columnBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementBuilder = nodeElementBuilder;
        this.columnBuilder = columnBuilder;
    }

    // ===================================================================================
    //                                                                               View
    //                                                                              ======
    public String buildView(ERView view, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<view>\n");
        xml.append("\t<physical_name>").append(escape(view.getPhysicalName())).append("</physical_name>\n");
        xml.append("\t<logical_name>").append(escape(view.getLogicalName())).append("</logical_name>\n");
        xml.append("\t<description>").append(escape(view.getDescription())).append("</description>\n");
        xml.append(tab(nodeElementBuilder.buildNodeElement(view, context)));
        xml.append("\t<sql>").append(escape(view.getSql())).append("</sql>\n");
        final List<ERColumn> columns = view.getColumns();
        xml.append(tab(columnBuilder.buildColumns(columns, context)));
        final ViewProperties viewProperties = (ViewProperties) view.getTableViewProperties();
        xml.append(tab(buildViewProperties(viewProperties, context)));
        xml.append("</view>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                     View Properties
    //                                                                     ===============
    private String buildViewProperties(ViewProperties viewProperties, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<view_properties>\n");
        final Integer tablespaceId = context.tablespaceMap.get(viewProperties.getTableSpace());
        if (tablespaceId != null) {
            xml.append("\t<tablespace_id>").append(tablespaceId).append("</tablespace_id>\n");
        }
        xml.append("<schema>").append(escape(viewProperties.getSchema())).append("</schema>\n");
        xml.append("</view_properties>\n");
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