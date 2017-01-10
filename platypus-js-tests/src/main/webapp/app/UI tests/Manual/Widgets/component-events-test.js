/**
 * 
 * @author mg
 */
function component_events_test() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function() {
        form.show();
    };

    function logEvent(aName, event) {
        P.Logger.info(aName + ' on ' + event.source.name);
    }

    function register(comp) {
        comp.onActionPerformed = function(event) {
            logEvent('onActionPerformed', event);
        };
        //
        comp.onMouseExited = function(event) {
            logEvent('onMouseExited', event);
        };
        comp.onMouseClicked = function(event) {
            if (event.clickCount > 1)
                logEvent('onDoubleClicked', event);
            else
                logEvent('onMouseClicked', event);
        };
        comp.onMousePressed = function(event) {
            logEvent('onMousePressed', event);
        };
        comp.onMouseReleased = function(event) {
            logEvent('onMouseReleased', event);
        };
        comp.onMouseEntered = function(event) {
            logEvent('onMouseEntered', event);
        };
        comp.onMouseWheelMoved = function(event) {
            logEvent('onMouseWheelMoved', event);
        };
        comp.onMouseDragged = function(event) {
            logEvent('onMouseDragged', event);
        };
        comp.onMouseMoved = function(event) {
            logEvent('onMouseMoved', event);
        };
        //
        comp.onComponentResized = function(event) {
            logEvent('onComponentResized', event);
        };
        comp.onComponentMoved = function(event) {
            logEvent('onComponentMoved', event);
        };
        comp.onComponentShown = function(event) {
            logEvent('onComponentShown', event);
        };
        comp.onComponentHidden = function(event) {
            logEvent('onComponentHidden', event);
        };
        comp.onComponentAdded = function(event) {
            logEvent('onComponentAdded', event);
        };
        comp.onComponentRemoved = function(event) {
            logEvent('onComponentRemoved', event);
        };
        //
        comp.onFocusGained = function(event) {
            logEvent('onFocusGained', event);
        };
        comp.onFocusLost = function(event) {
            logEvent('onFocusLost', event);
        };
        //
        comp.onKeyTyped = function(event) {
            logEvent('onKeyTyped', event);
        };
        comp.onKeyPressed = function(event) {
            logEvent('onKeyPressed', event);
        };
        comp.onKeyReleased = function(event) {
            logEvent('onKeyReleased', event);
        };
        if (comp.children) {
            comp.children.forEach(function(aChild) {
                register(aChild);
            });
        }
    }

    form.view.children.forEach(function(aComp) {
        register(aComp);
    });

    function invertVisible(aComp) {
        aComp.visible = !aComp.visible;
        if (aComp.children) {
            aComp.children.forEach(function(aChild) {
                invertVisible(aChild);
            });
        }
    }

    form.view.onMouseClicked = function(event) {
        if (event.clickCount > 1 && event.controlDown) {
            form.view.children.forEach(function(aComp) {
                invertVisible(aComp);
            });
        }
    };

    function touchSize(aComp) {
        aComp.width++;
        aComp.height++;
        if (aComp.children) {
            aComp.children.forEach(function(aChild) {
                touchSize(aChild);
            });
        }
    }

    form.view.onMouseClicked = function(event) {
        if (event.clickCount > 1) {
            if (event.controlDown) {
                form.view.children.forEach(function(aComp) {
                    invertVisible(aComp);
                });
            } else if (event.altDown) {
                form.view.children.forEach(function(aComp) {
                    touchSize(aComp);
                });
            }
        }
    };
    
    form.view.onComponentAdded = function(event){
        P.Logger.info("componentAdded on " + event.source.name);
    };
    
    form.view.onComponentRemoved = function(event){
        P.Logger.info("componentRemoved on " + event.source.name);
    };    
}
