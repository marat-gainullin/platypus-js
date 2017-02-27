/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rows;

import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import java.util.List;
import javax.swing.ListSelectionModel;

/**
 *
 * @author mg
 * @param <T>
 * @param <M>
 */
public class TreedRowsSorter<T, M extends TableFront2TreedModel<T>> extends TabularRowsSorter<M> {

    public TreedRowsSorter(M aModel, ListSelectionModel aViewSelection) {
        super(aModel, aViewSelection);
    }

    @Override
    public int compareRows(int row1, int row2) {
        T el1 = model.getElementAt(row1);
        T el2 = model.getElementAt(row2);
        if (model.unwrap().getParentOf(el1) != model.unwrap().getParentOf(el2)) {
            List<T> path1 = model.buildPathTo(el1);
            List<T> path2 = model.buildPathTo(el2);
            if (path2.contains(el1)) {
                // el1 is parent of el2
                return -1;
            }
            if (path1.contains(el2)) {
                // el2 is parent of el1
                return 1;
            }
            for (int i = 0; i < Math.min(path1.size(), path2.size()); i++) {
                if (path1.get(i) != path2.get(i)) {
                    row1 = model.getIndexOf(path1.get(i));
                    row2 = model.getIndexOf(path2.get(i));
                    break;
                }
            }
        }
        return super.compareRows(row1, row2);
    }
}
