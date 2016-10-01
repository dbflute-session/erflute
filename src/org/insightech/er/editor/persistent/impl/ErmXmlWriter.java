package org.insightech.er.editor.persistent.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.util.NameValue;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.db.impl.mysql.MySQLTableProperties;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.db.impl.postgres.PostgresTableProperties;
import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relationship;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.ERView;
import org.insightech.er.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.EnvironmentSetting;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.PageSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.editor.persistent.impl.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ErmXmlWriter {

    protected static final DateFormat DATE_FORMAT = PersistentXml.DATE_FORMAT;

    protected final PersistentXml persistentXml;

    public ErmXmlWriter(PersistentXml persistentXml) {
        this.persistentXml = persistentXml;
    }

    public InputStream write(ERDiagram diagram) throws IOException {
        InputStream inputStream = null;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String xml = this.createXML(diagram);
        out.write(xml.getBytes("UTF-8"));
        inputStream = new ByteArrayInputStream(out.toByteArray());
        return inputStream;
    }

    /*
    <dictionary>
        ...
    </dictionary>
    <tablespace_set>
    </tablespace_set>
    <contents>
        <table>
            <id>0</id>
            <height>-1</height>
            <width>-1</width>
                <font_name>Lucida Grande</font_name>
                <font_size>9</font_size>
            <x>752</x>
            <y>651</y>
            <color>
                <r>128</r>
                <g>128</g>
                <b>192</b>
            </color>
            <connections>
                <relation>
                    ...
                </relation>
            </connections>
            <physical_name>MEMBER_WITHDRAWAL</physical_name>
            <logical_name>会員退会情報</logical_name>
            <description>退会会員の退会に関する詳細な情報。&#x0D;退会会員のみデータが存在し、&quot;1 : 0..1&quot; のパターンの one-to-one である。&#x0D;共通カラムがあってバージョンNOがないパターン。&#x0D;基本的に更新は入らないが、重要なデータなので万が一のために更新系の共通カラムも。</description>
            <constraint></constraint>
            <primary_key_name></primary_key_name>
            <option></option>
            <columns>
                <normal_column>
                    <id>5</id>
                    <referenced_column>34</referenced_column>
                    <relation>0</relation>
                    <description></description>
                    <unique_key_name></unique_key_name>
                    <logical_name>メンバーID</logical_name>
                    <physical_name></physical_name>
                    ...
                </normal_column>
            </columns>
     */

    private String createXML(ERDiagram diagram) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<diagram>\n");
        if (diagram.getDbSetting() != null) {
            xml.append("\t<dbsetting>\n").append(tab(tab(buildDBSetting(diagram.getDbSetting())))).append("\t</dbsetting>\n");
        }
        if (diagram.getPageSetting() != null) {
            xml.append("\t<page_setting>\n")
                    .append(tab(tab(this.buildPageSetting(diagram.getPageSetting()))))
                    .append("\t</page_setting>\n");
        }
        xml.append("\t<category_index>").append(diagram.getCurrentCategoryIndex()).append("</category_index>\n");
        if (diagram.getCurrentErmodel() != null) {
            xml.append("\t<current_ermodel>").append(diagram.getCurrentErmodel().getName()).append("</current_ermodel>\n");
        }
        xml.append("\t<zoom>").append(diagram.getZoom()).append("</zoom>\n");
        xml.append("\t<x>").append(diagram.getX()).append("</x>\n");
        xml.append("\t<y>").append(diagram.getY()).append("</y>\n");
        appendColor(xml, "default_color", diagram.getDefaultColor());
        xml.append(tab(buildColor(diagram.getColor())));
        xml.append("\t<font_name>").append(escape(diagram.getFontName())).append("</font_name>\n");
        xml.append("\t<font_size>").append(diagram.getFontSize()).append("</font_size>\n");
        final PersistentContext context = persistentXml.getCurrentContext(diagram);
        xml.append(tab(buildDiagramContents(diagram.getDiagramContents(), context)));
        xml.append(tab(buildChangeTrackingList(diagram.getChangeTrackingList())));
        xml.append("</diagram>\n");
        return xml.toString();
    }

    private String buildDBSetting(DBSetting dbSetting) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<dbsystem>").append(escape(dbSetting.getDbsystem())).append("</dbsystem>\n");
        xml.append("<server>").append(escape(dbSetting.getServer())).append("</server>\n");
        xml.append("<port>").append(dbSetting.getPort()).append("</port>\n");
        xml.append("<database>").append(escape(dbSetting.getDatabase())).append("</database>\n");
        xml.append("<user>").append(escape(dbSetting.getUser())).append("</user>\n");
        xml.append("<password>").append(escape(dbSetting.getPassword())).append("</password>\n");
        xml.append("<use_default_driver>").append(dbSetting.isUseDefaultDriver()).append("</use_default_driver>\n");
        xml.append("<url>").append(escape(dbSetting.getUrl())).append("</url>\n");
        xml.append("<driver_class_name>").append(escape(dbSetting.getDriverClassName())).append("</driver_class_name>\n");
        return xml.toString();
    }

    private String buildPageSetting(PageSetting pageSetting) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<direction_horizontal>").append(pageSetting.isDirectionHorizontal()).append("</direction_horizontal>\n");
        xml.append("<scale>").append(pageSetting.getScale()).append("</scale>\n");
        xml.append("<paper_size>").append(escape(pageSetting.getPaperSize())).append("</paper_size>\n");
        xml.append("<top_margin>").append(pageSetting.getTopMargin()).append("</top_margin>\n");
        xml.append("<left_margin>").append(pageSetting.getLeftMargin()).append("</left_margin>\n");
        xml.append("<bottom_margin>").append(pageSetting.getBottomMargin()).append("</bottom_margin>\n");
        xml.append("<right_margin>").append(pageSetting.getRightMargin()).append("</right_margin>\n");
        return xml.toString();
    }

    private void appendColor(StringBuilder xml, String tagName, int[] defaultColor) {
        if (defaultColor == null) {
            return;
        }
        xml.append("\t<" + tagName + ">\n");
        xml.append("\t\t<r>").append(defaultColor[0]).append("</r>\n");
        xml.append("\t\t<g>").append(defaultColor[1]).append("</g>\n");
        xml.append("\t\t<b>").append(defaultColor[2]).append("</b>\n");
        xml.append("\t</" + tagName + ">\n");
    }

    private String buildDiagramContents(DiagramContents diagramContents, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append(biuldSettings(diagramContents.getSettings(), context));
        xml.append(buildDictionary(diagramContents.getDictionary(), context));
        xml.append(buildTablespace(diagramContents.getTablespaceSet(), context));
        xml.append(buildContents(diagramContents.getContents(), context));
        xml.append(buildERModel(diagramContents.getModelSet(), context));
        xml.append(buildColumnGroups(diagramContents.getGroups(), context));
        xml.append(buildSequence(diagramContents.getSequenceSet()));
        xml.append(buildTrigger(diagramContents.getTriggerSet()));
        return xml.toString();
    }

    // ===================================================================================
    //                                                                          Dictionary
    //                                                                          ==========
    private String buildDictionary(Dictionary dictionary, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<dictionary>\n");
        for (final Word word : dictionary.getWordList()) {
            xml.append(tab(this.buildWord(word, context)));
        }
        xml.append("</dictionary>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                               Word
    //                                                                              ======
    private String buildWord(Word word, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<word>\n");
        if (context != null) {
            xml.append("\t<id>").append(context.wordMap.get(word)).append("</id>\n");
        }
        xml.append("\t<length>").append(word.getTypeData().getLength()).append("</length>\n");
        xml.append("\t<decimal>").append(word.getTypeData().getDecimal()).append("</decimal>\n");
        final Integer arrayDimension = word.getTypeData().getArrayDimension();
        xml.append("\t<array>").append(word.getTypeData().isArray()).append("</array>\n");
        xml.append("\t<array_dimension>").append(arrayDimension).append("</array_dimension>\n");
        xml.append("\t<unsigned>").append(word.getTypeData().isUnsigned()).append("</unsigned>\n");
        xml.append("\t<args>").append(escape(word.getTypeData().getArgs())).append("</args>\n");
        xml.append("\t<description>").append(escape(word.getDescription())).append("</description>\n");
        xml.append("\t<logical_name>").append(escape(word.getLogicalName())).append("</logical_name>\n");
        xml.append("\t<physical_name>").append(escape(word.getPhysicalName())).append("</physical_name>\n");
        String type = "";
        if (word.getType() != null) {
            type = word.getType().getId();
        }
        xml.append("\t<type>").append(type).append("</type>\n");
        xml.append("</word>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                          Tablespace
    //                                                                          ==========
    private String buildTablespace(TablespaceSet tablespaceSet, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<tablespace_set>\n");
        for (final Tablespace tablespace : tablespaceSet) {
            xml.append(tab(doBuildTablespace(tablespace, context)));
        }
        xml.append("</tablespace_set>\n");
        return xml.toString();
    }

    private String doBuildTablespace(Tablespace tablespace, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<tablespace>\n");
        if (context != null) {
            xml.append("\t<id>").append(context.tablespaceMap.get(tablespace)).append("</id>\n");
        }
        xml.append("\t<name>").append(escape(tablespace.getName())).append("</name>\n");
        for (final Map.Entry<Environment, TablespaceProperties> entry : tablespace.getPropertiesMap().entrySet()) {
            final Environment environment = entry.getKey();
            final TablespaceProperties tablespaceProperties = entry.getValue();
            xml.append("\t<properties>\n");
            xml.append("\t\t<environment_id>").append(context.environmentMap.get(environment)).append("</environment_id>\n");
            if (tablespaceProperties instanceof DB2TablespaceProperties) {
                xml.append(tab(tab(this.createXML((DB2TablespaceProperties) tablespaceProperties))));
            } else if (tablespaceProperties instanceof MySQLTablespaceProperties) {
                xml.append(tab(tab(this.createXML((MySQLTablespaceProperties) tablespaceProperties))));
            } else if (tablespaceProperties instanceof OracleTablespaceProperties) {
                xml.append(tab(tab(this.createXML((OracleTablespaceProperties) tablespaceProperties))));
            } else if (tablespaceProperties instanceof PostgresTablespaceProperties) {
                xml.append(tab(tab(this.createXML((PostgresTablespaceProperties) tablespaceProperties))));
            }
            xml.append("\t</properties>\n");
        }
        xml.append("</tablespace>\n");
        return xml.toString();
    }

    private String createXML(DB2TablespaceProperties tablespace) {
        final StringBuilder xml = new StringBuilder();

        xml.append("<buffer_pool_name>").append(escape(tablespace.getBufferPoolName())).append("</buffer_pool_name>\n");
        xml.append("<container>").append(escape(tablespace.getContainer())).append("</container>\n");
        // xml.append("<container_device_path>").append(
        // escape(tablespace.getContainerDevicePath())).append(
        // "</container_device_path>\n");
        // xml.append("<container_directory_path>").append(
        // escape(tablespace.getContainerDirectoryPath())).append(
        // "</container_directory_path>\n");
        // xml.append("<container_file_path>").append(
        // escape(tablespace.getContainerFilePath())).append(
        // "</container_file_path>\n");
        // xml.append("<container_page_num>").append(
        // escape(tablespace.getContainerPageNum())).append(
        // "</container_page_num>\n");
        xml.append("<extent_size>").append(escape(tablespace.getExtentSize())).append("</extent_size>\n");
        xml.append("<managed_by>").append(escape(tablespace.getManagedBy())).append("</managed_by>\n");
        xml.append("<page_size>").append(escape(tablespace.getPageSize())).append("</page_size>\n");
        xml.append("<prefetch_size>").append(escape(tablespace.getPrefetchSize())).append("</prefetch_size>\n");
        xml.append("<type>").append(escape(tablespace.getType())).append("</type>\n");

        return xml.toString();
    }

    private String createXML(MySQLTablespaceProperties tablespace) {
        final StringBuilder xml = new StringBuilder();

        xml.append("<data_file>").append(escape(tablespace.getDataFile())).append("</data_file>\n");
        xml.append("<engine>").append(escape(tablespace.getEngine())).append("</engine>\n");
        xml.append("<extent_size>").append(escape(tablespace.getExtentSize())).append("</extent_size>\n");
        xml.append("<initial_size>").append(escape(tablespace.getInitialSize())).append("</initial_size>\n");
        xml.append("<log_file_group>").append(escape(tablespace.getLogFileGroup())).append("</log_file_group>\n");

        return xml.toString();
    }

    private String createXML(OracleTablespaceProperties tablespace) {
        final StringBuilder xml = new StringBuilder();

        xml.append("<auto_extend>").append(tablespace.isAutoExtend()).append("</auto_extend>\n");
        xml.append("<auto_segment_space_management>")
                .append(tablespace.isAutoSegmentSpaceManagement())
                .append("</auto_segment_space_management>\n");
        xml.append("<logging>").append(tablespace.isLogging()).append("</logging>\n");
        xml.append("<offline>").append(tablespace.isOffline()).append("</offline>\n");
        xml.append("<temporary>").append(tablespace.isTemporary()).append("</temporary>\n");
        xml.append("<auto_extend_max_size>").append(escape(tablespace.getAutoExtendMaxSize())).append("</auto_extend_max_size>\n");
        xml.append("<auto_extend_size>").append(escape(tablespace.getAutoExtendSize())).append("</auto_extend_size>\n");
        xml.append("<data_file>").append(escape(tablespace.getDataFile())).append("</data_file>\n");
        xml.append("<file_size>").append(escape(tablespace.getFileSize())).append("</file_size>\n");
        xml.append("<initial>").append(escape(tablespace.getInitial())).append("</initial>\n");
        xml.append("<max_extents>").append(escape(tablespace.getMaxExtents())).append("</max_extents>\n");
        xml.append("<min_extents>").append(escape(tablespace.getMinExtents())).append("</min_extents>\n");
        xml.append("<minimum_extent_size>").append(escape(tablespace.getMinimumExtentSize())).append("</minimum_extent_size>\n");
        xml.append("<next>").append(escape(tablespace.getNext())).append("</next>\n");
        xml.append("<pct_increase>").append(escape(tablespace.getPctIncrease())).append("</pct_increase>\n");

        return xml.toString();
    }

    private String createXML(PostgresTablespaceProperties tablespace) {
        final StringBuilder xml = new StringBuilder();

        xml.append("<location>").append(escape(tablespace.getLocation())).append("</location>\n");
        xml.append("<owner>").append(escape(tablespace.getOwner())).append("</owner>\n");

        return xml.toString();
    }

    // ===================================================================================
    //                                                                            Settings
    //                                                                            ========
    private String biuldSettings(Settings settings, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<settings>\n");
        xml.append("\t<database>").append(escape(settings.getDatabase())).append("</database>\n");
        xml.append("\t<capital>").append(settings.isCapital()).append("</capital>\n");
        xml.append("\t<table_style>").append(escape(settings.getTableStyle())).append("</table_style>\n");
        xml.append("\t<notation>").append(escape(settings.getNotation())).append("</notation>\n");
        xml.append("\t<notation_level>").append(settings.getNotationLevel()).append("</notation_level>\n");
        xml.append("\t<notation_expand_group>").append(settings.isNotationExpandGroup()).append("</notation_expand_group>\n");
        xml.append("\t<view_mode>").append(settings.getViewMode()).append("</view_mode>\n");
        xml.append("\t<outline_view_mode>").append(settings.getOutlineViewMode()).append("</outline_view_mode>\n");
        xml.append("\t<view_order_by>").append(settings.getViewOrderBy()).append("</view_order_by>\n");
        xml.append("\t<auto_ime_change>").append(settings.isAutoImeChange()).append("</auto_ime_change>\n");
        xml.append("\t<validate_physical_name>").append(settings.isValidatePhysicalName()).append("</validate_physical_name>\n");
        xml.append("\t<use_bezier_curve>").append(settings.isUseBezierCurve()).append("</use_bezier_curve>\n");
        xml.append("\t<suspend_validator>").append(settings.isSuspendValidator()).append("</suspend_validator>\n");
        xml.append("\t<titleFontEm>").append(settings.getTitleFontEm().toString()).append("</titleFontEm>\n");
        xml.append("\t<masterDataBasePath>").append(settings.getMasterDataBasePath().toString()).append("</masterDataBasePath>\n");
        xml.append(tab(createXML(settings.getExportSetting(), context)));
        xml.append(tab(createXML(settings.getCategorySetting(), context)));
        xml.append(tab(buildModelProperties(settings.getModelProperties(), context)));
        xml.append(tab(buildTableProperties((TableProperties) settings.getTableViewProperties(), context)));
        xml.append(tab(buildEnvironmentSetting(settings.getEnvironmentSetting(), context)));
        xml.append("</settings>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                Change Tracking List
    //                                                                ====================
    private String buildChangeTrackingList(ChangeTrackingList changeTrackingList) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<change_tracking_list>\n");
        for (final ChangeTracking changeTracking : changeTrackingList.getList()) {
            xml.append(tab(doBuildChangeTracking(changeTracking)));
        }
        xml.append("</change_tracking_list>\n");
        return xml.toString();
    }

    private String doBuildChangeTracking(ChangeTracking changeTracking) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<change_tracking>\n");
        xml.append("\t<updated_date>").append(DATE_FORMAT.format(changeTracking.getUpdatedDate())).append("</updated_date>\n");
        xml.append("\t<comment>").append(escape(changeTracking.getComment())).append("</comment>\n");
        final PersistentContext context = persistentXml.getChangeTrackingContext(changeTracking);
        xml.append(tab(this.buildDiagramContents(changeTracking.getDiagramContents(), context)));
        xml.append("</change_tracking>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                      Export Setting
    //                                                                      ==============
    private String createXML(ExportSetting exportSetting, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<export_setting>\n");
        xml.append("\t<category_name_to_export>")
                .append(escape(exportSetting.getCategoryNameToExport()))
                .append("</category_name_to_export>\n");
        xml.append("\t<ddl_output>").append(escape(exportSetting.getDdlOutput())).append("</ddl_output>\n");
        xml.append("\t<excel_output>").append(escape(exportSetting.getExcelOutput())).append("</excel_output>\n");
        xml.append("\t<excel_template>").append(escape(exportSetting.getExcelTemplate())).append("</excel_template>\n");
        xml.append("\t<image_output>").append(escape(exportSetting.getImageOutput())).append("</image_output>\n");
        xml.append("\t<put_diagram_on_excel>").append(exportSetting.isPutERDiagramOnExcel()).append("</put_diagram_on_excel>\n");
        xml.append("\t<use_logical_name_as_sheet>")
                .append(exportSetting.isUseLogicalNameAsSheet())
                .append("</use_logical_name_as_sheet>\n");
        xml.append("\t<open_after_saved>").append(exportSetting.isOpenAfterSaved()).append("</open_after_saved>\n");
        final DDLTarget ddlTarget = exportSetting.getDdlTarget();
        xml.append("\t<create_comment>").append(ddlTarget.createComment).append("</create_comment>\n");
        xml.append("\t<create_foreignKey>").append(ddlTarget.createForeignKey).append("</create_foreignKey>\n");
        xml.append("\t<create_index>").append(ddlTarget.createIndex).append("</create_index>\n");
        xml.append("\t<create_sequence>").append(ddlTarget.createSequence).append("</create_sequence>\n");
        xml.append("\t<create_table>").append(ddlTarget.createTable).append("</create_table>\n");
        xml.append("\t<create_tablespace>").append(ddlTarget.createTablespace).append("</create_tablespace>\n");
        xml.append("\t<create_trigger>").append(ddlTarget.createTrigger).append("</create_trigger>\n");
        xml.append("\t<create_view>").append(ddlTarget.createView).append("</create_view>\n");
        xml.append("\t<drop_index>").append(ddlTarget.dropIndex).append("</drop_index>\n");
        xml.append("\t<drop_sequence>").append(ddlTarget.dropSequence).append("</drop_sequence>\n");
        xml.append("\t<drop_table>").append(ddlTarget.dropTable).append("</drop_table>\n");
        xml.append("\t<drop_tablespace>").append(ddlTarget.dropTablespace).append("</drop_tablespace>\n");
        xml.append("\t<drop_trigger>").append(ddlTarget.dropTrigger).append("</drop_trigger>\n");
        xml.append("\t<drop_view>").append(ddlTarget.dropView).append("</drop_view>\n");
        xml.append("\t<inline_column_comment>").append(ddlTarget.inlineColumnComment).append("</inline_column_comment>\n");
        xml.append("\t<inline_table_comment>").append(ddlTarget.inlineTableComment).append("</inline_table_comment>\n");
        xml.append("\t<comment_value_description>").append(ddlTarget.commentValueDescription).append("</comment_value_description>\n");
        xml.append("\t<comment_value_logical_name>").append(ddlTarget.commentValueLogicalName).append("</comment_value_logical_name>\n");
        xml.append("\t<comment_value_logical_name_description>")
                .append(ddlTarget.commentValueLogicalNameDescription)
                .append("</comment_value_logical_name_description>\n");
        xml.append("\t<comment_replace_line_feed>").append(ddlTarget.commentReplaceLineFeed).append("</comment_replace_line_feed>\n");
        xml.append("\t<comment_replace_string>")
                .append(Format.null2blank(ddlTarget.commentReplaceString))
                .append("</comment_replace_string>\n");
        xml.append("</export_setting>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                   Category Settings
    //                                                                   =================
    private String createXML(CategorySetting categorySettings, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();

        xml.append("<category_settings>\n");
        xml.append("\t<free_layout>").append(categorySettings.isFreeLayout()).append("</free_layout>\n");
        xml.append("\t<show_referred_tables>").append(categorySettings.isShowReferredTables()).append("</show_referred_tables>\n");

        xml.append("\t<categories>\n");

        for (final Category category : categorySettings.getAllCategories()) {
            xml.append(tab(tab(this.createXML(category, categorySettings.isSelected(category), context))));
        }

        xml.append("\t</categories>\n");

        xml.append("</category_settings>\n");

        return xml.toString();
    }

    private String createXML(Category category, boolean isSelected, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<category>\n");
        xml.append(tab(buildNodeElement(category, context)));
        xml.append("\t<name>").append(escape(category.getName())).append("</name>\n");
        xml.append("\t<selected>").append(isSelected).append("</selected>\n");
        for (final NodeElement nodeElement : category.getContents()) {
            xml.append("\t<node_element>").append(context.nodeElementMap.get(nodeElement)).append("</node_element>\n");
        }
        xml.append("</category>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                              VGroup
    //                                                                              ======
    private String buildVGroup(VGroup group, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<group>\n");
        xml.append(tab(this.buildNodeElement(group, context)));
        xml.append("\t<name>").append(escape(group.getName())).append("</name>\n");
        //		xml.append("\t<selected>").append(isSelected).append("</selected>\n");
        for (final NodeElement nodeElement : group.getContents()) {
            xml.append("\t<node_element>")
                    .append(context.nodeElementMap.get(((ERVirtualTable) nodeElement).getRawTable()))
                    .append("</node_element>\n");
        }
        xml.append("</group>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                            Contents
    //                                                                            ========
    private String buildContents(NodeSet contents, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<contents>\n");
        for (final NodeElement content : contents) {
            String subxml = null;
            if (content instanceof ERTable) {
                subxml = this.buildTable((ERTable) content, context);
            } else if (content instanceof ERModel) {
                // do nothing
                //				subxml = this.createXMLERModel((ERModel) content, context);
            } else if (content instanceof Note) {
                //				subxml = this.createXML((Note) content, context);
            } else if (content instanceof ERView) {
                subxml = this.buildView((ERView) content, context);
            } else if (content instanceof InsertedImage) {
                subxml = this.buildImage((InsertedImage) content, context);
            } else if (content instanceof VGroup) {
                // do nothing
                //				subxml = this.createXML((VGroup) content, context);
            } else {
                throw new RuntimeException("not support " + content);
            }
            if (subxml != null)
                xml.append(tab(subxml));
        }
        xml.append("</contents>\n");
        return xml.toString();
    }

    private String buildERModel(ERModelSet modelSet, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<ermodels>\n");

        for (final ERModel erModel : modelSet) {
            xml.append("\t<ermodel>\n");
            xml.append("\t\t<id>").append(context.ermodelMap.get(erModel)).append("</id>\n");
            xml.append("\t\t<name>").append(erModel.getName()).append("</name>\n");
            appendColor(xml, "color", erModel.getColor());

            xml.append("\t\t<vtables>\n");
            for (final ERVirtualTable table : erModel.getTables()) {
                xml.append("\t\t\t<vtable>\n");
                xml.append("\t\t\t\t<id>").append(context.nodeElementMap.get(table.getRawTable())).append("</id>\n");
                xml.append("\t\t\t\t<x>").append(table.getX()).append("</x>\n");
                xml.append("\t\t\t\t<y>").append(table.getY()).append("</y>\n");
                appendFont(xml, table);
                xml.append("\t\t\t</vtable>\n");
            }
            xml.append("\t\t</vtables>\n");

            xml.append("\t\t<groups>\n");
            for (final VGroup group : erModel.getGroups()) {
                xml.append(buildVGroup(group, context));
            }
            xml.append("\t\t</groups>\n");

            xml.append("\t\t<notes>\n");
            for (final Note note : erModel.getNotes()) {
                xml.append(buildNote(note, context));
            }
            xml.append("\t\t</notes>\n");

            xml.append("\t</ermodel>\n");
        }

        xml.append("</ermodels>\n");
        return xml.toString();
    }

    private void appendFont(StringBuilder xml, NodeElement nodeElement) {
        xml.append("\t<font_name>").append(escape(nodeElement.getFontName())).append("</font_name>\n");
        xml.append("\t<font_size>").append(nodeElement.getFontSize()).append("</font_size>\n");
    }

    //	private String createXMLERModel(ERModel erModel, PersistentContext context) {
    //		StringBuilder xml = new StringBuilder();
    ////		xml.append("<ermodels>\n");
    ////
    ////		for (ERModel erModel : ermodels) {
    //			xml.append("\t<ermodel>\n");
    //			xml.append("\t\t<id>").append(context.nodeElementMap.get(erModel)).append("</id>\n");
    //			xml.append("\t\t<name>").append(erModel.getName()).append("</name>\n");
    //			xml.append("\t\t<vtables>\n");
    //			for (ERVirtualTable table : erModel.getTables()) {
    //				xml.append("\t\t\t<vtable>\n");
    //				xml.append("\t\t\t\t<id>").append(context.nodeElementMap.get(table.getRawTable())).append("</id>\n");
    //				xml.append("\t\t\t\t<x>").append(table.getX()).append("</x>\n");
    //				xml.append("\t\t\t\t<y>").append(table.getY()).append("</y>\n");
    //				xml.append("\t\t\t</vtable>\n");
    //			}
    //			xml.append("\t\t</vtables>\n");
    //			xml.append("\t</ermodel>\n");
    ////		}
    ////
    ////		xml.append("</ermodels>\n");
    //		return xml.toString();
    //	}

    private String buildNodeElement(NodeElement nodeElement, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<id>").append(Format.toString(context.nodeElementMap.get(nodeElement))).append("</id>\n");
        xml.append("<height>").append(nodeElement.getHeight()).append("</height>\n");
        xml.append("<width>").append(nodeElement.getWidth()).append("</width>\n");
        xml.append("\t<font_name>").append(escape(nodeElement.getFontName())).append("</font_name>\n");
        xml.append("\t<font_size>").append(nodeElement.getFontSize()).append("</font_size>\n");
        xml.append("<x>").append(nodeElement.getX()).append("</x>\n");
        xml.append("<y>").append(nodeElement.getY()).append("</y>\n");
        xml.append(this.buildColor(nodeElement.getColor()));
        final List<ConnectionElement> incomings = nodeElement.getIncomings();
        xml.append(this.buildConnections(incomings, context));
        return xml.toString();
    }

    private String buildColor(int[] colors) {
        final StringBuilder xml = new StringBuilder();
        if (colors != null) {
            xml.append("<color>\n");
            xml.append("\t<r>").append(colors[0]).append("</r>\n");
            xml.append("\t<g>").append(colors[1]).append("</g>\n");
            xml.append("\t<b>").append(colors[2]).append("</b>\n");
            xml.append("</color>\n");
        }
        return xml.toString();
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    private String buildTable(ERTable table, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<table>\n");
        xml.append(tab(buildNodeElement(table, context)));
        xml.append("\t<physical_name>").append(escape(table.getPhysicalName())).append("</physical_name>\n");
        xml.append("\t<logical_name>").append(escape(table.getLogicalName())).append("</logical_name>\n");
        xml.append("\t<description>").append(escape(table.getDescription())).append("</description>\n");
        xml.append("\t<constraint>").append(escape(table.getConstraint())).append("</constraint>\n");
        xml.append("\t<primary_key_name>").append(escape(table.getPrimaryKeyName())).append("</primary_key_name>\n");
        xml.append("\t<option>").append(escape(table.getOption())).append("</option>\n");
        final List<ERColumn> columns = table.getColumns();
        xml.append(tab(buildColumns(columns, context)));
        final List<ERIndex> indexes = table.getIndexes();
        xml.append(tab(buildIndexes(indexes, context)));
        final List<ComplexUniqueKey> complexUniqueKeyList = table.getComplexUniqueKeyList();
        xml.append(tab(buildComplexUniqueKeyList(complexUniqueKeyList, context)));
        final TableProperties tableProperties = (TableProperties) table.getTableViewProperties();
        xml.append(tab(buildTableProperties(tableProperties, context)));
        xml.append("</table>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                               View
    //                                                                              ======
    private String buildView(ERView view, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<view>\n");
        xml.append(tab(this.buildNodeElement(view, context)));
        xml.append("\t<physical_name>").append(escape(view.getPhysicalName())).append("</physical_name>\n");
        xml.append("\t<logical_name>").append(escape(view.getLogicalName())).append("</logical_name>\n");
        xml.append("\t<description>").append(escape(view.getDescription())).append("</description>\n");
        xml.append("\t<sql>").append(escape(view.getSql())).append("</sql>\n");
        final List<ERColumn> columns = view.getColumns();
        xml.append(tab(buildColumns(columns, context)));
        final ViewProperties viewProperties = (ViewProperties) view.getTableViewProperties();
        xml.append(tab(buildViewProperties(viewProperties, context)));
        xml.append("</view>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                    Model Properties
    //                                                                    ================
    private String buildModelProperties(ModelProperties modelProperties, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<model_properties>\n");
        xml.append(tab(this.buildNodeElement(modelProperties, context)));
        xml.append("\t<display>").append(modelProperties.isDisplay()).append("</display>\n");
        xml.append("\t<creation_date>").append(DATE_FORMAT.format(modelProperties.getCreationDate())).append("</creation_date>\n");
        xml.append("\t<updated_date>").append(DATE_FORMAT.format(modelProperties.getUpdatedDate())).append("</updated_date>\n");
        for (final NameValue property : modelProperties.getProperties()) {
            xml.append(tab(doBuildModelProperty(property, context)));
        }
        xml.append("</model_properties>\n");
        return xml.toString();
    }

    private String doBuildModelProperty(NameValue property, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<model_property>\n");
        xml.append("\t<name>").append(escape(property.getName())).append("</name>\n");
        xml.append("\t<value>").append(escape(property.getValue())).append("</value>\n");
        xml.append("</model_property>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                              Note
    //                                                                             =======
    private String buildNote(Note note, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<note>\n");
        xml.append(tab(buildNodeElement(note, context)));
        xml.append("\t<text>").append(escape(note.getText())).append("</text>\n");
        xml.append("</note>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                               Image
    //                                                                               =====
    private String buildImage(InsertedImage insertedImage, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<image>\n");
        xml.append(tab(buildNodeElement(insertedImage, context)));
        xml.append("\t<data>").append(insertedImage.getBase64EncodedData()).append("</data>\n");
        xml.append("\t<hue>").append(insertedImage.getHue()).append("</hue>\n");
        xml.append("\t<saturation>").append(insertedImage.getSaturation()).append("</saturation>\n");
        xml.append("\t<brightness>").append(insertedImage.getBrightness()).append("</brightness>\n");
        xml.append("\t<alpha>").append(insertedImage.getAlpha()).append("</alpha>\n");
        xml.append("\t<fix_aspect_ratio>").append(insertedImage.isFixAspectRatio()).append("</fix_aspect_ratio>\n");
        xml.append("</image>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                              Column
    //                                                                              ======
    private String buildColumns(List<ERColumn> columns, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<columns>\n");
        for (final ERColumn column : columns) {
            if (column instanceof ColumnGroup) {
                xml.append(tab(doBuildColumnsColumnGroup((ColumnGroup) column, context)));
            } else if (column instanceof NormalColumn) {
                xml.append(tab(doBuildNormalColumn((NormalColumn) column, context)));
            }
        }
        xml.append("</columns>\n");
        return xml.toString();
    }

    private String doBuildColumnsColumnGroup(ColumnGroup columnGroup, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<column_group>").append(context.columnGroupMap.get(columnGroup)).append("</column_group>\n");
        return xml.toString();
    }

    private String doBuildNormalColumn(NormalColumn normalColumn, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<normal_column>\n");
        Integer wordId = null;
        if (context != null) {
            wordId = context.wordMap.get(normalColumn.getWord());
            if (wordId != null) {
                xml.append("\t<word_id>").append(wordId).append("</word_id>\n");
            }
            xml.append("\t<id>").append(context.columnMap.get(normalColumn)).append("</id>\n");
            for (final NormalColumn referencedColumn : normalColumn.getReferencedColumnList()) {
                xml.append("\t<referenced_column>")
                        .append(Format.toString(context.columnMap.get(referencedColumn)))
                        .append("</referenced_column>\n");
            }
            for (final Relationship relation : normalColumn.getRelationshipList()) {
                xml.append("\t<relation>").append(context.connectionMap.get(relation)).append("</relation>\n");
            }
        }

        final String description = normalColumn.getForeignKeyDescription();
        final String logicalName = normalColumn.getForeignKeyLogicalName();
        final String physicalName = normalColumn.getForeignKeyPhysicalName();
        final SqlType sqlType = normalColumn.getType();

        xml.append("\t<description>").append(escape(description)).append("</description>\n");
        xml.append("\t<unique_key_name>").append(escape(normalColumn.getUniqueKeyName())).append("</unique_key_name>\n");
        xml.append("\t<logical_name>").append(escape(logicalName)).append("</logical_name>\n");
        xml.append("\t<physical_name>").append(escape(physicalName)).append("</physical_name>\n");

        String type = "";
        if (sqlType != null) {
            type = sqlType.getId();
        }
        xml.append("\t<type>").append(type).append("</type>\n");
        xml.append("\t<constraint>").append(escape(normalColumn.getConstraint())).append("</constraint>\n");
        xml.append("\t<default_value>").append(escape(normalColumn.getDefaultValue())).append("</default_value>\n");
        xml.append("\t<auto_increment>").append(normalColumn.isAutoIncrement()).append("</auto_increment>\n");
        xml.append("\t<foreign_key>").append(normalColumn.isForeignKey()).append("</foreign_key>\n");
        xml.append("\t<not_null>").append(normalColumn.isNotNull()).append("</not_null>\n");
        xml.append("\t<primary_key>").append(normalColumn.isPrimaryKey()).append("</primary_key>\n");
        xml.append("\t<unique_key>").append(normalColumn.isUniqueKey()).append("</unique_key>\n");
        xml.append("\t<character_set>").append(escape(normalColumn.getCharacterSet())).append("</character_set>\n");
        xml.append("\t<collation>").append(escape(normalColumn.getCollation())).append("</collation>\n");
        xml.append(tab(this.createXML(normalColumn.getAutoIncrementSetting())));
        xml.append("</normal_column>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                         Connections
    //                                                                         ===========
    private String buildConnections(List<ConnectionElement> incomings, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<connections>\n");
        for (final ConnectionElement connection : incomings) {
            if (connection instanceof CommentConnection) {
                xml.append(tab(this.buildCommentConnection((CommentConnection) connection, context)));
            } else if (connection instanceof Relationship) {
                xml.append(tab(this.buildRelationship((Relationship) connection, context)));
            }
        }
        xml.append("</connections>\n");
        return xml.toString();
    }

    private String buildConnectionElement(ConnectionElement connection, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<id>").append(context.connectionMap.get(connection)).append("</id>\n");
        xml.append("<source>").append(context.nodeElementMap.get(connection.getSource())).append("</source>\n");
        xml.append("<target>").append(context.nodeElementMap.get(connection.getTarget())).append("</target>\n");
        for (final Bendpoint bendpoint : connection.getBendpoints()) {
            xml.append(tab(this.buildBendPoint(bendpoint)));
        }
        return xml.toString();
    }

    private String buildBendPoint(Bendpoint bendpoint) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<bendpoint>\n");
        xml.append("\t<relative>").append(bendpoint.isRelative()).append("</relative>\n");
        xml.append("\t<x>").append(bendpoint.getX()).append("</x>\n");
        xml.append("\t<y>").append(bendpoint.getY()).append("</y>\n");
        xml.append("</bendpoint>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                  Comment Connection
    //                                                                  ==================
    private String buildCommentConnection(CommentConnection connection, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<comment_connection>\n");
        xml.append(tab(buildConnectionElement(connection, context)));
        xml.append("</comment_connection>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Relationship
    //                                                                        ============
    private String buildRelationship(Relationship relation, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<relation>\n");
        xml.append(tab(this.buildConnectionElement(relation, context)));
        xml.append("\t<child_cardinality>").append(escape(relation.getChildCardinality())).append("</child_cardinality>\n");
        xml.append("\t<parent_cardinality>").append(escape(relation.getParentCardinality())).append("</parent_cardinality>\n");
        xml.append("\t<reference_for_pk>").append(relation.isReferenceForPK()).append("</reference_for_pk>\n");
        xml.append("\t<name>").append(escape(relation.getName())).append("</name>\n");
        xml.append("\t<on_delete_action>").append(escape(relation.getOnDeleteAction())).append("</on_delete_action>\n");
        xml.append("\t<on_update_action>").append(escape(relation.getOnUpdateAction())).append("</on_update_action>\n");
        xml.append("\t<source_xp>").append(relation.getSourceXp()).append("</source_xp>\n");
        xml.append("\t<source_yp>").append(relation.getSourceYp()).append("</source_yp>\n");
        xml.append("\t<target_xp>").append(relation.getTargetXp()).append("</target_xp>\n");
        xml.append("\t<target_yp>").append(relation.getTargetYp()).append("</target_yp>\n");
        xml.append("\t<referenced_column>").append(context.columnMap.get(relation.getReferencedColumn())).append("</referenced_column>\n");
        xml.append("\t<referenced_complex_unique_key>")
                .append(context.complexUniqueKeyMap.get(relation.getReferencedComplexUniqueKey()))
                .append("</referenced_complex_unique_key>\n");
        xml.append("</relation>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                             Indexes
    //                                                                             =======
    private String buildIndexes(List<ERIndex> indexes, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<indexes>\n");
        for (final ERIndex index : indexes) {
            xml.append(tab(doBuildIndex(index, context)));
        }
        xml.append("</indexes>\n");
        return xml.toString();
    }

    private String doBuildIndex(ERIndex index, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<inidex>\n"); // typo?
        xml.append("\t<full_text>").append(index.isFullText()).append("</full_text>\n");
        xml.append("\t<non_unique>").append(index.isNonUnique()).append("</non_unique>\n");
        xml.append("\t<name>").append(escape(index.getName())).append("</name>\n");
        xml.append("\t<type>").append(escape(index.getType())).append("</type>\n");
        xml.append("\t<description>").append(escape(index.getDescription())).append("</description>\n");
        xml.append("\t<columns>\n");
        final List<Boolean> descs = index.getDescs();
        int count = 0;
        for (final ERColumn column : index.getColumns()) {
            xml.append("\t\t<column>\n");
            xml.append("\t\t\t<id>").append(context.columnMap.get(column)).append("</id>\n");
            Boolean desc = Boolean.FALSE;
            if (descs.size() > count) {
                desc = descs.get(count);
            }
            xml.append("\t\t\t<desc>").append(desc).append("</desc>\n");
            xml.append("\t\t</column>\n");
            count++;
        }
        xml.append("\t</columns>\n");
        xml.append("</inidex>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                             Complex Unique Key List
    //                                                             =======================
    private String buildComplexUniqueKeyList(List<ComplexUniqueKey> complexUniqueKeyList, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<complex_unique_key_list>\n");
        for (final ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
            xml.append(tab(doBuildComplexUniqueKey(complexUniqueKey, context)));
        }
        xml.append("</complex_unique_key_list>\n");
        return xml.toString();
    }

    private String doBuildComplexUniqueKey(ComplexUniqueKey complexUniqueKey, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<complex_unique_key>\n");
        xml.append("\t<id>").append(context.complexUniqueKeyMap.get(complexUniqueKey)).append("</id>\n");
        xml.append("\t<name>").append(Format.null2blank(complexUniqueKey.getUniqueKeyName())).append("</name>\n");
        xml.append("\t<columns>\n");
        for (final NormalColumn column : complexUniqueKey.getColumnList()) {
            xml.append("\t\t<column>\n");
            xml.append("\t\t\t<id>").append(context.columnMap.get(column)).append("</id>\n");
            xml.append("\t\t</column>\n");
        }
        xml.append("\t</columns>\n");
        xml.append("</complex_unique_key>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                 Environment Setting
    //                                                                 ===================
    private String buildEnvironmentSetting(EnvironmentSetting environmentSetting, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<environment_setting>\n");
        for (final Environment environment : environmentSetting.getEnvironments()) {
            xml.append("\t<environment>\n");
            final Integer environmentId = context.environmentMap.get(environment);
            xml.append("\t\t<id>").append(environmentId).append("</id>\n");
            xml.append("\t\t<name>").append(environment.getName()).append("</name>\n");
            xml.append("\t</environment>\n");
        }
        xml.append("</environment_setting>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                    Table Properties
    //                                                                    ================
    private String buildTableProperties(TableProperties tableProperties, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<table_properties>\n");
        final Integer tablespaceId = context.tablespaceMap.get(tableProperties.getTableSpace());
        if (tablespaceId != null) {
            xml.append("\t<tablespace_id>").append(tablespaceId).append("</tablespace_id>\n");
        }
        xml.append("\t<schema>").append(escape(tableProperties.getSchema())).append("</schema>\n");
        if (tableProperties instanceof MySQLTableProperties) {
            xml.append(tab(doBuildMySQLTableProperties((MySQLTableProperties) tableProperties)));
        } else if (tableProperties instanceof PostgresTableProperties) {
            xml.append(tab(doBuildPostgresTableProperties((PostgresTableProperties) tableProperties)));
        }
        xml.append("</table_properties>\n");
        return xml.toString();
    }

    private String doBuildMySQLTableProperties(MySQLTableProperties tableProperties) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<character_set>").append(escape(tableProperties.getCharacterSet())).append("</character_set>\n");
        xml.append("<collation>").append(escape(tableProperties.getCollation())).append("</collation>\n");
        xml.append("<storage_engine>").append(escape(tableProperties.getStorageEngine())).append("</storage_engine>\n");
        xml.append("<primary_key_length_of_text>")
                .append(tableProperties.getPrimaryKeyLengthOfText())
                .append("</primary_key_length_of_text>\n");
        return xml.toString();
    }

    private String doBuildPostgresTableProperties(PostgresTableProperties tableProperties) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<without_oids>").append(tableProperties.isWithoutOIDs()).append("</without_oids>\n");
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
    //                                                                       Column Groups
    //                                                                       =============
    private String buildColumnGroups(GroupSet columnGroups, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<column_groups>\n");
        for (final ColumnGroup columnGroup : columnGroups) {
            xml.append(tab(tab(doBuildColumnGroup(columnGroup, context))));
        }
        xml.append("</column_groups>\n");
        return xml.toString();
    }

    private String doBuildColumnGroup(ColumnGroup columnGroup, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<column_group>\n");
        xml.append("\t<id>").append(context.columnGroupMap.get(columnGroup)).append("</id>\n");
        xml.append("\t<group_name>").append(escape(columnGroup.getGroupName())).append("</group_name>\n");
        xml.append("\t<columns>\n");
        for (final NormalColumn normalColumn : columnGroup.getColumns()) {
            xml.append(tab(tab(this.doBuildNormalColumn(normalColumn, context))));
        }
        xml.append("\t</columns>\n");
        xml.append("</column_group>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    private String buildSequence(SequenceSet sequenceSet) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<sequence_set>\n");
        for (final Sequence sequence : sequenceSet) {
            xml.append(tab(this.createXML(sequence)));
        }
        xml.append("</sequence_set>\n");
        return xml.toString();
    }

    private String createXML(Sequence sequence) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<sequence>\n");
        xml.append("\t<name>").append(escape(sequence.getName())).append("</name>\n");
        xml.append("\t<schema>").append(escape(sequence.getSchema())).append("</schema>\n");
        xml.append("\t<increment>").append(Format.toString(sequence.getIncrement())).append("</increment>\n");
        xml.append("\t<min_value>").append(Format.toString(sequence.getMinValue())).append("</min_value>\n");
        xml.append("\t<max_value>").append(Format.toString(sequence.getMaxValue())).append("</max_value>\n");
        xml.append("\t<start>").append(Format.toString(sequence.getStart())).append("</start>\n");
        xml.append("\t<cache>").append(Format.toString(sequence.getCache())).append("</cache>\n");
        xml.append("\t<cycle>").append(sequence.isCycle()).append("</cycle>\n");
        xml.append("\t<order>").append(sequence.isOrder()).append("</order>\n");
        xml.append("\t<description>").append(escape(sequence.getDescription())).append("</description>\n");
        xml.append("\t<data_type>").append(escape(sequence.getDataType())).append("</data_type>\n");
        xml.append("\t<decimal_size>").append(Format.toString(sequence.getDecimalSize())).append("</decimal_size>\n");
        xml.append("</sequence>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                             Trigger
    //                                                                             =======
    private String buildTrigger(TriggerSet triggerSet) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<trigger_set>\n");
        for (final Trigger trigger : triggerSet) {
            xml.append(tab(doBuildTrigger(trigger)));
        }
        xml.append("</trigger_set>\n");
        return xml.toString();
    }

    private String doBuildTrigger(Trigger trigger) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<trigger>\n");
        xml.append("\t<name>").append(escape(trigger.getName())).append("</name>\n");
        xml.append("\t<schema>").append(escape(trigger.getSchema())).append("</schema>\n");
        xml.append("\t<sql>").append(escape(trigger.getSql())).append("</sql>\n");
        xml.append("\t<description>").append(escape(trigger.getDescription())).append("</description>\n");
        xml.append("</trigger>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private static String tab(String str) {
        str = str.replaceAll("\n\t", "\n\t\t");
        str = str.replaceAll("\n<", "\n\t<");
        return "\t" + str;
    }

    public static String escape(String s) {
        if (s == null) {
            return "";
        }

        final StringBuilder result = new StringBuilder(s.length() + 10);
        for (int i = 0; i < s.length(); ++i) {
            appendEscapedChar(result, s.charAt(i));
        }
        return result.toString();
    }

    private static void appendEscapedChar(StringBuilder buffer, char c) {
        final String replacement = getReplacement(c);
        if (replacement != null) {
            buffer.append('&');
            buffer.append(replacement);
            buffer.append(';');
        } else {
            buffer.append(c);
        }
    }

    private static String getReplacement(char c) {
        // Encode special XML characters into the equivalent character
        // references.
        // The first five are defined by default for all XML documents.
        // The next three (#xD, #xA, #x9) are encoded to avoid them
        // being converted to spaces on de-serialization
        switch (c) {
        case '<':
            return "lt"; //$NON-NLS-1$
        case '>':
            return "gt"; //$NON-NLS-1$
        case '"':
            return "quot"; //$NON-NLS-1$
        case '\'':
            return "apos"; //$NON-NLS-1$
        case '&':
            return "amp"; //$NON-NLS-1$
        case '\r':
            return "#x0D"; //$NON-NLS-1$
        case '\n':
            return "#x0A"; //$NON-NLS-1$
        case '\u0009':
            return "#x09"; //$NON-NLS-1$
        }
        return null;
    }
}