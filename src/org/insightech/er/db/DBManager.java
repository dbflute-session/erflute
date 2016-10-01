package org.insightech.er.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;

import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

/**
 * @author modified by jflute (originated in ermaster)
 */
public interface DBManager {

    int SUPPORT_AUTO_INCREMENT = 0;
    int SUPPORT_AUTO_INCREMENT_SETTING = 1;
    int SUPPORT_DESC_INDEX = 2;
    int SUPPORT_FULLTEXT_INDEX = 3;
    int SUPPORT_SCHEMA = 4;
    int SUPPORT_SEQUENCE = 5;

    String getId();

    String getURL(String serverName, String dbName, int port);

    int getDefaultPort();

    String getDriverClassName();

    Class<Driver> getDriverClass(String driverClassName);

    SqlTypeManager getSqlTypeManager();

    TableProperties createTableProperties(TableProperties tableProperties);

    TablespaceProperties createTablespaceProperties();

    TablespaceProperties checkTablespaceProperties(TablespaceProperties tablespaceProperties);

    DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon);

    boolean isSupported(int support);

    boolean doesNeedURLDatabaseName();

    boolean doesNeedURLServerName();

    boolean isReservedWord(String str);

    List<String> getIndexTypeList(ERTable table);

    PreImportFromDBManager getPreTableImportManager();

    ImportFromDBManager getTableImportManager();

    PreTableExportManager getPreTableExportManager();

    String[] getCurrentTimeValue();

    List<String> getImportSchemaList(Connection con) throws SQLException;

    List<String> getSystemSchemaList();

    BigDecimal getSequenceMaxValue();
}
