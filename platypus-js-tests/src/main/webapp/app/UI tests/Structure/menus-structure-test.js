/**
 * 
 * @author mg
 * @module
 */
function MenusStructureTest() {
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

    var conts = [new P.Menu()
        , new P.MenuBar()
        , new P.PopupMenu()
        ];
    var comps = [new P.Menu()
        , new P.MenuItem()
        , new P.MenuSeparator()
        , new P.CheckMenuItem()
        , new P.RadioMenuItem()
        ];

    for (var cn = 0; cn < conts.length; cn++) {
        var container = conts[cn];
        for (var c = 0; c < comps.length; c++) {
            var comp = comps[c];
            container.add(comp);
            if(comp.parent !== container)
                throw '.parent mismatch';
            checkContainerCildren(container);
        }
        for (var c = 0; c < comps.length; c++) {
            var comp = comps[c];
            container.remove(comp);
            if(comp.parent !== null)
                throw '.parent null mismatch';
            checkContainerCildren(container);
        }
    }
    
    (function(){
        if (self.onSuccess) {
            self.onSuccess();
        } else {
            P.Logger.severe("self.onSuccess is absent. So unable to report about test's result");
        }
    }).invokeLater();
}
