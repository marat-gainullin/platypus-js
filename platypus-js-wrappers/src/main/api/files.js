/* global Java */

define(['invoke'], function (Invoke) {
    var ScriptsClass = Java.type('com.eas.script.Scripts');
    var FilesClass = Java.type('java.nio.file.Files');
    var Paths = Java.type('java.nio.file.Paths');
    var AsynchronousFileChannelClass = Java.type('java.nio.channels.AsynchronousFileChannel');
    var StandardOpenOptionClass = Java.type('java.nio.file.StandardOpenOption');
    var CopyOptionClass = Java.type('java.nio.file.StandardCopyOption');
    var LinkOptionClass = Java.type('java.nio.file.LinkOption');
    var ByteBufferClass = Java.type('java.nio.ByteBuffer');
    var ByteArrayOutputStreamClass = Java.type('java.io.ByteArrayOutputStream');
    var StringClass = Java.type('java.lang.String');
    var CharsetClass = Java.type('java.nio.charset.Charset');
    var EnumSetClass = Java.type('java.util.EnumSet');

    function pathsGet(aPathText) {
        return Paths.get(aPathText);
    }

    function readString(path, onSuccess, onFailure, encoding, startPosition) {
        try {
            var pos = arguments.length > 4 ? startPosition : 0;
            var pathToReadFrom = typeof path === 'string' ? pathsGet(path) : path;
            var readChannel = openRead(pathToReadFrom);
            readChannel.read(pos, function (readText) {
                readChannel.close();
                onSuccess(readText);
            }, function (e) {
                readChannel.close();
                onFailure(e);
            }, arguments.length > 3 ? encoding : 'utf-8');
        } catch (e) {
            Invoke.later(function () {
                onFailure(e);
            });
        }
    }

    this.readLines = function (path, onLineRead, onSuccess, onFailure, aEncoding, aDelimiter, startPosition) {
        try {
            var pathToReadFrom = typeof path === 'string' ? pathsGet(path) : path;
            var readChannel = openRead(pathToReadFrom);
            try {
                var encoding = arguments.length > 4 ? aEncoding : 'utf-8';
                var delimiter = arguments.length > 5 ? aDelimiter : /\n/;
                var charset = CharsetClass.forName(encoding);
                var buffer = ByteBufferClass.allocate(readChannel.read.bufferSize);
                var readPosition = arguments.length > 6 ? startPosition : 0;
                var head = null;
                var abort = false;
                function continueReading() {
                    readChannel.readChunk(buffer, readPosition, function (read) {
                        try {
                            if (read === -1) {
                                readChannel.close();
                                if (head !== null) {
                                    onLineRead(head);
                                }
                                onSuccess();
                            } else {
                                readPosition += read;
                                buffer.flip();
                                var decodedCharBuffer = charset.decode(buffer);
                                var decodedString = String(decodedCharBuffer);
                                var lines = decodedString.split(delimiter);
                                if (lines.length > 1) {
                                    if (onLineRead((head === null ? '' : head) + lines[0]) === false) {
                                        abort = true;
                                    }
                                    if (!abort) {
                                        for (var l = 1; l < lines.length - 1; l++) {
                                            if (onLineRead(lines[l]) === false) {
                                                abort = true;
                                                break;
                                            }
                                        }
                                        head = lines[lines.length - 1];
                                    }
                                } else {
                                    head = (head === null ? '' : head) + lines[0];
                                }
                                buffer.compact();
                                if (abort) {
                                    readChannel.close();
                                    onSuccess();
                                } else {
                                    continueReading();
                                }
                            }
                        } catch (e) {
                            readChannel.close();
                            onFailure(e);
                        }
                    }, onFailure);
                }
                continueReading();
            } catch (e) {
                Invoke.later(function () {
                    onFailure(e);
                });
                readChannel.close();
            }
        } catch (e) {
            Invoke.later(function () {
                onFailure(e);
            });
        }
    };

    function appendString(path, textToWrite, onSuccess, onFailure, aEncoding, openMethod) {
        try {
            var pathToWriteTo = typeof path === 'string' ? pathsGet(path) : path;
            var encoding = arguments.length > 4 ? aEncoding : 'utf-8';
            var opener = arguments.length > 5 ? openMethod : openCreate;
            writeString(path, textToWrite, onSuccess, onFailure, encoding, opener, FilesClass.size(pathToWriteTo));
        } catch (e) {
            Invoke.later(function () {
                onFailure(e);
            });
        }
    }

    function writeString(path, textToWrite, onSuccess, onFailure, aEncoding, openMethod, startPosition) {
        try {
            var pathToWriteTo = typeof path === 'string' ? pathsGet(path) : path;
            var encoding = arguments.length > 4 ? aEncoding : 'utf-8';
            var opener = arguments.length > 5 ? openMethod : openCreate;
            var writePosition = arguments.length > 6 ? startPosition : 0;
            var writeChannel = opener(pathToWriteTo);
            writeChannel.write(textToWrite, writePosition, function (written) {
                writeChannel.close();
                onSuccess(written);
            }, function (e) {
                writeChannel.close();
                onFailure(e);
            }, encoding);
        } catch (e) {
            Invoke.later(function () {
                onFailure(e);
            });
        }
    }

    function Channel(aDelegate) {

        var self = this;

        /**
         * Returns the current size of this channel's file.
         *
         * @return  The current size of this channel's file, measured in bytes
         *
         * @throws  ClosedChannelException
         *          If this channel is closed
         * @throws  IOException
         *          If some other I/O error occurs
         */
        this.size = function () {
            return aDelegate.size();
        };
        /**
         * Truncates this channel's file to the given size.
         *
         * <p> If the given size is less than the file's current size then the file
         * is truncated, discarding any bytes beyond the new end of the file.  If
         * the given size is greater than or equal to the file's current size then
         * the file is not modified. </p>
         *
         * @param  size
         *         The new size, a non-negative byte count
         *
         * @return  This file channel
         *
         * @throws  NonWritableChannelException
         *          If this channel was not opened for writing
         *
         * @throws  ClosedChannelException
         *          If this channel is closed
         *
         * @throws  IllegalArgumentException
         *          If the new size is negative
         *
         * @throws  IOException
         *          If some other I/O error occurs
         */
        this.truncate = function (size) {
            return new Channel(aDelegate.truncate(size));
        };
        /**
         * Forces any updates to this channel's file to be written to the storage
         * device that contains it.
         *
         * <p> If this channel's file resides on a local storage device then when
         * this method returns it is guaranteed that all changes made to the file
         * since this channel was created, or since this method was last invoked,
         * will have been written to that device.  This is useful for ensuring that
         * critical information is not lost in the event of a system crash.
         *
         * <p> If the file does not reside on a local device then no such guarantee
         * is made.
         *
         * <p> The {@code metaData} parameter can be used to limit the number of
         * I/O operations that this method is required to perform.  Passing
         * {@code false} for this parameter indicates that only updates to the
         * file's content need be written to storage; passing {@code true}
         * indicates that updates to both the file's content and metadata must be
         * written, which generally requires at least one more I/O operation.
         * Whether this parameter actually has any effect is dependent upon the
         * underlying operating system and is therefore unspecified.
         *
         * <p> Invoking this method may cause an I/O operation to occur even if the
         * channel was only opened for reading.  Some operating systems, for
         * example, maintain a last-access time as part of a file's metadata, and
         * this time is updated whenever the file is read.  Whether or not this is
         * actually done is system-dependent and is therefore unspecified.
         *
         * <p> This method is only guaranteed to force changes that were made to
         * this channel's file via the methods defined in this class.
         *
         * @param   metaData
         *          If {@code true} then this method is required to force changes
         *          to both the file's content and metadata to be written to
         *          storage; otherwise, it need only force content changes to be
         *          written
         *
         * @throws  ClosedChannelException
         *          If this channel is closed
         *
         * @throws  IOException
         *          If some other I/O error occurs
         */
        this.force = function (metaData) {
            aDelegate.force(metaData);
        };
        /**
         * Reads a sequence of bytes from this channel into the given buffer,
         * starting at the given file position.
         *
         * <p> This method initiates the reading of a sequence of bytes from this
         * channel into the given buffer, starting at the given file position. The
         * result of the read is the number of bytes read or {@code -1} if the given
         * position is greater than or equal to the file's size at the time that the
         * read is attempted.
         *
         * If the given file position is greater than the file's size at the time
         * that the read is attempted then no bytes are read.
         *
         * @param   dst
         *          The buffer into which bytes are to be transferred
         * @param   {Number} position
         *          The file position at which the transfer is to begin;
         *          must be non-negative
         * @param   {Function} onSuccess
         *          The success callback. Consumes the number of actually read bytes.
         * @param   {Function} onFailure
         *          The failure callback. Consumes the cause of failure.
         *
         * @throws  IllegalArgumentException
         *          If the position is negative or the buffer is read-only
         * @throws  NonReadableChannelException
         *          If this channel was not opened for reading
         */
        this.readChunk = function (dst, position, onSuccess, onFailure) {
            if (ScriptsClass.getContext() !== null) {
                ScriptsClass.getContext().incAsyncsCount();
            }// else { //not in server environment }
            aDelegate.read(dst, position, null, ScriptsClass.asCompletionHandler(onSuccess, onFailure));
        };

        /**
         * Read full content of the channel and converts the read data into a string.
         * Uses #read.encoding encoding to convert bytes into string.
         * The 'utf-8' encoding is used by default.
         * @param   {Number} position
         *          The file position at which the transfer is to begin;
         *          must be non-negative
         * @param   {Function} onSuccess
         *          The success callback. Consumes the number of actually read bytes.
         * @param   {Function} onFailure
         *          The failure callback. Consumes the cause of failure.
         * @param   {String} [encoding='utf-8']
         *          The byte sequence is transformed into string with this encoding.
         *          The default value is 'utf-8'.
         */
        this.read = function (position, onSuccess, onFailure, encoding) {
            var encodingName = arguments.length > 3 ? encoding : 'utf-8';
            var accumulator = new ByteArrayOutputStreamClass();
            var buffer = ByteBufferClass.allocate(self.read.bufferSize);
            var readPosition = position;
            try {
                function continueReading() {
                    buffer.clear();
                    self.readChunk(buffer, readPosition, function (read) {
                        if (read === -1) {
                            var data = new StringClass(accumulator.toByteArray(), encodingName);
                            onSuccess(String(data));
                        } else {
                            readPosition += read;
                            accumulator.write(buffer.array(), 0, read);
                            continueReading();
                        }
                    }, onFailure);
                }
                continueReading();
            } catch (e) {
                Invoke.later(function () {
                    onFailure(e);
                });
            }
        };
        this.read.bufferSize = 1024 * 8; // 8 Kb;

        /**
         * Writes a sequence of bytes to this channel from the given buffer, starting
         * at the given file position.
         *
         * If the given position is greater than the file's size, at the time that
         * the write is attempted, then the file will be grown to accommodate the new
         * bytes; the values of any bytes between the previous end-of-file and the
         * newly-written bytes are unspecified.
         *
         * @param   {ByteBuffer} src
         *          The buffer from which bytes are to be transferred
         * @param   {Number} position
         *          The file position at which the transfer is to begin;
         *          must be non-negative
         * @param   {Function} onSuccess
         *          The success callback. Consumes the number of actually written bytes.
         * @param   {Function} onFailure
         *          The failure callback. Consumes the cause of failure.
         *
         * @throws  IllegalArgumentException
         *          If the position is negative
         * @throws  NonWritableChannelException
         *          If this channel was not opened for writing
         */
        this.writeChunk = function (src, position, onSuccess, onFailure) {
            if (ScriptsClass.getContext() !== null) {
                ScriptsClass.getContext().incAsyncsCount();
            }// else { //not in server environment }
            aDelegate.write(src, position, null, ScriptsClass.asCompletionHandler(onSuccess, onFailure));
        };

        /**
         * Writes a string into a file.
         * 
         * @param   {String-or-ByteBuffer} src
         *          A string or a buffer from which bytes are to be transferred.
         * @param   {Number} position
         *          The file position at which the transfer is to begin;
         *          must be non-negative
         * @param   {Function} onSuccess
         *          The success callback. Consumes the number of actually written bytes.
         * @param   {Function} onFailure
         *          The failure callback. Consumes the cause of failure.
         * @param   {String} [encoding='utf-8']
         *          The string is transformed into byte sequence with this encoding.
         *          The default value is 'utf-8'.
         *
         * @throws  IllegalArgumentException
         *          If the position is negative
         * @throws  NonWritableChannelException
         *          If this channel was not opened for writing
         */
        this.write = function (src, position, onSuccess, onFailure, encoding) {
            var encodingName = arguments.length > 4 ? encoding : 'utf-8';
            var buffer = ByteBufferClass.wrap(src.toString().getBytes(encodingName));
            var writePosition = position;
            function continueWriting() {
                try {
                    self.writeChunk(buffer, writePosition, function (written) {
                        writePosition += written;
                        if (buffer.hasRemaining()) {
                            continueWriting();
                        } else {
                            onSuccess(writePosition);
                        }
                    }, onFailure);
                } catch (e) {
                    Invoke.later(function () {
                        onFailure(e);
                    });
                }
            }
            continueWriting();
        };

        /**
         * Closes this channel.
         *
         * <p> Any outstanding asynchronous operations upon this channel will
         * complete with the exception {@link AsynchronousCloseException}. After a
         * channel is closed, further attempts to initiate asynchronous I/O
         * operations complete immediately with cause {@link ClosedChannelException}.
         *
         * <p>  This method otherwise behaves exactly as specified by the {@link
         * Channel} interface.
         *
         * @throws  IOException
         *          If an I/O error occurs
         */
        this.close = function () {
            aDelegate.close();
        };
        /**
         * Tells whether or not this channel is open.
         *
         * @returns {Boolean} <tt>true</tt> if, and only if, this channel is open
         */
        this.isOpen = function () {
            return aDelegate.isOpen();
        };
    }

    function openWrite(aPath) {
        return new Channel(AsynchronousFileChannelClass.open(aPath, EnumSetClass.of(StandardOpenOptionClass.WRITE), ScriptsClass.getTasksExecutorIfPresent()));
    }
    function openTruncate(aPath) {
        return new Channel(AsynchronousFileChannelClass.open(aPath, EnumSetClass.of(StandardOpenOptionClass.WRITE, StandardOpenOptionClass.TRUNCATE_EXISTING), ScriptsClass.getTasksExecutorIfPresent()));
    }
    function openCreate(aPath) {
        return new Channel(AsynchronousFileChannelClass.open(aPath, EnumSetClass.of(StandardOpenOptionClass.WRITE, StandardOpenOptionClass.CREATE), ScriptsClass.getTasksExecutorIfPresent()));
    }
    function openCreateNew(aPath) {
        return new Channel(AsynchronousFileChannelClass.open(aPath, EnumSetClass.of(StandardOpenOptionClass.WRITE, StandardOpenOptionClass.CREATE_NEW), ScriptsClass.getTasksExecutorIfPresent()));
    }
    function openRead(aPath) {
        return new Channel(AsynchronousFileChannelClass.open(aPath, EnumSetClass.of(StandardOpenOptionClass.READ), ScriptsClass.getTasksExecutorIfPresent()));
    }

    var module = {
        /**
         * Reads all contents of a file and returns it as a string.
         * @param   {String-or-Path} path
         *          Name of the path or a Path instance of the file to read from.
         * @param   {Function} onSuccess
         *          The success callback. Consumes the read string.
         * @param   {Function} onFailure
         *          The failure callback. Consumes the cause of failure.
         * @param   {String} [encoding='utf-8']
         *          The byte sequence is transformed into string with this encoding.
         *          The default value is 'utf-8'.
         * @param   {Number} [startPosition=0]
         *          Position, the reading to start from.
         */
        read: function (path, onSuccess, onFailure, encoding) {},
        /**
         * Starts line by line reading of all contents of the channel.
         * Uses memory window of size near to read buffer size.
         * Closes the channel end of file is detected or if onSuccess callback
         * explicitly returns 'false'.
         * 
         * @param   {String-or-Path} path
         *          Name of the path or a Path instance of the file to read from.
         * @param {Function} onLineRead
         *                   On line read callback. Consumes every new line, read from the channel.
         *                   Returns 'false' if reading process should be aborted.
         * @param {Function} onSuccess
         *                   Success callback. Called upon end of reading process with any reason
         *                   (abort by onLineRead callback return status or end of file reached).
         *                   Consumes nothing.
         *                   Returns nothing.
         * @param {Function} onFailure
         *                   Failure callback. Consumes a failure reason.
         * @param {String}   [encoding='utf-8']
         * @param {RegExp} [delimiter='\n']
         *                   Delimiter of lines. Can be reguler expression or a string with regular expression.
         * @param {Number}   [startPosition=0]
         *          Position, the reading to start from.
         */
        readLines: function (path, onLineRead, onSuccess, onFailure, encoding, delimiter) {},
        /**
         * Writes a string to a file.
         * The string is transformed into byte sequence with #write.encoding encoding.
         * The default value of write.encoding is 'utf-8'.
         * @param   {String-or-Path} path
         *          Name of the path or a Path instance of the file to write to.
         * @param   {String} text
         *          The string to be written.
         * @param   {Function} onSuccess
         *          The success callback. Consumes the number of actually written bytes.
         * @param   {Function} onFailure
         *          The failure callback. Consumes the cause of failure.
         * @param   {String} [encoding='utf-8']
         *          The string is transformed into byte sequence with this encoding.
         *          The default value is 'utf-8'.
         * @param   {Function} [openMethod=Files.openCreate]
         *          Method to use for file channel openning.
         * @param   {Number} [startPosition=0]
         *          Position to start writing from.
         */
        write: function (path, text, onSuccess, onFailure, encoding, openMethod, startPosition) {},
        /**
         * Appends a string to a file.
         * The string is transformed into byte sequence with #write.encoding encoding.
         * The default value of write.encoding is 'utf-8'.
         * @param   {String-or-Path} path
         *          Name of the path or a Path instance of the file to write to.
         * @param   {String} text
         *          The string to be written.
         * @param   {Function} onSuccess
         *          The success callback. Consumes the number of actually written bytes.
         * @param   {Function} onFailure
         *          The failure callback. Consumes the cause of failure.
         * @param   {String} [encoding='utf-8']
         *          The string is transformed into byte sequence with this encoding.
         *          The default value is 'utf-8'.
         * @param   {Function} [openMethod=Files.openCreate]
         *          Method to use for file channel openning.
         */
        append: function (path, text, onSuccess, onFailure, encoding, openMethod) {},

        /**
         * Creates a new path object with paths relativize and resolve capabilities.
         * Path is projected from java.nio.file.Path. Se the documentation on it at:
         * <a href="http://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html">java.nio.file.Path Official documentation</a>
         * @param {type} aPathText Text, the new path object should be constructed upon.
         * @returns {object} Path object with relativize and resolve capabilities.
         */
        path: function (aPathText) {
            return {
                getFileSystem: function () {},
                isAbsolute: function () {},
                getRoot: function () {},
                getFileName: function () {},
                getParent: function () {},
                getNameCount: function () {},
                getName: function (index) {},
                subpath: function (beginIndex, endIndex) {},
                startsWith: function (other) {},
                endsWith: function (other) {},
                normalize: function () {},
                resolve: function (other) {},
                resolveSibling: function (other) {},
                relativize: function (other) {},
                toUri: function () {},
                toAbsolutePath: function () {},
                toRealPath: function (options) {},
                toFile: function () {}
            };
        },
        /**
         * Utility functions. This utilities are projecttion of java.nio.file.Files.
         * See documetation on it at:
         * <a href="http://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html">java.nio.file.Files Official documentation<a>
         */
        Utils: {
            copy: function (sourcePath, targetPath, copyOptions) {},
            createDirectory: function (dirPath) {},
            createDirectories: function (dirPath) {},
            createFile: function (path, attrs) {},
            createLink: function (linkPath, existingPath) {},
            createSymbolicLink: function (linkPath, targetPath, attrs) {},
            delete: function (path) {},
            deleteIfExists: function (path) {},
            exists: function (path, options) {},
            getLastModifiedTime: function (path, options) {},
            isDirectory: function (path, options) {},
            isExecutable: function (path) {},
            isHidden: function (path) {},
            isReadable: function (path) {},
            isRegularFile: function (path, options) {},
            isSameFile: function (path, path2) {},
            isSymbolicLink: function (path) {},
            isWritable: function (path) {},
            move: function (source, target, options) {},
            notExists: function (path, options) {},
            probeContentType: function (path) {},
            readSymbolicLink: function (link) {},
            size: function (path) {}
        },
        /**
         * An object that configures how to copy or move a file.
         *
         * <p> Objects of this type may be used with the {@link
         * Files#copy(Path,Path,CopyOption[]) Files.copy(Path,Path,CopyOption...)},
         * {@link Files#copy(java.io.InputStream,Path,CopyOption[])
         * Files.copy(InputStream,Path,CopyOption...)} and {@link Files#move
         * Files.move(Path,Path,CopyOption...)} methods to configure how a file is
         * copied or moved.
         *
         * <p> The {@link StandardCopyOption} enumeration type defines the
         * <i>standard</i> options.
         */
        CopyOption: {
            /**
             * Replace an existing file if it exists.
             */
            REPLACE_EXISTING: {},
            /**
             * Copy attributes to the new file.
             */
            COPY_ATTRIBUTES: {},
            /**
             * Move the file as an atomic file system operation.
             */
            ATOMIC_MOVE: {}
        },
        /**
         * Defines the options as to how symbolic links are handled.
         */
        LinkOption: {
            /**
             * Do not follow symbolic links.
             *
             * @see Files#getFileAttributeView(Path,Class,LinkOption[])
             * @see Files#copy
             * @see SecureDirectoryStream#newByteChannel
             */
            NOFOLLOW_LINKS: {}
        },
        /**
         * Buffer class for bytes level read and write operations.
         * It is a projecttion of java.nio.ByteBuffer.
         * See the <a href="http://docs.oracle.com/javase/8/docs/api/java/nio/ByteBuffer.html">official documentation</a>
         */
        ByteBuffer: {},
        /**
         * Opens file for write access.
         * 
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        openWrite: function (path) {
            return new Channel();
        },
        /**
         * If the file already exists and it is opened for {@link #WRITE}
         * access, then its length is truncated to 0. This option is ignored
         * if the file is opened only for {@link #READ} access.
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        openTruncate: function (path) {
            return new Channel();
        },
        /**
         * If the file is opened for {@link #WRITE} access then bytes will be written
         * to the end of the file rather than the beginning.
         *
         * <p> If the file is opened for write access by other programs, then it
         * is file system specific if writing to the end of the file is atomic.
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        openAppend: function (path) {
            return new Channel();
        },
        /**
         * Create a new file if it does not exist.
         * This option is ignored if the {@link #CREATE_NEW} option is also set.
         * The check for the existence of the file and the creation of the file
         * if it does not exist is atomic with respect to other file system
         * operations.
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        openCreate: function (path) {
            return new Channel();
        },
        /**
         * Create a new file, failing if the file already exists.
         * The check for the existence of the file and the creation of the file
         * if it does not exist is atomic with respect to other file system
         * operations.
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        openCreateNew: function (path) {
            return new Channel();
        },
        /**
         * Opens file for read access.
         * 
         * @param {Path} path a Path instance.
         * @returns {Channel} an async file channel.
         */
        openRead: function (path) {
            return new Channel();
        }
    };
    Object.defineProperty(module, "read", {
        value: readString
    });
    Object.defineProperty(module, "readLines", {
        value: readLines
    });
    Object.defineProperty(module, "write", {
        value: writeString
    });
    Object.defineProperty(module, "append", {
        value: appendString
    });
    Object.defineProperty(module, "path", {
        value: pathsGet
    });
    Object.defineProperty(module, "openWrite", {
        value: openWrite
    });
    Object.defineProperty(module, "openTruncate", {
        value: openTruncate
    });
    Object.defineProperty(module, "openCreate", {
        value: openCreate
    });
    Object.defineProperty(module, "openCreateNew", {
        value: openCreateNew
    });
    Object.defineProperty(module, "openRead", {
        value: openRead
    });
    Object.defineProperty(module, "Utils", {
        value: FilesClass
    });
    Object.defineProperty(module, "LinkOption", {
        value: LinkOptionClass
    });
    Object.defineProperty(module, "CopyOption", {
        value: CopyOptionClass
    });
    Object.defineProperty(module, "ByteBuffer", {
        value: ByteBufferClass
    });
    return module;
});