define('invoke', function(invoke){
    function mc(){
        this.copyTest = function(aData, aOnSuccess){
            invoke.later(function(){
                aOnSuccess(aData);
            });
        };
    }
    return mc;
});