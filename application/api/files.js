define(function () {
    var FileClass = Java.type("java.io.File");
    var FileUtilsClass = Java.type("com.eas.util.FileUtils");

    function readString(aFileName, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        return FileUtilsClass.readString(new FileClass(aFileName), encoding);
    }

    function writeString(aFileName, aText, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        FileUtilsClass.writeString(new FileClass(aFileName), aText, encoding);
    }

    var module = {
        /**
         * Reads all contents of a file and returns it as a string.
         * @param {String} aFileName file absolute path.
         * @param {String} aEncoding Encoding name (e.g. "utf-8")
         * @returns String read from file.
         */
        read: function () {},
        /**
         * Writes a string contents of a file.
         * @param {String} aFileName file absolute path.
         * @param {type} aText String to write to file.
         * @param {String} aEncoding Encoding name (e.g. "utf-8")
         * @returns {undefined}.
         */
        write: function () {}
    };
    Object.defineProperty(module, "read", {
        value: readString
    });
    Object.defineProperty(module, "write", {
        value: writeString
    });
    return module;
});