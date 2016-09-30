package org.insightech.er.db.sqltype;

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
import org.insightech.er.db.sqltype.SqlType.TypeKey;

/**
 * #willdelete
 * @author ermaster
 * @author jflute
 */
public class SqlTypeFactory {

    public static void load() throws IOException, ClassNotFoundException {
        final InputStream in = SqlTypeFactory.class.getResourceAsStream("/SqlType.xls");
        try {
            final HSSFWorkbook workBook = POIUtils.readExcelBook(in);
            final HSSFSheet sheet = workBook.getSheetAt(0);
            final Map<String, Map<SqlType, String>> dbAliasMap = new HashMap<String, Map<SqlType, String>>();
            final Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap = new HashMap<String, Map<TypeKey, SqlType>>();
            final HSSFRow headerRow = sheet.getRow(0);
            for (int colNum = 4; colNum < headerRow.getLastCellNum(); colNum++) {
                final String dbId = POIUtils.getCellValue(sheet, 0, colNum);

                final Map<SqlType, String> aliasMap = new LinkedHashMap<SqlType, String>();
                dbAliasMap.put(dbId, aliasMap);

                final Map<TypeKey, SqlType> sqlTypeMap = new LinkedHashMap<TypeKey, SqlType>();
                dbSqlTypeMap.put(dbId, sqlTypeMap);
            }

            // #point ここでめっちゃセットしてる (その後、さらに初期化)
            SqlType.setDBAliasMap(dbAliasMap, dbSqlTypeMap);

            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                final HSSFRow row = sheet.getRow(rowNum);

                final String sqlTypeId = POIUtils.getCellValue(sheet, rowNum, 0);
                if (Check.isEmpty(sqlTypeId)) {
                    break;
                }
                final Class javaClass = Class.forName(POIUtils.getCellValue(sheet, rowNum, 1));
                final boolean needArgs = POIUtils.getBooleanCellValue(sheet, rowNum, 2);
                final boolean fullTextIndexable = POIUtils.getBooleanCellValue(sheet, rowNum, 3);

                final SqlType sqlType = new SqlType(sqlTypeId, javaClass, needArgs, fullTextIndexable);

                for (int colNum = 4; colNum < row.getLastCellNum(); colNum++) {

                    String dbId = POIUtils.getCellValue(sheet, 0, colNum);

                    if (Check.isEmpty(dbId)) {
                        dbId = POIUtils.getCellValue(sheet, 0, colNum - 1);
                        final String key = POIUtils.getCellValue(sheet, rowNum, colNum);
                        if (!Check.isEmpty(key)) {
                            sqlType.addToSqlTypeMap(key, dbId);
                        }

                    } else {
                        final Map<SqlType, String> aliasMap = dbAliasMap.get(dbId);

                        if (POIUtils.getCellColor(sheet, rowNum, colNum) != HSSFColor.RED.index) {
                            String alias = POIUtils.getCellValue(sheet, rowNum, colNum);

                            if (Check.isEmpty(alias)) {
                                alias = sqlTypeId;
                            }

                            aliasMap.put(sqlType, alias);

                            if (POIUtils.getCellColor(sheet, rowNum, colNum) == HSSFColor.SKY_BLUE.index) {
                                sqlType.addToSqlTypeMap(alias, dbId);
                            }
                        }
                    }
                }
            }

        } finally {
            in.close();
        }

    }

    public static void main(String[] args) {
        SqlType.main((String[]) null);
    }
}
