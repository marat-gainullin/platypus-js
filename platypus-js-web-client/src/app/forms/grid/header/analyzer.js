define([], function () {
    function HeaderAnalyzer() {
        var depth = 0;
        function maxDepth(aForest, aDepth) {
            aDepth++;
            if (depth < aDepth) {
                depth = aDepth;
            }
            for (var i = 0; i < aForest.length; i++) {
                var n = aForest[i];
                n.depthRemainder = 0;
                n.leavesCount = 0;
                if (n.children.length > 0) {
                    maxDepth(n.children, aDepth);
                }
            }
        }
        Object.defineProperty(HeaderAnalyzer, 'maxDepth', {
            get: function () {
                return maxDepth;
            }
        });

        function mine(aForest, aDepth, aParent) {
            aDepth++;
            var leavesCount = 0;
            for (var i = 0; i < aForest.length; i++) {
                var n = aForest[i];
                if (!n.leaf) {
                    leavesCount += mine(n.children, aDepth, n);
                } else {
                    n.depthRemainder = depth - aDepth;
                    leavesCount += 1;
                }
            }
            if (aParent) {
                aParent.leavesCount = leavesCount;
            }
            return leavesCount;
        }
        Object.defineProperty(HeaderAnalyzer, 'mine', {
            get: function () {
                return mine;
            }
        });
    }

    function analyze(aForest) {
        var analyzer = new HeaderAnalyzer();
        analyzer.maxDepth(aForest, 0);
        analyzer.mine(aForest, 0, null);
    }
    Object.defineProperty(HeaderAnalyzer, 'analyze', {
        get: function () {
            return analyze;
        }
    });
    function achieveLeaves(aRoots, aLeaves) {
        aRoots.forEach(function (node) {
            if (node.leaf) {
                aLeaves.push(node);
            } else {
                achieveLeaves(node.children, aLeaves);
            }
        });
    }
    Object.defineProperty(HeaderAnalyzer, 'achieveLeaves', {
        get: function () {
            return achieveLeaves;
        }
    });

    function toLeaves(aRoots) {
        var leaves = [];
        achieveLeaves(aRoots, leaves);
        return leaves;
    }
    Object.defineProperty(HeaderAnalyzer, 'toLeaves', {
        get: function () {
            return toLeaves;
        }
    });
    return HeaderAnalyzer;
});