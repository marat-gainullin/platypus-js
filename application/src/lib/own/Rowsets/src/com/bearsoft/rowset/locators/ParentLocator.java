/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.locators;

import com.bearsoft.rowset.utils.KeySet;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import java.util.Collections;
import java.util.Comparator;

/**
 * Locator with specific capability. It doesn't distinguish null and absent rows in underlying rowset.
 * So, rows with null or absent parents are treated as rows with null parents.
 * @author mg
 */
public class ParentLocator extends Locator {

    protected int parentColIndex = 0;
    protected Locator byPkLocator = null;
    protected TaggedList<RowWrap> parentless = new TaggedList<>();

    /**
     * <code>ParentLocator</code> constructor.
     * @param aRowset Underlying rowset.
     * @param aParentColIndex Column index that we are interested in.
     * @param aByPkLocator Locator used to discover that partcular row is absent.
     */
    public ParentLocator(Rowset aRowset, int aParentColIndex, Locator aByPkLocator) {
        super(aRowset);
        parentColIndex = aParentColIndex;
        byPkLocator = aByPkLocator;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void validate() throws RowsetException {
        assert !valid;
        assert byPkLocator != null;
        if (!byPkLocator.isValid()) {
            byPkLocator.validate();
        }
        super.validate();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void build() throws RowsetException {
        super.build();
        assert byPkLocator != null;
        assert byPkLocator.isValid();
        parentless.clear();
        for (int i = 0; i < rowset.size(); i++) {
            Object lParentId = rowset.getRow(i + 1).getColumnObject(parentColIndex);
            if (lParentId == null || !byPkLocator.find(lParentId)) {
                parentless.add(new RowWrap(rowset.getRow(i + 1), i + 1));
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void sort(Comparator<RowWrap> aRowWrapsComparator) {
        super.sort(aRowWrapsComparator);
        Collections.sort(parentless, aRowWrapsComparator);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean find(KeySet values) throws RowsetException {
        if (values != null && values.size() == 1 && values.get(0) == null) {
            if (rowset != null) {
                if (!valid) {
                    validate();
                }
                if (valid) {
                    subSet = parentless;
                    subSetPos = -1;
                    return (subSet != null && !subSet.isEmpty());
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        } else {
            return super.find(values);
        }
    }
}