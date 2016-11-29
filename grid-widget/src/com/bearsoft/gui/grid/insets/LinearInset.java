/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.insets;

import com.bearsoft.gui.grid.events.insets.InsetAfterLastChangedEvent;
import com.bearsoft.gui.grid.events.insets.InsetChangeListener;
import com.bearsoft.gui.grid.events.insets.InsetPreFirstChangedEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Alternativly such fields are named as preFrist and afterLast
 * @author Gala
 */
public class LinearInset {

    public static final int EMPTY_CONTENT = 0;
    protected int preFirst = 0;
    protected int afterLast = 0;
    protected Set<InsetChangeListener> listeners = new HashSet<>();

    public LinearInset(int aPrefirst, int aAfterlast) {
        super();
        preFirst = aPrefirst;
        afterLast = aAfterlast;
    }

    /**
     * Converts an index from space with insets (outer space) to inner space.
     * @param aValue Index in space with insets (outer space) space.
     * @param aContentSize Size of content section of the outer space.
     * When resulting index falls in content part of the underlying space, <code>aContentSize</code> is ignored.
     * @return Index, coverted to underlying space without insets (inner space).
     */
    public InsetPart toInnerSpace(int aValue, int aContentSize) {
        int index = -1;
        InsetPart.PartKind kind = InsetPart.PartKind.CONTENT;
        if (aValue >= 0 && aValue < preFirst) {
            kind = InsetPart.PartKind.BEFORE;
            index = aValue;
        } else if (aValue >= preFirst + aContentSize && aValue < preFirst + aContentSize + afterLast) {
            kind = InsetPart.PartKind.AFTER;
            index = aValue - preFirst - aContentSize;
        } else {
            index = aValue - preFirst;
        }
        InsetPart res = new InsetPart(kind, index);
        return res;
    }

    /**
     * Converts an index from delegate's space to space with insets.
     * @param aPart InsetPart instance describing inner index and it's place in outer space.
     * @param aContentSize Size of center part of outer space.
     * If InsetPart constructed with InsetPart.PartKind.CONTENT,
     * than aContentSize parameter has no meaning. In such case it's ignored and may be
     * substituted with special value LinearInset.EMPTY_CONTENT.
     * @return Index in space with insets (outer space).
     * @see InsetPart
     * @see InsetPart#InsetPart(com.bearsoft.gui.grid.insets.InsetPart.PartKind, int)
     * @see LinearInset#EMPTY_CONTENT
     */
    public int toOuterSpace(InsetPart aPart, int aContentSize) {
        if (aPart.kind == InsetPart.PartKind.CONTENT) {
            return aPart.getValue() + preFirst;
        } else if (aPart.kind == InsetPart.PartKind.AFTER) {
            return aPart.getValue() + preFirst + aContentSize;
        }
        return aPart.getValue();
    }

    /**
     * Returns pre-first elements count
     * @return Pre-first elements count
     */
    public int getPreFirst() {
        return preFirst;
    }

    public void setPreFirst(int aValue) {
        int oldValue = preFirst;
        preFirst = aValue;
        firePreFirstChanged(oldValue, preFirst);
    }

    /**
     * Returns after last elements count
     * @return After last elements count
     */
    public int getAfterLast() {
        return afterLast;
    }

    public void setAfterLast(int aValue) {
        int oldValue = afterLast;
        afterLast = aValue;
        fireAfterLastChanged(oldValue, afterLast);
    }

    protected void firePreFirstChanged(int aOldValue, int aNewValue) {
        InsetPreFirstChangedEvent event = new InsetPreFirstChangedEvent(this, aOldValue, aNewValue);
        for (InsetChangeListener l : listeners) {
            l.insetPreFirstChanged(event);
        }
    }

    protected void fireAfterLastChanged(int aOldValue, int aNewValue) {
        InsetAfterLastChangedEvent event = new InsetAfterLastChangedEvent(this, aOldValue, aNewValue);
        for (InsetChangeListener l : listeners) {
            l.insetAfterLastChanged(event);
        }
    }

    public void addInsetChangeListener(InsetChangeListener aListener) {
        listeners.add(aListener);
    }

    public void removeInsetChangeListener(InsetChangeListener aListener) {
        listeners.remove(aListener);
    }

    @Override
    public String toString() {
        return String.format("inset %d];[%d", preFirst, afterLast);
    }
}
