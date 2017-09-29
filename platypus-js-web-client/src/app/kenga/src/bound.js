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

    function observeElements(target, propListener) {
        function subscribe(aData, aListener) {
            var nHandler = listen(aData, aListener);
            if (nHandler) {
                return nHandler;
            }
            return null;
        }
        var subscribed = [];
        target.forEach(function (item) {
            var remover = subscribe(item, propListener);
            if (remover) {
                subscribed.push(remover);
            }
        });
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
    function PathComparator(field, ascending) {
        if(arguments.length < 2)
            ascending = true;
        Object.defineProperty(this, 'ascending', {
            get: function(){
                return ascending;
            }
        });
        function oCompare(od1, od2){
            if(od1 == null && od2 == null)
                return 0;
            else if(od1 == null)
                return 1;
            else if(od2 == null)
                return -1;
            if(typeof od1 === 'string')
                od1 = od1.toLowerCase();	
            if(typeof od2 === 'string')
                od2 = od2.toLowerCase();	
            if(od1 === od2)
                return 0;
            else if(od1 > od2)
                return 1;
            else
                return -1;
        }

        function compare(o1, o2) {
            var oData1 = Bound.getPathData(o1, field);
            var oData2 = Bound.getPathData(o2, field);
            var res = oCompare(oData1, oData2);
            return ascending ? res : -res;
        }
        Object.defineProperty(this, 'compare', {
            get: function(){
                return compare;
            }
        });
    }
    Object.defineProperty(Bound, 'PathComparator', {
        get: function () {
            return PathComparator;
        }
    });
    Object.defineProperty(Bound, 'observeElements', {
        get: function () {
            return observeElements;
        }
    });
    Object.defineProperty(Bound, 'listen', {
        get: function () {
            return listen;
        }
    });
    Object.defineProperty(Bound, 'getPathData', {
        get: function () {
            return getPathData;
        }
    });
    Object.defineProperty(Bound, 'setPathData', {
        get: function () {
            return setPathData;
        }
    });
    Object.defineProperty(Bound, 'observePath', {
        get: function () {
            return observePath;
        }
    });
    return Bound;
});