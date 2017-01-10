/**
 * 
 * @author mg 
 * @constructor
 */
function IconLoadTest() {
    var self = this, model = P.loadModel(this.constructor.name);
    var btn = new P.Button();

    var asyncLoaded = 0;
    self.execute = function (aOnSuccess) {
        function complete() {
            if (++asyncLoaded === (P.agent === P.HTML5 ? 3 : 4))
                aOnSuccess();
        }

        var isCompatible;
        if (P.agent === P.HTML5) {
            P.Icon.load('Resources/resource load.model', function (aLoaded) {
                //it cannot be loaded by Html5
                throw 'loaded.icon violation types incompatible 3';
            }, function (e) {
                complete();
            });
        } else {
            // sync resources loading test       
            var loadedIcon = P.Icon.load('Resources/wrench.png');
            try {
                btn.icon = loadedIcon;
            } catch (e) {
                throw 'loaded.icon violation 1';
            }
            var loadedResource = P.Resource.load('Resources/resource load.model');

            try {
                btn.icon = loadedResource;
                isCompatible = true;
            } catch (e) {
                //This is right behaviour - types are incompatible 
                isCompatible = false;
            }
            if (isCompatible) {
                throw 'loaded.icon violation types incompatible 1';
            }

            try {
                var loadedResource = P.Icon.load('Resources/resource load.model');
                isCompatible = true;
            } catch (ex) {
                //This is right behaviour - we have loaded a non image resource
                isCompatible = false;
            }
            if (isCompatible) {
                throw 'loaded.icon violation types incompatible 2';
            }

            var loadedFormInet = P.Icon.load('http://lh6.googleusercontent.com/-UXdNdTTGgXg/AAAAAAAAAAI/AAAAAAAAAAA/b3u7m4nqaNo/s32-c/photo.jpg');
            try {
                btn.icon = loadedIcon;
            } catch (e) {
                throw 'loaded.icon violation 2';
            }
            isCompatible = false;
            P.Icon.load('Resources/resource load.model', function (aLoaded) {
                //it cannot be loaded
                isCompatible = true;
            }, function (e) {
                isCompatible = false;
                complete();
            });
            P.Resource.load('Resources/wrench.png', function (aLoaded) {
                try {
                    btn.icon = aLoaded;
                    isCompatible = true;
                } catch (e) {
                    isCompatible = false;
                }
                if (isCompatible) {
                    throw 'loaded.icon violation types incompatible 4';
                }
                complete();
            }, function (e) {
                P.Logger.severe(e);
            });
        }

        P.Icon.load('Resources/wrench.png', function (aLoaded) {
            btn.icon = aLoaded;
            complete();
        }, function (e) {
            P.Logger.severe(e);
        });

        P.Icon.load('http://lh6.googleusercontent.com/-UXdNdTTGgXg/AAAAAAAAAAI/AAAAAAAAAAA/b3u7m4nqaNo/s32-c/photo.jpg', function (aLoaded) {
            btn.icon = aLoaded;
            complete();
        }, function (e) {
            P.Logger.severe(e);
        });
    };
}
