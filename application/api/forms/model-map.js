(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelMap");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelMap(aDelegate);
    });
    
    /**
     * Experimental. A model component that shows a map.
     * Unsupported in HTML5 client.
     * @constructor ModelMap ModelMap
     */
    P.ModelMap = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ModelMap.superclass)
            P.ModelMap.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.cursor = {};
        }
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseDragged = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onMouseDragged = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelMap){
            /**
             * Gets the parent of this component.
             * @property parent
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.parent = {};
        }
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseReleased = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onMouseReleased = {};
        }
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusLost = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onFocusLost = {};
        }
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMousePressed = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onMousePressed = {};
        }
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.foreground = {};
        }
        Object.defineProperty(this, "geoPosition", {
            get: function() {
                var value = delegate.geoPosition;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelMap){
            /**
             * The current geo position on the map.
             * @property geoPosition
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.geoPosition = {};
        }
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelMap){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.error = '';
        }
        Object.defineProperty(this, "tools", {
            get: function() {
                var value = delegate.tools;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelMap){
            /**
             * The map's mouse tools.
             * @property tools
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.tools = {};
        }
        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.enabled = true;
        }
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "backingUrl", {
            get: function() {
                var value = delegate.backingUrl;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.backingUrl = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * The map tiles service URL.
             * @property backingUrl
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.backingUrl = '';
        }
        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.componentPopupMenu = {};
        }
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.top = 0;
        }
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentResized = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseEntered = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onMouseEntered = {};
        }
        Object.defineProperty(this, "pane", {
            get: function() {
                var value = delegate.pane;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelMap){
            /**
             * The map's geo pane (read only).
             * @property pane
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.pane = {};
        }
        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.toolTipText = '';
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelMap){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.element = {};
        }
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Height of the component.
             * @property height
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.height = 0;
        }
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentShown = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onMouseMoved = {};
        }
        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.opaque = true;
        }
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.visible = true;
        }
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentHidden = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onComponentHidden = {};
        }
        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onActionPerformed = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onActionPerformed = {};
        }
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyReleased = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.focusable = true;
        }
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyTyped = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onKeyTyped = {};
        }
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelMap){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.component = {};
        }
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusGained = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onFocusGained = {};
        }
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.left = 0;
        }
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.background = {};
        }
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseClicked = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onMouseClicked = {};
        }
        Object.defineProperty(this, "onEvent", {
            get: function() {
                var value = delegate.onEvent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onEvent = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * The map's event handler function.
             * @property onEvent
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onEvent = {};
        }
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseExited = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelMap){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.name = '';
        }
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Width of the component.
             * @property width
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.width = 0;
        }
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * The font of this component.
             * @property font
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.font = {};
        }
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyPressed = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelMap){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ModelMap
             */
            P.ModelMap.prototype.onKeyPressed = {};
        }
    };
        /**
         * Gets map's layer by the title.
         * @param layerTitle the layer's title.
         * @return an <code>MapLayer</code> instance.
         * @method getLayer
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.getLayer = function(layerTitle) {
            var delegate = this.unwrap();
            var value = delegate.getLayer(P.boxAsJava(layerTitle));
            return P.boxAsJs(value);
        };

        /**
         * Hits to the specified point.
         * @param hitObject the object to hit, can be either a Point or a Polygon instance.
         * @return an array of <code>SelectionEntry</code> elements
         * @method hit
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.hit = function(hitObject) {
            var delegate = this.unwrap();
            var value = delegate.hit(P.boxAsJava(hitObject));
            return P.boxAsJs(value);
        };

        /**
         * Selects specified entries.
         * @param selectionEntries the array of <code>SelectionEntry</code> elements to select.
         * @method select
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.select = function(selectionEntries) {
            var delegate = this.unwrap();
            var value = delegate.select(P.boxAsJava(selectionEntries));
            return P.boxAsJs(value);
        };

        /**
         * Removes layer by the specified title.
         * @param layerTitle the layer's title.
         * @return <code>MapLayer</code> instance.
         * @method removeLayer
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.removeLayer = function(layerTitle) {
            var delegate = this.unwrap();
            var value = delegate.removeLayer(P.boxAsJava(layerTitle));
            return P.boxAsJs(value);
        };

        /**
         * Removes all layers of the map.
         * @return an array of <code>MapLayer</code> instances.
         * @method removeAllLayers
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.removeAllLayers = function() {
            var delegate = this.unwrap();
            var value = delegate.removeAllLayers();
            return P.boxAsJs(value);
        };

        /**
         * Transforms point from cartesian to geo coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method cartesian2Geo
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.cartesian2Geo = function(point) {
            var delegate = this.unwrap();
            var value = delegate.cartesian2Geo(P.boxAsJava(point));
            return P.boxAsJs(value);
        };

        /**
         * Transforms point from geo to cartesian coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method geo2Cartesian
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.geo2Cartesian = function(point) {
            var delegate = this.unwrap();
            var value = delegate.geo2Cartesian(P.boxAsJava(point));
            return P.boxAsJs(value);
        };

        /**
         * Transforms point from cartesian to screen coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method cartesian2Screen
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.cartesian2Screen = function(point) {
            var delegate = this.unwrap();
            var value = delegate.cartesian2Screen(P.boxAsJava(point));
            return P.boxAsJs(value);
        };

        /**
         * Tranaforms point from  screen to cartesian coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method screen2Cartesian
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.screen2Cartesian = function(point) {
            var delegate = this.unwrap();
            var value = delegate.screen2Cartesian(P.boxAsJava(point));
            return P.boxAsJs(value);
        };

        /**
         * Makes map move to the specified geo position.
         * @param position the position on the map.
         * @method goToGeoPosition
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.goToGeoPosition = function(position) {
            var delegate = this.unwrap();
            var value = delegate.goToGeoPosition(P.boxAsJava(position));
            return P.boxAsJs(value);
        };

        /**
         * Hits the selection on the specified point.
         * @param hitPoint the Point to hit.
         * @return an array of <code>SelectionEntry</code> elements.
         * @method hitSelection
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.hitSelection = function(hitPoint) {
            var delegate = this.unwrap();
            var value = delegate.hitSelection(P.boxAsJava(hitPoint));
            return P.boxAsJs(value);
        };

        /**
         * Fits the map to the specified area. If area parameter is not provided fits the map to the maximum extent.
         * @param area the <code>Geometry</code> of the specified area (optional)
         * @method fit
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.fit = function(area) {
            var delegate = this.unwrap();
            var value = delegate.fit(P.boxAsJava(area));
            return P.boxAsJs(value);
        };

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
        P.ModelMap.prototype.addLayer = function(layerTitle, rowset, geometryClass, styleAttributes) {
            var delegate = this.unwrap();
            var value = delegate.addLayer(P.boxAsJava(layerTitle), P.boxAsJava(rowset), P.boxAsJava(geometryClass), P.boxAsJava(styleAttributes));
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelMap
         */
        P.ModelMap.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();