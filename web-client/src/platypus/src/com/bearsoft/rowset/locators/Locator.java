/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.locators;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.ordering.HashOrderer;
import com.bearsoft.rowset.utils.KeySet;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Locator is filter subclass, intended to locate particular rows. It doesn't filter rowset, but rows partition is also used.
 * @author mg
 */
public class Locator extends HashOrderer {

    public static final String LOCATOR_IS_INVALID = "locator is invalid! Rowset was edited but locator havn\'t been rebuild.";
    public static final String INDEX_IS_INVALID = "index is out of bounds";
    public static final String WRONG_POSITION_MARKER = "invalid position in locator's subset";
    protected List<RowWrap> subSet;
    protected int subSetPos = -1;

    /**
     * Locator's constructor.
     * @param aRowset Rowset this locator is build for.
     */
    public Locator(Rowset aRowset) {
        super(aRowset);
    }

	/**
     * Returns <code>Row</code> object from rowset on witch this locator was created
     * @param aIndex Index of row in the rows subset vector. Index is 0-based
     * @return A <code>Row</code> object.
     */
    public Row getRow(int aIndex) throws IllegalStateException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    if (aIndex >= 0 && aIndex < subSet.size()) {
                        return subSet.get(aIndex).getRow();
                    } else {
                        throw new IllegalStateException(INDEX_IS_INVALID);
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return null;
    }

    /**
     * Returns index of row in the row's subset, defined by <code>HashOrderer</code>.
     * @param aRow Row which index is returned.
     * @return Index of row in the row's subset, defined by <code>HashOrderer</code>.
     * @throws IllegalStateException
     */
    public int indexOf(Row aRow) throws IllegalStateException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    for (int i = 0; i < subSet.size(); i++) {
                        RowWrap rw = subSet.get(i);
                        if (rw != null && rw.getRow() == aRow) {
                            return i;
                        }
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return -1;
    }

    /**
     * This function changes row position within same locator's subset of rows
     * @param aRow A Row, that position is to be changed.
     * @param aIndex Index, the row would be at.
     * @return A <code>Row</cdoe> object.
     * @throws ArrayIndexOutOfBoundsException
     */
    public Row changeRowPosition(Row aRow, int aIndex) throws ArrayIndexOutOfBoundsException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    int lIndex = -1;
                    RowWrap rw = null;
                    for (int i = 0; i < subSet.size(); i++) {
                        if (subSet.get(i).getRow() == aRow) {
                            lIndex = i;
                            rw = subSet.get(i);
                            break;
                        }
                    }
                    if (lIndex != -1) {
                        if (aIndex != lIndex) {
                            subSet.remove(lIndex);
                            subSet.add(aIndex, rw);
                        }
                        return rw.getRow();
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return null;
    }

    /**
     * Finds subset of rows, corresponding to values, passed to the method.
     * Performs converting of passed values from thier's types to rowset's corresponding fields types.
     * @param values Keys vector to find
     * @return Whether subset of rows is found.
     * @throws RowsetException
     */
    public boolean find(Object... values) throws RowsetException {
        if (values != null && values.length > 0) {
            KeySet ks = new KeySet();
            assert fieldsIndicies.size() == values.length;
            for (int i = 0; i < fieldsIndicies.size(); i++) {
                Object value = Converter.convert2RowsetCompatible(values[i], rowset.getFields().get(fieldsIndicies.get(i)).getTypeInfo());
                ks.add(value);
            }
            return find(ks);
        }
        return false;
    }

    /**
     * Find subset of rows, corresponding to <code>KeySet</code>, passed to the method.
     * Any converting is not performed. Types of values passed in <code>values</code> parameter must be equal with types of corresponding fields.
     * @param values Keys vector to find.
     * @return Whether subset of rows is found.
     * @throws RowsetException
     */
    public boolean find(KeySet values) throws RowsetException {
        if (rowset != null) {
            if (!valid) {
                validate();
            }
            if (valid) {
                subSet = ordered.get(values);
                subSetPos = -1;
                return (subSet != null && !subSet.isEmpty());
            } else {
                throw new IllegalStateException(LOCATOR_IS_INVALID);
            }
        } else {
            throw new IllegalStateException(ROWSET_MISSING);
        }
    }

    /**
     * Returns size of current subset. Current subset is defined when <code>find()</code> method is called.
     * @return Size of current subset, corresponding to <code>KeySet</code>, has been passed to <code>find()</code> method
     * @throws IllegalStateException
     */
    public int getSize() throws IllegalStateException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    return subSet.size();
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        } else {
            return 0;
        }
    }

    /**
     * Returns row position in rowset. Row is the subset's element at <code>aSubsetPos</code>.
     * @param aSubsetPos Subset's element position. It's 1-based.
     * @return Row position in rowset.
     * @throws IllegalStateException
     */
    public int getRowsetPos(int aSubsetPos) throws IllegalStateException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    if (aSubsetPos >= 1 && aSubsetPos <= subSet.size()) {
                        RowWrap rw = subSet.get(aSubsetPos - 1);
                        if (rw != null) {
                            return rw.getIndex();
                        }
                    } else {
                        throw new IllegalStateException(WRONG_POSITION_MARKER);
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return -1;
    }

    /**
     * Prepares the locator for iterating through underlying rowset.
     * It acts like rowset's method <code>beforeFirst()</code>
     */
    public void beforeFirst() {
        subSetPos = -1;
    }

    /**
     * Prepares the locator for iterating through underlying rowset.
     * It acts like rowset's method <code>afterLast()</code>
     */
    public void afterLast() {
        if (subSet != null) {
            subSetPos = subSet.size();
        }
    }

    /**
     * Positions the rowset on the row, corresponding to the first subset's position.
     * @return Whether rowset have been repositioned.
     * @throws IllegalStateException
     */
    public boolean first() throws IllegalStateException, InvalidCursorPositionException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    subSetPos = 0;
                    RowWrap rw = subSet.get(subSetPos);
                    if (rw != null) {
                        boolean res = rowset.absolute(rw.getIndex());
                        assert !res || rowset.getCurrentRow() == rw.getRow();
                        return res;
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return false;
    }

    /**
     * Positions the rowset on the row, corresponding to the next subset's position.
     * @return Whether rowset have been repositioned.
     * @throws IllegalStateException
     */
    public boolean next() throws IllegalStateException, InvalidCursorPositionException {
        if (subSet != null && !subSet.isEmpty() && !isAfterLast()) {
            if (rowset != null) {
                if (valid) {
                    subSetPos++;
                    if (subSetPos >= 0 && subSetPos < subSet.size()) {
                        RowWrap rw = subSet.get(subSetPos);
                        if (rw != null) {
                            boolean res = rowset.absolute(rw.getIndex());
                            assert !res || rowset.getCurrentRow() == rw.getRow();
                            return res;
                        }
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return false;
    }

    /**
     * Positions the rowset on the row, corresponding to the first subset's position.
     * @param index Subset's position. index is 0-based.
     * @return Whether rowset have been repositioned.
     * @throws IllegalStateException
     */
    public boolean absolute(int index) throws IllegalStateException, InvalidCursorPositionException {
        if (subSet != null && !subSet.isEmpty() && !isAfterLast()) {
            if (rowset != null) {
                if (valid) {
                    if (index >= 0 && index < subSet.size()) {
                        subSetPos = index;
                        RowWrap rw = subSet.get(subSetPos);
                        if (rw != null) {
                            boolean res = rowset.absolute(rw.getIndex());
                            assert !res || rowset.getCurrentRow() == rw.getRow();
                            return res;
                        }
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return false;
    }

    /**
     * Positions the rowset on the row, corresponding to the previous subset's position.
     * @return Whether rowset have been repositioned.
     * @throws IllegalStateException
     */
    public boolean previous() throws IllegalStateException, InvalidCursorPositionException {
        if (subSet != null && !subSet.isEmpty() && !isBeforeFirst()) {
            if (rowset != null) {
                if (valid) {
                    subSetPos--;
                    if (subSetPos >= 0 && subSetPos < subSet.size()) {
                        RowWrap rw = subSet.get(subSetPos);
                        if (rw != null) {
                            boolean res = rowset.absolute(rw.getIndex());
                            assert !res || rowset.getCurrentRow() == rw.getRow();
                            return res;
                        }
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return false;
    }

    /**
     * Positions the rowset on the row, corresponding to the last subset's position.
     * @return Whether rowset have been repositioned.
     * @throws IllegalStateException
     */
    public boolean last() throws IllegalStateException, InvalidCursorPositionException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    subSetPos = subSet.size() - 1;
                    RowWrap rw = subSet.get(subSetPos);
                    if (rw != null) {
                        boolean res = rowset.absolute(rw.getIndex());
                        assert !res || rowset.getCurrentRow() == rw.getRow();
                        return res;
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return false;
    }

    /**
     * Returns whether subset's position is before first.
     * @return Whether subset's position is before first.
     */
    public boolean isBeforeFirst() {
        return rowset == null || subSet == null || subSet.isEmpty() || subSetPos < 0;
    }

    /**
     * Returns whether subset's position is after last.
     * @return Whether subset's position is after last.
     */
    public boolean isAfterLast() {
        return rowset == null || subSet == null || subSet.isEmpty() || subSetPos >= subSet.size();
    }

    /**
     * Changes row index in the ordered structure.
     * @param aRow Row to change index of.
     * @param newIndex Value row index to be changed to.
     * @throws RowsetException
     */
    public void setRowOriginalIndex(Row aRow, int newIndex) throws RowsetException {
        KeySet ks = makeKeySet(aRow, fieldsIndicies);
        if (ks != null) {
            // delete from structure
            List<RowWrap> subset = ordered.get(ks);
            if (subset != null) {
                int rIndex = subset.indexOf(aRow);
                if (rIndex >= 0 && rIndex < subset.size()) {
                    RowWrap rw = subset.get(rIndex);
                    if (rw != null) {
                        rw.setIndex(newIndex);
                    }
                }
            }
        }
    }

    /**
     * Adds row to the ordered structure.
     * @param aRow Row to add.
     * @param aIndex Index to be assigned to <code>aRow</code> in ordered structure.
     * @throws RowsetException
     */
    public void add(Row aRow, int aIndex) throws RowsetException {
        KeySet ks = makeKeySet(aRow, fieldsIndicies);
        if (ks != null) {
            List<RowWrap> subset = ordered.get(ks);
            // add to structure
            if (subset == null) {
                subset = new ArrayList();
                ordered.put(ks, subset);
            }
            subset.add(new RowWrap(aRow, aIndex));
        }
    }

    /**
     * Builds ordered structure of this locator.
     * @throws RowsetException
     */
    @Override
    public void build() throws RowsetException {
        if (!constrainting) {
            if (rowset != null) {
                List<Row> rows = getRowsetRows();
                if (rows != null) {
                    ordered.clear();
                    for (int i = 0; i < rows.size(); i++) {
                        add(rows.get(i), i + 1);
                    }
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        } else {
            throw new IllegalStateException("Can't build locator while constrainting");
        }
    }

    /**
     * Sorts rows into partitioned subsets.
     * @param aRowWrapsComparator Comparator used while sorting.
     */
    public void sort(Comparator<RowWrap> aRowWrapsComparator) {
        if (ordered != null) {
            for (List<RowWrap> subSet2Sort : ordered.values()) {
                if (subSet2Sort != null) {
                    Collections.sort(subSet2Sort, aRowWrapsComparator);
                }
            }
        }
    }

    /**
     * Returns vector of rows gathered from all subsets in ordered structure.
     * @return Vector of rows gathered from all subsets in ordered structure.
     */
    public List<Row> getAllRowsVector() {
        List<Row> rows = new ArrayList();
        for (List<RowWrap> lsubSet : ordered.values()) {
            assert lsubSet != null;
            for (RowWrap rw : lsubSet) {
                assert rw != null;
                rows.add(rw.getRow());
            }
        }
        return rows;
    }

    public List<RowWrap> getSubSet() {
        return subSet;
    }

    /**
     * Marks this locator as invalid.
     */
    @Override
    public void invalidate() {
        super.invalidate();
        ordered.clear();
        subSet = null;
    }

    /**
     * Un-initializes the locator, helping GC do it's work.
     */
    @Override
    public void die() {
        super.die();
        subSet = null;
        valid = false;
    }

	protected JavaScriptObject jsPublished;

	public void setPublished(JavaScriptObject aPublished) {
		jsPublished = aPublished;
	}

	public JavaScriptObject getPublished() {
		return jsPublished;
	}
	
	public boolean find(JavaScriptObject aValues) throws Exception {
		JsArrayMixed fieldsValues = aValues.<JsArrayMixed> cast();
		if (getFields().size() == fieldsValues.length()) {
			Fields fields = getRowset().getFields();
			Object[] values = new Object[fieldsValues.length()];
			for (int i = 0; i < values.length; i++) {
				values[i] = RowsetUtils
						.extractValueFromJsArray(fieldsValues, i);
				values[i] = Converter.convert2RowsetCompatible(values[i],
						fields.get(getFields().get(i)).getTypeInfo());
			}
			return find(values);
		} else
			throw new IllegalArgumentException(
					"Searching keys must correspond to searching fields were specified while locator creation (may be createLocator call)");
	}
}
