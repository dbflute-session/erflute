package org.dbflute.erflute.db.sqltype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.db.impl.oracle.OracleDBManager;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class SqlType implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = -8273043043893517634L;
    private static Logger logger = Logger.getLogger(SqlType.class.getName());

    public static final String SQL_TYPE_ID_SERIAL = "serial";
    public static final String SQL_TYPE_ID_BIG_SERIAL = "bigserial";
    public static final String SQL_TYPE_ID_INTEGER = "integer";
    public static final String SQL_TYPE_ID_BIG_INT = "bigint";
    public static final String SQL_TYPE_ID_CHAR = "character";
    public static final String SQL_TYPE_ID_VARCHAR = "varchar";
    private static final Pattern NEED_LENGTH_PATTERN = Pattern.compile(".+\\([a-zA-Z][,\\)].*");
    private static final Pattern NEED_DECIMAL_PATTERN1 = Pattern.compile(".+\\([a-zA-Z],[a-zA-Z]\\)");
    private static final Pattern NEED_DECIMAL_PATTERN2 = Pattern.compile(".+\\([a-zA-Z]\\).*\\([a-zA-Z]\\)");
    private static final List<SqlType> SQL_TYPE_LIST = new ArrayList<>();

    /**
     * #analyzed DBの型名で抽象型を逆引きできるようにしている。<br>
     * DBリバースで利用されている。<br>
     * map:{ database = map:{ alias-name (e.g. int) = SqlType (本来many) } } <br>
     * e.g. map:{MySQL = map:{ int = int(n) or Integer }
     */
    private static Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap = new HashMap<>();

    /**
     * #analyzed こっちが大事な人。
     * map:{ database = map:{ SqlType = alias-name (e.g. int) } } <br>
     * e.g. map:{MySQL = map:{ int(n) or Integer = int }
     */
    private static Map<String, Map<SqlType, String>> dbAliasMap = new HashMap<>();

    // ===================================================================================
    //                                                                  Static Load Holder
    //                                                                  ==================
    static {
        try {
            SqlTypeFactory.load();
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static class TypeKey {
        private final String alias;
        private int size;

        public TypeKey(String alias, int size) {
            if (alias != null) {
                alias = alias.toUpperCase();
            }

            this.alias = alias;
            if (size == 0) {
                this.size = 0;
            } else if (size == Integer.MAX_VALUE) {
                this.size = 0;
            } else if (size > 0) {
                this.size = 1;
            } else {
                this.size = -1;
            }
        }

        @Override
        public boolean equals(Object obj) {
            final TypeKey other = (TypeKey) obj;
            if (this.alias == null) {
                if (other.alias == null) {
                    if (this.size == other.size) {
                        return true;
                    }
                    return false;
                } else {
                    return false;
                }
            } else {
                if (this.alias.equals(other.alias) && this.size == other.size) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (this.alias == null) {
                return this.size;
            }
            return (this.alias.hashCode() * 10) + this.size;
        }

        @Override
        public String toString() {
            return "TypeKey [alias=" + alias + ", size=" + size + "]";
        }
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final String name; // #analyzed エクセルの一番左っぽい
    private final Class<?> javaClass;
    private final boolean needArgs;
    boolean fullTextIndexable;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SqlType(String name, Class<?> javaClass, boolean needArgs, boolean fullTextIndexable) {
        this.name = name;
        this.javaClass = javaClass;
        this.needArgs = needArgs;
        this.fullTextIndexable = fullTextIndexable;
        SQL_TYPE_LIST.add(this);
    }

    // ===================================================================================
    //                                                                             Static
    //                                                                            ========
    public static void setDBAliasMap(Map<String, Map<SqlType, String>> dbAliasMap, Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap) {
        SqlType.dbAliasMap = dbAliasMap;
        SqlType.dbSqlTypeMap = dbSqlTypeMap;
    }

    protected static List<SqlType> getAllSqlType() {
        return SQL_TYPE_LIST;
    }

    public static SqlType valueOf(String database, String alias) {
        int size = 0;
        if (alias.indexOf("(") != -1) {
            size = 1;
        }
        return valueOf(database, alias, size);
    }

    public static SqlType valueOf(String database, String alias, int size) {
        if (alias == null) {
            return null;
        }
        final Map<TypeKey, SqlType> sqlTypeMap = dbSqlTypeMap.get(database);

        // decimal(19,4) = money 等に対応
        TypeKey typeKey = new TypeKey(alias, size);
        SqlType sqlType = sqlTypeMap.get(typeKey);

        if (sqlType == null) {
            alias = alias.replaceAll("\\(.*\\)", "");
            alias = alias.replaceAll(" UNSIGNED", "");

            typeKey = new TypeKey(alias, size);
            sqlType = sqlTypeMap.get(typeKey);

            if (sqlType == null) {
                // db import の場合に、サイズが取得されていても、指定はできないケースがある
                typeKey = new TypeKey(alias, 0);
                sqlType = sqlTypeMap.get(typeKey);
            }
        }

        return sqlType;
    }

    public static SqlType valueOfId(String id) {
        SqlType sqlType = null;
        if (id == null) {
            return null;
        }
        for (final SqlType type : SQL_TYPE_LIST) {
            if (id.equals(type.getId())) {
                sqlType = type;
            }
        }
        return sqlType;
    }

    public static List<String> getAliasList(String database) {
        final Map<SqlType, String> aliasMap = dbAliasMap.get(database);
        final Set<String> aliases = new LinkedHashSet<>();
        for (final Entry<SqlType, String> entry : aliasMap.entrySet()) {
            final String alias = entry.getValue();
            aliases.add(alias);
        }
        final List<String> list = new ArrayList<>(aliases);
        Collections.sort(list);
        return list;
    }

    // ===================================================================================
    //                                                                            Instance
    //                                                                            ========
    public void addToSqlTypeMap(String typeKeyId, String database) {
        int size = 0;
        if (!this.isUnsupported(database)) {
            if (this.isNeedLength(database)) {
                size = 1;
            }
            final TypeKey typeKey = new TypeKey(typeKeyId, size);
            final Map<TypeKey, SqlType> sqlTypeMap = dbSqlTypeMap.get(database);
            sqlTypeMap.put(typeKey, this);
        }
    }

    public boolean isNeedLength(String database) {
        final String alias = this.getAlias(database);
        if (alias == null) {
            return false;
        }
        final Matcher matcher = NEED_LENGTH_PATTERN.matcher(alias);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public boolean isNeedDecimal(String database) {
        final String alias = this.getAlias(database);
        if (alias == null) {
            return false;
        }
        Matcher matcher = NEED_DECIMAL_PATTERN1.matcher(alias);
        if (matcher.matches()) {
            return true;
        }
        matcher = NEED_DECIMAL_PATTERN2.matcher(alias);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public boolean isNeedCharSemantics(String database) {
        if (!OracleDBManager.ID.equals(database)) {
            return false;
        }
        if (this.name.startsWith(SQL_TYPE_ID_CHAR) || this.name.startsWith(SQL_TYPE_ID_VARCHAR)) {
            return true;
        }
        return false;
    }

    public String getAlias(String database) { // e.g. database=MySQL, return=int
        final Map<SqlType, String> aliasMap = dbAliasMap.get(database);
        return aliasMap.get(this);
    }

    public boolean isUnsupported(String database) {
        return getAlias(database) == null;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SqlType)) {
            return false;
        }
        final SqlType type = (SqlType) obj;
        return this.name.equals(type.name);
    }

    @Override
    public String toString() {
        return this.getId();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getId() {
        return this.name;
    }

    public boolean doesNeedArgs() {
        return this.needArgs;
    }

    public boolean isFullTextIndexable() {
        return this.fullTextIndexable;
    }

    public boolean isTimestamp() {
        return this.javaClass == Date.class;
    }

    public boolean isNumber() {
        return Number.class.isAssignableFrom(this.javaClass);
    }

    // ===================================================================================
    //                                                                              Main
    //                                                                            ========
    public static void main(String[] args) {
        final int maxIdLength = 37;
        final StringBuilder msg = new StringBuilder();
        msg.append("\n");
        final List<SqlType> list = getAllSqlType();
        final List<String> dbList = DBManagerFactory.getAllDBList();

        String str = "ID";
        msg.append(str);
        for (final String db : dbList) {
            int spaceLength = maxIdLength - str.length();
            if (spaceLength < 4) {
                spaceLength = 4;
            }
            for (int i = 0; i < spaceLength; i++) {
                msg.append(" ");
            }
            str = db;
            msg.append(db);
        }
        msg.append("\n");
        msg.append("\n");
        final StringBuilder builder = new StringBuilder();
        int errorCount = 0;
        for (final SqlType type : list) {
            builder.append(type.name);
            int spaceLength = maxIdLength - type.name.length();
            if (spaceLength < 4) {
                spaceLength = 4;
            }
            for (final String db : dbList) {
                for (int i = 0; i < spaceLength; i++) {
                    builder.append(" ");
                }
                final String alias = type.getAlias(db);
                if (alias != null) {
                    builder.append(type.getAlias(db));
                    spaceLength = maxIdLength - type.getAlias(db).length();
                    if (spaceLength < 4) {
                        spaceLength = 4;
                    }
                } else {
                    if (type.isUnsupported(db)) {
                        builder.append("□□□□□□");
                    } else {
                        builder.append("■■■■■■");
                        errorCount++;
                    }
                    spaceLength = maxIdLength - "□□□□□□".length();
                    if (spaceLength < 4) {
                        spaceLength = 4;
                    }
                }
            }
            builder.append("\r\n");
        }
        final String allColumn = builder.toString();
        msg.append(allColumn + "\n");
        int errorCount2 = 0;
        int errorCount3 = 0;
        for (final String db : dbList) {
            msg.append("-- for " + db + "\n");
            msg.append("CREATE TABLE TYPE_TEST (\n");
            int count = 0;
            for (final SqlType type : list) {
                final String alias = type.getAlias(db);
                if (alias == null) {
                    continue;
                }
                if (count != 0) {
                    msg.append(",\n");
                }
                msg.append("\tCOL_" + count + " ");
                if (type.isNeedLength(db) && type.isNeedDecimal(db)) {
                    final TypeData typeData = new TypeData(new Integer(1), new Integer(1), false, null, false, null, false);
                    str = Format.formatType(type, typeData, db);
                    if (str.equals(alias)) {
                        errorCount3++;
                        msg.append("×3");
                    }
                } else if (type.isNeedLength(db)) {
                    final TypeData typeData = new TypeData(new Integer(1), null, false, null, false, null, false);
                    str = Format.formatType(type, typeData, db);
                    if (str.equals(alias)) {
                        errorCount3++;
                        msg.append("×3");
                    }
                } else if (type.isNeedDecimal(db)) {
                    final TypeData typeData = new TypeData(null, new Integer(1), false, null, false, null, false);
                    str = Format.formatType(type, typeData, db);
                    if (str.equals(alias)) {
                        errorCount3++;
                        msg.append("×3");
                    }
                } else if (type.doesNeedArgs()) {
                    str = alias + "('1')";
                } else {
                    str = alias;
                }
                if (str != null) {
                    final Matcher m1 = NEED_LENGTH_PATTERN.matcher(str);
                    final Matcher m2 = NEED_DECIMAL_PATTERN1.matcher(str);
                    final Matcher m3 = NEED_DECIMAL_PATTERN2.matcher(str);
                    if (m1.matches() || m2.matches() || m3.matches()) {
                        errorCount2++;
                        msg.append("×2");
                    }
                }
                msg.append(str);
                count++;
            }
            msg.append("\n");
            msg.append(");\n");
            msg.append("\n");
        }
        msg.append("\n");
        msg.append(errorCount + " 個の型が変換できませんでした。\n");
        msg.append(errorCount2 + " 個の数字型の指定が不足しています。\n");
        msg.append(errorCount3 + " 個の数字型の指定が余分です。\n");
        logger.info(msg.toString());
    }
}
