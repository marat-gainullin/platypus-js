define(['files'], function(Files){
    return function(){
        this.execute = function(onSuccess, onFailure){
            var path = Files.path('path/to/file');
            if(path.resolve && path.relativize){
                onSuccess();
            } else{
                onFailure('path resolve / relativize violation');
            }
        };
    };
});