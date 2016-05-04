var layoutMin = {
    tags: {
        "lb": "Label", // se web
        "bt": "Button", // se web
        "ddb": "DropDownButton", // se web
        "bg": "ButtonGroup", // se web
        "cb": "CheckBox", // se web
        "ta": "TextArea", // se web
        "ha": "HtmlArea", // se web
        "ff": "FormattedField", // se web
        "pf": "PasswordField", // se web
        "pb": "ProgressBar", // se web
        "rb": "RadioButton", // se web
        "s": "Slider", // se web
        "tf": "TextField", // se web
        "tb": "ToggleButton", // se web
        "dp": "DesktopPane", // se web
        "mcb": "ModelCheckBox", // se web
        "mc": "ModelCombo", // se web
        "md": "ModelDate", // se web
        "mff": "ModelFormattedField", // se web
        "msp": "ModelSpin", // se web
        "mta": "ModelTextArea", // se web
        "mg": "ModelGrid", // se web
        "ap": "AnchorsPane", // se web
        "bp": "BorderPane", // se web
        "bx": "BoxPane", // se web
        "cp": "CardPane", // se web
        "fp": "FlowPane", // se web
        "gp": "GridPane", // se web
        "sp": "ScrollPane", // se web
        "spl": "SplitPane", // se web
        "tp": "TabbedPane", // se web
        "tl": "ToolBar", // se web
        "m": "Menu", // se web
        "mi": "MenuItem", // se web
        "cmi": "CheckMenuItem", // se web
        "rmi": "RadioMenuItem", // se web
        "ms": "MenuSeparator", // se web
        "mb": "MenuBar", // se web
        "pm": "PopupMenu", // se web
        "tpc": "TabbedPaneConstraints", // se web
        "bpc": "BorderPaneConstraints", // se web
        "cpc": "CardPaneConstraints", // se web
        "apc": "AnchorsPaneConstraints", // se web
        "cgc": "CheckGridColumn", // se web
        "rgc": "RadioGridColumn", // se web
        "sgc": "ServiceGridColumn", // se web
        "mgc": "ModelGridColumn", // se web
        "ft": "font"//  se web
    }, attributes: {
        "i": "icon", // web
        "tx": "text", // web
        "ha": "horizontalAlignment", // web
        "va": "verticalAlignment", // web
        "itg": "iconTextGap", // web
        "htp": "horizontalTextPosition", // web
        "vtp": "verticalTextPosition", // web
        "n": "name", // web
        "e": "editable", // web
        "et": "emptyText", // web
        "f": "field", // web
        "fr": "format", // web
        "ft": "font", // web
        "bg": "background", // web
        "fg": "foreground", // web
        "d": "data", // web
        "en": "enabled", // web
        "fc": "focusable", // web
        "o": "opaque", // web
        "ttt": "toolTipText", // web
        "cr": "cursor", // 
        "v": "visible", // web
        "nfc": "nextFocusableComponent", //
        "cpm": "componentPopupMenu", // web
        "bgr": "buttonGroup", // web
        "p": "parent", // web
        "tt": "tabTitle", // web
        "ti": "tabIcon", // web
        "ttp": "tabTooltipText", // web
        "pl": "place", // web
        "cn": "cardName", // web
        "l": "left", // web
        "r": "right", // web
        "t": "top", // web
        "b": "bottom", //web
        "w": "width", // web
        "h": "height", // web
        "sf": "sortField", // web
        "tl": "title", // web
        "ro": "readonly", // web
        "mw": "minWidth", // web
        "mxw": "maxWidth", // web
        "prw": "preferredWidth", // web
        "m": "movable", // web
        "rs": "resizable", // web
        "so": "selectOnly", // web
        "st": "selected", // web
        "s": "sortable", // web
        "pw": "prefWidth", // web
        "ph": "prefHeight", // web
        "dco": "defaultCloseOperation", // web
        "cle": "closable", // web
        "mxe": "maximizable", // web
        "mne": "minimizable", // web
        "udr": "undecorated", // web
        "opc": "opacity", // web
        "aot": "alwaysOnTop", // web
        "lbp": "locationByPlatform", // web
        "lf": "labelFor", //
        "ddm": "dropDownMenu", // web
        "vt": "valueType", // web
        "mm": "minimum", // web
        "vl": "value", // web
        "mx": "maximum", // web
        "nl": "nullable", // web
        "ls": "list", // web
        "dl": "displayList", // web
        "df": "displayField", // web
        "dtp": "datePicker", //
        "tmp": "timePicker", //
        "frc": "frozenColumns", //web
        "frr": "frozenRows", // web
        "ie": "insertable", // web
        "de": "deletable", // web
        "hv": "headerVisible", // web
        "dr": "draggableRows", // web
        "shl": "showHorizontalLines", // web
        "svl": "showVerticalLines", // web
        "soc": "showOddRowsInOtherColor", // web
        "rh": "rowsHeight", // web
        "orc": "oddRowsColor", // web
        "gc": "gridColor", // web
        "pf": "parentField", // web
        "cf": "childrenField", // web
        "on": "orientation", // web
        "wse": "wheelScrollingEnabled", // web
        "hsp": "horizontalScrollBarPolicy", // web
        "vsp": "verticalScrollBarPolicy", // web
        "ote": "oneTouchExpandable", // web
        "dvl": "dividerLocation", // web
        "ds": "dividerSize", // 
        "lc": "leftComponent", // web
        "rc": "rightComponent", // web
        "tp": "tabPlacement", // web
        "stl": "style", // web
        "sz": "size", // web
        // non minified attributes
        "step": "step",
        "min": "min",
        "max": "max",
        "hgap": "hgap",
        "vgap": "vgap",
        "rows": "rows",
        "columns": "columns",
        "view": "view"
    }};
print('layout tags:');
for (var t in layoutMin.tags) {
    print('put("' + layoutMin.tags[t] + '","' + t + '");');
}
print('layout attributes:');
for (var a in layoutMin.attributes) {
    print('put("' + layoutMin.attributes[a] + '","' + t + '");');
}