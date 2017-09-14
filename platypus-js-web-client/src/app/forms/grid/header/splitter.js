define([], function () {
    function HeaderSplitter(minLeave, maxLeave) {
        var splittedLeaves = [];
        var leaveIndex = -1;

        function toRoots() {
            var res = [];
            var met = new Set();
            for (var i = 0; i < splittedLeaves.length; i++) {
                var leaf = splittedLeaves[i];
                var parent = leaf;
                while (parent.parent) {
                    parent = parent.parent;
                }
                if (!met.has(parent)) {
                    met.add(parent);
                    res.push(parent);
                }
            }
            return res;
        }
        Object.defineProperty(this, 'toRoots', {
            get: function(){
                return toRoots;
            }
        });

        function process(toBeSplitted, aClonedParent) {
            var res = false;
            for (var i = 0; i < toBeSplitted.length; i++) {
                var n = toBeSplitted[i];
                var nc = n.copy();
                if (n.children.length === 0) {
                    leaveIndex++;
                    if (leaveIndex >= minLeave && leaveIndex <= maxLeave) {
                        res = true;
                        splittedLeaves.push(nc);
                        if (aClonedParent) {
                            aClonedParent.addColumnNode(nc);
                        }
                    }
                } else {
                    var isGoodLeaveIndex = process(n.children, nc);
                    if (isGoodLeaveIndex) {
                        res = true;
                        if (aClonedParent) {
                            aClonedParent.addColumnNode(nc);
                        }
                    }
                }
            }
            return res;
        }
        Object.defineProperty(this, 'process', {
            get: function(){
                return process;
            }
        });
    }
    var module = {};
    
    function injectHeaders(forest){
        forest.forEach(function(node){
            node.column.headers.push(node.header);
            injectHeaders(node.children);
        });
    }
    
    function split(toBeSplitted, aMinLeave, aMaxLeave) {
        var splitter = new HeaderSplitter(aMinLeave, aMaxLeave);
        splitter.process(toBeSplitted, null);
        var clonedPortion = splitter.toRoots();
        injectHeaders(clonedPortion);
        return clonedPortion;
    }
    Object.defineProperty(module, 'split', {
        get: function () {
            return split;
        }
    });
    return module;
});