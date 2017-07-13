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
        this.failureEcho = function (a, b, c, d, onSuccess, onFailure) {
            var result = a + ' - ' + b + ' - ' + c + ' - ' + d;
            Invoke.later(function () {
                onFailure({
                    calculated: result,
                    description: 'Errors from server modules should be JSON-ed'
                });
            });
        };
    }
    return EchoServerModule;
});