function StructuredCopyTestClient() {
    this.execute = function (aOnSuccess) {
        require('rpc', function (Rpc) {
            var copyProvider = new Rpc.Proxy('Server modules/Structured copy/CopyProvider');
            copyProvider.startCopyTest(function(){
                aOnSuccess();
            });
        });
    };
}