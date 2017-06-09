package com.eas.grid.columns.header;

import java.util.ArrayList;
import java.util.List;

public class HeaderAnalyzer {

    protected int depth;

    protected HeaderAnalyzer() {
        super();
    }

    public static void analyze(List<HeaderNode> aForest) {
        HeaderAnalyzer analyzer = new HeaderAnalyzer();
        analyzer.maxDepth(aForest, 0);
        analyzer.mine(aForest, 0, null);
    }

    protected void maxDepth(List<HeaderNode> aForest, int aDepth) {
        aDepth++;
        if (depth < aDepth) {
            depth = aDepth;
        }
        for (int i = 0; i < aForest.size(); i++) {
            HeaderNode n = aForest.get(i);
            if (!n.getChildren().isEmpty()) {
                maxDepth(n.getChildren(), aDepth);
            }
        }
    }

    protected int mine(List<HeaderNode> aForest, int aDepth, HeaderNode aParent) {
        aDepth++;
        int leavesCount = 0;
        for (int i = 0; i < aForest.size(); i++) {
            HeaderNode n = aForest.get(i);
            if (!n.isLeaf()) {
                leavesCount += mine(n.getChildren(), aDepth, n);
            } else {
                n.depthRemainder = depth - aDepth;
                leavesCount += 1;
            }
        }
        if (aParent != null) {
            aParent.leavesCount = leavesCount;
        }
        return leavesCount;
    }

    private static void achieveLeaves(List<HeaderNode> aRoots, List<HeaderNode> aLeaves) {
        for (HeaderNode node : aRoots) {
            if (node.isLeaf()) {
                aLeaves.add(node);
            } else {
                achieveLeaves(node.getChildren(), aLeaves);
            }
        }
    }

    public static List<HeaderNode> toLeaves(List<HeaderNode> aRoots) {
        List<HeaderNode> leaves = new ArrayList<>();
        achieveLeaves(aRoots, leaves);
        return leaves;
    }

}
