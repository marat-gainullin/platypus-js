define('callback-consumer', [], function(){
    function mc(){
        this.consume = function(aOnSuccess, aOnFailure){
            aOnSuccess();
        };
    }
    return mc;
});