package com.eas.grid.columns.header;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeaderSplitter {

    // settings
    protected int minLeave;
    protected int maxLeave;
    // processing
    protected List<HeaderNode> splittedLeaves = new ArrayList<>();
    protected int leaveIndex = -1;

    protected HeaderSplitter(int aMinLeave, int aMaxLeave) {
        super();
        minLeave = aMinLeave;
        maxLeave = aMaxLeave;
    }

    /**
     * 
     * @param toBeSplitted
     * @param aMinLeave Minimum leaf index, inclusive.
     * @param aMaxLeave Maximum leaf index, exclusive.
     * @return 
     */
    public static List<HeaderNode> split(List<HeaderNode> toBeSplitted, int aMinLeave, int aMaxLeave) {
        HeaderSplitter splitter = new HeaderSplitter(aMinLeave, aMaxLeave);
        splitter.process(toBeSplitted, null);
        return splitter.toRoots();
    }

    protected List<HeaderNode> toRoots() {
        List<HeaderNode> res = new ArrayList<>();
        Set<HeaderNode> met = new Set();
        for (int i = 0; i < splittedLeaves.size(); i++) {
            HeaderNode leaf = splittedLeaves.get(i);
            HeaderNode parent = leaf;
            while (parent.getParent() != null) {
                parent = parent.getParent();
            }
            if (!met.contains(parent)) {
                met.add(parent);
                res.add(parent);
            }
        }
        return res;
    }

    protected boolean process(List<HeaderNode> toBeSplitted, HeaderNode aClonedParent) {
        boolean res = false;
        for (int i = 0; i < toBeSplitted.size(); i++) {
            HeaderNode n = toBeSplitted.get(i);
            HeaderNode nc = n.lightCopy();
            if (n.getChildren().isEmpty()) {
                leaveIndex++;
                if (leaveIndex >= minLeave && leaveIndex <= maxLeave) {
                    res = true;
                    splittedLeaves.add(nc);
                    if (aClonedParent != null) {
                        aClonedParent.addColumnNode(nc);
                    }
                }
            } else {
                boolean isGoodLeaveIndex = process(n.getChildren(), nc);
                if (isGoodLeaveIndex) {
                    res = true;
                    if (aClonedParent != null) {
                        aClonedParent.addColumnNode(nc);
                    }
                }
            }
        }
        return res;
    }
}
