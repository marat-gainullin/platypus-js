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
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * A background color associated with this style.
         * @property background
         * @memberOf Style
         */
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            }
        });

        /**
         * An icon associated with this style.
         * @property icon
         * @memberOf Style
         */
        Object.defineProperty(this, "icon", {
            get: function() {
                var value = delegate.icon;
                return P.boxAsJs(value);
            }
        });

        /**
         * A forlder icon associated with this style.
         * @property folderIcon
         * @memberOf Style
         */
        Object.defineProperty(this, "folderIcon", {
            get: function() {
                var value = delegate.folderIcon;
                return P.boxAsJs(value);
            }
        });

        /**
         * A foreground color associated with this style.
         * @property foreground
         * @memberOf Style
         */
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            }
        });

        /**
         * An open forlder icon associated with this style.
         * @property openFolderIcon
         * @memberOf Style
         */
        Object.defineProperty(this, "openFolderIcon", {
            get: function() {
                var value = delegate.openFolderIcon;
                return P.boxAsJs(value);
            }
        });

        /**
         * An align constraint associated with this style:
         * CENTER = 0; TOP = 1; LEFT = 2; BOTTOM = 3; RIGHT = 4.
         * @property align
         * @memberOf Style
         */
        Object.defineProperty(this, "align", {
            get: function() {
                var value = delegate.align;
                return P.boxAsJs(value);
            }
        });

        /**
         * A leaf icon associated with this style.
         * @property leafIcon
         * @memberOf Style
         */
        Object.defineProperty(this, "leafIcon", {
            get: function() {
                var value = delegate.leafIcon;
                return P.boxAsJs(value);
            }
        });

        /**
         * A font associated with this style.
         * @property font
         * @memberOf Style
         */
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();