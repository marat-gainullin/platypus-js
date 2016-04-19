/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class MultiLevelHeaderTest {

    private void prepareColumns(DefaultTableColumnModel columns, List roots, MultiLevelHeader header) {
        for (int i = 0; i < 5; i++) {
            TableColumn col = new TableColumn(i);
            col.setHeaderValue(String.valueOf(i) + " col ");
            col.setMinWidth(20);
            col.setWidth(120);
            columns.addColumn(col);
        }

        header.setColumnModel(columns);
        GridColumnsNode g11;
        GridColumnsNode g12;
        GridColumnsNode g21;
        GridColumnsNode g22;
        GridColumnsNode g23;
        GridColumnsNode g24;
        GridColumnsNode g33;
        GridColumnsNode g34;

        g11 = new GridColumnsNode("g11 hhhhhhhhhh test test");
        {
            g21 = new GridColumnsNode(columns.getColumn(0)); // col 0
            g22 = new GridColumnsNode(columns.getColumn(1)); // col 1
            g11.addColumnNode(g21);
            g11.addColumnNode(g22);
        }

        g12 = new GridColumnsNode("g12 hhhhhhhhhh test test");
        {
            g23 = new GridColumnsNode("g23 hhhhhhhhhh test test");
            g24 = new GridColumnsNode(columns.getColumn(4)); // col 4
            g12.addColumnNode(g23);
            g12.addColumnNode(g24);
            {
                g33 = new GridColumnsNode(columns.getColumn(2)); // col 2
                g34 = new GridColumnsNode(columns.getColumn(3)); // col 3
                g23.addColumnNode(g33);
                g23.addColumnNode(g34);
            }
        }
        roots.add(g11);
        roots.add(g12);

        header.setRoots(roots);
    }

    @Test
    public void tree2GridCalculationsTest() {
        DefaultTableColumnModel columns = new DefaultTableColumnModel();
        List<GridColumnsNode> roots = new ArrayList<>();
        MultiLevelHeader header = new MultiLevelHeader();
        prepareColumns(columns, roots, header);

        header.regenerate();

        GridColumnsNode g11 = header.roots.get(0);
        GridColumnsNode g12 = header.roots.get(1);

        GridColumnsNode g21 = g11.getChildren().get(0);
        GridColumnsNode g22 = g11.getChildren().get(1);
        GridColumnsNode g23 = g12.getChildren().get(0);
        GridColumnsNode g24 = g12.getChildren().get(1);

        GridColumnsNode g33 = g23.getChildren().get(0);
        GridColumnsNode g34 = g23.getChildren().get(1);

        GridBagConstraints g11Constraints = header.group2Constraints.get(g11);
        GridBagConstraints g12Constraints = header.group2Constraints.get(g12);

        GridBagConstraints g21Constraints = header.group2Constraints.get(g21);
        GridBagConstraints g22Constraints = header.group2Constraints.get(g22);
        GridBagConstraints g23Constraints = header.group2Constraints.get(g23);
        GridBagConstraints g24Constraints = header.group2Constraints.get(g24);

        GridBagConstraints g33Constraints = header.group2Constraints.get(g33);
        GridBagConstraints g34Constraints = header.group2Constraints.get(g34);

        assertEquals(0, g11Constraints.gridx);
        assertEquals(0, g11Constraints.gridy);
        assertEquals(2, g11Constraints.gridwidth);
        assertEquals(1, g11Constraints.gridheight);

        assertEquals(2, g12Constraints.gridx);
        assertEquals(0, g12Constraints.gridy);
        assertEquals(3, g12Constraints.gridwidth);
        assertEquals(1, g12Constraints.gridheight);

        assertEquals(0, g21Constraints.gridx);
        assertEquals(1, g21Constraints.gridy);
        assertEquals(1, g21Constraints.gridwidth);
        assertEquals(2, g21Constraints.gridheight);

        assertEquals(1, g22Constraints.gridx);
        assertEquals(1, g22Constraints.gridy);
        assertEquals(1, g22Constraints.gridwidth);
        assertEquals(2, g22Constraints.gridheight);

        assertEquals(2, g23Constraints.gridx);
        assertEquals(1, g23Constraints.gridy);
        assertEquals(2, g23Constraints.gridwidth);
        assertEquals(1, g23Constraints.gridheight);

        assertEquals(4, g24Constraints.gridx);
        assertEquals(1, g24Constraints.gridy);
        assertEquals(1, g24Constraints.gridwidth);
        assertEquals(2, g24Constraints.gridheight);

        assertEquals(2, g33Constraints.gridx);
        assertEquals(2, g33Constraints.gridy);
        assertEquals(1, g33Constraints.gridwidth);
        assertEquals(1, g33Constraints.gridheight);

        assertEquals(3, g34Constraints.gridx);
        assertEquals(2, g34Constraints.gridy);
        assertEquals(1, g34Constraints.gridwidth);
        assertEquals(1, g34Constraints.gridheight);
    }
}
