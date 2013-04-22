/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.wrappers.collections;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rows descendants list implementation. The row/row descendants are used list elements.
 * @author Gala
 */
public class RowsList<T extends Row> extends RowsCollection implements List<T> {

    public RowsList(Rowset aRowset) {
        super(aRowset);
    }

    public boolean contains(Object oValue) {
        if (oValue instanceof Row) {
            try {
                Row value = (Row) oValue;
                Object[] key = getKey(value);
                return locator.find(key);
            } catch (Exception ex) {
                Logger.getLogger(RowsMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public Iterator<T> iterator() {
        return listIterator();
    }

    public Object[] toArray() {
        return rowset.getCurrent().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return rowset.getCurrent().toArray(a);
    }

    public boolean add(Row element) {
        try {
            assert element.getColumnCount() == rowset.getFields().getFieldsCount() : "Rows been added must consist same number of fields as a whole collection";
            rowset.insert(element, false);
            return true;
        } catch (RowsetException ex) {
            Logger.getLogger(RowsList.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean add(Row element, boolean aAjusting) {
        try {
            assert element.getColumnCount() == rowset.getFields().getFieldsCount() : "Rows been added must consist same number of fields as a whole collection";
            rowset.insert(element, aAjusting);
            return true;
        } catch (RowsetException ex) {
            Logger.getLogger(RowsList.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void add(int index, Row element) {
        try {
            if (rowset.absolute(index)) {
                add(element);
            }
        } catch (InvalidCursorPositionException ex) {
            Logger.getLogger(RowsList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean remove(Object o) {
        if (o instanceof Row) {
            Row row = (Row) o;
            try {
                Object[] key = getKey(row);
                if (locator.find(key) && locator.first()) {
                    int oldSize = size();
                    rowset.delete();
                    return oldSize > size();
                } else {
                    return false;
                }
            } catch (Exception ex) {
                Logger.getLogger(RowsMap.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean containsAll(Collection<?> c) {
        for (Object oRow : c) {
            if (!(oRow instanceof Row) || !contains((T) oRow)) {
                return false;
            }
        }
        return true;
    }

    public boolean addAll(Collection<? extends T> c) {
        for (Object oRow : c) {
            if (!(oRow instanceof Row)) {
                return false;
            }
        }
        int toAdd = c.size();
        int offered = 0;
        int added = 0;
        for (Object oRow : c) {
            offered++;
            if (add((Row) oRow, offered != toAdd)) {
                added++;
            }
        }
        return added == offered;
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        try {
            if (rowset.absolute(index)) {
                return addAll(c);
            }
        } catch (InvalidCursorPositionException ex) {
            Logger.getLogger(RowsList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean removeAll(Collection<?> c) {
        try {
            Set<Row> toDelete = new HashSet<>();
            for (Object row : c) {
                if (row instanceof Row) {
                    toDelete.add((Row) row);
                }
            }
            rowset.delete(toDelete);
            return false;// true if all elements of collection c are removed.
        } catch (RowsetException ex) {
            Logger.getLogger(RowsList.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean retainAll(Collection<?> c) {
        try {
            Set<Row> toDelete = new HashSet<>();
            for (Object row : rowset.getCurrent()) {
                if (row instanceof Row && !c.contains(row)) {
                    toDelete.add((Row) row);
                }
            }
            rowset.delete(toDelete);
            return false;// true if all elements not contained in collection c are removed.
        } catch (RowsetException ex) {
            Logger.getLogger(RowsList.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public T get(int index) {
        Row row = rowset.getCurrent().get(index);
        return (T) row;
    }

    public Row set(int index, Row element) {
        try {
            if (rowset.absolute(index)) {
                if (rowset.getCurrent().get(index) != element) {
                    // TODO refactor updateObject code, to achieve such
                    // situation: row is setted and all work, related to
                    // updating different column objects of the rowset
                }
                return element;
            }
        } catch (InvalidCursorPositionException ex) {
            Logger.getLogger(RowsList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public T remove(int index) {
        try {
            if (rowset.absolute(index)) {
                Row row = rowset.getCurrentRow();
                rowset.delete();
                return (T) row;
            }
        } catch (Exception ex) {
            Logger.getLogger(RowsList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int indexOf(Object o) {
        return rowset.getCurrent().indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return rowset.getCurrent().lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return null;// TODO: normal rowset iterator
    }

    public ListIterator<T> listIterator(int index) {
        return null;// TODO: normal rowset iterator
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return null;// TODO: new rowset and list above it
    }
}
