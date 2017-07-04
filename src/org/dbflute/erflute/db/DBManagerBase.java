package org.dbflute.erflute.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.editor.model.settings.JDBCDriverSetting;
import org.dbflute.erflute.preference.PreferenceInitializer;
import org.dbflute.erflute.preference.jdbc.JDBCPathDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class DBManagerBase implements DBManager {

    private final Set<String> reservedWords;

    public DBManagerBase() {
        DBManagerFactory.addDB(this);
        this.reservedWords = getReservedWords();
    }

    @Override
    public String getURL(String serverName, String dbName, int port) {
        String temp = serverName.replaceAll("\\\\", "\\\\\\\\");
        String url = getURL().replaceAll("<SERVER NAME>", temp);
        url = url.replaceAll("<PORT>", String.valueOf(port));
        temp = dbName.replaceAll("\\\\", "\\\\\\\\");
        url = url.replaceAll("<DB NAME>", temp);
        return url;
    }

    @Override
    public Class<Driver> getDriverClass(String driverClassName) {
        String path = null;
        try {
            try {
                @SuppressWarnings("unchecked")
                final Class<Driver> clazz = (Class<Driver>) Class.forName(driverClassName);
                return clazz;
            } catch (final ClassNotFoundException e) {
                path = PreferenceInitializer.getJDBCDriverPath(getId(), driverClassName);
                if (Check.isEmpty(path)) {
                    throw new IllegalStateException(
                            String.format("JDBC Driver Class \"%s\" is not found.\rIs \"Preferences> ERFlute> JDBC Driver\" set correctly?",
                                    driverClassName));
                }
                final ClassLoader loader = getClassLoader(path);
                @SuppressWarnings("unchecked")
                final Class<Driver> clazz = (Class<Driver>) loader.loadClass(driverClassName);
                return clazz;
            }
        } catch (final MalformedURLException | ClassNotFoundException e) {
            final JDBCPathDialog dialog = new JDBCPathDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    getId(), driverClassName, path, new ArrayList<JDBCDriverSetting>(), false);
            if (dialog.open() == IDialogConstants.OK_ID) {
                final JDBCDriverSetting newDriverSetting =
                        new JDBCDriverSetting(getId(), dialog.getDriverClassName(), dialog.getPath());
                final List<JDBCDriverSetting> driverSettingList = PreferenceInitializer.getJDBCDriverSettingList();
                if (driverSettingList.contains(newDriverSetting)) {
                    driverSettingList.remove(newDriverSetting);
                }
                driverSettingList.add(newDriverSetting);
                PreferenceInitializer.saveJDBCDriverSettingList(driverSettingList);
                return getDriverClass(dialog.getDriverClassName());
            }
        }
        return null;
    }

    private ClassLoader getClassLoader(String uri) throws MalformedURLException {
        final StringTokenizer tokenizer = new StringTokenizer(uri, ";");
        final int count = tokenizer.countTokens();
        final URL[] urls = new URL[count];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = new URL("file", "", tokenizer.nextToken());
        }
        return new URLClassLoader(urls, getClass().getClassLoader());
    }

    protected abstract String getURL();

    @Override
    public abstract String getDriverClassName();

    protected Set<String> getReservedWords() {
        final Set<String> reservedWords = new HashSet<>();
        final ResourceBundle bundle = ResourceBundle.getBundle(getClass().getPackage().getName() + ".reserved_word");
        final Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            reservedWords.add(keys.nextElement().toUpperCase());
        }
        return reservedWords;
    }

    @Override
    public boolean isReservedWord(String str) {
        return reservedWords.contains(str.toUpperCase());
    }

    @Override
    public boolean isSupported(int supportItem) {
        final int[] supportItems = getSupportItems();

        for (int i = 0; i < supportItems.length; i++) {
            if (supportItems[i] == supportItem) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean doesNeedURLDatabaseName() {
        return true;
    }

    @Override
    public boolean doesNeedURLServerName() {
        return true;
    }

    abstract protected int[] getSupportItems();

    @Override
    public List<String> getImportSchemaList(Connection con) throws SQLException {
        final List<String> schemaList = new ArrayList<>();
        final DatabaseMetaData metaData = con.getMetaData();
        try {
            final ResultSet rs = metaData.getSchemas();
            while (rs.next()) {
                schemaList.add(rs.getString(1));
            }
        } catch (final SQLException ignored) {
            // when schema is not supported
        }
        return schemaList;
    }

    @Override
    public List<String> getSystemSchemaList() {
        return new ArrayList<>();
    }
}
