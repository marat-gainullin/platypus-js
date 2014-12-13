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
    protected List<GridColumnsGroup> splittedLeaves = new ArrayList<>();
    protected int leaveIndex = -1;

    protected HeaderSplitter(int aMinLeave, int aMaxLeave) {
        super();
        minLeave = aMinLeave;
        maxLeave = aMaxLeave;
    }

    public static List<GridColumnsGroup> split(List<GridColumnsGroup> toBeSplitted, int aMinLeave, int aMaxLeave) {
        HeaderSplitter splitter = new HeaderSplitter(aMinLeave, aMaxLeave);
        splitter.process(toBeSplitted, null);
        return splitter.toRoots();
    }

    protected List<GridColumnsGroup> toRoots() {
        List<GridColumnsGroup> res = new ArrayList<>();
        Set<GridColumnsGroup> met = new HashSet<>();
        for (int i = 0; i < splittedLeaves.size(); i++) {
            GridColumnsGroup leaf = splittedLeaves.get(i);
            GridColumnsGroup parent = leaf;
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

    protected boolean process(List<GridColumnsGroup> toBeSplitted, GridColumnsGroup aClonedParent) {
        boolean res = false;
        for (int i = 0; i < toBeSplitted.size(); i++) {
            GridColumnsGroup n = toBeSplitted.get(i);
            GridColumnsGroup nc = new GridColumnsGroup(n.getTitle());
            nc.setStyle(n.getStyle());
            if (n.getChildren().isEmpty()) {
                leaveIndex++;
                if (leaveIndex >= minLeave && leaveIndex <= maxLeave) {
                    res = true;
                    splittedLeaves.add(nc);
                    if (aClonedParent != null) {
                        aClonedParent.addChild(nc);
                    }
                }
            } else {
                boolean isGoodLeaveIndex = process(n.getChildren(), nc);
                if (isGoodLeaveIndex) {
                    res = true;
                    if (aClonedParent != null) {
                        aClonedParent.addChild(nc);
                    }
                }
            }
        }
        return res;
    }
}
