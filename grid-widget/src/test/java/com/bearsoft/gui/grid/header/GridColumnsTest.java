/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header;

import com.eas.gui.FontStyle;
import java.awt.Color;
import java.awt.Font;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class GridColumnsTest {

    @Test
    public void equalsAssignTest() throws Exception {
        GridColumnsNode col = new GridColumnsNode();
        GridColumnsNode col1 = new GridColumnsNode();
        GridColumnsNode childCol = new GridColumnsNode();

        assertTrue(col.isEqual(col1));

        col.setBackground(Color.darkGray);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setForeground(Color.darkGray);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setReadonly(!col.isReadonly());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setEnabled(!col.isEnabled());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setReadonly(!col.isReadonly());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setSelectOnly(!col.isSelectOnly());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setTitle("some title");
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setVisible(!col.isVisible());
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setMinWidth(345);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setMaxWidth(345);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setBackground(Color.darkGray);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setForeground(Color.cyan);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.setFont(new Font("Arial", FontStyle.ITALIC, 7));
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));

        col.addColumnNode(childCol);
        assertFalse(col.isEqual(col1));
        col.assign(col1);
        assertTrue(col.isEqual(col1));
    }
}
