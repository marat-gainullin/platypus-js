/**
 * 
 * @author mg
 * @module
 */
function GroupsStructureTest() {
    var self = this;

    function checkContainerCildren(aContainer) {
        var i = 0;
        if (aContainer.count !== aContainer.children.length)
            throw '.count <> children.length';
        for (i = 0; i < aContainer.count; i++) {
            if (aContainer.children[i] !== aContainer.child(i)) {
                throw '.children[i] !== .child(i)';
            }
        }
    }

    var comps = [new P.ToggleButton()
                , new P.RadioButton()
                , new P.RadioMenuItem()
    ];
    var group1 = new P.ButtonGroup();
    var group2 = new P.ButtonGroup();

    // Test for add/remove methods
    for (var c = 0; c < comps.length; c++) {
        var comp = comps[c];
        // add test
        group1.add(comp);
        if (comp.buttonGroup !== group1)
            throw '.buttonGroup mismatch';
        checkContainerCildren(group1);
        checkContainerCildren(group2);
        // remove test
        group1.remove(comp);
        if (comp.buttonGroup !== null)
            throw '.buttonGroup mismatch';
        checkContainerCildren(group1);
        checkContainerCildren(group2);
        // re-add test
        group1.add(comp);
        if (comp.buttonGroup !== group1)
            throw '.buttonGroup mismatch';
        checkContainerCildren(group1);
        checkContainerCildren(group2);
        group2.add(comp);
        if (comp.buttonGroup !== group2)
            throw '.buttonGroup mismatch';
        if(group1.count > 0)
            throw 'group1.count mismatch';
        checkContainerCildren(group1);
        checkContainerCildren(group2);
        group2.remove(comp);
        if (comp.buttonGroup !== null)
            throw '.buttonGroup mismatch';
        checkContainerCildren(group1);
        checkContainerCildren(group2);
    }
    // Test for property assignments
    for (var c = 0; c < comps.length; c++) {
        var comp = comps[c];
        // add test
        comp.buttonGroup = group1;
        if (comp.buttonGroup !== group1)
            throw '.buttonGroup mismatch';
        checkContainerCildren(group1);
        checkContainerCildren(group2);
        // remove test
        comp.buttonGroup = null;
        if (comp.buttonGroup !== null)
            throw '.buttonGroup mismatch';
        checkContainerCildren(group1);
        checkContainerCildren(group2);
        // re-add test
        comp.buttonGroup = group1;
        if (comp.buttonGroup !== group1)
            throw '.buttonGroup mismatch';
        checkContainerCildren(group1);
        checkContainerCildren(group2);
        comp.buttonGroup = group2;
        if (comp.buttonGroup !== group2)
            throw '.buttonGroup mismatch';
        checkContainerCildren(group1);
        checkContainerCildren(group2);
        comp.buttonGroup = null;
        if (comp.buttonGroup !== null)
            throw '.buttonGroup mismatch';
        checkContainerCildren(group1);
        checkContainerCildren(group2);
    }
    // Test for property assignments
    for (var c in comps) {
        var comp = comps[c];
        // add test
        comp.buttonGroup = group1;
        if (comp.buttonGroup !== group1)
            throw '.buttonGroup mismatch';
    }
    // One of selected test
    for (var c in comps) {
        var comp = comps[c];
        comp.selected = true;
        for (var r in comps) {
            if (comps[r] === comp) {
                if (!comps[r].selected) {
                    throw '.selected mismatch';
                }
            } else if (comps[r].selected) {
                throw '!.selected mismatch 1';
            }
        }
    }
    // No one selected test
    for (var c in comps) {
        var comp = comps[c];
        comp.selected = false;
    }
    for (var c in comps) {
        var comp = comps[c];
        if(comp.selected)
            throw '!.selected mismatch 2';
    }
    
    (function(){
        if (self.onSuccess) {
            self.onSuccess();
        } else {
            P.Logger.severe("self.onSuccess is absent. So unable to report about test's result");
        }
    }).invokeLater();
}
