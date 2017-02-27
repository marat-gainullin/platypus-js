/**
 * 
 * @author mg
 * @module
 */
function PlainPropertiesTest() {
    var self = this, model = P.loadModel(this.constructor.name);
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
                , new P.Menu()
                , new P.MenuItem()
                , new P.MenuSeparator()
                , new P.CheckMenuItem()
                , new P.RadioMenuItem()
    ];

    for (var c in comps) {
        var comp = comps[c];
        comp.visible = true;
        if (!comp.visible)
            throw 'comp.visible mismatch';
        comp.visible = false;
        if (comp.visible)
            throw 'comp.visible mismatch';
        comp.enabled = true;
        if (!comp.enabled)
            throw 'comp.enabled mismatch';
        comp.enabled = false;
        if (comp.enabled)
            throw 'comp.enabled mismatch';
        comp.toolTipText = 'some tooltip text';
        if (comp.toolTipText !== 'some tooltip text')
            throw 'comp.toolTipText mismatch';
        comp.toolTipText = null;
        if (comp.toolTipText)
            throw 'comp.toolTipText mismatch';
        comp.background = P.Color.BLUE;
        if (comp.background !== P.Color.BLUE)
            throw 'comp.background mismatch';
        comp.background = null;
        if (comp.background)
            throw 'comp.background mismatch';
        comp.foreground = P.Color.RED;
        if (comp.foreground !== P.Color.RED)
            throw 'comp.foreground mismatch';
        comp.foreground = null;
        if (comp.foreground)
            throw 'comp.foreground mismatch';
        comp.opaque = true;
        if (!comp.opaque)
            throw 'comp.opaque mismatch';
        comp.opaque = false;
        if (comp.opaque)
            throw 'comp.opaque mismatch';
        var compFont = new P.Font('arial', P.FontStyle.BOLD, 12);
        comp.font = compFont;
        if (comp.font !== compFont)
            throw 'comp.font mismatch';
        comp.font = null;
        if (comp.font)
            throw 'comp.font mismatch';
        comp.cursor = P.Cursor.WAIT;
        if (comp.cursor !== P.Cursor.WAIT)
            throw 'comp.cursor mismatch';
        comp.cursor = null;
        if (comp.cursor)
            throw 'comp.cursor mismatch';
        comp.left = 25;
        if (comp.left !== 25)
            throw 'comp.left mismatch';
        comp.left = 0;
        if (comp.left)
            throw 'comp.left mismatch';
        comp.top = 58;
        if (comp.top !== 58)
            throw 'comp.top mismatch';
        comp.top = 0;
        if (comp.top)
            throw 'comp.top mismatch';
        comp.width = 89;
        if (comp.width !== 89)
            throw 'comp.width mismatch';
        comp.width = 0;
        if (comp.width)
            throw 'comp.width mismatch';
        comp.height = 96;
        if (comp.height !== 96)
            throw 'comp.height mismatch';
        comp.height = 0;
        if (comp.height)
            throw 'comp.height mismatch';
        if (!(comp instanceof P.Menu)
                && !(comp instanceof P.PopupMenu)
                && !(comp instanceof P.MenuItem)
                && !(comp instanceof P.MenuSeparator)
                && !(comp instanceof P.CheckMenuItem)
                && !(comp instanceof P.RadioMenuItem)) {
            var compMenu = new P.PopupMenu();
            comp.componentPopupMenu = compMenu;
            if (comp.componentPopupMenu !== compMenu)
                throw 'comp.componentPopupMenu mmismatch';
            comp.componentPopupMenu = null;
            if (comp.componentPopupMenu)
                throw 'comp.componentPopupMenu mmismatch';
        }
///
        if (P.agent === P.HTML5 && !comp.element)
            throw 'comp.element mismatch';
        if (P.agent === P.J2SE && !comp.component){
            var c = comp.component;
            throw 'comp.component mismatch';
        }
///
        var f = function() {
        };
        comp.onActionPerformed = f;
        if (comp.onActionPerformed !== f) {
            throw 'comp.onActionPerformed mismatch';
        }
        comp.onActionPerformed = null;
        if (comp.onActionPerformed)
            throw 'comp.onActionPerformed mismatch';
        comp.onMouseExited = f;
        if (comp.onMouseExited !== f)
            throw 'comp.onMouseExited mismatch';
        comp.onMouseExited = null;
        if (comp.onMouseExited)
            throw 'comp.onMouseExited mismatch';
        comp.onMouseClicked = f;
        if (comp.onMouseClicked !== f)
            throw 'comp.onMouseClicked mismatch';
        comp.onMouseClicked = null;
        if (comp.onMouseClicked)
            throw 'comp.onMouseClicked mismatch';
        comp.onMousePressed = f;
        if (comp.onMousePressed !== f)
            throw 'comp.onMousePressed mismatch';
        comp.onMousePressed = null;
        if (comp.onMousePressed)
            throw 'comp.onMousePressed mismatch';
        comp.onMouseReleased = f;
        if (comp.onMouseReleased !== f)
            throw 'comp.onMouseReleased mismatch';
        comp.onMouseReleased = null;
        if (comp.onMouseReleased)
            throw 'comp.onMouseReleased mismatch';
        comp.onMouseEntered = f;
        if (comp.onMouseEntered !== f)
            throw 'comp.onMouseEntered mmismatch';
        comp.onMouseEntered = null;
        if (comp.onMouseEntered)
            throw 'comp.onMouseEntered mmismatch';
        comp.onMouseWheelMoved = f;
        if (comp.onMouseWheelMoved !== f)
            throw 'comp.onMouseWheelMoved mismatch';
        comp.onMouseWheelMoved = null;
        if (comp.onMouseWheelMoved)
            throw 'comp.onMouseWheelMoved mismatch';
        comp.onMouseDragged = f;
        if (comp.onMouseDragged !== f)
            throw 'comp.onMouseDragged mismatch';
        comp.onMouseDragged = null;
        if (comp.onMouseDragged)
            throw 'comp.onMouseDragged mismatch';
        comp.onMouseMoved = f;
        if (comp.onMouseMoved !== f)
            throw 'comp.onMouseMoved mismatch';
        comp.onMouseMoved = null;
        if (comp.onMouseMoved)
            throw 'comp.onMouseMoved mismatch';
        comp.onComponentResized = f;
        if (comp.onComponentResized !== f)
            throw 'comp.onComponentResized mismatch'
        comp.onComponentResized = null;
        if (comp.onComponentResized)
            throw 'comp.onComponentResized mismatch'
        comp.onComponentMoved = f;
        if (comp.onComponentMoved !== f)
            throw 'comp.onComponentMoved mismatch';
        comp.onComponentMoved = null;
        if (comp.onComponentMoved)
            throw 'comp.onComponentMoved mismatch';
        comp.onComponentShown = f;
        if (comp.onComponentShown !== f)
            throw 'comp.onComponentShown mismatch';
        comp.onComponentShown = null;
        if (comp.onComponentShown)
            throw 'comp.onComponentShown mismatch';
        comp.onComponentHidden = f;
        if (comp.onComponentHidden !== f)
            throw 'comp.onComponentHidden mismatch';
        comp.onComponentAdded = f;
        if (comp.onComponentAdded !== f)
            throw 'comp.onComponentAdded mismatch';
        comp.onComponentAdded = null;
        if (comp.onComponentAdded)
            throw 'comp.onComponentAdded mismatch';
        comp.onComponentRemoved = f;
        if (comp.onComponentRemoved !== f)
            throw 'comp.onComponentRemoved mismatch';
        comp.onComponentRemoved = null;
        if (comp.onComponentRemoved)
            throw 'comp.onComponentRemoved mismatch';
        comp.onFocusGained = f;
        if (comp.onFocusGained !== f)
            throw 'comp.onFocusGained mismatch';
        comp.onFocusGained = null;
        if (comp.onFocusGained)
            throw 'comp.onFocusGained mismatch';
        comp.onFocusLost = f;
        if (comp.onFocusLost !== f)
            throw 'comp.onFocusLost mismatch';
        comp.onFocusLost = null;
        if (comp.onFocusLost)
            throw 'comp.onFocusLost mismatch';
        comp.onKeyTyped = f;
        if (comp.onKeyTyped !== f)
            throw 'comp.onKeyTyped mismatch';
        comp.onKeyTyped = null;
        if (comp.onKeyTyped)
            throw 'comp.onKeyTyped mismatch';
        comp.onKeyPressed = f;
        if (comp.onKeyPressed !== f)
            throw 'comp.onKeyPressed mismatch';
        comp.onKeyPressed = null;
        if (comp.onKeyPressed)
            throw 'comp.onKeyPressed mismatch';
        comp.onKeyReleased = f;
        if (comp.onKeyReleased !== f)
            throw 'comp.onKeyReleased mismatch';
        comp.onKeyReleased = null;
        if (comp.onKeyReleased)
            throw 'comp.onKeyReleased mismatch';
///
        comp.focus();
    }

    var comp = new P.DropDownButton();
    var menu = new P.PopupMenu();
    comp.dropDownMenu = menu;
    if (comp.dropDownMenu !== menu) {
        throw 'comp.dropDownMenu mismatch';
    }
    comp.dropDownMenu = null;
    if (comp.dropDownMenu) {
        throw 'comp.dropDownMenu mismatch';
    }

    var alignedTextComps = [
        new P.Label()
                , new P.Button()
                , new P.DropDownButton()
                , new P.ToggleButton()
                , new P.MenuItem()
    ];
    for (var c in alignedTextComps) {
        var comp = alignedTextComps[c];
        // horizontalTextPosition
        comp.horizontalTextPosition = P.HorizontalPosition.LEFT;
        if (comp.horizontalTextPosition !== P.HorizontalPosition.LEFT)
            throw 'comp.horizontalTextPosition mismatch';
        comp.horizontalTextPosition = P.HorizontalPosition.CENTER;
        if (comp.horizontalTextPosition !== P.HorizontalPosition.CENTER)
            throw 'comp.horizontalTextPosition mismatch';
        comp.horizontalTextPosition = P.HorizontalPosition.RIGHT;
        if (comp.horizontalTextPosition !== P.HorizontalPosition.RIGHT)
            throw 'comp.horizontalTextPosition mismatch';
        //verticalTextPosition
        comp.verticalTextPosition = P.VerticalPosition.TOP;
        if (comp.verticalTextPosition !== P.VerticalPosition.TOP)
            throw 'comp.verticalTextPosition mismatch';
        comp.verticalTextPosition = P.VerticalPosition.CENTER;
        if (comp.verticalTextPosition !== P.VerticalPosition.CENTER)
            throw 'comp.verticalTextPosition mismatch';
        comp.verticalTextPosition = P.VerticalPosition.BOTTOM;
        if (comp.verticalTextPosition !== P.VerticalPosition.BOTTOM)
            throw 'comp.verticalTextPosition mismatch';
        //horizontalAlignment
        comp.horizontalAlignment = P.HorizontalPosition.LEFT;
        if (comp.horizontalAlignment !== P.HorizontalPosition.LEFT)
            throw 'comp.horizontalAlignment mismatch';
        comp.horizontalAlignment = P.HorizontalPosition.CENTER;
        if (comp.horizontalAlignment !== P.HorizontalPosition.CENTER)
            throw 'comp.horizontalAlignment mismatch';
        comp.horizontalAlignment = P.HorizontalPosition.RIGHT;
        if (comp.horizontalAlignment !== P.HorizontalPosition.RIGHT)
            throw 'comp.horizontalAlignment mismatch';
        //verticalAlignment
        comp.verticalAlignment = P.VerticalPosition.TOP;
        if (comp.verticalAlignment !== P.VerticalPosition.TOP)
            throw 'comp.verticalAlignment mismatch';
        comp.verticalAlignment = P.VerticalPosition.CENTER;
        if (comp.verticalAlignment !== P.VerticalPosition.CENTER)
            throw 'comp.verticalAlignment mismatch';
        comp.verticalAlignment = P.VerticalPosition.BOTTOM;
        if (comp.verticalAlignment !== P.VerticalPosition.BOTTOM)
            throw 'comp.verticalAlignment mismatch';
        //iconTexGap
        comp.iconTextGap = 6;
        if (comp.iconTextGap !== 6)
            throw 'comp.iconTextGap mismatch';
        //icon
        var compIcon = P.Icon.load('UI tests/point.png');
        comp.icon = compIcon;
        if (comp.icon !== compIcon)
            throw 'comp.icon mismatch';
        comp.icon = null;
        if (comp.icon)
            throw 'comp.icon mismatch';
    }

    (function() {
        if (self.onSuccess) {
            self.onSuccess();
        } else {
            P.Logger.severe("self.onSuccess is absent. So unable to report about test's result");
        }
    }).invokeLater();
}

