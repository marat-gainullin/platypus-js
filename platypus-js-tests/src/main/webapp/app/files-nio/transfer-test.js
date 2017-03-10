define(['files', 'invoke'], function (Files, Invoke) {
    return function () {
        this.execute = function (onSuccess, onFailure) {
            var path = Files.path('transfer-test-source.txt');
            function writeFailured(e) {
                Files.Utils.deleteIfExists(path);
                onFailure(e);
            }
            var data = [];
            for (var i = 0; i < 50; i++) {
                data.push('Line written through java.nio.file # ' + i);
            }
            var writtenText = data.join('\n');
            Files.write(path, writtenText, function (written) {
                try {
                    var pathToReadFrom = Files.path('transfer-test-source.txt');
                    var pathToWriteTo = Files.path('transfer-test-target.txt');
                    var buffer = Files.ByteBuffer.allocate(13); // 13 bytes
                    var readChannel = Files.openRead(pathToReadFrom);
                    var writeChannel = Files.openCreateNew(pathToWriteTo);
                    var readPosition = 0;
                    var writePosition = 0;
                    function transferFailured(e) {
                        readChannel.close();
                        Files.Utils.deleteIfExists(pathToReadFrom);
                        writeChannel.close();
                        Files.Utils.deleteIfExists(pathToWriteTo);
                        onFailure(e);
                    }
                    function continueTransfer() {
                        readChannel.readChunk(buffer, readPosition, function (read) {
                            if (read !== -1) {
                                readPosition += read;
                            }
                            // prepare for reading from buffer while writing to target
                            buffer.flip();
                            // process
                            writeChannel.writeChunk(buffer, writePosition, function (written) {
                                writePosition += written;
                                // prepare the buffer to the next read
                                if (read !== -1 || buffer.hasRemaining()) {
                                    buffer.compact();
                                    continueTransfer();
                                } else {
                                    readChannel.close();
                                    writeChannel.close();
                                    Files.read(pathToWriteTo, function (justTransferredText) {
                                        if (justTransferredText !== writtenText) {
                                            transferFailured('justTransferredText !== writtenText violation');
                                        } else {
                                            Files.Utils.deleteIfExists(pathToReadFrom);
                                            Files.Utils.deleteIfExists(pathToWriteTo);
                                            onSuccess();
                                        }
                                    }, transferFailured);
                                }
                            }, transferFailured);
                        }, transferFailured);
                    }
                    continueTransfer();
                } catch (e) {
                    Invoke.later(function () {
                        onFailure(e);
                    });
                }
            }, writeFailured, 'utf-8', Files.openCreateNew);
        };
    };
});