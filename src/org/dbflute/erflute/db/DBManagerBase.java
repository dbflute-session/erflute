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
        this.reservedWords = this.getReservedWords();
    }

    @Override
    public String getURL(String serverName, String dbName, int port) {
        String temp = serverName.replaceAll("\\\\", "\\\\\\\\");
        String url = this.getURL().replaceAll("<SERVER NAME>", temp);
        url = url.replaceAll("<PORT>", String.valueOf(port));
        temp = dbName.replaceAll("\\\\", "\\\\\\\\");
        url = url.replaceAll("<DB NAME>", temp);
        return url;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Driver> getDriverClass(String driverClassName) {
        String path = null;
        Class<Driver> clazz = null;
        try {
            if (driverClassName.equals("sun.jdbc.odbc.JdbcOdbcDriver")) {
                return (Class<Driver>) Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            } else {
                path = PreferenceInitializer.getJDBCDriverPath(this.getId(), driverClassName);
                final ClassLoader loader = this.getClassLoader(path);
                clazz = (Class<Driver>) loader.loadClass(driverClassName);
            }
        } catch (final Exception e) {
            final JDBCPathDialog dialog =
                    new JDBCPathDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), this.getId(), driverClassName,
                            path, new ArrayList<JDBCDriverSetting>(), false);
            if (dialog.open() == IDialogConstants.OK_ID) {
                final JDBCDriverSetting newDriverSetting =
                        new JDBCDriverSetting(this.getId(), dialog.getDriverClassName(), dialog.getPath());
                final List<JDBCDriverSetting> driverSettingList = PreferenceInitializer.getJDBCDriverSettingList();
                if (driverSettingList.contains(newDriverSetting)) {
                    driverSettingList.remove(newDriverSetting);
                }
                driverSettingList.add(newDriverSetting);
                PreferenceInitializer.saveJDBCDriverSettingList(driverSettingList);
                clazz = this.getDriverClass(dialog.getDriverClassName());
            }
        }
        return clazz;
    }

    private ClassLoader getClassLoader(String uri) throws SQLException, MalformedURLException {
        final StringTokenizer tokenizer = new StringTokenizer(uri, ";");
        final int count = tokenizer.countTokens();
        final URL[] urls = new URL[count];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = new URL("file", "", tokenizer.nextToken());
        }
        return new URLClassLoader(urls, this.getClass().getClassLoader());
    }

    protected abstract String getURL();

    @Override
    public abstract String getDriverClassName();

    protected Set<String> getReservedWords() {
        final Set<String> reservedWords = new HashSet<String>();
        final ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getPackage().getName() + ".reserved_word");
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
        final int[] supportItems = this.getSupportItems();

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
        final List<String> schemaList = new ArrayList<String>();
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
        return new ArrayList<String>();
    }
}
