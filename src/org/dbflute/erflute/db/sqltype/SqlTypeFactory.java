package org.dbflute.erflute.db.sqltype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.db.sqltype.SqlType.TypeKey;

/**
 * #willdelete
 * @author modified by jflute (originated in ermaster)
 */
public class SqlTypeFactory {

    public static void load() throws IOException, ClassNotFoundException {
        try (final InputStream inputStream = SqlTypeFactory.class.getResourceAsStream("/SqlType.tsv");
                final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final List<List<String>> data =
                    bufferedReader.lines().map(line -> Arrays.asList(line.split("\\t"))).collect(Collectors.toList());

            final Map<String, Map<SqlType, String>> dbAliasMap = new HashMap<>();
            final Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap = new HashMap<>();
            final List<String> headerRow = data.get(0);
            // #point RDB分初期化。(StandardSQL, DB2, HSQLDB, MySQL, Oracle etc.)
            for (int colNum = 4; colNum < headerRow.size(); colNum++) {
                final String dbId = headerRow.get(colNum);
                dbAliasMap.put(dbId, new LinkedHashMap<SqlType, String>());
                dbSqlTypeMap.put(dbId, new LinkedHashMap<TypeKey, SqlType>());
            }
            // #point ここでめっちゃセットしてる (その後、さらに初期化)
            SqlType.setDBAliasMap(dbAliasMap, dbSqlTypeMap);

            // #point sqlTypeId(tsvの1行目)ごとの処理
            for (int rowNum = 1; rowNum < data.size(); rowNum++) {
                final List<String> row = data.get(rowNum);
                final String sqlTypeId = row.get(0);
                if (Check.isEmpty(sqlTypeId)) {
                    break;
                }
                final Class<?> javaClass = Class.forName(row.get(1));
                final boolean needArgs = row.size() < 3 ? false : Boolean.parseBoolean(row.get(2));
                final boolean fullTextIndexable = row.size() < 4 ? false : Boolean.parseBoolean(row.get(3));
                final SqlType sqlType = new SqlType(sqlTypeId, javaClass, needArgs, fullTextIndexable);
                // #point sqlTypeId(tsvの1行目)のRDBごとの処理
                for (int colNum = 4; colNum < row.size(); colNum++) {
                    final String dbId = headerRow.get(colNum);
                    final String value = row.get(colNum);
                    // 値がないものはRDBにsqlTypeIdに対応する型なし。
                    if (Check.isEmpty(value)) {
                        continue;
                    }
                    Arrays.stream(value.split("\\|")).forEach(v -> {
                        final String alias = v.replaceAll("\\=.+", "").replace("${sqlTypeId}", sqlTypeId);
                        final boolean reverseLookup = Boolean.parseBoolean(v.replaceAll(".+\\=", ""));

                        // TODO ymd 場当たり的な対応。SqlType.tsvの仕様が分からないため、以下のように実装した。
                        if (alias.toLowerCase().contains("varchar(max)")) {
                            final SqlType varcharMaxSqlType = new SqlType(alias, String.class, false, true);
                            dbAliasMap.get(dbId).put(varcharMaxSqlType, alias);
                            dbSqlTypeMap.get(dbId).put(new TypeKey(alias, 1), varcharMaxSqlType);
                            return;
                        }

                        // sqlTypeIdに対応するRDBの別名(先頭1つのみ)を登録
                        if (!dbAliasMap.get(dbId).containsKey(sqlType)) {
                            dbAliasMap.get(dbId).put(sqlType, alias);
                        }
                        // 型の逆引きが必要なものは登録
                        if (reverseLookup) {
                            sqlType.addToSqlTypeMap(alias, dbId);
                        }
                    });
                }
            }
        }
    }
}
