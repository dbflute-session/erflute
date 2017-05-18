package org.dbflute.erflute.db.sqltype;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import junit.framework.TestCase;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;

public class SqlTypeFactoryTest extends TestCase {

    private static Logger logger = Logger.getLogger(SqlTypeFactoryTest.class.getName());

    public void testname() throws Exception {
        // ## Arrange ##

        // ## Act ##
        final int maxIdLength = 37;
        final StringBuilder msg = new StringBuilder();
        msg.append("\n");
        final List<SqlType> list = SqlType.getAllSqlType();
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
            builder.append(type.getId());
            int spaceLength = maxIdLength - type.getId().length();
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
                    final Matcher m1 = SqlType.NEED_LENGTH_PATTERN.matcher(str);
                    final Matcher m2 = SqlType.NEED_DECIMAL_PATTERN1.matcher(str);
                    final Matcher m3 = SqlType.NEED_DECIMAL_PATTERN2.matcher(str);
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

        // ## Assert ##
    }
}
