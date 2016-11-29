package com.eas.grid.columns.header;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HeaderSplitter<T> {

    // settings
    protected int minLeave;
    protected int maxLeave;
    // processing
    protected List<HeaderNode<T>> splittedLeaves = new ArrayList<>();
    protected int leaveIndex = -1;

    protected HeaderSplitter(int aMinLeave, int aMaxLeave) {
        super();
        minLeave = aMinLeave;
        maxLeave = aMaxLeave;
    }

    public static <T> List<HeaderNode<T>> split(List<HeaderNode<T>> toBeSplitted, int aMinLeave, int aMaxLeave) {
        HeaderSplitter<T> splitter = new HeaderSplitter<T>(aMinLeave, aMaxLeave);
        splitter.process(toBeSplitted, null);
        return splitter.toRoots();
    }

    protected List<HeaderNode<T>> toRoots() {
        List<HeaderNode<T>> res = new ArrayList<>();
        Set<HeaderNode<T>> met = new HashSet<>();
        for (int i = 0; i < splittedLeaves.size(); i++) {
        	HeaderNode<T> leaf = splittedLeaves.get(i);
        	HeaderNode<T> parent = leaf;
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

    protected boolean process(List<HeaderNode<T>> toBeSplitted, HeaderNode<T> aClonedParent) {
        boolean res = false;
        for (int i = 0; i < toBeSplitted.size(); i++) {
        	HeaderNode<T> n = toBeSplitted.get(i);
        	HeaderNode<T> nc = n.lightCopy();
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
