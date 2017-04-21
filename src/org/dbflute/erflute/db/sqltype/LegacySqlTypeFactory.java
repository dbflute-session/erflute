package org.dbflute.erflute.db.sqltype;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.POIUtils;
import org.dbflute.erflute.db.sqltype.SqlType.TypeKey;

/**
 * #willdelete
 * @author modified by jflute (originated in ermaster)
 */
public class LegacySqlTypeFactory {

    public static void load() throws IOException, ClassNotFoundException {
        final InputStream ins = LegacySqlTypeFactory.class.getResourceAsStream("/SqlType.xls");
        try {
            final HSSFWorkbook workBook = POIUtils.readExcelBook(ins);
            final HSSFSheet sheet = workBook.getSheetAt(0);
            final Map<String, Map<SqlType, String>> dbAliasMap = new HashMap<>();
            final Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap = new HashMap<>();
            final HSSFRow headerRow = sheet.getRow(0);
            // RDB分初期化。(StandardSQL, DB2, HSQLDB, MySQL, Oracle, PostgreSQL, SQLite, SQLServer, SQLServer 2008, MSAccess)
            for (int colNum = 4; colNum < headerRow.getLastCellNum(); colNum++) {
                final String dbId = POIUtils.getCellValue(sheet, 0, colNum);
                // excel上、RDB名が1行おきに空白セルがあるためスキップする。(RDB情報は2列セットと思われる。)
                if (Check.isEmpty(dbId)) {
                    continue;
                }
                dbAliasMap.put(dbId, new LinkedHashMap<SqlType, String>());
                dbSqlTypeMap.put(dbId, new LinkedHashMap<TypeKey, SqlType>());
            }
            // #point ここでめっちゃセットしてる (その後、さらに初期化)
            SqlType.setDBAliasMap(dbAliasMap, dbSqlTypeMap);

            // sqlTypeId(excelの1行目)ごとの処理
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                final HSSFRow row = sheet.getRow(rowNum);
                final String sqlTypeId = POIUtils.getCellValue(sheet, rowNum, 0);
                if (Check.isEmpty(sqlTypeId)) {
                    break;
                }
                final Class<?> javaClass = Class.forName(POIUtils.getCellValue(sheet, rowNum, 1));
                final boolean needArgs = POIUtils.getBooleanCellValue(sheet, rowNum, 2);
                final boolean fullTextIndexable = POIUtils.getBooleanCellValue(sheet, rowNum, 3);
                final SqlType sqlType = new SqlType(sqlTypeId, javaClass, needArgs, fullTextIndexable);
                // sqlTypeId(excelの1行目)のRDBごとの処理
                for (int colNum = 4; colNum < row.getLastCellNum(); colNum++) {
                    String dbId = POIUtils.getCellValue(sheet, 0, colNum);
                    // RDB情報の2列(RDB名がない列)の処理
                    if (Check.isEmpty(dbId)) {
                        dbId = POIUtils.getCellValue(sheet, 0, colNum - 1);
                        final String key = POIUtils.getCellValue(sheet, rowNum, colNum);
                        if (!Check.isEmpty(key)) {
                            sqlType.addToSqlTypeMap(key, dbId);
                        }
                    } else {
                        // RDB情報の1列(RDB名がある列)の処理
                        // 背景色が赤以外(赤はスキップ)
                        if (POIUtils.getCellColor(sheet, rowNum, colNum) != HSSFColor.RED.index) {
                            String alias = POIUtils.getCellValue(sheet, rowNum, colNum);
                            // 別名(セル)が空だったら、sqlTypeIdにする。つまり、sqlTypeIdをセル上に設定しても同じ。(そのほうがわかりやすそう。もしくは同じと識別できる文字列${sqlTypeId}などにする)
                            if (Check.isEmpty(alias)) {
                                alias = sqlTypeId;
                            }
                            dbAliasMap.get(dbId).put(sqlType, alias);
                            // 背景色が青の場合、DBの型名で抽象型を逆引きできるように登録。
                            if (POIUtils.getCellColor(sheet, rowNum, colNum) == HSSFColor.SKY_BLUE.index) {
                                sqlType.addToSqlTypeMap(alias, dbId);
                            }
                        }
                    }
                }
            }
        } finally {
            ins.close();
        }
    }
}
