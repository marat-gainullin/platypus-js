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
        Object.defineProperty(this, 'maxDepth', {
            get: function () {
                return maxDepth;
            }
        });
        Object.defineProperty(this, 'depth', {
            get: function () {
                return depth;
            }
        });

        function mineDepth(aForest, aDepth) {
            aDepth++;
            for (var i = 0; i < aForest.length; i++) {
                var n = aForest[i];
                if (!n.leaf) {
                    mineDepth(n.children, aDepth);
                } else {
                    n.depthRemainder = depth - aDepth;
                }
            }
        }
        Object.defineProperty(this, 'mineDepth', {
            get: function () {
                return mineDepth;
            }
        });
        function mineLeaves(aLevel, aParent) {
            var leavesCount = 0;
            for (var i = 0; i < aLevel.length; i++) {
                var n = aLevel[i];
                if (n.visible) {
                    if (!n.leaf) {
                        leavesCount += mineLeaves(n.children, n);
                    } else {
                        leavesCount += 1;
                    }
                }
            }
            if (aParent) {
                aParent.leavesCount = leavesCount;
            }
            return leavesCount;
        }
        Object.defineProperty(this, 'mineLeaves', {
            get: function () {
                return mineLeaves;
            }
        });
    }

    var module = {};
    function analyzeDepth(aForest) {
        var analyzer = new HeaderAnalyzer();
        analyzer.maxDepth(aForest, 0);
        analyzer.mineDepth(aForest, 0);
        return analyzer.depth;
    }
    Object.defineProperty(module, 'analyzeDepth', {
        get: function () {
            return analyzeDepth;
        }
    });
    function analyzeLeaves(aForest) {
        var analyzer = new HeaderAnalyzer();
        analyzer.mineLeaves(aForest, null);
    }
    Object.defineProperty(module, 'analyzeLeaves', {
        get: function () {
            return analyzeLeaves;
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

    function toLeaves(aRoots) {
        var leaves = [];
        achieveLeaves(aRoots, leaves);
        return leaves;
    }
    Object.defineProperty(module, 'toLeaves', {
        get: function () {
            return toLeaves;
        }
    });
    return module;
});