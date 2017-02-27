package net.sf.jsqlparser.test.create;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.test.TestException;
import net.sf.jsqlparser.test.tablesfinder.TablesNamesFinder;
import org.junit.Test;
import static org.junit.Assert.*;

public class CreateTableTest {

    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testCreateTable() throws JSQLParserException {
        String statement
                = "CREATE TABLE mytab (mycol a (10, 20) c nm g, mycol2 mypar1 mypar2 (23,323,3) asdf ('23','123') dasd, "
                + "PRIMARY KEY (mycol2, mycol)) type = myisam";
        CreateTable createTable = (CreateTable) parserManager.parse(new StringReader(statement));
        assertEquals(2, createTable.getColumnDefinitions().size());
        assertEquals("mycol", ((ColumnDefinition) createTable.getColumnDefinitions().get(0)).getColumnName());
        assertEquals("mycol2", ((ColumnDefinition) createTable.getColumnDefinitions().get(1)).getColumnName());
        assertEquals("PRIMARY KEY", ((Index) createTable.getIndexes().get(0)).getType());
        assertEquals("mycol", ((Index) createTable.getIndexes().get(0)).getColumnsNames().get(1));
        assertEquals(statement, "" + createTable);
    }

    @Test
    public void testComment() throws JSQLParserException {
        String statement
                = "/*90053*/ CREATE /*11*/ TEMPORARY /**/ TABLE /**/ mytab /**/ (/**/ mycol /**/ a /**/ (/**/ 10 /**/, /**/ 20 /**/ ) "
                + "/** /c /**/ nm /**/ g /*100*/, /**/ mycol2 /**/ mypar1 /**/ mypar2 /**/ ( /**/ 23 /**/ , /**/ 323 /**/ , /**/ 3 /**/ ) "
                + "/**/ asdf /**/ ( /**/ '23' /**/ , /**/ '123' /**/ ) /**/ dasd /*200*/, "
                + "/**/ PRIMARY /**/ KEY /**/ (/**/ mycol2 /**/, /**/ mycol /**/ ) /*300*/, /**/ INDEX /**/ "
                + "auth /**/ (/**/ nickname /**/, /**/ password /**/ ) /**/ ) /**/ type /**/ = /**/ myisam /*eiortouei*/";
        CreateTable createTable = (CreateTable) parserManager.parse(new StringReader(statement));
        assertEquals(statement, "" + createTable);
    }

    @Test
    public void testRUBiSCreateList() throws Exception {
        URL rubis = Thread.currentThread().getContextClassLoader().getResource("RUBiS-create-requests.txt");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(rubis.openStream()))) {
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            int numSt = 1;
            while (true) {
                String line = getLine(in);
                if (line == null) {
                    break;
                }

                if (!line.equals("#begin")) {
                    break;
                }
                line = getLine(in);
                StringBuilder buf = new StringBuilder(line);
                while (true) {
                    line = getLine(in);
                    if (line.equals("#end")) {
                        break;
                    }
                    buf.append("\n");
                    buf.append(line);
                }

                String query = buf.toString();
                if (!getLine(in).equals("true")) {
                    continue;
                }

                String tableName = getLine(in);
                String cols = getLine(in);
                try {
                    CreateTable createTable = (CreateTable) parserManager.parse(new StringReader(query));
                    String[] colsList = null;
                    if (cols.equals("null")) {
                        colsList = new String[0];
                    } else {
                        StringTokenizer tokenizer = new StringTokenizer(cols, " ");

                        List colsListList = new ArrayList();
                        while (tokenizer.hasMoreTokens()) {
                            colsListList.add(tokenizer.nextToken());
                        }

                        colsList = (String[]) colsListList.toArray(new String[colsListList.size()]);

                    }
                    List colsFound = new ArrayList();
                    if (createTable.getColumnDefinitions() != null) {
                        for (Iterator iter = createTable.getColumnDefinitions().iterator(); iter.hasNext();) {
                            ColumnDefinition columnDefinition = (ColumnDefinition) iter.next();
                            String colName = columnDefinition.getColumnName();
                            boolean unique = false;
                            if (createTable.getIndexes() != null) {
                                for (Iterator iterator = createTable.getIndexes().iterator(); iterator.hasNext();) {
                                    Index index = (Index) iterator.next();
                                    if (index.getType().equals("PRIMARY KEY")
                                            && index.getColumnsNames().size() == 1
                                            && index.getColumnsNames().get(0).equals(colName)) {
                                        unique = true;
                                    }

                                }
                            }

                            if (!unique) {
                                if (columnDefinition.getColumnSpecStrings() != null) {
                                    for (Iterator iterator = columnDefinition.getColumnSpecStrings().iterator(); iterator.hasNext();) {
                                        String par = (String) iterator.next();
                                        if (par.equals("UNIQUE")) {
                                            unique = true;
                                        } else if (par.equals("PRIMARY") && iterator.hasNext() && iterator.next().equals("KEY")) {
                                            unique = true;
                                        }
                                    }
                                }
                            }
                            if (unique) {
                                colName += ".unique";
                            }
                            colsFound.add(colName.toLowerCase());
                        }
                    }

                    assertEquals("stm:" + query, colsList.length, colsFound.size());

                    for (int i = 0; i < colsList.length; i++) {
                        assertEquals("stm:" + query, colsList[i], colsFound.get(i));

                    }
                } catch (Exception e) {
                    throw new TestException("error at stm num: " + numSt, e);
                }
                numSt++;

            }
        }
    }

    private String getLine(BufferedReader in) throws Exception {
        String line = null;
        while (true) {
            line = in.readLine();
            if (line != null) {
                line.trim();
                if ((line.length() != 0) && ((line.length() < 2) || (line.length() >= 2) && !(line.charAt(0) == '/' && line.charAt(1) == '/'))) {
                    break;
                }
            } else {
                break;
            }

        }

        return line;
    }
}
