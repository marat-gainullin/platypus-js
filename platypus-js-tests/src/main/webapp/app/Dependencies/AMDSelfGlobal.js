function AMDSelfTest(){
    
    this.execute = function(onSuccess, onFailure){
        AMDSelfTest.onAMDSelfTestRequired = function(){
            AMDSelfTest.required++;
        };
        require('Dependencies/AMD self require', function(){
        }, onFailure);
        P.invokeDelayed(2000, function(){
            if(AMDSelfTest.required === 1)
                onSuccess();
            else
                onFailure('AMDSelfTest.required violation');
        });
    };
}
AMDSelfTest.required = 0;