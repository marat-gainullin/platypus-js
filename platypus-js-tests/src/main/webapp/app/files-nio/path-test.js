define(['files'], function(files){
    return function(){
        this.execute = function(onSuccess, onFailure){
            var path = files.path('path/to/file');
            if(path.resolve && path.relativize){
                onSuccess();
            } else{
                onFailure('path resolve / relativize violation');
            }
        };
    };
});