package org.dbflute.erflute.preference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.db.impl.standard_sql.StandardSQLDBManager;
import org.dbflute.erflute.editor.model.settings.DBSetting;
import org.dbflute.erflute.editor.model.settings.JDBCDriverSetting;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    private static final String JDBC_DRIVER_DB_NAME_PREFIX = "jdbc.driver.db.name.";
    private static final String JDBC_DRIVER_PATH_PREFIX = "jdbc.driver.path.";
    private static final String JDBC_DRIVER_CLASS_NAME_PREFIX = "jdbc.driver.class.name.";
    private static final String JDBC_DRIVER_CLASS_NAME_LIST_NUM = "jdbc.driver.class.name.list.num";
    private static final String DB_SETTING_LIST_NUM = "db.setting.list.num";
    private static final String DB_SETTING_DBSYSTEM = "db.setting.dbsystem.";
    private static final String DB_SETTING_SERVER = "db.setting.server.";
    private static final String DB_SETTING_PORT = "db.setting.port.";
    private static final String DB_SETTING_DATABASE = "db.setting.database.";
    private static final String DB_SETTING_USER = "db.setting.user.";
    private static final String DB_SETTING_USE_DEFAULT_DRIVER = "db.setting.use.default.driver.";
    private static final String DB_SETTING_URL = "db.setting.url.";
    private static final String DB_SETTING_DRIVER_CLASS_NAME = "db.setting.driver.class.name.";
    private static final String DB_SETTING_PASSWORD = "setting.password.";

    public PreferenceInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {
    }

    public static void saveJDBCDriverSettingList(List<JDBCDriverSetting> driverSettingList) {
        clearJDBCDriverInfo();
        for (final JDBCDriverSetting driverSetting : driverSettingList) {
            addJDBCDriver(driverSetting.getDb(), driverSetting.getClassName(), driverSetting.getPath());
        }
    }

    public static void addJDBCDriver(String db, String className, String path) {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        final int listSize = store.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

        store.setValue(PreferenceInitializer.JDBC_DRIVER_DB_NAME_PREFIX + listSize, Format.null2blank(db));
        store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX + listSize, Format.null2blank(className));
        store.setValue(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX + listSize, Format.null2blank(path));

        store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM, listSize + 1);
    }

    public static List<JDBCDriverSetting> getJDBCDriverSettingList() {
        final List<JDBCDriverSetting> list = new ArrayList<JDBCDriverSetting>();
        final List<JDBCDriverSetting> defaultDriverList = new ArrayList<JDBCDriverSetting>();
        for (final String db : DBManagerFactory.getAllDBList()) {
            if (!StandardSQLDBManager.ID.equals(db)) {
                final DBManager dbManager = DBManagerFactory.getDBManager(db);

                final JDBCDriverSetting driverSetting = new JDBCDriverSetting(db, dbManager.getDriverClassName(), null);
                defaultDriverList.add(driverSetting);
            }
        }
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        final int listSize = store.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

        for (int i = 0; i < listSize; i++) {
            final String db = store.getString(PreferenceInitializer.JDBC_DRIVER_DB_NAME_PREFIX + i);
            final String className = store.getString(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX + i);
            final String path = store.getString(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX + i);
            final JDBCDriverSetting setting = new JDBCDriverSetting(db, className, path);
            list.add(setting);
            defaultDriverList.remove(setting);
        }

        for (final JDBCDriverSetting defaultDriverSetting : defaultDriverList) {
            list.add(defaultDriverSetting);
        }
        Collections.sort(list);
        return list;
    }

    public static void clearJDBCDriverInfo() {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        final int num = store.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);
        for (int i = 0; i < num; i++) {
            store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX + i, "");
            store.setValue(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX + i, "");
        }
        store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM, 0);
    }

    public static String getJDBCDriverPath(String db, String driverClassName) {
        String path = null;
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        final int listSize = store.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);
        for (int i = 0; i < listSize; i++) {
            if (driverClassName.equals(store.getString(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX + i))) {
                path = store.getString(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX + i);
                break;
            }
        }
        return path;
    }

    public static DBSetting getDBSetting(int no) {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        final String dbsystem = store.getString(PreferenceInitializer.DB_SETTING_DBSYSTEM + no);
        final String server = store.getString(PreferenceInitializer.DB_SETTING_SERVER + no);
        final int portNo = store.getInt(PreferenceInitializer.DB_SETTING_PORT + no);

        final String database = store.getString(PreferenceInitializer.DB_SETTING_DATABASE + no);
        final String user = store.getString(PreferenceInitializer.DB_SETTING_USER + no);
        final String password = store.getString(PreferenceInitializer.DB_SETTING_PASSWORD + no);
        final String useDefaultDriverString = store.getString(PreferenceInitializer.DB_SETTING_USE_DEFAULT_DRIVER + no);
        final String url = store.getString(PreferenceInitializer.DB_SETTING_URL + no);
        final String driverClassName = store.getString(PreferenceInitializer.DB_SETTING_DRIVER_CLASS_NAME + no);

        boolean useDefaultDriver = true;
        if ("false".equals(useDefaultDriverString) || StandardSQLDBManager.ID.equals(dbsystem)) {
            useDefaultDriver = false;
        }
        return new DBSetting(dbsystem, server, portNo, database, user, password, useDefaultDriver, url, driverClassName);
    }

    public static void saveSetting(int no, DBSetting dbSetting) {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(PreferenceInitializer.DB_SETTING_DBSYSTEM + no, Format.null2blank(dbSetting.getDbsystem()));
        store.setValue(PreferenceInitializer.DB_SETTING_SERVER + no, Format.null2blank(dbSetting.getServer()));
        store.setValue(PreferenceInitializer.DB_SETTING_PORT + no, dbSetting.getPort());
        store.setValue(PreferenceInitializer.DB_SETTING_DATABASE + no, Format.null2blank(dbSetting.getDatabase()));
        store.setValue(PreferenceInitializer.DB_SETTING_USER + no, Format.null2blank(dbSetting.getUser()));
        store.setValue(PreferenceInitializer.DB_SETTING_PASSWORD + no, Format.null2blank(dbSetting.getPassword()));
        store.setValue(PreferenceInitializer.DB_SETTING_USE_DEFAULT_DRIVER + no, dbSetting.isUseDefaultDriver());
        store.setValue(PreferenceInitializer.DB_SETTING_URL + no, Format.null2blank(dbSetting.getUrl()));
        store.setValue(PreferenceInitializer.DB_SETTING_DRIVER_CLASS_NAME + no, Format.null2blank(dbSetting.getDriverClassName()));
    }

    public static void saveSetting(List<DBSetting> dbSettingList) {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(PreferenceInitializer.DB_SETTING_LIST_NUM, dbSettingList.size());
        for (int i = 0; i < dbSettingList.size(); i++) {
            final DBSetting dbSetting = dbSettingList.get(i);
            PreferenceInitializer.saveSetting(i + 1, dbSetting);
        }
    }

    public static List<DBSetting> getDBSettingList(String database) {
        final List<DBSetting> dbSettingList = new ArrayList<DBSetting>();
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        final int num = store.getInt(PreferenceInitializer.DB_SETTING_LIST_NUM);
        for (int i = 1; i <= num; i++) {
            final DBSetting dbSetting = PreferenceInitializer.getDBSetting(i);
            if (database != null && !dbSetting.getDbsystem().equals(database)) {
                continue;
            }
            dbSettingList.add(dbSetting);
        }
        Collections.sort(dbSettingList);
        return dbSettingList;
    }

    public static void addDBSetting(DBSetting dbSetting) {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        int num = store.getInt(PreferenceInitializer.DB_SETTING_LIST_NUM);
        num++;
        store.setValue(PreferenceInitializer.DB_SETTING_LIST_NUM, num);
        saveSetting(num, dbSetting);
    }
}
