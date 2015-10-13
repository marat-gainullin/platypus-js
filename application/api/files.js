define(function(){
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

    var module = {};
    Object.defineProperty(module, "read", {
        value: readString
    });
    Object.defineProperty(module, "write", {
        value: writeString
    });
    return module;
});