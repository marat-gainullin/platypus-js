package net.sf.jsqlparser.test.delete;

import java.io.StringReader;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.delete.Delete;
import static org.junit.Assert.*;
import org.junit.Test;

public class DeleteTest {

    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testDelete() throws JSQLParserException {
        String statement = "DELETE FROM mytable WHERE mytable.col = 9";

        Delete delete = (Delete) parserManager.parse(new StringReader(statement));
        assertEquals("mytable", delete.getTable().getName());
        assertEquals(statement, "" + delete);
    }
    
    @Test
    public void testComment() throws JSQLParserException {
        String statement =
                "/*90053*/ DELETE /*werwer*/ FROM /*wefsdfjil*/ mytable /*90piop*/ WHERE mytable.col = 9 /*eiortouei*/";
        Delete delete = (Delete) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + delete);
    }
}
