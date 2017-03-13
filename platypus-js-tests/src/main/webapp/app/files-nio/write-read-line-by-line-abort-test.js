define(['files'], function (Files) {
    return function () {
        this.execute = function (onSuccess, onFailure) {
            var path = Files.path('write-read-line-by-line-abort-test.txt');
            function failured(e) {
                Files.Utils.deleteIfExists(path);
                onFailure(e);
            }
            var data = [];
            for (var i = 0; i < 100; i++) {
                data.push('Line written through java.nio.file # ' + i);
            }
            Files.write(path, data.join('\n'), function (written) {
                var lines = [];
                Files.readLines(path, function(line){
                    lines.push(line);
                    return lines.length < 50;
                }, function(){
                    if(lines.length !== 50){
                        failured('written and read aborted data violation');
                    } else {
                        Files.Utils.delete(path);
                        onSuccess();
                    }
                }, failured, 'utf-8', /\n/);
            }, failured, 'utf-8', Files.openCreateNew);
        };
    };
});