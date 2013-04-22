package net.sf.jsqlparser.test.delete;

import java.io.StringReader;

import junit.framework.TestCase;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteTest extends TestCase {

    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    public DeleteTest(String arg0) {
        super(arg0);
    }

    public void testDelete() throws JSQLParserException {
        String statement = "DELETE FROM mytable WHERE mytable.col = 9";

        Delete delete = (Delete) parserManager.parse(new StringReader(statement));
        assertEquals("mytable", delete.getTable().getName());
        assertEquals(statement, "" + delete);
    }
    
    public void testComment() throws JSQLParserException {
        String statement =
                "/*90053*/ DELETE /*werwer*/ FROM /*wefsdfjil*/ mytable /*90piop*/ WHERE mytable.col = 9 /*eiortouei*/";
        Delete delete = (Delete) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + delete);
    }
    
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(DeleteTest.class);
    }
}
