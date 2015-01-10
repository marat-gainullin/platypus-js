package com.bearsoft.gui.grid.header;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeaderSplitter {

    // settings
    protected int minLeave;
    protected int maxLeave;
    // processing
    protected List<GridColumnsNode> splittedLeaves = new ArrayList<>();
    protected int leaveIndex = -1;

    protected HeaderSplitter(int aMinLeave, int aMaxLeave) {
        super();
        minLeave = aMinLeave;
        maxLeave = aMaxLeave;
    }

    public static List<GridColumnsNode> split(List<GridColumnsNode> toBeSplitted, int aMinLeave, int aMaxLeave) {
        HeaderSplitter splitter = new HeaderSplitter(aMinLeave, aMaxLeave);
        splitter.process(toBeSplitted, null);
        return splitter.toRoots();
    }

    protected List<GridColumnsNode> toRoots() {
        List<GridColumnsNode> res = new ArrayList<>();
        Set<GridColumnsNode> met = new HashSet<>();
        for (int i = 0; i < splittedLeaves.size(); i++) {
            GridColumnsNode leaf = splittedLeaves.get(i);
            GridColumnsNode parent = leaf;
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

    protected boolean process(List<GridColumnsNode> toBeSplitted, GridColumnsNode aClonedParent) {
        boolean res = false;
        for (int i = 0; i < toBeSplitted.size(); i++) {
            GridColumnsNode n = toBeSplitted.get(i);
            GridColumnsNode nc = n.lightCopy();
            nc.setTableColumn(n.getTableColumn());
            nc.setStyleSource(n);
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
