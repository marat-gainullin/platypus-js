/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.gui.ScriptColor";
    var javaClass = Java.type(className);
    /**
     * The <code>Color</code> class is used to encapsulate colors in the default RGB color space.* @param red Red compontent (optional)
     * @param green Green compontent (optional)
     * @param blue Blue compontent (optional)
     * @param alpha Alpha compontent (optional)
     * @constructor Color Color
     */
    function Color(red, green, blue, alpha) {
        var maxArgs = 4;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 4 ? new javaClass(B.boxAsJava(red), B.boxAsJava(green), B.boxAsJava(blue), B.boxAsJava(alpha))
            : arguments.length === 3 ? new javaClass(B.boxAsJava(red), B.boxAsJava(green), B.boxAsJava(blue))
            : arguments.length === 2 ? new javaClass(B.boxAsJava(red), B.boxAsJava(green))
            : arguments.length === 1 ? new javaClass(B.boxAsJava(red))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(Color.superclass)
            Color.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "WHITE", {
            get: function() {
                var value = delegate.WHITE;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "GRAY", {
            get: function() {
                var value = delegate.GRAY;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "BLUE", {
            get: function() {
                var value = delegate.BLUE;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "GREEN", {
            get: function() {
                var value = delegate.GREEN;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "RED", {
            get: function() {
                var value = delegate.RED;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "PINK", {
            get: function() {
                var value = delegate.PINK;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "LIGHT_GRAY", {
            get: function() {
                var value = delegate.LIGHT_GRAY;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "MAGENTA", {
            get: function() {
                var value = delegate.MAGENTA;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "BLACK", {
            get: function() {
                var value = delegate.BLACK;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "YELLOW", {
            get: function() {
                var value = delegate.YELLOW;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "DARK_GRAY", {
            get: function() {
                var value = delegate.DARK_GRAY;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "CYAN", {
            get: function() {
                var value = delegate.CYAN;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "ORANGE", {
            get: function() {
                var value = delegate.ORANGE;
                return B.boxAsJs(value);
            }
        });

    };
    Object.defineProperty(Color, "black", {value: new Color(0, 0, 0)});
    Object.defineProperty(Color, "BLACK", {value: new Color(0, 0, 0)});
    Object.defineProperty(Color, "blue", {value: new Color(0, 0, 0xff)});
    Object.defineProperty(Color, "BLUE", {value: new Color(0, 0, 0xff)});
    Object.defineProperty(Color, "cyan", {value: new Color(0, 0xff, 0xff)});
    Object.defineProperty(Color, "CYAN", {value: new Color(0, 0xff, 0xff)});
    Object.defineProperty(Color, "DARK_GRAY", {value: new Color(0x40, 0x40, 0x40)});
    Object.defineProperty(Color, "darkGray", {value: new Color(0x40, 0x40, 0x40)});
    Object.defineProperty(Color, "gray", {value: new Color(0x80, 0x80, 0x80)});
    Object.defineProperty(Color, "GRAY", {value: new Color(0x80, 0x80, 0x80)});
    Object.defineProperty(Color, "green", {value: new Color(0, 0xff, 0)});
    Object.defineProperty(Color, "GREEN", {value: new Color(0, 0xff, 0)});
    Object.defineProperty(Color, "LIGHT_GRAY", {value: new Color(0xC0, 0xC0, 0xC0)});
    Object.defineProperty(Color, "lightGray", {value: new Color(0xC0, 0xC0, 0xC0)});
    Object.defineProperty(Color, "magenta", {value: new Color(0xff, 0, 0xff)});
    Object.defineProperty(Color, "MAGENTA", {value: new Color(0xff, 0, 0xff)});
    Object.defineProperty(Color, "orange", {value: new Color(0xff, 0xC8, 0)});
    Object.defineProperty(Color, "ORANGE", {value: new Color(0xff, 0xC8, 0)});
    Object.defineProperty(Color, "pink", {value: new Color(0xFF, 0xAF, 0xAF)});
    Object.defineProperty(Color, "PINK", {value: new Color(0xFF, 0xAF, 0xAF)});
    Object.defineProperty(Color, "red", {value: new Color(0xFF, 0, 0)});
    Object.defineProperty(Color, "RED", {value: new Color(0xFF, 0, 0)});
    Object.defineProperty(Color, "white", {value: new Color(0xFF, 0xff, 0xff)});
    Object.defineProperty(Color, "WHITE", {value: new Color(0xFF, 0xff, 0xff)});
    Object.defineProperty(Color, "yellow", {value: new Color(0xFF, 0xff, 0)});
    Object.defineProperty(Color, "YELLOW", {value: new Color(0xFF, 0xff, 0)});

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Color(null, null, null, null, aDelegate);
    });
    return Color;
});