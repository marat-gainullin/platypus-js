/**
 * 
 * @author Andrew
 * @constructor
 */
function SyncServerModulesTest() {
    var self = this, model = P.loadModel(this.constructor.name);

    this.execute = function (aOnSuccess) {

        var sessionStatefull = new P.ServerModule("SessionStatefull");
        // Statefull test
        sessionStatefull.clearCallsCount();
        if (sessionStatefull.getCallsCount() !== 0) {
            throw "Fail SessionStatefull module getCallsCount test.";
        }
        sessionStatefull.incCallsCount(3);
        if (sessionStatefull.getCallsCount() !== 3) {
            throw "Fail SessionStatefull module getCallsCount test.";
        }
        var now = new Date();
        if (sessionStatefull.dateMarshaling(now).getTime() !== now.getTime()) {
            throw "Fail SessionStatefull module dateMarshaling test.";
        }
        var sendObj = {date: now, data: ["test", now, 2, true, 7.55], obj: {dt: now}};
        var getObj = sessionStatefull.objectWithDateMarshaling(sendObj);
        if (sendObj.date.getTime() !== getObj.date.getTime()) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        if (sendObj.data[0] !== getObj.data[0]) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        if (sendObj.data[1].getTime() !== getObj.data[1].getTime()) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        if (sendObj.data[2] !== getObj.data[2]) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        if (sendObj.data[3] !== getObj.data[3]) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        if (sendObj.obj.dt.getTime() !== getObj.obj.dt.getTime()) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        var sObj = new Object();
        sObj.date = now;
        sObj.data = ["test", now, 2, true, 7.55];
        sObj.obj = new Object();
        sObj.obj.dt = now;
        var gObj = sessionStatefull.objectWithDateMarshaling(sObj);
        if (sObj.date.getTime() !== gObj.date.getTime()) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        if (sObj.data[0] !== gObj.data[0]) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        if (sObj.data[1].getTime() !== gObj.data[1].getTime()) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        if (sObj.data[2] !== gObj.data[2]) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        if (sObj.data[3] !== gObj.data[3]) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }
        if (sObj.obj.dt.getTime() !== gObj.obj.dt.getTime()) {
            throw "Fail SessionStatefull module objectWithDateMarshaling test.";
        }

        var aSum = sessionStatefull.numbersMarshallingTest(4, 5);
        if (aSum !== (4 + 5)) {
            throw "Fail SessionStatefull module numbersMarshallingTest test";
        }
        aSum = sessionStatefull.stringMarshallingTest('4', '5');
        if (aSum !== '45') {
            throw "Fail SessionStatefull module stringMarshallingTest test";
        }
        var aInverse = sessionStatefull.booleanMarshallingTest(true);
        if (aInverse) {
            throw "Fail SessionStatefull module booleanMarshallingTest test";
        }
        var aIncremented = sessionStatefull.dateMarshallingTest(now);
        var incremented = new Date(now.getTime() + 10);
        if (incremented.getTime() !== aIncremented.getTime()) {
            throw "Fail SessionStatefull module dateMarshallingTest test";
        }
        var nullRes = sessionStatefull.nullMarshallingTest(null);
        if (nullRes !== null) {
            throw "Fail SessionStatefull module nullMarshallingTest test";
        }
        var undefinedRes = sessionStatefull.undefinedMarshallingTest(null); //TODO: Uncomment when platypus JS will be changed.
        if (undefinedRes !== null) {
            throw "Fail SessionStatefull module undefinedMarshallingTest test";
        }

        var rowset = sessionStatefull.rowsetMarshalingTest();
        if (!rowset || rowset.length !== 13) {
            throw "Fail SessionStatefull module rowsetMarshallingTest test";
        }

        // Stateless test 
        var sessionStateless = new P.ServerModule("SessionStateless");
        if (sessionStateless.getCallsCount() !== 0) {
            throw "Fail SessionStateless module getCallsCount test.";
        }
        sessionStateless.incCallsCount(3);
        if (sessionStateless.getCallsCount() !== 0) {
            throw "Fail SessionStateless module getCallsCount test.";
        }
        aOnSuccess();
    };
}
