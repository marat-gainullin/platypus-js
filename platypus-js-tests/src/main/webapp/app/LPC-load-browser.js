/* global P */

/**
 * 
 * @author mg
 * {global P}
 */
function LPC_load_browser() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    var maxRequests, requests = maxRequests = 100;
    
    form.btnRunIt.onActionPerformed = function (event) {
        var exepectedCalls = 1000000;
        var accounter = new P.ServerModule('Accounter');
        var dt = new Date();
        accounter.execute(exepectedCalls, function () {
            var dt1 = new Date();
            var t = dt1 - dt;
            P.Logger.info(exepectedCalls + ' LPC were performed in ' + t + ' ms. Throughput is ' + Math.round(exepectedCalls / t * 1000) + ' Hz');
            if(--requests > 0)
                form.btnRunIt.onActionPerformed(event);
            else
                requests = maxRequests;
        }, function (e) {
            P.Logger.severe(e);
        });
    };
}
