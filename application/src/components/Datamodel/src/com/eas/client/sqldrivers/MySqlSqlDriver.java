/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers;

import com.eas.client.ClientConstants;
import com.eas.client.SQLUtils;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.resolvers.MySqlTypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vy
 */
public class MySqlSqlDriver extends SqlDriver {

    // настройка экранирования наименования объектов БД
    private final TwinString[] charsForWrap = {new TwinString("`", "`")};
    private final char[] restrictedChars = {' ', ',', '\'', '"'};

    protected static final int[] mySqlErrorCodes = {};
    protected static final String[] platypusErrorMessages = {};
    protected MySqlTypesResolver resolver = new MySqlTypesResolver();
    protected static final String SET_SCHEMA_CLAUSE = "USE %s";
    protected static final String GET_SCHEMA_CLAUSE = "SELECT DATABASE()";
    protected static final String CREATE_SCHEMA_CLAUSE = "CREATE DATABASE %s ENGINE=InnoDB";
    // список схем в базе данных
    public static final String SQL_SCHEMAS = ""
            + "SELECT " + ClientConstants.JDBCCOLS_TABLE_SCHEM + " FROM "
            + "(SELECT schema_name AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + " FROM information_schema.schemata) schemas_alias "
            + "ORDER BY " + ClientConstants.JDBCCOLS_TABLE_SCHEM;
    //----------------------------------------------------------
    // список таблиц и представлений 
    //    1 - имя таблицы(представления) 
    //    2 - имя схемы-владельца 
    //    3 - тип (TABLE/VIEW)
    //    4 - комментарий
    public static final String SQL_TABLES_VIEWS = ""
            + "SELECT"
            + "  " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + "  " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME + ","
            + "  " + ClientConstants.JDBCCOLS_REMARKS + " "
            + "FROM"
            + "("
            + "SELECT"
            + "  table_name AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + "  table_schema AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + "  (CASE table_type WHEN 'BASE TABLE' THEN '" + ClientConstants.JDBCPKS_TABLE_TYPE_TABLE + "' ELSE table_type END) AS " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME + ","
            + "  table_comment AS " + ClientConstants.JDBCCOLS_REMARKS + " "
            + "FROM  information_schema.tables"
            + ") tables_views_alias ";
    public static final String SQL_ALL_TABLES_VIEWS = SQL_TABLES_VIEWS
            + "ORDER BY " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ", " + ClientConstants.JDBCCOLS_TABLE_NAME + " ";
    public static final String SQL_SCHEMA_TABLES_VIEWS = SQL_TABLES_VIEWS
            + "WHERE " + ClientConstants.JDBCCOLS_TABLE_SCHEM + " = '%s' "
            + "ORDER BY " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ", " + ClientConstants.JDBCCOLS_TABLE_NAME + " ";
    //-----------------------------------------------------------
    // список описаний колонок заданных таблиц
    //    1 - категория(?) таблицы (??? - не используется)
    //    2 - имя схемы
    //    3 - имя таблицы
    //    4 - имя колонки
    //    5 - тип колонки таблицы
    //    6 - код jdbc-типа для колонки (!!! - устанавливается в TypeResolver)
    //    7 - размер колонки
    //    8 - обязательность ввода (0-обязательно 1-необязательно)
    //    9 - число знаков после запятой (для чисел)
    //   10 - 
    //   11 - порядковый номер колонки в таблице (!-константа взята из индексов)
    //   12 - комментарий к колонке 
    //   13 - значение колонки по умолчанию (??? - не используется)
    protected static final String SQL_COLUMNS = ""
            + "SELECT"
            + "  TABLE_CAT,"
            + "  " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + "  " + ClientConstants.JDBCCOLS_COLUMN_NAME + ","
            + "  " + ClientConstants.JDBCCOLS_TYPE_NAME + ","
            + "  " + ClientConstants.JDBCCOLS_DATA_TYPE + ","
            + "  " + ClientConstants.JDBCCOLS_COLUMN_SIZE + ","
            + "  " + ClientConstants.JDBCCOLS_NULLABLE + ","
            + "  " + ClientConstants.JDBCCOLS_DECIMAL_DIGITS + ","
            + "  " + ClientConstants.JDBCCOLS_NUM_PREC_RADIX + ","
            + "  " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + "  " + ClientConstants.JDBCCOLS_REMARKS + ","
            + "  COLUMN_DEFAULT_VALUE "
            + "FROM"
            + "("
            + "SELECT"
            + "  table_catalog AS TABLE_CAT," //???
            + "  table_schema AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + "  table_name AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + "  column_name AS " + ClientConstants.JDBCCOLS_COLUMN_NAME + ","
            + "  data_type AS " + ClientConstants.JDBCCOLS_TYPE_NAME + ","
            + "  0 AS " + ClientConstants.JDBCCOLS_DATA_TYPE + ","
            + "  IFNULL(IFNULL(character_maximum_length,"
            + "         (case when numeric_precision is not null and instr(column_type,'(') > 0 and instr(column_type,')') > 0 "
            + "          then 0+substring(column_type,instr(column_type,'(')+1,instr(column_type,')') - instr(column_type,'(')-1) "
            + "          else null end)"
            + "        ),0) AS " + ClientConstants.JDBCCOLS_COLUMN_SIZE + ","
            + "  (CASE is_nullable WHEN 'YES'  then 1 else 0 end) AS " + ClientConstants.JDBCCOLS_NULLABLE + ","
            + "  numeric_scale AS " + ClientConstants.JDBCCOLS_DECIMAL_DIGITS + ","
            + "  10 AS " + ClientConstants.JDBCCOLS_NUM_PREC_RADIX + ","
            + "  ordinal_position AS " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + "  column_comment  AS " + ClientConstants.JDBCCOLS_REMARKS + ","
            + "  column_default AS COLUMN_DEFAULT_VALUE "
            + "FROM information_schema.columns "
            + "WHERE table_schema = '%s' AND table_name in (%s) "
            + "ORDER BY table_schema, table_name, ordinal_position"
            + ") columns_alias";
    //-----------------------------------------------------------
    // индексы
    //    1 - категория(?) таблицы (??? - не используется)
    //    2 - имя схемы
    //    3 - имя таблицы
    //    4 - уникальность (0-уникальный 1-не уникальный)
    //    5 - ??? 
    //    6 - имя индекса
    //    7 - тип индекса (??? здесь всегда 1)
    //    8 - порядковый номер колонки в индексе
    //    9 - имя колонки таблицы
    //   10 - индекс по возрастанию/убыванию значения (??? не используется)
    //   11 - число уникальных значений в индексе
    //   12 - ??? (не используется)
    //   13 - ??? (не используется)
    //   14 - создан первичным ключем
    //   15 - создан внешним ключем
    protected static final String SQL_INDEX_KEYS = ""
            + "SELECT"
            + "  TABLE_CAT,"
            + "  " + ClientConstants.JDBCIDX_TABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCIDX_TABLE_NAME + ","
            + "  " + ClientConstants.JDBCIDX_NON_UNIQUE + ","
            + "  " + ClientConstants.JDBCIDX_INDEX_QUALIFIER + ","
            + "  " + ClientConstants.JDBCIDX_INDEX_NAME + ","
            + "  " + ClientConstants.JDBCIDX_TYPE + ","
            + "  " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + "  " + ClientConstants.JDBCIDX_COLUMN_NAME + ","
            + "  " + ClientConstants.JDBCIDX_ASC_OR_DESC + ","
            + "  CARDINALITY,"
            + "  PAGES,"
            + "  FILTER_CONDITION,"
            + "  " + ClientConstants.JDBCIDX_PRIMARY_KEY + ","
            + "  " + ClientConstants.JDBCIDX_FOREIGN_KEY + " "
            + "FROM"
            + "("
            + "SELECT"
            + "  table_catalog AS TABLE_CAT,"
            + "  table_schema AS " + ClientConstants.JDBCIDX_TABLE_SCHEM + ","
            + "  table_name AS " + ClientConstants.JDBCIDX_TABLE_NAME + ","
            + "  non_unique AS " + ClientConstants.JDBCIDX_NON_UNIQUE + ","
            + "  null AS " + ClientConstants.JDBCIDX_INDEX_QUALIFIER + ","
            + "  index_name as " + ClientConstants.JDBCIDX_INDEX_NAME + ","
            + "  1 AS " + ClientConstants.JDBCIDX_TYPE + ","
            + "  seq_in_index AS " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + "  column_name AS " + ClientConstants.JDBCIDX_COLUMN_NAME + ","
            + "  collation AS " + ClientConstants.JDBCIDX_ASC_OR_DESC + ","
            + "  cardinality AS CARDINALITY,"
            + "  null AS PAGES,"
            + "  null AS FILTER_CONDITION,"
            + "  (case when index_name = 'PRIMARY' then 0 else 1 end) AS " + ClientConstants.JDBCIDX_PRIMARY_KEY + ","
            + " (SELECT distinct r.constraint_name FROM  information_schema.referential_constraints r "
            + "       WHERE r.constraint_catalog = s.table_catalog AND r.constraint_schema = s.table_schema AND"
            + "       r.table_name = s.table_name and r.constraint_name = s.index_name ) AS " + ClientConstants.JDBCIDX_FOREIGN_KEY + " "
            + "FROM information_schema.statistics s "
            + "WHERE table_schema = '%s' AND table_name in (%s) "
            + "ORDER BY non_unique, index_name, seq_in_index "
            + ") indexes_alias";
    //-----------------------------------------------------------
    // описание первичных ключей
    //    1 - категория(?) таблицы (??? - не используется)
    //    2 - имя схемы
    //    3 - имя таблицы
    //    4 - имя колонки
    //    5 - порядковый номер колонки в первичном ключе
    //    6 - имя первичного ключа
    protected static final String SQL_PRIMARY_KEYS = ""
            + "SELECT"
            + "  TABLE_CAT,"
            + "  " + ClientConstants.JDBCPKS_TABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCPKS_TABLE_NAME + ","
            + "  " + ClientConstants.JDBCPKS_COLUMN_NAME + ","
            + "  KEY_SEQ,"
            + "  " + ClientConstants.JDBCPKS_CONSTRAINT_NAME + " "
            + "FROM"
            + "("
            + "SELECT"
            + "  c.table_catalog AS TABLE_CAT,"
            + "  c.table_schema AS " + ClientConstants.JDBCPKS_TABLE_SCHEM + ","
            + "  c.table_name AS " + ClientConstants.JDBCPKS_TABLE_NAME + ","
            + "  c.column_name AS " + ClientConstants.JDBCPKS_COLUMN_NAME + ","
            + "  c.ordinal_position AS KEY_SEQ,"
            + "  NULL AS " + ClientConstants.JDBCPKS_CONSTRAINT_NAME + " "
            //            + "  c.constraint_name AS " + ClientConstants.JDBCPKS_CONSTRAINT_NAME + " "
            + "FROM  information_schema.table_constraints t, information_schema.key_column_usage c "
            + "WHERE"
//            + "  t.constraint_catalog = c.constraint_catalog AND"
            + "  t.constraint_schema = c.constraint_schema AND"
            + "  t.constraint_name = c.constraint_name AND"
            + "  t.table_schema = c.table_schema AND"
            + "  t.table_name = c.table_name AND"
            + "  t.constraint_type = 'PRIMARY KEY' AND"
            + "  t.table_schema = '%s' AND t.table_name in (%s) "
            + "ORDER BY c.table_catalog,c.table_schema,c.table_name, c.ordinal_position"
            + ") pkeys_alias";
    //-----------------------------------------------------------
    // описание внешних ключей
    //    1 - категория(?) таблицы первичного ключа(??? - не используется)
    //    2 - имя схемы первичного ключа
    //    3 - имя таблицы первичного ключа
    //    4 - имя первичного ключа
    //    5 - имя колонки первичного ключа
    //    6 - категория(?) таблицы внешнего ключа(??? - не используется)
    //    7 - имя схемы внешнего ключа
    //    8 - имя таблицы внешнего ключа
    //    9 - имя внешнего ключа
    //   10 - имя колонки внешнего ключа
    //   11 - порядковый номер колонки в первичном ключе
    //   12 - правило при обновлении данных  ( 0=CASCADE; 1=RESTRICT | NO ACTION; 2=SET NULL )
    //   13 - правило при удалении данных    ( 0=CASCADE; 1=RESTRICT | NO ACTION; 2=SET NULL ) 
    //   14 - применение отложенных проверок по ограничениям (5- , 6- , 7-не применяется )
    protected static final String SQL_FOREIGN_KEYS = ""
            + "SELECT"
            + "  PKTABLE_CAT,"
            + "  " + ClientConstants.JDBCFKS_FKPKTABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCFKS_FKPKTABLE_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FKPK_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FKPKCOLUMN_NAME + ","
            + "  FKTABLE_CAT,"
            + "  " + ClientConstants.JDBCFKS_FKTABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCFKS_FKTABLE_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FK_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FKCOLUMN_NAME + ","
            + "  KEY_SEQ,"
            + "  " + ClientConstants.JDBCFKS_FKUPDATE_RULE + ","
            + "  " + ClientConstants.JDBCFKS_FKDELETE_RULE + ","
            + "  " + ClientConstants.JDBCFKS_FKDEFERRABILITY + " "
            + "FROM"
            + "("
            + "SELECT"
            + "  pkc.table_catalog AS PKTABLE_CAT,"
            + "  pkc.table_schema AS " + ClientConstants.JDBCFKS_FKPKTABLE_SCHEM + ","
            + "  pkc.table_name AS " + ClientConstants.JDBCFKS_FKPKTABLE_NAME + ","
            + "  null AS " + ClientConstants.JDBCFKS_FKPK_NAME + ","
            //            + "  r.unique_constraint_name AS " + ClientConstants.JDBCFKS_FKPK_NAME + ","
            + "  pkc.column_name AS " + ClientConstants.JDBCFKS_FKPKCOLUMN_NAME + ","
            + "  fkc.table_catalog AS FKTABLE_CAT,"
            + "  fkc.table_schema AS " + ClientConstants.JDBCFKS_FKTABLE_SCHEM + ","
            + "  fkc.table_name AS " + ClientConstants.JDBCFKS_FKTABLE_NAME + ","
            + "  r.constraint_name AS " + ClientConstants.JDBCFKS_FK_NAME + ","
            + "  fkc.column_name AS " + ClientConstants.JDBCFKS_FKCOLUMN_NAME + ","
            + "  fkc.ordinal_position  AS KEY_SEQ,"
            + "  (CASE update_rule WHEN 'CASCADE' THEN 0 WHEN 'SET NULL' THEN 2 ELSE 1 END) AS " + ClientConstants.JDBCFKS_FKUPDATE_RULE + ","
            + "  (CASE delete_rule WHEN 'CASCADE' THEN 0 WHEN 'SET NULL' THEN 2 ELSE 1 END) AS " + ClientConstants.JDBCFKS_FKDELETE_RULE + ","
            + "  7 AS " + ClientConstants.JDBCFKS_FKDEFERRABILITY + " "
            + "FROM"
            + "  information_schema.referential_constraints r,"
            + "  information_schema.key_column_usage pkc,"
            + "  information_schema.key_column_usage fkc "
            + "WHERE"
//            + "  r.unique_constraint_catalog = pkc.constraint_catalog AND"
            + "  r.unique_constraint_schema = pkc.constraint_schema AND"
            + "  r.unique_constraint_name = pkc.constraint_name AND"
            + "  r.referenced_table_name = pkc.table_name AND"
//            + "  r.constraint_catalog = fkc.constraint_catalog AND"
            + "  r.constraint_schema = fkc.constraint_schema AND"
            + "  r.constraint_name = fkc.constraint_name AND"
            + "  fkc.ordinal_position = pkc.ordinal_position AND"
            + "  r.table_name = fkc.table_name AND"
            + "  fkc.table_schema  = '%s' AND fkc.table_name in (%s) "
            + "ORDER BY fkc.table_catalog, fkc.table_schema, fkc.table_name, fkc.ordinal_position"
            + ") fkeys_alias";

    public MySqlSqlDriver() {
        super();
    }

    @Override
    public TypesResolver getTypesResolver() {
        return resolver;
    }

    @Override
    public String getUsersSpaceInitResourceName() {
        return "/" + MySqlSqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/MySqlInitUsersSpace.sql";
    }

    @Override
    public String getVersionInitResourceName() {
        return "/" + MySqlSqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/MySqlInitVersion.sql";
    }

    @Override
    public Set<Integer> getSupportedJdbcDataTypes() {
        return resolver.getSupportedJdbcDataTypes();
    }

    @Override
    public String getSql4TableColumns(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_COLUMNS, prepareName(aOwnerName), tablesIn);
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TablePrimaryKeys(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_PRIMARY_KEYS, prepareName(aOwnerName), tablesIn);
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TableForeignKeys(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_FOREIGN_KEYS, prepareName(aOwnerName), constructIn(aTableNames));
        } else {
            return null;
        }
    }

    @Override
    public String getSql4Indexes(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_INDEX_KEYS, prepareName(aOwnerName), tablesIn);
        } else {
            return null;
        }
    }

    @Override
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        String schemaClause = ((aOwnerName != null && !aOwnerName.trim().isEmpty()) ? wrapNameIfRequired(aOwnerName) + "." : "");
        if (aDescription == null) {
            aDescription = "";
        }
        String sql0 = "DROP PROCEDURE IF EXISTS " + schemaClause + "setColumnComment";
        String sql1 = ""
                + "CREATE PROCEDURE " + schemaClause + "setColumnComment("
                + "    IN aOwnerName VARCHAR(100), "
                + "    IN aTableName VARCHAR(100), "
                + "    IN aFieldName VARCHAR(100), "
                + "    IN aDescription VARCHAR(100),"
                + "    OUT res text)"
                + "	LANGUAGE SQL"
                + "	NOT DETERMINISTIC"
                + "	MODIFIES SQL DATA"
                + "	SQL SECURITY INVOKER"
                + "	COMMENT 'Процедура для задания комментария к полю таблицы. Нужна из-за необходимости задавать ПОЛНОСТЬЮ определение поля. Для вывода генерируемых скриптов: call setColumnComment(schema,table,column,comment,@a);select @a;'"
                + "BEGIN"
                + "   SET @define_column = '';"
                + "   SET @select_stm = CONCAT"
                + "   ('SELECT CONCAT',"
                + "    '(',"
                + "    '  column_type,'' '',',"
                + "    '  (CASE is_nullable WHEN ''YES'' THEN ''NULL'' ELSE ''NOT NULL'' END),'' '',',"
                //+ "    '  IF(column_default is null,'''',CONCAT(''DEFAULT \"'',column_default,''\" '')),',"
                + "    '  IF(column_default is null,'''',CONCAT(''DEFAULT '',IF(CHARACTER_MAXIMUM_LENGTH > 0, ''\"'', ''''),column_default,IF(CHARACTER_MAXIMUM_LENGTH > 0, ''\"'', ''''),'' '')),',"
                + "    '  IF(extra is null,'''',CONCAT(extra,'' ''))',"
                + "    ') ',"
                + "    'INTO @define_column ',"
                + "    'FROM information_schema.COLUMNS ',"
                + "    'WHERE table_schema = ''',aOwnerName,''' AND',"
                + "    '  table_name = ''',aTableName,''' AND', "
                + "    '  column_name = ''',aFieldName,'''');"
                + "   PREPARE result_select FROM @select_stm;"
                + "   EXECUTE result_select;"
                + "   DROP PREPARE result_select;"
                + "   SET @stm = CONCAT('ALTER TABLE ', IF(LENGTH(aOwnerName), CONCAT('`',aOwnerName,'`.'), ''), '`',aTableName,'`'"
                + "   	' MODIFY COLUMN `',aFieldName,'` ',@define_column,"
                + "     IF(aDescription is null,'''',CONCAT(' COMMENT ''',aDescription,'''')));"
                + "   PREPARE alter_stm FROM @stm;"
                + "   EXECUTE alter_stm;"
                + "   DROP PREPARE alter_stm;"
                + "   SET res = CONCAT(@select_stm,';',@stm); "
                + "END";
        String sql2 = "CALL " + schemaClause + "setColumnComment('"
                + unwrapName(aOwnerName) + "','" + unwrapName(aTableName) + "','" + unwrapName(aFieldName) + "','" + aDescription + "',@a)";
        String sql3 = "DROP PROCEDURE " + schemaClause + "setColumnComment";
        return new String[]{sql0, sql1, sql2, sql3};
    }

    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        String fullName = makeFullName(aOwnerName, aTableName);
        if (aDescription == null) {
            aDescription = "";
        }
        return String.format("ALTER TABLE %s COMMENT='%s'", fullName, aDescription.replaceAll("'", "''"));
    }

    @Override
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        return "DROP TABLE " + makeFullName(aSchemaName, aTableName);
    }

    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        return String.format("DROP INDEX %s ON %s", wrapNameIfRequired(aIndexName), makeFullName(aSchemaName, aTableName));
    }

    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {

        String fkTableName = makeFullName(aSchemaName, aFk.getTable());
        String fkName = aFk.getCName();
        return String.format("ALTER TABLE %s DROP FOREIGN KEY %s", fkTableName, wrapNameIfRequired(fkName));
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        List<ForeignKeySpec> fkList = new ArrayList<>();
        fkList.add(aFk);
        return getSql4CreateFkConstraint(aSchemaName, fkList);
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, List<ForeignKeySpec> listFk) {
        if (listFk != null && listFk.size() > 0) {
            ForeignKeySpec fk = listFk.get(0);
            String fkTableName = makeFullName(aSchemaName, fk.getTable());
            String fkName = fk.getCName();
            String fkColumnName = wrapNameIfRequired(fk.getField());

            PrimaryKeySpec pk = fk.getReferee();
            String pkSchemaName = pk.getSchema();
            String pkTableName = makeFullName(aSchemaName, pk.getTable());
            String pkColumnName = wrapNameIfRequired(pk.getField());

            for (int i = 1; i < listFk.size(); i++) {
                fk = listFk.get(i);
                pk = fk.getReferee();
                fkColumnName += ", " + wrapNameIfRequired(fk.getField());
                pkColumnName += ", " + wrapNameIfRequired(pk.getField());
            }

            String fkRule = "";
            switch (fk.getFkUpdateRule()) {
                case CASCADE:
                    fkRule += " ON UPDATE CASCADE";
                    break;
                case NOACTION:
                    fkRule += " ON UPDATE NO ACTION";
                    break;
                case SETDEFAULT:
                    // !!! не используется
                    break;
                case SETNULL:
                    fkRule += " ON UPDATE SET NULL";
                    break;
            }
            switch (fk.getFkDeleteRule()) {
                case CASCADE:
                    fkRule += " ON DELETE CASCADE";
                    break;
                case NOACTION:
                    fkRule += " ON DELETE NO ACTION";
                    break;
                case SETDEFAULT:
                    // !!! не используется
                    break;
                case SETNULL:
                    fkRule += " ON DELETE SET NULL";
                    break;
            }
            return String.format("ALTER TABLE %s ADD CONSTRAINT %s"
                    + " FOREIGN KEY (%s) REFERENCES %s (%s) %s", fkTableName, fkName.isEmpty() ? "" : wrapNameIfRequired(fkName), fkColumnName, pkTableName, pkColumnName, fkRule);

        }
        return null;
    }

    @Override
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        assert aIndex.getColumns().size() > 0 : "index definition must consist of at least 1 column";

        String tableName = makeFullName(aSchemaName, aTableName);
        String fieldsList = "";
        for (int i = 0; i < aIndex.getColumns().size(); i++) {
            DbTableIndexColumnSpec column = aIndex.getColumns().get(i);
            fieldsList += wrapNameIfRequired(column.getColumnName());
            if (!column.isAscending()) {
                fieldsList += " DESC";
            }
            if (i != aIndex.getColumns().size() - 1) {
                fieldsList += ", ";
            }
        }
        return "CREATE " + (aIndex.isUnique() ? "UNIQUE " : "")
                + "INDEX " + wrapNameIfRequired(aIndex.getName()) + (aIndex.isHashed() ? " USING HASH " : " ")
                + "ON " + tableName + " (" + fieldsList + ")";
    }

    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String fullName = makeFullName(aSchemaName, aTableName);
        return String.format("CREATE TABLE %s (%s DECIMAL(18,0) NOT NULL,"
                + "CONSTRAINT PRIMARY KEY (%s)) ENGINE=InnoDB", fullName, wrapNameIfRequired(aPkFieldName), wrapNameIfRequired(aPkFieldName));
    }

    @Override
    public String parseException(Exception ex) {
        if (ex != null && ex instanceof SQLException) {
            SQLException sqlEx = (SQLException) ex;
            int errorCode = sqlEx.getErrorCode();
            for (int i = 0; i < mySqlErrorCodes.length; i++) {
                if (errorCode == mySqlErrorCodes[i]) {
                    return platypusErrorMessages[i];
                }
            }
        }
        return ex.getLocalizedMessage();
    }

    private String getFieldTypeDefinition(Field aField) {
        resolver.resolve2RDBMS(aField);
        String typeDefine = "";
        String sqlTypeName = aField.getTypeInfo().getSqlTypeName().toLowerCase();
        typeDefine += sqlTypeName;
        // field length
        int size = aField.getSize();
        int scale = aField.getScale();

        if (resolver.isScaled(sqlTypeName) && resolver.isSized(sqlTypeName) && size > 0) {
            typeDefine += "(" + String.valueOf(size) + "," + String.valueOf(scale) + ")";
        } else {
            if (resolver.isSized(sqlTypeName) && size > 0) {
                typeDefine += "(" + String.valueOf(size) + ")";
            }
        }
        return typeDefine;

    }

    @Override
    public String getSql4FieldDefinition(Field aField) {
        String fieldDefinition = wrapNameIfRequired(aField.getName()) + " " + getFieldTypeDefinition(aField);
        if (!aField.isSigned() && SQLUtils.getTypeGroup(aField.getTypeInfo().getSqlType()) == SQLUtils.TypesGroup.NUMBERS) {
            fieldDefinition += " UNSIGNED";
        }
        if (!aField.isNullable()) {
            fieldDefinition += " NOT NULL";
        } else {
            fieldDefinition += " NULL";
        }
        return fieldDefinition;
    }

    @Override
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, Field aOldFieldMd, Field aNewFieldMd) {
        return getSqls4RenamingField(aSchemaName, aTableName, aOldFieldMd.getName(), aNewFieldMd);
    }

    @Override
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, Field aNewFieldMd) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        return new String[]{String.format("ALTER TABLE %s CHANGE %s %s", fullTableName, wrapNameIfRequired(aOldFieldName), getSql4FieldDefinition(aNewFieldMd))};
    }

    @Override
    public Integer getJdbcTypeByRDBMSTypename(String aLowLevelTypeName) {
        return resolver.getJdbcTypeByRDBMSTypename(aLowLevelTypeName);
    }

    @Override
    public String getSql4TablesEnumeration(String schema4Sql) {
        if (schema4Sql == null || schema4Sql.isEmpty()) {
            return SQL_ALL_TABLES_VIEWS;
        } else {
            return String.format(SQL_SCHEMA_TABLES_VIEWS, prepareName(schema4Sql));
        }
    }

    @Override
    public String getSql4SchemasEnumeration() {
        return SQL_SCHEMAS;
    }

    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return String.format(CREATE_SCHEMA_CLAUSE, aSchemaName);
        }
        throw new IllegalArgumentException("Schema name is null or empty.");
    }

    @Override
    public String getSql4GetConnectionContext() {
        return GET_SCHEMA_CLAUSE;
    }

    @Override
    public void applyContextToConnection(Connection aConnection, String aSchema) throws Exception {
        if (aSchema != null && !aSchema.isEmpty()) {
            try (Statement stmt = aConnection.createStatement()) {
                stmt.execute(String.format(SET_SCHEMA_CLAUSE, wrapNameIfRequired(aSchema)));
            }
        }
    }

    @Override
    public String[] getSql4CreatePkConstraint(String aSchemaName, List<PrimaryKeySpec> listPk) {

        if (listPk != null && listPk.size() > 0) {
            PrimaryKeySpec pk = listPk.get(0);
            String tableName = pk.getTable();
            String pkTableName = makeFullName(aSchemaName, tableName);
            String pkName = wrapNameIfRequired(generatePkName(tableName, PKEY_NAME_SUFFIX));
            String pkColumnName = wrapNameIfRequired(pk.getField());
            for (int i = 1; i < listPk.size(); i++) {
                pk = listPk.get(i);
                pkColumnName += ", " + wrapNameIfRequired(pk.getField());
            }
            return new String[]{
                String.format("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)", pkTableName, pkName, pkColumnName)
            };
        }
        return null;
    }

    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
        String pkTableName = makeFullName(aSchemaName, aPk.getTable());
        return String.format("ALTER TABLE %s DROP PRIMARY KEY", pkTableName);
    }

    @Override
    public boolean isConstraintsDeferrable() {
        return false;
    }

    @Override
    public String[] getSqls4AddingField(String aSchemaName, String aTableName, Field aField) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        return new String[]{
            String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, fullTableName) + getSql4FieldDefinition(aField)
        };
    }

    @Override
    public TwinString[] getCharsForWrap() {
        return charsForWrap;
    }

    @Override
    public char[] getRestrictedChars() {
        return restrictedChars;
    }

    @Override
    public boolean isHadWrapped(String aName) {
        return false;
    }

    private String prepareName(String aName) {
        return (isWrappedName(aName) ? unwrapName(aName) : aName);
    }
}
