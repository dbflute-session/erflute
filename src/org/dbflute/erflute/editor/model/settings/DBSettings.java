package org.dbflute.erflute.editor.model.settings;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;

public class DBSettings implements Serializable, Comparable<DBSettings> {

    private static final long serialVersionUID = 1L;

    private final String dbsystem;
    private final String server;
    private final int port;
    private final String database;
    private final String user;
    private transient String password;
    private final boolean useDefaultDriver;
    private final String url;
    private final String driverClassName;

    public String getDbsystem() {
        return dbsystem;
    }

    public DBSettings(String dbsystem, String server, int port, String database,
            String user, String password, boolean useDefaultDriver, String url, String driverClassName) {
        this.dbsystem = dbsystem;
        this.server = server;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.useDefaultDriver = useDefaultDriver;
        this.url = url;
        this.driverClassName = driverClassName;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isUseDefaultDriver() {
        return useDefaultDriver;
    }

    public String getUrl() {
        return url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    @Override
    public int compareTo(DBSettings other) {
        int compareTo = getDbsystem().compareTo(other.getDbsystem());
        if (compareTo != 0) {
            return compareTo;
        }

        compareTo = getServer().compareTo(other.getServer());
        if (compareTo != 0) {
            return compareTo;
        }

        if (getPort() != other.getPort()) {
            return getPort() - other.getPort();
        }

        compareTo = getDatabase().compareTo(other.getDatabase());
        if (compareTo != 0) {
            return compareTo;
        }

        compareTo = getUser().compareTo(other.getUser());
        if (compareTo != 0) {
            return compareTo;
        }

        compareTo = getServer().compareTo(other.getServer());
        if (compareTo != 0) {
            return compareTo;
        }

        compareTo = getPassword().compareTo(other.getPassword());
        if (compareTo != 0) {
            return compareTo;
        }

        if (isUseDefaultDriver() != other.isUseDefaultDriver()) {
            if (isUseDefaultDriver()) {
                return -1;
            } else {
                return 1;
            }
        }

        compareTo = getUrl().compareTo(other.getUrl());
        if (compareTo != 0) {
            return compareTo;
        }

        compareTo = getDriverClassName().compareTo(other.getDriverClassName());
        if (compareTo != 0) {
            return compareTo;
        }

        return 0;
    }

    public String getTableNameWithSchema(String tableName, String schema) {
        if (schema == null) {
            return Format.null2blank(tableName);
        }

        final DBManager dbManager = DBManagerFactory.getDBManager(dbsystem);

        if (!dbManager.isSupported(DBManager.SUPPORT_SCHEMA)) {
            return Format.null2blank(tableName);
        }

        return schema + "." + Format.null2blank(tableName);
    }

    public Connection connect() throws InputException, InstantiationException, IllegalAccessException, SQLException {
        final DBManager manager = DBManagerFactory.getDBManager(getDbsystem());
        final Class<Driver> driverClass = manager.getDriverClass(getDriverClassName());

        if (driverClass == null) {
            throw new InputException("error.jdbc.driver.not.found");
        }

        final Driver driver = driverClass.newInstance();

        final Properties info = new Properties();
        if (getUser() != null) {
            info.put("user", getUser());
        }
        if (getPassword() != null) {
            info.put("password", getPassword());
        }

        return driver.connect(getUrl(), info);
    }
}
