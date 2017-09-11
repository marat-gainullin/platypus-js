define(function () {
    var addListenerName = "-platypus-listener-add-func";
    var removeListenerName = "-platypus-listener-remove-func";

    function listen(aTarget, aListener) {
        var addListener = aTarget[addListenerName];
        if (addListener) {
            addListener(aListener);
            return function () {
                aTarget[removeListenerName](aListener);
            };
        } else {
            return null;
        }
    }

    function getPathData(anElement, aPath) {
        if (anElement && aPath) {
            var target = anElement;
            var path = aPath.split('.');
            var propName = path[0];
            for (var i = 1; i < path.length; i++) {
                var target = target[propName];
                if (!target) {
                    propName = null;
                } else
                    propName = path[i];
            }
            if (propName) {
                return target[propName];
            } else
                return null;
        } else
            return null;
    }

    function setPathData(anElement, aPath, aValue) {
        if (aPath) {
            var target = anElement;
            var path = aPath.split('.');
            var propName = path[0];
            for (var i = 1; i < path.length; i++) {
                var target = target[propName];
                if (!target) {
                    propName = null;
                } else {
                    propName = path[i];
                }
            }
            if (propName) {
                target[propName] = aValue;
            }
        }
    }

    function observeElements(aTarget, aPropListener) {
        function subscribe(aData, aListener) {
            var nHandler = listen(aData, aListener);
            if (nHandler) {
                return nHandler;
            }
            return null;
        }
        var subscribed = [];
        for (var i = 0; i < aTarget.length; i++) {
            var remover = subscribe(aTarget[i], aPropListener);
            if (remover) {
                subscribed.push(remover);
            }
        }
        return {
            unlisten: function () {
                subscribed.forEach(function (aEntry) {
                    aEntry();
                });
            }
        };
    }

    function observePath(aTarget, aPath, aPropListener) {
        function subscribe(aData, aListener, aPropName) {
            var listenReg = listen(aData, function (aChange) {
                if (!aPropName || aChange.propertyName === aPropName) {
                    aListener(aChange);
                }
            });
            if (listenReg) {
                return listenReg;
            } else {
                return null;
            }
        }
        var subscribed = [];
        function listenPath() {
            subscribed = [];
            var data = aTarget;
            var path = aPath.split('.');
            for (var i = 0; i < path.length; i++) {
                var propName = path[i];
                var listener = i === path.length - 1 ? aPropListener : function (evt) {
                    subscribed.forEach(function (aEntry) {
                        aEntry();
                    });
                    listenPath();
                    var pathDatum = getPathData(aTarget, aPath);
                    aPropListener({source: aTarget, propertyName: aPath, oldValue: pathDatum, newValue: pathDatum});
                };
                var cookie = subscribe(data, listener, propName);
                if (cookie) {
                    subscribed.push(cookie);
                    if (data[propName])
                        data = data[propName];
                    else
                        break;
                } else {
                    break;
                }
            }
        }
        if (aTarget) {
            listenPath();
        }
        return {
            unlisten: function () {
                subscribed.forEach(function (removeSubscriber) {
                    removeSubscriber();
                });
            }
        };
    }

    function Bound() {
        var self = this;
        
        var data = null;
        var path = null;

        var settingToWidget = false;
        function toWidget(datum) {
            if (!settingToData) {
                settingToWidget = true;
                try {
                    self.value = datum;
                } finally {
                    settingToWidget = false;
                }
            }
        }

        var settingToData = false;
        function toData(datum) {
            if (!settingToWidget) {
                settingToData = true;
                try {
                    setPathData(data, path, datum);
                } finally {
                    settingToData = false;
                }
            }
        }

        this.addValueChangeHandler(function (evt) {
            toData(evt.newValue);
        });

        var boundReg = null;
        function unbind() {
            if (boundReg) {
                boundReg.unlisten();
                boundReg = null;
            }
        }

        function bind() {
            if (data && path) {
                boudReg = observePath(data, path, function (evt) {
                    toWidget(evt.newValue);
                });
                toWidget(getPathData(data, path));
            } else {
                self.value = null;
            }
        }

        function rebind() {
            unbind();
            bind();
        }

        Object.defineProperty(this, 'data', {
            get: function () {
                return data;
            },
            set: function (aValue) {
                if (data !== aValue) {
                    data = aValue;
                    rebind();
                }
            }
        });
        Object.defineProperty(this, 'field', {
            get: function () {
                return path;
            },
            set: function (aValue) {
                if (path !== aValue) {
                    path = aValue;
                    rebind();
                }
            }
        });
    }
    Object.defineProperty(Bound, 'observeElements', {
        get: function(){
            return observeElements;
        }
    });
    Object.defineProperty(Bound, 'listen', {
        get: function(){
            return listen;
        }
    });
    Object.defineProperty(Bound, 'getPathData', {
        get: function(){
            return getPathData;
        }
    });
    Object.defineProperty(Bound, 'setPathData', {
        get: function(){
            return setPathData;
        }
    });
    Object.defineProperty(Bound, 'observePath', {
        get: function(){
            return observePath;
        }
    });
    return Bound;
});