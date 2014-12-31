var PAdapterClass = Java.type("com.eas.client.scripts.PropertyChangeListenerJSAdapter");

function subscribe(aData, aListener) {
    if (aData.unwrap) {
        var target = aData.unwrap();
        if (target.addPropertyChangeListener) {
            var adapter = new PAdapterClass(aListener);
            target.addPropertyChangeListener(adapter);
            return adapter;
        }
    }
    return null;
}

function unsubscribe(aData, aListener) {
    if (aData.unwrap) {
        var target = aData.unwrap();
        if (target.removePropertyChangeListener) {
            target.removePropertyChangeListener(aListener);
        }
    }
}


function listen(aTarget, aPath, aListener) {
    var subscribed = [];
    function listenPath() {
        subscribed = [];
        var data = aTarget;
        var path = aPath.split(".");
        for (var i = 0; i < path.length; i++) {
            var propName = path[i];
            var listener = i === path.length - 1 ? aListener : function (evt) {
                subscribed.forEach(function (aEntry) {
                    unsubscribe(aEntry.target, aEntry.subscribed);
                });
                listenPath();
                aListener(evt);
            };
            var cookie = subscribe(data, listener, propName);
            if (cookie) {
                subscribed.push({'target': data, 'subscribed': cookie});
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
            subscribed.forEach(function (aEntry) {
                unsubscribe(aEntry.target, aEntry.subscribed);
            });
        }
    };
}

if (cookie) {
    cookie.unlisten();
    cookie = null;
}
if (data && field)
    cookie = P.listen(data, field, function (evt) {
    });