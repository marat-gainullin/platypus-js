/**
 * 
 * @author Andrew, Alexey
 */
function HttpPostTestClient() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    model.requery(function () {
    });

    form.button.onActionPerformed = function (event) {
        self.execute(function () {
            alert('All tests passed.');
        });
    };

    self.execute = function (aOnComplete) {
        var jCount = 0;
        function finished() {
            jCount++;
            if (jCount === 6)
                aOnComplete();
        }
        
        var req0 = new HTTPRequest();
        req0.module = 'HttpPostTestServer';
        req0.method = 'simplePostTest';
        req0.post("111", function (aRes) {
            if (aRes !== "ok!")
                throw "simplePostTest. Error! Response is wrong!";
            else {
                P.Logger.info("simplePostTest - passed");
                finished();
            }
        }, function (e) {
            throw "simplePostTest. Error in post query. Server response: " + e;
        });
        var req1 = new HTTPRequest();
        req1.module = 'HttpPostTestServer';
        req1.method = 'testBody';
        req1.contentType = 'text/plain;charset=utf-8';
        req1.post("111", function (aRes) {
            if (aRes !== "111")
                throw "testBody. Error! Response is not equal to request";
            else {
                P.Logger.info("testBody - passed");
                finished();
            }
        }, function (e) {
            throw "testBody. Error in post query. Server response: " + e;
        });
        
        var req2 = new HTTPRequest();
        req2.restMethod = 'restSimpleTest';
        req2.post('', function (aRes) {
            if (aRes !== "ok!")
                throw "restSimpleTest. Error! Wrong response from server.";
            else {
                P.Logger.info("restSimpleTest - passed");
                finished();
            }
        }, function (e) {
            throw "restSimpleTest. Error in rest POST query. Server response: " + e;
        });

        var req3 = new HTTPRequest();
        req3.restMethod = 'restUPCASETest';
        req3.post('', function (aRes) {
            if (aRes !== "ok!")
                throw "restUPCASETest. Error! Wrong response from server.";
            else {
                P.Logger.info("restUPCASETest - passed");
                finished();
            }
        }, function (e) {
            throw "restUPCASETest. Error in rest POST query. Server response: " + e;
        });
        //        
        var req4 = new HTTPRequest();
        req4.restMethod = 'restGetObjTest';
        req4.post("", function (aRes) {
            if (aRes.result !== "ok!")
                throw "restGetObjTest. Error! Wrong response from server.";
            else {
                P.Logger.info("restGetObjTest - passed");
                finished();
            }
        }, function (e) {
            throw "restGetObjTest. Error in rest POST query. Server response: " + e;
        });
        var bd = document.getElementsByTagName('body')[0];
        var dv = document.createElement("div");
        dv.innerHTML = '' +
                '<iframe id="test-frame" name="test-frame"></iframe>' +
                '<form id="test-post-form" action="application/restParamsTest" method="POST" target="test-frame" />' +
                '    <input name="p1" value="10" />' +
                '    <input name="p2" value="test" />' +
                '    <button id="form-submit" type="submit" />' +
                '</form>';

        bd.appendChild(dv);
        var tf = document.getElementById("test-frame");
        tf.onload = function () {
            var response = tf.contentDocument.body.childNodes[0].innerHTML;
            var res = JSON.parse(response);
            if (res.p1 === '10' && res.p2 === "test") {
                bd.removeChild(dv);
                finished();
            } else
                throw "restParamsTest. Error! Wrong response from server. Server response: " + response;
        };
        try {
            document.getElementById("test-post-form").submit();
        } catch (e) {
            throw "restParamsTest. Error!";
        }

    };
}
