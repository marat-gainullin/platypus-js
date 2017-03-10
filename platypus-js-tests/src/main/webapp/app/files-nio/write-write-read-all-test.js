define(['files'], function (Files) {
    return function () {
        this.execute = function (onSuccess, onFailure) {
            var path = Files.path('write-append-read-all-test.txt');
            function failured(e) {
                Files.Utils.deleteIfExists(path);
                onFailure(e);
            }
            var data = [];
            for (var i = 0; i < 50; i++) {
                data.push('Line written through java.nio.file # ' + i);
            }
            var writtenText1 = data.join('\n');
            Files.write(path, writtenText1, function (written) {
                var writtenText2 = 'Line overwritten through java.nio.file';
                Files.write(path, writtenText2, function (written) {
                    Files.read(path, function (readText) {
                        data[0] = writtenText2;
                        if (readText !== data.join('\n')) {
                            failured("readText !== data.join('\n') violation");
                        } else {
                            Files.Utils.delete(path);
                            onSuccess();
                        }
                    }, failured, 'utf-8');
                }, failured, 'utf-8', Files.openWrite, 0);
            }, failured, 'utf-8', Files.openCreateNew, 0);
        };
    };
});