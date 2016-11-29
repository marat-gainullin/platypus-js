package net.sf.jsqlparser.test.drop;

import java.io.StringReader;

import junit.framework.TestCase;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.drop.Drop;
import org.junit.Test;

public class DropTest extends TestCase {

    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testDrop() throws JSQLParserException {
        String statement =
                "DROP TABLE mytab";
        Drop drop = (Drop) parserManager.parse(new StringReader(statement));
        assertEquals("TABLE", drop.getType());
        assertEquals("mytab", drop.getName());
        assertEquals(statement, "" + drop);

        statement =
                "DROP INDEX myindex CASCADE";
        drop = (Drop) parserManager.parse(new StringReader(statement));
        assertEquals("INDEX", drop.getType());
        assertEquals("myindex", drop.getName());
        assertEquals("CASCADE", drop.getParameters().get(0));
        assertEquals(statement, "" + drop);
    }
    
    @Test
    public void testComment() throws JSQLParserException {
        String statement =
                "/*90053*/ DROP /*erlgjter*/ TABLE /*weweporwepr*/ mytab /*wekljrhs*/ CAS /*erpuppwe*/ MAS /*eiortouei*/";
        Drop drop = (Drop) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + drop);
    }
}
