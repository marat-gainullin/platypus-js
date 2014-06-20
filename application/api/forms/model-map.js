(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelMap");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelMap(aDelegate);
    });
    
    /**
     * A model component that shows a map.
     * @constructor ModelMap ModelMap
     */
    P.ModelMap = function ModelMap() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ModelMap.superclass)
            ModelMap.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ModelMap", {value: ModelMap});
    Object.defineProperty(ModelMap.prototype, "cursor", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.cursor;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.cursor = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.cursor = {};
    }
    Object.defineProperty(ModelMap.prototype, "onMouseDragged", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseDragged;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseDragged = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ModelMap.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.parent = {};
    }
    Object.defineProperty(ModelMap.prototype, "onMouseReleased", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseReleased;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseReleased = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ModelMap.prototype, "onFocusLost", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onFocusLost;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onFocusLost = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onFocusLost = {};
    }
    Object.defineProperty(ModelMap.prototype, "onMousePressed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMousePressed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMousePressed = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onMousePressed = {};
    }
    Object.defineProperty(ModelMap.prototype, "foreground", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.foreground;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.foreground = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.foreground = {};
    }
    Object.defineProperty(ModelMap.prototype, "geoPosition", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.geoPosition;
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * The current geo position on the map.
         * @property geoPosition
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.geoPosition = {};
    }
    Object.defineProperty(ModelMap.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.error = '';
    }
    Object.defineProperty(ModelMap.prototype, "tools", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.tools;
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * The map's mouse tools.
         * @property tools
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.tools = {};
    }
    Object.defineProperty(ModelMap.prototype, "enabled", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.enabled;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.enabled = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.enabled = true;
    }
    Object.defineProperty(ModelMap.prototype, "onComponentMoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentMoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentMoved = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ModelMap.prototype, "backingUrl", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.backingUrl;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.backingUrl = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * The map tiles service URL.
         * @property backingUrl
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.backingUrl = '';
    }
    Object.defineProperty(ModelMap.prototype, "componentPopupMenu", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.componentPopupMenu;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.componentPopupMenu = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ModelMap.prototype, "top", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.top;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.top = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.top = 0;
    }
    Object.defineProperty(ModelMap.prototype, "onComponentResized", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentResized;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentResized = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onComponentResized = {};
    }
    Object.defineProperty(ModelMap.prototype, "onMouseEntered", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseEntered;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseEntered = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ModelMap.prototype, "pane", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.pane;
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * The map's geo pane (read only).
         * @property pane
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.pane = {};
    }
    Object.defineProperty(ModelMap.prototype, "toolTipText", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.toolTipText;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.toolTipText = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.toolTipText = '';
    }
    Object.defineProperty(ModelMap.prototype, "height", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.height;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.height = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Height of the component.
         * @property height
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.height = 0;
    }
    Object.defineProperty(ModelMap.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.element = {};
    }
    Object.defineProperty(ModelMap.prototype, "onComponentShown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentShown;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentShown = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onComponentShown = {};
    }
    Object.defineProperty(ModelMap.prototype, "onMouseMoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseMoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseMoved = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ModelMap.prototype, "opaque", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.opaque;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.opaque = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.opaque = true;
    }
    Object.defineProperty(ModelMap.prototype, "visible", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.visible;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.visible = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.visible = true;
    }
    Object.defineProperty(ModelMap.prototype, "onComponentHidden", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentHidden;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentHidden = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ModelMap.prototype, "nextFocusableComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.nextFocusableComponent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.nextFocusableComponent = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ModelMap.prototype, "onActionPerformed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onActionPerformed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onActionPerformed = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ModelMap.prototype, "onKeyReleased", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onKeyReleased;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onKeyReleased = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ModelMap.prototype, "focusable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.focusable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.focusable = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.focusable = true;
    }
    Object.defineProperty(ModelMap.prototype, "onKeyTyped", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onKeyTyped;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onKeyTyped = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ModelMap.prototype, "onMouseWheelMoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseWheelMoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseWheelMoved = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ModelMap.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.component = {};
    }
    Object.defineProperty(ModelMap.prototype, "onFocusGained", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onFocusGained;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onFocusGained = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onFocusGained = {};
    }
    Object.defineProperty(ModelMap.prototype, "left", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.left;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.left = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.left = 0;
    }
    Object.defineProperty(ModelMap.prototype, "background", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.background;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.background = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.background = {};
    }
    Object.defineProperty(ModelMap.prototype, "onMouseClicked", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseClicked;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseClicked = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ModelMap.prototype, "onEvent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onEvent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onEvent = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * The map's event handler function.
         * @property onEvent
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onEvent = {};
    }
    Object.defineProperty(ModelMap.prototype, "onMouseExited", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseExited;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseExited = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onMouseExited = {};
    }
    Object.defineProperty(ModelMap.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.name = '';
    }
    Object.defineProperty(ModelMap.prototype, "width", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.width;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.width = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Width of the component.
         * @property width
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.width = 0;
    }
    Object.defineProperty(ModelMap.prototype, "font", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.font;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.font = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * The font of this component.
         * @property font
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.font = {};
    }
    Object.defineProperty(ModelMap.prototype, "onKeyPressed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onKeyPressed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onKeyPressed = P.boxAsJava(aValue);
        }
    });
    if(!ModelMap){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ModelMap.prototype, "hit", {
        value: function(hitObject) {
            var delegate = this.unwrap();
            var value = delegate.hit(P.boxAsJava(hitObject));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Hits to the specified point.
         * @param hitObject the object to hit, can be either a Point or a Polygon instance.
         * @return an array of <code>SelectionEntry</code> elements
         * @method hit
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.hit = function(hitObject){};
    }
    Object.defineProperty(ModelMap.prototype, "select", {
        value: function(selectionEntries) {
            var delegate = this.unwrap();
            var value = delegate.select(P.boxAsJava(selectionEntries));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Selects specified entries.
         * @param selectionEntries the array of <code>SelectionEntry</code> elements to select.
         * @method select
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.select = function(selectionEntries){};
    }
    Object.defineProperty(ModelMap.prototype, "getLayer", {
        value: function(layerTitle) {
            var delegate = this.unwrap();
            var value = delegate.getLayer(P.boxAsJava(layerTitle));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Gets map's layer by the title.
         * @param layerTitle the layer's title.
         * @return an <code>MapLayer</code> instance.
         * @method getLayer
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.getLayer = function(layerTitle){};
    }
    Object.defineProperty(ModelMap.prototype, "fit", {
        value: function(area) {
            var delegate = this.unwrap();
            var value = delegate.fit(P.boxAsJava(area));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Fits the map to the specified area. If area parameter is not provided fits the map to the maximum extent.
         * @param area the <code>Geometry</code> of the specified area (optional)
         * @method fit
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.fit = function(area){};
    }
    Object.defineProperty(ModelMap.prototype, "removeLayer", {
        value: function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.removeLayer(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Removes layer by the specified title.
         * @param layerTitle the layer's title.
         * @return <code>MapLayer</code> instance.
         * @method removeLayer
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.removeLayer = function(arg0){};
    }
    Object.defineProperty(ModelMap.prototype, "removeAllLayers", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.removeAllLayers();
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Removes all layers of the map.
         * @return an array of <code>MapLayer</code> instances.
         * @method removeAllLayers
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.removeAllLayers = function(){};
    }
    Object.defineProperty(ModelMap.prototype, "cartesian2Geo", {
        value: function(point) {
            var delegate = this.unwrap();
            var value = delegate.cartesian2Geo(P.boxAsJava(point));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Transforms point from cartesian to geo coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method cartesian2Geo
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.cartesian2Geo = function(point){};
    }
    Object.defineProperty(ModelMap.prototype, "geo2Cartesian", {
        value: function(point) {
            var delegate = this.unwrap();
            var value = delegate.geo2Cartesian(P.boxAsJava(point));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Transforms point from geo to cartesian coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method geo2Cartesian
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.geo2Cartesian = function(point){};
    }
    Object.defineProperty(ModelMap.prototype, "cartesian2Screen", {
        value: function(point) {
            var delegate = this.unwrap();
            var value = delegate.cartesian2Screen(P.boxAsJava(point));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Transforms point from cartesian to screen coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method cartesian2Screen
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.cartesian2Screen = function(point){};
    }
    Object.defineProperty(ModelMap.prototype, "screen2Cartesian", {
        value: function(point) {
            var delegate = this.unwrap();
            var value = delegate.screen2Cartesian(P.boxAsJava(point));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Tranaforms point from  screen to cartesian coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method screen2Cartesian
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.screen2Cartesian = function(point){};
    }
    Object.defineProperty(ModelMap.prototype, "goToGeoPosition", {
        value: function(position) {
            var delegate = this.unwrap();
            var value = delegate.goToGeoPosition(P.boxAsJava(position));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Makes map move to the specified geo position.
         * @param position the position on the map.
         * @method goToGeoPosition
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.goToGeoPosition = function(position){};
    }
    Object.defineProperty(ModelMap.prototype, "hitSelection", {
        value: function(hitPoint) {
            var delegate = this.unwrap();
            var value = delegate.hitSelection(P.boxAsJava(hitPoint));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Hits the selection on the specified point.
         * @param hitPoint the Point to hit.
         * @return an array of <code>SelectionEntry</code> elements.
         * @method hitSelection
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.hitSelection = function(hitPoint){};
    }
    Object.defineProperty(ModelMap.prototype, "addLayer", {
        value: function(layerTitle, rowset, geometryClass, styleAttributes) {
            var delegate = this.unwrap();
            var value = delegate.addLayer(P.boxAsJava(layerTitle), P.boxAsJava(rowset), P.boxAsJava(geometryClass), P.boxAsJava(styleAttributes));
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Adds new layer to the map.
         * @param layerTitle the layer's title.
         * @param rowset the layer's data.
         * @param geometryClass the geometry class.
         * @param styleAttributes the layer's style attributes.
         * @return <code>MapLayer</code> instance.
         * @method addLayer
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.addLayer = function(layerTitle, rowset, geometryClass, styleAttributes){};
    }
    Object.defineProperty(ModelMap.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ModelMap){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.focus = function(){};
    }
})();