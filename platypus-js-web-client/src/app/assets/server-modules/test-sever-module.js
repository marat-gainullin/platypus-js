/**
 * @public 
 */
define(['invoke'], function (Invoke) {
    function EchoServerModule() {
        this.echo = function (a, b, c, d, onSuccess, onFailure) {
            var result = a + ' - ' + b + ' - ' + c + ' - ' + d;
            Invoke.later(function () {
                onSuccess(result);
            });
        };
    }
    return EchoServerModule;
});