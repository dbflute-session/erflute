package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.Map;

import org.dbflute.erflute.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.dbflute.erflute.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.dbflute.erflute.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.dbflute.erflute.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenTablespaceBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenTablespaceBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                          Tablespace
    //                                                                          ==========
    public String buildTablespace(TablespaceSet tablespaceSet, PersistentContext context) {
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
                xml.append(tab(tab(this.buildDB2TablespaceProperties((DB2TablespaceProperties) tablespaceProperties))));
            } else if (tablespaceProperties instanceof MySQLTablespaceProperties) {
                xml.append(tab(tab(this.buildMySQLTablespaceProperties((MySQLTablespaceProperties) tablespaceProperties))));
            } else if (tablespaceProperties instanceof OracleTablespaceProperties) {
                xml.append(tab(tab(this.buildOracleTablespaceProperties((OracleTablespaceProperties) tablespaceProperties))));
            } else if (tablespaceProperties instanceof PostgresTablespaceProperties) {
                xml.append(tab(tab(this.buildPostgresTablespaceProperties((PostgresTablespaceProperties) tablespaceProperties))));
            }
            xml.append("\t</properties>\n");
        }
        xml.append("</tablespace>\n");
        return xml.toString();
    }

    private String buildDB2TablespaceProperties(DB2TablespaceProperties tablespace) {
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

    private String buildMySQLTablespaceProperties(MySQLTablespaceProperties tablespace) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<data_file>").append(escape(tablespace.getDataFile())).append("</data_file>\n");
        xml.append("<engine>").append(escape(tablespace.getEngine())).append("</engine>\n");
        xml.append("<extent_size>").append(escape(tablespace.getExtentSize())).append("</extent_size>\n");
        xml.append("<initial_size>").append(escape(tablespace.getInitialSize())).append("</initial_size>\n");
        xml.append("<log_file_group>").append(escape(tablespace.getLogFileGroup())).append("</log_file_group>\n");
        return xml.toString();
    }

    private String buildOracleTablespaceProperties(OracleTablespaceProperties tablespace) {
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

    private String buildPostgresTablespaceProperties(PostgresTablespaceProperties tablespace) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<location>").append(escape(tablespace.getLocation())).append("</location>\n");
        xml.append("<owner>").append(escape(tablespace.getOwner())).append("</owner>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String tab(String str) {
        return assistLogic.tab(str);
    }

    private String escape(String s) {
        return assistLogic.escape(s);
    }
}