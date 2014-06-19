(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelMap");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelMap(aDelegate);
    });
    
    /**
     * A model component that shows a map.
     * @constructor ModelMap ModelMap
     */
    P.ModelMap = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseDragged = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });

        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseReleased = P.boxAsJava(aValue);
            }
        });

        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusLost = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMousePressed = P.boxAsJava(aValue);
            }
        });

        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });

        /**
         * The current geo position on the map.
         * @property geoPosition
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "geoPosition", {
            get: function() {
                var value = delegate.geoPosition;
                return P.boxAsJs(value);
            }
        });

        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });

        /**
         * The map's mouse tools.
         * @property tools
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "tools", {
            get: function() {
                var value = delegate.tools;
                return P.boxAsJs(value);
            }
        });

        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });

        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * The map tiles service URL.
         * @property backingUrl
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "backingUrl", {
            get: function() {
                var value = delegate.backingUrl;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.backingUrl = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });

        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });

        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentResized = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseEntered = P.boxAsJava(aValue);
            }
        });

        /**
         * The map's geo pane (read only).
         * @property pane
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "pane", {
            get: function() {
                var value = delegate.pane;
                return P.boxAsJs(value);
            }
        });

        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });

        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });

        /**
         * Height of the component.
         * @property height
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });

        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentShown = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });

        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentHidden = P.boxAsJava(aValue);
            }
        });

        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });

        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onActionPerformed = P.boxAsJava(aValue);
            }
        });

        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyReleased = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
            }
        });

        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyTyped = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });

        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusGained = P.boxAsJava(aValue);
            }
        });

        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });

        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseClicked = P.boxAsJava(aValue);
            }
        });

        /**
         * The map's event handler function.
         * @property onEvent
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onEvent", {
            get: function() {
                var value = delegate.onEvent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onEvent = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseExited = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });

        /**
         * Width of the component.
         * @property width
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });

        /**
         * The font of this component.
         * @property font
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });

        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyPressed = P.boxAsJava(aValue);
            }
        });

        /**
         * Selects specified entries.
         * @param selectionEntries the array of <code>SelectionEntry</code> elements to select.
         * @method select
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "select", {
            get: function() {
                return function(selectionEntries) {
                    var value = delegate.select(P.boxAsJava(selectionEntries));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Hits to the specified point.
         * @param hitObject the object to hit, can be either a Point or a Polygon instance.
         * @return an array of <code>SelectionEntry</code> elements
         * @method hit
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "hit", {
            get: function() {
                return function(hitObject) {
                    var value = delegate.hit(P.boxAsJava(hitObject));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Gets map's layer by the title.
         * @param layerTitle the layer's title.
         * @return an <code>MapLayer</code> instance.
         * @method getLayer
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "getLayer", {
            get: function() {
                return function(layerTitle) {
                    var value = delegate.getLayer(P.boxAsJava(layerTitle));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Fits the map to the specified area. If area parameter is not provided fits the map to the maximum extent.
         * @param area the <code>Geometry</code> of the specified area (optional)
         * @method fit
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "fit", {
            get: function() {
                return function(area) {
                    var value = delegate.fit(P.boxAsJava(area));
                    return P.boxAsJs(value);
                };
            }
        });

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
        Object.defineProperty(this, "addLayer", {
            get: function() {
                return function(layerTitle, rowset, geometryClass, styleAttributes) {
                    var value = delegate.addLayer(P.boxAsJava(layerTitle), P.boxAsJava(rowset), P.boxAsJava(geometryClass), P.boxAsJava(styleAttributes));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Removes layer by the specified title.
         * @param layerTitle the layer's title.
         * @return <code>MapLayer</code> instance.
         * @method removeLayer
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "removeLayer", {
            get: function() {
                return function(arg0) {
                    var value = delegate.removeLayer(P.boxAsJava(arg0));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Removes all layers of the map.
         * @return an array of <code>MapLayer</code> instances.
         * @method removeAllLayers
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "removeAllLayers", {
            get: function() {
                return function() {
                    var value = delegate.removeAllLayers();
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Transforms point from cartesian to geo coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method cartesian2Geo
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "cartesian2Geo", {
            get: function() {
                return function(point) {
                    var value = delegate.cartesian2Geo(P.boxAsJava(point));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Transforms point from geo to cartesian coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method geo2Cartesian
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "geo2Cartesian", {
            get: function() {
                return function(point) {
                    var value = delegate.geo2Cartesian(P.boxAsJava(point));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Transforms point from cartesian to screen coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method cartesian2Screen
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "cartesian2Screen", {
            get: function() {
                return function(point) {
                    var value = delegate.cartesian2Screen(P.boxAsJava(point));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Tranaforms point from  screen to cartesian coordinate system.
         * @param point the <code>Point</code> to transform.
         * @return an tranformed <code>Point</code> instance.
         * @method screen2Cartesian
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "screen2Cartesian", {
            get: function() {
                return function(point) {
                    var value = delegate.screen2Cartesian(P.boxAsJava(point));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Makes map move to the specified geo position.
         * @param position the position on the map.
         * @method goToGeoPosition
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "goToGeoPosition", {
            get: function() {
                return function(position) {
                    var value = delegate.goToGeoPosition(P.boxAsJava(position));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Hits the selection on the specified point.
         * @param hitPoint the Point to hit.
         * @return an array of <code>SelectionEntry</code> elements.
         * @method hitSelection
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "hitSelection", {
            get: function() {
                return function(hitPoint) {
                    var value = delegate.hitSelection(P.boxAsJava(hitPoint));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelMap
         */
        Object.defineProperty(this, "focus", {
            get: function() {
                return function() {
                    var value = delegate.focus();
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();