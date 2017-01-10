/**
 * @public
 * @param {type} Lpc
 * @returns {CallbacksProvider_L4.mc}
 */
define('callback-provider', 'rpc', function(Lpc){
    var callbackConsumerProxy = new Lpc.Proxy('callback-consumer');
    function mc(){
        this.execute = function(aOnSuccess, aOnFailure){
            callbackConsumerProxy.consume(aOnSuccess, aOnFailure);
        };
    }
    return mc;
});