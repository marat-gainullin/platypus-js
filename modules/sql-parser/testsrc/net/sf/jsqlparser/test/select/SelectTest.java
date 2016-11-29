package net.sf.jsqlparser.test.select;

import java.io.StringReader;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.Union;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import net.sf.jsqlparser.util.deparser.StatementDeParser;
import static org.junit.Assert.*;
import org.junit.Test;

public class SelectTest {

    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testTableAlias() throws JSQLParserException {
        String statement = "SELECT t1.name FROM #table1 AS t1 WHERE t1.id = 12";
        Select select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);
    }

    @Test
    public void testLimit() throws JSQLParserException {
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT 3, ?";

        Select select = (Select) parserManager.parse(new StringReader(statement));

        assertEquals(3, ((PlainSelect) select.getSelectBody()).getLimit().getOffset());
        assertTrue(((PlainSelect) select.getSelectBody()).getLimit().isRowCountJdbcParameter());
        assertFalse(((PlainSelect) select.getSelectBody()).getLimit().isOffsetJdbcParameter());
        assertFalse(((PlainSelect) select.getSelectBody()).getLimit().isLimitAll());

        // toString uses standard syntax
        assertEquals(statement, "" + select);

        statement = "SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?";
        select = (Select) parserManager.parse(new StringReader(statement));

        assertEquals(0, ((PlainSelect) select.getSelectBody()).getLimit().getRowCount());
        assertTrue(((PlainSelect) select.getSelectBody()).getLimit().isOffsetJdbcParameter());
        assertFalse(((PlainSelect) select.getSelectBody()).getLimit().isLimitAll());
        assertEquals(statement, "" + select);

        statement =
                "(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION "
                + "(SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) LIMIT 3, 4";
        select = (Select) parserManager.parse(new StringReader(statement));
        Union union = (Union) select.getSelectBody();
        assertEquals(3, union.getLimit().getOffset());
        assertEquals(4, union.getLimit().getRowCount());

        // toString uses standard syntax
        assertEquals(statement, "" + select);

        statement =
                "(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION ALL "
                + "(SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) UNION ALL "
                + "(SELECT * FROM mytable3 WHERE mytable4.col = 9 OFFSET ?) LIMIT 4 OFFSET 3";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);
    }

    @Test
    public void testTop() throws JSQLParserException {
        String statement = "SELECT TOP 3 * FROM mytable WHERE mytable.col = 9";

        Select select = (Select) parserManager.parse(new StringReader(statement));

        assertEquals(3, ((PlainSelect) select.getSelectBody()).getTop().getRowCount());

        statement = "select top 5 foo from bar";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(5, ((PlainSelect) select.getSelectBody()).getTop().getRowCount());


    }

    @Test
    public void testCirilicChars() throws JSQLParserException {
        String statement =
                "/*привет*/ SELECT /*sdfsf*/ * FROM /*sdfs*/ моятаблица WHERE моятаблица.col = 9 /*ksjdf*/";
        Select select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);
    }

    @Test
    public void testComment() throws JSQLParserException {
        String statement =
                "/*welkjhkas*/ SELECT /*sdfsf*/ * FROM /*sdfs*/ mytable WHERE mytable.col = 9 /*ksjdf*/";
        Select select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);
        statement =
                "/*welkjhkas*/ SELECT /*0*/ DISTINCT /*1*/ ON /*2*/ (/*6*/ myid /*3*/ ) /*4*/ myid, /*5*/ mycol FROM mytable WHERE mytable.col = 9 /*ksjdf*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ * FROM /**/ tab1 /**/ LEFT /**/ OUTER /**/ JOIN /**/ tab2 /**/ ON tab1.id = tab2.id /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ * FROM /**/ tab1 /**/ LEFT /**/ OUTER /**/ JOIN /**/ tab2 /**/ ON tab1.id = tab2.id /**/ INNER /**/ JOIN /**/ tab3 /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ * FROM /**/ tab1 /**/ LEFT /**/ OUTER /**/ JOIN /**/ tab2 /**/ ON tab1.id = tab2.id /**/ JOIN /**/ tab3 /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ * FROM /**/ tab1 /**/ LEFT /**/ OUTER /**/ JOIN /**/ tab2 /**/ ON tab1.id = tab2.id /**/ INNER /**/ JOIN /**/ tab3 /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ * FROM /**/ TA2 /**/ LEFT /**/ OUTER /**/ JOIN /**/ O /**/ USING /**/ (/**/ col1 /**/, /**/ col2 /**/ ) WHERE D.OasSD = 'asdf' AND (kj >= 4 OR l < 'sdf') /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ * FROM /**/ tab1 /**/ INNER /**/ JOIN /**/ tab2 /**/ USING /**/ (/**/ id /**/, /**/ id2 /**/ ) /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ * FROM /**/ tab1 /**/ RIGHT /**/ OUTER /**/ JOIN /**/ tab2 /**/ USING /**/ (/**/ id /**/, /**/ id2 /**/ ) /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ * FROM /**/ foo /**/ AS /**/ f /**/ LEFT /**/ INNER /**/ JOIN (/**/ bar /**/ AS /**/ b /**/ RIGHT /**/ OUTER /**/ JOIN /**/ baz /**/ AS /**/ z /**/ ON f.id = z.id) /**/ ON f.id = b.id /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * FROM /*2*/ mytable /**/ WHERE /**/ mytable.col /**/ = /**/ 9 /*3*/ LIMIT /*4*/ 3 /*5*/ , /*6*/ ? /*7*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        // toString uses standard syntax
        statement = "/**/ SELECT /**/ * FROM /**/ mytable WHERE mytable.col = 9 /**/ LIMIT /**/ ? /**/ OFFSET /**/ 3 /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ * FROM /**/ mytable WHERE mytable.col = 9 /**/ OFFSET /**/ ? /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * FROM /*2*/ tab1 WHERE /*3*/ a > 34 GROUP BY tab1.b ORDER BY tab1.a /*6*/ DESC, tab1.b /*8*/ ASC /*9*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ TOP /*2*/ 3 /*3*/ * FROM /*4*/ mytable WHERE mytable.col = 9 /*5*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*1*/ WITH /**/ DINFO /**/ (DEPTNO, AVGSALARY, EMPCOUNT) /**/ AS "
                + "/**/ (/**/ SELECT OTHERS.WORKDEPT, AVG(OTHERS.SALARY), COUNT(*) FROM /**/ EMPLOYEE /**/ AS /**/ OTHERS "
                + "GROUP BY OTHERS.WORKDEPT /**/) /*2*/, /**/ DINFOMAX /**/ AS /**/ (/**/ SELECT MAX(AVGSALARY) /**/ AS /**/ AVGMAX FROM /**/ DINFO /**/) "
                + "/**/ SELECT THIS_EMP.EMPNO, THIS_EMP.SALARY, DINFO.AVGSALARY, DINFO.EMPCOUNT, DINFOMAX.AVGMAX "
                + "FROM /**/ EMPLOYEE /**/ AS /**/ THIS_EMP /**/ INNER /**/ JOIN /**/ DINFO /**/ INNER /**/ JOIN /**/ DINFOMAX "
                + "WHERE THIS_EMP.JOB = 'SALESREP' AND THIS_EMP.WORKDEPT = DINFO.DEPTNO /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ * FROM /**/ t1 WHERE t1.r IN /**/ (/**/ SELECT /**/ * FROM /**/ t2 /**/) /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement =
                "/**/ (/**/ SELECT /**/ * FROM /**/ mytable WHERE mytable.col = 9 /**/ OFFSET /**/ ? /**/) /**/ UNION "
                + "/**/ (/**/ SELECT /**/ * FROM /**/ mytable2 WHERE mytable2.col = 9 /**/ OFFSET /**/ ? /**/) "
                + "/**/ ORDER /**/ BY mytable.col /**/ DESC /**/ LIMIT /**/ 3 /**/ , /**/ 4 /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement =
                "/**/ SELECT /**/ * FROM /**/ mytable WHERE mytable.col = 9 /**/ OFFSET /**/ ? /**/ UNION "
                + "/**/ SELECT /**/ * FROM /**/ mytable2 WHERE mytable2.col = 9 /**/ OFFSET /**/ ? /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement =
                "/**/ SELECT /**/ * FROM /**/ mytable WHERE mytable.col = 9 /**/ ORDER /**/ BY mytable.col /**/ DESC /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT MAX(tab1.b) /**/ FROM /**/ tab1 /**/ WHERE a > 34 GROUP BY tab1.b /**/ HAVING MAX(tab1.b) > 56 /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * /*2*/ FROM /*2*/ tab1 /*2*/ WHERE /*3*/ a > 34 /*0*/ GROUP /*0*/ BY tab1.b /*0*/, tab1.a /*0*/, tab1.c /*0*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * /*2*/ INTO /*3*/ tab2 /*4*/ FROM /*7*/ tab1 /*8*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * /*4*/ FROM /*7*/ tab1 /*8*/ WHERE tab1.f /*2*/ NOT /*3*/ BETWEEN 3 /*5*/ AND ? /*6*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * /*4*/ FROM /*7*/ tab1 /*8*/ WHERE tab1.f /*2*/ IS /*1*/ NOT /*3*/ NULL /*6*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * /*4*/ FROM /*7*/ tab1 /*8*/ WHERE tab1.f /*2*/ NOT /*3*/ IN /**/ (/**/ 3 /**/, /**/ 5 /**/, /**/ ? /**/, /**/ 4 /**/ ) /*6*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * /*4*/ FROM /*7*/ tab1 /*8*/ WHERE tab1.f /*2*/ NOT /*3*/ LIKE 'bh%' /*6*/ ESCAPE /*34*/ '1' /*56*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * /*4*/ FROM /*7*/ tab1 /*8*/ WHERE /*5*/ NOT /*53*/ EXISTS (SELECT * FROM t1) /*56*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * /*2*/ FROM /*3*/ tab1 /*4*/ WHERE /**/ f1 /*5*/ = /**/ 5 /*6*/ AND /**/ f2 /*7*/ <= /**/ ? /*8*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ * /*2*/ FROM /*3*/ tab1 /*4*/ WHERE /*11*/ (/**/ a /*5*/ + /**/ b /*6*/ - /**/ c /*7*/ / /**/ d /*8*/ + /**/ e /*9*/ * /**/ f /*11*/ ) /*10*/ * /*11*/ (/**/ a /*11*/ / /**/ b /*12*/ * /*11*/ (/**/ a /*13*/ + /**/ b /*11*/ ) /*11*/ ) /*14*/ > /**/ ? /*15*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/*0*/ SELECT /*1*/ a /**/, /**/ b /**/, /**/ c /*2*/ FROM /*3*/ tab1 /*4*/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ {fn /**/ MAX /**/ (/**/ a /**/, /**/ b /**/, /**/ c /**/ ) /**/ } /**/, /**/ tm.COUNT /**/ (/**/ * /**/ ) /**/, /**/ D /**/ FROM /**/ tab1 /**/ GROUP /**/ BY /**/ D";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "/**/ SELECT /**/ MAX /**/ (/**/ DISTINCT /**/ id /**/ ) /**/ AS /**/ max /**/ FROM /**/ mytable /**/";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);

        statement = "SELECT t1.CALC, t.NAMECALC, t2.VALUEOUT "
                + "FROM TR_SENSOR_DEFINE_PARAM t1"
                + " INNER JOIN TR_CALC_PARAMS t ON t1.CALC = t.TR_CALC_PARAMS_ID"
                + " INNER JOIN TR_SENSORS_CALIBRATION t2 ON t1.SENSOR = t2.SENSOR"
                + " AND t2.VALUEIN <= :parValue"
                + " WHERE :parSensor = t1.SENSOR";
        select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);
    }

    @Test
    public void testSelectItems() throws JSQLParserException {
        String statement =
                "SELECT myid AS MYID, mycol, tab.*, schema.tab.*, mytab.mycol2, myschema.mytab.mycol, myschema.mytab.* FROM mytable WHERE mytable.col = 9";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();

        assertEquals("MYID", ((SelectExpressionItem) plainSelect.getSelectItems().get(0)).getAlias().getName());
        assertEquals("mycol", ((Column) ((SelectExpressionItem) plainSelect.getSelectItems().get(1)).getExpression()).getColumnName());
        assertEquals("tab", ((AllTableColumns) plainSelect.getSelectItems().get(2)).getTable().getName());
        assertEquals("schema", ((AllTableColumns) plainSelect.getSelectItems().get(3)).getTable().getSchemaName());
        assertEquals("schema.tab", ((AllTableColumns) plainSelect.getSelectItems().get(3)).getTable().getWholeTableName());
        assertEquals(
                "mytab.mycol2",
                ((Column) ((SelectExpressionItem) plainSelect.getSelectItems().get(4)).getExpression()).getWholeColumnName());
        assertEquals(
                "myschema.mytab.mycol",
                ((Column) ((SelectExpressionItem) plainSelect.getSelectItems().get(5)).getExpression()).getWholeColumnName());
        assertEquals("myschema.mytab", ((AllTableColumns) plainSelect.getSelectItems().get(6)).getTable().getWholeTableName());
        assertEquals(statement, "" + plainSelect);

        statement = "SELECT myid AS MYID, (SELECT MAX(ID) AS myid2 FROM mytable2) AS myalias FROM mytable WHERE mytable.col = 9";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals("myalias", ((SelectExpressionItem) plainSelect.getSelectItems().get(1)).getAlias().getName());
        assertEquals(statement, "" + plainSelect);

        statement = "SELECT (myid + myid2) AS MYID FROM mytable WHERE mytable.col = 9";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals("MYID", ((SelectExpressionItem) plainSelect.getSelectItems().get(0)).getAlias().getName());
        assertEquals(statement, "" + plainSelect);
    }

    @Test
    public void testUnion() throws JSQLParserException {
        String statement =
                "SELECT * FROM mytable WHERE mytable.col = 9 UNION "
                + "SELECT * FROM mytable3 WHERE mytable3.col = ? UNION "
                + "SELECT * FROM mytable2 LIMIT 3, 4";

        Union union = (Union) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(3, union.getPlainSelects().size());
        assertEquals("mytable", ((Table) ((PlainSelect) union.getPlainSelects().get(0)).getFromItem()).getName());
        assertEquals("mytable3", ((Table) ((PlainSelect) union.getPlainSelects().get(1)).getFromItem()).getName());
        assertEquals("mytable2", ((Table) ((PlainSelect) union.getPlainSelects().get(2)).getFromItem()).getName());
        assertEquals(3, ((PlainSelect) union.getPlainSelects().get(2)).getLimit().getOffset());

        //use brakets for toString
        //use standard limit syntax
        assertEquals(statement, "" + union);

    }

    @Test
    public void testEXCEPT() throws JSQLParserException {
        String statement =
                "SELECT /*sdfhjklsdh*/ * FROM mytable WHERE mytable.col = 9 EXCEPT "
                + "SELECT * FROM mytable3 WHERE mytable3.col = ? UNION "
                + "SELECT * FROM mytable2 LIMIT 3, 4";

        Union union = (Union) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(3, union.getPlainSelects().size());
        assertEquals("mytable", ((Table) ((PlainSelect) union.getPlainSelects().get(0)).getFromItem()).getName());
        assertEquals("mytable3", ((Table) ((PlainSelect) union.getPlainSelects().get(1)).getFromItem()).getName());
        assertEquals("mytable2", ((Table) ((PlainSelect) union.getPlainSelects().get(2)).getFromItem()).getName());
        assertEquals(3, ((PlainSelect) union.getPlainSelects().get(2)).getLimit().getOffset());

        //use brakets for toString
        //use standard limit syntax
        assertEquals(statement, "" + union);

        statement =
                "(SELECT * FROM mytable WHERE mytable.col = 9) INTERSECT "
                + "(SELECT * FROM mytable3 WHERE mytable3.col = ?) UNION "
                + "(SELECT * FROM mytable2 LIMIT 3, 4)";

        union = (Union) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(3, union.getPlainSelects().size());
        assertEquals("mytable", ((Table) ((PlainSelect) union.getPlainSelects().get(0)).getFromItem()).getName());
        assertEquals("mytable3", ((Table) ((PlainSelect) union.getPlainSelects().get(1)).getFromItem()).getName());
        assertEquals("mytable2", ((Table) ((PlainSelect) union.getPlainSelects().get(2)).getFromItem()).getName());
        assertEquals(3, ((PlainSelect) union.getPlainSelects().get(2)).getLimit().getOffset());

        //use brakets for toString
        //use standard limit syntax
        assertEquals(statement, "" + union);
    }

    @Test
    public void testDistinct() throws JSQLParserException {
        String statement = "SELECT DISTINCT ON (myid) myid, mycol FROM mytable WHERE mytable.col = 9";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(
                "myid",
                ((Column) ((SelectExpressionItem) plainSelect.getDistinct().getOnSelectItems().get(0)).getExpression()).getColumnName());
        assertEquals("mycol", ((Column) ((SelectExpressionItem) plainSelect.getSelectItems().get(1)).getExpression()).getColumnName());
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());
    }

    @Test
    public void testFrom() throws JSQLParserException {
        String statement =
                "SELECT * FROM mytable as mytable0, mytable1 alias_tab1, mytable2 as alias_tab2, (SELECT * FROM mytable3) AS mytable4 WHERE mytable.col = 9";
        String statementToString =
                "SELECT * FROM mytable as mytable0, mytable1 as alias_tab1, mytable2 as alias_tab2, (SELECT * FROM mytable3) AS mytable4 WHERE mytable.col = 9";

        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(3, plainSelect.getJoins().size());
        assertEquals("mytable0", ((Table) plainSelect.getFromItem()).getAlias().getName());
        assertEquals("alias_tab1", ((Join) plainSelect.getJoins().get(0)).getRightItem().getAlias().getName());
        assertEquals("alias_tab2", ((Join) plainSelect.getJoins().get(1)).getRightItem().getAlias().getName());
        assertEquals("mytable4", ((Join) plainSelect.getJoins().get(2)).getRightItem().getAlias().getName());
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());

    }

    @Test
    public void testJoin() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 LEFT outer JOIN tab2 ON tab1.id = tab2.id";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(1, plainSelect.getJoins().size());
        assertEquals("tab2", ((Table) ((Join) plainSelect.getJoins().get(0)).getRightItem()).getWholeTableName());
        assertEquals(
                "tab1.id",
                ((Column) ((EqualsTo) ((Join) plainSelect.getJoins().get(0)).getOnExpression()).getLeftExpression()).getWholeColumnName());
        assertTrue(((Join) plainSelect.getJoins().get(0)).isOuter());
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());

        statement = "SELECT * FROM tab1 LEFT outer JOIN tab2 ON tab1.id = tab2.id INNER JOIN tab3";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(2, plainSelect.getJoins().size());
        assertEquals("tab3", ((Table) ((Join) plainSelect.getJoins().get(1)).getRightItem()).getWholeTableName());
        assertFalse(((Join) plainSelect.getJoins().get(1)).isOuter());
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());

        statement = "SELECT * FROM tab1 LEFT outer JOIN tab2 ON tab1.id = tab2.id JOIN tab3";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(2, plainSelect.getJoins().size());
        assertEquals("tab3", ((Table) ((Join) plainSelect.getJoins().get(1)).getRightItem()).getWholeTableName());
        assertFalse(((Join) plainSelect.getJoins().get(1)).isOuter());

        // implicit INNER
        statement = "SELECT * FROM tab1 LEFT outer JOIN tab2 ON tab1.id = tab2.id INNER JOIN tab3";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());

        statement = "SELECT * FROM TA2 LEFT outer JOIN O USING (col1, col2) where D.OasSD = 'asdf' And (kj >= 4 OR l < 'sdf')";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());

        statement = "SELECT * FROM tab1 INNER JOIN tab2 USING (id, id2)";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(1, plainSelect.getJoins().size());
        assertEquals("tab2", ((Table) ((Join) plainSelect.getJoins().get(0)).getRightItem()).getWholeTableName());
        assertFalse(((Join) plainSelect.getJoins().get(0)).isOuter());
        assertEquals(2, ((Join) plainSelect.getJoins().get(0)).getUsingColumns().size());
        assertEquals("id2", ((Column) ((Join) plainSelect.getJoins().get(0)).getUsingColumns().get(1)).getWholeColumnName());
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());

        statement = "SELECT * FROM tab1 RIGHT OUTER JOIN tab2 USING (id, id2)";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());

        statement = "select * from foo as f LEFT INNER JOIN (bar as b RIGHT OUTER JOIN baz as z ON f.id = z.id) ON f.id = b.id";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());

    }

    @Test
    public void testFunctions() throws JSQLParserException {
        String statement = "SELECT MAX(id) as max FROM mytable WHERE mytable.col = 9";
        PlainSelect select = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals("max", ((SelectExpressionItem) select.getSelectItems().get(0)).getAlias().getName());
        assertEquals(statement.toUpperCase(), select.toString().toUpperCase());

        statement = "SELECT MAX(id), AVG(pro) as myavg FROM mytable WHERE mytable.col = 9 GROUP BY pro";
        select = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals("myavg", ((SelectExpressionItem) select.getSelectItems().get(1)).getAlias().getName());
        assertEquals(statement.toUpperCase(), select.toString().toUpperCase());

        statement = "SELECT MAX(a, b, c), COUNT(*), D FROM tab1 GROUP BY D";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        Function fun = (Function) ((SelectExpressionItem) plainSelect.getSelectItems().get(0)).getExpression();
        assertEquals("MAX", fun.getName());
        assertEquals("b", ((Column) fun.getParameters().getExpressions().get(1)).getWholeColumnName());
        assertTrue(((Function) ((SelectExpressionItem) plainSelect.getSelectItems().get(1)).getExpression()).isAllColumns());
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());

        statement = "SELECT {fn MAX(a, b, c)}, COUNT(*), D FROM tab1 GROUP BY D";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        fun = (Function) ((SelectExpressionItem) plainSelect.getSelectItems().get(0)).getExpression();
        assertTrue(fun.isEscaped());
        assertEquals("MAX", fun.getName());
        assertEquals("b", ((Column) fun.getParameters().getExpressions().get(1)).getWholeColumnName());
        assertTrue(((Function) ((SelectExpressionItem) plainSelect.getSelectItems().get(1)).getExpression()).isAllColumns());
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());

        statement = "SELECT ab.MAX(a, b, c), cd.COUNT(*), D FROM tab1 GROUP BY D";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        fun = (Function) ((SelectExpressionItem) plainSelect.getSelectItems().get(0)).getExpression();
        assertEquals("ab.MAX", fun.getName());
        assertEquals("b", ((Column) fun.getParameters().getExpressions().get(1)).getWholeColumnName());
        fun = (Function) ((SelectExpressionItem) plainSelect.getSelectItems().get(1)).getExpression();
        assertEquals("cd.COUNT", fun.getName());
        assertTrue(fun.isAllColumns());
        assertEquals(statement.toUpperCase(), plainSelect.toString().toUpperCase());
    }

    @Test
    public void testWhere() throws JSQLParserException {

        String statement = "SELECT * FROM tab1 WHERE ";
        String whereToString = "(a + b + c / d + e * f) * (a / b * (a + b)) > ?";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement + whereToString))).getSelectBody();
        assertTrue(plainSelect.getWhere() instanceof GreaterThan);
        assertTrue(((GreaterThan) plainSelect.getWhere()).getLeftExpression() instanceof Multiplication);
        assertEquals(statement + whereToString, "" + plainSelect);

        ExpressionDeParser expressionDeParser = new ExpressionDeParser();
        StringBuilder buffer = new StringBuilder();
        expressionDeParser.setBuffer(buffer);
        plainSelect.getWhere().accept(expressionDeParser);
        assertEquals(whereToString, buffer.toString());

        whereToString = "(7 * s + 9 / 3) not between 3 and ?";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement + whereToString))).getSelectBody();

        buffer = new StringBuilder();
        expressionDeParser.setBuffer(buffer);
        plainSelect.getWhere().accept(expressionDeParser);

        assertEquals(whereToString, buffer.toString());
        assertEquals((statement + whereToString).toLowerCase(), ("" + plainSelect).toLowerCase());

        whereToString = "a / b NOT IN (?, 's''adf', 234.2)";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement + whereToString))).getSelectBody();

        buffer = new StringBuilder();
        expressionDeParser.setBuffer(buffer);
        plainSelect.getWhere().accept(expressionDeParser);

        assertEquals(whereToString.toLowerCase(), buffer.toString().toLowerCase());
        assertEquals((statement + whereToString).toLowerCase(), ("" + plainSelect).toLowerCase());

        whereToString = "NOT 0 = 0";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement + whereToString))).getSelectBody();

        String where = " NOT (0 = 0)";
        whereToString = "NOT (0 = 0)";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement + whereToString))).getSelectBody();

        buffer = new StringBuilder();
        expressionDeParser.setBuffer(buffer);
        plainSelect.getWhere().accept(expressionDeParser);

        assertEquals(where.toLowerCase(), buffer.toString().toLowerCase());
        assertEquals((statement + whereToString).toLowerCase(), ("" + plainSelect).toLowerCase());
    }

    @Test
    public void testDebug() throws JSQLParserException {
        String statement = "Select t1.CALC, t.NAMECALC, t2.VALUEOUT "
                + "From TR_SENSOR_DEFINE_PARAM t1"
                + " Inner Join TR_CALC_PARAMS t on t1.CALC = t.TR_CALC_PARAMS_ID"
                + " Inner Join TR_SENSORS_CALIBRATION t2 on t1.SENSOR = t2.SENSOR"
                + " and t2.VALUEIN <= :parValue"
                + " Where :parSensor = t1.SENSOR";
        Select select = (Select) parserManager.parse(new StringReader(statement));
        StringBuilder buf = new StringBuilder();
        StatementDeParser deparser = new StatementDeParser(buf);
        deparser.visit(select);
    }

    @Test
    public void testGroupBy() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE a > 34 GROUP BY tab1.b";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(1, plainSelect.getGroupByColumnReferences().size());
        assertEquals("tab1.b", ((Column) plainSelect.getGroupByColumnReferences().get(0)).getWholeColumnName());
        assertEquals(statement, "" + plainSelect);

        statement = "SELECT * FROM tab1 WHERE a > 34 GROUP BY 2, 3";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(2, plainSelect.getGroupByColumnReferences().size());
        assertEquals(2, ((LongValue) plainSelect.getGroupByColumnReferences().get(0)).getValue());
        assertEquals(3, ((LongValue) plainSelect.getGroupByColumnReferences().get(1)).getValue());
        assertEquals(statement, "" + plainSelect);
    }

    @Test
    public void testHaving() throws JSQLParserException {
        String statement = "SELECT MAX(tab1.b) FROM tab1 WHERE a > 34 GROUP BY tab1.b HAVING MAX(tab1.b) > 56";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertTrue(plainSelect.getHaving() instanceof GreaterThan);
        assertEquals(statement, "" + plainSelect);

        statement = "SELECT MAX(tab1.b) FROM tab1 WHERE a > 34 HAVING MAX(tab1.b) IN (56, 32, 3, ?)";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertTrue(plainSelect.getHaving() instanceof InExpression);
        assertEquals(statement, "" + plainSelect);
    }

    @Test
    public void testExists() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE";
        String where = " EXISTS (SELECT * FROM tab2)";
        statement += where;
        Statement parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);

        PlainSelect plainSelect = (PlainSelect) ((Select) parsed).getSelectBody();
        ExpressionDeParser expressionDeParser = new ExpressionDeParser();
        StringBuilder buffer = new StringBuilder();
        expressionDeParser.setBuffer(buffer);
        SelectDeParser deParser = new SelectDeParser(expressionDeParser, buffer);
        expressionDeParser.setSelectVisitor(deParser);
        plainSelect.getWhere().accept(expressionDeParser);
        assertEquals(where.toLowerCase(), buffer.toString().replaceAll("[\n\r]+", "").toLowerCase());
    }

    @Test
    public void testOrderBy() throws JSQLParserException {
        //TODO: should there be a DESC marker in the OrderByElement class?
        String statement = "SELECT * FROM tab1 WHERE a > 34 GROUP BY tab1.b ORDER BY tab1.a DESC, tab1.b ASC";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(2, plainSelect.getOrderByElements().size());
        assertEquals("tab1.a", ((Column) ((OrderByElement) plainSelect.getOrderByElements().get(0)).getExpression()).getWholeColumnName());
        assertEquals("b", ((Column) ((OrderByElement) plainSelect.getOrderByElements().get(1)).getExpression()).getColumnName());
        assertTrue(((OrderByElement) plainSelect.getOrderByElements().get(1)).isAsc());
        assertFalse(((OrderByElement) plainSelect.getOrderByElements().get(0)).isAsc());
        assertEquals(statement, "" + plainSelect);

        ExpressionDeParser expressionDeParser = new ExpressionDeParser();
        StringBuilder buffer = new StringBuilder();
        SelectDeParser deParser = new SelectDeParser(expressionDeParser, buffer);
        expressionDeParser.setSelectVisitor(deParser);
        expressionDeParser.setBuffer(buffer);
        plainSelect.accept(deParser);
        assertEquals(statement.toString().toUpperCase(), buffer.toString().toUpperCase().replaceAll(ExpressionDeParser.LINE_SEPARATOR, ""));

        statement = "SELECT * FROM tab1 WHERE a > 34 GROUP BY tab1.b ORDER BY tab1.a, 2";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals(2, plainSelect.getOrderByElements().size());
        assertEquals("a", ((Column) ((OrderByElement) plainSelect.getOrderByElements().get(0)).getExpression()).getColumnName());
        assertEquals(2, ((LongValue) ((OrderByElement) plainSelect.getOrderByElements().get(1)).getExpression()).getValue());
        assertEquals(statement, "" + plainSelect);
    }

    @Test
    public void testTimestamp() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE a > {ts '2004-04-30 04:05:34.56'}";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals("2004-04-30 04:05:34.56", ((TimestampValue) ((GreaterThan) plainSelect.getWhere()).getRightExpression()).getValue().toString());
        assertEquals(statement, "" + plainSelect);
    }

    @Test
    public void testTime() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE a > {t '04:05:34'}";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals("04:05:34", (((TimeValue) ((GreaterThan) plainSelect.getWhere()).getRightExpression()).getValue()).toString());
        assertEquals(statement, "" + plainSelect);
    }

    @Test
    public void testCase() throws JSQLParserException {
        String statement = "SELECT a, CASE b WHEN 1 THEN 2 END FROM tab1";
        Statement parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);

        statement = "SELECT a, (CASE WHEN (a > 2) THEN 3 END) AS b FROM tab1";
        parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);

        statement = "SELECT a, (CASE WHEN a > 2 THEN 3 ELSE 4 END) AS b FROM tab1";
        parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);


        statement = "SELECT a, (CASE b WHEN 1 THEN 2 WHEN 3 THEN 4 ELSE 5 END) FROM tab1";
        parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);

        statement = "SELECT a, (CASE "
                + "WHEN b > 1 THEN 'BBB' "
                + "WHEN a = 3 THEN 'AAA' "
                + "END) FROM tab1";
        parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);

        statement = "SELECT a, (CASE "
                + "WHEN b > 1 THEN 'BBB' "
                + "WHEN a = 3 THEN 'AAA' "
                + "END) FROM tab1 "
                + "WHERE c = (CASE "
                + "WHEN d <> 3 THEN 5 "
                + "ELSE 10 "
                + "END)";
        parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);

        statement = "SELECT a, CASE a "
                + "WHEN 'b' THEN 'BBB' "
                + "WHEN 'a' THEN 'AAA' "
                + "END AS b FROM tab1";
        parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);

        statement = "SELECT a FROM tab1 WHERE CASE b WHEN 1 THEN 2 WHEN 3 THEN 4 ELSE 5 END > 34";
        parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);

        statement = "SELECT a FROM tab1 WHERE CASE b WHEN 1 THEN 2 + 3 ELSE 4 END > 34";
        parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);



        statement = "SELECT a, (CASE "
                + "WHEN (CASE a WHEN 1 THEN 10 ELSE 20 END) > 15 THEN 'BBB' "
                + //		"WHEN (SELECT c FROM tab2 WHERE d = 2) = 3 THEN 'AAA' " +
                "END) FROM tab1";
        parsed = parserManager.parse(new StringReader(statement));
        //System.out.println(""+statement);
        //System.out.println(""+parsed);
        assertEquals(statement, "" + parsed);
    }

    @Test
    public void testReplaceAsFunction() throws JSQLParserException {
        String statement = "SELECT REPLACE(a, 'b', c) FROM tab1";
        Statement parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);
    }

    @Test
    public void testLike() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE a LIKE 'test'";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals("test", (((StringValue) ((LikeExpression) plainSelect.getWhere()).getRightExpression()).getValue()).toString());
        assertEquals(statement, "" + plainSelect);

        statement = "SELECT * FROM tab1 WHERE a LIKE 'test' ESCAPE 'test2'";
        plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        assertEquals("test", (((StringValue) ((LikeExpression) plainSelect.getWhere()).getRightExpression()).getValue()).toString());
        assertEquals("test2", (((LikeExpression) plainSelect.getWhere()).getEscape()));
        assertEquals(statement, "" + plainSelect);
    }

    @Test
    public void testSelectOrderHaving() throws JSQLParserException {
        String statement = "SELECT units, count(units) AS num FROM currency GROUP BY units HAVING count(units) > 1 ORDER BY num";
        Statement parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);
    }

    @Test
    public void testDouble() throws JSQLParserException {
        String statement = "SELECT 1e2, * FROM mytable WHERE mytable.col = 9";
        Select select = (Select) parserManager.parse(new StringReader(statement));

        assertEquals(1e2, ((DoubleValue) ((SelectExpressionItem) ((PlainSelect) select.getSelectBody()).getSelectItems().get(0)).getExpression()).getValue(), 0);

        statement = "SELECT * FROM mytable WHERE mytable.col = 1.e2";
        select = (Select) parserManager.parse(new StringReader(statement));

        assertEquals(1e2, ((DoubleValue) ((BinaryExpression) ((PlainSelect) select.getSelectBody()).getWhere()).getRightExpression()).getValue(), 0);

        statement = "SELECT * FROM mytable WHERE mytable.col = 1.2e2";
        select = (Select) parserManager.parse(new StringReader(statement));

        assertEquals(1.2e2, ((DoubleValue) ((BinaryExpression) ((PlainSelect) select.getSelectBody()).getWhere()).getRightExpression()).getValue(), 0);

        statement = "SELECT * FROM mytable WHERE mytable.col = 2e2";
        select = (Select) parserManager.parse(new StringReader(statement));

        assertEquals(2e2, ((DoubleValue) ((BinaryExpression) ((PlainSelect) select.getSelectBody()).getWhere()).getRightExpression()).getValue(), 0);
    }

    @Test
    public void testWith() throws JSQLParserException {
        String statement = "WITH DINFO (DEPTNO, AVGSALARY, EMPCOUNT) AS "
                + "(SELECT OTHERS.WORKDEPT, AVG(OTHERS.SALARY), COUNT(*) FROM EMPLOYEE AS OTHERS "
                + "GROUP BY OTHERS.WORKDEPT), DINFOMAX AS (SELECT MAX(AVGSALARY) AS AVGMAX FROM DINFO) "
                + "SELECT THIS_EMP.EMPNO, THIS_EMP.SALARY, DINFO.AVGSALARY, DINFO.EMPCOUNT, DINFOMAX.AVGMAX "
                + "FROM EMPLOYEE AS THIS_EMP INNER JOIN DINFO INNER JOIN DINFOMAX "
                + "WHERE THIS_EMP.JOB = 'SALESREP' AND THIS_EMP.WORKDEPT = DINFO.DEPTNO";
        Select select = (Select) parserManager.parse(new StringReader(statement));
        Statement parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);
    }

    @Test
    public void testSelectAliasInQuotes() throws JSQLParserException {
        String statement = "SELECT mycolumn AS \"My Column Name\" FROM mytable";
        Statement parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);
    }

    @Test
    public void testSelectJoinWithComma() throws JSQLParserException {
        String statement = "SELECT cb.Genus, cb.Species FROM Coleccion_de_Briofitas AS cb, unigeoestados AS es "
                + "WHERE es.nombre = \"Tamaulipas\" AND cb.the_geom = es.geom";
        Statement parsed = parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + parsed);
    }

    @Test
    public void testDeparser() throws JSQLParserException {
        String statement = "SELECT a.OWNERLASTNAME, a.OWNERFIRSTNAME "
                + "FROM ANTIQUEOWNERS AS a, ANTIQUES AS b "
                + "WHERE b.BUYERID = a.OWNERID AND b.ITEM = 'Chair'";
        Statement parsed = parserManager.parse(new StringReader(statement));
        StatementDeParser deParser = new StatementDeParser(new StringBuilder());
        parsed.accept(deParser);
        assertEquals(statement.toLowerCase(), deParser.getBuffer().toString().replaceAll("[\r\n]+", "").toLowerCase());

        statement = "SELECT count(DISTINCT f + 4) FROM a";
        parsed = parserManager.parse(new StringReader(statement));
        deParser = new StatementDeParser(new StringBuilder());
        parsed.accept(deParser);
        assertEquals(statement.toLowerCase(), parsed.toString().replaceAll("[\r\n]+", "").toLowerCase());
        assertEquals(statement.toLowerCase(), deParser.getBuffer().toString().replaceAll("[\r\n]+", "").toLowerCase());

        statement = "SELECT count(DISTINCT f, g, h) FROM a";
        parsed = parserManager.parse(new StringReader(statement));
        deParser = new StatementDeParser(new StringBuilder());
        parsed.accept(deParser);
        assertEquals(statement.toLowerCase(), parsed.toString().replaceAll("[\r\n]+", "").toLowerCase());
        assertEquals(statement.toLowerCase(), deParser.getBuffer().toString().replaceAll("[\r\n]+", "").toLowerCase());
    }

    @Test
    public void testMysqlQuote() throws JSQLParserException {
        String statement = "SELECT `a.OWNERLASTNAME`, `OWNERFIRSTNAME` "
                + "FROM `ANTIQUEOWNERS` AS a, ANTIQUES AS b "
                + "WHERE b.BUYERID = a.OWNERID AND b.ITEM = 'Chair'";
        Statement parsed = parserManager.parse(new StringReader(statement));
        StatementDeParser deParser = new StatementDeParser(new StringBuilder());
        parsed.accept(deParser);

        assertEquals(statement, parsed.toString());
        assertEquals(statement.toLowerCase(), deParser.getBuffer().toString().replaceAll("[\r\n]+", "").toLowerCase());
    }

    @Test
    public void testConcat() throws JSQLParserException {
        String statement = "SELECT a || b || c + 4 FROM t";
        Statement parsed = parserManager.parse(new StringReader(statement));
        StatementDeParser deParser = new StatementDeParser(new StringBuilder());
        parsed.accept(deParser);

        assertEquals(statement, parsed.toString());
        assertEquals(statement.toLowerCase(), deParser.getBuffer().toString().replaceAll("[\r\n]+", "").toLowerCase());
    }

    @Test
    public void testMatches() throws JSQLParserException {
        String statement = "SELECT * FROM team WHERE team.search_column @@ to_tsquery('new & york & yankees')";
        Statement parsed = parserManager.parse(new StringReader(statement));
        StatementDeParser deParser = new StatementDeParser(new StringBuilder());
        parsed.accept(deParser);

        assertEquals(statement, parsed.toString());
        assertEquals(statement.toLowerCase(), deParser.getBuffer().toString().replaceAll("[\r\n]+", "").toLowerCase());
    }

    @Test
    public void testGroupByExpression() throws JSQLParserException {
        String statement =
                "SELECT col1, col2, col1 + col2, sum(col8)"
                + " FROM table1 "
                + "GROUP BY col1, col2, col1 + col2";
        Statement parsed = parserManager.parse(new StringReader(statement));
        StatementDeParser deParser = new StatementDeParser(new StringBuilder());
        parsed.accept(deParser);

        assertEquals(statement, parsed.toString());
        assertEquals(statement.toLowerCase(), deParser.getBuffer().toString().replaceAll("[\r\n]+", "").toLowerCase());
    }

    @Test
    public void testBitwise() throws JSQLParserException {
        String statement =
                "SELECT col1 & 32, col2 ^ col1, col1 | col2"
                + " FROM table1";
        Statement parsed = parserManager.parse(new StringReader(statement));
        StatementDeParser deParser = new StatementDeParser(new StringBuilder());
        parsed.accept(deParser);

        assertEquals(statement, parsed.toString());
        assertEquals(statement.toLowerCase(), deParser.getBuffer().toString().replaceAll("[\r\n]+", "").toLowerCase());
    }
}
