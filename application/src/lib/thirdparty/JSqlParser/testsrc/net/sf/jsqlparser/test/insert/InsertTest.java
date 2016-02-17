package net.sf.jsqlparser.test.insert;

import java.io.StringReader;

import junit.framework.TestCase;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.junit.Test;

public class InsertTest extends TestCase {

    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testRegularInsert() throws JSQLParserException {
        String statement = "INSERT INTO mytable (col1, col2, col3) VALUES (?, 'sadfsd', 234)";
        Insert insert = (Insert) parserManager.parse(new StringReader(statement));
        assertEquals("mytable", insert.getTable().getName());
        assertEquals(3, insert.getColumns().size());
        assertEquals("col1", ((Column) insert.getColumns().get(0)).getColumnName());
        assertEquals("col2", ((Column) insert.getColumns().get(1)).getColumnName());
        assertEquals("col3", ((Column) insert.getColumns().get(2)).getColumnName());
        assertEquals(3, ((ExpressionList) insert.getItemsList()).getExpressions().size());
        assertTrue(((ExpressionList) insert.getItemsList()).getExpressions().get(0) instanceof JdbcParameter);
        assertEquals("sadfsd", ((StringValue) ((ExpressionList) insert.getItemsList()).getExpressions().get(1)).getValue());
        assertEquals(234, ((LongValue) ((ExpressionList) insert.getItemsList()).getExpressions().get(2)).getValue());
        assertEquals(statement, "" + insert);

        statement = "INSERT INTO myschema.mytable VALUES (?, ?, 2.3)";
        insert = (Insert) parserManager.parse(new StringReader(statement));
        assertEquals("myschema.mytable", insert.getTable().getWholeTableName());
        assertEquals(3, ((ExpressionList) insert.getItemsList()).getExpressions().size());
        assertTrue(((ExpressionList) insert.getItemsList()).getExpressions().get(0) instanceof JdbcParameter);
        assertEquals(2.3, ((DoubleValue) ((ExpressionList) insert.getItemsList()).getExpressions().get(2)).getValue(), 0.0);
        assertEquals(statement, "" + insert);
    }
    
    @Test
    public void testComment() throws JSQLParserException {
        String statement =
                "/*90053*/ INSERT /*erte*/ INTO /*jkl;kl;*/ mytable /*dfgdg*/ (/*24242*/ col1 /*werfwer*/, /*24242*/ col2 /*werwerw*/, /*24242*/ col3 /*werwerw*/ ) /*werfsfs*/ VALUES /*weruwer*/ (/**/ ? /**/, /**/ 'sadfsd' /**/, /**/ 234 /**/ ) /*eiortouei*/";
        Insert insert = (Insert) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + insert);
        
        statement = "/*0*/ INSERT /*1*/ INTO /*2*/ mytable /*3*/ (/*4*/ col1 /*5*/, /*6*/ col2 /*7*/, /*8*/ col3 /*9*/ ) /*10*/ (/*11*/ SELECT /*12*/ * FROM /*13*/ mytable2 /*14*/) /*15*/";
        insert = (Insert) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + insert);
    }
    
    @Test
    public void testInsertFromSelect() throws JSQLParserException {
        String statement = "INSERT INTO mytable t (col1, col2, col3) SELECT * FROM mytable2";
        Insert insert = (Insert) parserManager.parse(new StringReader(statement));
        assertEquals("mytable", insert.getTable().getName());
        assertEquals(3, insert.getColumns().size());
        assertEquals("col1", ((Column) insert.getColumns().get(0)).getColumnName());
        assertEquals("col2", ((Column) insert.getColumns().get(1)).getColumnName());
        assertEquals("col3", ((Column) insert.getColumns().get(2)).getColumnName());
        assertTrue(insert.getItemsList() instanceof SubSelect);
        assertEquals("mytable2", ((Table) ((PlainSelect) ((SubSelect) insert.getItemsList()).getSelectBody()).getFromItem()).getName());

        //toString uses brakets
        String statementToString = "INSERT INTO mytable t (col1, col2, col3) (SELECT * FROM mytable2)";
        assertEquals(statementToString, "" + insert);
    }
}
