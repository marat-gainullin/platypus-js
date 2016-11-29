/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.jsqlparser.test.connect;

import java.io.StringReader;
import net.sf.jsqlparser.test.GeneralTest;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author mg
 */
public class ConnectByTest extends GeneralTest{


    @Test
    public void startWithPriorBeforeTest() throws JSQLParserException {
        String statement = "SELECT last_name, employee_id, manager_id, LEVEL FROM employees START WITH employee_id = :par1 CONNECT BY NOCYCLE (PRIOR employee_id <> manager_id) ORDER BY last_name ASC";
        checkParseAndDeparse(statement);
    }

    @Test
    public void startWithPriorAfterTest() throws JSQLParserException {
        String statement = "SELECT last_name, employee_id, manager_id, LEVEL FROM employees START WITH employee_id = ? CONNECT BY NOCYCLE employee_id = PRIOR manager_id ORDER BY last_name DESC";
        checkParseAndDeparse(statement);
    }
    
    @Test
    public void startWithPriorAfterTestWithComment() throws JSQLParserException {
        String statement = "/*0*/ SELECT /*1*/ last_name, /*2*/ employee_id, /*3*/ manager_id, LEVEL FROM employees /*4*/ START /*5*/ WITH employee_id = ? /*6*/ CONNECT /*7*/ BY /*8*/ NOCYCLE employee_id = PRIOR manager_id ORDER BY last_name DESC /*9*/";
        //checkParseAndDeparse(statement);
        Select select = (Select) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + select);
    }
}
