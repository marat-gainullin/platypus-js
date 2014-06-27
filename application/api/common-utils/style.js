(function() {
    var javaClass = Java.type("com.eas.gui.CascadedStyle");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Style(null, aDelegate);
    });
    
    /**
     * Style object. Can inherit propeties from the parent style object.
     * @param parent a parent <code>Style</code> (optional)
     * @constructor Style Style
     */
    P.Style = function (parent) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(parent))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.Style.superclass)
            P.Style.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            }
        });
        if(!P.Style){
            /**
             * A background color associated with this style.
             * @property background
             * @memberOf Style
             */
            P.Style.prototype.background = {};
        }
        Object.defineProperty(this, "icon", {
            get: function() {
                var value = delegate.icon;
                return P.boxAsJs(value);
            }
        });
        if(!P.Style){
            /**
             * An icon associated with this style.
             * @property icon
             * @memberOf Style
             */
            P.Style.prototype.icon = {};
        }
        Object.defineProperty(this, "folderIcon", {
            get: function() {
                var value = delegate.folderIcon;
                return P.boxAsJs(value);
            }
        });
        if(!P.Style){
            /**
             * A forlder icon associated with this style.
             * @property folderIcon
             * @memberOf Style
             */
            P.Style.prototype.folderIcon = {};
        }
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            }
        });
        if(!P.Style){
            /**
             * A foreground color associated with this style.
             * @property foreground
             * @memberOf Style
             */
            P.Style.prototype.foreground = {};
        }
        Object.defineProperty(this, "openFolderIcon", {
            get: function() {
                var value = delegate.openFolderIcon;
                return P.boxAsJs(value);
            }
        });
        if(!P.Style){
            /**
             * An open forlder icon associated with this style.
             * @property openFolderIcon
             * @memberOf Style
             */
            P.Style.prototype.openFolderIcon = {};
        }
        Object.defineProperty(this, "align", {
            get: function() {
                var value = delegate.align;
                return P.boxAsJs(value);
            }
        });
        if(!P.Style){
            /**
             * An align constraint associated with this style:
             * CENTER = 0; TOP = 1; LEFT = 2; BOTTOM = 3; RIGHT = 4.
             * @property align
             * @memberOf Style
             */
            P.Style.prototype.align = 0;
        }
        Object.defineProperty(this, "leafIcon", {
            get: function() {
                var value = delegate.leafIcon;
                return P.boxAsJs(value);
            }
        });
        if(!P.Style){
            /**
             * A leaf icon associated with this style.
             * @property leafIcon
             * @memberOf Style
             */
            P.Style.prototype.leafIcon = {};
        }
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            }
        });
        if(!P.Style){
            /**
             * A font associated with this style.
             * @property font
             * @memberOf Style
             */
            P.Style.prototype.font = {};
        }
    };
})();