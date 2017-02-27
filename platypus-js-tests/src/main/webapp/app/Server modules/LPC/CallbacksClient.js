function LPCCallbacksTest(){
    
    this.execute = function(aOnSuccess, aOnFailure){
        var provider = new P.ServerModule('callback-provider');
        provider.execute(aOnSuccess, aOnFailure);
    };
    
}