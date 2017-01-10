/**
 * 
 * @author mg
 * @module
 */
function WidgetsStructureTest() {
    var self = this, model = P.loadModel(this.constructor.name);

    function checkContainerChildren(aContainer) {
        var i = 0;
        if (aContainer.count !== aContainer.children.length)
            throw '.count <> children.length';
        for (i = 0; i < aContainer.count; i++) {
            var ch1 = aContainer.children[i];
            var ch2 = aContainer.child(i);
            if (ch1 !== ch2) {
                throw '.children[i] !== .child(i)';
            }
        }
    }

    function checkGridPaneChildren(aGridPane) {
        var children = aGridPane.children;
        var i = 0;
        for (var r = 0; r < aGridPane.rows; r++) {
            for (var c = 0; c < aGridPane.columns; c++) {
                var child = aGridPane.child(r, c);
                if (child !== null && child !== children[i++]) {
                    throw 'child != children[i++]'
                }
            }
        }
    }

    function checkObjectProjection(aEthalon, aContent) {
        for (var p in aEthalon) {
            var e = aEthalon[p];
            var c = aContent[p];
            if (aEthalon[p] !== aContent[p] && (aEthalon[p] + 'px') !== aContent[p]) {
                //throw 'Ethalon and content objects mismatch';
            }
        }
    }

    var anchors0 = new P.Anchors(10, 20, null, 40, 50, null);
    var anchors1 = new P.Anchors(null, '20px', '56px', '40px', '50px', null);
    var anchors2 = new P.Anchors('10%', null, '56%', '40%', null, '90%');
    var anchors3 = {left: 10, width: 20, top: 40, height: 50};
    var anchors4 = {width: '20px', right: '56px', top: '40px', height: '50px'};
    var anchors5 = {left: '10%', right: '56%', top: '40%', bottom: '90%'};
    checkObjectProjection(anchors3, anchors0);
    checkObjectProjection(anchors4, anchors1);
    checkObjectProjection(anchors5, anchors2);

    var comps = [new P.AbsolutePane()
                , new P.AnchorsPane()
                , new P.BorderPane()
                , new P.BoxPane()
                , new P.Button()
                , new P.CardPane()
                , new P.CheckBox()
                , new P.DesktopPane()
                , new P.DropDownButton()
                , new P.FlowPane()
                , new P.FormattedField()
                , new P.GridPane(10, 10)
                , new P.HtmlArea()
                , new P.Label()
                , new P.ModelCheckBox()
                , new P.ModelCombo()
                , new P.ModelDate()
                , new P.ModelFormattedField()
                , new P.ModelGrid()
                //, new P.ModelImage()
                //, new P.ModelMap()
                //, new P.ModelScheme()
                , new P.ModelSpin()
                , new P.ModelTextArea()
                , new P.PasswordField()
                , new P.ProgressBar()
                , new P.RadioButton()
                , new P.ScrollPane()
                , new P.SplitPane()
                , new P.TabbedPane()
                , new P.TextArea()
                , new P.TextField()
                , new P.ToggleButton()
                , new P.ToolBar()
    ];
    var conts = [new P.AbsolutePane()
                , new P.GridPane(10, 5)
                , new P.AnchorsPane()
                , new P.BorderPane()
                , new P.BoxPane()
                , new P.CardPane()
                , new P.FlowPane()
                , new P.ScrollPane()
                , new P.SplitPane()
                , new P.TabbedPane()
                , new P.ToolBar()
    ];

    // default container's constraints
    for (var cn = 0; cn < conts.length; cn++) {
        var container = conts[cn];
        for (var c = 0; c < comps.length; c++) {
            var comp = comps[c];
            if (container instanceof P.GridPane) {
                var cell = container.child(0, 3);
                if (cell) {
                    if (cell.parent !== container) {
                        throw '.parent mismatch';
                    }
                }
                container.add(comp, 0, 3);
                if (cell) {
                    if (cell.parent !== null) {
                        throw '.parent null mismatch';
                    }
                }
                checkGridPaneChildren(container);
            } else {
                container.add(comp);
                checkContainerChildren(container);
            }
            if (comp.parent !== container)
                throw '.parent mismatch';
            if (container instanceof P.BorderPane && container.centerComponent)
                break;
        }
        for (var c = 0; c < comps.length; c++) {
            var comp = comps[c];
            container.remove(comp);
            if (comp.parent !== null)
                throw '.parent null mismatch';
            if (container instanceof P.GridPane) {
                checkGridPaneChildren(container);
            } else {
                checkContainerChildren(container);
            }
        }
    }

    var plainJSOConstraints = true;
    // custom container's constraints
    function calcConstraint(aContainer) {
        plainJSOConstraints = !plainJSOConstraints;
        if (aContainer instanceof P.AnchorsPane) {
            return plainJSOConstraints ? anchors1 : anchors4;
        } else if (aContainer instanceof P.AbsolutePane) {
            return plainJSOConstraints ? anchors0 : anchors3;
        } else if (aContainer instanceof P.GridPane) {
            return [2, 3];
        } else if (aContainer instanceof P.CardPane) {
            return "someCard";
        } else if (aContainer instanceof P.TabbedPane) {
            return ["someTab", null];
        } else if (aContainer instanceof P.BorderPane) {
            return [P.HorizontalPosition.CENTER, null];
        }
        return null;
    }

    for (var cn = 0; cn < conts.length; cn++) {
        var container = conts[cn];
        for (var c = 0; c < comps.length; c++) {
            var comp = comps[c];
            var constraint = calcConstraint(container);
            if (Array.isArray(constraint))
                container.add(comp, constraint[0], constraint[1]);
            else
                container.add(comp, constraint);
            if (comp.parent !== container)
                throw '.parent mismatch';
            if (container instanceof P.GridPane) {
                checkGridPaneChildren(container);
            } else {
                checkContainerChildren(container);
            }
            if (container instanceof P.BorderPane && container.centerComponent)
                break;
        }
        for (var c = 0; c < comps.length; c++) {
            var comp = comps[c];
            container.remove(comp);
            if (comp.parent !== null)
                throw '.parent null mismatch';
            if (container instanceof P.GridPane) {
                checkGridPaneChildren(container);
            } else {
                checkContainerChildren(container);
            }
        }
    }
    (function() {
        if (self.onSuccess) {
            self.onSuccess();
        } else {
            P.Logger.severe("self.onSuccess is absent. So unable to report about test's result");
        }
    }).invokeLater();
}
