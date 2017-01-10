/**
 * 
 * @author mg
 * @module
 */
function UIGlobalScopeTest() {
    var self = this, model = P.loadModel(this.constructor.name);

    P.Logger.config("config test message");
    P.Logger.info("info test message");
    P.Logger.warning("warning test message");
    P.Logger.severe("severe test message");
    P.Logger.fine("fine test message");
    P.Logger.finer("finer test message");
    P.Logger.finest("finest test message");
    var k1 = P.VK_BACKSPACE;
    var k2 = P.VK_ENTER;
    var k3 = P.VK_ALT;
    var defaultDir = P.selectDirectory();
    P.selectFile(defaultDir, function(aSelected) {
        P.Logger.info("selected file: " + aSelected);
        var read = P.readString(aSelected);
    });
    P.selectDirectory(defaultDir, function(aSelected) {
        P.Logger.info("selected directory: " + aSelected);
    });
    P.selectColor("Choose some color", null, function(aSelected) {
        P.Logger.info("selected color: " + aSelected);
    });
    var resourceWritten = "UI-tests/sample-platypus-out.txt";
    P.writeString(P.Resource.applicationPath + "/app/" + resourceWritten, "sample-content", 'utf-8');
    //
    P.Logger.info("P.Form.shown.length: " + P.Form.shown.length);
    var mrForm = P.Form.getShownForm("ManualRunner");
    P.Logger.info("<Form>.view.constructor: " + mrForm.view.constructor);
    P.Form.onChange = function() {
        P.Logger.info("P.Form.shown.length: " + P.Form.shown.length);
        alert("alert test");
        var res = confirm("confirm test");
        var provided = prompt("provide some text", "already typed");
        provided = null;
        P.msgBox("msgBox test", "msgBox -title-");
        P.error("error test");
        P.warn("warn test");
    };
    mrForm.close();
    var bold = P.FontStyle.BOLD;
    var italic = P.FontStyle.ITALIC;
    var bold_italic = P.FontStyle.BOLD_ITALIC;
    var normal = P.FontStyle.NORMAL;
    var md51 = P.MD5Generator.generate("sample-text-data");
    var md52 = P.MD5.generate("sample-text-data");
    if (md51 !== md52)
        throw 'md5 mismatch';
    var id1 = P.IDGenerator.genID();
    if (!id1)
        throw 'id mismatch 1';
    var id2 = P.ID.generate();
    if (!id2)
        throw 'id mismatch 2';
    if (id1 === id2)
        throw 'id mismatch 3';
    var cursor1 = P.Cursor.TEXT;
    var cursor2 = P.Cursor.NE_RESIZE;
    var cursor3 = P.Cursor.MOVE;
    var cursor4 = P.Cursor.HAND;

    var color1 = P.Color.MAGENTA;
    var color2 = P.Color.lightGray;
    var color3 = P.Color.RED;
    var color4 = P.Color.yellow;
    var color5 = P.Color.BLACK;
    var color6 = P.Color.blue;
    var color7 = new P.Color(0, 0, 0xff, 0xff);
    if (color6.red !== color7.red || color6.green !== color7.green || color6.blue !== color7.blue || color6.alpha !== color7.alpha)
        throw 'color mismatch';

    var loaded = P.Resource.load("UI-tests/point.png");
    var loadedText = P.Resource.loadText(resourceWritten);
    var readText = P.readString(P.Resource.applicationPath + "/app/" + resourceWritten);
    if(loadedText !== readText)
        throw 'P.Resource.loadText | P.readString mismatch';
    P.Resource.upload();
    P.logout();
    P.Logger.info("P.Resource.applicationPath: " + P.Resource.applicationPath);
    (function() {
        P.Logger.info("invokeDelayed called");
    }).invokeDelayed(1000 * 10);

    (function() {
        P.Logger.info("invokeBackground called");
        P.Logger.info("invokeBackground waits...");
        var inputted = null;
        (function() {
            inputted = P.prompt("some value for another thread");
        }).invokeAndWait();
        P.Logger.info("invokeBackground wait is complete. inputted: " + inputted);
    }).invokeBackground();

    (function() {
        if (self.onSuccess) {
            self.onSuccess();
        } else {
            P.Logger.severe("self.onSuccess is absent. So unable to report about test's result");
        }
    }).invokeLater();
}

