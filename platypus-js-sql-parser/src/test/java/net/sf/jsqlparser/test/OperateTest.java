/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.jsqlparser.test;

import java.io.StringReader;
import java.util.Map;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.TablesFinder;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class OperateTest extends GeneralTest{

    @Test
    public void absTest() throws JSQLParserException {
        String statement = "select f1, abs(F3, F4, 1) as f2 from t1";
        checkParseAndDeparse(statement);
    }

    @Test
    public void maxTest() throws JSQLParserException {
        String statement = "select f1, max(f2) as mf2 from t1";
        checkParseAndDeparse(statement);
    }

    @Test
    public void nvlTest() throws JSQLParserException {
        String statement = "select f1, nvl(f2, f3) as nvlf2 from t1";
        checkParseAndDeparse(statement);
    }

    @Test
    public void betweenTest() throws JSQLParserException {
        String statement = "select f1, f2 from t1 where t1.f3 BETWEEN 2 AND 8";
        checkParseAndDeparse(statement);
    }

    @Test
    public void subqueryTest() throws JSQLParserException {
        String statement = "SELECT T0.ORDER_NO, 'Some text' AS VALUE_FIELD_1, TABLE1.ID, TABLE1.F1, TABLE1.F3, T0.AMOUNT FROM TABLE1, TABLE2, (SELECT GOODORDER.ORDER_ID AS ORDER_NO, GOODORDER.AMOUNT, CUSTOMER.CUSTOMER_NAME AS CUSTOMER FROM GOODORDER INNER JOIN CUSTOMER ON (GOODORDER.CUSTOMER = CUSTOMER.CUSTOMER_ID) AND (GOODORDER.AMOUNT > CUSTOMER.CUSTOMER_NAME) WHERE :P4 = GOODORDER.GOOD) aS T0 WHERE ((TABLE2.FIELDA < TABLE1.F1) AND (:P2 = TABLE1.F3)) AND (:P3 = T0.AMOUNT)";
        checkParseAndDeparse(statement);
    }

    @Test
    public void asteriskTest() throws JSQLParserException {
        String statement = "SELECT * FROM TABLE1, TABLE2 where TABLE1.f1 is null";
        checkParseAndDeparse(statement);
    }

    @Test
    public void keywordsSubstitutingByTableAliasTest() throws JSQLParserException {
        String statementText = "SELECT * FROM TABLE1 where TABLE1.f1 is null";
        checkParseAndDeparse(statementText);
        Statement statement = parserManager.parse(new StringReader(statementText));
        Map<String, Table> tbls = TablesFinder.getTablesMap(null, statement,true);
        assertEquals(1, tbls.size());
        assertEquals("TABLE1", tbls.keySet().iterator().next());
        tbls = null;
    }

    @Test
    public void keywordsSubstitutingByColumnAliasTest() throws JSQLParserException {
        // Where may be treated as alias.
        // Testing partial LL(2) grammar capability 
        String statement = "SELECT TABLE1.f1 FROM TABLE1 where TABLE1.f1 is null";
        checkParseAndDeparse(statement);
    }

    @Test
    public void tableAliasTest() throws JSQLParserException {
        String statement = "SELECT t1.f1 FROM TABLE1 as t1 where t1.f1 is null";
        checkParseAndDeparse(statement);
    }
    /*
    @Test
    public void singleLineCommentTest() throws JSQLParserException {
        String statement = "SELECT * FROM TABLE1, TABLE2\n --This is single line comment";
        checkParseAndDeparse(statement);
    }

    @Test
    public void multiLineCommentTest() throws JSQLParserException {
        String statement = "SELECT * FROM TABLE1, TABLE2\n"
                +"/*\n"
                + "This is multiline comment.\n"
                + "This is the second lime of multiline comment.\n"
                + "*/   /*";
        checkParseAndDeparse(statement);
    }
*/
    @Test
    public void deleteTest() throws JSQLParserException {
        String statement = "delete from MTD_ENTITIES where MDENT_ID = :id";
        checkParseAndDeparse(statement);
    }

    @Test
    public void insert3ParamsTest() throws JSQLParserException {
        String statement = "insert into MTD_ENTITIES(MDENT_ID, MDENT_TYPE, MDENT_NAME) values (:id, :type, :name)";
        checkParseAndDeparse(statement);
    }

    @Test
    public void insert4ParamsTest() throws JSQLParserException {
        String statement = "insert into MTD_ENTITIES(MDENT_ID, MDENT_TYPE, MDENT_NAME, MDENT_CONTENT_TXT) values (:id, :type, :name, :content)";
        checkParseAndDeparse(statement);
    }
}
