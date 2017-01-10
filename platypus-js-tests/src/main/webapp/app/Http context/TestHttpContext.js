/**
 * 
 * @author Andrew
 * @stateless
 * @public 
 * @constructor
 */
function TestHttpContext() {
    var self = this, model = P.loadModel(this.constructor.name);
    var httpContext;

    function checkHttpContext(){
        if(!httpContext)
            httpContext = new P.HttpContext();
    }

    self.addParam = function() {
        checkHttpContext();
        httpContext.response.headers.add("test", "test");
    };

    self.addCookie = function() {
        var cookie = {name: "test",
            value: "tost",
            comment: "test test",
            maxAge: 100};
        checkHttpContext();
        httpContext.response.addCookie(cookie);
    };

    self.changeBody = function() {
        checkHttpContext();
        httpContext.response.contentType = "text/plain";
        httpContext.response.characterEncoding = "utf-8";
        httpContext.response.body = JSON.stringify("Фишер: \"В 1970 году в Бледе я принял участие в международном \n" +
            "блицтурнире. В партии с Петросяном мы то и дело обменивались шахами, причем он произносил \n" +
            "это слово по-русски, а я – по-английски. В момент, когда у обоих уже начали зависать флажки,\n" +
            "я вдруг сказал по-русски: \"Вам шах, гроссмейстер!\" Петросян настолько поразился, что на какой-то \n" +
            "момент забыл о флажке и просрочил время.");
    };

}
