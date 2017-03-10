define(['files'], function (Files) {
    return function () {
        this.execute = function (onSuccess, onFailure) {
            var path = Files.path('write-truncate-read-all-test.txt');
            function failured(e) {
                Files.Utils.delete(path);
                onFailure(e);
            }
            var data = [];
            for (var i = 0; i < 50; i++) {
                data.push('Line written through java.nio.file # ' + i);
            }
            var writtenText1 = data.join('\n');
            Files.write(path, writtenText1, function (written) {
                var writtenText2 = data.join('\n');
                Files.write(path, writtenText2, function (written) {
                    Files.read(path, function (readText) {
                        if (readText !== writtenText2) {
                            failured('readText !== writtenText2 violation');
                        } else {
                            Files.Utils.delete(path);
                            onSuccess();
                        }
                    }, failured, 'utf-8');
                }, failured, 'utf-8', Files.openTruncate);
            }, failured, 'utf-8', Files.openCreateNew);
        };
    };
});