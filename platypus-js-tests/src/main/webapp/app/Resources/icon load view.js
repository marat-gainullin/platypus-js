/**
 * 
 * @author mg
 */
function icon_load() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    model.requery(function () {
    });

    form.btnTestFromInet.onActionPerformed = function (event) {
        form.btnTestFromInet.icon = null;
        if (P.agent !== P.HTML5) {
            var loadedFormInet = P.Icon.load('http://lh6.googleusercontent.com/-UXdNdTTGgXg/AAAAAAAAAAI/AAAAAAAAAAA/b3u7m4nqaNo/s32-c/photo.jpg');
            form.btnTestFromInet.icon = loadedFormInet;
        }
        P.Icon.load('http://lh6.googleusercontent.com/-UXdNdTTGgXg/AAAAAAAAAAI/AAAAAAAAAAA/b3u7m4nqaNo/s32-c/photo.jpg', function (loadedFormInet) {
            form.btnTestFromInet.icon = loadedFormInet;
        }, function (e) {
            P.Logger.info(e);
        });
//        form.btnTestFromInet.icon = loadedFormInet;
    };
    form.btnTesFromLocal.onActionPerformed = function (event) {
        form.btnTesFromLocal.icon = null;
        if (P.agent !== P.HTML5) {
            var loadedFormInet = P.Icon.load('Resources/wrench.png');
            form.btnTesFromLocal.icon = loadedFormInet;
        }
        P.Icon.load('Resources/wrench.png', function (loadedLocal) {
            form.btnTesFromLocal.icon = loadedLocal;
        }, function (e) {
            P.Logger.info(e);
        });
//        form.btnTesFromLocal.icon = loadedFormInet;

    };
}
