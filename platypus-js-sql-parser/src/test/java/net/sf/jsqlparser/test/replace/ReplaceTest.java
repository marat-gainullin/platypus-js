package net.sf.jsqlparser.test.replace;

import java.io.StringReader;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.SubSelect;
import static org.junit.Assert.*;
import org.junit.Test;

public class ReplaceTest {

    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testReplaceSyntax1() throws JSQLParserException {
        String statement = "REPLACE mytable SET col1='as', col2=?, col3=565";
        Replace replace = (Replace) parserManager.parse(new StringReader(statement));
        assertEquals("mytable", replace.getTable().getName());
        assertEquals(3, replace.getColumns().size());
        assertEquals("col1", ((Column) replace.getColumns().get(0)).getColumnName());
        assertEquals("col2", ((Column) replace.getColumns().get(1)).getColumnName());
        assertEquals("col3", ((Column) replace.getColumns().get(2)).getColumnName());
        assertEquals("as", ((StringValue) replace.getExpressions().get(0)).getValue());
        assertTrue(replace.getExpressions().get(1) instanceof JdbcParameter);
        assertEquals(565, ((LongValue) replace.getExpressions().get(2)).getValue());
        assertEquals(statement, "" + replace);

    }
    
    @Test
    public void testComment() throws JSQLParserException {
        String statement =
                "/*90053*/ REPLACE /*347*/ INTO /*347*/ mytable /*347*/ SET /*347*/ col1 /*347*/ ='as' /*347*/ , /*347*/ col2 /*347*/ =? /*347*/ , /*347*/ col3 /*347*/ =565 /*eiortouei*/";
        Replace replace = (Replace) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + replace);
        
        statement = "/*qwe*/ REPLACE /*1*/ mytable /*2*/ (/*3*/ col1 /*4*/, /*5*/ col2 /*6*/, /*7*/ col3 /*8*/ ) /*9*/ VALUES /*10*/ (/**/ 'as' /**/, /**/ ? /**/, /**/ 565 /**/ ) /*11*/";
        replace = (Replace) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + replace);
        
        statement = "/*qwe12313*/ REPLACE /*1*/ mytable /*2*/ (/*3*/ col1 /*4*/, /*5*/ col2 /*6*/, /*7*/ col3 /*8*/ ) /*9*/ SELECT * FROM mytable3 /*10*/";
        replace = (Replace) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + replace);
    }
    
    @Test
    public void testReplaceSyntax2() throws JSQLParserException {
        String statement = "REPLACE mytable (col1, col2, col3) VALUES ('as', ?, 565)";
        Replace replace = (Replace) parserManager.parse(new StringReader(statement));
        assertEquals("mytable", replace.getTable().getName());
        assertEquals(3, replace.getColumns().size());
        assertEquals("col1", ((Column) replace.getColumns().get(0)).getColumnName());
        assertEquals("col2", ((Column) replace.getColumns().get(1)).getColumnName());
        assertEquals("col3", ((Column) replace.getColumns().get(2)).getColumnName());
        assertEquals("as", ((StringValue) ((ExpressionList) replace.getItemsList()).getExpressions().get(0)).getValue());
        assertTrue(((ExpressionList) replace.getItemsList()).getExpressions().get(1) instanceof JdbcParameter);
        assertEquals(565, ((LongValue) ((ExpressionList) replace.getItemsList()).getExpressions().get(2)).getValue());
        assertEquals(statement, "" + replace);
    }

    @Test
    public void testReplaceSyntax3() throws JSQLParserException {
        String statement = "REPLACE mytable (col1, col2, col3) SELECT * FROM mytable3";
        Replace replace = (Replace) parserManager.parse(new StringReader(statement));
        assertEquals("mytable", replace.getTable().getName());
        assertEquals(3, replace.getColumns().size());
        assertEquals("col1", ((Column) replace.getColumns().get(0)).getColumnName());
        assertEquals("col2", ((Column) replace.getColumns().get(1)).getColumnName());
        assertEquals("col3", ((Column) replace.getColumns().get(2)).getColumnName());
        assertTrue(replace.getItemsList() instanceof SubSelect);
        //TODO:
        //assertEquals(statement, ""+replace);
    }
}
