/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.tree;

import com.bearsoft.gui.grid.data.TreedModel;
import com.bearsoft.gui.grid.events.data.ElementsDataChangedEvent;
import com.bearsoft.gui.grid.events.data.ElementsAddedEvent;
import com.bearsoft.gui.grid.events.data.ElementsRemovedEvent;
import com.bearsoft.gui.grid.events.data.TreedModelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 *
 * @author Gala
 */
public class FacadeBaseTest {

    protected class TreeItem {

        public TreeItem() {
            super();
        }

        public TreeItem(String aData1, String aData2, String aData3) {
            this();
            data1 = aData1;
            data2 = aData2;
            data3 = aData3;
        }
        protected String data1;
        protected String data2;
        protected String data3;
        protected TreeItem parent;
        protected List<TreeItem> children = new ArrayList<>();
    }
    protected List<TreeItem> tree = new ArrayList<>();
    protected int firstLevelCount = 200;
    protected int secondLevelCount = 30;
    protected int thirdLevelCount = 6;

    protected class TestTreedModel implements TreedModel<TreeItem> {

        protected Set<TreedModelListener<TreeItem>> listeners = new HashSet<>();

        public TreeItem getParentOf(TreeItem anElement) {
            return anElement.parent;
        }

        public List<TreeItem> getChildrenOf(TreeItem anElement) {
            if (anElement == null) {
                return tree;
            } else {
                return anElement.children;
            }
        }

        public boolean isLeaf(TreeItem anElement) {
            return anElement.children.isEmpty();
        }

        public int getColumnCount() {
            return 3;
        }

        public Class getColumnClass(int aColIndex) {
            return Object.class;
        }

        public String getColumnName(int aColIndex) {
            return String.format("Sample data column %d", aColIndex);
        }

        public Object getValue(TreeItem anElement, int aColIndex) {
            switch (aColIndex) {
                case 0:
                    return anElement.data1;
                case 1:
                    return anElement.data2;
                case 2:
                    return anElement.data3;
            }
            return null;
        }

        public void setValue(TreeItem anElement, int aColIndex, Object aValue) {
            switch (aColIndex) {
                case 0:
                    anElement.data1 = (String) aValue;
                    break;
                case 1:
                    anElement.data2 = (String) aValue;
                    break;
                case 2:
                    anElement.data3 = (String) aValue;
                    break;

            }
            fireCellsChanged(anElement, aColIndex, false);
        }

        public void remove(TreeItem anElement) {
            if (anElement.parent != null) {
                anElement.parent.children.remove(anElement);
            } else {
                tree.remove(anElement);
            }
            ArrayList list = new ArrayList();
            list.add(anElement);
            fireElementsRemoved(list, false);
        }

        public void add(TreeItem aParent, TreeItem anElement, int aIndex) {
            if (aParent != null) {
                anElement.parent = aParent;
                aParent.children.add(aIndex, anElement);
            } else {
                tree.add(aIndex, anElement);
            }
            ArrayList list = new ArrayList();
            list.add(anElement);
            fireElementsAdded(list, false);
        }

        public void add(TreeItem aParent, TreeItem anElement1, int aIndex1, TreeItem anElement2, int aIndex2, TreeItem anElement3, int aIndex3) {
            if (aParent != null) {
                anElement1.parent = aParent;
                aParent.children.add(aIndex1, anElement1);
                aParent.children.add(aIndex2, anElement2);
                aParent.children.add(aIndex3, anElement3);
            } else {
                tree.add(aIndex1, anElement1);
                tree.add(aIndex2, anElement2);
                tree.add(aIndex3, anElement3);
            }
            ArrayList list = new ArrayList();
            list.add(anElement1);
            list.add(anElement2);
            list.add(anElement3);
            fireElementsAdded(list, false);
        }

        public void fireCellsChanged(TreeItem anElement, int aColIndex, boolean aAjusting) {
            ElementsDataChangedEvent event = new ElementsDataChangedEvent(Collections.singletonList(anElement), aColIndex, aAjusting);
            for (TreedModelListener<TreeItem> l : listeners) {
                l.elementsDataChanged(event);
            }
        }

        public void fireElementsAdded(List<TreeItem> anElements, boolean aAjusting) {
            ElementsAddedEvent event = new ElementsAddedEvent(anElements, aAjusting);
            for (TreedModelListener<TreeItem> l : listeners) {
                l.elementsAdded(event);
            }
        }

        public void fireElementsRemoved(List<TreeItem> anElements, boolean aAjusting) {
            ElementsRemovedEvent event = new ElementsRemovedEvent(anElements, aAjusting);
            for (TreedModelListener<TreeItem> l : listeners) {
                l.elementsRemoved(event);
            }
        }

        public void addTreedModelListener(TreedModelListener<TreeItem> aListener) {
            listeners.add(aListener);
        }

        public void removeTreedModelListener(TreedModelListener<TreeItem> aListener) {
            listeners.remove(aListener);
        }
    }

    protected void initTree() {
        tree.clear();
        for (int i = 0; i < firstLevelCount; i++) {
            TreeItem item = new TreeItem();
            item.data1 = "item" + String.valueOf(i + 1);
            item.data2 = "col1; " + item.data1;
            item.data3 = "col2; " + item.data1;
            tree.add(item);
            for (int j = 0; j < secondLevelCount; j++) {
                TreeItem child = new TreeItem();
                child.data1 = "    child" + String.valueOf(j + 1);
                child.data2 = "col1; " + item.data1;
                child.data3 = "col2; " + item.data1;
                child.parent = item;
                item.children.add(child);
                for (int k = 0; k < thirdLevelCount; k++) {
                    TreeItem child1 = new TreeItem();
                    child1.data1 = "        grandchild" + String.valueOf(k + 1);
                    child1.data2 = "col1; " + item.data1;
                    child1.data3 = "col2; " + item.data1;
                    child.children.add(child1);
                    child1.parent = child;
                }
            }
        }
    }

    @Test
    public void dummyTest() {
    }
}
