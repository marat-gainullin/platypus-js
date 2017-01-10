/* global P */

/**
 * 
 * @author mg
 * {global P}
 */
function network_load_browser() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    model.requery(function () {
        // TODO : place your code here
    });

    form.btnRunIt.onActionPerformed = function (event) {
        var m = new P.ServerModule('PublicWorker');
        var errors = [];
        var exepectedCalls = 3000;
        var calls = 0;
        var dt = new Date();
        function complete() {
            if (++calls === exepectedCalls) {
                form.btnRunIt.enabled = true;
                if (errors.length > 0)
                    P.Logger.severe(errors.join('\n'));
                else {
                    var dt1 = new Date();
                    var t = dt1 - dt;
                    P.Logger.info(exepectedCalls + ' requests were performed in ' + t + 'ms. Throughput is ' + (exepectedCalls / t * 1000) + 'Hz');
                }
            }
        }
        form.btnRunIt.enabled = false;
        for (var i = 0; i < exepectedCalls; i++) {
            m.execute(i, function (aRes) {
                complete();
            }, function (e) {
                errors.push(e);
                complete();
            });
        }
    };
}
