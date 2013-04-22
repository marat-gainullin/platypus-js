/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.dataflow;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetConverter;
import com.bearsoft.rowset.exceptions.FlowProviderNotPagedException;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import java.util.Properties;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class PagingTest extends FlowBaseTest {

    @Test
    public void rightChainTest() throws Exception {
        System.out.println("rightChainTest");
        Properties props = new Properties();
        String url = makeTestConnectionDescription(props);

        ObservingResourcesProvider resCounter = new ObservingResourcesProvider(url, props, 1, 1, 0);
        JdbcFlowProvider flow = new JdbcFlowProviderAdapter(null, resCounter, new RowsetConverter(), selectClause4Paging);

        flow.setPageSize(2);

        Rowset rs = new Rowset(flow);
        rs.refresh(new Parameters());
        assertTrue(rs.size() >= 1);
        assertTrue(rs.size() <= flow.getPageSize());
        Fields fields = rs.getFields();
        do {
            assertTrue(rs.size() >= 1);
            assertTrue(rs.size() <= flow.getPageSize());
            Fields fields1 = rs.getFields();
            assertSame(fields1, fields);
        } while (rs.nextPage());
        resCounter.testResources();
    }

    @Test
    public void rightShortestPageChainTest() throws Exception {
        System.out.println("rightShortestPageChainTest");
        Properties props = new Properties();
        String url = makeTestConnectionDescription(props);

        ObservingResourcesProvider resCounter = new ObservingResourcesProvider(url, props, 2, 2, 0);
        JdbcFlowProvider flow = new JdbcFlowProviderAdapter(null, resCounter, new RowsetConverter(), selectClause4Paging);

        flow.setPageSize(1);

        Rowset rs = new Rowset(flow);
        rs.refresh(new Parameters());
        assertTrue(rs.size() >= 1);
        assertTrue(rs.size() <= flow.getPageSize());
        Fields fields = rs.getFields();
        do {
            assertTrue(rs.size() >= 1);
            assertTrue(rs.size() <= flow.getPageSize());
            Fields fields1 = rs.getFields();
            assertSame(fields1, fields);
        } while (rs.nextPage());

        rs.refresh(new Parameters());
        assertTrue(rs.size() >= 1);
        assertTrue(rs.size() <= flow.getPageSize());
        fields = rs.getFields();
        while (rs.nextPage()) {
            assertTrue(rs.size() >= 1);
            assertTrue(rs.size() <= flow.getPageSize());
            Fields fields1 = rs.getFields();
            assertSame(fields1, fields);
        }

        resCounter.testResources();
    }

    @Test
    public void wrongChainRefreshTest() throws Exception {
        System.out.println("wrongChainRefreshTest");
        Properties props = new Properties();
        String url = makeTestConnectionDescription(props);

        ObservingResourcesProvider resCounter = new ObservingResourcesProvider(url, props, 3, 3, 0);
        JdbcFlowProvider flow = new JdbcFlowProviderAdapter(null, resCounter, new RowsetConverter(), selectClause4Paging);
// This test needs strictly 7 rows in the table
        flow.setPageSize(2);

        Rowset rs = new Rowset(flow);
        rs.refresh(new Parameters());
        assertTrue(rs.size() >= 1);
        assertTrue(rs.size() <= flow.getPageSize());// The paged rowset's view maximum size is the page size.
        Fields fields = rs.getFields();
        int pagesCount = 1;
        int recCount = rs.size();
        while (rs.nextPage()) {
            pagesCount++;
            recCount += rs.size();
        }
        assertTrue(pagesCount >= 3);// It's needed for good half pages number.
        rs.refresh();
        // Let's invoke refresh in the middle of paging proceess...
        rs.nextPage();// Second page fetched.
        rs.refresh();
        // No exception, because of refresh starts new process from the begining.
        int pagesCount1 = 1;
        int recCount1 = rs.size();
        while (rs.nextPage()) {
            pagesCount1++;
            recCount1 += rs.size();
        }
        assertEquals(pagesCount1, pagesCount);
        assertEquals(recCount1, recCount);

        assertTrue(rs.size() >= 1);
        assertTrue(rs.size() < flow.getPageSize()); // NB: Strictly less than 1!
        Fields fields1 = rs.getFields();
        assertSame(fields1, fields);
        resCounter.testResources();
    }

    @Test
    public void wrongChainNextPageTest() throws Exception {
        System.out.println("wrongChainNextPageTest");
        Properties props = new Properties();
        String url = makeTestConnectionDescription(props);

        ObservingResourcesProvider resCounter = new ObservingResourcesProvider(url, props, 1, 1, 0);
        JdbcFlowProvider flow = new JdbcFlowProviderAdapter(null, resCounter, new RowsetConverter(), selectClause4Paging);

        Rowset rs = new Rowset(flow);
        rs.refresh(new Parameters());
        assertTrue(rs.size() >= 1);
        Fields fields = rs.getFields();
        try {
            rs.nextPage();
            assertTrue(false);
        } catch (FlowProviderNotPagedException ex) {
            // it's ok. This exception must be here!
        }
        assertTrue(rs.size() >= 1);
        Fields fields1 = rs.getFields();
        assertSame(fields1, fields);
        resCounter.testResources();
    }
}
