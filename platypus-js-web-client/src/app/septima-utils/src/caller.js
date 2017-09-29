define(function () {
    function extractFileName(aFrame) {
        if (aFrame) {
            // This is for Chrome stack traces
            var matched = aFrame.match(/(https?:\/\/.+):\d+:\d+/);
            if (matched) {
                return matched[1];
            } else {
                matched = aFrame.match(/(file:\/\/.+):\d+:\d+/);
                if (matched)
                    return matched[1];
                else
                    return null;
            }
        } else {
            return null;
        }
    }

    function lookupJsFile(exception) {
        var calledFromFile = null;
        var stack = exception.stack.split('\n');
        var firstFileName = extractFileName(stack[1]);// On Chrome the first line is a error text
        if (firstFileName) {
            for (var frameIdx = 1; frameIdx < stack.length; frameIdx++) {
                var fileName = extractFileName(stack[frameIdx]);
                if (fileName && fileName !== firstFileName) {
                    calledFromFile = fileName;
                    var lastQuestionIndex = calledFromFile.lastIndexOf('?');// case of cache busting
                    return lastQuestionIndex !== -1 ? calledFromFile.substring(0, lastQuestionIndex) : calledFromFile;
                }
            }
        }
        return calledFromFile;
    }

    function lookupDir(calledFromFile) {
        if (calledFromFile) {
            var lastSlashIndex = calledFromFile.lastIndexOf('/');
            return calledFromFile.substring(0, lastSlashIndex);
        } else {
            return null;
        }
    }
    var module = {};
    Object.defineProperty(module, 'lookupJsFile', {
        get: function () {
            return lookupJsFile;
        }
    });
    Object.defineProperty(module, 'lookupDir', {
        get: function () {
            return lookupDir;
        }
    });
    return module;
});